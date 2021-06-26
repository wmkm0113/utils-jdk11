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

package org.nervousync.crypto;

import org.nervousync.commons.core.Globals;
import org.nervousync.utils.ConvertUtils;
import org.nervousync.utils.DateTimeUtils;
import org.nervousync.utils.SecurityUtils;
import org.nervousync.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;

/**
 * The type Cryptor.
 */
public abstract class Cryptor {

    /**
     * The constant LOGGER.
     */
    protected static final Logger LOGGER = LoggerFactory.getLogger(Cryptor.class);

    /**
     * The Cipher mode.
     */
    protected final CipherMode cipherMode;
    private final EncodeType encodeType;

    /**
     * Instantiates a new Cryptor.
     *
     * @param cipherMode the cipher mode
     * @param encodeType the encode type
     */
    protected Cryptor(CipherMode cipherMode, EncodeType encodeType) {
        this.cipherMode = cipherMode;
        this.encodeType = encodeType;
    }

    /**
     * Encode string.
     *
     * @param dataBytes the data bytes
     * @return the string
     */
    protected String encode(byte[] dataBytes) {
        switch (this.encodeType) {
            case Hex:
                return ConvertUtils.byteToHex(dataBytes);
            case Base32:
                return StringUtils.base32Encode(dataBytes);
            case Base64:
                return StringUtils.base64Encode(dataBytes);
            default:
                return ConvertUtils.convertToString(dataBytes);
        }
    }

    /**
     * Decode byte [ ].
     *
     * @param string the string
     * @return the byte [ ]
     */
    protected byte[] decode(String string) {
        switch (this.encodeType) {
            case Hex:
                return ConvertUtils.hexToByte(string);
            case Base32:
                return StringUtils.base32Decode(string);
            case Base64:
                return StringUtils.base64Decode(string);
            default:
                return ConvertUtils.convertToByteArray(string);
        }
    }

    /**
     * Generate AES CipherKey Instance
     *
     * @param keyContent      AES Key bytes
     * @param randomAlgorithm Random Algorithm
     * @param keySize         AES Key Size
     * @return CipherKey Instance
     */
    private static CipherKey AESKey(byte[] keyContent, RandomAlgorithm randomAlgorithm, int keySize) {
        return new CipherKey(null, keyContent, randomAlgorithm, keySize);
    }

    /**
     * Generate DES CipherKey Instance
     *
     * @param keyContent the key content
     * @return CipherKey Instance
     */
    private static CipherKey DESKey(byte[] keyContent) {
        return new CipherKey(null, keyContent, RandomAlgorithm.NONE, Globals.DEFAULT_VALUE_INT);
    }

    private static CipherKey SM2Key(Key key) {
        return new CipherKey(key, new byte[0], RandomAlgorithm.NONE, Globals.DEFAULT_VALUE_INT);
    }

    /**
     * Generate DES CipherKey Instance
     *
     * @param keyContent      Key content
     * @param randomAlgorithm Random Algorithm
     * @return CipherKey Instance
     */
    private static CipherKey SM4Key(byte[] keyContent, RandomAlgorithm randomAlgorithm) {
        return new CipherKey(null, keyContent, randomAlgorithm, 128);
    }

    private static CipherKey RSAKey(Key key) {
        return new CipherKey(key, new byte[0], RandomAlgorithm.NONE, Globals.DEFAULT_VALUE_INT);
    }

    /**
     * Encrypt byte arrays with given algorithm, PRNG algorithm, key size and encrypt key
     *
     * @param cipherMode      the cipher mode
     * @param randomAlgorithm the random algorithm
     * @param arrB            Array data
     * @param key             RSA Key
     * @param keyData         Given binary key
     * @param keySize         Key Size
     * @return Encrypted Data
     * @throws NoSuchAlgorithmException the no such algorithm exception
     */
    protected static byte[] encryptData(CipherMode cipherMode, RandomAlgorithm randomAlgorithm, byte[] arrB,
                                        Key key, byte[] keyData, int keySize) throws NoSuchAlgorithmException {
        if (cipherMode == null) {
            throw new NoSuchAlgorithmException("Unknown algorithm! ");
        }
        try {
            switch (cipherMode.getAlgorithm()) {
                case "AES":
                    return initCipher(cipherMode.toString(), Cipher.ENCRYPT_MODE,
                            AESKey(keyData, randomAlgorithm, keySize)).doFinal(arrB);
                case "DES":
                case "DESede":
                    return initCipher(cipherMode.toString(), Cipher.ENCRYPT_MODE, DESKey(keyData)).doFinal(arrB);
                case "SM2":
                    return initCipher(cipherMode.toString(), Cipher.ENCRYPT_MODE, SM2Key(key)).doFinal(arrB);
                case "SM4":
                    return initCipher(cipherMode.toString(), Cipher.ENCRYPT_MODE,
                            SM4Key(keyData, randomAlgorithm)).doFinal(arrB);
                case "RSA":
                    Cipher cipher = initCipher(cipherMode.toString(), Cipher.ENCRYPT_MODE, RSAKey(key));
                    int blockSize = cipher.getBlockSize();
                    int outputSize = cipher.getOutputSize(arrB.length);
                    int leftSize = arrB.length % blockSize;
                    int blocksSize = leftSize != 0 ? arrB.length / blockSize + 1
                            : arrB.length / blockSize;
                    byte[] byteArray = new byte[outputSize * blocksSize];
                    int i = 0;
                    while (arrB.length - i * blockSize > 0) {
                        cipher.doFinal(arrB, i * blockSize, Math.min(arrB.length - i * blockSize, blockSize),
                                byteArray, i * outputSize);
                        i++;
                    }
                    return byteArray;
                default:
                    throw new NoSuchAlgorithmException("Unknown algorithm! ");
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
     *
     * @param cipherMode      the cipher mode
     * @param randomAlgorithm the random algorithm
     * @param arrB            Array data
     * @param key             RSA Key
     * @param keyData         Given binary key
     * @param keySize         Key Size
     * @return Encrypted Data
     * @throws NoSuchAlgorithmException the no such algorithm exception
     */
    protected static byte[] decryptData(CipherMode cipherMode, RandomAlgorithm randomAlgorithm, byte[] arrB,
                                        Key key, byte[] keyData, int keySize) throws NoSuchAlgorithmException {
        if (cipherMode == null) {
            throw new NoSuchAlgorithmException("Unknown algorithm! ");
        }
        try {
            switch (cipherMode.getAlgorithm()) {
                case "AES":
                    return initCipher(cipherMode.toString(), Cipher.DECRYPT_MODE,
                            AESKey(keyData, randomAlgorithm, keySize)).doFinal(arrB);
                case "DES":
                case "DESede":
                    return initCipher(cipherMode.toString(), Cipher.DECRYPT_MODE,
                            DESKey(keyData)).doFinal(arrB);
                case "SM2":
                    return initCipher(cipherMode.toString(), Cipher.DECRYPT_MODE,
                            SM2Key(key)).doFinal(arrB);
                case "SM4":
                    return initCipher(cipherMode.toString(), Cipher.DECRYPT_MODE,
                            SM4Key(keyData, randomAlgorithm)).doFinal(arrB);
                case "RSA":
                    Cipher cipher = initCipher(cipherMode.toString(), Cipher.DECRYPT_MODE, RSAKey(key));
                    int blockSize = cipher.getBlockSize();
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    int j = 0;
                    while (arrB.length - j * blockSize > 0) {
                        byteArrayOutputStream.write(cipher.doFinal(arrB, j * blockSize, blockSize));
                        j++;
                    }

                    return byteArrayOutputStream.toByteArray();
                default:
                    throw new NoSuchAlgorithmException("Unknown algorithm! ");
            }
        } catch (Exception e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Decrypt data error! ", e);
            }
        }
        return new byte[0];
    }

    /**
     * Initialize Cipher Instance
     *
     * @param algorithm  Algorithm
     * @param cipherMode Cipher Mode
     * @param cipherKey  CipherKey Instance
     * @return Cipher Instance
     * @throws GeneralSecurityException the general security exception
     * @throws InvalidKeyException      If RSA Key invalid
     */
    protected static Cipher initCipher(String algorithm, int cipherMode, CipherKey cipherKey)
            throws GeneralSecurityException, InvalidKeyException {
        long currentTime = DateTimeUtils.currentUTCTimeMillis();
        IvParameterSpec ivParameterSpec = null;
        byte[] keyContent = cipherKey.getKeyContent();
        byte[] ivContent = new byte[0];
        Key key;

        if (algorithm.startsWith("AES")) {
            key = new SecretKeySpec(keyContent, "AES");
            if (!algorithm.startsWith("AES/ECB")) {
                ivContent = new byte[16];
            }
        } else if (algorithm.startsWith("DESede")) {
            try {
                DESedeKeySpec deSedeKeySpec = new DESedeKeySpec(keyContent);
                SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
                key = keyFactory.generateSecret(deSedeKeySpec);
            } catch (Exception e) {
                key = null;
            }
            if (!algorithm.startsWith("DESede/ECB")) {
                ivContent = new byte[8];
            }
        } else if (algorithm.startsWith("DES")) {
            try {
                DESKeySpec desKeySpec = new DESKeySpec(keyContent);
                SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
                key = keyFactory.generateSecret(desKeySpec);
            } catch (Exception e) {
                key = null;
            }
            if (!algorithm.startsWith("DES/ECB")) {
                ivContent = new byte[8];
            }
        } else if (algorithm.startsWith("SM4")) {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("SM4", "BC");
            SecureRandom secureRandom = SecureRandom.getInstance(cipherKey.getRandomAlgorithm().getAlgorithm());
            secureRandom.setSeed(keyContent);
            keyGenerator.init(cipherKey.getKeySize(), secureRandom);
            key = keyGenerator.generateKey();
            if (!algorithm.startsWith("SM4/ECB")) {
                ivContent = new byte[8];
            }
        } else {
            key = cipherKey.getRsaKey();
        }

        if (ivContent.length > 0) {
            System.arraycopy(SecurityUtils.MD5(keyContent).getBytes(StandardCharsets.UTF_8),
                    0, ivContent, 0, ivContent.length);
            ivParameterSpec = new IvParameterSpec(ivContent);
        }

        long generateTime = DateTimeUtils.currentUTCTimeMillis() - currentTime;

        Cipher cipher = Cipher.getInstance(algorithm, "BC");
        cipher.init(cipherMode, key, ivParameterSpec);

        long initTime = DateTimeUtils.currentUTCTimeMillis() - currentTime - generateTime;

        long usedTime = DateTimeUtils.currentUTCTimeMillis() - currentTime;
        if (usedTime > 500) {
            System.out.println("Used time: " + usedTime + ":" + generateTime + ":" + initTime);
        }
        return cipher;
    }

    /**
     * The type Cipher mode.
     */
    protected static final class CipherMode {

        private final String algorithm;
        private final String mode;
        private final String padding;

        /**
         * Instantiates a new Cipher mode.
         *
         * @param algorithm the algorithm
         * @param mode      the mode
         * @param padding   the padding
         */
        public CipherMode(String algorithm, String mode, String padding) {
            this.algorithm = algorithm;
            this.mode = mode;
            this.padding = padding;
        }

        /**
         * Gets algorithm.
         *
         * @return the algorithm
         */
        public String getAlgorithm() {
            return algorithm;
        }

        public String toString() {
            return String.join("/", this.algorithm, this.mode, this.padding);
        }
    }

    /**
     * The enum Encode type.
     */
    public enum EncodeType {
        /**
         * Base 32 encode type.
         */
        Base32,
        /**
         * Base 64 encode type.
         */
        Base64,
        /**
         * Hex encode type.
         */
        Hex,
        /**
         * Default encode type.
         */
        Default
    }

    /**
     * The enum Random algorithm.
     */
    public enum RandomAlgorithm {
        /**
         * None random algorithm.
         */
        NONE(""),
        /**
         * Native random algorithm.
         */
        NATIVE("NativePRNG"),
        /**
         * Native blocking random algorithm.
         */
        NATIVE_BLOCKING("NativePRNGBlocking"),
        /**
         * Native non blocking random algorithm.
         */
        NATIVE_NON_BLOCKING("NativePRNGNonBlocking"),
        /**
         * Pkcs 11 random algorithm.
         */
        PKCS11("PKCS11"),
        /**
         * Sha 1 prng random algorithm.
         */
        SHA1PRNG("SHA1PRNG"),
        /**
         * Windows random algorithm.
         */
        WINDOWS("Windows-PRNG");

        private final String algorithm;

        RandomAlgorithm(String algorithm) {
            this.algorithm = algorithm;
        }

        /**
         * Gets algorithm.
         *
         * @return the algorithm
         */
        public String getAlgorithm() {
            return algorithm;
        }
    }

    /**
     * Cipher key
     */
    private static final class CipherKey {

        //  RSA key(PublicKey or PrivateKey)
        private final Key rsaKey;
        //  AES/DES/TripleDES Key bytes
        private final byte[] keyContent;
        //  PRNG Algorithm
        private final RandomAlgorithm randomAlgorithm;
        //  AES Key Size
        private final int keySize;

        /**
         * Constructor for CipherKey
         *
         * @param rsaKey          RSA Key Instance
         * @param keyContent      AES/DES/3DES Key bytes
         * @param randomAlgorithm Random Algorithm
         * @param keySize         AES Key Size
         */
        private CipherKey(Key rsaKey, byte[] keyContent, RandomAlgorithm randomAlgorithm, int keySize) {
            this.rsaKey = rsaKey;
            this.keyContent = keyContent;
            this.randomAlgorithm = randomAlgorithm;
            this.keySize = keySize;
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
         * Gets random algorithm.
         *
         * @return the random algorithm
         */
        public RandomAlgorithm getRandomAlgorithm() {
            return randomAlgorithm;
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
