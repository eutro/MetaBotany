package eutros.botaniapp.common.crafting.recipe;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import vazkii.botania.api.mana.ICompositableLens;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Function;

public class RecipeLensSplit extends SpecialRecipe {

    public static final IRecipeSerializer<RecipeLensSplit> SERIALIZER = new SpecialRecipeSerializer<>(RecipeLensSplit::new);

    public RecipeLensSplit(ResourceLocation location) {
        super(location);
    }

    @ParametersAreNonnullByDefault
    @Override
    public boolean matches(CraftingInventory inventory, World world) {
        ItemStack stack = ItemStack.EMPTY;

        for(int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack slotStack = inventory.getStackInSlot(i);
            if(!slotStack.isEmpty()) {
                Item item = slotStack.getItem();
                if(stack.isEmpty() && item instanceof ICompositableLens && !((ICompositableLens) item).getCompositeLens(slotStack).isEmpty()) {
                    stack = slotStack;
                } else {
                    return false;
                }
            }
        }

        return !stack.isEmpty();
    }

    @Nonnull
    @Override
    public ItemStack getCraftingResult(CraftingInventory inventory) {

        for(int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack slotStack = inventory.getStackInSlot(i);
            if(!slotStack.isEmpty()) {
                Item item = slotStack.getItem();
                return ((ICompositableLens) item).getCompositeLens(slotStack);
            }
        }

        return ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInventory inventory) {
        NonNullList<ItemStack> stacks = NonNullList.withSize(inventory.getSizeInventory(), ItemStack.EMPTY);

        for(int i = 0; i < stacks.size(); i++) {
            ItemStack slotStack = inventory.getStackInSlot(i);
            if(!slotStack.isEmpty()) {
                Item item = slotStack.getItem();
                stacks.set(i, clearComposite(slotStack, (ICompositableLens) item));
            }
        }

        inventory.clear();

        return stacks;
    }

    private ItemStack clearComposite(ItemStack stack, ICompositableLens item) {
        if(!stack.hasTag())
            return ItemStack.EMPTY;

        CompoundNBT cmp = item.getCompositeLens(stack).write(new CompoundNBT());

        Function<CompoundNBT, Boolean> f = new Function<CompoundNBT, Boolean>() {
            @Override
            public Boolean apply(CompoundNBT compound) {
                for(String s : compound.keySet()) {
                    INBT nbt = compound.get(s);
                    if(nbt instanceof CompoundNBT) {
                        if(nbt.equals(cmp)) {
                            compound.remove(s);
                            return true;
                        } else {
                            if(apply(cmp))
                                return true;
                        }
                    }
                }
                return false;
            }
        };

        f.apply(stack.getTag());

        return stack;
    }

    @Override
    public boolean canFit(int w, int h) {
        return w * h > 0;
    }

    @Nonnull
    @Override
    public IRecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

}
