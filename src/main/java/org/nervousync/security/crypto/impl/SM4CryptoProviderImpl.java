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
package org.nervousync.security.crypto.impl;

import org.nervousync.security.config.CipherConfig;
import org.nervousync.security.crypto.SymmetricCryptoProvider;
import org.nervousync.enumerations.crypto.CryptoMode;
import org.nervousync.exceptions.crypto.CryptoException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import java.security.SecureRandom;

/**
 * The type Sm 4 crypto provider.
 */
public final class SM4CryptoProviderImpl extends SymmetricCryptoProvider {

    /**
     * Instantiates a new Sm 4 crypto provider.
     *
     * @param cipherConfig    the cipher config
     * @param cryptoMode      the crypto mode
     * @param keyBytes        the key bytes
     * @param randomAlgorithm the random algorithm
     * @throws CryptoException the crypto exception
     */
    public SM4CryptoProviderImpl(CipherConfig cipherConfig, CryptoMode cryptoMode,
                                 byte[] keyBytes, String randomAlgorithm) throws CryptoException {
        super(cipherConfig, cryptoMode, new CipherKey(128, keyBytes, randomAlgorithm));
    }

    @Override
    protected Cipher initCipher() throws CryptoException {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("SM4", "BC");
            SecureRandom secureRandom = SecureRandom.getInstance(this.cipherKey.getRandomAlgorithm());
            secureRandom.setSeed(this.cipherKey.getKeyBytes());
            keyGenerator.init(this.cipherKey.getKeySize(), secureRandom);
            return super.generateCipher(keyGenerator.generateKey(),
                    this.cipherConfig.getMode().equalsIgnoreCase("ECB") ? 0 : 16);
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }
}
