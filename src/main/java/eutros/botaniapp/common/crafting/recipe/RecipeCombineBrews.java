package eutros.botaniapp.common.crafting.recipe;

import eutros.botaniapp.common.utils.InventoryCollectionWrapper;
import eutros.botaniapp.common.utils.MathUtils;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import vazkii.botania.api.brew.Brew;
import vazkii.botania.api.brew.IBrewItem;
import vazkii.botania.common.item.brew.ItemBrewBase;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO resolve internal references perhaps

public class RecipeCombineBrews extends SpecialRecipe {

    public static final IRecipeSerializer<RecipeCombineBrews> SERIALIZER = new SpecialRecipeSerializer<>(RecipeCombineBrews::new);

    public RecipeCombineBrews(ResourceLocation location) {
        super(location);
    }

    @ParametersAreNonnullByDefault
    @Override
    public boolean matches(CraftingInventory inventory, World world) {
        Brew brew = null;

        int count = 0;
        for(ItemStack s : new InventoryCollectionWrapper(inventory)) {
            if(s.isEmpty())
                continue;

            Item item = s.getItem();
            if(!(item instanceof ItemBrewBase))
                return false;

            Brew thisBrew = ((IBrewItem) item).getBrew(s);
            if(brew == null)
                brew = thisBrew;
            else if(thisBrew != brew)
                return false;

            count++;
        }

        return count > 1;
    }

    @Nonnull
    @ParametersAreNonnullByDefault
    @Override
    public ItemStack getCraftingResult(CraftingInventory inventory) {
        Supplier<Stream<ItemStack>> stream = () -> new InventoryCollectionWrapper(inventory).stream()
                .filter(s -> !s.isEmpty());
        List<ItemStack> list = stream.get().collect(Collectors.toList());

        ItemBrewBase firstItem = (ItemBrewBase) list.get(0).getItem();
        int maxSwigs = firstItem.getSwigsLeft(ItemStack.EMPTY);
        int swigSum = stream.get().map(s -> ((ItemBrewBase) s.getItem()).getSwigsLeft(s)).reduce(Integer::sum).orElse(0);

        ItemStack stack = list.get(0).copy();
        firstItem.setSwigsLeft(stack, Math.min(swigSum, maxSwigs));

        return stack;
    }

    @Nonnull
    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInventory inventory) {
        NonNullList<ItemStack> stacks = NonNullList.create();
        stacks.addAll(new InventoryCollectionWrapper(inventory));

        int swigSum = stacks.stream().filter(s -> !s.isEmpty())
                .map(s -> ((ItemBrewBase) s.getItem()).getSwigsLeft(s))
                .reduce(Integer::sum).orElse(0);

        boolean first = true;
        for(int i = 0; i < stacks.size(); i++) {
            ItemStack s = stacks.get(i);
            if(s.isEmpty())
                continue;

            s = s.copy();
            ItemBrewBase item = (ItemBrewBase) s.getItem();
            int max = item.getSwigsLeft(ItemStack.EMPTY);
            int swigs = MathUtils.clamp(swigSum, 0, max);
            if(swigs != 0)
                item.setSwigsLeft(s, swigs);
            else {
                Supplier<Item> baseItem = ObfuscationReflectionHelper.getPrivateValue(ItemBrewBase.class, item, "baseItem");
                s = baseItem == null ? ItemStack.EMPTY : new ItemStack(baseItem.get());
            }
            swigSum -= swigs;

            if(first) {
                s = ItemStack.EMPTY;
                first = false;
            }

            stacks.set(i, s);
        }

        return stacks;
    }

    @Override
    public boolean canFit(int w, int h) {
        return w * h > 1;
    }

    @Nonnull
    @Override
    public IRecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

}
