package org.nervousync.security.crypto;

import org.nervousync.security.config.CipherConfig;
import org.nervousync.enumerations.crypto.CryptoMode;
import org.nervousync.exceptions.crypto.CryptoException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import java.security.*;

/**
 * The type Asymmetric crypto provider.
 */
public abstract class AsymmetricCryptoProvider extends BaseCryptoProvider {

    /**
     * The Private key.
     */
    private final Key key;
    /**
     * The Signature.
     */
    protected Signature signature;

    /**
     * Instantiates a new Asymmetric crypto provider.
     *
     * @param cipherConfig the cipher config
     * @param cryptoMode   the crypto mode
     * @param cipherKey    the cipher key
     * @throws CryptoException the crypto exception
     */
    protected AsymmetricCryptoProvider(CipherConfig cipherConfig, CryptoMode cryptoMode,
                                       CipherKey cipherKey) throws CryptoException {
        super(cipherConfig, cryptoMode, cipherKey);
        this.key = cipherKey.getKey();
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
            case SIGNATURE:
            case VERIFY:
                try {
                    this.signature.update(dataBytes);
                } catch (SignatureException e) {
                    throw new CryptoException(e);
                }
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
                try {
                    this.signature.update(dataBytes);
                    return this.signature.sign();
                } catch (SignatureException e) {
                    throw new CryptoException(e);
                } finally {
                    this.reset();
                }
            case VERIFY:
                throw new CryptoException("Finish method was not support in verify mode, execute verify method instead!");
            default:
                throw new CryptoException("Unknown crypto mode! ");
        }
    }

    @Override
    public final boolean verify(byte[] signature) throws CryptoException {
        if (!CryptoMode.VERIFY.equals(this.cryptoMode)) {
            throw new CryptoException("Verify method must execute in verify mode! ");
        }
        try {
            boolean result = this.signature.verify(signature);
            this.reset();
            return result;
        } catch (SignatureException e) {
            throw new CryptoException(e);
        }
    }

    @Override
    public final void reset() throws CryptoException {
        switch (this.cryptoMode) {
            case ENCRYPT:
            case DECRYPT:
                this.cipher = this.initCipher();
                break;
            case SIGNATURE:
            case VERIFY:
                this.signature = this.initSignature();
                break;
            default:
                throw new CryptoException("Unknown crypto mode! ");
        }
    }

    @Override
    protected Cipher initCipher() throws CryptoException {
        switch (this.cryptoMode) {
            case ENCRYPT:
            case DECRYPT:
                return super.generateCipher(this.key, 0);
            default:
                throw new CryptoException("Unknown crypto mode! ");
        }
    }

    private Signature initSignature() throws CryptoException {
        try {
            Signature signature = Signature.getInstance(this.cipherConfig.getAlgorithm());
            switch (this.cryptoMode) {
                case SIGNATURE:
                    signature.initSign((PrivateKey) this.key);
                    break;
                case VERIFY:
                    signature.initVerify((PublicKey) this.key);
                    break;
                default:
                    throw new CryptoException("Unknown crypto mode! ");
            }
            return signature;
        } catch (NoSuchAlgorithmException | InvalidKeyException | ClassCastException e) {
            throw new CryptoException(e);
        }
    }
}
