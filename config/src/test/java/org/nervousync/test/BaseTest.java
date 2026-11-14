/*
 * Licensed to the Nervousync Studio (NSYC) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.nervousync.test;

import org.junit.jupiter.api.*;
import org.nervousync.commons.Globals;
import org.nervousync.enumerations.logger.LogLevel;
import org.nervousync.utils.logger.LoggerUtils;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public abstract class BaseTest {

    protected transient final LoggerUtils.Logger logger = LoggerUtils.getLogger(this.getClass());

    static {
        LoggerUtils.initLoggerConfigure(LogLevel.DEBUG);
    }

    protected BaseTest() {
    }

    @BeforeEach
    public final void init(final TestInfo testInfo) {
        this.logger.info("Execute_Begin_Test",
                testInfo.getTestClass().map(Class::getName).orElse(Globals.DEFAULT_VALUE_STRING), testInfo.getDisplayName());
    }

    @AfterEach
    public void print(TestInfo testInfo) {
        this.logger.info("Execute_End_Test",
                testInfo.getTestClass().map(Class::getName).orElse(Globals.DEFAULT_VALUE_STRING), testInfo.getDisplayName());
    }
}
