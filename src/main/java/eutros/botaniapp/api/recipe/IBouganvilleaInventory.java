package eutros.botaniapp.api.recipe;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.IInventory;
import vazkii.botania.api.subtile.TileEntityFunctionalFlower;

import java.util.List;

public interface IBouganvilleaInventory extends IInventory {

    /**
     * Gets the item that was just thrown to the Bouganvillea.
     *
     * @return Item thrown to the Bouganvillea.
     */
    ItemEntity getThrown();

    /**
     * Gets all the entities in the Bouganvillea's memory, including the head.
     *
     * @return All entities, including the head.
     */
    List<ItemEntity> allEntities();

    /**
     * Returns the flower TE itself.
     *
     * @return This inventory's flower.
     */
    TileEntityFunctionalFlower getFlower();

    /**
     * Cancels the anvil sound effect.
     */
    void cancelSound();

    /**
     * Sets the Bouganvillea to not replace the last thrown item, and instead copy it.
     */
    void noReplace();

}
