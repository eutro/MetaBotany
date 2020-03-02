package eutros.botaniapp.client.integration.jei;

import eutros.botaniapp.api.recipe.RecipeBouganvillea;
import eutros.botaniapp.common.block.flower.BotaniaPPFlowers;
import eutros.botaniapp.common.crafting.recipe.bouganvillea.RecipeBouganvilleaAnvil;
import eutros.botaniapp.common.utils.MathUtils;
import eutros.botaniapp.common.utils.Reference;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RecipeCategoryBouganvillea implements IRecipeCategory<RecipeBouganvillea> {

    public static final ResourceLocation LOCATION = new ResourceLocation(Reference.MOD_ID, "bouganvillea_recipe_category");

    private IDrawable background;
    private IDrawable icon;


    public RecipeCategoryBouganvillea(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(168, 64);
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
    @SuppressWarnings("unchecked")
    public void setIngredients(@NotNull RecipeBouganvillea recipe, @NotNull IIngredients ingredients) {
        if (recipe instanceof RecipeBouganvilleaAnvil) {
            List<List<ItemStack>> inputStash = Arrays.asList(new ArrayList<>(), new ArrayList<>());
            List<ItemStack> outputStash = new ArrayList<>();
            if(JEIBotaniaPPPlugin.runtime != null) {
                IRecipeManager recipeManager = JEIBotaniaPPPlugin.runtime.getRecipeManager();
                IRecipeCategory<Object> category = recipeManager.getRecipeCategory(VanillaRecipeCategoryUid.ANVIL);
                assert category != null;
                List<?> recipes = recipeManager.getRecipes(category);
                for (Object rcp : recipes) {
                    category.setIngredients(rcp, ingredients);
                    List<List<ItemStack>> inputs = ingredients.getInputs(VanillaTypes.ITEM);

                    for(int i = 0; i < 2; i++) {
                        for(int j = 0; j < inputs.get(1 - i).size(); j++) {
                            inputStash.get(i).addAll(inputs.get(i));
                        }
                    }

                    for(int i = 0; i < inputs.get(0).size() * inputs.get(1).size(); i++) {
                        outputStash.addAll(ingredients.getOutputs(VanillaTypes.ITEM).get(0));
                    }
                    ingredients.setInputs(VanillaTypes.ITEM, Collections.emptyList());
                    ingredients.setOutputs(VanillaTypes.ITEM, Collections.emptyList());
                }
                int maxLen = Math.min(outputStash.size(), Math.min(inputStash.get(0).size(), inputStash.get(1).size()));

                ingredients.setInputLists(VanillaTypes.ITEM, Arrays.asList(
                        Arrays.asList(recipe.getIngredients().get(0).getMatchingStacks()),
                        inputStash.get(0).subList(0, maxLen),
                        inputStash.get(1).subList(0, maxLen)
                ));
                ingredients.setOutputLists(VanillaTypes.ITEM, Collections.singletonList(outputStash.subList(0, maxLen)));
            }

        } else {
            ingredients.setInputIngredients(recipe.getIngredients());
            ingredients.setOutputLists(VanillaTypes.ITEM, Collections.singletonList(recipe.getDynamicOutput(ingredients.getInputs(VanillaTypes.ITEM))));
        }
    }

    @ParametersAreNonnullByDefault
    @Override
    public void setRecipe(IRecipeLayout recipeLayout, RecipeBouganvillea recipe, IIngredients ingredients) {
        IGuiItemStackGroup stacks = recipeLayout.getItemStacks();
        Point center = new Point(background.getWidth()/2-8, background.getHeight()/2-8);

        stacks.init(0, false, center.x, center.y);
        stacks.set(0, new ItemStack(BotaniaPPFlowers.bouganvillea));

        List<List<ItemStack>> inputs = ingredients.getInputs(VanillaTypes.ITEM);

        double angleBetweenEach = Math.max(30.0, 180.0 / inputs.size());
        Point point = new Point(center);
        point.translate(0, -background.getHeight()/3);
        point = MathUtils.rotatePointAbout(point, center, -angleBetweenEach*(inputs.size()-1)/2);

        for(int i = 0; i < inputs.size(); i++) {
            stacks.init(i+2, true, point.x, point.y);
            stacks.set(i+2, inputs.get(i));
            point = MathUtils.rotatePointAbout(point, center, angleBetweenEach);
        }

        stacks.init(1, false, center.x, center.y+background.getHeight()/3);
        stacks.set(1, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
    }

}
