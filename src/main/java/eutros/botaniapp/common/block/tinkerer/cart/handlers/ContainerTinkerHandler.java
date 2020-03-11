package eutros.botaniapp.common.block.tinkerer.cart.handlers;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import eutros.botaniapp.api.carttinkerer.CartTinkerHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.item.minecart.*;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IProperty;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.tileentity.FurnaceTileEntity;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ContainerTinkerHandler extends CartTinkerHandler {

    public ContainerTinkerHandler() {
        super(new Block[]{Blocks.CHEST, Blocks.HOPPER, Blocks.FURNACE}, ChestMinecartEntity.class, HopperMinecartEntity.class, FurnaceMinecartEntity.class);
    }

    private static final BiMap<Block, Class<? extends AbstractMinecartEntity>> cartMapping = HashBiMap.create();

    static {
        cartMapping.put(Blocks.CHEST, ChestMinecartEntity.class);
        cartMapping.put(Blocks.HOPPER, HopperMinecartEntity.class);
        cartMapping.put(Blocks.FURNACE, FurnaceMinecartEntity.class);
    }

    @Override
    public boolean doInsert(BlockPos sourcePos, BlockState sourceState, AbstractMinecartEntity destinationCart, World world) {
        AbstractMinecartEntity cart;
        try {
            Constructor<? extends AbstractMinecartEntity> constructor = cartMapping.get(sourceState.getBlock()).getDeclaredConstructor(World.class, double.class, double.class, double.class);
            cart = constructor.newInstance(world, destinationCart.getX(), destinationCart.getY(), destinationCart.getZ());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            return false;
        }

        TileEntity te = world.getTileEntity(sourcePos);
        if(!(te instanceof LockableTileEntity))
            return false;
        LockableTileEntity inv = (LockableTileEntity) te;
        if(cart instanceof ContainerMinecartEntity) {
            for(int i = 0; i < ((ContainerMinecartEntity) cart).getSizeInventory(); i++) {
                ((ContainerMinecartEntity) cart).setInventorySlotContents(i, inv.removeStackFromSlot(i));
            }
        } else {
            if(te instanceof FurnaceTileEntity) {
                int burnTime = 0;
                Object o = ObfuscationReflectionHelper.getPrivateValue(AbstractFurnaceTileEntity.class, (FurnaceTileEntity) te, "burnTime");
                if (o instanceof Integer)
                    burnTime += (Integer) o;
                ItemStack fuel = ((FurnaceTileEntity) te).removeStackFromSlot(1);
                burnTime += ForgeHooks.getBurnTime(fuel)*2.25;
                ObfuscationReflectionHelper.setPrivateValue(FurnaceMinecartEntity.class, (FurnaceMinecartEntity) cart, burnTime, "fuel");
            }
        }

        return super.doSwap(sourcePos, world.getFluidState(sourcePos).getBlockState(), destinationCart, cart, world);
    }

    @Override
    public boolean doExtract(BlockPos destinationPos, AbstractMinecartEntity sourceCart, World world) {
        AbstractMinecartEntity newCart = new MinecartEntity(world, sourceCart.getX(), sourceCart.getY(), sourceCart.getZ());
        List<ItemStack> contents = Collections.emptyList();
        if(sourceCart instanceof ContainerMinecartEntity) {
            ContainerMinecartEntity cart = (ContainerMinecartEntity) sourceCart;
            contents = IntStream.range(0, cart.getSizeInventory()).mapToObj(cart::removeStackFromSlot).collect(Collectors.toList());
        }

        Block block = cartMapping.inverse().get(sourceCart.getClass());
        BlockState state = block.getDefaultState();

        if(block != Blocks.HOPPER) {
            // Rotate to face the tinkerer.
            BlockPos diff = sourceCart.getPosition().subtract(destinationPos);
            state = rotate(state, Direction.getFacingFromVector(diff.getX(), diff.getY(), diff.getZ()));
        }

        if(block == Blocks.CHEST) {
            // Make the chest waterlogged if there's water there.
            IFluidState fluidState = world.getFluidState(destinationPos);
            if (fluidState.getFluid() == Fluids.WATER)
                state = state.with(ChestBlock.WATERLOGGED, true);
        }

        boolean ret = super.doSwap(destinationPos, state, sourceCart, newCart, world);
        TileEntity te = world.getTileEntity(destinationPos);

        if(sourceCart instanceof ContainerMinecartEntity) {
            if(te instanceof LockableTileEntity) {
                LockableTileEntity inv = (LockableTileEntity) te;
                List<ItemStack> finalContents = contents;
                IntStream.range(0, inv.getSizeInventory()).forEach(i -> inv.setInventorySlotContents(i, finalContents.get(i)));
            }
        }

        return ret;
    }

    @SuppressWarnings("unchecked")
    private static BlockState rotate(BlockState state, Direction direction) {
        for (IProperty<?> prop : state.getProperties()) {
            if (prop.getName().equals("facing") && prop.getValueClass() == Direction.class) {
                IProperty<Direction> facingProp = (IProperty<Direction>) prop;

                if (facingProp.getAllowedValues().contains(direction)) {
                    return state.with(facingProp, direction);
                }
            }
        }
        return state;
    }
}
