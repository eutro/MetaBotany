package eutros.botaniapp.client.integration.jei;

import eutros.botaniapp.api.recipe.RecipeBouganvillea;
import eutros.botaniapp.common.block.flower.BotaniaPPFlowers;
import eutros.botaniapp.common.utils.Reference;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

public class RecipeCategoryBouganvillea implements IRecipeCategory<RecipeBouganvillea> {

    public static final ResourceLocation LOCATION = new ResourceLocation(Reference.MOD_ID, "bouganvillea_recipe_category");

    private static final ResourceLocation vanilla_gui = new ResourceLocation("jei", "textures/gui/gui_vanilla.png");
    private IDrawable background;
    private IDrawable icon;


    public RecipeCategoryBouganvillea(IGuiHelper guiHelper) {
        this.background = guiHelper.drawableBuilder(vanilla_gui, 0, 168, 125, 18).addPadding(0, 20, 0, 0).build();
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(BotaniaPPFlowers.bouganvillea));
    }

    @NotNull
    @Override
    public ResourceLocation getUid() {
        return LOCATION;
    }

    @NotNull
    @Override
    public Class<RecipeBouganvillea> getRecipeClass() {
        return RecipeBouganvillea.class;
    }

    @NotNull
    @Override
    public String getTitle() {
        return I18n.format("block.botaniapp.flower_bouganvillea");
    }

    @NotNull
    @Override
    public IDrawable getBackground() {
        return background;
    }

    @NotNull
    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setIngredients(RecipeBouganvillea recipe, IIngredients ingredients) {
        ingredients.setInputIngredients(recipe.getIngredients());
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getRecipeOutput());
    }

    @ParametersAreNonnullByDefault
    @Override
    public void setRecipe(IRecipeLayout recipeLayout, RecipeBouganvillea recipe, IIngredients ingredients) {
        // oh no
    }
}
