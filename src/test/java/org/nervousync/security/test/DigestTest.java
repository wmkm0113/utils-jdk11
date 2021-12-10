package org.nervousync.security.test;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.nervousync.security.SecureProvider;
import org.nervousync.exceptions.crypto.CryptoException;
import org.nervousync.test.BaseTest;
import org.nervousync.utils.ConvertUtils;
import org.nervousync.utils.SecurityUtils;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public final class DigestTest extends BaseTest {

    @Test
    public void test000CRC() throws CryptoException {
        for (String algorithm : SecurityUtils.registeredCRC()) {
            SecureProvider secureProvider = SecurityUtils.CRC(algorithm);
            this.logger.info("CRC algorithm: {}, value: {}", algorithm,
                    SecureProvider.CRCResult(algorithm, secureProvider.finish("123456")));
        }
    }

    @Test
    public void test010MD5() throws CryptoException {
        SecureProvider secureProvider = SecurityUtils.MD5();
        this.logger.info("MD5 value: {}", ConvertUtils.byteToHex(secureProvider.finish("123456")));
        this.logger.info("HmacMD5 key: 110421, value: {}",
                ConvertUtils.byteToHex(SecurityUtils.HmacMD5("110421".getBytes()).finish("123456")));
    }

    @Test
    @Deprecated
    public void test020SHA1() throws CryptoException {
        SecureProvider secureProvider = SecurityUtils.SHA1();
        this.logger.info("SHA1 value: {}", ConvertUtils.byteToHex(secureProvider.finish("123456")));
    }

    @Test
    public void test021HmacSHA1() throws CryptoException {
        this.logger.info("HmacSHA1 key: 110421, value: {}",
                ConvertUtils.byteToHex(SecurityUtils.HmacSHA1("110421".getBytes()).finish("123456")));
    }

    @Test
    public void test030SHA224() throws CryptoException {
        SecureProvider secureProvider = SecurityUtils.SHA224();
        this.logger.info("SHA-224 value: {}", ConvertUtils.byteToHex(secureProvider.finish("123456")));
        this.logger.info("HmacSHA224 key: 110421, value: {}",
                ConvertUtils.byteToHex(SecurityUtils.HmacSHA224("110421".getBytes()).finish("123456")));
    }

    @Test
    public void test031SHA256() throws CryptoException {
        SecureProvider secureProvider = SecurityUtils.SHA256();
        this.logger.info("SHA-256 value: {}", ConvertUtils.byteToHex(secureProvider.finish("123456")));
        this.logger.info("HmacSHA256 key: 110421, value: {}",
                ConvertUtils.byteToHex(SecurityUtils.HmacSHA256("110421".getBytes()).finish("123456")));
    }

    @Test
    public void test032SHA384() throws CryptoException {
        SecureProvider secureProvider = SecurityUtils.SHA256();
        this.logger.info("SHA-384 value: {}", ConvertUtils.byteToHex(secureProvider.finish("123456")));
        this.logger.info("HmacSHA384 key: 110421, value: {}",
                ConvertUtils.byteToHex(SecurityUtils.HmacSHA384("110421".getBytes()).finish("123456")));
    }

    @Test
    public void test033SHA512() throws CryptoException {
        SecureProvider secureProvider = SecurityUtils.SHA512();
        this.logger.info("SHA-512 value: {}", ConvertUtils.byteToHex(secureProvider.finish("123456")));
        this.logger.info("HmacSHA512 key: 110421, value: {}",
                ConvertUtils.byteToHex(SecurityUtils.HmacSHA512("110421".getBytes()).finish("123456")));
    }

    @Test
    public void test034SHA512_224() throws CryptoException {
        SecureProvider secureProvider = SecurityUtils.SHA512_224();
        this.logger.info("SHA-512/224 value: {}", ConvertUtils.byteToHex(secureProvider.finish("123456")));
    }

    @Test
    public void test035SHA512_256() throws CryptoException {
        SecureProvider secureProvider = SecurityUtils.SHA512_256();
        this.logger.info("SHA-512/256 value: {}", ConvertUtils.byteToHex(secureProvider.finish("123456")));
    }

    @Test
    public void test040SHA3_224() throws CryptoException {
        SecureProvider secureProvider = SecurityUtils.SHA3_224();
        this.logger.info("SHA3-224 value: {}", ConvertUtils.byteToHex(secureProvider.finish("123456")));
    }

    @Test
    public void test041SHA3_256() throws CryptoException {
        SecureProvider secureProvider = SecurityUtils.SHA3_256();
        this.logger.info("SHA3-256 value: {}", ConvertUtils.byteToHex(secureProvider.finish("123456")));
    }

    @Test
    public void test042SHA3_384() throws CryptoException {
        SecureProvider secureProvider = SecurityUtils.SHA3_384();
        this.logger.info("SHA3-384 value: {}", ConvertUtils.byteToHex(secureProvider.finish("123456")));
    }

    @Test
    public void test043SHA3_512() throws CryptoException {
        SecureProvider secureProvider = SecurityUtils.SHA3_512();
        this.logger.info("SHA3-512 value: {}", ConvertUtils.byteToHex(secureProvider.finish("123456")));
    }

    @Test
    public void test044SHAKE128() throws CryptoException {
        SecureProvider secureProvider = SecurityUtils.SHAKE128();
        this.logger.info("SHAKE128 value: {}", ConvertUtils.byteToHex(secureProvider.finish("123456")));
    }

    @Test
    public void test045SHAKE256() throws CryptoException {
        SecureProvider secureProvider = SecurityUtils.SHAKE256();
        this.logger.info("SHAKE256 value: {}", ConvertUtils.byteToHex(secureProvider.finish("123456")));
    }

    @Test
    public void test050SM3() throws CryptoException {
        SecureProvider secureProvider = SecurityUtils.SM3();
        this.logger.info("SM3 value: {}", ConvertUtils.byteToHex(secureProvider.finish("123456")));
    }
}
