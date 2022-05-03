package org.nervousync.restful.converter.core;

import org.nervousync.restful.converter.ParameterConverter;
import org.nervousync.utils.StringUtils;

public final class LongConverter implements ParameterConverter {

    @Override
    public boolean match(Class<?> targetClass) {
        return Long.class.equals(targetClass);
    }

    @Override
    public String toString(Object object, String[] mediaTypes) {
        return (object instanceof Long) ? ((Long) object).toString() : null;
    }

    @Override
    public Object fromString(Class<?> clazz, String value) {
        if (!Long.class.equals(clazz)) {
            return null;
        }
        return StringUtils.notBlank(value) ? Long.valueOf(value) : null;
    }
}
