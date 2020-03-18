package eutros.botaniapp.common.entity.cart;

import eutros.botaniapp.api.internal.config.Configurable;
import eutros.botaniapp.common.utils.Reference;
import eutros.botaniapp.common.utils.proxyworld.BotaniaPPClientWorldProxy;
import eutros.botaniapp.common.utils.proxyworld.BotaniaPPProxyTickList;
import eutros.botaniapp.common.utils.proxyworld.BotaniaPPServerWorldProxy;
import eutros.botaniapp.common.utils.proxyworld.BotaniaPPWorldProxy;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameRules;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerTickList;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.registries.ObjectHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

public class EntityGenericBlockCart extends AbstractMinecartEntity {
    @ObjectHolder(Reference.MOD_ID + ":generic_block_cart")
    public static EntityType<EntityGenericBlockCart> TYPE;

    private static final String GROUND = "ground";
    private static final DataParameter<Integer> GROUND_STATE = EntityDataManager.createKey(EntityGenericBlockCart.class, DataSerializers.VARINT);

    int[] cachedNeighbors = {};

    public World proxyWorld;

    @Configurable(side=ModConfig.Type.CLIENT,
            comment={"Enable this if you're getting crashes featuring ClassCastException-s and BotaniaPPWorldProxy-s.",
            "Please ignore Forge complaining about its event bus being shut down."})
    public static boolean FORCE_CLIENT_WORLD_PROXY = false;

    public EntityGenericBlockCart(World world, double x, double y, double z, @Nullable BlockState state) {
        this(TYPE, world, x, y, z, state);
    }

    public EntityGenericBlockCart(EntityType<?> type, World world) {
        this(type, world, null);
    }

    public EntityGenericBlockCart(EntityType<?> type, World world, double x, double y, double z, @Nullable BlockState state) {
        super(type, world, x, y, z);
        init(world, state);
    }

    public EntityGenericBlockCart(EntityType<?> type, World world, @Nullable BlockState state) {
        super(type, world);
        init(world, state);
    }

    private void init(World world, @Nullable BlockState state) {
        setDisplayTile(state == null ? Blocks.AIR.getDefaultState() : state);
        proxyWorld = getProxyWorld(world);
    }

    protected int[] getNeighboringStates() {
        BlockPos pos = getPosition();
        return new int[] {
            world.getStrongPower(pos.offset(Direction.UP)) + (world.getBlockState(pos.offset(Direction.UP)).hashCode() << 4),
            world.getStrongPower(pos.offset(Direction.DOWN)) + (world.getBlockState(pos.offset(Direction.DOWN)).hashCode() << 4),
            world.getStrongPower(pos.offset(Direction.NORTH)) + (world.getBlockState(pos.offset(Direction.NORTH)).hashCode() << 4),
            world.getStrongPower(pos.offset(Direction.EAST)) + (world.getBlockState(pos.offset(Direction.EAST)).hashCode() << 4),
            world.getStrongPower(pos.offset(Direction.SOUTH)) + (world.getBlockState(pos.offset(Direction.SOUTH)).hashCode() << 4),
            world.getStrongPower(pos.offset(Direction.WEST)) + (world.getBlockState(pos.offset(Direction.WEST)).hashCode() << 4)
        };
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(GROUND_STATE, Block.getStateId(Blocks.AIR.getDefaultState()));
    }

    public void setGroundState(BlockState state) {
        setGroundState(Block.getStateId(state));
    }

    private void setGroundState(int id) {
        this.dataManager.set(GROUND_STATE, id);
    }

    public BlockState getGroundState() {
        return Block.getStateById(this.dataManager.get(GROUND_STATE));
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
    public void tick() {
        if(cachedNeighbors.length == 0) // World is incomplete when first constructed during loading, so this is initialised as empty.
            cachedNeighbors = getNeighboringStates();

        BlockPos oldPos = getPosition();
        super.tick();
        BlockPos pos = getPosition();
        BlockState state = getDisplayTile();
        int[] neighbors = {};
        if(oldPos.equals(pos))
            neighbors = getNeighboringStates();

        if(!oldPos.equals(pos) ||
                !Arrays.equals(cachedNeighbors, neighbors)) {
            BlockState oldState = world.getBlockState(oldPos);
            state.neighborChanged(proxyWorld, pos, oldState.getBlock(), oldPos, true);
            cachedNeighbors = neighbors;
        }
        if(!world.isRemote()) {
            ((ProxyServerWorld) proxyWorld).tick(null);
            if (state.ticksRandomly() &&
                    world.getRandom().nextInt(4096) <= world.getGameRules().getInt(GameRules.RANDOM_TICK_SPEED))
                state.randomTick((ServerWorld) proxyWorld, pos, world.getRandom());
        }
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
                Direction.getFacingDirections(player)[0].getOpposite(),
                getPosition(),
                getBoundingBox().contains(player.getEyePosition(1))));
    }

    @Override
    public boolean attackEntityFrom(@Nonnull DamageSource source, float amount) {
        //noinspection deprecation
        if (!this.world.isRemote && !this.removed) {
            if (this.isInvulnerableTo(source)) {
                return false;
            } else {
                this.setRollingDirection(-this.getRollingDirection());
                this.setRollingAmplitude(10);
                this.markVelocityChanged();

                float damage = 0;
                if(source.isUnblockable()) {
                    damage = amount;
                } else if(canHarvest(source, amount)) {
                    damage = amount;
                }

                this.setDamage(getDamage() + damage * 10F);
                boolean flag = source.getTrueSource() instanceof PlayerEntity && ((PlayerEntity)source.getTrueSource()).abilities.isCreativeMode;
                if (flag || this.getDamage() > 40.0F) {
                    this.removePassengers();
                    this.killMinecart(source);
                }
                return true;
            }
        } else {
            return true;
        }
    }

    @Override
    public void killMinecart(DamageSource source) {
        if(proxyWorld.isRemote())
            return;
        Entity trueSource = source.getTrueSource();

        if(trueSource instanceof PlayerEntity && ((PlayerEntity) trueSource).abilities.isCreativeMode) {
            remove();
        } else {
            super.killMinecart(source);
        }

        BlockState state = getDisplayTile();

        if(trueSource != null) {
            Block.spawnDrops(state, proxyWorld, getPosition(), proxyWorld.getTileEntity(getPosition()), trueSource, getTool(source));
        } else {
            Block.spawnDrops(state, proxyWorld, getPosition(), proxyWorld.getTileEntity(getPosition()));
        }

        state.onReplaced(proxyWorld, getPosition(), Blocks.AIR.getDefaultState(), false);
    }

    private boolean canHarvest(DamageSource source, float amount) {
        ItemStack tool = getTool(source);
        BlockState state = getDisplayTile();
        Vec3d pos = getPositionVec();

        return state.getHarvestLevel() == 0 ||
                source.getTrueSource() instanceof PlayerEntity && ForgeHooks.canHarvestBlock(state, (PlayerEntity) source.getTrueSource(), proxyWorld, getPosition()) ||
                ForgeHooks.canToolHarvestBlock(proxyWorld, getPosition(), tool) ||
                (source.isExplosion() &&
                        state.getExplosionResistance(proxyWorld,
                                getPosition(),
                                source.getImmediateSource(),
                                new Explosion(proxyWorld, source.getImmediateSource(), pos.x, pos.y, pos.z, amount, false, Explosion.Mode.BREAK))
                        < amount);
    }

    @NotNull
    private ItemStack getTool(DamageSource source) {
        ItemStack tool = ItemStack.EMPTY;

        if(!source.isProjectile() && source.getTrueSource() instanceof LivingEntity) {
            tool = ((LivingEntity) source.getTrueSource()).getHeldItem(((LivingEntity) source.getTrueSource()).getActiveHand());
        }
        return tool;
    }

    @Override
    protected void readAdditional(CompoundNBT cmp) {
        super.readAdditional(cmp);
        setGroundState(cmp.getInt(GROUND));
    }

    @Override
    protected void writeAdditional(@NotNull CompoundNBT cmp) {
        super.writeAdditional(cmp);
        cmp.putInt(GROUND, dataManager.get(GROUND_STATE));
    }

    public World getProxyWorld(World world) {
        if(world == null)
            return null;
        if(world instanceof ServerWorld) {
            return getServerWorldFactory().apply((ServerWorld) world);
        }
        if(world instanceof ClientWorld && FORCE_CLIENT_WORLD_PROXY) {
            try {
                LOGGER.warn("Don't mind me..."); // Look, I'm sorry for your eyes, okay?
                MinecraftForge.EVENT_BUS.shutdown(); // This is volatile, so let's just hope no other thread needs the event bus.
                World newWorld = getClientWorldFactory().apply((ClientWorld) world); // <- ClientWorld's initializer posts an event we should ignore.
                MinecraftForge.EVENT_BUS.start();
                return newWorld;
            } catch (Throwable e) {
                MinecraftForge.EVENT_BUS.start();
            }
        }
        return getGenericWorldFactory().apply(world);
    }

    protected Function<ServerWorld, ServerWorld> getServerWorldFactory() {
        return ProxyServerWorld::new;
    }

    protected Function<ClientWorld, ClientWorld> getClientWorldFactory() {
        return ProxyClientWorld::new;
    }

    protected Function<World, World> getGenericWorldFactory() {
        return ProxyWorld::new;
    }

    protected class ProxyServerWorld extends BotaniaPPServerWorldProxy {

        private ProxyTickList tickList;

        public ProxyServerWorld(ServerWorld world) {
            super(world);
            tickList = new ProxyTickList(super.getPendingBlockTicks());
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

        @Override
        public ServerTickList<Block> getPendingBlockTicks() {
            return tickList;
        }

        @Override
        public void tick(@Nullable BooleanSupplier p_72835_1_) {
            tickList.tick();
        }

        class ProxyTickList extends BotaniaPPProxyTickList<Block> {
            private Set<Long> schedule = new HashSet<>();
            private Block target;

            public ProxyTickList(ServerTickList<Block> tickList) {
                super(tickList);
                target = getDisplayTile().getBlock();
            }

            @Override
            public void scheduleTick(BlockPos pos, @NotNull Block in, int ticks, @NotNull TickPriority priority) {
                if (pos.equals(getPosition()) && getDisplayTile().getBlock() == in) {
                    schedule.add((long) ticks + world.getGameTime());
                    return;
                }
                super.scheduleTick(pos, in, ticks, priority);
            }

            @Override
            public void tick() {
                BlockState state = getDisplayTile();
                if(state.getBlock() != target) {
                    schedule.clear();
                    target = state.getBlock();
                } else if(!schedule.isEmpty()) {
                    long time = world.getGameTime();
                    Collection<Long> ticked = new LinkedList<>();

                    for(long l : schedule)
                        if(l <= time) {
                            state.scheduledTick(ProxyServerWorld.this,
                                    getPosition(),
                                    world.getRandom());
                            ticked.add(l);
                        }

                    schedule.removeAll(ticked);
                }
            }
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

    protected class ProxyWorld extends BotaniaPPWorldProxy {
        public ProxyWorld(World world) {
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
