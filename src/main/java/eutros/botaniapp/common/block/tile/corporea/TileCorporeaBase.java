/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 *
 * File Created @ [Feb 15, 2015, 12:25:19 AM (GMT)]
 */
package eutros.botaniapp.common.block.tile.corporea;

import eutros.botaniapp.common.block.tile.TileSimpleInventory;
import net.minecraft.tileentity.TileEntityType;
import vazkii.botania.api.corporea.CorporeaHelper;
import vazkii.botania.api.corporea.ICorporeaSpark;

import java.util.Objects;

public abstract class TileCorporeaBase extends TileSimpleInventory {
    public TileCorporeaBase(TileEntityType<?> type) {
        super(type);
    }

    // These TE's only extend TileSimpleInventory to give sparks an inventory to attach to
    @Override
    public final int getSizeInventory() {
        return 0;
    }

    public ICorporeaSpark getSpark() {
        return CorporeaHelper.getSparkForBlock(Objects.requireNonNull(world), getPos());
    }

}
