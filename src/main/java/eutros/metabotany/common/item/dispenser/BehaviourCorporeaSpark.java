package eutros.metabotany.common.item.dispenser;

import eutros.metabotany.common.item.MetaBotanyItems;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import vazkii.botania.api.corporea.CorporeaHelper;

public class BehaviourCorporeaSpark extends DefaultDispenseItemBehavior {

    @NotNull
    @Override
    protected ItemStack dispenseStack(IBlockSource source, @NotNull ItemStack stack) {
        World world = source.getWorld();
        Direction facing = world.getBlockState(source.getBlockPos()).get(DispenserBlock.FACING);
        BlockPos pos = source.getBlockPos().offset(facing);
        TileEntity tile = world.getTileEntity(pos);
        if(tile != null
                && (tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP).isPresent()
                || tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).isPresent())
                && !CorporeaHelper.instance().doesBlockHaveSpark(world, pos)) {
            stack.shrink(1);
            if(!world.isRemote) {
                EntityType<?> sparkType = ForgeRegistries.ENTITIES.getValue(new ResourceLocation("botania", "corporea_spark"));

                if(sparkType == null) return super.dispenseStack(source, stack);

                Entity spark = sparkType.create(world);

                if(spark == null) return super.dispenseStack(source, stack);

                if(stack.getItem() == MetaBotanyItems.BOTANIA_CORPOREA_SPARK_MASTER) {
                    DataParameter<Boolean> key = EntityDataManager.createKey(spark.getClass(), DataSerializers.BOOLEAN);
                    spark.getDataManager().set(key, true);
                }
                spark.setPosition(pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5);
                world.addEntity(spark);
            }
            return stack;
        }
        return super.dispenseStack(source, stack);
    }

}
