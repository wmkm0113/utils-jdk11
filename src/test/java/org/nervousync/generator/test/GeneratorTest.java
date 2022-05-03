package org.nervousync.generator.test;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.nervousync.generator.IGenerator;
import org.nervousync.utils.DateTimeUtils;
import org.nervousync.utils.IDUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public final class GeneratorTest {

    private transient final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    public void test000Nano() {
        this.logger.info("Nano random: {}", IDUtils.random("NanoID"));
        IGenerator nanoGenerator = IDUtils.nanoGenerator("abcdefghijklmnopqrstuvwxyz".toUpperCase(), 16);
        this.logger.info("Nano reconfigure random: {}", nanoGenerator.random());
    }

    @Test
    public void test010Snowflake() throws Exception {
        this.logger.info("Snowflake random: {}", IDUtils.random("Snowflake"));
        IGenerator snowflakeGenerator = IDUtils.snowflakeGenerator(DateTimeUtils.parseDate("20030421", "yyyyMMdd").getTime(), 2L, 5L);
        this.logger.info("Snowflake reconfigure random: {}", snowflakeGenerator.random());
    }

    @Test
    public void test020UUID() {
        this.logger.info("UUID version 1 random: {}", IDUtils.random("UUIDv1"));
        this.logger.info("UUID version 2 random: {}", IDUtils.random("UUIDv2"));
        this.logger.info("UUID version 3 random: {}", IDUtils.random("UUIDv3", "TestVersion3".getBytes()));
        this.logger.info("UUID version 4 random: {}", IDUtils.random("UUIDv4"));
        this.logger.info("UUID version 5 random: {}", IDUtils.random("UUIDv5", "TestVersion5".getBytes()));
    }

}
