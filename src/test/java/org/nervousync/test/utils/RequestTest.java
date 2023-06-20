package org.nervousync.test.utils;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.nervousync.test.BaseTest;
import org.nervousync.utils.RequestUtils;

import java.util.Optional;

public final class RequestTest extends BaseTest {

    @Test
    @Order(0)
    public void httpMethod() {
        this.logger.info("Http method: {}", RequestUtils.httpMethodOption("get"));
        this.logger.info("Http method: {}", RequestUtils.httpMethodOption("post"));
        this.logger.info("Http method: {}", RequestUtils.httpMethodOption("put"));
        this.logger.info("Http method: {}", RequestUtils.httpMethodOption("trace"));
        this.logger.info("Http method: {}", RequestUtils.httpMethodOption("head"));
        this.logger.info("Http method: {}", RequestUtils.httpMethodOption("delete"));
        this.logger.info("Http method: {}", RequestUtils.httpMethodOption("options"));
    }

    @Test
    @Order(10)
    public void DNS() {
        this.logger.info("Resolve www.baidu.com ip addresses: {}", RequestUtils.resolveDomain("www.baidu.com"));
    }

    @Test
    @Order(20)
    public void SSL() {
        Optional.ofNullable(RequestUtils.serverCertificate("https://www.baidu.com"))
                .ifPresent(certificate -> this.logger.info("Read certificate: {}", certificate));
    }

    @Test
    @Order(30)
    public void contentLength() {
        this.logger.info("Read content length: {}", RequestUtils.contentLength("https://www.baidu.com"));
    }
}
