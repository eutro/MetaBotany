package eutros.botaniapp.common.crafting;

import eutros.botaniapp.api.recipe.IBouganvilleaInventory;
import eutros.botaniapp.common.utils.Reference;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class BotaniaPPRecipeTypes {

    public static final IRecipeType<IRecipe<IBouganvilleaInventory>> BOUGANVILLEA = register("bouganvillea");

    static <T extends IRecipe<?>> IRecipeType<T> register(final String location) {
        return Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(Reference.MOD_ID, location), new IRecipeType<T>() {
            public String toString() {
                return location;
            }
        });
    }
}
