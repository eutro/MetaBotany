package eutros.botaniapp.api.internal.block.state;

import com.google.common.collect.ImmutableMap;
import eutros.botaniapp.api.internal.block.BlockRedstoneControlled;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.ParametersAreNonnullByDefault;

public class StateRedstoneControlled extends BlockState {

    public static final BooleanProperty POWERED = BooleanProperty.create("powered");

    public StateRedstoneControlled(Block block, ImmutableMap<IProperty<?>, Comparable<?>> map) {
        super(block, map);
    }

    @ParametersAreNonnullByDefault
    @Override
    public void neighborChanged(World world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if(!(getBlock() instanceof BlockRedstoneControlled))
            return;

        int power = 0;
        for(Pair<BlockPos, Direction> pair : ((BlockRedstoneControlled) getBlock()).getRedstoneChecks(pos)) {
            int powerAt = world.getRedstonePower(pair.getLeft(), pair.getRight());
            if(powerAt > power) {
                power = powerAt;
            }
        }

        boolean powered;
        try {
            powered = this.get(POWERED);
        } catch(IllegalArgumentException e) {
            return;
        }

        if((power > 0) != powered) {
            world.setBlockState(pos, this.with(POWERED, power > 0), 4);
            if(power > 0) {
                ((BlockRedstoneControlled) getBlock()).doPulse(this, pos, world, fromPos);
            }
        }
    }

}
