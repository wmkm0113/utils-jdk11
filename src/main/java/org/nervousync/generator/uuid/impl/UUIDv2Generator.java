/*
 * Copyright 2021 Nervousync Studio
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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
import org.nervousync.generator.uuid.timer.UUIDTimer;
import org.nervousync.utils.IDUtils;
import org.nervousync.utils.SystemUtils;

import java.util.UUID;

/**
 * The type Uui dv 2 generator.
 */
@GeneratorProvider(IDUtils.UUIDv2)
public final class UUIDv2Generator extends UUIDGenerator {

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
    public Object random() {
        return new UUID(super.highBits(this.uuidTimer.getTimestamp()), this.lowBits(SystemUtils.localMac())).toString();
    }

    @Override
    public Object random(byte[] dataBytes) {
        return this.random();
    }

    @Override
    protected long lowBits(byte[] dataBytes) {
        if (dataBytes.length != 6) {
            throw new IllegalArgumentException("Illegal offset, need room for 6 bytes");
        }
        long address = dataBytes[0] & 255;

        for(int i = 1; i < 6; ++i) {
            address = address << 8 | (long)(dataBytes[i] & 255);
        }

        int i = (int)(address >> 32);
        byte[] uuidBytes = new byte[16];
        int pos = 10;
        uuidBytes[pos++] = (byte)(i >> 8);
        uuidBytes[pos++] = (byte)i;
        i = (int)address;
        uuidBytes[pos++] = (byte)(i >> 24);
        uuidBytes[pos++] = (byte)(i >> 16);
        uuidBytes[pos++] = (byte)(i >> 8);
        uuidBytes[pos] = (byte)i;

        int sequence = uuidTimer.clockSequence();
        uuidBytes[8] = (byte)(sequence >> 8);
        uuidBytes[9] = (byte)sequence;

        long lowBits = (convertToLong(uuidBytes, 8) << 32) | (convertToLong(uuidBytes, 12) << 32 >>> 32);
        lowBits = lowBits << 2 >>> 2;
        lowBits |= -9223372036854775808L;
        return lowBits;
    }

    private static long convertToLong(byte[] buffer, int offset) {
        return buffer[offset] << 24 | (buffer[offset + 1] & 255) << 16
                | (buffer[offset + 2] & 255) << 8 | buffer[offset + 3] & 255;
    }
}
