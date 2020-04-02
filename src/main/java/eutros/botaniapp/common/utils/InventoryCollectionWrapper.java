package eutros.botaniapp.common.utils;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractCollection;
import java.util.Iterator;

public class InventoryCollectionWrapper extends AbstractCollection<ItemStack> {

    private IInventory inventory;

    public InventoryCollectionWrapper(IInventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public int size() {
        return inventory.getSizeInventory();
    }

    @Override
    public boolean isEmpty() {
        return inventory.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        if(!(o instanceof ItemStack))
            return false;

        for(ItemStack stack : this) {
            if(stack.equals((ItemStack) o, true))
                return true;
        }
        return false;
    }

    @NotNull
    @Override
    public Iterator<ItemStack> iterator() {
        return new InventoryIterator();
    }

    @Override
    public void clear() {
        inventory.clear();
    }

    private class InventoryIterator implements Iterator<ItemStack> {

        int index = 0;

        @Override
        public boolean hasNext() {
            return inventory.getSizeInventory() > index;
        }

        @Override
        public ItemStack next() {
            return inventory.getStackInSlot(index++);
        }

    }

}
