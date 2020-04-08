package eutros.botaniapp.common.block;

import eutros.botaniapp.common.block.tile.TileChargingPlate;
import eutros.botaniapp.common.block.tile.TileSimpleInventory;
import eutros.botaniapp.common.core.helper.InventoryHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
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
import vazkii.botania.api.mana.IManaItem;
import vazkii.botania.api.wand.IWandHUD;

import javax.annotation.Nonnull;
import java.util.Objects;

public class BlockChargingPlate extends Block implements IWandHUD {

    private static VoxelShape BASE = makeCuboidShape(2, 0, 2, 14, 2, 14);
    private static VoxelShape TOP = makeCuboidShape(0, 2, 0, 16, 4, 16);
    private static VoxelShape SHAPE = VoxelShapes.combineAndSimplify(BASE, TOP, IBooleanFunction.OR);

    public BlockChargingPlate(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @SuppressWarnings("deprecation")
    @Nonnull
    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Nonnull
    @Override
    public TileEntity createTileEntity(@Nonnull BlockState state, @Nonnull IBlockReader world) {
        return new TileChargingPlate();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderHUD(Minecraft mc, World world, BlockPos pos) {
        ((TileChargingPlate) Objects.requireNonNull(world.getTileEntity(pos))).renderHUD(mc);
    }

    @SuppressWarnings("deprecation")
    @NotNull
    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        TileChargingPlate charger = (TileChargingPlate) world.getTileEntity(pos);
        ItemStack pstack = player.getHeldItem(hand);
        assert charger != null;
        ItemStack cstack = charger.getItemHandler().getStackInSlot(0);
        if(!cstack.isEmpty()) {
            charger.getItemHandler().setStackInSlot(0, ItemStack.EMPTY);
            world.updateComparatorOutputLevel(pos, this);
            charger.markDirty();
            ItemHandlerHelper.giveItemToPlayer(player, cstack);
            return ActionResultType.SUCCESS;
        } else if(!pstack.isEmpty() && pstack.getItem() instanceof IManaItem) {
            charger.getItemHandler().setStackInSlot(0, pstack.split(1));
            world.updateComparatorOutputLevel(pos, this);
            charger.markDirty();

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

    @SuppressWarnings("deprecation")
    @Override
    public boolean hasComparatorInputOverride(BlockState state) {
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
        return ((TileChargingPlate) Objects.requireNonNull(worldIn.getTileEntity(pos))).comparatorPower();
    }

}
