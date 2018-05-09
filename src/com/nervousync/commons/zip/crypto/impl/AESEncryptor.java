/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervousync Studio.
 */
package com.nervousync.commons.zip.crypto.impl;

import com.nervousync.commons.core.Globals;
import com.nervousync.commons.zip.core.ZipConstants;
import com.nervousync.commons.zip.crypto.Encryptor;
import com.nervousync.commons.zip.operator.RawOperator;
import com.nervousync.exceptions.zip.ZipException;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Nov 30, 2017 3:42:11 PM $
 */
public class AESEncryptor extends AESCrypto implements Encryptor {
	
	private boolean finished = Globals.DEFAULT_VALUE_BOOLEAN;
	
	public AESEncryptor(char[] password, int aesStrength) throws ZipException {
		super.preInit(aesStrength);
		this.init(password);
	}

	@Override
	public int encryptData(byte[] buff) throws ZipException {
		if (buff == null) {
			throw new ZipException("input bytes are null, cannot perform AES encrpytion");
		}
		return this.encryptData(buff, 0, buff.length);
	}

	@Override
	public int encryptData(byte[] buff, int start, int len) throws ZipException {
		if (this.finished) {
			throw new ZipException("AES Encrypter is in finished state (A non 16 byte block has already been passed to encrypter)");
		}
		
		if (len % 16 != 0) {
			this.finished = true;
		}
		
		for (int i = start ; i < (start + len) ; i += ZipConstants.AES_BLOCK_SIZE) {
			this.loopCount = (i + ZipConstants.AES_BLOCK_SIZE <= (start + len)) ? 
					ZipConstants.AES_BLOCK_SIZE : ((start + len) - i);
			this.iv = RawOperator.prepareAESBuffer(this.nonce);
			this.aesEngine.processBlock(this.iv, this.countBlock);
			
			for (int j = 0 ; j < this.loopCount ; j++) {
				buff[i + j] = (byte)(buff[i + j] ^ this.countBlock[j]);
			}
			
			this.macBasedPRF.update(buff, i, this.loopCount);
			this.nonce++;
		}
		
		return len;
	}
	
	public byte[] getFinalMac() {
		byte[] rawMacBytes = this.macBasedPRF.doFinal();
		byte[] macBytes = new byte[10];
		System.arraycopy(rawMacBytes, 0, macBytes, 0, 10);
		return macBytes;
	}
	
	/**
	 * @return the derviedPasswordVerifier
	 */
	public byte[] getDerviedPasswordVerifier() {
		return derviedPasswordVerifier;
	}
}
