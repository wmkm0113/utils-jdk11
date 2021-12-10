package org.nervousync.security.crypto.impl;

import org.nervousync.security.config.CipherConfig;
import org.nervousync.security.crypto.SymmetricCryptoProvider;
import org.nervousync.enumerations.crypto.CryptoMode;
import org.nervousync.exceptions.crypto.CryptoException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import java.security.SecureRandom;

public final class SM4CryptoProviderImpl extends SymmetricCryptoProvider {

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
