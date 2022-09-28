package org.nervousync.test;

import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseTest {

    protected transient final Logger logger = LoggerFactory.getLogger(this.getClass());

    static {
        BasicConfigurator.configure();
    }
}
