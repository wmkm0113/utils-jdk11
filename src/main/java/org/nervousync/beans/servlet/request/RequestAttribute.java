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
package org.nervousync.beans.servlet.request;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Parse request attribute from HttpServletRequest
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Jun 19, 2015 8:56:50 AM $
 */
public final class RequestAttribute {

	/**
	 * Current session id
	 */
	private final String sessionId;
	/**
	 * Send attribute mapping for request
	 */
	private final Map<String, Object> attributeMap;

	private RequestAttribute(final HttpServletRequest request) {
		this.sessionId = request.getSession().getId();
		this.attributeMap = new HashMap<>();

		Enumeration<String> e = request.getAttributeNames();

		while (e.hasMoreElements()) {
			String name = e.nextElement();
			this.attributeMap.put(name, request.getAttribute(name));
		}
	}
	
	/**
	 * Parse given request
	 * @param request		HttpServletRequest will be parsed
	 * @return				Object of RequestAttribute
	 */
	public static RequestAttribute newInstance(final HttpServletRequest request) {
		return new RequestAttribute(request);
	}

	/**
	 * @return the sessionId
	 */
	public String getSessionId() {
		return sessionId;
	}

	/**
	 * @return the attributeMap
	 */
	public Map<String, Object> getAttributeMap() {
		return attributeMap;
	}
}
