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

package org.nervousync.crypto.impl;

import org.nervousync.commons.core.Globals;
import org.nervousync.crypto.core.AsymmetricCryptor;

import java.io.ByteArrayOutputStream;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.util.Date;
import java.util.Optional;

/**
 * The type Sm 2 cryptor.
 */
public final class SM2Cryptor extends AsymmetricCryptor {

    private final SM2Mode sm2Mode;

    /**
     * Instantiates a new Sm 2 cryptor.
     *
     * @param sm2Mode    the sm 2 mode
     * @param encodeType the encode type
     * @param publicKey  the public key
     * @param privateKey the private key
     */
    public SM2Cryptor(SM2Mode sm2Mode, EncodeType encodeType, PublicKey publicKey, PrivateKey privateKey) {
        super(new CipherMode("SM2", Globals.DEFAULT_VALUE_STRING, Globals.DEFAULT_VALUE_STRING),
                encodeType, "SM3withSM2", publicKey, privateKey);
        this.sm2Mode = sm2Mode;
    }

    /**
     * Generate SM2 public key with given key content
     *
     * @param keyContent key content
     * @return RSA public key object
     */
    public static Optional<PublicKey> publicKey(byte[] keyContent) {
        return generateKey(PublicKey.class, "EC", keyContent);
    }

    /**
     * Generate SM2 private key with given key content
     *
     * @param keyContent key content
     * @return RSA private key object
     */
    public static Optional<PrivateKey> privateKey(byte[] keyContent) {
        return generateKey(PrivateKey.class, "EC", keyContent);
    }

    /**
     * Generate key pair optional.
     *
     * @return the optional
     */
    public static Optional<KeyPair> generateKeyPair() {
        return AsymmetricCryptor.KeyPair("EC", RandomAlgorithm.SHA1PRNG.getAlgorithm(),
                Globals.INITIALIZE_INT_VALUE);
    }

    /**
     * X 509 certificate byte [ ].
     *
     * @param publicKey    the public key
     * @param serialNumber the serial number
     * @param beginDate    the begin date
     * @param endDate      the end date
     * @param certAlias    the cert alias
     * @param certName     the cert name
     * @param signKey      the sign key
     * @return byte [ ]
     */
    public static byte[] x509Certificate(PublicKey publicKey, long serialNumber, Date beginDate, Date endDate,
                                         String certAlias, String certName, PrivateKey signKey) {
        return AsymmetricCryptor.x509Certificate(publicKey, serialNumber, beginDate, endDate,
                certAlias, certName, signKey, "SM3withSM2")
                .map(x509Certificate -> {
                    try {
                        return x509Certificate.getEncoded();
                    } catch (CertificateEncodingException e) {
                        return new byte[0];
                    }
                })
                .orElse(new byte[0]);
    }

    /**
     * Key pair optional.
     *
     * @param serialNumber the serial number
     * @param beginDate    the begin date
     * @param endDate      the end date
     * @param certAlias    the cert alias
     * @param certName     the cert name
     * @param password     the password
     * @param signKey      the sign key
     * @return the optional
     */
    public static byte[] PKCS12(long serialNumber, Date beginDate, Date endDate,
                                String certAlias, String certName, String password, PrivateKey signKey) {
        return generateKeyPair()
                .map(keyPair ->
                        PKCS12Certificate(keyPair, serialNumber, beginDate, endDate, certAlias,
                                certName, password, signKey, "SM3withSM2"))
                .orElse(new byte[0]);
    }

    @Override
    protected byte[] convertResult(byte[] dataBytes) {
        if (SM2Mode.C1C2C3.equals(this.sm2Mode)) {
            return dataBytes;
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(dataBytes.length);
        byteArrayOutputStream.write(dataBytes, 0, 65);
        byteArrayOutputStream.write(dataBytes, 65, 32);
        byteArrayOutputStream.write(dataBytes, 97, dataBytes.length - 97);
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    protected byte[] parseResult(byte[] dataBytes) {
        if (SM2Mode.C1C2C3.equals(this.sm2Mode)) {
            return dataBytes;
        } else {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(dataBytes.length);
            byteArrayOutputStream.write(dataBytes, 0, 65);
            byteArrayOutputStream.write(dataBytes, dataBytes.length - 32, 32);
            byteArrayOutputStream.write(dataBytes, 65, dataBytes.length - 97);
            return byteArrayOutputStream.toByteArray();
        }
    }

    /**
     * The enum Sm 2 mode.
     */
    public enum SM2Mode {
        /**
         * C 1 c 2 c 3 sm 2 mode.
         */
        C1C2C3,
        /**
         * C 1 c 3 c 2 sm 2 mode.
         */
        C1C3C2
    }
}
