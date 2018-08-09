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
package com.nervousync.commons.beans.xml;

import java.io.Serializable;
import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nervousync.commons.core.Globals;
import com.nervousync.exceptions.xml.XmlException;
import com.nervousync.utils.BeanUtils;
import com.nervousync.utils.ReflectionUtils;
import com.nervousync.utils.XmlUtils;

/**
 * Base element define, all xml object define must extends this class
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Sep 23, 2010, 2010 1:22:51 PM $
 */
public class BaseElement implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 239544550914272242L;
	
	protected transient final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Parse xml string and setting fields data to this object
	 * @param xmlObj	XML string will be parsed
	 */
	public void parseXml(Object xmlObj) {
		BaseElement parseObj = XmlUtils.convertToObject(xmlObj, this.getClass());
		BeanUtils.copyProperties(parseObj, this);
	}
	
	/**
	 * Convert Object to XML String By Nervousync XML Util
	 * @return XML String
	 */
	public String toString() throws XmlException {
		return this.toString(null);
	}

	/**
	 * Convert Object to XML String By Nervousync XML Util 
	 * Explain all empty element
	 * 
	 * @param indent 				Indent string
	 * @return XML String
	 */
	public String toString(String indent) throws XmlException {
		return this.toString(indent, null);
	}
	
	/**
	 * Convert Object to XML String By Nervousync XML Util 
	 * Explain all empty element
	 * 
	 * @param indent 				Indent string
	 * @param encoding				Charset encoding
	 * @return XML String
	 */
	public String toString(String indent, String encoding) throws XmlException {
		return this.toString(indent, encoding, Globals.DEFAULT_VALUE_BOOLEAN);
	}
	
	/**
	 * Convert Object to XML String By Nervousync XML Util 
	 * Explain all empty element
	 * 
	 * @param indent 				Indent string
	 * @param encoding				Charset encoding
	 * @param expandEmptyElements 	Explain empty element status
	 * @return XML String
	 */
	public String toString(String indent, String encoding, boolean expandEmptyElements) throws XmlException {
		return XmlUtils.convertToXml(this, indent, encoding, expandEmptyElements);
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		
		if (this == o) {
			return true;
		}
		
		if (!o.getClass().equals(this.getClass())) {
			return false;
		}
		
		Field[] fields = this.getClass().getDeclaredFields();
		
		try {
			for (Field field : fields) {
				Object origValue = ReflectionUtils.getFieldValue(field, this);
				Object destValue = ReflectionUtils.getFieldValue(field, o);
				
				if (origValue != null ? !origValue.equals(destValue) : destValue != null) {
					return false;
				}
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		Field[] fields = this.getClass().getDeclaredFields();
		
		int result = Globals.INITIAL_HASH;

		try {
			for (Field field : fields) {
				Object origValue = ReflectionUtils.getFieldValue(field, this);
				result = Globals.MULTIPLIER * result + (origValue != null ? origValue.hashCode() : 0);
			}
		} catch (Exception e) {
			result = Globals.INITIAL_HASH;
		}
		
		return result;
	}
}
