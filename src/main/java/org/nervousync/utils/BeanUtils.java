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

import org.nervousync.annotations.beans.BeanProperty;
import org.nervousync.annotations.beans.Mappings;
import org.nervousync.beans.converter.DataConverter;
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

	private static final Map<String, BeanMapping> BEAN_CONFIG_MAP = new HashMap<>();

	private BeanUtils() {
	}

	/**
	 * Remove registered bean config
	 *
	 * @param classes Bean class array
	 */
	public static void removeBeanConfig(final Class<?>... classes) {
		Arrays.asList(classes).forEach(clazz -> BEAN_CONFIG_MAP.remove(ClassUtils.origClassName(clazz)));
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
	 * @param origMap		data map
	 * @param destObject    the target bean
	 */
	public static void copyProperties(final Map<String, Object> origMap, final Object destObject) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Data Map: {}", StringUtils.objectToString(origMap, StringUtils.StringType.JSON, Boolean.TRUE));
		}
		ReflectionUtils.setField(destObject, origMap);
	}

	/**
	 * Copy the property values of the given source bean into the target bean.
	 * <p>
	 * Note: The source and target classes do not have to match or even be derived
	 * from each other, as long as the properties match. Any bean properties that the
	 * source bean exposes but the target bean does not will silently be ignored.
	 * </p>
	 *
	 * @param destObject    target object
	 * @param origObjects	source object array
	 */
	public static void copyFrom(final Object destObject, final Object... origObjects) {
		if (destObject == null || origObjects.length == 0) {
			return;
		}
		if (origObjects.length == 1 && origObjects[0].getClass().isAssignableFrom(destObject.getClass())) {
			ReflectionUtils.shallowCopyFieldState(origObjects[0], destObject);
		} else {
			checkRegister(destObject.getClass());
			Optional.ofNullable(BEAN_CONFIG_MAP.get(ClassUtils.origClassName(destObject.getClass())))
					.ifPresent(beanMapping -> beanMapping.copyFrom(destObject, origObjects));
		}
	}

	/**
	 * Copy the property values of the given source bean into the target bean.
	 * <p>
	 * Note: The source and target classes do not have to match or even be derived
	 * from each other, as long as the properties match. Any bean properties that the
	 * source bean exposes but the target bean does not will silently be ignored.
	 * </p>
	 *
	 * @param origObject    source object
	 * @param destObjects	target object arrays
	 */
	public static void copyTo(final Object origObject, final Object... destObjects) {
		if (origObject == null || destObjects.length == 0) {
			return;
		}
		if (destObjects.length == 1 && origObject.getClass().isAssignableFrom(destObjects[0].getClass())) {
			ReflectionUtils.shallowCopyFieldState(origObject, destObjects[0]);
		} else {
			checkRegister(origObject.getClass());
			Optional.ofNullable(BEAN_CONFIG_MAP.get(ClassUtils.origClassName(origObject.getClass())))
					.ifPresent(beanMapping -> beanMapping.copyTo(origObject, destObjects));
		}
	}

	/**
	 * Register bean class if needed
	 *
	 * @param clazz Bean class
	 */
	private static void checkRegister(final Class<?> clazz) {
		String className = ClassUtils.origClassName(clazz);
		if (!BEAN_CONFIG_MAP.containsKey(className)) {
			BEAN_CONFIG_MAP.put(className, new BeanMapping(clazz));
		}
	}

	private static final class BeanMapping {

		private final List<FieldMapping> fieldMappings;

		BeanMapping(final Class<?> beanClass) {
			this.fieldMappings = new ArrayList<>();
			ReflectionUtils.getAllDeclaredFields(beanClass)
					.forEach(field -> this.fieldMappings.add(new FieldMapping(field)));
		}

		void copyFrom(final Object destObject, final Object... origObjects) {
			if (destObject == null || origObjects.length == 0) {
				return;
			}
			this.fieldMappings.forEach(fieldMapping -> fieldMapping.copyFrom(destObject, origObjects));
		}

		void copyTo(final Object origObject, final Object... destObjects) {
			if (origObject == null || destObjects.length == 0) {
				return;
			}
			this.fieldMappings.forEach(fieldMapping -> fieldMapping.copyTo(origObject, destObjects));
		}
	}

	private static final class FieldMapping {

		private final String fieldName;
		private final Class<?> fieldType;
		private final PropertyMapping fromMapping;
		private final Map<Class<?>, List<PropertyMapping>> propertyMappings;

		FieldMapping(final Field field) {
			this.fieldName = field.getName();
			this.fieldType = field.getType();
			this.fromMapping = field.isAnnotationPresent(BeanProperty.class)
					? BeanUtils.newInstance(field.getAnnotation(BeanProperty.class))
					: null;
			this.propertyMappings = new HashMap<>();
			if (field.isAnnotationPresent(Mappings.class)) {
				Arrays.asList(field.getAnnotation(Mappings.class).value()).forEach(this::registerProperty);
			}
		}

		void copyFrom(final Object destObject, final Object... origObjects) {
			if (this.fromMapping != null) {
				Arrays.stream(origObjects)
						.filter(origObject -> this.fromMapping.beanClass.equals(origObject.getClass()))
						.forEach(origObject -> this.copyData(origObject, destObject));
			}
		}

		void copyTo(final Object origObject, final Object... destObjects) {
			final Object fieldValue = ReflectionUtils.getFieldValue(this.fieldName, origObject);
			Arrays.asList(destObjects)
					.forEach(destObject ->
							Optional.ofNullable(this.propertyMappings.get(destObject.getClass()))
									.ifPresent(propertiesList ->
											propertiesList.forEach(propertyMapping ->
													propertyMapping.copyTo(fieldValue, destObject))));
		}

		private void registerProperty(final BeanProperty beanProperty) {
			Optional.ofNullable(BeanUtils.newInstance(beanProperty))
					.ifPresent(propertyMapping -> {
						List<PropertyMapping> mappingList =
								this.propertyMappings.getOrDefault(beanProperty.beanClass(), new ArrayList<>());
						mappingList.add(propertyMapping);
						this.propertyMappings.put(beanProperty.beanClass(), mappingList);
					});
		}

		private void copyData(final Object origObject, final Object destObject) {
			ReflectionUtils.setField(this.fieldName, destObject,
					this.fromMapping.readValue(origObject, this.fieldType));
		}
	}

	private static final class PropertyMapping {

		private final Class<?> beanClass;
		private final Class<?> fieldType;
		private final String fieldName;
		private final Class<? extends DataConverter> converterClass;

		PropertyMapping(final Class<?> beanClass, final Class<?> fieldType, final String fieldName,
						final Class<? extends DataConverter> converterClass) {
			this.beanClass = beanClass;
			this.fieldType = fieldType;
			this.fieldName = fieldName;
			this.converterClass = converterClass;
		}

		<T> T readValue(final Object origObject, final Class<T> fieldType) {
			Object fieldValue = ReflectionUtils.getFieldValue(this.fieldName, origObject);
			if (fieldValue != null) {
				return DataConverter.class.equals(this.converterClass)
						? fieldType.cast(fieldValue)
						: ObjectUtils.newInstance(this.converterClass).convert(fieldValue, fieldType);
			}
			return null;
		}

		void copyTo(final Object origObject, final Object destObject) {
			try {
				Object destValue = DataConverter.class.equals(this.converterClass)
						? origObject
						: ObjectUtils.newInstance(this.converterClass).convert(origObject, this.fieldType);
				ReflectionUtils.setField(this.fieldName, destObject, destValue);
			} catch (Exception e) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.error("Copy property value failed! ");
				}
			}
		}
	}

	private static PropertyMapping newInstance(final BeanProperty beanProperty) {
		return Optional.ofNullable(ClassUtils.findField(beanProperty.beanClass(), beanProperty.targetField()))
				.map(field ->
						new PropertyMapping(beanProperty.beanClass(), field.getType(),
								field.getName(), beanProperty.converter()))
				.orElse(null);
	}
}
