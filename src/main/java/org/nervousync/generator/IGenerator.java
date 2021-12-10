package org.nervousync.generator;

public interface IGenerator {

    void initialize();

    Object random();

    Object random(byte[] dataBytes);

}
