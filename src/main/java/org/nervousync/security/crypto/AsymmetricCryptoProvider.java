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
                throw new CryptoException("Crypto mode invalid! ");
        }
    }

    @Override
    public final byte[] finish(byte[] dataBytes, int position, int length) throws CryptoException {
        byte[] result;
        switch (this.cryptoMode) {
            case ENCRYPT:
            case DECRYPT:
                try {
                    result = this.cipher.doFinal(dataBytes, position, length);
                } catch (IllegalBlockSizeException | BadPaddingException e) {
                    throw new CryptoException(e);
                } finally {
                    this.reset();
                }
                break;
            case SIGNATURE:
                try {
                    this.signature.update(dataBytes);
                    result = this.signature.sign();
                } catch (SignatureException e) {
                    throw new CryptoException(e);
                } finally {
                    this.reset();
                }
                break;
            case VERIFY:
                throw new CryptoException("Finish method was not support in verify mode, execute verify method instead!");
            default:
                throw new CryptoException("Unknown crypto mode! ");
        }
        return result;
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
            Signature signInstance = Signature.getInstance(this.cipherConfig.getAlgorithm());
            switch (this.cryptoMode) {
                case SIGNATURE:
                    signInstance.initSign((PrivateKey) this.key);
                    break;
                case VERIFY:
                    signInstance.initVerify((PublicKey) this.key);
                    break;
                default:
                    throw new CryptoException("Unknown crypto mode! ");
            }
            return signInstance;
        } catch (NoSuchAlgorithmException | InvalidKeyException | ClassCastException e) {
            throw new CryptoException(e);
        }
    }
}
