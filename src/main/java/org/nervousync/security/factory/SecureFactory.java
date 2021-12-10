package org.nervousync.security.factory;

import org.nervousync.commons.core.Globals;
import org.nervousync.exceptions.crypto.CryptoException;
import org.nervousync.security.SecureProvider;
import org.nervousync.security.factory.config.FactoryConfig;
import org.nervousync.security.factory.config.SecureConfig;
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
    private static final String SETTING_FILE_NAME = "settings.xml";
    private static final String CONFIG_FILE_NAME = "secure.xml";

    private static String FOLDER_PATH = Globals.DEFAULT_VALUE_STRING;
    private static SecureAlgorithm FACTORY_ALGORITHM;
    private static SecureNode FACTORY_NODE = null;
    private static final Map<String, SecureNode> REGISTERED_NODE_MAP = new HashMap<>();

    private SecureFactory() {
    }

    /**
     * Initialized boolean.
     *
     * @return the boolean
     */
    public static boolean initialized() {
        return FACTORY_NODE != null && FACTORY_NODE.isInitialized();
    }

    /**
     * Initialize boolean.
     *
     * @param folderPath the folder path
     * @return the boolean
     */
    public static boolean initialize(String folderPath) {
        if (StringUtils.isEmpty(folderPath)) {
            return Boolean.FALSE;
        }

        FOLDER_PATH = folderPath;
        String settingPath = FOLDER_PATH + Globals.DEFAULT_PAGE_SEPARATOR + SETTING_FILE_NAME;
        return FileUtils.isExists(settingPath)
                ? initialize(BeanUtils.parseXml(FileUtils.readFile(settingPath), FactoryConfig.class))
                : initialize(settingPath, SecureAlgorithm.AES256);
    }

    /**
     * Initialize boolean.
     *
     * @param folderPath      the folder path
     * @param secureAlgorithm the secure algorithm
     * @return the boolean
     */
    public static boolean initialize(String folderPath, SecureAlgorithm secureAlgorithm) {
        if (StringUtils.isEmpty(folderPath)) {
            return Boolean.FALSE;
        }

        FOLDER_PATH = folderPath;
        String settingPath = FOLDER_PATH + Globals.DEFAULT_PAGE_SEPARATOR + SETTING_FILE_NAME;
        if (FileUtils.isExists(settingPath)) {
            return initialize(BeanUtils.parseXml(FileUtils.readFile(settingPath), FactoryConfig.class));
        }

        FactoryConfig factoryConfig = new FactoryConfig();
        factoryConfig.setLastModify(DateTimeUtils.currentTimeMillis());
        factoryConfig.setFactoryAlgorithm(secureAlgorithm.toString());
        factoryConfig.setFactoryKey(StringUtils.base64Encode(generate(secureAlgorithm)));
        if (FileUtils.saveFile(settingPath, factoryConfig.toXML())) {
            return initialize(factoryConfig);
        }
        return Boolean.FALSE;
    }

    /**
     * Config boolean.
     *
     * @param configName      the config name
     * @param secureAlgorithm the secure algorithm
     * @return the boolean
     */
    public static boolean config(String configName, SecureAlgorithm secureAlgorithm) {
        return config(configName, secureAlgorithm, generate(secureAlgorithm));
    }

    /**
     * Config boolean.
     *
     * @param configName      the config name
     * @param secureAlgorithm the secure algorithm
     * @param keyBytes        the key bytes
     * @return the boolean
     */
    public static boolean config(String configName, SecureAlgorithm secureAlgorithm, byte[] keyBytes) {
        if (StringUtils.isEmpty(configName) || keyBytes.length == 0) {
            return Boolean.FALSE;
        }
        register(secureConfig(configName, secureAlgorithm, keyBytes));
        return saveConfig();
    }

    /**
     * Encrypt byte [ ].
     *
     * @param configName the config name
     * @param dataBytes  the data bytes
     * @return the byte [ ]
     */
    public static byte[] encrypt(String configName, byte[] dataBytes) {
        if (StringUtils.notBlank(configName) && dataBytes.length > 0) {
            if (REGISTERED_NODE_MAP.containsKey(configName)) {
                try {
                    return REGISTERED_NODE_MAP.get(configName).initCryptor(Boolean.TRUE).finish(dataBytes);
                } catch (CryptoException e) {
                    LOGGER.error("Encrypt data error! ");
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Stack message: ", e);
                    }
                }
            }
        }
        return dataBytes;
    }

    /**
     * Decrypt byte [ ].
     *
     * @param configName the config name
     * @param dataBytes  the data bytes
     * @return the byte [ ]
     */
    public static byte[] decrypt(String configName, byte[] dataBytes) {
        if (StringUtils.notBlank(configName) && dataBytes.length > 0) {
            if (REGISTERED_NODE_MAP.containsKey(configName)) {
                try {
                    return REGISTERED_NODE_MAP.get(configName).initCryptor(Boolean.FALSE).finish(dataBytes);
                } catch (CryptoException e) {
                    LOGGER.error("Decrypt data error! ");
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Stack message: ", e);
                    }
                }
            }
        }
        return dataBytes;
    }

    private static boolean initialize(FactoryConfig factoryConfig) {
        if (factoryConfig == null) {
            return Boolean.FALSE;
        }
        FACTORY_NODE = new SecureNode(SecureAlgorithm.valueOf(factoryConfig.getFactoryAlgorithm()),
                StringUtils.base64Decode(factoryConfig.getFactoryKey()));
        if (FACTORY_NODE.isInitialized()) {
            SecureProvider secureProvider = FACTORY_NODE.initCryptor(Boolean.FALSE);
            if (secureProvider == null) {
                return Boolean.FALSE;
            }

            String configPath = FOLDER_PATH + Globals.DEFAULT_PAGE_SEPARATOR + CONFIG_FILE_NAME;
            if (FileUtils.isExists(configPath)) {
                SecureConfig secureConfig = BeanUtils.parseFile(configPath, SecureConfig.class);
                if (secureConfig == null) {
                    LOGGER.error("Read config error! ");
                    return Boolean.FALSE;
                }
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Last modify: {}", new Date(secureConfig.getLastModify()));
                }
                secureConfig.getConfigItemList().forEach(SecureFactory::register);
            } else {
                SecureConfig secureConfig = new SecureConfig();
                secureConfig.setLastModify(DateTimeUtils.currentTimeMillis());
                return FileUtils.saveFile(configPath, secureConfig.toXML());
            }
        }
        return Boolean.TRUE;
    }

    private static void register(SecureConfig.ConfigItem configItem) {
        SecureProvider secureProvider = FACTORY_NODE.initCryptor(Boolean.FALSE);
        if (secureProvider == null) {
            LOGGER.error("Secure factory not initialized! ");
            return;
        }
        byte[] keyBytes, encBytes = StringUtils.base64Decode(configItem.getSecureKey());
        try {
            keyBytes = secureProvider.finish(encBytes);
        } catch (CryptoException e) {
            keyBytes = encBytes;
        }
        if (keyBytes.length == 0) {
            keyBytes = encBytes;
        }
        SecureNode secureNode = new SecureNode(SecureAlgorithm.valueOf(configItem.getSecureAlgorithm()), keyBytes);
        if (secureNode.isInitialized()) {
            REGISTERED_NODE_MAP.put(configItem.getConfigName(), secureNode);
        } else {
            LOGGER.error("Initialize secure node {} error! ", configItem.getConfigName());
        }
    }

    private static boolean saveConfig() {
        if (StringUtils.isEmpty(FOLDER_PATH)) {
            return Boolean.FALSE;
        }

        long lastModify = DateTimeUtils.currentTimeMillis();
        FactoryConfig factoryConfig = new FactoryConfig();
        factoryConfig.setLastModify(lastModify);
        factoryConfig.setFactoryAlgorithm(FACTORY_NODE.getSecureAlgorithm().toString());
        factoryConfig.setFactoryKey(StringUtils.base64Encode(FACTORY_NODE.getKeyBytes()));
        String settingPath = FOLDER_PATH + Globals.DEFAULT_PAGE_SEPARATOR + SETTING_FILE_NAME;
        if (!FileUtils.saveFile(settingPath, factoryConfig.toXML())) {
            LOGGER.error("Save setting file error! Save path: {}", settingPath);
            return Boolean.FALSE;
        }

        List<SecureConfig.ConfigItem> configItemList = new ArrayList<>();
        REGISTERED_NODE_MAP.forEach((configName, secureNode) -> {
            SecureConfig.ConfigItem configItem =
                    secureConfig(configName, secureNode.getSecureAlgorithm(), secureNode.getKeyBytes());
            if (configItem != null) {
                configItemList.add(configItem);
            }
        });

        SecureConfig secureConfig = new SecureConfig();
        secureConfig.setLastModify(lastModify);
        secureConfig.setConfigItemList(configItemList);
        return FileUtils.saveFile(FOLDER_PATH + Globals.DEFAULT_PAGE_SEPARATOR + CONFIG_FILE_NAME,
                secureConfig.toXML());
    }

    private static SecureConfig.ConfigItem secureConfig(String configName,
                                                        SecureAlgorithm secureAlgorithm, byte[] keyBytes) {
        SecureProvider secureProvider = FACTORY_NODE.initCryptor(Boolean.TRUE);
        if (secureProvider == null) {
            LOGGER.error("Secure factory not initialized! ");
            return null;
        }

        byte[] encBytes;
        try {
            encBytes = secureProvider.finish(keyBytes);
        } catch (CryptoException e) {
            encBytes = keyBytes;
        }
        SecureConfig.ConfigItem configItem = new SecureConfig.ConfigItem();
        configItem.setConfigName(configName);
        configItem.setSecureAlgorithm(secureAlgorithm.toString());
        configItem.setSecureKey(StringUtils.base64Encode(encBytes));
        return configItem;
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
        public SecureNode(SecureAlgorithm secureAlgorithm, byte[] dataBytes) {
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

        /**
         * Gets secure algorithm.
         *
         * @return the secure algorithm
         */
        public SecureAlgorithm getSecureAlgorithm() {
            return secureAlgorithm;
        }

        /**
         * Get key bytes byte [ ].
         *
         * @return the byte [ ]
         */
        public byte[] getKeyBytes() {
            return keyBytes;
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
