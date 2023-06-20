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
package org.nervousync.security;

import org.nervousync.exceptions.crypto.CryptoException;
import org.nervousync.utils.RawUtils;
import org.nervousync.utils.SecurityUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

/**
 * The type Secure provider.
 */
public abstract class SecureProvider {

    /**
     * Append.
     *
     * @param strIn the str in
     * @throws CryptoException the crypto exception
     */
    public final void append(String strIn) throws CryptoException {
        this.append(strIn.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Append.
     *
     * @param dataBytes the data bytes
     * @throws CryptoException the crypto exception
     */
    public final void append(byte[] dataBytes) throws CryptoException {
        this.append(dataBytes, 0, dataBytes.length);
    }

    /**
     * Append.
     *
     * @param inBuffer the in buffer
     * @throws CryptoException the crypto exception
     */
    public final void append(ByteBuffer inBuffer) throws CryptoException {
        this.append(inBuffer.array());
    }

    /**
     * Finish byte [ ].
     *
     * @return the byte [ ]
     * @throws CryptoException the crypto exception
     */
    public final byte[] finish() throws CryptoException {
        return this.finish(new byte[0], 0, 0);
    }

    /**
     * Finish byte [ ].
     *
     * @param strIn the str in
     * @return the byte [ ]
     * @throws CryptoException the crypto exception
     */
    public final byte[] finish(String strIn) throws CryptoException {
        return this.finish(strIn.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Finish byte [ ].
     *
     * @param dataBytes the data bytes
     * @return the byte [ ]
     * @throws CryptoException the crypto exception
     */
    public final byte[] finish(byte[] dataBytes) throws CryptoException {
        return this.finish(dataBytes, 0, dataBytes.length);
    }

    /**
     * Finish byte [ ].
     *
     * @param inBuffer the in buffer
     * @return the byte [ ]
     * @throws CryptoException the crypto exception
     */
    public final byte[] finish(ByteBuffer inBuffer) throws CryptoException {
        return this.finish(inBuffer.array());
    }

    /**
     * Append.
     *
     * @param dataBytes the data bytes
     * @param position  the position
     * @param length    the length
     * @throws CryptoException the crypto exception
     */
    public abstract void append(byte[] dataBytes, int position, int length) throws CryptoException;

    /**
     * Finish byte [ ].
     *
     * @param dataBytes the data bytes
     * @param position  the position
     * @param length    the length
     * @return the byte [ ]
     * @throws CryptoException the crypto exception
     */
    public abstract byte[] finish(byte[] dataBytes, int position, int length) throws CryptoException;

    /**
     * Verify boolean.
     *
     * @param signature the signature
     * @return the boolean
     * @throws CryptoException the crypto exception
     */
    public abstract boolean verify(byte[] signature) throws CryptoException;

    /**
     * Reset.
     *
     * @throws CryptoException the crypto exception
     */
    public abstract void reset() throws CryptoException;

    /**
     * Convert crc result from byte arrays to string
     *
     * @param algorithm CRC algorithm
     * @param result    CRC result byte array
     * @return Converted result
     * @throws CryptoException CRC algorithm didn't find
     */
    public static String CRCResult(String algorithm, byte[] result) throws CryptoException {
        return SecurityUtils.crcConfig(algorithm)
                .map(crcConfig -> {
                    long crc = RawUtils.readLong(result, ByteOrder.LITTLE_ENDIAN);
                    StringBuilder stringBuilder = new StringBuilder(Long.toString(crc, 16));
                    while (stringBuilder.length() < crcConfig.getOutLength()) {
                        stringBuilder.insert(0, "0");
                    }
                    return "0x" + stringBuilder;
                })
                .orElseThrow(() -> new CryptoException("Unknown algorithm: " + algorithm));
    }
}
