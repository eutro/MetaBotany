package eutros.metabotany.api.recipe;

import eutros.metabotany.common.crafting.MetaBotanyRecipeTypes;
import eutros.metabotany.common.utils.MathUtils;
import net.minecraft.entity.item.ItemEntity;
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
 * This is the base recipe for the Bouganvillea. If your recipe does not extend this, it will not be usable by the Bouganvillea.
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
     * <p>
     * {@link RecipeBouganvillea#matches(IBouganvilleaInventory, World)} has already been called, and returned true.
     *
     * @param inventory The inventory the Bouganvillea currently has.
     * @return true if the recipe is finished, false if the recipe should go on.
     */
    abstract public boolean shouldTrigger(IBouganvilleaInventory inventory);

    /**
     * Gets whether the recipe is still valid. This should probably only be checking the last entry of {@link IBouganvilleaInventory#allEntities()},
     * as it returned true without it.
     * <p>
     * This gets called each time a new item is given to the Bouganvillea, and should return true
     * if a completed craft is possible from this inventory.
     *
     * @param inventory The inventory the Bouganvillea currently has.
     * @param world     The current world.
     * @return Whether this recipe should be initialized with the given inventory. This is not where you should check
     * if the recipe should actually craft, as the inventory is not completely filled.
     */
    @Override
    public abstract boolean matches(@NotNull IBouganvilleaInventory inventory, @NotNull World world);

    /**
     * Gets the recipe result from a certain list of stacks. This is used for JEI.
     * Doesn't need to be overriden unless {@link RecipeBouganvillea#isDynamic()} is true, which will trigger dynamic rendering in JEI.
     *
     * @param stacks The stacks this recipe received.
     * @return A stack that the recipe would output.
     */
    public ItemStack getStacksResult(List<ItemStack> stacks) {
        return output;
    }

    /**
     * Gets the dynamic output of a recipe.
     * By default, this uses {@link RecipeBouganvillea#getStacksResult(List)} to compute a full list of the outputs
     * it would give, keeping in sync with all the ingredients.
     *
     * @param ingredients A list of all the ingredients, where each ingredient is a list of the possible stacks.
     * @return A list of all the outputs with these ingredients.
     */
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

    @NotNull
    @Override
    public ItemStack getCraftingResult(IBouganvilleaInventory inventory) {
        return getStacksResult(inventory.allEntities().stream().map(ItemEntity::getItem).collect(Collectors.toList()));
    }

    @Override
    public final boolean canFit(int w, int h) {
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
        return MetaBotanyRecipeTypes.BOUGANVILLEA_TYPE.type;
    }

}
