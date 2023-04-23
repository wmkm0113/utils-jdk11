package org.nervousync.annotations.beans;

import org.nervousync.commons.core.Globals;
import org.nervousync.utils.StringUtils;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface OutputConfig {
    StringUtils.StringType type() default StringUtils.StringType.SIMPLE;

    boolean formatted() default false;

    String encoding() default Globals.DEFAULT_ENCODING;

}
