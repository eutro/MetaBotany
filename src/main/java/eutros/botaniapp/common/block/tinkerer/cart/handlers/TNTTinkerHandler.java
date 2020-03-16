package eutros.botaniapp.common.block.tinkerer.cart.handlers;

import eutros.botaniapp.api.carttinkerer.CartTinkerHandler;
import eutros.botaniapp.api.internal.config.Configurable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.item.minecart.MinecartEntity;
import net.minecraft.entity.item.minecart.TNTMinecartEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class TNTTinkerHandler extends CartTinkerHandler {

    @Configurable(path={"cart_tinkerer"},
            comment={"If true, disable tinkering of TNT to TNT minecarts."})
    public static boolean DISABLE_TNT = false;

    public TNTTinkerHandler() {
        super(new net.minecraft.block.Block[]{Blocks.TNT}, TNTMinecartEntity.class);
    }

    @Override
    public boolean doInsert(BlockPos sourcePos, BlockState sourceState, AbstractMinecartEntity destinationCart, World world, BlockPos tinkererPos) {
        if(DISABLE_TNT)
            return false;

        TNTMinecartEntity cart = new TNTMinecartEntity(world, destinationCart.getX(), destinationCart.getY(), destinationCart.getZ());

        return doSwap(sourcePos, world.getFluidState(sourcePos).getBlockState(), destinationCart, cart, world, tinkererPos);
    }

    @Override
    public boolean doExtract(BlockPos destinationPos, BlockState destinationState, AbstractMinecartEntity sourceCart, World world, BlockPos tinkererPos) {
        AbstractMinecartEntity newCart = new MinecartEntity(world, sourceCart.getX(), sourceCart.getY(), sourceCart.getZ());
        TNTMinecartEntity tntCart = (TNTMinecartEntity) sourceCart;

        int fuse = -1;
        Object fuze = ObfuscationReflectionHelper.getPrivateValue(TNTMinecartEntity.class, tntCart, "minecartTNTFuse");
        if(fuze instanceof Integer)
            fuse = (Integer) fuze;

        BlockState state = fuse >= 0 ? null : Blocks.TNT.getDefaultState();

        if(fuse >= 0) {
            TNTEntity tnt = new TNTEntity(world, destinationPos.getX(), destinationPos.getY(), destinationPos.getZ(), null);
            tnt.setFuse(fuse);
            world.addEntity(tnt);
        }

        return doSwap(destinationPos, state, sourceCart, newCart, world, tinkererPos);
    }
}
