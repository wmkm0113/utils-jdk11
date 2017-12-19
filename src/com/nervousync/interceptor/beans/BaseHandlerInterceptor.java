/*
 * Copyright © 2003 - 2014 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervousync Studio.
 */
package com.nervousync.interceptor.beans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.cglib.proxy.MethodInterceptor;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: May 7, 2014 2:46:11 PM $
 */
public abstract class BaseHandlerInterceptor implements MethodInterceptor {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
}
