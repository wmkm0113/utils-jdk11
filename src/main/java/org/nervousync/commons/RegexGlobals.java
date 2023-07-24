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
package org.nervousync.commons;

/**
 * Regex Library
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: Apr 10, 2017 5:23:06 PM $
 */
public final class RegexGlobals {
	/**
	 * <span class="en">Regular expression for match email address.</span>
	 * <span class="zh-CN">正则表达式，用于匹配电子邮件地址</span>
	 */
	public static final String EMAIL_ADDRESS =
		"[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]*" +
		"[a-zA-Z0-9])?\\.)+(?:[A-Z]{2}|asia|com|org|net|gov|mil|biz|info|mobi|name|aero|jobs|museum|travel)\\b";
	/**
	 * <span class="en">Regular expression for match UUID string.</span>
	 * <span class="zh-CN">正则表达式，用于匹配UUID字符串</span>
	 */
	public static final String UUID = "^([0-9a-f]{8}((-[0-9a-f]{4}){3})-[0-9a-f]{12})|([0-9a-f]{32})\\b";
	/**
	 * <span class="en">Regular expression for match hex string of MD5 value.</span>
	 * <span class="zh-CN">正则表达式，用于匹配MD5值的十六进制字符串</span>
	 */
	public static final String MD5_VALUE = "^[0-9a-f]{32}\\b";
	/**
	 * <span class="en">Regular expression for match XML string.</span>
	 * <span class="zh-CN">正则表达式，用于匹配XML字符串</span>
	 */
	public static final String XML = "<[a-zA-Z0-9]+[^>]*>(?:.|[\\r\\n])*?<\\/[a-zA-Z0-9]+>";
	/**
	 * <span class="en">Regular expression for match Luhn mod 10.</span>
	 * <span class="zh-CN">正则表达式，用于匹配Luhn模10字符串</span>
	 */
	public static final String LUHN = "^[\\d]+$";
	/**
	 * <span class="en">Regular expression for match China ID number.</span>
	 * <span class="zh-CN">正则表达式，用于匹配中国身份证号</span>
	 */
	public static final String CHN_ID_Card = "^[1-9](\\d{17}|(\\d{16}X))$";
	/**
	 * <span class="en">Regular expression for match China Social Credit Code.</span>
	 * <span class="zh-CN">正则表达式，用于匹配中国统一社会信用代码</span>
	 */
	public static final String CHN_Social_Credit = "^([1-9]|A|N|Y)[\\dA-Z]{17}$";
	/**
	 * <span class="en">Regular expression for match IPv4 address.</span>
	 * <span class="zh-CN">正则表达式，用于匹配IPv4地址字符串</span>
	 */
	public static final String IPV4_REGEX = "^(?:(?:25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\\.){3}(?:25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])$";
	/**
	 * <span class="en">Regular expression for match IPv6 address.</span>
	 * <span class="zh-CN">正则表达式，用于匹配IPv6地址字符串</span>
	 */
	public static final String IPV6_REGEX = "(?ix)(?<![:.\\w])(?:[A-F0-9]{1,4}:){6}(?:[A-F0-9]{1,4}:[A-F0-9]{1,4}|(?:(?:25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\\.){3}(?:25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9]))(?![:.\\w])";
	/**
	 * <span class="en">Regular expression for match IPv6 compress address.</span>
	 * <span class="zh-CN">正则表达式，用于匹配IPv6压缩地址字符串</span>
	 */
	public static final String IPV6_COMPRESS_REGEX = "(?ix)(?<![:.\\w])(?:(?:[A-F0-9]{1,4}:){7}[A-F0-9]{1,4}|(?=(?:[A-F0-9]{0,4}:){0,7}[A-F0-9]{0,4}$)(([A-F0-9]{1,4}:){1,7}|:)((:[A-F0-9]{1,4}){1,7}|:)|(?:[A-F0-9]{1,4}:){7}:|:(:[A-F0-9]{1,4}){7})(?![:.\\w])";
	/**
	 * <span class="en">Regular expression for match phone number.</span>
	 * <span class="zh-CN">正则表达式，用于匹配电话号码字符串</span>
	 */
	public static final String PHONE_NUMBER = "^(00|\\+)(\\d){1,}$";
}
