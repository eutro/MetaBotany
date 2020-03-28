package eutros.botaniapp.common.utils.serialization;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.util.NonNullList;

public class RecipeDeserialization {

    private RecipeDeserialization() {
    }


    public static NonNullList<Ingredient> deserializeIngredients(JsonArray ingredients) {
        NonNullList<Ingredient> ingredientList = NonNullList.create();

        for(int i = 0; i < ingredients.size(); ++i) {
            Ingredient ingredient = Ingredient.deserialize(ingredients.get(i));
            if(!ingredient.hasNoMatchingItems()) {
                ingredientList.add(ingredient);
            }
        }

        return ingredientList;
    }

    public static ItemStack deserializeItem(JsonObject result) {
        return ShapedRecipe.deserializeItem(result);
    }

}
