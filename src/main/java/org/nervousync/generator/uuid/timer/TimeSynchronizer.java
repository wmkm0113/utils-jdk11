package org.nervousync.generator.uuid.timer;

/**
 * The interface Time synchronizer.
 */
public interface TimeSynchronizer {

    /**
     * Initialize long.
     *
     * @return the long
     */
    long initialize();

    /**
     * Deactivate.
     */
    void deactivate();

    /**
     * Update long.
     *
     * @param currentTimeMillis the current time millis
     * @return the long
     */
    long update(long currentTimeMillis);

}
