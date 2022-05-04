package org.nervousync.restful.converter.core;

import org.nervousync.restful.converter.ParameterConverter;
import org.nervousync.utils.StringUtils;

/**
 * The type Double converter.
 */
public final class DoubleConverter implements ParameterConverter {

    @Override
    public boolean match(Class<?> targetClass) {
        return Double.class.equals(targetClass);
    }

    @Override
    public String toString(Object object, String[] mediaTypes) {
        return (object instanceof Double) ? object.toString() : null;
    }

    @Override
    public Object fromString(Class<?> clazz, String value) {
        if (!Double.class.equals(clazz)) {
            return null;
        }
        return StringUtils.notBlank(value) ? Double.valueOf(value) : null;
    }
}
