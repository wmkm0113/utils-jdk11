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

/**
 * The type Sha 2 digest provider.
 */
public final class SHA2DigestProviderImpl extends BaseDigestProvider {

    /**
     * Instantiates a new Sha 2 digest provider.
     *
     * @param algorithm the algorithm
     * @param keyBytes  the key bytes
     * @throws CryptoException the crypto exception
     */
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
