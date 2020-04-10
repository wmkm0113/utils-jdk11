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
package com.nervousync.commons.beans.xml.cache;

import javax.xml.bind.annotation.*;

import com.nervousync.commons.beans.xml.BaseElement;
import com.nervousync.commons.core.Globals;

/**
 * Cache server define
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Apr 25, 2017 3:18:38 PM $
 */
@XmlType
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public final class CacheServer extends BaseElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9179968915973853412L;

	/**
	 * Server address
	 */
	@XmlElement
	private String serverAddress		= "";
	/**
	 * Authenticate user name
	 */
	@XmlElement
	private String serverUserName		= "";
	/**
	 * Authenticate password
	 */
	@XmlElement
	private String serverPassword		= "";
	/**
	 * Server port number
	 */
	@XmlElement
	private int serverPort				= Globals.DEFAULT_VALUE_INT;
	/**
	 * Server weight
	 */
	@XmlElement
	private int serverWeight			= Globals.DEFAULT_CACHE_SERVER_WEIGHT;
	/**
	 * Is read only status
	 */
	@XmlElement
	private boolean readOnly			= Globals.DEFAULT_VALUE_BOOLEAN;
	
	/**
	 * Default constructor
	 */
	public CacheServer() {
		
	}

	/**
	 * Constructor by default server weight
	 * @param serverAddress     server address
	 * @param serverUserName    server user name
	 * @param serverPassword    server password
	 * @param serverPort        server port
	 */
	public CacheServer(String serverAddress, String serverUserName, String serverPassword, int serverPort) {
		this.serverAddress = serverAddress;
		this.serverUserName = serverUserName;
		this.serverPassword = serverPassword;
		this.serverPort = serverPort;
	}

	/**
	 * Constructor by given values
	 * @param serverAddress     server address
	 * @param serverUserName    server user name
	 * @param serverPassword    server password
	 * @param serverPort        server port
	 * @param serverWeight      server weight
	 * @param readOnly          server read only
	 */
	public CacheServer(String serverAddress, String serverUserName, String serverPassword, int serverPort, 
			int serverWeight, boolean readOnly) {
		this.serverAddress = serverAddress;
		this.serverUserName = serverUserName;
		this.serverPassword = serverPassword;
		this.serverPort = serverPort;
		this.serverWeight = serverWeight;
		this.readOnly = readOnly;
	}
	
	/**
	 * Equals server information
	 * @param serverAddress     server address
	 * @param serverPort        server port
	 * @return                  equal result
	 */
	public boolean equals(String serverAddress, int serverPort) {
		return (this.serverAddress.equalsIgnoreCase(serverAddress) && this.serverPort == serverPort);
	}

	/**
	 * @return the serverAddress
	 */
	public String getServerAddress() {
		return serverAddress;
	}

	/**
	 * @param serverAddress the serverAddress to set
	 */
	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	/**
	 * @return the serverUserName
	 */
	public String getServerUserName() {
		return serverUserName;
	}

	/**
	 * @param serverUserName the serverUserName to set
	 */
	public void setServerUserName(String serverUserName) {
		this.serverUserName = serverUserName;
	}

	/**
	 * @return the serverPassword
	 */
	public String getServerPassword() {
		return serverPassword;
	}

	/**
	 * @param serverPassword the serverPassword to set
	 */
	public void setServerPassword(String serverPassword) {
		this.serverPassword = serverPassword;
	}

	/**
	 * @return the serverPort
	 */
	public int getServerPort() {
		return serverPort;
	}

	/**
	 * @param serverPort the serverPort to set
	 */
	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	/**
	 * @return the serverWeight
	 */
	public int getServerWeight() {
		return serverWeight;
	}

	/**
	 * @param serverWeight the serverWeight to set
	 */
	public void setServerWeight(int serverWeight) {
		this.serverWeight = serverWeight;
	}

	/**
	 * @return the readOnly
	 */
	public boolean isReadOnly() {
		return readOnly;
	}

	/**
	 * @param readOnly the readOnly to set
	 */
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	/**
	 * Gets the value of serialVersionUID
	 *
	 * @return the value of serialVersionUID
	 */
	public static long getSerialVersionUID() {
		return serialVersionUID;
	}
}
