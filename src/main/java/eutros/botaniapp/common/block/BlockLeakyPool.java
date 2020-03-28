package eutros.botaniapp.common.block;

import eutros.botaniapp.api.internal.block.BlockRedstoneControlled;
import eutros.botaniapp.common.block.tile.TileLeakyPool;
import eutros.botaniapp.common.block.tile.TileSimpleInventory;
import eutros.botaniapp.common.core.helper.InventoryHelper;
import eutros.botaniapp.common.item.BotaniaPPItems;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import vazkii.botania.api.mana.ILens;
import vazkii.botania.api.wand.IWandHUD;
import vazkii.botania.api.wand.IWandable;

import javax.annotation.Nonnull;
import java.util.Objects;

public class BlockLeakyPool extends BlockRedstoneControlled implements IWandHUD, IWandable {

    private static final VoxelShape SLAB = makeCuboidShape(0, 0, 0, 16, 8, 16);
    private static final VoxelShape CUTOUT = makeCuboidShape(1, 1, 1, 15, 8, 15);
    private static final VoxelShape CUTOUT_2 = makeCuboidShape(6, 0, 6, 10, 1, 10);
    private static final VoxelShape POOL_SHAPE = VoxelShapes.combineAndSimplify(SLAB, CUTOUT, IBooleanFunction.ONLY_FIRST);
    private static final VoxelShape REAL_SHAPE = VoxelShapes.combineAndSimplify(POOL_SHAPE, CUTOUT_2, IBooleanFunction.ONLY_FIRST);

    public BlockLeakyPool(Properties builder) {
        super(builder);
    }

    @SuppressWarnings("deprecation")
    @Nonnull
    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx) {
        return REAL_SHAPE;
    }

    @SuppressWarnings("deprecation")
    @NotNull
    @Override
    public ActionResultType onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        TileLeakyPool pool = (TileLeakyPool) world.getTileEntity(pos);
        ItemStack pstack = player.getHeldItem(hand);
        assert pool != null;
        ItemStack lstack = pool.getItemHandler().getStackInSlot(0);
        if(!lstack.isEmpty() && !(pstack.getItem() == BotaniaPPItems.BOTANIA_SPARK)) {
            pool.getItemHandler().setStackInSlot(0, ItemStack.EMPTY);
            world.updateComparatorOutputLevel(pos, this);
            pool.markDirty();
            ItemHandlerHelper.giveItemToPlayer(player, lstack);
            return ActionResultType.SUCCESS;
        } else if(!pstack.isEmpty() && pstack.getItem() instanceof ILens) {
            pool.getItemHandler().setStackInSlot(0, pstack.split(1));
            world.updateComparatorOutputLevel(pos, this);
            pool.markDirty();

            return ActionResultType.SUCCESS;
        }

        return ActionResultType.PASS;
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

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nonnull
    @Override
    public TileEntity createTileEntity(@Nonnull BlockState state, @Nonnull IBlockReader world) {
        return new TileLeakyPool();
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean hasComparatorInputOverride(BlockState state) {
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public int getComparatorInputOverride(BlockState state, World world, BlockPos pos) {
        TileLeakyPool pool = (TileLeakyPool) world.getTileEntity(pos);
        assert pool != null;
        return TileLeakyPool.calculateComparatorLevel(pool.getCurrentMana(), TileLeakyPool.MAX_MANA);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderHUD(Minecraft mc, World world, BlockPos pos) {
        ((TileLeakyPool) Objects.requireNonNull(world.getTileEntity(pos))).renderHUD(mc);
    }

    @Override
    public boolean onUsedByWand(PlayerEntity player, ItemStack stack, World world, BlockPos pos, Direction side) {
        ((TileLeakyPool) Objects.requireNonNull(world.getTileEntity(pos))).onWanded(player);
        return true;
    }

}
