package com.nervousync.exceptions.beans;

import com.nervousync.commons.core.Globals;
import com.nervousync.utils.ObjectUtils;

public class FatalBeanException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8669110159361088248L;

	/**
	 * Create a new FatalBeanException with the specified message.
	 * @param msg the detail message
	 */
	public FatalBeanException(String msg) {
		super(msg);
	}

	/**
	 * Create a new FatalBeanException with the specified message
	 * and root cause.
	 * @param msg the detail message
	 * @param cause the root cause
	 */
	public FatalBeanException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		
		if (this == other) {
			return true;
		}
		
		if (other instanceof FatalBeanException) {
			FatalBeanException otherBe = (FatalBeanException) other;
			return (getMessage().equals(otherBe.getMessage()) &&
					ObjectUtils.nullSafeEquals(getCause(), otherBe.getCause()));
		}
		
		return Globals.DEFAULT_VALUE_BOOLEAN;
	}

	public int hashCode() {
		return getMessage().hashCode();
	}

}
