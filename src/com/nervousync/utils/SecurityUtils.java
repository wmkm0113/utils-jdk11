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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nervousync.commons.core.Globals;
import sun.misc.Cleaner;

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

	/* AES Method */
	
	/**
	 * Encrypt byte arrays with given encrypt key by AES128
	 * @param arrB						Byte arrays will be encrypted
	 * @param strKey					encrypt key
	 * @return							Encrypted result
	 * @throws Exception				Any exception of this operate will be throw up
	 */
	public static byte[] AES128Encrypt(byte[] arrB, String strKey) throws Exception {
		return AESEncrypt(arrB, strKey, 128);
	}

	/**
	 * Encrypt byte arrays with given encrypt key by AES256
	 * @param arrB						Byte arrays will be encrypted
	 * @param strKey					encrypt key
	 * @return							Encrypted result
	 * @throws Exception				Any exception of this operate will be throw up
	 */
	public static byte[] AES256Encrypt(byte[] arrB, String strKey) throws Exception {
		return AESEncrypt(arrB, strKey, 256);
	}

	/**
	 * Encrypt byte arrays with given encrypt key by AES
	 * @param arrB						Byte arrays will be encrypted
	 * @param strKey					encrypt key
	 * @param keySize					AES key size
	 * @return							Encrypted result
	 * @throws Exception				Any exception of this operate will be throw up
	 */
	public static byte[] AESEncrypt(byte[] arrB, String strKey, int keySize) throws Exception {
		if (strKey == null || strKey.length() % 16 != 0) {
			throw new Exception("Data length error");
		}
		Cipher encryptCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		Key key = SecurityUtils.generateAESKey(strKey.getBytes(), keySize);
		encryptCipher.init(Cipher.ENCRYPT_MODE, key);
		return encryptCipher.doFinal(arrB);
	}
	
	/**
	 * Encrypt byte arrays with default encrypt key by AES128
	 * @param arrB						Byte arrays will be encrypted
	 * @return							Encrypted result
	 * @throws Exception				Any exception of this operate will be throw up
	 */
	public static byte[] AES128Encrypt(byte[] arrB) throws Exception {
		return AESEncrypt(arrB, SecurityUtils.PRIVATE_KEY, 128);
	}

	/**
	 * Encrypt byte arrays with default encrypt key by AES256
	 * @param arrB						Byte arrays will be encrypted
	 * @return							Encrypted result
	 * @throws Exception				Any exception of this operate will be throw up
	 */
	public static byte[] AES256Encrypt(byte[] arrB) throws Exception {
		return AESEncrypt(arrB, SecurityUtils.PRIVATE_KEY, 256);
	}

	/**
	 * Encrypt string with given encrypt key by AES128
	 * @param strIn						String will be encrypted
	 * @param strKey					encrypt key
	 * @return							Encrypted result
	 * @throws Exception				Any exception of this operate will be throw up
	 */
	public static String AES128Encrypt(String strIn, String strKey) throws Exception {
		return ConvertUtils.byteArrayToHexString(
				AES128Encrypt(ConvertUtils.convertToByteArray(strIn), strKey));
	}

	/**
	 * Encrypt string with given encrypt key by AES256
	 * @param strIn						String will be encrypted
	 * @param strKey					encrypt key
	 * @return							Encrypted result
	 * @throws Exception				Any exception of this operate will be throw up
	 */
	public static String AES256Encrypt(String strIn, String strKey) throws Exception {
		return ConvertUtils.byteArrayToHexString(
				AES256Encrypt(ConvertUtils.convertToByteArray(strIn), strKey));
	}

	/**
	 * Encrypt string with default encrypt key by AES128
	 * @param strIn						String will be encrypted
	 * @return							Encrypted result
	 * @throws Exception				Any exception of this operate will be throw up
	 */
	public static String AES128Encrypt(String strIn) throws Exception {
		return AES128Encrypt(strIn, SecurityUtils.PRIVATE_KEY);
	}

	/**
	 * Encrypt string with default encrypt key by AES256
	 * @param strIn						String will be encrypted
	 * @return							Encrypted result
	 * @throws Exception				Any exception of this operate will be throw up
	 */
	public static String AES256Encrypt(String strIn) throws Exception {
		return AES256Encrypt(strIn, SecurityUtils.PRIVATE_KEY);
	}
	
	/**
	 * Decrypt byte arrays with given decrypt key by AES128
	 * @param arrB						Byte arrays will be decrypted
	 * @param strKey					Decrypt key
	 * @return							Decrypted result
	 * @throws Exception				Any exception of this operate will be throw up
	 */
	public static byte[] AES128Decrypt(byte[] arrB, String strKey) throws Exception {
		return AESDecrypt(arrB, strKey, 128);
	}

	/**
	 * Decrypt byte arrays with given decrypt key by AES256
	 * @param arrB						Byte arrays will be decrypted
	 * @param strKey					Decrypt key
	 * @return							Decrypted result
	 * @throws Exception				Any exception of this operate will be throw up
	 */
	public static byte[] AES256Decrypt(byte[] arrB, String strKey) throws Exception {
		return AESDecrypt(arrB, strKey, 256);
	}
	
	/**
	 * Decrypt byte arrays with given decrypt key by AES
	 * @param arrB						Byte arrays will be decrypted
	 * @param strKey					Decrypt key
	 * @param keySize					AES key size
	 * @return							Decrypted result
	 * @throws Exception				Any exception of this operate will be throw up
	 */
	public static byte[] AESDecrypt(byte[] arrB, String strKey, int keySize) throws Exception {
		if (strKey == null || strKey.length() % 16 != 0) {
			throw new Exception("Data length error");
		}
		Cipher decryptCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		Key key = SecurityUtils.generateAESKey(strKey.getBytes(), keySize);
		decryptCipher.init(Cipher.DECRYPT_MODE, key);
		return decryptCipher.doFinal(arrB);
	}

	/**
	 * Decrypt byte arrays with default decrypt key by AES128
	 * @param arrB						Byte arrays will be decrypted
	 * @return							Decrypted result
	 * @throws Exception				Any exception of this operate will be throw up
	 */
	public static byte[] AES128Decrypt(byte[] arrB) throws Exception {
		return AESDecrypt(arrB, SecurityUtils.PRIVATE_KEY, 128);
	}

	/**
	 * Decrypt byte arrays with default decrypt key by AES256
	 * @param arrB						Byte arrays will be decrypted
	 * @return							Decrypted result
	 * @throws Exception				Any exception of this operate will be throw up
	 */
	public static byte[] AES256Decrypt(byte[] arrB) throws Exception {
		return AESDecrypt(arrB, SecurityUtils.PRIVATE_KEY, 256);
	}

	/**
	 * Decrypt string with given decrypt key by AES128
	 * @param strIn						String will be decrypted
	 * @param strKey					Decrypt key
	 * @return							Decrypted result
	 */
	public static String AES128Decrypt(String strIn, String strKey) {
		try {
			byte[] decryptData = AESDecrypt(ConvertUtils.hexStrToByteArr(strIn), strKey, 128);
			return ConvertUtils.convertToString(decryptData);
		} catch (Exception e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Decrypt data error! ", e);
			}
		}
		return null;
	}

	/**
	 * Decrypt string with given decrypt key by AES256
	 * @param strIn						String will be decrypted
	 * @param strKey					Decrypt key
	 * @return							Decrypted result
	 */
	public static String AES256Decrypt(String strIn, String strKey) {
		try {
			byte[] decryptData = AESDecrypt(ConvertUtils.hexStrToByteArr(strIn), strKey, 256);
			return ConvertUtils.convertToString(decryptData);
		} catch (Exception e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Decrypt data error! ", e);
			}
		}
		return null;
	}

	/**
	 * Decrypt string with default decrypt key by AES128
	 * @param strIn						String will be decrypted
	 * @return							Decrypted result
	 */
	public static String AES128Decrypt(String strIn) {
		return AES128Decrypt(strIn, SecurityUtils.PRIVATE_KEY);
	}

	/**
	 * Decrypt string with default decrypt key by AES128
	 * @param strIn						String will be decrypted
	 * @return							Decrypted result
	 */
	public static String AES256Decrypt(String strIn) {
		return AES256Decrypt(strIn, SecurityUtils.PRIVATE_KEY);
	}
	
	/* DES Method */

	/**
	 * Encrypt byte arrays with given encrypt key by DES
	 * @param arrB						Byte arrays will be encrypted
	 * @param strKey					Encrypt key
	 * @return							Encrypt result
	 * @throws Exception				Any exception of this operate will be throw up
	 */
	public static byte[] DESEncrypt(byte[] arrB, String strKey) throws Exception {
		Cipher encryptCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
		Security.addProvider(new com.sun.crypto.provider.SunJCE());
		Key key = SecurityUtils.generateDESKey(strKey.getBytes());
		encryptCipher.init(Cipher.ENCRYPT_MODE, key);
		return encryptCipher.doFinal(arrB);
	}

	/**
	 * Encrypt string with given encrypt key by DES
	 * @param strIn						String will be encrypted
	 * @param strKey					Encrypt key
	 * @return							Encrypt result
	 * @throws Exception				Any exception of this operate will be throw up
	 */
	public static String DESEncrypt(String strIn, String strKey) throws Exception {
		return ConvertUtils.byteArrayToHexString(SecurityUtils.DESEncrypt(ConvertUtils.convertToByteArray(strIn), strKey));
	}
	
	/**
	 * Encrypt byte arrays with default encrypt key by DES
	 * @param arrB						Byte arrays will be encrypted
	 * @return							Encrypt result
	 * @throws Exception				Any exception of this operate will be throw up
	 */
	public static byte[] DESEncrypt(byte[] arrB) throws Exception {
		return SecurityUtils.DESEncrypt(arrB, SecurityUtils.PRIVATE_KEY);
	}

	/**
	 * Encrypt string with default encrypt key by DES
	 * @param strIn						String will be encrypted
	 * @return							Encrypt result
	 * @throws Exception				Any exception of this operate will be throw up
	 */
	public static String DESEncrypt(String strIn) throws Exception {
		return SecurityUtils.DESEncrypt(strIn, SecurityUtils.PRIVATE_KEY);
	}

	/**
	 * Decrypt byte arrays with given decrypt key by DES
	 * @param arrB						Byte arrays will be decrypted
	 * @param strKey					Decrypt key
	 * @return							Decrypt result
	 * @throws Exception				Any exception of this operate will be throw up
	 */
	public static byte[] DESDecrypt(byte[] arrB, String strKey) throws Exception {
		Cipher decryptCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
		Security.addProvider(new com.sun.crypto.provider.SunJCE());
		Key key = SecurityUtils.generateDESKey(strKey.getBytes());
		decryptCipher.init(Cipher.DECRYPT_MODE, key);
		return decryptCipher.doFinal(arrB);
	}

	/**
	 * Decrypt string with given decrypt key by DES
	 * @param strIn						String will be decrypted
	 * @param strKey					Decrypt key
	 * @return							Decrypt result
	 * @throws Exception				Any exception of this operate will be throw up
	 */
	public static String DESDecrypt(String strIn, String strKey) throws Exception {
		return ConvertUtils.convertToString(SecurityUtils.DESDecrypt(ConvertUtils.hexStrToByteArr(strIn), strKey));
	}

	/**
	 * Decrypt byte arrays with default decrypt key by DES
	 * @param arrB						Byte arrays will be decrypted
	 * @return							Decrypt result
	 * @throws Exception				Any exception of this operate will be throw up
	 */
	public static byte[] DESDecrypt(byte[] arrB) throws Exception {
		return SecurityUtils.DESDecrypt(arrB, SecurityUtils.PRIVATE_KEY);
	}

	/**
	 * Decrypt string with default decrypt key by DES
	 * @param strIn						String will be decrypted
	 * @return							Decrypt result
	 * @throws Exception				Any exception of this operate will be throw up
	 */
	public static String DESDecrypt(String strIn) throws Exception {
		return SecurityUtils.DESDecrypt(strIn, SecurityUtils.PRIVATE_KEY);
	}

	/**
	 * Encrypt string with given key by RSA
	 * @param strIn						String will be encrypted
	 * @param key						RSA key
	 * @return							Encrypt result
	 * @throws Exception				Any exception of this operate will be throw up
	 */
	public static String RSAEncrypt(String strIn, Key key) throws Exception {
		return ConvertUtils.byteArrayToHexString(SecurityUtils.RSAEncrypt(ConvertUtils.convertToByteArray(strIn), key));
	}
	
	/**
	 * Encrypt byte arrays with given key by RSA
	 * @param arrB						Byte arrays will be encrypted
	 * @param key						RSA key
	 * @return							Encrypt result
	 * @throws Exception				Any exception of this operate will be throw up
	 */
	public static byte[] RSAEncrypt(byte[] arrB, Key key) throws Exception {
		Cipher encryptCipher = SecurityUtils.initRSAEncryptCipher(key);
		if (encryptCipher != null) {
			int blockSize = encryptCipher.getBlockSize();
			int outputSize = encryptCipher.getOutputSize(arrB.length);
			
			int leavedSize = arrB.length % blockSize;
			
			int blocksSize = leavedSize != 0 ? arrB.length / blockSize + 1
					: arrB.length / blockSize;
			
			byte[] byteArray = new byte[outputSize * blocksSize];
			int i = 0;
			
			while (arrB.length - i * blockSize > 0) {
				if (arrB.length - i * blockSize > blockSize) {
					encryptCipher.doFinal(arrB, i * blockSize, blockSize, byteArray, i * outputSize);
				} else {
					encryptCipher.doFinal(arrB, i * blockSize, arrB.length - i * blockSize, byteArray, i * outputSize);
				}
				i++;
			}
			return byteArray;
		} else {
			throw new Exception("Initialize Cipher Error!");
		}
	}

	/**
	 * Decrypt string with given key by RSA
	 * @param strIn						String will be encrypted
	 * @param key						RSA key
	 * @return							Decrypt result
	 * @throws Exception				Any exception of this operate will be throw up
	 */
	public static String RSADecrypt(String strIn, Key key) throws Exception {
		return new String(SecurityUtils.RSADecrypt(ConvertUtils.hexStrToByteArr(strIn), key));
	}
	
	/**
	 * Decrypt byte array with given key by RSA
	 * @param arrB						Byte array will be encrypted
	 * @param key						RSA key
	 * @return							Decrypt result
	 * @throws Exception				Any exception of this operate will be throw up
	 */
	public static byte[] RSADecrypt(byte[] arrB, Key key) throws Exception {
		Cipher decryptCipher = initRSADecryptCipher(key);
		if (decryptCipher != null) {
			int blockSize = decryptCipher.getBlockSize();
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(64);
			
			int j = 0;
			
			while (arrB.length - j * blockSize > 0) {
				byteArrayOutputStream.write(decryptCipher.doFinal(arrB, j * blockSize, blockSize));
				j++;
			}
			
			return byteArrayOutputStream.toByteArray();
		} else {
			throw new Exception("Initialize Cipher Error!");
		}
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
	public static byte[] SignDataWithDSA(PrivateKey privateKey, String message) {
		return SignData(privateKey, message.getBytes(), "SHA256withDSA");
	}

	/**
	 * Signature data with DSA
	 * @param privateKey	Signature private key
	 * @param datas			Signature datas
	 * @return				Signature info
	 */
	public static byte[] SignDataWithDSA(PrivateKey privateKey, byte[] datas) {
		return SignData(privateKey, datas, "SHA256withDSA");
	}
	
	/**
	 * Verify signature info is valid
	 * @param publicKey		Verify public key
	 * @param datas			Signature datas
	 * @param signature		Signature info
	 * @return				Verify result
	 */
	public static boolean VerifyDSASign(PublicKey publicKey, 
			byte[] datas, byte[] signature) {
		return VerifySign(publicKey, datas, signature, "SHA256withDSA");
	}

	/**
	 * Signature data with RSA
	 * @param privateKey	Signature private key
	 * @param message		Signature datas
	 * @return				Signature info
	 */
	public static byte[] SignDataWithRSA(PrivateKey privateKey, String message) {
		return SignData(privateKey, message.getBytes(), "SHA256withRSA");
	}

	/**
	 * Signature data with RSA
	 * @param privateKey	Signature private key
	 * @param datas			Signature datas
	 * @return				Signature info
	 */
	public static byte[] SignDataWithRSA(PrivateKey privateKey, byte[] datas) {
		return SignData(privateKey, datas, "SHA256withRSA");
	}
	
	/**
	 * Signature data with HmacSHA1
	 * @param keyBytes		sign key bytes
	 * @param signDatas		sign data bytes
	 * @return				signature datas
	 */
	public static byte[] SignDataByHmacSHA1(byte[] keyBytes, byte[] signDatas) {
		return Hmac(keyBytes, signDatas, "HmacSHA1");
	}
	
	/**
	 * Signature data with HmacSHA256
	 * @param keyBytes		sign key bytes
	 * @param signDatas		sign data bytes
	 * @return				signature datas
	 */
	public static byte[] SignDataByHmacSHA256(byte[] keyBytes, byte[] signDatas) {
		return Hmac(keyBytes, signDatas, "HmacSHA256");
	}

	/**
	 * Verify signature info is valid
	 * @param publicKey		Verify public key
	 * @param datas			Signature datas
	 * @param signature		Signature info
	 * @return				Verify result
	 */
	public static boolean VerifyRSASign(PublicKey publicKey, 
			byte[] datas, byte[] signature) {
		return VerifySign(publicKey, datas, signature, "SHA256withRSA");
	}
	
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
	
	private static byte[] Hmac(byte[] keyBytes, byte[] signDatas, String algorithm) {
		try {
			Mac hmac = Mac.getInstance(algorithm);
			hmac.init(new SecretKeySpec(keyBytes, algorithm));
			return hmac.doFinal(signDatas);
		} catch (Exception e) {
			return null;
		}
	}
	
	private static byte[] SignData(PrivateKey privateKey, byte[] datas, String algorithm) {
		try {
			Signature signature = Signature.getInstance(algorithm);
			signature.initSign(privateKey);
			signature.update(datas);
			
			return signature.sign();
		} catch (Exception e) {
			return null;
		}
	}
	
	private static boolean VerifySign(PublicKey publicKey, 
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
	 * Get digest value
	 * @param source		Input source
	 * @param algorithm	Calc algorithm
	 * @return calc value
	 */
	private static String digestEncode(Object source, String algorithm) {
		if (source == null) {
			return Globals.DEFAULT_VALUE_STRING;
		}
		
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
				FileInputStream fileInputStream = null;
				FileChannel fileChannel = null;
				try {
					fileInputStream = new FileInputStream(file);
					fileChannel = fileInputStream.getChannel();
					MappedByteBuffer byteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
					messageDigest.update(byteBuffer);
					
					Cleaner cleaner = (Cleaner)ReflectionUtils.executeMethod("cleaner", byteBuffer);
					if (cleaner != null) {
						cleaner.clean();
					}
				} catch (Exception e) {
					LOGGER.error("Message digest error! ", e);
					return null;
				} finally {
					IOUtils.closeStream(fileChannel);
					IOUtils.closeStream(fileInputStream);
				}
			} else {
				LOGGER.error("File does not exists" + file.getAbsolutePath());
				return "";
			}
			return ConvertUtils.byteArrayToHexString(messageDigest.digest());
		} else {
			byte[] tempBytes = ConvertUtils.convertToByteArray(source);
			if (tempBytes != null) {
				messageDigest.update(tempBytes);
			}
			return ConvertUtils.byteArrayToHexString(messageDigest.digest());
		}
	}

	private static Cipher initRSAEncryptCipher(Key key) {
		try {
			//	Initialize encrypt Cipher
			Cipher encryptCipher = Cipher.getInstance("RSA", new BouncyCastleProvider());
			encryptCipher.init(Cipher.ENCRYPT_MODE, key);
			
			return encryptCipher;
		} catch (Exception e) {
			LOGGER.error("Initialize RSA Cipher Error!", e);
			return null;
		}
	}
	
	private static Cipher initRSADecryptCipher(Key key) {
		try {
			//	Initialize decrypt Cipher
			Cipher decryptCipher = Cipher.getInstance("RSA", new BouncyCastleProvider());
			decryptCipher.init(Cipher.DECRYPT_MODE, key);
			
			return decryptCipher;
		} catch (Exception e) {
			LOGGER.error("Initialize RSA Cipher Error!", e);
			return null;
		}
	}
	
	private static Key generateAESKey(byte[] keyContent, int keySize) throws NoSuchAlgorithmException {
		if (keyContent == null) {
			return null;
		}
		
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
		
		secureRandom.setSeed(keyContent);
		keyGenerator.init(keySize, secureRandom);

		SecretKey secretKey = keyGenerator.generateKey();
		
		return new SecretKeySpec(secretKey.getEncoded(), "AES");
	}
	
	private static Key generateDESKey(byte[] keyContent) throws NoSuchAlgorithmException {
		if (keyContent == null) {
			return null;
		}

		KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
		keyGenerator.init(new SecureRandom(keyContent));
		return keyGenerator.generateKey();
	}
}
