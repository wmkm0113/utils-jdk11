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
package com.nervousync.commons.http.proxy;

import java.net.Proxy.Type;

import com.nervousync.commons.core.Globals;

/**
 * Proxy server setting
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Jan 4, 2018 4:05:54 PM $
 */
public final class ProxyInfo {

	/**
	 * Proxy type
	 * @see java.net.Proxy.Type
	 */
	private final Type proxyType;
	/**
	 * Proxy server address
	 */
	private final String proxyAddress;
	/**
	 * Proxy server port
	 */
	private final int proxyPort;
	/**
	 * Proxy server user name
	 */
	private final String userName;
	/**
	 * Proxy server password
	 */
	private final String password;
	
	/**
	 * Default constructor
	 * @param proxyType			proxy type
	 * @param proxyAddress		proxy server address
	 * @param proxyPort			proxy port number
	 * @param userName			proxy user name
	 * @param password			proxy password
	 */
	private ProxyInfo(Type proxyType, String proxyAddress, 
			int proxyPort, String userName, String password) {
		this.proxyType = proxyType;
		this.proxyAddress = proxyAddress;
		this.proxyPort = proxyPort;
		this.userName = userName;
		this.password = password;
	}
	
	/**
	 * Generate ProxyInfo
	 * @param proxyType			Proxy type
	 * @param proxyAddress		Proxy server address
	 * @return					Proxy info instance
	 */
	public static ProxyInfo newInstance(Type proxyType, String proxyAddress) {
		int proxyPort;
		switch (proxyType) {
		case HTTP:
			proxyPort = 80;
			break;
		case SOCKS:
			proxyPort = 1080;
			break;
			default:
				proxyPort = Globals.DEFAULT_VALUE_INT;
				break;
		}
		return new ProxyInfo(proxyType, proxyAddress, proxyPort, null, null);
	}

	/**
	 * Generate ProxyInfo
	 * @param proxyType			Proxy type
	 * @param proxyAddress		Proxy server address
	 * @param proxyPort			Proxy server port
	 * @return					Proxy info instance
	 */
	public static ProxyInfo newInstance(Type proxyType, String proxyAddress, int proxyPort) {
		return new ProxyInfo(proxyType, proxyAddress, proxyPort, null, null);
	}

	/**
	 * Generate ProxyInfo
	 * @param proxyType			Proxy type
	 * @param proxyAddress		Proxy server address
	 * @param proxyPort			Proxy server port
	 * @param userName			Proxy server user name
	 * @param password			Proxy server password
	 * @return					Proxy info instance
	 */
	public static ProxyInfo newInstance(Type proxyType, String proxyAddress, 
			int proxyPort, String userName, String password) {
		return new ProxyInfo(proxyType, proxyAddress, proxyPort, userName, password);
	}

	/**
	 * @return the proxyType
	 */
	public Type getProxyType() {
		return proxyType;
	}

	/**
	 * @return the proxyAddress
	 */
	public String getProxyAddress() {
		return proxyAddress;
	}

	/**
	 * @return the proxyPort
	 */
	public int getProxyPort() {
		return proxyPort;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
}
