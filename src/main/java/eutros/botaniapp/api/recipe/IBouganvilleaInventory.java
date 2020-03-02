package eutros.botaniapp.api.recipe;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import vazkii.botania.api.subtile.TileEntityFunctionalFlower;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public interface IBouganvilleaInventory extends IInventory {

    /**
     * Gets all the entities this Bouganvillea has in its memory, not including the header, or the trigger.
     * @see IBouganvilleaInventory#getHead()
     * @see IBouganvilleaInventory#getTrigger()
     *
     * @return List of ItemEntities the Bouganvillea has in its memory.
     */
    List<ItemEntity> getEntities();

    /**
     * Gets the head of the Bouganvillea recipe, i.e. the first item it received.
     * Will be the entity of {@link ItemStack#EMPTY} if received in {@link RecipeBouganvillea#matches(IBouganvilleaInventory, World)}
     *
     * @return The head of the Bouganvillea recipe.
     */
    ItemEntity getHead();

    /**
     * Gets the item that has just been thrown.
     *
     * @return The trigger of the Bouganvillea recipe.
     */
    @Nonnull
    ItemEntity getTrigger();

    /**
     * Gets all the entities in the Bouganvillea's memory, including the head.
     *
     * @return All entities, including the head.
     */
    default List<ItemEntity> allEntities() {
        List<ItemEntity> entities = new ArrayList<>();
        entities.add(getHead());
        entities.addAll(getEntities());
        return entities;
    }

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
}
