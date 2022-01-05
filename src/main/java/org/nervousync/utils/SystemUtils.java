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
package org.nervousync.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;

import org.nervousync.commons.core.Globals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.nervousync.beans.network.NetworkInfo;
import org.nervousync.exceptions.beans.network.NetworkInfoException;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Jul 24, 2015 11:43:24 AM $
 */
public final class SystemUtils {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SystemUtils.class);
	
	private static final String JAVA_CERT_PATH = Globals.DEFAULT_PAGE_SEPARATOR + "lib"
			+ Globals.DEFAULT_PAGE_SEPARATOR + "security" + Globals.DEFAULT_PAGE_SEPARATOR + "cacerts";
	
	public static final String OPERATE_SYSTEM_NAME = System.getProperty("os.name");
	public static final String OPERATE_SYSTEM_VERSION = System.getProperty("os.version");
	public static final String JAVA_HOME = System.getProperty("java.home");
	public static final String JAVA_VERSION = System.getProperty("java.version");
	public static final String JAVA_TMP_DIR = System.getProperty("java.io.tmpdir");
	public static final String USER_NAME = System.getProperty("user.name");
	public static final String USER_HOME = System.getProperty("user.home");
	public static final String USER_DIR = System.getProperty("user.dir");
	
	private static final String IDENTIFIED_KEY = SystemUtils.generateIdentifiedKey();

	/**
	 * Constant identifying the 1.4.x JVM (J2SE 1.4).
	 */
	public static final int JAVA_1_4 = 1;

	/**
	 * Constant identifying the 1.5 JVM (Java 5).
	 */
	public static final int JAVA_1_5 = 2;

	/**
	 * Constant identifying the 1.6 JVM (Java 6).
	 */
	public static final int JAVA_1_6 = 3;

	/**
	 * Constant identifying the 1.7 JVM (Java 7).
	 */
	public static final int JAVA_1_7 = 4;

	/**
	 * Constant identifying the 1.8 JVM (Java 8).
	 */
	public static final int JAVA_1_8 = 5;

	/**
	 * Constant identifying the 9 JVM (Java 9).
	 */
	public static final int JAVA_9 = 6;

	/**
	 * Constant identifying the 10 JVM (Java 10).
	 */
	public static final int JAVA_10 = 7;

	/**
	 * Constant identifying the 11 JVM (Java 11).
	 */
	public static final int JAVA_11 = 8;

	/**
	 * Constant identifying the 12 JVM (Java 12).
	 */
	public static final int JAVA_12 = 9;

	/**
	 * Constant identifying the 13 JVM (Java 13).
	 */
	public static final int JAVA_13 = 10;

	/**
	 * Constant identifying the 14 JVM (Java 14).
	 */
	public static final int JAVA_14 = 11;

	private static final int MAJOR_VERSION;

	static {
		// version String should look like "1.4.2_10"
		if (JAVA_VERSION.startsWith("14.")) {
			MAJOR_VERSION = JAVA_14;
		} else if (JAVA_VERSION.startsWith("13.")) {
			MAJOR_VERSION = JAVA_13;
		} else if (JAVA_VERSION.startsWith("12.")) {
			MAJOR_VERSION = JAVA_12;
		} else if (JAVA_VERSION.startsWith("11.")) {
			MAJOR_VERSION = JAVA_11;
		} else if (JAVA_VERSION.startsWith("10.")) {
			MAJOR_VERSION = JAVA_10;
		} else if (JAVA_VERSION.startsWith("9.")) {
			MAJOR_VERSION = JAVA_9;
		} else if (JAVA_VERSION.startsWith("1.8.")) {
			MAJOR_VERSION = JAVA_1_8;
		} else if (JAVA_VERSION.startsWith("1.7.")) {
			MAJOR_VERSION = JAVA_1_7;
		} else if (JAVA_VERSION.startsWith("1.6.")) {
			MAJOR_VERSION = JAVA_1_6;
		} else if (JAVA_VERSION.startsWith("1.5.")) {
			MAJOR_VERSION = JAVA_1_5;
		} else {
			// else leave 1.4 as default (it's either 1.4 or unknown)
			MAJOR_VERSION = JAVA_1_4;
		}
	}

	private SystemUtils() {
	}

	/**
	 * Identify key of current machine
	 * @return  Identify key
	 */
	public static String identifiedKey() {
		return IDENTIFIED_KEY;
	}

	/**
	 * Current operate system is Microsoft Windows
	 * @return  check result
	 */
	public static boolean isWindows() {
		return OPERATE_SYSTEM_NAME.toLowerCase().contains("windows");
	}

	/**
	 * Current operate system is Unix
	 * @return  check result
	 */
	public static boolean isUnix() {
		return OPERATE_SYSTEM_NAME.toLowerCase().indexOf("unix") > 0;
	}

	/**
	 * Current operate system is Linux
	 * @return  check result
	 */
	public static boolean isLinux() {
		return OPERATE_SYSTEM_NAME.toLowerCase().indexOf("linux") > 0;
	}

	/**
	 * Current operate system is Apple MacOS
	 * @return  check result
	 */
	public static boolean isMacOS() {
		return OPERATE_SYSTEM_NAME.toLowerCase().indexOf("mac os") > 0;
	}

	/**
	 * Current operate system is Apple Mac OS X
	 * @return  check result
	 */
	public static boolean isMacOSX() {
		return OPERATE_SYSTEM_NAME.toLowerCase().indexOf("mac os x") > 0;
	}

	/**
	 * Current operate system is OS2
	 * @return  check result
	 */
	public static boolean isOS2() {
		return OPERATE_SYSTEM_NAME.toLowerCase().indexOf("os/2") > 0;
	}

	/**
	 * Current operate system is Solaris
	 * @return  check result
	 */
	public static boolean isSolaris() {
		return OPERATE_SYSTEM_NAME.toLowerCase().indexOf("solaris") > 0;
	}

	/**
	 * Current operate system is Sun OS
	 * @return  check result
	 */
	public static boolean isSunOS() {
		return OPERATE_SYSTEM_NAME.toLowerCase().indexOf("sunos") > 0;
	}

	/**
	 * Current operate system is MPEiX
	 * @return  check result
	 */
	public static boolean isMPEiX() {
		return OPERATE_SYSTEM_NAME.toLowerCase().indexOf("mpe/ix") > 0;
	}

	/**
	 * Current operate system is HPUX
	 * @return  check result
	 */
	public static boolean isHPUX() {
		return OPERATE_SYSTEM_NAME.toLowerCase().indexOf("hp-ux") > 0;
	}

	/**
	 * Current operate system is AIX
	 * @return  check result
	 */
	public static boolean isAIX() {
		return OPERATE_SYSTEM_NAME.toLowerCase().indexOf("aix") > 0;
	}

	/**
	 * Current operate system is OS390
	 * @return  check result
	 */
	public static boolean isOS390() {
		return OPERATE_SYSTEM_NAME.toLowerCase().indexOf("os/390") > 0;
	}

	/**
	 * Current operate system is Free BSD
	 * @return  check result
	 */
	public static boolean isFreeBSD() {
		return OPERATE_SYSTEM_NAME.toLowerCase().indexOf("freebsd") > 0;
	}

	/**
	 * Current operate system is Irix
	 * @return  check result
	 */
	public static boolean isIrix() {
		return OPERATE_SYSTEM_NAME.toLowerCase().indexOf("irix") > 0;
	}

	/**
	 * Current operate system is Digital Unix
	 * @return  check result
	 */
	public static boolean isDigitalUnix() {
		return OPERATE_SYSTEM_NAME.toLowerCase().indexOf("digital unix") > 0;
	}

	/**
	 * Current operate system is Netware
	 * @return  check result
	 */
	public static boolean isNetware() {
		return OPERATE_SYSTEM_NAME.toLowerCase().indexOf("netware") > 0;
	}

	/**
	 * Current operate system is OSF1
	 * @return  check result
	 */
	public static boolean isOSF1() {
		return OPERATE_SYSTEM_NAME.toLowerCase().indexOf("osf1") > 0;
	}

	/**
	 * Current operate system is OpenVMS
	 * @return  check result
	 */
	public static boolean isOpenVMS() {
		return OPERATE_SYSTEM_NAME.toLowerCase().indexOf("openvms") > 0;
	}

	/**
	 * Convenience method to determine if the current JVM is at least Java 1.4.
	 * @return <code>true</code> if the current JVM is at least Java 1.4
	 * @see #JAVA_1_4
	 */
	public static boolean isAtLeastJava1_4() {
		return MAJOR_VERSION >= JAVA_14;
	}

	/**
	 * Convenience method to determine if the current JVM is at least
	 * Java 1.5 (Java 5).
	 * @return <code>true</code> if the current JVM is at least Java 1.5
	 */
	public static boolean isAtLeastJava1_5() {
		return MAJOR_VERSION >= JAVA_1_5;
	}

	/**
	 * Convenience method to determine if the current JVM is at least
	 * Java 1.6 (Java 6).
	 * @return <code>true</code> if the current JVM is at least Java 1.6
	 */
	public static boolean isAtLeastJava1_6() {
		return MAJOR_VERSION >= JAVA_1_6;
	}

	/**
	 * Convenience method to determine if the current JVM is at least
	 * Java 1.7 (Java 7).
	 * @return <code>true</code> if the current JVM is at least Java 1.7
	 */
	public static boolean isAtLeastJava1_7() {
		return MAJOR_VERSION >= JAVA_1_7;
	}

	/**
	 * Convenience method to determine if the current JVM is at least
	 * Java 1.8 (Java 8).
	 * @return <code>true</code> if the current JVM is at least Java 1.8
	 */
	public static boolean isAtLeastJava1_8() {
		return MAJOR_VERSION >= JAVA_1_8;
	}

	/**
	 * Convenience method to determine if the current JVM is at least
	 * Java 9.
	 * @return <code>true</code> if the current JVM is at least Java 9
	 * @see #JAVA_9
	 * @see #JAVA_10
	 */
	public static boolean isAtLeastJava9() {
		return MAJOR_VERSION >= JAVA_9;
	}

	/**
	 * Convenience method to determine if the current JVM is at least
	 * Java 10.
	 * @return <code>true</code> if the current JVM is at least Java 10
	 * @see #JAVA_10
	 */
	public static boolean isAtLeastJava10() {
		return MAJOR_VERSION >= JAVA_10;
	}

	/**
	 * Convenience method to determine if the current JVM is at least
	 * Java 11.
	 * @return <code>true</code> if the current JVM is at least Java 11
	 * @see #JAVA_11
	 */
	public static boolean isAtLeastJava11() {
		return MAJOR_VERSION >= JAVA_11;
	}

	/**
	 * Convenience method to determine if the current JVM is at least
	 * Java 12.
	 * @return <code>true</code> if the current JVM is at least Java 12
	 * @see #JAVA_12
	 */
	public static boolean isAtLeastJava12() {
		return MAJOR_VERSION >= JAVA_12;
	}

	/**
	 * Convenience method to determine if the current JVM is at least
	 * Java 13.
	 * @return <code>true</code> if the current JVM is at least Java 13
	 * @see #JAVA_13
	 */
	public static boolean isAtLeastJava13() {
		return MAJOR_VERSION >= JAVA_13;
	}

	/**
	 * Convenience method to determine if the current JVM is at least
	 * Java 14.
	 * @return <code>true</code> if the current JVM is at least Java 14
	 * @see #JAVA_14
	 */
	public static boolean isAtLeastJava14() {
		return MAJOR_VERSION >= JAVA_14;
	}

	/**
	 * System certificate file path
	 * @return      System certificate file path
	 */
	public static String systemCertPath() {
		return JAVA_HOME + JAVA_CERT_PATH;
	}

	public static byte[] localMac() {
		byte[] macAddress = null;
		try {
			final InetAddress localHost = InetAddress.getLocalHost();
			try {
				final NetworkInterface localInterface = NetworkInterface.getByInetAddress(localHost);
				if (localInterface != null && !localInterface.isLoopback() && localInterface.isUp()) {
					macAddress = localInterface.getHardwareAddress();
				}
				if (macAddress == null) {
					final Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
					while (networkInterfaces.hasMoreElements() && macAddress == null) {
						final NetworkInterface networkInterface = networkInterfaces.nextElement();
						if (networkInterface != null && !networkInterface.isLoopback() && networkInterface.isUp()) {
							macAddress = networkInterface.getHardwareAddress();
						}
					}
				}
			} catch (final SocketException e) {
				LOGGER.error("Retrieve local MAC address error! ", e);
			}
			if (macAddress == null || macAddress.length == 0) {
				// Take only 6 bytes if the address is an IPv6 otherwise will pad with two zero bytes
				macAddress = Arrays.copyOf(localHost.getAddress(), 6);
			}
		} catch (final UnknownHostException ignored) {
			// ignored
		}
		return macAddress;
	}

	/**
	 * Generate current identify key
	 * @return  generated value
	 */
	private static String generateIdentifiedKey() {
		try {
			List<NetworkInfo> networkList = retrieveNetworkList();
			List<String> macAddressList = new ArrayList<>();
			
			for (NetworkInfo networkInfo : networkList) {
				if (!networkInfo.isVirtual() && networkInfo.getMacAddress().length() > 0 
						&& !macAddressList.contains(networkInfo.getMacAddress())) {
					macAddressList.add(networkInfo.getMacAddress());
				}
			}
			
			Collections.sort(macAddressList);
			return ConvertUtils.byteToHex(SecurityUtils.SHA256(macAddressList));
		} catch (Exception e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Generate identified key error!", e);
			}
		}
		
		return null;
	}

	/**
	 * Retrieve local network adapter list
	 * @return      Local network adapter info list
	 * @throws SocketException  Retrieve network interfaces error
	 */
	private static List<NetworkInfo> retrieveNetworkList() throws SocketException {
		List<NetworkInfo> networkList = new ArrayList<>();
		
		Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
		
		while (enumeration.hasMoreElements()) {
			try {
				networkList.add(new NetworkInfo(enumeration.nextElement()));
			} catch (NetworkInfoException e) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Retrieve network info error!", e);
				}
			}
		}
		
		return networkList;
	}
}
