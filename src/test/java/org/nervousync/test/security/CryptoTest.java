package org.nervousync.test.security;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.nervousync.exceptions.crypto.CryptoException;
import org.nervousync.security.api.SecureAdapter;
import org.nervousync.test.BaseTest;
import org.nervousync.utils.*;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Date;

public final class CryptoTest extends BaseTest {

    private static final String[] AES_CIPHER_MODES =
            new String[]{"ECB", "CBC", "CTR", "CTS", "CFB", "OFB", "CFB8", "OFB8", "CFB128", "OFB128"};
    private static final String[] DES_CIPHER_MODES =
            new String[]{"ECB", "CBC", "CTR", "CTS", "CFB", "OFB", "CFB8", "OFB8"};
    private static final String[] TRIPLE_DES_CIPHER_MODES =
            new String[]{"ECB", "CBC", "CTR", "CTS", "CFB", "OFB", "CFB8", "OFB8"};
    private static final String[] DEFAULT_PADDINGS = new String[]{"PKCS5Padding", "PKCS7Padding", "ISO10126Padding", "X9.23Padding"};

    private static final String[] RSA_PADDINGS = new String[]{
            "NoPadding", "PKCS1Padding", "OAEPWithSHA-1AndMGF1Padding", "OAEPWithSHA-224AndMGF1Padding",
            "OAEPWithSHA-256AndMGF1Padding", "OAEPWithSHA-384AndMGF1Padding", "OAEPWithSHA-512AndMGF1Padding",
            "OAEPWithSHA3-224AndMGF1Padding", "OAEPWithSHA3-256AndMGF1Padding", "OAEPWithSHA3-384AndMGF1Padding",
            "OAEPWithSHA3-512AndMGF1Padding"
    };
    private static final String ORIGINAL_STRING = "Test测试TestTest测试TestTestTest测试TestTestTestTest测试TestTestTestTestTest测试TestTestTestTestTestTest测试TestTestTestTestTestTestTest测试TestTestTestTestTest测试";

    private static final String[] SM4_CIPHER_MODES = new String[]{"ECB", "CBC", "CTR", "CFB", "OFB", "CFB8", "OFB8", "CFB128", "OFB128"};
    private static final String[] RC_CIPHER_MODES = new String[]{"ECB", "CBC", "CTR", "CFB", "OFB", "OFB", "CFB8", "OFB8"};

    @Test
    @Order(0)
    public void AES() throws CryptoException {
        byte[] aesKey = SecurityUtils.AES128Key();
        this.logger.info("Crypto_Key_Length", "AES128", StringUtils.base64Encode(aesKey));
        for (String cipherMode : AES_CIPHER_MODES) {
            for (String padding : DEFAULT_PADDINGS) {
                SecureAdapter encryptProvider = SecurityUtils.AESEncryptor(cipherMode, padding, aesKey);
                encryptProvider.append(ORIGINAL_STRING);
                String encResult = StringUtils.base64Encode(encryptProvider.finish());
                this.logger.info("Encrypt_Result", "AES", cipherMode, padding, encResult);
                SecureAdapter decryptProvider = SecurityUtils.AESDecryptor(cipherMode, padding, aesKey);
                decryptProvider.append(StringUtils.base64Decode(encResult));
                this.logger.info("Decrypt_Result", "AES", cipherMode, padding,
                        new String(decryptProvider.finish(), StandardCharsets.UTF_8));
            }
        }
        aesKey = SecurityUtils.AES192Key();
        this.logger.info("Crypto_Key_Length", "AES192", StringUtils.base64Encode(aesKey));
        for (String cipherMode : AES_CIPHER_MODES) {
            for (String padding : DEFAULT_PADDINGS) {
                SecureAdapter encryptProvider = SecurityUtils.AESEncryptor(cipherMode, padding, aesKey);
                String encResult = StringUtils.base64Encode(encryptProvider.finish(ORIGINAL_STRING));
                this.logger.info("Encrypt_Result", "AES", cipherMode, padding, encResult);
                SecureAdapter decryptProvider = SecurityUtils.AESDecryptor(cipherMode, padding, aesKey);
                this.logger.info("Decrypt_Result", "AES", cipherMode, padding,
                        new String(decryptProvider.finish(StringUtils.base64Decode(encResult)), StandardCharsets.UTF_8));
            }
        }
        aesKey = SecurityUtils.AES256Key();
        this.logger.info("Crypto_Key_Length", "AES256", StringUtils.base64Encode(aesKey));
        for (String cipherMode : AES_CIPHER_MODES) {
            for (String padding : DEFAULT_PADDINGS) {
                SecureAdapter encryptProvider = SecurityUtils.AESEncryptor(cipherMode, padding, aesKey);
                String encResult = StringUtils.base64Encode(encryptProvider.finish(ORIGINAL_STRING));
                this.logger.info("Encrypt_Result", "AES", cipherMode, padding, encResult);
                SecureAdapter decryptProvider = SecurityUtils.AESDecryptor(cipherMode, padding, aesKey);
                this.logger.info("Decrypt_Result", "AES", cipherMode, padding,
                        new String(decryptProvider.finish(StringUtils.base64Decode(encResult)), StandardCharsets.UTF_8));
            }
        }
    }

    @Test
    @Order(10)
    public void DES() throws CryptoException {
        byte[] desKey = SecurityUtils.DESKey();
        this.logger.info("Crypto_Key_Length", "DES", StringUtils.base64Encode(desKey));
        for (String cipherMode : DES_CIPHER_MODES) {
            for (String padding : DEFAULT_PADDINGS) {
                SecureAdapter encryptProvider = SecurityUtils.DESEncryptor(cipherMode, padding, desKey);
                String encResult = StringUtils.base64Encode(encryptProvider.finish(ORIGINAL_STRING));
                this.logger.info("Encrypt_Result", "DES", cipherMode, padding, encResult);
                SecureAdapter decryptProvider = SecurityUtils.DESDecryptor(cipherMode, padding, desKey);
                this.logger.info("Decrypt_Result", "DES", cipherMode, padding,
                        new String(decryptProvider.finish(StringUtils.base64Decode(encResult)), StandardCharsets.UTF_8));
            }
        }
    }

    @Test
    @Order(20)
    public void tripleDES() throws CryptoException {
        byte[] desKey = SecurityUtils.TripleDESKey();
        this.logger.info("Crypto_Key_Length", "TripleDES", StringUtils.base64Encode(desKey));
        for (String cipherMode : TRIPLE_DES_CIPHER_MODES) {
            for (String padding : DEFAULT_PADDINGS) {
                SecureAdapter encryptProvider = SecurityUtils.TripleDESEncryptor(cipherMode, padding, desKey);
                String encResult = StringUtils.base64Encode(encryptProvider.finish(ORIGINAL_STRING));
                this.logger.info("Encrypt_Result", "DESede", cipherMode, padding, encResult);
                SecureAdapter decryptProvider = SecurityUtils.TripleDESDecryptor(cipherMode, padding, desKey);
                this.logger.info("Decrypt_Result", "DESede", cipherMode, padding,
                        new String(decryptProvider.finish(StringUtils.base64Decode(encResult)), StandardCharsets.UTF_8));
            }
        }
    }

    @Test
    @Order(30)
    public void SM4() throws CryptoException {
        byte[] sm4Key = SecurityUtils.SM4Key();
        this.logger.info("Crypto_Key_Length", "SM4", StringUtils.base64Encode(sm4Key));
        for (String cipherMode : SM4_CIPHER_MODES) {
            for (String padding : DEFAULT_PADDINGS) {
                SecureAdapter encryptProvider = SecurityUtils.SM4Encryptor(cipherMode, padding, sm4Key);
                String encResult = StringUtils.base64Encode(encryptProvider.finish(ORIGINAL_STRING));
                this.logger.info("Encrypt_Result", "SM4", cipherMode, padding, encResult);
                SecureAdapter decryptProvider = SecurityUtils.SM4Decryptor(cipherMode, padding, sm4Key);
                this.logger.info("Decrypt_Result", "SM4", cipherMode, padding,
                        new String(decryptProvider.finish(StringUtils.base64Decode(encResult)), StandardCharsets.UTF_8));
            }
        }
    }

    @Test
    @Order(40)
    public void RSA() throws CryptoException {
        //  Generate RSA certificate
        KeyPair keyPair = SecurityUtils.RSAKeyPair();
        long currentTime = DateTimeUtils.currentTimeMillis();
        byte[] pkcs5 = CertificateUtils.PKCS12(keyPair, IDUtils.snowflake(),
                new Date(currentTime), new Date(currentTime + 30 * 24 * 60 * 60 * 1000L),
                "CERT", "CERT", "changeit", null, "SHA256withRSA");
        this.logger.info("Certificate_Result", "RSA", StringUtils.base64Encode(pkcs5));
        //  Testing for encrypting and decrypt data using RSA
        for (String padding : RSA_PADDINGS) {
            if (padding.equalsIgnoreCase("OAEPWithSHA-512AndMGF1Padding")) {
                //  Minimum key size was 2048 when padding mode is "OAEPWithSHA-512AndMGF1Padding"
                keyPair = SecurityUtils.RSAKeyPair(2048);
            }
            SecureAdapter encryptProvider = SecurityUtils.RSAEncryptor(padding, keyPair.getPublic());
            String encResult = StringUtils.base64Encode(encryptProvider.finish(ORIGINAL_STRING));
            this.logger.info("Encrypt_Result", "RSA", "ECB", padding, encResult);
            SecureAdapter decryptProvider = SecurityUtils.RSADecryptor(padding, keyPair.getPrivate());
            decryptProvider.append(StringUtils.base64Decode(encResult));
            this.logger.info("Decrypt_Result", "RSA", "ECB", padding,
                    new String(decryptProvider.finish(), StandardCharsets.UTF_8));
        }
        String randomString = StringUtils.randomString(128);
        SecureAdapter signProvider = SecurityUtils.RSASigner(keyPair.getPrivate());
        byte[] signBytes = signProvider.finish(randomString);
        this.logger.info("Signature_Result", randomString, "RSA", StringUtils.base64Encode(signBytes));
        SecureAdapter verifyProvider = SecurityUtils.RSAVerifier(keyPair.getPublic());
        verifyProvider.append(randomString);
        this.logger.info("Verify_Result", "RSA", verifyProvider.verify(signBytes));
    }

    @Test
    @Order(50)
    public void SM2() throws CryptoException {
        KeyPair keyPair = SecurityUtils.SM2KeyPair();
        long currentTime = DateTimeUtils.currentTimeMillis();
        byte[] pkcs5 = CertificateUtils.PKCS12(keyPair, IDUtils.snowflake(),
                new Date(currentTime), new Date(currentTime + 30 * 24 * 60 * 60 * 1000L),
                "CERT", "CERT", "changeit", null, "SM3withSM2");
        this.logger.info("Certificate_Result", "SM2", StringUtils.base64Encode(pkcs5));
        X509Certificate x509Certificate =
                CertificateUtils.x509(pkcs5, "CERT", "changeit", keyPair.getPublic(), Boolean.TRUE);
        if (x509Certificate == null) {
            return;
        }
        PublicKey publicKey = x509Certificate.getPublicKey();
        SecureAdapter encryptProvider = SecurityUtils.SM2Encryptor(publicKey);
        //  Default mode: C1C2C3
        byte[] mode0Bytes = encryptProvider.finish(ORIGINAL_STRING);
        String mode0Result = StringUtils.base64Encode(mode0Bytes);
        byte[] mode1Bytes = SecurityUtils.C1C2C3toC1C3C2(mode0Bytes);
        String mode1Result = StringUtils.base64Encode(mode1Bytes);
        this.logger.info("SM2_Encrypt_Result", mode0Result, "C1C2C3");
        this.logger.info("SM2_Encrypt_Result", mode1Result, "C1C3C2");
        SecureAdapter decryptProvider = SecurityUtils.SM2Decryptor(keyPair.getPrivate());
        this.logger.info("SM2_Decrypt_Result",
                new String(decryptProvider.finish(StringUtils.base64Decode(mode0Result)), StandardCharsets.UTF_8), "C1C2C3");
        this.logger.info("SM2_Decrypt_Result",
                new String(decryptProvider.finish(SecurityUtils.C1C3C2toC1C2C3(StringUtils.base64Decode(mode1Result))),
                        StandardCharsets.UTF_8), "C1C3C2");
        String randomString = StringUtils.randomString(128);
        SecureAdapter signProvider = SecurityUtils.SM2Signer(keyPair.getPrivate());
        byte[] signBytes = signProvider.finish(randomString);
        this.logger.info("Signature_Result", randomString, "SM2", StringUtils.base64Encode(signBytes));
        SecureAdapter verifyProvider = SecurityUtils.SM2Verifier(publicKey);
        verifyProvider.append(randomString);
        this.logger.info("Verify_Result", "SM2", verifyProvider.verify(signBytes));
    }

    @Test
    @Order(60)
    public void RC2() throws CryptoException {
        byte[] rc2Key = SecurityUtils.RC2Key();
        this.logger.info("Crypto_Key_Length", "RC2", StringUtils.base64Encode(rc2Key));
        SecureAdapter encAdaptor = SecurityUtils.RC2Encryptor(rc2Key);
        String defaultResult = StringUtils.base64Encode(encAdaptor.finish(ORIGINAL_STRING));
        this.logger.info("Encrypt_Result", "RC2", "CBC", "PKCS7Padding", defaultResult);
        SecureAdapter decAdaptor = SecurityUtils.RC2Decryptor(rc2Key);
        this.logger.info("Decrypt_Result", "RC2", "CBC", "PKCS7Padding",
                new String(decAdaptor.finish(StringUtils.base64Decode(defaultResult)), StandardCharsets.UTF_8));
        for (String cipherMode : RC_CIPHER_MODES) {
            for (String padding : DEFAULT_PADDINGS) {
                SecureAdapter encryptProvider = SecurityUtils.RC2Encryptor(cipherMode, padding, rc2Key);
                String encResult = StringUtils.base64Encode(encryptProvider.finish(ORIGINAL_STRING));
                this.logger.info("Encrypt_Result", "RC2", cipherMode, padding, encResult);
                SecureAdapter decryptProvider = SecurityUtils.RC2Decryptor(cipherMode, padding, rc2Key);
                this.logger.info("Decrypt_Result", "RC2", cipherMode, padding,
                        new String(decryptProvider.finish(StringUtils.base64Decode(encResult)), StandardCharsets.UTF_8));
            }
        }
    }

    @Test
    @Order(70)
    public void RC4() throws CryptoException {
        byte[] rc4Key = SecurityUtils.RC4Key();
        this.logger.info("Crypto_Key_Length", "RC4", StringUtils.base64Encode(rc4Key));
        SecureAdapter encryptProvider = SecurityUtils.RC4Encryptor(rc4Key);
        String encResult = StringUtils.base64Encode(encryptProvider.finish(ORIGINAL_STRING));
        this.logger.info("Encrypt_Result", "RC4", "", "", encResult);
        SecureAdapter decryptProvider = SecurityUtils.RC4Decryptor(rc4Key);
        this.logger.info("Decrypt_Result", "RC4", "", "",
                new String(decryptProvider.finish(StringUtils.base64Decode(encResult)), StandardCharsets.UTF_8));
    }

    @Test
    @Order(80)
    public void RC5() throws CryptoException {
        byte[] rc5Key = SecurityUtils.RC5Key();
        this.logger.info("Crypto_Key_Length", "RC5", StringUtils.base64Encode(rc5Key));
        SecureAdapter encAdaptor = SecurityUtils.RC5Encryptor(rc5Key);
        String defaultResult = StringUtils.base64Encode(encAdaptor.finish(ORIGINAL_STRING));
        this.logger.info("Encrypt_Result", "RC5", "CBC", "PKCS5Padding", defaultResult);
        SecureAdapter decAdaptor = SecurityUtils.RC5Decryptor(rc5Key);
        this.logger.info("Decrypt_Result", "RC5", "CBC", "PKCS5Padding",
                new String(decAdaptor.finish(StringUtils.base64Decode(defaultResult)), StandardCharsets.UTF_8));
        for (String cipherMode : RC_CIPHER_MODES) {
            for (String padding : DEFAULT_PADDINGS) {
                SecureAdapter encryptProvider = SecurityUtils.RC5Encryptor(cipherMode, padding, rc5Key);
                String encResult = StringUtils.base64Encode(encryptProvider.finish(ORIGINAL_STRING));
                this.logger.info("Encrypt_Result", "RC5", cipherMode, padding, encResult);
                SecureAdapter decryptProvider = SecurityUtils.RC5Decryptor(cipherMode, padding, rc5Key);
                this.logger.info("Decrypt_Result", "RC5", cipherMode, padding,
                        new String(decryptProvider.finish(StringUtils.base64Decode(encResult)), StandardCharsets.UTF_8));
            }
        }
    }

    @Test
    @Order(90)
    public void RC6() throws CryptoException {
        byte[] rc6Key = SecurityUtils.RC6Key();
        this.logger.info("Crypto_Key_Length", "RC6", StringUtils.base64Encode(rc6Key));
        SecureAdapter encAdaptor = SecurityUtils.RC6Encryptor(rc6Key);
        String defaultResult = StringUtils.base64Encode(encAdaptor.finish(ORIGINAL_STRING));
        this.logger.info("Encrypt_Result", "RC6", "CBC", "PKCS5Padding", defaultResult);
        SecureAdapter decAdaptor = SecurityUtils.RC6Decryptor(rc6Key);
        this.logger.info("Decrypt_Result", "RC6", "CBC", "PKCS5Padding",
                new String(decAdaptor.finish(StringUtils.base64Decode(defaultResult)), StandardCharsets.UTF_8));
        for (String cipherMode : RC_CIPHER_MODES) {
            for (String padding : DEFAULT_PADDINGS) {
                SecureAdapter encryptProvider = SecurityUtils.RC6Encryptor(cipherMode, padding, rc6Key);
                String encResult = StringUtils.base64Encode(encryptProvider.finish(ORIGINAL_STRING));
                this.logger.info("Encrypt_Result", "RC6", cipherMode, padding, encResult);
                SecureAdapter decryptProvider = SecurityUtils.RC6Decryptor(cipherMode, padding, rc6Key);
                this.logger.info("Decrypt_Result", "RC6", cipherMode, padding,
                        new String(decryptProvider.finish(StringUtils.base64Decode(encResult)), StandardCharsets.UTF_8));
            }
        }
    }

    @Test
    @Order(100)
    public void Blowfish() throws CryptoException {
        byte[] blowfishKey = SecurityUtils.BlowfishKey();
        this.logger.info("Crypto_Key_Length", "Blowfish", StringUtils.base64Encode(blowfishKey));
        SecureAdapter encAdaptor = SecurityUtils.BlowfishEncryptor(blowfishKey);
        String defaultResult = StringUtils.base64Encode(encAdaptor.finish(ORIGINAL_STRING));
        this.logger.info("Encrypt_Result", "Blowfish", "CBC", "PKCS7Padding", defaultResult);
        SecureAdapter decAdaptor = SecurityUtils.BlowfishDecryptor(blowfishKey);
        this.logger.info("Decrypt_Result", "Blowfish", "CBC", "PKCS7Padding",
                new String(decAdaptor.finish(StringUtils.base64Decode(defaultResult)), StandardCharsets.UTF_8));
        for (String cipherMode : RC_CIPHER_MODES) {
            for (String padding : DEFAULT_PADDINGS) {
                SecureAdapter encryptProvider = SecurityUtils.BlowfishEncryptor(cipherMode, padding, blowfishKey);
                String encResult = StringUtils.base64Encode(encryptProvider.finish(ORIGINAL_STRING));
                this.logger.info("Encrypt_Result", "Blowfish", cipherMode, padding, encResult);
                SecureAdapter decryptProvider = SecurityUtils.BlowfishDecryptor(cipherMode, padding, blowfishKey);
                this.logger.info("Decrypt_Result", "Blowfish", cipherMode, padding,
                        new String(decryptProvider.finish(StringUtils.base64Decode(encResult)), StandardCharsets.UTF_8));
            }
        }
    }
}
