package org.nervousync.test;

import org.apache.logging.log4j.Level;
import org.junit.jupiter.api.*;
import org.nervousync.utils.LoggerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public abstract class BaseTest {

    protected transient final Logger logger = LoggerFactory.getLogger(this.getClass());

    static {
        LoggerUtils.initLoggerConfigure(Level.ERROR, LoggerUtils.newLogger("org.nervousync", Level.DEBUG));
    }

    @BeforeEach
    public final void init(final TestInfo testInfo) {
        this.logger.info("Starting execute method {}", testInfo.getDisplayName());
    }

    @AfterEach
    public void print(TestInfo testInfo) {
        this.logger.info("Execute method {} finished", testInfo.getDisplayName());
    }

}
