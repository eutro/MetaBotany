package eutros.botaniapp.common.block.tinkerer.cart.handlers;

import eutros.botaniapp.api.carttinkerer.CartTinkerHandler;
import eutros.botaniapp.common.block.BotaniaPPBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.item.minecart.MinecartEntity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.botania.api.mana.IManaPool;
import vazkii.botania.common.entity.EntityPoolMinecart;

public class PoolTinkerHandler extends CartTinkerHandler {

    public PoolTinkerHandler() {
        super(new net.minecraft.block.Block[]{BotaniaPPBlocks.BOTANIA_MANA_POOL}, EntityPoolMinecart.class);
    }

    @Override
    public boolean doInsert(BlockPos sourcePos, BlockState sourceState, AbstractMinecartEntity destinationCart, World world, BlockPos tinkererPos) {
        EntityPoolMinecart cart = new EntityPoolMinecart(world, destinationCart.getX(), destinationCart.getY(), destinationCart.getZ());

        TileEntity te = world.getTileEntity(sourcePos);
        if(!(te instanceof IManaPool))
            return false;
        IManaPool pool = (IManaPool) te;
        cart.setMana(pool.getCurrentMana());

        return doSwap(sourcePos, world.getFluidState(sourcePos).getBlockState(), destinationCart, cart, world, tinkererPos);
    }

    @Override
    public boolean doExtract(BlockPos destinationPos, BlockState destinationState, AbstractMinecartEntity sourceCart, World world, BlockPos tinkererPos) {
        AbstractMinecartEntity newCart = new MinecartEntity(world, sourceCart.getX(), sourceCart.getY(), sourceCart.getZ());
        EntityPoolMinecart poolCart = (EntityPoolMinecart) sourceCart;

        BlockState state = BotaniaPPBlocks.BOTANIA_MANA_POOL.getDefaultState();

        boolean ret = doSwap(destinationPos, state, sourceCart, newCart, world, tinkererPos);

        TileEntity te = world.getTileEntity(destinationPos);
        if(te instanceof IManaPool) {
            IManaPool pool = (IManaPool) te;
            ((ITickableTileEntity) te).tick(); // Max mana isn't set until the first tick.
            pool.recieveMana(poolCart.getMana());
        }

        return ret;
    }
}
