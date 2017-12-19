/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervous Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervous Studio.
 */
package com.nervousync.commons.beans.snmp;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.nervousync.utils.DateTimeUtils;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Oct 25, 2017 9:55:18 PM $
 */
public final class SnmpData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9033003833049981503L;

	private long currentGMTTime;
	private String identifiedKey = null;
	private Map<String, String> dataMap = null;
	
	public SnmpData() {
		this.currentGMTTime = DateTimeUtils.currentGMTTimeMillis();
		this.dataMap = new HashMap<String, String>();
	}

	/**
	 * @return the currentGMTTime
	 */
	public long getCurrentGMTTime() {
		return currentGMTTime;
	}

	/**
	 * @return the identifiedKey
	 */
	public String getIdentifiedKey() {
		return identifiedKey;
	}
	
	/**
	 * @param identifiedKey the identifiedKey to set
	 */
	public void setIdentifiedKey(String identifiedKey) {
		this.identifiedKey = identifiedKey;
	}
	
	public void addData(String oid, String value) {
		if (!this.dataMap.containsKey(oid)) {
			this.dataMap.put(oid, value);
		}
	}
	
	public String readData(String oid) {
		return this.dataMap.get(oid);
	}
	
	public Iterator<Entry<String, String>> iterator() {
		return this.dataMap.entrySet().iterator();
	}
}
