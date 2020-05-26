package eutros.metabotany.common.block.tile.corporea.matchers;

import vazkii.botania.api.corporea.CorporeaHelper;
import vazkii.botania.api.corporea.ICorporeaRequestMatcher;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringMatcher {

    private static final Pattern pattern = Pattern.compile("\"(?<string>.+)\"");

    public static Optional<ICorporeaRequestMatcher> from(String text) {
        Matcher matcher = pattern.matcher(text);

        if(matcher.matches()) {
            return Optional.of(CorporeaHelper.instance().createMatcher(matcher.group("string").toLowerCase().trim()));
        }

        return Optional.empty();
    }

}
