/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervous Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervous Studio.
 */
package com.nervousync.commons.zip.core;

import java.util.TimeZone;

import com.nervousync.commons.core.Globals;
import com.nervousync.exceptions.zip.ZipException;
import com.nervousync.utils.StringUtils;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Nov 29, 2017 3:51:12 PM $
 */
public final class ZipOptions implements Cloneable {

	private int compressionMethod = ZipConstants.COMP_DEFLATE;
	private int compressionLevel = ZipConstants.DEFLATE_LEVEL_NORMAL;
	private boolean encryptFiles = Globals.DEFAULT_VALUE_BOOLEAN;
	private int encryptionMethod = ZipConstants.ENC_NO_ENCRYPTION;
	private boolean readHiddenFiles = true;
	private char[] password = null;
	private int aesKeyStrength = Globals.DEFAULT_VALUE_INT;
	private boolean includeRootFolder = true;
	private String rootFolderInZip = "";
	private TimeZone timeZone = TimeZone.getDefault();
	private long sourceFileCRC = Globals.DEFAULT_VALUE_LONG;
	private String defaultFolderPath = "";
	private String fileNameInZip = null;
	private boolean isSourceExternalStream = Globals.DEFAULT_VALUE_BOOLEAN;
	
	private ZipOptions() {
		
	}
	
	private ZipOptions(String password) {
		this.encryptFiles = true;
		this.password = password.toCharArray();
		this.encryptionMethod = ZipConstants.ENC_METHOD_STANDARD;
	}
	
	private ZipOptions(String password, int aesKeyStrength) {
		this.encryptFiles = true;
		this.password = password.toCharArray();
		this.encryptionMethod = ZipConstants.ENC_METHOD_AES;
		this.aesKeyStrength = aesKeyStrength;
	}
	
	public static ZipOptions newOptions() {
		return new ZipOptions();
	}
	
	public static ZipOptions standardEncryptOptions(String password) 
			throws ZipException {
		if (!StringUtils.isNotNullAndNotEmpty(password)) {
			throw new ZipException("Password is null");
		}
		return new ZipOptions(password);
	}
	
	public static ZipOptions aesEncryptOptions(String password) 
			throws ZipException {
		return ZipOptions.aesEncryptOptions(password, ZipConstants.AES_STRENGTH_128);
	}
	
	public static ZipOptions aesEncryptOptions(String password, 
			int aesKeyStrength) throws ZipException {
		if (!StringUtils.isNotNullAndNotEmpty(password)) {
			throw new ZipException("Password is null");
		}
		
		if (aesKeyStrength != ZipConstants.AES_STRENGTH_128 
				&& aesKeyStrength != ZipConstants.AES_STRENGTH_192 
				&& aesKeyStrength != ZipConstants.AES_STRENGTH_256) {
			throw new ZipException("Invalid aes strength");
		}
		
		return new ZipOptions(password, aesKeyStrength);
	}

	/**
	 * @return the compressionMethod
	 */
	public int getCompressionMethod() {
		return compressionMethod;
	}

	/**
	 * @param compressionMethod the compressionMethod to set
	 */
	public void setCompressionMethod(int compressionMethod) {
		this.compressionMethod = compressionMethod;
	}

	/**
	 * @return the compressionLevel
	 */
	public int getCompressionLevel() {
		return compressionLevel;
	}

	/**
	 * @param compressionLevel the compressionLevel to set
	 */
	public void setCompressionLevel(int compressionLevel) {
		this.compressionLevel = compressionLevel;
	}

	/**
	 * @return the encryptFiles
	 */
	public boolean isEncryptFiles() {
		return encryptFiles;
	}

	/**
	 * @return the encryptionMethod
	 */
	public int getEncryptionMethod() {
		return encryptionMethod;
	}

	/**
	 * @return the readHiddenFiles
	 */
	public boolean isReadHiddenFiles() {
		return readHiddenFiles;
	}

	/**
	 * @param readHiddenFiles the readHiddenFiles to set
	 */
	public void setReadHiddenFiles(boolean readHiddenFiles) {
		this.readHiddenFiles = readHiddenFiles;
	}

	/**
	 * @return the password
	 */
	public char[] getPassword() {
		return password;
	}

	/**
	 * @return the aesKeyStrength
	 */
	public int getAesKeyStrength() {
		return aesKeyStrength;
	}

	/**
	 * @return the includeRootFolder
	 */
	public boolean isIncludeRootFolder() {
		return includeRootFolder;
	}

	/**
	 * @param includeRootFolder the includeRootFolder to set
	 */
	public void setIncludeRootFolder(boolean includeRootFolder) {
		this.includeRootFolder = includeRootFolder;
	}

	/**
	 * @return the rootFolderInZip
	 */
	public String getRootFolderInZip() {
		return rootFolderInZip;
	}

	/**
	 * @param rootFolderInZip the rootFolderInZip to set
	 */
	public void setRootFolderInZip(String rootFolderInZip) {
		if (!rootFolderInZip.endsWith(Globals.DEFAULT_PAGE_SEPARATOR)) {
			this.rootFolderInZip = rootFolderInZip + Globals.DEFAULT_PAGE_SEPARATOR;
		}
		this.rootFolderInZip = StringUtils.replace(rootFolderInZip, Globals.DEFAULT_PAGE_SEPARATOR, "/");
	}

	/**
	 * @return the timeZone
	 */
	public TimeZone getTimeZone() {
		return timeZone;
	}

	/**
	 * @param timeZone the timeZone to set
	 */
	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}

	/**
	 * @return the sourceFileCRC
	 */
	public long getSourceFileCRC() {
		return sourceFileCRC;
	}

	/**
	 * @param sourceFileCRC the sourceFileCRC to set
	 */
	public void setSourceFileCRC(long sourceFileCRC) {
		this.sourceFileCRC = sourceFileCRC;
	}

	/**
	 * @return the defaultFolderPath
	 */
	public String getDefaultFolderPath() {
		return defaultFolderPath;
	}

	/**
	 * @param defaultFolderPath the defaultFolderPath to set
	 */
	public void setDefaultFolderPath(String defaultFolderPath) {
		this.defaultFolderPath = defaultFolderPath;
	}

	/**
	 * @return the fileNameInZip
	 */
	public String getFileNameInZip() {
		return fileNameInZip;
	}

	/**
	 * @param fileNameInZip the fileNameInZip to set
	 */
	public void setFileNameInZip(String fileNameInZip) {
		this.fileNameInZip = fileNameInZip;
	}

	/**
	 * @return the isSourceExternalStream
	 */
	public boolean isSourceExternalStream() {
		return isSourceExternalStream;
	}

	/**
	 * @param isSourceExternalStream the isSourceExternalStream to set
	 */
	public void setSourceExternalStream(boolean isSourceExternalStream) {
		this.isSourceExternalStream = isSourceExternalStream;
	}
	
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
