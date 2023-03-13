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
package org.nervousync.security.digest.impl;

import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jcajce.provider.digest.SHA1;
import org.nervousync.security.digest.BaseDigestProvider;
import org.nervousync.exceptions.crypto.CryptoException;

import java.security.MessageDigest;

/**
 * The type Sha 1 digest provider.
 */
public final class SHA1DigestProviderImpl extends BaseDigestProvider {

    /**
     * Instantiates a new Sha 1 digest provider.
     *
     * @throws CryptoException the crypto exception
     */
    public SHA1DigestProviderImpl() throws CryptoException {
        super("SHA-1", new byte[0]);
    }

    /**
     * Instantiates a new Sha 1 digest provider.
     *
     * @param keyBytes the key bytes
     * @throws CryptoException the crypto exception
     */
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
