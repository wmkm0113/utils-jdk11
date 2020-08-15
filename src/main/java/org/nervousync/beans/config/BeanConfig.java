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
package org.nervousync.beans.config;

import org.nervousync.beans.annotation.BeanConvert;
import org.nervousync.beans.provider.ConvertProvider;
import org.nervousync.commons.core.Globals;
import org.nervousync.utils.BeanUtils;
import org.nervousync.utils.ObjectUtils;
import org.nervousync.utils.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Java Bean Config using for BeanUtils method: copyProperties
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: 8/15/2020 11:46 AM $
 */
public final class BeanConfig implements Serializable {

	private static final long serialVersionUID = -8220400989433945047L;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private final String className;
	private final Hashtable<String, FieldConfig> fieldConfigHashtable;

	public BeanConfig(@Nonnull Class<?> beanClass) {
		this.className = beanClass.getName();
		List<FieldConfig> fieldConfigList = new ArrayList<>();
		for (Field field : beanClass.getDeclaredFields()) {
			if (!ReflectionUtils.isStatic(field)) {
				BeanConvert beanConvert = field.getAnnotation(BeanConvert.class);
				if (field.isAnnotationPresent(BeanConvert.class)) {
					beanConvert = field.getAnnotation(BeanConvert.class);
				}
				String fieldName = field.getName();
				if (beanConvert == null) {
					fieldConfigList.add(new FieldConfig(fieldName, field.getType(),
							ReflectionUtils.retrieveGetMethod(fieldName, beanClass),
							ReflectionUtils.retrieveSetMethod(fieldName, beanClass)));
				} else {
					fieldConfigList.add(new FieldConfig(fieldName, field.getType(),
							ReflectionUtils.retrieveGetMethod(fieldName, beanClass),
							ReflectionUtils.retrieveSetMethod(fieldName, beanClass),
							beanConvert.value()));
				}
			}
		}
		this.fieldConfigHashtable = new Hashtable<>(fieldConfigList.size());
		fieldConfigList.forEach(fieldConfig ->
				this.fieldConfigHashtable.put(fieldConfig.getFieldName(), fieldConfig));
	}

	/**
	 * Gets the value of serialVersionUID
	 *
	 * @return the value of serialVersionUID
	 */
	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	/**
	 * Gets the value of className
	 *
	 * @return the value of className
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * Check field exists status
	 *
	 * @param fieldName     Field name
	 * @return              Check result
	 */
	public boolean containsField(@Nonnull String fieldName) {
		return this.fieldConfigHashtable.containsKey(fieldName);
	}

	/**
	 * Retrieve all field value and convert to HashMap
	 * which field is annotation by org.nervousync.beans.annotation.BeanData
	 * @param object    Bean object
	 * @return          Converted data map
	 */
	public Map<String, Object> retrieveValue(@Nonnull Object object) {
		Map<String, Object> resultMap = new HashMap<>();
		if (object.getClass().getName().equals(this.className)) {
			this.fieldConfigHashtable.keySet().forEach(fieldName ->
					resultMap.put(fieldName, this.retrieveValue(fieldName, object)));
		}
		return resultMap;
	}

	/**
	 * Retrieve field value by given field name
	 * @param fieldName     Field name
	 * @param object        Bean object
	 * @return              Field value
	 */
	public Object retrieveValue(@Nonnull String fieldName, @Nonnull Object object) {
		if (object.getClass().getName().equals(this.className)
				&& this.fieldConfigHashtable.containsKey(fieldName)) {
			try {
				return this.fieldConfigHashtable.get(fieldName).getMethodGet().invoke(object);
			} catch (Exception e) {
				if (this.logger.isDebugEnabled()) {
					this.logger.debug("Stack message: ", e);
				}
			}
		}
		return null;
	}

	/**
	 * Copy given value to target field identify by given field name
	 *
	 * @param fieldName     field name
	 * @param object        Bean object
	 * @param value         field value
	 * @return              Copy result
	 */
	public boolean copyValue(@Nonnull String fieldName, @Nonnull Object object, @Nonnull Object value) {
		if (object.getClass().getName().equals(this.className)
				&& this.fieldConfigHashtable.containsKey(fieldName)) {
			FieldConfig fieldConfig = this.fieldConfigHashtable.get(fieldName);
			try {
				Class<?> fieldType = value.getClass();
				Object args = null;
				if (fieldType.equals(fieldConfig.getFieldType())) {
					if (BeanConfig.isPrimitiveClass(fieldType)) {
						args = value;
					} else {
						args = fieldConfig.getMethodGet().invoke(object);
						if (args == null) {
							args = ObjectUtils.newInstance(fieldConfig.getFieldType());
						}
						BeanUtils.copyProperties(value, args);
					}
				} else {
					if (this.logger.isDebugEnabled()) {
						this.logger.debug("Convert data start...");
					}
					ConvertProvider convertProvider = fieldConfig.retrieveConverterClass(value.getClass());
					if (convertProvider == null) {
						this.logger.warn("Data type not matched! Convert provider not found! ");
					} else {
						args = convertProvider.convert(value, fieldConfig.getFieldType());
					}
					if (this.logger.isDebugEnabled()) {
						this.logger.debug("Convert data finished");
					}
				}
				if (args == null) {
					args = value;
				}
				fieldConfig.getMethodSet().invoke(object, args);
				return true;
			} catch (Exception e) {
				if (this.logger.isDebugEnabled()) {
					this.logger.debug("Stack message: ", e);
				}
			}
		}
		return Globals.DEFAULT_VALUE_BOOLEAN;
	}

	private static boolean isPrimitiveClass(Class<?> clazz) {
		return clazz.isPrimitive() || Integer.class.equals(clazz)
				|| Double.class.equals(clazz) || Float.class.equals(clazz)
				|| Long.class.equals(clazz) || Short.class.equals(clazz)
				|| Boolean.class.equals(clazz) || Byte.class.equals(clazz)
				|| String.class.equals(clazz);
	}

	private static final class FieldConfig implements Serializable {

		private static final long serialVersionUID = 268647537906576706L;

		private final String fieldName;
		private final Class<?> fieldType;
		private final Method methodGet;
		private final Method methodSet;
		private final List<ConvertProvider> converters;

		FieldConfig(@Nonnull String fieldName, Class<?> fieldType, Method methodGet, Method methodSet,
		            Class<?>... dataConverters) {
			this.fieldName = fieldName;
			this.fieldType = fieldType;
			this.methodGet = methodGet;
			this.methodSet = methodSet;
			this.converters = new ArrayList<>(dataConverters.length);
			for (Class<?> dataConverter : dataConverters) {
				if (ConvertProvider.class.isAssignableFrom(dataConverter)) {
					this.converters.add((ConvertProvider)ObjectUtils.newInstance(dataConverter));
				}
			}
		}

		/**
		 * Gets the value of serialVersionUID
		 *
		 * @return the value of serialVersionUID
		 */
		public static long getSerialVersionUID() {
			return serialVersionUID;
		}

		/**
		 * Gets the value of fieldName
		 *
		 * @return the value of fieldName
		 */
		public String getFieldName() {
			return fieldName;
		}

		/**
		 * Gets the value of fieldType
		 *
		 * @return the value of fieldType
		 */
		public Class<?> getFieldType() {
			return fieldType;
		}

		/**
		 * Gets the value of methodGet
		 *
		 * @return the value of methodGet
		 */
		public Method getMethodGet() {
			return methodGet;
		}

		/**
		 * Gets the value of methodSet
		 *
		 * @return the value of methodSet
		 */
		public Method getMethodSet() {
			return methodSet;
		}

		public ConvertProvider retrieveConverterClass(Class<?> dataType) {
			for (ConvertProvider convertProvider : this.converters) {
				if (convertProvider.checkType(dataType)) {
					return convertProvider;
				}
			}
			return null;
		}
	}
}
