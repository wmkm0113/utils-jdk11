/*
 * Copyright 2017 Nervousync Studio
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.nervousync.beans.ip;

import org.nervousync.enumerations.ip.IPType;
import org.nervousync.commons.core.Globals;

import java.io.Serializable;

/**
 * The type Ip range.
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $Date: 2018-09-21 18:25
 */
public final class IPRange implements Serializable {
	
	private static final long serialVersionUID = 7569297312912043791L;

	/**
	 * IP address type
	 */
	private IPType ipType;
	/**
	 * Begin address
	 */
	private String beginAddress = Globals.DEFAULT_VALUE_STRING;
	/**
	 * End address
	 */
	private String endAddress = Globals.DEFAULT_VALUE_STRING;

	/**
	 * Instantiates a new Ip range.
	 */
	public IPRange() {

	}

	/**
	 * Gets ip type.
	 *
	 * @return the ip type
	 */
	public IPType getIpType() {
		return ipType;
	}

	/**
	 * Sets ip type.
	 *
	 * @param ipType the ip type
	 */
	public void setIpType(IPType ipType) {
		this.ipType = ipType;
	}

	/**
	 * Gets begin address.
	 *
	 * @return the beginning address
	 */
	public String getBeginAddress() {
		return beginAddress;
	}

	/**
	 * Sets begin address.
	 *
	 * @param beginAddress the beginning address
	 */
	public void setBeginAddress(String beginAddress) {
		this.beginAddress = beginAddress;
	}

	/**
	 * Gets end address.
	 *
	 * @return the end address
	 */
	public String getEndAddress() {
		return endAddress;
	}

	/**
	 * Sets end address.
	 *
	 * @param endAddress the end address
	 */
	public void setEndAddress(String endAddress) {
		this.endAddress = endAddress;
	}
}
