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
import java.sql.SQLException;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.nervousync.commons.core.Globals;

/**
 * The type Reflection utils.
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: Jan 13, 2010 4:26:58 PM $
 */
public final class ReflectionUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReflectionUtils.class);
	
	private ReflectionUtils() {
		
	}

	/**
	 * Parse field name from getter/setter method name
	 *
	 * @param methodName method name
	 * @return parsed field name
	 */
	public static String parseFieldName(String methodName) {
		String fieldName = null;
		
		if (methodName != null) {
			if (methodName.startsWith("get") || methodName.startsWith("set")) {
				fieldName = methodName.substring(3);
			} else if (methodName.startsWith("is")) {
				fieldName = methodName.substring(2);
			}
		}
		
		if (fieldName != null) {
			fieldName = fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);
		}
		
		return fieldName;
	}

	/**
	 * Parse method name by given field name, field define class and method type
	 *
	 * @param fieldName  field name
	 * @param fieldClass field define class
	 * @param methodType method type
	 * @return parsed method name
	 * @see ReflectionUtils.MethodType
	 */
	public static String parseMethodName(String fieldName, Class<?> fieldClass, MethodType methodType) {
		String methodName = null;
		
		if (fieldName != null && fieldClass != null && methodType != null) {
			switch (methodType) {
			case GetMethod:
				if (boolean.class.equals(fieldClass)) {
					methodName = "is" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
				} else {
					methodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
				}
				break;
			case SetMethod:
				methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
				break;
			}
		}
		
		return methodName;
	}

	/**
	 * Retrieve child enum define in given define class
	 *
	 * @param enumClass Define class
	 * @return enum data map
	 */
	public static Map<String, Object> parseEnum(Class<?> enumClass) {
		Map<String, Object> enumMap = new HashMap<>();
		
		if (enumClass != null && enumClass.isEnum()) {
			Object[] constants = enumClass.getEnumConstants();
			
			for (Object object : constants) {
				enumMap.put(object.toString(), object);
			}
		}
		
		return enumMap;
	}

	/**
	 * Retrieve all declared field names
	 *
	 * @param clazz Define class
	 * @return List of field name
	 */
	public static List<String> getAllDeclaredFieldNames(Class<?> clazz) {
		if (clazz == null) {
			return new ArrayList<>(0);
		}
		
		List<String> fieldList = new ArrayList<>();
		
		for (Field field : clazz.getDeclaredFields()) {
			if ((field.getModifiers() & Modifier.STATIC) == 0) {
				fieldList.add(field.getName());
			}
		}
		
		if (clazz.getSuperclass() != null) {
			List<String> superFieldList = ReflectionUtils.getAllDeclaredFieldNames(clazz.getSuperclass());
			
			for (String fieldName : superFieldList) {
				if (!fieldList.contains(fieldName)) {
					fieldList.add(fieldName);
				}
			}
		}
		
		return fieldList;
	}

	/**
	 * Gets all declared fields.
	 *
	 * @param clazz the clazz
	 * @return the all declared fields
	 */
	public static List<Field> getAllDeclaredFields(Class<?> clazz) {
		if (clazz == null) {
			return new ArrayList<>(0);
		}

		List<Field> fieldList = new ArrayList<>();
		Arrays.stream(clazz.getDeclaredFields()).filter(field -> (field.getModifiers() & Modifier.STATIC) == 0)
				.forEach(fieldList::add);

		if (clazz.getSuperclass() != null) {
			ReflectionUtils.getAllDeclaredFields(clazz.getSuperclass()).stream()
					.filter(field -> (field.getModifiers() & Modifier.STATIC) == 0)
					.forEach(fieldList::add);
		}

		return fieldList;
	}

	/**
	 * Attempt to find a {@link Field field} on the supplied {@link Class} with
	 * the supplied <code>name</code>. Searches all superclasses up to {@link Object}.
	 *
	 * @param clazz the class to introspect
	 * @param name  the name of the field
	 * @return the corresponding Field object, or <code>null</code> if not found
	 */
	public static Field findField(Class<?> clazz, String name) {
		return findField(clazz, name, null);
	}

	/**
	 * Attempt to find a {@link Field field} on the supplied {@link Class} with
	 * the supplied <code>name</code> and/or {@link Class type}. Searches all
	 * superclasses up to {@link Object}.
	 *
	 * @param clazz the class to introspect
	 * @param name  the name of the field
	 * @param type  the type of the field (maybe <code>null</code> if name is specified)
	 * @return the corresponding Field object, or <code>null</code> if not found
	 */
	public static Field findField(Class<?> clazz, String name, Class<?> type) {
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
	 * Set the field represented by the supplied {@link Field field object} on
	 * the specified {@link Object target object} to the specified
	 * <code>value</code>. In accordance with
	 * {@link Field#set(Object, Object)} semantics, the new value is
	 * automatically unwrapped if the underlying field has a primitive type.
	 * <p>Thrown exceptions are handled via a call to
	 * {@link #handleReflectionException(Exception)}.
	 *
	 * @param field  the field to set
	 * @param target the target object on which to set the field
	 * @param value  the value to set; may be <code>null</code>
	 */
	public static void setField(Field field, Object target, Object value) {
		try {
			field.set(target, value);
		}
		catch (IllegalAccessException ex) {
			handleReflectionException(ex);
			throw new IllegalStateException(
					"Unexpected reflection exception - " + ex.getClass().getName() + ": " + ex.getMessage());
		}
	}

	/**
	 * Get the field represented by the supplied {@link Field field object} on
	 * the specified {@link Object target object}. In accordance with
	 * {@link Field#get(Object)} semantics, the returned value is
	 * automatically wrapped if the underlying field has a primitive type.
	 * <p>Thrown exceptions are handled via a call to
	 * {@link #handleReflectionException(Exception)}.
	 *
	 * @param fieldName the name of field to get
	 * @param target    the target object from which to get the field
	 * @return the field's current value
	 */
	public static Object getFieldValue(String fieldName, Object target) {
		if (fieldName == null || target == null) {
			return null;
		}
		try {
			Method getMethod = ReflectionUtils.retrieveMethod(fieldName, target.getClass(), MethodType.GetMethod);
			if (getMethod != null) {
				return getMethod.invoke(target);
			} else {
				Field field = getFieldIfAvailable(target.getClass(), fieldName);
				return ReflectionUtils.getFieldValue(field, target);
			}
		}
		catch (Exception ex) {
			handleReflectionException(ex);
			throw new IllegalStateException(
					"Unexpected reflection exception - " + ex.getClass().getName() + ": " + ex.getMessage());
		}
	}

	/**
	 * Get the field represented by the supplied {@link Field field object} on
	 * the specified {@link Object target object}. In accordance with
	 * {@link Field#get(Object)} semantics, the returned value is
	 * automatically wrapped if the underlying field has a primitive type.
	 * <p>Thrown exceptions are handled via a call to
	 * {@link #handleReflectionException(Exception)}.
	 *
	 * @param field  the field to get
	 * @param target the target object from which to get the field
	 * @return the field's current value
	 */
	public static Object getFieldValue(Field field, Object target) {
		if (field == null || target == null) {
			return null;
		}
		try {
			makeAccessible(field);
			return field.get(target);
		}
		catch (IllegalAccessException ex) {
			handleReflectionException(ex);
			throw new IllegalStateException(
					"Unexpected reflection exception - " + ex.getClass().getName() + ": " + ex.getMessage());
		}
	}

	/**
	 * Retrieve get method method.
	 *
	 * @param fieldName the field name
	 * @param beanClass the bean class
	 * @return the method
	 */
	public static Method retrieveGetMethod(String fieldName, Class<?> beanClass) {
		return ReflectionUtils.retrieveMethod(fieldName, beanClass, MethodType.GetMethod);
	}

	/**
	 * Retrieve set method method.
	 *
	 * @param fieldName the field name
	 * @param beanClass the bean class
	 * @return the method
	 */
	public static Method retrieveSetMethod(String fieldName, Class<?> beanClass) {
		return ReflectionUtils.retrieveMethod(fieldName, beanClass, MethodType.SetMethod);
	}

	/**
	 * Execute method object.
	 *
	 * @param methodName the method name
	 * @param target     the target
	 * @return the object
	 * @throws IllegalArgumentException  the illegal argument exception
	 * @throws IllegalAccessException    the illegal access exception
	 * @throws InvocationTargetException the invocation target exception
	 */
	public static Object executeMethod(String methodName, Object target)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		return executeMethod(methodName, target, new Class[]{});
	}

	/**
	 * Execute method object.
	 *
	 * @param methodName   the method name
	 * @param target       the target
	 * @param paramClasses the param classes
	 * @param args         the args
	 * @return the object
	 * @throws IllegalArgumentException  the illegal argument exception
	 * @throws IllegalAccessException    the illegal access exception
	 * @throws InvocationTargetException the invocation target exception
	 */
	public static Object executeMethod(String methodName, Object target, Class<?>[] paramClasses, Object... args)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Method method = ReflectionUtils.findMethod(target.getClass(), methodName, paramClasses);
		if (method == null) {
			throw new IllegalArgumentException("Method named : " + methodName + " does not exists");
		}
		ReflectionUtils.makeAccessible(method);
		
		Object returnObj = null;
		if (method.getReturnType().equals(void.class)) {
			method.invoke(target, args);
		} else {
			returnObj = method.invoke(target, args);
		}
		return returnObj;
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
	public static <T> Constructor<T> findConstructor(Class<T> clazz)
			throws SecurityException, NoSuchMethodException {
		return clazz.getDeclaredConstructor();
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
	public static <T> Constructor<T> findConstructor(Class<T> clazz, Class<?>[] paramTypes)
			throws SecurityException, NoSuchMethodException {
		return clazz.getDeclaredConstructor(paramTypes);
	}

	/**
	 * Attempt to find a {@link Method} on the supplied class with the supplied name
	 * and no parameters. Searches all superclasses up to <code>Object</code>.
	 * <p>Returns <code>null</code> if no {@link Method} can be found.
	 *
	 * @param clazz the class to introspect
	 * @param name  the name of the method
	 * @return the Method object, or <code>null</code> if none found
	 */
	public static Method findMethod(Class<?> clazz, String name) {
		return findMethod(clazz, name, new Class[0]);
	}

	/**
	 * Attempt to find a {@link Method} on the supplied class with the supplied name
	 * and parameter types. Searches all superclasses up to <code>Object</code>.
	 * <p>Returns <code>null</code> if no {@link Method} can be found.
	 *
	 * @param clazz      the class to introspect
	 * @param name       the name of the method
	 * @param paramTypes the parameter types of the method (maybe <code>null</code> to indicate any signature)
	 * @return the Method object, or <code>null</code> if none found
	 */
	public static Method findMethod(Class<?> clazz, String name, Class<?>[] paramTypes) {
		if (clazz == null) {
			throw new IllegalArgumentException("Class must not be null");
		}
		if (name == null) {
			throw new IllegalArgumentException("Method name must not be null");
		}
		Class<?> searchType = clazz;
		while (!Object.class.equals(searchType) && searchType != null) {
			try {
				return searchType.isInterface()
						? searchType.getMethod(name, paramTypes)
						: searchType.getDeclaredMethod(name, paramTypes);
			} catch (NoSuchMethodException ignored) {
			}
			searchType = searchType.getSuperclass();
		}
		return null;
	}

	/**
	 * Invoke the specified {@link Method} against the supplied target object
	 * with no arguments. The target object can be <code>null</code> when
	 * invoking a static {@link Method}.
	 * <p>Thrown exceptions are handled via a call to {@link #handleReflectionException}.
	 *
	 * @param method the method to invoke
	 * @param target the target object to invoke the method on
	 * @return the invocation result, if any
	 * @see #invokeMethod(java.lang.reflect.Method, Object, Object[])
	 */
	public static Object invokeMethod(Method method, Object target) {
		return invokeMethod(method, target, null);
	}

	/**
	 * Invoke the specified {@link Method} against the supplied target object
	 * with the supplied arguments. The target object can be <code>null</code>
	 * when invoking a static {@link Method}.
	 * <p>Thrown exceptions are handled via a call to {@link #handleReflectionException}.
	 *
	 * @param method the method to invoke
	 * @param target the target object to invoke the method on
	 * @param args   the invocation arguments (maybe <code>null</code>)
	 * @return the invocation result, if any
	 */
	public static Object invokeMethod(Method method, Object target, Object[] args) {
		try {
			return method.invoke(target, args);
		}
		catch (Exception ex) {
			handleReflectionException(ex);
		}
		throw new IllegalStateException("Should never get here");
	}

	/**
	 * Invoke the specified JDBC API {@link Method} against the supplied
	 * target object with no arguments.
	 *
	 * @param method the method to invoke
	 * @param target the target object to invoke the method on
	 * @return the invocation result, if any
	 * @throws SQLException the JDBC API SQLException to rethrow (if any)
	 * @see #invokeJdbcMethod(java.lang.reflect.Method, Object, Object[])
	 */
	public static Object invokeJdbcMethod(Method method, Object target) throws SQLException {
		return invokeJdbcMethod(method, target, null);
	}

	/**
	 * Invoke the specified JDBC API {@link Method} against the supplied
	 * target object with the supplied arguments.
	 *
	 * @param method the method to invoke
	 * @param target the target object to invoke the method on
	 * @param args   the invocation arguments (maybe <code>null</code>)
	 * @return the invocation result, if any
	 * @throws SQLException the JDBC API SQLException to rethrow (if any)
	 * @see #invokeMethod(Method, Object, Object[]) #invokeMethod(java.lang.reflect.Method, Object, Object[])
	 */
	public static Object invokeJdbcMethod(Method method, Object target, Object[] args) throws SQLException {
		try {
			return method.invoke(target, args);
		}
		catch (IllegalAccessException ex) {
			handleReflectionException(ex);
		}
		catch (InvocationTargetException ex) {
			if (ex.getTargetException() instanceof SQLException) {
				throw (SQLException) ex.getTargetException();
			}
			handleInvocationTargetException(ex);
		}
		throw new IllegalStateException("Should never get here");
	}

	/**
	 * Handle the given reflection exception. Should only be called if
	 * no checked exception is expected to be thrown by the target method.
	 * <p>Throws the underlying RuntimeException or Error in case of an
	 * InvocationTargetException with such a root cause. Throws an
	 * IllegalStateException with an appropriate message else.
	 *
	 * @param ex the reflection exception to handle
	 */
	public static void handleReflectionException(Exception ex) {
		if (ex instanceof NoSuchMethodException) {
			throw new IllegalStateException("Method not found: " + ex.getMessage());
		}
		if (ex instanceof IllegalAccessException) {
			throw new IllegalStateException("Could not access method: " + ex.getMessage());
		}
		if (ex instanceof InvocationTargetException) {
			handleInvocationTargetException((InvocationTargetException) ex);
		}
		if (ex instanceof RuntimeException) {
			throw (RuntimeException) ex;
		}
		handleUnexpectedException(ex);
	}

	/**
	 * Handle the given invocation target exception. Should only be called if
	 * no checked exception is expected to be thrown by the target method.
	 * <p>Throws the underlying RuntimeException or Error in case of such
	 * a root cause. Throws an IllegalStateException else.
	 *
	 * @param ex the invocation target exception to handle
	 */
	public static void handleInvocationTargetException(InvocationTargetException ex) {
		rethrowRuntimeException(ex.getTargetException());
	}

	/**
	 * Rethrow the given {@link Throwable exception}, which is presumably the
	 * <em>target exception</em> of an {@link InvocationTargetException}.
	 * Should only be called if no checked exception is expected to be thrown by
	 * the target method.
	 * <p>Rethrows the underlying exception cast to an {@link RuntimeException}
	 * or {@link Error} if appropriate; otherwise, throws an
	 * {@link IllegalStateException}.
	 *
	 * @param ex the exception to rethrow
	 * @throws RuntimeException the rethrown exception
	 */
	public static void rethrowRuntimeException(Throwable ex) {
		if (ex instanceof RuntimeException) {
			throw (RuntimeException) ex;
		}
		if (ex instanceof Error) {
			throw (Error) ex;
		}
		handleUnexpectedException(ex);
	}

	/**
	 * Rethrow the given {@link Throwable exception}, which is presumably the
	 * <em>target exception</em> of an {@link InvocationTargetException}.
	 * Should only be called if no checked exception is expected to be thrown by
	 * the target method.
	 * <p>Rethrows the underlying exception cast to an {@link Exception} or
	 * {@link Error} if appropriate; otherwise, throws an
	 * {@link IllegalStateException}.
	 *
	 * @param ex the exception to rethrow
	 * @throws Exception the rethrown exception (in case of a checked exception)
	 */
	public static void rethrowException(Throwable ex) throws Exception {
		if (ex instanceof Exception) {
			throw (Exception) ex;
		}
		if (ex instanceof Error) {
			throw (Error) ex;
		}
		handleUnexpectedException(ex);
	}

	/**
	 * Determine whether the given method explicitly declares the given exception
	 * or one of its superclasses, which means that an exception to that type
	 * can be propagated as-is within a reflective invocation.
	 *
	 * @param method        the declaring method
	 * @param exceptionType the exception to throw
	 * @return <code>true</code> if the exception can be thrown as-is; <code>false</code> if it needs to be wrapped
	 */
	public static boolean declaresException(Method method, Class<?> exceptionType) {
		if (method == null) {
			throw new IllegalArgumentException("Method must not be null");
		}
		Class<?>[] declaredExceptions = method.getExceptionTypes();
		for (Class<?> declaredException : declaredExceptions) {
			if (declaredException.isAssignableFrom(exceptionType)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Determine whether the given field/method is a "public" constant.
	 *
	 * @param member 	the field/method to check
	 * @return 			is a "public" constant.
	 */
	public static boolean publicMember(Member member) {
		return Optional.ofNullable(member)
				.map(checkMember -> Modifier.isPublic(checkMember.getModifiers()))
				.orElse(Boolean.FALSE);
	}

	/**
	 * Determine whether the given field/method is a "protected" constant.
	 *
	 * @param member 	the field/method to check
	 * @return 			is a "protected" constant.
	 */
	public static boolean protectedMember(Member member) {
		return Optional.ofNullable(member)
				.map(checkMember -> Modifier.isProtected(checkMember.getModifiers()))
				.orElse(Boolean.FALSE);
	}

	/**
	 * Determine whether the given field/method is a "protected" constant.
	 *
	 * @param member 	the field/method to check
	 * @return 			is a "protected" constant.
	 */
	public static boolean privateMember(Member member) {
		return Optional.ofNullable(member)
				.map(checkMember -> Modifier.isPrivate(checkMember.getModifiers()))
				.orElse(Boolean.FALSE);
	}

	/**
	 * Determine whether the given field/method is a "static" constant.
	 *
	 * @param member 	the field/method to check
	 * @return 			is a "static" constant.
	 */
	public static boolean staticMember(Member member) {
		return Optional.ofNullable(member)
				.map(checkMember -> Modifier.isStatic(checkMember.getModifiers()))
				.orElse(Boolean.FALSE);
	}

	/**
	 * Determine whether the given field/method is not a "static" constant.
	 *
	 * @param member 	the field/method to check
	 * @return 			is not a "static" constant.
	 */
	public static boolean nonStaticMember(Member member) {
		return !ReflectionUtils.staticMember(member);
	}

	/**
	 * Determine whether the given field/method is a "final" constant.
	 *
	 * @param member 	the field/method to check
	 * @return 			is a "final" constant.
	 */
	public static boolean finalMember(Member member) {
		return Optional.ofNullable(member)
				.map(checkMember -> Modifier.isFinal(checkMember.getModifiers()))
				.orElse(Boolean.FALSE);
	}

	/**
	 * Determine whether the given field/method is not a "final" constant.
	 *
	 * @param member 	the field/method to check
	 * @return 			is not a "final" constant.
	 */
	public static boolean nonFinalMember(Member member) {
		return !ReflectionUtils.finalMember(member);
	}

	/**
	 * Determine whether the given field is a "public static final" constant.
	 *
	 * @param member 	the field/method to check
	 * @return 			is a "public static final" constant.
	 */
	public static boolean isPublicStaticFinal(Member member) {
		if (member != null) {
			int modifiers = member.getModifiers();
			return (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers));
		}
		return Boolean.FALSE;
	}

	/**
	 * Make the given field accessible, explicitly setting it accessible if necessary.
	 * The <code>setAccessible(true)</code> method is only called when actually necessary,
	 * to avoid unnecessary conflicts with a JVM SecurityManager (if active).
	 *
	 * @param field the field to make accessible
	 * @see java.lang.reflect.Field#setAccessible
	 */
	public static void makeAccessible(Field field) {
		if (!Modifier.isPublic(field.getModifiers()) ||
				!Modifier.isPublic(field.getDeclaringClass().getModifiers())) {
			field.setAccessible(true);
		}
	}

	/**
	 * Make the given method accessible, explicitly setting it accessible if necessary.
	 * The <code>setAccessible(true)</code> method is only called when actually necessary,
	 * to avoid unnecessary conflicts with a JVM SecurityManager (if active).
	 *
	 * @param method the method to make accessible
	 * @see java.lang.reflect.Method#setAccessible
	 */
	public static void makeAccessible(Method method) {
		if (!Modifier.isPublic(method.getModifiers()) ||
				!Modifier.isPublic(method.getDeclaringClass().getModifiers())) {
			method.setAccessible(true);
		}
	}

	/**
	 * Make the given constructor accessible, explicitly setting it accessible if necessary.
	 * The <code>setAccessible(true)</code> method is only called when actually necessary,
	 * to avoid unnecessary conflicts with a JVM SecurityManager (if active).
	 *
	 * @param ctor the constructor to make accessible
	 * @see java.lang.reflect.Constructor#setAccessible
	 */
	public static void makeAccessible(Constructor<?> ctor) {
		if (!Modifier.isPublic(ctor.getModifiers()) ||
				!Modifier.isPublic(ctor.getDeclaringClass().getModifiers())) {
			ctor.setAccessible(true);
		}
	}


	/**
	 * Perform the given callback operation on all matching methods of the
	 * given class and superclasses.
	 * <p>The same named method occurring on subclass and superclass will
	 * appear twice, unless excluded by a {@link MethodFilter}.
	 *
	 * @param targetClass class to start looking at
	 * @param mc          the callback to invoke for each method
	 * @throws IllegalArgumentException the illegal argument exception
	 * @see #doWithMethods(Class, MethodCallback, MethodFilter) #doWithMethods(Class, MethodCallback, MethodFilter)
	 */
	public static void doWithMethods(Class<?> targetClass, MethodCallback mc) throws IllegalArgumentException {
		doWithMethods(targetClass, mc, null);
	}

	/**
	 * Perform the given callback operation on all matching methods of the
	 * given class and superclasses.
	 * <p>The same named method occurring on subclass and superclass will
	 * appear twice, unless excluded by the specified {@link MethodFilter}.
	 *
	 * @param targetClass class to start looking at
	 * @param mc          the callback to invoke for each method
	 * @param mf          the filter that determines the methods to apply the callback to
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public static void doWithMethods(Class<?> targetClass, MethodCallback mc, MethodFilter mf)
			throws IllegalArgumentException {

		// Keep backing up the inheritance hierarchy.
		do {
			Method[] methods = targetClass.getDeclaredMethods();
			for (Method method : methods) {
				if (mf != null && !mf.matches(method)) {
					continue;
				}
				try {
					mc.doWith(method);
				} catch (IllegalArgumentException ex) {
					throw new IllegalStateException(
							"Shouldn't be illegal argument method '" + method.getName() + "': " + ex);
				}
			}
			targetClass = targetClass.getSuperclass();
		}
		while (targetClass != null);
	}

	/**
	 * Get all declared methods on the leaf class and all superclasses.
	 * Leaf class methods are included first.
	 *
	 * @param leafClass leaf class
	 * @return All declared method arrays
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public static Method[] getAllDeclaredMethods(Class<?> leafClass) throws IllegalArgumentException {
		final List<Method> list = new ArrayList<>(32);
		doWithMethods(leafClass, list::add);
		return list.toArray(new Method[0]);
	}

	/**
	 * Parse component type class.
	 *
	 * @param method the method
	 * @return the class
	 */
	public static Class<?> parseComponentType(Method method) {
		Class<?> returnClass = method.getReturnType();
		Class<?> returnItem = null;

		if (returnClass.isArray()) {
			returnItem = returnClass.getComponentType();
		} else if (List.class.isAssignableFrom(returnClass)) {
			returnItem = (Class<?>)((ParameterizedType)method.getGenericReturnType()).getActualTypeArguments()[0];
		}
		return returnItem;
	}

	/**
	 * Parse component type from field
	 *
	 * @param field	Field instance
	 * @return		Parsed component type or null if not a list or array
	 */
	public static Class<?> parseComponentType(Field field) {
		Class<?> fieldClass = field.getType();
		Class<?> componentClass = null;

		if (fieldClass.isArray()) {
			componentClass = fieldClass.getComponentType();
		} else if (List.class.isAssignableFrom(fieldClass)) {
			componentClass = (Class<?>)((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0];
		}
		return componentClass;
	}

	/**
	 * Invoke the given callback on all fields in the target class,
	 * going up the class hierarchy to get all declared fields.
	 *
	 * @param targetClass the target class to analyze
	 * @param fc          the callback to invoke for each field
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public static void doWithFields(Class<?> targetClass, FieldCallback fc) throws IllegalArgumentException {
		doWithFields(targetClass, fc, null);
	}

	/**
	 * Invoke the given callback on all fields in the target class,
	 * going up the class hierarchy to get all declared fields.
	 *
	 * @param targetClass the target class to analyze
	 * @param fc          the callback to invoke for each field
	 * @param ff          the filter that determines the fields to apply the callback to
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public static void doWithFields(Class<?> targetClass, FieldCallback fc, FieldFilter ff)
			throws IllegalArgumentException {

		// Keep backing up the inheritance hierarchy.
		do {
			// Copy each field declared on this class unless it's static or file.
			Field[] fields = targetClass.getDeclaredFields();
			for (Field field : fields) {
				// Skip static and final fields.
				if (ff != null && ff.matches(field)) {
					try {
						fc.doWith(field);
					} catch (IllegalAccessException ex) {
						throw new IllegalStateException(
								"Shouldn't be illegal to access field '" + field.getName() + "': " + ex);
					}
				}
			}
			targetClass = targetClass.getSuperclass();
		}
		while (targetClass != null && targetClass != Object.class);
	}

	/**
	 * Given the source object and the destination, which must be the same class
	 * or a subclass, copy all fields, including inherited fields. Designed to
	 * work on objects with public no-arg constructors.
	 *
	 * @param src  source object
	 * @param dest target object
	 * @throws IllegalArgumentException if the arguments are incompatible
	 */
	public static void shallowCopyFieldState(final Object src, final Object dest) throws IllegalArgumentException {
		if (src == null) {
			throw new IllegalArgumentException("Source for field copy cannot be null");
		}
		if (dest == null) {
			throw new IllegalArgumentException("Destination for field copy cannot be null");
		}
		if (!src.getClass().isAssignableFrom(dest.getClass())) {
			throw new IllegalArgumentException("Destination class [" + dest.getClass().getName() +
					"] must be same or subclass as source class [" + src.getClass().getName() + "]");
		}
		doWithFields(src.getClass(), field -> {
			makeAccessible(field);
			Object srcValue = field.get(src);
			field.set(dest, srcValue);
		}, COPYABLE_FIELDS);
	}

	/**
	 * Retrieve field object if available
	 *
	 * @param clazz     Define class
	 * @param fieldName field name
	 * @return Retrieve field object or null if not exists
	 */
	public static Field getFieldIfAvailable(Class<?> clazz, String fieldName) {
		if (clazz == null) {
			return null;
		}
		
		try {
			return clazz.getDeclaredField(fieldName);
		} catch (Exception e) {
			return getFieldIfAvailable(clazz.getSuperclass(), fieldName);
		}
	}

	/**
	 * Set field value values
	 *
	 * @param target       target object
	 * @param parameterMap field value map
	 */
	public static void setField(Object target, Map<String, ?> parameterMap) {
		if (target == null || parameterMap == null) {
			return;
		}

		parameterMap.forEach((key, value) -> {
			Object fieldValue;
			if (value.getClass().isArray()) {
				if (((Object[]) value).length == 1) {
					fieldValue = ((Object[]) value)[0];
				} else {
					fieldValue = value;
				}
			} else {
				fieldValue = value;
			}

			setField(key, target, fieldValue);
		});
	}

	/**
	 * Set field value
	 *
	 * @param fieldName field name
	 * @param target    target object
	 * @param value     field value
	 */
	public static void setField(String fieldName, Object target, Object value) {
		try {
			Method setMethod = ReflectionUtils.retrieveMethod(fieldName, target.getClass(), MethodType.SetMethod);
			if (setMethod != null) {
				setMethod.invoke(target, value);
			} else {
				Field field = getFieldIfAvailable(target.getClass(), fieldName);
				if (field == null) {
					return;
				}
				
				Object object = null;
				
				if (value != null) {
					Class<?> clazz = field.getType();
					if (!value.getClass().equals(clazz)) {
						int length;
						if (value.getClass().isArray()) {
							length = ((String[])value).length;
						} else {
							length = 1;
						}
						
						if (clazz.isArray()) {
							Class<?> arrayClass = clazz.getComponentType();
							object = Array.newInstance(arrayClass, length);
							
							if (arrayClass.isPrimitive()) {
								if (arrayClass.equals(int.class)) {
									if (length == 1) {
										Array.set(object, 0, Integer.parseInt(value.toString()));
									} else {
										for (int i = 0 ; i < length ; i++) {
											Array.set(object, i, Integer.parseInt(((String[])value)[i]));
										}
									}
								} else if (arrayClass.equals(double.class)) {
									if (length == 1) {
										Array.set(object, 0, Double.parseDouble(value.toString()));
									} else {
										for (int i = 0 ; i < length ; i++) {
											Array.set(object, i, Double.parseDouble(((String[])value)[i]));
										}
									}
								} else if (arrayClass.equals(float.class)) {
									if (length == 1) {
										Array.set(object, 0, Float.parseFloat(value.toString()));
									} else {
										for (int i = 0 ; i < length ; i++) {
											Array.set(object, i, Float.parseFloat(((String[])value)[i]));
										}
									}
								} else if (arrayClass.equals(long.class)) {
									if (length == 1) {
										Array.set(object, 0, Long.parseLong(value.toString()));
									} else {
										for (int i = 0 ; i < length ; i++) {
											Array.set(object, i, Long.parseLong(((String[])value)[i]));
										}
									}
								} else if (arrayClass.equals(short.class)) {
									if (length == 1) {
										Array.set(object, 0, Short.parseShort(value.toString()));
									} else {
										for (int i = 0 ; i < length ; i++) {
											Array.set(object, i, Short.parseShort(((String[])value)[i]));
										}
									}
								} else if (arrayClass.equals(boolean.class)) {
									if (length == 1) {
										Array.set(object, 0, Boolean.parseBoolean(value.toString()));
									} else {
										for (int i = 0 ; i < length ; i++) {
											Array.set(object, i, Boolean.parseBoolean(((String[])value)[i]));
										}
									}
								}
							} else {
								if (arrayClass.equals(String.class)) {
									if (length == 1) {
										Array.set(object, 0, value.toString());
									} else {
										object = value;
									}
								} else if (arrayClass.equals(Integer.class)) {
									if (length == 1) {
										Array.set(object, 0, Integer.valueOf(value.toString()));
									} else {
										for (int i = 0 ; i < length ; i++) {
											Array.set(object, i, Integer.valueOf(((String[])value)[i]));
										}
									}
								} else if (arrayClass.equals(Float.class)) {
									if (length == 1) {
										Array.set(object, 0, Float.valueOf(value.toString()));
									} else {
										for (int i = 0 ; i < length ; i++) {
											Array.set(object, i, Float.valueOf(((String[])value)[i]));
										}
									}
								} else if (arrayClass.equals(Double.class)) {
									if (length == 1) {
										Array.set(object, 0, Double.valueOf(value.toString()));
									} else {
										for (int i = 0 ; i < length ; i++) {
											Array.set(object, i, Double.valueOf(((String[])value)[i]));
										}
									}
								} else if (arrayClass.equals(Long.class)) {
									if (length == 1) {
										Array.set(object, 0, Long.valueOf(value.toString()));
									} else {
										for (int i = 0 ; i < length ; i++) {
											Array.set(object, i, Long.valueOf(((String[])value)[i]));
										}
									}
								} else if (arrayClass.equals(Boolean.class)) {
									if (length == 1) {
										Array.set(object, 0, Boolean.valueOf(value.toString()));
									} else {
										for (int i = 0 ; i < length ; i++) {
											Array.set(object, i, Boolean.valueOf(((String[])value)[i]));
										}
									}
								} else {
									if (length == 1) {
										Array.set(object, 0, value);
									} else {
										for (int i = 0 ; i < length ; i++) {
											Array.set(object, i, ((Object[])value)[i]);
										}
									}
								}
							}
						} else if (clazz.isPrimitive()) {
							//	Basic data type	int, double, float, long, short
							if (clazz.equals(int.class)) {
								object = Integer.parseInt(value.toString());
							} else if (clazz.equals(double.class)) {
								object = Double.parseDouble(value.toString());
							} else if (clazz.equals(float.class)) {
								object = Float.parseFloat(value.toString());
							} else if (clazz.equals(long.class)) {
								object = Long.parseLong(value.toString());
							} else if (clazz.equals(short.class)) {
								object = Short.parseShort(value.toString());
							} else if (clazz.equals(boolean.class)) {
								object = Boolean.parseBoolean(value.toString());
							}
						} else {
							if (clazz.equals(Integer.class)) {
								object = Integer.valueOf(value.toString());
							} else if (clazz.equals(Float.class)) {
								object = Float.valueOf(value.toString());
							} else if (clazz.equals(Double.class)) {
								object = Double.valueOf(value.toString());
							} else if (clazz.equals(Long.class)) {
								object = Long.valueOf(value.toString());
							} else if (clazz.equals(Short.class)) {
								object = Short.valueOf(value.toString());
							} else if (clazz.equals(Boolean.class)) {
								object = Boolean.valueOf(value.toString());
							} else {
								object = value;
							}
						}
					} else {
						object = value;
					}
				}
				
				makeAccessible(field);
				setField(field, target, object);
			}
		} catch (Exception e) {
			if (ReflectionUtils.LOGGER.isDebugEnabled()) {
				ReflectionUtils.LOGGER.debug("Convert to Object set field value error! ", e);
				if (value != null) {
					ReflectionUtils.LOGGER.debug(fieldName + "" + value.getClass().getName());
				}
			}
		}
	}

	/**
	 * Throws an IllegalStateException with the given exception as root cause.
	 * @param ex the unexpected exception
	 */
	private static void handleUnexpectedException(Throwable ex) {
		// Needs to avoid the chained constructor for JDK 1.4 compatibility.
		throw new IllegalStateException("Unexpected exception thrown", ex);
	}

	private static Method retrieveMethod(String fieldName, Class<?> targetClass, MethodType methodType) {
		Field field = ReflectionUtils.getFieldIfAvailable(targetClass, fieldName);
		if (field == null) {
			return null;
		}

		String methodName = ReflectionUtils.convertFieldNameToMethodName(field, methodType);
		switch(methodType) {
		case GetMethod:
			return ReflectionUtils.findMethod(targetClass, methodName);
		case SetMethod:
			return ReflectionUtils.findMethod(targetClass, methodName, new Class<?>[]{field.getType()});
		}

		return null;
	}

	private static String convertFieldNameToMethodName(Field field, MethodType methodType) {
		StringBuilder methodName = new StringBuilder();
		switch (methodType) {
			case GetMethod:
				if (boolean.class.equals(field.getType())) {
					methodName.append("is");
				} else {
					methodName.append("get");
				}
				break;
			case SetMethod:
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
	 * Action to take on each method.
	 */
	public interface MethodCallback {

		/**
		 * Perform an operation using the given method.
		 *
		 * @param method the method to operate on
		 * @throws IllegalArgumentException @see java.lang.IllegalArgumentException
		 */
		void doWith(Method method) throws IllegalArgumentException;
	}


	/**
	 * Callback optionally used to method fields to be operated on by a method callback.
	 */
	public interface MethodFilter {

		/**
		 * Determine whether the given method matches.
		 *
		 * @param method the method to check
		 * @return check result
		 */
		boolean matches(Method method);
	}


	/**
	 * Callback interface invoked on each field in the hierarchy.
	 */
	public interface FieldCallback {

		/**
		 * Perform an operation using the given field.
		 *
		 * @param field the field to operate on
		 * @throws IllegalArgumentException @see java.lang.IllegalArgumentException
		 * @throws IllegalAccessException   @see java.lang.IllegalAccessException
		 */
		void doWith(Field field) throws IllegalArgumentException, IllegalAccessException;
	}

	/**
	 * Callback optionally used to filter fields to be operated on by a field callback.
	 */
	public interface FieldFilter {

		/**
		 * Determine whether the given field matches.
		 *
		 * @param field the field to check
		 * @return check result
		 */
		boolean matches(Field field);
	}


	/**
	 * Pre-built FieldFilter that matches all non-static, non-final fields.
	 */
	public static final FieldFilter COPYABLE_FIELDS = field -> !(Modifier.isStatic(field.getModifiers()) ||
			Modifier.isFinal(field.getModifiers()));

	/**
	 * The enum Method type.
	 */
	public enum MethodType {
		/**
		 * Get method type.
		 */
		GetMethod,
		/**
		 * Set method type.
		 */
		SetMethod
	}
}
