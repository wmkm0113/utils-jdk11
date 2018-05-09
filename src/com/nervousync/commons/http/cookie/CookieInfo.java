/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervousync Studio.
 */
package com.nervousync.commons.http.cookie;

import java.text.ParseException;

import com.nervousync.commons.core.Globals;
import com.nervousync.utils.DateTimeUtils;
import com.nervousync.utils.StringUtils;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Jan 4, 2018 1:01:35 PM $
 */
public final class CookieInfo {

	private String name = null;
	private String value = null;
	private String path = null;
	private String domain = null;
	private long expires = Globals.DEFAULT_VALUE_LONG;
	private long maxAge = Globals.DEFAULT_VALUE_LONG;
	private boolean secure = Globals.DEFAULT_VALUE_BOOLEAN;
	private int version = 0;
	
	public CookieInfo(String responseCookieValue) {
		if (responseCookieValue != null && responseCookieValue.length() > 0) {
			String[] cookieItems = StringUtils.delimitedListToStringArray(responseCookieValue, ";");
			for (String cookieItem : cookieItems) {
				String[] cookieInfo = StringUtils.delimitedListToStringArray(cookieItem, "=");
				if (cookieInfo.length == 2) {
					if ("path".equalsIgnoreCase(cookieInfo[0])) {
						this.path = cookieInfo[1];
					} else if ("domain".equalsIgnoreCase(cookieInfo[0])) {
						this.domain = cookieInfo[1];
					} else if ("expires".equalsIgnoreCase(cookieInfo[0])) {
						try {
							this.expires = DateTimeUtils.parseGMTDate(cookieInfo[1]).getTime();
						} catch (ParseException e) {
							this.expires = Globals.DEFAULT_VALUE_LONG;
						}
					} else if ("max-age".equalsIgnoreCase(cookieInfo[0])) {
						this.maxAge = Long.parseLong(cookieInfo[1]);
					} else if ("version".equalsIgnoreCase(cookieInfo[0])) {
						this.version = Integer.parseInt(cookieInfo[1]);
					} else {
						this.name = cookieInfo[0];
						this.value = cookieInfo[1];
					}
				} else if (cookieInfo.length == 1 
						&& "secure".equalsIgnoreCase(cookieInfo[0])) {
					this.secure = true;
				}
			}
		}
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @return the domain
	 */
	public String getDomain() {
		return domain;
	}

	/**
	 * @return the expires
	 */
	public long getExpires() {
		return expires;
	}

	/**
	 * @return the maxAge
	 */
	public long getMaxAge() {
		return maxAge;
	}

	/**
	 * @return the secure
	 */
	public boolean isSecure() {
		return secure;
	}

	/**
	 * @return the version
	 */
	public int getVersion() {
		return version;
	}
}
