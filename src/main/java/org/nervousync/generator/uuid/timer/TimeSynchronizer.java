package org.nervousync.generator.uuid.timer;

public interface TimeSynchronizer {

    long initialize();

    void deactivate();

    long update(long currentTimeMillis);

}
