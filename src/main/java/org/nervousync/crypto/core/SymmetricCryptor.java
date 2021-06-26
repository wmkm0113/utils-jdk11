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

package org.nervousync.crypto.core;

import org.nervousync.crypto.Cryptor;
import org.nervousync.utils.StringUtils;

import java.nio.charset.StandardCharsets;

/**
 * The type Symmetric cryptor.
 */
public abstract class SymmetricCryptor extends Cryptor {

    /**
     * Instantiates a new Symmetric cryptor.
     *
     * @param cipherMode the cipher mode
     * @param encodeType the encode type
     */
    protected SymmetricCryptor(CipherMode cipherMode, EncodeType encodeType) {
        super(cipherMode, encodeType);
    }

    /**
     * Encrypt byte [ ].
     *
     * @param dataBytes the data bytes
     * @param strKey    the str key
     * @return the byte [ ]
     */
    public final byte[] encrypt(byte[] dataBytes, String strKey) {
        return this.encrypt(dataBytes, super.decode(strKey));
    }

    /**
     * Encrypt string.
     *
     * @param strIn  the str in
     * @param strKey the str key
     * @return the string
     */
    public final String encrypt(String strIn, String strKey) {
        byte[] dataBytes = this.encrypt(strIn.getBytes(StandardCharsets.UTF_8), strKey);
        return StringUtils.base64Encode(dataBytes);
    }

    /**
     * Encrypt byte [ ].
     *
     * @param dataBytes the data bytes
     * @param keyBytes  the key bytes
     * @return the byte [ ]
     */
    public abstract byte[] encrypt(byte[] dataBytes, byte[] keyBytes);

    /**
     * Decrypt byte [ ].
     *
     * @param dataBytes the data bytes
     * @param strKey    the str key
     * @return the byte [ ]
     */
    public final byte[] decrypt(byte[] dataBytes, String strKey) {
        return this.decrypt(dataBytes, super.decode(strKey));
    }

    /**
     * Decrypt string.
     *
     * @param strIn  the str in
     * @param strKey the str key
     * @return the string
     */
    public final String decrypt(String strIn, String strKey) {
        byte[] dataBytes = this.decrypt(super.decode(strIn), super.decode(strKey));
        return new String(dataBytes, StandardCharsets.UTF_8);
    }

    /**
     * Decrypt byte [ ].
     *
     * @param dataBytes the data bytes
     * @param keyBytes  the key bytes
     * @return the byte [ ]
     */
    public abstract byte[] decrypt(byte[] dataBytes, byte[] keyBytes);

    /**
     * New key string.
     *
     * @return the string
     */
    public abstract String newKey();
}
