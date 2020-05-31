package eutros.metabotany.common.block.tile;

import eutros.metabotany.common.block.BlockSparkPainter;
import eutros.metabotany.common.utils.Reference;
import net.minecraft.item.DyeColor;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

public class TileSparkPainter extends TileMod {

    @ObjectHolder(Reference.MOD_ID + ":spark_painter")
    public static TileEntityType<TileSparkPainter> TYPE;
    public final DyeColor color;

    public TileSparkPainter() {
        this(DyeColor.WHITE);
    }

    public TileSparkPainter(DyeColor color) {
        super(TYPE);
        this.color = color;
    }

    @Override
    public void validate() {
        super.validate();

        assert world != null;
        world.getPendingBlockTicks().scheduleTick(pos, BlockSparkPainter.dyeMap.get(color), 1);
    }

}
