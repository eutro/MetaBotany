/**
 * This class was created by <Mikeemoo/boq/nevercast>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 *
 * File Created @ [? (GMT)]
 */
package eutros.botaniapp.common.core.helper;

import eutros.botaniapp.common.block.tile.TileSimpleInventory;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import vazkii.botania.api.corporea.InvWithLocation;

public class InventoryHelper {

    public static InvWithLocation getInventoryWithLocation(World world, BlockPos pos, Direction side) {
        IItemHandler ret = getInventory(world, pos, side);
        if(ret == null)
            return null;
        else return new InvWithLocation(ret, world, pos);
    }

    public static IItemHandler getInventory(World world, BlockPos pos, Direction side) {
        TileEntity te = world.getTileEntity(pos);

        if(te == null)
            return null;

        LazyOptional<IItemHandler> ret = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
        if (!ret.isPresent())
            ret = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        return ret.orElse(null);
    }

    public static void dropInventory(TileSimpleInventory inv, World world, BlockState state, BlockPos pos) {
        if(inv != null) {
            for(int j1 = 0; j1 < inv.getSizeInventory(); ++j1) {
                ItemStack itemstack = inv.getItemHandler().getStackInSlot(j1);

                if(!itemstack.isEmpty()) {
                    net.minecraft.inventory.InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), itemstack);
                }
            }

            world.updateComparatorOutputLevel(pos, state.getBlock());
        }
    }

    public static void withdrawFromInventory(TileSimpleInventory inv, PlayerEntity player) {
        for(int i = inv.getSizeInventory() - 1; i >= 0; i--) {
            ItemStack stackAt = inv.getItemHandler().getStackInSlot(i);
            if(!stackAt.isEmpty()) {
                ItemStack copy = stackAt.copy();
                ItemHandlerHelper.giveItemToPlayer(player, copy);
                inv.getItemHandler().setStackInSlot(i, ItemStack.EMPTY);
                player.world.updateComparatorOutputLevel(inv.getPos(), null);
                break;
            }
        }
    }

}
