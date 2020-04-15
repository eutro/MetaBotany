package eutros.botaniapp.common.crafting.recipe;

import eutros.botaniapp.common.core.helper.ItemNBTHelper;
import eutros.botaniapp.common.item.BotaniaPPItems;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import vazkii.botania.common.item.ItemBlackHoleTalisman;

import javax.annotation.Nonnull;

public class RecipeBlackHoleTalismanInsert extends SpecialRecipe {

    public static final IRecipeSerializer<RecipeBlackHoleTalismanInsert> SERIALIZER = new SpecialRecipeSerializer<>(RecipeBlackHoleTalismanInsert::new);

    public RecipeBlackHoleTalismanInsert(ResourceLocation id) {
        super(id);
    }

    @Override
    public boolean matches(@Nonnull CraftingInventory inv, @Nonnull World world) {
        ItemStack talisman = ItemStack.EMPTY;
        int pop = 0;

        for(int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if(!stack.isEmpty()) {
                if(stack.getItem() == BotaniaPPItems.BOTANIA_BLACK_HOLE_TALISMAN) {
                    if(!talisman.isEmpty())
                        return false;
                    talisman = stack;
                    pop = i;
                }
            }
        }

        Block talismanBlock = ItemBlackHoleTalisman.getBlock(talisman);
        boolean otherItem = false;

        for(int i = 0; i < inv.getSizeInventory(); i++) {
            if(i == pop)
                continue;
            ItemStack stack = inv.getStackInSlot(i);
            if(!stack.isEmpty()) {
                Item item = stack.getItem();
                if(!(item instanceof BlockItem)) {
                    return false;
                } else {
                    Block block = ((BlockItem) item).getBlock();
                    if(talismanBlock != Blocks.AIR &&
                            ItemBlackHoleTalisman.getBlockCount(talisman) != 0 &&
                            (block != talismanBlock)) {
                        return false;
                    }
                    talismanBlock = block;
                }
                otherItem = true;
            }
        }

        return otherItem;
    }

    @Nonnull
    @Override
    public ItemStack getCraftingResult(@Nonnull CraftingInventory inv) {
        ItemStack talisman = ItemStack.EMPTY;
        int pop = 0;
        String name = "";

        for(int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if(!stack.isEmpty()) {
                if(stack.getItem() == BotaniaPPItems.BOTANIA_BLACK_HOLE_TALISMAN) {
                    talisman = stack;
                    pop = i;
                    break;
                }
            }
        }

        ItemStack newTalisman = talisman.copy();
        int count = ItemBlackHoleTalisman.getBlockCount(newTalisman);

        for(int i = 0; i < inv.getSizeInventory(); i++) {
            if(i == pop)
                continue;
            ItemStack stack = inv.getStackInSlot(i);
            if(!stack.isEmpty()) {
                count += stack.getCount();
                ResourceLocation id = stack.getItem().getRegistryName();
                if(id != null)
                    name = id.toString();
            }
        }

        ItemNBTHelper.setInt(newTalisman, "blockCount", count);
        ItemNBTHelper.setString(newTalisman, "blockName", name);

        return newTalisman;
    }

    @NotNull
    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInventory inv) {
        inv.clear();
        return NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height > 1;
    }

    @Nonnull
    @Override
    public IRecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

}

