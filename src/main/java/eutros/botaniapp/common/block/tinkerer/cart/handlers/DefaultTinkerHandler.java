package eutros.botaniapp.common.block.tinkerer.cart.handlers;

import eutros.botaniapp.api.carttinkerer.CartTinkerHandler;
import eutros.botaniapp.common.entity.cart.EntityGenericBlockCart;
import eutros.botaniapp.common.entity.cart.EntityGenericTileEntityCart;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.item.minecart.MinecartEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DefaultTinkerHandler extends CartTinkerHandler {

    public DefaultTinkerHandler() {
        super(new BlockState[]{});
    }

    @Override
    public boolean doInsert(BlockPos sourcePos, BlockState sourceState, AbstractMinecartEntity destinationCart, World world) {
        // TODO block blacklist/whitelist
        if(sourceState.isAir(world, sourcePos)) {
            return false;
        }
        TileEntity te = world.getTileEntity(sourcePos);
        EntityGenericBlockCart cart;
        if(te == null)
            cart = new EntityGenericBlockCart(world, destinationCart.getX(), destinationCart.getY(), destinationCart.getZ(), sourceState);
        else {
            cart = new EntityGenericTileEntityCart(world, destinationCart.getX(), destinationCart.getY(), destinationCart.getZ(), sourceState, te);
            te.write(new CompoundNBT()); // Completely and utterly remove the TE from existence.
            te.remove();
        }

        return doSwap(sourcePos, world.getFluidState(sourcePos).getBlockState(), destinationCart, cart, world);
    }

    @Override
    public boolean doExtract(BlockPos destinationPos, AbstractMinecartEntity sourceCart, World world) {
        BlockState state = sourceCart.getDisplayTile();
        AbstractMinecartEntity cart = new MinecartEntity(world, sourceCart.getX(), sourceCart.getY(), sourceCart.getZ());
        boolean ret = doSwap(destinationPos, state, sourceCart, cart, world);
        if(sourceCart instanceof EntityGenericTileEntityCart) {
            TileEntity tile = ((EntityGenericTileEntityCart) sourceCart).getTile();
            tile.validate();
            tile.setLocation(world, destinationPos);
            world.setTileEntity(destinationPos, tile);
        }
        return ret;
    }
}
