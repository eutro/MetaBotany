package eutros.botaniapp.common.entity.cart;

import eutros.botaniapp.common.utils.Reference;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.registries.ObjectHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class EntityGenericBlockCart extends AbstractMinecartEntity {
    @ObjectHolder(Reference.MOD_ID + ":generic_block_cart")
    public static EntityType<EntityGenericBlockCart> TYPE;

    public EntityGenericBlockCart(World world, double x, double y, double z, @Nullable BlockState state) {
        this(TYPE, world, x, y, z, state);
    }

    public EntityGenericBlockCart(World world) {
        this(TYPE, world);
    }

    public EntityGenericBlockCart(EntityType<?> type, World world) {
        this(type, world, null);
    }

    public EntityGenericBlockCart(EntityType<?> type, World world, double x, double y, double z, @Nullable BlockState state) {
        super(type, world, x, y, z);
        setDisplayTile(state);
    }

    public EntityGenericBlockCart(EntityType<?> type, World world, @Nullable BlockState state) {
        super(type, world);
        setDisplayTile(state);
    }

    @Nonnull
    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @NotNull
    @Override
    public Type getMinecartType() {
        return Type.RIDEABLE;
    }

    @Override
    public boolean hasDisplayTile() {
        return true;
    }

    @Override
    public boolean canBeRidden() {
        return false;
    }

    @Nonnull
    @Override
    public ItemStack getPickedResult(RayTraceResult target) {
        return getDisplayTile().getPickBlock(target, world, getPosition(), null);
    }

    @Override
    protected void applyDrag() { // Blocks are heavy.
        float f = 0.95F;
        this.setMotion(getMotion().mul(f, 0, f));
    }

    @Override
    public void killMinecart(DamageSource source) {
        super.killMinecart(source);
        entityDropItem(getDisplayTile().getBlock(), 0);
    }

}
