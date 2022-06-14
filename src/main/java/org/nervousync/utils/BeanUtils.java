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

import java.util.HashMap;
import java.util.Map;

import org.nervousync.beans.converter.config.BeanConfig;
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

	private static final Map<String, BeanConfig> BEAN_CONFIG_MAP = new HashMap<>();

	private BeanUtils() {
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
		dataMap.entrySet().stream()
				.filter(entry -> entry.getValue() != null)
				.forEach(entry -> targetBean.parseValue((String)entry.getKey(), dest, entry.getValue()));
	}

	/**
	 * Copy properties.
	 *
	 * @param orig the orig
	 * @param dest the dest
	 */
	public static void copyProperties(Object orig, Object dest) {
		copyProperties(orig, dest, null);
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
	public static void copyProperties(Object orig, Object dest, final Map<String, String> convertMapping) {
		String origClass = retrieveClassName(orig.getClass());
		String destClass = retrieveClassName(dest.getClass());
		BeanUtils.checkRegister(origClass);
		BeanUtils.checkRegister(destClass);

		BeanConfig targetBean = BEAN_CONFIG_MAP.get(destClass);
		BEAN_CONFIG_MAP.get(origClass).retrieveValue(orig)
				.entrySet().stream().filter(entry -> entry.getValue() != null)
				.forEach(entry -> {
					String fieldName;
					if (convertMapping == null) {
						fieldName = entry.getKey();
					} else {
						fieldName = convertMapping.getOrDefault(entry.getKey(), entry.getKey());
					}
					targetBean.copyValue(fieldName, dest, entry.getValue());
				});
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
}
