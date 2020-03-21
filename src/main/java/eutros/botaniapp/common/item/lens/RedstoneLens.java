package eutros.botaniapp.common.item.lens;

import eutros.botaniapp.common.block.BotaniaPPBlocks;
import eutros.botaniapp.common.block.tile.TilePoweredAir;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import vazkii.botania.api.internal.IManaBurst;

public class RedstoneLens extends ItemLens {

    public RedstoneLens(Properties properties) {
        super(properties);
    }

    @Override
    public boolean collideBurst(IManaBurst burst, RayTraceResult rtr, boolean isManaBlock, boolean dead, ItemStack stack) {
        if(!burst.isFake() && rtr.getType() == RayTraceResult.Type.BLOCK) {
            World world = ((Entity) burst).getEntityWorld();

            if(world.isRemote()) {
                return super.collideBurst(burst, rtr, isManaBlock, dead, stack);
            }

            BlockPos pos = ((BlockRayTraceResult) rtr).getPos();
            BlockPos target;
            for(Direction direction : Direction.values()) {
                target = pos.offset(direction);
                if(world.getBlockState(target).isAir(world, target)) {
                    world.setBlockState(target, BotaniaPPBlocks.poweredAir.getDefaultState(), 2);
                    TileEntity tile = world.getTileEntity(target);
                    if(tile instanceof TilePoweredAir) {
                        ((TilePoweredAir) tile).directions = new Direction[] {direction};
                    }
                    world.neighborChanged(pos, BotaniaPPBlocks.poweredAir, target);
                    return false;
                }
            }

            // Don't have direct access to the sides, try to use strong power instead

            for(Direction dir : Direction.values()) {
                if(world.getBlockState(pos.offset(dir)).isSolid())
                    for(Direction direction : Direction.values()) {
                        target = pos.offset(dir).offset(direction);
                        if(world.getBlockState(target).isAir(world, target)) {
                            world.setBlockState(target, BotaniaPPBlocks.poweredAir.getDefaultState(), 2);
                            TileEntity tile = world.getTileEntity(target);
                            if(tile instanceof TilePoweredAir) {
                                ((TilePoweredAir) tile).directions = new Direction[] {dir, direction};
                            }
                            world.neighborChanged(pos, BotaniaPPBlocks.poweredAir, target);
                            return false;
                        }
                    }
            }

        }
        return super.collideBurst(burst, rtr, isManaBlock, dead, stack);
    }
}
