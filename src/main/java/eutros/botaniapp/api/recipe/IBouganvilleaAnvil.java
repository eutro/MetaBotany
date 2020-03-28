package eutros.botaniapp.api.recipe;

import net.minecraft.item.ItemStack;

/**
 * Implement this on your item for custom breaking behaviour. The items still need to be added to the {@code botaniapp:boug_anvils} tag,
 * or they won't be recognised for the recipe.
 */
public interface IBouganvilleaAnvil {

    /**
     * Return a damaged version of this anvil.
     *
     * @return The damaged anvil. Return null to break.
     */
    ItemStack damage(ItemStack anvil);

}
