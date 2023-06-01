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
package org.nervousync.commons.core;

/**
 * Regex Library
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: Apr 10, 2017 5:23:06 PM $
 */
public final class RegexGlobals {

	/**
	 * The constant EMAIL_ADDRESS.
	 */
	public static final String EMAIL_ADDRESS =
		"[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]*" +
		"[a-zA-Z0-9])?\\.)+(?:[A-Z]{2}|asia|com|org|net|gov|mil|biz|info|mobi|name|aero|jobs|museum|travel)\\b";
	/**
	 * The constant UUID.
	 */
	public static final String UUID = "^([0-9a-f]{8}((-[0-9a-f]{4}){3})-[0-9a-f]{12})|([0-9a-f]{32})\\b";
	/**
	 * The constant MD5_VALUE.
	 */
	public static final String MD5_VALUE = "^[0-9a-f]{32}\\b";

	/**
	 * The constant XML.
	 */
	public static final String XML = "<[a-zA-Z0-9]+[^>]*>(?:.|[\\r\\n])*?<\\/[a-zA-Z0-9]+>";

	public static final String LUHN = "^[\\d]+$";

	public static final String CHN_ID_Card = "^[1-9](\\d{17}|(\\d{16}X))$";
	public static final String CHN_Social_Credit = "^([1-9]|A|N|Y)[\\dA-Z]{17}$";

	/**
	 * The constant IPV4_REGEX.
	 */
	public static final String IPV4_REGEX = "^(?:(?:25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\\.){3}(?:25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])$";
	/**
	 * The constant IPV6_REGEX.
	 */
	public static final String IPV6_REGEX = "(?ix)(?<![:.\\w])(?:[A-F0-9]{1,4}:){6}(?:[A-F0-9]{1,4}:[A-F0-9]{1,4}|(?:(?:25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\\.){3}(?:25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9]))(?![:.\\w])";
	/**
	 * The constant IPV6_COMPRESS_REGEX.
	 */
	public static final String IPV6_COMPRESS_REGEX = "(?ix)(?<![:.\\w])(?:(?:[A-F0-9]{1,4}:){7}[A-F0-9]{1,4}|(?=(?:[A-F0-9]{0,4}:){0,7}[A-F0-9]{0,4}$)(([A-F0-9]{1,4}:){1,7}|:)((:[A-F0-9]{1,4}){1,7}|:)|(?:[A-F0-9]{1,4}:){7}:|:(:[A-F0-9]{1,4}){7})(?![:.\\w])";

	/**
	 * The constant PHONE_NUMBER.
	 */
	public static final String PHONE_NUMBER = "^(00|\\+)(\\d){1,}$";
}
