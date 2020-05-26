package eutros.metabotany.common.item.lens;

import eutros.metabotany.asm.ASMHooks;
import net.minecraft.item.ItemStack;

/**
 * Actual mechanics are done with ASM. {@link ASMHooks#instanceOfHook(Object, ItemStack, int)}
 */
public class UnresponsiveLens extends ItemLens {

    public UnresponsiveLens(Properties properties) {
        super(properties);
    }

}
