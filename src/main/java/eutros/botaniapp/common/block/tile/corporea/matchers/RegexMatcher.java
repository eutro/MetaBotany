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

    public enum Type {
        NAME(""),
        ORE_DICT("ore"),
        LOC_KEY("deloc"),
        MOD_ID("mod"),
        ITEM_ID("raw_id"),
        RESOURCE_LOC("id");

        public final String code;

        Type(String code) {
            this.code = code;
        }

        static Type byCode(String code) {
            for(Type t : Type.values()) {
                if(t.code.equals(code))
                    return t;
            }
            return NAME;
        }
    }

    private final Pattern pattern;

    private static final String PATTERN = "pattern";
    private static final String FLAGS = "regex_flags";
    private static final String INVALID = "invalid";
    private static final String CODE = "code";

    public boolean invalid = false;
    public Type type;

    static {
        TileCorporeaRetainer.addCorporeaRequestMatcher("botaniapp_regex", RegexMatcher.class, RegexMatcher::serialize);
    }

    public RegexMatcher(Pattern pattern, Type type) {
        this.pattern = pattern;
        this.type = type;
    }

    public RegexMatcher(String pattern, int flags, Type type) {
        this.pattern = Pattern.compile(pattern, flags);
        this.type = type;
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
        } catch (RegularExpressionUtils.RegexTimeout e) {
            invalid = true;
        }

        return false;
    }

    @Override
    public void writeToNBT(CompoundNBT tag) {
        tag.putString(PATTERN, pattern.toString());
        tag.putInt(FLAGS, pattern.flags());
        tag.putBoolean(INVALID, invalid);
        tag.putString(CODE, type.code);
    }

    public static RegexMatcher serialize(CompoundNBT tag) {
        RegexMatcher regexMatcher = new RegexMatcher(tag.getString(PATTERN), tag.getInt(FLAGS), Type.byCode(tag.getString(CODE)));
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
