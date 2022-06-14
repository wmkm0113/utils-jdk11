package org.nervousync.test.security;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.nervousync.security.factory.SecureFactory;
import org.nervousync.test.BaseTest;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public final class FactoryTest extends BaseTest {

    @Test
    public void test000Initialize() {
        SecureFactory.initConfig(SecureFactory.SecureAlgorithm.RSA1024)
                .ifPresent(secureConfig ->
                        this.logger.info("Initialize result: {}", SecureFactory.initialize(secureConfig)));
    }

    @Test
    public void test010Config() {
        SecureFactory.initConfig(SecureFactory.SecureAlgorithm.AES128)
                .ifPresent(secureConfig -> this.logger.info("Config result: {}",
                        SecureFactory.getInstance().register("TestConfig", secureConfig)));
    }

    @Test
    public void test020Crypto() {
        String encResult = SecureFactory.getInstance().encrypt("TestConfig", "TestString中文测试");
        this.logger.info("Encrypt result: {}", encResult);
        this.logger.info("Decrypt result: {}", SecureFactory.getInstance().decrypt("TestConfig", encResult));
    }
}
