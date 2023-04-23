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

import org.nervousync.commons.core.Globals;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Stream;

/**
 * Miscellaneous class utility methods. Mainly for internal use within the
 * framework; consider Jakarta's Commons Lang for a more comprehensive suite
 * of class utilities.
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: Jan 13, 2010 3:53:41 PM $
 */
public final class ClassUtils {

	/** Suffix for array class names: "[]" */
	private static final String ARRAY_SUFFIX = "[]";

	/** Prefix for internal array class names: "[L" */
	private static final String INTERNAL_ARRAY_PREFIX = "[L";

	/** The package separator character '.' */
	private static final char PACKAGE_SEPARATOR = '.';

	/** The resource separator character '/' */
	private static final char RESOURCE_SEPARATOR = '/';

	/** The inner class separator character '$' */
	private static final char INNER_CLASS_SEPARATOR = '$';

	/** The CGLIB class separator character "$$" */
	private static final String CGLIB_CLASS_SEPARATOR = "$$";

	/** The Bytebuddy class separator character "$$" */
	private static final String BYTEBUDDY_CLASS_SEPARATOR = "$ByteBuddy";

	/**
	 * The ".class" file suffix
	 */
	public static final String CLASS_FILE_SUFFIX = ".class";

	/**
	 * Map with the primitive wrapper type as a key and corresponding primitive
	 * type as value, for example: Integer.class -> int.class.
	 */
	private static final Map<Object, Object> PRIMITIVE_WRAPPER_TYPE_MAP = new HashMap<>(8);

	/**
	 * Map with primitive type name as a key and corresponding primitive
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
	 *
	 * @return the default ClassLoader (never <code>null</code>)
	 * @see java.lang.Thread#getContextClassLoader() java.lang.Thread#getContextClassLoader()
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

	/**
	 * Default class loader optional.
	 *
	 * @param <T>   the type parameter
	 * @param clazz the clazz
	 * @return the optional
	 */
	public static <T> Optional<T> defaultClassLoader(final Class<T> clazz) {
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
	 *
	 * @param classLoaderToUse the actual ClassLoader to use for the thread context
	 * @return the original thread context ClassLoader, or <code>null</code> if not overridden
	 */
	public static ClassLoader overrideThreadContextClassLoader(final ClassLoader classLoaderToUse) {
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
	 * Determine whether the {@link Class} identified by the supplied name is present and can be loaded.
	 * Will return <code>Boolean.FALSE</code> if either the class or
	 * one of its dependencies is not present or cannot be loaded.
	 *
	 * @param className the name of the class to check
	 * @return whether the specified class is present
	 */
	public static boolean isPresent(final String className) {
		return isPresent(className, getDefaultClassLoader());
	}

	/**
	 * Determine whether the {@link Class} identified by the supplied name is present and can be loaded.
	 * Will return <code>Boolean.FALSE</code> if either the class or
	 * one of its dependencies is not present or cannot be loaded.
	 *
	 * @param className   the name of the class to check
	 * @param classLoader the class loader to use (maybe <code>null</code>, which indicates the default class loader)
	 * @return whether the specified class is present
	 */
	public static boolean isPresent(final String className, final ClassLoader classLoader) {
		try {
			forName(className, classLoader);
			return Boolean.TRUE;
		} catch (Throwable ex) {
			// Class or one of its dependencies is not present...
			return Boolean.FALSE;
		}
	}

	public static String origClassName(final Class<?> clazz) {
		String className = clazz.getName();
		if (className.contains(CGLIB_CLASS_SEPARATOR)) {
			//  Process for cglib
			className = className.substring(0, className.indexOf(CGLIB_CLASS_SEPARATOR));
		} else if (className.contains(BYTEBUDDY_CLASS_SEPARATOR)) {
			//  Process for ByteBuddy
			className = className.substring(0, className.indexOf(BYTEBUDDY_CLASS_SEPARATOR));
		}
		return className;
	}

	/**
	 * Replacement for <code>Class.forName()</code> that also returns Class instances
	 * for primitives (like "int") and array class names (like "String[]").
	 * <p>Always uses the default class loader: that is, preferably the thread context
	 * class loader, or the ClassLoader that loaded the ClassUtils class as fallback.
	 *
	 * @param className the name of the Class
	 * @return Class instance for the supplied name
	 * @throws IllegalArgumentException if the class name was not resolvable (that is, the class could not be found or the class file could not be loaded)
	 * @see Class#forName(String, boolean, ClassLoader) Class#forName(String, boolean, ClassLoader)
	 * @see #getDefaultClassLoader() #getDefaultClassLoader()
	 */
	public static Class<?> forName(final String className) throws IllegalArgumentException {
		return forName(className, getDefaultClassLoader());
	}

	/**
	 * Replacement for <code>Class.forName()</code> that also returns Class instances
	 * for primitives (like "int") and array class names (like "String[]").
	 *
	 * @param className        the name of the Class
	 * @param classLoader the class loader to use (maybe <code>null</code>, which indicates the default class loader)
	 * @return Class instance for the supplied name
	 * @throws IllegalArgumentException if the class name was not resolvable (that is, the class could not be found or the class file could not be loaded)
	 * @see Class#forName(String, boolean, ClassLoader) Class#forName(String, boolean, ClassLoader)
	 */
	public static Class<?> forName(final String className, final ClassLoader classLoader)
			throws IllegalArgumentException {
		if (StringUtils.isEmpty(className)) {
			throw new IllegalArgumentException("Class name must not be empty");
		}

		if (StringUtils.notBlank(className) && className.length() <= 8
				&& PRIMITIVE_TYPE_NAME_MAP.containsKey(className)) {
			return (Class<?>) PRIMITIVE_TYPE_NAME_MAP.get(className);
		}

		// "java.lang.String[]" style arrays
		if (className.endsWith(ARRAY_SUFFIX)) {
			String elementClassName = className.substring(0, className.length() - ARRAY_SUFFIX.length());
			Class<?> elementClass = forName(elementClassName, classLoader);
			return Array.newInstance(elementClass, 0).getClass();
		}

		int internalArrayMarker = className.indexOf(INTERNAL_ARRAY_PREFIX);
		if (internalArrayMarker != -1 && className.endsWith(";")) {
			String elementClassName = null;
			if (internalArrayMarker == 0) {
				elementClassName = className.substring(INTERNAL_ARRAY_PREFIX.length(), className.length() - 1);
			} else if (className.startsWith("[")) {
				elementClassName = className.substring(1);
			}
			Class<?> elementClass = forName(elementClassName, classLoader);
			return Array.newInstance(elementClass, 0).getClass();
		}

		ClassLoader classLoaderToUse = classLoader;
		if (classLoaderToUse == null) {
			classLoaderToUse = getDefaultClassLoader();
		}
		try {
			return classLoaderToUse.loadClass(className);
		} catch (ClassNotFoundException ex) {
			throw new IllegalArgumentException("Cannot find class [" + className + "]", ex);
		}
	}

	/**
	 * Check whether the given class is cache-safe in the given context,
	 * i.e. whether it is loaded by the given ClassLoader or a parent of it.
	 *
	 * @param clazz       the class to analyze
	 * @param classLoader the ClassLoader to potentially cache metadata in
	 * @return cache safe result
	 */
	public static boolean isCacheSafe(final Class<?> clazz, final ClassLoader classLoader) {
		if (clazz == null) {
			throw new IllegalArgumentException("Class must not be null");
		}
		ClassLoader target = clazz.getClassLoader();
		if (target == null) {
			return Boolean.FALSE;
		}
		ClassLoader cur = classLoader;
		if (cur == target) {
			return Boolean.TRUE;
		}
		while (cur != null) {
			cur = cur.getParent();
			if (cur == target) {
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}

	/**
	 * Determine the name of the class file, relative to the containing
	 * package: e.g. "String.class"
	 *
	 * @param clazz the class
	 * @return the file name of the ".class" file
	 */
	public static String getClassFileName(final Class<?> clazz) {
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
	 *
	 * @param clazz the class
	 * @return the package name, or the empty String if the class is defined in the default package
	 */
	public static String getPackageName(final Class<?> clazz) {
		if (clazz == null) {
			throw new IllegalArgumentException("Class must not be null");
		}
		String className = clazz.getName();
		int lastDotIndex = className.lastIndexOf(PACKAGE_SEPARATOR);
		return (lastDotIndex != -1 ? className.substring(0, lastDotIndex) : Globals.DEFAULT_VALUE_STRING);
	}

	/**
	 * Return the qualified name of the given class: usually simply
	 * the class name, but component type class name + "[]" for arrays.
	 *
	 * @param clazz the class
	 * @return the qualified name of the class
	 */
	public static String getQualifiedName(final Class<?> clazz) {
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
	 * Return a descriptive name for the given object's type: usually simply
	 * the class name, but component type class name + "[]" for arrays,
	 * and an appended list of implemented interfaces for JDK proxies.
	 *
	 * @param value the value to introspect
	 * @return the qualified name of the class
	 */
	public static String getDescriptiveType(final Object value) {
		if (value == null) {
			return null;
		}
		Class<?> clazz = value.getClass();
		if (Proxy.isProxyClass(clazz)) {
			final StringBuilder stringBuilder = new StringBuilder();
			Arrays.asList(clazz.getInterfaces())
					.forEach(interfaceClass -> stringBuilder.append(",").append(interfaceClass.getName()));
			return clazz.getName() + " implementing " + stringBuilder.substring(1);
		} else if (clazz.isArray()) {
			return getQualifiedNameForArray(clazz);
		} else {
			return clazz.getName();
		}
	}

	/**
	 * Primitive wrapper class.
	 *
	 * @param clazz the clazz
	 * @return the class
	 */
	public static Class<?> primitiveWrapper(final Class<?> clazz) {
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
	 *
	 * @param clazz the class to check
	 * @return whether the given class is a primitive wrapper class
	 */
	public static boolean isPrimitiveWrapper(final Class<?> clazz) {
		if (clazz == null) {
			throw new IllegalArgumentException("Class must not be null");
		}
		return PRIMITIVE_WRAPPER_TYPE_MAP.containsKey(clazz);
	}

	/**
	 * Check if the given class represents a primitive (i.e. boolean, byte,
	 * char, short, int, long, float, or double) or a primitive wrapper
	 * (i.e. Boolean, Byte, Character, Short, Integer, Long, Float, or Double).
	 *
	 * @param clazz the class to check
	 * @return whether the given class is a primitive or primitive wrapper class
	 */
	public static boolean isPrimitiveOrWrapper(final Class<?> clazz) {
		if (clazz == null) {
			throw new IllegalArgumentException("Class must not be null");
		}
		return (clazz.isPrimitive() || isPrimitiveWrapper(clazz));
	}

	/**
	 * Check if the given class represents an array of primitives,
	 * i.e. boolean, byte, char, short, int, long, float, or double.
	 *
	 * @param clazz the class to check
	 * @return whether the given class is a primitive array class
	 */
	public static boolean isPrimitiveArray(final Class<?> clazz) {
		if (clazz == null) {
			throw new IllegalArgumentException("Class must not be null");
		}
		return (clazz.isArray() && clazz.getComponentType().isPrimitive());
	}

	/**
	 * Check if the given class represents an array of primitive wrappers,
	 * i.e. Boolean, Byte, Character, Short, Integer, Long, Float, or Double.
	 *
	 * @param clazz the class to check
	 * @return whether the given class is a primitive wrapper array class
	 */
	public static boolean isPrimitiveWrapperArray(final Class<?> clazz) {
		if (clazz == null) {
			throw new IllegalArgumentException("Class must not be null");
		}
		return (clazz.isArray() && isPrimitiveWrapper(clazz.getComponentType()));
	}

	/**
	 * Check if the right-hand side type may be assigned to the left-hand side
	 * type, assuming setting by reflection. Considers primitive wrapper
	 * classes as assignable to the corresponding primitive types.
	 *
	 * @param targetType the target type
	 * @param checkType the value type that should be assigned to the target type
	 * @return if the target type is assignable from the value type
	 */
	public static boolean isAssignable(final Class<?> targetType, final Class<?> checkType) {
		if (targetType == null) {
			throw new IllegalArgumentException("Target type must not be null");
		}
		if (checkType == null) {
			throw new IllegalArgumentException("Check type must not be null");
		}
		return (targetType.isAssignableFrom(checkType) || targetType.equals(PRIMITIVE_WRAPPER_TYPE_MAP.get(checkType)));
	}

	/**
	 * Determine if the given type is assignable from the given value,
	 * assuming setting by reflection. Considers primitive wrapper classes
	 * as assignable to the corresponding primitive types.
	 *
	 * @param type  the target type
	 * @param value the value that should be assigned to the type
	 * @return if the type is assignable from the value
	 */
	public static boolean isAssignableValue(final Class<?> type, final Object value) {
		if (type == null) {
			throw new IllegalArgumentException("Type must not be null");
		}
		return (value != null ? isAssignable(type, value.getClass()) : !type.isPrimitive());
	}

	/**
	 * Convert a "/"-based resource path to a "."-based fully qualified class name.
	 *
	 * @param resourcePath the resource path pointing to a class
	 * @return the corresponding fully qualified class name
	 */
	public static String resourcePathToClassName(final String resourcePath) {
		return resourcePath.replace(RESOURCE_SEPARATOR, PACKAGE_SEPARATOR);
	}

	/**
	 * Convert a "."-based fully qualified class name to a "/"-based resource path.
	 *
	 * @param className the fully qualified class name
	 * @return the corresponding resource path, pointing to the class
	 */
	public static String classNameToResourcePath(String className) {
		return className.replace(PACKAGE_SEPARATOR, RESOURCE_SEPARATOR) + CLASS_FILE_SUFFIX;
	}

	/**
	 * Return a path suitable for use with <code>ClassLoader.getResource</code>
	 * also suitable for use with <code>Class.getResource</code> by prepending a
	 * slash ('/') to the return value. Built by taking the package of the specified
	 * class file, converting all dots ('.') to slashes ('/'), adding a trailing slash
	 * if necessary, and concatenating the specified resource name to this.
	 * As such, this function may be used to build a path suitable for
	 * loading a resource file that is in the same package as a class file.
	 *
	 * @param clazz        the Class whose package will be used as the base
	 * @param resourceName the resource name to append. A leading slash is optional.
	 * @return the built-up resource path
	 * @see java.lang.ClassLoader#getResource java.lang.ClassLoader#getResource
	 * @see java.lang.Class#getResource java.lang.Class#getResource
	 */
	public static String addResourcePathToPackagePath(final Class<?> clazz, final String resourceName) {
		if (resourceName == null) {
			throw new IllegalArgumentException("Resource name must not be null");
		}
		return classPackageAsResourcePath(clazz)
				+ (resourceName.startsWith(Character.toString(RESOURCE_SEPARATOR)) ? Globals.DEFAULT_VALUE_STRING : RESOURCE_SEPARATOR)
				+ resourceName;
	}

	/**
	 * Given an input class object, return a string which consists of the
	 * class's package name as a pathname, i.e., all dots ('.') are replaced by
	 * slashes ('/'). Neither a leading nor trailing slash is added. The result
	 * could be concatenated with a slash and the name of a resource, and fed
	 * directly to <code>ClassLoader.getResource()</code>. For it to be fed to
	 * <code>Class.getResource</code> instead, a leading slash would also have
	 * to be prepended to the returned value.
	 *
	 * @param clazz the input class. A <code>null</code> value or the default (empty) package will result in an empty string ("") being returned.
	 * @return a path which represents the package name
	 * @see ClassLoader#getResource ClassLoader#getResource
	 * @see Class#getResource Class#getResource
	 */
	public static String classPackageAsResourcePath(final Class<?> clazz) {
		if (clazz == null) {
			return Globals.DEFAULT_VALUE_STRING;
		}
		String className = clazz.getName();
		int packageEndIndex = className.lastIndexOf(PACKAGE_SEPARATOR);
		if (packageEndIndex == -1) {
			return Globals.DEFAULT_VALUE_STRING;
		}
		String packageName = className.substring(0, packageEndIndex);
		return packageName.replace(PACKAGE_SEPARATOR, RESOURCE_SEPARATOR);
	}

    /**
     * Find constructor constructor.
     *
     * @param <T>   the type parameter
     * @param clazz the clazz
     * @return the constructor
     * @throws SecurityException     the security exception
     * @throws NoSuchMethodException the no such method exception
     */
    public static <T> Constructor<T> findConstructor(final Class<T> clazz)
            throws SecurityException, NoSuchMethodException {
        return findConstructor(clazz, new Class[0]);
    }

    /**
     * Find constructor constructor.
     *
     * @param <T>        the type parameter
     * @param clazz      the clazz
     * @param paramTypes the param types
     * @return the constructor
     * @throws SecurityException     the security exception
     * @throws NoSuchMethodException the no such method exception
     */
    public static <T> Constructor<T> findConstructor(final Class<T> clazz, final Class<?>[] paramTypes)
            throws SecurityException, NoSuchMethodException {
        if (paramTypes == null || paramTypes.length == 0) {
            return clazz.getDeclaredConstructor();
        }
        return clazz.getDeclaredConstructor(paramTypes);
    }

    /**
     * Check given field name was exists field in target class
     *
     * @param clazz the class to introspect
     * @param name  the name of the field
     * @return the field object exists results
     */
    public static boolean existsField(final Class<?> clazz, final String name) {
        return findField(clazz, name) != null;
    }

    /**
     * Attempt to find a {@link Field field} on the supplied {@link Class} with
     * the supplied <code>name</code>.
	 * Searches all superclasses up to {@link Object}.
     *
     * @param clazz the class to introspect
     * @param name  the name of the field
     * @return the corresponding Field object, or <code>null</code> if not found
     */
    public static Field findField(final Class<?> clazz, final String name) {
        return findField(clazz, name, null);
    }

    /**
     * Attempt to find a {@link Field field} on the supplied {@link Class} with
     * the supplied <code>name</code> and/or {@link Class type}.
	 * Searches all superclasses up to {@link Object}.
     *
     * @param clazz the class to introspect
     * @param name  the name of the field
     * @param type  the type of the field (maybe <code>null</code> if name is specified)
     * @return the corresponding Field object, or <code>null</code> if not found
     */
    public static Field findField(final Class<?> clazz, final String name, final Class<?> type) {
        if (clazz == null) {
            throw new IllegalArgumentException("Class must not be null");
        }
        if (name == null) {
            throw new IllegalArgumentException("Name of the field must be specified");
        }
        Class<?> searchType = clazz;
        while (!Object.class.equals(searchType) && searchType != null) {
            try {
                Field field = searchType.getDeclaredField(name);
                if ((type == null || type.equals(field.getType()))) {
                    return field;
                }
            } catch (NoSuchFieldException ignored) {
            }
            searchType = searchType.getSuperclass();
        }
        return null;
    }

    /**
     * Attempt to find a {@link Method} on the supplied class with the supplied name
     * and no parameters.
	 * Searches all superclasses up to <code>Object</code>.
     * <p>Returns <code>null</code> if no {@link Method} can be found.
     *
     * @param clazz the class to introspect
     * @param name  the name of the method
     * @return the Method object, or <code>null</code> if none found
     */
    public static Method findMethod(final Class<?> clazz, final String name) {
        return findMethod(clazz, name, new Class[0]);
    }

    /**
     * Attempt to find a {@link Method} on the supplied class with the supplied name
     * and parameter types.
	 * Searches all superclasses up to <code>Object</code>.
     * <p>Returns <code>null</code> if no {@link Method} can be found.
     *
     * @param clazz      the class to introspect
     * @param name       the name of the method
     * @param paramTypes the parameter types of the method (maybe <code>null</code> to indicate any signature)
     * @return the Method object, or <code>null</code> if none found
     */
    public static Method findMethod(final Class<?> clazz, final String name, final Class<?>[] paramTypes) {
        if (clazz == null) {
            throw new IllegalArgumentException("Class must not be null");
        }
        if (name == null) {
            throw new IllegalArgumentException("Method name must not be null");
        }
        Class<?> searchType = clazz;
		Class<?>[] paramClasses = ((paramTypes == null) ? new Class[0] : paramTypes);
        while (!Object.class.equals(searchType) && searchType != null) {
            try {
                return searchType.isInterface()
                        ? searchType.getMethod(name, paramClasses)
                        : searchType.getDeclaredMethod(name, paramClasses);
            } catch (NoSuchMethodException ignored) {
            }
            searchType = searchType.getSuperclass();
        }
        return null;
    }

	public static Method getterMethod(final String fieldName, final Class<?> targetClass) {
		return findMethod(fieldName, targetClass, MethodType.Getter);
	}

	public static Method setterMethod(final String fieldName, final Class<?> targetClass) {
		return findMethod(fieldName, targetClass, MethodType.Setter);
	}

	/**
	 * Return all interfaces that the given instance implements as arrays,
	 * including ones implemented by superclasses.
	 *
	 * @param instance the instance to analyse for interfaces
	 * @return all interfaces that the given instance implements as arrays
	 */
	public static Class<?>[] getAllInterfaces(final Object instance) {
		if (instance == null) {
			throw new IllegalArgumentException("Instance must not be null");
		}
		return getAllInterfacesForClass(instance.getClass());
	}

	/**
	 * Return all interfaces that the given class implements as arrays,
	 * including ones implemented by superclasses.
	 * <p>If the class itself is an interface, it gets returned as sole interface.
	 *
	 * @param clazz the class to analyse for interfaces
	 * @return all interfaces that the given object implements as arrays
	 */
	public static Class<?>[] getAllInterfacesForClass(final Class<?> clazz) {
		return getAllInterfacesForClass(clazz, null);
	}

	/**
	 * Return all interfaces that the given class implements as arrays,
	 * including ones implemented by superclasses.
	 * <p>If the class itself is an interface, it gets returned as sole interface.
	 *
	 * @param clazz       the class to analyse for interfaces
	 * @param classLoader the ClassLoader that the interfaces need to be visible in (maybe <code>null</code> when accepting all declared interfaces)
	 * @return all interfaces that the given object implements as arrays
	 */
	public static Class<?>[] getAllInterfacesForClass(final Class<?> clazz, final ClassLoader classLoader) {
		if (clazz == null || clazz.isInterface()) {
			return new Class[] {clazz};
		}
		List<Class<?>> interfaces = new ArrayList<>();
		if (clazz.getSuperclass() != null) {
			List.of(getAllInterfacesForClass(clazz.getSuperclass(), classLoader))
					.forEach(interfaceClass -> {
						if (ObjectUtils.notContainsElement(interfaces, interfaceClass)) {
							interfaces.add(interfaceClass);
						}
					});
		}
		Stream.of(clazz.getInterfaces())
				.filter(interfaceClass ->
						(ObjectUtils.notContainsElement(interfaces, interfaceClass))
								&& (classLoader == null || isVisible(interfaceClass, classLoader)))
				.forEach(interfaces::add);
		return interfaces.toArray(new Class[0]);
	}

	/**
	 * Check whether the given class is visible in the given ClassLoader.
	 *
	 * @param clazz       the class to check (typically an interface)
	 * @param classLoader the ClassLoader to check against
	 *                    (maybe <code>null</code> in which case this method will always return <code>Boolean.TRUE</code>)
	 * @return class is visible
	 */
	public static boolean isVisible(final Class<?> clazz, final ClassLoader classLoader) {
		if (classLoader == null) {
			return Boolean.TRUE;
		}
		try {
			Class<?> actualClass = classLoader.loadClass(clazz.getName());
			return (clazz == actualClass);
			// Else: different interface class found...
		} catch (ClassNotFoundException ex) {
			// No interface class found...
			return Boolean.FALSE;
		}
	}

    /**
     * Parse component type from field
     *
     * @param clazz 	Class instance
     * @return Parsed component type or null if not a list or array
     */
    public static Class<?> componentType(final Class<?> clazz) {
        if (clazz.isArray()) {
            return clazz.getComponentType();
        } else if (Collection.class.isAssignableFrom(clazz)) {
            return  (Class<?>) ((ParameterizedType) clazz.getGenericInterfaces()[0]).getActualTypeArguments()[0];
        }
        return clazz;
    }

	/**
	 * Build a nice qualified name for an array:
	 * component type class name + "[]".
	 * @param clazz the array class
	 * @return a qualified name for the array class
	 */
	private static String getQualifiedNameForArray(final Class<?> clazz) {
		Class<?> currentClass = clazz;
		StringBuilder buffer = new StringBuilder();
		while (currentClass.isArray()) {
			currentClass = currentClass.getComponentType();
			buffer.append(ClassUtils.ARRAY_SUFFIX);
		}
		buffer.insert(0, currentClass.getName());
		return buffer.toString();
	}

    private static Method findMethod(final String fieldName, final Class<?> targetClass, final MethodType methodType) {
        Field field = ReflectionUtils.getFieldIfAvailable(targetClass, fieldName);
        if (field == null) {
            return null;
        }

		return ClassUtils.findMethod(targetClass, methodName(field, methodType),
				MethodType.Setter.equals(methodType) ? new Class<?>[]{field.getType()} : new Class[0]);
    }

    private static String methodName(final Field field, final MethodType methodType) {
        StringBuilder methodName = new StringBuilder();
        switch (methodType) {
            case Getter:
                if (boolean.class.equals(field.getType())) {
                    methodName.append("is");
                } else {
                    methodName.append("get");
                }
                break;
            case Setter:
                methodName.append("set");
                break;
            default:
                return Globals.DEFAULT_VALUE_STRING;
        }
        String fieldName = field.getName();
        methodName.append(fieldName.substring(0, 1).toUpperCase()).append(fieldName.substring(1));
        return methodName.toString();
    }

    /**
     * The enum Method type.
     */
    private enum MethodType {
        /**
         * Get method types.
         */
        Getter,
        /**
         * Set method type.
         */
        Setter
    }
}
