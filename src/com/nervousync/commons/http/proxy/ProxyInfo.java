/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervous Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervous Studio.
 */
package com.nervousync.commons.http.proxy;

import java.net.Proxy.Type;

import com.nervousync.commons.core.Globals;;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Jan 4, 2018 4:05:54 PM $
 */
public final class ProxyInfo {

	private Type proxyType = null;
	private String proxyAddress = null;
	private int proxyPort = Globals.DEFAULT_VALUE_INT;
	private String userName = null;
	private String password = null;
	
	private ProxyInfo(Type proxyType, String proxyAddress, 
			int proxyPort, String userName, String password) {
		this.proxyType = proxyType;
		this.proxyAddress = proxyAddress;
		this.proxyPort = proxyPort;
		this.userName = userName;
		this.password = password;
	}
	
	public static ProxyInfo newInstance(Type proxyType, String proxyAddress) {
		int proxyPort = Globals.DEFAULT_VALUE_INT;
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

	public static ProxyInfo newInstance(Type proxyType, String proxyAddress, int proxyPort) {
		return new ProxyInfo(proxyType, proxyAddress, proxyPort, null, null);
	}

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
