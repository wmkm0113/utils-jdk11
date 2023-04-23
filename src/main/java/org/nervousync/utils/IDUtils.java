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
package org.nervousync.utils;

import org.nervousync.annotations.generator.GeneratorProvider;
import org.nervousync.commons.core.Globals;
import org.nervousync.generator.IGenerator;
import org.nervousync.generator.nano.NanoGenerator;
import org.nervousync.generator.snowflake.SnowflakeGenerator;
import org.nervousync.generator.uuid.UUIDGenerator;
import org.nervousync.generator.uuid.impl.*;
import org.nervousync.generator.uuid.timer.TimeSynchronizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * The type ID utils.
 */
public final class IDUtils {

    /**
     * The constant UUIDv1.
     */
    public static final String UUIDv1 = "UUIDv1";
    /**
     * The constant UUIDv2.
     */
    public static final String UUIDv2 = "UUIDv2";
    /**
     * The constant UUIDv3.
     */
    public static final String UUIDv3 = "UUIDv3";
    /**
     * The constant UUIDv4.
     */
    public static final String UUIDv4 = "UUIDv4";
    /**
     * The constant UUIDv5.
     */
    public static final String UUIDv5 = "UUIDv5";
    /**
     * The constant NANO_ID.
     */
    public static final String NANO_ID = "NanoID";
    /**
     * The constant SNOWFLAKE.
     */
    public static final String SNOWFLAKE = "Snowflake";

    private static final Logger LOGGER = LoggerFactory.getLogger(IDUtils.class);
    private static final Map<String, IGenerator<?>> INITIALIZE_MAP = new HashMap<>();

    static {
        //  Using Java SPI to loading ID generator implements classes
        ServiceLoader.load(IGenerator.class)
                .forEach(iGenerator -> {
                    Class<?> generatorClass = iGenerator.getClass();
                    if (generatorClass.isAnnotationPresent(GeneratorProvider.class)) {
                        INITIALIZE_MAP.put(generatorClass.getAnnotation(GeneratorProvider.class).value(), iGenerator);
                    }
                });
        if (LOGGER.isDebugEnabled()) {
            LOGGER.info("Registered generator names: {}",
                    String.join(", ", IDUtils.registeredGenerators().toArray(new String[0])));
        }
        Runtime.getRuntime().addShutdownHook(new Thread(IDUtils::destroy));
    }

    public static void nanoConfig(final String alphabetConfig, final int generateLength) {
        if (INITIALIZE_MAP.containsKey(NANO_ID)) {
            synchronized (INITIALIZE_MAP) {
                NanoGenerator generator = (NanoGenerator) INITIALIZE_MAP.get(NANO_ID);
                generator.config(alphabetConfig, generateLength);
                INITIALIZE_MAP.put(NANO_ID, generator);
            }
        }
    }

    public static void snowflakeConfig(final long referenceTime, final long deviceId, final long instanceId) {
        if (INITIALIZE_MAP.containsKey(SNOWFLAKE)) {
            synchronized (INITIALIZE_MAP) {
                SnowflakeGenerator generator = (SnowflakeGenerator) INITIALIZE_MAP.get(SNOWFLAKE);
                generator.config(referenceTime, deviceId, instanceId);
                INITIALIZE_MAP.put(SNOWFLAKE, generator);
            }
        }
    }

    public static void uuidConfig(final TimeSynchronizer synchronizer) {
        if (INITIALIZE_MAP.containsKey(UUIDv2)) {
            synchronized (INITIALIZE_MAP) {
                UUIDv2Generator generator = (UUIDv2Generator) INITIALIZE_MAP.get(UUIDv2);
                generator.config(synchronizer);
                INITIALIZE_MAP.put(UUIDv2, generator);
            }
        }
    }

    public static String nano() {
        return Optional.ofNullable(INITIALIZE_MAP.get(NANO_ID))
                .map(generator -> ((NanoGenerator) generator).random())
                .orElse(Globals.DEFAULT_VALUE_STRING);
    }

    public static Long snowflake() {
        return Optional.ofNullable(INITIALIZE_MAP.get(SNOWFLAKE))
                .map(generator -> ((SnowflakeGenerator) generator).random())
                .orElse(Globals.DEFAULT_VALUE_LONG);
    }

    public static String UUIDv1() {
        return Optional.ofNullable(INITIALIZE_MAP.get(UUIDv1))
                .map(generator -> ((UUIDGenerator) generator).random())
                .orElse(Globals.DEFAULT_VALUE_STRING);
    }

    public static String UUIDv2() {
        return Optional.ofNullable(INITIALIZE_MAP.get(UUIDv2))
                .map(generator -> ((UUIDGenerator) generator).random())
                .orElse(Globals.DEFAULT_VALUE_STRING);
    }

    /**
     * Uuid version 3 generator generator.
     *
     * @return the generator
     */
    public static String UUIDv3(final byte[] dataBytes) {
        return Optional.ofNullable(INITIALIZE_MAP.get(UUIDv3))
                .map(generator -> ((UUIDGenerator) generator).random(dataBytes))
                .orElse(Globals.DEFAULT_VALUE_STRING);
    }

    /**
     * Uuid version 4 generator generator.
     *
     * @return the generator
     */
    public static String UUIDv4() {
        return Optional.ofNullable(INITIALIZE_MAP.get(UUIDv4))
                .map(generator -> ((UUIDGenerator) generator).random())
                .orElse(Globals.DEFAULT_VALUE_STRING);
    }

    /**
     * Uuid version 5 generator generator.
     *
     * @return the generator
     */
    public static String UUIDv5(final byte[] dataBytes) {
        return Optional.ofNullable(INITIALIZE_MAP.get(UUIDv5))
                .map(generator -> ((UUIDGenerator) generator).random(dataBytes))
                .orElse(Globals.DEFAULT_VALUE_STRING);
    }

    /**
     * Read the registered generator name list
     *
     * @return Registered generator name list
     */
    public static List<String> registeredGenerators() {
        return new ArrayList<>(INITIALIZE_MAP.keySet());
    }

    public static void destroy() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Destroy initialized generator instance...");
        }
        INITIALIZE_MAP.values().forEach(IGenerator::destroy);
        INITIALIZE_MAP.clear();
    }
}
