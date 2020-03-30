package eutros.botaniapp.common.crafting.recipe.bouganvillea;

import eutros.botaniapp.api.recipe.IBouganvilleaAnvil;
import eutros.botaniapp.api.recipe.IBouganvilleaInventory;
import eutros.botaniapp.api.recipe.RecipeBouganvillea;
import eutros.botaniapp.common.block.flower.functional.SubtileBouganvillea;
import eutros.botaniapp.common.utils.BotaniaPPFakePlayer;
import eutros.botaniapp.common.utils.Reference;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.subtile.TileEntityFunctionalFlower;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.Supplier;

public class RecipeBouganvilleaAnvil extends RecipeBouganvillea {

    private static final ResourceLocation BOUG_ANVILS = new ResourceLocation(Reference.MOD_ID, "boug_anvils");
    public static IRecipeSerializer<RecipeBouganvilleaAnvil> SERIALIZER = new SpecialRecipeSerializer<>(RecipeBouganvilleaAnvil::new);

    public RecipeBouganvilleaAnvil(ResourceLocation id) {
        super(id, ItemStack.EMPTY, SubtileBouganvillea.BUILTIN_GROUP);
    }

    @Override
    public boolean shouldTrigger(IBouganvilleaInventory inventory) {
        return inventory.getSizeInventory() > 2;
    }

    @ParametersAreNonnullByDefault
    @Override
    public boolean matches(IBouganvilleaInventory inventory, World world) {
        if(inventory.getSizeInventory() == 1) {
            ItemStack stack = inventory.getThrown().getItem();
            Tag<Item> tag = ItemTags.getCollection().get(BOUG_ANVILS);
            return tag != null && tag.contains(stack.getItem());
        }
        return true;
    }

    @NotNull
    @Override
    public ItemStack getCraftingResult(IBouganvilleaInventory inventory) {
        Supplier<ItemStack> defaultRet = () -> inventory.getThrown().getItem();

        // start RepairContainer copypasta

        ItemStack itemstack = inventory.getStackInSlot(1);
        int i = 0;
        int j;
        ItemStack result = inventory.getThrown().getItem();
        ItemStack combineWith = itemstack.copy();
        Map<Enchantment, Integer> enchantMap = EnchantmentHelper.getEnchantments(result);
        j = itemstack.getRepairCost() + (combineWith.isEmpty() ? 0 : combineWith.getRepairCost());
        int materialCost = 0;
        boolean flag = false;

        if(!combineWith.isEmpty()) {
            flag = combineWith.getItem() == Items.ENCHANTED_BOOK && !EnchantedBookItem.getEnchantments(combineWith).isEmpty();
            if(result.isDamageable() && result.getItem().getIsRepairable(itemstack, combineWith)) {
                int l2 = Math.min(result.getDamage(), result.getMaxDamage() / 4);
                if(l2 <= 0) {
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
                if(!flag && (result.getItem() != combineWith.getItem() || !result.isDamageable())) {
                    return defaultRet.get();
                }

                if(result.isDamageable() && !flag) {
                    int l = itemstack.getMaxDamage() - itemstack.getDamage();
                    int i1 = combineWith.getMaxDamage() - combineWith.getDamage();
                    int j1 = i1 + result.getMaxDamage() * 12 / 100;
                    int k1 = l + j1;
                    int l1 = result.getMaxDamage() - k1;
                    if(l1 < 0) {
                        l1 = 0;
                    }

                    if(l1 < result.getDamage()) {
                        result.setDamage(l1);
                        i += 2;
                    }
                }

                Map<Enchantment, Integer> map1 = EnchantmentHelper.getEnchantments(combineWith);
                boolean flag2 = false;
                boolean flag3 = false;

                for(Enchantment enchantment1 : map1.keySet()) {
                    if(enchantment1 != null) {
                        int i2 = enchantMap.getOrDefault(enchantment1, 0);
                        int j2 = map1.get(enchantment1);
                        j2 = i2 == j2 ? j2 + 1 : Math.max(j2, i2);
                        boolean flag1 = enchantment1.canApply(itemstack) || itemstack.getItem() == Items.ENCHANTED_BOOK;

                        for(Enchantment enchantment : enchantMap.keySet()) {
                            if(enchantment != enchantment1 && !enchantment1.isCompatibleWith(enchantment)) {
                                flag1 = false;
                                ++i;
                            }
                        }

                        if(!flag1) {
                            flag3 = true;
                        } else {
                            flag2 = true;
                            if(j2 > enchantment1.getMaxLevel()) {
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

                            if(flag) {
                                k3 = Math.max(1, k3 / 2);
                            }

                            i += k3 * j2;
                            if(itemstack.getCount() > 1) {
                                i = 40;
                            }
                        }
                    }
                }

                if(flag3 && !flag2) {
                    return defaultRet.get();
                }
            }
        }

        if(flag && !result.isBookEnchantable(combineWith)) result = ItemStack.EMPTY;

        int maximumCost = j + i;
        if(i <= 0) {
            result = ItemStack.EMPTY;
        }

        if(maximumCost >= 40) {
            result = ItemStack.EMPTY;
        }

        if(!result.isEmpty()) {
            int k2 = result.getRepairCost();
            if(!combineWith.isEmpty() && k2 < combineWith.getRepairCost()) {
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

        TileEntityFunctionalFlower flower = inventory.getFlower();
        World world = flower.getWorld();
        assert world != null;
        PlayerEntity player = Optional.ofNullable(inventory.getThrown().getThrowerId()).map(world::getPlayerByUuid)
                .orElseGet(() -> new BotaniaPPFakePlayer((ServerWorld) world));
        float breakChance = ForgeHooks.onAnvilRepair(player,
                result,
                inventory.getStackInSlot(0),
                inventory.getThrown().getItem());

        if(flower.getWorld().getRandom().nextFloat() < breakChance) {
            ItemStack anvil = inventory.getStackInSlot(0);

            anvil = damage(anvil);

            if(anvil == null) {
                inventory.removeStackFromSlot(0);
                inventory.cancelSound();
                world.playSound(null, flower.getEffectivePos(), SoundEvents.BLOCK_ANVIL_DESTROY, SoundCategory.BLOCKS, 0.5F, 1F);
            } else
                inventory.setInventorySlotContents(0, anvil);
        }

        flower.addMana(-maximumCost * 5);

        return result;
    }

    /**
     * Damage an anvil.
     * Currently resolves using the cursed tag abomination below.
     * Can and will more than likely break in a coming update :^)
     */
    @Nullable
    private ItemStack damage(ItemStack stack) {
        Item itemIn = stack.getItem();
        if(itemIn instanceof IBouganvilleaAnvil) { // API integration
            return ((IBouganvilleaAnvil) itemIn).damage(stack);
        }
        Tag<Item> tag = ItemTags.getCollection().get(BOUG_ANVILS); // Data integration
        if(tag != null) {
            Collection<Tag.ITagEntry<Item>> entries = tag.getEntries();

            for(Tag.ITagEntry<Item> entry : entries) {
                List<Item> items = new ArrayList<>();
                getItemsSorted(items, entry); // Some magic happens that hopefully doesn't break.

                boolean flag = false; // Just get the entry that follows.
                for(Item item : items) {
                    if(flag)
                        return new ItemStack(item);
                    if(item == itemIn)
                        flag = true;
                }
                if(flag)
                    return null;
            }
        }
        return null;
    }

    private void getItemsSorted(List<Item> list, Tag.ITagEntry<Item> entry) {
        if(entry instanceof Tag.TagEntry) { // This is a tag in its own right. What we are expecting of top-level entries.
            Tag<Item> tag = ItemTags.getCollection().get(((Tag.TagEntry<Item>) entry).getSerializedId());
            if(tag != null) // We just got this tag from its entry's ID, it shouldn't ever be null, but just in case.
                tag.getEntries().forEach(i -> getItemsSorted(list, i));
        } else if(entry instanceof Tag.ListEntry) {
            list.addAll(((Tag.ListEntry<Item>) entry).getTaggedItems()); // These seem to always be singleton lists.
        }
    }

    @NotNull
    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        Tag<Item> tag = ItemTags.getCollection().get(BOUG_ANVILS);
        if(tag != null)
            ingredients.add(Ingredient.fromTag(tag));
        else
            ingredients.add(Ingredient.EMPTY);
        return ingredients;
    }

    @NotNull
    @Override
    public IRecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

}
