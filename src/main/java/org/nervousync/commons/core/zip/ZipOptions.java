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
package org.nervousync.commons.core.zip;

import java.util.TimeZone;

import org.nervousync.exceptions.zip.ZipException;
import org.nervousync.commons.core.Globals;
import org.nervousync.utils.StringUtils;

/**
 * ZIP options
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: Nov 29, 2017 3:51:12 PM $
 */
public final class ZipOptions implements Cloneable {

	/**
	 * Folder, file name and comment charset encoding
	 */
	private String charsetEncoding = Globals.DEFAULT_SYSTEM_CHARSET;
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
	private boolean encryptFiles = Boolean.FALSE;
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
	private boolean sourceExternalStream = Boolean.FALSE;
	
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
	 *
	 * @return generated instance
	 */
	public static ZipOptions newOptions() {
		return new ZipOptions();
	}

	/**
	 * Generate a standard encrypt ZipOptions instance by given password
	 *
	 * @param password encrypt password
	 * @return generated instance
	 * @throws ZipException if password is null
	 */
	public static ZipOptions standardEncryptOptions(String password)
			throws ZipException {
		if (StringUtils.isEmpty(password)) {
			throw new ZipException("Password is null");
		}
		return new ZipOptions(password);
	}

	/**
	 * Generate an AES encrypt ZipOptions instance by given password
	 *
	 * @param password encrypt password
	 * @return generated instance
	 * @throws ZipException if password is null
	 */
	public static ZipOptions aesEncryptOptions(String password)
			throws ZipException {
		return ZipOptions.aesEncryptOptions(password, Globals.DEFAULT_VALUE_INT);
	}

	/**
	 * Generate an AES encrypt ZipOptions instance by given password and key strength
	 *
	 * @param password     encrypt password
	 * @param aesKeyLength the aes key length
	 * @return generated instance
	 * @throws ZipException if password is null
	 */
	public static ZipOptions aesEncryptOptions(String password,
			int aesKeyLength) throws ZipException {
		if (StringUtils.isEmpty(password)) {
			throw new ZipException("Password is null");
		}

		switch (aesKeyLength) {
			case 128:
			case Globals.DEFAULT_VALUE_INT:
				return new ZipOptions(password, ZipConstants.AES_STRENGTH_128);
			case 192:
				return new ZipOptions(password, ZipConstants.AES_STRENGTH_192);
			case 256:
				return new ZipOptions(password, ZipConstants.AES_STRENGTH_256);
			default:
				throw new ZipException("Invalid aes strength");
		}
	}

	/**
	 * Gets charset encoding.
	 *
	 * @return the charset encoding
	 */
	public String getCharsetEncoding() {
		return charsetEncoding;
	}

	/**
	 * Sets charset encoding.
	 *
	 * @param charsetEncoding the charset encoding
	 */
	public void setCharsetEncoding(String charsetEncoding) {
		this.charsetEncoding = charsetEncoding;
	}

	/**
	 * Gets compression method.
	 *
	 * @return the compressionMethod
	 */
	public int getCompressionMethod() {
		return compressionMethod;
	}

	/**
	 * Sets compression method.
	 *
	 * @param compressionMethod the compressionMethod to set
	 */
	public void setCompressionMethod(int compressionMethod) {
		this.compressionMethod = compressionMethod;
	}

	/**
	 * Gets compression level.
	 *
	 * @return the compressionLevel
	 */
	public int getCompressionLevel() {
		return compressionLevel;
	}

	/**
	 * Sets compression level.
	 *
	 * @param compressionLevel the compressionLevel to set
	 */
	public void setCompressionLevel(int compressionLevel) {
		this.compressionLevel = compressionLevel;
	}

	/**
	 * Is encrypt files boolean.
	 *
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
	 * Gets encryption method.
	 *
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
	 * Is read hidden files boolean.
	 *
	 * @return the readHiddenFiles
	 */
	public boolean isReadHiddenFiles() {
		return readHiddenFiles;
	}

	/**
	 * Sets read hidden files.
	 *
	 * @param readHiddenFiles the readHiddenFiles to set
	 */
	public void setReadHiddenFiles(boolean readHiddenFiles) {
		this.readHiddenFiles = readHiddenFiles;
	}

	/**
	 * Get password char [ ].
	 *
	 * @return the password
	 */
	public char[] getPassword() {
		return password == null ? new char[0] : password.clone();
	}

	/**
	 * Gets aes key strength.
	 *
	 * @return the aesKeyStrength
	 */
	public int getAesKeyStrength() {
		return aesKeyStrength;
	}

	/**
	 * Is include root folder boolean.
	 *
	 * @return the includeRootFolder
	 */
	public boolean isIncludeRootFolder() {
		return includeRootFolder;
	}

	/**
	 * Sets include root folder.
	 *
	 * @param includeRootFolder the includeRootFolder to set
	 */
	public void setIncludeRootFolder(boolean includeRootFolder) {
		this.includeRootFolder = includeRootFolder;
	}

	/**
	 * Gets root folder in zip.
	 *
	 * @return the rootFolderInZip
	 */
	public String getRootFolderInZip() {
		return rootFolderInZip;
	}

	/**
	 * Sets root folder in zip.
	 *
	 * @param rootFolderInZip the rootFolderInZip to set
	 */
	public void setRootFolderInZip(String rootFolderInZip) {
		if (!rootFolderInZip.endsWith(Globals.DEFAULT_PAGE_SEPARATOR)) {
			this.rootFolderInZip = rootFolderInZip + Globals.DEFAULT_PAGE_SEPARATOR;
		}
		this.rootFolderInZip = StringUtils.replace(rootFolderInZip, Globals.DEFAULT_PAGE_SEPARATOR, "/");
	}

	/**
	 * Gets time zone.
	 *
	 * @return the timeZone
	 */
	public TimeZone getTimeZone() {
		return timeZone;
	}

	/**
	 * Sets time zone.
	 *
	 * @param timeZone the timeZone to set
	 */
	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}

	/**
	 * Gets source file crc.
	 *
	 * @return the sourceFileCRC
	 */
	public long getSourceFileCRC() {
		return sourceFileCRC;
	}

	/**
	 * Sets source file crc.
	 *
	 * @param sourceFileCRC the sourceFileCRC to set
	 */
	public void setSourceFileCRC(long sourceFileCRC) {
		this.sourceFileCRC = sourceFileCRC;
	}

	/**
	 * Gets default folder path.
	 *
	 * @return the defaultFolderPath
	 */
	public String getDefaultFolderPath() {
		return defaultFolderPath;
	}

	/**
	 * Sets default folder path.
	 *
	 * @param defaultFolderPath the defaultFolderPath to set
	 */
	public void setDefaultFolderPath(String defaultFolderPath) {
		this.defaultFolderPath = defaultFolderPath;
	}

	/**
	 * Gets file name in zip.
	 *
	 * @return the fileNameInZip
	 */
	public String getFileNameInZip() {
		return fileNameInZip;
	}

	/**
	 * Sets file name in zip.
	 *
	 * @param fileNameInZip the fileNameInZip to set
	 */
	public void setFileNameInZip(String fileNameInZip) {
		this.fileNameInZip = fileNameInZip;
	}

	/**
	 * Is source external stream boolean.
	 *
	 * @return the isSourceExternalStream
	 */
	public boolean isSourceExternalStream() {
		return sourceExternalStream;
	}

	/**
	 * Sets source external stream.
	 *
	 * @param isSourceExternalStream the isSourceExternalStream to set
	 */
	public void setSourceExternalStream(boolean isSourceExternalStream) {
		this.sourceExternalStream = isSourceExternalStream;
	}
	
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
