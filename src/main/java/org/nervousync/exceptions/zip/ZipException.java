/*
 * Licensed to the Nervousync Studio (NSYC) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.nervousync.exceptions.zip;

import org.nervousync.utils.ObjectUtils;

/**
 * The type Zip exception.
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: Nov 22, 2017 4:08:31 PM $
 */
public class ZipException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4262795571663295796L;

	/**
	 * Instantiates a new Zip exception.
	 */
	public ZipException() {
		super();
	}

	/**
	 * Instantiates a new Zip exception.
	 *
	 * @param cause the cause
	 */
	public ZipException(Throwable cause) {
		super(cause);
	}

	/**
	 * Create a new ZipException with the specified message.
	 *
	 * @param msg the detail message
	 */
	public ZipException(String msg) {
		super(msg);
	}

	/**
	 * Create a new ZipException with the specified message
	 * and root cause.
	 *
	 * @param msg   the detail message
	 * @param cause the root cause
	 */
	public ZipException(String msg, Throwable cause) {
		super(msg, cause);
	}


	public boolean equals(Object other) {
		if (this == other) {
			return Boolean.TRUE;
		}
		if (other instanceof ZipException) {
			ZipException otherBe = (ZipException) other;
			return (getMessage().equals(otherBe.getMessage()) &&
					ObjectUtils.nullSafeEquals(getCause(), otherBe.getCause()));
		}
		
		return Boolean.FALSE;
	}

	public int hashCode() {
		return getMessage().hashCode();
	}
}
