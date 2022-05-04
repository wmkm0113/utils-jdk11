package org.nervousync.restful.converter.core;

import org.nervousync.restful.converter.ParameterConverter;
import org.nervousync.utils.StringUtils;

/**
 * The type Character converter.
 */
public final class CharacterConverter implements ParameterConverter {

    @Override
    public boolean match(Class<?> targetClass) {
        return Character.class.equals(targetClass);
    }

    @Override
    public String toString(Object object, String[] mediaTypes) {
        return (object instanceof Character) ? ((Character) object).toString() : null;
    }

    @Override
    public Object fromString(Class<?> clazz, String value) {
        if (!Character.class.equals(clazz)) {
            return null;
        }
        return (StringUtils.notBlank(value) && value.length() == 1) ? value.charAt(0) : null;
    }
}
