/*
 * Copyright 2017 Nervousync Studio
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.*;

import org.nervousync.annotations.beans.BeanMapping;
import org.nervousync.annotations.beans.FieldMapping;
import org.nervousync.annotations.beans.KeyMapping;
import org.nervousync.beans.converter.DataConverter;
import org.nervousync.beans.converter.impl.blob.Base64DataConverter;
import org.nervousync.commons.core.Globals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The type Bean utils.
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: Jun 25, 2015 2:55:15 PM $
 */
public final class BeanUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(BeanUtils.class);

	private static final Map<String, MappingBean> BEAN_CONFIG_MAP = new HashMap<>();

	private BeanUtils() {
	}

	/**
	 * Remove registered bean config
	 *
	 * @param classes Bean class array
	 */
	public static void removeBeanConfig(final Class<?>... classes) {
		Arrays.asList(classes).forEach(clazz -> BEAN_CONFIG_MAP.remove(retrieveClassName(clazz)));
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Registered bean config count: {}", BEAN_CONFIG_MAP.size());
		}
	}

	/**
	 * Copy the map values into the target bean identify by map key.
	 * <p>
	 * Note: The source and target classes do not have to match or even be derived
	 * from each other, as long as the properties match. Any bean properties that the
	 * source bean exposes but the target bean does not will silently be ignored.
	 * </p>
	 *
	 * @param orig      data map
	 * @param dest      the target bean
	 */
	public static void copyProperties(final Map<String, String> orig, final Object dest) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Data Map: {}", StringUtils.objectToString(orig, StringUtils.StringType.JSON, true));
		}
		BeanUtils.checkRegister(dest.getClass());
		Optional.ofNullable(BEAN_CONFIG_MAP.get(retrieveClassName(dest.getClass())))
				.ifPresent(mappingBean -> mappingBean.copyProperties(dest, orig));
	}

	/**
	 * Copy properties.
	 *
	 * @param dest          dest object
	 * @param origObjects   original object array
	 */
	public static void copyProperties(final Object dest, final Object... origObjects) {
		copyProperties(dest, null, origObjects);
	}

	/**
	 * Copy the property values of the given source bean into the target bean.
	 * <p>
	 * Note: The source and target classes do not have to match or even be derived
	 * from each other, as long as the properties match. Any bean properties that the
	 * source bean exposes but the target bean does not will silently be ignored.
	 * </p>
	 *
	 * @param dest          dest object
	 * @param convertMapping field mapping
	 * @param origObjects   original object array
	 */
	public static void copyProperties(final Object dest, final Map<String, String> convertMapping,
	                                  final Object... origObjects) {
		Arrays.asList(origObjects).forEach(origObject -> BeanUtils.checkRegister(origObject.getClass()));
		BeanUtils.checkRegister(dest.getClass());

		if (convertMapping == null || convertMapping.isEmpty()) {
			Optional.ofNullable(BEAN_CONFIG_MAP.get(retrieveClassName(dest.getClass())))
					.ifPresent(mappingBean -> mappingBean.copyProperties(dest, origObjects));
		} else {
			Map<String, String> dataMap = new HashMap<>();
			Arrays.asList(origObjects).forEach(origObject ->
					Optional.ofNullable(BEAN_CONFIG_MAP.get(retrieveClassName(origObject.getClass())))
							.ifPresent(mappingBean -> dataMap.putAll(mappingBean.retrieveValue(origObject))));
			convertMapping.forEach((key, value) -> {
				if (dataMap.containsKey(key)) {
					dataMap.put(value, dataMap.get(key));
				}
			});
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Data map: {}", StringUtils.objectToString(dataMap, StringUtils.StringType.JSON, Boolean.TRUE));
			}
			Optional.ofNullable(BEAN_CONFIG_MAP.get(retrieveClassName(dest.getClass())))
					.ifPresent(mappingBean -> mappingBean.copyProperties(dest, dataMap));
		}
	}

	/**
	 * Register bean class if needed
	 *
	 * @param clazz Bean class
	 */
	private static void checkRegister(final Class<?> clazz) {
		String className = retrieveClassName(clazz);
		if (!BEAN_CONFIG_MAP.containsKey(className)) {
			BEAN_CONFIG_MAP.put(className, new MappingBean(clazz));
		}
	}

	private static String retrieveClassName(Class<?> clazz) {
		String className = clazz.getName();
		if (className.contains("$$")) {
			//  Process for cglib
			className = className.substring(0, className.indexOf("$$"));
		} else if (className.contains("$ByteBuddy")) {
			//  Process for ByteBuddy
			className = className.substring(0, className.indexOf("$ByteBuddy"));
		}
		return className;
	}
	/**
	 * Java Bean Config using for BeanUtils method: copyProperties
	 *
	 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
	 * @version $Revision : 1.0 $ $Date: 8/15/2020 11:46 AM $
	 */
	private static final class MappingBean {

		/**
		 * Java bean class name
		 */
		private final Class<?> beanClass;
		/**
		 * Field config map
		 */
		private final List<MappingField> fieldConfigList;

		/**
		 * Instantiates a new Bean config.
		 *
		 * @param beanClass the bean class
		 */
		public MappingBean(Class<?> beanClass) {
			this.beanClass = beanClass;
			this.fieldConfigList = new ArrayList<>();
			ReflectionUtils.getAllDeclaredFields(beanClass).stream()
					.filter(ReflectionUtils::nonStaticMember)
					.forEach(field ->
							this.fieldConfigList.add(new MappingField(field,
									ReflectionUtils.retrieveGetMethod(field.getName(), beanClass),
									ReflectionUtils.retrieveSetMethod(field.getName(), beanClass))));
		}

		/**
		 * Retrieve all field value and convert to HashMap
		 * which field is annotation by org.nervousync.beans.annotation.BeanData
		 *
		 * @param object Bean object
		 * @return Converted data map
		 */
		public Map<String, String> retrieveValue(Object object) {
			Map<String, String> resultMap = new HashMap<>();
			this.fieldConfigList.forEach(mappingField ->
					resultMap.put(mappingField.getFieldName(), mappingField.readProperty(object)));
			return resultMap;
		}

		/**
		 * Retrieve field value by given field name
		 *
		 * @param fieldName Field name
		 * @param object    Bean object
		 * @return Field value
		 */
		public String readProperty(String fieldName, Object object) {
			return this.fieldConfigList.stream()
					.filter(mappingField -> mappingField.getFieldName().equals(fieldName))
					.findFirst()
					.map(mappingField -> mappingField.readProperty(object))
					.orElse(null);
		}

		public void copyProperties(final Object destObject, final Map<String, String> dataMap) {
			if (destObject != null && this.beanClass.equals(destObject.getClass())) {
				this.fieldConfigList.forEach(mappingField -> mappingField.copyProperty(destObject, dataMap));
			}
		}

		public void copyProperties(final Object destObject, final Object... origObjects) {
			if (destObject != null && this.beanClass.equals(destObject.getClass())) {
				this.fieldConfigList.forEach(mappingField -> mappingField.copyProperty(destObject, origObjects));
			}
		}
	}

	private static final class BeanMappingConfig {

		private final Class<?> beanClass;
		private final String fieldName;

		BeanMappingConfig(final Class<?> beanClass, final String fieldName) {
			this.beanClass = beanClass;
			this.fieldName = fieldName;
		}

		boolean matchType(final Object object) {
			return object != null && this.beanClass.equals(object.getClass());
		}

		String readProperty(final Object object) {
			return Optional.ofNullable(BEAN_CONFIG_MAP
					.get(retrieveClassName(this.beanClass)))
					.map(mappingBean -> mappingBean.readProperty(this.fieldName, object))
					.orElse(Globals.DEFAULT_VALUE_STRING);
		}
	}

	private static String encodeValue(final Object readObject, final boolean array,
	                                  final Class<? extends DataConverter> converterClass) {
		if (readObject == null) {
			return null;
		}
		if (converterClass != null && !DataConverter.class.equals(converterClass)
				&& DataConverter.class.isAssignableFrom(converterClass)) {
			return Optional.ofNullable(ObjectUtils.newInstance(converterClass))
					.map(dataConverter -> dataConverter.encode(readObject))
					.orElse(null);
		} else {
			if (array) {
				return StringUtils.objectToString(readObject, StringUtils.StringType.JSON, Boolean.TRUE);
			} else if (readObject instanceof String) {
				return (String) readObject;
			} else {
				return new Base64DataConverter().encode(readObject);
			}
		}
	}

	private static final class MappingField {

		private final String fieldName;
		private final boolean array;
		private final Class<?> fieldType;
		private final Class<?> paramClass;
		private final Field field;
		private final Method methodGet;
		private final Method methodSet;
		private final BeanMappingConfig beanMappingConfig;
		private final String keyName;
		private final Class<? extends DataConverter> converterClass;

		/**
		 * Instantiates a new Field config.
		 *
		 * @param methodSet      the method set
		 */
		MappingField(final Field field, final Method methodGet, final Method methodSet) {
			this.fieldName = field.getName();
			this.array = field.getType().isArray() || List.class.isAssignableFrom(field.getType());
			this.fieldType = field.getType();
			if (this.array) {
				if (this.fieldType.isArray()) {
					this.paramClass = field.getType().getComponentType();
				} else {
					this.paramClass = (Class<?>)((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0];
				}
			} else {
				this.paramClass = field.getType();
			}
			this.field = field;
			this.methodGet = methodGet;
			this.methodSet = methodSet;
			if (field.isAnnotationPresent(BeanMapping.class)) {
				BeanMapping beanMapping = field.getAnnotation(BeanMapping.class);
				this.beanMappingConfig = new BeanMappingConfig(beanMapping.beanClass(),
						StringUtils.notBlank(beanMapping.fieldName()) ? beanMapping.fieldName() : this.fieldName);
			} else {
				this.beanMappingConfig = null;
			}
			if (field.isAnnotationPresent(KeyMapping.class)) {
				KeyMapping keyMapping = field.getAnnotation(KeyMapping.class);
				this.keyName = keyMapping.value();
			} else {
				this.keyName = this.fieldName;
			}
			this.converterClass = field.isAnnotationPresent(FieldMapping.class)
					? field.getAnnotation(FieldMapping.class).value()
					: null;
		}

		/**
		 * Gets the value of fieldName
		 *
		 * @return the value of fieldName
		 */
		public String getFieldName() {
			return fieldName;
		}

		String readProperty(final Object object) {
			if (object == null) {
				return Globals.DEFAULT_VALUE_STRING;
			}
			try {
				return encodeValue(
						(this.methodGet == null)
								? ReflectionUtils.getFieldValue(this.field, object)
								: this.methodGet.invoke(object),
						this.array, this.converterClass);
			} catch (IllegalAccessException | InvocationTargetException e) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Read object property error! ", e);
				}
			}
			return Globals.DEFAULT_VALUE_STRING;
		}

		void copyProperty(final Object destObject, final Object... origObjects) {
			String origContent = Arrays.stream(origObjects)
					.filter(this.beanMappingConfig::matchType)
					.findFirst()
					.map(this.beanMappingConfig::readProperty)
					.orElse(Globals.DEFAULT_VALUE_STRING);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Read property content: {}", origContent);
			}
			this.writeProperty(destObject, origContent);
		}

		void copyProperty(final Object destObject, final Map<String, String> dataMap) {
			if (destObject == null || dataMap == null) {
				return;
			}
			this.writeProperty(destObject, dataMap.get(StringUtils.isEmpty(this.keyName) ? this.fieldName : this.keyName));
		}

		private void writeProperty(final Object object, final String origContent) {
			if (StringUtils.notBlank(origContent)) {
				Object setValue;
				if (this.converterClass != null && !DataConverter.class.equals(this.converterClass)
						&& DataConverter.class.isAssignableFrom(this.converterClass)) {
					if (this.array) {
						if (this.fieldType.isArray()) {
							setValue = ObjectUtils.newInstance(this.converterClass).decode(origContent, this.fieldType);
						} else {
							setValue = ObjectUtils.newInstance(this.converterClass).decode(origContent, this.paramClass);
						}
					} else {
						setValue = ObjectUtils.newInstance(this.converterClass).decode(origContent, this.fieldType);
					}
				} else {
					if (this.array) {
						if (this.fieldType.isArray()) {
							setValue = StringUtils.stringToObject(origContent, this.fieldType);
						} else {
							setValue = StringUtils.stringToList(origContent, Globals.DEFAULT_ENCODING, this.paramClass);
						}
					} else {
						if (String.class.equals(this.paramClass)) {
							setValue = origContent;
						} else {
							setValue = new Base64DataConverter().decode(origContent, this.paramClass);
						}
					}
				}
				if (setValue != null) {
					if (this.methodSet != null) {
						try {
							this.methodSet.invoke(object, setValue);
						} catch (IllegalAccessException | InvocationTargetException e) {
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug("Write object property error! ", e);
							}
						}
					} else {
						ReflectionUtils.setField(this.field, object, setValue);
					}
				}
			}
		}
	}
}
