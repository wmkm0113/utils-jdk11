package org.nervousync.test.security;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.nervousync.security.factory.SecureConfig;
import org.nervousync.security.factory.SecureFactory;
import org.nervousync.test.BaseTest;
import org.nervousync.utils.StringUtils;

public final class FactoryTest extends BaseTest {

    @Test
    @Order(0)
    public void schema() {
        SecureFactory.initConfig(SecureFactory.SecureAlgorithm.AES256)
                .ifPresent(secureConfig -> {
                    SecureConfig parseConfig =
                            StringUtils.stringToObject(secureConfig.toXML(Boolean.TRUE), SecureConfig.class,
                                    "https://nervousync.org/schemas/secure");
                    this.logger.info("Parsed config: {}", parseConfig.toXML(Boolean.TRUE));
                });
    }

    @Test
    @Order(10)
    public void initialize() {
        SecureFactory.initConfig(SecureFactory.SecureAlgorithm.RSA1024)
                .ifPresent(secureConfig ->
                        this.logger.info("Initialize result: {}", SecureFactory.initialize(secureConfig)));
    }

    @Test
    @Order(20)
    public void config() {
        SecureFactory.initConfig(SecureFactory.SecureAlgorithm.AES128)
                .ifPresent(secureConfig -> this.logger.info("Config result: {}",
                        SecureFactory.getInstance().register("TestConfig", secureConfig)));
    }

    @Test
    @Order(30)
    public void crypto() {
        String encResult = SecureFactory.getInstance().encrypt("TestConfig", "TestString中文测试");
        this.logger.info("Encrypt result: {}", encResult);
        this.logger.info("Decrypt result: {}", SecureFactory.getInstance().decrypt("TestConfig", encResult));
    }
}
