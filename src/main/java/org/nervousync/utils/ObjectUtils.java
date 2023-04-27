/*
 * Licensed to the Nervousync Studio (NSYC) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.nervousync.utils;

import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import org.nervousync.beans.core.BeanObject;
import org.nervousync.commons.core.Globals;
import org.nervousync.enumerations.xml.DataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The type Object utils.
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: Jan 13, 2010 4:26:58 PM $
 */
public final class ObjectUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectUtils.class);

    private static final String ARRAY_START = "[";
    private static final String ARRAY_END = "]";
    private static final String ARRAY_ELEMENT_SEPARATOR = ", ";

    private ObjectUtils() {
    }

    /**
     * Create a proxy object instance
     *
     * @param <T>   T
     * @param clazz define class
     * @return object instance
     */
    public static <T> T newInstance(final Class<T> clazz) {
        return newInstance(clazz, new Object[0]);
    }

    /**
     * Create a proxy object instance
     *
     * @param <T>   T
     * @param clazz define class
     * @return object instance
     */
    public static <T> T newInstance(final Class<T> clazz, final Object[] paramValues) {
        if (clazz == null) {
            return null;
        }
        Constructor<T> constructor;
        try {
            if (paramValues == null || paramValues.length == 0) {
                constructor = ClassUtils.findConstructor(clazz);
                if (!Modifier.isPublic(clazz.getModifiers()) || !ReflectionUtils.publicMember(constructor)) {
                    ReflectionUtils.makeAccessible(constructor);
                }
                return constructor.newInstance();
            } else {
                Class<?>[] paramTypes = new Class[paramValues.length];
                for (int i = 0; i < paramValues.length; i++) {
                    paramTypes[i] = paramValues[i].getClass();
                }
                constructor = ClassUtils.findConstructor(clazz, paramTypes);
                if (!Modifier.isPublic(clazz.getModifiers()) || !ReflectionUtils.publicMember(constructor)) {
                    ReflectionUtils.makeAccessible(constructor);
                }
                return constructor.newInstance(paramValues);
            }
        } catch (SecurityException | NoSuchMethodException | InstantiationException
                 | IllegalAccessException | InvocationTargetException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Create proxy instance error! ", e);
            }
        }
        return null;
    }

    /**
     * Create a proxy object instance
     *
     * @param <T>               T
     * @param clazz             define class
     * @param methodInterceptor method interceptor instance
     * @return object instance
     */
    public static <T> T newInstance(final Class<T> clazz, final InvocationHandler methodInterceptor) {
        return newInstance(clazz, new Class[]{clazz}, methodInterceptor);
    }

    /**
     * Create a proxy object instance
     *
     * @param <T>   T
     * @param clazz define class
     * @return object instance
     */
    public static <T> T newInstance(final Class<T> clazz, final Class<?>[] interfaceClasses,
                                    final InvocationHandler methodInterceptor) {
        return createProxyInstance(clazz, interfaceClasses, methodInterceptor);
    }

    /**
     * Create a proxy object instance
     *
     * @param <T>               T
     * @param clazz             define class
     * @param invocationHandler method invocation handler instance
     * @return object instance
     */
    private static <T> T createProxyInstance(final Class<T> clazz, final Class<?>[] interfaceClasses,
                                             final InvocationHandler invocationHandler) {
        if (clazz == null || invocationHandler == null) {
            return newInstance(clazz);
        }
        Class<?>[] interfaces;
        if (interfaceClasses == null || interfaceClasses.length == 0) {
            interfaces = new Class[]{clazz};
        } else {
            interfaces = interfaceClasses;
        }
        return clazz.cast(Proxy.newProxyInstance(clazz.getClassLoader(), interfaces, invocationHandler));
    }

    /**
     * Check whether the given exception is compatible with the exceptions
     * declared in a throw clause.
     *
     * @param ex                 the exception to checked
     * @param declaredExceptions the exceptions declared in the throw clause
     * @return whether the given exception is compatible
     */
    public static boolean isCompatibleWithThrowClause(final Throwable ex, final Class<?>[] declaredExceptions) {
        if (ex instanceof RuntimeException || ex instanceof Error) {
            return Boolean.TRUE;
        }
        if (declaredExceptions != null) {
            return Arrays.stream(declaredExceptions)
                    .anyMatch(declaredException -> ClassUtils.isAssignableValue(declaredException, ex));
        }
        return Boolean.FALSE;
    }

    /**
     * Return whether the given array is empty: that is, <code>null</code>
     * or of zero-length.
     *
     * @param array the array to check
     * @return whether the given array is empty
     */
    public static boolean isEmpty(final Object[] array) {
        return (array == null || array.length == 0);
    }

    /**
     * Return whether the given array is empty: that is, <code>null</code>
     * or of zero-length.
     *
     * @param object the object to check
     * @return whether the given array is empty
     */
    public static boolean isNull(final Object object) {
        if (object == null) {
            return Boolean.TRUE;
        }

        if (object.getClass().isArray()) {
            return (Array.getLength(object) == 0);
        } else {
            if (object instanceof String) {
                return (((String) object).length() == 0);
            }
        }

        return Boolean.FALSE;
    }

    /**
     * Check whether the given array contains the given element.
     *
     * @param array   the array to check (maybe <code>null</code>,
     *                in which case the return value will always be <code>Boolean.FALSE</code>)
     * @param element the element to check for
     * @return whether the element has been found in the given array
     */
    public static boolean notContainsElement(final Collection<?> array, final Object element) {
        if (array == null) {
            return Boolean.TRUE;
        }
        return Arrays.stream(toObjectArray(array)).noneMatch(object -> nullSafeEquals(object, element));
    }

    /**
     * Append the given Object to the given array, returning a new array
     * consisting of the input array contents plus the given Object.
     *
     * @param array the array to append to (can be <code>null</code>)
     * @param obj   the Object to append
     * @return the new array (of the same component type; never <code>null</code>)
     */
    public static Object[] addObjectToArray(final Object[] array, final Object obj) {
        Class<?> compType = Object.class;
        if (array != null) {
            compType = array.getClass().getComponentType();
        } else if (obj != null) {
            compType = obj.getClass();
        }
        int newArrLength = (array != null ? array.length + 1 : 1);
        Object[] newArr = (Object[]) Array.newInstance(compType, newArrLength);
        if (array != null) {
            System.arraycopy(array, 0, newArr, 0, array.length);
        }
        newArr[newArr.length - 1] = obj;
        return newArr;
    }

    /**
     * Convert the given array (which may be a primitive array) to an
     * object array (if necessary of primitive wrapper objects).
     * <p>A <code>null</code> source value will be converted to an
     * empty Object array.
     *
     * @param source the (potentially primitive) array
     * @return the corresponding object array (never <code>null</code>)
     * @throws IllegalArgumentException if the parameter is not an array
     */
    public static Object[] toObjectArray(final Object source) {
        if (source instanceof Object[]) {
            return (Object[]) source;
        }
        if (source == null) {
            return new Object[0];
        }
        if (!source.getClass().isArray()) {
            throw new IllegalArgumentException("Source is not an array: " + source);
        }
        int length = Array.getLength(source);
        if (length == 0) {
            return new Object[0];
        }
        Class<?> wrapperType = Array.get(source, 0).getClass();
        Object[] newArray = (Object[]) Array.newInstance(wrapperType, length);
        for (int i = 0; i < length; i++) {
            newArray[i] = Array.get(source, i);
        }
        return newArray;
    }

    /**
     * Retrieve simple data type data type.
     *
     * @param clazz the clazz
     * @return the data type
     */
    public static DataType retrieveSimpleDataType(final Class<?> clazz) {
        if (clazz == null) {
            return DataType.UNKNOWN;
        }

        if (clazz.equals(Character[].class) || clazz.equals(char[].class)) {
            return DataType.CDATA;
        } else if (clazz.equals(Byte[].class) || clazz.equals(byte[].class)) {
            return DataType.BINARY;
        } else if (clazz.equals(Boolean.class) || clazz.equals(boolean.class)) {
            return DataType.BOOLEAN;
        } else if (clazz.equals(Date.class)) {
            return DataType.DATE;
        } else if (clazz.equals(Integer.class) || clazz.equals(int.class)
                || clazz.equals(Float.class) || clazz.equals(float.class)
                || clazz.equals(Double.class) || clazz.equals(double.class)
                || clazz.equals(Short.class) || clazz.equals(short.class)
                || clazz.equals(Long.class) || clazz.equals(long.class)
                || clazz.equals(byte.class) || clazz.equals(BigInteger.class)
                || clazz.equals(BigDecimal.class) || clazz.equals(Number.class)) {
            return DataType.NUMBER;
        } else if (clazz.equals(String.class)) {
            return DataType.STRING;
        } else if (BeanObject.class.isAssignableFrom(clazz)
                && (clazz.isAnnotationPresent(XmlType.class) || clazz.isAnnotationPresent(XmlRootElement.class))) {
            return DataType.OBJECT;
        } else if (clazz.isEnum()) {
            return DataType.ENUM;
        } else {
            return DataType.UNKNOWN;
        }
    }

    //---------------------------------------------------------------------
    // Convenience methods for content-based equality/hash-code handling
    //---------------------------------------------------------------------

    /**
     * Determine if the given objects are equal, returning <code>Boolean.TRUE</code>
     * if both are <code>null</code> or <code>Boolean.FALSE</code> if only one is
     * <code>null</code>.
     * <p>Compares arrays with <code>Arrays.equals</code>, performing an equality
     * check based on the array elements rather than the array reference.
     *
     * @param o1 first Object to compare
     * @param o2 second Object to compare
     * @return whether the given objects are equal
     * @see java.util.Arrays#equals java.util.Arrays#equals
     */
    public static boolean nullSafeEquals(final Object o1, final Object o2) {
        if (o1 == o2) {
            return Boolean.TRUE;
        }
        if (o1 == null || o2 == null) {
            return Boolean.FALSE;
        }
        if (o1.equals(o2)) {
            return Boolean.TRUE;
        }
        if (o1.getClass().isArray() && o2.getClass().isArray()) {
            if (o1 instanceof Object[] && o2 instanceof Object[]) {
                return Arrays.equals((Object[]) o1, (Object[]) o2);
            }
            if (o1 instanceof boolean[] && o2 instanceof boolean[]) {
                return Arrays.equals((boolean[]) o1, (boolean[]) o2);
            }
            if (o1 instanceof byte[] && o2 instanceof byte[]) {
                return Arrays.equals((byte[]) o1, (byte[]) o2);
            }
            if (o1 instanceof char[] && o2 instanceof char[]) {
                return Arrays.equals((char[]) o1, (char[]) o2);
            }
            if (o1 instanceof double[] && o2 instanceof double[]) {
                return Arrays.equals((double[]) o1, (double[]) o2);
            }
            if (o1 instanceof float[] && o2 instanceof float[]) {
                return Arrays.equals((float[]) o1, (float[]) o2);
            }
            if (o1 instanceof int[] && o2 instanceof int[]) {
                return Arrays.equals((int[]) o1, (int[]) o2);
            }
            if (o1 instanceof long[] && o2 instanceof long[]) {
                return Arrays.equals((long[]) o1, (long[]) o2);
            }
            if (o1 instanceof short[] && o2 instanceof short[]) {
                return Arrays.equals((short[]) o1, (short[]) o2);
            }
        }
        return Boolean.FALSE;
    }

    /**
     * Return as hash code for the given object; typically the value of
     * <code>{@link Object#hashCode()}</code>.
     * If the object is an array, this method will delegate to any of the <code>nullSafeHashCode</code>
     * methods for arrays in this class.
     * If the object is <code>null</code>, this method returns 0.
     *
     * @param obj check object
     * @return object hash code
     * @see #nullSafeHashCode(Object[]) #nullSafeHashCode(Object[])
     * @see #nullSafeHashCode(boolean[]) #nullSafeHashCode(boolean[])
     * @see #nullSafeHashCode(byte[]) #nullSafeHashCode(byte[])
     * @see #nullSafeHashCode(char[]) #nullSafeHashCode(char[])
     * @see #nullSafeHashCode(double[]) #nullSafeHashCode(double[])
     * @see #nullSafeHashCode(float[]) #nullSafeHashCode(float[])
     * @see #nullSafeHashCode(int[]) #nullSafeHashCode(int[])
     * @see #nullSafeHashCode(long[]) #nullSafeHashCode(long[])
     * @see #nullSafeHashCode(short[]) #nullSafeHashCode(short[])
     */
    public static int nullSafeHashCode(final Object obj) {
        if (obj == null) {
            return 0;
        }
        if (obj.getClass().isArray()) {
            if (obj instanceof Object[]) {
                return nullSafeHashCode((Object[]) obj);
            }
            if (obj instanceof boolean[]) {
                return nullSafeHashCode((boolean[]) obj);
            }
            if (obj instanceof byte[]) {
                return nullSafeHashCode((byte[]) obj);
            }
            if (obj instanceof char[]) {
                return nullSafeHashCode((char[]) obj);
            }
            if (obj instanceof double[]) {
                return nullSafeHashCode((double[]) obj);
            }
            if (obj instanceof float[]) {
                return nullSafeHashCode((float[]) obj);
            }
            if (obj instanceof int[]) {
                return nullSafeHashCode((int[]) obj);
            }
            if (obj instanceof long[]) {
                return nullSafeHashCode((long[]) obj);
            }
            if (obj instanceof short[]) {
                return nullSafeHashCode((short[]) obj);
            }
        }
        return obj.hashCode();
    }

    /**
     * Return a hash code based on the contents of the specified array.
     * If <code>array</code> is <code>null</code>, this method returns 0.
     *
     * @param array specified array
     * @return hash code result
     */
    public static int nullSafeHashCode(final Object[] array) {
        if (array == null) {
            return 0;
        }
        int hash = Globals.INITIAL_HASH;
//		int arraySize = array.length;
        for (Object anArray : array) {
            hash = Globals.MULTIPLIER * hash + nullSafeHashCode(anArray);
        }
        return hash;
    }

    /**
     * Return a hash code based on the contents of the specified array.
     * If <code>array</code> is <code>null</code>, this method returns 0.
     *
     * @param array specified array
     * @return hash code result
     */
    public static int nullSafeHashCode(final boolean[] array) {
        if (array == null) {
            return 0;
        }
        int hash = Globals.INITIAL_HASH;
        for (boolean bool : array) {
            hash = Globals.MULTIPLIER * hash + hashCode(bool);
        }
        return hash;
    }

    /**
     * Return a hash code based on the contents of the specified array.
     * If <code>array</code> is <code>null</code>, this method returns 0.
     *
     * @param array specified array
     * @return hash code result
     */
    public static int nullSafeHashCode(final byte[] array) {
        if (array == null) {
            return 0;
        }
        int hash = Globals.INITIAL_HASH;
        for (byte b : array) {
            hash = Globals.MULTIPLIER * hash + b;
        }
        return hash;
    }

    /**
     * Return a hash code based on the contents of the specified array.
     * If <code>array</code> is <code>null</code>, this method returns 0.
     *
     * @param array specified array
     * @return hash code result
     */
    public static int nullSafeHashCode(final char[] array) {
        if (array == null) {
            return 0;
        }
        int hash = Globals.INITIAL_HASH;
        for (char c : array) {
            hash = Globals.MULTIPLIER * hash + c;
        }
        return hash;
    }

    /**
     * Return a hash code based on the contents of the specified array.
     * If <code>array</code> is <code>null</code>, this method returns 0.
     *
     * @param array specified array
     * @return hash code result
     */
    public static int nullSafeHashCode(final double[] array) {
        if (array == null) {
            return 0;
        }
        int hash = Globals.INITIAL_HASH;
        for (double d : array) {
            hash = Globals.MULTIPLIER * hash + hashCode(d);
        }
        return hash;
    }

    /**
     * Return a hash code based on the contents of the specified array.
     * If <code>array</code> is <code>null</code>, this method returns 0.
     *
     * @param array specified array
     * @return hash code result
     */
    public static int nullSafeHashCode(final float[] array) {
        if (array == null) {
            return 0;
        }
        int hash = Globals.INITIAL_HASH;
        for (float f : array) {
            hash = Globals.MULTIPLIER * hash + hashCode(f);
        }
        return hash;
    }

    /**
     * Return a hash code based on the contents of the specified array.
     * If <code>array</code> is <code>null</code>, this method returns 0.
     *
     * @param array specified array
     * @return hash code result
     */
    public static int nullSafeHashCode(final int[] array) {
        if (array == null) {
            return 0;
        }
        int hash = Globals.INITIAL_HASH;
        for (int i : array) {
            hash = Globals.MULTIPLIER * hash + i;
        }
        return hash;
    }

    /**
     * Return a hash code based on the contents of the specified array.
     * If <code>array</code> is <code>null</code>, this method returns 0.
     *
     * @param array specified array
     * @return hash code result
     */
    public static int nullSafeHashCode(final long[] array) {
        if (array == null) {
            return 0;
        }
        int hash = Globals.INITIAL_HASH;
        for (long l : array) {
            hash = Globals.MULTIPLIER * hash + hashCode(l);
        }
        return hash;
    }

    /**
     * Return a hash code based on the contents of the specified array.
     * If <code>array</code> is <code>null</code>, this method returns 0.
     *
     * @param array specified array
     * @return hash code result
     */
    public static int nullSafeHashCode(final short[] array) {
        if (array == null) {
            return 0;
        }
        int hash = Globals.INITIAL_HASH;
        for (short s : array) {
            hash = Globals.MULTIPLIER * hash + s;
        }
        return hash;
    }

    /**
     * Return the same value as <code>{@link Boolean#hashCode()}</code>.
     *
     * @param bool boolean value
     * @return hash code result
     * @see Boolean#hashCode() Boolean#hashCode()
     */
    public static int hashCode(final boolean bool) {
        return bool ? 1231 : 1237;
    }

    /**
     * Return the same value as <code>{@link Double#hashCode()}</code>.
     *
     * @param dbl double value
     * @return hash code result
     * @see Double#hashCode() Double#hashCode()
     */
    public static int hashCode(final double dbl) {
        return hashCode(Double.doubleToLongBits(dbl));
    }

    /**
     * Return the same value as <code>{@link Float#hashCode()}</code>.
     *
     * @param flt float value
     * @return hash code result
     * @see Float#hashCode() Float#hashCode()
     */
    public static int hashCode(final float flt) {
        return Float.floatToIntBits(flt);
    }

    /**
     * Return the same value as <code>{@link Long#hashCode()}</code>.
     *
     * @param lng long value
     * @return hash code result
     * @see Long#hashCode() Long#hashCode()
     */
    public static int hashCode(final long lng) {
        return (int) (lng ^ (lng >>> 32));
    }


    //---------------------------------------------------------------------
    // Convenience methods for toString output
    //---------------------------------------------------------------------

    /**
     * Determine the class name for the given object.
     * <p>Returns <code>""</code> if <code>obj</code> is <code>null</code>.
     *
     * @param obj the object to introspect (maybe <code>null</code>)
     * @return the corresponding class name
     */
    public static String nullSafeClassName(final Object obj) {
        return obj != null ? nullSafeClassName(obj.getClass()) : Globals.DEFAULT_VALUE_STRING;
    }

    /**
     * Determine the class name for the given object.
     * <p>Returns <code>""</code> if <code>obj</code> is <code>null</code>.
     *
     * @param clazz the object to introspect (maybe <code>null</code>)
     * @return the corresponding class name
     */
    public static String nullSafeClassName(final Class<?> clazz) {
        return (clazz != null ? clazz.getName() : Globals.DEFAULT_VALUE_STRING);
    }

    /**
     * Return a String representation of the specified Object.
     * <p>Builds a String representation of the contents in case of an array.
     * Returns <code>""</code> if <code>obj</code> is <code>null</code>.
     *
     * @param obj the object to build a String representation for
     * @return a String representation of <code>obj</code>
     */
    public static String nullSafeToString(final Object obj) {
        if (obj == null) {
            return Globals.DEFAULT_VALUE_STRING;
        }
        if (obj instanceof String) {
            return (String) obj;
        }
        if (ClassUtils.isPrimitiveArray(obj.getClass())) {
            if (obj instanceof boolean[]) {
                return nullSafeToString((boolean[]) obj);
            }
            if (obj instanceof byte[]) {
                return nullSafeToString((byte[]) obj);
            }
            if (obj instanceof char[]) {
                return nullSafeToString((char[]) obj);
            }
            if (obj instanceof double[]) {
                return nullSafeToString((double[]) obj);
            }
            if (obj instanceof float[]) {
                return nullSafeToString((float[]) obj);
            }
            if (obj instanceof int[]) {
                return nullSafeToString((int[]) obj);
            }
            if (obj instanceof long[]) {
                return nullSafeToString((long[]) obj);
            }
            if (obj instanceof short[]) {
                return nullSafeToString((short[]) obj);
            }
            return ARRAY_START + ARRAY_END;
        } else if (obj instanceof Class<?>[]) {
            return nullSafeToString((Class<?>[]) obj);
        } else if (obj.getClass().isArray()) {
            return nullSafeToString(toObjectArray(obj));
        } else if (obj.getClass().isPrimitive()) {
            return Optional.ofNullable(ClassUtils.primitiveWrapper(obj.getClass()))
                    .map(wrapperClass -> ClassUtils.findMethod(wrapperClass, "toString", new Class[]{obj.getClass()}))
                    .map(method -> (String) ReflectionUtils.invokeMethod(method, null, new Object[]{obj}))
                    .orElse(Globals.DEFAULT_VALUE_STRING);
        } else {
            return obj.toString();
        }
    }

    /**
     * Return a String representation of the contents of the specified array.
     * <p>The String representation consists of a list of the array's elements,
     * enclosed in curly braces (<code>"{}"</code>).
     * Adjacent elements are separated by the characters <code>", "</code> (a comma followed by a space).
     * Returns <code>""</code> if <code>array</code> is <code>null</code>.
     *
     * @param array the array to build a String representation for
     * @return a String representation of <code>array</code>
     */
    public static String nullSafeToString(final Object[] array) {
        if (array == null || array.length == 0) {
            return Globals.DEFAULT_VALUE_STRING;
        }
        StringBuilder stringBuilder = new StringBuilder();
        Arrays.asList(array).forEach(object -> stringBuilder.append(ARRAY_ELEMENT_SEPARATOR).append(object));
        return stringBuilderCompletion(stringBuilder);
    }

    /**
     * Return a String representation of the contents of the specified array.
     * <p>The String representation consists of a list of the array's elements,
     * enclosed in curly braces (<code>"{}"</code>).
     * Adjacent elements are separated by the characters <code>", "</code> (a comma followed by a space).
     * Returns <code>""</code> if <code>array</code> is <code>null</code>.
     *
     * @param array the array to build a String representation for
     * @return a String representation of <code>array</code>
     */
    public static String nullSafeToString(final Class<?>[] array) {
        if (array == null) {
            return Globals.DEFAULT_VALUE_STRING;
        }
        if (array.length == 0) {
            return ARRAY_START + ARRAY_END;
        }
        StringBuilder stringBuilder = new StringBuilder();
        Arrays.asList(array).forEach(clazz -> stringBuilder.append(ARRAY_ELEMENT_SEPARATOR).append(clazz.getName()));
        return stringBuilderCompletion(stringBuilder);
    }

    /**
     * Return a String representation of the contents of the specified array.
     * <p>The String representation consists of a list of the array's elements,
     * enclosed in curly braces (<code>"{}"</code>).
     * Adjacent elements are separated by the characters <code>", "</code> (a comma followed by a space).
     * Returns <code>""</code> if <code>array</code> is <code>null</code>.
     *
     * @param array the array to build a String representation for
     * @return a String representation of <code>array</code>
     */
    public static String nullSafeToString(final boolean[] array) {
        if (array == null) {
            return Globals.DEFAULT_VALUE_STRING;
        }
        if (array.length == 0) {
            return ARRAY_START + ARRAY_END;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (boolean bool : array) {
            stringBuilder.append(ARRAY_ELEMENT_SEPARATOR).append(bool);
        }
        return stringBuilderCompletion(stringBuilder);
    }

    /**
     * Return a String representation of the contents of the specified array.
     * <p>The String representation consists of a list of the array's elements,
     * enclosed in curly braces (<code>"{}"</code>).
     * Adjacent elements are separated by the characters <code>", "</code> (a comma followed by a space).
     * Returns <code>""</code> if <code>array</code> is <code>null</code>.
     *
     * @param array the array to build a String representation for
     * @return a String representation of <code>array</code>
     */
    public static String nullSafeToString(final byte[] array) {
        if (array == null) {
            return Globals.DEFAULT_VALUE_STRING;
        }
        if (array.length == 0) {
            return ARRAY_START + ARRAY_END;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : array) {
            stringBuilder.append(ARRAY_ELEMENT_SEPARATOR).append(b);
        }
        return stringBuilderCompletion(stringBuilder);
    }

    /**
     * Return a String representation of the contents of the specified array.
     * <p>The String representation consists of a list of the array's elements,
     * enclosed in curly braces (<code>"{}"</code>).
     * Adjacent elements are separated by the characters <code>", "</code> (a comma followed by a space).
     * Returns <code>""</code> if <code>array</code> is <code>null</code>.
     *
     * @param array the array to build a String representation for
     * @return a String representation of <code>array</code>
     */
    public static String nullSafeToString(final char[] array) {
        if (array == null) {
            return Globals.DEFAULT_VALUE_STRING;
        }
        if (array.length == 0) {
            return ARRAY_START + ARRAY_END;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (char ch : array) {
            stringBuilder.append(ARRAY_ELEMENT_SEPARATOR).append(ch);
        }
        return stringBuilderCompletion(stringBuilder);
    }

    /**
     * Return a String representation of the contents of the specified array.
     * <p>The String representation consists of a list of the array's elements,
     * enclosed in curly braces (<code>"{}"</code>).
     * Adjacent elements are separated by the characters <code>", "</code> (a comma followed by a space).
     * Returns <code>""</code> if <code>array</code> is <code>null</code>.
     *
     * @param array the array to build a String representation for
     * @return a String representation of <code>array</code>
     */
    public static String nullSafeToString(final double[] array) {
        if (array == null) {
            return Globals.DEFAULT_VALUE_STRING;
        }
        if (array.length == 0) {
            return ARRAY_START + ARRAY_END;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (double d : array) {
            stringBuilder.append(ARRAY_ELEMENT_SEPARATOR).append(d);
        }
        return stringBuilderCompletion(stringBuilder);
    }

    /**
     * Return a String representation of the contents of the specified array.
     * <p>The String representation consists of a list of the array's elements,
     * enclosed in curly braces (<code>"{}"</code>).
     * Adjacent elements are separated by the characters <code>", "</code> (a comma followed by a space).
     * Returns <code>""</code> if <code>array</code> is <code>null</code>.
     *
     * @param array the array to build a String representation for
     * @return a String representation of <code>array</code>
     */
    public static String nullSafeToString(final float[] array) {
        if (array == null) {
            return Globals.DEFAULT_VALUE_STRING;
        }
        if (array.length == 0) {
            return ARRAY_START + ARRAY_END;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (float f : array) {
            stringBuilder.append(ARRAY_ELEMENT_SEPARATOR).append(f);
        }
        return stringBuilderCompletion(stringBuilder);
    }

    /**
     * Return a String representation of the contents of the specified array.
     * <p>The String representation consists of a list of the array's elements,
     * enclosed in curly braces (<code>"{}"</code>).
     * Adjacent elements are separated by the characters <code>", "</code> (a comma followed by a space).
     * Returns <code>""</code> if <code>array</code> is <code>null</code>.
     *
     * @param array the array to build a String representation for
     * @return a String representation of <code>array</code>
     */
    public static String nullSafeToString(final int[] array) {
        if (array == null) {
            return Globals.DEFAULT_VALUE_STRING;
        }
        if (array.length == 0) {
            return ARRAY_START + ARRAY_END;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i : array) {
            stringBuilder.append(ARRAY_ELEMENT_SEPARATOR).append(i);
        }
        return stringBuilderCompletion(stringBuilder);
    }

    /**
     * Return a String representation of the contents of the specified array.
     * <p>The String representation consists of a list of the array's elements,
     * enclosed in curly braces (<code>"{}"</code>).
     * Adjacent elements are separated by the characters <code>", "</code> (a comma followed by a space).
     * Returns <code>""</code> if <code>array</code> is <code>null</code>.
     *
     * @param array the array to build a String representation for
     * @return a String representation of <code>array</code>
     */
    public static String nullSafeToString(final long[] array) {
        if (array == null) {
            return Globals.DEFAULT_VALUE_STRING;
        }
        if (array.length == 0) {
            return ARRAY_START + ARRAY_END;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (long l : array) {
            stringBuilder.append(ARRAY_ELEMENT_SEPARATOR).append(l);
        }
        return stringBuilderCompletion(stringBuilder);
    }

    /**
     * Return a String representation of the contents of the specified array.
     * <p>The String representation consists of a list of the array's elements,
     * enclosed in curly braces (<code>"{}"</code>).
     * Adjacent elements are separated by the characters <code>", "</code> (a comma followed by a space).
     * Returns <code>""</code> if <code>array</code> is <code>null</code>.
     *
     * @param array the array to build a String representation for
     * @return a String representation of <code>array</code>
     */
    public static String nullSafeToString(final short[] array) {
        if (array == null) {
            return Globals.DEFAULT_VALUE_STRING;
        }
        if (array.length == 0) {
            return ARRAY_START + ARRAY_END;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (short s : array) {
            stringBuilder.append(ARRAY_ELEMENT_SEPARATOR).append(s);
        }
        return stringBuilderCompletion(stringBuilder);
    }

    private static String stringBuilderCompletion(final StringBuilder stringBuilder) {
        stringBuilder.insert(ARRAY_ELEMENT_SEPARATOR.length(), ARRAY_START).append(ARRAY_END);
        return stringBuilder.substring(ARRAY_ELEMENT_SEPARATOR.length());
    }
}
