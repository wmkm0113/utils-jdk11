/*
 * Copyright 2021 Nervousync Studio
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * The type Des crypto provider.
 */
public final class DESCryptoProviderImpl extends SymmetricCryptoProvider {

    /**
     * Instantiates a new Des crypto provider.
     *
     * @param cipherConfig the cipher config
     * @param cryptoMode   the crypto mode
     * @param keyBytes     the key bytes
     * @throws CryptoException the crypto exception
     */
    public DESCryptoProviderImpl(CipherConfig cipherConfig, CryptoMode cryptoMode, byte[] keyBytes)
            throws CryptoException {
        super(cipherConfig, cryptoMode, new CipherKey(keyBytes));
    }

    @Override
    protected Cipher initCipher() throws CryptoException {
        try {
            DESKeySpec desKeySpec = new DESKeySpec(this.cipherKey.getKeyBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            return super.generateCipher(keyFactory.generateSecret(desKeySpec),
                    this.cipherConfig.getMode().equalsIgnoreCase("ECB") ? 0 : 8);
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }
}
