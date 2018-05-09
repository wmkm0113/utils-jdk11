/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervousync Studio.
 */
package com.nervousync.commons.core;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Apr 10, 2017 5:23:06 PM $
 */
public final class RegexGlobals {

	public static final String EMAIL_ADDRESS = 
		"[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]*" +
		"[a-zA-Z0-9])?\\.)+(?:[A-Z]{2}|asia|com|org|net|gov|mil|biz|info|mobi|name|aero|jobs|museum|travel)\\b";
	public static final String UUID = "^([0-9a-f]{8}((-[0-9a-f]{4}){3})-[0-9a-f]{12})|([0-9a-f]{32})\\b";
	public static final String MD5_VALUE = "^[0-9a-f]{32}\\b";
	
	public static final String IPV4_REGEX = "^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$";
	public static final String IPV6_REGEX = "^([\\da-fA-F]{1,4}(:[\\da-fA-F]{1,4}){7}|([\\da-fA-F]{1,4}){0,1}:(:[\\da-fA-F]{1,4}){1,7}|[\\da-fA-F]{1,4}::|::)$";
	public static final String IPV6_COMPRESS_REGEX = "(^|:)(0+(:|$)){2,8}";
}
