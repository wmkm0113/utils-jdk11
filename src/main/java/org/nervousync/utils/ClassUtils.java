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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.Introspector;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.*;

/**
 * Miscellaneous class utility methods. Mainly for internal use within the
 * framework; consider Jakarta's Commons Lang for a more comprehensive suite
 * of class utilities.
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Jan 13, 2010 3:53:41 PM $
 */
public final class ClassUtils {

	/** Suffix for array class names: "[]" */
	private static final String ARRAY_SUFFIX = "[]";

	/** Prefix for internal array class names: "[L" */
	private static final String INTERNAL_ARRAY_PREFIX = "[L";

	/** The package separator character '.' */
	private static final char PACKAGE_SEPARATOR = '.';

	/** The inner class separator character '$' */
	private static final char INNER_CLASS_SEPARATOR = '$';

	/** The CGLIB class separator character "$$" */
	private static final String CGLIB_CLASS_SEPARATOR = "$$";

	/** The ".class" file suffix */
	public static final String CLASS_FILE_SUFFIX = ".class";
	
	/**
	 * Map with primitive wrapper type as key and corresponding primitive
	 * type as value, for example: Integer.class -> int.class.
	 */
	private static final Map<Object, Object> PRIMITIVE_WRAPPER_TYPE_MAP = new HashMap<>(8);

	/**
	 * Map with primitive type name as key and corresponding primitive
	 * type as value, for example: "int" -> "int.class".
	 */
	private static final Map<Object, Object> PRIMITIVE_TYPE_NAME_MAP = new HashMap<>(16);

	private static ClassLoader DEFAULT_CLASSLOADER = null;

	static {
		PRIMITIVE_WRAPPER_TYPE_MAP.put(Boolean.class, boolean.class);
		PRIMITIVE_WRAPPER_TYPE_MAP.put(Byte.class, byte.class);
		PRIMITIVE_WRAPPER_TYPE_MAP.put(Character.class, char.class);
		PRIMITIVE_WRAPPER_TYPE_MAP.put(Double.class, double.class);
		PRIMITIVE_WRAPPER_TYPE_MAP.put(Float.class, float.class);
		PRIMITIVE_WRAPPER_TYPE_MAP.put(Integer.class, int.class);
		PRIMITIVE_WRAPPER_TYPE_MAP.put(Long.class, long.class);
		PRIMITIVE_WRAPPER_TYPE_MAP.put(Short.class, short.class);

		Set<Object> primitiveTypeNames = new HashSet<>(16);
		primitiveTypeNames.addAll(PRIMITIVE_WRAPPER_TYPE_MAP.values());
		primitiveTypeNames.addAll(Arrays.asList(
				boolean[].class, byte[].class, char[].class, double[].class,
				float[].class, int[].class, long[].class, short[].class));
		primitiveTypeNames.forEach(primitiveClass ->
				PRIMITIVE_TYPE_NAME_MAP.put(((Class<?>)primitiveClass).getName(), primitiveClass));
	}
	
	private ClassUtils() {
	}
	
	/**
	 * Return the default ClassLoader to use: typically the thread context
	 * ClassLoader, if available; the ClassLoader that loaded the ClassUtils
	 * class will be used as fallback.
	 * <p>Call this method if you intend to use the thread context ClassLoader
	 * in a scenario where you absolutely need a non-null ClassLoader reference:
	 * for example, for class path resource loading (but not necessarily for
	 * <code>Class.forName</code>, which accepts a <code>null</code> ClassLoader
	 * reference as well).
	 * @return the default ClassLoader (never <code>null</code>)
	 * @see java.lang.Thread#getContextClassLoader()
	 */
	public static ClassLoader getDefaultClassLoader() {
		if (DEFAULT_CLASSLOADER != null) {
			return DEFAULT_CLASSLOADER;
		}
		
		ClassLoader cl = null;
		
		try {
			cl = Thread.currentThread().getContextClassLoader();
		} catch (Throwable ex) {
			// Cannot access thread context ClassLoader - falling back to system class loader...
		}
		
		if (cl == null) {
			// No thread context class loader -> use class loader of this class.
			cl = ClassUtils.class.getClassLoader();
		}
		
		return cl;
	}

	public static <T> Optional<T> defaultClassLoader(Class<T> clazz) {
		try {
			return Optional.of(clazz.cast(getDefaultClassLoader()));
		} catch (Exception e) {
			return Optional.empty();
		}
	}

	/**
	 * Override the thread context ClassLoader with the environment's bean ClassLoader
	 * if necessary, i.e. if the bean ClassLoader is not equivalent to the thread
	 * context ClassLoader already.
	 * @param classLoaderToUse the actual ClassLoader to use for the thread context
	 * @return the original thread context ClassLoader, or <code>null</code> if not overridden
	 */
	public static ClassLoader overrideThreadContextClassLoader(ClassLoader classLoaderToUse) {
		Thread currentThread = Thread.currentThread();
		ClassLoader threadContextClassLoader = currentThread.getContextClassLoader();
		if (classLoaderToUse != null && !classLoaderToUse.equals(threadContextClassLoader)) {
			currentThread.setContextClassLoader(classLoaderToUse);
			DEFAULT_CLASSLOADER = classLoaderToUse;
			return threadContextClassLoader;
		} else {
			return null;
		}
	}

	/**
	 * Determine whether the {@link Class} identified by the supplied name is present
	 * and can be loaded. Will return <code>false</code> if either the class or
	 * one of its dependencies is not present or cannot be loaded.
	 * @param className the name of the class to check
	 * @return whether the specified class is present
	 */
	public static boolean isPresent(String className) {
		return isPresent(className, getDefaultClassLoader());
	}

	/**
	 * Determine whether the {@link Class} identified by the supplied name is present
	 * and can be loaded. Will return <code>false</code> if either the class or
	 * one of its dependencies is not present or cannot be loaded.
	 * @param className the name of the class to check
	 * @param classLoader the class loader to use
	 * (maybe <code>null</code>, which indicates the default class loader)
	 * @return whether the specified class is present
	 */
	public static boolean isPresent(String className, ClassLoader classLoader) {
		try {
			forName(className, classLoader);
			return true;
		} catch (Throwable ex) {
			// Class or one of its dependencies is not present...
			return false;
		}
	}

	/**
	 * Replacement for <code>Class.forName()</code> that also returns Class instances
	 * for primitives (like "int") and array class names (like "String[]").
	 * <p>Always uses the default class loader: that is, preferably the thread context
	 * class loader, or the ClassLoader that loaded the ClassUtils class as fallback.
	 * @param name the name of the Class
	 * @return Class instance for the supplied name
	 * @throws ClassNotFoundException if the class was not found
	 * @throws LinkageError if the class file could not be loaded
	 * @see Class#forName(String, boolean, ClassLoader)
	 * @see #getDefaultClassLoader()
	 */
	public static Class<?> forName(String name) throws ClassNotFoundException, LinkageError {
		return forName(name, getDefaultClassLoader());
	}

	/**
	 * Replacement for <code>Class.forName()</code> that also returns Class instances
	 * for primitives (like "int") and array class names (like "String[]").
	 * @param name the name of the Class
	 * @param classLoader the class loader to use
	 * (maybe <code>null</code>, which indicates the default class loader)
	 * @return Class instance for the supplied name
	 * @throws ClassNotFoundException if the class was not found
	 * @throws LinkageError if the class file could not be loaded
	 * @see Class#forName(String, boolean, ClassLoader)
	 */
	public static Class<?> forName(String name, ClassLoader classLoader) throws ClassNotFoundException, LinkageError {
		if (name == null) {
			throw new IllegalArgumentException("Name must not be null");
		}

		Class<?> clazz = resolvePrimitiveClassName(name);
		if (clazz != null) {
			return clazz;
		}

		// "java.lang.String[]" style arrays
		if (name.endsWith(ARRAY_SUFFIX)) {
			String elementClassName = name.substring(0, name.length() - ARRAY_SUFFIX.length());
			Class<?> elementClass = forName(elementClassName, classLoader);
			return Array.newInstance(elementClass, 0).getClass();
		}

		int internalArrayMarker = name.indexOf(INTERNAL_ARRAY_PREFIX);
		if (internalArrayMarker != -1 && name.endsWith(";")) {
			String elementClassName = null;
			if (internalArrayMarker == 0) {
				elementClassName = name.substring(INTERNAL_ARRAY_PREFIX.length(), name.length() - 1);
			} else if (name.startsWith("[")) {
				elementClassName = name.substring(1);
			}
			Class<?> elementClass = forName(elementClassName, classLoader);
			return Array.newInstance(elementClass, 0).getClass();
		}

		ClassLoader classLoaderToUse = classLoader;
		if (classLoaderToUse == null) {
			classLoaderToUse = getDefaultClassLoader();
		}
		return classLoaderToUse.loadClass(name);
	}

	/**
	 * Resolve the given class name into a Class instance. Supports
	 * primitives (like "int") and array class names (like "String[]").
	 * <p>This is effectively equivalent to the <code>forName</code>
	 * method with the same arguments, with the only difference being
	 * the exceptions thrown in case of class loading failure.
	 * @param className the name of the Class
	 * @param classLoader the class loader to use
	 * (maybe <code>null</code>, which indicates the default class loader)
	 * @return Class instance for the supplied name
	 * @throws IllegalArgumentException if the class name was not resolvable
	 * (that is, the class could not be found or the class file could not be loaded)
	 * @see #forName(String, ClassLoader)
	 */
	public static Class<?> resolveClassName(String className, ClassLoader classLoader) throws IllegalArgumentException {
		try {
			return forName(className, classLoader);
		} catch (ClassNotFoundException ex) {
			throw new IllegalArgumentException("Cannot find class [" + className + "]", ex);
		} catch (LinkageError ex) {
			throw new IllegalArgumentException(
					"Error loading class [" + className + "]: problem with class file or dependent class.", ex);
		}
	}

	/**
	 * Resolve the given class name as primitive class, if appropriate,
	 * according to the JVM's naming rules for primitive classes.
	 * <p>Also supports the JVM's internal class names for primitive arrays.
	 * Does <i>not</i> support the "[]" suffix notation for primitive arrays;
	 * this is only supported by {@link #forName}.
	 * @param name the name of the potentially primitive class
	 * @return the primitive class, or <code>null</code> if the name does not denote
	 * a primitive class or primitive array class
	 */
	public static Class<?> resolvePrimitiveClassName(String name) {
		Class<?> result = null;
		// Most class names will be quite long, considering that they
		// SHOULD sit in a package, so a length check is worthwhile.
		if (name != null && name.length() <= 8) {
			// Could be a primitive - likely.
			result = (Class<?>)PRIMITIVE_TYPE_NAME_MAP.get(name);
		}
		return result;
	}

	/**
	 * Return the user-defined class for the given instance: usually simply
	 * the class of the given instance, but the original class in case of a
	 * CGLIB-generated subclass.
	 * @param instance the instance to check
	 * @return the user-defined class
	 */
	public static Class<?> getUserClass(Object instance) {
		if (instance == null) {
			throw new IllegalArgumentException("Instance must not be null");
		}
		return getUserClass(instance.getClass());
	}

	/**
	 * Return the user-defined class for the given class: usually simply the given
	 * class, but the original class in case of a CGLIB-generated subclass.
	 * @param clazz the class to check
	 * @return the user-defined class
	 */
	public static Class<?> getUserClass(Class<?> clazz) {
		return (clazz != null && clazz.getName().contains(CGLIB_CLASS_SEPARATOR) ?
				clazz.getSuperclass() : clazz);
	}

	/**
	 * Check whether the given class is cache-safe in the given context,
	 * i.e. whether it is loaded by the given ClassLoader or a parent of it.
	 * @param clazz the class to analyze
	 * @param classLoader the ClassLoader to potentially cache metadata in
	 * @return cache safe result
	 */
	public static boolean isCacheSafe(Class<?> clazz, ClassLoader classLoader) {
		if (clazz == null) {
			throw new IllegalArgumentException("Class must not be null");
		}
		ClassLoader target = clazz.getClassLoader();
		if (target == null) {
			return false;
		}
		ClassLoader cur = classLoader;
		if (cur == target) {
			return true;
		}
		while (cur != null) {
			cur = cur.getParent();
			if (cur == target) {
				return true;
			}
		}
		return false;
	}


	/**
	 * Get the class name without the qualified package name.
	 * @param className the className to get the short name for
	 * @return the class name of the class without the package name
	 * @throws IllegalArgumentException if the className is empty
	 */
	public static String getShortName(String className) {
		if (StringUtils.hasLength(className)) {
			throw new IllegalArgumentException("Class name must not be empty");
		}
		int lastDotIndex = className.lastIndexOf(PACKAGE_SEPARATOR);
		int nameEndIndex = className.indexOf(CGLIB_CLASS_SEPARATOR);
		if (nameEndIndex == -1) {
			nameEndIndex = className.length();
		}
		String shortName = className.substring(lastDotIndex + 1, nameEndIndex);
		shortName = shortName.replace(INNER_CLASS_SEPARATOR, PACKAGE_SEPARATOR);
		return shortName;
	}

	/**
	 * Get the class name without the qualified package name.
	 * @param clazz the class to get the short name for
	 * @return the class name of the class without the package name
	 */
	public static String getShortName(Class<?> clazz) {
		return getShortName(getQualifiedName(clazz));
	}

	/**
	 * Return the short string name of a Java class in decapitated JavaBeans
	 * property format. Strips the outer class name in case of an inner class.
	 * @param clazz the class
	 * @return the short name rendered in a standard JavaBeans property format
	 * @see java.beans.Introspector#decapitalize(String)
	 */
	public static String getShortNameAsProperty(Class<?> clazz) {
		String shortName = ClassUtils.getShortName(clazz);
		int dotIndex = shortName.lastIndexOf('.');
		shortName = (dotIndex != -1 ? shortName.substring(dotIndex + 1) : shortName);
		return Introspector.decapitalize(shortName);
	}

	/**
	 * Determine the name of the class file, relative to the containing
	 * package: e.g. "String.class"
	 * @param clazz the class
	 * @return the file name of the ".class" file
	 */
	public static String getClassFileName(Class<?> clazz) {
		if (clazz == null) {
			throw new IllegalArgumentException("Class must not be null");
		}
		String className = clazz.getName();
		int lastDotIndex = className.lastIndexOf(PACKAGE_SEPARATOR);
		return className.substring(lastDotIndex + 1) + CLASS_FILE_SUFFIX;
	}

	/**
	 * Determine the name of the package of the given class:
	 * e.g. "java.lang" for the <code>java.lang.String</code> class.
	 * @param clazz the class
	 * @return the package name, or the empty String if the class
	 * is defined in the default package
	 */
	public static String getPackageName(Class<?> clazz) {
		if (clazz == null) {
			throw new IllegalArgumentException("Class must not be null");
		}
		String className = clazz.getName();
		int lastDotIndex = className.lastIndexOf(PACKAGE_SEPARATOR);
		return (lastDotIndex != -1 ? className.substring(0, lastDotIndex) : "");
	}

	/**
	 * Return the qualified name of the given class: usually simply
	 * the class name, but component type class name + "[]" for arrays.
	 * @param clazz the class
	 * @return the qualified name of the class
	 */
	public static String getQualifiedName(Class<?> clazz) {
		if (clazz == null) {
			throw new IllegalArgumentException("Class must not be null");
		}
		if (clazz.isArray()) {
			return getQualifiedNameForArray(clazz);
		} else {
			return clazz.getName();
		}
	}

	/**
	 * Return the qualified name of the given method, consisting of
	 * fully qualified interface/class name + "." + method name.
	 * @param method the method
	 * @return the qualified name of the method
	 */
	public static String getQualifiedMethodName(Method method) {
		if (method == null) {
			throw new IllegalArgumentException("Method must not be null");
		}
		return method.getDeclaringClass().getName() + "." + method.getName();
	}

	/**
	 * Return a descriptive name for the given object's type: usually simply
	 * the class name, but component type class name + "[]" for arrays,
	 * and an appended list of implemented interfaces for JDK proxies.
	 * @param value the value to introspect
	 * @return the qualified name of the class
	 */
	public static String getDescriptiveType(Object value) {
		if (value == null) {
			return null;
		}
		Class<?> clazz = value.getClass();
		if (Proxy.isProxyClass(clazz)) {
			StringBuilder buf = new StringBuilder(clazz.getName());
			buf.append(" implementing ");
			Class<?>[] interfaces = clazz.getInterfaces();
			for (int i = 0; i < interfaces.length; i++) {
				buf.append(interfaces[i].getName());
				if (i < interfaces.length - 1) {
					buf.append(',');
				}
			}
			return buf.toString();
		} else if (clazz.isArray()) {
			return getQualifiedNameForArray(clazz);
		} else {
			return clazz.getName();
		}
	}


	/**
	 * Determine whether the given class has a constructor with the given signature.
	 * <p>Essentially translates <code>NoSuchMethodException</code> to "false".
	 * @param clazz	the clazz to analyze
	 * @param paramTypes the parameter types of the method
	 * @return whether the class has a corresponding constructor
	 * @see java.lang.Class#getMethod
	 */
	public static boolean hasConstructor(Class<?> clazz, Class<?>[] paramTypes) {
		return (getConstructorIfAvailable(clazz, paramTypes) != null);
	}

	/**
	 * Determine whether the given class has a constructor with the given signature,
	 * and return it if available (else return <code>null</code>).
	 * <p>Essentially translates <code>NoSuchMethodException</code> to <code>null</code>.
	 * @param clazz	the clazz to analyze
	 * @param paramTypes the parameter types of the method
	 * @return the constructor, or <code>null</code> if not found
	 * @see java.lang.Class#getConstructor
	 */
	public static Constructor<?> getConstructorIfAvailable(Class<?> clazz, Class<?>[] paramTypes) {
		if (clazz == null) {
			throw new IllegalArgumentException("Class must not be null");
		}
		try {
			return clazz.getConstructor(paramTypes);
		} catch (NoSuchMethodException ex) {
			return null;
		}
	}
	
	/**
	 * Determine all fields include private, protected and public
	 * @param clazz				Target define class
	 * @param includeParent		Determine parent class fields
	 * @return					List of all fields
	 */
	public static List<Field> getDeclaredFields(Class<?> clazz, boolean includeParent) {
		List<Field> fieldList = new ArrayList<>();
		getDeclaredFields(clazz, includeParent, fieldList);
		return fieldList;
	}
	
	/**
	 * Check given field name is field of given class
	 * @param clazz				Target define class
	 * @param fieldName			Field name
	 * @return					Check result
	 */
	public static boolean hasField(Class<?> clazz, String fieldName) {
		return (getFieldIfAvailable(clazz, fieldName) != null);
	}
	
	/**
	 * Determine whether the given class has a field with the given field name.
	 * @param clazz				Target define class
	 * @param fieldName			Field name
	 * @return					Field object
	 */
	public static Field getFieldIfAvailable(Class<?> clazz, String fieldName) {
		if (clazz == null) {
			throw new IllegalArgumentException("Class must not be null");
		}
		if (fieldName == null) {
			throw new IllegalArgumentException("Method name must not be null");
		}
		try {
			return clazz.getField(fieldName);
		} catch (SecurityException | NoSuchFieldException e) {
			return null;
		}
	}

	/**
	 * Determine whether the given class has a method with the given signature.
	 * <p>Essentially translates <code>NoSuchMethodException</code> to "false".
	 * @param clazz	the clazz to analyze
	 * @param methodName the name of the method
	 * @param paramTypes the parameter types of the method
	 * @return whether the class has a corresponding method
	 * @see java.lang.Class#getMethod
	 */
	public static boolean hasMethod(Class<?> clazz, String methodName, Class<?>[] paramTypes) {
		return (getMethodIfAvailable(clazz, methodName, paramTypes) != null);
	}

	/**
	 * Determine whether the given class has a method with the given signature,
	 * and return it if available (else return <code>null</code>).
	 * <p>Essentially translates <code>NoSuchMethodException</code> to <code>null</code>.
	 * @param clazz	the clazz to analyze
	 * @param methodName the name of the method
	 * @param paramTypes the parameter types of the method
	 * @return the method, or <code>null</code> if not found
	 * @see java.lang.Class#getMethod
	 */
	public static Method getMethodIfAvailable(Class<?> clazz, String methodName, Class<?>[] paramTypes) {
		if (clazz == null) {
			throw new IllegalArgumentException("Class must not be null");
		}
		if (methodName == null) {
			throw new IllegalArgumentException("Method name must not be null");
		}
		try {
			return clazz.getMethod(methodName, paramTypes);
		} catch (NoSuchMethodException ex) {
			return null;
		}
	}

	/**
	 * Return the number of methods with a given name (with any argument types),
	 * for the given class and/or its superclasses. Includes non-public methods.
	 * @param clazz	the clazz to check
	 * @param methodName the name of the method
	 * @return the number of methods with the given name
	 */
	public static int getMethodCountForName(Class<?> clazz, String methodName) {
		if (clazz == null) {
			throw new IllegalArgumentException("Class must not be null");
		}
		if (methodName == null) {
			throw new IllegalArgumentException("Method name must not be null");
		}
		int count = 0;
		Method[] declaredMethods = clazz.getDeclaredMethods();
		for (Method method : declaredMethods) {
			if (methodName.equals(method.getName())) {
				count++;
			}
		}
		Class<?>[] interfaces = clazz.getInterfaces();
		for (Class<?> interfaceClass : interfaces) {
			count += getMethodCountForName(interfaceClass, methodName);
		}
		if (clazz.getSuperclass() != null) {
			count += getMethodCountForName(clazz.getSuperclass(), methodName);
		}
		return count;
	}

	/**
	 * Does the given class and/or its superclasses at least have one or more
	 * methods (with any argument types)? Includes non-public methods.
	 * @param clazz	the clazz to check
	 * @param methodName the name of the method
	 * @return whether there is at least one method with the given name
	 */
	public static boolean hasAtLeastOneMethodWithName(Class<?> clazz, String methodName) {
		if (clazz == null) {
			throw new IllegalArgumentException("Class must not be null");
		}
		if (methodName == null) {
			throw new IllegalArgumentException("Method name must not be null");
		}
		Method[] declaredMethods = clazz.getDeclaredMethods();
		for (Method method : declaredMethods) {
			if (method.getName().equals(methodName)) {
				return true;
			}
		}
		Class<?>[] interfaces = clazz.getInterfaces();
		for (Class<?> interfaceClass : interfaces) {
			if (hasAtLeastOneMethodWithName(interfaceClass, methodName)) {
				return true;
			}
		}
		return (clazz.getSuperclass() != null && hasAtLeastOneMethodWithName(clazz.getSuperclass(), methodName));
	}

	/**
	 * Given a method, which may come from an interface, and a target class used
	 * in the current reflective invocation, find the corresponding target method
	 * if there is one. E.g. the method may be <code>IFoo.bar()</code> and the
	 * target class may be <code>DefaultFoo</code>. In this case, the method may be
	 * <code>DefaultFoo.bar()</code>. This enables attributes on that method to be found.
	 * @param method the method to be invoked, which may come from an interface
	 * @param targetClass the target class for the current invocation.
	 * May be <code>null</code> or may not even implement the method.
	 * @return the specific target method, or the original method if the
	 * <code>targetClass</code> doesn't implement it or is <code>null</code>
	 */
	public static Method getMostSpecificMethod(Method method, Class<?> targetClass) {
		if (method != null && targetClass != null && !targetClass.equals(method.getDeclaringClass())) {
			try {
				method = targetClass.getMethod(method.getName(), method.getParameterTypes());
			} catch (NoSuchMethodException ex) {
				// Perhaps the target class doesn't implement this method:
				// that's fine, just use the original method.
			}
		}
		return method;
	}

	/**
	 * Return a static method of a class.
	 * @param methodName the static method name
	 * @param clazz	the class which defines the method
	 * @param args the parameter types to the method
	 * @return the static method, or <code>null</code> if no static method was found
	 * @throws IllegalArgumentException if the method name is blank or the clazz is null
	 */
	public static Method getStaticMethod(Class<?> clazz, String methodName, Class<?>[] args) {
		if (clazz == null) {
			throw new IllegalArgumentException("Class must not be null");
		}
		if (methodName == null) {
			throw new IllegalArgumentException("Method name must not be null");
		}
		try {
			Method method = clazz.getDeclaredMethod(methodName, args);
			if ((method.getModifiers() & Modifier.STATIC) != 0) {
				return method;
			}
		} catch (NoSuchMethodException e) {
			Logger logger = LoggerFactory.getLogger(ClassUtils.class);
			if (logger.isDebugEnabled()) {
				logger.debug("Get static method error! ", e);
			}
		}
		return null;
	}

	public static Class<?> primitiveWrapper(Class<?> clazz) {
		if (clazz.isPrimitive()) {
			for (Map.Entry<Object, Object> entry : PRIMITIVE_WRAPPER_TYPE_MAP.entrySet()) {
				if (entry.getValue().equals(clazz)) {
					return (Class<?>)entry.getKey();
				}
			}
		}
		return clazz;
	}

	/**
	 * Check if the given class represents a primitive wrapper,
	 * i.e. Boolean, Byte, Character, Short, Integer, Long, Float, or Double.
	 * @param clazz the class to check
	 * @return whether the given class is a primitive wrapper class
	 */
	public static boolean isPrimitiveWrapper(Class<?> clazz) {
		if (clazz == null) {
			throw new IllegalArgumentException("Class must not be null");
		}
		return PRIMITIVE_WRAPPER_TYPE_MAP.containsKey(clazz);
	}

	/**
	 * Check if the given class represents a primitive (i.e. boolean, byte,
	 * char, short, int, long, float, or double) or a primitive wrapper
	 * (i.e. Boolean, Byte, Character, Short, Integer, Long, Float, or Double).
	 * @param clazz the class to check
	 * @return whether the given class is a primitive or primitive wrapper class
	 */
	public static boolean isPrimitiveOrWrapper(Class<?> clazz) {
		if (clazz == null) {
			throw new IllegalArgumentException("Class must not be null");
		}
		return (clazz.isPrimitive() || isPrimitiveWrapper(clazz));
	}

	/**
	 * Check if the given class represents an array of primitives,
	 * i.e. boolean, byte, char, short, int, long, float, or double.
	 * @param clazz the class to check
	 * @return whether the given class is a primitive array class
	 */
	public static boolean isPrimitiveArray(Class<?> clazz) {
		if (clazz == null) {
			throw new IllegalArgumentException("Class must not be null");
		}
		return (clazz.isArray() && clazz.getComponentType().isPrimitive());
	}

	/**
	 * Check if the given class represents an array of primitive wrappers,
	 * i.e. Boolean, Byte, Character, Short, Integer, Long, Float, or Double.
	 * @param clazz the class to check
	 * @return whether the given class is a primitive wrapper array class
	 */
	public static boolean isPrimitiveWrapperArray(Class<?> clazz) {
		if (clazz == null) {
			throw new IllegalArgumentException("Class must not be null");
		}
		return (clazz.isArray() && isPrimitiveWrapper(clazz.getComponentType()));
	}

	/**
	 * Check if the right-hand side type may be assigned to the left-hand side
	 * type, assuming setting by reflection. Considers primitive wrapper
	 * classes as assignable to the corresponding primitive types.
	 * @param lhsType the target type
	 * @param rhsType	the value type that should be assigned to the target type
	 * @return if the target type is assignable from the value type
	 */
	public static boolean isAssignable(Class<?> lhsType, Class<?> rhsType) {
		if (lhsType == null) {
			throw new IllegalArgumentException("Left-hand side type must not be null");
		}
		if (rhsType == null) {
			throw new IllegalArgumentException("Right-hand side type must not be null");
		}
		return (lhsType.isAssignableFrom(rhsType) ||
				lhsType.equals(PRIMITIVE_WRAPPER_TYPE_MAP.get(rhsType)));
	}

	/**
	 * Determine if the given type is assignable from the given value,
	 * assuming setting by reflection. Considers primitive wrapper classes
	 * as assignable to the corresponding primitive types.
	 * @param type	the target type
	 * @param value the value that should be assigned to the type
	 * @return if the type is assignable from the value
	 */
	public static boolean isAssignableValue(Class<?> type, Object value) {
		if (type == null) {
			throw new IllegalArgumentException("Type must not be null");
		}
		return (value != null ? isAssignable(type, value.getClass()) : !type.isPrimitive());
	}


	/**
	 * Convert a "/"-based resource path to a "."-based fully qualified class name.
	 * @param resourcePath the resource path pointing to a class
	 * @return the corresponding fully qualified class name
	 */
	public static String convertResourcePathToClassName(String resourcePath) {
		return resourcePath.replace('/', '.');
	}

	/**
	 * Convert a "."-based fully qualified class name to a "/"-based resource path.
	 * @param className the fully qualified class name
	 * @return the corresponding resource path, pointing to the class
	 */
	public static String convertClassNameToResourcePath(String className) {
		return className.replace('.', '/');
	}

	/**
	 * Return a path suitable for use with <code>ClassLoader.getResource</code>
	 * (also suitable for use with <code>Class.getResource</code> by prepending a
	 * slash ('/') to the return value. Built by taking the package of the specified
	 * class file, converting all dots ('.') to slashes ('/'), adding a trailing slash
	 * if necessary, and concatenating the specified resource name to this.
	 * As such, this function may be used to build a path suitable for
	 * loading a resource file that is in the same package as a class file,
	 * @param clazz	the Class whose package will be used as the base
	 * @param resourceName the resource name to append. A leading slash is optional.
	 * @return the built-up resource path
	 * @see java.lang.ClassLoader#getResource
	 * @see java.lang.Class#getResource
	 */
	public static String addResourcePathToPackagePath(Class<?> clazz, String resourceName) {
		if (resourceName == null) {
			throw new IllegalArgumentException("Resource name must not be null");
		}
		if (!resourceName.startsWith("/")) {
			return classPackageAsResourcePath(clazz) + "/" + resourceName;
		}
		return classPackageAsResourcePath(clazz) + resourceName;
	}

	/**
	 * Given an input class object, return a string which consists of the
	 * class's package name as a pathname, i.e., all dots ('.') are replaced by
	 * slashes ('/'). Neither a leading nor trailing slash is added. The result
	 * could be concatenated with a slash and the name of a resource, and fed
	 * directly to <code>ClassLoader.getResource()</code>. For it to be fed to
	 * <code>Class.getResource</code> instead, a leading slash would also have
	 * to be prepended to the returned value.
	 * @param clazz the input class. A <code>null</code> value or the default
	 * (empty) package will result in an empty string ("") being returned.
	 * @return a path which represents the package name
	 * @see ClassLoader#getResource
	 * @see Class#getResource
	 */
	public static String classPackageAsResourcePath(Class<?> clazz) {
		if (clazz == null) {
			return "";
		}
		String className = clazz.getName();
		int packageEndIndex = className.lastIndexOf('.');
		if (packageEndIndex == -1) {
			return "";
		}
		String packageName = className.substring(0, packageEndIndex);
		return packageName.replace('.', '/');
	}

	/**
	 * Build a String that consists of the names of the classes/interfaces
	 * in the given array.
	 * <p>Basically like <code>AbstractCollection.toString()</code>, but stripping
	 * the "class "/"interface " prefix before every class name.
	 * @param classes a Collection of Class objects (maybe <code>null</code>)
	 * @return a String of form "[com.foo.Bar, com.foo.Baz]"
	 * @see java.util.AbstractCollection#toString()
	 */
	public static String classNamesToString(Class<?>[] classes) {
		return classNamesToString(Arrays.asList(classes));
	}

	/**
	 * Build a String that consists of the names of the classes/interfaces
	 * in the given collection.
	 * <p>Basically like <code>AbstractCollection.toString()</code>, but stripping
	 * the "class "/"interface " prefix before every class name.
	 * @param classes a Collection of Class objects (maybe <code>null</code>)
	 * @return a String of form "[com.foo.Bar, com.foo.Baz]"
	 * @see java.util.AbstractCollection#toString()
	 */
	public static String classNamesToString(Collection<?> classes) {
		if (CollectionUtils.isEmpty(classes)) {
			return "[]";
		}
		StringBuilder sb = new StringBuilder("[");
		for (Iterator<?> it = classes.iterator(); it.hasNext(); ) {
			Class<?> clazz = (Class<?>) it.next();
			sb.append(clazz.getName());
			if (it.hasNext()) {
				sb.append(", ");
			}
		}
		sb.append("]");
		return sb.toString();
	}


	/**
	 * Return all interfaces that the given instance implements as array,
	 * including ones implemented by superclasses.
	 * @param instance the instance to analyse for interfaces
	 * @return all interfaces that the given instance implements as array
	 */
	public static Class<?>[] getAllInterfaces(Object instance) {
		if (instance == null) {
			throw new IllegalArgumentException("Instance must not be null");
		}
		return getAllInterfacesForClass(instance.getClass());
	}

	/**
	 * Return all interfaces that the given class implements as array,
	 * including ones implemented by superclasses.
	 * <p>If the class itself is an interface, it gets returned as sole interface.
	 * @param clazz the class to analyse for interfaces
	 * @return all interfaces that the given object implements as array
	 */
	public static Class<?>[] getAllInterfacesForClass(Class<?> clazz) {
		return getAllInterfacesForClass(clazz, null);
	}

	/**
	 * Return all interfaces that the given class implements as array,
	 * including ones implemented by superclasses.
	 * <p>If the class itself is an interface, it gets returned as sole interface.
	 * @param clazz the class to analyse for interfaces
	 * @param classLoader the ClassLoader that the interfaces need to be visible in
	 * (maybe <code>null</code> when accepting all declared interfaces)
	 * @return all interfaces that the given object implements as array
	 */
	public static Class<?>[] getAllInterfacesForClass(Class<?> clazz, ClassLoader classLoader) {
		if (clazz == null) {
			throw new IllegalArgumentException("Class must not be null");
		}
		if (clazz.isInterface()) {
			return new Class[] {clazz};
		}
		List<Class<?>> interfaces = new ArrayList<>();
		while (clazz != null) {
			for (int i = 0; i < clazz.getInterfaces().length; i++) {
				Class<?> ifc = clazz.getInterfaces()[i];
				if (!interfaces.contains(ifc) &&
						(classLoader == null || isVisible(ifc, classLoader))) {
					interfaces.add(ifc);
				}
			}
			clazz = clazz.getSuperclass();
		}
		return interfaces.toArray(new Class[0]);
	}

	/**
	 * Return all interfaces that the given instance implements as Set,
	 * including ones implemented by superclasses.
	 * @param instance the instance to analyse for interfaces
	 * @return all interfaces that the given instance implements as Set
	 */
	public static Set<?> getAllInterfacesAsSet(Object instance) {
		if (instance == null) {
			throw new IllegalArgumentException("Instance must not be null");
		}
		return getAllInterfacesForClassAsSet(instance.getClass());
	}

	/**
	 * Return all interfaces that the given class implements as Set,
	 * including ones implemented by superclasses.
	 * <p>If the class itself is an interface, it gets returned as sole interface.
	 * @param clazz the class to analyse for interfaces
	 * @return all interfaces that the given object implements as Set
	 */
	public static Set<?> getAllInterfacesForClassAsSet(Class<?> clazz) {
		return getAllInterfacesForClassAsSet(clazz, null);
	}

	/**
	 * Return all interfaces that the given class implements as Set,
	 * including ones implemented by superclasses.
	 * <p>If the class itself is an interface, it gets returned as sole interface.
	 * @param clazz the class to analyse for interfaces
	 * @param classLoader the ClassLoader that the interfaces need to be visible in
	 * (maybe <code>null</code> when accepting all declared interfaces)
	 * @return all interfaces that the given object implements as Set
	 */
	public static Set<?> getAllInterfacesForClassAsSet(Class<?> clazz, ClassLoader classLoader) {
		if (clazz == null) {
			throw new IllegalArgumentException("Class must not be null");
		}
		if (clazz.isInterface()) {
			return Collections.singleton(clazz);
		}
		Set<Object> interfaces = new LinkedHashSet<>();
		while (clazz != null) {
			for (int i = 0; i < clazz.getInterfaces().length; i++) {
				Class<?> ifc = clazz.getInterfaces()[i];
				if (classLoader == null || isVisible(ifc, classLoader)) {
					interfaces.add(ifc);
				}
			}
			clazz = clazz.getSuperclass();
		}
		return interfaces;
	}

	/**
	 * Check whether the given class is visible in the given ClassLoader.
	 * @param clazz the class to check (typically an interface)
	 * @param classLoader the ClassLoader to check against (maybe <code>null</code>,
	 * in which case this method will always return <code>true</code>)
	 * @return class is visible
	 */
	public static boolean isVisible(Class<?> clazz, ClassLoader classLoader) {
		if (classLoader == null) {
			return true;
		}
		try {
			Class<?> actualClass = classLoader.loadClass(clazz.getName());
			return (clazz == actualClass);
			// Else: different interface class found...
		} catch (ClassNotFoundException ex) {
			// No interface class found...
			return false;
		}
	}

	/**
	 * Build a nice qualified name for an array:
	 * component type class name + "[]".
	 * @param clazz the array class
	 * @return a qualified name for the array class
	 */
	private static String getQualifiedNameForArray(Class<?> clazz) {
		StringBuilder buffer = new StringBuilder();
		while (clazz.isArray()) {
			clazz = clazz.getComponentType();
			buffer.append(ClassUtils.ARRAY_SUFFIX);
		}
		buffer.insert(0, clazz.getName());
		return buffer.toString();
	}

	private static void getDeclaredFields(Class<?> clazz, boolean includeParent, List<Field> fieldList) {
		if (fieldList == null) {
			return;
		}

		if (includeParent && clazz.getSuperclass() != null) {
			getDeclaredFields(clazz.getSuperclass(), true, fieldList);
		}

		Collections.addAll(fieldList, clazz.getDeclaredFields());
	}
}
