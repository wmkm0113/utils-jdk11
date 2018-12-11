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
package com.nervousync.exceptions.servlet;

import com.nervousync.commons.core.Globals;
import com.nervousync.utils.ObjectUtils;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Dec 19, 2017 2:32:21 PM $
 */
public class RequestException extends Exception {
	
	/**
	 *
	 */
	private static final long serialVersionUID = -5116536960719845728L;
	
	/**
	 * Create a new RequestException with the specified message.
	 * @param msg the detail message
	 */
	public RequestException(String msg) {
		super(msg);
	}
	
	/**
	 * Create a new RequestException with the specified message
	 * and root cause.
	 * @param msg the detail message
	 * @param cause the root cause
	 */
	public RequestException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		
		if (this == other) {
			return true;
		}
		
		if (other instanceof RequestException) {
			RequestException otherBe = (RequestException) other;
			return (getMessage().equals(otherBe.getMessage()) &&
					ObjectUtils.nullSafeEquals(getCause(), otherBe.getCause()));
		}
		
		return Globals.DEFAULT_VALUE_BOOLEAN;
	}
	
	public int hashCode() {
		return getMessage().hashCode();
	}
}
