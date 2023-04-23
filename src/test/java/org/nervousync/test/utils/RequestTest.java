package org.nervousync.test.utils;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.nervousync.test.BaseTest;
import org.nervousync.utils.RequestUtils;

import java.util.Optional;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public final class RequestTest extends BaseTest {

    @Test
    public void test000Method() {
        this.logger.info("Http method: {}", RequestUtils.httpMethodOption("get"));
        this.logger.info("Http method: {}", RequestUtils.httpMethodOption("post"));
        this.logger.info("Http method: {}", RequestUtils.httpMethodOption("put"));
        this.logger.info("Http method: {}", RequestUtils.httpMethodOption("trace"));
        this.logger.info("Http method: {}", RequestUtils.httpMethodOption("head"));
        this.logger.info("Http method: {}", RequestUtils.httpMethodOption("delete"));
        this.logger.info("Http method: {}", RequestUtils.httpMethodOption("options"));
    }

    @Test
    public void test010DNS() {
        this.logger.info("Resolve www.baidu.com ip addresses: {}", RequestUtils.resolveDomain("www.baidu.com"));
    }

    @Test
    public void test020SSL() {
        Optional.ofNullable(RequestUtils.serverCertificate("https://www.baidu.com"))
                .ifPresent(certificate -> this.logger.info("Read certificate: {}", certificate));
    }

    @Test
    public void test030ContentLength() {
        this.logger.info("Read content length: {}", RequestUtils.contentLength("https://www.baidu.com"));
    }
}
