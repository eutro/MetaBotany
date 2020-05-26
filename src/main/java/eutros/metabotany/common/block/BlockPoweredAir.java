package eutros.metabotany.common.block;

import eutros.metabotany.common.block.tile.TilePoweredAir;
import net.minecraft.block.AirBlock;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;

import javax.annotation.Nullable;

public class BlockPoweredAir extends AirBlock {

    public BlockPoweredAir(Properties builder) {
        super(builder);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TilePoweredAir();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public int getWeakPower(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
        TileEntity tile = world.getTileEntity(pos);
        if(!(tile instanceof TilePoweredAir))
            return 0;

        if((((TilePoweredAir) tile).directions.length == 0 ||
                ((TilePoweredAir) tile).directions[0] == side))
            return 15;

        return 0;
    }

    @SuppressWarnings("deprecation")
    @Override
    public int getStrongPower(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
        TileEntity tile = world.getTileEntity(pos);
        if(!(tile instanceof TilePoweredAir))
            return 0;

        Direction[] directions = ((TilePoweredAir) tile).directions;
        if((directions.length == 0 ||
                (directions.length > 1 && directions[1] == side)))
            return 15;

        return 0;
    }

    @Override
    public boolean shouldCheckWeakPower(BlockState state, IWorldReader world, BlockPos pos, Direction side) {
        TileEntity tile = world.getTileEntity(pos);
        if(!(tile instanceof TilePoweredAir))
            return false;

        Direction[] directions = ((TilePoweredAir) tile).directions;
        return ((directions.length == 0 ||
                (directions.length > 1 && directions[1] == side)));
    }

}
