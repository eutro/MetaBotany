package eutros.botaniapp.common.block.tile;

import eutros.botaniapp.common.block.BlockSparkPainter;
import eutros.botaniapp.common.utils.Reference;
import net.minecraft.item.DyeColor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TileSparkPainter extends TileEntity {

    private static final Logger LOGGER = LogManager.getLogger();

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
