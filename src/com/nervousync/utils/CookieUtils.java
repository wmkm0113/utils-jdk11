/*
 * Copyright © 2003 - 2010 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervousync Studio.
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
				if (lifeCycle != null && lifeCycle.intValue() >= 0) {
					//	Default Cookie Life Cycle
					cookie.setMaxAge(60 * 60 * lifeCycle.intValue());
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
		
		if (cookies == null || cookies.length == 0) {
			return null;
		}
		
		int cookieLength = cookies.length;
		
		for (int i = 0 ; i < cookieLength ; i++) {
			Cookie cookie = cookies[i];
			if (cookie.getName().equals(cookieName)) {
				return cookie;
			}
		}
		
		return null;
	}

	public static String getCookieValue(String cookieName, HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		
		if (cookies == null || cookies.length == 0) {
			return null;
		}
		
		int cookieLength = cookies.length;
		
		for (int i = 0 ; i < cookieLength ; i++) {
			Cookie cookie = cookies[i];
			if (cookie.getName().equals(cookieName)) {
				return cookie.getValue();
			}
		}
		
		return null;
	}

	public static boolean delCookie(String cookieName, HttpServletRequest request, HttpServletResponse response) {
		return delCookie(getCookie(cookieName, request), response);
	}

	public static boolean delCookie(Cookie cookie, HttpServletResponse response) {
		addP3PHeader(response);
		if (cookie == null) {
			return true;
		}
		
		cookie.setMaxAge(0);
		
		response.addCookie(cookie);
		
		return true;
	}

	private static void addP3PHeader(HttpServletResponse response) {
		if (response != null) {
			response.addHeader("P3P:CP", "CURa ADMa DEVa PSAo PSDo OUR BUS UNI PUR INT DEM STA PRE COM NAV OTC NOI DSP COR");
		}
	}
}
