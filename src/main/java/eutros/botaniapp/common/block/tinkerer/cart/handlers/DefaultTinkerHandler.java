package eutros.botaniapp.common.block.tinkerer.cart.handlers;

import eutros.botaniapp.api.carttinkerer.CartTinkerHandler;
import eutros.botaniapp.common.entity.cart.EntityGenericBlockCart;
import eutros.botaniapp.common.entity.cart.EntityGenericTileEntityCart;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.item.minecart.MinecartEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.IProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

import static net.minecraft.state.properties.BlockStateProperties.*;

public class DefaultTinkerHandler extends CartTinkerHandler {

    public DefaultTinkerHandler() {
        super(new BlockState[]{});
    }

    private static final Set<IProperty<?>> propertyBlacklist = new HashSet<>();

    static {
        propertyBlacklist.add(DOUBLE_BLOCK_HALF);
        propertyBlacklist.add(BED_PART);
    }

    @Override
    public boolean doInsert(BlockPos sourcePos, BlockState sourceState, AbstractMinecartEntity destinationCart, World world, BlockPos tinkererPos) {
        // TODO block blacklist/whitelist
        if(sourceState.isAir(world, sourcePos)) {
            return false;
        }

        if(sourceState.getProperties().stream().map(propertyBlacklist::contains).reduce(Boolean::logicalOr).orElse(false))
            return false;

        if(sourceState.has(WATERLOGGED) && sourceState.get(WATERLOGGED))
            sourceState = sourceState.with(WATERLOGGED, false);

        TileEntity te = world.getTileEntity(sourcePos);
        EntityGenericBlockCart cart;
        if(te == null)
            cart = new EntityGenericBlockCart(world, destinationCart.getX(), destinationCart.getY(), destinationCart.getZ(), sourceState);
        else {
            cart = new EntityGenericTileEntityCart(world, destinationCart.getX(), destinationCart.getY(), destinationCart.getZ(), sourceState, te);
            te.write(new CompoundNBT()); // Completely and utterly remove the TE from existence.
            te.remove();
        }

        return doSwap(sourcePos, world.getFluidState(sourcePos).getBlockState(), destinationCart, cart, world, tinkererPos);
    }

    @Override
    public boolean doExtract(BlockPos destinationPos, BlockState destinationState, AbstractMinecartEntity sourceCart, World world, BlockPos tinkererPos) {
        BlockState state = sourceCart.getDisplayTile();

        IFluidState fluidState = world.getFluidState(destinationPos);
        if(state.has(WATERLOGGED) && fluidState.isSource() && fluidState.getFluid() == Fluids.WATER)
            state = state.with(WATERLOGGED, true);

        AbstractMinecartEntity cart = new MinecartEntity(world, sourceCart.getX(), sourceCart.getY(), sourceCart.getZ());
        boolean ret = doSwap(destinationPos, state, sourceCart, cart, world, tinkererPos);
        if(sourceCart instanceof EntityGenericTileEntityCart) {
            TileEntity tile = ((EntityGenericTileEntityCart) sourceCart).getTile();
            tile.validate();
            tile.setLocation(world, destinationPos);
            world.setTileEntity(destinationPos, tile);
        }
        return ret;
    }
}
