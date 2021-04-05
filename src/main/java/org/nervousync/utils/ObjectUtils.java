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

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.nervousync.commons.beans.core.BeanObject;
import org.nervousync.commons.core.Globals;
import org.nervousync.enumerations.xml.DataType;
import org.nervousync.interceptor.beans.HandlerInterceptor;

import net.sf.cglib.proxy.Enhancer;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Jan 13, 2010 4:26:58 PM $
 */
public final class ObjectUtils {

	private static final String EMPTY_STRING = "";
	private static final String NULL_STRING = "null";
	private static final String ARRAY_START = "{";
	private static final String ARRAY_END = "}";
	private static final String EMPTY_ARRAY = ARRAY_START + ARRAY_END;
	private static final String ARRAY_ELEMENT_SEPARATOR = ", ";
	
	private ObjectUtils() {
		
	}

	/**
	 * Create a proxy object instance
	 * @param className		class name
	 * @return				object instance
	 * @throws ClassNotFoundException	if class was not found
	 * @throws LinkageError				if class link error
	 */
	public static Object newInstance(String className) 
			throws ClassNotFoundException, LinkageError {
		return newInstance(className, null, null, null);
	}

	/**
	 * Create a proxy object instance
	 * @param className		class name
	 * @param paramClasses  parameter class array
	 * @param args			Constructor parameters
	 * @return				object instance
	 * @throws ClassNotFoundException	if class was not found
	 * @throws LinkageError				if class link error
	 */
	public static Object newInstance(String className, Class<?>[] paramClasses, Object[] args)
			throws ClassNotFoundException, LinkageError {
		return newInstance(className, paramClasses, args, null);
	}

	/**
	 * Create a proxy object instance
	 * @param className		class name
	 * @param paramClasses  parameter class array
	 * @param args			Constructor parameters
	 * @param methodInterceptor method interceptor instance
	 * @return				object instance
	 * @throws ClassNotFoundException	if class was not found
	 * @throws LinkageError				if class link error
	 */
	public static Object newInstance(String className, Class<?>[] paramClasses, Object[] args, HandlerInterceptor methodInterceptor)
			throws ClassNotFoundException, LinkageError {
		HandlerInterceptor[] methodInterceptors = null;
		if (methodInterceptor != null) {
			methodInterceptors = new HandlerInterceptor[]{methodInterceptor};
		}
		return createProxyInstance(ClassUtils.forName(className), paramClasses, args, methodInterceptors);
	}

	/**
	 * Create a proxy object instance
	 * @param clazz		define class
	 * @param <T>		T
	 * @return			object instance
	 */
	public static <T> T newInstance(Class<T> clazz) {
		return createProxyInstance(clazz, null, null, new HandlerInterceptor[]{});
	}

	/**
	 * Create a proxy object instance
	 * @param clazz		define class
	 * @param handlerInterceptors method interceptor instance array
	 * @param <T>		T
	 * @return			object instance
	 */
	public static <T> T createProxyInstance(Class<T> clazz, HandlerInterceptor... handlerInterceptors) {
		return createProxyInstance(clazz, null, null, handlerInterceptors);
	}

	/**
	 * Create a proxy object instance
	 * @param clazz		define class
	 * @param paramClasses  parameter class array
	 * @param args		Constructor parameters
	 * @param methodInterceptor method interceptor instance
	 * @param <T>		T
	 * @return			object instance
	 */
	public static <T> T createProxyInstance(Class<T> clazz, Class<?>[] paramClasses, Object[] args, HandlerInterceptor methodInterceptor) {
		HandlerInterceptor[] methodInterceptors = null;
		if (methodInterceptor != null) {
			methodInterceptors = new HandlerInterceptor[]{methodInterceptor};
		}
		return createProxyInstance(clazz, paramClasses, args, methodInterceptors);
	}

	/**
	 * Create a proxy object instance
	 * @param clazz		define class
	 * @param paramClasses  parameter class array
	 * @param args		Constructor parameters
	 * @param methodInterceptors  method interceptor instance arrays
	 * @param <T>		T
	 * @return			object instance
	 */
	public static <T> T createProxyInstance(Class<T> clazz, Class<?>[] paramClasses, Object[] args, HandlerInterceptor[] methodInterceptors) {
		Object object;

		if (methodInterceptors != null && methodInterceptors.length > 0) {
			Enhancer enhancer = new Enhancer();
			enhancer.setSuperclass(clazz);

			if (methodInterceptors.length == 1) {
				enhancer.setCallback(methodInterceptors[0]);
			} else {
				enhancer.setCallbacks(methodInterceptors);
			}

			if (args == null || args.length == 0) {
				object = enhancer.create();
			} else {
				object = enhancer.create(paramClasses, args);
			}
		} else {
			try {
				if (args == null || args.length == 0) {
					object = clazz.getDeclaredConstructor().newInstance();
				} else {
					object = clazz.getDeclaredConstructor(paramClasses).newInstance(args);
				}
			} catch (SecurityException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
				object = null;
			}
		}

		if (clazz.isInstance(object)) {
			return clazz.cast(object);
		}
		return null;
	}

	/**
	 * Return whether the given throwable is a checked exception:
	 * that is, neither a RuntimeException nor an Error.
	 * @param ex the throwable to check
	 * @return whether the throwable is a checked exception
	 * @see java.lang.Exception
	 * @see java.lang.RuntimeException
	 * @see java.lang.Error
	 */
	public static boolean isCheckedException(Throwable ex) {
		return !(ex instanceof RuntimeException || ex instanceof Error);
	}

	/**
	 * Check whether the given exception is compatible with the exceptions
	 * declared in a throws clause.
	 * @param ex the exception to checked
	 * @param declaredExceptions the exceptions declared in the throws clause
	 * @return whether the given exception is compatible
	 */
	public static boolean isCompatibleWithThrowsClause(Throwable ex, Class<?>[] declaredExceptions) {
		if (!isCheckedException(ex)) {
			return true;
		}
		if (declaredExceptions != null) {
			for (Class<?> declaredException : declaredExceptions) {
				if (declaredException.isAssignableFrom(ex.getClass())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Return whether the given array is empty: that is, <code>null</code>
	 * or of zero length.
	 * @param array the array to check
	 * @return whether the given array is empty
	 */
	public static boolean isEmpty(Object[] array) {
		return (array == null || array.length == 0);
	}

	/**
	 * Return whether the given array is empty: that is, <code>null</code>
	 * or of zero length.
	 * @param object the object to check
	 * @return whether the given array is empty
	 */
	public static boolean isNull(Object object) {
		if (object == null) {
			return true;
		}
		
		if (object.getClass().isArray()) {
			return (Array.getLength(object) == 0);
		} else {
			if (object instanceof String) {
				return (((String)object).length() == 0);
			}
		}
		
		return false;
	}

	/**
	 * Check whether the given array contains the given element.
	 * @param array the array to check (may be <code>null</code>,
	 * in which case the return value will always be <code>false</code>)
	 * @param element the element to check for
	 * @return whether the element has been found in the given array
	 */
	public static boolean containsElement(Object[] array, Object element) {
		if (array == null) {
			return false;
		}
		for (Object anArray : array) {
			if (nullSafeEquals(anArray, element)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Append the given Object to the given array, returning a new array
	 * consisting of the input array contents plus the given Object.
	 * @param array the array to append to (can be <code>null</code>)
	 * @param obj the Object to append
	 * @return the new array (of the same component type; never <code>null</code>)
	 */
	public static Object[] addObjectToArray(Object[] array, Object obj) {
		Class<?> compType = Object.class;
		if (array != null) {
			compType = array.getClass().getComponentType();
		}
		else if (obj != null) {
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
	 * @param source the (potentially primitive) array
	 * @return the corresponding object array (never <code>null</code>)
	 * @throws IllegalArgumentException if the parameter is not an array
	 */
	public static Object[] toObjectArray(Object source) {
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

	public static DataType retrieveSimpleDataType(Class<?> clazz) {
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
	 * Determine if the given objects are equal, returning <code>true</code>
	 * if both are <code>null</code> or <code>false</code> if only one is
	 * <code>null</code>.
	 * <p>Compares arrays with <code>Arrays.equals</code>, performing an equality
	 * check based on the array elements rather than the array reference.
	 * @param o1 first Object to compare
	 * @param o2 second Object to compare
	 * @return whether the given objects are equal
	 * @see java.util.Arrays#equals
	 */
	public static boolean nullSafeEquals(Object o1, Object o2) {
		if (o1 == o2) {
			return true;
		}
		if (o1 == null || o2 == null) {
			return false;
		}
		if (o1.equals(o2)) {
			return true;
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
		return false;
	}

	/**
	 * Return as hash code for the given object; typically the value of
	 * <code>{@link Object#hashCode()}</code>. If the object is an array,
	 * this method will delegate to any of the <code>nullSafeHashCode</code>
	 * methods for arrays in this class. If the object is <code>null</code>,
	 * this method returns 0.
	 * @param obj		check object
	 * @see #nullSafeHashCode(Object[])
	 * @see #nullSafeHashCode(boolean[])
	 * @see #nullSafeHashCode(byte[])
	 * @see #nullSafeHashCode(char[])
	 * @see #nullSafeHashCode(double[])
	 * @see #nullSafeHashCode(float[])
	 * @see #nullSafeHashCode(int[])
	 * @see #nullSafeHashCode(long[])
	 * @see #nullSafeHashCode(short[])
	 * @return object hash code
	 */
	public static int nullSafeHashCode(Object obj) {
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
	 * @param array	  specified array
	 * @return 	hash code result
	 */
	public static int nullSafeHashCode(Object[] array) {
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
	 * @param array	  specified array
	 * @return 	hash code result
	 */
	public static int nullSafeHashCode(boolean[] array) {
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
	 * @param array	  specified array
	 * @return 	hash code result
	 */
	public static int nullSafeHashCode(byte[] array) {
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
	 * @param array	  specified array
	 * @return 	hash code result
	 */
	public static int nullSafeHashCode(char[] array) {
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
	 * @param array	  specified array
	 * @return 	hash code result
	 */
	public static int nullSafeHashCode(double[] array) {
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
	 * @param array	  specified array
	 * @return 	hash code result
	 */
	public static int nullSafeHashCode(float[] array) {
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
	 * @param array	  specified array
	 * @return 	hash code result
	 */
	public static int nullSafeHashCode(int[] array) {
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
	 * @param array	  specified array
	 * @return 	hash code result
	 */
	public static int nullSafeHashCode(long[] array) {
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
	 * @param array	  specified array
	 * @return 	hash code result
	 */
	public static int nullSafeHashCode(short[] array) {
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
	 * @see Boolean#hashCode()
	 * @param bool	  boolean value
	 * @return 	hash code result
	 */
	public static int hashCode(boolean bool) {
		return bool ? 1231 : 1237;
	}

	/**
	 * Return the same value as <code>{@link Double#hashCode()}</code>.
	 * @see Double#hashCode()
	 * @param dbl	  double value
	 * @return 	hash code result
	 */
	public static int hashCode(double dbl) {
		return hashCode(Double.doubleToLongBits(dbl));
	}

	/**
	 * Return the same value as <code>{@link Float#hashCode()}</code>.
	 * @see Float#hashCode()
	 * @param flt	  float value
	 * @return 	hash code result
	 */
	public static int hashCode(float flt) {
		return Float.floatToIntBits(flt);
	}

	/**
	 * Return the same value as <code>{@link Long#hashCode()}</code>.
	 * @see Long#hashCode()
	 * @param lng	  long value
	 * @return 	hash code result
	 */
	public static int hashCode(long lng) {
		return (int) (lng ^ (lng >>> 32));
	}


	//---------------------------------------------------------------------
	// Convenience methods for toString output
	//---------------------------------------------------------------------

	/**
	 * Return a String representation of an object's overall identity.
	 * @param obj the object (may be <code>null</code>)
	 * @return the object's identity as String representation,
	 * or an empty String if the object was <code>null</code>
	 */
	public static String identityToString(Object obj) {
		if (obj == null) {
			return EMPTY_STRING;
		}
		return obj.getClass().getName() + "@" + getIdentityHexString(obj);
	}

	/**
	 * Return a hex String form of an object's identity hash code.
	 * @param obj the object
	 * @return the object's identity code in hex notation
	 */
	public static String getIdentityHexString(Object obj) {
		return Integer.toHexString(System.identityHashCode(obj));
	}

	/**
	 * Return a content-based String representation if <code>obj</code> is
	 * not <code>null</code>; otherwise returns an empty String.
	 * <p>Differs from {@link #nullSafeToString(Object)} in that it returns
	 * an empty String rather than "null" for a <code>null</code> value.
	 * @param obj the object to build a display String for
	 * @return a display String representation of <code>obj</code>
	 * @see #nullSafeToString(Object)
	 */
	public static String getDisplayString(Object obj) {
		if (obj == null) {
			return EMPTY_STRING;
		}
		return nullSafeToString(obj);
	}

	/**
	 * Determine the class name for the given object.
	 * <p>Returns <code>"null"</code> if <code>obj</code> is <code>null</code>.
	 * @param obj the object to introspect (may be <code>null</code>)
	 * @return the corresponding class name
	 */
	public static String nullSafeClassName(Object obj) {
		return (obj != null ? obj.getClass().getName() : NULL_STRING);
	}

	/**
	 * Return a String representation of the specified Object.
	 * <p>Builds a String representation of the contents in case of an array.
	 * Returns <code>"null"</code> if <code>obj</code> is <code>null</code>.
	 * @param obj the object to build a String representation for
	 * @return a String representation of <code>obj</code>
	 */
	public static String nullSafeToString(Object obj) {
		if (obj == null) {
			return NULL_STRING;
		}
		if (obj instanceof String) {
			return (String) obj;
		}
		if (obj instanceof Object[]) {
			return nullSafeToString((Object[]) obj);
		}
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
		if (obj instanceof List) {
			StringBuilder StringBuilder = new StringBuilder();
			int count = ((List<?>)obj).size();
			String split = "";
			for (int i = 0 ; i < count ; i++) {
				Object object = ((List<?>)obj).get(i);
				StringBuilder.append(split).append(nullSafeToString(object));
				split = ",";
			}
			return StringBuilder.toString();
		}
		return obj.toString();
	}

	/**
	 * Return a String representation of the contents of the specified array.
	 * <p>The String representation consists of a list of the array's elements,
	 * enclosed in curly braces (<code>"{}"</code>). Adjacent elements are separated
	 * by the characters <code>", "</code> (a comma followed by a space). Returns
	 * <code>"null"</code> if <code>array</code> is <code>null</code>.
	 * @param array the array to build a String representation for
	 * @return a String representation of <code>array</code>
	 */
	public static String nullSafeToString(Object[] array) {
		if (array == null) {
			return NULL_STRING;
		}
		StringBuilder stringBuilder = new StringBuilder();
		for (Object object : array) {
			stringBuilder.append(ARRAY_ELEMENT_SEPARATOR);
			stringBuilder.append(object);
		}
		return ARRAY_START + (stringBuilder.length() > 0 ? stringBuilder.substring(1) : stringBuilder.toString()) + ARRAY_END;
	}

	/**
	 * Return a String representation of the contents of the specified array.
	 * <p>The String representation consists of a list of the array's elements,
	 * enclosed in curly braces (<code>"{}"</code>). Adjacent elements are separated
	 * by the characters <code>", "</code> (a comma followed by a space). Returns
	 * <code>"null"</code> if <code>array</code> is <code>null</code>.
	 * @param array the array to build a String representation for
	 * @return a String representation of <code>array</code>
	 */
	public static String nullSafeToString(boolean[] array) {
		if (array == null) {
			return NULL_STRING;
		}
		StringBuilder stringBuilder = new StringBuilder();
		for (boolean bool : array) {
			stringBuilder.append(ARRAY_ELEMENT_SEPARATOR);
			stringBuilder.append(bool);
		}
		return ARRAY_START + (stringBuilder.length() > 0 ? stringBuilder.substring(1) : stringBuilder.toString()) + ARRAY_END;
	}

	/**
	 * Return a String representation of the contents of the specified array.
	 * <p>The String representation consists of a list of the array's elements,
	 * enclosed in curly braces (<code>"{}"</code>). Adjacent elements are separated
	 * by the characters <code>", "</code> (a comma followed by a space). Returns
	 * <code>"null"</code> if <code>array</code> is <code>null</code>.
	 * @param array the array to build a String representation for
	 * @return a String representation of <code>array</code>
	 */
	public static String nullSafeToString(byte[] array) {
		if (array == null) {
			return NULL_STRING;
		}
		StringBuilder stringBuilder = new StringBuilder();
		for (byte b : array) {
			stringBuilder.append(ARRAY_ELEMENT_SEPARATOR);
			stringBuilder.append(b);
		}
		return ARRAY_START + (stringBuilder.length() > 0 ? stringBuilder.substring(1) : stringBuilder.toString()) + ARRAY_END;
	}

	/**
	 * Return a String representation of the contents of the specified array.
	 * <p>The String representation consists of a list of the array's elements,
	 * enclosed in curly braces (<code>"{}"</code>). Adjacent elements are separated
	 * by the characters <code>", "</code> (a comma followed by a space). Returns
	 * <code>"null"</code> if <code>array</code> is <code>null</code>.
	 * @param array the array to build a String representation for
	 * @return a String representation of <code>array</code>
	 */
	public static String nullSafeToString(char[] array) {
		if (array == null) {
			return NULL_STRING;
		}
		StringBuilder stringBuilder = new StringBuilder();
		for (char ch : array) {
			stringBuilder.append(ARRAY_ELEMENT_SEPARATOR);
			stringBuilder.append(ch);
		}
		return ARRAY_START + (stringBuilder.length() > 0 ? stringBuilder.substring(1) : stringBuilder.toString()) + ARRAY_END;
	}

	/**
	 * Return a String representation of the contents of the specified array.
	 * <p>The String representation consists of a list of the array's elements,
	 * enclosed in curly braces (<code>"{}"</code>). Adjacent elements are separated
	 * by the characters <code>", "</code> (a comma followed by a space). Returns
	 * <code>"null"</code> if <code>array</code> is <code>null</code>.
	 * @param array the array to build a String representation for
	 * @return a String representation of <code>array</code>
	 */
	public static String nullSafeToString(double[] array) {
		if (array == null) {
			return NULL_STRING;
		}
		StringBuilder stringBuilder = new StringBuilder();
		for (double d : array) {
			stringBuilder.append(ARRAY_ELEMENT_SEPARATOR);
			stringBuilder.append(d);
		}
		return ARRAY_START + (stringBuilder.length() > 0 ? stringBuilder.substring(1) : stringBuilder.toString()) + ARRAY_END;
	}

	/**
	 * Return a String representation of the contents of the specified array.
	 * <p>The String representation consists of a list of the array's elements,
	 * enclosed in curly braces (<code>"{}"</code>). Adjacent elements are separated
	 * by the characters <code>", "</code> (a comma followed by a space). Returns
	 * <code>"null"</code> if <code>array</code> is <code>null</code>.
	 * @param array the array to build a String representation for
	 * @return a String representation of <code>array</code>
	 */
	public static String nullSafeToString(float[] array) {
		if (array == null) {
			return NULL_STRING;
		}
		StringBuilder stringBuilder = new StringBuilder();
		for (float f : array) {
			stringBuilder.append(ARRAY_ELEMENT_SEPARATOR);
			stringBuilder.append(f);
		}
		return ARRAY_START + (stringBuilder.length() > 0 ? stringBuilder.substring(1) : stringBuilder.toString()) + ARRAY_END;
	}

	/**
	 * Return a String representation of the contents of the specified array.
	 * <p>The String representation consists of a list of the array's elements,
	 * enclosed in curly braces (<code>"{}"</code>). Adjacent elements are separated
	 * by the characters <code>", "</code> (a comma followed by a space). Returns
	 * <code>"null"</code> if <code>array</code> is <code>null</code>.
	 * @param array the array to build a String representation for
	 * @return a String representation of <code>array</code>
	 */
	public static String nullSafeToString(int[] array) {
		if (array == null) {
			return NULL_STRING;
		}
		StringBuilder stringBuilder = new StringBuilder();
		for (int i : array) {
			stringBuilder.append(ARRAY_ELEMENT_SEPARATOR);
			stringBuilder.append(i);
		}
		return ARRAY_START + (stringBuilder.length() > 0 ? stringBuilder.substring(1) : stringBuilder.toString()) + ARRAY_END;
	}

	/**
	 * Return a String representation of the contents of the specified array.
	 * <p>The String representation consists of a list of the array's elements,
	 * enclosed in curly braces (<code>"{}"</code>). Adjacent elements are separated
	 * by the characters <code>", "</code> (a comma followed by a space). Returns
	 * <code>"null"</code> if <code>array</code> is <code>null</code>.
	 * @param array the array to build a String representation for
	 * @return a String representation of <code>array</code>
	 */
	public static String nullSafeToString(long[] array) {
		if (array == null) {
			return NULL_STRING;
		}
		StringBuilder stringBuilder = new StringBuilder();
		for (long l : array) {
			stringBuilder.append(ARRAY_ELEMENT_SEPARATOR);
			stringBuilder.append(l);
		}
		return ARRAY_START + (stringBuilder.length() > 0 ? stringBuilder.substring(1) : stringBuilder.toString()) + ARRAY_END;
	}

	/**
	 * Return a String representation of the contents of the specified array.
	 * <p>The String representation consists of a list of the array's elements,
	 * enclosed in curly braces (<code>"{}"</code>). Adjacent elements are separated
	 * by the characters <code>", "</code> (a comma followed by a space). Returns
	 * <code>"null"</code> if <code>array</code> is <code>null</code>.
	 * @param array the array to build a String representation for
	 * @return a String representation of <code>array</code>
	 */
	public static String nullSafeToString(short[] array) {
		if (array == null) {
			return NULL_STRING;
		}
		StringBuilder stringBuilder = new StringBuilder();
		for (short s : array) {
			stringBuilder.append(ARRAY_ELEMENT_SEPARATOR);
			stringBuilder.append(s);
		}
		return ARRAY_START + (stringBuilder.length() > 0 ? stringBuilder.substring(1) : stringBuilder.toString()) + ARRAY_END;
	}
}
