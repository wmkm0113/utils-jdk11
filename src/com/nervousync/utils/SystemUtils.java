/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervousync Studio.
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
			return SecurityUtils.MD5Encode(macAddrList);
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
