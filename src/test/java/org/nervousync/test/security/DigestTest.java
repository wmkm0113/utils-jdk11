package org.nervousync.test.security;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.nervousync.security.SecureProvider;
import org.nervousync.exceptions.crypto.CryptoException;
import org.nervousync.test.BaseTest;
import org.nervousync.utils.ConvertUtils;
import org.nervousync.utils.SecurityUtils;

public final class DigestTest extends BaseTest {

    @Test
    @Order(0)
    public void CRC() throws CryptoException {
        for (String algorithm : SecurityUtils.registeredCRC()) {
            SecureProvider secureProvider = SecurityUtils.CRC(algorithm);
            this.logger.info("CRC algorithm: {}, value: {}", algorithm,
                    SecureProvider.CRCResult(algorithm, secureProvider.finish("123456")));
        }
    }

    @Test
    @Deprecated
    @Order(10)
    public void MD5() throws CryptoException {
        SecureProvider secureProvider = SecurityUtils.MD5();
        this.logger.info("MD5 value: {} (Provider)", ConvertUtils.byteToHex(secureProvider.finish("123456")));
        this.logger.info("MD5 value: {} (Static)", ConvertUtils.byteToHex(SecurityUtils.MD5("123456")));
        this.logger.info("HmacMD5 key: 110421, value: {} (Provider)",
                ConvertUtils.byteToHex(SecurityUtils.HmacMD5("110421".getBytes()).finish("123456")));
        this.logger.info("HmacMD5 key: 110421, value: {}",
                ConvertUtils.byteToHex(SecurityUtils.HmacMD5("110421".getBytes(), "123456")));
    }

    @Test
    @Deprecated
    @Order(20)
    public void SHA1() throws CryptoException {
        SecureProvider secureProvider = SecurityUtils.SHA1();
        this.logger.info("SHA1 value: {} (Provider)", ConvertUtils.byteToHex(secureProvider.finish("123456")));
        this.logger.info("SHA1 value: {} (Static)", ConvertUtils.byteToHex(SecurityUtils.SHA1("123456")));
    }

    @Test
    @Order(21)
    public void HmacSHA1() throws CryptoException {
        this.logger.info("HmacSHA1 key: 110421, value: {} (Provider)",
                ConvertUtils.byteToHex(SecurityUtils.HmacSHA1("110421".getBytes()).finish("123456")));
        this.logger.info("HmacSHA1 key: 110421, value: {} (Static)",
                ConvertUtils.byteToHex(SecurityUtils.HmacSHA1("110421".getBytes(), "123456")));
    }

    @Test
    @Order(30)
    public void SHA224() throws CryptoException {
        SecureProvider secureProvider = SecurityUtils.SHA224();
        this.logger.info("SHA-224 value: {} (Provider)", ConvertUtils.byteToHex(secureProvider.finish("123456")));
        this.logger.info("SHA-224 value: {} (Static)", ConvertUtils.byteToHex(SecurityUtils.SHA224("123456")));
        this.logger.info("HmacSHA224 key: 110421, value: {} (Provider)",
                ConvertUtils.byteToHex(SecurityUtils.HmacSHA224("110421".getBytes()).finish("123456")));
        this.logger.info("HmacSHA224 key: 110421, value: {} (Static)",
                ConvertUtils.byteToHex(SecurityUtils.HmacSHA224("110421".getBytes(),"123456")));
    }

    @Test
    @Order(31)
    public void SHA256() throws CryptoException {
        SecureProvider secureProvider = SecurityUtils.SHA256();
        this.logger.info("SHA-256 value: {} (Provider)", ConvertUtils.byteToHex(secureProvider.finish("123456")));
        this.logger.info("SHA-256 value: {} (Static)", ConvertUtils.byteToHex(SecurityUtils.SHA256("123456")));
        this.logger.info("HmacSHA256 key: 110421, value: {} (Provider)",
                ConvertUtils.byteToHex(SecurityUtils.HmacSHA256("110421".getBytes()).finish("123456")));
        this.logger.info("HmacSHA256 key: 110421, value: {} (Static)",
                ConvertUtils.byteToHex(SecurityUtils.HmacSHA256("110421".getBytes(),"123456")));
    }

    @Test
    @Order(32)
    public void SHA384() throws CryptoException {
        SecureProvider secureProvider = SecurityUtils.SHA384();
        this.logger.info("SHA-384 value: {} (Provider)", ConvertUtils.byteToHex(secureProvider.finish("123456")));
        this.logger.info("SHA-384 value: {} (Static)", ConvertUtils.byteToHex(SecurityUtils.SHA384("123456")));
        this.logger.info("HmacSHA384 key: 110421, value: {} (Provider)",
                ConvertUtils.byteToHex(SecurityUtils.HmacSHA384("110421".getBytes()).finish("123456")));
        this.logger.info("HmacSHA384 key: 110421, value: {} (Static)",
                ConvertUtils.byteToHex(SecurityUtils.HmacSHA384("110421".getBytes(),"123456")));
    }

    @Test
    @Order(33)
    public void SHA512() throws CryptoException {
        SecureProvider secureProvider = SecurityUtils.SHA512();
        this.logger.info("SHA-512 value: {} (Provider)", ConvertUtils.byteToHex(secureProvider.finish("123456")));
        this.logger.info("SHA-512 value: {} (Static)", ConvertUtils.byteToHex(SecurityUtils.SHA512("123456")));
        this.logger.info("HmacSHA512 key: 110421, value: {} (Provider)",
                ConvertUtils.byteToHex(SecurityUtils.HmacSHA512("110421".getBytes()).finish("123456")));
        this.logger.info("HmacSHA512 key: 110421, value: {} (Static)",
                ConvertUtils.byteToHex(SecurityUtils.HmacSHA512("110421".getBytes(), "123456")));
    }

    @Test
    @Order(34)
    public void SHA512_224() throws CryptoException {
        SecureProvider secureProvider = SecurityUtils.SHA512_224();
        this.logger.info("SHA-512/224 value: {} (Provider)", ConvertUtils.byteToHex(secureProvider.finish("123456")));
        this.logger.info("SHA-512/224 value: {} (Static)", ConvertUtils.byteToHex(SecurityUtils.SHA512_224("123456")));
        this.logger.info("HmacSHA512/224 key: 110421, value: {} (Provider)",
                ConvertUtils.byteToHex(SecurityUtils.HmacSHA512_224("110421".getBytes()).finish("123456")));
        this.logger.info("HmacSHA512/224 key: 110421, value: {} (Static)",
                ConvertUtils.byteToHex(SecurityUtils.HmacSHA512_224("110421".getBytes(), "123456")));
    }

    @Test
    @Order(35)
    public void SHA512_256() throws CryptoException {
        SecureProvider secureProvider = SecurityUtils.SHA512_256();
        this.logger.info("SHA-512/256 value: {} (Provider)", ConvertUtils.byteToHex(secureProvider.finish("123456")));
        this.logger.info("SHA-512/256 value: {} (Static)", ConvertUtils.byteToHex(SecurityUtils.SHA512_256("123456")));
        this.logger.info("HmacSHA512/256 key: 110421, value: {} (Provider)",
                ConvertUtils.byteToHex(SecurityUtils.HmacSHA512_256("110421".getBytes()).finish("123456")));
        this.logger.info("HmacSHA512/256 key: 110421, value: {} (Static)",
                ConvertUtils.byteToHex(SecurityUtils.HmacSHA512_256("110421".getBytes(), "123456")));
    }

    @Test
    @Order(40)
    public void SHA3_224() throws CryptoException {
        SecureProvider secureProvider = SecurityUtils.SHA3_224();
        this.logger.info("SHA3-224 value: {} (Provider)", ConvertUtils.byteToHex(secureProvider.finish("123456")));
        this.logger.info("SHA3-224 value: {} (Static)", ConvertUtils.byteToHex(SecurityUtils.SHA3_224("123456")));
        this.logger.info("HmacSHA3-224 key: 110421, value: {} (Provider)",
                ConvertUtils.byteToHex(SecurityUtils.HmacSHA3_224("110421".getBytes()).finish("123456")));
        this.logger.info("HmacSHA3-224 key: 110421, value: {} (Static)",
                ConvertUtils.byteToHex(SecurityUtils.HmacSHA3_224("110421".getBytes(), "123456")));
    }

    @Test
    @Order(41)
    public void SHA3_256() throws CryptoException {
        SecureProvider secureProvider = SecurityUtils.SHA3_256();
        this.logger.info("SHA3-256 value: {} (Provider)", ConvertUtils.byteToHex(secureProvider.finish("123456")));
        this.logger.info("SHA3-256 value: {} (Static)", ConvertUtils.byteToHex(SecurityUtils.SHA3_256("123456")));
        this.logger.info("HmacSHA3-256 key: 110421, value: {} (Provider)",
                ConvertUtils.byteToHex(SecurityUtils.HmacSHA3_256("110421".getBytes()).finish("123456")));
        this.logger.info("HmacSHA3-256 key: 110421, value: {} (Static)",
                ConvertUtils.byteToHex(SecurityUtils.HmacSHA3_256("110421".getBytes(), "123456")));
    }

    @Test
    @Order(42)
    public void SHA3_384() throws CryptoException {
        SecureProvider secureProvider = SecurityUtils.SHA3_384();
        this.logger.info("SHA3-384 value: {} (Provider)", ConvertUtils.byteToHex(secureProvider.finish("123456")));
        this.logger.info("SHA3-384 value: {} (Static)", ConvertUtils.byteToHex(SecurityUtils.SHA3_384("123456")));
        this.logger.info("HmacSHA3-384 key: 110421, value: {} (Provider)",
                ConvertUtils.byteToHex(SecurityUtils.HmacSHA3_384("110421".getBytes()).finish("123456")));
        this.logger.info("HmacSHA3-384 key: 110421, value: {} (Static)",
                ConvertUtils.byteToHex(SecurityUtils.HmacSHA3_384("110421".getBytes(), "123456")));
    }

    @Test
    @Order(43)
    public void SHA3_512() throws CryptoException {
        SecureProvider secureProvider = SecurityUtils.SHA3_512();
        this.logger.info("SHA3-512 value: {} (Provider)", ConvertUtils.byteToHex(secureProvider.finish("123456")));
        this.logger.info("SHA3-512 value: {} (Static)", ConvertUtils.byteToHex(SecurityUtils.SHA3_512("123456")));
        this.logger.info("HmacSHA3-512 key: 110421, value: {} (Provider)",
                ConvertUtils.byteToHex(SecurityUtils.HmacSHA3_512("110421".getBytes()).finish("123456")));
        this.logger.info("HmacSHA3-512 key: 110421, value: {} (Static)",
                ConvertUtils.byteToHex(SecurityUtils.HmacSHA3_512("110421".getBytes(), "123456")));
    }

    @Test
    @Order(44)
    public void SHAKE128() throws CryptoException {
        SecureProvider secureProvider = SecurityUtils.SHAKE128();
        this.logger.info("SHAKE128 value: {} (Provider)", ConvertUtils.byteToHex(secureProvider.finish("123456")));
        this.logger.info("SHAKE128 value: {} (Static)", ConvertUtils.byteToHex(SecurityUtils.SHAKE128("123456")));
    }

    @Test
    @Order(45)
    public void SHAKE256() throws CryptoException {
        SecureProvider secureProvider = SecurityUtils.SHAKE256();
        this.logger.info("SHAKE256 value: {} (Provider)", ConvertUtils.byteToHex(secureProvider.finish("123456")));
        this.logger.info("SHAKE256 value: {} (Static)", ConvertUtils.byteToHex(SecurityUtils.SHAKE256("123456")));
    }

    @Test
    @Order(50)
    public void SM3() throws CryptoException {
        SecureProvider secureProvider = SecurityUtils.SM3();
        this.logger.info("SM3 value: {} (Provider)", ConvertUtils.byteToHex(secureProvider.finish("123456")));
        this.logger.info("SM3 value: {} (Static)", ConvertUtils.byteToHex(SecurityUtils.SM3("123456")));
        this.logger.info("HmacSM3 key: 110421, value: {} (Provider)",
                ConvertUtils.byteToHex(SecurityUtils.HmacSM3("110421".getBytes()).finish("123456")));
        this.logger.info("HmacSM3 key: 110421, value: {} (Static)",
                ConvertUtils.byteToHex(SecurityUtils.HmacSM3("110421".getBytes(), "123456")));
    }
}
