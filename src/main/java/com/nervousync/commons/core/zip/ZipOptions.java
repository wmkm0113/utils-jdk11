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
package com.nervousync.commons.core.zip;

import java.util.TimeZone;

import com.nervousync.commons.core.Globals;
import com.nervousync.exceptions.zip.ZipException;
import com.nervousync.utils.StringUtils;

/**
 * ZIP options
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Nov 29, 2017 3:51:12 PM $
 */
public final class ZipOptions implements Cloneable {

	/**
	 * Compress method
	 */
	private int compressionMethod = ZipConstants.COMP_DEFLATE;
	/**
	 * Compress level
	 */
	private int compressionLevel = ZipConstants.DEFLATE_LEVEL_NORMAL;
	/**
	 * Encrypt files status
	 */
	private boolean encryptFiles = Globals.DEFAULT_VALUE_BOOLEAN;
	/**
	 * Encrypt method
	 */
	private int encryptionMethod = ZipConstants.ENC_NO_ENCRYPTION;
	/**
	 * Status of read hidden file
	 */
	private boolean readHiddenFiles = true;
	/**
	 * Encrypt/Decrypt password
	 */
	private char[] password = null;
	/**
	 * AES key length
	 */
	private int aesKeyStrength = Globals.DEFAULT_VALUE_INT;
	/**
	 * Include root folder
	 */
	private boolean includeRootFolder = true;
	/**
	 * Root folder path
	 */
	private String rootFolderInZip = "";
	/**
	 * Timezone setting
	 * @see java.util.TimeZone
	 */
	private TimeZone timeZone = TimeZone.getDefault();
	/**
	 * CRC
	 */
	private long sourceFileCRC = Globals.DEFAULT_VALUE_LONG;
	/**
	 * Default folder path
	 */
	private String defaultFolderPath = "";
	/**
	 * File name in zip
	 */
	private String fileNameInZip = null;
	/**
	 * Status of source external stream
	 */
	private boolean isSourceExternalStream = Globals.DEFAULT_VALUE_BOOLEAN;
	
	/**
	 * Default Constructor
	 */
	private ZipOptions() {
		
	}
	
	/**
	 * Constructor by given password
	 * @param password  password
	 */
	private ZipOptions(String password) {
		this.encryptFiles = true;
		this.password = password.toCharArray();
		this.encryptionMethod = ZipConstants.ENC_METHOD_STANDARD;
	}
	
	/**
	 * Constructor by given password and key strength
	 * @param password encrypt password
	 * @param aesKeyStrength AES key strength
	 */
	private ZipOptions(String password, int aesKeyStrength) {
		this.encryptFiles = true;
		this.password = password.toCharArray();
		this.encryptionMethod = ZipConstants.ENC_METHOD_AES;
		this.aesKeyStrength = aesKeyStrength;
	}
	
	/**
	 * Generate default ZipOptions instance
	 * @return generated instance
	 */
	public static ZipOptions newOptions() {
		return new ZipOptions();
	}
	
	/**
	 * Generate a standard encrypt ZipOptions instance by given password
	 * @param password encrypt password
	 * @return generated instance
	 * @throws ZipException	if password is null
	 */
	public static ZipOptions standardEncryptOptions(String password) 
			throws ZipException {
		if (!StringUtils.isNotNullAndNotEmpty(password)) {
			throw new ZipException("Password is null");
		}
		return new ZipOptions(password);
	}
	
	/**
	 * Generate an AES encrypt ZipOptions instance by given password
	 * @param password encrypt password
	 * @return generated instance
	 * @throws ZipException	if password is null
	 */
	public static ZipOptions aesEncryptOptions(String password) 
			throws ZipException {
		return ZipOptions.aesEncryptOptions(password, ZipConstants.AES_STRENGTH_128);
	}

	/**
	 * Generate an AES encrypt ZipOptions instance by given password and key strength
	 * @param password encrypt password
	 * @param aesKeyStrength AES key strength
	 * @return generated instance
	 * @throws ZipException	if password is null
	 */
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
	 * Sets the encryptFiles.
	 *
	 * @param encryptFiles encryptFiles
	 */
	public void setEncryptFiles(boolean encryptFiles) {
		this.encryptFiles = encryptFiles;
	}

	/**
	 * @return the encryptionMethod
	 */
	public int getEncryptionMethod() {
		return encryptionMethod;
	}

	/**
	 * Sets the encryptionMethod.
	 *
	 * @param encryptionMethod encryptionMethod
	 */
	public void setEncryptionMethod(int encryptionMethod) {
		this.encryptionMethod = encryptionMethod;
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
