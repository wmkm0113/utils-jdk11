package org.nervousync.security.test;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.nervousync.security.SecureProvider;
import org.nervousync.exceptions.crypto.CryptoException;
import org.nervousync.test.BaseTest;
import org.nervousync.utils.*;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Date;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public final class CryptoTest extends BaseTest {

    private static final String[] DEFAULT_CIPHER_MODES =
            new String[] {"ECB", "CBC", "CTR", "CTS", "CFB", "OFB", /*"PCBC", "CFB8", "CFB128", "OFB8", "OFB128"*/};
    private static final String[] DEFAULT_PADDINGS = new String[] {"PKCS5Padding", "ISO10126Padding"};
    private static final String ORIGINAL_STRING = "123456";

    @Test
    public void test000AES128() throws CryptoException {
        byte[] aesKey = SecurityUtils.AES128Key();
        this.logger.info("AES128 Key: {}", StringUtils.base64Encode(aesKey));
        for (String cipherMode : DEFAULT_CIPHER_MODES) {
            for (String padding : DEFAULT_PADDINGS) {
                SecureProvider encryptProvider = SecurityUtils.AESEncryptor(cipherMode, padding, aesKey);
                encryptProvider.append(ORIGINAL_STRING);
                String encResult = StringUtils.base64Encode(encryptProvider.finish());
                this.logger.info("AES/{}/{} encrypt result: {}", cipherMode, padding, encResult);
                SecureProvider decryptProvider = SecurityUtils.AESDecryptor(cipherMode, padding, aesKey);
                decryptProvider.append(StringUtils.base64Decode(encResult));
                this.logger.info("AES/{}/{} decrypt result: {}", cipherMode, padding,
                        new String(decryptProvider.finish(), StandardCharsets.UTF_8));
            }
        }
        aesKey = SecurityUtils.AES192Key();
        this.logger.info("AES192 Key: {}", StringUtils.base64Encode(aesKey));
        for (String cipherMode : DEFAULT_CIPHER_MODES) {
            for (String padding : DEFAULT_PADDINGS) {
                SecureProvider encryptProvider = SecurityUtils.AESEncryptor(cipherMode, padding, aesKey);
                String encResult = StringUtils.base64Encode(encryptProvider.finish(ORIGINAL_STRING));
                this.logger.info("AES/{}/{} encrypt result: {}", cipherMode, padding, encResult);
                SecureProvider decryptProvider = SecurityUtils.AESDecryptor(cipherMode, padding, aesKey);
                this.logger.info("AES/{}/{} decrypt result: {}", cipherMode, padding,
                        new String(decryptProvider.finish(StringUtils.base64Decode(encResult)), StandardCharsets.UTF_8));
            }
        }
        aesKey = SecurityUtils.AES256Key();
        this.logger.info("AES256 Key: {}", StringUtils.base64Encode(aesKey));
        for (String cipherMode : DEFAULT_CIPHER_MODES) {
            for (String padding : DEFAULT_PADDINGS) {
                SecureProvider encryptProvider = SecurityUtils.AESEncryptor(cipherMode, padding, aesKey);
                String encResult = StringUtils.base64Encode(encryptProvider.finish(ORIGINAL_STRING));
                this.logger.info("AES/{}/{} encrypt result: {}", cipherMode, padding, encResult);
                SecureProvider decryptProvider = SecurityUtils.AESDecryptor(cipherMode, padding, aesKey);
                this.logger.info("AES/{}/{} decrypt result: {}", cipherMode, padding,
                        new String(decryptProvider.finish(StringUtils.base64Decode(encResult)), StandardCharsets.UTF_8));
            }
        }
    }

    @Test
    public void test010DES() throws CryptoException {
        byte[] desKey = SecurityUtils.DESKey();
        this.logger.info("DES Key: {}", StringUtils.base64Encode(desKey));
        for (String cipherMode : DEFAULT_CIPHER_MODES) {
            for (String padding : DEFAULT_PADDINGS) {
                SecureProvider encryptProvider = SecurityUtils.DESEncryptor(cipherMode, padding, desKey);
                String encResult = StringUtils.base64Encode(encryptProvider.finish(ORIGINAL_STRING));
                this.logger.info("DES/{}/{} encrypt result: {}", cipherMode, padding, encResult);
                SecureProvider decryptProvider = SecurityUtils.DESDecryptor(cipherMode, padding, desKey);
                this.logger.info("DES/{}/{} decrypt result: {}", cipherMode, padding,
                        new String(decryptProvider.finish(StringUtils.base64Decode(encResult)), StandardCharsets.UTF_8));
            }
        }
    }

    @Test
    public void test020TripleDES() throws CryptoException {
        byte[] desKey = SecurityUtils.TripleDESKey();
        this.logger.info("TripleDES Key: {}", StringUtils.base64Encode(desKey));
        for (String cipherMode : DEFAULT_CIPHER_MODES) {
            for (String padding : DEFAULT_PADDINGS) {
                SecureProvider encryptProvider = SecurityUtils.TripleDESEncryptor(cipherMode, padding, desKey);
                String encResult = StringUtils.base64Encode(encryptProvider.finish(ORIGINAL_STRING));
                this.logger.info("DESede/{}/{} encrypt result: {}", cipherMode, padding, encResult);
                SecureProvider decryptProvider = SecurityUtils.TripleDESDecryptor(cipherMode, padding, desKey);
                this.logger.info("DESede/{}/{} decrypt result: {}", cipherMode, padding,
                        new String(decryptProvider.finish(StringUtils.base64Decode(encResult)), StandardCharsets.UTF_8));
            }
        }
    }

    private static final String[] SM4_CIPHER_MODES = new String[] {"ECB", "CBC", "CTR", "CFB", "OFB"};
    private static final String[] SM4_PADDINGS =
            new String[] {"PKCS5Padding", "PKCS7Padding", "ISO10126Padding", "X9.23Padding"};

    @Test
    public void test030SM4() throws CryptoException {
        byte[] sm4Key = SecurityUtils.SM4Key();
        this.logger.info("SM4 Key: {}", StringUtils.base64Encode(sm4Key));
        for (String cipherMode : SM4_CIPHER_MODES) {
            for (String padding : SM4_PADDINGS) {
                SecureProvider encryptProvider = SecurityUtils.SM4Encryptor(cipherMode, padding, sm4Key);
                String encResult = StringUtils.base64Encode(encryptProvider.finish(ORIGINAL_STRING));
                this.logger.info("SM4/{}/{} encrypt result: {}", cipherMode, padding, encResult);
                SecureProvider decryptProvider = SecurityUtils.SM4Decryptor(cipherMode, padding, sm4Key);
                this.logger.info("SM4/{}/{} decrypt result: {}", cipherMode, padding,
                        new String(decryptProvider.finish(StringUtils.base64Decode(encResult)), StandardCharsets.UTF_8));
            }
        }
    }

    private static final String[] RSA_PADDINGS = new String[] {
            "NoPadding", "PKCS1Padding", "OAEPWithSHA-1AndMGF1Padding",
            "OAEPWithSHA-256AndMGF1Padding", "OAEPWithSHA-384AndMGF1Padding", "OAEPWithSHA-512AndMGF1Padding"
    };

    @Test
    public void test040RSA() throws CryptoException {
        KeyPair keyPair = SecurityUtils.RSAKeyPair(2048);
        long currentTime = DateTimeUtils.currentTimeMillis();
        byte[] pkcs5 = CertificateUtils.PKCS12(keyPair, (Long) IDUtils.random("Snowflake"),
                new Date(currentTime), new Date(currentTime + 30 * 24 * 60 * 60 * 1000L),
                "CERT", "CERT", "changeit", null, "SHA256withRSA");
        this.logger.info("RSA certificate: {}", StringUtils.base64Encode(pkcs5));
        for (String padding : RSA_PADDINGS) {
            SecureProvider encryptProvider = SecurityUtils.RSAEncryptor(padding, keyPair.getPublic());
            String encResult = StringUtils.base64Encode(encryptProvider.finish(ORIGINAL_STRING));
            this.logger.info("RSA/ECB/{} encrypt result: {}", padding, encResult);
            SecureProvider decryptProvider = SecurityUtils.RSADecryptor(padding, keyPair.getPrivate());
            decryptProvider.append(StringUtils.base64Decode(encResult));
            this.logger.info("RSA/ECB/{} decrypt result: {}", padding,
                    new String(decryptProvider.finish(), StandardCharsets.UTF_8));
        }
    }

    @Test
    public void test050SM2() throws CryptoException {
        KeyPair keyPair = SecurityUtils.SM2KeyPair();
        long currentTime = DateTimeUtils.currentTimeMillis();
        byte[] pkcs5 = CertificateUtils.PKCS12(keyPair, (Long) IDUtils.random("Snowflake"),
                new Date(currentTime), new Date(currentTime + 30 * 24 * 60 * 60 * 1000L),
                "CERT", "CERT", "changeit", null, "SM3withSM2");
        this.logger.info("SM2 certificate: {}", StringUtils.base64Encode(pkcs5));
        X509Certificate x509Certificate =
                CertificateUtils.x509(pkcs5, "CERT", "changeit", keyPair.getPublic(), Boolean.TRUE);
        if (x509Certificate == null) {
            return;
        }
        PublicKey publicKey = x509Certificate.getPublicKey();
        SecureProvider encryptProvider = SecurityUtils.SM2Encryptor(publicKey);
        String encResult = StringUtils.base64Encode(encryptProvider.finish(ORIGINAL_STRING));
        this.logger.info("SM2 encrypt result: {}", encResult);
        SecureProvider decryptProvider = SecurityUtils.SM2Decryptor(keyPair.getPrivate());
        this.logger.info("SM2 decrypt result: {}",
                new String(decryptProvider.finish(StringUtils.base64Decode(encResult)), StandardCharsets.UTF_8));
    }
}
