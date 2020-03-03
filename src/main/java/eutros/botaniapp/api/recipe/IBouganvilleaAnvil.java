package eutros.botaniapp.api.recipe;

import net.minecraft.item.ItemStack;

/**
 * This supplements the botaniapp:boug_anvils tag. Implement this on your item for custom breaking behaviour.
 */
public interface IBouganvilleaAnvil {

    /**
     * Return a damaged version of this anvil.
     *
     * @return The damaged anvil. Return null to break.
     */
    ItemStack damage(ItemStack anvil);
}
