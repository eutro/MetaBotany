package eutros.botaniapp.api.recipe;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.IInventory;
import vazkii.botania.api.subtile.TileEntityFunctionalFlower;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
     * This will be empty if no other registered recipe is found, i.e. if the Bouganvillea is renaming.
     *
     * @return The head of the Bouganvillea recipe.
     */
    Optional<ItemEntity> getHead();

    /**
     * Gets the item that has just been thrown. This is the item that triggered the craft, if it is currently taking place.
     * If the craft is underway, this returned true for {@link RecipeBouganvillea#shouldTrigger(IBouganvilleaInventory)}.
     *
     * @return The trigger of the Bouganvillea recipe.
     */
    @Nonnull
    ItemEntity getTrigger();

    /**
     * Gets all the entities in the Bouganvillea's memory, including the head.
     *
     * @return All entities, including the head, if it is present.
     */
    default List<ItemEntity> allEntities() {
        ArrayList<ItemEntity> entities = new ArrayList<>();
        getHead().ifPresent(entities::add);
        entities.addAll(getEntities());
        return entities;
    }

    /**
     * Returns the flower TE itself.
     *
     * @return This inventory's flower.
     */
    TileEntityFunctionalFlower getFlower();
}
