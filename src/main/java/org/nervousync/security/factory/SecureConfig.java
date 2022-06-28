/*
 * Copyright © 2003 Nervousync® Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Nervousync Studio.
 */

package org.nervousync.security.factory;

import org.nervousync.beans.core.BeanObject;

import jakarta.xml.bind.annotation.*;

/**
 * The type Crypto config.
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: 12/12/2020 11:05 PM $
 */
@XmlType(name = "secure_config", namespace = "https://nervousync.org/schemas/secure")
@XmlRootElement(name = "secure_config", namespace = "https://nervousync.org/schemas/secure")
@XmlAccessorType(XmlAccessType.NONE)
public final class SecureConfig extends BeanObject {

    /**
     * Secure using algorithm
     */
    @XmlElement(name = "secure_algorithm")
    private SecureFactory.SecureAlgorithm secureAlgorithm = null;
    /**
     * Secure initialize key
     */
    @XmlElement(name = "secure_key")
    private String secureKey = null;

    /**
     * Instantiates a new Secure Config.
     */
    public SecureConfig() {
    }

    /**
     * Gets secure algorithm.
     *
     * @return the secure algorithm
     */
    public SecureFactory.SecureAlgorithm getSecureAlgorithm() {
        return secureAlgorithm;
    }

    /**
     * Sets secure algorithm.
     *
     * @param secureAlgorithm the secure algorithm
     */
    public void setSecureAlgorithm(SecureFactory.SecureAlgorithm secureAlgorithm) {
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
