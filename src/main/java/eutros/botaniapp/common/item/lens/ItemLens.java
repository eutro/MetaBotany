package eutros.botaniapp.common.item.lens;

import eutros.botaniapp.common.core.helper.ItemNBTHelper;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import vazkii.botania.api.internal.IManaBurst;
import vazkii.botania.api.mana.*;

import javax.annotation.Nonnull;

public class ItemLens extends Item implements ILens, ICompositableLens, ILensControl {

    protected static final String TAG_COLOR = "color";
    private static final String TAG_COMPOSITE_LENS = "compositeLens";
    protected boolean updateColor = false;

    public ItemLens(Properties properties) {
        super(properties);
    }

    @Nonnull
    @Override
    public ITextComponent getDisplayName(@Nonnull ItemStack stack) {
        ItemStack compositeLens = getCompositeLens(stack);
        if(compositeLens.isEmpty())
            return super.getDisplayName(stack);
        String shortKeyA = stack.getTranslationKey() + ".short";
        String shortKeyB = compositeLens.getTranslationKey() + ".short";
        return new TranslationTextComponent("item.botania.composite_lens", new TranslationTextComponent(shortKeyA), new TranslationTextComponent(shortKeyB));
    }

    @Override
    public void apply(ItemStack stack, BurstProperties props) {
        int color = ItemNBTHelper.getInt(stack, TAG_COLOR, -1);
        if(color != -1)
            props.color = DyeColor.byId(color).getColorValue();

        ItemStack compositeLens = getCompositeLens(stack);
        if(!compositeLens.isEmpty() && compositeLens.getItem() instanceof ILens)
            ((ILens) compositeLens.getItem()).apply(compositeLens, props);
    }

    @Override
    public boolean collideBurst(IManaBurst burst, RayTraceResult pos, boolean isManaBlock, boolean dead, ItemStack stack) {

        ItemStack compositeLens = getCompositeLens(stack);
        if(!compositeLens.isEmpty() && compositeLens.getItem() instanceof ILens)
            dead = ((ILens) compositeLens.getItem()).collideBurst(burst, pos, isManaBlock, dead, compositeLens);

        return dead;
    }

    @Override
    public void updateBurst(IManaBurst burst, ItemStack stack) {

        if(updateColor && ((ThrowableEntity) burst).world.isRemote)
            burst.setColor(getLensColor(stack));

        ItemStack compositeLens = getCompositeLens(stack);
        if(!compositeLens.isEmpty() && compositeLens.getItem() instanceof ILens)
            ((ILens) compositeLens.getItem()).updateBurst(burst, compositeLens);
    }

    @Override
    public int getLensColor(ItemStack stack) {
        return DyeColor.byId(ItemNBTHelper.getInt(stack, TAG_COLOR, 0)).getColorValue();
    }

    @Override
    public boolean doParticles(IManaBurst burst, ItemStack stack) {
        return true;
    }

    protected boolean isBlacklist(ItemStack sourceLens, ItemStack compositeLens) {
        return false;
    }

    @Override
    public boolean canCombineLenses(ItemStack sourceLens, ItemStack compositeLens) {
        ICompositableLens sourceItem = (ICompositableLens) sourceLens.getItem();
        ICompositableLens compositeItem = (ICompositableLens) compositeLens.getItem();
        if(sourceItem == compositeItem || isBlacklist(sourceLens, compositeLens))
            return false;

        return compositeItem.isCombinable(compositeLens);
    }

    @Override
    public ItemStack getCompositeLens(ItemStack stack) {
        CompoundNBT cmp = ItemNBTHelper.getCompound(stack, TAG_COMPOSITE_LENS, true);
        if(cmp == null)
            return ItemStack.EMPTY;
        else return ItemStack.read(cmp);
    }

    @Override
    public ItemStack setCompositeLens(ItemStack sourceLens, ItemStack compositeLens) {
        if(!compositeLens.isEmpty()) {
            CompoundNBT cmp = compositeLens.write(new CompoundNBT());
            ItemNBTHelper.setCompound(sourceLens, TAG_COMPOSITE_LENS, cmp);
        }
        return sourceLens;
    }

    @Override
    public int getProps(ItemStack stack) {
        return 0;
    }

    @Override
    public boolean isCombinable(ItemStack stack) {
        return true;
    }

    @Override
    public boolean isControlLens(ItemStack stack) {
        ItemStack compositeLens = getCompositeLens(stack);
        if(!compositeLens.isEmpty() && compositeLens.getItem() instanceof ILens)
            return ((ILensControl) compositeLens.getItem()).isControlLens(compositeLens);
        return false;
    }

    @Override
    public boolean allowBurstShooting(ItemStack stack, IManaSpreader spreader, boolean redstone) {
        ItemStack compositeLens = getCompositeLens(stack);
        if(!compositeLens.isEmpty() && compositeLens.getItem() instanceof ILens)
            return ((ILensControl) compositeLens.getItem()).allowBurstShooting(compositeLens, spreader, redstone);
        return false;
    }

    @Override
    public void onControlledSpreaderTick(ItemStack stack, IManaSpreader spreader, boolean redstone) {
        ItemStack compositeLens = getCompositeLens(stack);
        if(!compositeLens.isEmpty() && compositeLens.getItem() instanceof ILens)
            ((ILensControl) compositeLens.getItem()).onControlledSpreaderTick(compositeLens, spreader, redstone);
    }

    @Override
    public void onControlledSpreaderPulse(ItemStack stack, IManaSpreader spreader, boolean redstone) {
        ItemStack compositeLens = getCompositeLens(stack);
        if(!compositeLens.isEmpty() && compositeLens.getItem() instanceof ILens)
            ((ILensControl) compositeLens.getItem()).onControlledSpreaderTick(compositeLens, spreader, redstone);
    }

}