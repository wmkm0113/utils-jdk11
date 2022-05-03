package org.nervousync.utils;

import org.nervousync.annotations.generator.GeneratorProvider;
import org.nervousync.commons.core.Globals;
import org.nervousync.generator.IGenerator;
import org.nervousync.generator.nano.NanoGenerator;
import org.nervousync.generator.snowflake.SnowflakeGenerator;
import org.nervousync.generator.uuid.impl.UUIDv2Generator;
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
    private static final Map<String, IGenerator> INITIALIZE_MAP = new HashMap<>();

    static {
        //  Using Java SPI to loading ID generator implement classes
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
    }

    /**
     * Nano generator generator.
     *
     * @param alphabetConfig the alphabet config
     * @param generateLength generate length
     * @return the generator
     */
    public static IGenerator nanoGenerator(final String alphabetConfig, final int generateLength) {
        NanoGenerator nanoGenerator = new NanoGenerator();
        nanoGenerator.config(alphabetConfig, generateLength);
        return nanoGenerator;
    }

    /**
     * Snowflake generator generator.
     *
     * @param referenceTime the reference time
     * @param deviceId      the device id
     * @param instanceId    the instance id
     * @return the generator
     */
    public static IGenerator snowflakeGenerator(final long referenceTime, final long deviceId, final long instanceId) {
        SnowflakeGenerator snowflakeGenerator = new SnowflakeGenerator();
        snowflakeGenerator.config(referenceTime, deviceId, instanceId);
        return snowflakeGenerator;
    }

    /**
     * Uui dv 2 generator generator.
     *
     * @param synchronizer the synchronizer
     * @return the generator
     */
    public static IGenerator UUIDv2Generator(final TimeSynchronizer synchronizer) {
        UUIDv2Generator generator = new UUIDv2Generator();
        generator.config(synchronizer);
        return generator;
    }

    /**
     * Generate ID
     *
     * @param generatorName Generator name for who will be used
     * @return Generated ID
     */
    public static Object random(final String generatorName) {
        return Optional.ofNullable(INITIALIZE_MAP.get(generatorName))
                .map(IGenerator::random)
                .orElse(Globals.DEFAULT_VALUE_STRING);
    }

    /**
     * Generate ID using given parameter, using for UUID version3 and version5
     *
     * @param generatorName Generator name for who will be used
     * @param dataBytes     Random parameter
     * @return Generated ID
     */
    public static Object random(final String generatorName, final byte[] dataBytes) {
        return Optional.ofNullable(INITIALIZE_MAP.get(generatorName))
                .map(iGenerator -> iGenerator.random(dataBytes))
                .orElse(Globals.DEFAULT_VALUE_STRING);
    }

    /**
     * Read registered generator name list
     *
     * @return Registered generator name list
     */
    public static List<String> registeredGenerators() {
        return new ArrayList<>(INITIALIZE_MAP.keySet());
    }
}
