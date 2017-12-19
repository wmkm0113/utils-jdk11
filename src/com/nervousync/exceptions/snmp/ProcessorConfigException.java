/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervous Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervous Studio.
 */
package com.nervousync.exceptions.snmp;

import com.nervousync.utils.ObjectUtils;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Oct 25, 2017 10:30:11 PM $
 */
public class ProcessorConfigException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -156906740773445520L;

	/**
	 * Create a new ProcessorConfigException with the specified message.
	 * @param msg the detail message
	 */
	public ProcessorConfigException(String msg) {
		super(msg);
	}

	/**
	 * Create a new ProcessorConfigException with the specified message
	 * and root cause.
	 * @param msg the detail message
	 * @param cause the root cause
	 */
	public ProcessorConfigException(String msg, Throwable cause) {
		super(msg, cause);
	}


	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof ProcessorConfigException)) {
			return false;
		}
		ProcessorConfigException otherBe = (ProcessorConfigException) other;
		return (getMessage().equals(otherBe.getMessage()) &&
				ObjectUtils.nullSafeEquals(getCause(), otherBe.getCause()));
	}

	public int hashCode() {
		return getMessage().hashCode();
	}
}
