package eutros.botaniapp.common.block.tile;

import com.google.common.base.Predicates;
import com.mojang.blaze3d.platform.GlStateManager;
import eutros.botaniapp.common.block.BlockLeakyPool;
import eutros.botaniapp.common.block.BotaniaPPBlocks;
import eutros.botaniapp.common.sound.BotaniaPPSounds;
import eutros.botaniapp.common.utils.Reference;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ObjectHolder;
import org.lwjgl.opengl.GL11;
import vazkii.botania.api.internal.IManaBurst;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.api.mana.*;
import vazkii.botania.api.mana.spark.ISparkAttachable;
import vazkii.botania.api.mana.spark.ISparkEntity;
import vazkii.botania.client.core.handler.HUDHandler;
import vazkii.botania.client.fx.WispParticleData;
import vazkii.botania.common.core.handler.ManaNetworkHandler;
import vazkii.botania.common.entity.EntityManaBurst;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;
import java.util.UUID;

// TODO make this less similar to original mana pool's
public class TileLeakyPool extends TileSimpleInventory implements IManaPool, IKeyLocked, ISparkAttachable, IThrottledPacket, ITickableTileEntity, IManaSpreader {

    public static final int MAX_MANA = 1000000;

    private static final String TAG_MANA = "mana";
    private static final String TAG_KNOWN_MANA = "knownMana";
    private static final String TAG_COLOR = "color";
    private static final String TAG_INPUT_KEY = "inputKey";
    private static final String TAG_OUTPUT_KEY = "outputKey";
    private static final String TAG_DRIP = "drip";
    private static final Color PARTICLE_COLOR = new Color(0x00C6FF);
    @ObjectHolder(Reference.MOD_ID + ":" + Reference.BlockNames.LEAKY_POOL)
    public static TileEntityType<TileLeakyPool> TYPE;
    private static final int BURST_MAX_MANA = 50000;

    public DyeColor color = DyeColor.WHITE;
    private int mana;
    private int knownMana = -1;
    private int ticks = 0;
    private final String outputKey = "";
    private float dripProgress = 0;
    private String inputKey = "";
    private UUID identity;
    private boolean sendPacket = false;
    private int lastBurstDeathTick = 0;
    private int burstParticleTick = 0;

    public TileLeakyPool() {
        super(TYPE);
    }

    public static int calculateComparatorLevel(int mana, int max) {
        int val = (int) ((double) mana / (double) max * 15.0);
        if(mana > 0)
            val = Math.max(val, 1);
        return val;
    }

    @Override
    public boolean isFull() {
        assert world != null;
        Block blockBelow = world.getBlockState(pos.down()).getBlock();
        return blockBelow != BotaniaPPBlocks.BOTANIA_MANA_VOID && getCurrentMana() >= MAX_MANA;
    }

    @Override
    public void recieveMana(int mana) {
        int old = this.mana;
        this.mana = Math.max(0, Math.min(getCurrentMana() + mana, MAX_MANA));
        if(old != this.mana) {
            markDirty();
            assert world != null;
            world.updateComparatorOutputLevel(pos, world.getBlockState(pos).getBlock());
            markDispatchable();
        }
    }

    @Override
    public void remove() {
        super.remove();
        ManaNetworkEvent.removePool(this);
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        ManaNetworkEvent.removePool(this);
    }

    @Override
    public void tick() {
        assert world != null;

        if(!ManaNetworkHandler.instance.isPoolIn(this) && !isRemoved()) {
            ManaNetworkEvent.addPool(this);
        }

        boolean shouldShoot = true;
        BlockState state = world.getBlockState(pos.down());
        boolean canFire = state.isAir(world, pos.down());

        if(getCurrentMana() <= 0 || !canFire) {
            shouldShoot = false;
            dripProgress = 0;
        } else {
            dripProgress += 1/getDripFrequency();
        }

        if(world.isRemote) {
            double particleChance = 1F - (double) getCurrentMana() / (double) MAX_MANA * 0.1;
            if(Math.random() > particleChance) {
                WispParticleData data = WispParticleData.wisp((float) Math.random() / 3F, PARTICLE_COLOR.getRed() / 255F, PARTICLE_COLOR.getGreen() / 255F, PARTICLE_COLOR.getBlue() / 255F, 2F);
                world.addParticle(data, pos.getX() + 0.3 + Math.random() * 0.5, pos.getY() + 0.6 + Math.random() * 0.25, pos.getZ() + Math.random(), 0, (float) Math.random() / 25F, 0);
            }
            return;
        }

        boolean redstone = false;

        for(Direction dir : Direction.values()) {
            int redstoneSide = world.getRedstonePower(pos.offset(dir), dir);
            if(redstoneSide > 0) {
                redstone = true;
            }
        }

        if(canFire) {
            ItemStack lens = itemHandler.getStackInSlot(0);
            ILensControl control = getLensController(lens);
            if (control != null) {
                control.onControlledSpreaderTick(lens, this, redstone);

                shouldShoot = control.allowBurstShooting(lens, this, redstone);
            }

            while(shouldShoot && dripProgress >= 1) {
                tryShootBurst();
                dripProgress = Math.max(dripProgress - 1, 0);
            }
        }

        if(sendPacket && ticks % 10 == 0) {
            VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
            sendPacket = false;
        }

        ticks++;
    }

    public float getDripPercentage(float offset) {
        return Math.min(dripProgress + offset/getDripFrequency(), 1F);
    }

    public float getDripFrequency() {
        return MAX_MANA/(Math.max(1F, mana));
    }

    private void tryShootBurst() {
        EntityManaBurst burst = getBurst(false);
        if (world != null && !world.isRemote) {
            mana -= burst.getMana();
            burst.setShooterUUID(getIdentifier());
            world.addEntity(burst);
            burst.ping();
            // TODO use own sound
            world.playSound(null, pos, BotaniaPPSounds.BOTANIA_SPREADER_FIRE, SoundCategory.BLOCKS, 0.05F, 0.7F + 0.3F * (float) Math.random());
        }
    }

    public ILensControl getLensController(ItemStack stack) {
        if(!stack.isEmpty() && stack.getItem() instanceof ILensControl) {
            ILensControl control = (ILensControl) stack.getItem();
            if(control.isControlLens(stack))
                return control;
        }

        return null;
    }

    @Override
    public int getSizeInventory() {
        return 1;
    }

    @Override
    protected SimpleItemStackHandler createItemHandler() {
        return new SimpleItemStackHandler(this, true) {
            @Override
            protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
                return 1;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                if(!stack.isEmpty() && stack.getItem() instanceof ILens)
                    return super.insertItem(slot, stack, simulate);
                else return stack;
            }
        };
    }

    private EntityManaBurst getBurst(boolean fake) {
        int ticksBeforeManaLoss = 160;
        float manaLossPerTick = 20F;
        float motionModifier = 0.5F;
        float gravity = 0F;
        BurstProperties props = new BurstProperties(BURST_MAX_MANA, ticksBeforeManaLoss, manaLossPerTick, gravity, motionModifier, PARTICLE_COLOR.getRGB());

        ItemStack lens = itemHandler.getStackInSlot(0);
        if(!lens.isEmpty() && lens.getItem() instanceof ILensEffect)
            ((ILensEffect) lens.getItem()).apply(lens, props);

        EntityManaBurst burst = new EntityManaBurst(this, fake);

        // Move burst half a block down so it starts at the bottom of the pool.
        Vec3d vec = burst.getPositionVector();
        burst.setPosition(vec.x, vec.y-0.5, vec.z);

        burst.setSourceLens(lens);
        burst.setMana(Math.min(mana, BURST_MAX_MANA));

        burst.setColor(props.color);
        burst.setStartingMana(props.maxMana);
        burst.setMinManaLoss(props.ticksBeforeManaLoss);
        burst.setManaLossPerTick(props.manaLossPerTick);
        burst.setGravity(props.gravity);
        burst.setMotion(burst.getMotion().scale(props.motionModifier));

        return burst;
    }

    @Override
    public void writePacketNBT(CompoundNBT cmp) {
        super.writePacketNBT(cmp);

        cmp.putInt(TAG_MANA, mana);
        cmp.putInt(TAG_COLOR, color.getId());
        cmp.putFloat(TAG_DRIP, dripProgress);

        cmp.putString(TAG_INPUT_KEY, inputKey);
        cmp.putString(TAG_OUTPUT_KEY, outputKey);
    }

    @Override
    public void readPacketNBT(CompoundNBT cmp) {
        super.readPacketNBT(cmp);

        mana = cmp.getInt(TAG_MANA);
        color = DyeColor.byId(cmp.getInt(TAG_COLOR));
        dripProgress = cmp.getFloat(TAG_DRIP);

        if(cmp.contains(TAG_INPUT_KEY))
            inputKey = cmp.getString(TAG_INPUT_KEY);
        if(cmp.contains(TAG_OUTPUT_KEY))
            inputKey = cmp.getString(TAG_OUTPUT_KEY);

        if(cmp.contains(TAG_KNOWN_MANA))
            knownMana = cmp.getInt(TAG_KNOWN_MANA);
    }

    public void onWanded(PlayerEntity player) {
        assert world != null;

        if(player == null)
            return;

        if(!world.isRemote) {
            CompoundNBT nbttagcompound = new CompoundNBT();
            writePacketNBT(nbttagcompound);
            nbttagcompound.putInt(TAG_KNOWN_MANA, getCurrentMana());
            if(player instanceof ServerPlayerEntity)
                ((ServerPlayerEntity) player).connection.sendPacket(new SUpdateTileEntityPacket(pos, -999, nbttagcompound));
        }

        world.playSound(null, player.posX, player.posY, player.posZ, BotaniaPPSounds.BOTANIA_DING, SoundCategory.PLAYERS, 0.11F, 1F);
    }

    @OnlyIn(Dist.CLIENT)
    public void renderHUD(Minecraft mc) {
        ItemStack pool = new ItemStack(getBlockState().getBlock());
        String name = pool.getDisplayName().getString();
        int color = 0xFF4444;
        HUDHandler.drawSimpleManaHUD(color, knownMana, MAX_MANA, name);

        int x = Minecraft.getInstance().mainWindow.getScaledWidth() / 2 - 11;
        int y = Minecraft.getInstance().mainWindow.getScaledHeight() / 2 + 30;

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        mc.textureManager.bindTexture(HUDHandler.manaBar);
        GlStateManager.color4f(1F, 1F, 1F, 1F);

        net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();
        mc.getItemRenderer().renderItemAndEffectIntoGUI(pool, x + 26, y);
        net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();

        GlStateManager.disableLighting();
        GlStateManager.disableBlend();
    }

    @Override
    public boolean canRecieveManaFromBursts() {
        return true;
    }

    @Override
    public boolean isOutputtingPower() {
        return false;
    }

    @Override
    public int getCurrentMana() {
        if (getBlockState().getBlock() instanceof BlockLeakyPool) {
            return mana;
        }
        return 0;
    }

    @Override
    public String getInputKey() {
        return inputKey;
    }

    @Override
    public String getOutputKey() {
        return outputKey;
    }

    @Override
    public boolean canAttachSpark(ItemStack stack) {
        return true;
    }

    @Override
    public void attachSpark(ISparkEntity entity) {}

    @Override
    public ISparkEntity getAttachedSpark() {
        assert world != null;
        List<Entity> sparks = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos.up(), pos.up().add(1, 1, 1)), Predicates.instanceOf(ISparkEntity.class));
        if(sparks.size() == 1) {
            Entity e = sparks.get(0);
            return (ISparkEntity) e;
        }

        return null;
    }

    @Override
    public boolean areIncomingTranfersDone() {
        return false;
    }

    @Override
    public int getAvailableSpaceForMana() {
        int space = Math.max(0, MAX_MANA - getCurrentMana());
        if(space > 0)
            return space;
        else {
            assert world != null;
            if(world.getBlockState(pos.down()).getBlock() == BotaniaPPBlocks.BOTANIA_MANA_VOID)
                return MAX_MANA;
            else return 0;
        }
    }

    @Override
    public DyeColor getColor() {
        return color;
    }

    @Override
    public void setColor(DyeColor color) {
        this.color = color;
        assert world != null;
        world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 3);
    }

    @Override
    public void markDispatchable() {
        sendPacket = true;
    }

    @Override
    public int getBurstParticleTick() {
        return burstParticleTick;
    }

    @Override
    public void setBurstParticleTick(int i) {
        burstParticleTick = i;
    }

    @Override
    public int getLastBurstDeathTick() {
        return lastBurstDeathTick;
    }

    @Override
    public void setLastBurstDeathTick(int i) {
        lastBurstDeathTick = i;
    }

    public void setCanShoot(boolean canShoot) {}

    @Override
    public IManaBurst runBurstSimulation() {
        EntityManaBurst fakeBurst = getBurst(true);
        fakeBurst.setScanBeam();
        fakeBurst.getCollidedTile(true);
        return fakeBurst;
    }

    @Override
    public float getRotationX() {
        return 0;
    }

    @Override
    public void setRotationX(float rot) {}

    @Override
    public float getRotationY() {
        return 270F;
    }

    @Override
    public void setRotationY(float rot) {}

    @Override
    public void commitRedirection() {}

    @Override
    public void pingback(IManaBurst burst, UUID expectedIdentity) {}

    @Override
    public UUID getIdentifier() {
        if(identity == null)
            identity = UUID.randomUUID();
        return identity;
    }
}
