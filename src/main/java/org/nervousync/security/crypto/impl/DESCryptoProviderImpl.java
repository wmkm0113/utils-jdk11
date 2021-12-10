package org.nervousync.security.crypto.impl;

import org.nervousync.security.config.CipherConfig;
import org.nervousync.security.crypto.SymmetricCryptoProvider;
import org.nervousync.enumerations.crypto.CryptoMode;
import org.nervousync.exceptions.crypto.CryptoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public final class DESCryptoProviderImpl extends SymmetricCryptoProvider {

    private transient static final Logger LOGGER = LoggerFactory.getLogger(DESCryptoProviderImpl.class);

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
