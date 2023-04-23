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
package org.nervousync.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.nervousync.commons.core.Globals;

import java.util.Arrays;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Jan 13, 2010 1:49:41 PM $
 */
public final class CookieUtils {

	private CookieUtils() {
	}

	/**
	 * Set cookie
	 * @param cookieName    cookie name
	 * @param cookieValue   cookie value
	 * @param request       http request
	 * @param response      http response
	 * @return              Set result
	 */
	public static boolean setCookie(String cookieName, String cookieValue,
									HttpServletRequest request, HttpServletResponse response) {
		return setCookie(cookieName, cookieValue, null, null, request, response);
	}

	/**
	 * Set cookie
	 * @param cookieName    cookie name
	 * @param cookieValue   cookie value
	 * @param domainName    domain name
	 * @param request       http request
	 * @param response      http response
	 * @return              Set result
	 */
	public static boolean setCookie(String cookieName, String cookieValue, String domainName,
									HttpServletRequest request, HttpServletResponse response) {
		return setCookie(cookieName, cookieValue, domainName, null, request, response);
	}

	/**
	 * Set cookie
	 * @param cookieName    cookie name
	 * @param cookieValue   cookie value
	 * @param lifeCycle     life cycle
	 * @param request       http request
	 * @param response      http response
	 * @return              Set result
	 */
	public static boolean setCookie(String cookieName, String cookieValue, Integer lifeCycle,
									HttpServletRequest request, HttpServletResponse response) {
		return setCookie(cookieName, cookieValue, null, lifeCycle, request, response);
	}

	/**
	 * Set cookie
	 * @param cookieName    cookie name
	 * @param cookieValue   cookie value
	 * @param domainName    domain name
	 * @param lifeCycle     life cycle
	 * @param request       http request
	 * @param response      http response
	 * @return              Set result
	 */
	public static boolean setCookie(String cookieName, String cookieValue, String domainName,
									Integer lifeCycle, HttpServletRequest request, HttpServletResponse response) {
		try {
			Cookie cookie = getCookie(cookieName, request);

			if (cookie == null) {
				cookie = new Cookie(cookieName, cookieValue);

				cookie.setPath("/");
				if (domainName != null) {
					cookie.setDomain("." + domainName);
				}
				if (lifeCycle != null && lifeCycle >= 0) {
					//	Default Cookie Life Cycle
					cookie.setMaxAge(60 * 60 * lifeCycle);
				}
			} else {
				cookie.setValue(cookieValue);
			}

			response.addCookie(cookie);

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Read cookie value
	 * @param cookieName    cookie name
	 * @param request       http request
	 * @return              cookie instance or null if not exists
	 */
	public static Cookie getCookie(String cookieName, HttpServletRequest request) {
		return Arrays.stream(request.getCookies())
				.filter(cookie -> cookie.getName().equalsIgnoreCase(cookieName))
				.findFirst()
				.orElse(null);
	}

	/**
	 * Read cookie value
	 * @param cookieName    cookie name
	 * @param request       http request
	 * @return              cookie value or null if not exists
	 */
	public static String getCookieValue(String cookieName, HttpServletRequest request) {
		return Arrays.stream(request.getCookies())
				.filter(cookie -> cookie.getName().equalsIgnoreCase(cookieName))
				.findFirst()
				.map(Cookie::getValue)
				.orElse(Globals.DEFAULT_VALUE_STRING);
	}

	/**
	 * Remove cookie
	 * @param cookieName    cookie name
	 * @param request       http request
	 * @param response      http response
	 */
	public static void delCookie(String cookieName, HttpServletRequest request, HttpServletResponse response) {
		delCookie(getCookie(cookieName, request), response);
	}

	/**
	 * Remove cookie
	 * @param cookie        cookie instance
	 * @param response      http response
	 */
	public static void delCookie(Cookie cookie, HttpServletResponse response) {
		if (cookie != null) {
			cookie.setMaxAge(0);
			response.addCookie(cookie);
		}
	}
}
