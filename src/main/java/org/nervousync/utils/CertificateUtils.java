package org.nervousync.utils;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.nervousync.commons.core.Globals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.Optional;

public final class CertificateUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(CertificateUtils.class);

    /**
     * Generate KeyPair using given algorithm/random algorithm/key size
     *
     * @param algorithm             Algorithm
     * @param randomAlgorithm       Random algorithm
     * @param keySize               Key size
     * @return                      Generated key pair
     */
    public static KeyPair KeyPair(String algorithm, String randomAlgorithm, int keySize) {
        if (keySize % 128 != 0) {
            LOGGER.error("Key size is invalid");
            return null;
        }

        if (StringUtils.isEmpty(randomAlgorithm)) {
            LOGGER.error("Random algorithm not configure, use default: SHA1PRNG");
            randomAlgorithm = "SHA1PRNG";
        }

        KeyPair keyPair = null;
        try {
            //	Initialize keyPair instance
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(algorithm, "BC");
            if (algorithm.equalsIgnoreCase("EC")) {
                ECGenParameterSpec ecGenParameterSpec = new ECGenParameterSpec("sm2p256v1");
                keyPairGenerator.initialize(ecGenParameterSpec, SecureRandom.getInstance(randomAlgorithm));
            } else {
                keyPairGenerator.initialize(keySize, SecureRandom.getInstance(randomAlgorithm));
            }
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | NoSuchProviderException e) {
            LOGGER.error("Initialize key pair generator error! ");
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Stack message: ", e);
            }
        }
        return keyPair;
    }

    /**
     * Convert public key instance to X.509 certificate
     *
     * @param publicKey         Public key
     * @param serialNumber      Certificate serial number
     * @param beginDate         Certificate begin date
     * @param endDate           Certificate end date
     * @param certName          Certificate name
     * @param signKey           Certificate signer private key
     * @param signAlgorithm     Signature algorithm
     * @return                  Generated X.509 certificate
     */
    public static X509Certificate x509(PublicKey publicKey, long serialNumber, Date beginDate, Date endDate,
                                       String certName, PrivateKey signKey, String signAlgorithm) {
        if (publicKey == null || signKey == null || StringUtils.isEmpty(signAlgorithm)) {
            return null;
        }
        X500Name subjectDN = new X500Name("CN=" + certName);
        SubjectPublicKeyInfo publicKeyInfo = SubjectPublicKeyInfo.getInstance(publicKey.getEncoded());
        X509v3CertificateBuilder x509v3CertificateBuilder =
                new X509v3CertificateBuilder(subjectDN, BigInteger.valueOf(serialNumber),
                        beginDate, endDate, subjectDN, publicKeyInfo);
        try {
            x509v3CertificateBuilder.addExtension(Extension.basicConstraints,
                    Boolean.FALSE, new BasicConstraints(Boolean.FALSE));
            ContentSigner contentSigner = new JcaContentSignerBuilder(signAlgorithm).setProvider("BC").build(signKey);
            X509CertificateHolder certificateHolder = x509v3CertificateBuilder.build(contentSigner);
            return new JcaX509CertificateConverter().getCertificate(certificateHolder);
        } catch (OperatorCreationException | GeneralSecurityException | IOException e) {
            LOGGER.error("Generate PKCS12 Certificate Failed! ");
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Stack message: ", e);
            }
        }
        return null;
    }

    /**
     * Read X.509 Certificate
     *
     * @param certBytes         Certificate data bytes
     * @param verifyKey         Verifier key
     * @param checkValidity     <code>true</code> for check certificate signature, <code>false</code> for not check
     * @return                  Read X.509 certificate or null for invalid
     */
    public static X509Certificate x509(byte[] certBytes, PublicKey verifyKey, boolean checkValidity) {
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
        return x509Certificate;
    }

    /**
     * Read X.509 certificate from keystore/PKCS12 data bytes
     *
     * @param storeBytes        Keystore/PKCS12 data bytes
     * @param certAlias         Certificate alias
     * @param password          Certificate password
     * @return                  Read X.509 certificate or null for invalid
     */
    public static X509Certificate x509(byte[] storeBytes, String certAlias, String password) {
        return x509(storeBytes, certAlias, password, null, Boolean.FALSE);
    }

    /**
     * Read X.509 certificate from file. File format: keystore/PKCS12
     *
     * @param storePath         Keystore/PKCS12 file path
     * @param certAlias         Certificate alias
     * @param password          Certificate password
     * @return                  Read X.509 certificate or null for invalid
     */
    public static X509Certificate x509(String storePath, String certAlias, String password) {
        try {
            return x509(FileUtils.readFileBytes(storePath), certAlias, password, null, Boolean.FALSE);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Read X.509 certificate from file. File format: keystore/PKCS12
     *
     * @param storeBytes        Keystore/PKCS12 data bytes
     * @param certAlias         Certificate alias
     * @param password          Certificate password
     * @param verifyKey         Verifier key
     * @param checkValidity     <code>true</code> for check certificate signature, <code>false</code> for not check
     * @return                  Read X.509 certificate or null for invalid
     */
    public static X509Certificate x509(byte[] storeBytes, String certAlias, String password,
                                                  PublicKey verifyKey, boolean checkValidity) {
        return Optional.ofNullable(loadKeyStore(storeBytes, password))
                .filter(keyStore -> checkKey(keyStore, certAlias))
                .map(keyStore -> {
                    X509Certificate x509Certificate;
                    try {
                        x509Certificate = (X509Certificate) keyStore.getCertificate(certAlias);
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
                    return x509Certificate;
                })
                .orElse(null);
    }

    /**
     * Generate PublicKey from key data bytes
     *
     * @param algorithm     Key algorithm
     * @param keyBytes      Key data bytes
     * @return              Generated publicKey
     */
    public static Optional<PublicKey> publicKey(String algorithm, byte[] keyBytes) {
        try {
            return Optional.of(KeyFactory.getInstance(algorithm).generatePublic(new X509EncodedKeySpec(keyBytes)));
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            LOGGER.error("Generate key from data bytes error! ");
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Stack message: ", e);
            }
            return Optional.empty();
        }
    }

    /**
     * Generate PrivateKey from key data bytes
     *
     * @param algorithm     Key algorithm
     * @param keyBytes      Key data bytes
     * @return              Generated privateKey
     */
    public static PrivateKey privateKey(String algorithm, byte[] keyBytes) {
        try {
            return KeyFactory.getInstance(algorithm).generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            LOGGER.error("Generate key from data bytes error! ");
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Stack message: ", e);
            }
            return null;
        }
    }

    /**
     * Read Private key from keystore/PKCS12 data bytes
     *
     * @param storeBytes        Keystore/PKCS12 data bytes
     * @param certAlias         Certificate alias
     * @param password          Certificate password
     * @return                  Read privateKey or null for invalid
     */
    public static PrivateKey privateKey(byte[] storeBytes, String certAlias, String password) {
        try {
            KeyStore keyStore = loadKeyStore(storeBytes, password);
            if (keyStore != null && CertificateUtils.checkKey(keyStore, password)) {
                return CertificateUtils.privateKey(keyStore, certAlias, password);
            }
        } catch (Exception e) {
            LOGGER.error("Read private key from KeyStore error! ");
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Stack message: ", e);
            }
        }
        return null;
    }

    /**
     * Read X.509 certificate from file. File format: keystore/PKCS12
     *
     * @param storePath         Keystore/PKCS12 file path
     * @param certAlias         Certificate alias
     * @param password          Certificate password
     * @return                  Read X.509 certificate or null for invalid
     * @throws FileNotFoundException        If storePath file not found
     */
    public static PrivateKey privateKey(String storePath, String certAlias, String password)
            throws FileNotFoundException {
        KeyStore keyStore = loadKeyStore(storePath, password);
        if (keyStore != null && CertificateUtils.checkKey(keyStore, password)) {
            return CertificateUtils.privateKey(keyStore, certAlias, password);
        }
        return null;
    }

    /**
     * Generate PKCS12
     *
     * @param keyPair           Key pair
     * @param serialNumber      Certificate serial number
     * @param beginDate         Certificate begin date
     * @param endDate           Certificate end date
     * @param certAlias         Certificate alias name
     * @param certName          Certificate name
     * @param passWord          Certificate password
     * @param signKey           Certificate signer private key
     * @param signAlgorithm     Signature algorithm
     * @return                  Generated PKCS12 data bytes
     */
    public static byte[] PKCS12(KeyPair keyPair, long serialNumber, Date beginDate, Date endDate, String certAlias,
                                String certName, String passWord, PrivateKey signKey, String signAlgorithm) {
        if (StringUtils.isEmpty(passWord)) {
            passWord = Globals.DEFAULT_VALUE_STRING;
        }
        X500Name subjectDN = new X500Name("CN=" + certName);
        SubjectPublicKeyInfo publicKeyInfo = SubjectPublicKeyInfo.getInstance(keyPair.getPublic().getEncoded());
        X509v3CertificateBuilder x509v3CertificateBuilder =
                new X509v3CertificateBuilder(subjectDN, BigInteger.valueOf(serialNumber),
                        beginDate, endDate, subjectDN, publicKeyInfo);
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            x509v3CertificateBuilder.addExtension(Extension.basicConstraints, Boolean.FALSE,
                    new BasicConstraints(Boolean.FALSE));
            ContentSigner contentSigner =
                    new JcaContentSignerBuilder(signAlgorithm).setProvider("BC")
                            .build(signKey == null ? keyPair.getPrivate() : signKey);
            X509CertificateHolder certificateHolder = x509v3CertificateBuilder.build(contentSigner);
            X509Certificate x509Certificate =
                    new JcaX509CertificateConverter().setProvider("BC").getCertificate(certificateHolder);

            KeyStore keyStore = KeyStore.getInstance("PKCS12", "BC");
            keyStore.load(null, null);
            keyStore.setKeyEntry(certAlias, keyPair.getPrivate(),
                    passWord.toCharArray(), new Certificate[]{x509Certificate});
            byteArrayOutputStream = new ByteArrayOutputStream();
            keyStore.store(byteArrayOutputStream, passWord.toCharArray());
            return byteArrayOutputStream.toByteArray();
        } catch (OperatorCreationException | GeneralSecurityException | IOException e) {
            LOGGER.error("Generate PKCS12 Certificate Failed! ");
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Stack message: ", e);
            }
            return new byte[0];
        } finally {
            IOUtils.closeStream(byteArrayOutputStream);
        }
    }

    /**
     * Read PKCS12 from data bytes
     *
     * @param storeBytes        Data bytes
     * @param password          Certificate password
     * @return                  Read PKCS12 instance
     */
    public static KeyStore loadKeyStore(byte[] storeBytes, String password) {
        return loadKeyStore(new ByteArrayInputStream(storeBytes), password == null ? null : password.toCharArray());
    }

    /**
     * Read PKCS12 from file
     *
     * @param storePath         File path
     * @param password          Certificate password
     * @return                  Read PKCS12 instance
     * @throws FileNotFoundException    If storePath file not found
     */
    public static KeyStore loadKeyStore(String storePath, String password) throws FileNotFoundException {
        return loadKeyStore(new FileInputStream(storePath), password == null ? null : password.toCharArray());
    }

    /**
     * Read PKCS12 from stream
     *
     * @param inputStream       Input stream
     * @param certPassword      Password char array
     * @return                  Read PKCS12 instance
     */
    public static KeyStore loadKeyStore(InputStream inputStream, char[] certPassword) {
        KeyStore keyStore;
        try {
            keyStore = KeyStore.getInstance("PKCS12", "BC");
            keyStore.load(inputStream, certPassword);
        } catch (Exception e) {
            e.printStackTrace();
            keyStore = null;
        } finally {
            IOUtils.closeStream(inputStream);
        }
        return keyStore;
    }

    /**
     * Check certificate alias was exists
     *
     * @param keyStore      PKCS12
     * @param certAlias     Certificate alias
     * @return              Check result
     */
    public static boolean checkKey(KeyStore keyStore, String certAlias) {
        if (keyStore == null || certAlias == null) {
            return Boolean.FALSE;
        }
        try {
            return keyStore.isKeyEntry(certAlias);
        } catch (KeyStoreException e) {
            return Boolean.FALSE;
        }
    }

    /**
     * Read X.509 certificate from PKCS12 instance
     * @param keyStore          PKCS12 instance
     * @param certAlias         Certificate alias name
     * @return                  X.509 certificate
     */
    public static X509Certificate x509(KeyStore keyStore, String certAlias) {
        try {
            return (X509Certificate) keyStore.getCertificate(certAlias);
        } catch (KeyStoreException e) {
            LOGGER.error("Read certificate error!");
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Stack message: ", e);
            }
            return null;
        }
    }

    /**
     * Read private key from PKCS12 instance
     * @param keyStore          PKCS12 instance
     * @param certAlias         Certificate alias name
     * @param password          Certificate password
     * @return                  Private key
     */
    public static PrivateKey privateKey(KeyStore keyStore, String certAlias, String password) {
        try {
            return (PrivateKey) keyStore.getKey(certAlias, password == null ? null : password.toCharArray());
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            LOGGER.error("Read private key from key store error! ");
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Stack message: ", e);
            }
            return null;
        }
    }
}
