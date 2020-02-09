package eutros.botaniapp.common.core;

import eutros.botaniapp.common.core.helper.ItemNBTHelper;
import eutros.botaniapp.common.item.BotaniaPPItems;
import eutros.botaniapp.common.utils.Reference;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.NonNullList;
import vazkii.patchouli.common.item.PatchouliItems;

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
    }

    private void addItem(IItemProvider item) {
        item.asItem().fillItemGroup(this, list);
    }
}
