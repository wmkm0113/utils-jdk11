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
package org.nervousync.exceptions.cache;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Apr 25, 2017 6:30:42 PM $
 */
public final class CacheException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -156775157749202954L;

	/**
	 * Creates a new instance of CachedException without detail message.
	 */
	public CacheException() {
	}

	/**
	 * Constructs an instance of CachedException with the specified detail message.
	 *
	 * @param errorMessage The detail message.
	 */
	public CacheException(String errorMessage) {
		super(errorMessage);
	}

	/**
	 * Creates an instance of CachedException with nested exception
	 *
	 * @param e Nested exception
	 */
	public CacheException(Exception e) {
		super(e);
	}
}
