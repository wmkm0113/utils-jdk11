/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervousync Studio.
 */
package com.nervousync.exceptions.xml;

import com.nervousync.commons.core.Globals;
import com.nervousync.utils.ObjectUtils;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Mar 28, 2016 11:41:40 AM $
 */
public class XmlException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6884662156679821138L;
	
	public XmlException() {
		super();
	}
	
	public XmlException(Throwable cause) {
		super(cause);
	}

	/**
	 * Create a new BeansException with the specified message.
	 * @param msg the detail message
	 */
	public XmlException(String msg) {
		super(msg);
	}

	/**
	 * Create a new BeansException with the specified message
	 * and root cause.
	 * @param msg the detail message
	 * @param cause the root cause
	 */
	public XmlException(String msg, Throwable cause) {
		super(msg, cause);
	}


	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other instanceof XmlException) {
			XmlException otherBe = (XmlException) other;
			return (getMessage().equals(otherBe.getMessage()) &&
					ObjectUtils.nullSafeEquals(getCause(), otherBe.getCause()));
		}
		
		return Globals.DEFAULT_VALUE_BOOLEAN;
	}

	public int hashCode() {
		return getMessage().hashCode();
	}
}
