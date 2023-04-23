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
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public static String fieldName(final String methodName) {
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
     * Retrieve child enum define in given define class
     *
     * @param enumClass Define class
     * @return enum data map
     */
    public static Map<String, Object> parseEnum(final Class<?> enumClass) {
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
     * Gets all declared fields.
     *
     * @param clazz the clazz
     * @return the all declared fields
     */
    public static List<Field> getAllDeclaredFields(final Class<?> clazz) {
        if (clazz == null) {
            return new ArrayList<>(0);
        }

        List<Field> fieldList = new ArrayList<>();
        Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> !staticMember(field))
                .forEach(fieldList::add);

        if (clazz.getSuperclass() != null) {
            ReflectionUtils.getAllDeclaredFields(clazz.getSuperclass())
                    .stream()
                    .filter(field -> !staticMember(field))
                    .forEach(fieldList::add);
        }

        return fieldList;
    }

    /**
     * Set the field represented by the supplied {@link Field field object} on
     * the specified {@link Object target object} to the specified
     * <code>value</code>.
     * In accordance with {@link Field#set(Object, Object)} semantics, the new value is
     * automatically unwrapped if the underlying field has a primitive type.
     * <p>Thrown exceptions are handled via a call to
     * {@link #handleReflectionException(Exception)}.
     *
     * @param field  the field to set
     * @param target the target object on which to set the field
     * @param value  the value to set; may be <code>null</code>
     */
    public static void setField(final Field field, final Object target, final Object value) {
        try {
            field.set(target, value);
        } catch (IllegalAccessException ex) {
            handleReflectionException(ex);
            throw new IllegalStateException(
                    "Unexpected reflection exception - " + ex.getClass().getName() + ": " + ex.getMessage());
        }
    }

    /**
     * Get the field represented by the supplied {@link Field field object} on
     * the specified {@link Object target object}.
     * In accordance with {@link Field#get(Object)} semantics, the returned value is
     * automatically wrapped if the underlying field has a primitive type.
     * <p>Thrown exceptions are handled via a call to
     * {@link #handleReflectionException(Exception)}.
     *
     * @param fieldName the name of field to get
     * @param target    the target object from which to get the field
     * @return the field's current value
     */
    public static Object getFieldValue(final String fieldName, final Object target) {
        if (fieldName == null || target == null) {
            return null;
        }
        try {
            Method getMethod = ClassUtils.getterMethod(fieldName, target.getClass());
            if (getMethod != null) {
                return getMethod.invoke(target);
            } else {
                Field field = getFieldIfAvailable(target.getClass(), fieldName);
                return ReflectionUtils.getFieldValue(field, target);
            }
        } catch (Exception ex) {
            handleReflectionException(ex);
            throw new IllegalStateException(
                    "Unexpected reflection exception - " + ex.getClass().getName() + ": " + ex.getMessage());
        }
    }

    /**
     * Get the field represented by the supplied {@link Field field object} on
     * the specified {@link Object target object}.
     * In accordance with {@link Field#get(Object)} semantics, the returned value is
     * automatically wrapped if the underlying field has a primitive type.
     * <p>Thrown exceptions are handled via a call to
     * {@link #handleReflectionException(Exception)}.
     *
     * @param field  the field to get
     * @param target the target object from which to get the field
     * @return the field's current value
     */
    public static Object getFieldValue(final Field field, final Object target) {
        if (field == null || target == null) {
            return null;
        }
        try {
            makeAccessible(field);
            return field.get(target);
        } catch (IllegalAccessException ex) {
            handleReflectionException(ex);
            throw new IllegalStateException(
                    "Unexpected reflection exception - " + ex.getClass().getName() + ": " + ex.getMessage());
        }
    }

    /**
     * Execute method object.
     *
     * @param methodName the method name
     * @param target     the target
     * @return the object
     * @throws IllegalArgumentException  the illegal argument exception
     */
    public static Object executeMethod(final String methodName, final Object target)
            throws IllegalArgumentException {
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
     * @throws IllegalArgumentException the illegal argument exception
     */
    public static Object executeMethod(final String methodName, final Object target,
                                       final Class<?>[] paramClasses, final Object... args)
            throws IllegalArgumentException {
        boolean noParam = paramClasses == null || paramClasses.length == 0;
        Method method = noParam
                ? ClassUtils.findMethod(target.getClass(), methodName)
                : ClassUtils.findMethod(target.getClass(), methodName, paramClasses);
        if (method == null) {
            throw new IllegalArgumentException("Method named : " + methodName + " does not exists");
        }
        ReflectionUtils.makeAccessible(method);

        Object returnObj = noParam ? invokeMethod(method, target) : invokeMethod(method, target, args);
        return method.getReturnType().equals(void.class) ? null : returnObj;
    }

    /**
     * Invoke the specified {@link Method} against the supplied target object
     * with no arguments.
     * The target object can be <code>null</code> when invoking a static {@link Method}.
     * <p>Thrown exceptions are handled via a call to {@link #handleReflectionException}.
     *
     * @param method the method to invoke
     * @param target the target object to invoke the method on
     * @return the invocation result, if any
     * @see #invokeMethod(java.lang.reflect.Method, Object, Object[])
     */
    public static Object invokeMethod(final Method method, final Object target) {
        return invokeMethod(method, target, null);
    }

    /**
     * Invoke the specified {@link Method} against the supplied target object
     * with the supplied arguments.
     * The target object can be <code>null</code> when invoking a static {@link Method}.
     * <p>Thrown exceptions are handled via a call to {@link #handleReflectionException}.
     *
     * @param method the method to invoke
     * @param target the target object to invoke the method on
     * @param args   the invocation arguments (maybe <code>null</code>)
     * @return the invocation result, if any
     */
    public static Object invokeMethod(final Method method, final Object target, final Object[] args) {
        try {
            return method.invoke(target, args);
        } catch (Exception ex) {
            handleReflectionException(ex);
        }
        throw new IllegalStateException("Should never get here");
    }

    /**
     * Handle the given reflection exception.
     * Should only be called if no checked exception is expected to be thrown by the target method.
     * <p>Throws the underlying RuntimeException or Error in case of an
     * InvocationTargetException with such a root cause.
     * Throw an IllegalStateException with an appropriate message else.
     *
     * @param ex the reflection exception to handle
     */
    public static void handleReflectionException(final Exception ex) {
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
     * Handle the given invocation target exception.
     * Should only be called if no checked exception is expected to be thrown by the target method.
     * <p>Throws the underlying RuntimeException or Error in case of such
     * a root cause.
     * Throws an IllegalStateException else.
     *
     * @param ex the invocation target exception to handle
     */
    public static void handleInvocationTargetException(final InvocationTargetException ex) {
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
    public static void rethrowRuntimeException(final Throwable ex) {
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
    public static void rethrowException(final Throwable ex) throws Exception {
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
    public static boolean declaresException(final Method method, final Class<?> exceptionType) {
        if (method == null) {
            throw new IllegalArgumentException("Method must not be null");
        }
        Class<?>[] declaredExceptions = method.getExceptionTypes();
        for (Class<?> declaredException : declaredExceptions) {
            if (declaredException.isAssignableFrom(exceptionType)) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    /**
     * Determine whether the given field/method is a "public" constant.
     *
     * @param member the field/method to check
     * @return is a "public" constant.
     */
    public static boolean publicMember(final Member member) {
        return Optional.ofNullable(member)
                .map(checkMember -> Modifier.isPublic(checkMember.getModifiers()))
                .orElse(Boolean.FALSE);
    }

    /**
     * Determine whether the given field/method is a "protected" constant.
     *
     * @param member the field/method to check
     * @return is a "protected" constant.
     */
    public static boolean protectedMember(final Member member) {
        return Optional.ofNullable(member)
                .map(checkMember -> Modifier.isProtected(checkMember.getModifiers()))
                .orElse(Boolean.FALSE);
    }

    /**
     * Determine whether the given field/method is a "protected" constant.
     *
     * @param member the field/method to check
     * @return is a "protected" constant.
     */
    public static boolean privateMember(final Member member) {
        return Optional.ofNullable(member)
                .map(checkMember -> Modifier.isPrivate(checkMember.getModifiers()))
                .orElse(Boolean.FALSE);
    }

    /**
     * Determine whether the given field/method is a "static" constant.
     *
     * @param member the field/method to check
     * @return is a "static" constant.
     */
    public static boolean staticMember(final Member member) {
        return Optional.ofNullable(member)
                .map(checkMember -> Modifier.isStatic(checkMember.getModifiers()))
                .orElse(Boolean.FALSE);
    }

    /**
     * Determine whether the given field/method is a "final" constant.
     *
     * @param member the field/method to check
     * @return is a "final" constant.
     */
    public static boolean finalMember(final Member member) {
        return Optional.ofNullable(member)
                .map(checkMember -> Modifier.isFinal(checkMember.getModifiers()))
                .orElse(Boolean.FALSE);
    }

    /**
     * Determine whether the given field is a "public static final" constant.
     *
     * @param member the field/method to check
     * @return is a "public static final" constant.
     */
    public static boolean isPublicStaticFinal(final Member member) {
        return publicMember(member) && staticMember(member) && finalMember(member);
    }

    /**
     * Make the given field accessible, explicitly setting it accessible if necessary.
     * The <code>setAccessible(true)</code> method is only called when actually necessary,
     * to avoid unnecessary conflicts with a JVM SecurityManager (if active).
     *
     * @param field the field to make accessible
     * @see java.lang.reflect.Field#setAccessible
     */
    public static void makeAccessible(final Field field) {
        if (privateMember(field) || protectedMember(field) ||
                !Modifier.isPublic(field.getDeclaringClass().getModifiers())) {
            field.setAccessible(Boolean.TRUE);
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
    public static void makeAccessible(final Method method) {
        if (privateMember(method) || protectedMember(method) ||
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
    public static void makeAccessible(final Constructor<?> ctor) {
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
     * @param callback    the callback to invoke for each method
     * @throws IllegalArgumentException the illegal argument exception
     * @see #doWithMethods(Class, MethodCallback, MethodFilter) #doWithMethods(Class, MethodCallback, MethodFilter)
     */
    public static void doWithMethods(Class<?> targetClass, final MethodCallback callback)
            throws IllegalArgumentException {
        doWithMethods(targetClass, callback, null);
    }

    /**
     * Perform the given callback operation on all matching methods of the
     * given class and superclasses.
     * <p>The same named method occurring on subclass and superclass will
     * appear twice, unless excluded by the specified {@link MethodFilter}.
     *
     * @param targetClass class to start looking at
     * @param callback    the callback to invoke for each method
     * @param filter      the filter that determines the methods to apply the callback to
     * @throws IllegalArgumentException the illegal argument exception
     */
    public static void doWithMethods(Class<?> targetClass, final MethodCallback callback, final MethodFilter filter)
            throws IllegalArgumentException {

        // Keep backing up the inheritance hierarchy.
        do {
            Arrays.stream(targetClass.getDeclaredMethods())
                    .filter(method -> (filter == null || filter.matches(method)))
                    .forEach(method -> {
                        try {
                            callback.doWith(method);
                        } catch (IllegalArgumentException ex) {
                            throw new IllegalStateException(
                                    "Shouldn't be illegal argument method '" + method.getName() + "': " + ex);
                        }
                    });
            targetClass = targetClass.getSuperclass();
        }
        while (targetClass != null);
    }

    /**
     * Get all declared methods on the given class and all superclasses.
     * Leaf class methods are included first.
     *
     * @param clazz given class
     * @return All declared method arrays
     * @throws IllegalArgumentException the illegal argument exception
     */
    public static Method[] getAllDeclaredMethods(final Class<?> clazz) throws IllegalArgumentException {
        final List<Method> list = new ArrayList<>(32);
        doWithMethods(clazz, list::add);
        return list.toArray(new Method[0]);
    }

    /**
     * Parse component type class.
     *
     * @param method the method
     * @return the class
     */
    public static Class<?> componentType(final Method method) {
        Class<?> returnClass = method.getReturnType();
        Class<?> returnItem = null;

        if (returnClass.isArray()) {
            returnItem = returnClass.getComponentType();
        } else if (List.class.isAssignableFrom(returnClass)) {
            returnItem = (Class<?>) ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
        }
        return returnItem;
    }

    /**
     * Invoke the given callback on all fields in the target class,
     * going up the class hierarchy to get all declared fields.
     *
     * @param targetClass the target class to analyze
     * @param callback    the callback to invoke for each field
     * @throws IllegalArgumentException the illegal argument exception
     */
    public static void doWithFields(final Class<?> targetClass, final FieldCallback callback)
            throws IllegalArgumentException {
        doWithFields(targetClass, callback, null);
    }

    /**
     * Invoke the given callback on all fields in the target class,
     * going up the class hierarchy to get all declared fields.
     *
     * @param targetClass the target class to analyze
     * @param callback    the callback to invoke for each field
     * @param filter      the filter that determines the fields to apply the callback to
     * @throws IllegalArgumentException the illegal argument exception
     */
    public static void doWithFields(final Class<?> targetClass, final FieldCallback callback, final FieldFilter filter)
            throws IllegalArgumentException {
        // Keep backing up the inheritance hierarchy.
        Class<?> currentClass = targetClass;
        do {
            // Copy each field declared on this class unless it's static or file.
            Field[] fields = currentClass.getDeclaredFields();
            for (Field field : fields) {
                // Skip static and final fields.
                if (filter != null && filter.matches(field)) {
                    try {
                        callback.doWith(field);
                    } catch (IllegalAccessException ex) {
                        throw new IllegalStateException(
                                "Shouldn't be illegal to access field '" + field.getName() + "': " + ex);
                    }
                }
            }
            currentClass = currentClass.getSuperclass();
        }
        while (currentClass != null && currentClass != Object.class);
    }

    /**
     * Given the source object and the destination, which must be the same class
     * or a subclass, copy all fields, including inherited fields.
     * Designed to work on objects with public no-arg constructors.
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
     * Retrieve the field object if available
     *
     * @param clazz     Define class
     * @param fieldName field name
     * @return Retrieve the field object or null if not exists
     */
    public static Field getFieldIfAvailable(final Class<?> clazz, final String fieldName) {
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
    public static void setField(final Object target, final Map<String, ?> parameterMap) {
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
    public static void setField(final String fieldName, final Object target, final Object value) {
        try {
            Method setMethod = ClassUtils.setterMethod(fieldName, target.getClass());
            if (setMethod != null) {
                setMethod.invoke(target, value);
            } else {
                Field field = getFieldIfAvailable(target.getClass(), fieldName);
                if (field != null) {
                    makeAccessible(field);
                    setField(field, target, value);
                }
            }
        } catch (Exception e) {
            if (ReflectionUtils.LOGGER.isDebugEnabled()) {
                ReflectionUtils.LOGGER.debug("Convert to Object set field value error! ", e);
                if (value != null) {
                    ReflectionUtils.LOGGER.debug(fieldName + ":" + value.getClass().getName());
                }
            }
        }
    }

    /**
     * Throws an IllegalStateException with the given exception as root cause.
     *
     * @param ex the unexpected exception
     */
    private static void handleUnexpectedException(final Throwable ex) {
        // Needs to avoid the chained constructor for JDK 1.4 compatibility.
        throw new IllegalStateException("Unexpected exception thrown", ex);
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
}
