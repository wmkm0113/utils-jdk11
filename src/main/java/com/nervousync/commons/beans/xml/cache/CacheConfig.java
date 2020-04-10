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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.*;

import com.nervousync.commons.beans.xml.BaseElement;
import com.nervousync.commons.core.Globals;

/**
 * Cache server configure
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Apr 25, 2017 3:09:14 PM $
 */
@XmlType
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public final class CacheConfig extends BaseElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6700233652090948759L;

	@XmlElement
	private String providerName                    = null;
	/**
	 * Cache server list
	 */
	@XmlElementWrapper
	@XmlElement(name = "cache-server")
	private List<CacheServer> serverConfigList;
	/**
	 * Cache server connect timeout
	 */
	@XmlElement
	private int connectTimeout						= 1;
	/**
	 * Cache server operate timeout
	 */
	@XmlElement
	private int serverTimeout						= 1;
	/**
	 * Default expire time
	 */
	@XmlElement
	private int expireTime							= Globals.DEFAULT_VALUE_INT;
	/**
	 * Client pool size
	 */
	@XmlElement
	private int clientPoolSize						= 5;
	/**
	 * Maximum client size
	 */
	@XmlElement
	private int maximumClient						= 500;
	
	public CacheConfig() {
		this.serverConfigList = new ArrayList<>();
	}

	public CacheConfig(String providerName, int connectTimeout, int serverTimeout,
			int expireTime, int clientPoolSize, int maximumClient) {
		this.providerName = providerName;
		this.connectTimeout = connectTimeout;
		this.serverTimeout = serverTimeout;
		this.expireTime = expireTime;
		this.clientPoolSize = clientPoolSize;
		this.maximumClient = maximumClient;
		this.serverConfigList = new ArrayList<>();
	}
	
	public void addCacheServer(String serverAddress, String serverUserName, 
			String serverPassword, int serverPort) {
		if (this.serverExists(serverAddress, serverPort)) {
			return;
		}
		this.serverConfigList.add(new CacheServer(serverAddress, serverUserName, serverPassword, serverPort));
	}

	/**
	 * Gets the value of serialVersionUID
	 *
	 * @return the value of serialVersionUID
	 */
	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	/**
	 * Gets the value of providerName
	 *
	 * @return the value of providerName
	 */
	public String getProviderName() {
		return providerName;
	}

	/**
	 * Sets the providerName
	 *
	 * @param providerName the providerName to set
	 */
	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}

	/**
	 * @return the serverConfigList
	 */
	public List<CacheServer> getServerConfigList() {
		return serverConfigList;
	}

	/**
	 * @param serverConfigList the serverConfigList to set
	 */
	public void setServerConfigList(List<CacheServer> serverConfigList) {
		this.serverConfigList = serverConfigList;
	}

	/**
	 * @return the connectTimeout
	 */
	public int getConnectTimeout() {
		return connectTimeout;
	}

	/**
	 * @param connectTimeout the connectTimeout to set
	 */
	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	/**
	 * @return the serverTimeout
	 */
	public int getServerTimeout() {
		return serverTimeout;
	}

	/**
	 * @param serverTimeout the serverTimeout to set
	 */
	public void setServerTimeout(int serverTimeout) {
		this.serverTimeout = serverTimeout;
	}

	/**
	 * @return the expireTime
	 */
	public int getExpireTime() {
		return expireTime;
	}

	/**
	 * @param expireTime the expireTime to set
	 */
	public void setExpireTime(int expireTime) {
		this.expireTime = expireTime;
	}

	/**
	 * @return the clientPoolSize
	 */
	public int getClientPoolSize() {
		return clientPoolSize;
	}

	/**
	 * @param clientPoolSize the clientPoolSize to set
	 */
	public void setClientPoolSize(int clientPoolSize) {
		this.clientPoolSize = clientPoolSize;
	}

	/**
	 * @return the maximumClient
	 */
	public int getMaximumClient() {
		return maximumClient;
	}

	/**
	 * @param maximumClient the maximumClient to set
	 */
	public void setMaximumClient(int maximumClient) {
		this.maximumClient = maximumClient;
	}

	private boolean serverExists(String serverAddress, int serverPort) {
		for (CacheServer serverConfig : this.serverConfigList) {
			if (serverConfig.equals(serverAddress, serverPort)) {
				return true;
			}
		}
		return false;
	}
}
