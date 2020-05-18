package eutros.botaniapp.client.integration.jei;

import com.mojang.blaze3d.systems.RenderSystem;
import eutros.botaniapp.common.block.BotaniaPPBlocks;
import eutros.botaniapp.common.core.helper.ItemNBTHelper;
import eutros.botaniapp.common.crafting.recipe.RecipeLeakyPool;
import eutros.botaniapp.common.utils.Reference;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.block.Blocks;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.*;
import java.util.Collections;
import java.util.List;

public class RecipeCategoryTNTPool implements IRecipeCategory<RecipeLeakyPool> {

    public static final ResourceLocation LOCATION = new ResourceLocation(Reference.MOD_ID, "tnt_pool_recipe_category");

    private IDrawable background;
    private IDrawable icon;
    private IDrawable overlay;

    public RecipeCategoryTNTPool(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(new ResourceLocation(Reference.MOD_ID, "textures/gui/jei_a_to_b.png"),
                0, 0, 144, 68);
        this.overlay = guiHelper.createDrawable(new ResourceLocation(Reference.MOD_ID, "textures/gui/explosion_crafting.png"),
                0, 0, 50, 44);
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(Blocks.TNT));
    }

    @Nonnull
    @Override
    public ResourceLocation getUid() {
        return LOCATION;
    }

    @Nonnull
    @Override
    public Class<? extends RecipeLeakyPool> getRecipeClass() {
        return RecipeLeakyPool.class;
    }

    @Nonnull
    @Override
    public String getTitle() {
        return I18n.format("recipe." + BotaniaPPBlocks.leakyPool.getTranslationKey());
    }

    @Nonnull
    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Nonnull
    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @ParametersAreNonnullByDefault
    @Override
    public void setIngredients(RecipeLeakyPool recipe, IIngredients ingredients) {
        ItemStack renderStack = new ItemStack(BotaniaPPBlocks.BOTANIA_MANA_POOL);
        ItemNBTHelper.setBoolean(renderStack, "RenderFull", true);
        ingredients.setInput(VanillaTypes.ITEM, renderStack);
        ingredients.setOutput(VanillaTypes.ITEM, new ItemStack(BotaniaPPBlocks.leakyPool));
    }

    @Override
    public void draw(@NotNull RecipeLeakyPool recipe, double mouseX, double mouseY) {
        RenderSystem.enableAlphaTest();
        RenderSystem.enableBlend();
        overlay.draw(background.getWidth() / 2 - overlay.getWidth() / 2,
                background.getHeight() / 2 - overlay.getHeight() / 2);
        RenderSystem.disableAlphaTest();
        RenderSystem.disableBlend();
    }

    @Nonnull
    @Override
    public List<String> getTooltipStrings(@NotNull RecipeLeakyPool recipe, double mouseX, double mouseY) {

        if(Math.abs(mouseX - background.getWidth() / 2F) <= overlay.getWidth() / 2F &&
                Math.abs(mouseY - background.getHeight() / 2F) <= overlay.getHeight() / 2F)
            return Collections.singletonList(I18n.format("botaniapp.explosion.explosion"));

        return Collections.emptyList();
    }

    @ParametersAreNonnullByDefault
    @Override
    public void setRecipe(IRecipeLayout layout, RecipeLeakyPool recipe, IIngredients ingredients) {
        IGuiItemStackGroup stacks = layout.getItemStacks();
        List<List<ItemStack>> inputs = ingredients.getInputs(VanillaTypes.ITEM);
        Point center = new Point(background.getWidth() / 2 - 9, background.getHeight() / 2 - 8);

        stacks.init(0, true, center.x - background.getWidth() / 3, center.y);
        stacks.set(0, inputs.get(0));

        List<List<ItemStack>> outputs = ingredients.getOutputs(VanillaTypes.ITEM);

        stacks.init(1, false, center.x + background.getWidth() / 3, center.y);
        stacks.set(1, outputs.get(0));
    }

}
