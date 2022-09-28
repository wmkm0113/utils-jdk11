/*
 * Copyright 2017 Nervousync Studio
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.nervousync.exceptions.beans.network;

import org.nervousync.utils.ObjectUtils;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Jul 24, 2015 12:18:33 PM $
 */
public final class IPAddressException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3604984790027973079L;

	/**
	 * Create a new IPAddressException with the specified message.
	 * @param msg the detail message
	 */
	public IPAddressException(String msg) {
		super(msg);
	}

	/**
	 * Create a new IPAddressException with the specified message
	 * and root cause.
	 * @param msg the detail message
	 * @param cause the root cause
	 */
	public IPAddressException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public boolean equals(Object other) {
		if (other == null) {
			return Boolean.FALSE;
		}
		
		if (this == other) {
			return Boolean.TRUE;
		}
		
		if (other instanceof IPAddressException) {
			IPAddressException otherBe = (IPAddressException) other;
			return (getMessage().equals(otherBe.getMessage()) &&
					ObjectUtils.nullSafeEquals(getCause(), otherBe.getCause()));
		}
		
		return Boolean.FALSE;
	}

	public int hashCode() {
		return getMessage().hashCode();
	}
}
