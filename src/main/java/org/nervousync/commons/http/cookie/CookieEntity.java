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
package org.nervousync.commons.http.cookie;

import java.text.ParseException;

import org.nervousync.commons.core.Globals;
import org.nervousync.utils.DateTimeUtils;
import org.nervousync.utils.StringUtils;

/**
 * Cookie information
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Jan 4, 2018 1:01:35 PM $
 */
public final class CookieEntity {

	/**
	 * Cookie name
	 */
	private String name = null;
	/**
	 * Cookie value
	 */
	private String value = null;
	/**
	 * Cookie path
	 */
	private String path = null;
	/**
	 * Domain name
	 */
	private String domain = null;
	/**
	 * Cookie expires value
	 */
	private long expires = Globals.DEFAULT_VALUE_LONG;
	/**
	 * Maximum age value
	 */
	private long maxAge = Globals.DEFAULT_VALUE_LONG;
	/**
	 * Secure status
	 */
	private boolean secure = Boolean.FALSE;
	/**
	 * Version value if exists cookie name is "version"
	 */
	private int version = 0;
	
	/**
	 * Constructor
	 * @param responseCookieValue cookie value from response header information
	 */
	public CookieEntity(String responseCookieValue) {
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
