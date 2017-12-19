/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervousync Studio.
 */
package com.nervousync.utils;

import java.io.IOException;
import java.util.Hashtable;

import net.sf.cglib.beans.BeanCopier;
import net.sf.cglib.core.Converter;

import com.nervousync.exceptions.beans.BeansException;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Jun 25, 2015 2:55:15 PM $
 */
public final class BeanUtils {

	private static Hashtable<String, BeanCopier> BEAN_COPIER_MAP = new Hashtable<String, BeanCopier>();
	
	private BeanUtils() {
		
	}
	
	/**
	 * Copy the property values of the given source bean into the target bean.
	 * <p>Note: The source and target classes do not have to match or even be derived
	 * from each other, as long as the properties match. Any bean properties that the
	 * source bean exposes but the target bean does not will silently be ignored.
	 * <p>This is just a convenience method. For more complex transfer needs,
	 * consider using a full BeanWrapper.
	 * @param orig the source bean
	 * @param dest the target bean
	 * @throws BeansException if the copying failed
	 * @see BeanWrapper
	 */
	public static void copyProperties(Object orig, Object dest) throws BeansException {
		BeanUtils.copyProperties(orig, dest, null);
	}
	
	/**
	 * Copy the property values of the given source bean into the target bean.
	 * <p>Note: The source and target classes do not have to match or even be derived
	 * from each other, as long as the properties match. Any bean properties that the
	 * source bean exposes but the target bean does not will silently be ignored.
	 * <p>This is just a convenience method. For more complex transfer needs,
	 * consider using a full BeanWrapper.
	 * @param orig the source bean
	 * @param dest the target bean
	 * @param converter converter instance
	 * @throws BeansException if the copying failed
	 * @see BeanWrapper
	 */
	public static void copyProperties(Object orig, Object dest, Converter converter) throws BeansException {
		BeanCopier beanCopier = null;
		try {
			String cacheKey = null;
			if (converter == null) {
				cacheKey = BeanUtils.generateKey(orig.getClass(), dest.getClass(), null);
			} else {
				cacheKey = BeanUtils.generateKey(orig.getClass(), dest.getClass(), converter.getClass());
			}
			
			if (BeanUtils.BEAN_COPIER_MAP.containsKey(cacheKey)) {
				beanCopier = BeanUtils.BEAN_COPIER_MAP.get(cacheKey);
			} else {
				if (converter == null) {
					beanCopier = BeanCopier.create(orig.getClass(), dest.getClass(), false);
				} else {
					beanCopier = BeanCopier.create(orig.getClass(), dest.getClass(), true);
				}
				BeanUtils.BEAN_COPIER_MAP.put(cacheKey, beanCopier);
			}
			
			beanCopier.copy(orig, dest, converter);
		} catch (IOException e) {
			throw new BeansException("", e);
		}
	}
	
	private static String generateKey(Class<?> origClass, Class<?> destClass, Class<?> converterClass) throws IOException {
		if (converterClass == null) {
			return SecurityUtils.MD5Encode(origClass.getName() + "To" + destClass.getName());
		} else {
			return SecurityUtils.MD5Encode(origClass.getName() + "To" + destClass.getName() + "Converter:" + converterClass.getName());
		}
	}
}
