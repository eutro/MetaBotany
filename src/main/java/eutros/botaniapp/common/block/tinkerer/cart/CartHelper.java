package eutros.botaniapp.common.block.tinkerer.cart;

import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.world.World;

public class CartHelper {

    public static void switchCarts(World world, AbstractMinecartEntity oldCart, AbstractMinecartEntity newCart) {
        newCart.setMotion(oldCart.getMotion());
        newCart.setRollingAmplitude(oldCart.getRollingAmplitude());
        newCart.setRollingDirection(oldCart.getRollingDirection());
        newCart.setDamage(oldCart.getDamage());
        newCart.setPos(oldCart.getX(),
                       oldCart.getY(),
                       oldCart.getZ());
        newCart.prevPosX = oldCart.prevPosX;
        newCart.prevPosY = oldCart.prevPosY;
        newCart.prevPosZ = oldCart.prevPosZ;
        newCart.lastTickPosX = oldCart.lastTickPosX;
        newCart.lastTickPosY = oldCart.lastTickPosY;
        newCart.lastTickPosZ = oldCart.lastTickPosZ;
        newCart.rotationPitch = oldCart.rotationPitch;
        newCart.rotationYaw = oldCart.rotationYaw;
        newCart.prevRotationPitch = oldCart.prevRotationYaw;
        newCart.prevRotationYaw = oldCart.prevRotationPitch;
        newCart.chunkCoordX = oldCart.chunkCoordX;
        newCart.chunkCoordY = oldCart.chunkCoordY;
        newCart.chunkCoordZ = oldCart.chunkCoordZ;
        newCart.setGlowing(oldCart.isGlowing());
        oldCart.getTags().forEach(newCart::addTag);
        world.addEntity(newCart);
        oldCart.remove(false);
    }

}
