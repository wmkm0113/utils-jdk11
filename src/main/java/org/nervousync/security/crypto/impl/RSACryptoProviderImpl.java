package org.nervousync.security.crypto.impl;

import org.nervousync.security.config.CipherConfig;
import org.nervousync.security.crypto.AsymmetricCryptoProvider;
import org.nervousync.enumerations.crypto.CryptoMode;
import org.nervousync.exceptions.crypto.CryptoException;

/**
 * The type Rsa crypto provider.
 */
public final class RSACryptoProviderImpl extends AsymmetricCryptoProvider {

    /**
     * Instantiates a new Rsa crypto provider.
     *
     * @param cipherConfig the cipher config
     * @param cryptoMode   the crypto mode
     * @param cipherKey    the cipher key
     * @throws CryptoException the crypto exception
     */
    public RSACryptoProviderImpl(CipherConfig cipherConfig, CryptoMode cryptoMode,
                                 CipherKey cipherKey) throws CryptoException {
        super(cipherConfig, cryptoMode, cipherKey);
//                new CipherKey(keyType, keyBytes, "RSA", randomAlgorithm, keySize,
//                        certAlias, password, checkValidity, verifyKey));
    }
}
