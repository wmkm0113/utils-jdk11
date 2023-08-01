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
                    this.logger.info("Secure_Config", parseConfig.toXML(Boolean.TRUE));
                });
    }

    @Test
    @Order(10)
    public void initialize() {
        SecureFactory.initConfig(SecureFactory.SecureAlgorithm.RSA1024)
                .ifPresent(secureConfig ->
                        this.logger.info("Secure_Init_Result", SecureFactory.initialize(secureConfig)));
    }

    @Test
    @Order(20)
    public void config() {
        SecureFactory.initConfig(SecureFactory.SecureAlgorithm.AES128)
                .ifPresent(secureConfig -> this.logger.info("Secure_Init_Result",
                        SecureFactory.register("TestConfig", secureConfig)));
    }

    @Test
    @Order(30)
    public void crypto() {
        String encResult = SecureFactory.encrypt("TestConfig", "TestString中文测试");
        this.logger.info("Secure_Encrypt_Result", encResult);
        this.logger.info("Secure_Decrypt_Result", SecureFactory.decrypt("TestConfig", encResult));
    }
}
