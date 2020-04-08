package eutros.botaniapp.common.block.tinkerer.tile;

import eutros.botaniapp.common.block.tile.TileSimpleInventory;
import eutros.botaniapp.common.utils.Reference;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.registries.ObjectHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.internal.VanillaPacketDispatcher;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TileFrameTinkerer extends TileSimpleInventory {

    @ObjectHolder(Reference.MOD_ID + ":" + Reference.BlockNames.FRAME_TINKERER)
    public static TileEntityType<TileFrameTinkerer> TYPE;

    public TileFrameTinkerer() {
        super(TYPE);
    }

    @Override
    public int getSizeInventory() {
        return 1;
    }

    protected SimpleItemStackHandler createItemHandler() {
        return new SimpleItemStackHandler(this, true) {
            @Override
            protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
                return 1;
            }
        };
    }

    public void doSwitch() {
        assert world != null;
        ItemStack stack = itemHandler.getStackInSlot(0);

        ItemFrameEntity frame = getFrame(false);

        if(frame == null)
            return;

        ItemStack frameStack = frame.getDisplayedItem().copy();
        frame.setDisplayedItem(stack.copy());

        if(!frameStack.isEmpty()) {
            //Removing an item from a frame doesn't play the sound, for some reason.
            world.playSound(null, frame.getPosX(), frame.getPosY(), frame.getPosZ(), SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM, SoundCategory.BLOCKS, 1f, 1);
        }

        itemHandler.setStackInSlot(0, frameStack);
        markDirty();
        VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
    }

    @NotNull
    private List<ItemFrameEntity> getFrames() {
        assert world != null;
        List<ItemFrameEntity> frames = new ArrayList<>();
        for(Direction dir : Direction.values()) {
            AxisAlignedBB aabb = new AxisAlignedBB(pos.offset(dir));
            frames.addAll(world.getEntitiesWithinAABB(ItemFrameEntity.class, aabb));
        }
        return frames;
    }

    @Nullable
    private ItemFrameEntity getFrame(boolean filter) {
        assert world != null;
        List<ItemFrameEntity> frames = getFrames();
        if(filter)
            frames = frames.stream().filter(f -> !f.getDisplayedItem().isEmpty()).collect(Collectors.toList());

        if(frames.isEmpty()) {
            return null;
        }
        return frames.get(world.rand.nextInt(frames.size()));
    }

    public boolean doRotate() {
        assert world != null;
        ItemFrameEntity frame = getFrame(true);

        if(frame == null)
            return false;

        world.playSound(null, getPos(), SoundEvents.ENTITY_ITEM_FRAME_ROTATE_ITEM, SoundCategory.BLOCKS, 1.0F, 1.0F);
        frame.setItemRotation(frame.getRotation() + 1);
        return true;
    }

}
