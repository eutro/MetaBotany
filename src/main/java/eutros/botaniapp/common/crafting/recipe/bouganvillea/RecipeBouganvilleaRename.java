package eutros.botaniapp.common.crafting.recipe.bouganvillea;

import eutros.botaniapp.api.recipe.IBouganvilleaInventory;
import eutros.botaniapp.api.recipe.RecipeBouganvillea;
import eutros.botaniapp.common.block.flower.functional.SubtileBouganvillea;
import eutros.botaniapp.common.item.BotaniaPPItems;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class RecipeBouganvilleaRename extends RecipeBouganvillea {

    protected static final ItemStack manaString = new ItemStack(BotaniaPPItems.BOTANIA_MANA_STRING);
    protected static final Ingredient RENAMED_INGREDIENT = Ingredient.fromItems(Items.DIRT, Items.NAME_TAG, Items.PAPER, Items.DIAMOND_SWORD);
    public static IRecipeSerializer<RecipeBouganvilleaRename> SERIALIZER = new SpecialRecipeSerializer<>(RecipeBouganvilleaRename::new);
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
                    manaString.copy().setDisplayName(new StringTextComponent("..?"))),
            RENAMED_INGREDIENT
    );

    public RecipeBouganvilleaRename(ResourceLocation location) {
        this(location,
                manaString.copy().setDisplayName(new StringTextComponent("What is your name?")),
                SubtileBouganvillea.BUILTIN_GROUP);
    }

    public RecipeBouganvilleaRename(ResourceLocation location, ItemStack output, String group) {
        super(location, output, group);
    }

    @Override
    public boolean shouldTrigger(IBouganvilleaInventory inv) {
        ItemStack stack = inv.getThrown().getItem();
        return stack.getCount() != 1 || stack.getItem() != BotaniaPPItems.BOTANIA_MANA_STRING;
    }

    @ParametersAreNonnullByDefault
    @Override
    public boolean matches(IBouganvilleaInventory inventory, World world) {
        return inventory.getSizeInventory() > 1 ||
                inventory.getThrown().getItem().getItem() == BotaniaPPItems.BOTANIA_MANA_STRING;
    }

    public ItemStack getStacksResult(List<ItemStack> stacks) {
        // TODO add limitations
        StringBuilder builder = new StringBuilder();
        for(ItemStack stack : stacks.subList(0, stacks.size() - 1)) {
            builder.append(stackName(stack));
        }

        String s = builder.toString();
        ItemStack thrown = stacks.get(stacks.size() - 1).copy();
        if(!s.equals("")) {
            thrown.setDisplayName(new StringTextComponent(s));
        }

        return thrown;
    }

    @NotNull
    @Override
    public IRecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @NotNull
    @Override
    public NonNullList<Ingredient> getIngredients() {
        return exampleIngredients;
    }

    @Override
    public boolean isDynamic() {
        return true;
    }

    protected String stackName(ItemStack stack) {
        return stack.getDisplayName().getFormattedText();
    }

    protected String stackNameOrEmpty(ItemStack stack) {
        return stack.hasDisplayName() ? stackName(stack) : "";
    }

}
