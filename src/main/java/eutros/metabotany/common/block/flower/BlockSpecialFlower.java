/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * <p>
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * <p>
 * File Created @ [Jan 22, 2014, 7:06:38 PM (GMT)]
 */
package eutros.metabotany.common.block.flower;

import eutros.metabotany.common.block.MetaBotanyBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effects;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import vazkii.botania.api.subtile.TileEntitySpecialFlower;
import vazkii.botania.api.wand.IWandHUD;
import vazkii.botania.api.wand.IWandable;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

// TODO beg for API integration I guess?

@SuppressWarnings("deprecation")
public class BlockSpecialFlower extends FlowerBlock implements IWandable, IWandHUD {

    private static final VoxelShape SHAPE = makeCuboidShape(4.8, 0, 4.8, 12.8, 16, 12.8);
    private final Supplier<? extends TileEntitySpecialFlower> teProvider;

    protected BlockSpecialFlower(Properties props, Supplier<? extends TileEntitySpecialFlower> teProvider) {
        super(Effects.SPEED, 4, props);
        this.teProvider = teProvider;
    }

    @Nonnull
    @Override
    public VoxelShape getShape(BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos, ISelectionContext ctx) {
        Vec3d shift = state.getOffset(world, pos);
        return SHAPE.withOffset(shift.x, shift.y, shift.z);
    }

    @Override
    public boolean hasComparatorInputOverride(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(BlockState state, World world, BlockPos pos) {
        return ((TileEntitySpecialFlower) Objects.requireNonNull(world.getTileEntity(pos))).getComparatorInputOverride();
    }

    @Override
    public int getWeakPower(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
        return ((TileEntitySpecialFlower) Objects.requireNonNull(world.getTileEntity(pos))).getPowerLevel(side);
    }

    @Override
    public int getStrongPower(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
        return getWeakPower(state, world, pos, side);
    }

    @Override
    public boolean canProvidePower(BlockState state) {
        return true;
    }

    @Override
    protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return state.getBlock() == MetaBotanyBlocks.BOTANIA_RED_STRING_RELAY
                || state.getBlock() == Blocks.MYCELIUM
                || super.isValidGround(state, worldIn, pos);
    }

    @Override
    public void onBlockHarvested(World world, @NotNull BlockPos pos, BlockState state, @NotNull PlayerEntity player) {
        super.onBlockHarvested(world, pos, state, player);
        ((TileEntitySpecialFlower) Objects.requireNonNull(world.getTileEntity(pos))).onBlockHarvested(world, pos, state, player);
    }

    @Nonnull
    @Override
    public List<ItemStack> getDrops(@Nonnull BlockState state, @Nonnull LootContext.Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder);
        TileEntity te = builder.get(LootParameters.BLOCK_ENTITY);
        if(te instanceof TileEntitySpecialFlower) {
            return ((TileEntitySpecialFlower) te).getDrops(drops, builder);
        } else {
            return drops;
        }
    }

    @Override
    public boolean eventReceived(BlockState state, World world, BlockPos pos, int par5, int par6) {
        super.eventReceived(state, world, pos, par5, par6);
        TileEntity tileentity = world.getTileEntity(pos);
        return tileentity != null && tileentity.receiveClientEvent(par5, par6);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nonnull
    @Override
    public TileEntity createTileEntity(@Nonnull BlockState state, @Nonnull IBlockReader world) {
        return teProvider.get();
    }

    @Override
    public boolean onUsedByWand(PlayerEntity player, ItemStack stack, World world, BlockPos pos, Direction side) {
        return ((TileEntitySpecialFlower) Objects.requireNonNull(world.getTileEntity(pos))).onWanded(player, stack);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
        ((TileEntitySpecialFlower) Objects.requireNonNull(world.getTileEntity(pos))).onBlockPlacedBy(world, pos, state, entity, stack);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean isMoving) {
        ((TileEntitySpecialFlower) Objects.requireNonNull(world.getTileEntity(pos))).onBlockAdded(world, pos, state);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderHUD(Minecraft mc, World world, BlockPos pos) {
        ((TileEntitySpecialFlower) Objects.requireNonNull(world.getTileEntity(pos))).renderHUD(mc);
    }

}
