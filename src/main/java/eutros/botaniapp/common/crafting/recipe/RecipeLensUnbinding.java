package eutros.botaniapp.common.crafting.recipe;

import com.google.gson.JsonObject;
import eutros.botaniapp.common.item.BotaniaPPItems;
import eutros.botaniapp.common.item.lens.BindingLens;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RecipeLensUnbinding implements ICraftingRecipe {
    public static final IRecipeSerializer<RecipeLensUnbinding> SERIALIZER = new Serializer();
    private final ShapelessRecipe compose;

    public RecipeLensUnbinding(ShapelessRecipe compose) {
        this.compose = compose;
    }

    @Override
    public boolean matches(@NotNull CraftingInventory inv, @NotNull World worldIn) {
        return compose.matches(inv, worldIn);
    }

    @NotNull
    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);

            if(stack.getItem() == BotaniaPPItems.bindingLens) {
                stack = stack.copy();
                BindingLens.setBindingAttempt(stack, BindingLens.UNBOUND_POS);
                return stack;
            }
        }
        return new ItemStack(BotaniaPPItems.bindingLens);
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

    private static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<RecipeLensUnbinding> {
        @Nonnull
        @Override
        public RecipeLensUnbinding read(@Nonnull ResourceLocation recipeId, @Nonnull JsonObject json) {
            return new RecipeLensUnbinding(CRAFTING_SHAPELESS.read(recipeId, json));
        }

        @Nullable
        @Override
        public RecipeLensUnbinding read(@Nonnull ResourceLocation recipeId, @Nonnull PacketBuffer buffer) {
            return new RecipeLensUnbinding(CRAFTING_SHAPELESS.read(recipeId, buffer));
        }

        @Override
        public void write(@Nonnull PacketBuffer buffer, @Nonnull RecipeLensUnbinding recipe) {
            CRAFTING_SHAPELESS.write(buffer, recipe.compose);
        }
    }
}
