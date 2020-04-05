package eutros.botaniapp.common.core.handler;

import com.google.common.base.Stopwatch;
import com.google.common.collect.AbstractIterator;
import eutros.botaniapp.common.item.BotaniaPPItems;
import eutros.botaniapp.common.item.ItemManaCompactedStacks;
import eutros.botaniapp.common.utils.MathUtils;
import eutros.botaniapp.common.utils.Reference;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import vazkii.botania.api.mana.ManaItemHandler;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class TerraPickMiningHandler {

    private static List<MiningAgent> agents = new LinkedList<>();
    private static Stopwatch timer = Stopwatch.createUnstarted();

    @SubscribeEvent
    public static void tickEnd(TickEvent.ServerTickEvent evt) {
        if(evt.phase != TickEvent.Phase.END || agents.isEmpty())
            return;

        timer.start();
        long elapsed;
        do {
            if(agents.stream().map(MiningAgent::advance).reduce(true, Boolean::logicalAnd))
                break;
            elapsed = timer.elapsed(TimeUnit.MILLISECONDS);
        } while(elapsed < 40);
        timer.reset();

        agents.removeIf(MiningAgent::isComplete);
    }

    public static void createEvent(PlayerEntity player, ItemStack stack, World world, BlockPos pos, int range, int depth, Predicate<BlockState> filter, boolean tipped, Direction side, BlockPos trueMid) {
        agents.add(new MiningAgent(player, stack, world, pos, range, depth, filter, tipped, side, trueMid));
        player.getFoodStats().addExhaustion(range * range * depth / 10F);
    }

    private static class MiningAgent {

        private final Tag<Item> E_PICK_TAG =
                ItemTags.getCollection().get(new ResourceLocation("botania", "disposable"));
        private final PlayerEntity player;
        private final ItemStack stack;
        private final World world;
        private final BlockPos centerPos;
        private final Predicate<BlockState> filter;
        private final boolean tipped;
        private final int range;
        private final BlockPos trueMid;
        private Direction side;
        private Iterator<BlockPos> iterator;
        private Collection<ItemStack> drops = new HashSet<>();
        private int xp = 0;

        public MiningAgent(PlayerEntity player, ItemStack stack, World world, BlockPos pos, int range, int depth, Predicate<BlockState> filter, boolean tipped, Direction side, BlockPos trueMid) {
            this.player = player;
            this.stack = stack;
            this.world = world;
            this.centerPos = pos;
            this.filter = filter;
            this.tipped = tipped;
            this.side = side;
            this.range = range * 2 + 1;
            this.trueMid = trueMid;
            this.iterator = new SpiralIterator();
            if(depth > 1) {
                createEvent(player, stack, world, pos.offset(side.getOpposite()), range, depth - 1, filter, tipped, side, pos);
            }
        }

        private class SpiralIterator extends AbstractIterator<BlockPos> {

            private BlockPos.Mutable pos = new BlockPos.Mutable(centerPos);
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

        }

        public boolean advance() {
            if(!iterator.hasNext() || stack.isEmpty())
                return true;

            BlockPos pos = iterator.next();
            if(pos.equals(trueMid)) return false;

            if(!world.isAreaLoaded(pos, 0))
                return false;

            BlockState state = world.getBlockState(pos);
            Block block = state.getBlock();

            if(!world.isRemote && filter.test(state)
                    && !block.isAir(state, world, pos) && state.getPlayerRelativeBlockHardness(player, world, pos) > 0
                    && state.canHarvestBlock(player.world, pos, player)) {
                int exp = ForgeHooks.onBlockBreakEvent(world, ((ServerPlayerEntity) player).interactionManager.getGameType(), (ServerPlayerEntity) player, pos);
                if(exp == -1)
                    return false;
                xp += exp;

                if(!player.abilities.isCreativeMode) {
                    TileEntity tile = world.getTileEntity(pos);

                    if(block.removedByPlayer(state, world, pos, player, true, world.getFluidState(pos))) {
                        block.onPlayerDestroy(world, pos, state);

                        if(E_PICK_TAG != null && (!tipped || !E_PICK_TAG.contains(block.asItem()))) {
                            player.addStat(Stats.BLOCK_MINED.get(block));
                            drops.addAll(Block.getDrops(state, (ServerWorld) world, pos, tile, player, stack));
                            state.spawnAdditionalDrops(world, pos, stack);
                        }
                    }

                    damageItem();
                } else world.removeBlock(pos, false);
            }
            return false;
        }

        private void damageItem() {
            boolean manaRequested = ManaItemHandler.requestManaExactForTool(stack, player, 80, true);

            if(!manaRequested)
                stack.damageItem(1, player, e -> {
                });
        }

        /**
         * Executed once at the end of each cycle.
         *
         * @return whether this agent has finished its job
         */
        public boolean isComplete() {
            if(iterator.hasNext() && !stack.isEmpty())
                return false;

            player.giveExperiencePoints(xp);
            xp = 0;
            if(!drops.isEmpty()) {
                drops = this.drops.stream().reduce(new LinkedList<>(), // Collapse adjacent stacks of the same item.
                        (LinkedList<ItemStack> list, ItemStack secondStack) -> {
                            if(!list.isEmpty()) {
                                ItemStack firstStack = list.get(list.size() - 1);
                                if(firstStack.isItemEqual(secondStack)) {
                                    int max = firstStack.getMaxStackSize();
                                    int diff = max - firstStack.getCount();
                                    if(diff < secondStack.getCount()) {
                                        secondStack.setCount(secondStack.getCount() - diff);
                                        firstStack.setCount(max);
                                    } else {
                                        firstStack.setCount(secondStack.getCount() + firstStack.getCount());
                                        return list;
                                    }
                                }
                            }
                            list.add(secondStack);
                            return list;
                        },
                        (a, b) -> b);
            }

            if(!drops.isEmpty()) {
                ItemStack stack = new ItemStack(BotaniaPPItems.compactedStacks);
                ItemManaCompactedStacks.setStacks(stack, drops.stream());
                Block.spawnAsEntity(world, centerPos, stack);
            }

            return true;
        }

    }

}
