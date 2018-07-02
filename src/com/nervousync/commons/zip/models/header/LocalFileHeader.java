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
package com.nervousync.commons.zip.models.header;

import java.io.DataInput;
import java.io.InputStream;
import java.io.RandomAccessFile;

import com.nervousync.commons.core.Globals;
import com.nervousync.commons.core.zip.ZipConstants;
import com.nervousync.commons.zip.crypto.impl.AESCrypto;
import com.nervousync.exceptions.zip.ZipException;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Nov 29, 2017 2:29:26 PM $
 */
public class LocalFileHeader extends FileHeader {

	private byte[] extraField;
	private long offsetStartOfData;
	private boolean writeComprSizeInZip64ExtraRecord;
	
	/**
	 * @return the extraField
	 */
	public byte[] getExtraField() {
		return extraField;
	}

	/**
	 * @param extraField the extraField to set
	 */
	public void setExtraField(byte[] extraField) {
		this.extraField = extraField;
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
	 * @return the writeComprSizeInZip64ExtraRecord
	 */
	public boolean isWriteComprSizeInZip64ExtraRecord() {
		return writeComprSizeInZip64ExtraRecord;
	}

	/**
	 * @param writeComprSizeInZip64ExtraRecord the writeComprSizeInZip64ExtraRecord to set
	 */
	public void setWriteComprSizeInZip64ExtraRecord(boolean writeComprSizeInZip64ExtraRecord) {
		this.writeComprSizeInZip64ExtraRecord = writeComprSizeInZip64ExtraRecord;
	}
	
	public boolean verifyPassword(DataInput input) throws ZipException {
		if (!this.isEncrypted()) {
			return true;
		}

		try {
			if (this.getEncryptionMethod() == ZipConstants.ENC_METHOD_AES) {
				byte[] salt = null;
				if (this.getAesExtraDataRecord() != null) {
					int saltLength = Globals.DEFAULT_VALUE_INT;
					
					switch (this.getAesExtraDataRecord().getAesStrength()) {
					case ZipConstants.AES_STRENGTH_128:
						saltLength = 8;
						break;
					case ZipConstants.AES_STRENGTH_192:
						saltLength = 12;
						break;
					case ZipConstants.AES_STRENGTH_256:
						saltLength = 16;
						break;
						default:
							throw new ZipException("unable to determine salt length: invalid aes key strength");
					}
					salt = new byte[saltLength];
					if (input instanceof RandomAccessFile) {
						((RandomAccessFile)input).seek(this.getOffsetStartOfData());
						((RandomAccessFile)input).read(salt);
					} else if (input instanceof InputStream) {
						((InputStream)input).skip(this.getOffsetStartOfData());
						((InputStream)input).read(salt);
					}
				}
				
				byte[] passwordBytes = new byte[2];
				if (input instanceof RandomAccessFile) {
					((RandomAccessFile)input).read(passwordBytes);
				} else if (input instanceof InputStream) {
					((InputStream)input).read(passwordBytes);
				}
				
				return AESCrypto.verifyPassword(this.getAesExtraDataRecord().getAesStrength(), 
						salt, this.getPassword(), passwordBytes);
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
