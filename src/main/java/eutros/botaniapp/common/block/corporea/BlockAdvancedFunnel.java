package eutros.botaniapp.common.block.corporea;

import eutros.botaniapp.common.block.tile.corporea.TileAdvancedFunnel;
import eutros.botaniapp.common.block.tile.corporea.TileCorporeaBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.botania.api.wand.IWandHUD;
import vazkii.botania.api.wand.IWandable;

import javax.annotation.Nonnull;
import java.util.Objects;

public class BlockAdvancedFunnel extends BlockCorporeaBase implements IWandable, IWandHUD {
    // TODO block drops

    public static final BooleanProperty POWERED = BooleanProperty.create("powered");

    public BlockAdvancedFunnel(Properties builder) {
        super(builder);
        setDefaultState(stateContainer.getBaseState().with(POWERED, false));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }

    @Nonnull
    @Override
    public TileCorporeaBase createTileEntity(@Nonnull BlockState state, @Nonnull IBlockReader world) {
        return new TileAdvancedFunnel();
    }

    // TODO find non-deprecated way of doing this
    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, world, pos, blockIn, fromPos, isMoving);
        boolean power = world.getRedstonePowerFromNeighbors(pos) > 0 || world.getRedstonePowerFromNeighbors(pos.up()) > 0;
        boolean powered = state.get(POWERED);

        if(power != powered) {
            TileEntity te = world.getTileEntity(pos);
            assert te != null;

            if (power) {
                ((TileAdvancedFunnel) te).doRequest();
            }
            world.setBlockState(pos, state.with(POWERED, power), 4);
        }
    }

    @Override
    public boolean onUsedByWand(PlayerEntity player, ItemStack stack, World world, BlockPos pos, Direction side) {
        return ((TileAdvancedFunnel) Objects.requireNonNull(world.getTileEntity(pos))).onWanded();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderHUD(Minecraft mc, World world, BlockPos pos) {
        ((TileAdvancedFunnel) Objects.requireNonNull(world.getTileEntity(pos))).renderHUD(mc);
    }
}
