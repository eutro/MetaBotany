package eutros.botaniapp.common.core.handler;

import com.google.common.base.Stopwatch;
import com.google.common.collect.AbstractIterator;
import eutros.botaniapp.common.item.BotaniaPPItems;
import eutros.botaniapp.common.item.ItemManaCompactedStacks;
import eutros.botaniapp.common.item.ItemTerraPickPP;
import eutros.botaniapp.common.utils.MathUtils;
import eutros.botaniapp.common.utils.Reference;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.items.ItemHandlerHelper;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import vazkii.botania.api.mana.ManaItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class TerraPickMiningHandler extends WorldSavedData {

    private Map<UUID, MiningAgent> agents = new HashMap<>();
    private Queue<Pair<MiningAgent, Triple<UUID, PlayerEntity, ItemStack>>> activeAgents = new ArrayDeque<>();
    private final Stopwatch timer = Stopwatch.createUnstarted();
    private World world;

    private static final String ID = Reference.MOD_ID + "_terra_pick_mining_handler";

    public TerraPickMiningHandler(World world) {
        super(ID);
        this.world = world;
    }

    @SubscribeEvent
    public static void tickEnd(TickEvent.ServerTickEvent evt) {
        if(evt.type != TickEvent.Type.SERVER || evt.phase != TickEvent.Phase.END) return;
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

        StreamSupport.stream(server.getWorlds().spliterator(), true)
                .map(TerraPickMiningHandler::get)
                .filter(Objects::nonNull).forEach(TerraPickMiningHandler::tick);
    }

    public void tick() {
        if(activeAgents.isEmpty()) return;

        timer.start();
        boolean flag = false;
        while(!flag && !activeAgents.isEmpty()) {
            for(Iterator<Pair<MiningAgent, Triple<UUID, PlayerEntity, ItemStack>>> iterator = activeAgents.iterator(); iterator.hasNext(); ) {
                Pair<MiningAgent, Triple<UUID, PlayerEntity, ItemStack>> pair = iterator.next();
                MiningAgent agent = pair.getLeft();
                Triple<UUID, PlayerEntity, ItemStack> triple = pair.getRight();
                if(agent.advance(triple.getMiddle(), triple.getRight())) {
                    if(agent.complete()) {
                        agents.remove(triple.getLeft());
                        iterator.remove();
                    }
                }
                if(timer.elapsed(TimeUnit.MILLISECONDS) > 40) flag = true;
            }
        }
        activeAgents.clear();
        timer.reset();

        markDirty();
    }

    @Nullable
    public static UUID createEvent(PlayerEntity player, ItemStack stack, World world, BlockPos pos, int range, int depth, boolean tipped, Direction side, BlockPos trueMid) {
        TerraPickMiningHandler instance = get(world);
        if(instance == null) return null;
        return instance.createEvent(player, stack, pos, range, depth, tipped, side, trueMid);
    }

    @Nullable
    public static TerraPickMiningHandler get(World world) {
        if(!(world instanceof ServerWorld)) return null;

        TerraPickMiningHandler instance = ((ServerWorld) world).getSavedData().get(() -> new TerraPickMiningHandler(world), ID);
        if(instance == null) {
            instance = new TerraPickMiningHandler(world);
            instance.markDirty();
            ((ServerWorld) world).getSavedData().set(instance);
        }

        return instance;
    }

    public UUID createEvent(PlayerEntity player, ItemStack stack, BlockPos pos, int range, int depth, boolean tipped, Direction side, BlockPos trueMid) {
        UUID uuid = UUID.randomUUID();
        agents.put(uuid, new MiningAgent(player, stack, pos, range, depth, tipped, side, trueMid));
        player.getFoodStats().addExhaustion(range * range * depth / 10F);
        markDirty();
        return uuid;
    }

    @Override
    public void read(@Nonnull CompoundNBT cmp) {
        CompoundNBT agents = cmp.getCompound("agents");
        this.agents = agents.keySet().stream().map(k -> Pair.of(k, agents.get(k))).map(n -> {
            MiningAgent agent = new MiningAgent();
            agent.read((CompoundNBT) n.getRight());
            return Pair.of(UUID.fromString(n.getLeft()), agent);
        }).collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
    }

    @Nonnull
    @Override
    public CompoundNBT write(@Nonnull CompoundNBT cmp) {
        CompoundNBT ags = new CompoundNBT();
        agents.keySet().stream().map(k -> Pair.of(k.toString(), agents.get(k)))
                .forEach(pair -> ags.put(pair.getLeft(), pair.getRight().write()));
        cmp.put("agents", ags);
        return cmp;
    }

    public boolean prod(INBT nbt, PlayerEntity player, ItemStack stack) {
        if(!(nbt instanceof StringNBT)) return false;

        UUID uuid = UUID.fromString(nbt.getString());
        MiningAgent agent = agents.get(uuid);

        if(agent == null)
            return false;

        return activeAgents.add(Pair.of(agent, Triple.of(uuid, player, stack)));
    }

    private class MiningAgent {

        private final Tag<Item> E_PICK_TAG =
                ItemTags.getCollection().get(new ResourceLocation("botania", "disposable"));
        private BlockPos centerPos;
        private Predicate<BlockState> filter = state -> ItemTerraPickPP.MATERIALS.contains(state.getMaterial());
        private boolean tipped;
        private int range;
        private BlockPos trueMid;
        private Direction side;
        private SpiralIterator iterator;
        private Collection<ItemStack> drops = new HashSet<>();
        private boolean frozen = false;
        private boolean disabled = false;

        public MiningAgent(PlayerEntity player, ItemStack stack, BlockPos pos, int range, int depth, boolean tipped, Direction side, BlockPos trueMid) {
            this.centerPos = pos;
            this.tipped = tipped;
            this.side = side;
            this.range = range * 2 + 1;
            this.trueMid = trueMid;
            this.iterator = new SpiralIterator();
            if(depth > 1) {
                ItemTerraPickPP.addHandler(stack, TerraPickMiningHandler.this.createEvent(player, stack, pos, range, depth - 1, tipped, side, trueMid));
            }
        }

        public MiningAgent() {
        }

        private class SpiralIterator extends AbstractIterator<BlockPos> {

            public BlockPos.Mutable pos = new BlockPos.Mutable(centerPos);
            private Direction facing = MathUtils.roll(side);
            private int cap = 1;
            private int dist = 1;

            @Override
            protected BlockPos computeNext() {
                if((cap + 3) / 2 > range && dist == 0)
                    return endOfData();
                if(dist > 0) {
                    BlockPos ret = pos.toImmutable();
                    dist--;
                    pos.move(facing);
                    return ret;
                }
                dist = cap++ / 2 + 1;
                facing = MathUtils.rotateAround(facing, side.getAxis());
                return computeNext();
            }

            public void read(CompoundNBT cmp) {
                cap = cmp.getInt("cap");
                dist = cmp.getInt("dist");
                facing = Direction.byName(cmp.getString("facing"));
                int[] coord = cmp.getIntArray("pos");
                pos = new BlockPos.Mutable(coord[0], coord[1], coord[2]);
            }

            public CompoundNBT write() {
                CompoundNBT cmp = new CompoundNBT();
                cmp.putInt("cap", cap);
                cmp.putInt("dist", dist);
                cmp.putString("facing", facing.getName());
                cmp.putIntArray("pos", new int[] {pos.getX(), pos.getY(), pos.getZ()});
                return cmp;
            }

        }

        public boolean advance(PlayerEntity player, ItemStack stack) {
            BlockPos pos = iterator.pos;
            if(!frozen || World.isOutsideBuildHeight(pos)) {
                if(stack.isEmpty()) {
                    disabled = true;
                    return true;
                }

                if(!iterator.hasNext()) {
                    frozen = false;
                    return true;
                }

                do {
                    iterator.next();
                    pos = iterator.pos;
                } while(World.isOutsideBuildHeight(pos) && iterator.hasNext());
            }

            if(pos.equals(trueMid)) return false;

            if(!world.isAreaLoaded(pos, 0)) {
                frozen = true;
                return true;
            } else {
                frozen = false;
            }

            BlockState state = world.getBlockState(pos);
            Block block = state.getBlock();

            if(!world.isRemote && filter.test(state)
                    && !block.isAir(state, world, pos) && state.getPlayerRelativeBlockHardness(player, world, pos) > 0
                    && state.canHarvestBlock(player.world, pos, player)) {
                int exp = ForgeHooks.onBlockBreakEvent(world, ((ServerPlayerEntity) player).interactionManager.getGameType(), (ServerPlayerEntity) player, pos);
                if(exp == -1)
                    return false;
                player.giveExperiencePoints(exp);

                if(!player.abilities.isCreativeMode) {
                    TileEntity tile = world.getTileEntity(pos);

                    if(block.removedByPlayer(state, world, pos, player, true, world.getFluidState(pos))) {
                        block.onPlayerDestroy(world, pos, state);

                        if(E_PICK_TAG != null && (!tipped || !E_PICK_TAG.contains(block.asItem()))) {
                            player.addStat(Stats.BLOCK_MINED.get(block));
                            List<ItemStack> blockDrops = Block.getDrops(state, (ServerWorld) world, pos, tile, player, stack);
                            blockDrops.forEach(thisStack -> {
                                for(ItemStack otherStack : drops) {
                                    if(ItemHandlerHelper.canItemStacksStack(thisStack, otherStack)) {
                                        otherStack.setCount(otherStack.getCount() + thisStack.getCount());
                                        return;
                                    }
                                }

                                drops.add(thisStack);
                            });
                            state.spawnAdditionalDrops(world, pos, stack);
                        }
                    }

                    damageItem(player, stack);
                    return !stack.isEmpty();
                } else world.removeBlock(pos, false);
            }
            return false;
        }

        private void damageItem(PlayerEntity player, ItemStack stack) {
            boolean manaRequested = ManaItemHandler.instance().requestManaExactForTool(stack, player, 80, true);

            if(!manaRequested)
                stack.damageItem(1, player, e -> {
                });
        }

        public boolean complete() {
            if(frozen || disabled) return false;

            if(!drops.isEmpty()) {
                drops = drops.parallelStream().flatMap(s -> {
                    Stream.Builder<ItemStack> builder = Stream.builder();
                    int max = s.getMaxStackSize();
                    int fullStacks = s.getCount() / max;
                    if(fullStacks > 0) {
                        ItemStack full = s.copy();
                        full.setCount(max);
                        for(int i = 0; i < fullStacks; i++) {
                            builder.add(full.copy());
                        }
                    }
                    int partial = s.getCount() % max;
                    if(partial > 0) {
                        s.setCount(partial);
                        builder.add(s);
                    }
                    return builder.build();
                }).collect(Collectors.toList());

                ItemStack stack = new ItemStack(BotaniaPPItems.compactedStacks);
                ItemManaCompactedStacks.setStacks(stack, drops.stream());
                Block.spawnAsEntity(world, centerPos, stack);
            }

            return true;
        }

        public CompoundNBT write() {
            CompoundNBT cmp = new CompoundNBT();
            cmp.putBoolean("tipped", tipped);
            cmp.putInt("range", range);
            cmp.putString("side", side.getName());
            ListNBT stacks = new ListNBT();
            drops.stream().map(s -> s.write(new CompoundNBT())).forEach(stacks::add);
            cmp.put("drops", stacks);
            cmp.put("iterator", iterator.write());
            cmp.putIntArray("centerPos", new int[] {centerPos.getX(), centerPos.getY(), centerPos.getZ()});
            cmp.putIntArray("trueMid", new int[] {trueMid.getX(), trueMid.getY(), trueMid.getZ()});
            return cmp;
        }

        public void read(CompoundNBT cmp) {
            tipped = cmp.getBoolean("tipped");
            range = cmp.getInt("range");
            side = Direction.byName(cmp.getString("side"));
            ListNBT stacks = cmp.getList("drops", 10);
            drops = stacks.stream().map(CompoundNBT.class::cast).map(ItemStack::read).collect(Collectors.toList());

            int[] coord = cmp.getIntArray("centerPos");
            centerPos = new BlockPos.Mutable(coord[0], coord[1], coord[2]);

            coord = cmp.getIntArray("trueMid");
            trueMid = new BlockPos.Mutable(coord[0], coord[1], coord[2]);

            iterator = new SpiralIterator();
            iterator.read(cmp.getCompound("iterator"));
        }

    }

}
