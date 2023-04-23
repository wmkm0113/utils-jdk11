/*
 * Licensed to the Nervousync Studio (NSYC) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import org.nervousync.commons.core.Globals;
import org.nervousync.security.config.CipherConfig;
import org.nervousync.enumerations.crypto.CryptoMode;
import org.nervousync.exceptions.crypto.CryptoException;
import org.nervousync.utils.SecurityUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import java.nio.ByteBuffer;
import java.security.*;
import java.util.Arrays;

/**
 * The type Asymmetric crypto provider.
 */
public abstract class AsymmetricCryptoProvider extends BaseCryptoProvider {

    /**
     * The Private key.
     */
    private final Key key;
    private final int blockLength;
    private final int blockSize;
    private byte[] appendBuffer;
    private byte[] dataBytes;
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
    protected AsymmetricCryptoProvider(final CipherConfig cipherConfig, final CryptoMode cryptoMode,
                                       final CipherKey cipherKey, final int paddingLength) throws CryptoException {
        super(cipherConfig, cryptoMode, cipherKey);
        this.key = cipherKey.getKey();
        this.blockLength = SecurityUtils.rsaKeySize(this.key) >> 3;
        if (paddingLength > 0) {
            this.blockSize = this.blockLength - paddingLength;
        } else {
            this.blockSize = this.blockLength;
        }
        this.appendBuffer = new byte[0];
        this.dataBytes = new byte[0];
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
                this.appendBuffer(dataBytes, position, length);
                this.process();
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

    private void appendBuffer(byte[] dataBytes, int position, int length) {
        this.appendBuffer = ByteBuffer.allocate(this.appendBuffer.length + length)
                .put(this.appendBuffer)
                .put(dataBytes, position, length)
                .array();
    }

    private void process() throws CryptoException {
        int blockLength = CryptoMode.ENCRYPT.equals(this.cryptoMode) ? this.blockSize : this.blockLength;
        if (blockLength == Globals.DEFAULT_VALUE_INT || this.appendBuffer.length < blockLength) {
            return;
        }
        int position = 0;
        while (position + blockLength < this.appendBuffer.length) {
            byte[] dataBytes = new byte[blockLength];
            System.arraycopy(this.appendBuffer, position, dataBytes, Globals.INITIALIZE_INT_VALUE, blockLength);
            try {
                byte[] encBytes = this.cipher.doFinal(dataBytes);
                this.dataBytes = concat(this.dataBytes, encBytes);
            } catch (IllegalBlockSizeException | BadPaddingException e) {
                throw new CryptoException(e);
            } finally {
                this.reset();
            }
            position += blockLength;
        }
        int remainLength = this.appendBuffer.length - position;
        this.appendBuffer = ByteBuffer.allocate(remainLength).put(this.appendBuffer, position, remainLength).array();
    }

    private static byte[] concat(final byte[] dataBytes, final byte[] concatBytes) {
        if (dataBytes == null || dataBytes.length == 0) {
            return concatBytes;
        }

        if (concatBytes == null || concatBytes.length == 0) {
            return dataBytes;
        }
        byte[] newBytes = Arrays.copyOf(dataBytes, dataBytes.length + concatBytes.length);
        System.arraycopy(concatBytes, Globals.INITIALIZE_INT_VALUE, newBytes, dataBytes.length, concatBytes.length);
        return newBytes;
    }

    @Override
    public final byte[] finish(byte[] dataBytes, int position, int length) throws CryptoException {
        byte[] result;
        switch (this.cryptoMode) {
            case ENCRYPT:
            case DECRYPT:
                this.appendBuffer(dataBytes, position, length);
                this.process();
                if (this.appendBuffer.length > 0) {
                    byte[] finalBytes = new byte[this.appendBuffer.length];
                    System.arraycopy(this.appendBuffer, Globals.INITIALIZE_INT_VALUE, finalBytes,
                            Globals.INITIALIZE_INT_VALUE, this.appendBuffer.length);
                    try {
                        byte[] encBytes = this.cipher.doFinal(finalBytes);
                        result = concat(this.dataBytes, encBytes);
                    } catch (IllegalBlockSizeException | BadPaddingException e) {
                        throw new CryptoException(e);
                    } finally {
                        this.reset();
                        this.appendBuffer = new byte[0];
                    }
                } else {
                    result = this.dataBytes;
                }
                this.dataBytes = new byte[0];
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
                return super.generateCipher(this.key, Globals.INITIALIZE_INT_VALUE);
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
