package eutros.botaniapp.common.item.dispenser;

import eutros.botaniapp.common.item.BotaniaPPItems;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import org.jetbrains.annotations.NotNull;
import vazkii.botania.api.corporea.CorporeaHelper;
import vazkii.botania.common.entity.EntityCorporeaSpark;

public class BehaviourCorporeaSpark extends DefaultDispenseItemBehavior {

    @NotNull
    @Override
    protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
        World world = source.getWorld();
        Direction facing = world.getBlockState(source.getBlockPos()).get(DispenserBlock.FACING);
        BlockPos pos = source.getBlockPos().offset(facing);
        TileEntity tile = world.getTileEntity(pos);
        if(tile != null
                && (tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP).isPresent()
                || tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).isPresent())
                && !CorporeaHelper.doesBlockHaveSpark(world, pos)) {
            stack.shrink(1);
            if(!world.isRemote) {
                EntityCorporeaSpark spark = new EntityCorporeaSpark(world);
                if(stack.getItem() == BotaniaPPItems.BOTANIA_CORPOREA_SPARK_MASTER)
                    spark.setMaster(true);
                spark.setPosition(pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5);
                world.addEntity(spark);
            }
            return stack;
        }
        return super.dispenseStack(source, stack);
    }
}
