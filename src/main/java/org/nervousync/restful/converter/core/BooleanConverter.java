package org.nervousync.restful.converter.core;

import org.nervousync.restful.converter.ParameterConverter;
import org.nervousync.utils.StringUtils;

public final class BooleanConverter implements ParameterConverter {

    @Override
    public boolean match(Class<?> targetClass) {
        return Boolean.class.equals(targetClass);
    }

    @Override
    public String toString(Object object, String[] mediaTypes) {
        return (object instanceof Boolean) ? ((Boolean) object).toString() : null;
    }

    @Override
    public Object fromString(String value) {
        return StringUtils.notBlank(value) ? Boolean.valueOf(value) : null;
    }
}
