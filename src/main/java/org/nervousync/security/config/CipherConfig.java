package org.nervousync.security.config;

/**
 * The type Cipher config.
 */
public final class CipherConfig {

    /**
     * Cipher Algorithm
     */
    private final String algorithm;
    /**
     * Cipher Mode
     */
    private final String mode;
    /**
     * Padding Mode
     */
    private final String padding;

    /**
     * Instantiates a new Cipher mode.
     *
     * @param algorithm the algorithm
     * @param mode      the mode
     * @param padding   the padding
     */
    public CipherConfig(String algorithm, String mode, String padding) {
        this.algorithm = algorithm;
        this.mode = mode;
        this.padding = padding;
    }

    /**
     * Gets algorithm.
     *
     * @return the algorithm
     */
    public String getAlgorithm() {
        return algorithm;
    }

    /**
     * Gets mode.
     *
     * @return the mode
     */
    public String getMode() {
        return mode;
    }

    /**
     * Gets padding.
     *
     * @return the padding
     */
    public String getPadding() {
        return padding;
    }

    public String toString() {
        return String.join("/", this.algorithm, this.mode, this.padding);
    }
}
