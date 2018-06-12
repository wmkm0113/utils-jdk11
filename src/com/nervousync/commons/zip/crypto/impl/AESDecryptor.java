/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervousync Studio.
 */
package com.nervousync.commons.zip.crypto.impl;

import com.nervousync.commons.core.zip.ZipConstants;
import com.nervousync.commons.zip.crypto.Decryptor;
import com.nervousync.commons.zip.models.header.LocalFileHeader;
import com.nervousync.commons.zip.operator.RawOperator;
import com.nervousync.exceptions.zip.ZipException;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Dec 2, 2017 10:55:30 AM $
 */
public class AESDecryptor extends AESCrypto implements Decryptor {

	private LocalFileHeader localFileHeader = null;
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
				this.iv = RawOperator.prepareAESBuffer(this.nonce);
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
