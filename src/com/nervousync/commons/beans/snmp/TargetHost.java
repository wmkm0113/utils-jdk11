/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervous Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervous Studio.
 */
package com.nervousync.commons.beans.snmp;

import java.io.Serializable;

import com.nervousync.enumeration.snmp.SNMPVersion;
import com.nervousync.enumeration.snmp.auth.SNMPAuthProtocol;
import com.nervousync.enumeration.snmp.auth.SNMPAuthType;
import com.nervousync.enumeration.snmp.auth.SNMPPrivProtocol;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Oct 25, 2017 9:47:36 PM $
 */
public class TargetHost implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7043141633658888918L;
	
	private static transient final int DEFAULT_SNMP_PORT = 161;

	private String ipAddress = null;
	private String community = null;
	private SNMPAuthType auth = SNMPAuthType.NOAUTH_NOPRIV;
	private SNMPAuthProtocol authProtocol = null;
	private String authPassword = null;
	private SNMPPrivProtocol privProtocol = null;
	private String privPassword = null;
	private SNMPVersion version = SNMPVersion.VERSION2C;
	private int port = DEFAULT_SNMP_PORT;
	private int retries = 1;
	private long timeOut = 1000L;
	
	public TargetHost(String ipAddress, String community) {
		this.ipAddress = ipAddress;
		this.community = community;
	}

	public TargetHost(String ipAddress, String community, int port) {
		this(ipAddress, community);
		this.port = port;
	}

	public TargetHost(String ipAddress, String community, 
			int port, int retries, long timeOut) {
		this(ipAddress, community, port);
		this.retries = retries;
		this.timeOut = timeOut;
	}
	
	/**
	 * @return the ipAddress
	 */
	public String getIpAddress() {
		return ipAddress;
	}

	/**
	 * @param ipAddress the ipAddress to set
	 */
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	/**
	 * @return the community
	 */
	public String getCommunity() {
		return community;
	}

	/**
	 * @param community the community to set
	 */
	public void setCommunity(String community) {
		this.community = community;
	}

	/**
	 * @return the auth
	 */
	public SNMPAuthType getAuth() {
		return auth;
	}

	/**
	 * @return the authProtocol
	 */
	public SNMPAuthProtocol getAuthProtocol() {
		return authProtocol;
	}

	/**
	 * @return the authPassword
	 */
	public String getAuthPassword() {
		return authPassword;
	}

	/**
	 * @return the privProtocol
	 */
	public SNMPPrivProtocol getPrivProtocol() {
		return privProtocol;
	}

	/**
	 * @return the privPassword
	 */
	public String getPrivPassword() {
		return privPassword;
	}

	/**
	 * @return the version
	 */
	public SNMPVersion getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(SNMPVersion version) {
		this.version = version;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @return the retries
	 */
	public int getRetries() {
		return retries;
	}

	/**
	 * @param retries the retries to set
	 */
	public void setRetries(int retries) {
		this.retries = retries;
	}

	/**
	 * @return the timeOut
	 */
	public long getTimeOut() {
		return timeOut;
	}

	/**
	 * @param timeOut the timeOut to set
	 */
	public void setTimeOut(long timeOut) {
		this.timeOut = timeOut;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	public void authNoPriv(SNMPAuthProtocol authProtocol, String authPassword) {
		this.auth = SNMPAuthType.AUTH_NOPRIV;
		this.authProtocol = authProtocol;
		this.authPassword = authPassword;
	}

	public void authWithPriv(SNMPAuthProtocol authProtocol, String authPassword, 
			SNMPPrivProtocol privProtocol, String privPassword) {
		this.auth = SNMPAuthType.AUTH_PRIV;
		this.authProtocol = authProtocol;
		this.authPassword = authPassword;
		this.privProtocol = privProtocol;
		this.privPassword = privPassword;
	}

	/**
	 * @return the defaultSnmpPort
	 */
	public static int getDefaultSnmpPort() {
		return DEFAULT_SNMP_PORT;
	}
}
