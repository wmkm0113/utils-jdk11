package org.nervousync.security.digest;

import org.bouncycastle.crypto.Mac;
import org.nervousync.commons.core.Globals;
import org.nervousync.security.SecureProvider;
import org.nervousync.exceptions.crypto.CryptoException;
import org.nervousync.utils.StringUtils;

import java.security.MessageDigest;
import java.util.Arrays;

public abstract class BaseDigestProvider extends SecureProvider {

    private final boolean macMode;
    private final MessageDigest messageDigest;
    private final Mac hmac;

    protected BaseDigestProvider(String algorithm, byte[] keyBytes) throws CryptoException {
        if (StringUtils.isEmpty(algorithm)) {
            throw new CryptoException("Unknown algorithm! ");
        }
        this.macMode = algorithm.toUpperCase().contains("HMAC");
        this.messageDigest = this.macMode ? null : this.initDigest(algorithm);
        this.hmac = this.macMode ? this.initHmac(algorithm, keyBytes) : null;
    }

    protected abstract MessageDigest initDigest(String algorithm) throws CryptoException;

    protected abstract Mac initHmac(String algorithm, byte[] keyBytes) throws CryptoException;

    @Override
    public final void append(byte[] dataBytes, int position, int length) throws CryptoException {
        if (dataBytes.length < (position + length)) {
            throw new CryptoException("Data bytes invalid!");
        }
        if (this.macMode) {
            this.hmac.update(dataBytes, position, length);
        } else {
            this.messageDigest.update(dataBytes, position, length);
        }
    }

    @Override
    public final byte[] finish(byte[] dataBytes, int position, int length) throws CryptoException {
        if (dataBytes.length < (position + length)) {
            throw new CryptoException("Data bytes invalid!");
        }
        this.append(dataBytes, position, length);
        byte[] result;
        if (this.macMode) {
            result = new byte[this.hmac.getMacSize()];
            this.hmac.doFinal(result, 0);
        } else {
            result = this.messageDigest.digest();
        }
        this.reset();
        return result;
    }

    @Override
    public final boolean verify(byte[] signature) throws CryptoException {
        byte[] calcResult;
        if (this.macMode) {
            calcResult = new byte[this.hmac.getMacSize()];
            this.hmac.doFinal(calcResult, 0);
        } else {
            calcResult = this.messageDigest.digest();
        }
        boolean result = Arrays.equals(calcResult, signature);
        this.reset();
        return result;
    }

    @Override
    public final void reset() throws CryptoException {
        if (this.macMode) {
            this.hmac.reset();
        } else {
            this.messageDigest.reset();
        }
    }

    public int macLength() {
        return this.macMode ? this.hmac.getMacSize() : Globals.DEFAULT_VALUE_INT;
    }
}
