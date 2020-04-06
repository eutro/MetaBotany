package eutros.botaniapp.common.enchantment;

import eutros.botaniapp.api.BotaniaPPAPI;
import eutros.botaniapp.api.internal.config.Configurable;
import eutros.botaniapp.asm.ASMHooks;
import eutros.botaniapp.common.item.BotaniaPPItems;
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
    public static int MAX_LEVEL = 5;

    @Configurable(path = {"enchantment", "sharper"},
                  comment = {"How much to increment the enchantment level before dividing the block hardness by it.",
                          "Make this negative MAX_LEVEL or lower to disable hardness warp."})
    public static int HARDNESS_WARP = 1;

    protected EnchantmentSharper() {
        super(Rarity.RARE,
                BotaniaPPAPI.getInstance().ENCHANTMENT_TYPE_MANA_LENS,
                new EquipmentSlotType[] {});
    }

    @Override
    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        Item item = stack.getItem();
        return item == BotaniaPPItems.BOTANIA_MINE_LENS || item == BotaniaPPItems.BOTANIA_WEIGHT_LENS;
    }

    @Override
    public int getMinEnchantability(int level) {
        return level;
    }

}
