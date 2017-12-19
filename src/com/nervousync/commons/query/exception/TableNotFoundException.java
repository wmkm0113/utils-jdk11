/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervousync Studio.
 */
package com.nervousync.commons.query.exception;

import com.nervousync.utils.ObjectUtils;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Apr 15, 2016 6:41:30 PM $
 */
public class TableNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6841629417497956520L;
	
	public TableNotFoundException() {
		super("");
	}

	/**
	 * Create a new TableNotFoundException with the specified message.
	 * @param msg the detail message
	 */
	public TableNotFoundException(String msg) {
		super(msg);
	}

	/**
	 * Create a new TableNotFoundException with the specified message
	 * and root cause.
	 * @param msg the detail message
	 * @param cause the root cause
	 */
	public TableNotFoundException(String msg, Throwable cause) {
		super(msg, cause);
	}


	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof TableNotFoundException)) {
			return false;
		}
		TableNotFoundException otherBe = (TableNotFoundException) other;
		return (getMessage().equals(otherBe.getMessage()) &&
				ObjectUtils.nullSafeEquals(getCause(), otherBe.getCause()));
	}

	public int hashCode() {
		return getMessage().hashCode();
	}

}
