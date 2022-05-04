package org.nervousync.restful.converter;

/**
 * The interface Parameter converter.
 */
public interface ParameterConverter {

    /**
     * Match boolean.
     *
     * @param targetClass the target class
     * @return the boolean
     */
    boolean match(Class<?> targetClass);

    /**
     * To string string.
     *
     * @param object     the object
     * @param mediaTypes the media types
     * @return the string
     */
    String toString(Object object, String[] mediaTypes);

    /**
     * From string object.
     *
     * @param clazz the clazz
     * @param value the value
     * @return the object
     */
    Object fromString(Class<?> clazz, String value);
}
