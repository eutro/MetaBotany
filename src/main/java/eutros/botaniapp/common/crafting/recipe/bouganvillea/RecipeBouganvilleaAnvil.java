package eutros.botaniapp.common.crafting.recipe.bouganvillea;

import eutros.botaniapp.api.recipe.IBouganvilleaAnvil;
import eutros.botaniapp.api.recipe.IBouganvilleaInventory;
import eutros.botaniapp.api.recipe.RecipeBouganvillea;
import eutros.botaniapp.common.block.flower.functional.SubtileBouganvillea;
import eutros.botaniapp.common.utils.BotaniaPPFakePlayer;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;

public class RecipeBouganvilleaAnvil extends RecipeBouganvillea {

    private IRecipeSerializer<RecipeBouganvilleaRename> SERIALIZER = new SpecialRecipeSerializer<>(RecipeBouganvilleaRename::new);

    public RecipeBouganvilleaAnvil(ResourceLocation id) {
        super(id, null, Ingredient.fromItems(Blocks.ANVIL), SubtileBouganvillea.FALLBACK_GROUP);
    }

    @Override
    public boolean shouldTrigger(IBouganvilleaInventory inventory) {
        return inventory.getSizeInventory() > 1;
    }

    @Override
    public boolean checkHead(ItemEntity entity) {
        Block block = ((BlockItem) entity.getItem().getItem()).getBlock();
        return block instanceof IBouganvilleaAnvil || block instanceof AnvilBlock;
    }

    @NotNull
    @Override
    public ItemStack getCraftingResult(IBouganvilleaInventory inventory) {
        Supplier<ItemStack> defaultRet = () -> inventory.getTrigger().getItem();

        // start RepairContainer copypasta

        ItemStack itemstack = inventory.getStackInSlot(1);
        int i = 0;
        int j;
        ItemStack result = itemstack.copy();
        ItemStack combineWith = inventory.getTrigger().getItem();
        Map<Enchantment, Integer> enchantMap = EnchantmentHelper.getEnchantments(result);
        j = itemstack.getRepairCost() + (combineWith.isEmpty() ? 0 : combineWith.getRepairCost());
        int materialCost = 0;
        boolean flag = false;

        if (!combineWith.isEmpty()) {
            flag = combineWith.getItem() == Items.ENCHANTED_BOOK && !EnchantedBookItem.getEnchantments(combineWith).isEmpty();
            if (result.isDamageable() && result.getItem().getIsRepairable(itemstack, combineWith)) {
                int l2 = Math.min(result.getDamage(), result.getMaxDamage() / 4);
                if (l2 <= 0) {
                    return defaultRet.get();
                }

                int i3;
                for(i3 = 0; l2 > 0 && i3 < combineWith.getCount(); ++i3) {
                    int j3 = result.getDamage() - l2;
                    result.setDamage(j3);
                    ++i;
                    l2 = Math.min(result.getDamage(), result.getMaxDamage() / 4);
                }

                materialCost = i3;
            } else {
                if (!flag && (result.getItem() != combineWith.getItem() || !result.isDamageable())) {
                    return defaultRet.get();
                }

                if (result.isDamageable() && !flag) {
                    int l = itemstack.getMaxDamage() - itemstack.getDamage();
                    int i1 = combineWith.getMaxDamage() - combineWith.getDamage();
                    int j1 = i1 + result.getMaxDamage() * 12 / 100;
                    int k1 = l + j1;
                    int l1 = result.getMaxDamage() - k1;
                    if (l1 < 0) {
                        l1 = 0;
                    }

                    if (l1 < result.getDamage()) {
                        result.setDamage(l1);
                        i += 2;
                    }
                }

                Map<Enchantment, Integer> map1 = EnchantmentHelper.getEnchantments(combineWith);
                boolean flag2 = false;
                boolean flag3 = false;

                for(Enchantment enchantment1 : map1.keySet()) {
                    if (enchantment1 != null) {
                        int i2 = enchantMap.getOrDefault(enchantment1, 0);
                        int j2 = map1.get(enchantment1);
                        j2 = i2 == j2 ? j2 + 1 : Math.max(j2, i2);
                        boolean flag1 = enchantment1.canApply(itemstack) || itemstack.getItem() == Items.ENCHANTED_BOOK;

                        for(Enchantment enchantment : enchantMap.keySet()) {
                            if (enchantment != enchantment1 && !enchantment1.isCompatibleWith(enchantment)) {
                                flag1 = false;
                                ++i;
                            }
                        }

                        if (!flag1) {
                            flag3 = true;
                        } else {
                            flag2 = true;
                            if (j2 > enchantment1.getMaxLevel()) {
                                j2 = enchantment1.getMaxLevel();
                            }

                            enchantMap.put(enchantment1, j2);
                            int k3 = 0;
                            switch(enchantment1.getRarity()) {
                                case COMMON:
                                    k3 = 1;
                                    break;
                                case UNCOMMON:
                                    k3 = 2;
                                    break;
                                case RARE:
                                    k3 = 4;
                                    break;
                                case VERY_RARE:
                                    k3 = 8;
                            }

                            if (flag) {
                                k3 = Math.max(1, k3 / 2);
                            }

                            i += k3 * j2;
                            if (itemstack.getCount() > 1) {
                                i = 40;
                            }
                        }
                    }
                }

                if (flag3 && !flag2) {
                    return defaultRet.get();
                }
            }
        }

        if (flag && !result.isBookEnchantable(combineWith)) result = ItemStack.EMPTY;

        int maximumCost = j + i;
        if (i <= 0) {
            result = ItemStack.EMPTY;
        }

        if (maximumCost >= 40) {
            result = ItemStack.EMPTY;
        }

        if (!result.isEmpty()) {
            int k2 = result.getRepairCost();
            if (!combineWith.isEmpty() && k2 < combineWith.getRepairCost()) {
                k2 = combineWith.getRepairCost();
            }

            k2 = k2 * 2 + 1;

            result.setRepairCost(k2);
            EnchantmentHelper.setEnchantments(enchantMap, result);
        } else
            return defaultRet.get();

        // end copypasta

        if(materialCost > 0)
            combineWith.shrink(materialCost);
        else
            combineWith = ItemStack.EMPTY;

        inventory.setInventorySlotContents(1, combineWith);

        float breakChance = ForgeHooks.onAnvilRepair(new BotaniaPPFakePlayer((ServerWorld) inventory.getFlower().getWorld()),
                result,
                inventory.getStackInSlot(0),
                inventory.getTrigger().getItem());

        if(new Random(inventory.getFlower().getPos().hashCode()).nextFloat() < breakChance) {
            Block anvilBlock = ((BlockItem) inventory.getStackInSlot(0).getItem()).getBlock();

            anvilBlock = damage(anvilBlock);

            if (anvilBlock == null)
                inventory.removeStackFromSlot(0);
            else
                inventory.setInventorySlotContents(0, new ItemStack(anvilBlock));
        }


        return result;
    }

    @Nullable
    private Block damage(Block block) {
        if(block instanceof IBouganvilleaAnvil) {
            return ((IBouganvilleaAnvil) block).damage(block);
        }
        if (block == Blocks.ANVIL) {
            return Blocks.CHIPPED_ANVIL;
        } else {
            return block == Blocks.CHIPPED_ANVIL ? Blocks.DAMAGED_ANVIL : null;
        }
    }

    @NotNull
    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.from(Ingredient.EMPTY, Ingredient.EMPTY);
    }

    @NotNull
    @Override
    public IRecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }
}
