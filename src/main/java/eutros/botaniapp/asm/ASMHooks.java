package eutros.botaniapp.asm;

import eutros.botaniapp.common.enchantment.BotaniaPPEnchantments;
import eutros.botaniapp.common.enchantment.EnchantmentSharper;
import eutros.botaniapp.common.item.BotaniaPPItems;
import net.minecraft.block.LeavesBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vazkii.botania.api.internal.IManaBurst;
import vazkii.botania.api.mana.ICompositableLens;
import vazkii.botania.api.mana.IManaBlock;
import vazkii.botania.api.mana.IManaReceiver;
import vazkii.botania.api.mana.IManaTrigger;

@SuppressWarnings("unused")
public class ASMHooks {

    private static Logger LOGGER = LogManager.getLogger();

    /**
     * Called from {@link vazkii.botania.common.item.lens.LensMine#collideBurst(IManaBurst, ThrowableEntity, RayTraceResult, boolean, boolean, ItemStack)} and
     * {@link vazkii.botania.common.item.lens.LensWeight#collideBurst(IManaBurst, ThrowableEntity, RayTraceResult, boolean, boolean, ItemStack)},
     * augmenting the config based mining level.
     *
     * @param base  The base level, without any enchantments.
     * @param stack The lens stack.
     * @return The new mining level.
     */
    public static int manaLensEnchantSharper(int base, ItemStack stack) {
        int level = EnchantmentHelper.getEnchantmentLevel(BotaniaPPEnchantments.lensSharper, stack);

        return base + level;
    }

    /**
     * Called from {@link vazkii.botania.common.item.lens.LensMine#collideBurst(IManaBurst, ThrowableEntity, RayTraceResult, boolean, boolean, ItemStack)} and
     * {@link vazkii.botania.common.item.lens.LensWeight#collideBurst(IManaBurst, ThrowableEntity, RayTraceResult, boolean, boolean, ItemStack)},
     * modifying the block's hardness.
     * If the hardness is {@code -1F} or {@code > 50F}, the block won't be mined.
     *
     * @param base  The actual hardness of the block.
     * @param stack The lens stack.
     * @return The hardness it should be handled as.
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

    /**
     * Called on all of the cased 'instancof'-s in {@link vazkii.botania.common.entity.EntityManaBurst#onImpact(RayTraceResult)}.
     * Also called from the {@link vazkii.botania.common.item.lens.Lens#collideBurst(IManaBurst, ThrowableEntity, RayTraceResult, boolean, boolean, ItemStack)}'s
     * of {@link vazkii.botania.common.item.lens.LensMine} and {@link vazkii.botania.common.item.lens.LensDamage}.
     *
     * @param test  the object being 'instanceof'-ed
     * @param stack the lens stack of the burst
     * @param desc  the operand of the instanceof instruction, indexed as: {
     *              {@literal 0}: {@link LeavesBlock},
     *              {@literal 1}: {@link IManaTrigger},
     *              {@literal 2}: {@link IManaReceiver},
     *              {@literal 3}: {@link PlayerEntity},
     *              {@literal 4}: {@link IManaBlock}
     *              }
     * @return whether {@param test} should be considered an instance of the {@param desc}-referenced class.
     */
    @SuppressWarnings("JavadocReference")
    public static boolean instanceOfHook(Object test, ItemStack stack, int desc) {
        if(stack == null ||
                !(stack.getItem() == BotaniaPPItems.unresponsiveLens ||
                        (stack.getItem() instanceof ICompositableLens &&
                                ((ICompositableLens) stack.getItem()).getCompositeLens(stack).getItem() == BotaniaPPItems.unresponsiveLens))) {
            switch(desc) {
                case 0:
                    return test instanceof LeavesBlock;
                case 1:
                    return test instanceof IManaTrigger;
                case 2:
                    return test instanceof IManaReceiver;
                case 3:
                    return test instanceof PlayerEntity;
                case 4:
                    return test instanceof IManaBlock;
            }
        }
        return false; // deny, deny, deny
    }

}
