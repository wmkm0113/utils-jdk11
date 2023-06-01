package org.nervousync.test;

import org.apache.logging.log4j.Level;
import org.nervousync.utils.LoggerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseTest {

    protected transient final Logger logger = LoggerFactory.getLogger(this.getClass());

    static {
        LoggerUtils.initLoggerConfigure(Level.ERROR, LoggerUtils.newLogger("org.nervousync", Level.DEBUG));
    }
}
