package org.nervousync.security.crypto;

import org.nervousync.security.config.CipherConfig;
import org.nervousync.enumerations.crypto.CryptoMode;
import org.nervousync.exceptions.crypto.CryptoException;
import org.nervousync.utils.StringUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

public abstract class SymmetricCryptoProvider extends BaseCryptoProvider {

    protected SymmetricCryptoProvider(CipherConfig cipherConfig, CryptoMode cryptoMode,
                                      CipherKey cipherKey) throws CryptoException {
        super(cipherConfig, cryptoMode, cipherKey);
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
            case VERIFY:
                throw new CryptoException("Current method was not support!");
            default:
                throw new CryptoException("Unknown crypto mode! ");
        }
    }

    @Override
    public final boolean verify(byte[] signature) throws CryptoException {
        throw new CryptoException("Current method was not support!");
    }

    @Override
    public final void reset() throws CryptoException {
        switch (this.cryptoMode) {
            case ENCRYPT:
            case DECRYPT:
                this.cipher = this.initCipher();
                break;
            default:
                throw new CryptoException("Unknown crypto mode! ");
        }
    }

    public static byte[] generateKey(String algorithm, int keySize, String randomAlgorithm) throws CryptoException {
        if (StringUtils.isEmpty(algorithm)) {
            throw new CryptoException("Unknown algorithm! ");
        }

        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm, "BC");
            switch (algorithm.toUpperCase()) {
                case "AES":
                    keyGenerator.init(keySize, SecureRandom.getInstance(randomAlgorithm));
                    break;
                case "SM4":
                    keyGenerator.init(keySize, new SecureRandom());
                    break;
                case "DES":
                case "DESEDE":
                    break;
                default:
                    throw new CryptoException("Unknown algorithm! ");
            }
            SecretKey secretKey = keyGenerator.generateKey();
            return secretKey.getEncoded();
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new CryptoException(e);
        }
    }
}
