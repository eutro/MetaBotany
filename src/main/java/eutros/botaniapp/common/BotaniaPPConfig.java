package eutros.botaniapp.common;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import eutros.botaniapp.common.utils.Reference;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import org.apache.commons.lang3.tuple.Pair;

import java.nio.file.Path;

public class BotaniaPPConfig {

    public static class Common {

        public static final String PATH = Reference.MOD_ID + "-common.toml";
        public final IntValue REGEX_TIMEOUT;

        Common(ForgeConfigSpec.Builder builder) {
            builder.comment("Common configuration settings")
                    .push("common");

            REGEX_TIMEOUT = builder
                    .comment("Time until a Regular Expression times out, in nanoseconds. Used for the Advanced Corporea Funnel's RegEx Matcher to counteract ReDoSing.")
                    .translation("config.botaniapp.regex_timeout")
                    .defineInRange("REGEX_TIMEOUT", 500000, 0, Integer.MAX_VALUE);
        }
    }

    static final ForgeConfigSpec COMMON_SPEC;
    public static final Common COMMON;
    static {
        final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON_SPEC = specPair.getRight();
        COMMON = specPair.getLeft();
    }

    public static void loadConfig(ForgeConfigSpec spec, Path path) {

        final CommentedFileConfig configData = CommentedFileConfig.builder(path)
                .sync()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .build();

        configData.load();
        spec.setConfig(configData);
    }
}
