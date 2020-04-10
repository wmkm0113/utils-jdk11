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

package com.nervousync.exceptions.crc;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: 4/10/2020 3:12 PM $
 */
public class CRCException extends RuntimeException {

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = -1103811278288860831L;

	/**
	 * Creates a new instance of CRCException without detail message.
	 */
	public CRCException() {
	}

	/**
	 * Constructs an instance of CRCException with the specified detail message.
	 *
	 * @param errorMessage The detail message.
	 */
	public CRCException(String errorMessage) {
		super(errorMessage);
	}

	/**
	 * Creates an instance of CRCException with nested exception
	 *
	 * @param e Nested exception
	 */
	public CRCException(Exception e) {
		super(e);
	}
}
