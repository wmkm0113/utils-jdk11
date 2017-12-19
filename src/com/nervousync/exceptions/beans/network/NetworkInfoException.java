/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervousync Studio.
 */
package com.nervousync.exceptions.beans.network;

import com.nervousync.commons.core.Globals;
import com.nervousync.utils.ObjectUtils;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Jul 24, 2015 12:25:26 PM $
 */
public class NetworkInfoException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1431511763422513673L;

	/**
	 * Create a new NetworkInfoException with the specified message.
	 * @param msg the detail message
	 */
	public NetworkInfoException(String msg) {
		super(msg);
	}

	/**
	 * Create a new NetworkInfoException with the specified message
	 * and root cause.
	 * @param msg the detail message
	 * @param cause the root cause
	 */
	public NetworkInfoException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		
		if (this == other) {
			return true;
		}
		
		if (other instanceof NetworkInfoException) {
			NetworkInfoException otherBe = (NetworkInfoException) other;
			return (getMessage().equals(otherBe.getMessage()) &&
					ObjectUtils.nullSafeEquals(getCause(), otherBe.getCause()));
		}
		
		return Globals.DEFAULT_VALUE_BOOLEAN;
	}

	public int hashCode() {
		return getMessage().hashCode();
	}
}
