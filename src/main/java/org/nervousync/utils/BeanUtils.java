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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.xml.bind.JAXB;
import org.nervousync.beans.config.BeanConfig;
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

	private static final Hashtable<String, BeanConfig> BEAN_CONFIG_MAP = new Hashtable<>();

	private BeanUtils() {
	}

	/**
	 * Parse file content to target bean class
	 *
	 * @param <T>       Template
	 * @param filePath  File path
	 * @param beanClass Target bean class
	 * @return Converted object
	 */
	public static <T> T parseFile(String filePath, Class<T> beanClass) {
		if (StringUtils.isEmpty(filePath) || !FileUtils.isExists(filePath)) {
			LOGGER.error("Can't found file: {}", filePath);
			return null;
		}
		String textContent = FileUtils.readFile(filePath);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Parse string: {}", textContent);
		}
		String extName = StringUtils.getFilenameExtension(filePath);
		switch (extName.toLowerCase()) {
			case "json":
				return BeanUtils.parseJSON(FileUtils.readFile(filePath), beanClass);
			case "xml":
				return BeanUtils.parseXml(FileUtils.readFile(filePath), beanClass);
			case "yaml":
				return BeanUtils.parseYaml(FileUtils.readFile(filePath), beanClass);
			default:
				return null;
		}
	}

	/**
	 * Parse xml string and setting fields data to this object
	 *
	 * @param <T>       Object
	 * @param string    XML string or XML file location will be parsed
	 * @param beanClass Entity class
	 * @return Convert object
	 */
	public static <T> T parseXml(String string, Class<T> beanClass) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Parse string: {} to bean: {}", string, beanClass.getName());
		}
		return parseXml(string, Globals.DEFAULT_ENCODING, beanClass);
	}

	/**
	 * Parse xml string and setting fields data to this object
	 *
	 * @param <T>       Object
	 * @param string    the string
	 * @param encoding  Character encoding
	 * @param beanClass the bean class
	 * @return Convert object
	 */
	public static <T> T parseXml(String string, String encoding, Class<T> beanClass) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Parse string: {} use encoding: {} to bean: {}", string, encoding, beanClass.getName());
		}
		String stringEncoding = (encoding == null) ? Globals.DEFAULT_ENCODING : encoding;
		try (InputStream inputStream = new ByteArrayInputStream(string.getBytes(stringEncoding))) {
			return JAXB.unmarshal(inputStream, beanClass);
		} catch (IOException e) {
			LOGGER.error("Parse xml error! ");
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Stack message: ", e);
			}
		}
		return null;
	}

	/**
	 * Parse json t.
	 *
	 * @param <T>       the type parameter
	 * @param string    the string
	 * @param beanClass the bean class
	 * @return the t
	 */
	public static <T> T parseJSON(String string, Class<T> beanClass) {
		if (String.class.equals(beanClass)) {
			return beanClass.cast(string);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Parse string: {} to bean: {}", string, beanClass.getName());
		}
		return convertMapToBean(string, StringUtils.StringType.JSON, beanClass);
	}

	/**
	 * Convert JSON string to List and bind data to java bean
	 *
	 * @param <T>      T
	 * @param jsonData JSON String
	 * @param clazz    Bind JavaBean define class
	 * @return List of JavaBean
	 */
	public static <T> List<T> parseJSONToList(String jsonData, Class<T> clazz) {
		return parseToList(jsonData, clazz);
	}

	/**
	 * Parse YAML t.
	 *
	 * @param <T>       the type parameter
	 * @param string    the string
	 * @param beanClass the bean class
	 * @return the t
	 */
	public static <T> T parseYaml(String string, Class<T> beanClass) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Parse string: {} to bean: {}", string, beanClass.getName());
		}
		return convertMapToBean(string, StringUtils.StringType.YAML, beanClass);
	}

	/**
	 * Convert Yaml string to List and bind data to java bean
	 *
	 * @param <T>      T
	 * @param yamlData the yaml data
	 * @param clazz    Bind JavaBean define class
	 * @return List of JavaBean
	 */
	public static <T> List<T> parseYamlToList(String yamlData, Class<T> clazz) {
		return parseToList(yamlData, clazz);
	}

	/**
	 * Remove registered bean config
	 *
	 * @param className Bean class name
	 */
	public static void removeBeanConfig(String className) {
		BEAN_CONFIG_MAP.remove(className);
	}

	/**
	 * Copy the map values into the target bean identify by map key.
	 * <p>
	 * Note: The source and target classes do not have to match or even be derived
	 * from each other, as long as the properties match. Any bean properties that the
	 * source bean exposes but the target bean does not will silently be ignored.
	 * </p>
	 *
	 * @param dataMap data map
	 * @param dest    the target bean
	 */
	public static void copyProperties(Map<?, ?> dataMap, Object dest) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Data Map: {}", StringUtils.objectToString(dataMap, StringUtils.StringType.JSON, true));
		}
		String className = retrieveClassName(dest.getClass());
		BeanUtils.checkRegister(className);
		BeanConfig targetBean = BEAN_CONFIG_MAP.get(className);
		dataMap.entrySet().stream().filter(entry -> entry.getValue() != null)
				.forEach(entry -> targetBean.parseValue((String)entry.getKey(), dest, entry.getValue()));
	}

	/**
	 * Copy the property values of the given source bean into the target bean.
	 * <p>
	 * Note: The source and target classes do not have to match or even be derived
	 * from each other, as long as the properties match. Any bean properties that the
	 * source bean exposes but the target bean does not will silently be ignored.
	 * </p>
	 *
	 * @param orig           the source bean
	 * @param dest           the target bean
	 * @param convertMapping field mapping
	 */
	public static void copyProperties(Object orig, Object dest, Hashtable<String, String> convertMapping) {
		String origClass = retrieveClassName(orig.getClass());
		String destClass = retrieveClassName(dest.getClass());
		BeanUtils.checkRegister(origClass);
		BeanUtils.checkRegister(destClass);

		BeanConfig targetBean = BEAN_CONFIG_MAP.get(destClass);
		BEAN_CONFIG_MAP.get(origClass).retrieveValue(orig)
				.entrySet().stream().filter(entry -> entry.getValue() != null)
				.forEach(entry ->
						targetBean.copyValue(convertMapping.getOrDefault(entry.getKey(), entry.getKey()),
								dest, entry.getValue()));
	}

	private static <T> List<T> parseToList(String string, Class<T> clazz) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JavaType javaType = objectMapper.getTypeFactory().constructParametricType(ArrayList.class, clazz);
			return objectMapper.readValue(string, javaType);
		} catch (Exception e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Convert json string to object bean error! ", e);
			}
			return new ArrayList<>();
		}
	}

	/**
	 * Register bean class if needed
	 *
	 * @param className Bean class name
	 */
	private static void checkRegister(String className) {
		if (!BEAN_CONFIG_MAP.containsKey(className)) {
			try {
				BEAN_CONFIG_MAP.put(className, new BeanConfig(ClassUtils.forName(className)));
			} catch (ClassNotFoundException e) {
				LOGGER.error("Class not found! Class name: {}", className);
			}
		}
	}

	private static String retrieveClassName(Class<?> clazz) {
		String className = clazz.getName();
		if (className.contains("$$")) {
			className = className.substring(0, className.indexOf("$$"));
		}
		return className;
	}

	private static <T> T convertMapToBean(String data, StringUtils.StringType stringType, Class<T> beanClass) {
		Map<String, Object> dataMap = StringUtils.dataToMap(data, stringType);
		if (dataMap.isEmpty()) {
			return null;
		}
		T object = ObjectUtils.newInstance(beanClass);
		BeanUtils.copyProperties(dataMap, object);
		return object;
	}
}
