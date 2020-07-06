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
package com.nervousync.utils;

import java.util.Hashtable;

import com.nervousync.commons.core.Globals;
import net.sf.cglib.beans.BeanCopier;
import net.sf.cglib.core.Converter;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Jun 25, 2015 2:55:15 PM $
 */
public final class BeanUtils {

	private static final Hashtable<String, BeanCopier> BEAN_COPIER_MAP = new Hashtable<>();

	private BeanUtils() {
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
	 * @return Process result
	 */
	public static boolean copyProperties(Object orig, Object dest) {
		return BeanUtils.copyProperties(orig, dest, null);
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
	 * @param converter converter instance
	 * @return Process result
	 */
	public static boolean copyProperties(Object orig, Object dest, Converter converter) {
		if (orig == null || dest == null) {
			return Globals.DEFAULT_VALUE_BOOLEAN;
		}

		String cacheKey = BeanUtils.generateKey(orig.getClass(), dest.getClass(),
				converter == null ? null : converter.getClass());

		BeanCopier beanCopier;
		if (BeanUtils.BEAN_COPIER_MAP.containsKey(cacheKey)) {
			beanCopier = BeanUtils.BEAN_COPIER_MAP.get(cacheKey);
		} else {
			beanCopier = BeanCopier.create(orig.getClass(), dest.getClass(), converter != null);
			BeanUtils.BEAN_COPIER_MAP.put(cacheKey, beanCopier);
		}

		beanCopier.copy(orig, dest, converter);
		return true;
	}

	/**
	 * Generate key of cached bean copier
	 * @param origClass         Original class
	 * @param destClass         Dest class
	 * @param converterClass    Convert class
	 * @return                  cache key
	 */
	private static String generateKey(Class<?> origClass, Class<?> destClass, Class<?> converterClass) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(origClass.getName()).append("To").append(destClass.getName());
		if (converterClass != null) {
			stringBuilder.append("Converter:").append(converterClass.getName());
		}
		return SecurityUtils.SHA256(stringBuilder.toString());
	}
}
