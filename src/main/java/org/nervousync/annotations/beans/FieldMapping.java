package org.nervousync.annotations.beans;

import org.nervousync.beans.converter.DataConverter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface FieldMapping {

	Class<? extends DataConverter> value() default DataConverter.class;

}
