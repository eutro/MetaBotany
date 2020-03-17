package eutros.botaniapp.common.utils.proxyworld;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.profiler.IProfiler;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.NetworkTagManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.*;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.*;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.lighting.WorldLightManager;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

@SuppressWarnings({"NullableProblems", "deprecation", "ConstantConditions"})
public class BotaniaPPClientWorldProxy extends ClientWorld {
    private ClientWorld innerWorld;

    public BotaniaPPClientWorldProxy(ClientWorld world) {
        super(Minecraft.getInstance().getConnection(),
                new WorldSettings(world.getWorldInfo()),
                world.getDimension().getType(),
                0,
                world.getProfiler(),
                null);
        this.innerWorld = world;
    }

    @Override
    public void tick(BooleanSupplier p_72835_1_) {
        innerWorld.tick(p_72835_1_);
    }

    @Override
    public Iterable<Entity> getAllEntities() {
        return innerWorld.getAllEntities();
    }

    @Override
    public void tickEntities() {
        innerWorld.tickEntities();
    }

    @Override
    public void func_217418_a(Entity p_217418_1_) {
        innerWorld.func_217418_a(p_217418_1_);
    }

    @Override
    public void func_217420_a(Entity p_217420_1_, Entity p_217420_2_) {
        innerWorld.func_217420_a(p_217420_1_, p_217420_2_);
    }

    @Override
    public void func_217423_b(Entity p_217423_1_) {
        innerWorld.func_217423_b(p_217423_1_);
    }

    @Override
    public void onChunkUnloaded(Chunk p_217409_1_) {
        innerWorld.onChunkUnloaded(p_217409_1_);
    }

    @Override
    public void resetChunkColor(int p_228323_1_, int p_228323_2_) {
        innerWorld.resetChunkColor(p_228323_1_, p_228323_2_);
    }

    @Override
    public void reloadColor() {
        innerWorld.reloadColor();
    }

    @Override
    public int func_217425_f() {
        return innerWorld.func_217425_f();
    }

    @Override
    public void addLightning(LightningBoltEntity p_217410_1_) {
        innerWorld.addLightning(p_217410_1_);
    }

    @Override
    public void addPlayer(int p_217408_1_, AbstractClientPlayerEntity p_217408_2_) {
        innerWorld.addPlayer(p_217408_1_, p_217408_2_);
    }

    @Override
    public void addEntity(int p_217411_1_, Entity p_217411_2_) {
        innerWorld.addEntity(p_217411_1_, p_217411_2_);
    }

    @Override
    public void removeEntityFromWorld(int p_217413_1_) {
        innerWorld.removeEntityFromWorld(p_217413_1_);
    }

    @Override
    public void addEntitiesToChunk(Chunk p_217417_1_) {
        innerWorld.addEntitiesToChunk(p_217417_1_);
    }

    @Override
    public void invalidateRegionAndSetBlock(BlockPos p_195597_1_, BlockState p_195597_2_) {
        innerWorld.invalidateRegionAndSetBlock(p_195597_1_, p_195597_2_);
    }

    @Override
    public void animateTick(int p_73029_1_, int p_73029_2_, int p_73029_3_) {
        innerWorld.animateTick(p_73029_1_, p_73029_2_, p_73029_3_);
    }

    @Override
    public void animateTick(int p_184153_1_, int p_184153_2_, int p_184153_3_, int p_184153_4_, Random p_184153_5_, boolean p_184153_6_, BlockPos.Mutable p_184153_7_) {
        innerWorld.animateTick(p_184153_1_, p_184153_2_, p_184153_3_, p_184153_4_, p_184153_5_, p_184153_6_, p_184153_7_);
    }

    @Override
    public void removeAllEntities() {
        innerWorld.removeAllEntities();
    }

    @Override
    public void playSound(BlockPos p_184156_1_, SoundEvent p_184156_2_, SoundCategory p_184156_3_, float p_184156_4_, float p_184156_5_, boolean p_184156_6_) {
        innerWorld.playSound(p_184156_1_, p_184156_2_, p_184156_3_, p_184156_4_, p_184156_5_, p_184156_6_);
    }

    @Override
    public void setScoreboard(Scoreboard p_96443_1_) {
        innerWorld.setScoreboard(p_96443_1_);
    }

    @Override
    public void markSurroundingsForRerender(int p_217427_1_, int p_217427_2_, int p_217427_3_) {
        innerWorld.markSurroundingsForRerender(p_217427_1_, p_217427_2_, p_217427_3_);
    }

    @Override
    public float func_228326_g_(float p_228326_1_) {
        return innerWorld.func_228326_g_(p_228326_1_);
    }

    @Override
    public Vec3d func_228318_a_(BlockPos p_228318_1_, float p_228318_2_) {
        return innerWorld.func_228318_a_(p_228318_1_, p_228318_2_);
    }

    @Override
    public Vec3d getCloudsColor(float p_228328_1_) {
        return innerWorld.getCloudsColor(p_228328_1_);
    }

    @Override
    public Vec3d getFogColor(float p_228329_1_) {
        return innerWorld.getFogColor(p_228329_1_);
    }

    @Override
    public float func_228330_j_(float p_228330_1_) {
        return innerWorld.func_228330_j_(p_228330_1_);
    }

    @Override
    public double getSkyDarknessHeight() {
        return innerWorld.getSkyDarknessHeight();
    }

    @Override
    public int getLightningTicksLeft() {
        return innerWorld.getLightningTicksLeft();
    }

    @Override
    public int calculateColor(BlockPos p_228321_1_, ColorResolver p_228321_2_) {
        return innerWorld.calculateColor(p_228321_1_, p_228321_2_);
    }

    @Override
    public void notifyBlockUpdate(BlockPos p_184138_1_, BlockState p_184138_2_, BlockState p_184138_3_, int p_184138_4_) {
        innerWorld.notifyBlockUpdate(p_184138_1_, p_184138_2_, p_184138_3_, p_184138_4_);
    }

    @Override
    public void playSound(@Nullable PlayerEntity p_184148_1_, double p_184148_2_, double p_184148_4_, double p_184148_6_, SoundEvent p_184148_8_, SoundCategory p_184148_9_, float p_184148_10_, float p_184148_11_) {
        innerWorld.playSound(p_184148_1_, p_184148_2_, p_184148_4_, p_184148_6_, p_184148_8_, p_184148_9_, p_184148_10_, p_184148_11_);
    }

    @Override
    public void playMovingSound(@Nullable PlayerEntity p_217384_1_, Entity p_217384_2_, SoundEvent p_217384_3_, SoundCategory p_217384_4_, float p_217384_5_, float p_217384_6_) {
        innerWorld.playMovingSound(p_217384_1_, p_217384_2_, p_217384_3_, p_217384_4_, p_217384_5_, p_217384_6_);
    }

    @Nullable
    @Override
    public Entity getEntityByID(int p_73045_1_) {
        return innerWorld.getEntityByID(p_73045_1_);
    }

    @Nullable
    @Override
    public MapData getMapData(String p_217406_1_) {
        return innerWorld.getMapData(p_217406_1_);
    }

    @Override
    public void registerMapData(MapData p_217399_1_) {
        innerWorld.registerMapData(p_217399_1_);
    }

    @Override
    public int getNextMapId() {
        return innerWorld.getNextMapId();
    }

    @Override
    public void sendBlockBreakProgress(int p_175715_1_, BlockPos p_175715_2_, int p_175715_3_) {
        innerWorld.sendBlockBreakProgress(p_175715_1_, p_175715_2_, p_175715_3_);
    }

    @Override
    public Scoreboard getScoreboard() {
        return innerWorld.getScoreboard();
    }

    @Override
    public RecipeManager getRecipeManager() {
        return innerWorld.getRecipeManager();
    }

    @Override
    public NetworkTagManager getTags() {
        return innerWorld.getTags();
    }

    @Override
    public float getCurrentMoonPhaseFactor() {
        return innerWorld.getCurrentMoonPhaseFactor();
    }

    @Override
    public int getMoonPhase() {
        return innerWorld.getMoonPhase();
    }

    @Override
    public ITickList<Block> getPendingBlockTicks() {
        return innerWorld.getPendingBlockTicks();
    }

    @Override
    public ITickList<Fluid> getPendingFluidTicks() {
        return innerWorld.getPendingFluidTicks();
    }

    @Override
    public Difficulty getDifficulty() {
        return innerWorld.getDifficulty();
    }

    @Override
    public boolean chunkExists(int p_217354_1_, int p_217354_2_) {
        return innerWorld.chunkExists(p_217354_1_, p_217354_2_);
    }

    @Override
    public Biome getBiome(BlockPos p_226691_1_) {
        return innerWorld.getBiome(p_226691_1_);
    }

    @Override
    public int getColor(BlockPos p_225525_1_, ColorResolver p_225525_2_) {
        return innerWorld.getColor(p_225525_1_, p_225525_2_);
    }

    @Override
    public int getLightLevel(LightType p_226658_1_, BlockPos p_226658_2_) {
        return innerWorld.getLightLevel(p_226658_1_, p_226658_2_);
    }

    @Override
    public int getBaseLightLevel(BlockPos p_226659_1_, int p_226659_2_) {
        return innerWorld.getBaseLightLevel(p_226659_1_, p_226659_2_);
    }

    @Override
    public boolean isSkyVisible(BlockPos p_226660_1_) {
        return innerWorld.isSkyVisible(p_226660_1_);
    }

    @Override
    public Biome getBiomeForNoiseGen(int p_225526_1_, int p_225526_2_, int p_225526_3_) {
        return innerWorld.getBiomeForNoiseGen(p_225526_1_, p_225526_2_, p_225526_3_);
    }

    @Override
    public void playEvent(@Nullable PlayerEntity p_217378_1_, int p_217378_2_, BlockPos p_217378_3_, int p_217378_4_) {
        innerWorld.playEvent(p_217378_1_, p_217378_2_, p_217378_3_, p_217378_4_);
    }

    @Override
    public void playEvent(int p_217379_1_, BlockPos p_217379_2_, int p_217379_3_) {
        innerWorld.playEvent(p_217379_1_, p_217379_2_, p_217379_3_);
    }

    @Override
    public Stream<VoxelShape> getEmptyCollisionShapes(@Nullable Entity p_223439_1_, AxisAlignedBB p_223439_2_, Set<Entity> p_223439_3_) {
        return innerWorld.getEmptyCollisionShapes(p_223439_1_, p_223439_2_, p_223439_3_);
    }

    @Nullable
    @Override
    public PlayerEntity getClosestPlayer(double p_190525_1_, double p_190525_3_, double p_190525_5_, double p_190525_7_, @Nullable Predicate<Entity> p_190525_9_) {
        return innerWorld.getClosestPlayer(p_190525_1_, p_190525_3_, p_190525_5_, p_190525_7_, p_190525_9_);
    }

    @Nullable
    @Override
    public PlayerEntity getClosestPlayer(Entity p_217362_1_, double p_217362_2_) {
        return innerWorld.getClosestPlayer(p_217362_1_, p_217362_2_);
    }

    @Nullable
    @Override
    public PlayerEntity getClosestPlayer(double p_217366_1_, double p_217366_3_, double p_217366_5_, double p_217366_7_, boolean p_217366_9_) {
        return innerWorld.getClosestPlayer(p_217366_1_, p_217366_3_, p_217366_5_, p_217366_7_, p_217366_9_);
    }

    @Nullable
    @Override
    public PlayerEntity getClosestPlayer(double p_217365_1_, double p_217365_3_, double p_217365_5_) {
        return innerWorld.getClosestPlayer(p_217365_1_, p_217365_3_, p_217365_5_);
    }

    @Override
    public boolean isPlayerWithin(double p_217358_1_, double p_217358_3_, double p_217358_5_, double p_217358_7_) {
        return innerWorld.isPlayerWithin(p_217358_1_, p_217358_3_, p_217358_5_, p_217358_7_);
    }

    @Nullable
    @Override
    public PlayerEntity getClosestPlayer(EntityPredicate p_217370_1_, LivingEntity p_217370_2_) {
        return innerWorld.getClosestPlayer(p_217370_1_, p_217370_2_);
    }

    @Nullable
    @Override
    public PlayerEntity getClosestPlayer(EntityPredicate p_217372_1_, LivingEntity p_217372_2_, double p_217372_3_, double p_217372_5_, double p_217372_7_) {
        return innerWorld.getClosestPlayer(p_217372_1_, p_217372_2_, p_217372_3_, p_217372_5_, p_217372_7_);
    }

    @Nullable
    @Override
    public PlayerEntity getClosestPlayer(EntityPredicate p_217359_1_, double p_217359_2_, double p_217359_4_, double p_217359_6_) {
        return innerWorld.getClosestPlayer(p_217359_1_, p_217359_2_, p_217359_4_, p_217359_6_);
    }

    @Nullable
    @Override
    public <T extends LivingEntity> T getClosestEntityWithinAABB(Class<? extends T> p_217360_1_, EntityPredicate p_217360_2_, @Nullable LivingEntity p_217360_3_, double p_217360_4_, double p_217360_6_, double p_217360_8_, AxisAlignedBB p_217360_10_) {
        return innerWorld.getClosestEntityWithinAABB(p_217360_1_, p_217360_2_, p_217360_3_, p_217360_4_, p_217360_6_, p_217360_8_, p_217360_10_);
    }

    @Nullable
    @Override
    public <T extends LivingEntity> T getClosestEntityIncludingUngeneratedChunks(Class<? extends T> p_225318_1_, EntityPredicate p_225318_2_, @Nullable LivingEntity p_225318_3_, double p_225318_4_, double p_225318_6_, double p_225318_8_, AxisAlignedBB p_225318_10_) {
        return innerWorld.getClosestEntityIncludingUngeneratedChunks(p_225318_1_, p_225318_2_, p_225318_3_, p_225318_4_, p_225318_6_, p_225318_8_, p_225318_10_);
    }

    @Nullable
    @Override
    public <T extends LivingEntity> T getClosestEntity(List<? extends T> p_217361_1_, EntityPredicate p_217361_2_, @Nullable LivingEntity p_217361_3_, double p_217361_4_, double p_217361_6_, double p_217361_8_) {
        return innerWorld.getClosestEntity(p_217361_1_, p_217361_2_, p_217361_3_, p_217361_4_, p_217361_6_, p_217361_8_);
    }

    @Override
    public List<PlayerEntity> getTargettablePlayersWithinAABB(EntityPredicate p_217373_1_, LivingEntity p_217373_2_, AxisAlignedBB p_217373_3_) {
        return innerWorld.getTargettablePlayersWithinAABB(p_217373_1_, p_217373_2_, p_217373_3_);
    }

    @Override
    public <T extends LivingEntity> List<T> getTargettableEntitiesWithinAABB(Class<? extends T> p_217374_1_, EntityPredicate p_217374_2_, LivingEntity p_217374_3_, AxisAlignedBB p_217374_4_) {
        return innerWorld.getTargettableEntitiesWithinAABB(p_217374_1_, p_217374_2_, p_217374_3_, p_217374_4_);
    }

    @Nullable
    @Override
    public PlayerEntity getPlayerByUuid(UUID p_217371_1_) {
        return innerWorld.getPlayerByUuid(p_217371_1_);
    }

    @Override
    public Stream<VoxelShape> getCollisions(@Nullable Entity p_226667_1_, AxisAlignedBB p_226667_2_, Set<Entity> p_226667_3_) {
        return innerWorld.getCollisions(p_226667_1_, p_226667_2_, p_226667_3_);
    }

    @Override
    public Stream<VoxelShape> getBlockCollisions(@Nullable Entity p_226666_1_, AxisAlignedBB p_226666_2_) {
        return innerWorld.getBlockCollisions(p_226666_1_, p_226666_2_);
    }

    @Override
    public boolean checkNoEntityCollision(@Nullable Entity p_195585_1_, VoxelShape p_195585_2_) {
        return innerWorld.checkNoEntityCollision(p_195585_1_, p_195585_2_);
    }

    @Override
    public <T extends Entity> List<T> getEntitiesWithinAABB(Class<? extends T> p_217357_1_, AxisAlignedBB p_217357_2_) {
        return innerWorld.getEntitiesWithinAABB(p_217357_1_, p_217357_2_);
    }

    @Override
    public <T extends Entity> List<T> getEntitiesIncludingUngeneratedChunks(Class<? extends T> p_225317_1_, AxisAlignedBB p_225317_2_) {
        return innerWorld.getEntitiesIncludingUngeneratedChunks(p_225317_1_, p_225317_2_);
    }

    @Override
    public boolean canPlace(BlockState p_226663_1_, BlockPos p_226663_2_, ISelectionContext p_226663_3_) {
        return innerWorld.canPlace(p_226663_1_, p_226663_2_, p_226663_3_);
    }

    @Override
    public boolean intersectsEntities(Entity p_226668_1_) {
        return innerWorld.intersectsEntities(p_226668_1_);
    }

    @Override
    public boolean doesNotCollide(AxisAlignedBB p_226664_1_) {
        return innerWorld.doesNotCollide(p_226664_1_);
    }

    @Override
    public boolean doesNotCollide(Entity p_226669_1_) {
        return innerWorld.doesNotCollide(p_226669_1_);
    }

    @Override
    public boolean doesNotCollide(Entity p_226665_1_, AxisAlignedBB p_226665_2_) {
        return innerWorld.doesNotCollide(p_226665_1_, p_226665_2_);
    }

    @Override
    public boolean doesNotCollide(@Nullable Entity p_226662_1_, AxisAlignedBB p_226662_2_, Set<Entity> p_226662_3_) {
        return innerWorld.doesNotCollide(p_226662_1_, p_226662_2_, p_226662_3_);
    }

    @Override
    public BlockPos getHeight(Heightmap.Type p_205770_1_, BlockPos p_205770_2_) {
        return innerWorld.getHeight(p_205770_1_, p_205770_2_);
    }

    @Override
    public int getMaxHeight() {
        return innerWorld.getMaxHeight();
    }

    @Override
    public boolean isAirBlock(BlockPos p_175623_1_) {
        return innerWorld.isAirBlock(p_175623_1_);
    }

    @Override
    public boolean canBlockSeeSky(BlockPos p_175710_1_) {
        return innerWorld.canBlockSeeSky(p_175710_1_);
    }

    @Override
    public float getBrightness(BlockPos p_205052_1_) {
        return innerWorld.getBrightness(p_205052_1_);
    }

    @Override
    public int getStrongPower(BlockPos p_175627_1_, Direction p_175627_2_) {
        return innerWorld.getStrongPower(p_175627_1_, p_175627_2_);
    }

    @Override
    public IChunk getChunk(BlockPos p_217349_1_) {
        return innerWorld.getChunk(p_217349_1_);
    }

    @Override
    public IChunk getChunk(int p_217348_1_, int p_217348_2_, ChunkStatus p_217348_3_) {
        return innerWorld.getChunk(p_217348_1_, p_217348_2_, p_217348_3_);
    }

    @Override
    public boolean hasWater(BlockPos p_201671_1_) {
        return innerWorld.hasWater(p_201671_1_);
    }

    @Override
    public boolean containsAnyLiquid(AxisAlignedBB p_72953_1_) {
        return innerWorld.containsAnyLiquid(p_72953_1_);
    }

    @Override
    public int getLight(BlockPos p_201696_1_) {
        return innerWorld.getLight(p_201696_1_);
    }

    @Override
    public int getNeighborAwareLightSubtracted(BlockPos p_205049_1_, int p_205049_2_) {
        return innerWorld.getNeighborAwareLightSubtracted(p_205049_1_, p_205049_2_);
    }

    @Override
    public boolean isBlockLoaded(BlockPos p_175667_1_) {
        return innerWorld.isBlockLoaded(p_175667_1_);
    }

    @Override
    public boolean isAreaLoaded(BlockPos center, int range) {
        return innerWorld.isAreaLoaded(center, range);
    }

    @Override
    public boolean isAreaLoaded(BlockPos p_175707_1_, BlockPos p_175707_2_) {
        return innerWorld.isAreaLoaded(p_175707_1_, p_175707_2_);
    }

    @Override
    public boolean isAreaLoaded(int p_217344_1_, int p_217344_2_, int p_217344_3_, int p_217344_4_, int p_217344_5_, int p_217344_6_) {
        return innerWorld.isAreaLoaded(p_217344_1_, p_217344_2_, p_217344_3_, p_217344_4_, p_217344_5_, p_217344_6_);
    }

    @Override
    public List<AbstractClientPlayerEntity> getPlayers() {
        return innerWorld.getPlayers();
    }

    @Override
    public List<Entity> getEntitiesWithinAABBExcludingEntity(@Nullable Entity p_72839_1_, AxisAlignedBB p_72839_2_) {
        return innerWorld.getEntitiesWithinAABBExcludingEntity(p_72839_1_, p_72839_2_);
    }

    @Override
    public Biome getGeneratorStoredBiome(int p_225604_1_, int p_225604_2_, int p_225604_3_) {
        return innerWorld.getGeneratorStoredBiome(p_225604_1_, p_225604_2_, p_225604_3_);
    }

    @Override
    public int getLightValue(BlockPos p_217298_1_) {
        return innerWorld.getLightValue(p_217298_1_);
    }

    @Override
    public int getMaxLightLevel() {
        return innerWorld.getMaxLightLevel();
    }

    @Override
    public int getHeight() {
        return innerWorld.getHeight();
    }

    @Override
    public BlockRayTraceResult rayTraceBlocks(RayTraceContext p_217299_1_) {
        return innerWorld.rayTraceBlocks(p_217299_1_);
    }

    @Nullable
    @Override
    public BlockRayTraceResult rayTraceBlocks(Vec3d p_217296_1_, Vec3d p_217296_2_, BlockPos p_217296_3_, VoxelShape p_217296_4_, BlockState p_217296_5_) {
        return innerWorld.rayTraceBlocks(p_217296_1_, p_217296_2_, p_217296_3_, p_217296_4_, p_217296_5_);
    }

    @Override
    public boolean destroyBlock(BlockPos p_175655_1_, boolean p_175655_2_) {
        return innerWorld.destroyBlock(p_175655_1_, p_175655_2_);
    }

    @Override
    public boolean addEntity(Entity p_217376_1_) {
        return innerWorld.addEntity(p_217376_1_);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
        return innerWorld.getCapability(cap);
    }

    @Override
    public boolean isRemote() {
        return innerWorld.isRemote();
    }

    @Nullable
    @Override
    public MinecraftServer getServer() {
        return innerWorld.getServer();
    }

    @Override
    public void setInitialSpawnLocation() {
        innerWorld.setInitialSpawnLocation();
    }

    @Override
    public BlockState getGroundAboveSeaLevel(BlockPos p_184141_1_) {
        return innerWorld.getGroundAboveSeaLevel(p_184141_1_);
    }

    @Override
    public Chunk getChunkAt(BlockPos p_175726_1_) {
        return innerWorld.getChunkAt(p_175726_1_);
    }

    @Override
    public Chunk getChunk(int p_212866_1_, int p_212866_2_) {
        return innerWorld.getChunk(p_212866_1_, p_212866_2_);
    }

    @Override
    public IChunk getChunk(int p_217353_1_, int p_217353_2_, ChunkStatus p_217353_3_, boolean p_217353_4_) {
        return innerWorld.getChunk(p_217353_1_, p_217353_2_, p_217353_3_, p_217353_4_);
    }

    @Override
    public boolean setBlockState(BlockPos p_180501_1_, BlockState p_180501_2_, int p_180501_3_) {
        return innerWorld.setBlockState(p_180501_1_, p_180501_2_, p_180501_3_);
    }

    @Override
    public void markAndNotifyBlock(BlockPos p_180501_1_, @Nullable Chunk chunk, BlockState blockstate, BlockState p_180501_2_, int p_180501_3_) {
        innerWorld.markAndNotifyBlock(p_180501_1_, chunk, blockstate, p_180501_2_, p_180501_3_);
    }

    @Override
    public void onBlockStateChange(BlockPos p_217393_1_, BlockState p_217393_2_, BlockState p_217393_3_) {
        innerWorld.onBlockStateChange(p_217393_1_, p_217393_2_, p_217393_3_);
    }

    @Override
    public boolean removeBlock(BlockPos p_217377_1_, boolean p_217377_2_) {
        return innerWorld.removeBlock(p_217377_1_, p_217377_2_);
    }

    @Override
    public boolean breakBlock(BlockPos p_225521_1_, boolean p_225521_2_, @Nullable Entity p_225521_3_) {
        return innerWorld.breakBlock(p_225521_1_, p_225521_2_, p_225521_3_);
    }

    @Override
    public boolean setBlockState(BlockPos p_175656_1_, BlockState p_175656_2_) {
        return innerWorld.setBlockState(p_175656_1_, p_175656_2_);
    }

    @Override
    public void notifyNeighbors(BlockPos p_195592_1_, Block p_195592_2_) {
        innerWorld.notifyNeighbors(p_195592_1_, p_195592_2_);
    }

    @Override
    public void checkBlockRerender(BlockPos p_225319_1_, BlockState p_225319_2_, BlockState p_225319_3_) {
        innerWorld.checkBlockRerender(p_225319_1_, p_225319_2_, p_225319_3_);
    }

    @Override
    public void notifyNeighborsOfStateChange(BlockPos p_195593_1_, Block p_195593_2_) {
        innerWorld.notifyNeighborsOfStateChange(p_195593_1_, p_195593_2_);
    }

    @Override
    public void notifyNeighborsOfStateExcept(BlockPos p_175695_1_, Block p_175695_2_, Direction p_175695_3_) {
        innerWorld.notifyNeighborsOfStateExcept(p_175695_1_, p_175695_2_, p_175695_3_);
    }

    @Override
    public void neighborChanged(BlockPos p_190524_1_, Block p_190524_2_, BlockPos p_190524_3_) {
        innerWorld.neighborChanged(p_190524_1_, p_190524_2_, p_190524_3_);
    }

    @Override
    public int getHeight(Heightmap.Type p_201676_1_, int p_201676_2_, int p_201676_3_) {
        return innerWorld.getHeight(p_201676_1_, p_201676_2_, p_201676_3_);
    }

    @Override
    public WorldLightManager getLightingProvider() {
        return innerWorld.getLightingProvider();
    }

    @Override
    public BlockState getBlockState(BlockPos p_180495_1_) {
        return innerWorld.getBlockState(p_180495_1_);
    }

    @Override
    public IFluidState getFluidState(BlockPos p_204610_1_) {
        return innerWorld.getFluidState(p_204610_1_);
    }

    @Override
    public boolean isDaytime() {
        return innerWorld.isDaytime();
    }

    @Override
    public boolean isNight() {
        return innerWorld.isNight();
    }

    @Override
    public void playSound(@Nullable PlayerEntity p_184133_1_, BlockPos p_184133_2_, SoundEvent p_184133_3_, SoundCategory p_184133_4_, float p_184133_5_, float p_184133_6_) {
        innerWorld.playSound(p_184133_1_, p_184133_2_, p_184133_3_, p_184133_4_, p_184133_5_, p_184133_6_);
    }

    @Override
    public void playSound(double p_184134_1_, double p_184134_3_, double p_184134_5_, SoundEvent p_184134_7_, SoundCategory p_184134_8_, float p_184134_9_, float p_184134_10_, boolean p_184134_11_) {
        innerWorld.playSound(p_184134_1_, p_184134_3_, p_184134_5_, p_184134_7_, p_184134_8_, p_184134_9_, p_184134_10_, p_184134_11_);
    }

    @Override
    public void addParticle(IParticleData p_195594_1_, double p_195594_2_, double p_195594_4_, double p_195594_6_, double p_195594_8_, double p_195594_10_, double p_195594_12_) {
        innerWorld.addParticle(p_195594_1_, p_195594_2_, p_195594_4_, p_195594_6_, p_195594_8_, p_195594_10_, p_195594_12_);
    }

    @Override
    public void addParticle(IParticleData p_195590_1_, boolean p_195590_2_, double p_195590_3_, double p_195590_5_, double p_195590_7_, double p_195590_9_, double p_195590_11_, double p_195590_13_) {
        innerWorld.addParticle(p_195590_1_, p_195590_2_, p_195590_3_, p_195590_5_, p_195590_7_, p_195590_9_, p_195590_11_, p_195590_13_);
    }

    @Override
    public void addOptionalParticle(IParticleData p_195589_1_, double p_195589_2_, double p_195589_4_, double p_195589_6_, double p_195589_8_, double p_195589_10_, double p_195589_12_) {
        innerWorld.addOptionalParticle(p_195589_1_, p_195589_2_, p_195589_4_, p_195589_6_, p_195589_8_, p_195589_10_, p_195589_12_);
    }

    @Override
    public void addOptionalParticle(IParticleData p_217404_1_, boolean p_217404_2_, double p_217404_3_, double p_217404_5_, double p_217404_7_, double p_217404_9_, double p_217404_11_, double p_217404_13_) {
        innerWorld.addOptionalParticle(p_217404_1_, p_217404_2_, p_217404_3_, p_217404_5_, p_217404_7_, p_217404_9_, p_217404_11_, p_217404_13_);
    }

    @Override
    public float getCelestialAngleRadians(float p_72929_1_) {
        return innerWorld.getCelestialAngleRadians(p_72929_1_);
    }

    @Override
    public boolean addTileEntity(TileEntity p_175700_1_) {
        return innerWorld.addTileEntity(p_175700_1_);
    }

    @Override
    public void addTileEntities(Collection<TileEntity> p_147448_1_) {
        innerWorld.addTileEntities(p_147448_1_);
    }

    @Override
    public void tickBlockEntities() {
        innerWorld.tickBlockEntities();
    }

    @Override
    public void guardEntityTick(Consumer<Entity> p_217390_1_, Entity p_217390_2_) {
        innerWorld.guardEntityTick(p_217390_1_, p_217390_2_);
    }

    @Override
    public boolean checkBlockCollision(AxisAlignedBB p_72829_1_) {
        return innerWorld.checkBlockCollision(p_72829_1_);
    }

    @Override
    public boolean isFlammableWithin(AxisAlignedBB p_147470_1_) {
        return innerWorld.isFlammableWithin(p_147470_1_);
    }

    @Nullable
    @Override
    public BlockState findBlockstateInArea(AxisAlignedBB p_203067_1_, Block p_203067_2_) {
        return innerWorld.findBlockstateInArea(p_203067_1_, p_203067_2_);
    }

    @Override
    public boolean isMaterialInBB(AxisAlignedBB p_72875_1_, Material p_72875_2_) {
        return innerWorld.isMaterialInBB(p_72875_1_, p_72875_2_);
    }

    @Override
    public Explosion createExplosion(@Nullable Entity p_217385_1_, double p_217385_2_, double p_217385_4_, double p_217385_6_, float p_217385_8_, Explosion.Mode p_217385_9_) {
        return innerWorld.createExplosion(p_217385_1_, p_217385_2_, p_217385_4_, p_217385_6_, p_217385_8_, p_217385_9_);
    }

    @Override
    public Explosion createExplosion(@Nullable Entity p_217398_1_, double p_217398_2_, double p_217398_4_, double p_217398_6_, float p_217398_8_, boolean p_217398_9_, Explosion.Mode p_217398_10_) {
        return innerWorld.createExplosion(p_217398_1_, p_217398_2_, p_217398_4_, p_217398_6_, p_217398_8_, p_217398_9_, p_217398_10_);
    }

    @Override
    public Explosion createExplosion(@Nullable Entity p_217401_1_, @Nullable DamageSource p_217401_2_, double p_217401_3_, double p_217401_5_, double p_217401_7_, float p_217401_9_, boolean p_217401_10_, Explosion.Mode p_217401_11_) {
        return innerWorld.createExplosion(p_217401_1_, p_217401_2_, p_217401_3_, p_217401_5_, p_217401_7_, p_217401_9_, p_217401_10_, p_217401_11_);
    }

    @Override
    public boolean extinguishFire(@Nullable PlayerEntity p_175719_1_, BlockPos p_175719_2_, Direction p_175719_3_) {
        return innerWorld.extinguishFire(p_175719_1_, p_175719_2_, p_175719_3_);
    }

    @Override
    public String getProviderName() {
        return innerWorld.getProviderName();
    }

    @Nullable
    @Override
    public TileEntity getTileEntity(BlockPos p_175625_1_) {
        return innerWorld.getTileEntity(p_175625_1_);
    }

    @Override
    public void setTileEntity(BlockPos p_175690_1_, @Nullable TileEntity p_175690_2_) {
        innerWorld.setTileEntity(p_175690_1_, p_175690_2_);
    }

    @Override
    public void removeTileEntity(BlockPos p_175713_1_) {
        innerWorld.removeTileEntity(p_175713_1_);
    }

    @Override
    public boolean isBlockPresent(BlockPos p_195588_1_) {
        return innerWorld.isBlockPresent(p_195588_1_);
    }

    @Override
    public boolean isTopSolid(BlockPos p_217400_1_, Entity p_217400_2_) {
        return innerWorld.isTopSolid(p_217400_1_, p_217400_2_);
    }

    @Override
    public void setAllowedSpawnTypes(boolean p_72891_1_, boolean p_72891_2_) {
        innerWorld.setAllowedSpawnTypes(p_72891_1_, p_72891_2_);
    }

    @Override
    public void close() throws IOException {
        innerWorld.close();
    }

    @Nullable
    @Override
    public IBlockReader getExistingChunk(int p_225522_1_, int p_225522_2_) {
        return innerWorld.getExistingChunk(p_225522_1_, p_225522_2_);
    }

    @Override
    public void markChunkDirty(BlockPos p_175646_1_, TileEntity p_175646_2_) {
        innerWorld.markChunkDirty(p_175646_1_, p_175646_2_);
    }

    @Override
    public int getSeaLevel() {
        return innerWorld.getSeaLevel();
    }

    @Override
    public int getStrongPower(BlockPos p_175676_1_) {
        return innerWorld.getStrongPower(p_175676_1_);
    }

    @Override
    public boolean isSidePowered(BlockPos p_175709_1_, Direction p_175709_2_) {
        return innerWorld.isSidePowered(p_175709_1_, p_175709_2_);
    }

    @Override
    public int getRedstonePower(BlockPos p_175651_1_, Direction p_175651_2_) {
        return innerWorld.getRedstonePower(p_175651_1_, p_175651_2_);
    }

    @Override
    public boolean isBlockPowered(BlockPos p_175640_1_) {
        return innerWorld.isBlockPowered(p_175640_1_);
    }

    @Override
    public int getRedstonePowerFromNeighbors(BlockPos p_175687_1_) {
        return innerWorld.getRedstonePowerFromNeighbors(p_175687_1_);
    }

    @Override
    public void sendQuittingDisconnectingPacket() {
        innerWorld.sendQuittingDisconnectingPacket();
    }

    @Override
    public void setGameTime(long p_82738_1_) {
        innerWorld.setGameTime(p_82738_1_);
    }

    @Override
    public long getGameTime() {
        return innerWorld.getGameTime();
    }

    @Override
    public void setDayTime(long p_72877_1_) {
        innerWorld.setDayTime(p_72877_1_);
    }

    @Override
    public BlockPos getSpawnPoint() {
        return innerWorld.getSpawnPoint();
    }

    @Override
    public boolean isBlockModifiable(PlayerEntity p_175660_1_, BlockPos p_175660_2_) {
        return innerWorld.isBlockModifiable(p_175660_1_, p_175660_2_);
    }

    @Override
    public boolean canMineBlockBody(PlayerEntity player, BlockPos pos) {
        return innerWorld.canMineBlockBody(player, pos);
    }

    @Override
    public void setEntityState(Entity p_72960_1_, byte p_72960_2_) {
        innerWorld.setEntityState(p_72960_1_, p_72960_2_);
    }

    @Override
    public void addBlockEvent(BlockPos p_175641_1_, Block p_175641_2_, int p_175641_3_, int p_175641_4_) {
        innerWorld.addBlockEvent(p_175641_1_, p_175641_2_, p_175641_3_, p_175641_4_);
    }

    @Override
    public GameRules getGameRules() {
        return innerWorld.getGameRules();
    }

    @Override
    public void setThunderStrength(float p_147442_1_) {
        innerWorld.setThunderStrength(p_147442_1_);
    }

    @Override
    public void setRainStrength(float p_72894_1_) {
        innerWorld.setRainStrength(p_72894_1_);
    }

    @Override
    public boolean isThundering() {
        return innerWorld.isThundering();
    }

    @Override
    public boolean isRaining() {
        return innerWorld.isRaining();
    }

    @Override
    public boolean isRainingAt(BlockPos p_175727_1_) {
        return innerWorld.isRainingAt(p_175727_1_);
    }

    @Override
    public boolean isBlockinHighHumidity(BlockPos p_180502_1_) {
        return innerWorld.isBlockinHighHumidity(p_180502_1_);
    }

    @Override
    public void playBroadcastSound(int p_175669_1_, BlockPos p_175669_2_, int p_175669_3_) {
        innerWorld.playBroadcastSound(p_175669_1_, p_175669_2_, p_175669_3_);
    }

    @Override
    public int getActualHeight() {
        return innerWorld.getActualHeight();
    }

    @Override
    public CrashReportCategory fillCrashReport(CrashReport p_72914_1_) {
        return innerWorld.fillCrashReport(p_72914_1_);
    }

    @Override
    public void makeFireworks(double p_92088_1_, double p_92088_3_, double p_92088_5_, double p_92088_7_, double p_92088_9_, double p_92088_11_, @Nullable CompoundNBT p_92088_13_) {
        innerWorld.makeFireworks(p_92088_1_, p_92088_3_, p_92088_5_, p_92088_7_, p_92088_9_, p_92088_11_, p_92088_13_);
    }

    @Override
    public void updateComparatorOutputLevel(BlockPos p_175666_1_, Block p_175666_2_) {
        innerWorld.updateComparatorOutputLevel(p_175666_1_, p_175666_2_);
    }

    @Override
    public DifficultyInstance getDifficultyForLocation(BlockPos p_175649_1_) {
        return innerWorld.getDifficultyForLocation(p_175649_1_);
    }

    @Override
    public int getSkylightSubtracted() {
        return innerWorld.getSkylightSubtracted();
    }

    @Override
    public void setLightningTicksLeft(int p_225605_1_) {
        innerWorld.setLightningTicksLeft(p_225605_1_);
    }

    @Override
    public void sendPacketToServer(IPacket<?> p_184135_1_) {
        innerWorld.sendPacketToServer(p_184135_1_);
    }

    @Override
    public Random getRandom() {
        return innerWorld.getRandom();
    }

    @Override
    public boolean hasBlockState(BlockPos p_217375_1_, Predicate<BlockState> p_217375_2_) {
        return innerWorld.hasBlockState(p_217375_1_, p_217375_2_);
    }

    @Override
    public BlockPos getBlockRandomPos(int p_217383_1_, int p_217383_2_, int p_217383_3_, int p_217383_4_) {
        return innerWorld.getBlockRandomPos(p_217383_1_, p_217383_2_, p_217383_3_, p_217383_4_);
    }

    @Override
    public boolean isSaveDisabled() {
        return innerWorld.isSaveDisabled();
    }

    @Override
    public IProfiler getProfiler() {
        return innerWorld.getProfiler();
    }

    @Override
    public BiomeManager getBiomeAccess() {
        return innerWorld.getBiomeAccess();
    }

    @Override
    public double getMaxEntityRadius() {
        return innerWorld.getMaxEntityRadius();
    }

    @Override
    public double increaseMaxEntityRadius(double value) {
        return innerWorld.increaseMaxEntityRadius(value);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return innerWorld.getCapability(cap, side);
    }

    @Override
    public List<Entity> getEntitiesInAABBexcluding(@Nullable Entity p_175674_1_, AxisAlignedBB p_175674_2_, @Nullable Predicate<? super Entity> p_175674_3_) {
        return innerWorld.getEntitiesInAABBexcluding(p_175674_1_, p_175674_2_, p_175674_3_);
    }

    @Override
    public <T extends Entity> List<T> getEntitiesWithinAABB(@Nullable EntityType<T> p_217394_1_, AxisAlignedBB p_217394_2_, Predicate<? super T> p_217394_3_) {
        return innerWorld.getEntitiesWithinAABB(p_217394_1_, p_217394_2_, p_217394_3_);
    }

    @Override
    public <T extends Entity> List<T> getEntitiesWithinAABB(Class<? extends T> p_175647_1_, AxisAlignedBB p_175647_2_, @Nullable Predicate<? super T> p_175647_3_) {
        return innerWorld.getEntitiesWithinAABB(p_175647_1_, p_175647_2_, p_175647_3_);
    }

    @Override
    public <T extends Entity> List<T> getEntitiesIncludingUngeneratedChunks(Class<? extends T> p_225316_1_, AxisAlignedBB p_225316_2_, @Nullable Predicate<? super T> p_225316_3_) {
        return innerWorld.getEntitiesIncludingUngeneratedChunks(p_225316_1_, p_225316_2_, p_225316_3_);
    }
}
