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
package com.nervousync.commons.zip.crypto.impl;

import com.nervousync.commons.core.zip.ZipConstants;
import com.nervousync.commons.zip.crypto.Decryptor;
import com.nervousync.commons.zip.models.header.LocalFileHeader;
import com.nervousync.exceptions.zip.ZipException;
import com.nervousync.utils.RawUtils;

/**
 * Decryptor implement of AES
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Dec 2, 2017 10:55:30 AM $
 */
public class AESDecryptor extends AESCrypto implements Decryptor {

	private LocalFileHeader localFileHeader;
	private byte[] storedMac = null;

	public AESDecryptor(LocalFileHeader localFileHeader, 
			byte[] salt, byte[] passwordBytes) throws ZipException {
		if (localFileHeader == null) {
			throw new ZipException("Local file header is null!");
		}
		
		this.localFileHeader = localFileHeader;
		if (this.localFileHeader.getAesExtraDataRecord() == null) {
			throw new ZipException("Invalid aes extra data record!");
		}

		super.preInit(this.localFileHeader.getAesExtraDataRecord().getAesStrength());
		this.init(salt, this.localFileHeader.getPassword());
		
		if (!this.verifyPassword(passwordBytes)) {
			throw new ZipException("Wrong password!");
		}
	}
	
	@Override
	public int decryptData(byte[] buff) throws ZipException {
		return this.decryptData(buff, 0, buff.length);
	}

	@Override
	public int decryptData(byte[] buff, int start, int len) throws ZipException {
		if (this.aesEngine == null) {
			throw new ZipException("Please initialize first!");
		}
  
		try {
			for (int i = start ; i < (start + len) ; i += ZipConstants.AES_BLOCK_SIZE) {
				this.loopCount = (i + ZipConstants.AES_BLOCK_SIZE <= (start + len)) ? 
						ZipConstants.AES_BLOCK_SIZE : ((start + len) - i);
				
				this.macBasedPRF.update(buff, i, this.loopCount);
				this.iv = RawUtils.prepareAESBuffer(this.nonce);
				this.aesEngine.processBlock(this.iv, this.countBlock);
				
				for (int j = 0 ; j < this.loopCount ; j++) {
					buff[i + j] = (byte)(buff[i + j] ^ this.countBlock[j]);
				}
				
				this.nonce++;
			}
			
			return len;
		} catch (Exception e) {
			if (e instanceof ZipException) {
				throw (ZipException)e;
			} else {
				throw new ZipException(e);
			}
		}
	}
	
	public byte[] calculateAuthenticationBytes() {
		return this.macBasedPRF.doFinal();
	}

	/**
	 * @return the saltLength
	 */
	public int getSaltLength() {
		return saltLength;
	}

	/**
	 * @return the storedMac
	 */
	public byte[] getStoredMac() {
		return storedMac;
	}

	/**
	 * @param storedMac the storedMac to set
	 */
	public void setStoredMac(byte[] storedMac) {
		this.storedMac = storedMac;
	}
}
