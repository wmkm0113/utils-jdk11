package org.nervousync.security.crypto.impl;

import org.nervousync.security.config.CipherConfig;
import org.nervousync.security.crypto.AsymmetricCryptoProvider;
import org.nervousync.enumerations.crypto.CryptoMode;
import org.nervousync.exceptions.crypto.CryptoException;

public final class SM2CryptoProviderImpl extends AsymmetricCryptoProvider {

    public SM2CryptoProviderImpl(CipherConfig cipherConfig, CryptoMode cryptoMode,
                                 CipherKey cipherKey) throws CryptoException {
        super(cipherConfig, cryptoMode, cipherKey);
//                new CipherKey(keyType, keyBytes, "EC", "SHA1PRNG", keySize,
//                        certAlias, password, checkValidity, verifyKey));
    }
}
