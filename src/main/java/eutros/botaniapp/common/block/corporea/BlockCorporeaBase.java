package eutros.botaniapp.common.block.corporea;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;

public class BlockCorporeaBase extends Block {

    public BlockCorporeaBase(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }
}
