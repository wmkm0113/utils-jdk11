package org.nervousync.security.digest.impl;

import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jcajce.provider.digest.SM3;
import org.nervousync.security.digest.BaseDigestProvider;
import org.nervousync.exceptions.crypto.CryptoException;

import java.security.MessageDigest;

/**
 * The type Sm 3 digest provider.
 */
public final class SM3DigestProviderImpl extends BaseDigestProvider {

    /**
     * Instantiates a new Sm 3 digest provider.
     *
     * @throws CryptoException the crypto exception
     */
    public SM3DigestProviderImpl() throws CryptoException {
        super("SM3", new byte[0]);
    }

    /**
     * Instantiates a new Sm 3 digest provider.
     *
     * @param keyBytes the key bytes
     * @throws CryptoException the crypto exception
     */
    public SM3DigestProviderImpl(byte[] keyBytes) throws CryptoException {
        super("SM3/HMAC", keyBytes);
    }

    @Override
    protected MessageDigest initDigest(String algorithm) {
        return new SM3.Digest();
    }

    @Override
    protected Mac initHmac(String algorithm, byte[] keyBytes) {
        HMac hmac = new HMac(new SM3Digest());
        hmac.init(new KeyParameter(keyBytes));
        return hmac;
    }
}
