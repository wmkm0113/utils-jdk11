/*
 * Copyright 2018 Nervousync Studio
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
package org.nervousync.commons.http.header;

/**
 * Simple header of request
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Jan 4, 2018 12:15:18 PM $
 */
public final class SimpleHeader {

	/**
	 * Header name
	 */
	private final String headerName;
	/**
	 * Header value
	 */
	private final String headerValue;
	
	/**
	 * Default constructor
	 * @param headerName		Header name
	 * @param headerValue		Header value
	 */
	public SimpleHeader(String headerName, String headerValue) {
		this.headerName = headerName;
		this.headerValue = headerValue;
	}

	/**
	 * @return the headerName
	 */
	public String getHeaderName() {
		return headerName;
	}

	/**
	 * @return the headerValue
	 */
	public String getHeaderValue() {
		return headerValue;
	}
}
