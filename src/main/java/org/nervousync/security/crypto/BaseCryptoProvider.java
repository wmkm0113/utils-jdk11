package org.nervousync.security.crypto;

import org.nervousync.commons.core.Globals;
import org.nervousync.security.SecureProvider;
import org.nervousync.security.config.CipherConfig;
import org.nervousync.enumerations.crypto.CryptoMode;
import org.nervousync.exceptions.crypto.CryptoException;
import org.nervousync.utils.SecurityUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import java.security.Key;

public abstract class BaseCryptoProvider extends SecureProvider {

    protected final CipherConfig cipherConfig;
    protected final CryptoMode cryptoMode;
    protected final CipherKey cipherKey;
    protected Cipher cipher;

    protected BaseCryptoProvider(CipherConfig cipherConfig, CryptoMode cryptoMode, CipherKey cipherKey) {
        this.cipherConfig = cipherConfig;
        this.cryptoMode = cryptoMode;
        this.cipherKey = cipherKey;
    }

    protected abstract Cipher initCipher() throws CryptoException;

    protected final Cipher generateCipher(Key key, int ivLength) throws CryptoException {
        IvParameterSpec ivParameterSpec = null;
        if (ivLength > 0) {
            byte[] ivContent = new byte[ivLength];
            System.arraycopy(SecurityUtils.MD5(this.cipherKey.getKeyBytes()),
                    0, ivContent, 0, ivContent.length);
            ivParameterSpec = new IvParameterSpec(ivContent);
        }
        try {
            Cipher cipher = Cipher.getInstance(this.cipherConfig.toString(), "BC");
            switch (this.cryptoMode) {
                case ENCRYPT:
                    cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);
                    break;
                case DECRYPT:
                    cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);
                    break;
                default:
                    throw new CryptoException("Unsupported crypto mode! ");
            }
            return cipher;
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }

    public static final class CipherKey {

        private final int keySize;
        private final byte[] keyBytes;
        private final String randomAlgorithm;
        private final Key key;

        public CipherKey(byte[] keyBytes) {
            this(Globals.DEFAULT_VALUE_INT, keyBytes, Globals.DEFAULT_VALUE_STRING, null);
        }

        public CipherKey(int keySize, byte[] keyBytes, String randomAlgorithm) {
            this(keySize, keyBytes, randomAlgorithm, null);
        }

        public CipherKey(Key key) {
            this(Globals.DEFAULT_VALUE_INT, new byte[0], Globals.DEFAULT_VALUE_STRING, key);
        }

        private CipherKey(int keySize, byte[] keyBytes, String randomAlgorithm,
                         Key key) {
            this.keySize = keySize;
            this.keyBytes = keyBytes;
            this.randomAlgorithm = randomAlgorithm;
            this.key = key;
        }

        public int getKeySize() {
            return keySize;
        }

        public byte[] getKeyBytes() {
            return keyBytes;
        }

        public String getRandomAlgorithm() {
            return randomAlgorithm;
        }

        public Key getKey() {
            return key;
        }
    }
}
