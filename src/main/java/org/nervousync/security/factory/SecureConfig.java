/*
 * Licensed to the Nervousync Studio (NSYC) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
