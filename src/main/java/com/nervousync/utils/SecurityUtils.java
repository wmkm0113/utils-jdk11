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
package com.nervousync.utils;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.annotation.Nonnull;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nervousync.commons.core.Globals;

/**
 * Security Utils
 * 
 * Implements:
 * 		MD5 Encode
 * 		SHA Encode
 * 		DES Encrypt/Decrypt
 * 		RSA Encrypt/Decrypt
 * 
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Jan 13, 2010 11:23:13 AM $
 */
public final class SecurityUtils implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2929476536772097530L;

	/**
	 * Log for SecurityUtils class
	 */
	private transient static final Logger LOGGER = LoggerFactory.getLogger(SecurityUtils.class);

	/**
	 * DES Algorithm
	 */
	public transient static final String DES_ECB_NO_PADDING         = "DES/ECB/NoPadding";
	public transient static final String DES_ECB_PKCS5_PADDING      = "DES/ECB/PKCS5Padding";
	public transient static final String DES_CBC_NO_PADDING         = "DES/CBC/NoPadding";
	public transient static final String DES_CBC_PKCS5_PADDING      = "DES/CBC/PKCS5Padding";
	public transient static final String DES_CFB_NO_PADDING         = "DES/CFB/NoPadding";
	public transient static final String DES_CFB_PKCS5_PADDING      = "DES/CFB/PKCS5Padding";
	public transient static final String DES_OFB_NO_PADDING         = "DES/OFB/NoPadding";
	public transient static final String DES_OFB_PKCS5_PADDING      = "DES/OFB/PKCS5Padding";
	public transient static final String DES_EDE_ECB_NO_PADDING     = "DESede/ECB/NoPadding";
	public transient static final String DES_EDE_ECB_PKCS5_PADDING  = "DESede/ECB/PKCS5Padding";
	public transient static final String DES_EDE_CBC_NO_PADDING     = "DESede/CBC/NoPadding";
	public transient static final String DES_EDE_CBC_PKCS5_PADDING  = "DESede/CBC/PKCS5Padding";
	public transient static final String DES_EDE_CFB_NO_PADDING     = "DESede/CFB/NoPadding";
	public transient static final String DES_EDE_CFB_PKCS5_PADDING  = "DESede/CFB/PKCS5Padding";
	public transient static final String DES_EDE_OFB_NO_PADDING     = "DESede/OFB/NoPadding";
	public transient static final String DES_EDE_OFB_PKCS5_PADDING  = "DESede/OFB/PKCS5Padding";
	/**
	 * AES Algorithm
	 */
	public transient static final String AES_CBC_NO_PADDING         = "AES/CBC/NoPadding";
	public transient static final String AES_CBC_PKCS5_PADDING      = "AES/CBC/PKCS5Padding";
	public transient static final String AES_CBC_PKCS7_PADDING      = "AES/CBC/PKCS7Padding";
	public transient static final String AES_CBC_ISO10126_Padding   = "AES/CBC/ISO10126Padding";
	public transient static final String AES_CFB_NO_PADDING         = "AES/CFB/NoPadding";
	public transient static final String AES_CFB_PKCS5_PADDING      = "AES/CFB/PKCS5Padding";
	public transient static final String AES_CFB_PKCS7_PADDING      = "AES/CFB/PKCS7Padding";
	public transient static final String AES_CFB_ISO10126_Padding   = "AES/CFB/ISO10126Padding";
	public transient static final String AES_ECB_NO_PADDING         = "AES/ECB/NoPadding";
	public transient static final String AES_ECB_PKCS5_PADDING      = "AES/ECB/PKCS5Padding";
	public transient static final String AES_ECB_PKCS7_PADDING      = "AES/ECB/PKCS7Padding";
	public transient static final String AES_ECB_ISO10126_Padding   = "AES/ECB/ISO10126Padding";
	public transient static final String AES_OFB_NO_PADDING         = "AES/OFB/NoPadding";
	public transient static final String AES_OFB_PKCS5_PADDING      = "AES/OFB/PKCS5Padding";
	public transient static final String AES_OFB_PKCS7_PADDING      = "AES/OFB/PKCS7Padding";
	public transient static final String AES_OFB_ISO10126_Padding   = "AES/OFB/ISO10126Padding";
	/**
	 * RSA Algorithm
	 */
	public transient static final String RSA_PKCS1_PADDING          = "RSA/ECB/PKCS1Padding";
	public transient static final String RSA_OAEP_SHA1_PADDING      = "RSA/ECB/OAEPWithSHA-1AndMGF1Padding";
	public transient static final String RSA_OAEP_SHA256_PADDING    = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";

	/**
	 * PRNG Algorithm
	 */
	public transient static final String PRNG_ALGORITHM_NATIVE                  = "NativePRNG";
	public transient static final String PRNG_ALGORITHM_NATIVE_BLOCKING         = "NativePRNGBlocking";
	public transient static final String PRNG_ALGORITHM_NATIVE_NON_BLOCKING     = "NativePRNGNonBlocking";
	public transient static final String PRNG_ALGORITHM_NATIVE_PKCS11           = "PKCS11";
	public transient static final String PRNG_ALGORITHM_NATIVE_SHA1PRNG         = "SHA1PRNG";
	public transient static final String PRNG_ALGORITHM_NATIVE_WINDOWS          = "Windows-PRNG";

	/**
	 * Default key value
	 */
	private static final String PRIVATE_KEY = StringUtils.randomString(32);

	private SecurityUtils() {
	}
	
	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	/* MD5 Method */
	
	/**
	 * Get MD5 value. Only encode <code>String</code>
	 * @param source		source object
	 * @return MD5 value
	 */
	public static String MD5(Object source) {
		return digestEncode(source, "MD5");
	}
	
	/* SHA Method */
	
	/**
	 * Get SHA1 value. Only encode <code>String</code>
	 * Using SHA256 instead
	 * @param source		source object
	 * @return SHA1 value
	 */
	@Deprecated
	public static String SHA1(Object source) {
		return digestEncode(source, "SHA1");
	}
	
	/**
	 * Get SHA256 value. Only encode <code>String</code>
	 * @param source		source object
	 * @return SHA256 value
	 */
	public static String SHA256(Object source) {
		return digestEncode(source, "SHA-256");
	}
	
	/**
	 * Get SHA512 value. Only encode <code>String</code>
	 * @param source		source object
	 * @return SHA512 value
	 */
	public static String SHA512(Object source) {
		return digestEncode(source, "SHA-512");
	}

	/* Encrypt Data Method*/

	/**
	 * Encrypt byte arrays with given encrypt key by AES128
	 * @param arrB						Byte arrays will be encrypted
	 * @param strKey					encrypt key
	 * @return							Encrypted result
	 */
	public static byte[] AES128Encrypt(byte[] arrB, String strKey) {
		return EncryptData(AES_CBC_PKCS5_PADDING, PRNG_ALGORITHM_NATIVE_SHA1PRNG, arrB, null, strKey, 128);
	}

	/**
	 * Encrypt byte arrays with given algorithm and encrypt key by AES128
	 * @param algorithm                 Algorithm
	 * @param arrB						Byte arrays will be encrypted
	 * @param strKey					encrypt key
	 * @return							Encrypted result
	 */
	public static byte[] AES128Encrypt(String algorithm, byte[] arrB, String strKey) {
		return EncryptData(algorithm, PRNG_ALGORITHM_NATIVE_SHA1PRNG, arrB, null, strKey, 128);
	}

	/**
	 * Encrypt byte arrays with given algorithm and encrypt key by AES128
	 * @param algorithm                 Algorithm
	 * @param prngAlgorithm             PRNG Algorithm
	 * @param arrB						Byte arrays will be encrypted
	 * @param strKey					encrypt key
	 * @return							Encrypted result
	 */
	public static byte[] AES128Encrypt(String algorithm, String prngAlgorithm, byte[] arrB, String strKey) {
		return EncryptData(algorithm, prngAlgorithm, arrB, null, strKey, 128);
	}

	/**
	 * Encrypt byte arrays with given encrypt key by AES128
	 * @param arrB						Byte arrays will be encrypted
	 * @param keyContent				Binary key content
	 * @return							Encrypted result
	 */
	public static byte[] AES128Encrypt(byte[] arrB, byte[] keyContent) {
		return EncryptData(AES_CBC_PKCS5_PADDING, PRNG_ALGORITHM_NATIVE_SHA1PRNG, arrB, null, keyContent, 128);
	}

	/**
	 * Encrypt byte arrays with given algorithm and encrypt key by AES128
	 * @param algorithm                 Algorithm
	 * @param arrB						Byte arrays will be encrypted
	 * @param keyContent				Binary key content
	 * @return							Encrypted result
	 */
	public static byte[] AES128Encrypt(String algorithm, byte[] arrB, byte[] keyContent) {
		return EncryptData(algorithm, PRNG_ALGORITHM_NATIVE_SHA1PRNG, arrB, null, keyContent, 128);
	}

	/**
	 * Encrypt byte arrays with given algorithm and encrypt key by AES128
	 * @param algorithm                 Algorithm
	 * @param prngAlgorithm             PRNG Algorithm
	 * @param arrB						Byte arrays will be encrypted
	 * @param keyContent				Binary key content
	 * @return							Encrypted result
	 */
	public static byte[] AES128Encrypt(String algorithm, String prngAlgorithm, byte[] arrB, byte[] keyContent) {
		return EncryptData(algorithm, prngAlgorithm, arrB, null, keyContent, 128);
	}

	/**
	 * Encrypt byte arrays with given encrypt key by AES256
	 * @param arrB						Byte arrays will be encrypted
	 * @param strKey					encrypt key
	 * @return							Encrypted result
	 */
	public static byte[] AES256Encrypt(byte[] arrB, String strKey) {
		return EncryptData(AES_CBC_PKCS5_PADDING, PRNG_ALGORITHM_NATIVE_SHA1PRNG, arrB, null, strKey, 256);
	}

	/**
	 * Encrypt byte arrays with given algorithm and encrypt key by AES256
	 * @param algorithm                 Algorithm
	 * @param arrB						Byte arrays will be encrypted
	 * @param strKey					encrypt key
	 * @return							Encrypted result
	 */
	public static byte[] AES256Encrypt(String algorithm, byte[] arrB, String strKey) {
		return EncryptData(algorithm, PRNG_ALGORITHM_NATIVE_SHA1PRNG, arrB, null, strKey, 256);
	}

	/**
	 * Encrypt byte arrays with given algorithm and encrypt key by AES256
	 * @param algorithm                 Algorithm
	 * @param prngAlgorithm             PRNG Algorithm
	 * @param arrB						Byte arrays will be encrypted
	 * @param strKey					encrypt key
	 * @return							Encrypted result
	 */
	public static byte[] AES256Encrypt(String algorithm, String prngAlgorithm, byte[] arrB, String strKey) {
		return EncryptData(algorithm, prngAlgorithm, arrB, null, strKey, 256);
	}

	/**
	 * Encrypt byte arrays with given encrypt key by AES256
	 * @param arrB						Byte arrays will be encrypted
	 * @param keyContent				Binary key content
	 * @return							Encrypted result
	 */
	public static byte[] AES256Encrypt(byte[] arrB, byte[] keyContent) {
		return EncryptData(AES_CBC_PKCS5_PADDING, PRNG_ALGORITHM_NATIVE_SHA1PRNG, arrB, null, keyContent, 256);
	}

	/**
	 * Encrypt byte arrays with given algorithm and encrypt key by AES256
	 * @param algorithm                 Algorithm
	 * @param arrB						Byte arrays will be encrypted
	 * @param keyContent				Binary key content
	 * @return							Encrypted result
	 */
	public static byte[] AES256Encrypt(String algorithm, byte[] arrB, byte[] keyContent) {
		return EncryptData(algorithm, PRNG_ALGORITHM_NATIVE_SHA1PRNG, arrB, null, keyContent, 256);
	}

	/**
	 * Encrypt byte arrays with given algorithm and encrypt key by AES256
	 * @param algorithm                 Algorithm
	 * @param prngAlgorithm             PRNG Algorithm
	 * @param arrB						Byte arrays will be encrypted
	 * @param keyContent				Binary key content
	 * @return							Encrypted result
	 */
	public static byte[] AES256Encrypt(String algorithm, String prngAlgorithm, byte[] arrB, byte[] keyContent) {
		return EncryptData(algorithm, prngAlgorithm, arrB, null, keyContent, 256);
	}

	/**
	 * Encrypt string with default encrypt key by AES128
	 * @param strIn						String will be encrypted
	 * @return							Encrypted result
	 */
	public static String AES128Encrypt(String strIn) {
		return AES128Encrypt(AES_CBC_PKCS5_PADDING, PRNG_ALGORITHM_NATIVE_SHA1PRNG, strIn, SecurityUtils.PRIVATE_KEY);
	}

	/**
	 * Encrypt string with given encrypt key by AES128
	 * @param strIn						String will be encrypted
	 * @param strKey					encrypt key
	 * @return							Encrypted result
	 */
	public static String AES128Encrypt(String strIn, String strKey) {
		return AES128Encrypt(AES_CBC_PKCS5_PADDING, PRNG_ALGORITHM_NATIVE_SHA1PRNG, strIn, strKey);
	}

	/**
	 * Encrypt string with given encrypt key by AES128
	 * @param algorithm                 Algorithm
	 * @param strIn						String will be encrypted
	 * @param strKey					encrypt key
	 * @return							Encrypted result
	 */
	public static String AES128Encrypt(String algorithm, String strIn, String strKey) {
		return ConvertUtils.byteArrayToHexString(
				AES128Encrypt(algorithm, PRNG_ALGORITHM_NATIVE_SHA1PRNG, ConvertUtils.convertToByteArray(strIn), strKey));
	}

	/**
	 * Encrypt string with given encrypt key by AES128
	 * @param algorithm                 Algorithm
	 * @param prngAlgorithm             PRNG Algorithm
	 * @param strIn						String will be encrypted
	 * @param strKey					encrypt key
	 * @return							Encrypted result
	 */
	public static String AES128Encrypt(String algorithm, String prngAlgorithm, String strIn, String strKey) {
		return ConvertUtils.byteArrayToHexString(
				AES128Encrypt(algorithm, prngAlgorithm, ConvertUtils.convertToByteArray(strIn), strKey));
	}

	/**
	 * Encrypt string with default encrypt key by AES256
	 * @param strIn						String will be encrypted
	 * @return							Encrypted result
	 */
	public static String AES256Encrypt(String strIn) {
		return AES256Encrypt(AES_CBC_PKCS5_PADDING, PRNG_ALGORITHM_NATIVE_SHA1PRNG, strIn, SecurityUtils.PRIVATE_KEY);
	}

	/**
	 * Encrypt string with given encrypt key by AES256
	 * @param strIn						String will be encrypted
	 * @param strKey					encrypt key
	 * @return							Encrypted result
	 */
	public static String AES256Encrypt(String strIn, String strKey) {
		return AES256Encrypt(AES_CBC_PKCS5_PADDING, PRNG_ALGORITHM_NATIVE_SHA1PRNG, strIn, strKey);
	}

	/**
	 * Encrypt string with given encrypt key by AES256
	 * @param algorithm                 Algorithm
	 * @param strIn						String will be encrypted
	 * @param strKey					encrypt key
	 * @return							Encrypted result
	 */
	public static String AES256Encrypt(String algorithm, String strIn, String strKey) {
		return AES256Encrypt(algorithm, PRNG_ALGORITHM_NATIVE_SHA1PRNG, strIn, strKey);
	}

	/**
	 * Encrypt string with given encrypt key by AES256
	 * @param algorithm             Algorithm
	 * @param prngAlgorithm         PRNG Algorithm
	 * @param strIn					String will be encrypted
	 * @param strKey				encrypt key
	 * @return						Encrypted result
	 */
	public static String AES256Encrypt(String algorithm, String prngAlgorithm, String strIn, String strKey) {
		return ConvertUtils.byteArrayToHexString(
				AES256Encrypt(algorithm, prngAlgorithm, ConvertUtils.convertToByteArray(strIn), strKey));
	}

	/**
	 * Encrypt byte arrays with given encrypt key by DES
	 * @param arrB					Byte arrays will be encrypted
	 * @param keyContent			Binary key content
	 * @return						Encrypt result
	 */
	public static byte[] DESEncrypt(byte[] arrB, byte[] keyContent) {
		return DESEncrypt(DES_CBC_PKCS5_PADDING, PRNG_ALGORITHM_NATIVE_SHA1PRNG, arrB, keyContent);
	}

	/**
	 * Encrypt byte arrays with given encrypt key by DES
	 * @param algorithm             Encrypt algorithm
	 * @param arrB					Byte arrays will be encrypted
	 * @param keyContent			Binary key content
	 * @return						Encrypt result
	 */
	public static byte[] DESEncrypt(String algorithm, byte[] arrB, byte[] keyContent) {
		return DESEncrypt(algorithm, PRNG_ALGORITHM_NATIVE_SHA1PRNG, arrB, keyContent);
	}

	/**
	 * Encrypt byte arrays with given encrypt key by DES
	 * @param algorithm             Encrypt algorithm
	 * @param prngAlgorithm         PRNG Algorithm
	 * @param arrB					Byte arrays will be encrypted
	 * @param keyContent			Binary key content
	 * @return						Encrypt result
	 */
	public static byte[] DESEncrypt(String algorithm, String prngAlgorithm, byte[] arrB, byte[] keyContent) {
		return EncryptData(algorithm, prngAlgorithm, arrB, null, keyContent, Globals.DEFAULT_VALUE_INT);
	}

	/**
	 * Encrypt byte arrays with given encrypt key by DES
	 * @param arrB						Byte arrays will be encrypted
	 * @param strKey					Encrypt key
	 * @return							Encrypt result
	 */
	public static byte[] DESEncrypt(byte[] arrB, String strKey) {
		return DESEncrypt(DES_CBC_PKCS5_PADDING, PRNG_ALGORITHM_NATIVE_SHA1PRNG, arrB, strKey);
	}

	/**
	 * Encrypt byte arrays with given encrypt key by DES
	 * @param algorithm             Encrypt algorithm
	 * @param arrB					Byte arrays will be encrypted
	 * @param strKey				Encrypt key
	 * @return						Encrypt result
	 */
	public static byte[] DESEncrypt(String algorithm, byte[] arrB, String strKey) {
		return DESEncrypt(algorithm, PRNG_ALGORITHM_NATIVE_SHA1PRNG, arrB, strKey);
	}

	/**
	 * Encrypt byte arrays with given encrypt key by DES
	 * @param algorithm             Encrypt algorithm
	 * @param prngAlgorithm         PRNG Algorithm
	 * @param arrB					Byte arrays will be encrypted
	 * @param strKey				Encrypt key
	 * @return						Encrypt result
	 */
	public static byte[] DESEncrypt(String algorithm, String prngAlgorithm, byte[] arrB, String strKey) {
		return EncryptData(algorithm, prngAlgorithm, arrB, null, strKey, Globals.DEFAULT_VALUE_INT);
	}

	/**
	 * Encrypt string with given encrypt key by DES
	 * @param strIn					String will be encrypted
	 * @param strKey				Encrypt key
	 * @return						Encrypt result
	 */
	public static String DESEncrypt(String strIn, String strKey) {
		return ConvertUtils.byteArrayToHexString(SecurityUtils.DESEncrypt(DES_CBC_PKCS5_PADDING,
				PRNG_ALGORITHM_NATIVE_SHA1PRNG, ConvertUtils.convertToByteArray(strIn), strKey));
	}

	/**
	 * Encrypt string with given encrypt key by DES
	 * @param algorithm             Encrypt algorithm
	 * @param strIn					String will be encrypted
	 * @param strKey				Encrypt key
	 * @return						Encrypt result
	 */
	public static String DESEncrypt(String algorithm, String strIn, String strKey) {
		return ConvertUtils.byteArrayToHexString(SecurityUtils.DESEncrypt(algorithm,
				PRNG_ALGORITHM_NATIVE_SHA1PRNG, ConvertUtils.convertToByteArray(strIn), strKey));
	}

	/**
	 * Encrypt string with given encrypt key by DES
	 * @param algorithm             Encrypt algorithm
	 * @param prngAlgorithm         PRNG Algorithm
	 * @param strIn				    String will be encrypted
	 * @param strKey				Encrypt key
	 * @return						Encrypt result
	 */
	public static String DESEncrypt(String algorithm, String prngAlgorithm, String strIn, String strKey) {
		return ConvertUtils.byteArrayToHexString(SecurityUtils.DESEncrypt(algorithm,
				prngAlgorithm, ConvertUtils.convertToByteArray(strIn), strKey));
	}

	/**
	 * Encrypt string with given key by RSA
	 * @param strIn						String will be encrypted
	 * @param key						RSA key
	 * @return							Encrypt result
	 */
	public static String RSAEncrypt(String strIn, Key key) {
		return ConvertUtils.byteArrayToHexString(SecurityUtils.EncryptData(RSA_PKCS1_PADDING, null,
				ConvertUtils.convertToByteArray(strIn), key, (String)null, Globals.DEFAULT_VALUE_INT));
	}

	/**
	 * Encrypt string with given key by RSA
	 * @param algorithm                 Encrypt algorithm
	 * @param strIn						String will be encrypted
	 * @param key						RSA key
	 * @return							Encrypt result
	 */
	public static String RSAEncrypt(String algorithm, String strIn, Key key) {
		return ConvertUtils.byteArrayToHexString(SecurityUtils.EncryptData(algorithm, null,
				ConvertUtils.convertToByteArray(strIn), key, (String)null, Globals.DEFAULT_VALUE_INT));
	}

	/**
	 * Encrypt byte arrays with given key by RSA
	 * @param arrB						Byte arrays will be encrypted
	 * @param key						RSA key
	 * @return							Encrypt result
	 */
	public static byte[] RSAEncrypt(byte[] arrB, Key key) {
		return SecurityUtils.EncryptData(RSA_PKCS1_PADDING, null, arrB, key, (String)null, Globals.DEFAULT_VALUE_INT);
	}

	/* Decrypt Data Method */

	/**
	 * Decrypt byte arrays with given decrypt key by AES128
	 * @param arrB						Byte arrays will be decrypted
	 * @param strKey					Decrypt key
	 * @return							Decrypted result
	 */
	public static byte[] AES128Decrypt(byte[] arrB, String strKey) {
		return DecryptData(AES_CBC_PKCS5_PADDING, PRNG_ALGORITHM_NATIVE_SHA1PRNG, arrB, null, strKey, 128);
	}

	/**
	 * Decrypt byte arrays with given algorithm and decrypt key by AES128
	 * @param algorithm                 Algorithm
	 * @param arrB						Byte arrays will be decrypted
	 * @param strKey					Decrypt key
	 * @return							Decrypted result
	 */
	public static byte[] AES128Decrypt(String algorithm, byte[] arrB, String strKey) {
		return DecryptData(algorithm, PRNG_ALGORITHM_NATIVE_SHA1PRNG, arrB, null, strKey, 128);
	}

	/**
	 * Decrypt byte arrays with given algorithm and decrypt key by AES128
	 * @param algorithm                 Algorithm
	 * @param prngAlgorithm             PRNG Algorithm
	 * @param arrB						Byte arrays will be decrypted
	 * @param strKey					Decrypt key
	 * @return							Decrypted result
	 */
	public static byte[] AES128Decrypt(String algorithm, String prngAlgorithm, byte[] arrB, String strKey) {
		return DecryptData(algorithm, prngAlgorithm, arrB, null, strKey, 128);
	}

	/**
	 * Decrypt byte arrays with given decrypt key by AES128
	 * @param arrB						Byte arrays will be decrypted
	 * @param keyContent				Binary key content
	 * @return							Decrypted result
	 */
	public static byte[] AES128Decrypt(byte[] arrB, byte[] keyContent) {
		return DecryptData(AES_CBC_PKCS5_PADDING, PRNG_ALGORITHM_NATIVE_SHA1PRNG, arrB, null, keyContent, 128);
	}

	/**
	 * Decrypt byte arrays with given algorithm and decrypt key by AES128
	 * @param algorithm                 Algorithm
	 * @param arrB						Byte arrays will be decrypted
	 * @param keyContent				Binary key content
	 * @return							Decrypted result
	 */
	public static byte[] AES128Decrypt(String algorithm, byte[] arrB, byte[] keyContent) {
		return DecryptData(algorithm, PRNG_ALGORITHM_NATIVE_SHA1PRNG, arrB, null, keyContent, 128);
	}

	/**
	 * Decrypt byte arrays with given algorithm and decrypt key by AES128
	 * @param algorithm                 Algorithm
	 * @param prngAlgorithm             PRNG Algorithm
	 * @param arrB						Byte arrays will be decrypted
	 * @param keyContent				Binary key content
	 * @return							Decrypted result
	 */
	public static byte[] AES128Decrypt(String algorithm, String prngAlgorithm, byte[] arrB, byte[] keyContent) {
		return DecryptData(algorithm, prngAlgorithm, arrB, null, keyContent, 128);
	}

	/**
	 * Decrypt string with given decrypt key by AES128
	 * @param strIn						String will be decrypted
	 * @param strKey					Decrypt key
	 * @return							Decrypted result
	 */
	public static String AES128Decrypt(String strIn, String strKey) {
		return AES128Decrypt(AES_CBC_PKCS5_PADDING, PRNG_ALGORITHM_NATIVE_SHA1PRNG, strIn, strKey);
	}

	/**
	 * Decrypt string with given algorithm and decrypt key by AES128
	 * @param algorithm                 Algorithm
	 * @param strIn						String will be decrypted
	 * @param strKey					Decrypt key
	 * @return							Decrypted result
	 */
	public static String AES128Decrypt(String algorithm, String strIn, String strKey) {
		return AES128Decrypt(algorithm, PRNG_ALGORITHM_NATIVE_SHA1PRNG, strIn, strKey);
	}

	/**
	 * Decrypt string with given algorithm and decrypt key by AES128
	 * @param algorithm                 Algorithm
	 * @param prngAlgorithm             PRNG Algorithm
	 * @param strIn						String will be decrypted
	 * @param strKey					Decrypt key
	 * @return							Decrypted result
	 */
	public static String AES128Decrypt(String algorithm, String prngAlgorithm, String strIn, String strKey) {
		try {
			byte[] decryptData = DecryptData(algorithm, prngAlgorithm,
					ConvertUtils.hexStrToByteArr(strIn), null, strKey, 128);
			return ConvertUtils.convertToString(decryptData);
		} catch (Exception e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Decrypt data error! ", e);
			}
		}
		return null;
	}

	/**
	 * Decrypt byte arrays with given decrypt key by AES256
	 * @param arrB						Byte arrays will be decrypted
	 * @param strKey					Decrypt key
	 * @return							Decrypted result
	 */
	public static byte[] AES256Decrypt(byte[] arrB, String strKey) {
		return DecryptData(AES_CBC_PKCS5_PADDING, PRNG_ALGORITHM_NATIVE_SHA1PRNG, arrB, null, strKey, 256);
	}

	/**
	 * Decrypt byte arrays with given algorithm and decrypt key by AES256
	 * @param algorithm                 Algorithm
	 * @param arrB						Byte arrays will be decrypted
	 * @param strKey					Decrypt key
	 * @return							Decrypted result
	 */
	public static byte[] AES256Decrypt(String algorithm, byte[] arrB, String strKey) {
		return DecryptData(algorithm, PRNG_ALGORITHM_NATIVE_SHA1PRNG, arrB, null, strKey, 256);
	}

	/**
	 * Decrypt byte arrays with given algorithm and decrypt key by AES256
	 * @param algorithm                 Algorithm
	 * @param prngAlgorithm             PRNG Algorithm
	 * @param arrB						Byte arrays will be decrypted
	 * @param strKey					Decrypt key
	 * @return							Decrypted result
	 */
	public static byte[] AES256Decrypt(String algorithm, String prngAlgorithm, byte[] arrB, String strKey) {
		return DecryptData(algorithm, prngAlgorithm, arrB, null, strKey, 256);
	}

	/**
	 * Decrypt byte arrays with given decrypt key by AES256
	 * @param arrB						Byte arrays will be decrypted
	 * @param keyContent			    Decrypt key
	 * @return							Decrypted result
	 */
	public static byte[] AES256Decrypt(byte[] arrB, byte[] keyContent) {
		return DecryptData(AES_CBC_PKCS5_PADDING, PRNG_ALGORITHM_NATIVE_SHA1PRNG, arrB, null, keyContent, 256);
	}

	/**
	 * Decrypt byte arrays with given algorithm and decrypt key by AES256
	 * @param algorithm                 Algorithm
	 * @param arrB						Byte arrays will be decrypted
	 * @param keyContent			    Decrypt key
	 * @return							Decrypted result
	 */
	public static byte[] AES256Decrypt(String algorithm, byte[] arrB, byte[] keyContent) {
		return DecryptData(algorithm, PRNG_ALGORITHM_NATIVE_SHA1PRNG, arrB, null, keyContent, 256);
	}

	/**
	 * Decrypt byte arrays with given algorithm and decrypt key by AES256
	 * @param algorithm                 Algorithm
	 * @param prngAlgorithm             PRNG Algorithm
	 * @param arrB						Byte arrays will be decrypted
	 * @param keyContent			    Decrypt key
	 * @return							Decrypted result
	 */
	public static byte[] AES256Decrypt(String algorithm, String prngAlgorithm, byte[] arrB, byte[] keyContent) {
		return DecryptData(algorithm, prngAlgorithm, arrB, null, keyContent, 256);
	}

	/**
	 * Decrypt string with given decrypt key by AES256
	 * @param strIn						String will be decrypted
	 * @param strKey					Decrypt key
	 * @return							Decrypted result
	 */
	public static String AES256Decrypt(String strIn, String strKey) {
		return AES256Decrypt(AES_CBC_PKCS5_PADDING, PRNG_ALGORITHM_NATIVE_SHA1PRNG, strIn, strKey);
	}

	/**
	 * Decrypt string with given algorithm and decrypt key by AES256
	 * @param algorithm                 Algorithm
	 * @param strIn						String will be decrypted
	 * @param strKey					Decrypt key
	 * @return							Decrypted result
	 */
	public static String AES256Decrypt(String algorithm, String strIn, String strKey) {
		return AES256Decrypt(algorithm, PRNG_ALGORITHM_NATIVE_SHA1PRNG, strIn, strKey);
	}

	/**
	 * Decrypt string with given algorithm and decrypt key by AES256
	 * @param algorithm                 Algorithm
	 * @param prngAlgorithm             PRNG Algorithm
	 * @param strIn						String will be decrypted
	 * @param strKey					Decrypt key
	 * @return							Decrypted result
	 */
	public static String AES256Decrypt(String algorithm, String prngAlgorithm, String strIn, String strKey) {
		try {
			byte[] decryptData = DecryptData(algorithm, prngAlgorithm, ConvertUtils.hexStrToByteArr(strIn), null, strKey, 256);
			return ConvertUtils.convertToString(decryptData);
		} catch (Exception e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Decrypt data error! ", e);
			}
		}
		return null;
	}

	/**
	 * Decrypt string with given decrypt key by DES
	 * @param strIn						String will be decrypted
	 * @param strKey					Decrypt key
	 * @return							Decrypt result
	 */
	public static String DESDecrypt(String strIn, String strKey) {
		return ConvertUtils.convertToString(DecryptData(DES_CBC_PKCS5_PADDING, PRNG_ALGORITHM_NATIVE_SHA1PRNG,
				ConvertUtils.hexStrToByteArr(strIn), null, strKey, Globals.DEFAULT_VALUE_INT));
	}

	/**
	 * Decrypt string with given decrypt key by DES
	 * @param algorithm                 Algorithm
	 * @param strIn						String will be decrypted
	 * @param strKey					Decrypt key
	 * @return							Decrypt result
	 */
	public static String DESDecrypt(String algorithm, String strIn, String strKey) {
		return ConvertUtils.convertToString(DecryptData(algorithm, PRNG_ALGORITHM_NATIVE_SHA1PRNG,
				ConvertUtils.hexStrToByteArr(strIn), null, strKey, Globals.DEFAULT_VALUE_INT));
	}

	/**
	 * Decrypt string with given decrypt key by DES
	 * @param algorithm                 Algorithm
	 * @param prngAlgorithm             PRNG Algorithm
	 * @param strIn						String will be decrypted
	 * @param strKey					Decrypt key
	 * @return							Decrypt result
	 */
	public static String DESDecrypt(String algorithm, String prngAlgorithm, String strIn, String strKey) {
		return ConvertUtils.convertToString(DecryptData(algorithm, prngAlgorithm,
				ConvertUtils.hexStrToByteArr(strIn), null, strKey, Globals.DEFAULT_VALUE_INT));
	}

	/**
	 * Decrypt string with given key by RSA
	 * @param strIn						String will be encrypted
	 * @param key						RSA key
	 * @return							Decrypt result
	 */
	public static String RSADecrypt(String strIn, Key key) {
		return ConvertUtils.convertToString(DecryptData(RSA_PKCS1_PADDING, null,
				ConvertUtils.hexStrToByteArr(strIn), key, (String)null, Globals.DEFAULT_VALUE_INT));
	}

	/**
	 * Decrypt string with given key by RSA
	 * @param algorithm                 Algorithm
	 * @param strIn						String will be encrypted
	 * @param key						RSA key
	 * @return							Decrypt result
	 */
	public static String RSADecrypt(String algorithm, String strIn, Key key) {
		return ConvertUtils.convertToString(DecryptData(algorithm, null,
				ConvertUtils.hexStrToByteArr(strIn), key, (String)null, Globals.DEFAULT_VALUE_INT));
	}

	/**
	 * Decrypt byte array with given key by RSA
	 * @param arrB						Byte array will be encrypted
	 * @param key						RSA key
	 * @return							Decrypt result
	 */
	public static byte[] RSADecrypt(byte[] arrB, Key key) {
		return DecryptData(RSA_PKCS1_PADDING, null, arrB, key, (String)null, Globals.DEFAULT_VALUE_INT);
	}

	/**
	 * Decrypt byte array with given key by RSA
	 * @param algorithm                 Algorithm
	 * @param arrB						Byte array will be encrypted
	 * @param key						RSA key
	 * @return							Decrypt result
	 */
	public static byte[] RSADecrypt(String algorithm, byte[] arrB, Key key) {
		return DecryptData(algorithm, null, arrB, key, (String)null, Globals.DEFAULT_VALUE_INT);
	}

	/**
	 * Generate RSA key pair with default key size: 1024
	 * @return	Key pair
	 * @throws Exception	Any exception of this operate will be throw up
	 */
	public static KeyPair RSAKeyPair() throws Exception {
		return SecurityUtils.KeyPair("RSA", 1024);
	}

	/**
	 * Generate RSA key pair with given key size
	 * @param keySize	key size
	 * @return	Key pair
	 * @throws Exception	Any exception of this operate will be throw up
	 */
	public static KeyPair RSAKeyPair(int keySize) throws Exception {
		return SecurityUtils.KeyPair("RSA", keySize);
	}

	/**
	 * Generate DSA key pair with default key size: 1024
	 * @return	Key pair
	 * @throws Exception	key size invalid or 
	 * if a KeyPairGeneratorSpi implementation for the specified algorithm is not available from the specified Provider object.
	 * if the specified provider is null.
	 */
	public static KeyPair DSAKeyPair() throws Exception {
		return SecurityUtils.KeyPair("DSA", 1024);
	}

	/**
	 * Generate DSA key pair with given key size
	 * @param keySize	key size
	 * @return	Key pair
	 * @throws Exception	key size invalid or 
	 * if a KeyPairGeneratorSpi implementation for the specified algorithm is not available from the specified Provider object.
	 * if the specified provider is null.
	 */
	public static KeyPair DSAKeyPair(int keySize) throws Exception {
		return SecurityUtils.KeyPair("DSA", keySize);
	}

	/**
	 * Generate DSA public key with given key content
	 * @param keyContent	key content
	 * @return	DSA public key object
	 * @throws InvalidKeySpecException if the given key specification is inappropriate for this key factory to produce a public key.
	 * @throws NoSuchAlgorithmException if no Provider supports a KeyFactorySpi implementation for the specified algorithm.
	 */
	public static PublicKey DSAPublicKey(byte[] keyContent) 
			throws InvalidKeySpecException, NoSuchAlgorithmException {
		return KeyFactory.getInstance("DSA").generatePublic(new X509EncodedKeySpec(keyContent));
	}
	
	/**
	 * Generate DSA private key with given key content
	 * @param keyContent	key content
	 * @return	DSA private key object
	 * @throws InvalidKeySpecException if the given key specification is inappropriate for this key factory to produce a public key.
	 * @throws NoSuchAlgorithmException if no Provider supports a KeyFactorySpi implementation for the specified algorithm.
	 */
	public static PrivateKey DSAPrivateKey(byte[] keyContent) 
			throws InvalidKeySpecException, NoSuchAlgorithmException {
		return KeyFactory.getInstance("DSA").generatePrivate(new PKCS8EncodedKeySpec(keyContent));
	}

	/**
	 * Generate RSA public key with given key content
	 * @param keyContent	key content
	 * @return	RSA public key object
	 * @throws InvalidKeySpecException if the given key specification is inappropriate for this key factory to produce a public key.
	 * @throws NoSuchAlgorithmException if no Provider supports a KeyFactorySpi implementation for the specified algorithm.
	 */
	public static PublicKey RSAPublicKey(byte[] keyContent) 
			throws InvalidKeySpecException, NoSuchAlgorithmException {
		return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(keyContent));
	}

	/**
	 * Generate RSA public key with given modulus and exponent
	 * @param modulus	modulus
	 * @param exponent	exponent
	 * @return	RSA public key object
	 * @throws InvalidKeySpecException if the given key specification is inappropriate for this key factory to produce a public key.
	 * @throws NoSuchAlgorithmException if no Provider supports a KeyFactorySpi implementation for the specified algorithm.
	 */
	public static PublicKey RSAPublicKey(BigInteger modulus, BigInteger exponent) 
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		KeyFactory keyFactory = KeyFactory.getInstance("RSA", new BouncyCastleProvider());
		RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(modulus, exponent);
		
		return keyFactory.generatePublic(publicKeySpec);
	}

	/**
	 * Generate RSA private key with given key content
	 * @param keyContent	key content
	 * @return	RSA private key object
	 * @throws InvalidKeySpecException if the given key specification is inappropriate for this key factory to produce a public key.
	 * @throws NoSuchAlgorithmException if no Provider supports a KeyFactorySpi implementation for the specified algorithm.
	 */
	public static PrivateKey RSAPrivateKey(byte[] keyContent) 
			throws InvalidKeySpecException, NoSuchAlgorithmException {
		return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(keyContent));
	}

	/**
	 * Generate RSA private key with given modulus and exponent
	 * @param modulus	modulus
	 * @param exponent	exponent
	 * @return	RSA private key object
	 * @throws InvalidKeySpecException if the given key specification is inappropriate for this key factory to produce a public key.
	 * @throws NoSuchAlgorithmException if no Provider supports a KeyFactorySpi implementation for the specified algorithm.
	 */
	public static PrivateKey RSAPrivateKey(BigInteger modulus, BigInteger exponent) 
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		KeyFactory keyFactory = KeyFactory.getInstance("RSA", new BouncyCastleProvider());
		RSAPrivateKeySpec privateKeySpec = new RSAPrivateKeySpec(modulus, exponent);
		
		return keyFactory.generatePrivate(privateKeySpec);
	}
	
	/**
	 * Signature data with DSA
	 * @param privateKey	Signature private key
	 * @param message		Signature datas
	 * @return				Signature info
	 */
	public static byte[] signDataWithDSA(PrivateKey privateKey, String message) {
		return signData(privateKey, message.getBytes(Charset.defaultCharset()), "SHA256withDSA");
	}

	/**
	 * Signature data with DSA
	 * @param privateKey	Signature private key
	 * @param filePath		Signature file path
	 * @return				Signature info
	 */
	public static byte[] signFileWithDSA(PrivateKey privateKey, String filePath) {
		return signFile(privateKey, filePath, "SHA256withDSA");
	}

	/**
	 * Signature data with DSA
	 * @param privateKey	Signature private key
	 * @param datas			Signature datas
	 * @return				Signature info
	 */
	public static byte[] signDataWithDSA(PrivateKey privateKey, byte[] datas) {
		return signData(privateKey, datas, "SHA256withDSA");
	}
	
	/**
	 * Verify signature info is valid
	 * @param publicKey		Verify public key
	 * @param datas			Signature datas
	 * @param signature		Signature info
	 * @return				Verify result
	 */
	public static boolean verifyDSASign(PublicKey publicKey, byte[] datas, byte[] signature) {
		return verifySign(publicKey, datas, signature, "SHA256withDSA");
	}

	/**
	 * Verify signature info is valid
	 * @param publicKey		Verify public key
	 * @param filePath		Signature file path
	 * @param signature		Signature info
	 * @return				Verify result
	 */
	public static boolean verifyDSASign(PublicKey publicKey, String filePath, byte[] signature) {
		return verifySign(publicKey, filePath, signature, "SHA256withDSA");
	}

	/**
	 * Signature data with RSA
	 * @param privateKey	Signature private key
	 * @param message		Signature datas
	 * @return				Signature info
	 */
	public static byte[] signDataWithRSA(PrivateKey privateKey, String message) {
		return signData(privateKey, message.getBytes(Charset.defaultCharset()), "SHA256withRSA");
	}

	/**
	 * Signature data with RSA
	 * @param privateKey	Signature private key
	 * @param datas			Signature datas
	 * @return				Signature info
	 */
	public static byte[] signDataWithRSA(PrivateKey privateKey, byte[] datas) {
		return signData(privateKey, datas, "SHA256withRSA");
	}

	/**
	 * Signature data with RSA
	 * @param privateKey	Signature private key
	 * @param filePath		Signature file path
	 * @return				Signature info
	 */
	public static byte[] signFileWithRSA(PrivateKey privateKey, String filePath) {
		return signFile(privateKey, filePath, "SHA256withRSA");
	}

	/**
	 * Signature data with HmacMD5
	 * @param keyBytes		sign key bytes
	 * @param signDatas		sign data bytes
	 * @return				signature datas
	 */
	public static byte[] signDataByHmacMD5(byte[] keyBytes, byte[] signDatas) {
		return Hmac(keyBytes, signDatas, "HmacMD5");
	}

	/**
	 * Signature data with HmacSHA1
	 * @param keyBytes		sign key bytes
	 * @param signDatas		sign data bytes
	 * @return				signature datas
	 */
	public static byte[] signDataByHmacSHA1(byte[] keyBytes, byte[] signDatas) {
		return Hmac(keyBytes, signDatas, "HmacSHA1");
	}

	/**
	 * Signature data with HmacSHA256
	 * @param keyBytes		sign key bytes
	 * @param signDatas		sign data bytes
	 * @return				signature datas
	 */
	public static byte[] signDataByHmacSHA256(byte[] keyBytes, byte[] signDatas) {
		return Hmac(keyBytes, signDatas, "HmacSHA256");
	}

	/**
	 * Signature data with HmacSHA384
	 * @param keyBytes		sign key bytes
	 * @param signDatas		sign data bytes
	 * @return				signature datas
	 */
	public static byte[] signDataByHmacSHA384(byte[] keyBytes, byte[] signDatas) {
		return Hmac(keyBytes, signDatas, "HmacSHA384");
	}

	/**
	 * Signature data with HmacSHA512
	 * @param keyBytes		sign key bytes
	 * @param signDatas		sign data bytes
	 * @return				signature datas
	 */
	public static byte[] signDataByHmacSHA512(byte[] keyBytes, byte[] signDatas) {
		return Hmac(keyBytes, signDatas, "HmacSHA512");
	}

	/**
	 * Signature data with HmacSHA512-224
	 * @param keyBytes		sign key bytes
	 * @param signDatas		sign data bytes
	 * @return				signature datas
	 */
	public static byte[] signDataByHmacSHA512_224(byte[] keyBytes, byte[] signDatas) {
		return Hmac(keyBytes, signDatas, "HmacSHA512/224");
	}

	/**
	 * Signature data with HmacSHA512-256
	 * @param keyBytes		sign key bytes
	 * @param signDatas		sign data bytes
	 * @return				signature datas
	 */
	public static byte[] signDataByHmacSHA512_256(byte[] keyBytes, byte[] signDatas) {
		return Hmac(keyBytes, signDatas, "HmacSHA512/256");
	}

	/**
	 * Verify signature info is valid
	 * @param publicKey		Verify public key
	 * @param datas			Signature datas
	 * @param signature		Signature info
	 * @return				Verify result
	 */
	public static boolean verifyRSASign(PublicKey publicKey, byte[] datas, byte[] signature) {
		return verifySign(publicKey, datas, signature, "SHA256withRSA");
	}

	/**
	 * Verify signature info is valid
	 * @param publicKey		Verify public key
	 * @param filePath		Signature file path
	 * @param signature		Signature info
	 * @return				Verify result
	 */
	public static boolean verifyRSASign(PublicKey publicKey, String filePath, byte[] signature) {
		return verifySign(publicKey, filePath, signature, "SHA256withRSA");
	}

	/**
	 * Encrypt byte arrays with given algorithm, PRNG algorithm, key size and encrypt key
	 * @param algorithm             Encrypt algorithm
	 * @param prngAlgorithm         PRNG Algorithm
	 * @param arrB                  Array data
	 * @param key                   RSA Key
	 * @param strKey                Given encrypt key
	 * @param keySize               Key Size
	 * @return                      Encrypted Data
	 */
	private static byte[] EncryptData(String algorithm, String prngAlgorithm, byte[] arrB, Key key, String strKey, int keySize) {
		try {
			return EncryptData(algorithm, prngAlgorithm, arrB, key,
					strKey == null ? null : strKey.getBytes(Globals.DEFAULT_ENCODING),
					keySize);
		} catch (Exception e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Encrypt data error! ", e);
			}
		}
		return new byte[0];
	}

	/**
	 * Encrypt byte arrays with given algorithm, PRNG algorithm, key size and encrypt key
	 * @param algorithm             Encrypt algorithm
	 * @param prngAlgorithm         PRNG Algorithm
	 * @param arrB                  Array data
	 * @param key                   RSA Key
	 * @param keyData               Given binary key
	 * @param keySize               Key Size
	 * @return                      Encrypted Data
	 */
	private static byte[] EncryptData(String algorithm, String prngAlgorithm, byte[] arrB, Key key, byte[] keyData, int keySize) {
		try {
			if (algorithm.startsWith("AES")) {
				return initCipher(algorithm, Cipher.ENCRYPT_MODE,
						CipherKey.AESKey(keyData, prngAlgorithm, keySize)).doFinal(arrB);
			} else if (algorithm.startsWith("DESede")) {
				return initCipher(algorithm, Cipher.ENCRYPT_MODE,
						CipherKey.DESKey(keyData, prngAlgorithm)).doFinal(arrB);
			} else if (algorithm.startsWith("DES")) {
				return initCipher(algorithm, Cipher.ENCRYPT_MODE,
						CipherKey.DESKey(keyData, prngAlgorithm)).doFinal(arrB);
			} else if (algorithm.startsWith("RSA")) {
				Cipher cipher = SecurityUtils.initCipher(algorithm, Cipher.ENCRYPT_MODE, CipherKey.RSAKey(key));
				int blockSize = cipher.getBlockSize();
				int outputSize = cipher.getOutputSize(arrB.length);

				int leavedSize = arrB.length % blockSize;

				int blocksSize = leavedSize != 0 ? arrB.length / blockSize + 1
						: arrB.length / blockSize;

				byte[] byteArray = new byte[outputSize * blocksSize];
				int i = 0;

				while (arrB.length - i * blockSize > 0) {
					cipher.doFinal(arrB, i * blockSize, Math.min(arrB.length - i * blockSize, blockSize), byteArray, i * outputSize);
					i++;
				}
				return byteArray;
			} else {
				throw new Exception("Unknown algorithm! ");
			}
		} catch (Exception e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Encrypt data error! ", e);
			}
		}
		return new byte[0];
	}

	/**
	 * Encrypt byte arrays with given algorithm, PRNG algorithm, key size and encrypt key
	 * @param algorithm             Encrypt algorithm
	 * @param prngAlgorithm         PRNG Algorithm
	 * @param arrB                  Array data
	 * @param key                   RSA Key
	 * @param strKey                Given encrypt key
	 * @param keySize               Key Size
	 * @return                      Encrypted Data
	 */
	private static byte[] DecryptData(String algorithm, String prngAlgorithm, byte[] arrB, Key key, String strKey, int keySize) {
		try {
			return DecryptData(algorithm, prngAlgorithm, arrB, key,
					strKey == null ? null : strKey.getBytes(Globals.DEFAULT_ENCODING),
					keySize);
		} catch (Exception e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Decrypt data error! ", e);
			}
		}
		return new byte[0];
	}

	/**
	 * Encrypt byte arrays with given algorithm, PRNG algorithm, key size and encrypt key
	 * @param algorithm             Encrypt algorithm
	 * @param prngAlgorithm         PRNG Algorithm
	 * @param arrB                  Array data
	 * @param key                   RSA Key
	 * @param keyData               Given binary key
	 * @param keySize               Key Size
	 * @return                      Encrypted Data
	 */
	private static byte[] DecryptData(String algorithm, String prngAlgorithm, byte[] arrB, Key key, byte[] keyData, int keySize) {
		try {
			if (algorithm.startsWith("AES")) {
				return initCipher(algorithm, Cipher.DECRYPT_MODE,
						CipherKey.AESKey(keyData, prngAlgorithm, keySize)).doFinal(arrB);
			} else if (algorithm.startsWith("DESede")) {
				return initCipher(algorithm, Cipher.DECRYPT_MODE,
						CipherKey.DESKey(keyData, prngAlgorithm)).doFinal(arrB);
			} else if (algorithm.startsWith("DES")) {
				return initCipher(algorithm, Cipher.DECRYPT_MODE,
						CipherKey.DESKey(keyData, prngAlgorithm)).doFinal(arrB);
			} else if (algorithm.startsWith("RSA")) {
				Cipher cipher = SecurityUtils.initCipher(algorithm, Cipher.DECRYPT_MODE, CipherKey.RSAKey(key));
				int blockSize = cipher.getBlockSize();
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

				int j = 0;

				while (arrB.length - j * blockSize > 0) {
					byteArrayOutputStream.write(cipher.doFinal(arrB, j * blockSize, blockSize));
					j++;
				}

				return byteArrayOutputStream.toByteArray();
			} else {
				throw new Exception("Unknown algorithm! ");
			}
		} catch (Exception e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Decrypt data error! ", e);
			}
		}
		return new byte[0];
	}

	/**
	 * Generate key pair by given algorithm
	 * @param algorithm     Algorithm
	 * @param keySize       Key size
	 * @return              Generated key pair
	 * @throws Exception    Key size invalid or other exceptions
	 */
	private static KeyPair KeyPair(String algorithm, int keySize) throws Exception {
		if (keySize % 128 != 0) {
			throw new Exception("Key size is invalid");
		}
		//	Initialize keyPair instance
		KeyPairGenerator keyPairGenerator = 
			KeyPairGenerator.getInstance(algorithm, new BouncyCastleProvider());
		keyPairGenerator.initialize(keySize, new SecureRandom());
		
		//	Generate keyPair
		return keyPairGenerator.generateKeyPair();
	}

	/**
	 * Calculate Hmac value by given algorithm
	 * @param keyBytes      Signature key bytes
	 * @param signDatas     Signature Data
	 * @param algorithm     Algorithm
	 * @return              Hmac bytes
	 */
	private static byte[] Hmac(byte[] keyBytes, byte[] signDatas, String algorithm) {
		try {
			Mac hmac = Mac.getInstance(algorithm);
			hmac.init(new SecretKeySpec(keyBytes, algorithm));
			return hmac.doFinal(signDatas);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Signature data by given private key and algorithm
	 * @param privateKey        Signature using private key
	 * @param datas             Signature data
	 * @param algorithm         Algorithm
	 * @return                  Signature value bytes
	 */
	private static byte[] signData(PrivateKey privateKey, byte[] datas, String algorithm) {
		try {
			Signature signature = Signature.getInstance(algorithm);
			signature.initSign(privateKey);
			signature.update(datas);
			
			return signature.sign();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Signature data by given private key and algorithm
	 * @param privateKey        Signature using private key
	 * @param filePath          Signature file path
	 * @param algorithm         Algorithm
	 * @return                  Signature value bytes
	 */
	private static byte[] signFile(PrivateKey privateKey, String filePath, String algorithm) {
		if (FileUtils.isExists(filePath)) {
			InputStream inputStream = null;
			try {
				Signature signature = Signature.getInstance(algorithm);
				signature.initSign(privateKey);
				inputStream = FileUtils.loadFile(filePath);

				byte[] buffer = new byte[Globals.DEFAULT_BUFFER_SIZE];
				int readLength;
				while ((readLength = inputStream.read(buffer)) != -1) {
					signature.update(buffer, 0, readLength);
				}

				return signature.sign();
			} catch (Exception e) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Stack message: ", e);
				}
			} finally {
				IOUtils.closeStream(inputStream);
			}
		}
		return null;
	}

	/**
	 * Verify signature data
	 * @param publicKey     Verify using public key
	 * @param datas         Signature data
	 * @param signature     Signature value bytes
	 * @param algorithm     Algorithm
	 * @return              Verify result
	 */
	private static boolean verifySign(PublicKey publicKey,
			byte[] datas, byte[] signature, String algorithm) {
		try {
			Signature signInstance = Signature.getInstance(algorithm);
			
			signInstance.initVerify(publicKey);
			signInstance.update(datas);
			
			return signInstance.verify(signature);
		} catch (Exception e) {
			return Globals.DEFAULT_VALUE_BOOLEAN;
		}
	}

	/**
	 * Verify signature data
	 * @param publicKey     Verify using public key
	 * @param filePath		Signature file path
	 * @param signature     Signature value bytes
	 * @param algorithm     Algorithm
	 * @return              Verify result
	 */
	private static boolean verifySign(PublicKey publicKey,
	                                  String filePath, byte[] signature, String algorithm) {
		if (FileUtils.isExists(filePath)) {
			InputStream inputStream = null;
			try {
				Signature signInstance = Signature.getInstance(algorithm);
				signInstance.initVerify(publicKey);
				inputStream = FileUtils.loadFile(filePath);

				byte[] buffer = new byte[Globals.DEFAULT_BUFFER_SIZE];
				int readLength;
				while ((readLength = inputStream.read(buffer)) != -1) {
					signInstance.update(buffer, 0, readLength);
				}

				return signInstance.verify(signature);
			} catch (Exception e) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Stack message: ", e);
				}
			} finally {
				IOUtils.closeStream(inputStream);
			}
		}
		return Globals.DEFAULT_VALUE_BOOLEAN;
	}

	/**
	 * Get digest value
	 * @param source		Input source
	 * @param algorithm	Calc algorithm
	 * @return calc value
	 */
	private static String digestEncode(@Nonnull Object source, @Nonnull String algorithm) {
		MessageDigest messageDigest;
		
		//	Initialize MessageDigest Instance
		try {
			messageDigest = MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException ex) {
			LOGGER.error("Initialize failed, maybe the MessageDigest does not support " + algorithm + "!", ex);
			return Globals.DEFAULT_VALUE_STRING;
		}

		if (source instanceof File) {
			File file = (File)source;
			if (file.exists() && file.isFile()) {
				RandomAccessFile randomAccessFile = null;
				try {
					randomAccessFile = new RandomAccessFile(file, Globals.READ_MODE);
					byte[] readBuffer = new byte[Globals.READ_FILE_BUFFER_SIZE];
					int readLength;
					while ((readLength = randomAccessFile.read(readBuffer)) > 0) {
						messageDigest.update(readBuffer, 0, readLength);
					}
				} catch (Exception e) {
					LOGGER.error("Message digest error! ", e);
					return null;
				} finally {
					IOUtils.closeStream(randomAccessFile);
				}
			} else {
				LOGGER.error("File does not exists" + file.getAbsolutePath());
				return "";
			}
		} else {
			byte[] tempBytes = ConvertUtils.convertToByteArray(source);
			if (tempBytes != null) {
				messageDigest.update(tempBytes);
			}
		}
		return ConvertUtils.byteArrayToHexString(messageDigest.digest());
	}

	/**
	 * Generate AES Key Instance
	 * @param prngAlgorithm     PRNG Algorithm
	 * @param keyContent        key bytes
	 * @param keySize           Key size
	 * @return              Key Instance
	 * @throws NoSuchAlgorithmException     Algorithm not supported
	 */
	private static Key generateAESKey(String prngAlgorithm,
	                                  byte[] keyContent, int keySize) throws NoSuchAlgorithmException {
		if (keyContent == null) {
			return null;
		}

		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		SecureRandom secureRandom = SecureRandom.getInstance(prngAlgorithm);
		secureRandom.setSeed(keyContent);
		keyGenerator.init(keySize, secureRandom);

		SecretKey secretKey = keyGenerator.generateKey();

		return new SecretKeySpec(secretKey.getEncoded(), "AES");
	}

	/**
	 * Generate DES Key Instance
	 * @param keyContent    Encrypt key bytes
	 * @return              Key Instance
	 * @throws NoSuchAlgorithmException     Algorithm not supported
	 */
	private static Key generateDESKey(byte[] keyContent) throws NoSuchAlgorithmException {
		if (keyContent == null) {
			return null;
		}

		KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
		keyGenerator.init(new SecureRandom(keyContent));
		return keyGenerator.generateKey();
	}

	/**
	 * Generate 3DES Key Instance
	 * @param keyContent    Encrypt key bytes
	 * @return              Key Instance
	 * @throws NoSuchAlgorithmException     Algorithm not supported
	 */
	private static Key generateTripleDESKey(byte[] keyContent) throws NoSuchAlgorithmException {
		if (keyContent == null) {
			return null;
		}

		KeyGenerator keyGenerator = KeyGenerator.getInstance("DESede");
		keyGenerator.init(new SecureRandom(keyContent));
		return keyGenerator.generateKey();
	}

	/**
	 * Initialize Cipher Instance
	 * @param algorithm         Algorithm
	 * @param cipherMode        Cipher Mode
	 * @param cipherKey         CipherKey Instance
	 * @return                  Cipher Instance
	 * @throws NoSuchAlgorithmException     If Algorithm not supported
	 * @throws NoSuchPaddingException       If padding type not supported
	 * @throws InvalidAlgorithmParameterException   If IV data invalid
	 * @throws InvalidKeyException          If RSA Key invalid
	 */
	private static Cipher initCipher(String algorithm, int cipherMode, CipherKey cipherKey)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException,
			InvalidKeyException {
		IvParameterSpec ivParameterSpec = null;
		byte[] keyContent = cipherKey.getKeyContent();
		byte[] ivContent = new byte[0];
		Provider provider = null;
		Key key;

		if (algorithm.startsWith("AES")) {
			key = generateAESKey(cipherKey.getPrngAlgorithm(), keyContent, cipherKey.getKeySize());
			if (!algorithm.startsWith("AES/ECB")) {
				ivContent = new byte[16];
			}
			if (algorithm.endsWith("PKCS7Padding")) {
				provider = new BouncyCastleProvider();
			}
		} else if (algorithm.startsWith("DESede")) {
			key = generateTripleDESKey(keyContent);
			if (!algorithm.startsWith("DESede/ECB")) {
				ivContent = new byte[8];
			}
		} else if (algorithm.startsWith("DES")) {
			key = generateDESKey(keyContent);
			if (!algorithm.startsWith("DES/ECB")) {
				ivContent = new byte[8];
			}
		} else {
			provider = new BouncyCastleProvider();
			key = cipherKey.getRsaKey();
		}

		if (ivContent.length > 0) {
			System.arraycopy(SecurityUtils.MD5(keyContent).getBytes(Charset.defaultCharset()),
					0, ivContent, 0, ivContent.length);
			ivParameterSpec = new IvParameterSpec(ivContent);
		}

		Cipher cipher = (provider == null) ? Cipher.getInstance(algorithm) : Cipher.getInstance(algorithm, provider);
		cipher.init(cipherMode, key, ivParameterSpec);

		return cipher;
	}

	/**
	 * Cipher key
	 */
	private static final class CipherKey {

		//  RSA key(PublicKey or PrivateKey)
		private final Key rsaKey;
		//  AES/DES/3DES Key bytes
		private final byte[] keyContent;
		//  PRNG Algorithm
		private final String prngAlgorithm;
		//  AES Key Size
		private final int keySize;

		/**
		 * Constructor for CipherKey
		 * @param rsaKey            RSA Key Instance
		 * @param keyContent        AES/DES/3DES Key bytes
		 * @param prngAlgorithm     PRNG Algorithm
		 * @param keySize           AES Key Size
		 */
		private CipherKey(Key rsaKey, byte[] keyContent, String prngAlgorithm, int keySize) {
			this.rsaKey = rsaKey;
			this.keyContent = keyContent;
			this.prngAlgorithm = prngAlgorithm;
			this.keySize = keySize;
		}

		/**
		 * Generate DES CipherKey Instance
		 * @param strKey    Key content
		 * @return          CipherKey Instance
		 * @throws UnsupportedEncodingException     Unsupported Encoding
		 */
		public static CipherKey DESKey(String strKey) throws UnsupportedEncodingException {
			return new CipherKey(null, strKey.getBytes(Globals.DEFAULT_ENCODING),
					PRNG_ALGORITHM_NATIVE_SHA1PRNG, Globals.DEFAULT_VALUE_INT);
		}

		/**
		 * Generate DES CipherKey Instance
		 * @param keyContent        Key content
		 * @param prngAlgorithm     PRNG Algorithm
		 * @return                  CipherKey Instance
		 */
		public static CipherKey DESKey(byte[] keyContent, String prngAlgorithm) {
			return new CipherKey(null, keyContent, prngAlgorithm, Globals.DEFAULT_VALUE_INT);
		}

		/**
		 * Generate DES CipherKey Instance
		 * @param strKey            Key content
		 * @param prngAlgorithm     PRNG Algorithm
		 * @return                  CipherKey Instance
		 * @throws UnsupportedEncodingException     Unsupported Encoding
		 */
		public static CipherKey DESKey(String strKey, String prngAlgorithm) throws UnsupportedEncodingException {
			return new CipherKey(null, strKey.getBytes(Globals.DEFAULT_ENCODING),
					prngAlgorithm, Globals.DEFAULT_VALUE_INT);
		}

		/**
		 * Generate AES CipherKey Instance
		 * @param keyContent        AES Key bytes
		 * @param keySize           AES Key Size
		 * @return                  CipherKey Instance
		 */
		public static CipherKey AESKey(byte[] keyContent, int keySize) {
			return new CipherKey(null, keyContent, PRNG_ALGORITHM_NATIVE_SHA1PRNG, keySize);
		}

		/**
		 * Generate AES CipherKey Instance
		 * @param keyContent        AES Key bytes
		 * @param prngAlgorithm     PRNG Algorithm
		 * @param keySize           AES Key Size
		 * @return                  CipherKey Instance
		 */
		public static CipherKey AESKey(byte[] keyContent, String prngAlgorithm, int keySize) {
			return new CipherKey(null, keyContent, prngAlgorithm, keySize);
		}

		/**
		 * Generate RSA CipherKey Instance
		 * @param rsaKey        RSA Key Instance
		 * @return              CipherKey Instance
		 */
		public static CipherKey RSAKey(Key rsaKey) {
			return new CipherKey(rsaKey, new byte[0],
					null, Globals.DEFAULT_VALUE_INT);
		}

		/**
		 * Gets the value of rsaKey
		 *
		 * @return the value of rsaKey
		 */
		public Key getRsaKey() {
			return rsaKey;
		}

		/**
		 * Gets the value of keyContent
		 *
		 * @return the value of keyContent
		 */
		public byte[] getKeyContent() {
			return keyContent;
		}

		/**
		 * Gets the value of prngAlgorithm
		 *
		 * @return the value of prngAlgorithm
		 */
		public String getPrngAlgorithm() {
			return prngAlgorithm;
		}

		/**
		 * Gets the value of keySize
		 *
		 * @return the value of keySize
		 */
		public int getKeySize() {
			return keySize;
		}
	}
}
