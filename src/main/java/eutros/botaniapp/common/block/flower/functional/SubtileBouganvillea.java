package eutros.botaniapp.common.block.flower.functional;

import eutros.botaniapp.api.recipe.IBouganvilleaInventory;
import eutros.botaniapp.api.recipe.RecipeBouganvillea;
import eutros.botaniapp.common.crafting.BotaniaPPRecipeTypes;
import eutros.botaniapp.common.crafting.recipe.bouganvillea.RecipeBouganvilleaAnvil;
import eutros.botaniapp.common.crafting.recipe.bouganvillea.RecipeBouganvilleaRename;
import eutros.botaniapp.common.utils.Reference;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.DoubleNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraftforge.registries.ObjectHolder;
import org.jetbrains.annotations.NotNull;
import vazkii.botania.api.subtile.RadiusDescriptor;
import vazkii.botania.api.subtile.TileEntityFunctionalFlower;
import vazkii.botania.common.network.PacketBotaniaEffect;
import vazkii.botania.common.network.PacketHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

// TODO consume mana
// TODO format strings
// TODO extra goodies
public class SubtileBouganvillea extends TileEntityFunctionalFlower {
    @ObjectHolder(Reference.MOD_ID + ":" + Reference.FlowerNames.BOUGANVILLEA)
    public static TileEntityType<SubtileBouganvillea> TYPE;

    private static final String TAG_MEMORY = "item_memory";
    private static final String TAG_RECIPE = "active_recipe";
    private static final String TAG_ANVILLED = "botaniapp_bouganvilled";

    @Nullable
    private RecipeBouganvillea activeRecipe = null;

    @Nullable
    private ItemAndPos head = null;

    public static final String FALLBACK_GROUP = "botaniapp:bouganvillea_fallback";

    public static Map<ResourceLocation, RecipeBouganvillea> fallbackRecipes = new HashMap<ResourceLocation, RecipeBouganvillea>() {{
        ResourceLocation RENAME = new ResourceLocation(Reference.MOD_ID, "bouganvillea_rename");
        ResourceLocation ANVIL = new ResourceLocation(Reference.MOD_ID, "bouganvillea_anvil");
        put(RENAME, new RecipeBouganvilleaRename(RENAME));
        put(ANVIL, new RecipeBouganvilleaAnvil(ANVIL));
        }};

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
        return 20;
    }

    @Override
    public void tickFlower() {
        super.tickFlower();

        assert world != null;
        if(world.isRemote || redstoneSignal > 0)
            return;

        List<ItemEntity> items = world.getEntitiesWithinAABB(ItemEntity.class, new AxisAlignedBB(pos.add(-1, -1, -1), pos.add(2, 2, 2)));

        for(ItemEntity e : items) {
            if(e.getTags().contains(TAG_ANVILLED) || e.getAge() < 30 + getSlowdownFactor())
                continue;

            IBouganvilleaInventory inventory = getInventory(e);

            if(activeRecipe == null) {
                List<IRecipe<IBouganvilleaInventory>> possibleRecipes = world.getRecipeManager().getRecipes(BotaniaPPRecipeTypes.BOUGANVILLEA, inventory, world);

                for(IRecipe<IBouganvilleaInventory> provisionalRecipe : possibleRecipes) {
                    if(!(provisionalRecipe instanceof RecipeBouganvillea))
                        continue;

                    RecipeBouganvillea recipe = (RecipeBouganvillea) provisionalRecipe;

                    if(recipe.checkHead(e)) {
                        setRecipe(e, recipe);
                        return;
                    }
                }

                for(RecipeBouganvillea recipe : fallbackRecipes.values()) {
                    if(recipe.checkHead(e)) {
                        setRecipe(e, recipe);
                        return;
                    }
                }

                if(activeRecipe == null) {
                    e.addTag(TAG_ANVILLED); // So the Bouganvillea doesn't go through all recipes each tick.
                    continue;
                }
            }

            if(!activeRecipe.shouldTrigger(inventory)) {
                memory.add(new ItemAndPos(e));
                consumeItem(e);
            } else {
                doRecipe(e);
            }
            break;
        }
    }

    private void setRecipe(ItemEntity e, RecipeBouganvillea recipe) {
        assert world != null;

        head = new ItemAndPos(e);
        activeRecipe = recipe;
        consumeItem(e);
        world.playSound(null, getEffectivePos(), SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.BLOCKS, 0.5F, 2F);
    }

    public void consumeItem(ItemEntity e) {
        e.addTag(TAG_ANVILLED);
        e.remove();
        spawnParticles(e, getEffectivePos(), 1);
    }

    @Nonnull
    private IBouganvilleaInventory getInventory(ItemEntity entity) {
        return new BouganvilleaInventory(entity);
    }

    private void doRecipe(ItemEntity e) {
        if(activeRecipe == null)
            return;

        ItemStack stack = activeRecipe.getCraftingResult(getInventory(e));

        e.setItem(stack);

        BlockPos efPos = getEffectivePos();

        e.addTag(TAG_ANVILLED);
        spawnParticles(e, efPos, 3);
        e.setPickupDelay(0);

        assert world != null;
        dropAll();
        activeRecipe = null;

        world.playSound(null, efPos, SoundEvents.BLOCK_ANVIL_USE, SoundCategory.BLOCKS, 0.5f, 1f);
    }

    private void dropAll() {
        assert world != null;

        if(head != null) {
            memory.add(head);
            head = null;
        }

        for(ItemAndPos iap : memory) {
            ItemEntity iapEntity = iap.getEntity(world);
            iapEntity.addTag(TAG_ANVILLED);
            iapEntity.setDefaultPickupDelay();
            world.addEntity(iapEntity);
        }

        memory.clear();
    }

    private void spawnParticles(ItemEntity e, BlockPos efPos, int p) {
        PacketHandler.sendToNearby(world, efPos, new PacketBotaniaEffect(PacketBotaniaEffect.EffectType.ITEM_SMOKE, e.getX(), e.getY(), e.getZ(), e.getEntityId(), p));
    }

    @Override
    public RadiusDescriptor getRadius() {
        return new RadiusDescriptor.Square(getEffectivePos(), 1);
    }

    @Override
    public void readFromPacketNBT(CompoundNBT cmp) {
        super.readFromPacketNBT(cmp);
        assert world != null;

        memory = cmp.getList(TAG_MEMORY, 10).stream().map(s -> ItemAndPos.fromNBT((CompoundNBT) s)).collect(Collectors.toList());
        String recipeId = cmp.getString(TAG_RECIPE);

        if(recipeId.equals("")) {
            activeRecipe = null;
        } else {
            // TODO fix with fallbacks
            IRecipe<?> recipeCandidate;
            ResourceLocation recipeLoc = new ResourceLocation(recipeId);
            recipeCandidate = world.getRecipeManager().getRecipe(recipeLoc).orElse(null);
            if(recipeCandidate == null)
                recipeCandidate = fallbackRecipes.getOrDefault(recipeLoc, null);
            activeRecipe = recipeCandidate instanceof RecipeBouganvillea ? (RecipeBouganvillea) recipeCandidate : null;
        }
    }

    @Override
    public void writeToPacketNBT(CompoundNBT cmp) {
        super.writeToPacketNBT(cmp);

        ListNBT list = new ListNBT();
        list.addAll(memory.stream().map(ItemAndPos::toNBT).collect(Collectors.toList()));
        cmp.put(TAG_MEMORY, list);
        cmp.putString(TAG_RECIPE, activeRecipe == null ? "" : activeRecipe.getId().toString());
    }

    @Override
    public void onBlockHarvested(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        dropAll();
        super.onBlockHarvested(world, pos, state, player);
    }

    @Override
    public List<ItemStack> getDrops(List<ItemStack> list, LootContext.Builder ctx) {
        return super.getDrops(list, ctx);
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

        public ItemAndPos withStack(ItemStack stack) {
            return new ItemAndPos(stack, pos);
        }

        public static ItemAndPos fromNBT(CompoundNBT cmp) {
            ItemStack stack = ItemStack.read(cmp.getCompound(TAG_ITEM));

            ListNBT position = cmp.getList(TAG_POS, 6);
            Vec3d pos = new Vec3d(position.getDouble(0), position.getDouble(1), position.getDouble(2));

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
        public List<ItemEntity> getEntities() {
            return memory.stream().map(iap -> iap.getEntity(world)).collect(Collectors.toList());
        }

        @Override
        public Optional<ItemEntity> getHead() {
            return Optional.ofNullable(head).map(iap -> iap.getEntity(world));
        }

        @Nonnull
        @Override
        public ItemEntity getTrigger() {
            return trigger;
        }

        @Override
        public TileEntityFunctionalFlower getFlower() {
            return SubtileBouganvillea.this;
        }

        @Override
        public int getSizeInventory() {
            return memory.size() + (head == null ? 0 : 1);
        }

        @Override
        public boolean isEmpty() {
            return getSizeInventory() == 0;
        }

        @NotNull
        @Override
        public ItemStack getStackInSlot(int index) {
            return index == 0 ? Optional.ofNullable(head).map(iap -> iap.stack).orElse(ItemStack.EMPTY) : memory.get(index-1).stack;
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
            if(index == 0) {
                head = null;
            }
            else {
                memory.remove(index-1);
            }
            return stack;
        }

        @Override
        public void setInventorySlotContents(int index, @NotNull ItemStack stack) {
            if(index == 0)
                head = new ItemAndPos(stack, head == null ? new Vec3d(getEffectivePos()) : head.pos);
            else {
                memory.set(index-1, memory.get(index - 1).withStack(stack));
            }
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
            dropAll();
        }
    }
}
