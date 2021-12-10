package org.nervousync.enumerations.crypto;

import org.nervousync.commons.core.Globals;

public enum RandomAlgorithm {
    /**
     * None random algorithm.
     */
    NONE(Globals.DEFAULT_VALUE_STRING),
    /**
     * Native random algorithm.
     */
    NATIVE("NativePRNG"),
    /**
     * Native blocking random algorithm.
     */
    NATIVE_BLOCKING("NativePRNGBlocking"),
    /**
     * Native nonblocking random algorithm.
     */
    NATIVE_NONBLOCKING("NativePRNGNonBlocking"),
    /**
     * Pkcs 11 random algorithm.
     */
    PKCS11("PKCS11"),
    /**
     * Sha 1 prng random algorithm.
     */
    SHA1PRNG("SHA1PRNG"),
    /**
     * Windows random algorithm.
     */
    WINDOWS("Windows-PRNG");

    private final String algorithm;

    RandomAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    /**
     * Gets algorithm.
     *
     * @return the algorithm
     */
    public String getAlgorithm() {
        return algorithm;
    }
}
