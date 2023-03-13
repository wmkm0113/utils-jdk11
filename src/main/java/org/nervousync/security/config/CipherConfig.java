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
