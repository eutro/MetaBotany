package eutros.botaniapp.common.block.tile.corporea.matchers;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import vazkii.botania.api.corporea.ICorporeaRequestMatcher;
import vazkii.botania.common.block.tile.corporea.TileCorporeaRetainer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO ReDoS protection
public class RegexMatcher implements ICorporeaRequestMatcher {

    private final Pattern pattern;

    private static final String PATTERN = "pattern";
    private static final String FLAGS = "regex_flags";

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
        Matcher matcher = pattern.matcher(stack.getDisplayName().getFormattedText());
        return matcher.matches();
    }

    @Override
    public void writeToNBT(CompoundNBT tag) {
        tag.putString(PATTERN, pattern.toString());
        tag.putInt(FLAGS, pattern.flags());
    }

    public static RegexMatcher serialize(CompoundNBT tag) {
        return new RegexMatcher(tag.getString(PATTERN), tag.getInt(FLAGS));
    }

    @Override
    public ITextComponent getRequestName() {
        return null;
    }
}
