package org.nervousync.security.digest.impl;

import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jcajce.provider.digest.SHA1;
import org.nervousync.security.digest.BaseDigestProvider;
import org.nervousync.exceptions.crypto.CryptoException;

import java.security.MessageDigest;

public final class SHA1DigestProviderImpl extends BaseDigestProvider {

    public SHA1DigestProviderImpl() throws CryptoException {
        super("SHA-1", new byte[0]);
    }

    public SHA1DigestProviderImpl(byte[] keyBytes) throws CryptoException {
        super("SHA-1/HMAC", keyBytes);
    }

    @Override
    protected MessageDigest initDigest(String algorithm) throws CryptoException {
        if ("SHA-1".equalsIgnoreCase(algorithm)) {
            return new SHA1.Digest();
        }
        throw new CryptoException("Unknown algorithm! ");
    }

    @Override
    protected HMac initHmac(String algorithm, byte[] keyBytes) throws CryptoException {
        if ("SHA-1/HMAC".equalsIgnoreCase(algorithm)) {
            HMac hmac = new HMac(new SHA1Digest());
            hmac.init(new KeyParameter(keyBytes));
            return hmac;
        }
        throw new CryptoException("Unknown algorithm! ");
    }
}
