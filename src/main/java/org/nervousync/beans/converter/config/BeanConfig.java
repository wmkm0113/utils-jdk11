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
package org.nervousync.beans.converter.config;

import org.nervousync.annotations.beans.BeanConvert;
import org.nervousync.beans.converter.provider.ConvertProvider;
import org.nervousync.beans.converter.provider.impl.blob.EncodeBase64Provider;
import org.nervousync.beans.converter.provider.impl.blob.ParseBase64Provider;
import org.nervousync.beans.converter.provider.impl.json.EncodeJSONProvider;
import org.nervousync.beans.converter.provider.impl.json.ParseJSONProvider;
import org.nervousync.beans.converter.provider.impl.basic.ParseNumberProvider;
import org.nervousync.beans.converter.provider.impl.xml.EncodeXMLProvider;
import org.nervousync.beans.converter.provider.impl.xml.ParseXMLProvider;
import org.nervousync.beans.core.BeanObject;
import org.nervousync.commons.core.Globals;
import org.nervousync.enumerations.xml.DataType;
import org.nervousync.utils.BeanUtils;
import org.nervousync.utils.ObjectUtils;
import org.nervousync.utils.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.*;

/**
 * Java Bean Config using for BeanUtils method: copyProperties
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: 8/15/2020 11:46 AM $
 */
public final class BeanConfig implements Serializable {

	private static final long serialVersionUID = -8220400989433945047L;

	/**
	 * Logger
	 */
	private final transient Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Java bean class name
	 */
	private final String className;
	/**
	 * Field config map
	 */
	private final Hashtable<String, FieldConfig> fieldConfigHashtable;

	/**
	 * Instantiates a new Bean config.
	 *
	 * @param beanClass the bean class
	 */
	public BeanConfig(Class<?> beanClass) {
		this.className = beanClass.getName();
		List<FieldConfig> fieldConfigList = new ArrayList<>();
		ReflectionUtils.getAllDeclaredFields(beanClass).stream()
				.filter(field -> ReflectionUtils.nonStaticMember(field) && !ReflectionUtils.publicMember(field))
				.forEach(field -> {
					Class<?>[] dataConverters;
					if (field.isAnnotationPresent(BeanConvert.class)) {
						dataConverters = field.getAnnotation(BeanConvert.class).value();
					} else {
						dataConverters = new Class<?>[0];
					}
					String fieldName = field.getName();
					boolean isArray = field.getType().isArray() || List.class.isAssignableFrom(field.getType());
					Class<?> paramClass;
					if (isArray) {
						if (field.getType().isArray()) {
							paramClass = field.getType().getComponentType();
						} else {
							paramClass = (Class<?>)((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0];
						}
					} else {
						paramClass = field.getType();
					}
					fieldConfigList.add(new FieldConfig(fieldName, isArray, field.getType(), paramClass,
							ReflectionUtils.retrieveGetMethod(fieldName, beanClass),
							ReflectionUtils.retrieveSetMethod(fieldName, beanClass),
							dataConverters));
				});
		this.fieldConfigHashtable = new Hashtable<>(fieldConfigList.size(), 1f);
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
	 * @param fieldName Field name
	 * @return Check result
	 */
	public boolean containsField(String fieldName) {
		return this.fieldConfigHashtable.containsKey(fieldName);
	}

	/**
	 * Retrieve all field value and convert to HashMap
	 * which field is annotation by org.nervousync.beans.annotation.BeanData
	 *
	 * @param object Bean object
	 * @return Converted data map
	 */
	public Map<String, Object> retrieveValue(Object object) {
		Map<String, Object> resultMap = new HashMap<>();
		this.fieldConfigHashtable.keySet().forEach(fieldName ->
				resultMap.put(fieldName, this.retrieveValue(fieldName, object)));
		return resultMap;
	}

	/**
	 * Retrieve field value by given field name
	 *
	 * @param fieldName Field name
	 * @param object    Bean object
	 * @return Field value
	 */
	public Object retrieveValue(String fieldName, Object object) {
		if (this.fieldConfigHashtable.containsKey(fieldName)) {
			try {
				FieldConfig fieldConfig = this.fieldConfigHashtable.get(fieldName);
				if (fieldConfig.getMethodGet() == null) {
					return ReflectionUtils.getFieldValue(fieldName, object);
				} else {
					return fieldConfig.getMethodGet().invoke(object);
				}
			} catch (Exception e) {
				if (this.logger.isDebugEnabled()) {
					this.logger.debug("Stack message: ", e);
				}
			}
		}
		return null;
	}

	private Object parseBean(Map<?, ?> value, Class<?> beanClass) {
		Object beanObject = ObjectUtils.newInstance(beanClass);
		BeanUtils.copyProperties(value, beanObject);
		return beanObject;
	}

	private Object parseBoolean(Object value) {
		switch (ObjectUtils.retrieveSimpleDataType(value.getClass())) {
			case STRING:
				return "true".equalsIgnoreCase((String) value);
			case NUMBER:
				return "1".equalsIgnoreCase(value.toString());
			default:
				return value;
		}
	}

	private Object parseValue(Object value, FieldConfig fieldConfig) {
		Class<?> dataType = value.getClass();
		ConvertProvider convertProvider = fieldConfig.retrieveConverterClass(dataType);
		Object parsedValue = null;
		if (convertProvider == null) {
			DataType targetType = ObjectUtils.retrieveSimpleDataType(fieldConfig.getFieldType());
			if (DataType.NUMBER.equals(targetType)) {
				convertProvider = new ParseNumberProvider();
			} else if (DataType.BOOLEAN.equals(targetType)) {
				parsedValue = this.parseBoolean(value);
			} else {
				this.logger.warn("Data type not matched! Convert provider not found! ");
				if (this.logger.isDebugEnabled()) {
					this.logger.debug("Bean class: {}, field name: {}, field type: {}, value type: {}",
							this.className, fieldConfig.getFieldName(), fieldConfig.getFieldType().getName(),
							dataType.getName());
				}
			}
		}
		if (convertProvider != null) {
			parsedValue = convertProvider.convert(value, fieldConfig.getFieldType());
		}
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("Convert data finished");
		}
		return parsedValue;
	}

	/**
	 * Copy given value to target field identify by given field name
	 *
	 * @param fieldName field name
	 * @param object    Bean object
	 * @param value     field value
	 */
	public void parseValue(String fieldName, Object object, Object value) {
		if (this.fieldConfigHashtable.containsKey(fieldName)) {
			FieldConfig fieldConfig = this.fieldConfigHashtable.get(fieldName);
			try {
				Object args = null;
				if (matchFieldType(fieldConfig.getFieldType(), value.getClass())) {
					args = value;
				} else if (fieldConfig.isArray()) {
					if (value.getClass().isArray()) {
						List<Object> valueList = new ArrayList<>();
						Arrays.stream((Map<?, ?>[]) value)
								.forEach(itemMap ->
										valueList.add(this.parseBean(itemMap, fieldConfig.getParamClass())));
						args = valueList.toArray();
					} else if (List.class.isAssignableFrom(value.getClass())) {
						List<Object> valueList = new ArrayList<>();
						((List<?>)value).stream()
								.filter(Map.class::isInstance)
								.forEach(item ->
										valueList.add(this.parseBean((Map<?, ?>) item, fieldConfig.getParamClass())));
						args = valueList;
					}
				} else if (value instanceof Map && BeanObject.class.isAssignableFrom(fieldConfig.getFieldType())) {
					args = this.parseBean((Map<?, ?>) value, fieldConfig.getFieldType());
				} else {
					if (this.logger.isDebugEnabled()) {
						this.logger.debug("Convert data start...");
					}
					args = this.parseValue(value, fieldConfig);
					if (this.logger.isDebugEnabled()) {
						this.logger.debug("Convert data finished");
					}
				}
				if (args == null) {
					args = value;
				}
				if (fieldConfig.getMethodSet() == null) {
					ReflectionUtils.setField(fieldName, object, args);
				} else {
					fieldConfig.getMethodSet().invoke(object, args);
				}
			} catch (Exception e) {
				if (this.logger.isDebugEnabled()) {
					this.logger.debug("Stack message: ", e);
				}
			}
		}
	}

	/**
	 * Copy given value to target field identify by given field name
	 *
	 * @param fieldName     field name
	 * @param object        Bean object
	 * @param value         field value
	 */
	public void copyValue(String fieldName, Object object, Object value) {
		if (this.fieldConfigHashtable.containsKey(fieldName)) {
			FieldConfig fieldConfig = this.fieldConfigHashtable.get(fieldName);
			try {
				Class<?> dataType = value.getClass();
				Object args = null;
				if (matchFieldType(fieldConfig.getFieldType(), value.getClass())) {
					args = value;
				} else {
					if (this.logger.isDebugEnabled()) {
						this.logger.debug("Convert data start...");
					}
					ConvertProvider convertProvider = fieldConfig.retrieveConverterClass(dataType);
					if (convertProvider == null) {
						DataType targetType = ObjectUtils.retrieveSimpleDataType(fieldConfig.getFieldType());
						if (DataType.NUMBER.equals(targetType)) {
							convertProvider = new ParseNumberProvider();
						} else if (DataType.BOOLEAN.equals(targetType)) {
							args = this.parseBoolean(value);
						} else {
							this.logger.warn("Data type not matched! Convert provider not found! ");
							if (this.logger.isDebugEnabled()) {
								this.logger.debug("Bean class: {}, field name: {}, field type: {}, value type: {}",
										this.className, fieldName, fieldConfig.getFieldType().getName(), dataType.getName());
							}
						}
					}
					if (convertProvider != null) {
						args = convertProvider.convert(value, fieldConfig.getFieldType());
					}
					if (this.logger.isDebugEnabled()) {
						this.logger.debug("Convert data finished");
					}
				}
				if (args == null) {
					args = value;
				}
				if (fieldConfig.getMethodSet() == null) {
					ReflectionUtils.setField(fieldName, object, args);
				} else {
					fieldConfig.getMethodSet().invoke(object, args);
				}
			} catch (Exception e) {
				if (this.logger.isDebugEnabled()) {
					this.logger.debug("Stack message: ", e);
				}
			}
		}
	}

	private static boolean matchFieldType(Class<?> fieldType, Class<?> currentType) {
		if (fieldType.equals(currentType)) {
			return true;
		}
		Class<?> matchType = fieldType.isPrimitive() ? convertPrimitiveToWrapperClass(fieldType) : fieldType;
		Class<?> targetType = currentType.isPrimitive() ? convertPrimitiveToWrapperClass(currentType) : currentType;
		return matchType.equals(targetType);
	}

	private static Class<?> convertPrimitiveToWrapperClass(Class<?> primitiveClass) {
		if (int.class.equals(primitiveClass)) {
			return Integer.class;
		}
		if (double.class.equals(primitiveClass)) {
			return Double.class;
		}
		if (float.class.equals(primitiveClass)) {
			return Float.class;
		}
		if (long.class.equals(primitiveClass)) {
			return Long.class;
		}
		if (short.class.equals(primitiveClass)) {
			return Short.class;
		}
		if (boolean.class.equals(primitiveClass)) {
			return Boolean.class;
		}
		if (byte.class.equals(primitiveClass)) {
			return Byte.class;
		}
		if (char.class.equals(primitiveClass)) {
			return Character.class;
		}
		return primitiveClass;
	}

	private static final class FieldConfig implements Serializable {

		private static final long serialVersionUID = 268647537906576706L;

		private final String fieldName;
		private final boolean array;
		private final Class<?> fieldType;
		private final Class<?> paramClass;
		private final transient Method methodGet;
		private final transient Method methodSet;
		private final transient List<ConvertProvider> converters;

		/**
		 * Instantiates a new Field config.
		 *
		 * @param fieldName      the field name
		 * @param array          the array
		 * @param fieldType      the field type
		 * @param paramClass     the param class
		 * @param methodGet      the method get
		 * @param methodSet      the method set
		 * @param dataConverters the data converters
		 */
		FieldConfig(String fieldName, boolean array, Class<?> fieldType, Class<?> paramClass,
		            Method methodGet, Method methodSet, Class<?>... dataConverters) {
			this.fieldName = fieldName;
			this.array = array;
			this.fieldType = fieldType;
			this.paramClass = paramClass;
			this.methodGet = methodGet;
			this.methodSet = methodSet;
			if (dataConverters.length == 0) {
				this.converters = new ArrayList<>();
				switch (ObjectUtils.retrieveSimpleDataType(fieldType)) {
					case BINARY:
						this.converters.add(new EncodeBase64Provider());
						this.converters.add(new ParseBase64Provider());
						break;
					case NUMBER:
						this.converters.add(new ParseNumberProvider());
						break;
					case OBJECT:
						this.converters.add(new EncodeJSONProvider());
						this.converters.add(new ParseJSONProvider());
						this.converters.add(new EncodeXMLProvider());
						this.converters.add(new ParseXMLProvider());
						break;
					case STRING:
						this.converters.add(new EncodeBase64Provider());
						this.converters.add(new EncodeJSONProvider());
						this.converters.add(new EncodeXMLProvider());
						break;
					default:
						this.converters.add(new ParseNumberProvider());
						this.converters.add(new EncodeBase64Provider());
						this.converters.add(new ParseBase64Provider());
						this.converters.add(new EncodeJSONProvider());
						this.converters.add(new ParseJSONProvider());
						this.converters.add(new EncodeXMLProvider());
						this.converters.add(new ParseXMLProvider());
						break;
				}
			} else {
				this.converters = new ArrayList<>(dataConverters.length);
				for (Class<?> dataConverter : dataConverters) {
					if (ConvertProvider.class.isAssignableFrom(dataConverter)) {
						this.converters.add((ConvertProvider)ObjectUtils.newInstance(dataConverter));
					}
				}
			}
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
		 * Is array boolean.
		 *
		 * @return the boolean
		 */
		public boolean isArray() {
			return array;
		}

		/**
		 * Gets field type.
		 *
		 * @return the field type
		 */
		public Class<?> getFieldType() {
			return fieldType;
		}

		/**
		 * Gets param class.
		 *
		 * @return the param class
		 */
		public Class<?> getParamClass() {
			return paramClass;
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

		/**
		 * Retrieve converter class convert provider.
		 *
		 * @param dataType the data type
		 * @return the convert provider
		 */
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
