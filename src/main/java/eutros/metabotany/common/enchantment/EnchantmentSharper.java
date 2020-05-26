package eutros.metabotany.common.enchantment;

import eutros.metabotany.api.MetaBotanyAPI;
import eutros.metabotany.api.internal.config.Configurable;
import eutros.metabotany.asm.ASMHooks;
import eutros.metabotany.common.item.MetaBotanyItems;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Logic is performed in {@link ASMHooks#manaLensEnchantSharper(int, ItemStack)} and {@link ASMHooks#manaLensEnchantSharperHardness(float, ItemStack)}.
 */
public class EnchantmentSharper extends Enchantment {

    @Configurable(path = {"enchantment", "sharper"},
                  comment = "\"Sharper\" enchantment max level")
    public static int MAX_LEVEL = 3;

    @Configurable(path = {"enchantment", "sharper"},
                  comment = {"How much to increment the enchantment level before dividing the block hardness by it.",
                          "Make this negative MAX_LEVEL or lower to disable hardness warp."})
    public static int HARDNESS_WARP = 1;

    protected EnchantmentSharper() {
        super(Rarity.RARE,
                MetaBotanyAPI.getInstance().ENCHANTMENT_TYPE_MANA_LENS,
                new EquipmentSlotType[] {});
    }

    @Override
    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        Item item = stack.getItem();
        return item == MetaBotanyItems.BOTANIA_MINE_LENS || item == MetaBotanyItems.BOTANIA_WEIGHT_LENS;
    }

    @Override
    public int getMinEnchantability(int level) {
        return level * 2;
    }

}
