package org.nervousync.annotations.beans;

import org.nervousync.commons.core.Globals;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface BeanMapping {

	Class<?> beanClass();

	String fieldName() default Globals.DEFAULT_VALUE_STRING;

}
