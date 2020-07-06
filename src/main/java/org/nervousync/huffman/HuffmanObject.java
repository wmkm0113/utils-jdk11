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
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Nov 6, 2017 2:13:35 PM $
 */
public class HuffmanObject {

	private final Hashtable<String, Object> codeMapping = new Hashtable<>();
	private final String huffmanValue;
	
	public HuffmanObject(Hashtable<String, Object> codeMapping, String huffmanValue) {
		if (codeMapping != null) {
			codeMapping.forEach(this.codeMapping::put);
		}
		this.huffmanValue = huffmanValue;
	}

	public String generateCodeMapping() {
		return StringUtils.convertObjectToJSONString(this.codeMapping);
	}
	
	/**
	 * @return the codeMapping
	 */
	public Hashtable<String, Object> getCodeMapping() {
		Hashtable<String, Object> returnMap = new Hashtable<>();
		codeMapping.forEach(returnMap::put);
		return returnMap;
	}

	/**
	 * @return the huffmanValue
	 */
	public String getHuffmanValue() {
		return huffmanValue;
	}
}
