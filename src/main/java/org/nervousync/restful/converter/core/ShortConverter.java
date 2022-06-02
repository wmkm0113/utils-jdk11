package org.nervousync.restful.converter.core;

import org.nervousync.restful.converter.ParameterConverter;
import org.nervousync.utils.StringUtils;

/**
 * The type Short converter.
 */
public final class ShortConverter implements ParameterConverter {

    @Override
    public boolean match(Class<?> targetClass) {
        return Short.class.equals(targetClass);
    }

    @Override
    public String toString(Object object, String[] mediaTypes) {
        return (object instanceof Short) ? ((Short) object).toString() : null;
    }

    @Override
    public Object fromString(Class<?> clazz, String value) {
        if (!Short.class.equals(clazz)) {
            return null;
        }
        return StringUtils.notBlank(value) ? Short.valueOf(value) : null;
    }
}
