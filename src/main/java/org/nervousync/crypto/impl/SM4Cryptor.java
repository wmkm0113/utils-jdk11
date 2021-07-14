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
import org.nervousync.utils.StringUtils;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

public final class SM4Cryptor extends SymmetricCryptor {

    private final RandomAlgorithm randomAlgorithm;

    public SM4Cryptor(SM4Mode sm4Mode, EncodeType encodeType, RandomAlgorithm randomAlgorithm) {
        super(sm4Mode.getCipherMode(), encodeType);
        this.randomAlgorithm = randomAlgorithm;
    }

    @Override
    public byte[] encrypt(byte[] dataBytes, byte[] keyBytes) {
        try {
            return encryptData(this.cipherMode, this.randomAlgorithm, dataBytes,
                    null, keyBytes, 128);
        } catch (NoSuchAlgorithmException e) {
            return new byte[0];
        }
    }

    @Override
    public byte[] decrypt(byte[] dataBytes, byte[] keyBytes) {
        try {
            return decryptData(this.cipherMode, this.randomAlgorithm, dataBytes,
                    null, keyBytes, 128);
        } catch (NoSuchAlgorithmException e) {
            return new byte[0];
        }
    }

    @Override
    public String newKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("SM4", "BC");
            keyGenerator.init(128, new SecureRandom());
            SecretKey secretKey = keyGenerator.generateKey();
            return StringUtils.base64Encode(secretKey.getEncoded());
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            return Globals.DEFAULT_VALUE_STRING;
        }
    }

    /**
     * The enum Triple des mode.
     */
    public enum SM4Mode {
        /**
         * SM4/ECB/NoPadding
         */
        ECB_NoPadding("ECB", "NoPadding"),
        /**
         * SM4/ECB/PKCS5Padding
         */
        ECB_PKCS5Padding("ECB", "PKCS5Padding"),
        /**
         * SM4/ECB/PKCS7Padding
         */
        ECB_PKCS7Padding("ECB", "PKCS7Padding"),
        /**
         * SM4/ECB/ISO10126Padding
         */
        ECB_ISO10126Padding("ECB", "ISO10126Padding"),
        /**
         * SM4/ECB/X9.23Padding
         */
        ECB_ANSIX923Padding("ECB", "X9.23Padding"),
        /**
         * SM4/CBC/NoPadding
         */
        CBC_NoPadding("CBC", "NoPadding"),
        /**
         * SM4/CBC/PKCS5Padding
         */
        CBC_PKCS5Padding("CBC", "PKCS5Padding"),
        /**
         * SM4/CBC/PKCS7Padding
         */
        CBC_PKCS7Padding("CBC", "PKCS7Padding"),
        /**
         * SM4/CBC/ISO10126Padding
         */
        CBC_ISO10126Padding("CBC", "ISO10126Padding"),
        /**
         * SM4/CBC/X9.23Padding
         */
        CBC_ANSIX923Padding("CBC", "X9.23Padding"),
        /**
         * SM4/CTR/NoPadding
         */
        CTR_NoPadding("CTR", "NoPadding"),
        /**
         * SM4/CTR/PKCS5Padding
         */
        CTR_PKCS5Padding("CTR", "PKCS5Padding"),
        /**
         * SM4/CTR/PKCS7Padding
         */
        CTR_PKCS7Padding("CTR", "PKCS7Padding"),
        /**
         * SM4/CTR/ISO10126Padding
         */
        CTR_ISO10126Padding("CTR", "ISO10126Padding"),
        /**
         * SM4/CTR/X9.23Padding
         */
        CTR_ANSIX923Padding("CTR", "X9.23Padding"),
        /**
         * SM4/CFB/NoPadding
         */
        CFB_NoPadding("CFB", "NoPadding"),
        /**
         * SM4/CFB/PKCS5Padding
         */
        CFB_PKCS5Padding("CFB", "PKCS5Padding"),
        /**
         * SM4/CFB/PKCS7Padding
         */
        CFB_PKCS7Padding("CFB", "PKCS7Padding"),
        /**
         * SM4/CFB/ISO10126Padding
         */
        CFB_ISO10126Padding("CFB", "ISO10126Padding"),
        /**
         * SM4/CFB/X9.23Padding
         */
        CFB_ANSIX923Padding("CFB", "X9.23Padding"),
        /**
         * SM4/OFB/NoPadding
         */
        OFB_NoPadding("OFB", "NoPadding"),
        /**
         * SM4/OFB/PKCS5Padding
         */
        OFB_PKCS5Padding("OFB", "PKCS5Padding"),
        /**
         * SM4/OFB/PKCS7Padding
         */
        OFB_PKCS7Padding("OFB", "PKCS7Padding"),
        /**
         * SM4/OFB/ISO10126Padding
         */
        OFB_ISO10126Padding("OFB", "ISO10126Padding"),
        /**
         * SM4/OFB/X9.23Padding
         */
        OFB_ANSIX923Padding("OFB", "X9.23Padding");

        private final CipherMode cipherMode;

        SM4Mode(String mode, String padding) {
            this.cipherMode = new CipherMode("SM4", mode, padding);
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
