package eutros.botaniapp.asm;

import eutros.botaniapp.common.enchantment.BotaniaPPEnchantments;
import eutros.botaniapp.common.enchantment.EnchantmentSharper;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vazkii.botania.api.internal.IManaBurst;

@SuppressWarnings("unused")
public class ASMHooks {

    private static Logger LOGGER = LogManager.getLogger();

    /**
     * Called at a harvesting lens' collision, augmenting the config based mining level.
     *
     * @param base  The base level, without any enchantments.
     * @param stack The lens stack.
     * @return The new mining level.
     *
     * @see vazkii.botania.common.item.lens.LensMine#collideBurst(IManaBurst, ThrowableEntity, RayTraceResult, boolean, boolean, ItemStack)
     * @see vazkii.botania.common.item.lens.LensWeight#collideBurst(IManaBurst, ThrowableEntity, RayTraceResult, boolean, boolean, ItemStack)
     */
    public static int manaLensEnchantSharper(int base, ItemStack stack) {
        int level = EnchantmentHelper.getEnchantmentLevel(BotaniaPPEnchantments.lensSharper, stack);

        return base + level;
    }

    /**
     * Called from harvesting lens' collision, modifying the block's hardness.
     * If the hardness is {@code -1F} or {@code > 50F}, the block won't be mined.
     *
     * @param base  The actual hardness of the block.
     * @param stack The lens stack.
     * @return The hardness it should be handled as.
     *
     * @see vazkii.botania.common.item.lens.LensMine#collideBurst(IManaBurst, ThrowableEntity, RayTraceResult, boolean, boolean, ItemStack)
     * @see vazkii.botania.common.item.lens.LensWeight#collideBurst(IManaBurst, ThrowableEntity, RayTraceResult, boolean, boolean, ItemStack)
     */
    public static float manaLensEnchantSharperHardness(float base, ItemStack stack) {
        if(base == -1F)
            return base;

        int level = EnchantmentHelper.getEnchantmentLevel(BotaniaPPEnchantments.lensSharper, stack);

        if(level > 0) {
            level += EnchantmentSharper.HARDNESS_WARP;
            return base / level;
        }

        return base;
    }

}
