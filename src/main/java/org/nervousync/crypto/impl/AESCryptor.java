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
import java.security.SecureRandom;

public final class AESCryptor extends SymmetricCryptor {

    private final int keySize;
    private final RandomAlgorithm randomAlgorithm;

    public AESCryptor(int keySize, AESMode aesMode, EncodeType encodeType, RandomAlgorithm randomAlgorithm) {
        super(aesMode.getCipherMode(), encodeType);
        this.keySize = keySize;
        this.randomAlgorithm = randomAlgorithm;
    }

    @Override
    public byte[] encrypt(byte[] dataBytes, byte[] keyBytes) {
        try {
            return encryptData(this.cipherMode, this.randomAlgorithm, dataBytes, null, keyBytes, this.keySize);
        } catch (NoSuchAlgorithmException e) {
            return new byte[0];
        }
    }

    @Override
    public byte[] decrypt(byte[] dataBytes, byte[] keyBytes) {
        try {
            return decryptData(this.cipherMode, this.randomAlgorithm, dataBytes, null, keyBytes, this.keySize);
        } catch (NoSuchAlgorithmException e) {
            return new byte[0];
        }
    }

    @Override
    public String newKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(this.keySize, SecureRandom.getInstance(this.randomAlgorithm.getAlgorithm()));
            SecretKey secretKey = keyGenerator.generateKey();
            return super.encode(secretKey.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            return Globals.DEFAULT_VALUE_STRING;
        }
    }

    /**
     * The enum Aes mode.
     */
    public enum AESMode {
        /**
         * AES/ECB/NoPadding
         */
        ECB_NoPadding("ECB", "NoPadding"),
        /**
         * AES/ECB/PKCS5Padding
         */
        ECB_PKCS5Padding("ECB", "PKCS5Padding"),
        /**
         * AES/ECB/ISO10126Padding
         */
        ECB_ISO10126Padding("ECB", "ISO10126Padding"),
        /**
         * AES/CBC/NoPadding
         */
        CBC_NoPadding("CBC", "NoPadding"),
        /**
         * AES/CBC/PKCS5Padding
         */
        CBC_PKCS5Padding("CBC", "PKCS5Padding"),
        /**
         * AES/CBC/ISO10126Padding
         */
        CBC_ISO10126Padding("CBC", "ISO10126Padding"),
        /**
         * AES/PCBC/NoPadding
         */
        PCBC_NoPadding("PCBC", "NoPadding"),
        /**
         * AES/PCBC/PKCS5Padding
         */
        PCBC_PKCS5Padding("PCBC", "PKCS5Padding"),
        /**
         * AES/PCBC/ISO10126Padding
         */
        PCBC_ISO10126Padding("PCBC", "ISO10126Padding"),
        /**
         * AES/CTR/NoPadding
         */
        CTR_NoPadding("CTR", "NoPadding"),
        /**
         * AES/CTR/PKCS5Padding
         */
        CTR_PKCS5Padding("CTR", "PKCS5Padding"),
        /**
         * AES/CTR/ISO10126Padding
         */
        CTR_ISO10126Padding("CTR", "ISO10126Padding"),
        /**
         * AES/CTS/NoPadding
         */
        CTS_NoPadding("CTS", "NoPadding"),
        /**
         * AES/CTS/PKCS5Padding
         */
        CTS_PKCS5Padding("CTS", "PKCS5Padding"),
        /**
         * AES/CTS/ISO10126Padding
         */
        CTS_ISO10126Padding("CTS", "ISO10126Padding"),
        /**
         * AES/CFB/NoPadding
         */
        CFB_NoPadding("CFB", "NoPadding"),
        /**
         * AES/CFB/PKCS5Padding
         */
        CFB_PKCS5Padding("CFB", "PKCS5Padding"),
        /**
         * AES/CFB/ISO10126Padding
         */
        CFB_ISO10126Padding("CFB", "ISO10126Padding"),
        /**
         * AES/CFB8/NoPadding
         */
        CFB8_NoPadding("CFB8", "NoPadding"),
        /**
         * AES/CFB8/PKCS5Padding
         */
        CFB8_PKCS5Padding("CFB8", "PKCS5Padding"),
        /**
         * AES/CFB8/ISO10126Padding
         */
        CFB8_ISO10126Padding("CFB8", "ISO10126Padding"),
        /**
         * AES/CFB128/NoPadding
         */
        CFB128_NoPadding("CFB128", "NoPadding"),
        /**
         * AES/CFB128/PKCS5Padding
         */
        CFB128_PKCS5Padding("CFB128", "PKCS5Padding"),
        /**
         * AES/CFB128/ISO10126Padding
         */
        CFB128_ISO10126Padding("CFB128", "ISO10126Padding"),
        /**
         * AES/OFB/NoPadding
         */
        OFB_NoPadding("OFB", "NoPadding"),
        /**
         * AES/OFB/PKCS5Padding
         */
        OFB_PKCS5Padding("OFB", "PKCS5Padding"),
        /**
         * AES/OFB/ISO10126Padding
         */
        OFB_ISO10126Padding("OFB", "ISO10126Padding"),
        /**
         * AES/OFB8/NoPadding
         */
        OFB8_NoPadding("OFB8", "NoPadding"),
        /**
         * AES/OFB8/PKCS5Padding
         */
        OFB8_PKCS5Padding("OFB8", "PKCS5Padding"),
        /**
         * AES/OFB8/ISO10126Padding
         */
        OFB8_ISO10126Padding("OFB8", "ISO10126Padding"),
        /**
         * AES/OFB128/NoPadding
         */
        OFB128_NoPadding("OFB128", "NoPadding"),
        /**
         * AES/OFB128/PKCS5Padding
         */
        OFB128_PKCS5Padding("OFB128", "PKCS5Padding"),
        /**
         * AES/OFB128/ISO10126Padding
         */
        OFB128_ISO10126Padding("OFB128", "ISO10126Padding");

        private final CipherMode cipherMode;

        AESMode(String mode, String padding) {
            this.cipherMode = new CipherMode("AES", mode, padding);
        }

        /**
         * Gets cipher mode.
         *
         * @return the cipher mode
         */
        private CipherMode getCipherMode() {
            return cipherMode;
        }
    }
}
