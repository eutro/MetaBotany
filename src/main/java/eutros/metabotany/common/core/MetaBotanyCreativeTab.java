package eutros.metabotany.common.core;

import eutros.metabotany.common.block.BlockSparkPainter;
import eutros.metabotany.common.block.MetaBotanyBlocks;
import eutros.metabotany.common.block.flower.MetaBotanyFlowers;
import eutros.metabotany.common.core.helper.ItemNBTHelper;
import eutros.metabotany.common.item.MetaBotanyItems;
import eutros.metabotany.common.utils.Reference;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class MetaBotanyCreativeTab extends ItemGroup {

    public static final MetaBotanyCreativeTab INSTANCE = new MetaBotanyCreativeTab();
    private NonNullList<ItemStack> list;

    public MetaBotanyCreativeTab() {
        super(Reference.MOD_ID);
    }

    @Nonnull
    @Override
    public ItemStack createIcon() {
        ItemStack stack = new ItemStack(MetaBotanyItems.bindingLens);
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

        addItem(MetaBotanyItems.bindingLens);
        addItem(MetaBotanyItems.redstoneLens);
        addItem(MetaBotanyItems.redirectPlusLens);
        addItem(MetaBotanyBlocks.frameTinkerer);
        addItem(MetaBotanyBlocks.advancedFunnel);
        addItem(MetaBotanyBlocks.chargingPlate);
        addItem(MetaBotanyBlocks.leakyPool);
        addItem(BlockSparkPainter.dyeMap.get(DyeColor.WHITE));
        addItem(MetaBotanyFlowers.bouganvillea);
        addItem(MetaBotanyFlowers.bouganvilleaFloating);
    }

    private void addItem(@Nullable IItemProvider item) {
        if(item != null)
            item.asItem().fillItemGroup(this, list);
    }

}
