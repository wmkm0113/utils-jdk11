/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervousync Studio.
 */
package com.nervousync.utils;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Jun 19, 2015 1:38:20 PM $
 */
public final class JavaVersion {

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
	
	private JavaVersion() {
		
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
		return JavaVersion.getMajorVersion() >= JAVA_15;
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
		return JavaVersion.getMajorVersion() >= JAVA_16;
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
		return JavaVersion.getMajorVersion() >= JAVA_17;
	}

	/**
	 * Convenience method to determine if the current JVM is at least
	 * Java 1.8 (Java 8).
	 * @return <code>true</code> if the current JVM is at least Java 1.8
	 * @see #getMajorVersion()
	 * @see #JAVA_18
	 */
	public static boolean isAtLeastJava18() {
		return JavaVersion.getMajorVersion() >= JAVA_18;
	}

	/**
	 * Convenience method to determine if the current JVM is at least
	 * Java 1.9 (Java 9).
	 * @return <code>true</code> if the current JVM is at least Java 1.9
	 * @see #getMajorVersion()
	 * @see #JAVA_19
	 */
	public static boolean isAtLeastJava19() {
		return JavaVersion.getMajorVersion() >= JAVA_19;
	}
}
