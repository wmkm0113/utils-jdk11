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
public final class TargetHost implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7043141633658888918L;
	
	private static final int DEFAULT_SNMP_PORT = 161;

	/**
	 * Host ip address
	 */
	private final String ipAddress;
	/**
	 * Community name
	 */
	private final String community;
	/**
	 * SNMP port, default is 161
	 */
	private final int port;
	/**
	 * Retries setting
	 */
	private final int retries;
	/**
	 * Timeout setting
	 */
	private final long timeOut;
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
	 * Constructor for TargetHost
	 * @param ipAddress		Target ip address of host
	 * @param community		Community name
	 * @param port			Port of host
	 * @param retries		Retries setting
	 * @param timeOut		timeout setting
	 */
	private TargetHost(final String ipAddress, final String community,
	                   final int port, final int retries, final long timeOut) {
		this.ipAddress = ipAddress;
		this.community = community;
		this.port = port <= 0 ? DEFAULT_SNMP_PORT : port;
		this.retries = retries <= 0 ? 1 : retries;
		this.timeOut = timeOut <= 0L ? 1000L : timeOut;
	}

	public static TargetHost local() {
		return local("public", DEFAULT_SNMP_PORT, 1, 1000L);
	}

	public static TargetHost local(final String community) {
		return local(community, DEFAULT_SNMP_PORT, 1, 1000L);
	}

	public static TargetHost local(final String community, final int port) {
		return local(community, port, 1, 1000L);
	}

	public static TargetHost local(final String community, final int port, final int retries, final long timeOut) {
		return new TargetHost("127.0.0.1", community, port, retries, timeOut);
	}

	/**
	 * @param ipAddress		Target ip address
	 *
	 * @return  TargetHost instance
	 */
	public static TargetHost remote(final String ipAddress) {
		return remote(ipAddress, "public");
	}

	/**
	 * @param ipAddress		Target ip address
	 * @param community		Community name
	 *
	 * @return  TargetHost instance
	 */
	public static TargetHost remote(final String ipAddress, final String community) {
		return remote(ipAddress, community, DEFAULT_SNMP_PORT, 1, 1000L);
	}

	/**
	 * @param ipAddress		Target ip address of host
	 * @param community		Community name
	 * @param port			Port of host
	 *
	 * @return  TargetHost instance
	 */
	public static TargetHost remote(final String ipAddress, final String community, final int port) {
		return remote(ipAddress, community, port, 1, 1000L);
	}

	/**
	 * @param ipAddress		Target ip address of host
	 * @param community		Community name
	 * @param port			Port of host
	 * @param retries		Retries setting
	 * @param timeOut		timeout setting
	 *
	 * @return  TargetHost instance
	 */
	public static TargetHost remote(final String ipAddress, final String community,
	                                     final int port, final int retries, final long timeOut) {
		return new TargetHost(ipAddress, community, port, retries, timeOut);
	}

	/**
	 * @return the ipAddress
	 */
	public String getIpAddress() {
		return ipAddress;
	}

	/**
	 * @return the community
	 */
	public String getCommunity() {
		return community;
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
	public void setVersion(final SNMPVersion version) {
		this.version = version;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @return the retries
	 */
	public int getRetries() {
		return retries;
	}

	/**
	 * @return the timeOut
	 */
	public long getTimeOut() {
		return timeOut;
	}

	/**
	 * Setting for authentication with SNMPAuthType.AUTH_NOPRIV
	 * @param authProtocol		Authentication protocol 
	 * @see SNMPAuthProtocol
	 * @param authPassword		Authentication password
	 */
	public void authNoPriv(final SNMPAuthProtocol authProtocol, final String authPassword) {
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
	public void authWithPriv(final SNMPAuthProtocol authProtocol, final String authPassword,
	                         final SNMPPrivProtocol privProtocol, final String privPassword) {
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
