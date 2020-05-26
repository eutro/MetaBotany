package eutros.metabotany.common.block.tile.corporea.matchers;

import eutros.metabotany.common.utils.Reference;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import vazkii.botania.api.corporea.CorporeaHelper;
import vazkii.botania.api.corporea.ICorporeaRequestMatcher;

import java.util.Optional;

public abstract class AdvancedMatcher implements ICorporeaRequestMatcher {

    public static ICorporeaRequestMatcher fromItemStack(ItemStack stack, boolean checkNBT) {
        if(!stack.hasDisplayName())
            return CorporeaHelper.instance().createMatcher(stack, checkNBT);

        String name = stack.getDisplayName().getFormattedText();
        Optional<ICorporeaRequestMatcher> matcher;

        matcher = RegexMatcher.from(name);
        if(matcher.isPresent()) {
            return matcher.get();
        }

        matcher = StringMatcher.from(name);
        return matcher.orElseGet(() -> CorporeaHelper.instance().createMatcher(stack, checkNBT));
    }

    public boolean isInvalid() {
        return false;
    }

    public static class InvalidMatcher extends AdvancedMatcher {

        static {
            CorporeaHelper.instance().registerRequestMatcher(new ResourceLocation(Reference.MOD_ID, "invalid"),
                    InvalidMatcher.class,
                    InvalidMatcher::new);
        }

        public InvalidMatcher(CompoundNBT compoundNBT) {
            new InvalidMatcher();
        }

        public InvalidMatcher() {
            super();
        }

        @Override
        public boolean isStackValid(ItemStack stack) {
            return false;
        }

        @Override
        public void writeToNBT(CompoundNBT tag) {
        }

        @Override
        public ITextComponent getRequestName() {
            return new StringTextComponent(TextFormatting.RED + "INVALID");
        }

        public boolean isInvalid() {
            return true;
        }
    }

}
