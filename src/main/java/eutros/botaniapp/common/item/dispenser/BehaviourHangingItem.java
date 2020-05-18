package eutros.botaniapp.common.item.dispenser;

import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class BehaviourHangingItem extends DefaultDispenseItemBehavior {

    @Nonnull
    @Override
    protected ItemStack dispenseStack(IBlockSource source, @Nonnull ItemStack stack) {
        World world = source.getWorld();
        BlockPos pos = source.getBlockPos();
        Direction facing = world.getBlockState(pos).get(DispenserBlock.FACING);

        BlockPos wallPos = pos.offset(facing);
        for(int i = 2; i < 4 && world.getBlockState(wallPos).isAir(world, wallPos); i++) {
            wallPos = pos.offset(facing, i);
        }

        ActionResultType result = stack.getItem()
                .onItemUse(
                        new UseContext(world,
                                stack,
                                new BlockRayTraceResult(
                                        new Vec3d(0.5, 0.5, 0.5),
                                        facing.getOpposite(),
                                        wallPos,
                                        false
                                )
                        )
                );

        if(!result.isSuccess()) {
            return super.dispenseStack(source, stack);
        }

        return stack;
    }

    private static class UseContext extends ItemUseContext {

        public UseContext(World world, ItemStack stack, BlockRayTraceResult rayTraceResultIn) {
            super(world, null, Hand.MAIN_HAND, stack, rayTraceResultIn);
        }

    }

}
