package eutros.botaniapp.common.crafting.recipe;

import eutros.botaniapp.common.item.BotaniaPPItems;
import eutros.botaniapp.common.item.lens.BindingLens;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class RecipeLensUnbinding extends SpecialRecipe {

    public static final IRecipeSerializer<RecipeLensUnbinding> SERIALIZER = new SpecialRecipeSerializer<>(RecipeLensUnbinding::new);

    public RecipeLensUnbinding(ResourceLocation location) {
        super(location);
    }

    @Override
    public boolean matches(@NotNull CraftingInventory inv, @NotNull World worldIn) {
        boolean bound = false;
        boolean found = false;

        for(int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if(stack.isEmpty())
                continue;

            if(found)
                return false;

            if(stack.getItem() == BotaniaPPItems.bindingLens) {
                stack = stack.copy();
                bound = BindingLens.getBindingAttempt(stack).isPresent();
                found = true;
            }
        }

        return bound;
    }

    @NotNull
    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        for(int i = 0; i < inv.getSizeInventory(); i++) {
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
        return width * height > 0;
    }

    @Nonnull
    @Override
    public NonNullList<Ingredient> getIngredients() {
        ItemStack stack = new ItemStack(BotaniaPPItems.bindingLens);

        BindingLens.setBindingAttempt(stack, new BlockPos(0, 0, 0));

        return NonNullList.withSize(1, Ingredient.fromStacks(stack));
    }

    @NotNull
    @Override
    public ItemStack getRecipeOutput() {
        return new ItemStack(BotaniaPPItems.bindingLens);
    }

    @NotNull
    @Override
    public IRecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

}
