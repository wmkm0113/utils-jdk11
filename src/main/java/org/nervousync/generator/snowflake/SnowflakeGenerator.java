package org.nervousync.generator.snowflake;

import org.nervousync.annotations.generator.GeneratorProvider;
import org.nervousync.commons.core.Globals;
import org.nervousync.generator.IGenerator;
import org.nervousync.utils.DateTimeUtils;
import org.nervousync.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GeneratorProvider("Snowflake")
public final class SnowflakeGenerator implements IGenerator {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Default reference time, default is 2011-04-21 00:00 CST
     */
    private static final long REFERENCE_TIME = 1303315200000L;
    /**
     * Sequence mask code, sequence id bits: 12
     */
    private static final long SEQUENCE_MASK = ~(-1L << 12L);
    /**
     * Node identified id (between 0 and 63), default: 1L
     */
    private long deviceId;
    /**
     * Node instance id (between 0 and 63), default: 1L
     */
    private long instanceId;
    /**
     * Begin timestamp value
     */
    private long referenceTime = REFERENCE_TIME;
    /**
     * Sequence index in millisecond
     */
    private long sequenceIndex;
    /**
     * Previous generate time
     */
    private long lastTime = Globals.DEFAULT_VALUE_LONG;

    public static final String REFERENCE_CONFIG = "org.nervousync.snowflake.ReferenceTime";
    public static final String DEVICE_CONFIG = "org.nervousync.snowflake.DeviceID";
    public static final String INSTANCE_CONFIG = "org.nervousync.snowflake.InstanceID";

    @Override
    public void initialize() {
        this.referenceTime =
                StringUtils.matches(System.getProperty(REFERENCE_CONFIG), "^\\d{1,}$")
                        ? Long.parseLong(System.getProperty(REFERENCE_CONFIG)) : REFERENCE_TIME;
        if (StringUtils.matches(System.getProperty(DEVICE_CONFIG), "^\\d{1,2}$")) {
            long deviceId = Long.parseLong(System.getProperty(DEVICE_CONFIG));
            this.deviceId = (deviceId >= 0L && deviceId <= 64L) ? deviceId : 1L;
        } else {
            this.deviceId = 1L;
        }
        if (StringUtils.matches(System.getProperty(INSTANCE_CONFIG), "^\\d{1,2}$")) {
            long instanceId = Long.parseLong(System.getProperty(INSTANCE_CONFIG));
            this.instanceId = (instanceId >= 0L && instanceId <= 64L) ? instanceId : 1L;
        } else {
            this.instanceId = 1L;
        }
        this.sequenceIndex = 0L;
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Snowflake config: reference time: {}, deviceId: {}, instanceId: {}",
                    this.referenceTime, this.deviceId, this.instanceId);
        }
    }

    @Override
    public Object random() {
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
    public Object random(byte[] dataBytes) {
        return this.random();
    }
}
