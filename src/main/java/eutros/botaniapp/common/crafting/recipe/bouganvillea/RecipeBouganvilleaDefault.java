package eutros.botaniapp.common.crafting.recipe.bouganvillea;

import com.google.gson.JsonObject;
import eutros.botaniapp.api.recipe.IBouganvilleaInventory;
import eutros.botaniapp.api.recipe.RecipeBouganvillea;
import eutros.botaniapp.common.utils.serialization.RecipeDeserialization;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class RecipeBouganvilleaDefault extends RecipeBouganvillea {

    public static IRecipeSerializer<RecipeBouganvilleaDefault> SERIALIZER = new Serializer();

    public NonNullList<Ingredient> ingredients;

    public RecipeBouganvilleaDefault(ResourceLocation id, ItemStack output, NonNullList<Ingredient> ingredients, @Nullable String group) {
        super(id, output, group);
        this.ingredients = ingredients;
    }

    @NotNull
    @Override
    public NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    @Override
        public boolean shouldTrigger(IBouganvilleaInventory inventory) {
        // TODO recipe checking
        return true;
    }

    @ParametersAreNonnullByDefault
    @Override
    public boolean matches(IBouganvilleaInventory inventory, World world) {
        return ingredients.get(0).test(inventory.getTrigger().getItem());
    }

    @NotNull
        @Override
        public ItemStack getCraftingResult(@NotNull IBouganvilleaInventory inventory) {
        return getRecipeOutput();
    }

    @NotNull
    @Override
    public IRecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    private static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<RecipeBouganvilleaDefault> {
        @Nonnull
        @Override
        public RecipeBouganvilleaDefault read(@Nonnull ResourceLocation recipeId, @Nonnull JsonObject json) {

            String group = JSONUtils.getString(json, "group", "");
            NonNullList<Ingredient> ingredients = RecipeDeserialization.deserializeIngredients(JSONUtils.getJsonArray(json, "ingredients"));
            ItemStack result = RecipeDeserialization.deserializeItem(JSONUtils.getJsonObject(json, "result"));

            return new RecipeBouganvilleaDefault(recipeId, result, ingredients, group);
        }

        @Nullable
        @Override
        public RecipeBouganvilleaDefault read(@Nonnull ResourceLocation recipeId, @Nonnull PacketBuffer buffer) {
            String group = buffer.readString(32767);
            int ingredientSize = buffer.readVarInt();
            NonNullList<Ingredient> ingredients = NonNullList.withSize(ingredientSize, Ingredient.EMPTY);

            for(int j = 0; j < ingredients.size(); ++j) {
                ingredients.set(j, Ingredient.read(buffer));
            }

            ItemStack result = buffer.readItemStack();

            return new RecipeBouganvilleaDefault(recipeId, result, ingredients, group);
        }

        @Override
        public void write(@Nonnull PacketBuffer buffer, @Nonnull RecipeBouganvilleaDefault recipe) {
            buffer.writeString(recipe.getGroup(), 32767);
            buffer.writeVarInt(recipe.ingredients.size());

            recipe.ingredients.forEach(i -> i.write(buffer));

            buffer.writeItemStack(recipe.getRecipeOutput());
        }
    }
}
