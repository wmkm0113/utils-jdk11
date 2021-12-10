package org.nervousync.generator.test;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.nervousync.generator.nano.NanoGenerator;
import org.nervousync.generator.snowflake.SnowflakeGenerator;
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
        System.setProperty(NanoGenerator.ALPHABET_CONFIG, "abcdefghijklmnopqrstuvwxyz".toUpperCase());
        System.setProperty(NanoGenerator.LENGTH_CONFIG, "16");
        IDUtils.updateConfig();
        this.logger.info("Nano reconfigure random: {}", IDUtils.random("NanoID"));
    }

    @Test
    public void test010Snowflake() throws Exception {
        this.logger.info("Snowflake random: {}", IDUtils.random("Snowflake"));
        System.setProperty(SnowflakeGenerator.REFERENCE_CONFIG,
                Long.toString(DateTimeUtils.parseDate("20030421", "yyyyMMdd").getTime()));
        System.setProperty(SnowflakeGenerator.DEVICE_CONFIG, "2");
        System.setProperty(SnowflakeGenerator.INSTANCE_CONFIG, "5");
        IDUtils.updateConfig();
        this.logger.info("Snowflake reconfigure random: {}", IDUtils.random("Snowflake"));
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
