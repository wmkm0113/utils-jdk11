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
package org.nervousync.zip.models.header;

import java.util.List;

import org.nervousync.commons.Globals;
import org.nervousync.zip.models.AESExtraDataRecord;
import org.nervousync.zip.models.ExtraDataRecord;
import org.nervousync.zip.models.Zip64ExtendInfo;

/**
 * The type File header.
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Nov 29, 2017 2:30:48 PM $
 */
public class FileHeader {

	private int signature;
	private int extractNeeded;
	private byte[] generalPurposeFlag;
	private int compressionMethod;
	private int lastModFileTime;
	private long crc32;
	private byte[] crcBuffer;
	private long compressedSize;
	private long originalSize;
	private String entryPath;
	private int fileNameLength;
	private int extraFieldLength;
	private char[] password;
	private List<ExtraDataRecord> extraDataRecords;
	private Zip64ExtendInfo zip64ExtendInfo;
	private AESExtraDataRecord aesExtraDataRecord;
	private boolean fileNameUTF8Encoded;
	private boolean isEncrypted;
	private boolean dataDescriptorExists;
	private int encryptionMethod = Globals.ENC_NO_ENCRYPTION;

	/**
	 * Instantiates a new File header.
	 */
	public FileHeader() {
	}

	/**
	 * Gets signature.
	 *
	 * @return the signature
	 */
	public int getSignature() {
		return signature;
	}

	/**
	 * Sets signature.
	 *
	 * @param signature the signature to set
	 */
	public void setSignature(int signature) {
		this.signature = signature;
	}

	/**
	 * Gets extract needed.
	 *
	 * @return the extractNeeded
	 */
	public int getExtractNeeded() {
		return extractNeeded;
	}

	/**
	 * Sets extract needed.
	 *
	 * @param extractNeeded the extractNeeded to set
	 */
	public void setExtractNeeded(int extractNeeded) {
		this.extractNeeded = extractNeeded;
	}

	/**
	 * Get general purpose flag byte [ ].
	 *
	 * @return the generalPurposeFlag
	 */
	public byte[] getGeneralPurposeFlag() {
		return generalPurposeFlag == null ? new byte[0] : generalPurposeFlag.clone();
	}

	/**
	 * Sets general purpose flag.
	 *
	 * @param generalPurposeFlag the generalPurposeFlag to set
	 */
	public void setGeneralPurposeFlag(byte[] generalPurposeFlag) {
		this.generalPurposeFlag = generalPurposeFlag == null ? new byte[0] : generalPurposeFlag.clone();
		if (generalPurposeFlag != null) {
			this.setEncrypted((generalPurposeFlag[0] & 1) != 0);
		}
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
	 * Gets last mod file time.
	 *
	 * @return the lastModFileTime
	 */
	public int getLastModFileTime() {
		return lastModFileTime;
	}

	/**
	 * Sets last mod file time.
	 *
	 * @param lastModFileTime the lastModFileTime to set
	 */
	public void setLastModFileTime(int lastModFileTime) {
		this.lastModFileTime = lastModFileTime;
	}

	/**
	 * Gets crc 32.
	 *
	 * @return the crc32
	 */
	public long getCrc32() {
		return crc32 & 0xFFFFFFFFL;
	}

	/**
	 * Sets crc 32.
	 *
	 * @param crc32 the crc32 to set
	 */
	public void setCrc32(long crc32) {
		this.crc32 = crc32;
	}

	/**
	 * Get crc buffer byte [ ].
	 *
	 * @return the crcBuffer
	 */
	public byte[] getCrcBuffer() {
		return crcBuffer == null ? new byte[0] : crcBuffer.clone();
	}

	/**
	 * Sets crc buffer.
	 *
	 * @param crcBuffer the crcBuffer to set
	 */
	public void setCrcBuffer(byte[] crcBuffer) {
		this.crcBuffer = crcBuffer == null ? new byte[0] : crcBuffer.clone();
	}

	/**
	 * Gets compressed size.
	 *
	 * @return the compressedSize
	 */
	public long getCompressedSize() {
		return compressedSize;
	}

	/**
	 * Sets compressed size.
	 *
	 * @param compressedSize the compressedSize to set
	 */
	public void setCompressedSize(long compressedSize) {
		this.compressedSize = compressedSize;
	}

	/**
	 * Gets original size.
	 *
	 * @return the originalSize
	 */
	public long getOriginalSize() {
		return originalSize;
	}

	/**
	 * Sets original size.
	 *
	 * @param originalSize the originalSize to set
	 */
	public void setOriginalSize(long originalSize) {
		this.originalSize = originalSize;
	}

	/**
	 * Gets file name length.
	 *
	 * @return the fileNameLength
	 */
	public int getFileNameLength() {
		return fileNameLength;
	}

	/**
	 * Sets file name length.
	 *
	 * @param fileNameLength the fileNameLength to set
	 */
	public void setFileNameLength(int fileNameLength) {
		this.fileNameLength = fileNameLength;
	}

	/**
	 * Gets extra field length.
	 *
	 * @return the extraFieldLength
	 */
	public int getExtraFieldLength() {
		return extraFieldLength;
	}

	/**
	 * Sets extra field length.
	 *
	 * @param extraFieldLength the extraFieldLength to set
	 */
	public void setExtraFieldLength(int extraFieldLength) {
		this.extraFieldLength = extraFieldLength;
	}

	/**
	 * Gets the entry path.
	 *
	 * @return the entryPath
	 */
	public String getEntryPath() {
		return entryPath;
	}

	/**
	 * Sets the entry path.
	 *
	 * @param entryPath the entryPath to set
	 */
	public void setEntryPath(String entryPath) {
		this.entryPath = entryPath;
	}

	/**
	 * Is encrypted boolean.
	 *
	 * @return the isEncrypted
	 */
	public boolean isEncrypted() {
		return isEncrypted;
	}

	/**
	 * Sets encrypted.
	 *
	 * @param isEncrypted the isEncrypted to set
	 */
	public void setEncrypted(boolean isEncrypted) {
		this.isEncrypted = isEncrypted;
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
	 * Sets password.
	 *
	 * @param password the password to set
	 */
	public void setPassword(char[] password) {
		this.password = password == null ? new char[0] : password.clone();
	}

	/**
	 * Is data descriptor exists boolean.
	 *
	 * @return the dataDescriptorExists
	 */
	public boolean isDataDescriptorExists() {
		return dataDescriptorExists;
	}

	/**
	 * Sets data descriptor exists.
	 *
	 * @param dataDescriptorExists the dataDescriptorExists to set
	 */
	public void setDataDescriptorExists(boolean dataDescriptorExists) {
		this.dataDescriptorExists = dataDescriptorExists;
	}

	/**
	 * Gets zip 64 extend info.
	 *
	 * @return the zip64ExtendInfo
	 */
	public Zip64ExtendInfo getZip64ExtendInfo() {
		return zip64ExtendInfo;
	}

	/**
	 * Sets zip 64 extend info.
	 *
	 * @param zip64ExtendInfo the zip64ExtendInfo to set
	 */
	public void setZip64ExtendInfo(Zip64ExtendInfo zip64ExtendInfo) {
		this.zip64ExtendInfo = zip64ExtendInfo;
	}

	/**
	 * Gets aes extra data record.
	 *
	 * @return the aesExtraDataRecord
	 */
	public AESExtraDataRecord getAesExtraDataRecord() {
		return aesExtraDataRecord;
	}

	/**
	 * Sets aes extra data record.
	 *
	 * @param aesExtraDataRecord the aesExtraDataRecord to set
	 */
	public void setAesExtraDataRecord(AESExtraDataRecord aesExtraDataRecord) {
		this.aesExtraDataRecord = aesExtraDataRecord;
	}

	/**
	 * Gets extra data records.
	 *
	 * @return the extraDataRecords
	 */
	public List<ExtraDataRecord> getExtraDataRecords() {
		return extraDataRecords;
	}

	/**
	 * Sets extra data records.
	 *
	 * @param extraDataRecords the extraDataRecords to set
	 */
	public void setExtraDataRecords(List<ExtraDataRecord> extraDataRecords) {
		this.extraDataRecords = extraDataRecords;
	}

	/**
	 * Is file name utf 8 encoded boolean.
	 *
	 * @return the fileNameUTF8Encoded
	 */
	public boolean isFileNameUTF8Encoded() {
		return fileNameUTF8Encoded;
	}

	/**
	 * Sets file name utf 8 encoded.
	 *
	 * @param fileNameUTF8Encoded the fileNameUTF8Encoded to set
	 */
	public void setFileNameUTF8Encoded(boolean fileNameUTF8Encoded) {
		this.fileNameUTF8Encoded = fileNameUTF8Encoded;
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
	 * Sets encryption method.
	 *
	 * @param encryptionMethod the encryptionMethod to set
	 */
	public void setEncryptionMethod(int encryptionMethod) {
		this.encryptionMethod = encryptionMethod;
	}
}
