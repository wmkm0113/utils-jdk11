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
package org.nervousync.huffman;

import java.util.Hashtable;

import org.nervousync.utils.StringUtils;

/**
 * The type Huffman object.
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: Nov 6, 2017 2:13:35 PM $
 */
public class HuffmanObject {

	private final Hashtable<String, Object> codeMapping = new Hashtable<>();
	private final String huffmanValue;

	/**
	 * Instantiates a new Huffman object.
	 *
	 * @param codeMapping  the code mapping
	 * @param huffmanValue the huffman value
	 */
	public HuffmanObject(Hashtable<String, Object> codeMapping, String huffmanValue) {
		if (codeMapping != null) {
			this.codeMapping.putAll(codeMapping);
		}
		this.huffmanValue = huffmanValue;
	}

	/**
	 * Generate code mapping string.
	 *
	 * @return the string
	 */
	public String generateCodeMapping() {
		return StringUtils.objectToString(this.codeMapping, StringUtils.StringType.JSON, true);
	}

	/**
	 * Gets code mapping.
	 *
	 * @return the codeMapping
	 */
	public Hashtable<String, Object> getCodeMapping() {
		return this.codeMapping;
	}

	/**
	 * Gets huffman value.
	 *
	 * @return the huffmanValue
	 */
	public String getHuffmanValue() {
		return huffmanValue;
	}
}
