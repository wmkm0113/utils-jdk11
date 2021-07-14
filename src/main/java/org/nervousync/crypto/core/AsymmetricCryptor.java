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

package org.nervousync.crypto.core;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.nervousync.commons.core.Globals;
import org.nervousync.crypto.Cryptor;
import org.nervousync.utils.FileUtils;
import org.nervousync.utils.IOUtils;
import org.nervousync.utils.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.Optional;

/**
 * The type Asymmetric cryptor.
 */
public abstract class AsymmetricCryptor extends Cryptor {

    /**
     * The Public key.
     */
    protected final PublicKey publicKey;
    /**
     * The Private key.
     */
    protected final PrivateKey privateKey;

    private final String signAlgorithm;

    /**
     * Instantiates a new Asymmetric cryptor.
     *
     * @param cipherMode    the cipher mode
     * @param encodeType    the encode type
     * @param signAlgorithm the sign algorithm
     * @param publicKey     the public key
     * @param privateKey    the private key
     */
    protected AsymmetricCryptor(CipherMode cipherMode, EncodeType encodeType,
                                String signAlgorithm, PublicKey publicKey, PrivateKey privateKey) {
        super(cipherMode, encodeType);
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        this.signAlgorithm = signAlgorithm;
    }

    /**
     * Encrypt string.
     *
     * @param strIn the str in
     * @return the string
     */
    public final String encrypt(String strIn) {
        return super.encode(this.encrypt(strIn.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * Decrypt string.
     *
     * @param strIn the str in
     * @return the string
     */
    public final String decrypt(String strIn) {
        return new String(this.decrypt(super.decode(strIn)), StandardCharsets.UTF_8);
    }

    /**
     * Sign data byte [ ].
     *
     * @param dataBytes the data bytes
     * @return the byte [ ]
     */
    public final byte[] signData(byte[] dataBytes) {
        return this.signData(this.signAlgorithm, dataBytes);
    }

    /**
     * Sign data byte [ ].
     *
     * @param signAlgorithm the sign algorithm
     * @param dataBytes     the data bytes
     * @return the byte [ ]
     */
    public final byte[] signData(String signAlgorithm, byte[] dataBytes) {
        try {
            Signature signature = Signature.getInstance(signAlgorithm);
            signature.initSign(this.privateKey);
            signature.update(dataBytes);

            return signature.sign();
        } catch (Exception e) {
            LOGGER.error("Signature data failed! ");
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Stack message: ", e);
            }
            return new byte[0];
        }
    }

    /**
     * Sign file byte [ ].
     *
     * @param filePath the file path
     * @return the byte [ ]
     */
    public final byte[] signFile(String filePath) {
        return this.signFile(this.signAlgorithm, filePath);
    }

    /**
     * Sign file byte [ ].
     *
     * @param signAlgorithm the sign algorithm
     * @param filePath      the file path
     * @return the byte [ ]
     */
    public final byte[] signFile(String signAlgorithm, String filePath) {
        if (FileUtils.isExists(filePath)) {
            InputStream inputStream = null;
            try {
                Signature signature = Signature.getInstance(signAlgorithm);
                signature.initSign(this.privateKey);
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
        return new byte[0];
    }

    /**
     * Verify boolean.
     *
     * @param dataBytes     the data byte array
     * @param signature the signature
     * @return the boolean
     */
    public final boolean verify(byte[] dataBytes, byte[] signature) {
        return this.verify(dataBytes, signature, this.signAlgorithm);
    }

    /**
     * Verify boolean.
     *
     * @param datas         the datas
     * @param signature     the signature
     * @param signAlgorithm the sign algorithm
     * @return the boolean
     */
    public final boolean verify(byte[] datas, byte[] signature, String signAlgorithm) {
        try {
            Signature signInstance = Signature.getInstance(signAlgorithm);

            signInstance.initVerify(this.publicKey);
            signInstance.update(datas);

            return signInstance.verify(signature);
        } catch (Exception e) {
            return Globals.DEFAULT_VALUE_BOOLEAN;
        }
    }

    /**
     * Verify boolean.
     *
     * @param filePath  the file path
     * @param signature the signature
     * @return the boolean
     */
    public final boolean verify(String filePath, byte[] signature) {
        return this.verify(filePath, signature, this.signAlgorithm);
    }

    /**
     * Verify boolean.
     *
     * @param filePath      the file path
     * @param signature     the signature
     * @param signAlgorithm the sign algorithm
     * @return the boolean
     */
    public final boolean verify(String filePath, byte[] signature, String signAlgorithm) {
        if (FileUtils.isExists(filePath)) {
            InputStream inputStream = null;
            try {
                Signature signInstance = Signature.getInstance(signAlgorithm);
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
     * Verify certificate signature
     *
     * @param certificate Certificate
     * @return Verify result
     */
    public final boolean verify(Certificate certificate) {
        try {
            certificate.verify(this.publicKey);
            return Boolean.TRUE;
        } catch (Exception e) {
            return Boolean.FALSE;
        }
    }

    /**
     * Encrypt byte [ ].
     *
     * @param dataBytes the data bytes
     * @return the byte [ ]
     */
    public final byte[] encrypt(byte[] dataBytes) {
        try {
            byte[] encrypted = encryptData(this.cipherMode, RandomAlgorithm.NONE,
                    dataBytes, this.publicKey, new byte[0], Globals.DEFAULT_VALUE_INT);
            return convertResult(encrypted);
        } catch (NoSuchAlgorithmException e) {
            return new byte[0];
        }
    }

    /**
     * Decrypt byte [ ].
     *
     * @param dataBytes the data bytes
     * @return the byte [ ]
     */
    public final byte[] decrypt(byte[] dataBytes) {
        try {
            return decryptData(this.cipherMode, RandomAlgorithm.NONE,
                    this.parseResult(dataBytes), this.privateKey, new byte[0], Globals.DEFAULT_VALUE_INT);
        } catch (NoSuchAlgorithmException e) {
            return new byte[0];
        }
    }

    /**
     * Gets public key.
     *
     * @return the public key
     */
    public PublicKey getPublicKey() {
        return publicKey;
    }

    /**
     * Gets private key.
     *
     * @return the private key
     */
    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    /**
     * X 509 certificate optional.
     *
     * @param publicKey     the public key
     * @param serialNumber  the serial number
     * @param beginDate     the begin date
     * @param endDate       the end date
     * @param certName      the cert name
     * @param signKey       the sign key
     * @param signAlgorithm the sign algorithm
     * @return the optional
     */
    protected static Optional<X509Certificate> x509Certificate(PublicKey publicKey, long serialNumber, Date beginDate,
                                                               Date endDate, String certName, PrivateKey signKey,
                                                               String signAlgorithm) {
        if (publicKey == null || signKey == null || StringUtils.isEmpty(signAlgorithm)) {
            return Optional.empty();
        }
        X500Name subjectDN = new X500Name("CN=" + certName);
        SubjectPublicKeyInfo publicKeyInfo = SubjectPublicKeyInfo.getInstance(publicKey.getEncoded());
        X509v3CertificateBuilder x509v3CertificateBuilder =
                new X509v3CertificateBuilder(subjectDN, BigInteger.valueOf(serialNumber),
                        beginDate, endDate, subjectDN, publicKeyInfo);
        try {
            x509v3CertificateBuilder.addExtension(org.bouncycastle.asn1.x509.Extension.basicConstraints,
                    Globals.DEFAULT_VALUE_BOOLEAN, new BasicConstraints(Globals.DEFAULT_VALUE_BOOLEAN));
            ContentSigner contentSigner = new JcaContentSignerBuilder(signAlgorithm).setProvider("BC").build(signKey);
            X509CertificateHolder certificateHolder = x509v3CertificateBuilder.build(contentSigner);
            return Optional.of(new JcaX509CertificateConverter().getCertificate(certificateHolder));
        } catch (OperatorCreationException | GeneralSecurityException | IOException e) {
            LOGGER.error("Generate PKCS12 Certificate Failed! ");
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Stack message: ", e);
            }
        }
        return Optional.empty();
    }

    /**
     * Generate key optional.
     *
     * @param <T>        the type parameter
     * @param clazz      the clazz
     * @param algorithm  the algorithm
     * @param keyContent the key content
     * @return the optional
     */
    protected static <T> Optional<T> generateKey(Class<T> clazz, String algorithm, byte[] keyContent) {
        Object generatedKey = null;
        try {
            if (PrivateKey.class.equals(clazz)) {
                generatedKey = KeyFactory.getInstance(algorithm).generatePrivate(new PKCS8EncodedKeySpec(keyContent));
            } else if (PublicKey.class.equals(clazz)) {
                generatedKey = KeyFactory.getInstance(algorithm).generatePublic(new X509EncodedKeySpec(keyContent));
            }
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            LOGGER.error("Generate key from data bytes error! ");
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Stack message: ", e);
            }
        }
        return generatedKey == null ? Optional.empty() : Optional.of(clazz.cast(generatedKey));
    }

    /**
     * Generate key pair by given algorithm
     *
     * @param algorithm     Algorithm
     * @param prngAlgorithm the prng algorithm
     * @param keySize       Key size
     * @return Generated key pair
     */
    protected static Optional<KeyPair> KeyPair(String algorithm, String prngAlgorithm, int keySize) {
        if (keySize % 128 != 0) {
            LOGGER.error("Key size is invalid");
            return Optional.empty();
        }

        if (StringUtils.isEmpty(prngAlgorithm)) {
            LOGGER.error("PRNG algorithm is invalid");
            return Optional.empty();
        }

        KeyPair keyPair = null;
        try {
            //	Initialize keyPair instance
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(algorithm, "BC");
            if (algorithm.equalsIgnoreCase("EC")) {
                ECGenParameterSpec ecGenParameterSpec = new ECGenParameterSpec("sm2p256v1");
                keyPairGenerator.initialize(ecGenParameterSpec, SecureRandom.getInstance(prngAlgorithm));
            } else {
                keyPairGenerator.initialize(keySize, SecureRandom.getInstance(prngAlgorithm));
            }
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | NoSuchProviderException e) {
            LOGGER.error("Initialize key pair generator error! ");
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Stack message: ", e);
            }
        }
        return Optional.ofNullable(keyPair);
    }

    /**
     * PKCS 12 certificate byte [ ].
     *
     * @param keyPair       the key pair
     * @param serialNumber  the serial number
     * @param beginDate     the begin date
     * @param endDate       the end date
     * @param certAlias     the cert alias
     * @param certName      the cert name
     * @param password      the password
     * @param signKey       the sign key
     * @param signAlgorithm the sign algorithm
     * @return the byte [ ]
     */
    protected static byte[] PKCS12Certificate(KeyPair keyPair, long serialNumber, Date beginDate, Date endDate,
                                              String certAlias, String certName, String password,
                                              PrivateKey signKey, String signAlgorithm) {
        if (StringUtils.isEmpty(password)) {
            password = Globals.DEFAULT_VALUE_STRING;
        }
        X500Name subjectDN = new X500Name("CN=" + certName);
        SubjectPublicKeyInfo publicKeyInfo = SubjectPublicKeyInfo.getInstance(keyPair.getPublic().getEncoded());
        X509v3CertificateBuilder x509v3CertificateBuilder =
                new X509v3CertificateBuilder(subjectDN, BigInteger.valueOf(serialNumber),
                        beginDate, endDate, subjectDN, publicKeyInfo);
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            x509v3CertificateBuilder.addExtension(org.bouncycastle.asn1.x509.Extension.basicConstraints,
                    Globals.DEFAULT_VALUE_BOOLEAN, new BasicConstraints(Globals.DEFAULT_VALUE_BOOLEAN));
            ContentSigner contentSigner =
                    new JcaContentSignerBuilder(signAlgorithm).setProvider("BC")
                            .build(signKey == null ? keyPair.getPrivate() : signKey);
            X509CertificateHolder certificateHolder = x509v3CertificateBuilder.build(contentSigner);
            X509Certificate x509Certificate = new JcaX509CertificateConverter().getCertificate(certificateHolder);

            KeyStore keyStore = KeyStore.getInstance("PKCS12", "BC");
            keyStore.load(null, null);
            keyStore.setKeyEntry(certAlias, keyPair.getPrivate(),
                    password.toCharArray(), new Certificate[]{x509Certificate});
            byteArrayOutputStream = new ByteArrayOutputStream();
            keyStore.store(byteArrayOutputStream, password.toCharArray());
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
     * Convert result byte [ ].
     *
     * @param dataBytes the data bytes
     * @return the byte [ ]
     */
    protected abstract byte[] convertResult(byte[] dataBytes);

    /**
     * Parse result byte [ ].
     *
     * @param dataBytes the data bytes
     * @return the byte [ ]
     */
    protected abstract byte[] parseResult(byte[] dataBytes);
}
