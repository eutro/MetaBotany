package eutros.botaniapp.common.entity.cart;

import eutros.botaniapp.common.utils.Reference;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EntityGenericTileEntityCart extends EntityGenericBlockCart {
    @ObjectHolder(Reference.MOD_ID + ":generic_tile_entity_cart")
    public static EntityType<EntityGenericTileEntityCart> TYPE;
    private static final String TAG_TILE = "contained_tile";
    private static final String TAG_TILE_DATA = "data";
    private static final String TAG_TILE_LOC = "id";

    private static final DataParameter<CompoundNBT> TILE = EntityDataManager.createKey(EntityGenericTileEntityCart.class, DataSerializers.COMPOUND_NBT);

    public EntityGenericTileEntityCart(EntityType<?> type, World world) {
        super(type, world);
        setTile(null);
    }

    public EntityGenericTileEntityCart(World world) {
        this(TYPE, world);
    }

    public EntityGenericTileEntityCart(World world, double x, double y, double z, BlockState state, TileEntity tile) {
        super(TYPE, world, x, y, z, state);
        setTile(tile);
    }

    public TileEntity getTile() {
        return deserializeTile(dataManager.get(TILE));
    }

    private void setTile(TileEntity tile) {
        dataManager.set(TILE, serializeTile(tile));
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

}
