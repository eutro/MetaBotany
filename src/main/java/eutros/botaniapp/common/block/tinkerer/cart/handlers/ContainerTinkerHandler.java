package eutros.botaniapp.common.block.tinkerer.cart.handlers;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import eutros.botaniapp.api.carttinkerer.CartTinkerHandler;
import eutros.botaniapp.api.internal.config.Configurable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.item.minecart.*;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IProperty;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ContainerTinkerHandler extends CartTinkerHandler {

    @Configurable(path={"cart_tinkerer"},
            comment={"If true, disable tinkering of chests to chest minecarts."})
    public static boolean DISABLE_CHEST = false;

    @Configurable(path={"cart_tinkerer"},
            comment={"If true, disable tinkering of furnaces to furnace minecarts."})
    public static boolean DISABLE_FURNACE = false;

    @Configurable(path={"cart_tinkerer"},
            comment={"If true, disable tinkering of hoppers to hopper minecarts."})
    public static boolean DISABLE_HOPPER = false;

    public ContainerTinkerHandler() {
        super(new Block[]{Blocks.CHEST, Blocks.HOPPER, Blocks.FURNACE}, ChestMinecartEntity.class, HopperMinecartEntity.class, FurnaceMinecartEntity.class);
    }

    private static final BiMap<Block, AbstractMinecartEntity.Type> cartMapping = HashBiMap.create();

    static {
        cartMapping.put(Blocks.CHEST, AbstractMinecartEntity.Type.CHEST);
        cartMapping.put(Blocks.HOPPER, AbstractMinecartEntity.Type.HOPPER);
        cartMapping.put(Blocks.FURNACE, AbstractMinecartEntity.Type.FURNACE);
    }

    @Override
    public boolean doInsert(BlockPos sourcePos, BlockState sourceState, AbstractMinecartEntity destinationCart, World world, BlockPos tinkererPos) {
        Block block = sourceState.getBlock();
        if(DISABLE_CHEST && block == Blocks.CHEST ||
                DISABLE_FURNACE && block == Blocks.FURNACE ||
                DISABLE_HOPPER && block == Blocks.HOPPER)
            return false;

        AbstractMinecartEntity cart = AbstractMinecartEntity.create(world,
                destinationCart.getX(),
                destinationCart.getY(),
                destinationCart.getZ(),
                cartMapping.get(sourceState.getBlock()));

        TileEntity te = world.getTileEntity(sourcePos);
        if(!(te instanceof LockableTileEntity))
            return false;

        LockableTileEntity inv = (LockableTileEntity) te;
        if(cart instanceof ContainerMinecartEntity) {
            for(int i = 0; i < ((ContainerMinecartEntity) cart).getSizeInventory(); i++) {
                ((ContainerMinecartEntity) cart).setInventorySlotContents(i, inv.removeStackFromSlot(i));
            }
        }

        return doSwap(sourcePos, world.getFluidState(sourcePos).getBlockState(), destinationCart, cart, world, tinkererPos);
    }

    @Override
    public boolean doExtract(BlockPos destinationPos, BlockState destinationState, AbstractMinecartEntity sourceCart, World world, BlockPos tinkererPos) {
        AbstractMinecartEntity newCart = new MinecartEntity(world, sourceCart.getX(), sourceCart.getY(), sourceCart.getZ());
        List<ItemStack> contents = Collections.emptyList();
        if(sourceCart instanceof ContainerMinecartEntity) {
            ContainerMinecartEntity cart = (ContainerMinecartEntity) sourceCart;
            contents = IntStream.range(0, cart.getSizeInventory()).mapToObj(cart::removeStackFromSlot).collect(Collectors.toList());
        }

        Block block = cartMapping.inverse().get(sourceCart.getType());
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

        boolean ret = doSwap(destinationPos, state, sourceCart, newCart, world, tinkererPos);
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
