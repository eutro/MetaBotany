package eutros.botaniapp.api.recipe;

import eutros.botaniapp.common.crafting.BotaniaPPRecipeTypes;
import eutros.botaniapp.common.utils.MathUtils;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This is the default recipe of the Bouganvillea.
 */
public abstract class RecipeBouganvillea implements IRecipe<IBouganvilleaInventory> {

    private final ResourceLocation id;
    private final ItemStack output;
    private final Ingredient header;
    private final String group;

    public RecipeBouganvillea(ResourceLocation id, ItemStack output, Ingredient header, @Nullable String group) {
        this.id = id;
        this.output = output;
        this.header = header;
        this.group = group == null ? "" : group;
    }

    abstract public boolean shouldTrigger(IBouganvilleaInventory inventory);

    @Override
    public boolean matches(IBouganvilleaInventory inventory, @NotNull World world) {
        return inventory.getHead().map(t -> header.test(t.getItem())).get();
    }

    @Override
    public boolean canFit(int w, int h) {
        return false;
    }

    @NotNull
    @Override
    public ItemStack getRecipeOutput() {
        return output;
    }

    @NotNull
    @Override
    public ResourceLocation getId() {
        return id;
    }

    @NotNull
    @Override
    public String getGroup() {
        return group;
    }

    @NotNull
    @Override
    public IRecipeType<?> getType() {
        return BotaniaPPRecipeTypes.BOUGANVILLEA;
    }

    public ItemStack getStacksResult(List<ItemStack> stacks) {
        return output;
    }

    public abstract boolean checkHead(ItemEntity entity);

    public List<ItemStack> getDynamicOutput(List<List<ItemStack>> ingredients) {
        if(!isDynamic())
            return Collections.singletonList(getRecipeOutput());
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
