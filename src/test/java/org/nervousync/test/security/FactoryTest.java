package org.nervousync.test.security;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.nervousync.security.factory.SecureConfig;
import org.nervousync.security.factory.SecureFactory;
import org.nervousync.test.BaseTest;
import org.nervousync.utils.StringUtils;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public final class FactoryTest extends BaseTest {

    @Test
    public void test000Schema() {
        SecureFactory.initConfig(SecureFactory.SecureAlgorithm.AES256)
                .ifPresent(secureConfig -> {
                    SecureConfig parseConfig =
                            StringUtils.xmlToObject(secureConfig.toXML(Boolean.TRUE), SecureConfig.class,
                                    "https://nervousync.org/schemas/secure");
                    this.logger.info("Parsed config: {}", parseConfig.toXML(Boolean.TRUE));
                });
    }

    @Test
    public void test010Initialize() {
        SecureFactory.initConfig(SecureFactory.SecureAlgorithm.RSA1024)
                .ifPresent(secureConfig ->
                        this.logger.info("Initialize result: {}", SecureFactory.initialize(secureConfig)));
    }

    @Test
    public void test020Config() {
        SecureFactory.initConfig(SecureFactory.SecureAlgorithm.AES128)
                .ifPresent(secureConfig -> this.logger.info("Config result: {}",
                        SecureFactory.getInstance().register("TestConfig", secureConfig)));
    }

    @Test
    public void test030Crypto() {
        String encResult = SecureFactory.getInstance().encrypt("TestConfig", "TestString中文测试");
        this.logger.info("Encrypt result: {}", encResult);
        this.logger.info("Decrypt result: {}", SecureFactory.getInstance().decrypt("TestConfig", encResult));
    }
}
