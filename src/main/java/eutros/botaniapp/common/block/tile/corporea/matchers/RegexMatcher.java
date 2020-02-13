package eutros.botaniapp.common.block.tile.corporea.matchers;

import eutros.botaniapp.common.BotaniaPPConfig;
import eutros.botaniapp.common.utils.RegularExpressionUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import vazkii.botania.api.corporea.ICorporeaRequestMatcher;
import vazkii.botania.common.block.tile.corporea.TileCorporeaRetainer;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static eutros.botaniapp.common.utils.RegularExpressionUtils.replaceAll;

public class RegexMatcher extends AdvancedMatcher {

    private static final Pattern regexPatternMatcher = Pattern.compile("/(?<exp>.+)/" + // Matches the expression itself.
            "(?<type>(N(AME)?)|(O(RE(_?DICT)?)?)|(T(AGS?)?)|(U(NLOC)?)|(M(OD)?)|(P(ATH)?)|(ID?))?" + // What should be matched?
            "(?<tags>[ixul]{0,4})"); // Normal RegEx tags: case insensitive, comments, unicode case, literal

    public enum Type {
        NAME('N'),
        ORE_DICT('O'),
        TAG('T'),
        LOC_KEY('U'),
        MOD_ID('M'),
        ITEM_ID('P'),
        RESOURCE_LOC('I');

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
            return code == null || code.length() == 0 ? NAME : byCode(code.charAt(0));
        }

    }
    private final Pattern pattern;

    private static final String PATTERN = "pattern";
    private static final String FLAGS = "regex_flags";
    private static final String INVALID = "invalid";
    private static final String CODE = "code";
    private static final String SOURCE = "source";

    public boolean invalid = false;
    public String source;
    public Type type;

    static {
        TileCorporeaRetainer.addCorporeaRequestMatcher("botaniapp_regex", RegexMatcher.class, RegexMatcher::serialize);
    }

    public RegexMatcher(Pattern pattern, Type type, String source) {
        this.pattern = pattern;
        this.type = type;
        this.source = source;
    }

    public RegexMatcher(String pattern, int flags, Type type, String source) {
        this.pattern = Pattern.compile(pattern, flags);
        this.type = type;
        this.source = source;
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
            case ORE_DICT:
            case TAG:
                for(ResourceLocation tagLoc : ItemTags.getCollection().getOwningTags(stack.getItem())) {
                    if(matchText(tagLoc.getPath()))
                        return true;
                }
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

        return matchText(text);
    }

    private Boolean matchText(String text) {
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
        tag.putString(SOURCE, source);
    }

    public static RegexMatcher serialize(CompoundNBT tag) {
        RegexMatcher regexMatcher = new RegexMatcher(tag.getString(PATTERN), tag.getInt(FLAGS), Type.byCode(tag.getString(CODE)), tag.getString(SOURCE));
        regexMatcher.invalid = tag.getBoolean(INVALID);
        return regexMatcher;
    }

    private static final Pattern patternMatcher = Pattern.compile("/(.+)/([a-zA-Z]*)$");
    // This is when the leaning toothpick syndrome hits.
    private static final Pattern specialEscapeMatcher = Pattern.compile("\\.|(\\\\([wWsSbBtnr]|([0-9]+)))");
    private static final Pattern escapeMatcher = Pattern.compile("\\\\[+*\\\\?()\\[\\]](?!\u00a7r)");
    private static final Pattern parenthesisMatcher = Pattern.compile("[()](?!\u00a7r)");
    private static final Pattern bracketMatcher = Pattern.compile("[\\[\\]](?!\u00a7r)");
    private static final Pattern quantifierMatcher = Pattern.compile("([+*?]|(\\{[\\d,]+?}))(?!\u00a7r)");
    private static final Pattern tagMatcher = Pattern.compile("/([A-Z]*)([a-z]*)$");

    @Override
    public ITextComponent getRequestName() {
        String name;
        if(!invalid) {
            name = replaceAll(source, patternMatcher, TextFormatting.GRAY + "/" + TextFormatting.RESET + "$1" + TextFormatting.GRAY + "/$2");
            name = replaceAll(name, specialEscapeMatcher, TextFormatting.GOLD + "$0" + TextFormatting.RESET);
            name = replaceAll(name, escapeMatcher, TextFormatting.LIGHT_PURPLE + "$0" + TextFormatting.RESET);
            name = replaceAll(name, parenthesisMatcher, TextFormatting.GREEN + "$0" + TextFormatting.RESET);
            name = replaceAll(name, bracketMatcher, TextFormatting.GOLD + "$0" + TextFormatting.RESET);
            name = replaceAll(name, quantifierMatcher, TextFormatting.AQUA + "$0" + TextFormatting.RESET);
            name = replaceAll(name, tagMatcher, "/" + TextFormatting.DARK_GREEN + "$1" + TextFormatting.BLUE + "$2");
        } else {
            name = TextFormatting.RED + source + TextFormatting.RESET;
        }
        return new StringTextComponent(name);
    }

    @Override
    public boolean isInvalid() {
        return invalid;
    }

    public static Optional<ICorporeaRequestMatcher> from(String name) {
        Matcher matcher = regexPatternMatcher.matcher(name);
        if(matcher.matches()) {
            Pattern pattern;

            int mask = 0;

            String tags = matcher.group("tags");
            for(int i = 0; i < tags.length(); i++) {
                switch(tags.charAt(i)) {
                    case 'i':
                        mask |= Pattern.CASE_INSENSITIVE;
                        break;
                    case 'x':
                        mask |= Pattern.COMMENTS;
                        break;
                    case 'u':
                        mask |= Pattern.UNICODE_CASE;
                        break;
                    case 'l':
                        mask |= Pattern.LITERAL;
                        break;
                }
            }

            try {
                pattern = Pattern.compile(matcher.group("exp"), mask);
            } catch(PatternSyntaxException e) {
                return Optional.of(new AdvancedMatcher.InvalidMatcher());
            }

            RegexMatcher regexMatcher = new RegexMatcher(pattern,
                    Type.byCode(matcher.group("type")), name);
            return Optional.of(regexMatcher);
        }
        return Optional.empty();
    }
}
