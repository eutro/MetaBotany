package eutros.botaniapp.common.entity.cart;

import eutros.botaniapp.common.utils.Reference;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class EntityGenericTileEntityCart extends EntityGenericBlockCart {
    @ObjectHolder(Reference.MOD_ID + ":generic_tile_entity_cart")
    public static EntityType<EntityGenericTileEntityCart> TYPE;
    private static final String TAG_TILE = "contained_tile";
    private static final String TAG_TILE_DATA = "data";
    private static final String TAG_TILE_LOC = "id";

    private static final DataParameter<CompoundNBT> TILE = EntityDataManager.createKey(EntityGenericTileEntityCart.class, DataSerializers.COMPOUND_NBT);
    private TileEntity cachedTile;

    public EntityGenericTileEntityCart(EntityType<?> type, World world) {
        super(type, world);
        setTile(null);
        cachedTile = null;
    }

    public EntityGenericTileEntityCart(World world) {
        this(TYPE, world);
    }

    public EntityGenericTileEntityCart(World world, double x, double y, double z, BlockState state, TileEntity tile) {
        this(TYPE, world, x, y, z, state, tile);
    }

    public EntityGenericTileEntityCart(EntityType<?> type, World world, double x, double y, double z, BlockState state, TileEntity tile) {
        super(type, world, x, y, z, state);
        setTile(tile);
        cachedTile = null;
    }

    public TileEntity getTile() {
        if(cachedTile == null) {
            TileEntity tile = deserializeTile(dataManager.get(TILE));
            if (tile != null)
                tile.setLocation(proxyWorld, getPosition());
            cachedTile = tile;
        }
        if(cachedTile != null && (!cachedTile.hasWorld() || !cachedTile.getPos().equals(getPosition())))
            cachedTile.setLocation(proxyWorld, getPosition());
        return cachedTile;
    }

    private void setTile(TileEntity tile) {
        cachedTile = tile;
        dataManager.set(TILE, serializeTile(tile));
    }

    @Override
    public void tick() {
        super.tick();
        TileEntity tile = getTile();
        if(tile instanceof ITickableTileEntity)
            ((ITickableTileEntity) tile).tick();
    }

    @Override
    protected void registerData() {
        super.registerData();
        dataManager.register(TILE, new CompoundNBT());
    }

    @Override
    protected void readAdditional(CompoundNBT cmp) {
        super.readAdditional(cmp);
        CompoundNBT wrappedTile = cmp.getCompound(TAG_TILE);
        setTile(deserializeTile(wrappedTile));
    }

    @Override
    protected void writeAdditional(@NotNull CompoundNBT cmp) {
        super.writeAdditional(cmp);
        CompoundNBT wrappedTile = serializeTile(getTile());
        cmp.put(TAG_TILE, wrappedTile);
    }

    @Nullable
    private TileEntity deserializeTile(CompoundNBT wrappedTile) {
        ResourceLocation name = new ResourceLocation(wrappedTile.getString(TAG_TILE_LOC));
        TileEntityType<? extends TileEntity> type = ForgeRegistries.TILE_ENTITIES.getValue(name);
        TileEntity tile = null;
        if(type != null) {
            tile = type.create();
            if(tile != null)
                tile.read(wrappedTile.getCompound(TAG_TILE_DATA));
        }
        return tile;
    }

    @NotNull
    private CompoundNBT serializeTile(@Nullable TileEntity tile) {
        CompoundNBT tileNBT = new CompoundNBT();
        CompoundNBT wrappedTile = new CompoundNBT();
        if(tile != null) {
            tile.write(tileNBT);
            wrappedTile.put(TAG_TILE_DATA, tileNBT);
            ResourceLocation name = tile.getType().getRegistryName();
            wrappedTile.putString(TAG_TILE_LOC, name == null ? "" : name.toString());
        }
        return wrappedTile;
    }

    @Override
    protected Function<ServerWorld, ServerWorld> getServerWorldFactory() {
        return ProxyServerWorld::new;
    }

    @Override
    protected Function<ClientWorld, ClientWorld> getClientWorldFactory() {
        return ProxyClientWorld::new;
    }

    @Override
    protected Function<World, World> getGenericWorldFactory() {
        return ProxyWorld::new;
    }

    protected class ProxyServerWorld extends EntityGenericBlockCart.ProxyServerWorld {
        // TODO step down
        public ProxyServerWorld(ServerWorld world) {
            super(world);
        }

        @Nullable
        @Override
        public TileEntity getTileEntity(BlockPos pos) {
            return pos.equals(getPosition()) ? getTile() : super.getTileEntity(pos);
        }

        @Override
        public void markChunkDirty(BlockPos pos, TileEntity te) {
            if(pos.equals(getPosition()))
                setTile(te);
            super.markChunkDirty(pos, te);
        }
    }

    protected class ProxyClientWorld extends EntityGenericBlockCart.ProxyClientWorld {
        public ProxyClientWorld(ClientWorld world) {
            super(world);
        }

        @Nullable
        @Override
        public TileEntity getTileEntity(BlockPos pos) {
            return pos.equals(getPosition()) ? getTile() : super.getTileEntity(pos);
        }

        @Override
        public void markChunkDirty(BlockPos pos, TileEntity te) {
            if(pos.equals(getPosition()))
                setTile(te);
            super.markChunkDirty(pos, te);
        }
    }

    protected class ProxyWorld extends EntityGenericBlockCart.ProxyWorld {
        public ProxyWorld(World world) {
            super(world);
        }

        @Nullable
        @Override
        public TileEntity getTileEntity(BlockPos pos) {
            return pos.equals(getPosition()) ? getTile() : super.getTileEntity(pos);
        }

        @Override
        public void markChunkDirty(BlockPos pos, TileEntity te) {
            if(pos.equals(getPosition()))
                setTile(te);
            super.markChunkDirty(pos, te);
        }
    }
}
