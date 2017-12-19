/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervous Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervous Studio.
 */
package com.nervousync.huffman;

import java.util.Hashtable;

import com.nervousync.utils.StringUtils;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Nov 6, 2017 2:13:35 PM $
 */
public class HuffmanObject {

	private Hashtable<String, Object> codeMapping = new Hashtable<String, Object>();
	private String huffmanValue = null;
	
	public HuffmanObject(Hashtable<String, Object> codeMapping, String huffmanValue) {
		this.codeMapping = codeMapping;
		this.huffmanValue = huffmanValue;
	}

	public String generateCodeMapping() {
		return StringUtils.convertObjectToJSONString(this.codeMapping);
	}
	
	/**
	 * @return the codeMapping
	 */
	public Hashtable<String, Object> getCodeMapping() {
		return codeMapping;
	}

	/**
	 * @return the huffmanValue
	 */
	public String getHuffmanValue() {
		return huffmanValue;
	}
}
