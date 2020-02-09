package eutros.botaniapp.common.core.helper;

import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;

import javax.annotation.Nullable;

public class PetalHelper {

    public static int idOf(Item item) {
        int colorId;

        if (item instanceof vazkii.botania.common.item.material.ItemPetal) {
            colorId = ((vazkii.botania.common.item.material.ItemPetal)item).color.getId();
        } else {
            if (!(item instanceof BlockItem) || !(((BlockItem)item).getBlock() instanceof vazkii.botania.common.block.decor.BlockModMushroom)) {
                return -1;
            }

            colorId = ((vazkii.botania.common.block.decor.BlockModMushroom)((BlockItem)item).getBlock()).color.getId();
        }

        return colorId;
    }

    public static DyeColor colorOf(Item item) {
        return DyeColor.byId(idOf(item));
    }
}
