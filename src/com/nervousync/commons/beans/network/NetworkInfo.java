/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervousync Studio.
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

import com.nervousync.commons.core.Globals;
import com.nervousync.exceptions.beans.network.IPAddressException;
import com.nervousync.exceptions.beans.network.NetworkInfoException;
import com.nervousync.utils.StringUtils;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Jul 24, 2015 11:53:10 AM $
 */
public final class NetworkInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8060054814830700945L;
	
	private transient final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static final String REGEX_IPv4 = "((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))";
	private static final String REGEX_IPv6 = "^\\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))" 
			+ "|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))" 
			+ "|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))" 
			+ "|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))"
			+ "|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))" 
			+ "|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))" 
			+ "|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))" 
			+ "|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:)))(%.+)?\\s*$";
	
	private boolean virtual = Globals.DEFAULT_VALUE_BOOLEAN;
	private String displayName = null;
	private String macAddress = "";
	private List<IPAddrInfo> ipAddrInfos = new ArrayList<IPAddrInfo>();
	
	public NetworkInfo(NetworkInterface networkInterface) throws NetworkInfoException {
		if (networkInterface == null) {
			throw new NetworkInfoException("NetworkInterface is null");
		}
		try {
			if (networkInterface.isUp() && !networkInterface.isVirtual()) {
				byte[] macAddr = networkInterface.getHardwareAddress();
				if (macAddr != null && macAddr.length > 0) {
					for (byte mac : macAddr) {
						String addr = Integer.toHexString(mac & 0xFF);
						if (addr.length() == 1) {
							addr = "0" + addr;
						}
						this.macAddress += (addr.toUpperCase() + ":");
					}
					this.macAddress = this.macAddress.substring(0, this.macAddress.length() - 1);
				}
			}
		} catch (SocketException e) {
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
				if (this.logger.isDebugEnabled()) {
					this.logger.debug("Read IP Address Info Error! ", e);
				}
				continue;
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
		List<IPAddrInfo> addrInfos = new ArrayList<IPAddrInfo>();
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
		List<IPAddrInfo> addrInfos = new ArrayList<IPAddrInfo>();
		for (IPAddrInfo ipAddrInfo : this.ipAddrInfos) {
			if (NetworkInfo.isIPv6Address(ipAddrInfo.getIpAddress())) {
				addrInfos.add(ipAddrInfo);
			}
		}
		return addrInfos;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public static final class IPAddrInfo implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = -2882813548945783456L;
		
		private String ipAddress = null;
		private boolean local = false;
		private boolean loop = false;
		private boolean linkLocal = false;
		
		public IPAddrInfo() {
			
		}
		
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
