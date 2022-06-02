package org.nervousync.annotations.generator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for ID generator implement class
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface GeneratorProvider {
    /**
     * Generator implement name
     *
     * @return the generator name
     */
    String value();
}
