package org.nervousync.test.generator;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.nervousync.test.BaseTest;
import org.nervousync.utils.DateTimeUtils;
import org.nervousync.utils.IDUtils;

public final class GeneratorTest extends BaseTest {

    @Test
	@Order(0)
    public void nano() {
        this.logger.info("Nano_Random", IDUtils.nano());
        IDUtils.nanoConfig("abcdefghijklmnopqrstuvwxyz".toUpperCase(), 16);
        this.logger.info("Nano_Reconfigure_Random", IDUtils.nano());
    }

    @Test
	@Order(10)
    public void snowflake() throws Exception {
        this.logger.info("Snowflake_Random", IDUtils.snowflake());
        IDUtils.snowflakeConfig(DateTimeUtils.parseDate("20030421", "yyyyMMdd").getTime(), 2L, 5L);
        this.logger.info("Snowflake_Reconfigure_Random", IDUtils.snowflake());
    }

    @Test
	@Order(20)
    public void UUID() {
        this.logger.info("UUID_Random", 1, IDUtils.UUIDv1());
        this.logger.info("UUID_Random", 2, IDUtils.UUIDv2());
        this.logger.info("UUID_Random", 3, IDUtils.UUIDv3("TestVersion3".getBytes()));
        this.logger.info("UUID_Random", 4, IDUtils.UUIDv4());
        this.logger.info("UUID_Random", 5, IDUtils.UUIDv5("TestVersion5".getBytes()));
    }

}
