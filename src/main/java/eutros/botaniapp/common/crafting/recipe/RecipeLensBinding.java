package eutros.botaniapp.common.crafting.recipe;

import com.google.gson.JsonObject;
import eutros.botaniapp.common.core.helper.PetalHelper;
import eutros.botaniapp.common.item.lens.BindingLens;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.IShapedRecipe;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.jetbrains.annotations.NotNull;
import vazkii.botania.common.item.ModItems;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RecipeLensBinding implements ICraftingRecipe, IShapedRecipe<CraftingInventory> {
    public static final IRecipeSerializer<RecipeLensBinding> SERIALIZER = new Serializer();
    private final ShapedRecipe compose;

    public RecipeLensBinding(ShapedRecipe compose) {
        this.compose = compose;
    }

    @Override
    public boolean matches(@NotNull CraftingInventory inv, @NotNull World worldIn) {
        return compose.matches(inv, worldIn);
    }

    @NotNull
    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        int first = -1;
        int colorId;
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            Item item = stack.getItem();

            colorId = item == ModItems.pixieDust ? 16 : PetalHelper.idOf(item);

            if(colorId == -1) {
                continue;
            }

            if (first == -1)
                first = colorId;
            else
                return BindingLens.forColors(first, colorId);
        }
        return BindingLens.forColors(first != -1 ? first : 0, 0);
    }

    @Override
    public boolean canFit(int width, int height) {
        return compose.canFit(width, height);
    }

    @Nonnull
    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInventory inv) {
        return compose.getRemainingItems(inv);
    }

    @Nonnull
    @Override
    public NonNullList<Ingredient> getIngredients() {
        return compose.getIngredients();
    }

    @Nonnull
    @Override
    public String getGroup() {
        return compose.getGroup();
    }

    @Nonnull
    @Override
    public ItemStack getIcon() {
        return compose.getIcon();
    }

    @NotNull
    @Override
    public ItemStack getRecipeOutput() {
        return compose.getRecipeOutput();
    }

    @NotNull
    @Override
    public ResourceLocation getId() {
        return compose.getId();
    }

    @NotNull
    @Override
    public IRecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public int getRecipeWidth() {
        return compose.getRecipeWidth();
    }

    @Override
    public int getRecipeHeight() {
        return compose.getRecipeHeight();
    }

    private static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<RecipeLensBinding> {
        @Nonnull
        @Override
        public RecipeLensBinding read(@Nonnull ResourceLocation recipeId, @Nonnull JsonObject json) {
            return new RecipeLensBinding(CRAFTING_SHAPED.read(recipeId, json));
        }

        @Nullable
        @Override
        public RecipeLensBinding read(@Nonnull ResourceLocation recipeId, @Nonnull PacketBuffer buffer) {
            return new RecipeLensBinding(CRAFTING_SHAPED.read(recipeId, buffer));
        }

        @Override
        public void write(@Nonnull PacketBuffer buffer, @Nonnull RecipeLensBinding recipe) {
            CRAFTING_SHAPED.write(buffer, recipe.compose);
        }
    }
}
