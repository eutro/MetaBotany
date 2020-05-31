package eutros.metabotany.common.block.tile;

import eutros.metabotany.common.block.MetaBotanyBlocks;
import eutros.metabotany.common.utils.Reference;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.registries.ObjectHolder;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Arrays;

public class TilePoweredAir extends TileMod implements ITickableTileEntity {

    private static final String DIRECTION = "direction";
    @ObjectHolder(Reference.MOD_ID + ":" + Reference.BlockNames.POWERED_AIR)
    public static TileEntityType<TilePoweredAir> TYPE;
    public Direction[] directions = {};

    public TilePoweredAir(TileEntityType<?> type) {
        super(type);
    }

    public TilePoweredAir() {
        this(TYPE);
    }

    @Override
    public void tick() {
        if(world != null) {
            world.setBlockState(getPos(), Blocks.AIR.getDefaultState());
            BlockPos target = getPos();
            for(Direction dir : directions)
                target = target.offset(dir.getOpposite());
            world.neighborChanged(target, MetaBotanyBlocks.poweredAir, getPos());
        }
    }

    @Override
    public void read(@NotNull CompoundNBT cmp) {
        directions = (Direction[]) Arrays.stream(cmp.getIntArray(DIRECTION)).mapToObj(Direction::byIndex).toArray();
        super.read(cmp);
    }

    @Nonnull
    @Override
    public CompoundNBT write(@NotNull CompoundNBT cmp) {
        super.write(cmp);

        cmp.putIntArray(DIRECTION, Arrays.stream(directions).mapToInt(Direction::getIndex).toArray());

        return cmp;
    }

}
