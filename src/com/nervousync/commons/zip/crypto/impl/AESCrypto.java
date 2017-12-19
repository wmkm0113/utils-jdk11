/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervous Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervous Studio.
 */
package com.nervousync.commons.zip.crypto.impl;

import java.util.Arrays;
import java.util.Random;

import com.nervousync.commons.core.Globals;
import com.nervousync.commons.zip.core.ZipConstants;
import com.nervousync.commons.zip.crypto.PBKDF2.MacBasedPRF;
import com.nervousync.commons.zip.crypto.PBKDF2.PBKDF2Options;
import com.nervousync.commons.zip.engine.AESEngine;
import com.nervousync.commons.zip.engine.PBKDF2.PBKDF2Engine;
import com.nervousync.exceptions.zip.ZipException;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Dec 2, 2017 11:16:18 AM $
 */
public class AESCrypto {

	private byte[] saltBytes;
	
	protected int keyLength;
	protected int macLength;
	protected int saltLength;
	
	protected byte[] aesKey = null;
	protected byte[] macKey = null;
	protected byte[] derviedPasswordVerifier = null;

	protected int nonce = 1;
	protected int loopCount = 0;
	
	protected byte[] iv = null;
	protected byte[] countBlock = null;

	protected AESEngine aesEngine = null;
	protected MacBasedPRF macBasedPRF = null;
	
	protected void preInit(int aesStrength) {
		if (aesStrength != ZipConstants.AES_STRENGTH_128
				&& aesStrength != ZipConstants.AES_STRENGTH_192
				&& aesStrength != ZipConstants.AES_STRENGTH_256) {
			throw new ZipException("Invalid key strength in AES encrypter constructor");
		}
		
		this.iv = new byte[ZipConstants.AES_BLOCK_SIZE];
		this.countBlock = new byte[ZipConstants.AES_BLOCK_SIZE];
		
		switch (aesStrength) {
		case ZipConstants.AES_STRENGTH_128:
			this.keyLength = 16;
			this.macLength = 16;
			this.saltLength = 8;
			break;
		case ZipConstants.AES_STRENGTH_192:
			this.keyLength = 24;
			this.macLength = 24;
			this.saltLength = 12;
			break;
		case ZipConstants.AES_STRENGTH_256:
			this.keyLength = 32;
			this.macLength = 32;
			this.saltLength = 16;
			break;
			default:
				throw new ZipException("Invalid aes key strength!");
		}
	}
	
	protected void init(char[] password) {
		if (password == null || password.length == 0) {
			throw new ZipException("Password is null or empty");
		}
		this.generateSalt();
		this.initCrypto(password);
	}
	
	protected void init(byte[] salt, char[] password) {
		if (password == null || password.length == 0) {
			throw new ZipException("Password is null or empty");
		}
		this.saltBytes = salt;
		this.initCrypto(password);
	}
	
	private void initCrypto(char[] password) {
		byte[] keyBytes = this.deriveKey(this.saltBytes, password, 
				this.keyLength + this.macLength + ZipConstants.PASSWORD_VERIFIER_LENGTH);
		
		if (keyBytes == null || keyBytes.length != 
				(this.keyLength + this.macLength + ZipConstants.PASSWORD_VERIFIER_LENGTH)) {
			throw new ZipException("Invalid derived key!");
		}

		this.aesKey = new byte[this.keyLength];
		this.macKey = new byte[this.macLength];
		this.derviedPasswordVerifier = new byte[ZipConstants.PASSWORD_VERIFIER_LENGTH];
		
		System.arraycopy(keyBytes, 0, this.aesKey, 0, this.keyLength);
		System.arraycopy(keyBytes, this.keyLength, this.macKey, 0, this.macLength);
		System.arraycopy(keyBytes, (this.keyLength + this.macLength), 
				this.derviedPasswordVerifier, 0, ZipConstants.PASSWORD_VERIFIER_LENGTH);
		
		this.aesEngine = new AESEngine(this.aesKey);
		this.macBasedPRF = new MacBasedPRF("HmacSHA1");
		this.macBasedPRF.init(this.macKey);
	}
	
	protected byte[] deriveKey(byte[] salt, char[] password, int dkLen) throws ZipException {
		try {
			PBKDF2Options options = new PBKDF2Options("HmacSHA1", "ISO-8859-1", salt, 1000);
			PBKDF2Engine engine = new PBKDF2Engine(options);
			return engine.deriveKey(password, dkLen);
		} catch (Exception e) {
			throw new ZipException(e);
		}
	}
	
	protected boolean verifyPassword(byte[] password) {
		if (this.derviedPasswordVerifier == null) {
			throw new ZipException("Invalid dervied password verifier!");
		}

		return Arrays.equals(password, this.derviedPasswordVerifier);
	}
	
	public static boolean verifyPassword(int aesStrength, byte[] salt, 
			char[] password, byte[] passwordBytes) {
		if (password == null || password.length == 0 || passwordBytes == null || passwordBytes.length == 0) {
			return Globals.DEFAULT_VALUE_BOOLEAN;
		}
		AESCrypto aesCrypto = new AESCrypto();
		aesCrypto.preInit(aesStrength);
		aesCrypto.init(salt, password);
		return aesCrypto.verifyPassword(passwordBytes);
	}

	/**
	 * @return the saltBytes
	 */
	public byte[] getSaltBytes() {
		return saltBytes;
	}
	
	private void generateSalt() throws ZipException {
		int rounds = 0;
		
		if (this.saltLength == 8) {
			rounds = 2;
		} else if (this.saltLength == 12) {
			rounds = 3;
		} else if (this.saltLength == 16) {
			rounds = 4;
		} else {
			throw new ZipException("Invalid salt size!");
		}
		
		this.saltBytes = new byte[this.saltLength];
		for (int i = 0 ; i < rounds ; i++) {
			Random random = new Random();
			int temp = random.nextInt();
			this.saltBytes[0 + i * 4] = (byte)(temp >> 24);
			this.saltBytes[1 + i * 4] = (byte)(temp >> 16);
			this.saltBytes[2 + i * 4] = (byte)(temp >> 8);
			this.saltBytes[3 + i * 4] = (byte)temp;
		}
	}
}
