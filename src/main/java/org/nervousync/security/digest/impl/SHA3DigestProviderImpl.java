package org.nervousync.security.digest.impl;

import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.digests.*;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jcajce.provider.digest.*;
import org.nervousync.security.digest.BaseDigestProvider;
import org.nervousync.exceptions.crypto.CryptoException;
import org.nervousync.utils.StringUtils;

import java.security.MessageDigest;

public final class SHA3DigestProviderImpl extends BaseDigestProvider {

    public SHA3DigestProviderImpl(String algorithm) throws CryptoException {
        super(algorithm, new byte[0]);
    }

    public SHA3DigestProviderImpl(String algorithm, byte[] keyBytes) throws CryptoException {
        super(algorithm, keyBytes);
    }

    @Override
    protected MessageDigest initDigest(String algorithm) throws CryptoException {
        if (StringUtils.isEmpty(algorithm)) {
            throw new CryptoException("Unknown algorithm! ");
        }
        switch (algorithm.toUpperCase()) {
            case "SHA3-224":
                return new SHA3.Digest224();
            case "SHA3-256":
                return new SHA3.Digest256();
            case "SHA3-384":
                return new SHA3.Digest384();
            case "SHA3-512":
                return new SHA3.Digest512();
            case "SHAKE128":
                return new SHA3.DigestShake128_256();
            case "SHAKE256":
                return new SHA3.DigestShake256_512();
            default:
                throw new CryptoException("Unknown algorithm! ");
        }
    }

    @Override
    protected Mac initHmac(String algorithm, byte[] keyBytes) throws CryptoException {
        if (StringUtils.isEmpty(algorithm) || !algorithm.toUpperCase().endsWith("HMAC")) {
            throw new CryptoException("Unknown algorithm! ");
        }
        HMac hmac;
        switch (algorithm.toUpperCase()) {
            case "SHA3-224/HMAC":
                hmac = new HMac(new SHA3Digest(224));
                break;
            case "SHA3-256/HMAC":
                hmac = new HMac(new SHA3Digest(256));
                break;
            case "SHA3-384/HMAC":
                hmac = new HMac(new SHA3Digest(384));
                break;
            case "SHA3-512/HMAC":
                hmac = new HMac(new SHA3Digest(512));
                break;
            default:
                throw new CryptoException("Unknown algorithm! ");
        }
        hmac.init(new KeyParameter(keyBytes));
        return hmac;
    }
}
