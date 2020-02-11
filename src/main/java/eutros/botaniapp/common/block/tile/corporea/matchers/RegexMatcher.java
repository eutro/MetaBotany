package eutros.botaniapp.common.block.tile.corporea.matchers;

import eutros.botaniapp.common.BotaniaPPConfig;
import eutros.botaniapp.common.utils.RegularExpressionUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import vazkii.botania.common.block.tile.corporea.TileCorporeaRetainer;

import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexMatcher extends AdvancedMatcher {

    public enum Type {
        NAME('n'),
        LOC_KEY('u'),
        MOD_ID('m'),
        ITEM_ID('r'),
        RESOURCE_LOC('i');

        public final char code;

        Type(char code) {
            this.code = code;
        }

        static Type byCode(char code) {
            for(Type t : Type.values()) {
                if(t.code == code)
                    return t;
            }
            return NAME;
        }

        static Type byCode(String code) {
            return byCode(code.length() == 0 ? 'n' : code.charAt(0));
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

        String text = "";

        switch(type) {
            case LOC_KEY:
                text = stack.getTranslationKey();
                break;
            case MOD_ID:
            case ITEM_ID:
            case RESOURCE_LOC:
                ResourceLocation registryName = stack.getItem().getRegistryName();
                if(registryName != null) {
                    switch(type) {
                        case MOD_ID:
                            text = registryName.getNamespace();
                            break;
                        case ITEM_ID:
                            text = registryName.getPath();
                            break;
                        case RESOURCE_LOC:
                            text = registryName.toString();
                            break;
                    }
                    break;
                }
            default:
                text = stack.getDisplayName().getFormattedText();
        }

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
        tag.putString(CODE, String.valueOf(type.code));
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
