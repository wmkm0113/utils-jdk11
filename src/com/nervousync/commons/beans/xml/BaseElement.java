/*
 * Copyright © 2003 - 2010 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervousync Studio.
 */
package com.nervousync.commons.beans.xml;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nervousync.exceptions.xml.XmlException;
import com.nervousync.utils.BeanUtils;
import com.nervousync.utils.ReflectionUtils;
import com.nervousync.utils.XmlUtils;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Sep 23, 2010, 2010 1:22:51 PM $
 */
public class BaseElement implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 239544550914272242L;
	
	protected transient final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public BaseElement() {
		
	}
	
	public void parseXml(Object xmlObj) {
		BaseElement parseObj = XmlUtils.convertToObject(xmlObj, this.getClass());
		BeanUtils.copyProperties(parseObj, this);
	}
	
	/**
	 * Convert Object to XML String By Nervous XML Util
	 * @return XML String
	 */
	public String toXML() throws XmlException {
		return XmlUtils.convertToXml(this, null, null, false);
	}

	/**
	 * Convert Object to XML String By Nervous XML Util 
	 * Expain all empty element
	 * 
	 * @param indent 
	 * @param expainEmptyElement expain empty element status
	 * @return XML String
	 */
	public String toXML(String indent) throws XmlException {
		return XmlUtils.convertToXml(this, indent, null, false);
	}
	
	/**
	 * Convert Object to XML String By Nervous XML Util 
	 * Expain all empty element
	 * 
	 * @param indent 
	 * @param expainEmptyElement expain empty element status
	 * @return XML String
	 */
	public String toXML(String indent, String encoding) throws XmlException {
		return XmlUtils.convertToXml(this, indent, encoding, false);
	}
	
	/**
	 * Convert Object to XML String By Nervous XML Util 
	 * Expain all empty element
	 * 
	 * @param indent 
	 * @param expainEmptyElement expain empty element status
	 * @return XML String
	 */
	public String toXML(String indent, String encoding, boolean expandEmptyElements) throws XmlException {
		return XmlUtils.convertToXml(this, indent, encoding, expandEmptyElements);
	}
	
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
				String fieldName = field.getName();
				String methodName = null;
				
				if (field.getType().equals(boolean.class)) {
					methodName = "is" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
				} else {
					methodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
				}
				
				if ("serialVersionUID".equals(fieldName)) {
					methodName = "getSerialversionuid";
				}
				
				Method getMethod = ReflectionUtils.findMethod(this.getClass(), methodName, new Class[]{});
				
				Object origValue = getMethod.invoke(this, new Object[]{});
				Object destValue = getMethod.invoke(o, new Object[]{});
				
				if (origValue != null ? !origValue.equals(destValue) : destValue != null) {
					return false;
				}
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	public int hashCode() {
		Field[] fields = this.getClass().getDeclaredFields();
		
		int result = 0;

		try {
			for (Field field : fields) {
				String fieldName = field.getName();
				String methodName = null;
				
				if (field.getType().equals(boolean.class)) {
					methodName = "is" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
				} else {
					methodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
				}
				
				if ("serialVersionUID".equals(fieldName)) {
					methodName = "getSerialversionuid";
				}
				
				Method getMethod = ReflectionUtils.findMethod(this.getClass(), methodName, new Class[]{});
				
				Object origValue = getMethod.invoke(this, new Object[]{});
				
				result = 29 * result + (origValue != null ? origValue.hashCode() : 0);
			}
		} catch (Exception e) {
			return 0;
		}
		
		return result;
	}
}
