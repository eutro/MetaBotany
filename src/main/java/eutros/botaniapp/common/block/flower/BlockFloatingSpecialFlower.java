/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * <p>
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * <p>
 * File Created @ [Nov 17, 2014, 5:31:53 PM (GMT)]
 */
package eutros.botaniapp.common.block.flower;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
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
import vazkii.botania.common.block.decor.BlockFloatingFlower;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Supplier;

// TODO beg for API integration I guess?

@SuppressWarnings("deprecation")
public class BlockFloatingSpecialFlower extends BlockFloatingFlower implements IWandable, IWandHUD {

    private final Supplier<? extends TileEntitySpecialFlower> teProvider;

    public BlockFloatingSpecialFlower(Properties props, Supplier<? extends TileEntitySpecialFlower> teProvider) {
        super(DyeColor.WHITE, props);
        this.teProvider = teProvider;
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

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
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

    @Nonnull
    @Override
    public TileEntity createTileEntity(@Nonnull BlockState state, @Nonnull IBlockReader world) {
        return teProvider.get();
    }

}
