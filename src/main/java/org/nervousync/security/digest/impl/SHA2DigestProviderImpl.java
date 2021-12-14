package org.nervousync.security.digest.impl;

import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.digests.*;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jcajce.provider.digest.SHA224;
import org.bouncycastle.jcajce.provider.digest.SHA256;
import org.bouncycastle.jcajce.provider.digest.SHA384;
import org.bouncycastle.jcajce.provider.digest.SHA512;
import org.nervousync.security.digest.BaseDigestProvider;
import org.nervousync.exceptions.crypto.CryptoException;
import org.nervousync.utils.StringUtils;

import java.security.MessageDigest;

public final class SHA2DigestProviderImpl extends BaseDigestProvider {

    public SHA2DigestProviderImpl(String algorithm, byte[] keyBytes) throws CryptoException {
        super(algorithm, keyBytes);
    }

    @Override
    protected MessageDigest initDigest(String algorithm) throws CryptoException {
        if (StringUtils.isEmpty(algorithm)) {
            throw new CryptoException("Unknown algorithm! ");
        }
        switch (algorithm.toUpperCase()) {
            case "SHA-224":
                return new SHA224.Digest();
            case "SHA-256":
                return new SHA256.Digest();
            case "SHA-384":
                return new SHA384.Digest();
            case "SHA-512":
                return new SHA512.Digest();
            case "SHA-512/224":
                return new SHA512.DigestT224();
            case "SHA-512/256":
                return new SHA512.DigestT256();
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
            case "SHA-224/HMAC":
                hmac = new HMac(new SHA224Digest());
                break;
            case "SHA-256/HMAC":
                hmac = new HMac(new SHA256Digest());
                break;
            case "SHA-384/HMAC":
                hmac = new HMac(new SHA384Digest());
                break;
            case "SHA-512/HMAC":
                hmac = new HMac(new SHA512Digest());
                break;
            case "SHA-512/224/HMAC":
                hmac = new HMac(new SHA512tDigest(224));
                break;
            case "SHA-512/256/HMAC":
                hmac = new HMac(new SHA512tDigest(256));
                break;
            default:
                throw new CryptoException("Unknown algorithm! ");
        }
        hmac.init(new KeyParameter(keyBytes));
        return hmac;
    }
}