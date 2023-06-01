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

import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jcajce.provider.digest.MD5;
import org.nervousync.security.digest.BaseDigestProvider;
import org.nervousync.exceptions.crypto.CryptoException;

import java.security.MessageDigest;

/**
 * The type Md 5 digest provider.
 */
@Deprecated
public final class MD5DigestProviderImpl extends BaseDigestProvider {

    /**
     * Instantiates a new Md 5 digest provider.
     *
     * @throws CryptoException the crypto exception
     */
    public MD5DigestProviderImpl() throws CryptoException {
        super("MD5", new byte[0]);
    }

    /**
     * Instantiates a new Md 5 digest provider.
     *
     * @param keyBytes the key bytes
     * @throws CryptoException the crypto exception
     */
    public MD5DigestProviderImpl(byte[] keyBytes) throws CryptoException {
        super("MD5/HMAC", keyBytes);
    }

    @Override
    protected MessageDigest initDigest(String algorithm) {
        return new MD5.Digest();
    }

    @Override
    protected Mac initHmac(String algorithm, byte[] keyBytes) {
        HMac hmac = new HMac(new MD5Digest());
        hmac.init(new KeyParameter(keyBytes));
        return hmac;
    }
}
