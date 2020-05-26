package eutros.metabotany.common.crafting.recipe.bouganvillea;

import eutros.metabotany.api.recipe.IBouganvilleaInventory;
import eutros.metabotany.api.recipe.RecipeBouganvillea;
import eutros.metabotany.common.block.flower.functional.SubtileBouganvillea;
import eutros.metabotany.common.item.MetaBotanyItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeBouganvilleaRename extends RecipeBouganvillea {

    protected static final ItemStack manaString = new ItemStack(MetaBotanyItems.BOTANIA_MANA_STRING);
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

    private static final Map<Item, TextFormatting> tfMap = new HashMap<>();

    static {
        tfMap.put(Items.INK_SAC, TextFormatting.BOLD);
        tfMap.put(Items.WATER_BUCKET, TextFormatting.RESET);
        tfMap.put(Items.ARROW, TextFormatting.STRIKETHROUGH);
        tfMap.put(Items.FEATHER, TextFormatting.UNDERLINE);
        tfMap.put(ForgeRegistries.ITEMS.getValue(new ResourceLocation("botania", "gaia_head")), TextFormatting.OBFUSCATED);
    }

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
        Item item = stack.getItem();
        return stack.getCount() != 1 || (item != MetaBotanyItems.BOTANIA_MANA_STRING
                && !Tags.Items.DYES.contains(item) && !tfMap.containsKey(item));
    }

    @ParametersAreNonnullByDefault
    @Override
    public boolean matches(IBouganvilleaInventory inventory, World world) {
        Item item = inventory.getThrown().getItem().getItem();
        return inventory.getSizeInventory() > 1 ||
                item == MetaBotanyItems.BOTANIA_MANA_STRING ||
                Tags.Items.DYES.contains(item) ||
                tfMap.containsKey(item);
    }

    public ItemStack getStacksResult(List<ItemStack> stacks) {
        int MAX_LENGTH = 36;

        StringBuilder builder = new StringBuilder();
        for(ItemStack stack : stacks.subList(0, stacks.size() - 1)) {
            Item item = stack.getItem();
            if(Tags.Items.DYES.contains(item)) {
                item.getTags().stream()
                        .map(Object::toString)
                        .filter(s -> s.startsWith(Tags.Items.DYES.getId() + "/"))
                        .findFirst()
                        .map(s -> s.substring(Tags.Items.DYES.getId().toString().length() + 1))
                        .map(TextFormatting::getValueByName)
                        .map(Object::toString)
                        .map(f -> f + TextFormatting.ITALIC)
                        .ifPresent(builder::append);
                continue;
            }

            if(tfMap.containsKey(item)) {
                builder.append(tfMap.get(item)).append(TextFormatting.ITALIC);
                continue;
            }

            builder.append(stackName(stack));

            if(builder.length() >= MAX_LENGTH) {
                break;
            }
        }

        String s = builder.toString();
        ItemStack thrown = stacks.get(stacks.size() - 1).copy();

        if(!s.equals("")) {
            ITextComponent component = new StringTextComponent(s);
            thrown.setDisplayName(component);
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
