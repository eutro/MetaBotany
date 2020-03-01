package eutros.botaniapp.common.crafting.recipe.bouganvillea;

import eutros.botaniapp.api.recipe.IBouganvilleaInventory;
import eutros.botaniapp.api.recipe.RecipeBouganvillea;
import eutros.botaniapp.common.block.flower.functional.SubtileBouganvillea;
import eutros.botaniapp.common.item.BotaniaPPItems;
import eutros.botaniapp.common.utils.MathUtils;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RecipeBouganvilleaRename extends RecipeBouganvillea {

    private IRecipeSerializer<RecipeBouganvilleaRename> SERIALIZER = new SpecialRecipeSerializer<>(RecipeBouganvilleaRename::new);

    private static final ItemStack manaString = new ItemStack(BotaniaPPItems.BOTANIA_MANA_STRING);

    public RecipeBouganvilleaRename(ResourceLocation location) {
        super(location,
                manaString.copy().setDisplayName(new StringTextComponent("What is your name?")),
                null,
                SubtileBouganvillea.FALLBACK_GROUP);
    }

    @Override
    public boolean shouldTrigger(IBouganvilleaInventory inv) {
        ItemStack stack = inv.getTrigger().getItem();
        return stack.getCount() != 1 || stack.getItem() != BotaniaPPItems.BOTANIA_MANA_STRING;
    }

    @Override
    public boolean checkHead(ItemEntity entity) {
        return entity.getItem().getItem() == BotaniaPPItems.BOTANIA_MANA_STRING;
    }

    @NotNull
    @Override
    public ItemStack getCraftingResult(IBouganvilleaInventory inventory) {
        TextComponent tc = new StringTextComponent("");
        for(ItemEntity entity : inventory.allEntities()) {
            tc.appendSibling(entity.getItem().getDisplayName());
        }

        if(!tc.equals(new StringTextComponent("")))
            inventory.getTrigger().getItem().setDisplayName(tc);

        return inventory.getTrigger().getItem();
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
    public List<ItemStack> getDynamicOutput(List<List<ItemStack>> ingredients) {
        // lovely
        return IntStream.range(0, ingredients.stream().map(List::size)
                                                      .reduce(1, MathUtils::lcm)) // Create a stream 0 to the LCM of all list lengths.
                    .boxed().map(i -> //                          Then, from 0 to that LCM,
                        getStacksResult(ingredients.stream() // get the ItemStack returned
                                .map(s -> s.get(i % s.size())) // for all the combinations that will be shown.

                                .collect(Collectors.toList())) // This collects the set of ingredients for a single craft.
                )
                .collect(Collectors.toList()); // This collects all the results.
    }
}
