/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Nervousync Studio.
 */
package com.nervousync.commons.adapter.xml;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $Date: 2018-10-15 14:09
 */
public class CDataAdapter extends XmlAdapter<String, String> {
	
	public static final String CDATA_BEGIN = "<![CDATA[";
	public static final String CDATA_END = "]]>";
	
	/**
	 * Convert a value type to a bound type.
	 *
	 * @param v The value to be converted. Can be null.
	 */
	@Override
	public String unmarshal(String v) {
		if (v == null) {
			return "";
		}
		
		String dataValue = v;
		if (dataValue.startsWith(CDATA_BEGIN)) {
			dataValue = dataValue.substring(CDATA_BEGIN.length());
		}
		
		if (dataValue.endsWith(CDATA_END)) {
			dataValue.substring(0, dataValue.length() - CDATA_END.length());
		}
		return dataValue;
	}
	
	/**
	 * Convert a bound type to a value type.
	 *
	 * @param v The value to be convereted. Can be null.
	 */
	@Override
	public String marshal(String v) {
		if (v == null) {
			return CDATA_BEGIN + CDATA_END;
		} else {
			return CDATA_BEGIN + v + CDATA_END;
		}
	}
}
