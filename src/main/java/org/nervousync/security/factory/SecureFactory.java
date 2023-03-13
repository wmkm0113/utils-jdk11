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
package org.nervousync.security.factory;

import org.nervousync.exceptions.crypto.CryptoException;
import org.nervousync.security.SecureProvider;
import org.nervousync.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.*;

/**
 * Secure factory for protect password in config file
 * <p>
 * Supported algorithm: RSA1024/RSA2048/SM2/AES128/AES192/AES256/DES/3DES/SM4
 */
public final class SecureFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecureFactory.class);

    private static final String SECURE_CERTIFICATE_ALIAS = "NSYC";
    private static final String SECURE_CERTIFICATE_PASSWORD = "ns0528AO";

    private static final SecureFactory INSTANCE = new SecureFactory();

    private SecureNode factoryNode = null;
    private final Map<String, SecureNode> registeredNodeMap;

    private SecureFactory() {
        this.registeredNodeMap = new HashMap<>();
    }

    /**
     * Initialized boolean.
     *
     * @return the boolean
     */
    public static boolean initialized() {
        return INSTANCE.factoryNode != null && INSTANCE.factoryNode.isInitialized();
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static SecureFactory getInstance() {
        return SecureFactory.INSTANCE;
    }

    /**
     * Initialize boolean.
     *
     * @param secureConfig the secure config
     * @return the boolean
     */
    public static boolean initialize(final SecureConfig secureConfig) {
        return SecureNode.initFactory(secureConfig)
                .filter(SecureNode::isInitialized)
                .map(secureNode -> {
                    if (INSTANCE.factoryNode != null && LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Override factory config! ");
                    }
                    INSTANCE.factoryNode = secureNode;
                    return Boolean.TRUE;
                })
                .orElse(Boolean.FALSE);
    }

    /**
     * Check given secure algorithm was supported
     *
     * @param secureAlgorithm Secure algorithm name
     * @return Check result
     */
    public static boolean supportedAlgorithm(String secureAlgorithm) {
        try {
            SecureAlgorithm.valueOf(secureAlgorithm);
            return Boolean.TRUE;
        } catch (IllegalArgumentException e) {
            return Boolean.FALSE;
        }
    }

    /**
     * Init config optional.
     *
     * @param secureAlgorithm the secure algorithm
     * @return optional
     */
    public static Optional<SecureConfig> initConfig(SecureAlgorithm secureAlgorithm) {
        final byte[] keyBytes = generate(secureAlgorithm);
        if (keyBytes.length == 0) {
            LOGGER.error("Key bytes is empty! ");
            return Optional.empty();
        }
        byte[] encBytes = INSTANCE.initKey(keyBytes, Boolean.TRUE);

        SecureConfig secureConfig = new SecureConfig();
        secureConfig.setSecureAlgorithm(secureAlgorithm);
        secureConfig.setSecureKey(StringUtils.base64Encode(encBytes));
        return Optional.of(secureConfig);
    }

    /**
     * Registered config boolean.
     *
     * @param configName the config name
     * @return the boolean
     */
    public boolean registeredConfig(String configName) {
        if (StringUtils.isEmpty(configName) || this.factoryNode == null || !this.factoryNode.isInitialized()) {
            return Boolean.FALSE;
        }
        return this.registeredNodeMap.containsKey(configName);
    }

    /**
     * Register boolean.
     *
     * @param configName   the config name
     * @param secureConfig the secure config
     * @return the boolean
     */
    public boolean register(String configName, SecureConfig secureConfig) {
        if (StringUtils.isEmpty(configName) || this.factoryNode == null || !this.factoryNode.isInitialized()) {
            return Boolean.FALSE;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Current Config: {}", secureConfig.toXML(Boolean.TRUE));
        }
        return SecureNode.initialize(secureConfig).filter(SecureNode::isInitialized)
                .map(secureNode -> {
                    if (this.registeredNodeMap.containsKey(configName)) {
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("Override secure config: {}", configName);
                        }
                    }
                    this.registeredNodeMap.put(configName, secureNode);
                    return Boolean.TRUE;
                })
                .orElse(Boolean.FALSE);
    }

    /**
     * Deregister.
     *
     * @param configName the config name
     */
    public void deregister(String configName) {
        if (StringUtils.notBlank(configName)) {
            this.registeredNodeMap.remove(configName);
        }
    }

    /**
     * Re-encrypt password data.
     *
     * @param dataContent   Original password data.
     * @param originalName  Original secure name.
     * @param secureName    New secure name.
     * @return      Re-encrypt password data
     */
    public String update(final String dataContent, final String originalName, final String secureName) {
        if (StringUtils.isEmpty(dataContent)) {
            return dataContent;
        }

        byte[] dataBytes = StringUtils.notBlank(originalName)
                ? this.decrypt(originalName, StringUtils.base64Decode(dataContent))
                : ConvertUtils.convertToByteArray(dataContent);
        if (StringUtils.isEmpty(secureName)) {
            return ConvertUtils.convertToString(dataBytes);
        } else {
            return StringUtils.base64Encode(this.encrypt(secureName, dataBytes));
        }
    }

    /**
     * Encrypt data content
     *
     * @param configName    the config name
     * @param dataContent   the data bytes
     * @return the byte [ ]
     */
    public String encrypt(final String configName, final String dataContent) {
        if (StringUtils.isEmpty(dataContent) || StringUtils.isEmpty(configName) || !this.registeredConfig(configName)) {
            return dataContent;
        }
        byte[] dataBytes = ConvertUtils.convertToByteArray(dataContent);
        return Optional.ofNullable(this.registeredNodeMap.get(configName))
                .map(secureNode -> secureNode.initCryptor(Boolean.TRUE))
                .map(secureProvider -> {
                    try {
                        return StringUtils.base64Encode(secureProvider.finish(dataBytes));
                    } catch (CryptoException e) {
                        LOGGER.error("Encrypt data error! ");
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("Stack message: ", e);
                        }
                        return dataContent;
                    }
                })
                .orElse(dataContent);
    }

    /**
     * Decrypt byte [ ].
     *
     * @param configName    the config name
     * @param dataContent   the data bytes
     * @return the byte [ ]
     */
    public String decrypt(String configName, String dataContent) {
        if (StringUtils.isEmpty(dataContent) || StringUtils.isEmpty(configName) || !this.registeredConfig(configName)) {
            return dataContent;
        }
        byte[] encBytes = StringUtils.base64Decode(dataContent);
        if (encBytes.length == 0) {
            return dataContent;
        }
        return Optional.ofNullable(this.registeredNodeMap.get(configName))
                .map(secureNode -> secureNode.initCryptor(Boolean.FALSE))
                .map(secureProvider -> {
                    try {
                        return ConvertUtils.convertToString(secureProvider.finish(encBytes));
                    } catch (CryptoException e) {
                        LOGGER.error("Encrypt data error! ");
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("Stack message: ", e);
                        }
                        return dataContent;
                    }
                })
                .orElse(dataContent);
    }

    private byte[] encrypt(String configName, byte[] dataBytes) {
        if (StringUtils.notBlank(configName) && dataBytes.length > 0) {
            return Optional.ofNullable(this.registeredNodeMap.get(configName))
                    .map(secureNode -> secureNode.initCryptor(Boolean.TRUE))
                    .map(secureProvider -> {
                        try {
                            return secureProvider.finish(dataBytes);
                        } catch (CryptoException e) {
                            LOGGER.error("Encrypt data error! ");
                            if (LOGGER.isDebugEnabled()) {
                                LOGGER.debug("Stack message: ", e);
                            }
                            return dataBytes;
                        }
                    })
                    .orElse(dataBytes);
        }
        return dataBytes;
    }

    private byte[] decrypt(String configName, byte[] dataBytes) {
        if (StringUtils.notBlank(configName) && dataBytes.length > 0) {
            return Optional.ofNullable(this.registeredNodeMap.get(configName))
                    .map(secureNode -> secureNode.initCryptor(Boolean.FALSE))
                    .map(secureProvider -> {
                        try {
                            return secureProvider.finish(dataBytes);
                        } catch (CryptoException e) {
                            LOGGER.error("Encrypt data error! ");
                            if (LOGGER.isDebugEnabled()) {
                                LOGGER.debug("Stack message: ", e);
                            }
                            return dataBytes;
                        }
                    })
                    .orElse(dataBytes);
        }
        return dataBytes;
    }

    private byte[] initKey(byte[] dataBytes, boolean encrypt) {
        if (this.factoryNode == null) {
            return dataBytes;
        }
        SecureProvider secureProvider = this.factoryNode.initCryptor(encrypt);
        if (secureProvider == null) {
            LOGGER.error("Secure factory not initialized! ");
            return new byte[0];
        }
        try {
            return secureProvider.finish(dataBytes);
        } catch (Exception e) {
            return dataBytes;
        }
    }

    private static byte[] generate(SecureAlgorithm secureAlgorithm) {
        switch (secureAlgorithm) {
            case RSA1024:
                return convertKeyPair(SecurityUtils.RSAKeyPair(1024), "SHA256withRSA");
            case RSA2048:
                return convertKeyPair(SecurityUtils.RSAKeyPair(2048), "SHA256withRSA");
            case SM2:
                return convertKeyPair(SecurityUtils.SM2KeyPair(), "SM3withSM2");
            case AES128:
                return SecurityUtils.AES128Key();
            case AES192:
                return SecurityUtils.AES192Key();
            case AES256:
                return SecurityUtils.AES256Key();
            case DES:
                return SecurityUtils.DESKey();
            case TRIPLE_DES:
                return SecurityUtils.TripleDESKey();
            case SM4:
                return SecurityUtils.SM4Key();
            default:
                return new byte[0];
        }
    }

    private static byte[] convertKeyPair(KeyPair keyPair, String signAlgorithm) {
        long currentTime  = DateTimeUtils.currentTimeMillis();
        return CertificateUtils.PKCS12(keyPair, currentTime, new Date(currentTime),
                new Date(currentTime + 365 * 24 * 60 * 60 * 1000L), SECURE_CERTIFICATE_ALIAS,
                SECURE_CERTIFICATE_ALIAS, SECURE_CERTIFICATE_PASSWORD, null, signAlgorithm);
    }

    private static final class SecureNode {

        private final boolean initialized;
        private final SecureAlgorithm secureAlgorithm;
        private final byte[] keyBytes;
        private final PrivateKey privateKey;
        private final PublicKey publicKey;

        /**
         * Instantiates a new Secure node.
         *
         * @param secureAlgorithm the algorithm
         * @param dataBytes       the data bytes
         */
        private SecureNode(SecureAlgorithm secureAlgorithm, byte[] dataBytes) {
            this.secureAlgorithm = secureAlgorithm;
            switch (this.secureAlgorithm) {
                case RSA1024:
                case RSA2048:
                case SM2:
                    this.keyBytes = dataBytes;
                    KeyStore keyStore = CertificateUtils.loadKeyStore(dataBytes, SECURE_CERTIFICATE_PASSWORD);
                    if (keyStore == null) {
                        this.initialized = Boolean.FALSE;
                        this.privateKey = null;
                        this.publicKey = null;
                    } else {
                        this.publicKey =
                                Optional.ofNullable(CertificateUtils.x509(keyStore, SECURE_CERTIFICATE_ALIAS))
                                        .map(Certificate::getPublicKey)
                                        .orElse(null);
                        this.privateKey = CertificateUtils.privateKey(keyStore, SECURE_CERTIFICATE_ALIAS,
                                SECURE_CERTIFICATE_PASSWORD);
                        this.initialized = (this.publicKey != null && this.privateKey != null);
                    }
                    break;
                case AES128:
                case AES192:
                case AES256:
                case DES:
                case TRIPLE_DES:
                case SM4:
                    this.initialized = Boolean.TRUE;
                    this.keyBytes = dataBytes;
                    this.privateKey = null;
                    this.publicKey = null;
                    break;
                default:
                    this.initialized = Boolean.FALSE;
                    this.keyBytes = null;
                    this.privateKey = null;
                    this.publicKey = null;
                    break;
            }
        }

        /**
         * Initialize optional.
         *
         * @param secureConfig the secure config
         * @return the optional
         */
        public static Optional<SecureNode> initialize(SecureConfig secureConfig) {
            if (secureConfig == null) {
                return Optional.empty();
            }

            try {
                return Optional.of(new SecureNode(secureConfig.getSecureAlgorithm(),
                        INSTANCE.initKey(StringUtils.base64Decode(secureConfig.getSecureKey()), Boolean.FALSE)));
            } catch (IllegalArgumentException e) {
                return Optional.empty();
            }
        }

        private static Optional<SecureNode> initFactory(SecureConfig secureConfig) {
            if (secureConfig == null) {
                return Optional.empty();
            }

            try {
                return Optional.of(new SecureNode(secureConfig.getSecureAlgorithm(),
                        StringUtils.base64Decode(secureConfig.getSecureKey())));
            } catch (IllegalArgumentException e) {
                return Optional.empty();
            }
        }

        private SecureProvider initCryptor(boolean encrypt) {
            SecureProvider secureProvider = null;
            if (this.initialized) {
                try {
                    switch (this.secureAlgorithm) {
                        case RSA1024:
                        case RSA2048:
                            secureProvider = encrypt ? SecurityUtils.RSAEncryptor(this.publicKey)
                                    : SecurityUtils.RSADecryptor(this.privateKey);
                            break;
                        case SM2:
                            secureProvider = encrypt ? SecurityUtils.SM2Encryptor(this.publicKey)
                                    : SecurityUtils.SM2Decryptor(this.privateKey);
                            break;
                        case AES128:
                        case AES192:
                        case AES256:
                            secureProvider = encrypt ? SecurityUtils.AESEncryptor(this.keyBytes)
                                    : SecurityUtils.AESDecryptor(this.keyBytes);
                            break;
                        case DES:
                            secureProvider = encrypt ? SecurityUtils.DESEncryptor(this.keyBytes)
                                    : SecurityUtils.DESDecryptor(this.keyBytes);
                            break;
                        case TRIPLE_DES:
                            secureProvider = encrypt ? SecurityUtils.TripleDESEncryptor(this.keyBytes)
                                    : SecurityUtils.TripleDESDecryptor(this.keyBytes);
                            break;
                        case SM4:
                            secureProvider = encrypt ? SecurityUtils.SM4Encryptor(this.keyBytes)
                                    : SecurityUtils.SM4Decryptor(this.keyBytes);
                            break;
                        default:
                            break;
                    }
                } catch (CryptoException e) {
                    LOGGER.error("Initialize cryptor error! ");
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Stack message: ", e);
                    }
                }
            }
            return secureProvider;
        }

        /**
         * Is initialized boolean.
         *
         * @return the boolean
         */
        public boolean isInitialized() {
            return initialized;
        }
    }

    /**
     * The enum Secure algorithm.
     */
    public enum SecureAlgorithm {
        /**
         * Rsa 1024 secure algorithm.
         */
        RSA1024,
        /**
         * Rsa 2048 secure algorithm.
         */
        RSA2048,
        /**
         * Sm 2 secure algorithm.
         */
        SM2,
        /**
         * Aes 128 secure algorithm.
         */
        AES128,
        /**
         * Aes 192 secure algorithm.
         */
        AES192,
        /**
         * Aes 256 secure algorithm.
         */
        AES256,
        /**
         * Des secure algorithm.
         */
        DES,
        /**
         * Triple des secure algorithm.
         */
        TRIPLE_DES,
        /**
         * Sm 4 secure algorithm.
         */
        SM4
    }
}
