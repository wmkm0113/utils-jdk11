package com.nervousync.exceptions.beans.interceptor.download;

import com.nervousync.commons.core.Globals;

import com.nervousync.utils.ObjectUtils;

public class DownloadInterceptorException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8864243933042922923L;

	/**
	 * Create a new DownloadInterceptorException with the specified message.
	 * @param msg the detail message
	 */
	public DownloadInterceptorException(String msg) {
		super(msg);
	}

	/**
	 * Create a new DownloadInterceptorException with the specified message
	 * and root cause.
	 * @param msg the detail message
	 * @param cause the root cause
	 */
	public DownloadInterceptorException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		
		if (this == other) {
			return true;
		}
		
		if (other instanceof DownloadInterceptorException) {
			DownloadInterceptorException otherBe = (DownloadInterceptorException) other;
			return (getMessage().equals(otherBe.getMessage()) &&
					ObjectUtils.nullSafeEquals(getCause(), otherBe.getCause()));
		}
		
		return Globals.DEFAULT_VALUE_BOOLEAN;
	}

	public int hashCode() {
		return getMessage().hashCode();
	}

}
