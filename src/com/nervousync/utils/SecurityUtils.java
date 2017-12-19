/*
 * Copyright © 2003 - 2010 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervousync Studio.
 */
package com.nervousync.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
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
	
	/* AES Utils Variables */
	private static boolean SUPPORT_ADVANCE_MODE = false;
	private static final int AES_NORMAL_KEYSIZE = 128;
	private static final int AES_ADVANCE_KEYSIZE = 256;

	/**
	 * Default key value
	 */
	private static final String PRIVATE_KEY = StringUtils.randomString(32);

	static {
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
			String keyContent = StringUtils.randomString(32);
			byte[] seed = keyContent.getBytes();
			keyGenerator.init(AES_ADVANCE_KEYSIZE, new SecureRandom(seed));
			
			SecurityUtils.SUPPORT_ADVANCE_MODE = true;
		} catch (Exception e) {
			LOGGER.warn("AES/SHA is running at normal mode, please update security settings to use advance mode");
			SecurityUtils.SUPPORT_ADVANCE_MODE = false;
		}
	}
	
	private SecurityUtils() {
		
	}
	
	public static boolean isSupportAdvanceMode() {
		return SUPPORT_ADVANCE_MODE;
	}
	
	/* MD5 Method */
	
	/**
	 * Get MD5 value. Only encode <code>String</code>
	 * @param source
	 * @return MD5 value
	 */
	public static String MD5Encode(Object source) {
		return digestEncode(source, "MD5");
	}
	
	/* SHA Method */
	
	/**
	 * Get SHA value. Only encode <code>String</code>
	 * @param source
	 * @return MD5 value
	 */
	public static String SHAEncode(Object source) {
		return SHAEncode(source, SecurityUtils.SUPPORT_ADVANCE_MODE);
	}
	
	/**
	 * Get SHA value. Only encode <code>String</code>
	 * @param source
	 * @param advanceMode
	 * @return MD5 value
	 */
	public static String SHAEncode(Object source, boolean advanceModeIfSupport) {
		String algorithm = null;
		if (SecurityUtils.SUPPORT_ADVANCE_MODE && advanceModeIfSupport) {
			algorithm = "SHA-512";
		}

		if (algorithm == null) {
			algorithm = "SHA-256";
		}
		
		return digestEncode(source, algorithm);
	}

	/* AES Method */
	
	/**
	 * 加密字节数组
	 * 
	 * @param arrB						需加密的字节数组
	 * @param strKey					使用的加密密钥
	 * @param keySize					密钥长度
	 * @param advanceModeIfSupport		强制指定高级模式加密
	 * @return							加密后的字节数组
	 * @throws Exception				本方法不处理任何异常，所有异常全部抛出
	 */
	public static byte[] AESEncrypt(byte[] arrB, String strKey) throws Exception {
		return AESEncrypt(arrB, strKey, SecurityUtils.SUPPORT_ADVANCE_MODE);
	}

	/**
	 * 加密字节数组
	 * 
	 * @param arrB						需加密的字节数组
	 * @param strKey					使用的加密密钥
	 * @param keySize					密钥长度
	 * @param advanceModeIfSupport		强制指定高级模式加密
	 * @return							加密后的字节数组
	 * @throws Exception				本方法不处理任何异常，所有异常全部抛出
	 */
	public static byte[] AESEncrypt(byte[] arrB, String strKey, boolean advanceModeIfSupport) throws Exception {
		if (strKey == null || strKey.length() % 16 != 0) {
			throw new Exception("Data length error");
		}
		Cipher encryptCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		Key key = SecurityUtils.generateAESKey(strKey.getBytes(), advanceModeIfSupport);
		encryptCipher.init(Cipher.ENCRYPT_MODE, key);
		return encryptCipher.doFinal(arrB);
	}

	/**
	 * 加密字符串
	 * 
	 * @param strIn						需加密的字符串
	 * @param strKey					使用的加密密钥
	 * @return							加密后的字节数组
	 * @throws Exception				本方法不处理任何异常，所有异常全部抛出
	 */
	public static String AESEncrypt(String strIn, String strKey) throws Exception {
		return ConvertUtils.byteArrayToHexString(AESEncrypt(ConvertUtils.convertToByteArray(strIn), 
				strKey, SecurityUtils.SUPPORT_ADVANCE_MODE));
	}

	/**
	 * 加密字符串
	 * 
	 * @param strIn						需加密的字符串
	 * @param strKey					使用的加密密钥
	 * @param advanceModeIfSupport		强制指定高级模式加密
	 * @return							加密后的字节数组
	 * @throws Exception				本方法不处理任何异常，所有异常全部抛出
	 */
	public static String AESEncrypt(String strIn, String strKey, boolean advanceModeIfSupport) throws Exception {
		return ConvertUtils.byteArrayToHexString(AESEncrypt(ConvertUtils.convertToByteArray(strIn), strKey, advanceModeIfSupport));
	}

	/**
	 * 使用默认密钥加密字节数组
	 * 
	 * @param arrB						需加密的字节数组
	 * @return							加密后的字节数组
	 * @throws Exception				本方法不处理任何异常，所有异常全部抛出
	 */
	public static byte[] AESEncrypt(byte[] arrB) throws Exception {
		return AESEncrypt(arrB, SecurityUtils.PRIVATE_KEY, SecurityUtils.SUPPORT_ADVANCE_MODE);
	}

	/**
	 * 使用默认密钥加密字节数组
	 * 
	 * @param arrB						需加密的字节数组
	 * @param advanceModeIfSupport		强制指定高级模式加密
	 * @return							加密后的字节数组
	 * @throws Exception				本方法不处理任何异常，所有异常全部抛出
	 */
	public static byte[] AESEncrypt(byte[] arrB, boolean advanceModeIfSupport) throws Exception {
		return AESEncrypt(arrB, SecurityUtils.PRIVATE_KEY, advanceModeIfSupport);
	}

	/**
	 * 使用默认密钥加密字符串
	 * 
	 * @param strIn						需加密的字符串
	 * @return							加密后的字符串
	 * @throws Exception				本方法不处理任何异常，所有异常全部抛出
	 */
	public static String AESEncrypt(String strIn) throws Exception {
		return AESEncrypt(strIn, SecurityUtils.PRIVATE_KEY, SecurityUtils.SUPPORT_ADVANCE_MODE);
	}

	/**
	 * 使用默认密钥加密字符串
	 * 
	 * @param strIn						需加密的字符串
	 * @param advanceModeIfSupport		强制指定高级模式加密
	 * @return							加密后的字符串
	 * @throws Exception				本方法不处理任何异常，所有异常全部抛出
	 */
	public static String AESEncrypt(String strIn, boolean advanceModeIfSupport) throws Exception {
		return AESEncrypt(strIn, SecurityUtils.PRIVATE_KEY, advanceModeIfSupport);
	}
	
	/**
	 * 解密字节数组
	 * 
	 * @param arrB						需解密的字节数组
	 * @param strKey					使用的解密密钥
	 * @return							解密后的字节数组
	 * @throws Exception				本方法不处理任何异常，所有异常全部抛出
	 */
	public static byte[] AESDecrypt(byte[] arrB, String strKey) throws Exception {
		return AESDecrypt(arrB, strKey, SecurityUtils.SUPPORT_ADVANCE_MODE);
	}
	
	/**
	 * 解密字节数组
	 * 
	 * @param arrB						需解密的字节数组
	 * @param strKey					使用的解密密钥
	 * @param advanceModeIfSupport		强制指定高级模式加密
	 * @return							解密后的字节数组
	 * @throws Exception				本方法不处理任何异常，所有异常全部抛出
	 */
	public static byte[] AESDecrypt(byte[] arrB, String strKey, boolean advanceModeIfSupport) throws Exception {
		if (strKey == null || strKey.length() % 16 != 0) {
			throw new Exception("Data length error");
		}
		Cipher decryptCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		Key key = SecurityUtils.generateAESKey(strKey.getBytes(), advanceModeIfSupport);
		decryptCipher.init(Cipher.DECRYPT_MODE, key);
		return decryptCipher.doFinal(arrB);
	}

	/**
	 * 解密字符串
	 * 
	 * @param strIn						需解密的字符串
	 * @param strKey					使用的解密密钥
	 * @return							解密后的字符串
	 * @throws Exception				本方法不处理任何异常，所有异常全部抛出
	 */
	public static String AESDecrypt(String strIn, String strKey) throws Exception {
		return AESDecrypt(strIn, strKey, SUPPORT_ADVANCE_MODE);
	}

	/**
	 * 解密字符串
	 * 
	 * @param strIn						需解密的字符串
	 * @param strKey					使用的解密密钥
	 * @param advanceModeIfSupport		强制指定高级模式加密
	 * @return							解密后的字符串
	 * @throws Exception				本方法不处理任何异常，所有异常全部抛出
	 */
	public static String AESDecrypt(String strIn, String strKey, boolean advanceModeIfSupport) throws Exception {
		try {
			byte[] decryptData = AESDecrypt(ConvertUtils.hexStrToByteArr(strIn), strKey, advanceModeIfSupport);
			return ConvertUtils.convertToString(decryptData);
		} catch (Exception e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Decrypt data error! ", e);
			}
		}
		return null;
	}

	/**
	 * 使用默认密钥解密字节数组
	 * 
	 * @param arrB						需解密的字节数组
	 * @param strKey					使用的解密密钥
	 * @return							解密后的字节数组
	 * @throws Exception				本方法不处理任何异常，所有异常全部抛出
	 */
	public static byte[] AESDecrypt(byte[] arrB) throws Exception {
		return AESDecrypt(arrB, SecurityUtils.PRIVATE_KEY, SecurityUtils.SUPPORT_ADVANCE_MODE);
	}

	/**
	 * 使用默认密钥解密字节数组
	 * 
	 * @param arrB						需解密的字节数组
	 * @param strKey					使用的解密密钥
	 * @param advanceModeIfSupport		强制指定高级模式加密
	 * @return							解密后的字节数组
	 * @throws Exception				本方法不处理任何异常，所有异常全部抛出
	 */
	public static byte[] AESDecrypt(byte[] arrB, boolean advanceModeIfSupport) throws Exception {
		return AESDecrypt(arrB, SecurityUtils.PRIVATE_KEY, advanceModeIfSupport);
	}

	/**
	 * 使用默认密钥解密字符串
	 * 
	 * @param strIn						需解密的字符串
	 * @param strKey					使用的解密密钥
	 * @return							解密后的字符串
	 * @throws Exception				本方法不处理任何异常，所有异常全部抛出
	 */
	public static String AESDecrypt(String strIn) throws Exception {
		return AESDecrypt(strIn, SecurityUtils.PRIVATE_KEY, SecurityUtils.SUPPORT_ADVANCE_MODE);
	}

	/**
	 * 使用默认密钥解密字符串
	 * 
	 * @param strIn						需解密的字符串
	 * @param strKey					使用的解密密钥
	 * @param advanceModeIfSupport		强制指定高级模式加密
	 * @return							解密后的字符串
	 * @throws Exception				本方法不处理任何异常，所有异常全部抛出
	 */
	public static String AESDecrypt(String strIn, boolean advanceModeIfSupport) throws Exception {
		return AESDecrypt(strIn, SecurityUtils.PRIVATE_KEY, advanceModeIfSupport);
	}
	
	/* DES Method */

	/**
	 * 加密字节数组
	 * 
	 * @param arrB			需加密的字节数组
	 * @param strKey		使用的加密密钥
	 * @return				加密后的字节数组
	 * @throws Exception	本方法不处理任何异常，所有异常全部抛出
	 */
	public static byte[] DESEncrypt(byte[] arrB, String strKey) throws Exception {
		Cipher encryptCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
		Security.addProvider(new com.sun.crypto.provider.SunJCE());
		Key key = SecurityUtils.generateDESKey(strKey.getBytes());
		encryptCipher.init(Cipher.ENCRYPT_MODE, key);
		return encryptCipher.doFinal(arrB);
	}

	/**
	 * 加密字符串
	 * 
	 * @param strIn			需加密的字符串
	 * @param strKey		使用的加密密钥
	 * @return				加密后的字符串
	 * @throws Exception	本方法不处理任何异常，所有异常全部抛出
	 */
	public static String DESEncrypt(String strIn, String strKey) throws Exception {
		return ConvertUtils.byteArrayToHexString(SecurityUtils.DESEncrypt(ConvertUtils.convertToByteArray(strIn), strKey));
	}
	
	/**
	 * 使用默认密钥加密字节数组
	 * 
	 * @param arrB			需加密的字节数组
	 * @return				加密后的字节数组
	 * @throws Exception	本方法不处理任何异常，所有异常全部抛出
	 */
	public static byte[] DESEncrypt(byte[] arrB) throws Exception {
		return SecurityUtils.DESEncrypt(arrB, SecurityUtils.PRIVATE_KEY);
	}

	/**
	 * 使用默认密钥加密字符串
	 * 
	 * @param strIn			需加密的字符串
	 * @return				加密后的字符串
	 * @throws Exception	本方法不处理任何异常，所有异常全部抛出
	 */
	public static String DESEncrypt(String strIn) throws Exception {
		return SecurityUtils.DESEncrypt(strIn, SecurityUtils.PRIVATE_KEY);
	}

	/**
	 * 解密字节数组
	 * 
	 * @param arrB			需解密的字节数组
	 * @param strKey		使用的解密密钥
	 * @return				解密后的字节数组
	 * @throws Exception	本方法不处理任何异常，所有异常全部抛出
	 */
	public static byte[] DESDecrypt(byte[] arrB, String strKey) throws Exception {
		Cipher decryptCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
		Security.addProvider(new com.sun.crypto.provider.SunJCE());
		Key key = SecurityUtils.generateDESKey(strKey.getBytes());
		decryptCipher.init(Cipher.DECRYPT_MODE, key);
		return decryptCipher.doFinal(arrB);
	}

	/**
	 * 解密字符串
	 * 
	 * @param strIn			需解密的字符串
	 * @param strKey		使用的解密密钥
	 * @return				解密后的字符串
	 * @throws Exception	本方法不处理任何异常，所有异常全部抛出
	 */
	public static String DESDecrypt(String strIn, String strKey) throws Exception {
		return ConvertUtils.convertToString(SecurityUtils.DESDecrypt(ConvertUtils.hexStrToByteArr(strIn), strKey));
	}

	/**
	 * 使用默认密钥解密字节数组
	 * 
	 * @param arrB			需解密的字节数组
	 * @param strKey		使用的解密密钥
	 * @return				解密后的字节数组
	 * @throws Exception	本方法不处理任何异常，所有异常全部抛出
	 */
	public static byte[] DESDecrypt(byte[] arrB) throws Exception {
		return SecurityUtils.DESDecrypt(arrB, SecurityUtils.PRIVATE_KEY);
	}

	/**
	 * 使用默认密钥解密字符串
	 * 
	 * @param strIn			需解密的字符串
	 * @param strKey		使用的解密密钥
	 * @return				解密后的字符串
	 * @throws Exception	本方法不处理任何异常，所有异常全部抛出
	 */
	public static String DESDecrypt(String strIn) throws Exception {
		return SecurityUtils.DESDecrypt(strIn, SecurityUtils.PRIVATE_KEY);
	}

	/**
	 * 加密字符串
	 * 
	 * @param strIn			需加密的字符串
	 * @param strKey		使用的加密密钥
	 * @return				加密后的字符串
	 * @throws Exception	本方法不处理任何异常，所有异常全部抛出
	 */
	public static String RSAEncrypt(String strIn, Key key) throws Exception {
		return ConvertUtils.byteArrayToHexString(SecurityUtils.RSAEncrypt(ConvertUtils.convertToByteArray(strIn), key));
	}
	
	/**
	 * 加密字节数组
	 * 
	 * @param arrB			需加密的字节数组
	 * @param strKey		使用的加密密钥
	 * @return				加密后的字节数组
	 * @throws Exception	本方法不处理任何异常，所有异常全部抛出
	 */
	public static byte[] RSAEncrypt(byte[] arrB, Key key) throws Exception {
		Cipher encryptCipher = SecurityUtils.initRSAEncryptClipher(key);
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
	 * 解密字符串
	 * 
	 * @param strIn			需解密的字符串
	 * @param strKey		使用的解密密钥
	 * @return				解密后的字符串
	 * @throws Exception	本方法不处理任何异常，所有异常全部抛出
	 */
	public static String RSADecrypt(String strIn, Key key) throws Exception {
		return new String(SecurityUtils.RSADecrypt(ConvertUtils.hexStrToByteArr(strIn), key));
	}
	
	/**
	 * 解密字节数组
	 * 
	 * @param arrB			需解密的字节数组
	 * @param privateKey	使用的解密密钥
	 * @return				解密后的字节数组
	 * @throws Exception	本方法不处理任何异常，所有异常全部抛出
	 */
	public static byte[] RSADecrypt(byte[] arrB, Key key) throws Exception {
		Cipher decryptCipher = initRSADecryptClipher(key);
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

	public static KeyPair generateKeyPair() throws Exception {
		return SecurityUtils.generateKeyPair(1024);
	}

	public static KeyPair generateKeyPair(int keySize) throws Exception {
		if (keySize % 128 != 0) {
			throw new Exception("Keysize is invalid");
		}
		//	Initialize keyPair instance
		KeyPairGenerator keyPairGenerator = 
			KeyPairGenerator.getInstance("RSA", new BouncyCastleProvider());
		keyPairGenerator.initialize(keySize, new SecureRandom());
		
		//	Generate keyPair
		return keyPairGenerator.generateKeyPair();
	}

	public static PublicKey generateRSAPublicKey(byte[] keyContent) 
			throws InvalidKeySpecException, NoSuchAlgorithmException {
		return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(keyContent));
	}
	
	public static PublicKey generateRSAPublicKey(BigInteger modulus, BigInteger exponent) 
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		KeyFactory keyFactory = KeyFactory.getInstance("RSA", new BouncyCastleProvider());
		RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(modulus, exponent);
		
		return keyFactory.generatePublic(publicKeySpec);
	}
	
	public static PrivateKey generateRSAPrivateKey(byte[] keyContent) 
			throws InvalidKeySpecException, NoSuchAlgorithmException {
		return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(keyContent));
	}
	
	public static PrivateKey generateRSAPrivateKey(BigInteger modulus, BigInteger exponent) 
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		KeyFactory keyFactory = KeyFactory.getInstance("RSA", new BouncyCastleProvider());
		RSAPrivateKeySpec privateKeySpec = new RSAPrivateKeySpec(modulus, exponent);
		
		return keyFactory.generatePrivate(privateKeySpec);
	}
	
	/**
	 * Get MD5 value
	 * @param source	Input source
	 * @param isFile	<code>true</code> Is file <code>false</code> Is String
	 * @return MD5 value
	 * @throws ClassCastException
	 * @throws IOException 
	 */
	private static String digestEncode(Object source, String algorithm) {
		if (source == null) {
			return "";
		}
		
		MessageDigest messageDigest = null;
		
		//	Initialize MessageDigest Instance
		try {
			messageDigest = MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException ex) {
			LOGGER.error("Initialize failed, maybe the MessageDigest does not support " + algorithm + "!", ex);
		}

		if (source instanceof File) {
			File file = (File)source;
			if (file != null && file.exists() && file.isFile()) {
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
					try {
						if (fileChannel != null) {
							fileChannel.close();
						}
						
						if (fileInputStream != null) {
							fileInputStream.close();
						}
					} catch (IOException e) {
						
					}
				}
			} else {
				LOGGER.error("File does not exists" + file.getAbsolutePath());
				return "";
			}
			return ConvertUtils.byteArrayToHexString(messageDigest.digest());
		} else if (source instanceof String) {
			byte[] strTemp = ((String)source).getBytes();
			messageDigest.update(strTemp);
			return ConvertUtils.byteArrayToHexString(messageDigest.digest());
		} else {
			byte[] tempBytes = ConvertUtils.convertToByteArray(source);
			messageDigest.update(tempBytes);
			return ConvertUtils.byteArrayToHexString(messageDigest.digest());
		}
	}

	private static Cipher initRSAEncryptClipher(Key key) {
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
	
	private static Cipher initRSADecryptClipher(Key key) {
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
	
	private static Key generateAESKey(byte[] keyContent, boolean advanceModeIfSupport) throws NoSuchAlgorithmException {
		if (keyContent == null) {
			return null;
		}
		
		int keySize = Globals.DEFAULT_VALUE_INT;

		if (SUPPORT_ADVANCE_MODE) {
			keySize = AES_ADVANCE_KEYSIZE;
		} else {
			keySize = AES_NORMAL_KEYSIZE;
		}
		
		if (SUPPORT_ADVANCE_MODE && !advanceModeIfSupport) {
			keySize = AES_NORMAL_KEYSIZE;
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
