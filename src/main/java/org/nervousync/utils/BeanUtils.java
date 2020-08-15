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

import java.util.Hashtable;
import java.util.Map;

import org.nervousync.beans.config.BeanConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Jun 25, 2015 2:55:15 PM $
 */
public final class BeanUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(BeanUtils.class);

	private static final Hashtable<String, BeanConfig> BEAN_CONFIG_MAP = new Hashtable<>();

	private BeanUtils() {
	}

	/**
	 * Remove registered bean config
	 *
	 * @param className     Bean class name
	 */
	public static void removeBeanConfig(String className) {
		BEAN_CONFIG_MAP.remove(className);
	}

	/**
	 * Copy the map values into the target bean identify by map key.
	 * <p>
	 *     Note: The source and target classes do not have to match or even be derived
	 *     from each other, as long as the properties match. Any bean properties that the
	 *     source bean exposes but the target bean does not will silently be ignored.
	 * </p>
	 * @param dataMap data map
	 * @param dest the target bean
	 */
	public static void copyProperties(@Nonnull Map<String, Object> dataMap, @Nonnull Object dest) {
		BeanUtils.checkRegister(dest.getClass());
		BeanConfig targetBean = BEAN_CONFIG_MAP.get(dest.getClass().getName());
		dataMap.forEach((key, value) -> targetBean.copyValue(key, dest, value));
	}

	/**
	 * Copy the property values of the given source bean into the target bean.
	 * <p>
	 *     Note: The source and target classes do not have to match or even be derived
	 *     from each other, as long as the properties match. Any bean properties that the
	 *     source bean exposes but the target bean does not will silently be ignored.
	 * </p>
	 * @param orig the source bean
	 * @param dest the target bean
	 */
	public static void copyProperties(@Nonnull Object orig, @Nonnull Object dest) {
		BeanUtils.copyProperties(orig, dest, new Hashtable<>(0));
	}

	/**
	 * Copy the property values of the given source bean into the target bean.
	 * <p>
	 *     Note: The source and target classes do not have to match or even be derived
	 *     from each other, as long as the properties match. Any bean properties that the
	 *     source bean exposes but the target bean does not will silently be ignored.
	 * </p>
	 * @param orig the source bean
	 * @param dest the target bean
	 * @param convertMapping    field mapping
	 */
	public static void copyProperties(@Nonnull Object orig, @Nonnull Object dest,
	                                     @Nonnull Hashtable<String, String> convertMapping) {
		BeanUtils.checkRegister(orig.getClass());
		BeanUtils.checkRegister(dest.getClass());

		BeanConfig targetBean = BEAN_CONFIG_MAP.get(dest.getClass().getName());
		BEAN_CONFIG_MAP.get(orig.getClass().getName()).retrieveValue(orig)
				.forEach((key, value) ->
						targetBean.copyValue(convertMapping.getOrDefault(key, key), dest, value)
				);
	}

	/**
	 * Register bean class if needed
	 * @param beanClass Bean class
	 */
	private static void checkRegister(@Nonnull Class<?> beanClass) {
		if (!BEAN_CONFIG_MAP.containsKey(beanClass.getName())) {
			BEAN_CONFIG_MAP.put(beanClass.getName(), new BeanConfig(beanClass));
		}
	}
}
