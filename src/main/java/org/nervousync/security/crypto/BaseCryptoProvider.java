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

/**
 * The type Base crypto provider.
 */
public abstract class BaseCryptoProvider extends SecureProvider {

    /**
     * The Cipher config.
     */
    protected final CipherConfig cipherConfig;
    /**
     * The Crypto mode.
     */
    protected final CryptoMode cryptoMode;
    /**
     * The Cipher key.
     */
    protected final CipherKey cipherKey;
    /**
     * The Cipher.
     */
    protected Cipher cipher;

    /**
     * Instantiates a new Base crypto provider.
     *
     * @param cipherConfig the cipher config
     * @param cryptoMode   the crypto mode
     * @param cipherKey    the cipher key
     */
    protected BaseCryptoProvider(CipherConfig cipherConfig, CryptoMode cryptoMode, CipherKey cipherKey) {
        this.cipherConfig = cipherConfig;
        this.cryptoMode = cryptoMode;
        this.cipherKey = cipherKey;
    }

    /**
     * Init cipher.
     *
     * @return the cipher
     * @throws CryptoException the crypto exception
     */
    protected abstract Cipher initCipher() throws CryptoException;

    /**
     * Generate cipher.
     *
     * @param key      the key
     * @param ivLength the iv length
     * @return the cipher
     * @throws CryptoException the crypto exception
     */
    protected final Cipher generateCipher(Key key, int ivLength) throws CryptoException {
        IvParameterSpec ivParameterSpec = null;
        if (ivLength > 0) {
            byte[] ivContent = new byte[ivLength];
            System.arraycopy(SecurityUtils.MD5(this.cipherKey.getKeyBytes()),
                    0, ivContent, 0, ivContent.length);
            ivParameterSpec = new IvParameterSpec(ivContent);
        }
        try {
            Cipher cipherInstance = Cipher.getInstance(this.cipherConfig.toString(), "BC");
            switch (this.cryptoMode) {
                case ENCRYPT:
                    cipherInstance.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);
                    break;
                case DECRYPT:
                    cipherInstance.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);
                    break;
                default:
                    throw new CryptoException("Unsupported crypto mode! ");
            }
            return cipherInstance;
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }

    /**
     * The type Cipher key.
     */
    public static final class CipherKey {

        private final int keySize;
        private final byte[] keyBytes;
        private final String randomAlgorithm;
        private final Key key;

        /**
         * Instantiates a new Cipher key.
         *
         * @param keyBytes the key bytes
         */
        public CipherKey(byte[] keyBytes) {
            this(Globals.DEFAULT_VALUE_INT, keyBytes, Globals.DEFAULT_VALUE_STRING, null);
        }

        /**
         * Instantiates a new Cipher key.
         *
         * @param keySize         the key size
         * @param keyBytes        the key bytes
         * @param randomAlgorithm the random algorithm
         */
        public CipherKey(int keySize, byte[] keyBytes, String randomAlgorithm) {
            this(keySize, keyBytes, randomAlgorithm, null);
        }

        /**
         * Instantiates a new Cipher key.
         *
         * @param key the key
         */
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

        /**
         * Gets key size.
         *
         * @return the key size
         */
        public int getKeySize() {
            return keySize;
        }

        /**
         * Get key bytes byte [ ].
         *
         * @return the byte [ ]
         */
        public byte[] getKeyBytes() {
            return keyBytes;
        }

        /**
         * Gets random algorithm.
         *
         * @return the random algorithm
         */
        public String getRandomAlgorithm() {
            return randomAlgorithm;
        }

        /**
         * Gets key.
         *
         * @return the key
         */
        public Key getKey() {
            return key;
        }
    }
}
