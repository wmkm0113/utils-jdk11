package org.nervousync.security.test;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.nervousync.security.factory.SecureFactory;
import org.nervousync.test.BaseTest;
import org.nervousync.utils.SecurityUtils;
import org.nervousync.utils.StringUtils;

import java.nio.charset.StandardCharsets;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public final class FactoryTest extends BaseTest {

    @Test
    public void test000Initialize() {
        this.logger.info("Initialize result: {}", SecureFactory.initialize("D:\\secure", SecureFactory.SecureAlgorithm.RSA1024));
    }

    @Test
    public void test001Initialize() {
        this.logger.info("Initialize result: {}", SecureFactory.initialize("D:\\secure"));
    }

    @Test
    public void test010Config() {
        this.logger.info("Config result: {}", SecureFactory.config("TestConfig", SecureFactory.SecureAlgorithm.AES128));
    }

    @Test
    public void test011Config() {
        this.logger.info("Config result: {}", SecureFactory.config("TestConfig", SecureFactory.SecureAlgorithm.AES128, SecurityUtils.AES128Key()));
    }

    @Test
    public void test020Crypto() {
        String encResult = StringUtils.base64Encode(SecureFactory.encrypt("TestConfig", "TestString中文测试".getBytes(StandardCharsets.UTF_8)));
        this.logger.info("Encrypt result: {}", encResult);
        byte[] decryptResult = SecureFactory.decrypt("TestConfig", StringUtils.base64Decode(encResult));
        this.logger.info("Decrypt result: {}", decryptResult);
    }
}
