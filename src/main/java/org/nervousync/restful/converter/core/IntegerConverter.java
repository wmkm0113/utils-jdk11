package org.nervousync.restful.converter.core;

import org.nervousync.restful.converter.ParameterConverter;
import org.nervousync.utils.StringUtils;

public final class IntegerConverter implements ParameterConverter {

    @Override
    public boolean match(Class<?> targetClass) {
        return Integer.class.equals(targetClass);
    }

    @Override
    public String toString(Object object, String[] mediaTypes) {
        return (object instanceof Integer) ? object.toString() : null;
    }

    @Override
    public Object fromString(Class<?> clazz, String value) {
        if (!Integer.class.equals(clazz)) {
            return null;
        }
        return StringUtils.notBlank(value) ? Integer.valueOf(value) : null;
    }
}
