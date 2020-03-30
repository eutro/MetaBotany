package eutros.botaniapp.common.block.flower.functional;

import com.mojang.blaze3d.systems.RenderSystem;
import eutros.botaniapp.api.recipe.IBouganvilleaInventory;
import eutros.botaniapp.api.recipe.RecipeBouganvillea;
import eutros.botaniapp.common.core.network.BotaniaPPEffectPacket;
import eutros.botaniapp.common.core.network.PacketHandler;
import eutros.botaniapp.common.crafting.BotaniaPPRecipeTypes;
import eutros.botaniapp.common.utils.MathUtils;
import eutros.botaniapp.common.utils.Reference;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.DoubleNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ObjectHolder;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;
import vazkii.botania.api.subtile.RadiusDescriptor;
import vazkii.botania.api.subtile.TileEntityFunctionalFlower;

import javax.annotation.Nonnull;
import java.awt.*;
import java.awt.geom.Point2D;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class SubtileBouganvillea extends TileEntityFunctionalFlower {

    public static final String BUILTIN_GROUP = "botaniapp:bouganvillea_builtin";
    private static final String TAG_MEMORY = "item_memory";
    private static final String TAG_RECIPE = "active_recipe";
    private static final String TAG_ANVILLED = "botaniapp_bouganvilled";
    @ObjectHolder(Reference.MOD_ID + ":" + Reference.FlowerNames.BOUGANVILLEA)
    public static TileEntityType<SubtileBouganvillea> TYPE;
    public boolean soundCanceled = false;
    public boolean shouldReplace = true;
    private List<RecipeBouganvillea> activeRecipes = Collections.emptyList();
    private List<ResourceLocation> unresolvedRecipes = Collections.emptyList();
    private List<ItemAndPos> memory = new ArrayList<>();

    public SubtileBouganvillea(TileEntityType<?> type) {
        super(type);
    }

    public SubtileBouganvillea() {
        this(TYPE);
    }

    @Override
    public boolean acceptsRedstone() {
        return true;
    }

    @Override
    public int getMaxMana() {
        return 500;
    }

    @Override
    public int getColor() {
        return 0x5F5F5F;
    }

    @Override
    public void tickFlower() {
        super.tickFlower();

        assert world != null;
        if(world.isRemote || redstoneSignal > 0 || getMana() < getMaxMana())
            return;

        BlockPos efPos = getEffectivePos();
        List<ItemEntity> items = world.getEntitiesWithinAABB(ItemEntity.class, new AxisAlignedBB(efPos.add(-1, -1, -1), efPos.add(2, 2, 2)));
        items.sort(Comparator.comparingInt(ItemEntity::getAge).reversed());

        for(ItemEntity e : items) {
            if(e.getTags().contains(TAG_ANVILLED) || e.getAge() < 30 + getSlowdownFactor())
                continue;

            IBouganvilleaInventory inventory = getInventory(e);

            if(activeRecipes.isEmpty()) {
                if(unresolvedRecipes.isEmpty()) {
                    activeRecipes = world.getRecipeManager().getRecipes(BotaniaPPRecipeTypes.BOUGANVILLEA_TYPE.type, inventory, world);
                } else {
                    unresolvedRecipes.forEach(this::resolveRecipe);
                    unresolvedRecipes = Collections.emptyList();
                }
                if(activeRecipes.isEmpty() && !memory.isEmpty()) {
                    dropAll();
                    markDirty();
                    sync();
                }
            } else
                activeRecipes = activeRecipes.stream().filter(i -> i.matches(inventory, world)).collect(Collectors.toList());

            if(!activeRecipes.isEmpty()) {
                if(memory.isEmpty())
                    setRecipe(e);
            } else {
                e.addTag(TAG_ANVILLED); // So the Bouganvillea doesn't go through all recipes each tick.
                continue;
            }

            for(RecipeBouganvillea recipe : activeRecipes) {
                if(recipe.shouldTrigger(inventory)) {
                    doRecipe(e, recipe);
                    return;
                }
            }
            memory.add(new ItemAndPos(e));
            consumeItem(e,
                    memory.size() == 1 ?
                    SoundEvents.BLOCK_ANVIL_PLACE :
                    SoundEvents.ENTITY_ITEM_PICKUP);
            break;
        }
    }

    private void setRecipe(ItemEntity e) {
        assert world != null;

        BlockPos efPos = getEffectivePos();
        List<ItemEntity> items = world.getEntitiesWithinAABB(ItemEntity.class, new AxisAlignedBB(efPos.add(-1, -1, -1), efPos.add(2, 2, 2)));
        items.forEach(i -> {
            if(i != e && i.getAge() < 50) { // Hopefully prevents loop spasms.
                i.removeTag(TAG_ANVILLED);
            }
        });
    }

    public void consumeItem(ItemEntity e, SoundEvent sound) {
        assert world != null;

        e.addTag(TAG_ANVILLED);
        e.remove();
        spawnParticles(e, 2);
        addMana(-20);
        markDirty();
        sync();
        world.playSound(null, getEffectivePos(), sound, SoundCategory.BLOCKS, 0.5F, 2F);
    }

    @Nonnull
    private IBouganvilleaInventory getInventory(ItemEntity entity) {
        return new BouganvilleaInventory(entity);
    }

    private void doRecipe(ItemEntity e, RecipeBouganvillea recipe) {
        assert world != null;

        ItemStack stack = recipe.getCraftingResult(getInventory(e));

        if(!shouldReplace) {
            memory.add(new ItemAndPos(stack, e.getPositionVec()));
            shouldReplace = true;
        } else
            e.setItem(stack);


        BlockPos efPos = getEffectivePos();

        e.addTag(TAG_ANVILLED);
        spawnParticles(e, 3);
        e.setPickupDelay(0);

        dropAll();
        markDirty();
        sync();

        if(!soundCanceled)
            world.playSound(null, efPos, SoundEvents.BLOCK_ANVIL_USE, SoundCategory.BLOCKS, 0.5f, 1f);
        else
            soundCanceled = false;
    }

    private void dropAll() {
        assert world != null;

        for(ItemAndPos iap : memory) {
            ItemEntity iapEntity = iap.getEntity(world);
            iapEntity.addTag(TAG_ANVILLED);
            iapEntity.setDefaultPickupDelay();
            world.addEntity(iapEntity);
        }

        memory.clear();
        activeRecipes = Collections.emptyList();
    }

    private void spawnParticles(ItemEntity e, int p) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putInt(e.getEntityId());
        buffer.putInt(p);
        PacketHandler.sendToNearby(world,
                getEffectivePos(),
                new BotaniaPPEffectPacket(BotaniaPPEffectPacket.EffectType.SMOKE,
                        buffer.array())
        );
    }

    @Override
    public RadiusDescriptor getRadius() {
        return new RadiusDescriptor.Square(getEffectivePos(), 1);
    }

    @Override
    public void readFromPacketNBT(CompoundNBT cmp) {
        super.readFromPacketNBT(cmp);

        memory = cmp.getList(TAG_MEMORY, 10).stream().map(s -> ItemAndPos.fromNBT((CompoundNBT) s)).collect(Collectors.toList());
        ListNBT recipes = cmp.getList(TAG_RECIPE, 8);

        if(recipes.isEmpty()) {
            activeRecipes = Collections.emptyList();
        } else {
            activeRecipes = new ArrayList<>();
            unresolvedRecipes = new ArrayList<>();
            for(int i = 0; i < recipes.size(); i++) {
                String loc = recipes.getString(i);
                ResourceLocation recipeLoc = new ResourceLocation(loc);
                if(world != null) {
                    resolveRecipe(recipeLoc);
                } else {
                    unresolvedRecipes.add(recipeLoc);
                }
            }
        }
    }

    private void resolveRecipe(ResourceLocation recipeLoc) {
        assert world != null;

        IRecipe<?> recipeCandidate;
        recipeCandidate = world.getRecipeManager().getRecipe(recipeLoc).orElse(null);
        if(recipeCandidate instanceof RecipeBouganvillea) {
            activeRecipes.add((RecipeBouganvillea) recipeCandidate);
        }
    }

    @Override
    public void writeToPacketNBT(CompoundNBT cmp) {
        super.writeToPacketNBT(cmp);

        ListNBT list = new ListNBT();
        list.addAll(memory.stream().map(ItemAndPos::toNBT).collect(Collectors.toList()));
        cmp.put(TAG_MEMORY, list);
        ListNBT recipes = new ListNBT();

        for(RecipeBouganvillea recipe : activeRecipes) {
            recipes.add(StringNBT.of(recipe.getId().toString()));
        }

        cmp.put(TAG_RECIPE, recipes);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderHUD(Minecraft mc) {
        super.renderHUD(mc);

        if(!memory.isEmpty()) {
            final float sf = 0.8F;

            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            RenderHelper.enable();
            ItemRenderer itemRenderer = mc.getItemRenderer();

            double angleBetweenEach = Math.min(60.0, memory.size() > 1 ? 180.0 / (memory.size() - 1) : 0);
            Point center = new Point(mc.getWindow().getScaledWidth() / 2, mc.getWindow().getScaledHeight() / 2);
            int radius = 40;
            Point2D point = new Point2D.Double(center.getX(), center.getY() - radius);
            point = MathUtils.rotatePointAbout(point, center, -angleBetweenEach * (memory.size() - 1) / 2);

            assert mc.player != null;
            for(int i = 0; i < memory.size(); i++) {
                ItemAndPos itemAndPos = memory.get(i);
                ItemStack stack = itemAndPos.stack;
                itemRenderer.renderItemAndEffectIntoGUI(stack, (int) Math.round(point.getX() - 8), (int) Math.round(point.getY() - 8));

                if(mc.player.isSneaking()) {
                    RenderSystem.pushMatrix();
                    String formattedText = stack.getDisplayName().getFormattedText();
                    RenderSystem.translated(point.getX(), point.getY(), 0);
                    RenderSystem.scalef(sf, sf, sf);
                    int width = mc.fontRenderer.getStringWidth(formattedText);
                    GL11.glRotated(15, 0, 0, i < memory.size() / 2 ? 1 : -1);
                    mc.fontRenderer.drawStringWithShadow(formattedText, i < memory.size() / 2 ? -width - 10 : 10, -5, 0xFFFFFF);
                    RenderSystem.popMatrix();
                }

                point = MathUtils.rotatePointAbout(point, center, angleBetweenEach);
            }

            RenderHelper.disableStandardItemLighting();
            RenderSystem.disableLighting();
            RenderSystem.disableBlend();
        }
    }

    @Override
    public void onBlockHarvested(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBlockHarvested(world, pos, state, player);
        if(!world.isRemote()) {
            dropAll();
        }
    }

    public List<ItemStack> getMemory() {
        return memory.stream().map(a -> a.stack).collect(Collectors.toList());
    }

    private static class ItemAndPos {

        private static final String TAG_ITEM = "item";
        private static final String TAG_POS = "pos";

        public final ItemStack stack;
        public final Vec3d pos;

        public ItemAndPos(ItemEntity e) {
            stack = e.getItem();
            pos = e.getPositionVec();
        }

        public ItemAndPos(ItemStack stack, Vec3d pos) {
            this.stack = stack;
            this.pos = pos;
        }

        public static ItemAndPos fromNBT(CompoundNBT cmp) {
            ItemStack stack = ItemStack.read(cmp.getCompound(TAG_ITEM));

            ListNBT position = cmp.getList(TAG_POS, 6);
            Vec3d pos = new Vec3d(position.getDouble(0), position.getDouble(1), position.getDouble(2));

            return new ItemAndPos(stack, pos);
        }

        public ItemAndPos withStack(ItemStack stack) {
            return new ItemAndPos(stack, pos);
        }

        public CompoundNBT toNBT() {
            CompoundNBT cmp = new CompoundNBT();

            cmp.put(TAG_ITEM, stack.serializeNBT());

            ListNBT position = new ListNBT();
            double[] coords = {pos.x, pos.y, pos.z};
            position.addAll(Arrays.stream(coords).mapToObj(DoubleNBT::of).collect(Collectors.toList()));
            cmp.put(TAG_POS, position);

            return cmp;
        }

        public ItemEntity getEntity(World world) {
            ItemEntity itemEntity = new ItemEntity(world, pos.x, pos.y, pos.z);
            itemEntity.setItem(stack);
            return itemEntity;
        }

    }

    private class BouganvilleaInventory implements IBouganvilleaInventory {

        private final ItemEntity trigger;

        public BouganvilleaInventory(ItemEntity trigger) {
            this.trigger = trigger;
        }

        @Override
        public ItemEntity getThrown() {
            return trigger;
        }

        @Override
        public List<ItemEntity> allEntities() {
            List<ItemEntity> entities = memory.stream().map(iap -> iap.getEntity(world)).collect(Collectors.toList());
            entities.add(trigger);
            return entities;
        }

        @Override
        public TileEntityFunctionalFlower getFlower() {
            return SubtileBouganvillea.this;
        }

        @Override
        public void cancelSound() {
            soundCanceled = true;
        }

        @Override
        public void noReplace() {
            shouldReplace = false;
        }

        @Override
        public int getSizeInventory() {
            return memory.size() + 1;
        }

        @Override
        public boolean isEmpty() {
            return getSizeInventory() == 0;
        }

        @NotNull
        @Override
        public ItemStack getStackInSlot(int index) {
            return (index < memory.size() ? memory.get(index).stack : trigger.getItem());
        }

        @NotNull
        @Override
        public ItemStack decrStackSize(int index, int quantity) {
            return getStackInSlot(index).split(quantity);
        }

        @NotNull
        @Override
        public ItemStack removeStackFromSlot(int index) {
            ItemStack stack = getStackInSlot(index);
            if(index < memory.size())
                memory.remove(index);
            else
                trigger.setItem(ItemStack.EMPTY);
            return stack;
        }

        @Override
        public void setInventorySlotContents(int index, @NotNull ItemStack stack) {
            if(index < memory.size())
                memory.set(index, memory.get(index).withStack(stack));
            else if(index == memory.size())
                trigger.setItem(stack);
            else
                memory.add(index, new ItemAndPos(stack, trigger.getPositionVector()));
        }

        @Override
        public void markDirty() {
            SubtileBouganvillea.this.markDirty();
        }

        @Override
        public boolean isUsableByPlayer(@NotNull PlayerEntity player) {
            return true;
        }

        @Override
        public void clear() {
            memory.clear();
        }

    }

}
