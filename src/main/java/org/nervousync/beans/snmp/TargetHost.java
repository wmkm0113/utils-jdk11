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

import org.nervousync.enumerations.snmp.SNMPVersion;
import org.nervousync.enumerations.snmp.auth.SNMPAuthProtocol;
import org.nervousync.enumerations.snmp.auth.SNMPAuthType;
import org.nervousync.enumerations.snmp.auth.SNMPPrivProtocol;

/**
 * SNMP host define
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Oct 25, 2017 9:47:36 PM $
 */
public class TargetHost implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7043141633658888918L;
	
	private static final int DEFAULT_SNMP_PORT = 161;

	/**
	 * Host ip address
	 */
	private String ipAddress;
	/**
	 * Community name
	 */
	private String community;
	/**
	 * SNMP authentication type
	 */
	private SNMPAuthType auth = SNMPAuthType.NOAUTH_NOPRIV;
	/**
	 * SNMP authentication protocol
	 */
	private SNMPAuthProtocol authProtocol = null;
	/**
	 * SNMP authentication password
	 */
	private String authPassword = null;
	/**
	 * SNMP privPassword encrypt type
	 */
	private SNMPPrivProtocol privProtocol = null;
	/**
	 * SNMP privPassword
	 */
	private String privPassword = null;
	/**
	 * SNMP version define
	 */
	private SNMPVersion version = SNMPVersion.VERSION2C;
	/**
	 * SNMP port, default is 161
	 */
	private int port = DEFAULT_SNMP_PORT;
	/**
	 * Retries setting
	 */
	private int retries = 1;
	/**
	 * Timeout setting
	 */
	private long timeOut = 1000L;
	
	/**
	 * Constructor for TargetHost
	 * @param ipAddress		Target ip address
	 * @param community		Community name
	 */
	public TargetHost(String ipAddress, String community) {
		this.ipAddress = ipAddress;
		this.community = community;
	}

	/**
	 * Constructor for TargetHost
	 * @param ipAddress		Target ip address of host
	 * @param community		Community name
	 * @param port			Port of host
	 */
	public TargetHost(String ipAddress, String community, int port) {
		this(ipAddress, community);
		this.port = port;
	}

	/**
	 * Constructor for TargetHost
	 * @param ipAddress		Target ip address of host
	 * @param community		Community name
	 * @param port			Port of host
	 * @param retries		Retries setting
	 * @param timeOut		timeout setting
	 */
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
	 * Setting for authentication with SNMPAuthType.AUTH_NOPRIV
	 * @param authProtocol		Authentication protocol 
	 * @see SNMPAuthProtocol
	 * @param authPassword		Authentication password
	 */
	public void authNoPriv(SNMPAuthProtocol authProtocol, String authPassword) {
		this.auth = SNMPAuthType.AUTH_NOPRIV;
		this.authProtocol = authProtocol;
		this.authPassword = authPassword;
	}

	/**
	 * Setting for authentication with SNMPAuthType.AUTH_PRIV
	 * @param authProtocol		Authentication protocol 
	 * @see SNMPAuthProtocol
	 * @param authPassword		Authentication password
	 * @param privProtocol		PrivProtocol
	 * @see SNMPPrivProtocol
	 * @param privPassword		PrivPassword
	 */
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
