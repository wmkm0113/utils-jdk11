package org.nervousync.test.generator;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.nervousync.test.BaseTest;
import org.nervousync.utils.DateTimeUtils;
import org.nervousync.utils.IDUtils;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public final class GeneratorTest extends BaseTest {

    @Test
    public void test000Nano() {
        this.logger.info("Nano random: {}", IDUtils.nano());
        IDUtils.nanoConfig("abcdefghijklmnopqrstuvwxyz".toUpperCase(), 16);
        this.logger.info("Nano reconfigure random: {}", IDUtils.nano());
    }

    @Test
    public void test010Snowflake() throws Exception {
        this.logger.info("Snowflake random: {}", IDUtils.snowflake());
        IDUtils.snowflakeConfig(DateTimeUtils.parseDate("20030421", "yyyyMMdd").getTime(), 2L, 5L);
        this.logger.info("Snowflake reconfigure random: {}", IDUtils.snowflake());
    }

    @Test
    public void test020UUID() {
        this.logger.info("UUID version 1 random: {}", IDUtils.UUIDv1());
        this.logger.info("UUID version 2 random: {}", IDUtils.UUIDv2());
        this.logger.info("UUID version 3 random: {}", IDUtils.UUIDv3("TestVersion3".getBytes()));
        this.logger.info("UUID version 4 random: {}", IDUtils.UUIDv4());
        this.logger.info("UUID version 5 random: {}", IDUtils.UUIDv5("TestVersion5".getBytes()));
    }

}
