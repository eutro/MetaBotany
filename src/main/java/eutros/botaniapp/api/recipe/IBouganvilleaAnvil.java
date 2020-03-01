package eutros.botaniapp.api.recipe;

import net.minecraft.block.Block;
import org.jetbrains.annotations.Nullable;

/**
 * Any classes that implement this will be usable in the Bouganvillea anvil recipe, though they will not show in JEI.
 */
public interface IBouganvilleaAnvil {

    /**
     * Return a damaged version of this anvil.
     *
     * @return The damaged anvil. Return null to break.
     */
    @Nullable
    Block damage(Block block);
}
