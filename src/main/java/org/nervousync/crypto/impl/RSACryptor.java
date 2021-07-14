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

package org.nervousync.crypto.impl;

import org.nervousync.crypto.core.AsymmetricCryptor;

import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Date;
import java.util.Optional;

/**
 * The type Rsa cryptor.
 */
public final class RSACryptor extends AsymmetricCryptor {

    private static final int DEFAULT_KEY_SIZE = 1024;

    /**
     * Instantiates a new Rsa cryptor.
     *
     * @param rsaMode    the rsa mode
     * @param encodeType the encode type
     * @param publicKey  the public key
     * @param privateKey the private key
     */
    public RSACryptor(RSAMode rsaMode, EncodeType encodeType, PublicKey publicKey, PrivateKey privateKey) {
        super(rsaMode.getCipherMode(), encodeType, "SHA256withRSA", publicKey, privateKey);
    }

    /**
     * Generate RSA public key with given key content
     *
     * @param keyContent key content
     * @return RSA public key object
     */
    public static Optional<PublicKey> publicKey(byte[] keyContent) {
        return generateKey(PublicKey.class, "RSA", keyContent);
    }

    /**
     * Generate RSA public key with given modulus and exponent
     *
     * @param modulus  modulus
     * @param exponent exponent
     * @return RSA public key object
     */
    public static Optional<PublicKey> publicKey(BigInteger modulus, BigInteger exponent) {
        return generateRSAKey(PublicKey.class, modulus, exponent);
    }

    /**
     * Generate RSA private key with given key content
     *
     * @param keyContent key content
     * @return RSA private key object
     */
    public static Optional<PrivateKey> privateKey(byte[] keyContent) {
        return generateKey(PrivateKey.class, "RSA", keyContent);
    }

    /**
     * Generate RSA private key with given modulus and exponent
     *
     * @param modulus  modulus
     * @param exponent exponent
     * @return RSA private key object
     */
    public static Optional<PrivateKey> privateKey(BigInteger modulus, BigInteger exponent) {
        return generateRSAKey(PrivateKey.class, modulus, exponent);
    }

    /**
     * Key pair optional.
     *
     * @return the optional
     */
    public static Optional<KeyPair> generateKeyPair() {
        return generateKeyPair(DEFAULT_KEY_SIZE);
    }

    /**
     * Key pair optional.
     *
     * @param keySize the key size
     * @return the optional
     */
    public static Optional<KeyPair> generateKeyPair(int keySize) {
        return generateKeyPair(keySize, RandomAlgorithm.SHA1PRNG);
    }

    /**
     * Key pair optional.
     *
     * @param keySize         the key size
     * @param randomAlgorithm the random algorithm
     * @return the optional
     */
    public static Optional<KeyPair> generateKeyPair(int keySize, RandomAlgorithm randomAlgorithm) {
        return AsymmetricCryptor.KeyPair("RSA", randomAlgorithm.getAlgorithm(), keySize);
    }

    /**
     * X 509 certificate byte [ ].
     *
     * @param publicKey    the public key
     * @param serialNumber the serial number
     * @param beginDate    the begin date
     * @param endDate      the end date
     * @param certName     the cert name
     * @param signKey      the sign key
     * @return byte [ ]
     */
    public static byte[] x509Certificate(PublicKey publicKey, long serialNumber, Date beginDate, Date endDate,
                                         String certName, PrivateKey signKey) {
        return AsymmetricCryptor.x509Certificate(publicKey, serialNumber, beginDate, endDate,
                certName, signKey, "SHA256withRSA")
                .map(x509Certificate -> {
                    try {
                        return x509Certificate.getEncoded();
                    } catch (CertificateEncodingException e) {
                        return new byte[0];
                    }
                })
                .orElse(new byte[0]);
    }

    /**
     * Key pair optional.
     *
     * @param serialNumber the serial number
     * @param beginDate    the begin date
     * @param endDate      the end date
     * @param certAlias    the cert alias
     * @param certName     the cert name
     * @param password     the password
     * @param signKey      the sign key
     * @return the optional
     */
    public static byte[] PKCS12(long serialNumber, Date beginDate, Date endDate, String certAlias,
                                String certName, String password, PrivateKey signKey) {
        return PKCS12(DEFAULT_KEY_SIZE, serialNumber, beginDate, endDate, certAlias, certName, password, signKey);
    }

    /**
     * Key pair optional.
     *
     * @param keySize      the key size
     * @param serialNumber the serial number
     * @param beginDate    the begin date
     * @param endDate      the end date
     * @param certAlias    the cert alias
     * @param certName     the cert name
     * @param password     the password
     * @param signKey      the sign key
     * @return the optional
     */
    public static byte[] PKCS12(int keySize, long serialNumber, Date beginDate, Date endDate,
                                String certAlias, String certName, String password, PrivateKey signKey) {
        return PKCS12(keySize, RandomAlgorithm.SHA1PRNG, serialNumber, beginDate,
                endDate, certAlias, certName, password, signKey);
    }

    /**
     * Key pair optional.
     *
     * @param keySize         the key size
     * @param randomAlgorithm the random algorithm
     * @param serialNumber    the serial number
     * @param beginDate       the begin date
     * @param endDate         the end date
     * @param certAlias       the cert alias
     * @param certName        the cert name
     * @param password        the password
     * @param signKey         the sign key
     * @return the optional
     */
    public static byte[] PKCS12(int keySize, RandomAlgorithm randomAlgorithm, long serialNumber, Date beginDate,
                                Date endDate, String certAlias, String certName, String password, PrivateKey signKey) {
        return KeyPair("RSA", randomAlgorithm.getAlgorithm(), keySize)
                .map(keyPair ->
                        PKCS12Certificate(keyPair, serialNumber, beginDate, endDate, certAlias,
                                certName, password, signKey, "SHA256withRSA"))
                .orElse(new byte[0]);
    }

    @Override
    protected byte[] convertResult(byte[] dataBytes) {
        return dataBytes;
    }

    @Override
    protected byte[] parseResult(byte[] dataBytes) {
        return dataBytes;
    }

    private static <T> Optional<T> generateRSAKey(Class<T> clazz, BigInteger modulus, BigInteger exponent) {
        Object generatedKey = null;
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA", "BC");
            if (PrivateKey.class.equals(clazz)) {
                RSAPrivateKeySpec privateKeySpec = new RSAPrivateKeySpec(modulus, exponent);
                generatedKey = keyFactory.generatePrivate(privateKeySpec);
            } else if (PublicKey.class.equals(clazz)) {
                RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(modulus, exponent);
                generatedKey = keyFactory.generatePublic(publicKeySpec);
            }
        } catch (InvalidKeySpecException | NoSuchAlgorithmException | NoSuchProviderException e) {
            LOGGER.error("Generate key from data bytes error! ");
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Stack message: ", e);
            }
        }
        return generatedKey == null ? Optional.empty() : Optional.of(clazz.cast(generatedKey));
    }

    /**
     * The enum Rsa mode.
     */
    public enum RSAMode {
        /**
         * RSA/ECB/NoPadding
         */
        NoPadding("NoPadding"),
        /**
         * RSA/ECB/PKCS1Padding
         */
        PKCS1Padding("PKCS1Padding"),
        /**
         * RSA/ECB/OAEPWithMD5-AndMGF1Padding
         */
        OAEPWITHMD5ANDMGF1Padding("OAEPWithMD5-AndMGF1Padding"),
        /**
         * RSA/ECB/OAEPWithSHA-1AndMGF1Padding
         */
        OAEPWITHSHA1ANDMGF1Padding("OAEPWithSHA-1AndMGF1Padding"),
        /**
         * RSA/ECB/OAEPWithSHA-256AndMGF1Padding
         */
        OAEPWITHSHA256ANDMGF1Padding("OAEPWithSHA-256AndMGF1Padding"),
        /**
         * RSA/ECB/OAEPWithSHA-384AndMGF1Padding
         */
        OAEPWITHSHA384ANDMGF1Padding("OAEPWithSHA-384AndMGF1Padding"),
        /**
         * RSA/ECB/OAEPWithSHA-512AndMGF1Padding
         */
        OAEPWITHSHA512ANDMGF1Padding("OAEPWithSHA-512AndMGF1Padding");

        private final CipherMode cipherMode;

        RSAMode(String padding) {
            this.cipherMode = new CipherMode("RSA", "ECB", padding);
        }

        /**
         * Gets cipher mode.
         *
         * @return the cipher mode
         */
        public CipherMode getCipherMode() {
            return cipherMode;
        }

        public String toString() {
            return this.cipherMode.toString();
        }
    }
}
