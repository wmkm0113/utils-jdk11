package org.nervousync.restful.converter.core;

import org.nervousync.restful.converter.ParameterConverter;
import org.nervousync.utils.StringUtils;

public final class FloatConverter implements ParameterConverter {

    @Override
    public boolean match(Class<?> targetClass) {
        return Float.class.equals(targetClass);
    }

    @Override
    public String toString(Object object, String[] mediaTypes) {
        return (object instanceof Float) ? object.toString() : null;
    }

    @Override
    public Object fromString(String value) {
        return StringUtils.notBlank(value) ? Float.valueOf(value) : null;
    }
}
