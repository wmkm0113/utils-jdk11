/*
 * Copyright © 2003 Nervousync® Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Nervousync Studio.
 */

package org.nervousync.security.factory.config;

import org.nervousync.beans.core.BeanObject;

import jakarta.xml.bind.annotation.*;
import org.nervousync.commons.core.Globals;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Crypto config.
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: 12/12/2020 11:05 PM $
 */
@XmlRootElement(name = "secure-config")
@XmlAccessorType(XmlAccessType.NONE)
public final class SecureConfig extends BeanObject {

    /**
     * The constant serialVersionUID.
     */
    private static final long serialVersionUID = -6218777997427252876L;

    @XmlElement(name = "last-modify")
    private long lastModify = Globals.DEFAULT_VALUE_LONG;
    @XmlElementWrapper(name = "config-items")
    @XmlElement(name = "config-item")
    private List<ConfigItem> configItemList = new ArrayList<>();

    /**
     * Instantiates a new Crypto config.
     */
    public SecureConfig() {
    }

    /**
     * Gets serial version uid.
     *
     * @return the serial version uid
     */
    public static long getSerialVersionUID() {
        return serialVersionUID;
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
     * Gets config item list.
     *
     * @return the config item list
     */
    public List<ConfigItem> getConfigItemList() {
        return configItemList;
    }

    /**
     * Sets config item list.
     *
     * @param configItemList the config item list
     */
    public void setConfigItemList(List<ConfigItem> configItemList) {
        this.configItemList = configItemList;
    }

    /**
     * The type Config item.
     */
    @XmlType(name = "config-item")
    @XmlRootElement(name = "config-item")
    @XmlAccessorType(XmlAccessType.NONE)
    public static final class ConfigItem extends BeanObject {

        private static final long serialVersionUID = -3488306441660131582L;

        /**
         * Config identify name
         */
        @XmlElement(name = "config-name")
        private String configName = null;
        /**
         * Secure using algorithm
         */
        @XmlElement(name = "secure-algorithm")
        private String secureAlgorithm = null;
        /**
         * Secure initialize key
         */
        @XmlElement(name = "secure-key")
        private String secureKey = null;

        /**
         * Instantiates a new Config item.
         */
        public ConfigItem() {
        }

        /**
         * Gets config name.
         *
         * @return the config name
         */
        public String getConfigName() {
            return configName;
        }

        /**
         * Sets config name.
         *
         * @param configName the config name
         */
        public void setConfigName(String configName) {
            this.configName = configName;
        }

        /**
         * Gets secure algorithm.
         *
         * @return the secure algorithm
         */
        public String getSecureAlgorithm() {
            return secureAlgorithm;
        }

        /**
         * Sets secure algorithm.
         *
         * @param secureAlgorithm the secure algorithm
         */
        public void setSecureAlgorithm(String secureAlgorithm) {
            this.secureAlgorithm = secureAlgorithm;
        }

        /**
         * Gets secure key.
         *
         * @return the secure key
         */
        public String getSecureKey() {
            return secureKey;
        }

        /**
         * Sets secure key.
         *
         * @param secureKey the secure key
         */
        public void setSecureKey(String secureKey) {
            this.secureKey = secureKey;
        }
    }
}
