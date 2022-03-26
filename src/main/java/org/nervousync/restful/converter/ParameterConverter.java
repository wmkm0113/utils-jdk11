package org.nervousync.restful.converter;

public interface ParameterConverter {

    boolean match(Class<?> targetClass);

    String toString(Object object, String[] mediaTypes);

    Object fromString(String value);
}
