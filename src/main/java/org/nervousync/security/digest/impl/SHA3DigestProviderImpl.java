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
import org.bouncycastle.jcajce.provider.digest.*;
import org.nervousync.security.digest.BaseDigestProvider;
import org.nervousync.exceptions.crypto.CryptoException;
import org.nervousync.utils.StringUtils;

import java.security.MessageDigest;

/**
 * The type Sha 3 digest provider.
 */
public final class SHA3DigestProviderImpl extends BaseDigestProvider {

    /**
     * Instantiates a new Sha 3 digest provider.
     *
     * @param algorithm the algorithm
     * @throws CryptoException the crypto exception
     */
    public SHA3DigestProviderImpl(String algorithm) throws CryptoException {
        super(algorithm, new byte[0]);
    }

    /**
     * Instantiates a new Sha 3 digest provider.
     *
     * @param algorithm the algorithm
     * @param keyBytes  the key bytes
     * @throws CryptoException the crypto exception
     */
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
