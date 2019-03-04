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
