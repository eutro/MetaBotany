package eutros.botaniapp.api.recipe;

import eutros.botaniapp.common.crafting.BotaniaPPRecipeTypes;
import eutros.botaniapp.common.utils.MathUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
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
    private final String group;

    public RecipeBouganvillea(ResourceLocation id, ItemStack output, @Nullable String group) {
        this.id = id;
        this.output = output;
        this.group = group == null ? "" : group;
    }

    /**
     * Get whether the recipe should finish crafting.
     * If this returns true, {@link RecipeBouganvillea#getRecipeOutput()} will be run, where the recipe can be carried out.
     *
     * @param inventory The inventory the Bouganvillea currently has.
     * @return true if the recipe should be cancelled or finished, false if the recipe should go on.
     */
    abstract public boolean shouldTrigger(IBouganvilleaInventory inventory);

    /**
     * Gets whether the recipe should be initialized. Only {@link IBouganvilleaInventory#getTrigger()} will be set,
     * which should be used to check for validity.
     *
     * @param inventory The inventory the Bouganvillea currently has.
     * @param world The current world.
     * @return Whether this recipe should be initialized with the given inventory. This is not where you should check
     * if the recipe should actually craft, as the inventory is not completely filled.
     */
    @Override
    public abstract boolean matches(@NotNull IBouganvilleaInventory inventory, @NotNull World world);

    @Override
    public final boolean canFit(int w, int h) {
        return false;
    }

    /**
     * Used to define what order recipes should be resolved in.
     *
     * @return The priority of this recipe. Higher values take precedence.
     */
    public int getPriority() {
        return 0;
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
        return BotaniaPPRecipeTypes.BOUGANVILLEA_TYPE.type;
    }

    public ItemStack getStacksResult(List<ItemStack> stacks) {
        return output;
    }

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
