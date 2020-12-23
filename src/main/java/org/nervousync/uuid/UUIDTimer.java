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
package org.nervousync.uuid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: 12/21/2020 4:13 PM $
 */
public final class UUIDTimer {

	private static final Logger LOGGER = LoggerFactory.getLogger(UUIDTimer.class);

	protected final TimerSynchronizer synchronizer;
	protected final Random random;
	private int sequence;
	private long systemTimestamp;
	private long usedTimestamp;
	private long unsafeTimestamp;
	private int counter = 0;
	private static final int MAX_WAIT_COUNT = 50;

	public UUIDTimer(TimerSynchronizer synchronizer) {
		this.random = new Random(System.currentTimeMillis());
		this.synchronizer = synchronizer;
		this.initCounters();
		this.systemTimestamp = 0L;
		this.usedTimestamp = 0L;
		if (synchronizer != null) {
			long initTimestamp = synchronizer.initialize();
			if (initTimestamp > this.usedTimestamp) {
				this.usedTimestamp = initTimestamp;
			}
		}

		this.unsafeTimestamp = 0L;
	}

	private void initCounters() {
		this.sequence = this.random.nextInt();
		this.counter = this.sequence >> 16 & 255;
	}

	public int clockSequence() {
		return this.sequence & '\uFFFF';
	}

	public synchronized long getTimestamp() {
		long currentTimeMillis = System.currentTimeMillis();
		if (currentTimeMillis < this.systemTimestamp) {
			LOGGER.warn("System time going backwards! (got value {}, last {}", currentTimeMillis, this.systemTimestamp);
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
}
