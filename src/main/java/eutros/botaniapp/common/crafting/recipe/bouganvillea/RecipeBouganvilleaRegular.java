package eutros.botaniapp.common.crafting.recipe.bouganvillea;

import com.google.gson.JsonObject;
import eutros.botaniapp.api.recipe.IBouganvilleaInventory;
import eutros.botaniapp.api.recipe.RecipeBouganvillea;
import eutros.botaniapp.common.utils.serialization.RecipeDeserialization;
import net.minecraft.entity.item.ItemEntity;
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
import java.util.List;

public class RecipeBouganvilleaRegular extends RecipeBouganvillea {

    public static IRecipeSerializer<RecipeBouganvilleaRegular> SERIALIZER = new Serializer();

    public NonNullList<Ingredient> ingredients;

    public RecipeBouganvilleaRegular(ResourceLocation id, ItemStack output, NonNullList<Ingredient> ingredients, @Nullable String group) {
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
        return inventory.getSizeInventory() == ingredients.size();
    }

    @ParametersAreNonnullByDefault
    @Override
    public boolean matches(IBouganvilleaInventory inventory, World world) {
        ItemStack stack = inventory.getThrown().getItem();
        return ingredients.get(inventory.getSizeInventory() - 1).test(stack);
    }

    @NotNull
    @Override
    public ItemStack getCraftingResult(@NotNull IBouganvilleaInventory inventory) {
        ItemStack stack = getRecipeOutput().copy();
        inventory.noReplace();
        List<ItemEntity> entities = inventory.allEntities();
        int minStack = entities.stream().map(ItemEntity::getItem).map(ItemStack::getCount).reduce(Math::min).orElse(0);
        for(ItemEntity e : entities) {
            e.getItem().shrink(minStack);
        }
        stack.setCount(minStack);
        return stack;
    }

    @NotNull
    @Override
    public IRecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    private static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<RecipeBouganvilleaRegular> {

        @Nonnull
        @Override
        public RecipeBouganvilleaRegular read(@Nonnull ResourceLocation recipeId, @Nonnull JsonObject json) {

            String group = JSONUtils.getString(json, "group", "");
            NonNullList<Ingredient> ingredients = RecipeDeserialization.deserializeIngredients(JSONUtils.getJsonArray(json, "ingredients"));
            ItemStack result = RecipeDeserialization.deserializeItem(JSONUtils.getJsonObject(json, "result"));

            return new RecipeBouganvilleaRegular(recipeId, result, ingredients, group);
        }

        @Nullable
        @Override
        public RecipeBouganvilleaRegular read(@Nonnull ResourceLocation recipeId, @Nonnull PacketBuffer buffer) {
            String group = buffer.readString(32767);
            int ingredientSize = buffer.readVarInt();
            NonNullList<Ingredient> ingredients = NonNullList.withSize(ingredientSize, Ingredient.EMPTY);

            for(int j = 0; j < ingredients.size(); ++j) {
                ingredients.set(j, Ingredient.read(buffer));
            }

            ItemStack result = buffer.readItemStack();

            return new RecipeBouganvilleaRegular(recipeId, result, ingredients, group);
        }

        @Override
        public void write(@Nonnull PacketBuffer buffer, @Nonnull RecipeBouganvilleaRegular recipe) {
            buffer.writeString(recipe.getGroup(), 32767);
            buffer.writeVarInt(recipe.ingredients.size());

            recipe.ingredients.forEach(i -> i.write(buffer));

            buffer.writeItemStack(recipe.getRecipeOutput());
        }

    }

}
