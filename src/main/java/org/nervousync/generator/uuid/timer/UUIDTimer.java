package org.nervousync.generator.uuid.timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public final class UUIDTimer {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private TimeSynchronizer synchronizer;
    private Random random;
    private int sequence;
    private long systemTimestamp;
    private long usedTimestamp;
    private long unsafeTimestamp;
    private int counter = 0;
    private static final int MAX_WAIT_COUNT = 50;

    public UUIDTimer() {
        this.config(null);
    }

    public void config(final TimeSynchronizer synchronizer) {
        if (this.synchronizer == null || !this.synchronizer.equals(synchronizer)) {
            this.synchronizer = synchronizer;
            this.init();
        }
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

    public int clockSequence() {
        return this.sequence & '\uFFFF';
    }

    public synchronized long getTimestamp() {
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis < this.systemTimestamp) {
            this.logger.warn("System time going backwards! (got value {}, last {}",
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
                this.logger.warn("Timestamp over-run: need to reinitialize random sequence");
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

        this.logger.warn("Need to wait for {} milliseconds; virtual clock advanced too far in the future", delayMillis);
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
