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
package org.nervousync.beans.snmp;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.nervousync.utils.DateTimeUtils;

/**
 * Reading data maps of SNMP datas
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: Oct 25, 2017 9:55:18 PM $
 */
public final class SNMPData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9033003833049981503L;

	/**
	 * GMT time of reading datas operate.
	 */
	private final long currentGMTTime;
	/**
	 * Identified key
	 */
	private String identifiedKey = null;
	/**
	 * Reading data map
	 */
	private final Map<String, String> dataMap;

	/**
	 * Instantiates a new Snmp data.
	 */
	public SNMPData() {
		this.currentGMTTime = DateTimeUtils.currentUTCTimeMillis();
		this.dataMap = new HashMap<>();
	}

	/**
	 * Gets current gmt time.
	 *
	 * @return the currentGMTTime
	 */
	public long getCurrentGMTTime() {
		return currentGMTTime;
	}

	/**
	 * Gets identified key.
	 *
	 * @return the identifiedKey
	 */
	public String getIdentifiedKey() {
		return identifiedKey;
	}

	/**
	 * Sets identified key.
	 *
	 * @param identifiedKey the identifiedKey to set
	 */
	public void setIdentifiedKey(String identifiedKey) {
		this.identifiedKey = identifiedKey;
	}

	/**
	 * Add data.
	 *
	 * @param oid   the oid
	 * @param value the value
	 */
	public void addData(String oid, String value) {
		if (!this.dataMap.containsKey(oid)) {
			this.dataMap.put(oid, value);
		}
	}

	/**
	 * Read data string.
	 *
	 * @param oid the oid
	 * @return the string
	 */
	public String readData(String oid) {
		return this.dataMap.get(oid);
	}

	/**
	 * Iterator.
	 *
	 * @return the iterator
	 */
	public Iterator<Entry<String, String>> iterator() {
		return this.dataMap.entrySet().iterator();
	}
}
