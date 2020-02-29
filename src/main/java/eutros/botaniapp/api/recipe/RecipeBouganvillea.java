package eutros.botaniapp.api.recipe;

import eutros.botaniapp.common.crafting.BotaniaPPRecipeTypes;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

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

    public abstract boolean checkHead(ItemEntity entity);
}
