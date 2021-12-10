package org.nervousync.utils;

import org.nervousync.annotations.generator.GeneratorProvider;
import org.nervousync.commons.core.Globals;
import org.nervousync.generator.IGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public final class IDUtils {

    public static final String UUIDv1 = "UUIDv1";
    public static final String UUIDv2 = "UUIDv2";
    public static final String UUIDv3 = "UUIDv3";
    public static final String UUIDv4 = "UUIDv4";
    public static final String UUIDv5 = "UUIDv5";
    public static final String NANO_ID = "NanoID";
    public static final String SNOWFLAKE = "Snowflake";

    private transient static final Logger LOGGER = LoggerFactory.getLogger(IDUtils.class);
    private static final Map<String, String> GENERATOR_MAP = new HashMap<>();
    private static final Map<String, IGenerator> INITIALIZE_MAP = new HashMap<>();

    static {
        //  Using Java SPI to loading ID generator implement classes
        ServiceLoader.load(IGenerator.class)
                .forEach(iGenerator -> {
                    Class<?> generatorClass = iGenerator.getClass();
                    if (generatorClass.isAnnotationPresent(GeneratorProvider.class)) {
                        GENERATOR_MAP.put(generatorClass.getAnnotation(GeneratorProvider.class).value(),
                                generatorClass.getName());
                    }
                });
        if (LOGGER.isDebugEnabled()) {
            LOGGER.info("Registered generator names: {}",
                    String.join(", ", IDUtils.registeredGenerators().toArray(new String[0])));
        }
    }

    /**
     * Update all registered generator configure
     */
    public static void updateConfig() {
        INITIALIZE_MAP.values().forEach(IGenerator::initialize);
    }

    /**
     * Update target generator configure by given generator name
     * @param generatorName     Generator name for who will be reconfigured
     */
    public static void update(String generatorName) {
        if (GENERATOR_MAP.containsKey(generatorName)) {
            INITIALIZE_MAP.get(generatorName).initialize();
        }
    }

    /**
     * Generate ID
     * @param generatorName     Generator name for who will be used
     * @return                  Generated ID
     */
    public static Object random(String generatorName) {
        return retrieveGenerator(generatorName).map(IGenerator::random).orElse(Globals.DEFAULT_VALUE_STRING);
    }

    /**
     * Generate ID using given parameter, using for UUID version3 and version5
     *
     * @param generatorName     Generator name for who will be used
     * @param dataBytes         Random parameter
     * @return                  Generated ID
     */
    public static Object random(String generatorName, byte[] dataBytes) {
        return retrieveGenerator(generatorName)
                .map(iGenerator -> iGenerator.random(dataBytes))
                .orElse(Globals.DEFAULT_VALUE_STRING);
    }

    /**
     * Read registered generator name list
     * @return      Registered generator name list
     */
    public static List<String> registeredGenerators() {
        return new ArrayList<>(GENERATOR_MAP.keySet());
    }

    private static Optional<IGenerator> retrieveGenerator(String generatorName) {
        if (GENERATOR_MAP.containsKey(generatorName)) {
            IGenerator iGenerator;
            if (INITIALIZE_MAP.containsKey(generatorName)) {
                iGenerator = INITIALIZE_MAP.get(generatorName);
            } else {
                try {
                    iGenerator = (IGenerator) ObjectUtils.newInstance(GENERATOR_MAP.get(generatorName));
                    iGenerator.initialize();
                    INITIALIZE_MAP.put(generatorName, iGenerator);
                } catch (Exception e) {
                    iGenerator = null;
                }
            }
            return Optional.ofNullable(iGenerator);
        }
        return Optional.empty();
    }
}
