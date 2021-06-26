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

package org.nervousync.crypto.impl;

import org.nervousync.commons.core.Globals;
import org.nervousync.crypto.core.SymmetricCryptor;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;

public final class DESCryptor extends SymmetricCryptor {

    public DESCryptor(String algorithm, DESMode desMode, EncodeType encodeType) {
        super(new CipherMode(algorithm, desMode.getMode(), desMode.getPadding()), encodeType);
    }

    @Override
    public byte[] encrypt(byte[] dataBytes, byte[] keyBytes) {
        try {
            return encryptData(this.cipherMode, RandomAlgorithm.NONE, dataBytes,
                    null, keyBytes, Globals.DEFAULT_VALUE_INT);
        } catch (NoSuchAlgorithmException e) {
            return new byte[0];
        }
    }

    @Override
    public byte[] decrypt(byte[] dataBytes, byte[] keyBytes) {
        try {
            return decryptData(this.cipherMode, RandomAlgorithm.NONE, dataBytes,
                    null, keyBytes, Globals.DEFAULT_VALUE_INT);
        } catch (NoSuchAlgorithmException e) {
            return new byte[0];
        }
    }

    @Override
    public String newKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(this.cipherMode.getAlgorithm());
            SecretKey secretKey = keyGenerator.generateKey();
            return super.encode(secretKey.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            return Globals.DEFAULT_VALUE_STRING;
        }
    }

    /**
     * The enum Des mode.
     */
    public enum DESMode {
        /**
         * ECB/NoPadding
         */
        ECB_NoPadding("ECB", "NoPadding"),
        /**
         * ECB/PKCS5Padding
         */
        ECB_PKCS5Padding("ECB", "PKCS5Padding"),
        /**
         * ECB/ISO10126Padding
         */
        ECB_ISO10126Padding("ECB", "ISO10126Padding"),
        /**
         * CBC/NoPadding
         */
        CBC_NoPadding("CBC", "NoPadding"),
        /**
         * CBC/PKCS5Padding
         */
        CBC_PKCS5Padding("CBC", "PKCS5Padding"),
        /**
         * CBC/ISO10126Padding
         */
        CBC_ISO10126Padding("CBC", "ISO10126Padding"),
        /**
         * PCBC/NoPadding
         */
        PCBC_NoPadding("PCBC", "NoPadding"),
        /**
         * PCBC/PKCS5Padding
         */
        PCBC_PKCS5Padding("PCBC", "PKCS5Padding"),
        /**
         * PCBC/ISO10126Padding
         */
        PCBC_ISO10126Padding("PCBC", "ISO10126Padding"),
        /**
         * CTR/NoPadding
         */
        CTR_NoPadding("CTR", "NoPadding"),
        /**
         * CTR/PKCS5Padding
         */
        CTR_PKCS5Padding("CTR", "PKCS5Padding"),
        /**
         * CTR/ISO10126Padding
         */
        CTR_ISO10126Padding("CTR", "ISO10126Padding"),
        /**
         * CTS/NoPadding
         */
        CTS_NoPadding("CTS", "NoPadding"),
        /**
         * CTS/PKCS5Padding
         */
        CTS_PKCS5Padding("CTS", "PKCS5Padding"),
        /**
         * CTS/ISO10126Padding
         */
        CTS_ISO10126Padding("CTS", "ISO10126Padding"),
        /**
         * CFB/NoPadding
         */
        CFB_NoPadding("CFB", "NoPadding"),
        /**
         * CFB/PKCS5Padding
         */
        CFB_PKCS5Padding("CFB", "PKCS5Padding"),
        /**
         * CFB/ISO10126Padding
         */
        CFB_ISO10126Padding("CFB", "ISO10126Padding"),
        /**
         * CFB8/NoPadding
         */
        CFB8_NoPadding("CFB8", "NoPadding"),
        /**
         * CFB8/PKCS5Padding
         */
        CFB8_PKCS5Padding("CFB8", "PKCS5Padding"),
        /**
         * CFB8/ISO10126Padding
         */
        CFB8_ISO10126Padding("CFB8", "ISO10126Padding"),
        /**
         * CFB64/NoPadding
         */
        CFB64_NoPadding("CFB64", "NoPadding"),
        /**
         * CFB64/PKCS5Padding
         */
        CFB64_PKCS5Padding("CFB64", "PKCS5Padding"),
        /**
         * CFB64/ISO10126Padding
         */
        CFB64_ISO10126Padding("CFB64", "ISO10126Padding"),
        /**
         * OFB/NoPadding
         */
        OFB_NoPadding("OFB", "NoPadding"),
        /**
         * OFB/PKCS5Padding
         */
        OFB_PKCS5Padding("OFB", "PKCS5Padding"),
        /**
         * OFB/ISO10126Padding
         */
        OFB_ISO10126Padding("OFB", "ISO10126Padding"),
        /**
         * OFB8/NoPadding
         */
        OFB8_NoPadding("OFB8", "NoPadding"),
        /**
         * OFB8/PKCS5Padding
         */
        OFB8_PKCS5Padding("OFB8", "PKCS5Padding"),
        /**
         * OFB8/ISO10126Padding
         */
        OFB8_ISO10126Padding("OFB8", "ISO10126Padding"),
        /**
         * OFB64/NoPadding
         */
        OFB64_NoPadding("OFB64", "NoPadding"),
        /**
         * OFB64/PKCS5Padding
         */
        OFB64_PKCS5Padding("OFB64", "PKCS5Padding"),
        /**
         * OFB64/ISO10126Padding
         */
        OFB64_ISO10126Padding("OFB64", "ISO10126Padding");

        private final String mode;
        private final String padding;

        DESMode(String mode, String padding) {
            this.mode = mode;
            this.padding = padding;
        }

        private String getMode() {
            return mode;
        }

        private String getPadding() {
            return padding;
        }
    }
}
