/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervous Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervous Studio.
 */
package com.nervousync.exceptions.zip;

import com.nervousync.commons.core.Globals;
import com.nervousync.utils.ObjectUtils;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Nov 22, 2017 4:08:31 PM $
 */
public class ZipException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4262795571663295796L;
	
	public ZipException() {
		super();
	}
	
	public ZipException(Throwable cause) {
		super(cause);
	}

	/**
	 * Create a new ZipException with the specified message.
	 * @param msg the detail message
	 */
	public ZipException(String msg) {
		super(msg);
	}

	/**
	 * Create a new ZipException with the specified message
	 * and root cause.
	 * @param msg the detail message
	 * @param cause the root cause
	 */
	public ZipException(String msg, Throwable cause) {
		super(msg, cause);
	}


	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other instanceof ZipException) {
			ZipException otherBe = (ZipException) other;
			return (getMessage().equals(otherBe.getMessage()) &&
					ObjectUtils.nullSafeEquals(getCause(), otherBe.getCause()));
		}
		
		return Globals.DEFAULT_VALUE_BOOLEAN;
	}

	public int hashCode() {
		return getMessage().hashCode();
	}
}
