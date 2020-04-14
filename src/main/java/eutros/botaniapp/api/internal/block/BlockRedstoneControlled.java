package eutros.botaniapp.api.internal.block;

import eutros.botaniapp.api.internal.block.state.StateRedstoneControlled;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Listens to redstone pulses on this block, for {@link BlockRedstoneControlled#doPulse(BlockState, BlockPos, World, BlockPos)}
 */
public abstract class BlockRedstoneControlled extends Block {

    protected final StateContainer<Block, BlockState> stateContainer;

    public BlockRedstoneControlled(Properties properties) {
        super(properties);
        StateContainer.Builder<Block, BlockState> builder = new StateContainer.Builder<>(this);
        fillStateContainer(builder);
        stateContainer = builder.create(StateRedstoneControlled::new);
        setDefaultState(stateContainer.getBaseState().with(StateRedstoneControlled.POWERED, false));
    }

    @NotNull
    @Override
    public StateContainer<Block, BlockState> getStateContainer() {
        return stateContainer;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(StateRedstoneControlled.POWERED);
    }

    /**
     * Perform whatever needs to be performed on redstone pulse.
     */
    public void doPulse(BlockState state, BlockPos pos, World world, BlockPos from) {
    }

    /**
     * Get the positions that need to be checked with {@link World#getRedstonePower(BlockPos, Direction)}.
     *
     * @param pos The position that needs to be accounted for.
     * @return All the positions that need to be checked.
     */
    public Collection<Pair<BlockPos, Direction>> getRedstoneChecks(BlockPos pos) {
        Collection<Pair<BlockPos, Direction>> checks = new ArrayList<>();
        for(Direction d : getDirections()) {
            checks.add(Pair.of(pos.offset(d), d));
        }
        return checks;
    }

    /**
     * Which directions to check for redstone control.
     */
    protected Direction[] getDirections() {
        return Direction.values();
    }

}
