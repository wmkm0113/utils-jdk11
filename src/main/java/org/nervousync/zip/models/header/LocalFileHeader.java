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

import java.io.DataInput;
import java.io.InputStream;
import java.io.RandomAccessFile;

import org.nervousync.commons.core.Globals;
import org.nervousync.commons.core.zip.ZipConstants;
import org.nervousync.exceptions.zip.ZipException;
import org.nervousync.zip.crypto.impl.aes.AESCrypto;
import org.nervousync.zip.models.header.utils.HeaderOperator;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Nov 29, 2017 2:29:26 PM $
 */
public class LocalFileHeader extends FileHeader {

	private byte[] extraField;
	private long offsetStartOfData;
	private boolean writeCompressSizeInZip64ExtraRecord;
	
	/**
	 * @return the extraField
	 */
	public byte[] getExtraField() {
		return extraField == null ? new byte[0] : extraField.clone();
	}

	/**
	 * @param extraField the extraField to set
	 */
	public void setExtraField(byte[] extraField) {
		this.extraField = extraField == null ? new byte[0] : extraField.clone();
	}

	/**
	 * @return the offsetStartOfData
	 */
	public long getOffsetStartOfData() {
		return offsetStartOfData;
	}

	/**
	 * @param offsetStartOfData the offsetStartOfData to set
	 */
	public void setOffsetStartOfData(long offsetStartOfData) {
		this.offsetStartOfData = offsetStartOfData;
	}

	/**
	 * @return the writeCompressSizeInZip64ExtraRecord
	 */
	public boolean isWriteCompressSizeInZip64ExtraRecord() {
		return writeCompressSizeInZip64ExtraRecord;
	}

	/**
	 * @param writeCompressSizeInZip64ExtraRecord the writeCompressSizeInZip64ExtraRecord to set
	 */
	public void setWriteCompressSizeInZip64ExtraRecord(boolean writeCompressSizeInZip64ExtraRecord) {
		this.writeCompressSizeInZip64ExtraRecord = writeCompressSizeInZip64ExtraRecord;
	}
	
	public boolean verifyPassword(DataInput input) throws ZipException {
		if (!this.isEncrypted()) {
			return true;
		}

		try {
			if (this.getEncryptionMethod() == ZipConstants.ENC_METHOD_AES) {
				if (this.getAesExtraDataRecord() != null) {
					byte[] salt = new byte[HeaderOperator.retrieveSaltLength(this.getAesExtraDataRecord().getAesStrength())];
					int readLength = Globals.DEFAULT_VALUE_INT;
					if (input instanceof RandomAccessFile) {
						((RandomAccessFile) input).seek(this.getOffsetStartOfData());
						readLength = ((RandomAccessFile) input).read(salt);
					} else if (input instanceof InputStream) {
						long skipLength = ((InputStream) input).skip(this.getOffsetStartOfData());
						if (skipLength == this.getOffsetStartOfData()) {
							readLength = ((InputStream) input).read(salt);
						}
					}

					if (readLength != salt.length) {
						return Globals.DEFAULT_VALUE_BOOLEAN;
					}

					readLength = Globals.DEFAULT_VALUE_INT;
					byte[] passwordBytes = new byte[2];
					if (input instanceof RandomAccessFile) {
						readLength = ((RandomAccessFile) input).read(passwordBytes);
					} else if (input instanceof InputStream) {
						readLength = ((InputStream) input).read(passwordBytes);
					}

					if (readLength == 2) {
						return AESCrypto.verifyPassword(this.getAesExtraDataRecord().getAesStrength(),
								salt, this.getPassword(), passwordBytes);
					}
				}
				return Globals.DEFAULT_VALUE_BOOLEAN;
			} else if (this.getEncryptionMethod() == ZipConstants.ENC_METHOD_STANDARD) {
				//	Not supported verify password of standard encrypt
				return true;
			} else {
				throw new ZipException("Unsupported encryption method");
			}
		} catch (Exception e) {
			return Globals.DEFAULT_VALUE_BOOLEAN;
		}
	}
}
