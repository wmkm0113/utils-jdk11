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
import org.nervousync.security.api.SecureAdapter;
import org.nervousync.utils.*;

import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.*;

/**
 * <h2 class="en">Secure factory instance</h2>
 * <p class="en">
 *     Running in singleton mode. Using for protect password in any configure files.
 *     Supported algorithm: RSA1024/RSA2048/SM2/AES128/AES192/AES256/DES/3DES/SM4
 * </p>
 * <h2 class="zh-CN">安全配置信息定义</h2>
 * <p class="zh-CN">使用单例模式运行。用于在任何配置文件中保护密码。支持的算法：RSA1024/RSA2048/SM2/AES128/AES192/AES256/DES/3DES/SM4</p>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: Jan 13, 2012 12:33:56 $
 */
public final class SecureFactory {
	/**
	 * <span class="en">Logger instance</span>
	 * <span class="zh-CN">日志对象</span>
	 */
    private static final LoggerUtils.Logger LOGGER = LoggerUtils.getLogger(SecureFactory.class);
	/**
	 * <span class="en">Default certificate alias</span>
	 * <span class="zh-CN">默认证书别名</span>
	 */
    private static final String SECURE_CERTIFICATE_ALIAS = "NSYC";
	/**
	 * <span class="en">Default certificate password</span>
	 * <span class="zh-CN">默认证书库密码</span>
	 */
    private static final String SECURE_CERTIFICATE_PASSWORD = "ns0528AO";
	/**
	 * <span class="en">Root secure node</span>
	 * <span class="zh-CN">根安全节点</span>
	 */
    private static SecureNode FACTORY_NODE = null;
	/**
	 * <span class="en">Registered secure node mapping</span>
	 * <span class="zh-CN">已注册的安全节点映射</span>
	 */
    private static final Map<String, SecureNode> REGISTERED_NODE_MAP = new HashMap<>();
    /**
	 * <h3 class="en">Private constructor method for SecureFactory</h3>
	 * <h3 class="zh-CN">安全工厂的私有构造方法</h3>
     */
    private SecureFactory() {
    }
    /**
	 * <h3 class="en">Check root secure node was configured</h3>
	 * <h3 class="zh-CN">检查根安全节点是否配置成功</h3>
     *
     * @return  <span class="en">Check result</span>
     *          <span class="zh-CN">检查结果</span>
     */
    public static boolean initialized() {
        return FACTORY_NODE != null && FACTORY_NODE.isInitialized();
    }
    /**
	 * <h3 class="en">Configure root secure node using given secure config</h3>
	 * <h3 class="zh-CN">使用给定的安全配置信息设置安全工厂的根安全节点</h3>
     *
     * @param secureConfig  <span class="en">Secure config information</span>
     *                      <span class="zh-CN">安全配置信息</span>
     *
     * @return  <span class="en">Initialize result</span>
     *          <span class="zh-CN">初始化结果</span>
     */
    public static boolean initialize(final SecureConfig secureConfig) {
        return SecureNode.initFactory(secureConfig)
                .filter(SecureNode::isInitialized)
                .map(secureNode -> {
                    if (FACTORY_NODE != null && LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Utils", "Override_Factory_Config_Debug");
                    }
                    FACTORY_NODE = secureNode;
                    return Boolean.TRUE;
                })
                .orElse(Boolean.FALSE);
    }
    /**
	 * <h3 class="en">Check given secure algorithm was supported</h3>
	 * <h3 class="zh-CN">检查给定的安全算法支持状态</h3>
     *
     * @param secureAlgorithm   <span class="en">Secure algorithm</span>
	 *                          <span class="zh-CN">安全算法</span>
     *
     * @return  <span class="en">Check result</span>
     *          <span class="zh-CN">检查结果</span>
     */
    public static boolean supportedAlgorithm(final String secureAlgorithm) {
        try {
            SecureAlgorithm.valueOf(secureAlgorithm);
            return Boolean.TRUE;
        } catch (IllegalArgumentException e) {
            return Boolean.FALSE;
        }
    }
    /**
	 * <h3 class="en">Generate secure configure information using given secure algorithm</h3>
	 * <h3 class="zh-CN">使用给定的安全算法生成安全配置信息实例对象</h3>
     *
     * @param secureAlgorithm   <span class="en">Secure algorithm</span>
	 *                          <span class="zh-CN">安全算法</span>
     *
     * @return  <span class="en">Optional of SecureConfig instance</span>
     *          <span class="zh-CN">Optional包装的安全配置信息实例对象</span>
     */
    public static Optional<SecureConfig> initConfig(final SecureAlgorithm secureAlgorithm) {
        final byte[] keyBytes = generate(secureAlgorithm);
        if (keyBytes.length == 0) {
            LOGGER.error("Utils", "Key_Bytes_Empty_Error");
            return Optional.empty();
        }
        byte[] encBytes = initKey(keyBytes, Boolean.TRUE);

        SecureConfig secureConfig = new SecureConfig();
        secureConfig.setSecureAlgorithm(secureAlgorithm);
        secureConfig.setSecureKey(StringUtils.base64Encode(encBytes));
        return Optional.of(secureConfig);
    }
    /**
	 * <h3 class="en">Check given secure name was registered</h3>
	 * <h3 class="zh-CN">检查给定的安全名称注册状态</h3>
     *
     * @param secureName    <span class="en">Secure name</span>
     *                      <span class="zh-CN">安全名称</span>
     * @return the boolean
     */
    public static boolean registeredConfig(final String secureName) {
        if (StringUtils.isEmpty(secureName) || FACTORY_NODE == null || !FACTORY_NODE.isInitialized()) {
            return Boolean.FALSE;
        }
        return REGISTERED_NODE_MAP.containsKey(secureName);
    }

    /**
	 * <h3 class="en">Register secure config by given secure name and configure information instance</h3>
	 * <h3 class="zh-CN">将给定的安全名称和安全配置信息实例注册到安全工厂</h3>
     *
     * @param secureName    <span class="en">Secure name</span>
     *                      <span class="zh-CN">安全名称</span>
     * @param secureConfig  <span class="en">Secure config information</span>
     *                      <span class="zh-CN">安全配置信息</span>
     *
     * @return  <span class="en">Register result</span>
     *          <span class="zh-CN">注册结果</span>
     */
    public static boolean register(final String secureName, final SecureConfig secureConfig) {
        if (StringUtils.isEmpty(secureName) || FACTORY_NODE == null || !FACTORY_NODE.isInitialized()) {
            return Boolean.FALSE;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Current Config: {}", secureConfig.toXML(Boolean.TRUE));
        }
        return SecureNode.initialize(secureConfig).filter(SecureNode::isInitialized)
                .map(secureNode -> {
                    if (REGISTERED_NODE_MAP.containsKey(secureName)) {
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("Override secure config: {}", secureName);
                        }
                    }
                    REGISTERED_NODE_MAP.put(secureName, secureNode);
                    return Boolean.TRUE;
                })
                .orElse(Boolean.FALSE);
    }
    /**
	 * <h3 class="en">Deregister secure config by given secure name</h3>
	 * <h3 class="zh-CN">将给定的安全名称取消注册</h3>
     *
     * @param secureName    <span class="en">Secure name</span>
     *                      <span class="zh-CN">安全名称</span>
     */
    public static void deregister(String secureName) {
        if (StringUtils.notBlank(secureName)) {
            REGISTERED_NODE_MAP.remove(secureName);
        }
    }
    /**
	 * <h3 class="en">Update secure config protected password data</h3>
	 * <h3 class="zh-CN">更新安全配置保护的密码信息</h3>
     *
     * @param dataContent   <span class="en">Password data</span>
     *                      <span class="zh-CN">密码信息</span>
     * @param originalName  <span class="en">Original secure name</span>
     *                      <span class="zh-CN">旧安全配置名称</span>
     * @param secureName    <span class="en">New secure name</span>
     *                      <span class="zh-CN">新安全配置名称</span>
     *
     * @return  <span class="en">Updated password data</span>
     *          <span class="zh-CN">更新后的密码信息</span>
     */
    public static String update(final String dataContent, final String originalName, final String secureName) {
        if (StringUtils.isEmpty(dataContent)) {
            return dataContent;
        }

        String string = decrypt(originalName, dataContent);
        if (StringUtils.isEmpty(secureName)) {
            return string;
        } else {
            return encrypt(secureName, string);
        }
    }
    /**
	 * <h3 class="en">Encrypt data content using given secure name</h3>
	 * <h3 class="zh-CN">使用给定的安全名称加密密码信息</h3>
     *
     * @param secureName    <span class="en">New secure name</span>
     *                      <span class="zh-CN">新安全配置名称</span>
     * @param dataContent   <span class="en">Password data</span>
     *                      <span class="zh-CN">密码信息</span>
     *
     * @return  <span class="en">Encrypted password data</span>
     *          <span class="zh-CN">加密后的密码信息</span>
     */
    public static String encrypt(final String secureName, final String dataContent) {
        if (StringUtils.isEmpty(dataContent) || StringUtils.isEmpty(secureName) || !registeredConfig(secureName)) {
            return dataContent;
        }

        return Optional.ofNullable(REGISTERED_NODE_MAP.get(secureName))
                .map(secureNode -> secureNode.initCryptor(Boolean.TRUE))
                .map(secureProvider -> {
                    try {
                        return StringUtils.base64Encode(secureProvider.finish(ConvertUtils.toByteArray(dataContent)));
                    } catch (CryptoException e) {
                        LOGGER.error("Utils", "Encrypt_Data_Error");
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("Utils", "Stack_Message_Error", e);
                        }
                        return dataContent;
                    }
                })
                .orElse(dataContent);
    }
    /**
	 * <h3 class="en">Decrypt data content using given secure name</h3>
	 * <h3 class="zh-CN">使用给定的安全名称解密密码信息</h3>
     *
     * @param secureName    <span class="en">New secure name</span>
     *                      <span class="zh-CN">新安全配置名称</span>
     * @param dataContent   <span class="en">Password data</span>
     *                      <span class="zh-CN">密码信息</span>
     *
     * @return  <span class="en">Decrypted password data</span>
     *          <span class="zh-CN">解密后的密码信息</span>
     */
    public static String decrypt(final String secureName, final String dataContent) {
        if (StringUtils.isEmpty(dataContent) || StringUtils.isEmpty(secureName) || !registeredConfig(secureName)) {
            return dataContent;
        }
        byte[] encBytes = StringUtils.base64Decode(dataContent);
        if (encBytes.length == 0) {
            return dataContent;
        }
        return Optional.ofNullable(REGISTERED_NODE_MAP.get(secureName))
                .map(secureNode -> secureNode.initCryptor(Boolean.FALSE))
                .map(secureProvider -> {
                    try {
                        return ConvertUtils.toString(secureProvider.finish(encBytes));
                    } catch (CryptoException e) {
                        LOGGER.error("Utils", "Encrypt_Data_Error");
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("Utils", "Stack_Message_Error", e);
                        }
                        return dataContent;
                    }
                })
                .orElse(dataContent);
    }
    /**
	 * <h3 class="en">Initialize key bytes</h3>
	 * <h3 class="zh-CN">初始化加密密钥数据</h3>
     *
     * @param dataBytes     <span class="en">key bytes</span>
     *                      <span class="zh-CN">加密密钥数据</span>
     * @param encrypt       <span class="en">Encrypt status</span>
     *                      <span class="zh-CN">加密密钥数据</span>
     *
     * @return  <span class="en">Initialized data bytes</span>
     *          <span class="zh-CN">初始化的数据</span>
     */
    private static byte[] initKey(final byte[] dataBytes, final boolean encrypt) {
        if (FACTORY_NODE == null) {
            return dataBytes;
        }
        SecureAdapter secureAdapter = FACTORY_NODE.initCryptor(encrypt);
        if (secureAdapter == null) {
            LOGGER.error("Utils", "Security_Factory_Not_Initialized_Error");
            return new byte[0];
        }
        try {
            return secureAdapter.finish(dataBytes);
        } catch (Exception e) {
            return dataBytes;
        }
    }
    /**
	 * <h3 class="en">Generate secure key by given secure algorithm</h3>
	 * <h3 class="zh-CN">使用给定的安全算法生成安全密钥</h3>
     *
     * @param secureAlgorithm   <span class="en">Secure algorithm</span>
	 *                          <span class="zh-CN">安全算法</span>
     *
     * @return  <span class="en">Generated key data bytes</span>
     *          <span class="zh-CN">生成的安全密钥数据</span>
     */
    private static byte[] generate(final SecureAlgorithm secureAlgorithm) {
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
    /**
	 * <h3 class="en">Convert asymmetric key pair instance to secure key data bytes, using given signature algorithm</h3>
	 * <h3 class="zh-CN">使用给定的签名算法将非对称密钥对实例对象转换为安全密钥数据</h3>
     *
     * @param keyPair           <span class="en">Asymmetric key pair instance</span>
	 *                          <span class="zh-CN">非对称密钥对实例对象</span>
     * @param signAlgorithm     <span class="en">Signature algorithm</span>
	 *                          <span class="zh-CN">签名算法</span>
     *
     * @return  <span class="en">Generated key data bytes</span>
     *          <span class="zh-CN">生成的安全密钥数据</span>
     */
    private static byte[] convertKeyPair(final KeyPair keyPair, final String signAlgorithm) {
        long currentTime  = DateTimeUtils.currentTimeMillis();
        return CertificateUtils.PKCS12(keyPair, currentTime, new Date(currentTime),
                new Date(currentTime + 365 * 24 * 60 * 60 * 1000L), SECURE_CERTIFICATE_ALIAS,
                SECURE_CERTIFICATE_ALIAS, SECURE_CERTIFICATE_PASSWORD, null, signAlgorithm);
    }
    /**
     * <h2 class="en">Secure Node</h2>
     * <h2 class="zh-CN">安全配置信息定义</h2>
     *
     * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
     * @version $Revision : 1.0 $ $Date: Jan 13, 2012 12:38:45 $
     */
    private static final class SecureNode {
        /**
         * <span class="en">Node initialize status</span>
         * <span class="zh-CN">节点初始化状态</span>
         */
        private final boolean initialized;
        /**
         * <span class="en">Secure algorithm</span>
         * <span class="zh-CN">安全算法</span>
         */
        private final SecureAlgorithm secureAlgorithm;
        /**
         * <span class="en">Secure key data bytes</span>
         * <span class="zh-CN">安全密钥数据</span>
         */
        private final byte[] keyBytes;
        /**
         * <span class="en">Asymmetric private key</span>
         * <span class="zh-CN">非对称加密私钥</span>
         */
        private final PrivateKey privateKey;
        /**
         * <span class="en">Asymmetric public key</span>
         * <span class="zh-CN">非对称加密公钥</span>
         */
        private final PublicKey publicKey;
        /**
         * <h3 class="en">Constructor for SecureNode</h3>
         * <h3 class="zh-CN">安全节点构造方法</h3>
         *
         * @param secureAlgorithm   <span class="en">Secure algorithm</span>
         *                          <span class="zh-CN">安全算法</span>
         * @param dataBytes         <span class="en">Secure key data bytes</span>
         *                          <span class="zh-CN">安全密钥数据</span>
         */
        private SecureNode(final SecureAlgorithm secureAlgorithm, final byte[] dataBytes) {
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
         * <h3 class="en">Static method for initialize secure node by given secure config</h3>
         * <h3 class="zh-CN">静态方法用于使用给定的安全配置信息初始化安全节点实例</h3>
         *
         * @param secureConfig  <span class="en">Secure config information</span>
         *                      <span class="zh-CN">安全配置信息</span>
         *
         * @return  <span class="en">Optional of SecureNode instance</span>
         *          <span class="zh-CN">Optional包装的安全节点实例对象</span>
         */
        public static Optional<SecureNode> initialize(final SecureConfig secureConfig) {
            if (secureConfig == null) {
                return Optional.empty();
            }

            try {
                return Optional.of(new SecureNode(secureConfig.getSecureAlgorithm(),
                        initKey(StringUtils.base64Decode(secureConfig.getSecureKey()), Boolean.FALSE)));
            } catch (IllegalArgumentException e) {
                return Optional.empty();
            }
        }
        /**
         * <h3 class="en">Static method for initialize factory secure node by given secure config</h3>
         * <h3 class="zh-CN">静态方法用于使用给定的安全配置信息初始化根安全节点实例</h3>
         *
         * @param secureConfig  <span class="en">Secure config information</span>
         *                      <span class="zh-CN">安全配置信息</span>
         *
         * @return  <span class="en">Optional of SecureNode instance</span>
         *          <span class="zh-CN">Optional包装的安全节点实例对象</span>
         */
        private static Optional<SecureNode> initFactory(final SecureConfig secureConfig) {
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
        /**
         * <h3 class="en">Initialize secure adapter</h3>
         * <h3 class="zh-CN">初始化加密解密适配器</h3>
         *
         * @param encrypt   <span class="en">Encrypt status</span>
         *                  <span class="zh-CN">加密状态</span>
         *
         * @return  <span class="en">Initialized adapter instance</span>
         *          <span class="zh-CN">初始化的适配器实例对象</span>
         */
        private SecureAdapter initCryptor(boolean encrypt) {
            SecureAdapter secureAdapter = null;
            if (this.initialized) {
                try {
                    switch (this.secureAlgorithm) {
                        case RSA1024:
                        case RSA2048:
                            secureAdapter = encrypt ? SecurityUtils.RSAEncryptor(this.publicKey)
                                    : SecurityUtils.RSADecryptor(this.privateKey);
                            break;
                        case SM2:
                            secureAdapter = encrypt ? SecurityUtils.SM2Encryptor(this.publicKey)
                                    : SecurityUtils.SM2Decryptor(this.privateKey);
                            break;
                        case AES128:
                        case AES192:
                        case AES256:
                            secureAdapter = encrypt ? SecurityUtils.AESEncryptor(this.keyBytes)
                                    : SecurityUtils.AESDecryptor(this.keyBytes);
                            break;
                        case DES:
                            secureAdapter = encrypt ? SecurityUtils.DESEncryptor(this.keyBytes)
                                    : SecurityUtils.DESDecryptor(this.keyBytes);
                            break;
                        case TRIPLE_DES:
                            secureAdapter = encrypt ? SecurityUtils.TripleDESEncryptor(this.keyBytes)
                                    : SecurityUtils.TripleDESDecryptor(this.keyBytes);
                            break;
                        case SM4:
                            secureAdapter = encrypt ? SecurityUtils.SM4Encryptor(this.keyBytes)
                                    : SecurityUtils.SM4Decryptor(this.keyBytes);
                            break;
                        default:
                            break;
                    }
                } catch (CryptoException e) {
                    LOGGER.error("Utils", "Init_Crypto_Error");
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Utils", "Stack_Message_Error", e);
                    }
                }
            }
            return secureAdapter;
        }
        /**
         * <h3 class="en">Getter method for Node initialize status</h3>
         * <h3 class="zh-CN">节点初始化状态的Getter方法</h3>
         *
         * @return  <span class="en">Node initialize status</span>
         *          <span class="zh-CN">节点初始化状态</span>
         */
        public boolean isInitialized() {
            return initialized;
        }
    }
    /**
     * <h2 class="en">Enumeration of Secure Algorithm</h2>
     * <h2 class="zh-CN">安全算法的枚举类</h2>
     *
     * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
     * @version $Revision : 1.0 $ $Date: Jan 13, 2012 12:37:28 $
     */
    public enum SecureAlgorithm {
        RSA1024, RSA2048, SM2, AES128, AES192, AES256, DES, TRIPLE_DES, SM4
    }
}
