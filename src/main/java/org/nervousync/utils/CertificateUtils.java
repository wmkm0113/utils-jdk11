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

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
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

/**
 * <h2 class="en">Certificate Utils</h2>
 * <span class="en">
 *     <ul>Generate Keypair</ul>
 *     <ul>Signature and generate X.509 certificate</ul>
 *     <ul>Parse X.509 certificate from certificate file, PKCS12 file or binary data arrays</ul>
 *     <ul>Validate X.509 certificate period and signature</ul>
 *     <ul>Read PublicKey/PrivateKey from binary data arrays or PKCS12 file</ul>
 *     <ul>Signature and generate PKCS12 file</ul>
 * </span>
 * <h2 class="zhs">数字证书工具</h2>
 * <span class="zhs">
 *     <ul>生成密钥对</ul>
 *     <ul>签发X.509证书</ul>
 *     <ul>从证书文件、PKCS12文件或二进制数据中读取X.509证书</ul>
 *     <ul>验证X.509证书的有效期、数字签名</ul>
 *     <ul>从PKCS12文件或二进制数据中读取公钥和私钥</ul>
 *     <ul>生成PKCS12文件</ul>
 * </span>
 */
public final class CertificateUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(CertificateUtils.class);

    static {
        /*
         * <span class="en">Add Bouncy Castle Provider</span>
         * <span class="zhs">添加Bouncy Castle算法库</span>
         */
        Security.addProvider(new BouncyCastleProvider());
    }

    private CertificateUtils() {
    }

    /**
     * <h3 class="en">Generate KeyPair using given algorithm/secure random algorithm/key size</h3>
     * <h3 class="zhs">根据给定的算法、安全随机数算法、密钥长度生成密钥对</h3>
     *
     * @param algorithm       <span class="en">Algorithm</span>
     *                        <span class="zhs">算法</span>
     * @param randomAlgorithm <span class="en">Secure Random Algorithm</span>
     *                        <span class="zhs">安全随机数算法</span>
     * @param keySize         <span class="en">Key size</span>
     *                        <span class="zhs">密钥长度</span>
     * @return  <span class="en">Generated key pair</span>
     *          <span class="zhs">生成的密钥对</span>
     */
    public static KeyPair keyPair(final String algorithm, final String randomAlgorithm, final int keySize) {
        if (keySize % 128 != 0) {
            LOGGER.error("Key size is invalid");
            return null;
        }

        KeyPair keyPair = null;
        try {
            SecureRandom secureRandom;
            if (StringUtils.isEmpty(randomAlgorithm)) {
                LOGGER.error("Random algorithm not configure, use default: SHA1PRNG");
                secureRandom = SecureRandom.getInstance("SHA1PRNG");
            } else {
                secureRandom = SecureRandom.getInstance(randomAlgorithm);
            }

            //	Initialize keyPair instance
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(algorithm, "BC");
            if (algorithm.equalsIgnoreCase("EC")) {
                ECGenParameterSpec ecGenParameterSpec = new ECGenParameterSpec("sm2p256v1");
                keyPairGenerator.initialize(ecGenParameterSpec, secureRandom);
            } else {
                keyPairGenerator.initialize(keySize, secureRandom);
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
     * <h3 class="en">Convert public key to X.509 certificate, signature certificate by given private key and signature algorithm</h3>
     * <h3 class="zhs">转换PublicKey为X509证书，并使用给定的私钥和签名算法进行签名</h3>
     *
     * @param publicKey     <span class="en">Public key</span>
     *                      <span class="zhs">公钥</span>
     * @param serialNumber  <span class="en">Certificate Serial Number</span>
     *                      <span class="zhs">证书的序列号</span>
     * @param beginDate     <span class="en">Certificate Begin Date</span>
     *                      <span class="zhs">证书有效期起始时间</span>
     * @param endDate       <span class="en">Certificate End Date</span>
     *                      <span class="zhs">证书有效期截至时间</span>
     * @param commonName    <span class="en">Certificate Common Name</span>
     *                      <span class="zhs">证书的公用名称（CN字段）</span>
     * @param signKey       <span class="en">Certificate Signer Private Key</span>
     *                      <span class="zhs">证书签发者的私钥</span>
     * @param signAlgorithm <span class="en">Signature Algorithm</span>
     *                      <span class="zhs">签名算法</span>
     * @return  <span class="en">Generated X.509 certificate</span>
     *          <span class="zhs">生成的X.509格式证书</span>
     */
    public static X509Certificate x509(final PublicKey publicKey, final long serialNumber,
                                       final Date beginDate, final Date endDate, final String commonName,
                                       final PrivateKey signKey, final String signAlgorithm) {
        if (publicKey == null || signKey == null || StringUtils.isEmpty(signAlgorithm)) {
            return null;
        }
        X500Name subjectDN = new X500Name("CN=" + commonName);
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
     * <h3 class="en">Read X.509 Certificate</h3>
     * <h3 class="zhs">读取X.509格式的证书</h3>
     *
     * @param certBytes <span class="en">Certificate Data Bytes</span>
     *                  <span class="zhs">证书的字节数组</span>
     * @return  <span class="en">Read X.509 certificate or null for invalid</span>
     *          <span class="zhs">读取的X.509证书， 如果数据非法，则返回null</span>
     */
    public static X509Certificate x509(final byte[] certBytes) {
        return x509(certBytes, Boolean.FALSE);
    }

    /**
     * <h3 class="en">Read X.509 Certificate and verified by given public key</h3>
     * <h3 class="zhs">读取X.509格式的证书并使用给定的公钥验证证书签名</h3>
     *
     * @param certBytes <span class="en">Certificate Data Bytes</span>
     *                  <span class="zhs">证书的字节数组</span>
     * @param verifyKey <span class="en">Verifier Public Key</span>
     *                  <span class="zhs">验证签名使用的公钥</span>
     * @return  <span class="en">Read X.509 certificate or null for invalid</span>
     *          <span class="zhs">读取的X.509证书， 如果数据非法或证书签名验证失败，则返回null</span>
     */
    public static X509Certificate x509(byte[] certBytes, PublicKey verifyKey) {
        return x509(certBytes, verifyKey, Boolean.FALSE);
    }

    /**
     * <h3 class="en">Read X.509 Certificate and check certificate validity period</h3>
     * <h3 class="zhs">读取X.509格式的证书并检查证书是否在有效期内</h3>
     *
     * @param certBytes <span class="en">Certificate Data Bytes</span>
     *                  <span class="zhs">证书的字节数组</span>
     * @param checkValidity <span class="en"><code>true</code> for check certificate signature, <code>false</code> for not check</span>
     *                      <span class="zhs"><code>true</code>检查证书有效期, <code>false</code>不检查证书有效期</span>
     * @return  <span class="en">Read X.509 certificate or null for invalid</span>
     *          <span class="zhs">读取的X.509证书， 如果数据非法或证书未在有效期内，则返回null</span>
     */
    public static X509Certificate x509(byte[] certBytes, boolean checkValidity) {
        return x509(certBytes, null, checkValidity);
    }

    /**
     * <h3 class="en">Read X.509 Certificate, verified by given public key and check certificate validity period</h3>
     * <h3 class="zhs">读取X.509格式的证书，使用给定的公钥验证证书签名并检查证书是否在有效期内</h3>
     *
     * @param certBytes <span class="en">Certificate Data Bytes</span>
     *                  <span class="zhs">证书的字节数组</span>
     * @param verifyKey <span class="en">Verifier Public Key</span>
     *                  <span class="zhs">验证签名使用的公钥</span>
     * @param checkValidity <span class="en"><code>true</code> for check certificate signature, <code>false</code> for not check</span>
     *                      <span class="zhs"><code>true</code>检查证书有效期, <code>false</code>不检查证书有效期</span>
     * @return  <span class="en">Read X.509 certificate or null for invalid</span>
     *          <span class="zhs">读取的X.509证书， 如果数据非法、证书签名验证失败或证书未在有效期内，则返回null</span>
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
     * <h3 class="en">Check X.509 Certificate Validity Period</h3>
     * <h3 class="zhs">检查证书是否在有效期内</h3>
     *
     * @param x509Certificate <span class="en">X.509 Certificate</span>
     *                        <span class="zhs">X.509证书</span>
     * @return  <span class="en">Verify Result. <code>true</code> for success, <code>false</code> for failed</span>
     *          <span class="zhs">验证结果。<code>true</code>验证通过, <code>false</code>验证失败</span>
     */
    public static boolean verify(final X509Certificate x509Certificate) {
        return verify(x509Certificate, null, Boolean.TRUE);
    }

    /**
     * <h3 class="en">Verify X.509 Certificate Signature by given Public Key</h3>
     * <h3 class="zhs">使用给定的公钥检查证书签名是否有效</h3>
     *
     * @param x509Certificate   <span class="en">X.509 Certificate</span>
     *                          <span class="zhs">X.509证书</span>
     * @param verifyKey         <span class="en">Verifier Public Key</span>
     *                          <span class="zhs">验证签名使用的公钥</span>
     * @return  <span class="en">Verify Result. <code>true</code> for success, <code>false</code> for failed</span>
     *          <span class="zhs">验证结果。<code>true</code>验证通过, <code>false</code>验证失败</span>
     */
    public static boolean verify(final X509Certificate x509Certificate, final PublicKey verifyKey) {
        return verify(x509Certificate, verifyKey, Boolean.FALSE);
    }

    /**
     * <h3 class="en">Check X.509 Certificate Validity Period and Verify Signature by given Public Key</h3>
     * <h3 class="zhs">检查证书是否在有效期内并使用给定的公钥检查证书签名是否有效</h3>
     *
     * @param x509Certificate   <span class="en">X.509 Certificate</span>
     *                          <span class="zhs">X.509证书</span>
     * @param verifyKey         <span class="en">Verifier Public Key</span>
     *                          <span class="zhs">验证签名使用的公钥</span>
     * @param checkValidity <span class="en"><code>true</code> for check certificate signature, <code>false</code> for not check</span>
     *                      <span class="zhs"><code>true</code>检查证书有效期, <code>false</code>不检查证书有效期</span>
     * @return  <span class="en">Verify Result. <code>true</code> for success, <code>false</code> for failed</span>
     *          <span class="zhs">验证结果。<code>true</code>验证通过, <code>false</code>验证失败</span>
     */
    public static boolean verify(final X509Certificate x509Certificate, final PublicKey verifyKey,
                                 final boolean checkValidity) {
        if (x509Certificate == null) {
            return Boolean.FALSE;
        }
        try {
            if (checkValidity) {
                x509Certificate.checkValidity();
            }
            x509Certificate.verify((verifyKey == null) ? x509Certificate.getPublicKey() : verifyKey, "BC");
            return Boolean.TRUE;
        } catch (Exception e) {
            LOGGER.error("Certificate is invalid! ");
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Stack message: ", e);
            }
            return Boolean.FALSE;
        }
    }

    /**
     * <h3 class="en">Read X.509 certificate from Keystore/PKCS12 data bytes</h3>
     * <h3 class="zhs">从Keystore/PKCS12的二进制数据中读取X.509证书</h3>
     *
     * @param storeBytes <span class="en">Keystore/PKCS12 data bytes</span>
     *                   <span class="zhs">Keystore/PKCS12的二进制数据</span>
     * @param certAlias  <span class="en">Certificate alias name</span>
     *                   <span class="zhs">证书别名</span>
     * @param password   <span class="en">Password of Keystore/PKCS12</span>
     *                   <span class="zhs">Keystore/PKCS12的密码</span>
     * @return  <span class="en">Read X.509 certificate or null for invalid</span>
     *          <span class="zhs">读取的X.509证书， 如果数据非法或未找到别名指定的证书，则返回null</span>
     */
    public static X509Certificate x509(byte[] storeBytes, String certAlias, String password) {
        return x509(storeBytes, certAlias, password, null, Boolean.FALSE);
    }

    /**
     * <h3 class="en">Read X.509 certificate from file. File format: Keystore/PKCS12</h3>
     * <h3 class="zhs">从指定路径的Keystore/PKCS12文件中读取X.509证书， 文件格式为：Keystore/PKCS12</h3>
     *
     * @param storePath <span class="en">Keystore/PKCS12 file path</span>
     *                  <span class="zhs">Keystore/PKCS12文件路径</span>
     * @param certAlias  <span class="en">Certificate alias name</span>
     *                   <span class="zhs">证书别名</span>
     * @param password   <span class="en">Password of Keystore/PKCS12</span>
     *                   <span class="zhs">Keystore/PKCS12的密码</span>
     * @return  <span class="en">Read X.509 certificate or null for invalid</span>
     *          <span class="zhs">读取的X.509证书， 如果数据非法或未找到别名指定的证书，则返回null</span>
     */
    public static X509Certificate x509(String storePath, String certAlias, String password) {
        try {
            return x509(FileUtils.readFileBytes(storePath), certAlias, password);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * <h3 class="en">Read X.509 certificate from Keystore/PKCS12 data bytes, Validity Period and Verify Signature by given Public Key.</h3>
     * <h3 class="zhs">从Keystore/PKCS12的二进制数据中读取X.509证书，检查证书是否在有效期内并使用给定的公钥检查证书签名是否有效</h3>
     *
     * @param storeBytes <span class="en">Keystore/PKCS12 data bytes</span>
     *                   <span class="zhs">Keystore/PKCS12的二进制数据</span>
     * @param certAlias  <span class="en">Certificate alias name</span>
     *                   <span class="zhs">证书别名</span>
     * @param password   <span class="en">Password of Keystore/PKCS12</span>
     *                   <span class="zhs">Keystore/PKCS12的密码</span>
     * @param verifyKey         <span class="en">Verifier Public Key</span>
     *                          <span class="zhs">验证签名使用的公钥</span>
     * @param checkValidity <span class="en"><code>true</code> for check certificate signature, <code>false</code> for not check</span>
     *                      <span class="zhs"><code>true</code>检查证书有效期, <code>false</code>不检查证书有效期</span>
     * @return  <span class="en">Read X.509 certificate or null for invalid</span>
     *          <span class="zhs">读取的X.509证书， 如果数据非法、未找到别名指定的证书、证书未在有效期或证书签名错误，则返回null</span>
     */
    public static X509Certificate x509(final byte[] storeBytes, final String certAlias, final String password,
                                       final PublicKey verifyKey, final boolean checkValidity) {
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
     * <h3 class="en">Generate PublicKey from key data bytes and given algorithm</h3>
     * <h3 class="zhs">根据给定的算法和二进制数据生成公钥</h3>
     *
     * @param algorithm <span class="en">Key algorithm</span>
     *                  <span class="zhs">算法</span>
     * @param keyBytes  <span class="en">Key data bytes</span>
     *                  <span class="zhs">二进制数据</span>
     * @return  <span class="en">Generated publicKey or null if data bytes invalid</span>
     *          <span class="zhs">生成的公钥，如果二进制数据非法则返回null</span>
     */
    public static PublicKey publicKey(String algorithm, byte[] keyBytes) {
        try {
            return KeyFactory.getInstance(algorithm).generatePublic(new X509EncodedKeySpec(keyBytes));
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            LOGGER.error("Generate key from data bytes error! ");
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Stack message: ", e);
            }
        }
        return null;
    }

    /**
     * <h3 class="en">Generate PrivateKey from key data bytes and given algorithm</h3>
     * <h3 class="zhs">根据给定的算法和二进制数据生成私钥</h3>
     *
     * @param algorithm <span class="en">Key algorithm</span>
     *                  <span class="zhs">算法</span>
     * @param keyBytes  <span class="en">Key data bytes</span>
     *                  <span class="zhs">二进制数据</span>
     * @return  <span class="en">Generated privateKey or null if data bytes invalid</span>
     *          <span class="zhs">生成的私钥，如果二进制数据非法则返回null</span>
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
     * <h3 class="en">Read PrivateKey from Keystore/PKCS12 data bytes</h3>
     *
     * @param storeBytes <span class="en">Keystore/PKCS12 data bytes</span>
     *                   <span class="zhs">Keystore/PKCS12的二进制数据</span>
     * @param certAlias  <span class="en">Certificate alias name</span>
     *                   <span class="zhs">证书别名</span>
     * @param password   <span class="en">Password of Keystore/PKCS12</span>
     *                   <span class="zhs">Keystore/PKCS12的密码</span>
     * @return  <span class="en">Read PrivateKey or null if data bytes invalid</span>
     *          <span class="zhs">读取的私钥， 如果数据非法、未找到别名指定的证书，则返回null</span>
     */
    public static PrivateKey privateKey(byte[] storeBytes, String certAlias, String password) {
        KeyStore keyStore = loadKeyStore(storeBytes, password);
        if (keyStore != null && CertificateUtils.checkKey(keyStore, certAlias)) {
            return CertificateUtils.privateKey(keyStore, certAlias, password);
        }
        return null;
    }

    /**
     * <h3 class="en">Read PrivateKey from file. File format: Keystore/PKCS12</h3>
     * <h3 class="zhs">从指定路径的Keystore/PKCS12文件中读取私钥， 文件格式为：Keystore/PKCS12</h3>
     *
     * @param storePath <span class="en">Keystore/PKCS12 file path</span>
     *                  <span class="zhs">Keystore/PKCS12文件路径</span>
     * @param certAlias  <span class="en">Certificate alias name</span>
     *                   <span class="zhs">证书别名</span>
     * @param password   <span class="en">Password of Keystore/PKCS12</span>
     *                   <span class="zhs">Keystore/PKCS12的密码</span>
     * @return  <span class="en">Read PrivateKey or null if invalid</span>
     *          <span class="zhs">读取的私钥， 如果数据非法或未找到别名指定的证书，则返回null</span>
     */
    public static PrivateKey privateKey(String storePath, String certAlias, String password) {
        KeyStore keyStore = loadKeyStore(storePath, password);
        if (keyStore != null && CertificateUtils.checkKey(keyStore, certAlias)) {
            return CertificateUtils.privateKey(keyStore, certAlias, password);
        }
        return null;
    }

    /**
     * <h3 class="en">Generate PKCS12 Data Bytes, include PrivateKey, PublicKey and generate signature</h3>
     * <h3 class="zhs">生成PKCS12格式的证书二进制数据，包含公钥、私钥及数字签名</h3>
     *
     * @param keyPair       <span class="en">Key pair</span>
     *                      <span class="zhs">密钥对</span>
     * @param serialNumber  <span class="en">Certificate Serial Number</span>
     *                      <span class="zhs">证书的序列号</span>
     * @param beginDate     <span class="en">Certificate Begin Date</span>
     *                      <span class="zhs">证书有效期起始时间</span>
     * @param endDate       <span class="en">Certificate End Date</span>
     *                      <span class="zhs">证书有效期截至时间</span>
     * @param certAlias  <span class="en">Certificate alias name</span>
     *                   <span class="zhs">证书别名</span>
     * @param commonName    <span class="en">Certificate Common Name</span>
     *                      <span class="zhs">证书的公用名称（CN字段）</span>
     * @param passWord   <span class="en">Password of Keystore/PKCS12</span>
     *                   <span class="zhs">Keystore/PKCS12的密码</span>
     * @param signKey       <span class="en">Certificate Signer Private Key</span>
     *                      <span class="zhs">证书签发者的私钥</span>
     * @param signAlgorithm <span class="en">Signature Algorithm</span>
     *                      <span class="zhs">签名算法</span>
     * @return  <span class="en">Generated PKCS12 data bytes or 0 length byte array if has error</span>
     *          <span class="zhs">生成的PKCS12格式二进制数据，如果出错则返回长度为0的二进制数据</span>
     */
    public static byte[] PKCS12(final KeyPair keyPair, final long serialNumber,
                                final Date beginDate, final Date endDate, final String certAlias,
                                final String commonName, final String passWord,
                                final PrivateKey signKey, final String signAlgorithm) {
        char[] charArray;
        if (StringUtils.isEmpty(passWord)) {
            charArray = Globals.DEFAULT_VALUE_STRING.toCharArray();
        } else {
            charArray = passWord.toCharArray();
        }
        X500Name subjectDN = new X500Name("CN=" + commonName);
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
            keyStore.setKeyEntry(certAlias, keyPair.getPrivate(), charArray, new Certificate[]{x509Certificate});
            byteArrayOutputStream = new ByteArrayOutputStream();
            keyStore.store(byteArrayOutputStream, charArray);
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
     * <h3 class="en">Read PKCS12 KeyStore from data bytes</h3>
     * <h3 class="zhs">从二进制数据中读取PKCS12格式的密钥库</h3>
     *
     * @param storeBytes <span class="en">Keystore/PKCS12 data bytes</span>
     *                   <span class="zhs">Keystore/PKCS12的二进制数据</span>
     * @param password  <span class="en">Password of Keystore/PKCS12</span>
     *                  <span class="zhs">Keystore/PKCS12的密码</span>
     * @return  <span class="en">Read PKCS12 KeyStore or null for error</span>
     *          <span class="zhs">读取的PKCS12格式密钥库， 如果数据非法则返回null</span>
     */
    public static KeyStore loadKeyStore(byte[] storeBytes, String password) {
        return loadKeyStore(new ByteArrayInputStream(storeBytes), password);
    }

    /**
     * <h3 class="en">Read PKCS12 KeyStore from given file path</h3>
     * <h3 class="zhs">从指定的文件位置读取PKCS12格式的密钥库</h3>
     *
     * @param storePath <span class="en">Keystore/PKCS12 file path</span>
     *                  <span class="zhs">Keystore/PKCS12文件路径</span>
     * @param password  <span class="en">Password of Keystore/PKCS12</span>
     *                  <span class="zhs">Keystore/PKCS12的密码</span>
     * @return  <span class="en">Read PKCS12 KeyStore or null for error</span>
     *          <span class="zhs">读取的PKCS12格式密钥库， 如果文件未找到或数据非法则返回null</span>
     */
    public static KeyStore loadKeyStore(String storePath, String password) {
        try {
            return loadKeyStore(new FileInputStream(storePath), password);
        } catch (FileNotFoundException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Read data error! ", e);
            }
        }
        return null;
    }

    /**
     * <h3 class="en">Read PKCS12 KeyStore from input stream</h3>
     * <h3 class="zhs">从输入流读取PKCS12格式的密钥库</h3>
     *
     * @param inputStream   <span class="en">Input stream</span>
     *                      <span class="zhs">输入流</span>
     * @param password      <span class="en">Password of Keystore/PKCS12</span>
     *                      <span class="zhs">Keystore/PKCS12的密码</span>
     * @return  <span class="en">Read PKCS12 KeyStore or null for error</span>
     *          <span class="zhs">读取的PKCS12格式密钥库， 如果数据非法则返回null</span>
     */
    public static KeyStore loadKeyStore(InputStream inputStream, String password) {
        KeyStore keyStore;
        try {
            keyStore = KeyStore.getInstance("PKCS12", "BC");
            keyStore.load(inputStream, password == null ? null : password.toCharArray());
        } catch (Exception e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Read data error! ", e);
            }
            keyStore = null;
        } finally {
            IOUtils.closeStream(inputStream);
        }
        return keyStore;
    }

    /**
     * <h3 class="en">Check PKCS12 contains certificate alias</h3>
     * <h3 class="zhs">检查PKCS12标准的密钥库中是否包含给定别名的证书</h3>
     *
     * @param keyStore  <span class="en">PKCS12 KeyStore</span>
     *                  <span class="zhs">PKCS12标准的密钥库</span>
     * @param certAlias <span class="en">Certificate alias name</span>
     *                  <span class="zhs">证书别名</span>
     * @return  <span class="en">Check result. <code>true</code> for exists <code>false</code> for not found</span>
     *          <span class="zhs">检查结果 <code>true</code>证书存在 <code>false</code>证书不存在</span>
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
     * <h3 class="en">Read X.509 certificate from PKCS12 KeyStore</h3>
     * <h3 class="zhs">从PKCS12格式的密钥库中读取X.509证书</h3>
     *
     * @param keyStore  <span class="en">PKCS12 KeyStore</span>
     *                  <span class="zhs">PKCS12标准的密钥库</span>
     * @param certAlias <span class="en">Certificate alias name</span>
     *                  <span class="zhs">证书别名</span>
     * @return  <span class="en">Read X.509 certificate or null for not found</span>
     *          <span class="zhs">读取的X.509证书， 如果数据非法、未找到别名指定的证书，则返回null</span>
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
     * <h3 class="en">Read PrivateKey from PKCS12 KeyStore</h3>
     * <h3 class="zhs">从PKCS12格式的密钥库中读取私钥</h3>
     *
     * @param keyStore  <span class="en">PKCS12 KeyStore</span>
     *                  <span class="zhs">PKCS12标准的密钥库</span>
     * @param certAlias <span class="en">Certificate alias name</span>
     *                  <span class="zhs">证书别名</span>
     * @param password  <span class="en">Password of Keystore/PKCS12</span>
     *                  <span class="zhs">Keystore/PKCS12的密码</span>
     * @return  <span class="en">Read PrivateKey or null if invalid</span>
     *          <span class="zhs">读取的私钥， 如果数据非法或未找到别名指定的证书，则返回null</span>
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
