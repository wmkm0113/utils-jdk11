/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Nervousync Studio.
 */
package com.nervousync.commons.adapter.xml;

import com.nervousync.utils.DateTimeUtils;

import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.Date;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $Date: 2018-10-15 14:31
 */
public class DateTimeAdapter extends XmlAdapter<String, Date> {
	/**
	 * Convert a value type to a bound type.
	 *
	 * @param v The value to be converted. Can be null.
	 * @throws Exception if there's an error during the conversion. The caller is responsible for
	 *                   reporting the error to the user through {@link ValidationEventHandler}.
	 */
	@Override
	public Date unmarshal(String v) throws Exception {
		return DateTimeUtils.parseSiteMapDate(v);
	}
	
	/**
	 * Convert a bound type to a value type.
	 *
	 * @param v The value to be converted. Can be null.
	 */
	@Override
	public String marshal(Date v) {
		return DateTimeUtils.formatDateForSiteMap(v);
	}
}
