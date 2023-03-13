/*
 * Licensed to the Nervousync Studio (NSYC) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
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
package org.nervousync.generator.snowflake;

import org.nervousync.annotations.generator.GeneratorProvider;
import org.nervousync.commons.core.Globals;
import org.nervousync.generator.IGenerator;
import org.nervousync.utils.DateTimeUtils;
import org.nervousync.utils.IDUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The type Snowflake generator.
 */
@GeneratorProvider(IDUtils.SNOWFLAKE)
public final class SnowflakeGenerator implements IGenerator<Long> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Default ID
     */
    private static final long DEFAULT_ID = 1L;
    /**
     * Sequence mask code, sequence id bits: 12
     */
    private static final long SEQUENCE_MASK = ~(-1L << 12L);
    /**
     * Node identified id (between 0 and 63), default: 1L
     */
    private long deviceId = DEFAULT_ID;
    /**
     * Node instance id (between 0 and 63), default: 1L
     */
    private long instanceId = DEFAULT_ID;
    /**
     * Begin timestamp value
     */
    private long referenceTime = Globals.DEFAULT_REFERENCE_TIME;
    /**
     * Sequence index in millisecond
     */
    private long sequenceIndex = 0L;
    /**
     * Previous generate time
     */
    private long lastTime = Globals.DEFAULT_VALUE_LONG;

    /**
     * Config.
     *
     * @param referenceTime the reference time
     * @param deviceId      the device id
     * @param instanceId    the instance id
     */
    public void config(final long referenceTime, final long deviceId, final long instanceId) {
        this.referenceTime = (referenceTime >= 0L) ? referenceTime : Globals.DEFAULT_REFERENCE_TIME;
        this.deviceId = (deviceId >= 0L && deviceId <= 64L) ? deviceId : DEFAULT_ID;
        this.instanceId = (instanceId >= 0L && instanceId <= 64L) ? instanceId : DEFAULT_ID;
        this.sequenceIndex = 0L;
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Snowflake config: reference time: {}, deviceId: {}, instanceId: {}",
                    this.referenceTime, this.deviceId, this.instanceId);
        }
    }

    @Override
    public Long random() {
        long currentTime = DateTimeUtils.currentUTCTimeMillis();
        if (currentTime < this.lastTime) {
            throw new RuntimeException(
                    String.format("System clock moved backwards. Refusing to generate id for %d milliseconds",
                            this.lastTime - currentTime));
        }

        if (currentTime == this.lastTime) {
            this.sequenceIndex = (this.sequenceIndex + 1) & SEQUENCE_MASK;
            if (this.sequenceIndex == 0) {
                while (true) {
                    if ((currentTime = DateTimeUtils.currentUTCTimeMillis()) > this.lastTime) {
                        break;
                    }
                }
            }
        } else {
            this.sequenceIndex = 0L;
        }
        this.lastTime = currentTime;

        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Last time: {}, reference time: {}, Device ID: {}, instanceId: {}, sequenceIndex: {}",
                    this.lastTime, this.referenceTime, this.deviceId, this.instanceId, this.sequenceIndex);
        }

        return ((this.lastTime - this.referenceTime) << 22L)
                | (this.deviceId << 17L) | (this.instanceId << 12L) | this.sequenceIndex;
    }

    @Override
    public Long random(byte[] dataBytes) {
        return this.random();
    }

    @Override
    public void destroy() {
    }
}
