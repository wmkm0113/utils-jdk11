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
package org.nervousync.generator.uuid.impl;

import org.nervousync.annotations.generator.GeneratorProvider;
import org.nervousync.generator.uuid.UUIDGenerator;
import org.nervousync.generator.uuid.timer.TimeSynchronizer;
import org.nervousync.utils.IDUtils;
import org.nervousync.utils.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * The type Uui dv 2 generator.
 */
@GeneratorProvider(IDUtils.UUIDv2)
public final class UUIDv2Generator extends UUIDGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(UUIDv2Generator.class);

    private final UUIDTimer uuidTimer;

    /**
     * Instantiates a new Uui dv 2 generator.
     */
    public UUIDv2Generator() {
        this.uuidTimer = new UUIDTimer();
    }

    /**
     * Config.
     *
     * @param synchronizer the synchronizer
     */
    public void config(final TimeSynchronizer synchronizer) {
        this.uuidTimer.config(synchronizer);
    }

    @Override
    public String random() {
        return new UUID(super.highBits(this.uuidTimer.getTimestamp()), this.lowBits(SystemUtils.localMac())).toString();
    }

    @Override
    public String random(byte[] dataBytes) {
        return this.random();
    }

    @Override
    protected long lowBits(byte[] dataBytes) {
        if (dataBytes.length != 6) {
            throw new IllegalArgumentException("Illegal offset, need room for 6 bytes");
        }
        long address = dataBytes[0] & 255;

        for (int i = 1; i < 6; ++i) {
            address = address << 8 | (long) (dataBytes[i] & 255);
        }

        int i = (int) (address >> 32);
        byte[] uuidBytes = new byte[16];
        int pos = 10;
        uuidBytes[pos++] = (byte) (i >> 8);
        uuidBytes[pos++] = (byte) i;
        i = (int) address;
        uuidBytes[pos++] = (byte) (i >> 24);
        uuidBytes[pos++] = (byte) (i >> 16);
        uuidBytes[pos++] = (byte) (i >> 8);
        uuidBytes[pos] = (byte) i;

        int sequence = uuidTimer.clockSequence();
        uuidBytes[8] = (byte) (sequence >> 8);
        uuidBytes[9] = (byte) sequence;

        long lowBits = (convertToLong(uuidBytes, 8) << 32) | (convertToLong(uuidBytes, 12) << 32 >>> 32);
        lowBits = lowBits << 2 >>> 2;
        lowBits |= -9223372036854775808L;
        return lowBits;
    }

    @Override
    public void destroy() {
        this.uuidTimer.destroy();
    }

    private static long convertToLong(byte[] buffer, int offset) {
        return buffer[offset] << 24 | (buffer[offset + 1] & 255) << 16
                | (buffer[offset + 2] & 255) << 8 | buffer[offset + 3] & 255;
    }

    private static final class UUIDTimer {

        private TimeSynchronizer synchronizer;
        private Random random;
        private int sequence;
        private long systemTimestamp;
        private long usedTimestamp;
        private long unsafeTimestamp;
        private int counter = 0;
        private static final int MAX_WAIT_COUNT = 50;

        /**
         * Instantiates a new Uuid timer.
         */
        public UUIDTimer() {
            this.config(null);
        }

        /**
         * Config.
         *
         * @param synchronizer the synchronizer
         */
        public void config(final TimeSynchronizer synchronizer) {
            if (this.synchronizer == null || !this.synchronizer.equals(synchronizer)) {
                this.synchronizer = synchronizer;
            }
            this.init();
        }

        private void init() {
            this.random = new Random(System.currentTimeMillis());
            this.initCounters();
            this.systemTimestamp = 0L;
            this.usedTimestamp = 0L;
            if (this.synchronizer != null) {
                long initTimestamp = this.synchronizer.initialize();
                if (initTimestamp > this.usedTimestamp) {
                    this.usedTimestamp = initTimestamp;
                }
            }
            this.unsafeTimestamp = 0L;
        }

        /**
         * Clock sequence int.
         *
         * @return the int
         */
        public int clockSequence() {
            return this.sequence & '\uFFFF';
        }

        /**
         * Gets timestamp.
         *
         * @return the timestamp
         */
        public synchronized long getTimestamp() {
            long currentTimeMillis = System.currentTimeMillis();
            if (currentTimeMillis < this.systemTimestamp) {
                LOGGER.warn("System time going backwards! (got value {}, last {}",
                        currentTimeMillis, this.systemTimestamp);
                this.systemTimestamp = currentTimeMillis;
            }

            if (currentTimeMillis <= this.usedTimestamp) {
                if (this.counter < 10000) {
                    currentTimeMillis = this.usedTimestamp;
                } else {
                    long actDiff = this.usedTimestamp - currentTimeMillis;
                    long origTime = currentTimeMillis;
                    currentTimeMillis = this.usedTimestamp + 1L;
                    LOGGER.warn("Timestamp over-run: need to reinitialize random sequence");
                    this.initCounters();
                    if (actDiff >= 100L) {
                        slowDown(origTime, actDiff);
                    }
                }
            } else {
                this.counter &= 255;
            }

            this.usedTimestamp = currentTimeMillis;
            if (this.synchronizer != null && currentTimeMillis >= this.unsafeTimestamp) {
                this.unsafeTimestamp = this.synchronizer.update(currentTimeMillis);
            }

            currentTimeMillis *= 10000L;
            currentTimeMillis += 122192928000000000L;
            currentTimeMillis += this.counter;
            ++this.counter;
            return currentTimeMillis;
        }

        private void initCounters() {
            this.sequence = this.random.nextInt();
            this.counter = this.sequence >> 16 & 255;
        }

        private void slowDown(long startTime, long actDiff) {
            long ratio = actDiff / 100L;
            long delayMillis;
            if (ratio < 2L) {
                delayMillis = 1L;
            } else if (ratio < 10L) {
                delayMillis = 2L;
            } else if (ratio < 600L) {
                delayMillis = 3L;
            } else {
                delayMillis = 5L;
            }

            LOGGER.warn("Need to wait for {} milliseconds; virtual clock advanced too far in the future", delayMillis);
            long timeOutMillis = startTime + delayMillis;
            int counter = 0;

            while (counter <= MAX_WAIT_COUNT && System.currentTimeMillis() < timeOutMillis) {
                try {
                    TimeUnit.MILLISECONDS.sleep(delayMillis);
                } catch (InterruptedException ignored) {
                }
                delayMillis = 1L;
                counter++;
            }
        }

        void destroy() {
            if (this.synchronizer != null) {
                this.synchronizer.deactivate();
            }
        }
    }
}
