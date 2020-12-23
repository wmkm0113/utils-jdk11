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
package org.nervousync.commons.beans.ip;

import org.nervousync.enumerations.ip.IPType;
import org.nervousync.commons.core.Globals;

import java.io.Serializable;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $Date: 2018-09-21 18:25
 */
public final class IPRange implements Serializable {
	
	private static final long serialVersionUID = 7569297312912043791L;
	
	private IPType ipType;
	private String beginAddress = Globals.DEFAULT_VALUE_STRING;
	private String endAddress = Globals.DEFAULT_VALUE_STRING;

	public IPRange() {

	}

	public IPType getIpType() {
		return ipType;
	}

	public void setIpType(IPType ipType) {
		this.ipType = ipType;
	}

	public String getBeginAddress() {
		return beginAddress;
	}

	public void setBeginAddress(String beginAddress) {
		this.beginAddress = beginAddress;
	}

	public String getEndAddress() {
		return endAddress;
	}

	public void setEndAddress(String endAddress) {
		this.endAddress = endAddress;
	}
}
