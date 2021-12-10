package org.nervousync.security.factory.config;

import jakarta.xml.bind.annotation.*;
import org.nervousync.beans.core.BeanObject;
import org.nervousync.commons.core.Globals;

/**
 * The type Factory config.
 */
@XmlRootElement(name = "factory-config")
@XmlAccessorType(XmlAccessType.NONE)
public final class FactoryConfig extends BeanObject {

    private static final long serialVersionUID = 9205620114979633475L;

    @XmlElement(name = "last-modify")
    private long lastModify = Globals.DEFAULT_VALUE_LONG;
    @XmlElement(name = "factory-algorithm")
    private String factoryAlgorithm = "AES256";
    @XmlElement(name = "factory-key")
    private String factoryKey = "";

    /**
     * Instantiates a new Factory config.
     */
    public FactoryConfig() {
    }

    /**
     * Gets last modify.
     *
     * @return the last modify
     */
    public long getLastModify() {
        return lastModify;
    }

    /**
     * Sets last modify.
     *
     * @param lastModify the last modify
     */
    public void setLastModify(long lastModify) {
        this.lastModify = lastModify;
    }

    /**
     * Gets factory algorithm.
     *
     * @return the factory algorithm
     */
    public String getFactoryAlgorithm() {
        return factoryAlgorithm;
    }

    /**
     * Sets factory algorithm.
     *
     * @param factoryAlgorithm the factory algorithm
     */
    public void setFactoryAlgorithm(String factoryAlgorithm) {
        this.factoryAlgorithm = factoryAlgorithm;
    }

    /**
     * Gets factory key.
     *
     * @return the factory key
     */
    public String getFactoryKey() {
        return factoryKey;
    }

    /**
     * Sets factory key.
     *
     * @param factoryKey the factory key
     */
    public void setFactoryKey(String factoryKey) {
        this.factoryKey = factoryKey;
    }
}
