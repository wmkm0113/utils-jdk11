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
package com.nervousync.utils;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import com.nervousync.commons.core.Globals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nervousync.commons.beans.network.NetworkInfo;
import com.nervousync.exceptions.beans.network.NetworkInfoException;

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
	 * Constant identifying the 1.3.x JVM (JDK 1.3).
	 */
	public static final int JAVA_13 = 0;

	/**
	 * Constant identifying the 1.4.x JVM (J2SE 1.4).
	 */
	public static final int JAVA_14 = 1;

	/**
	 * Constant identifying the 1.5 JVM (Java 5).
	 */
	public static final int JAVA_15 = 2;

	/**
	 * Constant identifying the 1.6 JVM (Java 6).
	 */
	public static final int JAVA_16 = 3;

	/**
	 * Constant identifying the 1.7 JVM (Java 7).
	 */
	public static final int JAVA_17 = 4;

	/**
	 * Constant identifying the 1.8 JVM (Java 8).
	 */
	public static final int JAVA_18 = 5;

	/**
	 * Constant identifying the 9 JVM (Java 9).
	 */
	public static final int JAVA_9 = 6;

	/**
	 * Constant identifying the 10 JVM (Java 10).
	 */
	public static final int JAVA_10 = 7;

	private static final int MAJOR_VERSION;

	static {
		// version String should look like "1.4.2_10"
		if (JAVA_VERSION.startsWith("10.")) {
			MAJOR_VERSION = JAVA_10;
		} else if (JAVA_VERSION.startsWith("9.")) {
			MAJOR_VERSION = JAVA_9;
		} else if (JAVA_VERSION.startsWith("1.8.")) {
			MAJOR_VERSION = JAVA_18;
		} else if (JAVA_VERSION.startsWith("1.7.")) {
			MAJOR_VERSION = JAVA_17;
		} else if (JAVA_VERSION.startsWith("1.6.")) {
			MAJOR_VERSION = JAVA_16;
		} else if (JAVA_VERSION.startsWith("1.5.")) {
			MAJOR_VERSION = JAVA_15;
		} else {
			// else leave 1.4 as default (it's either 1.4 or unknown)
			MAJOR_VERSION = JAVA_14;
		}
	}

	private SystemUtils() {

	}

	public static String identifiedKey() {
		return IDENTIFIED_KEY;
	}

	public static boolean isWindows() {
		return OPERATE_SYSTEM_NAME.toLowerCase().indexOf("windows") > 0;
	}

	public static boolean isUnix() {
		return OPERATE_SYSTEM_NAME.toLowerCase().indexOf("unix") > 0;
	}

	public static boolean isLinux() {
		return OPERATE_SYSTEM_NAME.toLowerCase().indexOf("linux") > 0;
	}

	public static boolean isMacOS() {
		return OPERATE_SYSTEM_NAME.toLowerCase().indexOf("mac os") > 0;
	}

	public static boolean isMacOSX() {
		return OPERATE_SYSTEM_NAME.toLowerCase().indexOf("mac os x") > 0;
	}

	public static boolean isOS2() {
		return OPERATE_SYSTEM_NAME.toLowerCase().indexOf("os/2") > 0;
	}

	public static boolean isSolaris() {
		return OPERATE_SYSTEM_NAME.toLowerCase().indexOf("solaris") > 0;
	}

	public static boolean isSunOS() {
		return OPERATE_SYSTEM_NAME.toLowerCase().indexOf("sunos") > 0;
	}

	public static boolean isMPEiX() {
		return OPERATE_SYSTEM_NAME.toLowerCase().indexOf("mpe/ix") > 0;
	}

	public static boolean isHPUX() {
		return OPERATE_SYSTEM_NAME.toLowerCase().indexOf("hp-ux") > 0;
	}

	public static boolean isAIX() {
		return OPERATE_SYSTEM_NAME.toLowerCase().indexOf("aix") > 0;
	}

	public static boolean isOS390() {
		return OPERATE_SYSTEM_NAME.toLowerCase().indexOf("os/390") > 0;
	}

	public static boolean isFreeBSD() {
		return OPERATE_SYSTEM_NAME.toLowerCase().indexOf("freebsd") > 0;
	}

	public static boolean isIrix() {
		return OPERATE_SYSTEM_NAME.toLowerCase().indexOf("irix") > 0;
	}

	public static boolean isDigitalUnix() {
		return OPERATE_SYSTEM_NAME.toLowerCase().indexOf("digital unix") > 0;
	}

	public static boolean isNetware() {
		return OPERATE_SYSTEM_NAME.toLowerCase().indexOf("netware") > 0;
	}

	public static boolean isOSF1() {
		return OPERATE_SYSTEM_NAME.toLowerCase().indexOf("osf1") > 0;
	}

	public static boolean isOpenVMS() {
		return OPERATE_SYSTEM_NAME.toLowerCase().indexOf("openvms") > 0;
	}

	/**
	 * Convenience method to determine if the current JVM is at least Java 1.4.
	 * @return <code>true</code> if the current JVM is at least Java 1.4
	 * @see #JAVA_14
	 * @see #JAVA_15
	 * @see #JAVA_16
	 * @see #JAVA_17
	 * @see #JAVA_18
	 * @see #JAVA_9
	 * @see #JAVA_10
	 */
	public static boolean isAtLeastJava14() {
		return MAJOR_VERSION >= JAVA_14;
	}

	/**
	 * Convenience method to determine if the current JVM is at least
	 * Java 1.5 (Java 5).
	 * @return <code>true</code> if the current JVM is at least Java 1.5
	 * @see #JAVA_15
	 * @see #JAVA_16
	 * @see #JAVA_17
	 * @see #JAVA_18
	 * @see #JAVA_9
	 * @see #JAVA_10
	 */
	public static boolean isAtLeastJava15() {
		return MAJOR_VERSION >= JAVA_15;
	}

	/**
	 * Convenience method to determine if the current JVM is at least
	 * Java 1.6 (Java 6).
	 * @return <code>true</code> if the current JVM is at least Java 1.6
	 * @see #JAVA_16
	 * @see #JAVA_17
	 * @see #JAVA_18
	 * @see #JAVA_9
	 * @see #JAVA_10
	 */
	public static boolean isAtLeastJava16() {
		return MAJOR_VERSION >= JAVA_16;
	}

	/**
	 * Convenience method to determine if the current JVM is at least
	 * Java 1.7 (Java 7).
	 * @return <code>true</code> if the current JVM is at least Java 1.7
	 * @see #JAVA_17
	 * @see #JAVA_18
	 * @see #JAVA_9
	 * @see #JAVA_10
	 */
	public static boolean isAtLeastJava17() {
		return MAJOR_VERSION >= JAVA_17;
	}

	/**
	 * Convenience method to determine if the current JVM is at least
	 * Java 1.8 (Java 8).
	 * @return <code>true</code> if the current JVM is at least Java 1.8
	 * @see #JAVA_18
	 * @see #JAVA_9
	 * @see #JAVA_10
	 */
	public static boolean isAtLeastJava18() {
		return MAJOR_VERSION >= JAVA_18;
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
	 * System certificate file path
	 * @return      System certificate file path
	 */
	public static String systemCertPath() {
		return JAVA_HOME + JAVA_CERT_PATH;
	}

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
			return SecurityUtils.SHA256(macAddressList);
		} catch (Exception e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Generate identified key error!", e);
			}
		}
		
		return null;
	}
	
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
