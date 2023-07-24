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

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import org.nervousync.annotations.beans.BeanProperty;
import org.nervousync.beans.converter.Adapter;
import org.nervousync.beans.converter.impl.beans.AbstractBeanAdapter;
import org.nervousync.commons.Globals;
import org.nervousync.enumerations.beans.DataFlow;

/**
 * <h2 class="en">JavaBean Utilities</h2>
 * <span class="en">
 *     <span>Current utilities implements features:</span>
 *     <ul>Copy object fields value from source object to target object based field name</ul>
 *     <ul>Copy object fields value from source object array to target object based annotation: BeanProperty</ul>
 *     <ul>Copy object fields value from source object to target object arrays based annotation: BeanProperty</ul>
 * </span>
 * <h2 class="zh-CN">JavaBean工具集</h2>
 * <span class="zh-CN">
 *     <span>此工具集实现以下功能:</span>
 *     <ul>根据属性名称从源数据对象复制数据到目标数据对象</ul>
 *     <ul>根据BeanProperty注解从源数据对象数组复制数据到目标数据对象</ul>
 *     <ul>根据BeanProperty注解从源数据对象复制数据到目标数据对象数组</ul>
 * </span>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: Jun 25, 2015 14:55:15 $
 */
public final class BeanUtils {
    /**
     * <span class="en">Logger instance</span>
     * <span class="zh-CN">日志实例</span>
     */
    private static final LoggerUtils.Logger LOGGER = LoggerUtils.getLogger(BeanUtils.class);
    /**
     * <span class="en">Registered JavaBean mappings</span>
     * <span class="zh-CN">已注册的JavaBean映射</span>
     */
    private static final Map<String, BeanMapping> BEAN_CONFIG_MAP = new HashMap<>();
    /**
     * <h3 class="en">Private constructor for BeanUtils</h3>
     * <h3 class="zh-CN">JavaBean工具集的私有构造函数</h3>
     */
    private BeanUtils() {
    }
    /**
     * <h3 class="en">Remove registered JavaBean class</h3>
     * <h3 class="zh-CN">移除已注册的JavaBean类映射</h3>
     *
     * @param classes <span class="en">Want removed JavaBean class array</span>
     *                <span class="zh-CN">需要移除的JavaBean类数组</span>
     */
    public static void removeBeanConfig(final Class<?>... classes) {
        Arrays.asList(classes).forEach(clazz -> BEAN_CONFIG_MAP.remove(ClassUtils.originalClassName(clazz)));
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Utils", "Register_Bean_Config_Count_Debug", BEAN_CONFIG_MAP.size());
        }
    }
    /**
     * <h3 class="en">Copy the map values into the target JavaBean instance</h3>
     * <p class="en">Data mapping to JavaBean field identified by map key</p>
     * <h3 class="zh-CN">复制Map中的值到目标JavaBean实例</h3>
     * <p class="zh-CN">数据使用Map的键值映射到JavaBean属性</p>
     *
     * @param originalMap  <span class="en">Original data map</span>
     *                     <span class="zh-CN">来源数据Map</span>
     * @param targetObject <span class="en">Target JavaBean instance</span>
     *                     <span class="zh-CN">目标JavaBean实例</span>
     */
    public static void copyProperties(final Map<String, Object> originalMap, final Object targetObject) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Utils", "Data_Map_Debug",
                    StringUtils.objectToString(originalMap, StringUtils.StringType.JSON, Boolean.TRUE));
        }
        checkRegister(targetObject.getClass());
        Optional.ofNullable(BEAN_CONFIG_MAP.get(ClassUtils.originalClassName(targetObject.getClass())))
                .ifPresent(beanMapping -> beanMapping.copyData(targetObject, originalMap));
    }
    /**
     * <h3 class="en">Copy the property values of the given source bean arrays into the target bean</h3>
     * <p class="en">Data mapping by field annotated with org.nervousync.annotations.beans.BeanProperty</p>
     * <h3 class="zh-CN">从给定的JavaBean对象数组复制映射的属性值到目标对象</h3>
     * <p class="en">数据映射配置于使用org.nervousync.annotations.beans.BeanProperty注解的属性</p>
     *
     * @param targetObject    <span class="en">Target JavaBean instance</span>
     *                        <span class="zh-CN">目标JavaBean实例</span>
     * @param originalObjects <span class="en">Original JavaBean instance array</span>
     *                        <span class="zh-CN">数据源JavaBean实例数组</span>
     */
    public static void copyFrom(final Object targetObject, final Object... originalObjects) {
        if (targetObject == null || originalObjects.length == 0) {
            return;
        }
        checkRegister(targetObject.getClass());
        Optional.ofNullable(BEAN_CONFIG_MAP.get(ClassUtils.originalClassName(targetObject.getClass())))
                .ifPresent(beanMapping -> beanMapping.copyData(DataFlow.IN, targetObject, originalObjects));
    }
    /**
     * <h3 class="en">Copy the property values into the given target JavaBean instance arrays</h3>
     * <p class="en">Data mapping by field annotated with org.nervousync.annotations.beans.Mappings</p>
     * <h3 class="zh-CN">从源对象复制属性值到给定的JavaBean对象数组</h3>
     * <p class="en">数据映射配置于使用org.nervousync.annotations.beans.Mappings注解的属性</p>
     *
     * @param originalObject <span class="en">Original JavaBean instance</span>
     *                       <span class="zh-CN">数据源JavaBean实例</span>
     * @param targetObjects  <span class="en">Target JavaBean instance array</span>
     *                       <span class="zh-CN">目标JavaBean实例数组</span>
     */
    public static void copyTo(final Object originalObject, final Object... targetObjects) {
        if (originalObject == null || targetObjects.length == 0) {
            return;
        }
        checkRegister(originalObject.getClass());
        Optional.ofNullable(BEAN_CONFIG_MAP.get(ClassUtils.originalClassName(originalObject.getClass())))
                .ifPresent(beanMapping -> beanMapping.copyData(DataFlow.OUT, originalObject, targetObjects));
    }
    /**
     * <h3 class="en">Check and register JavaBean mapping configs</h3>
     * <p class="en">If given JavaBean class instance not registered, generate BeanMapping instance and register the given JavaBean class mapping configure</p>
     * <h3 class="zh-CN">检查并注册JavaBean映射配置</h3>
     * <p class="en">如果给定的JavaBean类没有注册映射配置，则生成映射配置对象，并执行注册</p>
     *
     * @param clazz <span class="en">Given JavaBean class instance</span>
     *              <span class="zh-CN">给定的JavaBean类对象</span>
     */
    private static void checkRegister(final Class<?> clazz) {
        Optional.of(ClassUtils.originalClassName(clazz))
                .filter(StringUtils::notBlank)
                .filter(className -> !BEAN_CONFIG_MAP.containsKey(className))
                .ifPresent(className -> BEAN_CONFIG_MAP.put(className, new BeanMapping(clazz)));
    }
    /**
     * <h2 class="en">JavaBean mapping configure define</h2>
     * <p class="en">Private inner class for define JavaBean mapping configure</p>
     * <h2 class="zh-CN">JavaBean映射配置定义</h2>
     * <p class="zh-CN">定义JavaBean映射配置的私有内部类</p>
     */
    private static final class BeanMapping {
        /**
         * <span class="en">JavaBean field mapping configure list</span>
         * <span class="zh-CN">JavaBean属性映射配置列表</span>
         */
        private final List<FieldMapping> fieldMappings;
        /**
         * <h3 class="en">Constructor for parse given JavaBean class instance and generate BeanMapping instance</h3>
         * <h3 class="zh-CN">构造方法用于解析给定的JavaBean类对象，并生成BeanMapping对象</h3>
         *
         * @param beanClass <span class="en">Given JavaBean class instance</span>
         *                  <span class="zh-CN">给定的JavaBean类对象</span>
         */
        BeanMapping(final Class<?> beanClass) {
            this.fieldMappings = new ArrayList<>();
            ReflectionUtils.getAllDeclaredFields(beanClass, Boolean.TRUE)
                    .forEach(field -> this.fieldMappings.add(new FieldMapping(field)));
        }
        /**
         * <h3 class="en">Copy property value from data map</h3>
         * <h3 class="zh-CN">从数据Map复制属性数据</h3>
         *
         * @param targetObject <span class="en">Target JavaBean instance</span>
         *                     <span class="zh-CN">目标JavaBean实例</span>
         * @param originalMap  <span class="en">Original data map</span>
         *                     <span class="zh-CN">来源数据Map</span>
         */
        void copyData(final Object targetObject, final Map<String, Object> originalMap) {
            this.fieldMappings.forEach(fieldMapping -> fieldMapping.copyData(targetObject, originalMap));
        }
        /**
         * <h3 class="en">Copy the property values between the JavaBean instance and JavaBean instance arrays</h3>
         * <h3 class="zh-CN">在JavaBean对象实例和JavaBean对象实例数组间复制数据</h3>
         *
         * @see org.nervousync.enumerations.beans.DataFlow
         * @param dataFlow  <span class="en">Data flow, <code>IN</code> from arrays to object, <code>OUT</code> from object to arrays</span>
         *                  <span class="zh-CN">数据流向<，<code>IN</code>从实例数组复制数据到实例对象，<code>OUT</code>从实例对象复制数据到实例数组/span>
         * @param object    <span class="en">JavaBean instance</span>
         *                  <span class="zh-CN">JavaBean实例</span>
         * @param objects   <span class="en">JavaBean instance array</span>
         *                  <span class="zh-CN">JavaBean实例数组</span>
         */
        void copyData(final DataFlow dataFlow, final Object object, final Object... objects) {
            this.fieldMappings.forEach(fieldMapping -> fieldMapping.copyData(dataFlow, object, objects));
        }
    }
    /**
     * <h2 class="en">JavaBean field mapping configure define</h2>
     * <p class="en">Private inner class for define JavaBean field mapping configure</p>
     * <h2 class="zh-CN">JavaBean属性映射配置定义</h2>
     * <p class="zh-CN">定义JavaBean属性映射配置的私有内部类</p>
     */
    private static final class FieldMapping {
        /**
         * <span class="en">JavaBean field name</span>
         * <span class="zh-CN">JavaBean属性名</span>
         */
        private final String fieldName;
        /**
         * <span class="en">JavaBean field type class</span>
         * <span class="zh-CN">JavaBean属性数据类型</span>
         */
        private final Class<?> fieldType;
        /**
         * <span class="en">JavaBean field data mapping configure</span>
         * <span class="zh-CN">JavaBean属性数据映射配置</span>
         */
        private final List<PropertyMapping> propertyMappings;
        /**
         * <h3 class="en">Constructor for parse given JavaBean field instance and generate FieldMapping instance</h3>
         * <h3 class="zh-CN">构造方法用于解析给定的JavaBean属性对象，并生成FieldMapping对象</h3>
         *
         * @param field <span class="en">JavaBean field instance</span>
         *              <span class="zh-CN">JavaBean类属性对象</span>
         */
        FieldMapping(final Field field) {
            this.fieldName = field.getName();
            this.fieldType = field.getType();
            this.propertyMappings = new ArrayList<>();
            Arrays.asList(field.getAnnotationsByType(BeanProperty.class)).forEach(this::registerProperty);
            this.propertyMappings.sort((o1, o2) -> o2.compare(o1));
        }
        /**
         * <h3 class="en">Copy property value from data map</h3>
         * <h3 class="zh-CN">从数据Map复制属性数据</h3>
         *
         * @param targetObject <span class="en">Target JavaBean instance</span>
         *                     <span class="zh-CN">目标JavaBean实例</span>
         * @param originalMap  <span class="en">Original data map</span>
         *                     <span class="zh-CN">来源数据Map</span>
         */
        void copyData(final Object targetObject, final Map<String, Object> originalMap) {
            if (originalMap == null || originalMap.isEmpty()) {
                return;
            }
            Optional.ofNullable(originalMap.get(this.fieldName))
                    .map(fieldValue ->
                            this.propertyMappings.stream()
                                    .filter(propertyMapping ->
                                            DataFlow.IN.equals(propertyMapping.dataFlow)
                                                    && Map.class.equals(propertyMapping.beanClass))
                                    .findFirst()
                                    .map(propertyMapping ->
                                            propertyMapping.convertData(fieldValue, this.fieldType))
                                    .orElse(fieldValue))
                    .ifPresent(fieldValue -> ReflectionUtils.setField(this.fieldName, targetObject, fieldValue));
        }
        /**
         * <h3 class="en">Copy the property values between the JavaBean instance and JavaBean instance arrays</h3>
         * <h3 class="zh-CN">在JavaBean对象实例和JavaBean对象实例数组间复制数据</h3>
         *
         * @param dataFlow  <span class="en">Data flow, <code>IN</code> from arrays to object, <code>OUT</code> from object to arrays</span>
         *                  <span class="zh-CN">数据流向<，<code>IN</code>从实例数组复制数据到实例对象，<code>OUT</code>从实例对象复制数据到实例数组/span>
         * @see org.nervousync.enumerations.beans.DataFlow
         * @param object    <span class="en">JavaBean instance</span>
         *                  <span class="zh-CN">JavaBean实例</span>
         * @param objects   <span class="en">JavaBean instance array</span>
         *                  <span class="zh-CN">JavaBean实例数组</span>
         */
        void copyData(final DataFlow dataFlow, final Object object, final Object... objects) {
            if (object == null || objects == null || objects.length == 0) {
                return;
            }
            final AtomicInteger priority = new AtomicInteger(Globals.DEFAULT_VALUE_INT);
            if (this.propertyMappings.isEmpty()) {
                switch (dataFlow) {
                    case IN:
                        Arrays.asList(objects).forEach(obj -> copyProperties(obj, object));
                        break;
                    case OUT:
                        Arrays.asList(objects).forEach(obj -> copyProperties(object, obj));
                        break;
                }
            } else {
                this.propertyMappings.stream()
                        .filter(propertyMapping -> propertyMapping.getDataFlow().equals(dataFlow))
                        .forEach(propertyMapping -> {
                            if (propertyMapping.copyData(priority.get(), this.fieldName, this.fieldType, object, objects)) {
                                priority.set(propertyMapping.getSortCode());
                            }
                        });
            }
        }
        /**
         * <h3 class="en">Copy the property values from the source object to the target object, based field name</h3>
         * <h3 class="zh-CN">从源数据对象复制数据到目标对象，复制依据属性名称</h3>
         *
         * @param sourceObject  <span class="en">Source object instance</span>
         *                      <span class="zh-CN">源数据对象</span>
         * @param targetObject  <span class="en">Target object instance</span>
         *                      <span class="zh-CN">目标数据对象</span>
         */
        void copyProperties(final Object sourceObject, final Object targetObject) {
            ReflectionUtils.getAllDeclaredFields(sourceObject.getClass(), Boolean.TRUE)
                    .forEach(field -> Optional.ofNullable(ReflectionUtils.getFieldValue(field, sourceObject))
                            .ifPresent(fieldValue ->
                                    ReflectionUtils.setField(field.getName(), targetObject, fieldValue)));
        }
        /**
         * <h3 class="en">Register BeanProperty annotation who was annotated at field</h3>
         * <h3 class="zh-CN">注册注解在属性上的BeanProperty注解</h3>
         *
         * @param beanProperty  <span class="en">Annotation instance of BeanProperty</span>
         *                      <span class="zh-CN">BeanProperty注解实例</span>
         */
        private void registerProperty(final BeanProperty beanProperty) {
            if (this.propertyMappings.stream().anyMatch(propertyMapping -> propertyMapping.exists(beanProperty))) {
                LOGGER.warn("Utils", "JavaBean_Property_Mapping_Existed_Warn",
                        beanProperty.beanClass(), beanProperty.targetField());
            }
            if (Map.class.equals(beanProperty.beanClass())) {
                this.propertyMappings.add(new PropertyMapping(beanProperty));
                return;
            }
            Optional.ofNullable(ReflectionUtils.getFieldIfAvailable(beanProperty.beanClass(), beanProperty.targetField()))
                    .ifPresent(field -> this.propertyMappings.add(new PropertyMapping(beanProperty)));
        }
    }
    private static final class PropertyMapping {
        private final int sortCode;
        private final DataFlow dataFlow;
        private final Class<?> beanClass;
        private final String fieldName;
        private final String className;
        PropertyMapping(final BeanProperty beanProperty) {
            this.sortCode = beanProperty.sortCode();
            this.dataFlow = beanProperty.dataFlow();
            this.beanClass = beanProperty.beanClass();
            this.fieldName = beanProperty.targetField();
            this.className = beanProperty.converter().getName();
        }
        public int getSortCode() {
            return sortCode;
        }
        public DataFlow getDataFlow() {
            return dataFlow;
        }
        public Class<?> getBeanClass() {
            return beanClass;
        }
        public String getFieldName() {
            return fieldName;
        }
        boolean exists(final BeanProperty beanProperty) {
            return this.dataFlow.equals(beanProperty.dataFlow())
                    && this.beanClass.equals(beanProperty.beanClass())
                    && this.fieldName.equals(beanProperty.targetField());
        }
        int compare(final PropertyMapping propertyMapping) {
            if (this.sortCode != propertyMapping.getSortCode()) {
                return Integer.compare(propertyMapping.getSortCode(), this.sortCode);
            }
            if (!this.beanClass.equals(propertyMapping.getBeanClass())) {
                return propertyMapping.getBeanClass().getName().compareTo(this.beanClass.getName());
            }
            return propertyMapping.getFieldName().compareTo(this.fieldName);
        }
        @SuppressWarnings("unchecked")
        Object convertData(final Object fieldValue, final Class<?> sourceClass) {
            final Object wrapperObject;
            if (fieldValue.getClass().isPrimitive()) {
                Class<?> primitiveClass = fieldValue.getClass();
                wrapperObject =
                        Optional.ofNullable(ReflectionUtils.findMethod(ClassUtils.primitiveWrapper(primitiveClass),
                                        "valueOf", new Class[]{primitiveClass}))
                                .map(method -> ReflectionUtils.invokeMethod(method, null, new Object[]{fieldValue}))
                                .orElse(fieldValue);
            } else {
                wrapperObject = fieldValue;
            }
            return Optional.ofNullable(this.className)
                    .filter(StringUtils::notBlank)
                    .map(ClassUtils::forName)
                    .filter(converterClass ->
                            Adapter.class.isAssignableFrom(converterClass) && !Adapter.class.equals(converterClass))
                    .map(converterClass -> (Adapter<Object, Object>) ObjectUtils.newInstance(converterClass))
                    .map(adapter -> convertData(adapter, sourceClass, wrapperObject))
                    .orElse(fieldValue);
        }

        Object convertData(final Adapter<Object, Object> adapter, final Class<?> beanClass, final Object wrapperObject) {
            try {
                switch (this.dataFlow) {
                    case IN:
                        if (adapter instanceof AbstractBeanAdapter) {
                            ((AbstractBeanAdapter<Object, Object>) adapter).setBeanClass(beanClass);
                        }
                        return adapter.unmarshal(wrapperObject);
                    case OUT:
                        return adapter.marshal(wrapperObject);
                    default:
                        return null;
                }
            } catch (Exception e) {
                LOGGER.error("Utils", "Convert_Data_Error");
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Utils", "Stack_Message_Error", e);
                }
                return null;
            }
        }
        boolean copyData(final int priority, final String fieldName, final Class<?> sourceClass,
                         final Object object, final Object... objects) {
            final Object targetObject;
            final String targetField;
            switch (this.dataFlow) {
                case IN:
                    targetObject = object;
                    targetField = fieldName;
                    break;
                case OUT:
                    targetObject = Arrays.stream(objects)
                            .filter(obj -> obj != null && obj.getClass().equals(this.beanClass))
                            .findFirst()
                            .orElse(null);
                    targetField = this.fieldName;
                    break;
                default:
                    return Boolean.FALSE;
            }
            final Object fieldValue;
            switch (this.dataFlow) {
                case IN:
                    fieldValue = Arrays.stream(objects)
                            .filter(obj -> obj != null && obj.getClass().equals(this.beanClass))
                            .findFirst()
                            .map(obj -> ReflectionUtils.getFieldValue(this.fieldName, obj))
                            .orElse(null);
                    break;
                case OUT:
                    fieldValue = ReflectionUtils.getFieldValue(fieldName, object);
                    break;
                default:
                    fieldValue = null;
                    break;
            }
            if (targetObject != null && fieldValue != null
                    && (DataFlow.OUT.equals(this.dataFlow) || (priority == Globals.DEFAULT_VALUE_INT || priority < this.sortCode))) {
                ReflectionUtils.setField(targetField, targetObject, this.convertData(fieldValue, sourceClass));
                return Boolean.TRUE;

            }
            return Boolean.FALSE;
        }
    }
}