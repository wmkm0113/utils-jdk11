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
import java.security.cert.*;
import java.security.cert.Certificate;
import java.util.Optional;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.crypto.digests.SM3Digest;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.nervousync.crypto.Cryptor;
import org.nervousync.crypto.impl.*;
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
	private transient static final Logger LOGGER = LoggerFactory.getLogger(SecurityUtils.class);

	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	private SecurityUtils() {
	}

	/* MD5 Method */

	/**
	 * Get MD5 value. Only encode <code>String</code>
	 *
	 * @param source source object
	 * @return MD5 value
	 */
	public static String MD5(Object source) {
		return digestEncode(source, "MD5");
	}

	/* SHA Method */

	/**
	 * Get SHA1 value. Only encode <code>String</code>
	 * Using SHA256 instead
	 *
	 * @param source source object
	 * @return SHA1 value
	 */
	@Deprecated
	public static String SHA1(Object source) {
		return digestEncode(source, "SHA1");
	}

	/**
	 * Get SHA256 value. Only encode <code>String</code>
	 *
	 * @param source source object
	 * @return SHA256 value
	 */
	public static String SHA256(Object source) {
		return digestEncode(source, "SHA-256");
	}

	/**
	 * Get SHA512 value. Only encode <code>String</code>
	 *
	 * @param source source object
	 * @return SHA512 value
	 */
	public static String SHA512(Object source) {
		return digestEncode(source, "SHA-512");
	}

	/**
	 * Get SHA512 value. Only encode <code>String</code>
	 *
	 * @param source source object
	 * @return SHA512 value
	 */
	public static String SM3(Object source) {
		return digestEncode(source, "SM3");
	}

	/**
	 * Signature data with HmacMD5
	 *
	 * @param keyBytes  sign key bytes
	 * @param signDatas sign data bytes
	 * @return signature datas
	 */
	public static byte[] signDataByHmacMD5(byte[] keyBytes, byte[] signDatas) {
		return Hmac(keyBytes, signDatas, "HmacMD5");
	}

	/**
	 * Signature data with HmacSHA1
	 *
	 * @param keyBytes  sign key bytes
	 * @param signDatas sign data bytes
	 * @return signature datas
	 */
	public static byte[] signDataByHmacSHA1(byte[] keyBytes, byte[] signDatas) {
		return Hmac(keyBytes, signDatas, "HmacSHA1");
	}

	/**
	 * Signature data with HmacSHA256
	 *
	 * @param keyBytes  sign key bytes
	 * @param signDatas sign data bytes
	 * @return signature datas
	 */
	public static byte[] signDataByHmacSHA256(byte[] keyBytes, byte[] signDatas) {
		return Hmac(keyBytes, signDatas, "HmacSHA256");
	}

	/**
	 * Signature data with HmacSHA384
	 *
	 * @param keyBytes  sign key bytes
	 * @param signDatas sign data bytes
	 * @return signature datas
	 */
	public static byte[] signDataByHmacSHA384(byte[] keyBytes, byte[] signDatas) {
		return Hmac(keyBytes, signDatas, "HmacSHA384");
	}

	/**
	 * Signature data with HmacSHA512
	 *
	 * @param keyBytes  sign key bytes
	 * @param signDatas sign data bytes
	 * @return signature datas
	 */
	public static byte[] signDataByHmacSHA512(byte[] keyBytes, byte[] signDatas) {
		return Hmac(keyBytes, signDatas, "HmacSHA512");
	}

	/**
	 * Signature data with HmacSHA512-224
	 *
	 * @param keyBytes  sign key bytes
	 * @param signDatas sign data bytes
	 * @return signature datas
	 */
	public static byte[] signDataByHmacSHA512_224(byte[] keyBytes, byte[] signDatas) {
		return Hmac(keyBytes, signDatas, "HmacSHA512/224");
	}

	/**
	 * Signature data with HmacSHA512-256
	 *
	 * @param keyBytes  sign key bytes
	 * @param signDatas sign data bytes
	 * @return signature datas
	 */
	public static byte[] signDataByHmacSHA512_256(byte[] keyBytes, byte[] signDatas) {
		return Hmac(keyBytes, signDatas, "HmacSHA512/256");
	}

	/**
	 * Initialize AES128 Cryptor
	 * AES Mode:            AES/CBC/PKCS5Padding
	 * RandomAlgorithm:     SHA1PRNG
	 *
	 * @return AESCryptor instance
	 */
	public static AESCryptor AES128() {
		return AES128(AESCryptor.AESMode.CBC_PKCS5Padding, Cryptor.EncodeType.Default, Cryptor.RandomAlgorithm.SHA1PRNG);
	}

	/**
	 * Initialize AES128 Cryptor
	 * AES Mode:            AES/CBC/PKCS5Padding
	 * RandomAlgorithm:     SHA1PRNG
	 *
	 * @param encodeType the encode type
	 * @return AESCryptor instance
	 */
	public static AESCryptor AES128(Cryptor.EncodeType encodeType) {
		return AES128(AESCryptor.AESMode.CBC_PKCS5Padding, encodeType, Cryptor.RandomAlgorithm.SHA1PRNG);
	}

	/**
	 * Initialize AES128 Cryptor using given random algorithm
	 * AES Mode:            AES/CBC/PKCS5Padding
	 *
	 * @param randomAlgorithm Random algorithm
	 * @return AESCryptor instance
	 * @see Cryptor.RandomAlgorithm
	 */
	public static AESCryptor AES128(Cryptor.RandomAlgorithm randomAlgorithm) {
		return AES128(AESCryptor.AESMode.CBC_PKCS5Padding, Cryptor.EncodeType.Default, randomAlgorithm);
	}

	/**
	 * Initialize AES128 Cryptor using given AES Mode
	 * RandomAlgorithm:     SHA1PRNG
	 *
	 * @param aesMode AES Mode
	 * @return AESCryptor instance
	 * @see AESCryptor.AESMode
	 */
	public static AESCryptor AES128(AESCryptor.AESMode aesMode) {
		return AES128(aesMode, Cryptor.EncodeType.Default, Cryptor.RandomAlgorithm.SHA1PRNG);
	}

	/**
	 * Initialize AES128 Cryptor using given AES Mode and random algorithm
	 *
	 * @param aesMode         AES Mode
	 * @param randomAlgorithm Random algorithm
	 * @return AESCryptor instance
	 * @see AESCryptor.AESMode
	 * @see Cryptor.RandomAlgorithm
	 */
	public static AESCryptor AES128(AESCryptor.AESMode aesMode, Cryptor.RandomAlgorithm randomAlgorithm) {
		return AES128(aesMode, Cryptor.EncodeType.Default, randomAlgorithm);
	}

	/**
	 * Initialize AES128 Cryptor using given AES Mode and random algorithm
	 *
	 * @param aesMode         AES Mode
	 * @param encodeType      the encode type
	 * @param randomAlgorithm Random algorithm
	 * @return AESCryptor instance
	 * @see AESCryptor.AESMode
	 * @see Cryptor.RandomAlgorithm
	 */
	public static AESCryptor AES128(AESCryptor.AESMode aesMode, Cryptor.EncodeType encodeType,
									Cryptor.RandomAlgorithm randomAlgorithm) {
		return new AESCryptor(128, aesMode, encodeType, randomAlgorithm);
	}

	/**
	 * Initialize AES256 Cryptor
	 * AES Mode:            AES/CBC/PKCS5Padding
	 * RandomAlgorithm:     SHA1PRNG
	 *
	 * @return AESCryptor instance
	 */
	public static AESCryptor AES256() {
		return AES256(AESCryptor.AESMode.CBC_PKCS5Padding, Cryptor.EncodeType.Default, Cryptor.RandomAlgorithm.SHA1PRNG);
	}

	/**
	 * Initialize AES256 Cryptor
	 * AES Mode:            AES/CBC/PKCS5Padding
	 * RandomAlgorithm:     SHA1PRNG
	 *
	 * @param encodeType the encode type
	 * @return AESCryptor instance
	 */
	public static AESCryptor AES256(Cryptor.EncodeType encodeType) {
		return AES256(AESCryptor.AESMode.CBC_PKCS5Padding, encodeType, Cryptor.RandomAlgorithm.SHA1PRNG);
	}

	/**
	 * Initialize AES256 Cryptor using given random algorithm
	 * AES Mode:            AES/CBC/PKCS5Padding
	 *
	 * @param randomAlgorithm Random algorithm
	 * @return AESCryptor instance
	 * @see Cryptor.RandomAlgorithm
	 */
	public static AESCryptor AES256(Cryptor.RandomAlgorithm randomAlgorithm) {
		return AES256(AESCryptor.AESMode.CBC_PKCS5Padding, Cryptor.EncodeType.Default, randomAlgorithm);
	}

	/**
	 * Initialize AES256 Cryptor using given AES Mode
	 * RandomAlgorithm:     SHA1PRNG
	 *
	 * @param aesMode AES Mode
	 * @return AESCryptor instance
	 * @see AESCryptor.AESMode
	 */
	public static AESCryptor AES256(AESCryptor.AESMode aesMode) {
		return AES256(aesMode, Cryptor.EncodeType.Default, Cryptor.RandomAlgorithm.SHA1PRNG);
	}

	/**
	 * Initialize AES256 Cryptor using given AES Mode and random algorithm
	 *
	 * @param aesMode         AES Mode
	 * @param randomAlgorithm Random algorithm
	 * @return AESCryptor instance
	 * @see AESCryptor.AESMode
	 * @see Cryptor.RandomAlgorithm
	 */
	public static AESCryptor AES256(AESCryptor.AESMode aesMode, Cryptor.RandomAlgorithm randomAlgorithm) {
		return AES256(aesMode, Cryptor.EncodeType.Default, randomAlgorithm);
	}

	/**
	 * Initialize AES256 Cryptor using given AES Mode and random algorithm
	 *
	 * @param aesMode         AES Mode
	 * @param encodeType      the encode type
	 * @param randomAlgorithm Random algorithm
	 * @return AESCryptor instance
	 * @see AESCryptor.AESMode
	 * @see Cryptor.RandomAlgorithm
	 */
	public static AESCryptor AES256(AESCryptor.AESMode aesMode, Cryptor.EncodeType encodeType,
									Cryptor.RandomAlgorithm randomAlgorithm) {
		return new AESCryptor(256, aesMode, encodeType, randomAlgorithm);
	}

	/**
	 * Initialize DES Cryptor
	 * DES Mode: DES/CBC/PKCS5Padding
	 *
	 * @return DESCryptor instance
	 */
	public static DESCryptor DES() {
		return DES(DESCryptor.DESMode.CBC_PKCS5Padding, Cryptor.EncodeType.Default);
	}

	/**
	 * Initialize DES Cryptor
	 * DES Mode: DES/CBC/PKCS5Padding
	 *
	 * @param encodeType the encode type
	 * @return DESCryptor instance
	 */
	public static DESCryptor DES(Cryptor.EncodeType encodeType) {
		return DES(DESCryptor.DESMode.CBC_PKCS5Padding, encodeType);
	}

	/**
	 * Initialize DES Cryptor using given DES Mode
	 *
	 * @param desMode DES Mode
	 * @return DESCryptor instance
	 * @see DESCryptor.DESMode
	 */
	public static DESCryptor DES(DESCryptor.DESMode desMode) {
		return DES(desMode, Cryptor.EncodeType.Default);
	}

	/**
	 * Initialize DES Cryptor using given DES Mode
	 *
	 * @param desMode    DES Mode
	 * @param encodeType the encode type
	 * @return DESCryptor instance
	 * @see DESCryptor.DESMode
	 */
	public static DESCryptor DES(DESCryptor.DESMode desMode, Cryptor.EncodeType encodeType) {
		return new DESCryptor("DES", desMode, encodeType);
	}

	/**
	 * Initialize Triple DES Cryptor
	 * DES Mode: DESede/CBC/PKCS5Padding
	 *
	 * @return DESCryptor instance
	 */
	public static DESCryptor TripleDES() {
		return TripleDES(DESCryptor.DESMode.CBC_PKCS5Padding, Cryptor.EncodeType.Default);
	}

	/**
	 * Initialize Triple DES Cryptor
	 * DES Mode: DESede/CBC/PKCS5Padding
	 *
	 * @param encodeType the encode type
	 * @return DESCryptor instance
	 */
	public static DESCryptor TripleDES(Cryptor.EncodeType encodeType) {
		return TripleDES(DESCryptor.DESMode.CBC_PKCS5Padding, encodeType);
	}

	/**
	 * Initialize Triple DES Cryptor using given DES Mode
	 *
	 * @param desMode DES Mode
	 * @return DESCryptor instance
	 * @see DESCryptor.DESMode
	 */
	public static DESCryptor TripleDES(DESCryptor.DESMode desMode) {
		return TripleDES(desMode, Cryptor.EncodeType.Default);
	}

	/**
	 * Initialize Triple DES Cryptor using given DES Mode
	 *
	 * @param desMode    DES Mode
	 * @param encodeType the encode type
	 * @return DESCryptor instance
	 * @see DESCryptor.DESMode
	 */
	public static DESCryptor TripleDES(DESCryptor.DESMode desMode, Cryptor.EncodeType encodeType) {
		return new DESCryptor("DESede", desMode, encodeType);
	}

	/**
	 * Initialize RSA Cryptor and generate key pair
	 *
	 * @return RSACryptor instance
	 * @throws CryptoException if generate key pair error
	 */
	public static RSACryptor RSA() throws CryptoException {
		return RSA(1024);
	}

	/**
	 * Initialize RSA Cryptor and generate key pair
	 *
	 * @param keySize the key size
	 * @return RSACryptor instance
	 * @throws CryptoException if generate key pair error
	 */
	public static RSACryptor RSA(int keySize) throws CryptoException {
		return RSACryptor.generateKeyPair(keySize)
				.map(SecurityUtils::RSA)
				.orElseThrow(() -> new CryptoException("Generate key pair error"));
	}

	/**
	 * Initialize RSA Cryptor using given key pair
	 *
	 * @param storePath the key store path
	 * @param certAlias the cert alias
	 * @param password  the password
	 * @return RSACryptor instance
	 * @throws CryptoException       the crypto exception
	 * @throws FileNotFoundException the file not found exception
	 */
	public static RSACryptor RSA(String storePath, String certAlias, String password)
			throws CryptoException, FileNotFoundException {
		return loadKeyStore(new FileInputStream(storePath), password == null ? null : password.toCharArray())
				.filter(keyStore -> SecurityUtils.checkKeyEntry(keyStore, certAlias))
				.map(keyStore -> RSA(keyStore, certAlias, password))
				.orElseThrow(() -> new CryptoException("Read cert file error"));
	}

	/**
	 * Initialize RSA Cryptor using given key pair
	 *
	 * @param storeBytes the key store byte arrays
	 * @param certAlias  the cert alias
	 * @param password   the password
	 * @return RSACryptor instance
	 * @throws CryptoException the crypto exception
	 */
	public static RSACryptor RSA(byte[] storeBytes, String certAlias, String password) throws CryptoException {
		return loadKeyStore(new ByteArrayInputStream(storeBytes), password == null ? null : password.toCharArray())
				.filter(keyStore -> SecurityUtils.checkKeyEntry(keyStore, certAlias))
				.map(keyStore -> RSA(keyStore, certAlias, password))
				.orElseThrow(() -> new CryptoException("Read cert file error"));
	}

	private static RSACryptor RSA(KeyStore keyStore, String certAlias, String password) throws CryptoException {
		try {
			PrivateKey privateKey =
					(PrivateKey) keyStore.getKey(certAlias,
							password == null ? null : password.toCharArray());
			PublicKey publicKey = keyStore.getCertificate(certAlias).getPublicKey();
			return RSA(publicKey, privateKey);
		} catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
			throw new CryptoException("Read key from cert file error! ");
		}
	}

	/**
	 * Initialize RSA Cryptor using given key pair
	 *
	 * @param keyPair RSA Key pair
	 * @return RSACryptor instance
	 */
	public static RSACryptor RSA(KeyPair keyPair) {
		return RSA(RSACryptor.RSAMode.PKCS1Padding, Cryptor.EncodeType.Default,
				keyPair.getPublic(), keyPair.getPrivate());
	}

	/**
	 * Initialize RSA Cryptor using given key pair
	 *
	 * @param encodeType the encode type
	 * @param keyPair    RSA Key pair
	 * @return RSACryptor instance
	 */
	public static RSACryptor RSA(Cryptor.EncodeType encodeType, KeyPair keyPair) {
		return RSA(RSACryptor.RSAMode.PKCS1Padding, encodeType, keyPair.getPublic(), keyPair.getPrivate());
	}

	/**
	 * Initialize RSA Cryptor
	 * RSA Mode: RSA/ECB/PKCS1Padding
	 *
	 * @param publicKey  the public key
	 * @param privateKey the private key
	 * @return RSACryptor instance
	 */
	public static RSACryptor RSA(PublicKey publicKey, PrivateKey privateKey) {
		return RSA(RSACryptor.RSAMode.PKCS1Padding, publicKey, privateKey);
	}

	/**
	 * Initialize RSA Cryptor
	 * RSA Mode: RSA/ECB/PKCS1Padding
	 *
	 * @param encodeType the encode type
	 * @param publicKey  the public key
	 * @param privateKey the private key
	 * @return RSACryptor instance
	 */
	public static RSACryptor RSA(Cryptor.EncodeType encodeType, PublicKey publicKey, PrivateKey privateKey) {
		return RSA(RSACryptor.RSAMode.PKCS1Padding, encodeType, publicKey, privateKey);
	}

	/**
	 * Initialize RSA Cryptor using given RSA Mode
	 *
	 * @param rsaMode    RSA Mode
	 * @param publicKey  the public key
	 * @param privateKey the private key
	 * @return RSACryptor instance
	 * @see RSACryptor.RSAMode
	 */
	public static RSACryptor RSA(RSACryptor.RSAMode rsaMode, PublicKey publicKey, PrivateKey privateKey) {
		return RSA(rsaMode, Cryptor.EncodeType.Default, publicKey, privateKey);
	}

	/**
	 * Initialize RSA Cryptor using given RSA Mode
	 *
	 * @param rsaMode    RSA Mode
	 * @param encodeType the encode type
	 * @param publicKey  the public key
	 * @param privateKey the private key
	 * @return RSACryptor instance
	 * @see RSACryptor.RSAMode
	 */
	public static RSACryptor RSA(RSACryptor.RSAMode rsaMode, Cryptor.EncodeType encodeType,
								 PublicKey publicKey, PrivateKey privateKey) {
		return new RSACryptor(rsaMode, encodeType, publicKey, privateKey);
	}

	/**
	 * Initialize SM2 Cryptor and generate key pair
	 *
	 * @return SM2Cryptor instance
	 * @throws CryptoException if generate key pair error
	 */
	public static SM2Cryptor SM2() throws CryptoException {
		return SM2Cryptor.generateKeyPair()
				.map(SecurityUtils::SM2)
				.orElseThrow(() -> new CryptoException("Generate key pair error"));
	}

	/**
	 * Initialize SM2 Cryptor using given key pair
	 *
	 * @param certPath  the cert path
	 * @param certAlias the cert alias
	 * @param password  the password
	 * @return SM2Cryptor instance
	 * @throws CryptoException       the crypto exception
	 * @throws FileNotFoundException the file not found exception
	 */
	public static SM2Cryptor SM2(String certPath, String certAlias, String password)
			throws CryptoException, FileNotFoundException {
		return SM2(SM2Cryptor.SM2Mode.C1C3C2, certPath, certAlias, password);
	}

	/**
	 * Initialize SM2 Cryptor using given key pair
	 *
	 * @param sm2Mode   the sm 2 mode
	 * @param storePath the key store path
	 * @param certAlias the cert alias
	 * @param password  the password
	 * @return SM2Cryptor instance
	 * @throws CryptoException       the crypto exception
	 * @throws FileNotFoundException the file not found exception
	 */
	public static SM2Cryptor SM2(SM2Cryptor.SM2Mode sm2Mode, String storePath, String certAlias, String password)
			throws CryptoException, FileNotFoundException {
		return loadKeyStore(new FileInputStream(storePath), password == null ? null : password.toCharArray())
				.filter(keyStore -> {
					try {
						return keyStore.isKeyEntry(certAlias);
					} catch (KeyStoreException e) {
						return Globals.DEFAULT_VALUE_BOOLEAN;
					}
				})
				.map(keyStore -> SM2(keyStore, sm2Mode, certAlias, password))
				.orElseThrow(() -> new CryptoException("Read cert file error"));
	}

	/**
	 * Initialize SM2 Cryptor using given key pair
	 *
	 * @param sm2Mode    the sm 2 mode
	 * @param storeBytes the key store byte arrays
	 * @param certAlias  the cert alias
	 * @param password   the password
	 * @return SM2Cryptor instance
	 * @throws CryptoException the crypto exception
	 */
	public static SM2Cryptor SM2(SM2Cryptor.SM2Mode sm2Mode, byte[] storeBytes, String certAlias, String password)
			throws CryptoException {
		return loadKeyStore(new ByteArrayInputStream(storeBytes), password == null ? null : password.toCharArray())
				.filter(keyStore -> {
					try {
						return keyStore.isKeyEntry(certAlias);
					} catch (KeyStoreException e) {
						return Globals.DEFAULT_VALUE_BOOLEAN;
					}
				})
				.map(keyStore -> SM2(keyStore, sm2Mode, certAlias, password))
				.orElseThrow(() -> new CryptoException("Read cert file error"));
	}

	private static SM2Cryptor SM2(KeyStore keyStore, SM2Cryptor.SM2Mode sm2Mode, String certAlias, String password)
			throws CryptoException {
		try {
			PrivateKey privateKey =
					(PrivateKey) keyStore.getKey(certAlias,
							password == null ? null : password.toCharArray());
			PublicKey publicKey = keyStore.getCertificate(certAlias).getPublicKey();
			return SM2(sm2Mode, publicKey, privateKey);
		} catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
			throw new CryptoException("Read key from cert file error! ");
		}
	}

	/**
	 * Initialize SM2 Cryptor using given key pair
	 *
	 * @param keyPair SM2 Key pair
	 * @return SM2Cryptor instance
	 */
	public static SM2Cryptor SM2(KeyPair keyPair) {
		return SM2(SM2Cryptor.SM2Mode.C1C3C2, keyPair);
	}

	/**
	 * Initialize SM2 Cryptor using given key pair
	 *
	 * @param encodeType the encode type
	 * @param keyPair    SM2 Key pair
	 * @return SM2Cryptor instance
	 */
	public static SM2Cryptor SM2(Cryptor.EncodeType encodeType, KeyPair keyPair) {
		return SM2(SM2Cryptor.SM2Mode.C1C3C2, Cryptor.EncodeType.Default, keyPair);
	}

	/**
	 * Initialize SM2 Cryptor using given key pair
	 *
	 * @param sm2Mode the sm 2 mode
	 * @param keyPair SM2 Key pair
	 * @return SM2Cryptor instance
	 */
	public static SM2Cryptor SM2(SM2Cryptor.SM2Mode sm2Mode, KeyPair keyPair) {
		return SM2(sm2Mode, Cryptor.EncodeType.Default, keyPair.getPublic(), keyPair.getPrivate());
	}

	/**
	 * Initialize SM2 Cryptor using given key pair
	 *
	 * @param sm2Mode    the sm 2 mode
	 * @param encodeType the encode type
	 * @param keyPair    SM2 Key pair
	 * @return SM2Cryptor instance
	 */
	public static SM2Cryptor SM2(SM2Cryptor.SM2Mode sm2Mode, Cryptor.EncodeType encodeType, KeyPair keyPair) {
		return SM2(sm2Mode, encodeType, keyPair.getPublic(), keyPair.getPrivate());
	}

	/**
	 * Initialize SM2 Cryptor using given privateKey and publicKey
	 *
	 * @param publicKey  Public key
	 * @param privateKey Private key
	 * @return SM2Cryptor instance
	 */
	public static SM2Cryptor SM2(PublicKey publicKey, PrivateKey privateKey) {
		return SM2(SM2Cryptor.SM2Mode.C1C3C2, Cryptor.EncodeType.Default, publicKey, privateKey);
	}

	/**
	 * Initialize SM2 Cryptor using given privateKey and publicKey
	 *
	 * @param encodeType the encode type
	 * @param publicKey  Public key
	 * @param privateKey Private key
	 * @return SM2Cryptor instance
	 */
	public static SM2Cryptor SM2(Cryptor.EncodeType encodeType, PublicKey publicKey, PrivateKey privateKey) {
		return SM2(SM2Cryptor.SM2Mode.C1C3C2, encodeType, publicKey, privateKey);
	}

	/**
	 * Initialize SM2 Cryptor using given privateKey and publicKey
	 *
	 * @param sm2Mode    the sm 2 mode
	 * @param publicKey  Public key
	 * @param privateKey Private key
	 * @return SM2Cryptor instance
	 */
	public static SM2Cryptor SM2(SM2Cryptor.SM2Mode sm2Mode, PublicKey publicKey, PrivateKey privateKey) {
		return SM2(sm2Mode, Cryptor.EncodeType.Default, publicKey, privateKey);
	}

	/**
	 * Initialize SM2 Cryptor using given privateKey and publicKey
	 *
	 * @param sm2Mode    the sm 2 mode
	 * @param encodeType the encode type
	 * @param publicKey  Public key
	 * @param privateKey Private key
	 * @return SM2Cryptor instance
	 */
	public static SM2Cryptor SM2(SM2Cryptor.SM2Mode sm2Mode, Cryptor.EncodeType encodeType,
								 PublicKey publicKey, PrivateKey privateKey) {
		return new SM2Cryptor(sm2Mode, encodeType, publicKey, privateKey);
	}

	/**
	 * Initialize SM4 Cryptor
	 * SM4 Mode:            SM4/CBC/NoPadding
	 * RandomAlgorithm:     SHA1PRNG
	 *
	 * @return SM4Cryptor instance
	 */
	public static SM4Cryptor SM4() {
		return SM4(SM4Cryptor.SM4Mode.CBC_NoPadding, Cryptor.EncodeType.Default, Cryptor.RandomAlgorithm.SHA1PRNG);
	}

	/**
	 * Initialize SM4 Cryptor
	 * SM4 Mode:            SM4/CBC/NoPadding
	 * RandomAlgorithm:     SHA1PRNG
	 *
	 * @param encodeType the encode type
	 * @return SM4Cryptor instance
	 */
	public static SM4Cryptor SM4(Cryptor.EncodeType encodeType) {
		return SM4(SM4Cryptor.SM4Mode.CBC_NoPadding, encodeType, Cryptor.RandomAlgorithm.SHA1PRNG);
	}

	/**
	 * Initialize SM4 Cryptor using given random algorithm
	 * SM4 Mode:            SM4/CBC/NoPadding
	 *
	 * @param randomAlgorithm Random algorithm
	 * @return SM4Cryptor instance
	 * @see Cryptor.RandomAlgorithm
	 */
	public static SM4Cryptor SM4(Cryptor.RandomAlgorithm randomAlgorithm) {
		return SM4(SM4Cryptor.SM4Mode.CBC_NoPadding, Cryptor.EncodeType.Default, randomAlgorithm);
	}

	/**
	 * Initialize SM4 Cryptor using given random algorithm
	 * SM4 Mode:            SM4/CBC/NoPadding
	 *
	 * @param encodeType      the encode type
	 * @param randomAlgorithm Random algorithm
	 * @return SM4Cryptor instance
	 * @see Cryptor.RandomAlgorithm
	 */
	public static SM4Cryptor SM4(Cryptor.EncodeType encodeType, Cryptor.RandomAlgorithm randomAlgorithm) {
		return SM4(SM4Cryptor.SM4Mode.CBC_NoPadding, Cryptor.EncodeType.Default, randomAlgorithm);
	}

	/**
	 * Initialize SM4 Cryptor using given SM4 Mode
	 * RandomAlgorithm:     SHA1PRNG
	 *
	 * @param sm4Mode SM4 Mode
	 * @return SM4Cryptor instance
	 * @see SM4Cryptor.SM4Mode
	 */
	public static SM4Cryptor SM4(SM4Cryptor.SM4Mode sm4Mode) {
		return SM4(sm4Mode, Cryptor.EncodeType.Default, Cryptor.RandomAlgorithm.SHA1PRNG);
	}

	/**
	 * Initialize SM4 Cryptor using given SM4 Mode and random algorithm
	 *
	 * @param sm4Mode         SM4 Mode
	 * @param randomAlgorithm Random algorithm
	 * @return SM4Cryptor instance
	 * @see SM4Cryptor.SM4Mode
	 * @see Cryptor.RandomAlgorithm
	 */
	public static SM4Cryptor SM4(SM4Cryptor.SM4Mode sm4Mode, Cryptor.RandomAlgorithm randomAlgorithm) {
		return SM4(sm4Mode, Cryptor.EncodeType.Default, randomAlgorithm);
	}

	/**
	 * Initialize SM4 Cryptor using given SM4 Mode and random algorithm
	 *
	 * @param sm4Mode         SM4 Mode
	 * @param encodeType      the encode type
	 * @param randomAlgorithm Random algorithm
	 * @return SM4Cryptor instance
	 * @see SM4Cryptor.SM4Mode
	 * @see Cryptor.RandomAlgorithm
	 */
	public static SM4Cryptor SM4(SM4Cryptor.SM4Mode sm4Mode, Cryptor.EncodeType encodeType,
								 Cryptor.RandomAlgorithm randomAlgorithm) {
		return new SM4Cryptor(sm4Mode, encodeType, randomAlgorithm);
	}

	/**
	 * Read public key from X.509 certificate file
	 *
	 * @param certBytes Certificate file data bytes
	 * @return Public key
	 */
	public static PublicKey readPublicKey(byte[] certBytes) {
		return SecurityUtils.readPublicKey(certBytes, null, Boolean.TRUE);
	}

	/**
	 * Read public key from X.509 certificate file
	 *
	 * @param certBytes     Certificate file data bytes
	 * @param checkValidity the check validity
	 * @return Public key
	 */
	public static PublicKey readPublicKey(byte[] certBytes, boolean checkValidity) {
		return SecurityUtils.readPublicKey(certBytes, null, checkValidity);
	}

	/**
	 * Read public key from X.509 certificate file
	 *
	 * @param certBytes Certificate file data bytes
	 * @param publicKey the public key
	 * @return Public key
	 */
	public static PublicKey readPublicKey(byte[] certBytes, PublicKey publicKey) {
		return SecurityUtils.readPublicKey(certBytes, publicKey, Boolean.TRUE);
	}

	/**
	 * Read public key from X.509 certificate file
	 *
	 * @param certBytes     Certificate file data bytes
	 * @param verifyKey     the verify key
	 * @param checkValidity Check certificate validity
	 * @return Public key
	 */
	public static PublicKey readPublicKey(byte[] certBytes, PublicKey verifyKey, boolean checkValidity) {
		return readCertificate(certBytes, verifyKey, checkValidity)
				.map(X509Certificate::getPublicKey)
				.orElse(null);
	}

	/**
	 * Read certificate optional.
	 *
	 * @param certBytes the cert bytes
	 * @return the optional
	 */
	public static Optional<X509Certificate> readCertificate(byte[] certBytes) {
		return readCertificate(certBytes, null, Boolean.TRUE);
	}

	/**
	 * Read certificate optional.
	 *
	 * @param certBytes the cert bytes
	 * @param verifyKey the verify key
	 * @return the optional
	 */
	public static Optional<X509Certificate> readCertificate(byte[] certBytes, PublicKey verifyKey) {
		return readCertificate(certBytes, verifyKey, Boolean.TRUE);
	}

	/**
	 * Read certificate optional.
	 *
	 * @param certBytes     the cert bytes
	 * @param checkValidity the check validity
	 * @return the optional
	 */
	public static Optional<X509Certificate> readCertificate(byte[] certBytes, boolean checkValidity) {
		return readCertificate(certBytes, null, checkValidity);
	}

	/**
	 * Read certificate optional.
	 *
	 * @param certBytes     the cert bytes
	 * @param verifyKey     the verify key
	 * @param checkValidity the check validity
	 * @return the optional
	 */
	public static Optional<X509Certificate> readCertificate(byte[] certBytes, PublicKey verifyKey,
															boolean checkValidity) {
		X509Certificate x509Certificate;
		try {
			CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509", "BC");
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(certBytes);
			x509Certificate = (X509Certificate) certificateFactory.generateCertificate(byteArrayInputStream);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Certificate SN: {}", x509Certificate.getSerialNumber().toString());
			}
			if (checkValidity) {
				x509Certificate.checkValidity();
			}
			if (verifyKey != null) {
				x509Certificate.verify(verifyKey, "BC");
			}
		} catch (Exception e) {
			LOGGER.error("Certificate is invalid! ");
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Stack message: ", e);
			}
			x509Certificate = null;
		}
		return Optional.ofNullable(x509Certificate);
	}

	/**
	 * Read certificate optional.
	 *
	 * @param storeBytes the store bytes
	 * @param certAlias  the cert alias
	 * @param password   the password
	 * @return the optional
	 */
	public static Optional<X509Certificate> readCertificate(byte[] storeBytes, String certAlias, String password) {
		try {
			return loadKeyStore(new ByteArrayInputStream(storeBytes), password == null ? null : password.toCharArray())
					.flatMap(keyStore -> SecurityUtils.readCertificate(keyStore, certAlias));
		} catch (Exception e) {
			return Optional.empty();
		}
	}

	/**
	 * Read certificate optional.
	 *
	 * @param storePath the store path
	 * @param certAlias the cert alias
	 * @param password  the password
	 * @return the optional
	 */
	public static Optional<X509Certificate> readCertificate(String storePath, String certAlias, String password) {
		try {
			return loadKeyStore(new FileInputStream(storePath), password == null ? null : password.toCharArray())
					.filter(keyStore -> SecurityUtils.checkKeyEntry(keyStore, certAlias))
					.flatMap(keyStore -> SecurityUtils.readCertificate(keyStore, certAlias));
		} catch (Exception e) {
			return Optional.empty();
		}
	}

	/**
	 * Read private key optional.
	 *
	 * @param storeBytes the key store byte arrays
	 * @param certAlias  the cert alias
	 * @param password   the password
	 * @return the optional
	 */
	public static Optional<PrivateKey> readPrivateKey(byte[] storeBytes, String certAlias, String password) {
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(storeBytes);
		final char[] certPwd = password == null ? null : password.toCharArray();
		try {
			return loadKeyStore(new ByteArrayInputStream(storeBytes), certPwd)
					.filter(keyStore -> SecurityUtils.checkKeyEntry(keyStore, certAlias))
					.map(keyStore -> {
						try {
							return (PrivateKey) keyStore.getKey(certAlias, certPwd);
						} catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
							e.printStackTrace();
							return null;
						}
					});
		} catch (Exception e) {
			return Optional.empty();
		}
	}

	/**
	 * Read private key from pfx optional.
	 *
	 * @param storePath the key store path
	 * @param certAlias the cert alias
	 * @param password  the password
	 * @return the optional
	 * @throws FileNotFoundException the file not found exception
	 */
	public static Optional<PrivateKey> readPrivateKey(String storePath, String certAlias, String password)
			throws FileNotFoundException {
		final char[] certPwd = password == null ? null : password.toCharArray();
		return loadKeyStore(new FileInputStream(storePath), certPwd)
				.filter(keyStore -> SecurityUtils.checkKeyEntry(keyStore, certAlias))
				.map(keyStore -> {
					try {
						return (PrivateKey) keyStore.getKey(certAlias, certPwd);
					} catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
						e.printStackTrace();
						return null;
					}
				});
	}

	/**
	 * Read public key from pfx optional.
	 *
	 * @param storeBytes the key store byte arrays
	 * @param certAlias  the cert alias
	 * @param password   the password
	 * @return the optional
	 */
	public static Optional<PublicKey> readPublicKey(byte[] storeBytes, String certAlias, String password) {
		return loadKeyStore(new ByteArrayInputStream(storeBytes), password == null ? null : password.toCharArray())
				.filter(keyStore -> SecurityUtils.checkKeyEntry(keyStore, certAlias))
				.flatMap(keyStore -> SecurityUtils.readCertificate(keyStore, certAlias))
				.map(Certificate::getPublicKey);
	}

	/**
	 * Read public key from pfx optional.
	 *
	 * @param storePath the key store path
	 * @param certAlias the cert alias
	 * @param password  the password
	 * @return the optional
	 * @throws FileNotFoundException the file not found exception
	 */
	public static Optional<PublicKey> readPublicKey(String storePath, String certAlias, String password)
			throws FileNotFoundException {
		return loadKeyStore(new FileInputStream(storePath), password == null ? null : password.toCharArray())
				.filter(keyStore -> SecurityUtils.checkKeyEntry(keyStore, certAlias))
				.flatMap(keyStore -> SecurityUtils.readCertificate(keyStore, certAlias))
				.map(Certificate::getPublicKey);
	}

	/**
	 * Load key store optional.
	 *
	 * @param storeBytes the store bytes
	 * @param password   the password
	 * @return the optional
	 */
	public static Optional<KeyStore> loadKeyStore(byte[] storeBytes, String password) {
		return loadKeyStore(new ByteArrayInputStream(storeBytes), password == null ? null : password.toCharArray());
	}

	/**
	 * Load key store optional.
	 *
	 * @param storePath the store path
	 * @param password  the password
	 * @return the optional
	 * @throws FileNotFoundException the file not found exception
	 */
	public static Optional<KeyStore> loadKeyStore(String storePath, String password) throws FileNotFoundException {
		return loadKeyStore(new FileInputStream(storePath), password == null ? null : password.toCharArray());
	}

	private static boolean checkKeyEntry(KeyStore keyStore, String certAlias) {
		if (keyStore == null || certAlias == null) {
			return Globals.DEFAULT_VALUE_BOOLEAN;
		}
		try {
			return keyStore.isKeyEntry(certAlias);
		} catch (KeyStoreException e) {
			return Globals.DEFAULT_VALUE_BOOLEAN;
		}
	}

	private static Optional<KeyStore> loadKeyStore(InputStream inputStream, char[] certPwd) {
		KeyStore keyStore;
		try {
			keyStore = KeyStore.getInstance("PKCS12", "BC");
			keyStore.load(inputStream, certPwd);
		} catch (Exception e) {
			e.printStackTrace();
			keyStore = null;
		} finally {
			IOUtils.closeStream(inputStream);
		}
		return Optional.ofNullable(keyStore);
	}

	private static Optional<X509Certificate> readCertificate(KeyStore keyStore, String certAlias) {
		try {
			return Optional.ofNullable((X509Certificate) keyStore.getCertificate(certAlias));
		} catch (KeyStoreException e) {
			e.printStackTrace();
			return Optional.empty();
		}
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
	 * Get digest value
	 * @param source		Input source
	 * @param algorithm	Calc algorithm
	 * @return calc value
	 */
	private static String digestEncode(Object source, String algorithm) {
		MessageDigest messageDigest;

		//	Initialize MessageDigest Instance
		try {
			messageDigest = MessageDigest.getInstance(algorithm, "BC");
		} catch (NoSuchAlgorithmException | NoSuchProviderException ex) {
			LOGGER.error("Initialize failed, maybe the MessageDigest does not support " + algorithm + "!", ex);
			return Globals.DEFAULT_VALUE_STRING;
		}

		if (source instanceof File) {
			digestFile((File)source, messageDigest);
		} else {
			byte[] tempBytes = ConvertUtils.convertToByteArray(source);
			if (tempBytes != null) {
				messageDigest.update(tempBytes);
			}
		}
		return ConvertUtils.byteToHex(messageDigest.digest());
	}

	/**
	 * Calculate given file digest value
	 * @param file      File object
	 * @param digest    Digest instance
	 */
	private static void digestFile(File file, Object digest) {
		if (file.exists() && file.isFile()) {
			RandomAccessFile randomAccessFile = null;
			try {
				randomAccessFile = new RandomAccessFile(file, Globals.READ_MODE);
				byte[] readBuffer = new byte[Globals.READ_FILE_BUFFER_SIZE];
				int readLength;
				while ((readLength = randomAccessFile.read(readBuffer)) > 0) {
					if (digest instanceof MessageDigest) {
						((MessageDigest)digest).update(readBuffer, 0, readLength);
					} else {
						((SM3Digest)digest).update(readBuffer, 0, readLength);
					}
				}
			} catch (Exception e) {
				LOGGER.error("Message digest error! ", e);
			} finally {
				IOUtils.closeStream(randomAccessFile);
			}
		} else {
			LOGGER.error("File does not exists" + file.getAbsolutePath());
		}
	}
}
