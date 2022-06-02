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
package org.nervousync.utils;

import java.io.*;
import java.security.*;
import java.util.*;

import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.nervousync.security.SecureProvider;
import org.nervousync.security.config.CRCConfig;
import org.nervousync.security.config.CipherConfig;
import org.nervousync.security.digest.impl.*;
import org.nervousync.security.crypto.BaseCryptoProvider;
import org.nervousync.security.crypto.SymmetricCryptoProvider;
import org.nervousync.security.crypto.impl.*;
import org.nervousync.enumerations.crypto.CryptoMode;
import org.nervousync.exceptions.crypto.CryptoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.nervousync.commons.core.Globals;

/**
 * Security Utils
 * <p>
 * Implements:
 * MD5 Encode
 * SHA Encode
 * DES Encrypt/Decrypt
 * RSA Encrypt/Decrypt
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: Jan 13, 2010 11:23:13 AM $
 */
public final class SecurityUtils implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 2929476536772097530L;

	/**
	 * Log for SecurityUtils class
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(SecurityUtils.class);
	private static final Map<String, CRCConfig> REGISTERED_CRC_CONFIG = new HashMap<>();

	static {
		Security.addProvider(new BouncyCastleProvider());
		CRC();
		LOGGER.info("Registered CRC config: {}",
				String.join(",", new ArrayList<>(REGISTERED_CRC_CONFIG.keySet())));
	}

	private SecurityUtils() {
	}

	/**
	 * Register CRC algorithm
	 *
	 * @param algorithm Algorithm name
	 * @param crcConfig CRC config
	 */
	public static void registerConfig(String algorithm, CRCConfig crcConfig) {
		if (StringUtils.isEmpty(algorithm) || crcConfig == null) {
			LOGGER.error("Algorithm or crcConfig is null! ");
			return;
		}
		if (crcConfig.getBit() > 32) {
			LOGGER.error("Cannot calculate CRC value lager than 32 bit");
			return;
		}
		if (REGISTERED_CRC_CONFIG.containsKey(algorithm)) {
			LOGGER.warn("Algorithm name: " + algorithm + " was exists, override exists config!");
		}
		REGISTERED_CRC_CONFIG.put(algorithm, crcConfig);
	}

	/**
	 * Registered crc list.
	 *
	 * @return the list
	 */
	public static List<String> registeredCRC() {
		return new ArrayList<>(REGISTERED_CRC_CONFIG.keySet());
	}

	/**
	 * Initialize CRC provider
	 *
	 * @param algorithm CRC algorithm
	 * @return Initialized crc provider
	 * @throws CryptoException CRC algorithm not found
	 */
	public static SecureProvider CRC(String algorithm) throws CryptoException {
		if (REGISTERED_CRC_CONFIG.containsKey(algorithm)) {
			return new CRCDigestProviderImpl(REGISTERED_CRC_CONFIG.get(algorithm));
		}
		throw new CryptoException("Unknown algorithm: " + algorithm);
	}

	/**
	 * Crc config optional.
	 *
	 * @param algorithm the algorithm
	 * @return the optional
	 */
	public static Optional<CRCConfig> crcConfig(String algorithm) {
		return Optional.ofNullable(REGISTERED_CRC_CONFIG.get(algorithm));
	}

	/*
	 * Digest Methods
	 */

	/**
	 * MD5 Digest Provider
	 *
	 * @return Initialized provider
	 * @throws CryptoException Cipher transformation not found
	 */
	public static SecureProvider MD5() throws CryptoException {
		return new MD5DigestProviderImpl();
	}

	/**
	 * Calculate MD5 value of given source object
	 *
	 * @param source Input source
	 * @return Calculate result or zero length array if process has error
	 */
	public static byte[] MD5(Object source) {
		try {
			return digest(source, MD5());
		} catch (CryptoException e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Calculate MD5 value error! ", e);
			}
			return new byte[0];
		}
	}

	/**
	 * HmacMD5 Digest Provider
	 *
	 * @param keyBytes Hmac key bytes
	 * @return Initialized provider
	 * @throws CryptoException Cipher transformation not found
	 */
	public static SecureProvider HmacMD5(byte[] keyBytes) throws CryptoException {
		return new MD5DigestProviderImpl(keyBytes);
	}

	/**
	 * Calculate HmacMD5 value of given source object
	 *
	 * @param keyBytes Hmac key bytes
	 * @param source   Input source
	 * @return Calculate result or zero length array if process has error
	 */
	public static byte[] HmacMD5(byte[] keyBytes, Object source) {
		try {
			return digest(source, HmacMD5(keyBytes));
		} catch (CryptoException e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Calculate HmacMD5 value error! ", e);
			}
			return new byte[0];
		}
	}

	/**
	 * SHA1 Digest Provider
	 *
	 * @return Initialized provider
	 * @throws CryptoException Cipher transformation not found
	 */
	@Deprecated
	public static SecureProvider SHA1() throws CryptoException {
		return new SHA1DigestProviderImpl();
	}

	/**
	 * Calculate SHA1 value of given source object
	 *
	 * @param source Input source
	 * @return Calculate result or zero length array if process has error
	 */
	@Deprecated
	public static byte[] SHA1(Object source) {
		try {
			return digest(source, SHA1());
		} catch (CryptoException e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Calculate SHA1 value error! ", e);
			}
			return new byte[0];
		}
	}

	/**
	 * HmacSHA1 Digest Provider
	 *
	 * @param keyBytes Hmac key bytes
	 * @return Initialized provider
	 * @throws CryptoException Cipher transformation not found
	 */
	public static SecureProvider HmacSHA1(byte[] keyBytes) throws CryptoException {
		return new SHA1DigestProviderImpl(keyBytes);
	}

	/**
	 * Calculate HmacSHA1 value of given source object
	 *
	 * @param keyBytes Hmac key bytes
	 * @param source   Input source
	 * @return Calculate result or zero length array if process has error
	 */
	public static byte[] HmacSHA1(byte[] keyBytes, Object source) {
		try {
			return digest(source, HmacSHA1(keyBytes));
		} catch (CryptoException e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Calculate HmacMD5 value error! ", e);
			}
			return new byte[0];
		}
	}

	/**
	 * SHA-224 Digest Provider
	 *
	 * @return Initialized provider
	 * @throws CryptoException Cipher transformation not found
	 */
	public static SecureProvider SHA224() throws CryptoException {
		return new SHA2DigestProviderImpl("SHA-224", new byte[0]);
	}

	/**
	 * Calculate SHA-224 value of given source object
	 *
	 * @param source Input source
	 * @return Calculate result or zero length array if process has error
	 */
	public static byte[] SHA224(Object source) {
		try {
			return digest(source, SHA256());
		} catch (CryptoException e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Calculate HmacMD5 value error! ", e);
			}
			return new byte[0];
		}
	}

	/**
	 * HmacSHA224 Digest Provider
	 *
	 * @param keyBytes Hmac key bytes
	 * @return Initialized provider
	 * @throws CryptoException Cipher transformation not found
	 */
	public static SecureProvider HmacSHA224(byte[] keyBytes) throws CryptoException {
		return new SHA2DigestProviderImpl("SHA-224/HMAC", keyBytes);
	}

	/**
	 * Calculate HmacSHA224 value of given source object
	 *
	 * @param keyBytes Hmac key bytes
	 * @param source   Input source
	 * @return Calculate result or zero length array if process has error
	 */
	public static byte[] HmacSHA224(byte[] keyBytes, Object source) {
		try {
			return digest(source, HmacSHA256(keyBytes));
		} catch (CryptoException e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Calculate HmacMD5 value error! ", e);
			}
			return new byte[0];
		}
	}

	/**
	 * SHA-256 Digest Provider
	 *
	 * @return Initialized provider
	 * @throws CryptoException Cipher transformation not found
	 */
	public static SecureProvider SHA256() throws CryptoException {
		return new SHA2DigestProviderImpl("SHA-256", new byte[0]);
	}

	/**
	 * Calculate SHA-256 value of given source object
	 *
	 * @param source Input source
	 * @return Calculate result or zero length array if process has error
	 */
	public static byte[] SHA256(Object source) {
		try {
			return digest(source, SHA256());
		} catch (CryptoException e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Calculate HmacMD5 value error! ", e);
			}
			return new byte[0];
		}
	}

	/**
	 * HmacSHA256 Digest Provider
	 *
	 * @param keyBytes Hmac key bytes
	 * @return Initialized provider
	 * @throws CryptoException Cipher transformation not found
	 */
	public static SecureProvider HmacSHA256(byte[] keyBytes) throws CryptoException {
		return new SHA2DigestProviderImpl("SHA-256/HMAC", keyBytes);
	}

	/**
	 * Calculate HmacSHA256 value of given source object
	 *
	 * @param keyBytes Hmac key bytes
	 * @param source   Input source
	 * @return Calculate result or zero length array if process has error
	 */
	public static byte[] HmacSHA256(byte[] keyBytes, Object source) {
		try {
			return digest(source, HmacSHA256(keyBytes));
		} catch (CryptoException e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Calculate HmacMD5 value error! ", e);
			}
			return new byte[0];
		}
	}

	/**
	 * SHA-384 Digest Provider
	 *
	 * @return Initialized provider
	 * @throws CryptoException Cipher transformation not found
	 */
	public static SecureProvider SHA384() throws CryptoException {
		return new SHA2DigestProviderImpl("SHA-384", new byte[0]);
	}

	/**
	 * Calculate SHA-384 value of given source object
	 *
	 * @param source Input source
	 * @return Calculate result or zero length array if process has error
	 */
	public static byte[] SHA384(Object source) {
		try {
			return digest(source, SHA384());
		} catch (CryptoException e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Calculate HmacMD5 value error! ", e);
			}
			return new byte[0];
		}
	}

	/**
	 * HmacSHA384 Digest Provider
	 *
	 * @param keyBytes Hmac key bytes
	 * @return Initialized provider
	 * @throws CryptoException Cipher transformation not found
	 */
	public static SecureProvider HmacSHA384(byte[] keyBytes) throws CryptoException {
		return new SHA2DigestProviderImpl("SHA-384/HMAC", keyBytes);
	}

	/**
	 * Calculate HmacSHA384 value of given source object
	 *
	 * @param keyBytes Hmac key bytes
	 * @param source   Input source
	 * @return Calculate result or zero length array if process has error
	 */
	public static byte[] HmacSHA384(byte[] keyBytes, Object source) {
		try {
			return digest(source, HmacSHA384(keyBytes));
		} catch (CryptoException e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Calculate HmacMD5 value error! ", e);
			}
			return new byte[0];
		}
	}

	/**
	 * SHA-512 Digest Provider
	 *
	 * @return Initialized provider
	 * @throws CryptoException Cipher transformation not found
	 */
	public static SecureProvider SHA512() throws CryptoException {
		return new SHA2DigestProviderImpl("SHA-512", new byte[0]);
	}

	/**
	 * Calculate SHA-512 value of given source object
	 *
	 * @param source Input source
	 * @return Calculate result or zero length array if process has error
	 */
	public static byte[] SHA512(Object source) {
		try {
			return digest(source, SHA512());
		} catch (CryptoException e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Calculate HmacMD5 value error! ", e);
			}
			return new byte[0];
		}
	}

	/**
	 * HmacSHA512 Digest Provider
	 *
	 * @param keyBytes Hmac key bytes
	 * @return Initialized provider
	 * @throws CryptoException Cipher transformation not found
	 */
	public static SecureProvider HmacSHA512(byte[] keyBytes) throws CryptoException {
		return new SHA2DigestProviderImpl("SHA-512/HMAC", keyBytes);
	}

	/**
	 * Calculate HmacSHA512 value of given source object
	 *
	 * @param keyBytes Hmac key bytes
	 * @param source   Input source
	 * @return Calculate result or zero length array if process has error
	 */
	public static byte[] HmacSHA512(byte[] keyBytes, Object source) {
		try {
			return digest(source, HmacSHA512(keyBytes));
		} catch (CryptoException e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Calculate HmacMD5 value error! ", e);
			}
			return new byte[0];
		}
	}

	/**
	 * SHA-512/224 Digest Provider
	 *
	 * @return Initialized provider
	 * @throws CryptoException Cipher transformation not found
	 */
	public static SecureProvider SHA512_224() throws CryptoException {
		return new SHA2DigestProviderImpl("SHA-512/224", new byte[0]);
	}

	/**
	 * Calculate SHA-512/224 value of given source object
	 *
	 * @param source Input source
	 * @return Calculate result or zero length array if process has error
	 */
	public static byte[] SHA512_224(Object source) {
		try {
			return digest(source, SHA512_224());
		} catch (CryptoException e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Calculate HmacMD5 value error! ", e);
			}
			return new byte[0];
		}
	}

	/**
	 * HmacSHA512/224 Digest Provider
	 *
	 * @param keyBytes Hmac key bytes
	 * @return Initialized provider
	 * @throws CryptoException Cipher transformation not found
	 */
	public static SecureProvider HmacSHA512_224(byte[] keyBytes) throws CryptoException {
		return new SHA2DigestProviderImpl("SHA-512/224/HMAC", keyBytes);
	}

	/**
	 * Calculate HmacSHA512/224 value of given source object
	 *
	 * @param keyBytes Hmac key bytes
	 * @param source   Input source
	 * @return Calculate result or zero length array if process has error
	 */
	public static byte[] HmacSHA512_224(byte[] keyBytes, Object source) {
		try {
			return digest(source, HmacSHA512_224(keyBytes));
		} catch (CryptoException e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Calculate HmacMD5 value error! ", e);
			}
			return new byte[0];
		}
	}

	/**
	 * SHA512-256 Digest Provider
	 *
	 * @return Initialized provider
	 * @throws CryptoException Cipher transformation not found
	 */
	public static SecureProvider SHA512_256() throws CryptoException {
		return new SHA2DigestProviderImpl("SHA-512/256", new byte[0]);
	}

	/**
	 * Calculate SHA-512/256 value of given source object
	 *
	 * @param source Input source
	 * @return Calculate result or zero length array if process has error
	 */
	public static byte[] SHA512_256(Object source) {
		try {
			return digest(source, SHA512_256());
		} catch (CryptoException e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Calculate HmacMD5 value error! ", e);
			}
			return new byte[0];
		}
	}

	/**
	 * HmacSHA512/256 Digest Provider
	 *
	 * @param keyBytes Hmac key bytes
	 * @return Initialized provider
	 * @throws CryptoException Cipher transformation not found
	 */
	public static SecureProvider HmacSHA512_256(byte[] keyBytes) throws CryptoException {
		return new SHA2DigestProviderImpl("SHA-512/256/HMAC", keyBytes);
	}

	/**
	 * Calculate HmacSHA512/256 value of given source object
	 *
	 * @param keyBytes Hmac key bytes
	 * @param source   Input source
	 * @return Calculate result or zero length array if process has error
	 */
	public static byte[] HmacSHA512_256(byte[] keyBytes, Object source) {
		try {
			return digest(source, HmacSHA512_256(keyBytes));
		} catch (CryptoException e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Calculate HmacMD5 value error! ", e);
			}
			return new byte[0];
		}
	}

	/**
	 * SHA3-224 Digest Provider
	 *
	 * @return Initialized provider
	 * @throws CryptoException Cipher transformation not found
	 */
	public static SecureProvider SHA3_224() throws CryptoException {
		return new SHA3DigestProviderImpl("SHA3-224");
	}

	/**
	 * Calculate SHA3-224 value of given source object
	 *
	 * @param source Input source
	 * @return Calculate result or zero length array if process has error
	 */
	public static byte[] SHA3_224(Object source) {
		try {
			return digest(source, SHA3_224());
		} catch (CryptoException e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Calculate HmacMD5 value error! ", e);
			}
			return new byte[0];
		}
	}

	/**
	 * HmacSHA3-224 Digest Provider
	 *
	 * @param keyBytes Hmac key bytes
	 * @return Initialized provider
	 * @throws CryptoException Cipher transformation not found
	 */
	public static SecureProvider HmacSHA3_224(byte[] keyBytes) throws CryptoException {
		return new SHA3DigestProviderImpl("SHA3-224/HMAC", keyBytes);
	}

	/**
	 * Calculate HmacSHA3-224 value of given source object
	 *
	 * @param keyBytes Hmac key bytes
	 * @param source   Input source
	 * @return Calculate result or zero length array if process has error
	 */
	public static byte[] HmacSHA3_224(byte[] keyBytes, Object source) {
		try {
			return digest(source, HmacSHA3_224(keyBytes));
		} catch (CryptoException e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Calculate HmacMD5 value error! ", e);
			}
			return new byte[0];
		}
	}

	/**
	 * SHA3-256 Digest Provider
	 *
	 * @return Initialized provider
	 * @throws CryptoException Cipher transformation not found
	 */
	public static SecureProvider SHA3_256() throws CryptoException {
		return new SHA3DigestProviderImpl("SHA3-256");
	}

	/**
	 * Calculate SHA3-256 value of given source object
	 *
	 * @param source Input source
	 * @return Calculate result or zero length array if process has error
	 */
	public static byte[] SHA3_256(Object source) {
		try {
			return digest(source, SHA3_256());
		} catch (CryptoException e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Calculate HmacMD5 value error! ", e);
			}
			return new byte[0];
		}
	}

	/**
	 * HmacSHA3-256 Digest Provider
	 *
	 * @param keyBytes Hmac key bytes
	 * @return Initialized provider
	 * @throws CryptoException Cipher transformation not found
	 */
	public static SecureProvider HmacSHA3_256(byte[] keyBytes) throws CryptoException {
		return new SHA3DigestProviderImpl("SHA3-256/HMAC", keyBytes);
	}

	/**
	 * Calculate HmacSHA3-256 value of given source object
	 *
	 * @param keyBytes Hmac key bytes
	 * @param source   Input source
	 * @return Calculate result or zero length array if process has error
	 */
	public static byte[] HmacSHA3_256(byte[] keyBytes, Object source) {
		try {
			return digest(source, HmacSHA3_256(keyBytes));
		} catch (CryptoException e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Calculate HmacMD5 value error! ", e);
			}
			return new byte[0];
		}
	}

	/**
	 * SHA3-384 Digest Provider
	 *
	 * @return Initialized provider
	 * @throws CryptoException Cipher transformation not found
	 */
	public static SecureProvider SHA3_384() throws CryptoException {
		return new SHA3DigestProviderImpl("SHA3-384");
	}

	/**
	 * Calculate SHA3-384 value of given source object
	 *
	 * @param source Input source
	 * @return Calculate result or zero length array if process has error
	 */
	public static byte[] SHA3_384(Object source) {
		try {
			return digest(source, SHA3_384());
		} catch (CryptoException e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Calculate HmacMD5 value error! ", e);
			}
			return new byte[0];
		}
	}

	/**
	 * HmacSHA3-384 Digest Provider
	 *
	 * @param keyBytes Hmac key bytes
	 * @return Initialized provider
	 * @throws CryptoException Cipher transformation not found
	 */
	public static SecureProvider HmacSHA3_384(byte[] keyBytes) throws CryptoException {
		return new SHA3DigestProviderImpl("SHA3-384/HMAC", keyBytes);
	}

	/**
	 * Calculate HmacSHA3-384 value of given source object
	 *
	 * @param keyBytes Hmac key bytes
	 * @param source   Input source
	 * @return Calculate result or zero length array if process has error
	 */
	public static byte[] HmacSHA3_384(byte[] keyBytes, Object source) {
		try {
			return digest(source, HmacSHA3_384(keyBytes));
		} catch (CryptoException e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Calculate HmacMD5 value error! ", e);
			}
			return new byte[0];
		}
	}

	/**
	 * SHA3-512 Digest Provider
	 *
	 * @return Initialized provider
	 * @throws CryptoException Cipher transformation not found
	 */
	public static SecureProvider SHA3_512() throws CryptoException {
		return new SHA3DigestProviderImpl("SHA3-512");
	}

	/**
	 * Calculate SHA3-512 value of given source object
	 *
	 * @param source Input source
	 * @return Calculate result or zero length array if process has error
	 */
	public static byte[] SHA3_512(Object source) {
		try {
			return digest(source, SHA3_512());
		} catch (CryptoException e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Calculate HmacMD5 value error! ", e);
			}
			return new byte[0];
		}
	}

	/**
	 * HmacSHA3-512 Digest Provider
	 *
	 * @param keyBytes Hmac key bytes
	 * @return Initialized provider
	 * @throws CryptoException Cipher transformation not found
	 */
	public static SecureProvider HmacSHA3_512(byte[] keyBytes) throws CryptoException {
		return new SHA3DigestProviderImpl("SHA3-512/HMAC", keyBytes);
	}

	/**
	 * Calculate HmacSHA3-512 value of given source object
	 *
	 * @param keyBytes Hmac key bytes
	 * @param source   Input source
	 * @return Calculate result or zero length array if process has error
	 */
	public static byte[] HmacSHA3_512(byte[] keyBytes, Object source) {
		try {
			return digest(source, HmacSHA3_512(keyBytes));
		} catch (CryptoException e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Calculate HmacMD5 value error! ", e);
			}
			return new byte[0];
		}
	}

	/**
	 * SHAKE128 Digest Provider
	 *
	 * @return Initialized provider
	 * @throws CryptoException Cipher transformation not found
	 */
	public static SecureProvider SHAKE128() throws CryptoException {
		return new SHA3DigestProviderImpl("SHAKE128");
	}

	/**
	 * Calculate SHAKE128 value of given source object
	 *
	 * @param source Input source
	 * @return Calculate result or zero length array if process has error
	 */
	public static byte[] SHAKE128(Object source) {
		try {
			return digest(source, SHAKE128());
		} catch (CryptoException e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Calculate HmacMD5 value error! ", e);
			}
			return new byte[0];
		}
	}

	/**
	 * SHAKE256 Digest Provider
	 *
	 * @return Initialized provider
	 * @throws CryptoException Cipher transformation not found
	 */
	public static SecureProvider SHAKE256() throws CryptoException {
		return new SHA3DigestProviderImpl("SHAKE256");
	}

	/**
	 * Calculate SHAKE256 value of given source object
	 *
	 * @param source Input source
	 * @return Calculate result or zero length array if process has error
	 */
	public static byte[] SHAKE256(Object source) {
		try {
			return digest(source, SHAKE256());
		} catch (CryptoException e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Calculate HmacMD5 value error! ", e);
			}
			return new byte[0];
		}
	}

	/**
	 * SM3 Digest Provider
	 *
	 * @return Initialized provider
	 * @throws CryptoException Cipher transformation not found
	 */
	public static SecureProvider SM3() throws CryptoException {
		return new SM3DigestProviderImpl();
	}

	/**
	 * Calculate SM3 value of given source object
	 *
	 * @param source Input source
	 * @return Calculate result or zero length array if process has error
	 */
	public static byte[] SM3(Object source) {
		try {
			return digest(source, SM3());
		} catch (CryptoException e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Calculate HmacMD5 value error! ", e);
			}
			return new byte[0];
		}
	}

	/**
	 * HmacSM3 Digest Provider
	 *
	 * @param keyBytes Hmac key bytes
	 * @return Initialized provider
	 * @throws CryptoException Cipher transformation not found
	 */
	public static SecureProvider HmacSM3(byte[] keyBytes) throws CryptoException {
		return new SM3DigestProviderImpl(keyBytes);
	}

	/**
	 * Calculate HmacSM3 value of given source object
	 *
	 * @param keyBytes Hmac key bytes
	 * @param source   Input source
	 * @return Calculate result or zero length array if process has error
	 */
	public static byte[] HmacSM3(byte[] keyBytes, Object source) {
		try {
			return digest(source, HmacSM3(keyBytes));
		} catch (CryptoException e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Calculate HmacMD5 value error! ", e);
			}
			return new byte[0];
		}
	}

	/*
	 *	Symmetric methods
	 */

	/**
	 * Initialize DES encryptor using default cipher mode and padding mode
	 *
	 * @param keyBytes DES key bytes
	 * @return Initialized encryptor
	 * @throws CryptoException Cipher transformation not found
	 */
	public static SecureProvider DESEncryptor(byte[] keyBytes) throws CryptoException {
		return DESEncryptor("CBC", "PKCS5Padding", keyBytes);
	}

	/**
	 * Initialize DES encryptor
	 *
	 * @param mode     Block cipher mode
	 * @param padding  Padding mode
	 * @param keyBytes DES key bytes
	 * @return Initialized encryptor
	 * @throws CryptoException Cipher transformation not found
	 */
	public static SecureProvider DESEncryptor(String mode, String padding, byte[] keyBytes) throws CryptoException {
		return new DESCryptoProviderImpl(new CipherConfig("DES", mode, padding), CryptoMode.ENCRYPT, keyBytes);
	}

	/**
	 * Initialize DES decryptor using default cipher mode and padding mode
	 *
	 * @param keyBytes DES key bytes
	 * @return Initialized decryptor
	 * @throws CryptoException Cipher transformation not found
	 */
	public static SecureProvider DESDecryptor(byte[] keyBytes) throws CryptoException {
		return DESDecryptor("CBC", "PKCS5Padding", keyBytes);
	}

	/**
	 * Initialize DES decryptor
	 *
	 * @param mode     Block cipher mode
	 * @param padding  Padding mode
	 * @param keyBytes DES key bytes
	 * @return Initialized decryptor
	 * @throws CryptoException Cipher transformation not found
	 */
	public static SecureProvider DESDecryptor(String mode, String padding, byte[] keyBytes) throws CryptoException {
		return new DESCryptoProviderImpl(new CipherConfig("DES", mode, padding), CryptoMode.DECRYPT, keyBytes);
	}

	/**
	 * Generate DES key
	 *
	 * @return Generated key bytes or 0 length byte array if process error
	 */
	public static byte[] DESKey() {
		try {
			return SymmetricCryptoProvider.generateKey("DES", Globals.DEFAULT_VALUE_INT, Globals.DEFAULT_VALUE_STRING);
		} catch (CryptoException e) {
			return new byte[0];
		}
	}

	/**
	 * Initialize TripleDES encryptor using default cipher mode and padding mode
	 *
	 * @param keyBytes TripleDES key bytes
	 * @return Initialized encryptor
	 * @throws CryptoException Cipher transformation not found
	 */
	public static SecureProvider TripleDESEncryptor(byte[] keyBytes) throws CryptoException {
		return TripleDESEncryptor("CBC", "PKCS5Padding", keyBytes);
	}

	/**
	 * Initialize TripleDES encryptor
	 *
	 * @param mode     Block cipher mode
	 * @param padding  Padding mode
	 * @param keyBytes TripleDES key bytes
	 * @return Initialized encryptor
	 * @throws CryptoException Cipher transformation not found
	 */
	public static SecureProvider TripleDESEncryptor(String mode, String padding, byte[] keyBytes)
			throws CryptoException {
		return new TripleDESCryptoProviderImpl(new CipherConfig("DESede", mode, padding),
				CryptoMode.ENCRYPT, keyBytes);
	}

	/**
	 * Initialize TripleDES decryptor using default cipher mode and padding mode
	 *
	 * @param keyBytes TripleDES key bytes
	 * @return Initialized decryptor
	 * @throws CryptoException Cipher transformation not found
	 */
	public static SecureProvider TripleDESDecryptor(byte[] keyBytes) throws CryptoException {
		return TripleDESDecryptor("CBC", "PKCS5Padding", keyBytes);
	}

	/**
	 * Initialize TripleDES decryptor
	 *
	 * @param mode     Block cipher mode
	 * @param padding  Padding mode
	 * @param keyBytes TripleDES key bytes
	 * @return Initialized decryptor
	 * @throws CryptoException Cipher transformation not found
	 */
	public static SecureProvider TripleDESDecryptor(String mode, String padding, byte[] keyBytes)
			throws CryptoException {
		return new TripleDESCryptoProviderImpl(new CipherConfig("DESede", mode, padding),
				CryptoMode.DECRYPT, keyBytes);
	}

	/**
	 * Generate TripleDES key
	 *
	 * @return Generated key bytes or 0 length byte array if process error
	 */
	public static byte[] TripleDESKey() {
		try {
			return SymmetricCryptoProvider.generateKey("DESede", Globals.DEFAULT_VALUE_INT, Globals.DEFAULT_VALUE_STRING);
		} catch (CryptoException e) {
			return new byte[0];
		}
	}

	/**
	 * Initialize SM4 encryptor using default cipher mode, padding mode and random algorithm
	 *
	 * @param keyBytes SM4 key bytes
	 * @return Initialized encryptor
	 * @throws CryptoException Cipher transformation not found
	 */
	public static SecureProvider SM4Encryptor(byte[] keyBytes) throws CryptoException {
		return SM4Encryptor("CBC", "NoPadding", keyBytes, "SHA1PRNG");
	}

	/**
	 * Initialize SM4 encryptor
	 *
	 * @param mode     Block cipher mode
	 * @param padding  Padding mode
	 * @param keyBytes SM4 key bytes
	 * @return Initialized encryptor
	 * @throws CryptoException Cipher transformation not found
	 */
	public static SecureProvider SM4Encryptor(String mode, String padding, byte[] keyBytes) throws CryptoException {
		return SM4Encryptor(mode, padding, keyBytes, "SHA1PRNG");
	}

	/**
	 * Initialize SM4 encryptor
	 *
	 * @param mode            Block cipher mode
	 * @param padding         Padding mode
	 * @param keyBytes        SM4 key bytes
	 * @param randomAlgorithm Random algorithm
	 * @return Initialized encryptor
	 * @throws CryptoException Cipher transformation not found
	 */
	public static SecureProvider SM4Encryptor(String mode, String padding, byte[] keyBytes, String randomAlgorithm)
			throws CryptoException {
		return new SM4CryptoProviderImpl(new CipherConfig("SM4", mode, padding),
				CryptoMode.ENCRYPT, keyBytes, randomAlgorithm);
	}

	/**
	 * Initialize SM4 decryptor using default cipher mode and padding mode
	 *
	 * @param keyBytes SM4 key bytes
	 * @return Initialized decryptor
	 * @throws CryptoException Cipher transformation not found
	 */
	public static SecureProvider SM4Decryptor(byte[] keyBytes) throws CryptoException {
		return SM4Decryptor("CBC", "PKCS5Padding", keyBytes, "SHA1PRNG");
	}

	/**
	 * Initialize SM4 decryptor
	 *
	 * @param mode     Block cipher mode
	 * @param padding  Padding mode
	 * @param keyBytes SM4 key bytes
	 * @return Initialized decryptor
	 * @throws CryptoException Cipher transformation not found
	 */
	public static SecureProvider SM4Decryptor(String mode, String padding, byte[] keyBytes) throws CryptoException {
		return SM4Decryptor(mode, padding, keyBytes, "SHA1PRNG");
	}

	/**
	 * Initialize SM4 decryptor
	 *
	 * @param mode            Block cipher mode
	 * @param padding         Padding mode
	 * @param keyBytes        SM4 key bytes
	 * @param randomAlgorithm Random algorithm
	 * @return Initialized decryptor
	 * @throws CryptoException Cipher transformation not found
	 */
	public static SecureProvider SM4Decryptor(String mode, String padding, byte[] keyBytes, String randomAlgorithm)
			throws CryptoException {
		return new SM4CryptoProviderImpl(new CipherConfig("SM4", mode, padding),
				CryptoMode.DECRYPT, keyBytes, randomAlgorithm);
	}

	/**
	 * Generate SM4 key
	 *
	 * @return Generated key bytes or 0 length byte array if process error
	 */
	public static byte[] SM4Key() {
		try {
			return SymmetricCryptoProvider.generateKey("SM4", 128, Globals.DEFAULT_VALUE_STRING);
		} catch (CryptoException e) {
			return new byte[0];
		}
	}

	/*
	 * Asymmetric methods
	 */

	/**
	 * Initialize RSA encryptor using default padding mode: PKCS1Padding
	 *
	 * @param publicKey RSA PublicKey
	 * @return Initialized encryptor
	 * @throws CryptoException Cipher transformation not found
	 */
	public static SecureProvider RSAEncryptor(PublicKey publicKey) throws CryptoException {
		return RSAEncryptor("PKCS1Padding", publicKey);
	}

	/**
	 * Initialize RSA encryptor using given padding mode
	 *
	 * @param padding   Padding mode
	 * @param publicKey RSA PublicKey
	 * @return Initialized encryptor
	 * @throws CryptoException Cipher transformation not found
	 */
	public static SecureProvider RSAEncryptor(String padding, PublicKey publicKey) throws CryptoException {
		return new RSACryptoProviderImpl(new CipherConfig("RSA", "ECB", padding),
				CryptoMode.ENCRYPT, new BaseCryptoProvider.CipherKey(publicKey));
	}

	/**
	 * Initialize RSA decryptor using default padding mode: PKCS1Padding
	 *
	 * @param privateKey RSA PrivateKey
	 * @return Initialized decryptor
	 * @throws CryptoException Cipher transformation not found
	 */
	public static SecureProvider RSADecryptor(PrivateKey privateKey) throws CryptoException {
		return RSADecryptor("PKCS1Padding", privateKey);
	}

	/**
	 * Initialize RSA decryptor using given padding mode
	 *
	 * @param padding    Padding mode
	 * @param privateKey RSA PrivateKey
	 * @return Initialized decryptor
	 * @throws CryptoException Cipher transformation not found
	 */
	public static SecureProvider RSADecryptor(String padding, PrivateKey privateKey) throws CryptoException {
		return new RSACryptoProviderImpl(new CipherConfig("RSA", "ECB", padding),
				CryptoMode.DECRYPT, new BaseCryptoProvider.CipherKey(privateKey));
	}

	/**
	 * Initialize RSA Signer using default algorithm: SHA256withRSA
	 *
	 * @param privateKey RSA PrivateKey
	 * @return Initialized signer
	 * @throws CryptoException Cipher transformation not found
	 */
	public static SecureProvider RSASigner(PrivateKey privateKey) throws CryptoException {
		return RSASigner("SHA256withRSA", privateKey);
	}

	/**
	 * Initialize RSA Signer using given algorithm
	 *
	 * @param algorithm  Sign algorithm
	 * @param privateKey RSA PrivateKey
	 * @return Initialized signer
	 * @throws CryptoException Cipher transformation not found
	 */
	public static SecureProvider RSASigner(String algorithm, PrivateKey privateKey) throws CryptoException {
		return new RSACryptoProviderImpl(
				new CipherConfig(algorithm, Globals.DEFAULT_VALUE_STRING, Globals.DEFAULT_VALUE_STRING),
				CryptoMode.SIGNATURE, new BaseCryptoProvider.CipherKey(privateKey));
	}

	/**
	 * Initialize RSA signature verifier using default algorithm: SHA256withRSA
	 *
	 * @param publicKey RSA PublicKey
	 * @return Initialized signer
	 * @throws CryptoException Cipher transformation not found
	 */
	public static SecureProvider RSAVerifier(PublicKey publicKey) throws CryptoException {
		return RSAVerifier("SHA256withRSA", publicKey);
	}

	/**
	 * Initialize RSA signature verifier using given algorithm
	 *
	 * @param algorithm Sign algorithm
	 * @param publicKey RSA PublicKey
	 * @return Initialized signer
	 * @throws CryptoException Cipher transformation not found
	 */
	public static SecureProvider RSAVerifier(String algorithm, PublicKey publicKey) throws CryptoException {
		return new RSACryptoProviderImpl(
				new CipherConfig(algorithm, Globals.DEFAULT_VALUE_STRING, Globals.DEFAULT_VALUE_STRING),
				CryptoMode.VERIFY, new BaseCryptoProvider.CipherKey(publicKey));
	}

	/**
	 * Generate RSA KeyPair using default Key size: 1024 and default random algorithm: SHA1PRNG
	 *
	 * @return Generated key pair
	 */
	public static KeyPair RSAKeyPair() {
		return RSAKeyPair(1024, "SHA1PRNG");
	}

	/**
	 * Generate RSA KeyPair using given Key size and default random algorithm: SHA1PRNG
	 *
	 * @param keySize RSA key size
	 * @return Generated key pair
	 */
	public static KeyPair RSAKeyPair(int keySize) {
		return RSAKeyPair(keySize, "SHA1PRNG");
	}

	/**
	 * Generate RSA KeyPair using given Key size and random algorithm
	 *
	 * @param keySize         RSA key size
	 * @param randomAlgorithm Random algorithm
	 * @return Generated key pair
	 */
	public static KeyPair RSAKeyPair(int keySize, String randomAlgorithm) {
		return CertificateUtils.KeyPair("RSA", randomAlgorithm, keySize);
	}

	/**
	 * Initialize SM2 encryptor
	 *
	 * @param publicKey SM2 PublicKey
	 * @return Initialized encryptor
	 * @throws CryptoException Cipher transformation not found
	 */
	public static SecureProvider SM2Encryptor(PublicKey publicKey) throws CryptoException {
		return new SM2CryptoProviderImpl(
				new CipherConfig("SM2", Globals.DEFAULT_VALUE_STRING, Globals.DEFAULT_VALUE_STRING),
				CryptoMode.ENCRYPT, new BaseCryptoProvider.CipherKey(publicKey));
	}

	/**
	 * Initialize SM2 decryptor
	 *
	 * @param privateKey SM2 PrivateKey
	 * @return Initialized decryptor
	 * @throws CryptoException Cipher transformation not found
	 */
	public static SecureProvider SM2Decryptor(PrivateKey privateKey) throws CryptoException {
		return new SM2CryptoProviderImpl(
				new CipherConfig("SM2", Globals.DEFAULT_VALUE_STRING, Globals.DEFAULT_VALUE_STRING),
				CryptoMode.DECRYPT, new BaseCryptoProvider.CipherKey(privateKey));
	}

	/**
	 * Initialize SM2 signer
	 *
	 * @param privateKey SM2 PrivateKey
	 * @return Initialized signer
	 * @throws CryptoException Cipher transformation not found
	 */
	public static SecureProvider SM2Signer(PrivateKey privateKey) throws CryptoException {
		return new SM2CryptoProviderImpl(
				new CipherConfig("SM3withSM2", Globals.DEFAULT_VALUE_STRING, Globals.DEFAULT_VALUE_STRING),
				CryptoMode.SIGNATURE, new BaseCryptoProvider.CipherKey(privateKey));
	}

	/**
	 * Initialize SM2 signature verifier
	 *
	 * @param publicKey SM2 PublicKey
	 * @return Initialized verifier
	 * @throws CryptoException Cipher transformation not found
	 */
	public static SecureProvider SM2Verifier(PublicKey publicKey) throws CryptoException {
		return new SM2CryptoProviderImpl(
				new CipherConfig("SM3withSM2", Globals.DEFAULT_VALUE_STRING, Globals.DEFAULT_VALUE_STRING),
				CryptoMode.VERIFY, new BaseCryptoProvider.CipherKey(publicKey));
	}

	/**
	 * Generate SM2 KeyPair using default random algorithm: SHA1PRNG
	 *
	 * @return Generated key pair
	 */
	public static KeyPair SM2KeyPair() {
		return CertificateUtils.KeyPair("EC", "SHA1PRNG", Globals.INITIALIZE_INT_VALUE);
	}

	/**
	 * Generate SM2 KeyPair using given random algorithm
	 *
	 * @param randomAlgorithm Random algorithm
	 * @return Generated key pair
	 */
	public static KeyPair SM2KeyPair(String randomAlgorithm) {
		return CertificateUtils.KeyPair("EC", randomAlgorithm, Globals.DEFAULT_VALUE_INT);
	}

	/**
	 * Convert C1|C2|C3 to C1|C3|C2
	 *
	 * @param dataBytes C1|C2|C3 data bytes
	 * @return C1 |C3|C2 data bytes
	 */
	public static byte[] C1C2C3toC1C3C2(byte[] dataBytes) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(dataBytes.length);
		byteArrayOutputStream.write(dataBytes, 0, 65);
		byteArrayOutputStream.write(dataBytes, 65, 32);
		byteArrayOutputStream.write(dataBytes, 97, dataBytes.length - 97);
		return byteArrayOutputStream.toByteArray();
	}

	/**
	 * Convert C1|C3|C2 to C1|C2|C3
	 *
	 * @param dataBytes C1|C3|C2 data bytes
	 * @return C1 |C2|C3 data bytes
	 */
	public static byte[] C1C3C2toC1C2C3(byte[] dataBytes) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(dataBytes.length);
		byteArrayOutputStream.write(dataBytes, 0, 65);
		byteArrayOutputStream.write(dataBytes, dataBytes.length - 32, 32);
		byteArrayOutputStream.write(dataBytes, 65, dataBytes.length - 97);
		return byteArrayOutputStream.toByteArray();
	}

	/**
	 * Initialize AES encryptor using default cipher mode and padding mode
	 *
	 * @param keyBytes AES key bytes
	 * @return Initialized encryptor
	 * @throws CryptoException Cipher transformation not found
	 */
	public static SecureProvider AESEncryptor(byte[] keyBytes) throws CryptoException {
		return AESEncryptor("CBC", "PKCS5Padding", keyBytes);
	}

	/**
	 * Initialize AES encryptor
	 *
	 * @param mode     Block cipher mode
	 * @param padding  Padding mode
	 * @param keyBytes AES key bytes
	 * @return Initialized encryptor
	 * @throws CryptoException Cipher transformation not found
	 */
	public static SecureProvider AESEncryptor(String mode, String padding, byte[] keyBytes) throws CryptoException {
		return new AESCryptoProviderImpl(new CipherConfig("AES", mode, padding), CryptoMode.ENCRYPT, keyBytes);
	}

	/**
	 * Initialize AES decryptor using default cipher mode and padding mode
	 *
	 * @param keyBytes AES key bytes
	 * @return Initialized decryptor
	 * @throws CryptoException Cipher transformation not found
	 */
	public static SecureProvider AESDecryptor(byte[] keyBytes) throws CryptoException {
		return AESDecryptor("CBC", "PKCS5Padding", keyBytes);
	}

	/**
	 * Initialize AES decryptor
	 *
	 * @param mode     Block cipher mode
	 * @param padding  Padding mode
	 * @param keyBytes AES key bytes
	 * @return Initialized decryptor
	 * @throws CryptoException Cipher transformation not found
	 */
	public static SecureProvider AESDecryptor(String mode, String padding, byte[] keyBytes) throws CryptoException {
		return new AESCryptoProviderImpl(new CipherConfig("AES", mode, padding), CryptoMode.DECRYPT, keyBytes);
	}

	/*
	 * Key generators
	 */

	/**
	 * Generate AES key using default random algorithm: SHA1PRNG, Key size: 128
	 *
	 * @return Generated key bytes or 0 length byte array if process error
	 */
	public static byte[] AES128Key() {
		return AES128Key("SHA1PRNG");
	}

	/**
	 * Generate AES key using given random algorithm: SHA1PRNG, Key size: 128
	 *
	 * @param randomAlgorithm Random algorithm
	 * @return Generated key bytes or 0 length byte array if process error
	 */
	public static byte[] AES128Key(String randomAlgorithm) {
		try {
			return SymmetricCryptoProvider.generateKey("AES", 128, randomAlgorithm);
		} catch (CryptoException e) {
			return new byte[0];
		}
	}

	/**
	 * Generate AES key using default random algorithm: SHA1PRNG, Key size: 192
	 *
	 * @return Generated key bytes or 0 length byte array if process error
	 */
	public static byte[] AES192Key() {
		return AES192Key("SHA1PRNG");
	}

	/**
	 * Generate AES key using given random algorithm: SHA1PRNG, Key size: 192
	 *
	 * @param randomAlgorithm Random algorithm
	 * @return Generated key bytes or 0 length byte array if process error
	 */
	public static byte[] AES192Key(String randomAlgorithm) {
		try {
			return SymmetricCryptoProvider.generateKey("AES", 192, randomAlgorithm);
		} catch (CryptoException e) {
			return new byte[0];
		}
	}

	/**
	 * Generate AES key using default random algorithm: SHA1PRNG, Key size: 256
	 *
	 * @return Generated key bytes or 0 length byte array if process error
	 */
	public static byte[] AES256Key() {
		return AES256Key("SHA1PRNG");
	}

	/**
	 * Generate AES key using given random algorithm: SHA1PRNG, Key size: 256
	 *
	 * @param randomAlgorithm Random algorithm
	 * @return Generated key bytes or 0 length byte array if process error
	 */
	public static byte[] AES256Key(String randomAlgorithm) {
		try {
			return SymmetricCryptoProvider.generateKey("AES", 256, randomAlgorithm);
		} catch (CryptoException e) {
			return new byte[0];
		}
	}

	/**
	 * Process digest operate to given source using crypto provider
	 *
	 * @param source			Data source
	 * @param secureProvider	Crypto provider
	 * @return					Calculate result, convert byte array to hex string
	 * @throws CryptoException	Cipher transformation not found
	 */
	private static byte[] digest(Object source, SecureProvider secureProvider) throws CryptoException {
		if (source instanceof File) {
			try (InputStream inputStream = new FileInputStream((File) source)) {
				byte[] readBuffer = new byte[Globals.READ_FILE_BUFFER_SIZE];
				int readLength;
				while ((readLength = inputStream.read(readBuffer)) > 0) {
					secureProvider.append(readBuffer, 0, readLength);
				}
			} catch (Exception e) {
				LOGGER.error("Message digest error! ", e);
				return new byte[0];
			}
			return secureProvider.finish();
		} else if (source instanceof SmbFile) {
			try (InputStream inputStream = new SmbFileInputStream((SmbFile) source)) {
				byte[] readBuffer = new byte[Globals.READ_FILE_BUFFER_SIZE];
				int readLength;
				while ((readLength = inputStream.read(readBuffer)) > 0) {
					secureProvider.append(readBuffer, 0, readLength);
				}
			} catch (Exception e) {
				LOGGER.error("Message digest error! ", e);
				return new byte[0];
			}
			return secureProvider.finish();
		} else {
			return secureProvider.finish(ConvertUtils.convertToByteArray(source));
		}
	}

	/**
	 * Register CRC Algorithm
	 */
	private static void CRC() {
		SecurityUtils.registerConfig("CRC-3/GSM",
				new CRCConfig(3, 0x3, 0x0, 0x7, false, false));
		SecurityUtils.registerConfig("CRC-3/ROHC",
				new CRCConfig(3, 0x3, 0x7, 0x0, true, true));
		SecurityUtils.registerConfig("CRC-4/G-704",
				new CRCConfig(4, 0x3, 0x0, 0x0, true, true));
		SecurityUtils.registerConfig("CRC-4/INTERLAKEN",
				new CRCConfig(4, 0x3, 0xF, 0xF, false, false));
		SecurityUtils.registerConfig("CRC-5/EPC-C1G2",
				new CRCConfig(5, 0x09, 0x09, 0x00, false, false));
		SecurityUtils.registerConfig("CRC-5/G-704",
				new CRCConfig(5, 0x15, 0x00, 0x00, true, true));
		SecurityUtils.registerConfig("CRC-5/USB",
				new CRCConfig(5, 0x05, 0x1F, 0x1F, true, true));
		SecurityUtils.registerConfig("CRC-6/CDMA2000-A",
				new CRCConfig(6, 0x27, 0x3F, 0x00, false, false));
		SecurityUtils.registerConfig("CRC-6/CDMA2000-B",
				new CRCConfig(6, 0x07, 0x3F, 0x00, false, false));
		SecurityUtils.registerConfig("CRC-6/DARC",
				new CRCConfig(6, 0x19, 0x00, 0x00, true, true));
		SecurityUtils.registerConfig("CRC-6/G-704",
				new CRCConfig(6, 0x03, 0x00, 0x00, true, true));
		SecurityUtils.registerConfig("CRC-6/GSM",
				new CRCConfig(6, 0x2F, 0x00, 0x3F, false, false));
		SecurityUtils.registerConfig("CRC-7/MMC",
				new CRCConfig(7, 0x09, 0x00, 0x00, false, false));
		SecurityUtils.registerConfig("CRC-7/ROHC",
				new CRCConfig(7, 0x4F, 0x7F, 0x00, true, true));
		SecurityUtils.registerConfig("CRC-7/UMTS",
				new CRCConfig(7, 0x45, 0x00, 0x00, false, false));
		SecurityUtils.registerConfig("CRC-8/AUTOSAR",
				new CRCConfig(8, 0x2F, 0xFF, 0xFF, false, false));
		SecurityUtils.registerConfig("CRC-8/BLUETOOTH",
				new CRCConfig(8, 0xA7, 0x00, 0x00, true, true));
		SecurityUtils.registerConfig("CRC-8/CDMA2000",
				new CRCConfig(8, 0x9B, 0xFF, 0x00, false, false));
		SecurityUtils.registerConfig("CRC-8/DARC",
				new CRCConfig(8, 0x39, 0x00, 0x00, true, true));
		SecurityUtils.registerConfig("CRC-8/DVB-S2",
				new CRCConfig(8, 0xD5, 0x00, 0x00, false, false));
		SecurityUtils.registerConfig("CRC-8/GSM-A",
				new CRCConfig(8, 0x1D, 0x00, 0x00, false, false));
		SecurityUtils.registerConfig("CRC-8/GSM-B",
				new CRCConfig(8, 0x49, 0x00, 0xFF, false, false));
		SecurityUtils.registerConfig("CRC-8/I-432-1",
				new CRCConfig(8, 0x07, 0x00, 0x55, false, false));
		SecurityUtils.registerConfig("CRC-8/I-CODE",
				new CRCConfig(8, 0x1D, 0xFD, 0x00, false, false));
		SecurityUtils.registerConfig("CRC-8/LTE",
				new CRCConfig(8, 0x9B, 0x00, 0x00, false, false));
		SecurityUtils.registerConfig("CRC-8/MAXIM-DOW",
				new CRCConfig(8, 0x31, 0x00, 0x00, true, true));
		SecurityUtils.registerConfig("CRC-8/MIFARE-MAD",
				new CRCConfig(8, 0x1D, 0xC7, 0x00, false, false));
		SecurityUtils.registerConfig("CRC-8/NRSC-5",
				new CRCConfig(8, 0x31, 0xFF, 0x00, false, false));
		SecurityUtils.registerConfig("CRC-8/OPENSAFETY",
				new CRCConfig(8, 0x2F, 0x00, 0x00, false, false));
		SecurityUtils.registerConfig("CRC-8/ROHC",
				new CRCConfig(8, 0x07, 0xFF, 0x00, true, true));
		SecurityUtils.registerConfig("CRC-8/SAE-J1850",
				new CRCConfig(8, 0x1D, 0xFF, 0xFF, false, false));
		SecurityUtils.registerConfig("CRC-8/SMBUS",
				new CRCConfig(8, 0x07, 0x00, 0x00, false, false));
		SecurityUtils.registerConfig("CRC-8/TECH-3250",
				new CRCConfig(8, 0x1D, 0xFF, 0x00, true, true));
		SecurityUtils.registerConfig("CRC-8/WCDMA",
				new CRCConfig(8, 0x9B, 0x00, 0x00, true, true));
		SecurityUtils.registerConfig("CRC-10/ATM",
				new CRCConfig(10, 0x233, 0x000, 0x000, false, false));
		SecurityUtils.registerConfig("CRC-10/CDMA2000",
				new CRCConfig(10, 0x3D9, 0x3FF, 0x000, false, false));
		SecurityUtils.registerConfig("CRC-10/GSM",
				new CRCConfig(10, 0x175, 0x000, 0x3FF, false, false));
		SecurityUtils.registerConfig("CRC-11/FLEXRAY",
				new CRCConfig(11, 0x385, 0x01A, 0x000, false, false));
		SecurityUtils.registerConfig("CRC-11/UMTS",
				new CRCConfig(11, 0x307, 0x000, 0x000, false, false));
		SecurityUtils.registerConfig("CRC-12/CDMA2000",
				new CRCConfig(12, 0xF13, 0xFFF, 0x000, false, false));
		SecurityUtils.registerConfig("CRC-12/DECT",
				new CRCConfig(12, 0x80F, 0x000, 0x000, false, false));
		SecurityUtils.registerConfig("CRC-12/GSM",
				new CRCConfig(12, 0xD31, 0x000, 0xFFF, false, false));
		SecurityUtils.registerConfig("CRC-12/UMTS",
				new CRCConfig(12, 0x80F, 0x000, 0x000, false, true));
		SecurityUtils.registerConfig("CRC-13/BBC",
				new CRCConfig(13, 0x1CF5, 0x0000, 0x0000, false, false));
		SecurityUtils.registerConfig("CRC-14/DARC",
				new CRCConfig(14, 0x0805, 0x0000, 0x0000, true, true));
		SecurityUtils.registerConfig("CRC-14/GSM",
				new CRCConfig(14, 0x202D, 0x0000, 0x3FFF, false, false));
		SecurityUtils.registerConfig("CRC-15/CAN",
				new CRCConfig(15, 0x4599, 0x0000, 0x0000, false, false));
		SecurityUtils.registerConfig("CRC-15/MPT1327",
				new CRCConfig(15, 0x6815, 0x0000, 0x0001, false, false));
		SecurityUtils.registerConfig("CRC-16/ARC",
				new CRCConfig(16, 0x8005, 0x0000, 0x0000, true, true));
		SecurityUtils.registerConfig("CRC-16/CDMA2000",
				new CRCConfig(16, 0xC867, 0xFFFF, 0x0000, false, false));
		SecurityUtils.registerConfig("CRC-16/CMS",
				new CRCConfig(16, 0x8005, 0xFFFF, 0x0000, false, false));
		SecurityUtils.registerConfig("CRC-16/DDS-110",
				new CRCConfig(16, 0x8005, 0x800D, 0x0000, false, false));
		SecurityUtils.registerConfig("CRC-16/DECT-R",
				new CRCConfig(16, 0x0589, 0x0000, 0x0001, false, false));
		SecurityUtils.registerConfig("CRC-16/DECT-X",
				new CRCConfig(16, 0x0589, 0x0000, 0x0000, false, false));
		SecurityUtils.registerConfig("CRC-16/DNP",
				new CRCConfig(16, 0x3D65, 0x0000, 0xFFFF, true, true));
		SecurityUtils.registerConfig("CRC-16/EN-13757",
				new CRCConfig(16, 0x3D65, 0x0000, 0xFFFF, false, false));
		SecurityUtils.registerConfig("CRC-16/GENIBUS",
				new CRCConfig(16, 0x1021, 0xFFFF, 0xFFFF, false, false));
		SecurityUtils.registerConfig("CRC-16/GSM",
				new CRCConfig(16, 0x1021, 0x0000, 0xFFFF, false, false));
		SecurityUtils.registerConfig("CRC-16/IBM-3740",
				new CRCConfig(16, 0x1021, 0xFFFF, 0x0000, false, false));
		SecurityUtils.registerConfig("CRC-16/IBM-SDLC",
				new CRCConfig(16, 0x1021, 0xFFFF, 0xFFFF, true, true));
		SecurityUtils.registerConfig("CRC-16/ISO-IEC-14443-3-A",
				new CRCConfig(16, 0x1021, 0xC6C6, 0x0000, true, true));
		SecurityUtils.registerConfig("CRC-16/KERMIT",
				new CRCConfig(16, 0x1021, 0x0000, 0x0000, true, true));
		SecurityUtils.registerConfig("CRC-16/LJ1200",
				new CRCConfig(16, 0x6F63, 0x0000, 0x0000, false, false));
		SecurityUtils.registerConfig("CRC-16/MAXIM-DOW",
				new CRCConfig(16, 0x8005, 0x0000, 0xFFFF, true, true));
		SecurityUtils.registerConfig("CRC-16/MCRF4XX",
				new CRCConfig(16, 0x1021, 0xFFFF, 0x0000, true, true));
		SecurityUtils.registerConfig("CRC-16/MODBUS",
				new CRCConfig(16, 0x8005, 0xFFFF, 0x0000, true, true));
		SecurityUtils.registerConfig("CRC-16/NRSC-5",
				new CRCConfig(16, 0x080B, 0xFFFF, 0x0000, true, true));
		SecurityUtils.registerConfig("CRC-16/OPENSAFETY-A",
				new CRCConfig(16, 0x5935, 0x0000, 0x0000, false, false));
		SecurityUtils.registerConfig("CRC-16/OPENSAFETY-B",
				new CRCConfig(16, 0x755B, 0x0000, 0x0000, false, false));
		SecurityUtils.registerConfig("CRC-16/PROFIBUS",
				new CRCConfig(16, 0x1DCF, 0xFFFF, 0xFFFF, false, false));
		SecurityUtils.registerConfig("CRC-16/RIELLO",
				new CRCConfig(16, 0x1021, 0xB2AA, 0x0000, true, true));
		SecurityUtils.registerConfig("CRC-16/SPI-FUJITSU",
				new CRCConfig(16, 0x1021, 0x1D0F, 0x0000, false, false));
		SecurityUtils.registerConfig("CRC-16/T10-DIF",
				new CRCConfig(16, 0x8BB7, 0x0000, 0x0000, false, false));
		SecurityUtils.registerConfig("CRC-16/TELEDISK",
				new CRCConfig(16, 0xA097, 0x0000, 0x0000, false, false));
		SecurityUtils.registerConfig("CRC-16/TMS37157",
				new CRCConfig(16, 0x1021, 0x89EC, 0x0000, true, true));
		SecurityUtils.registerConfig("CRC-16/UMTS",
				new CRCConfig(16, 0x8005, 0x0000, 0x0000, false, false));
		SecurityUtils.registerConfig("CRC-16/USB",
				new CRCConfig(16, 0x8005, 0xFFFF, 0xFFFF, true, true));
		SecurityUtils.registerConfig("CRC-16/XMODEM",
				new CRCConfig(16, 0x1021, 0x0000, 0x0000, false, false));
		SecurityUtils.registerConfig("CRC-17/CAN-FD",
				new CRCConfig(17, 0x1685B, 0x00000, 0x00000, false, false));
		SecurityUtils.registerConfig("CRC-21/CAN-FD",
				new CRCConfig(21, 0x102899, 0x000000, 0x000000, false, false));
		SecurityUtils.registerConfig("CRC-24/BLE",
				new CRCConfig(24, 0x00065B, 0x555555, 0x000000, true, true));
		SecurityUtils.registerConfig("CRC-24/FLEXRAY-A",
				new CRCConfig(24, 0x5D6DCB, 0xFEDCBA, 0x000000, false, false));
		SecurityUtils.registerConfig("CRC-24/FLEXRAY-B",
				new CRCConfig(24, 0x5D6DCB, 0xABCDEF, 0x000000, false, false));
		SecurityUtils.registerConfig("CRC-24/INTERLAKEN",
				new CRCConfig(24, 0x328B63, 0xFFFFFF, 0xFFFFFF, false, false));
		SecurityUtils.registerConfig("CRC-24/LTE-A",
				new CRCConfig(24, 0x864CFB, 0x000000, 0x000000, false, false));
		SecurityUtils.registerConfig("CRC-24/LTE-B",
				new CRCConfig(24, 0x800063, 0x000000, 0x000000, false, false));
		SecurityUtils.registerConfig("CRC-24/OPENPGP",
				new CRCConfig(24, 0x864CFB, 0xB704CE, 0x000000, false, false));
		SecurityUtils.registerConfig("CRC-24/OS-9",
				new CRCConfig(24, 0x800063, 0xFFFFFF, 0xFFFFFF, false, false));
		SecurityUtils.registerConfig("CRC-30/CDMA",
				new CRCConfig(30, 0x2030B9C7, 0x3FFFFFFF, 0x3FFFFFFF, false, false));
		SecurityUtils.registerConfig("CRC-31/PHILIPS",
				new CRCConfig(31, 0x04C11DB7, 0x7FFFFFFF, 0x7FFFFFFF, false, false));
		SecurityUtils.registerConfig("CRC-32/AIXM",
				new CRCConfig(32, 0x814141AB, 0x00000000, 0x00000000, false, false));
		SecurityUtils.registerConfig("CRC-32/AUTOSAR",
				new CRCConfig(32, 0xF4ACFB13, 0xFFFFFFFF, 0xFFFFFFFF, true, true));
		SecurityUtils.registerConfig("CRC-32/BASE91-D",
				new CRCConfig(32, 0xA833982B, 0xFFFFFFFF, 0xFFFFFFFF, true, true));
		SecurityUtils.registerConfig("CRC-32/BZIP2",
				new CRCConfig(32, 0x04C11DB7, 0xFFFFFFFF, 0xFFFFFFFF, false, false));
		SecurityUtils.registerConfig("CRC-32/CD-ROM-EDC",
				new CRCConfig(32, 0x8001801B, 0x00000000, 0x00000000, true, true));
		SecurityUtils.registerConfig("CRC-32/CKSUM",
				new CRCConfig(32, 0x04C11DB7, 0x00000000, 0xFFFFFFFF, false, false));
		SecurityUtils.registerConfig("CRC-32/ISCSI",
				new CRCConfig(32, 0x1EDC6F41, 0xFFFFFFFF, 0xFFFFFFFF, true, true));
		SecurityUtils.registerConfig("CRC-32/ISO-HDLC",
				new CRCConfig(32, 0x04C11DB7, 0xFFFFFFFF, 0xFFFFFFFF, true, true));
		SecurityUtils.registerConfig("CRC-32/JAMCRC",
				new CRCConfig(32, 0x04C11DB7, 0xFFFFFFFF, 0x00000000, true, true));
		SecurityUtils.registerConfig("CRC-32/MPEG-2",
				new CRCConfig(32, 0x04C11DB7, 0xFFFFFFFF, 0x00000000, false, false));
		SecurityUtils.registerConfig("CRC-32/XFER",
				new CRCConfig(32, 0x000000AF, 0x00000000, 0x00000000, false, false));
	}
}
