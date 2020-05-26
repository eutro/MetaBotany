package eutros.metabotany.api.internal.config;

import net.minecraftforge.fml.config.ModConfig;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
public @interface Configurable {

    /**
     * The path, not including the field name.
     */
    String[] path() default {};

    /**
     * A comment for this field.
     */
    String[] comment() default {};

    /**
     * A localisation key for the comment.
     */
    String translation() default "";

    /**
     * A static method to call when the field is updated.
     * The method must be a member of the same class as the field.
     *
     * Parameters:
     * - {@code String name} the name of the field
     * - {@code T value} the new value of the field, which has already been set
     */
    String callback() default "";

    /**
     * The side this config value should appear on.
     */
    ModConfig.Type side() default ModConfig.Type.COMMON;

}
