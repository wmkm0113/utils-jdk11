package com.nervousync.exceptions.file;

import com.nervousync.exceptions.beans.BeansException;
import com.nervousync.utils.ObjectUtils;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: 2018-12-22 12:58 $
 */
public class FileProtocolNotSupportException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 9107162443379919761L;

	/**
	 * Create a new FileProtocolNotSupportException with the specified message.
	 * @param msg the detail message
	 */
	public FileProtocolNotSupportException(String msg) {
		super(msg);
	}

	/**
	 * Create a new FileProtocolNotSupportException with the specified message
	 * and root cause.
	 * @param msg the detail message
	 * @param cause the root cause
	 */
	public FileProtocolNotSupportException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof BeansException)) {
			return false;
		}
		BeansException otherBe = (BeansException) other;
		return (getMessage().equals(otherBe.getMessage()) &&
				ObjectUtils.nullSafeEquals(getCause(), otherBe.getCause()));
	}

	public int hashCode() {
		return getMessage().hashCode();
	}
}
