package eutros.botaniapp.common.core;

import eutros.botaniapp.common.block.BlockSparkPainter;
import eutros.botaniapp.common.block.BotaniaPPBlocks;
import eutros.botaniapp.common.block.flower.BotaniaPPFlowers;
import eutros.botaniapp.common.core.helper.ItemNBTHelper;
import eutros.botaniapp.common.item.BotaniaPPItems;
import eutros.botaniapp.common.utils.Reference;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;

public final class BotaniappCreativeTab extends ItemGroup {

    public static final BotaniappCreativeTab INSTANCE = new BotaniappCreativeTab();
    private NonNullList<ItemStack> list;

    public BotaniappCreativeTab() {
        super(Reference.MOD_ID);
    }

    @Nonnull
    @Override
    public ItemStack createIcon() {
        ItemStack stack = new ItemStack(BotaniaPPItems.bindingLens);
        ItemNBTHelper.setInt(stack, "color1", 16);
        ItemNBTHelper.setInt(stack, "color2", 16);
        return stack;
    }

    @Override
    public boolean hasSearchBar() {
        return false;
    }

    @Override
    public void fill(@Nonnull NonNullList<ItemStack> list) {
        this.list = list;

        addItem(BotaniaPPItems.bindingLens);
        addItem(BotaniaPPItems.redstoneLens);
        addItem(BotaniaPPBlocks.frameTinkerer);
        addItem(BotaniaPPBlocks.advancedFunnel);
        addItem(BotaniaPPBlocks.chargingPlate);
        addItem(BotaniaPPBlocks.leakyPool);
        addItem(BlockSparkPainter.dyeMap.get(DyeColor.WHITE));
        addItem(BotaniaPPFlowers.bouganvillea);
        addItem(BotaniaPPFlowers.bouganvilleaFloating);
    }

    private void addItem(IItemProvider item) {
        item.asItem().fillItemGroup(this, list);
    }
}
