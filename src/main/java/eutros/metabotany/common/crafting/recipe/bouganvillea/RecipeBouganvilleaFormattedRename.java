package eutros.metabotany.common.crafting.recipe.bouganvillea;

import eutros.metabotany.api.recipe.IBouganvilleaInventory;
import eutros.metabotany.common.item.MetaBotanyItems;
import net.minecraft.item.ItemStack;
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
import java.util.regex.Pattern;

public class RecipeBouganvilleaFormattedRename extends RecipeBouganvilleaRename {

    private static final Pattern formatPattern = Pattern.compile("\\{}");
    public static IRecipeSerializer<RecipeBouganvilleaFormattedRename> SERIALIZER = new SpecialRecipeSerializer<>(RecipeBouganvilleaFormattedRename::new);
    private final ItemStack redString = new ItemStack(MetaBotanyItems.BOTANIA_RED_STRING);
    private final NonNullList<Ingredient> exampleIngredients = NonNullList.from(
            Ingredient.EMPTY,
            Ingredient.fromStacks(redString.copy().setDisplayName(new StringTextComponent("{} is {}.")),
                    redString.copy().setDisplayName(new StringTextComponent("{} is not {}.")),
                    redString.copy().setDisplayName(new StringTextComponent("This is {1}."))),
            Ingredient.fromStacks(manaString.copy().setDisplayName(new StringTextComponent("The Cake")),
                    manaString.copy().setDisplayName(new StringTextComponent("The world")),
                    manaString.copy().setDisplayName(new StringTextComponent("Your mind")),
                    manaString.copy().setDisplayName(new StringTextComponent("That"))),
            Ingredient.fromStacks(manaString.copy().setDisplayName(new StringTextComponent("a lie")),
                    manaString.copy().setDisplayName(new StringTextComponent("real"))),
            RENAMED_INGREDIENT
    );

    public RecipeBouganvilleaFormattedRename(ResourceLocation location) {
        super(location);
    }

    @NotNull
    @Override
    public IRecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public boolean shouldTrigger(IBouganvilleaInventory inv) {
        ItemStack stack = inv.getThrown().getItem();
        return stack.getCount() != 1 ||
                (stack.getItem() != MetaBotanyItems.BOTANIA_RED_STRING &&
                        stack.getItem() != MetaBotanyItems.BOTANIA_MANA_STRING);
    }

    @ParametersAreNonnullByDefault
    @Override
    public boolean matches(IBouganvilleaInventory inventory, World world) {
        return inventory.getSizeInventory() > 1 ||
                inventory.getThrown().getItem().getItem() == MetaBotanyItems.BOTANIA_RED_STRING;
    }

    @Override
    public ItemStack getStacksResult(List<ItemStack> stacks) {
        String format = stackNameOrEmpty(stacks.get(0));

        boolean indexedFormatting = !formatPattern.matcher(format).find();

        List<ItemStack> strings = stacks.subList(1, stacks.size() - 1);
        for(int i = 0; i < strings.size(); i++) {
            ItemStack stack = strings.get(i);
            format = replaceNext(format, stackNameOrEmpty(stack), indexedFormatting ? i : -1);
        }

        ItemStack thrown = stacks.get(stacks.size() - 1).copy();
        if(format.equals("")) {
            thrown.clearCustomName();
        } else {
            thrown.setDisplayName(new StringTextComponent(format));
        }

        return thrown;
    }

    private String replaceNext(String format, String replacement, int index) {
        if(index < 0) {
            return formatPattern.matcher(format).replaceFirst(replacement);
        } else {
            return format.replace("{" + index + "}", replacement);
        }
    }

    @NotNull
    @Override
    public NonNullList<Ingredient> getIngredients() {
        return exampleIngredients;
    }

}
