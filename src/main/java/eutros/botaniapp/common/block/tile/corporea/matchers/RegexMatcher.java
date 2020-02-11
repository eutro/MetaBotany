package eutros.botaniapp.common.block.tile.corporea.matchers;

import eutros.botaniapp.common.BotaniaPPConfig;
import eutros.botaniapp.common.utils.RegularExpressionUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import vazkii.botania.common.block.tile.corporea.TileCorporeaRetainer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexMatcher extends AdvancedMatcher {

    private final Pattern pattern;

    private static final String PATTERN = "pattern";
    private static final String FLAGS = "regex_flags";
    private static final String INVALID = "invalid";

    public boolean invalid = false;

    static {
        TileCorporeaRetainer.addCorporeaRequestMatcher("botaniapp_regex", RegexMatcher.class, RegexMatcher::serialize);
    }

    public RegexMatcher(Pattern pattern) {
        this.pattern = pattern;
    }

    public RegexMatcher(String pattern, int flags) {
        this.pattern = Pattern.compile(pattern, flags);
    }

    @Override
    public boolean isStackValid(ItemStack stack) {
        if(invalid || stack.isEmpty()) {
            return false;
        }

        String text = stack.getDisplayName().getFormattedText();
        Matcher matcher = RegularExpressionUtils.createMatcherWithTimeout(text, pattern, BotaniaPPConfig.COMMON.REGEX_TIMEOUT.get());
        try {
            return matcher.matches();
        } catch (RuntimeException e) {
            invalid = true;
        }

        return false;
    }

    @Override
    public void writeToNBT(CompoundNBT tag) {
        tag.putString(PATTERN, pattern.toString());
        tag.putInt(FLAGS, pattern.flags());
        tag.putBoolean(INVALID, invalid);
    }

    public static RegexMatcher serialize(CompoundNBT tag) {
        RegexMatcher regexMatcher = new RegexMatcher(tag.getString(PATTERN), tag.getInt(FLAGS));
        regexMatcher.invalid = tag.getBoolean(INVALID);
        return regexMatcher;
    }

    @Override
    public ITextComponent getRequestName() {
        return null;
    }

    @Override
    public boolean isInvalid() {
        return invalid;
    }
}
