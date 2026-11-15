/*
 * Licensed to the Nervousync Studio (NSYC) under one or more
 * contributor license agreements. See the NOTICE file distributed with
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
package org.nervousync.zip.crypto.impl.aes;

import org.nervousync.archive.exceptions.ArchiveException;
import org.nervousync.exceptions.zip.ZipException;
import org.nervousync.archive.zip.crypto.ZipCrypto;
import org.nervousync.commons.Globals;
import org.nervousync.commons.zip.ZipGlobals;
import org.nervousync.exceptions.crypto.CryptoException;
import org.nervousync.exceptions.utils.DataInvalidException;
import org.nervousync.security.CryptoAdaptor;
import org.nervousync.utils.commons.RawUtils;
import org.nervousync.utils.security.DigestUtils;

import java.nio.ByteOrder;
import java.util.Arrays;

/**
 * AES Crypto
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Dec 2, 2017 11:16:18 $
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
	CryptoAdaptor macBasedPRF = null;
//	private final boolean forEncryption;

	/**
	 * Instantiates a new Aes crypto.
	 */
	protected AESCrypto() {
	}

	/**
	 * Verify given password
	 *
	 * @param aesStrength   AES key strength
	 * @param salt          salt bytes
	 * @param password      password
	 * @param passwordBytes password bytes
	 * @return verify result
	 */
	public static boolean verifyPassword(final int aesStrength, final byte[] salt,
	                                     final char[] password, final byte[] passwordBytes) throws ZipException {
		if (password == null || password.length == 0 || passwordBytes == null || passwordBytes.length == 0) {
			return Boolean.FALSE;
		}
		AESCrypto aesCrypto = new AESCrypto();
		aesCrypto.preInit(aesStrength);
		aesCrypto.init(salt, password);
		return aesCrypto.verifyPassword(passwordBytes);
	}

	/**
	 * Get salt bytes byte [ ].
	 *
	 * @return the saltBytes
	 */
	public byte[] getSaltBytes() {
		return saltBytes == null ? new byte[0] : saltBytes.clone();
	}

	/**
	 * Prepare initializer
	 *
	 * @param aesStrength AES key strength
	 */
	void preInit(final int aesStrength) throws ZipException {
		this.iv = new byte[ZipGlobals.AES_BLOCK_SIZE];
		this.countBlock = new byte[ZipGlobals.AES_BLOCK_SIZE];

		switch (aesStrength) {
			case ZipGlobals.AES_STRENGTH_128:
				this.keyLength = 16;
				this.macLength = 16;
				this.saltLength = 8;
				break;
			case ZipGlobals.AES_STRENGTH_192:
				this.keyLength = 24;
				this.macLength = 24;
				this.saltLength = 12;
				break;
			case ZipGlobals.AES_STRENGTH_256:
				this.keyLength = 32;
				this.macLength = 32;
				this.saltLength = 16;
				break;
			default:
				throw new ZipException(0x0000001B0005L);
		}
	}

	/**
	 * Initialize by given password
	 *
	 * @param password password
	 * @throws ZipException the zip exception
	 */
	void init(final char[] password) throws ZipException {
		if (password == null || password.length == 0) {
			throw new ZipException(0x0000001B0006L);
		}
		this.generateSalt();
		try {
			this.initCrypto(password);
		} catch (CryptoException e) {
			throw new ZipException(0x0000001B000AL, e);
		}
	}

	/**
	 * Initialize by given password and salt
	 *
	 * @param salt     salt bytes
	 * @param password password char arrays
	 * @throws ZipException the zip exception
	 */
	void init(final byte[] salt, final char[] password) throws ZipException {
		if (password == null || password.length == 0) {
			throw new ZipException(0x0000001B0006L);
		}
		this.saltBytes = salt == null ? new byte[0] : salt.clone();
		try {
			this.initCrypto(password);
		} catch (CryptoException e) {
			throw new ZipException(0x0000001B000AL, e);
		}
	}

	/**
	 * Process data.
	 *
	 * @param buff  the buff
	 * @param index the index
	 */
	void processData(final byte[] buff, final int index) throws ZipException, DataInvalidException {
		this.iv = new byte[16];
		RawUtils.writeInt(this.iv, ByteOrder.LITTLE_ENDIAN, this.nonce);
		this.aesEngine.processBlock(this.iv, this.countBlock);

		for (int j = 0; j < this.loopCount; j++) {
			buff[index + j] = (byte) (buff[index + j] ^ this.countBlock[j]);
		}
		this.nonce++;
	}

	/**
	 * Verify given password
	 *
	 * @param password password
	 * @return verify result
	 */
	boolean verifyPassword(final byte[] password) throws ZipException {
		if (this.derivedPasswordVerifier == null) {
			throw new ZipException(0x0000001B0007L);
		}

		return Arrays.equals(password, this.derivedPasswordVerifier);
	}

	/**
	 * Initialize crypto
	 *
	 * @param password password
	 */
	private void initCrypto(final char[] password) throws CryptoException, ZipException {
		byte[] keyBytes;
		try {
			keyBytes = ZipCrypto.deriveKey(this.saltBytes, ZipCrypto.encodePassword(password, true),
					this.keyLength + this.macLength + ZipGlobals.PASSWORD_VERIFIER_LENGTH);
		} catch (DataInvalidException | ArchiveException e) {
			throw new ZipException(0x0000001B000AL, e);
		}

		if (keyBytes.length != (this.keyLength + this.macLength + ZipGlobals.PASSWORD_VERIFIER_LENGTH)) {
			throw new ZipException(0x0000001B0008L);
		}

		byte[] aesKey = new byte[this.keyLength];
		byte[] macKey = new byte[this.macLength];
		this.derivedPasswordVerifier = new byte[ZipGlobals.PASSWORD_VERIFIER_LENGTH];

		System.arraycopy(keyBytes, Globals.INITIALIZE_INT_VALUE, aesKey, Globals.INITIALIZE_INT_VALUE, this.keyLength);
		System.arraycopy(keyBytes, this.keyLength, macKey, Globals.INITIALIZE_INT_VALUE, this.macLength);
		System.arraycopy(keyBytes, (this.keyLength + this.macLength),
				this.derivedPasswordVerifier, Globals.INITIALIZE_INT_VALUE, ZipGlobals.PASSWORD_VERIFIER_LENGTH);

		this.aesEngine = new AESEngine(aesKey);
		this.macBasedPRF = DigestUtils.HmacSHA1(macKey);
	}

	/**
	 * Generate salt data
	 *
	 * @throws ZipException if salt length was invalid
	 */
	private void generateSalt() throws ZipException {
		int rounds = this.saltLength / 4;
		if (rounds < 2 || rounds > 4) {
			throw new ZipException(0x0000001B0009);
		}

		this.saltBytes = new byte[this.saltLength];
		for (int i = 0; i < rounds; i++) {
			int temp = Globals.random();
			this.saltBytes[i * 4] = (byte) (temp >> 24);
			this.saltBytes[1 + i * 4] = (byte) (temp >> 16);
			this.saltBytes[2 + i * 4] = (byte) (temp >> 8);
			this.saltBytes[3 + i * 4] = (byte) temp;
		}
	}
}
