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
package org.nervousync.security.crypto;

import org.nervousync.security.config.CipherConfig;
import org.nervousync.enumerations.crypto.CryptoMode;
import org.nervousync.exceptions.crypto.CryptoException;
import org.nervousync.utils.StringUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

/**
 * The type Symmetric crypto provider.
 */
public abstract class SymmetricCryptoProvider extends BaseCryptoProvider {

    /**
     * Instantiates a new Symmetric crypto provider.
     *
     * @param cipherConfig the cipher config
     * @param cryptoMode   the crypto mode
     * @param cipherKey    the cipher key
     * @throws CryptoException the crypto exception
     */
    protected SymmetricCryptoProvider(CipherConfig cipherConfig, CryptoMode cryptoMode,
                                      CipherKey cipherKey) throws CryptoException {
        super(cipherConfig, cryptoMode, cipherKey);
        this.reset();
    }

    @Override
    public final void append(byte[] dataBytes, int position, int length) throws CryptoException {
        if (dataBytes.length < (position + length)) {
            throw new CryptoException("Data bytes invalid!");
        }
        switch (this.cryptoMode) {
            case ENCRYPT:
            case DECRYPT:
                this.cipher.update(dataBytes, position, length);
                break;
            default:
                throw new CryptoException("Unknown crypto mode! ");
        }
    }

    @Override
    public final byte[] finish(byte[] dataBytes, int position, int length) throws CryptoException {
        switch (this.cryptoMode) {
            case ENCRYPT:
            case DECRYPT:
                try {
                    return this.cipher.doFinal(dataBytes, position, length);
                } catch (IllegalBlockSizeException | BadPaddingException e) {
                    throw new CryptoException(e);
                } finally {
                    this.reset();
                }
            case SIGNATURE:
            case VERIFY:
                throw new CryptoException("Current method was not support!");
            default:
                throw new CryptoException("Unknown crypto mode! ");
        }
    }

    @Override
    public final boolean verify(byte[] signature) throws CryptoException {
        throw new CryptoException("Current method was not support!");
    }

    @Override
    public final void reset() throws CryptoException {
        switch (this.cryptoMode) {
            case ENCRYPT:
            case DECRYPT:
                this.cipher = this.initCipher();
                break;
            default:
                throw new CryptoException("Unknown crypto mode! ");
        }
    }

    /**
     * Generate key byte [ ].
     *
     * @param algorithm       the algorithm
     * @param keySize         the key size
     * @param randomAlgorithm the random algorithm
     * @return the byte [ ]
     * @throws CryptoException the crypto exception
     */
    public static byte[] generateKey(String algorithm, int keySize, String randomAlgorithm) throws CryptoException {
        if (StringUtils.isEmpty(algorithm)) {
            throw new CryptoException("Unknown algorithm! ");
        }

        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm, "BC");
            switch (algorithm.toUpperCase()) {
                case "AES":
                    keyGenerator.init(keySize, SecureRandom.getInstance(randomAlgorithm));
                    break;
                case "SM4":
                    keyGenerator.init(keySize, new SecureRandom());
                    break;
                case "DES":
                case "DESEDE":
                    break;
                default:
                    throw new CryptoException("Unknown algorithm! ");
            }
            SecretKey secretKey = keyGenerator.generateKey();
            return secretKey.getEncoded();
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new CryptoException(e);
        }
    }
}
