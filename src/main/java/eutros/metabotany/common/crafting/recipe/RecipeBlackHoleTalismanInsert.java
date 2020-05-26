package eutros.metabotany.common.crafting.recipe;

import eutros.metabotany.common.core.helper.ItemNBTHelper;
import eutros.metabotany.common.item.MetaBotanyItems;
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
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
                if(stack.getItem() == MetaBotanyItems.BOTANIA_BLACK_HOLE_TALISMAN) {
                    if(!talisman.isEmpty())
                        return false;
                    talisman = stack;
                    pop = i;
                }
            }
        }

        Block talismanBlock = getBlock(talisman);
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
                            getBlockCount(talisman) != 0 &&
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
                if(stack.getItem() == MetaBotanyItems.BOTANIA_BLACK_HOLE_TALISMAN) {
                    talisman = stack;
                    pop = i;
                    break;
                }
            }
        }

        ItemStack newTalisman = talisman.copy();
        int count = getBlockCount(newTalisman);

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

    public static int getBlockCount(ItemStack stack) {
        return ItemNBTHelper.getInt(stack, "blockCount", 0);
    }

    @Nullable
    public static Block getBlock(ItemStack stack) {
        ResourceLocation id = ResourceLocation.tryCreate(getBlockName(stack));
        return id != null ? ForgeRegistries.BLOCKS.getValue(id) : null;
    }

    private static String getBlockName(ItemStack stack) {
        return ItemNBTHelper.getString(stack, "blockName", "");
    }

}

