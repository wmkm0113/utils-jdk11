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
 * The type Crc config.
 */
public final class CRCConfig {

    //  CRC bit
    private final int bit;
    //  CRC polynomial
    private final long polynomial;
    //  CRC initialize value
    private final long init;
    //  CRC XOR out value
    private final long xorOut;
    //  CRC output data length
    private final int outLength;
    //  Reverse data bytes
    private final boolean refIn;
    //  Reverse CRC result before final XOR
    private final boolean refOut;

    /**
     * Instantiates a new Crc config.
     *
     * @param bit        the bit
     * @param polynomial the polynomial
     * @param init       the init
     * @param xorOut     the xor out
     * @param refIn      the ref in
     * @param refOut     the ref out
     */
    public CRCConfig(int bit, long polynomial, long init, long xorOut, boolean refIn, boolean refOut) {
        this.bit = bit;
        this.polynomial = polynomial;
        this.init = init;
        this.xorOut = xorOut;
        this.outLength = (bit % 4 != 0) ? ((bit / 4) + 1) : (bit / 4);
        this.refIn = refIn;
        this.refOut = refOut;
    }

    /**
     * Gets the value of bit
     *
     * @return the value of bit
     */
    public int getBit() {
        return bit;
    }

    /**
     * Gets the value of polynomial
     *
     * @return the value of polynomial
     */
    public long getPolynomial() {
        return polynomial;
    }

    /**
     * Gets the value of init
     *
     * @return the value of init
     */
    public long getInit() {
        return init;
    }

    /**
     * Gets the value of xorOut
     *
     * @return the value of xorOut
     */
    public long getXorOut() {
        return xorOut;
    }

    /**
     * Gets the value of outLength
     *
     * @return the value of outLength
     */
    public int getOutLength() {
        return outLength;
    }

    /**
     * Gets the value of refIn
     *
     * @return the value of refIn
     */
    public boolean isRefIn() {
        return refIn;
    }

    /**
     * Gets the value of refOut
     *
     * @return the value of refOut
     */
    public boolean isRefOut() {
        return refOut;
    }
}
