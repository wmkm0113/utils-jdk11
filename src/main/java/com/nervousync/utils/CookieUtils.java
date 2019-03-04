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
package com.nervousync.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Jan 13, 2010 1:49:41 PM $
 */
public final class CookieUtils {

	private CookieUtils() {
		
	}

	public static boolean setCookie(String cookieName, String cookieValue, 
			HttpServletRequest request, HttpServletResponse response) {
		return setCookie(cookieName, cookieValue, null, null, request, response);
	}

	public static boolean setCookie(String cookieName, String cookieValue, String domainName, 
			HttpServletRequest request, HttpServletResponse response) {
		return setCookie(cookieName, cookieValue, domainName, null, request, response);
	}

	public static boolean setCookie(String cookieName, String cookieValue, Integer lifeCycle, 
			HttpServletRequest request, HttpServletResponse response) {
		return setCookie(cookieName, cookieValue, null, lifeCycle, request, response);
	}

	public static boolean setCookie(String cookieName, String cookieValue, String domainName, 
			Integer lifeCycle, HttpServletRequest request, HttpServletResponse response) {
		CookieUtils.addP3PHeader(response);
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

	public static Cookie getCookie(String cookieName, HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		
		if (cookies != null && cookies.length > 0) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(cookieName)) {
					return cookie;
				}
			}
		}

		return null;
	}

	public static String getCookieValue(String cookieName, HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		
		if (cookies != null && cookies.length > 0) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(cookieName)) {
					return cookie.getValue();
				}
			}
		}

		return null;
	}

	public static void delCookie(String cookieName, HttpServletRequest request, HttpServletResponse response) {
		delCookie(getCookie(cookieName, request), response);
	}

	public static void delCookie(Cookie cookie, HttpServletResponse response) {
		addP3PHeader(response);
		if (cookie != null) {
			cookie.setMaxAge(0);
			response.addCookie(cookie);
		}
	}

	private static void addP3PHeader(HttpServletResponse response) {
		if (response != null) {
			response.addHeader("P3P:CP", "CURa ADMa DEVa PSAo PSDo OUR BUS UNI PUR INT DEM STA PRE COM NAV OTC NOI DSP COR");
		}
	}
}
