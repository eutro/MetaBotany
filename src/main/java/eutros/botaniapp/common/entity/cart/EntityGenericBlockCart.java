package eutros.botaniapp.common.entity.cart;

import eutros.botaniapp.common.utils.Reference;
import eutros.botaniapp.common.utils.proxyworld.BotaniaPPClientWorldProxy;
import eutros.botaniapp.common.utils.proxyworld.BotaniaPPServerWorldProxy;
import eutros.botaniapp.common.utils.proxyworld.BotaniaPPWorldProxy;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.EventBus;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.registries.ObjectHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;

public class EntityGenericBlockCart extends AbstractMinecartEntity {
    @ObjectHolder(Reference.MOD_ID + ":generic_block_cart")
    public static EntityType<EntityGenericBlockCart> TYPE;

    public World proxyWorld;

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
        proxyWorld = getProxyWorld(world);
    }

    public EntityGenericBlockCart(EntityType<?> type, World world, @Nullable BlockState state) {
        super(type, world);
        setDisplayTile(state);
        proxyWorld = getProxyWorld(world);
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
    public boolean processInitialInteract(PlayerEntity player, Hand hand) {
        return !player.shouldCancelInteraction() && !world.isRemote;
    }

    @NotNull
    @Override
    public ActionResultType applyPlayerInteraction(PlayerEntity player, Vec3d vec, Hand hand) {
        return getDisplayTile().onUse(proxyWorld, player, hand, new BlockRayTraceResult(vec,
                Direction.getFacingDirections(player)[0],
                getPosition(),
                getBoundingBox().contains(player.getEyePosition(1))));
    }

    @Override
    public void tick() {
        super.tick();
        if(!world.isRemote() &&
                getDisplayTile().ticksRandomly() &&
                world.getRandom().nextInt(4096) <= world.getGameRules().getInt(GameRules.RANDOM_TICK_SPEED))
            getDisplayTile().randomTick((ServerWorld) proxyWorld, getPosition(), world.getRandom());
    }

    @Override
    public void killMinecart(DamageSource source) {
        super.killMinecart(source);
        entityDropItem(getDisplayTile().getBlock(), 0);
        getDisplayTile().onReplaced(proxyWorld, getPosition(), Blocks.AIR.getDefaultState(), false);
    }

    public World getProxyWorld(World world) {
        if(world == null)
            return null;
        if(world instanceof ServerWorld) {
            return new ProxyServerWorld((ServerWorld) world);
        }
        if(world instanceof ClientWorld) {
            try {
                Field shutdown = EventBus.class.getDeclaredField("shutdown");
                shutdown.set(MinecraftForge.EVENT_BUS, false);
                ProxyClientWorld proxyClientWorld = new ProxyClientWorld((ClientWorld) world); // <- ClientWorld's initializer posts an event we should ignore.
                MinecraftForge.EVENT_BUS.start();
                return proxyClientWorld;
            } catch (Throwable e) {
                MinecraftForge.EVENT_BUS.start();
            }
        }
        return new BotaniaPPWorldProxy(world);
    }

    protected class ProxyServerWorld extends BotaniaPPServerWorldProxy {
        public ProxyServerWorld(ServerWorld world) {
            super(world);
        }

        @Override
        public BlockState getBlockState(BlockPos pos) {
            return pos.equals(getPosition()) ? getDisplayTile() : super.getBlockState(pos);
        }

        @Override
        public IFluidState getFluidState(BlockPos pos) {
            return pos.equals(getPosition()) ? getDisplayTile().getFluidState() : super.getFluidState(pos);
        }

        @Override
        public boolean setBlockState(BlockPos pos, BlockState state, int flags) {
            if(pos.equals(getPosition())) {
                setDisplayTile(state);
                return true;
            }
            return super.setBlockState(pos, state, flags);
        }
    }

    protected class ProxyClientWorld extends BotaniaPPClientWorldProxy {
        public ProxyClientWorld(ClientWorld world) {
            super(world);
        }

        @Override
        public BlockState getBlockState(BlockPos pos) {
            return pos.equals(getPosition()) ? getDisplayTile() : super.getBlockState(pos);
        }

        @Override
        public IFluidState getFluidState(BlockPos pos) {
            return pos.equals(getPosition()) ? getDisplayTile().getFluidState() : super.getFluidState(pos);
        }

        @Override
        public boolean setBlockState(BlockPos pos, BlockState state, int flags) {
            if(pos.equals(getPosition())) {
                setDisplayTile(state);
                return true;
            }
            return super.setBlockState(pos, state, flags);
        }
    }
}
