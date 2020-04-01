package eutros.botaniapp.api;

import net.minecraft.enchantment.EnchantmentType;

public class BotaniaPPAPI {

    /**
     * An enchantment that can be applied to mana lenses.
     */
    public final EnchantmentType ENCHANTMENT_TYPE_MANA_LENS;

    private BotaniaPPAPI(EnchantmentType mana_lens) {
        ENCHANTMENT_TYPE_MANA_LENS = mana_lens;
    }

    private static BotaniaPPAPI INSTANCE;

    /**
     * Set in the mod's constructor.
     */
    public static BotaniaPPAPI getInstance() {
        return INSTANCE;
    }

    /**
     * Used internally to set the BotaniaPPAPI instance.
     *
     * @throws IllegalAccessError if there is an attempt to set the instance a second time
     */
    public static void setInstance(EnchantmentType mana_lens) throws IllegalAccessError {
        if(INSTANCE != null)
            throw new IllegalAccessError();
        INSTANCE = new BotaniaPPAPI(mana_lens);
    }

}
