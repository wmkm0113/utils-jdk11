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
package com.nervousync.commons.zip.crypto.impl.aes;

import java.util.Arrays;
import java.util.Random;

import com.nervousync.commons.core.Globals;
import com.nervousync.commons.core.zip.ZipConstants;
import com.nervousync.commons.zip.crypto.PBKDF2.MacBasedPRF;
import com.nervousync.commons.zip.crypto.PBKDF2.PBKDF2Options;
import com.nervousync.commons.zip.engine.AESEngine;
import com.nervousync.commons.zip.engine.PBKDF2.PBKDF2Engine;
import com.nervousync.exceptions.zip.ZipException;
import com.nervousync.utils.RawUtils;

/**
 * AES Crypto
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Dec 2, 2017 11:16:18 AM $
 */
public class AESCrypto {

	/**
	 * Salt data array
	 */
	private byte[] saltBytes;
	
	/**
	 * key length
	 */
	private int keyLength;
	/**
	 * mac length
	 */
	private int macLength;
	/**
	 * salt length
	 */
	int saltLength;

	/**
	 * current password bytes
	 */
	byte[] derivedPasswordVerifier = null;

	/**
	 * nonce
	 */
	int nonce = 1;
	/**
	 * loop count
	 */
	int loopCount = 0;
	
	/**
	 * iv bytes
	 */
	byte[] iv = null;
	/**
	 * count block bytes
	 */
	byte[] countBlock = null;

	/**
	 * AES engine
	 */
	AESEngine aesEngine = null;
	/**
	 * MacBasedPRF instance
	 */
	MacBasedPRF macBasedPRF = null;

	/**
	 * Verify given password
	 * @param aesStrength		AES key strength
	 * @param salt				salt bytes
	 * @param password			password 
	 * @param passwordBytes		password bytes
	 * @return	verify result
	 */
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

	/**
	 * Prepare initialize
	 * @param aesStrength	AES key strength
	 */
	void preInit(int aesStrength) {
		if (aesStrength != ZipConstants.AES_STRENGTH_128
				&& aesStrength != ZipConstants.AES_STRENGTH_192
				&& aesStrength != ZipConstants.AES_STRENGTH_256) {
			throw new ZipException("Invalid key strength in AES encryptor constructor");
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
	
	/**
	 * Initialize by given password
	 * @param password	password
	 */
	void init(char[] password) {
		if (password == null || password.length == 0) {
			throw new ZipException("Password is null or empty");
		}
		this.generateSalt();
		this.initCrypto(password);
	}
	
	/**
	 * Initialize by given password and salt
	 * @param salt			salt bytes
	 * @param password		password char arrays
	 */
	void init(byte[] salt, char[] password) {
		if (password == null || password.length == 0) {
			throw new ZipException("Password is null or empty");
		}
		this.saltBytes = salt;
		this.initCrypto(password);
	}

	void processData(byte[] buff, int index) {
		this.iv = RawUtils.prepareAESBuffer(this.nonce);
		this.aesEngine.processBlock(this.iv, this.countBlock);

		for (int j = 0 ; j < this.loopCount ; j++) {
			buff[index + j] = (byte)(buff[index + j] ^ this.countBlock[j]);
		}
		this.nonce++;
	}
	
	/**
	 * Derive key
	 * @param salt			salt bytes
	 * @param password		password
	 * @param dkLen			length
	 * @return				processed data bytes
	 */
	private byte[] deriveKey(byte[] salt, char[] password, int dkLen) {
		PBKDF2Options options = new PBKDF2Options("HmacSHA1", "ISO-8859-1", salt, 1000);
		PBKDF2Engine engine = new PBKDF2Engine(options);
		return engine.deriveKey(password, dkLen);
	}
	
	/**
	 * Verify given password
	 * @param password	password
	 * @return	verify result
	 */
	boolean verifyPassword(byte[] password) {
		if (this.derivedPasswordVerifier == null) {
			throw new ZipException("Invalid derived password verifier!");
		}

		return Arrays.equals(password, this.derivedPasswordVerifier);
	}
	
	/**
	 * Initialize cryptor
	 * @param password	password
	 */
	private void initCrypto(char[] password) {
		byte[] keyBytes = this.deriveKey(this.saltBytes, password, 
				this.keyLength + this.macLength + ZipConstants.PASSWORD_VERIFIER_LENGTH);
		
		if (keyBytes.length != (this.keyLength + this.macLength + ZipConstants.PASSWORD_VERIFIER_LENGTH)) {
			throw new ZipException("Invalid derived key!");
		}

		byte[] aesKey = new byte[this.keyLength];
		byte[] macKey = new byte[this.macLength];
		this.derivedPasswordVerifier = new byte[ZipConstants.PASSWORD_VERIFIER_LENGTH];
		
		System.arraycopy(keyBytes, 0, aesKey, 0, this.keyLength);
		System.arraycopy(keyBytes, this.keyLength, macKey, 0, this.macLength);
		System.arraycopy(keyBytes, (this.keyLength + this.macLength), 
				this.derivedPasswordVerifier, 0, ZipConstants.PASSWORD_VERIFIER_LENGTH);
		
		this.aesEngine = new AESEngine(aesKey);
		this.macBasedPRF = new MacBasedPRF("HmacSHA1");
		this.macBasedPRF.init(macKey);
	}
	
	/**
	 * Generate salt data
	 * @throws ZipException if salt length was invalid
	 */
	private void generateSalt() throws ZipException {
		int rounds;
		
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
			this.saltBytes[i * 4] = (byte)(temp >> 24);
			this.saltBytes[1 + i * 4] = (byte)(temp >> 16);
			this.saltBytes[2 + i * 4] = (byte)(temp >> 8);
			this.saltBytes[3 + i * 4] = (byte)temp;
		}
	}
}
