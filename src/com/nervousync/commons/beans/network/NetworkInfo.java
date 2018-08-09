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
package com.nervousync.commons.beans.network;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nervousync.exceptions.beans.network.IPAddressException;
import com.nervousync.exceptions.beans.network.NetworkInfoException;
import com.nervousync.utils.StringUtils;

/**
 * System interface network information
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Jul 24, 2015 11:53:10 AM $
 */
public final class NetworkInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8060054814830700945L;

	private static final String REGEX_IPv4 = "((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))";
	private static final String REGEX_IPv6 = "^\\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))" 
			+ "|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))" 
			+ "|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))" 
			+ "|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))"
			+ "|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))" 
			+ "|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))" 
			+ "|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))" 
			+ "|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:)))(%.+)?\\s*$";
	
	/**
	 * Is virtual adapter
	 */
	private boolean virtual;
	/**
	 * Interface display name in system
	 */
	private String displayName;
	/**
	 * Interface adapter physical address
	 */
	private String macAddress = "";
	/**
	 * IP address list of interface configured
	 */
	private final List<IPAddrInfo> ipAddrInfos = new ArrayList<>();
	
	/**
	 * Constructor for NetworkInfo
	 * @param networkInterface			NetworkInterface value
	 * @throws NetworkInfoException		If value of NetworkInterface is null or catch other SocketException
	 */
	public NetworkInfo(NetworkInterface networkInterface) throws NetworkInfoException {
		if (networkInterface == null) {
			throw new NetworkInfoException("NetworkInterface is null");
		}
		Logger logger = LoggerFactory.getLogger(this.getClass());
		try {
			if (networkInterface.isUp() && !networkInterface.isVirtual()) {
				byte[] macAddr = networkInterface.getHardwareAddress();
				if (macAddr != null && macAddr.length > 0) {
					StringBuilder stringBuilder = new StringBuilder();
					for (byte mac : macAddr) {
						stringBuilder.append(":");
						String addr = Integer.toHexString(mac & 0xFF);
						if (addr.length() == 1) {
							addr = "0" + addr;
						}
						stringBuilder.append(addr.toUpperCase());
					}
					this.macAddress = stringBuilder.substring(1);
				}
			}
		} catch (SocketException e) {
			if (logger.isDebugEnabled()) {
				logger.debug("Retrieve network info error! ", e);
			}
			throw new NetworkInfoException("Retrieve network info error! ", e);
		}

		this.virtual = networkInterface.isVirtual();
		
		this.displayName = networkInterface.getDisplayName();
		Enumeration<InetAddress> enumeration = networkInterface.getInetAddresses();
		
		while (enumeration.hasMoreElements()) {
			try {
				IPAddrInfo ipAddrInfo = new IPAddrInfo(enumeration.nextElement());
				this.ipAddrInfos.add(ipAddrInfo);
			} catch (IPAddressException e) {
				if (logger.isDebugEnabled()) {
					logger.debug("Read IP Address Info Error! ", e);
				}
			}
		}
	}
	
	/**
	 * @return the virtual
	 */
	public boolean isVirtual() {
		return virtual;
	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @return the macAddress
	 */
	public String getMacAddress() {
		return macAddress;
	}

	/**
	 * @return the ipAddrInfos
	 */
	public List<IPAddrInfo> getIpAddrInfos() {
		return ipAddrInfos;
	}

	/**
	 * @return the IPv4 address list
	 */
	public List<IPAddrInfo> getIPv4AddrInfos() {
		List<IPAddrInfo> addrInfos = new ArrayList<>();
		for (IPAddrInfo ipAddrInfo : this.ipAddrInfos) {
			if (NetworkInfo.isIPv4Address(ipAddrInfo.getIpAddress())) {
				addrInfos.add(ipAddrInfo);
			}
		}
		return addrInfos;
	}

	/**
	 * @return the IPv6 address list
	 */
	public List<IPAddrInfo> getIPv6AddrInfos() {
		List<IPAddrInfo> addrInfos = new ArrayList<>();
		for (IPAddrInfo ipAddrInfo : this.ipAddrInfos) {
			if (NetworkInfo.isIPv6Address(ipAddrInfo.getIpAddress())) {
				addrInfos.add(ipAddrInfo);
			}
		}
		return addrInfos;
	}

	/**
	 * @return the serialversionUID
	 */
	public static long getSerialversionUID() {
		return serialVersionUID;
	}

	/**
	 * Configured ip address information
	 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
	 * @version $Revision: 1.0 $ $Date: Jul 2, 2018 $
	 */
	public static final class IPAddrInfo implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = -2882813548945783456L;
		
		/**
		 * IP address, supported IPv4 and IPv6
		 */
		private String ipAddress;
		/**
		 * Is site local address
		 */
		private boolean local;
		/**
		 * Is loop back address
		 */
		private boolean loop;
		/**
		 * Is link local status
		 */
		private boolean linkLocal;
		
		/**
		 * Constructor
		 * @param inetAddress				InetAddress object read from interface
		 * @throws IPAddressException		Given inetAddress is null
		 */
		public IPAddrInfo(InetAddress inetAddress) throws IPAddressException {
			if (inetAddress == null) {
				throw new IPAddressException("InetAddress is null");
			}
			this.ipAddress = inetAddress.getHostAddress();
			this.local = inetAddress.isSiteLocalAddress();
			this.loop = inetAddress.isLoopbackAddress();
			this.linkLocal = inetAddress.isLinkLocalAddress();
		}

		/**
		 * @return the serialversionuid
		 */
		public static long getSerialversionuid() {
			return serialVersionUID;
		}

		/**
		 * @return the ipAddress
		 */
		public String getIpAddress() {
			return ipAddress;
		}

		/**
		 * @return the local
		 */
		public boolean isLocal() {
			return local;
		}

		/**
		 * @return the loop
		 */
		public boolean isLoop() {
			return loop;
		}

		/**
		 * @return the linkLocal
		 */
		public boolean isLinkLocal() {
			return linkLocal;
		}
	}
	
	private static boolean isIPv4Address(String ipAddress) {
		return StringUtils.matches(ipAddress, REGEX_IPv4);
	}
	
	private static boolean isIPv6Address(String ipAddress) {
		return StringUtils.matches(ipAddress, REGEX_IPv6);
	}
}
