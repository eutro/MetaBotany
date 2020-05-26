package eutros.metabotany.api;

import net.minecraft.enchantment.EnchantmentType;

public class MetaBotanyAPI {

    /**
     * An enchantment that can be applied to mana lenses.
     */
    public final EnchantmentType ENCHANTMENT_TYPE_MANA_LENS;

    private MetaBotanyAPI(EnchantmentType mana_lens) {
        ENCHANTMENT_TYPE_MANA_LENS = mana_lens;
    }

    private static MetaBotanyAPI INSTANCE;

    /**
     * Set in the mod's constructor.
     */
    public static MetaBotanyAPI getInstance() {
        return INSTANCE;
    }

    /**
     * Used internally to set the {@link MetaBotanyAPI} instance.
     *
     * @throws IllegalAccessError if there is an attempt to set the instance a second time
     */
    public static void setInstance(EnchantmentType mana_lens) throws IllegalAccessError {
        if(INSTANCE != null)
            throw new IllegalAccessError();
        INSTANCE = new MetaBotanyAPI(mana_lens);
    }

}
