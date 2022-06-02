package org.nervousync.generator;

/**
 * The interface Generator.
 */
public interface IGenerator {

    /**
     * Random object.
     *
     * @return the object
     */
    Object random();

    /**
     * Random object.
     *
     * @param dataBytes the data bytes
     * @return the object
     */
    Object random(byte[] dataBytes);

}
