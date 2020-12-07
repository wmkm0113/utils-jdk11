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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.nervousync.commons.core.Globals;

/**
 * Snowflake Utility
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Jun 27, 2018 $
 */
public final class SnowflakeUtils {
	
	/**
	 * Instance object
	 */
	private static volatile SnowflakeUtils INSTANCE = null;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Default timestamp value, default is 2011-04-21 00:00 CST
	 */
	private static final long REFERENCE_TIME = 1303286400000L;
	/**
	 * Default instance id
	 */
	private static final long INSTANCE_ID = 1L;
	/**
	 * Sequence mask code, sequence id bits: 12
	 */
	private static final long SEQUENCE_MASK = ~(-1L << 12L);
	/**
	 * Node identified id (between 0 and 63)
	 */
	private static long DEVICE_ID;
	/**
	 * Node instance id (between 0 and 63), default: 1L
	 */
	private final long instanceId;
	/**
	 * Begin timestamp value
	 */
	private final long referenceTime;
	/**
	 * Sequence index in millisecond
	 */
	private long sequenceIndex;
	/**
	 * Previous generate time
	 */
	private long lastTime = Globals.DEFAULT_VALUE_LONG;
	
	static {
		DEVICE_ID = SystemUtils.identifiedKey().hashCode() % 64L;
		if (DEVICE_ID < 0L) {
			DEVICE_ID *= -1L;
		}
	}
	
	/**
	 * Constructor
	 * @param referenceTime		Reference time value
	 * @param instanceId		Instance id
	 */
	private SnowflakeUtils(long referenceTime, long instanceId) {
		if (logger.isDebugEnabled()) {
			logger.debug("DEVICE ID: {}", DEVICE_ID);
		}

		this.referenceTime = (referenceTime < 0L) ? REFERENCE_TIME : referenceTime;
		this.instanceId = (instanceId >= 0L && instanceId < 64L) ? instanceId : INSTANCE_ID;
		this.sequenceIndex = 0L;
	}
	
	/**
	 * Initialize by default reference time value
	 */
	public static void initialize() {
		initialize(Globals.DEFAULT_VALUE_LONG, Globals.DEFAULT_VALUE_LONG);
	}
	
	/**
	 * Initialize by given reference time
	 * @param referenceTime	reference time
	 * @param instanceId Instance id
	 */
	public static void initialize(long referenceTime, long instanceId) {
		if (INSTANCE == null) {
			synchronized (SnowflakeUtils.class) {
				if (INSTANCE == null) {
					setINSTANCE(new SnowflakeUtils(referenceTime, instanceId));
				}
			}
		}
	}

	private static void setINSTANCE(SnowflakeUtils snowflakeUtils) {
		INSTANCE = snowflakeUtils;
	}
	
	/**
	 * Get utility instance
	 * @return	Snowflake utils
	 */
	public static SnowflakeUtils getInstance() {
		if (SnowflakeUtils.INSTANCE == null) {
			SnowflakeUtils.initialize();
		}
		return SnowflakeUtils.INSTANCE;
	}
	
	/**
	 * Generate original snowflake id
	 * @return generated ID
	 */
	public long generateId() {
		return this.generateValue(true);
	}

	/**
	 * Generate id start with current date format as "yyyyMMdd"
	 * @param isUTC		Current day is UTC
	 * @return generated ID
	 */
	public long generateDateId(boolean isUTC) {
		if (INSTANCE == null) {
			SnowflakeUtils.initialize();
		}
		return INSTANCE.generateDateValue(isUTC);
	}

	/**
	 * Generate id start with current time format as "yyyyMMddHHmm"
	 * @param isUTC		Current time is UTC
	 * @return generated ID
	 */
	public long generateTimeId(boolean isUTC) {
		if (INSTANCE == null) {
			SnowflakeUtils.initialize();
		}
		return INSTANCE.generateTimeValue(isUTC);
	}
	
	private synchronized long generateDateValue(boolean isUTC) {
		return Long.parseLong(Integer.toString(isUTC ? DateTimeUtils.currentUTCDay() : DateTimeUtils.currentDay())
				+ this.generateValue(Globals.DEFAULT_VALUE_BOOLEAN));
	}
	
	private synchronized long generateTimeValue(boolean isUTC) {
		return Long.parseLong(Long.toString(isUTC ? DateTimeUtils.currentUTCTime() : DateTimeUtils.currentTime())
				+ this.generateValue(Globals.DEFAULT_VALUE_BOOLEAN));
	}
	
	private synchronized long generateValue(boolean calcTime) {
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
					this.lastTime, this.referenceTime, SnowflakeUtils.DEVICE_ID, this.instanceId, this.sequenceIndex);
		}
		
		if (calcTime) {
			return ((this.lastTime - this.referenceTime) << 22L) | (DEVICE_ID << 17L) | (this.instanceId << 12L) | this.sequenceIndex;
		} else {
			return (DEVICE_ID << 17L) | (this.instanceId << 12L) | this.sequenceIndex;
		}
	}

	private Object readResolve() {
		return SnowflakeUtils.INSTANCE;
	}
}
