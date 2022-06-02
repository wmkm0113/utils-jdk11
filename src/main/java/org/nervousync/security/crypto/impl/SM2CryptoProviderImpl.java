package org.nervousync.security.crypto.impl;

import org.nervousync.security.config.CipherConfig;
import org.nervousync.security.crypto.AsymmetricCryptoProvider;
import org.nervousync.enumerations.crypto.CryptoMode;
import org.nervousync.exceptions.crypto.CryptoException;

/**
 * The type Sm 2 crypto provider.
 */
public final class SM2CryptoProviderImpl extends AsymmetricCryptoProvider {

    /**
     * Instantiates a new Sm 2 crypto provider.
     *
     * @param cipherConfig the cipher config
     * @param cryptoMode   the crypto mode
     * @param cipherKey    the cipher key
     * @throws CryptoException the crypto exception
     */
    public SM2CryptoProviderImpl(CipherConfig cipherConfig, CryptoMode cryptoMode,
                                 CipherKey cipherKey) throws CryptoException {
        super(cipherConfig, cryptoMode, cipherKey);
//                new CipherKey(keyType, keyBytes, "EC", "SHA1PRNG", keySize,
//                        certAlias, password, checkValidity, verifyKey));
    }
}
