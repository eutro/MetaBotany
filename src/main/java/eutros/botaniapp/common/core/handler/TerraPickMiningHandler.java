package eutros.botaniapp.common.core.handler;

import com.google.common.base.Stopwatch;
import eutros.botaniapp.common.utils.Reference;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.common.item.equipment.tool.elementium.ItemElementiumPick;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class TerraPickMiningHandler {

    private static Set<MiningAgent> agents = new HashSet<>();
    private static Stopwatch timer = Stopwatch.createUnstarted();

    @SubscribeEvent
    public static void tickEnd(TickEvent.ServerTickEvent evt) {
        if(evt.phase != TickEvent.Phase.END)
            return;

        timer.start();
        long elapsed;
        do {
            agents.forEach(MiningAgent::advance);
            elapsed = timer.elapsed(TimeUnit.MILLISECONDS);
        } while(elapsed < 40);
        timer.reset();

        agents.removeIf(MiningAgent::isComplete);
    }

    public static void createEvent(PlayerEntity player, ItemStack stack, World world, BlockPos pos, Vec3i beginDiff, Vec3i endDiff, Predicate<BlockState> filter, boolean tipped) {
        agents.add(new MiningAgent(player, stack, world, pos, beginDiff, endDiff, filter, tipped));
    }

    private static class MiningAgent {

        private final PlayerEntity player;
        private final ItemStack stack;
        private final World world;
        private final BlockPos centerPos;
        private final Predicate<BlockState> filter;
        private final boolean tipped;
        private Iterator<BlockPos> iterator;

        public MiningAgent(PlayerEntity player, ItemStack stack, World world, BlockPos pos, Vec3i beginDiff, Vec3i endDiff, Predicate<BlockState> filter, boolean tipped) {
            this.player = player;
            this.stack = stack;
            this.world = world;
            this.centerPos = pos;
            this.filter = filter;
            this.tipped = tipped;
            this.iterator = BlockPos.getAllInBoxMutable(pos.add(beginDiff), pos.add(endDiff)).iterator();
        }

        public void advance() {
            if(!iterator.hasNext()) return;

            BlockPos pos = iterator.next();
            if(pos.equals(centerPos)) return;

            if(!world.isAreaLoaded(pos, 0))
                return;

            BlockState state = world.getBlockState(pos);
            Block block = state.getBlock();

            if(!world.isRemote && filter.test(state)
                    && !block.isAir(state, world, pos) && state.getPlayerRelativeBlockHardness(player, world, pos) > 0
                    && state.canHarvestBlock(player.world, pos, player)) {
                int exp = ForgeHooks.onBlockBreakEvent(world, ((ServerPlayerEntity) player).interactionManager.getGameType(), (ServerPlayerEntity) player, pos);
                if(exp == -1)
                    return;

                if(!player.abilities.isCreativeMode) {
                    TileEntity tile = world.getTileEntity(pos);

                    if(block.removedByPlayer(state, world, pos, player, true, world.getFluidState(pos))) {
                        block.onPlayerDestroy(world, pos, state);

                        if(!tipped || !ItemElementiumPick.isDisposable(block)) {
                            block.harvestBlock(world, player, pos, state, tile, stack);
                            block.dropXpOnBlockBreak(world, pos, exp);
                        }
                    }

                    damageItem();
                } else world.removeBlock(pos, false);
            }
        }

        private void damageItem() {
            boolean manaRequested = ManaItemHandler.requestManaExactForTool(stack, player, 80, true);

            if(!manaRequested)
                stack.damageItem(1, player, e -> {
                });
        }

        public boolean isComplete() {
            return !iterator.hasNext();
        }

    }

}
