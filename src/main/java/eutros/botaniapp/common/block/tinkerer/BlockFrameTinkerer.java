package eutros.botaniapp.common.block.tinkerer;

import eutros.botaniapp.api.internal.block.BlockRedstoneControlled;
import eutros.botaniapp.common.block.tile.TileSimpleInventory;
import eutros.botaniapp.common.block.tinkerer.tile.TileFrameTinkerer;
import eutros.botaniapp.common.core.helper.InventoryHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import vazkii.botania.api.wand.IWandable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class BlockFrameTinkerer extends BlockRedstoneControlled implements IWandable {

    private static VoxelShape SHAPE = makeCuboidShape(0, 0, 0, 16, 3, 16);

    public BlockFrameTinkerer(Properties properties) {
        super(properties);
    }

    @NotNull
    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @SuppressWarnings("deprecation")
    @NotNull
    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        TileFrameTinkerer charger = (TileFrameTinkerer) world.getTileEntity(pos);
        ItemStack pstack = player.getHeldItem(hand);
        assert charger != null;
        ItemStack cstack = charger.getItemHandler().getStackInSlot(0);
        if(!cstack.isEmpty()) {
            charger.getItemHandler().setStackInSlot(0, ItemStack.EMPTY);
            world.updateComparatorOutputLevel(pos, this);
            charger.markDirty();
            ItemHandlerHelper.giveItemToPlayer(player, cstack);
            return ActionResultType.SUCCESS;
        } else if(!pstack.isEmpty()) {
            charger.getItemHandler().setStackInSlot(0, pstack.split(1));
            world.updateComparatorOutputLevel(pos, this);
            charger.markDirty();

            return ActionResultType.SUCCESS;
        }

        return ActionResultType.PASS;
    }

    @Override
    public void doPulse(BlockState state, BlockPos pos, World world, BlockPos from) {
        if(!world.isRemote()) {
            ((TileFrameTinkerer) Objects.requireNonNull(world.getTileEntity(pos))).doSwitch();
        }
    }

    @Override
    public boolean onUsedByWand(PlayerEntity player, ItemStack stack, World world, BlockPos pos, Direction side) {
        return !world.isRemote() &&
                ((TileFrameTinkerer) Objects.requireNonNull(world.getTileEntity(pos))).doRotate();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onReplaced(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull BlockState newState, boolean isMoving) {
        if(state.getBlock() != newState.getBlock()) {
            TileSimpleInventory inv = (TileSimpleInventory) world.getTileEntity(pos);
            InventoryHelper.dropInventory(inv, world, state, pos);
        }
        super.onReplaced(state, world, pos, newState, isMoving);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileFrameTinkerer();
    }

}
