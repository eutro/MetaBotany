package eutros.botaniapp.common.block.tile;

import com.mojang.blaze3d.systems.RenderSystem;
import eutros.botaniapp.client.core.helper.HUDHelper;
import eutros.botaniapp.common.block.BotaniaPPBlocks;
import eutros.botaniapp.common.utils.Reference;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.ObjectHolder;
import org.lwjgl.opengl.GL11;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.api.mana.IManaItem;
import vazkii.botania.api.mana.spark.ISparkAttachable;
import vazkii.botania.api.mana.spark.ISparkEntity;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class TileChargingPlate extends TileSimpleInventory implements ISparkAttachable, ITickableTileEntity {

    @ObjectHolder(Reference.MOD_ID + ":" + Reference.BlockNames.CHARGING_PLATE)
    public static TileEntityType<TileChargingPlate> TYPE;

    private boolean shouldDispatch = false;

    public TileChargingPlate() {
        super(TYPE);
    }

    @Override
    public int getSizeInventory() {
        return 1;
    }

    protected SimpleItemStackHandler createItemHandler() {
        return new SimpleItemStackHandler(this, true) {
            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                if(!stack.isEmpty() && stack.getItem() instanceof IManaItem) {
                    return super.insertItem(slot, stack, simulate);
                }
                return stack;
            }

            @Override
            protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
                return 1;
            }
        };
    }


    @Override
    public boolean isFull() {
        return getManaItem().map(item -> {
            ItemStack stack = itemHandler.getStackInSlot(0);
            return item.getMana(stack) >= item.getMaxMana(stack);
        }).orElse(true);
    }

    @Override
    public void receiveMana(int mana) {
        getManaItem().ifPresent(item -> {
            if(!isFull()) {
                ItemStack stack = itemHandler.getStackInSlot(0);
                item.addMana(stack, Math.min(mana, item.getMaxMana(stack) - item.getMana(stack)));
                assert world != null;
                markDirty();
                shouldDispatch = true;
            }
        });
    }

    @Override
    public boolean canReceiveManaFromBursts() {
        return getManaItem().isPresent();
    }

    @Override
    public int getCurrentMana() {
        return getManaItem().map(item -> item.getMana(itemHandler.getStackInSlot(0))).orElse(0);
    }

    private Optional<IManaItem> getManaItem() {
        ItemStack stack = itemHandler.getStackInSlot(0);
        if(stack.isEmpty())
            return Optional.empty();
        return Optional.of((IManaItem) stack.getItem());
    }

    public void renderHUD(Minecraft mc) {
        int color = 0x4444FF;
        ItemStack stack = itemHandler.getStackInSlot(0);
        MainWindow window = mc.getMainWindow();
        int scaledWidth = window.getScaledWidth();
        int scaledHeight = window.getScaledHeight();
        int x = scaledWidth / 2;
        int y = scaledHeight / 2;
        String name = stack.isEmpty() ? I18n.format(BotaniaPPBlocks.chargingPlate.getTranslationKey()) : stack.getDisplayName().getFormattedText();

        if(Optional.ofNullable(stack.getItem().getRegistryName()).map(ResourceLocation::getPath).map(s -> s.equals("terra_pick")).orElse(false)) {
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            int ttpX = x - 51;
            int ttpY = y + 36;
            int ttpWidth = 102;
            MinecraftForge.EVENT_BUS.post(new RenderTooltipEvent.PostText(stack, Collections.emptyList(), ttpX, ttpY, mc.fontRenderer, ttpWidth, 1));
            AbstractGui.fill(ttpX - 1, ttpY - 4, ttpX + ttpWidth + 1, ttpY - 3, 0xFF000000);

            mc.fontRenderer.drawStringWithShadow(name, x - mc.fontRenderer.getStringWidth(name) / 2F, y + 8, color);
        } else {
            BotaniaAPI.instance().internalHandler().drawSimpleManaHUD(color,
                    getCurrentMana(),
                    getManaItem().map(item -> item.getMaxMana(stack)).orElse(1),
                    name);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        }

        mc.textureManager.bindTexture(HUDHelper.MANA_HUD);

        RenderHelper.enableStandardItemLighting();
        mc.getItemRenderer().renderItemAndEffectIntoGUI(stack, x + 8, y - 8);
        RenderHelper.disableStandardItemLighting();

        RenderSystem.disableLighting();
        RenderSystem.disableBlend();

    }

    @Override
    public void markDirty() {
        super.markDirty();
        assert world != null;
        world.updateComparatorOutputLevel(pos, getBlockState().getBlock());
    }

    public int comparatorPower() {
        ItemStack stack = itemHandler.getStackInSlot(0);
        Optional<IManaItem> manaItem = getManaItem();
        return manaItem.map(item -> {
            int raw = (int) ((double) item.getMana(stack) / (double) item.getMaxMana(stack) * 15.0);
            return Math.max(raw, 1);
        }).orElse(0);
    }

    @Override
    public boolean canAttachSpark(ItemStack itemStack) {
        return true;
    }

    @Override
    public void attachSpark(ISparkEntity iSparkEntity) {
    }

    @Override
    public int getAvailableSpaceForMana() {
        ItemStack stack = itemHandler.getStackInSlot(0);
        Optional<IManaItem> manaItem = getManaItem();
        return manaItem.map(item -> item.getMaxMana(stack) - item.getMana(stack))
                .orElse(0);
    }

    @Override
    public ISparkEntity getAttachedSpark() {
        assert world != null;
        List<Entity> sparks = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos.up(), pos.up().add(1, 1, 1)), ISparkEntity.class::isInstance);
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
    public void tick() {
        assert world != null;

        if(shouldDispatch && world.getGameTime() % 10 == 0) {
            VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
            shouldDispatch = false;
        }
    }

}