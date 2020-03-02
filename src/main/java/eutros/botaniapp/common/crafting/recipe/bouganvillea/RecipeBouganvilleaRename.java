package eutros.botaniapp.common.crafting.recipe.bouganvillea;

import eutros.botaniapp.api.recipe.IBouganvilleaInventory;
import eutros.botaniapp.api.recipe.RecipeBouganvillea;
import eutros.botaniapp.common.block.flower.functional.SubtileBouganvillea;
import eutros.botaniapp.common.item.BotaniaPPItems;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class RecipeBouganvilleaRename extends RecipeBouganvillea {

    public static IRecipeSerializer<RecipeBouganvilleaRename> SERIALIZER = new SpecialRecipeSerializer<>(RecipeBouganvilleaRename::new);

    private static final ItemStack manaString = new ItemStack(BotaniaPPItems.BOTANIA_MANA_STRING);

    public RecipeBouganvilleaRename(ResourceLocation location) {
        super(location,
                manaString.copy().setDisplayName(new StringTextComponent("What is your name?")),
                SubtileBouganvillea.BUILTIN_GROUP);
    }

    @Override
    public boolean shouldTrigger(IBouganvilleaInventory inv) {
        ItemStack stack = inv.getThrown().getItem();
        return stack.getCount() != 1 || stack.getItem() != BotaniaPPItems.BOTANIA_MANA_STRING;
    }

    @ParametersAreNonnullByDefault
    @Override
    public boolean matches(IBouganvilleaInventory inventory, World world) {
        return inventory.getThrown().getItem().getItem() == BotaniaPPItems.BOTANIA_MANA_STRING;
    }

    @NotNull
    @Override
    public ItemStack getCraftingResult(IBouganvilleaInventory inventory) {
        // TODO these seem a bit dodgy
        TextComponent tc = new StringTextComponent("");
        for(ItemEntity entity : inventory.allEntities().subList(0, inventory.getSizeInventory()-1)) {
            tc.appendSibling(entity.getItem().getDisplayName());
        }

        if(!tc.equals(new StringTextComponent("")))
            inventory.getThrown().getItem().setDisplayName(tc);

        return inventory.getThrown().getItem();
    }

    public ItemStack getStacksResult(List<ItemStack> stacks) {
        TextComponent tc = new StringTextComponent("");
        for(ItemStack stack : stacks) {
            tc.appendSibling(stack.getDisplayName());
        }

        ItemStack trigger = stacks.get(stacks.size() - 1).copy();
        if(!tc.equals(new StringTextComponent("")))
            trigger.setDisplayName(tc);

        return trigger;
    }

    @NotNull
    @Override
    public IRecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    private final NonNullList<Ingredient> exampleIngredients = NonNullList.from(
            Ingredient.EMPTY,
                Ingredient.fromStacks(manaString.copy().setDisplayName(new StringTextComponent("What ")),
                        manaString.copy().setDisplayName(new StringTextComponent("kimi "))),
                Ingredient.fromStacks(manaString.copy().setDisplayName(new StringTextComponent("is ")),
                        manaString.copy().setDisplayName(new StringTextComponent("no "))),
                Ingredient.fromStacks(manaString.copy().setDisplayName(new StringTextComponent("your ")),
                        manaString.copy().setDisplayName(new StringTextComponent("na "))),
                Ingredient.fromStacks(manaString.copy().setDisplayName(new StringTextComponent("name")),
                        manaString.copy().setDisplayName(new StringTextComponent("wa"))),
                Ingredient.fromStacks(manaString.copy().setDisplayName(new StringTextComponent("?")),
                        manaString.copy().setDisplayName(new StringTextComponent("?!")),
                        manaString.copy().setDisplayName(new StringTextComponent("..?")))
            );

    @NotNull
    @Override
    public NonNullList<Ingredient> getIngredients() {
        return exampleIngredients;
    }

    @Override
    public boolean isDynamic() {
        return true;
    }
}
