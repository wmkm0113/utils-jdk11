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
package org.nervousync.commons.adapter.xml;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/**
 * The type C data adapter.
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $Date: 2018-10-15 14:09
 */
public final class CDataAdapter extends XmlAdapter<String, String> {

	/**
	 * The constant CDATA_BEGIN.
	 */
	public static final String CDATA_BEGIN = "<![CDATA[";
	/**
	 * The constant CDATA_END.
	 */
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
			dataValue = dataValue.substring(0, dataValue.length() - CDATA_END.length());
		}
		return dataValue;
	}
	
	/**
	 * Convert a bound type to a value type.
	 *
	 * @param v The value to be converted. Can be null.
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
