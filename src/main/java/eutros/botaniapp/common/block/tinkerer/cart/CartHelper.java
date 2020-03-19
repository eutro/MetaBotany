package eutros.botaniapp.common.block.tinkerer.cart;

import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.item.minecart.MinecartEntity;
import net.minecraft.world.World;

public class CartHelper {

    public static void switchCarts(World world, AbstractMinecartEntity oldCart, AbstractMinecartEntity newCart) {
        copyFrom(oldCart, newCart);
        world.addEntity(newCart);
        oldCart.remove(false);
    }

    public static void swapCartPositions(AbstractMinecartEntity cartA, AbstractMinecartEntity cartB) {
        MinecartEntity bufferCart = new MinecartEntity(cartA.getEntityWorld(), cartB.getX(), cartB.getY(), cartB.getZ());

        int mask = 0b0001;
        copyFrom(cartB, bufferCart, mask);
        copyFrom(cartA, cartB, mask);
        copyFrom(bufferCart, cartA, mask);
        bufferCart.remove();
    }

    private static void copyFrom(AbstractMinecartEntity from, AbstractMinecartEntity to) {
        copyFrom(from, to, 0xF);
    }

    /**
     * {@code 0001} - position
     * {@code 0010} - motion
     * {@code 0100} - rotation
     * {@code 1000} - anything else
     */
    private static void copyFrom(AbstractMinecartEntity from, AbstractMinecartEntity to, int mask) {
        if((mask & 0b0001) != 0) {
            to.setPosition(from.getX(), from.getY(), from.getZ());
            to.prevPosX = from.prevPosX;
            to.prevPosY = from.prevPosY;
            to.prevPosZ = from.prevPosZ;
            to.chunkCoordX = from.chunkCoordX;
            to.chunkCoordY = from.chunkCoordY;
            to.chunkCoordZ = from.chunkCoordZ;
            to.lastTickPosX = from.lastTickPosX;
            to.lastTickPosY = from.lastTickPosY;
            to.lastTickPosZ = from.lastTickPosZ;
        }

        if((mask & 0b0010) != 0) {
            to.setMotion(from.getMotion());
            to.setRollingAmplitude(from.getRollingAmplitude());
            to.setRollingDirection(from.getRollingDirection());
        }

        if((mask & 0b0100) != 0) {
            to.rotationPitch = from.rotationPitch;
            to.rotationYaw = from.rotationYaw;
            to.prevRotationPitch = from.prevRotationYaw;
            to.prevRotationYaw = from.prevRotationPitch;
        }

        if((mask & 0b1000) != 0) {
            to.setDamage(from.getDamage());
            to.setGlowing(from.isGlowing());
            from.getTags().forEach(to::addTag);
        }
    }

}
