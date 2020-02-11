package eutros.botaniapp.common.block.tile.corporea.matchers;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import vazkii.botania.api.corporea.CorporeaHelper;
import vazkii.botania.api.corporea.ICorporeaRequestMatcher;
import vazkii.botania.common.block.tile.corporea.TileCorporeaRetainer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public abstract class AdvancedMatcher implements ICorporeaRequestMatcher {

    private static final Pattern regexPatternMatcher = Pattern.compile("([numri]?)/(.+)/(i?)");

    public static ICorporeaRequestMatcher fromItemStack(ItemStack stack, boolean checkNBT) {
        if(!stack.hasDisplayName())
            return CorporeaHelper.createMatcher(stack, checkNBT);

        String name = stack.getDisplayName().getFormattedText();

        Matcher matcher = regexPatternMatcher.matcher(name);
        if(matcher.matches()) {
            Pattern pattern;

            try {
                pattern = Pattern.compile(matcher.group(2));
            } catch(PatternSyntaxException e) {
                return new InvalidMatcher();
            }

            return new RegexMatcher(pattern,
                    RegexMatcher.Type.byCode(matcher.group(1)));
        }

        return CorporeaHelper.createMatcher(stack, checkNBT);
    }

    public boolean isInvalid() {
        return false;
    }

    private static class InvalidMatcher extends AdvancedMatcher {

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
            return null;
        }

        public boolean isInvalid() {
            return true;
        }

        static {
            TileCorporeaRetainer.addCorporeaRequestMatcher("invalid", InvalidMatcher.class, InvalidMatcher::new);
        }
    }
}
