package eutros.botaniapp.common.block.corporea;

import eutros.botaniapp.api.internal.block.BlockRedstoneControlled;
import eutros.botaniapp.common.block.tile.corporea.TileAdvancedFunnel;
import eutros.botaniapp.common.block.tile.corporea.TileCorporeaBase;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;
import vazkii.botania.api.wand.IWandHUD;
import vazkii.botania.api.wand.IWandable;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Objects;

public class BlockAdvancedFunnel extends BlockRedstoneControlled implements IWandable, IWandHUD {

    public BlockAdvancedFunnel(Properties builder) {
        super(builder);
    }

    @Override
    public Collection<Pair<BlockPos, Direction>> getRedstoneChecks(BlockPos pos) {
        Collection<Pair<BlockPos, Direction>> checks = super.getRedstoneChecks(pos);
        checks.addAll(super.getRedstoneChecks(pos.up()));
        return checks;
    }

    @Override
    public void doPulse(BlockState state, BlockPos pos, World world, BlockPos from) {
        ((TileAdvancedFunnel) Objects.requireNonNull(world.getTileEntity(pos))).doRequest();
    }

    @Nonnull
    @Override
    public TileCorporeaBase createTileEntity(@Nonnull BlockState state, @Nonnull IBlockReader world) {
        return new TileAdvancedFunnel();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public boolean onUsedByWand(PlayerEntity player, ItemStack stack, World world, BlockPos pos, Direction side) {
        return ((TileAdvancedFunnel) Objects.requireNonNull(world.getTileEntity(pos))).onWanded();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderHUD(Minecraft mc, World world, BlockPos pos) {
        ((TileAdvancedFunnel) Objects.requireNonNull(world.getTileEntity(pos))).renderHUD(mc);
    }
}
