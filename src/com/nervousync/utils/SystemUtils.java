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
	 * Constant identifying the 1.9 JVM (Java 9).
	 */
	public static final int JAVA_19 = 6;

	private static final String JAVA_VBERSION;

	private static final int MAJOR_VERSION;

	static {
		JAVA_VBERSION = System.getProperty("java.version");
		// version String should look like "1.4.2_10"
		if (JAVA_VBERSION.indexOf("1.9.") != -1) {
			MAJOR_VERSION = JAVA_19;
		} else if (JAVA_VBERSION.indexOf("1.8.") != -1) {
			MAJOR_VERSION = JAVA_18;
		} else if (JAVA_VBERSION.indexOf("1.7.") != -1) {
			MAJOR_VERSION = JAVA_17;
		} else if (JAVA_VBERSION.indexOf("1.6.") != -1) {
			MAJOR_VERSION = JAVA_16;
		} else if (JAVA_VBERSION.indexOf("1.5.") != -1) {
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
	 * Return the full Java version string, as returned by
	 * <code>System.getProperty("java.version")</code>.
	 * @return the full Java version string
	 * @see System#getProperty(String)
	 */
	public static String getJavaVbersion() {
		return JAVA_VBERSION;
	}

	/**
	 * Get the major version code. This means we can do things like
	 * <code>if (getMajorJavaVersion() < JAVA_14)</code>.
	 * @return a code comparable to the JAVA_XX codes in this class
	 * @see #JAVA_13
	 * @see #JAVA_14
	 * @see #JAVA_15
	 * @see #JAVA_16
	 * @see #JAVA_17
	 * @see #JAVA_18
	 */
	public static int getMajorVersion() {
		return MAJOR_VERSION;
	}

	/**
	 * Convenience method to determine if the current JVM is at least Java 1.4.
	 * @return <code>true</code> if the current JVM is at least Java 1.4
	 * @see #getMajorVersion()
	 * @see #JAVA_14
	 * @see #JAVA_15
	 * @see #JAVA_16
	 * @see #JAVA_17
	 * @see #JAVA_18
	 */
	public static boolean isAtLeastJava14() {
		return true;
	}

	/**
	 * Convenience method to determine if the current JVM is at least
	 * Java 1.5 (Java 5).
	 * @return <code>true</code> if the current JVM is at least Java 1.5
	 * @see #getMajorVersion()
	 * @see #JAVA_15
	 * @see #JAVA_16
	 * @see #JAVA_17
	 * @see #JAVA_18
	 */
	public static boolean isAtLeastJava15() {
		return getMajorVersion() >= JAVA_15;
	}

	/**
	 * Convenience method to determine if the current JVM is at least
	 * Java 1.6 (Java 6).
	 * @return <code>true</code> if the current JVM is at least Java 1.6
	 * @see #getMajorVersion()
	 * @see #JAVA_16
	 * @see #JAVA_17
	 * @see #JAVA_18
	 */
	public static boolean isAtLeastJava16() {
		return getMajorVersion() >= JAVA_16;
	}

	/**
	 * Convenience method to determine if the current JVM is at least
	 * Java 1.7 (Java 7).
	 * @return <code>true</code> if the current JVM is at least Java 1.7
	 * @see #getMajorVersion()
	 * @see #JAVA_17
	 * @see #JAVA_18
	 */
	public static boolean isAtLeastJava17() {
		return getMajorVersion() >= JAVA_17;
	}

	/**
	 * Convenience method to determine if the current JVM is at least
	 * Java 1.8 (Java 8).
	 * @return <code>true</code> if the current JVM is at least Java 1.8
	 * @see #getMajorVersion()
	 * @see #JAVA_18
	 */
	public static boolean isAtLeastJava18() {
		return getMajorVersion() >= JAVA_18;
	}

	/**
	 * Convenience method to determine if the current JVM is at least
	 * Java 1.9 (Java 9).
	 * @return <code>true</code> if the current JVM is at least Java 1.9
	 * @see #getMajorVersion()
	 * @see #JAVA_19
	 */
	public static boolean isAtLeastJava19() {
		return getMajorVersion() >= JAVA_19;
	}
	
	private static String generateIdentifiedKey() {
		try {
			List<NetworkInfo> networkInfos = retrieveNetworkInfos();
			List<String> macAddrList = new ArrayList<String>();
			
			for (NetworkInfo networkInfo : networkInfos) {
				if (!networkInfo.isVirtual() && networkInfo.getMacAddress().length() > 0 
						&& !macAddrList.contains(networkInfo.getMacAddress())) {
					macAddrList.add(networkInfo.getMacAddress());
				}
			}
			
			Collections.sort(macAddrList);
			return SecurityUtils.MD5(macAddrList);
		} catch (Exception e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Generate identified key error!", e);
			}
		}
		
		return null;
	}
	
	private static List<NetworkInfo> retrieveNetworkInfos() throws SocketException {
		List<NetworkInfo> networkInfos = new ArrayList<NetworkInfo>();
		
		Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
		
		while (enumeration.hasMoreElements()) {
			try {
				NetworkInfo networkInfo = new NetworkInfo(enumeration.nextElement());
				networkInfos.add(networkInfo);
			} catch (NetworkInfoException e) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Retrieve network info error!", e);
				}
				continue;
			}
		}
		
		return networkInfos;
	}
}
