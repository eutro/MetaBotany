package eutros.botaniapp.common.block.tile.corporea.matchers;

import net.minecraft.item.ItemStack;
import vazkii.botania.api.corporea.CorporeaHelper;
import vazkii.botania.api.corporea.ICorporeaRequestMatcher;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AdvancedMatcher implements ICorporeaRequestMatcher {

    private static final Pattern regexPatternMatcher = Pattern.compile("/(.+)/(i?)");

    public static ICorporeaRequestMatcher fromItemStack(ItemStack stack, boolean checkNBT) {
        if(!stack.hasDisplayName())
            return CorporeaHelper.createMatcher(stack, checkNBT);

        String name = stack.getDisplayName().getFormattedText();

        Matcher matcher = regexPatternMatcher.matcher(name);
        if(matcher.matches()) {
            return new RegexMatcher(Pattern.compile(matcher.group(1)));
        }

        return CorporeaHelper.createMatcher(stack, checkNBT);
    }

    public boolean isInvalid() {
        return false;
    }
}
