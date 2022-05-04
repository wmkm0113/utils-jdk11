package org.nervousync.security;

import org.nervousync.exceptions.crypto.CryptoException;
import org.nervousync.utils.RawUtils;
import org.nervousync.utils.SecurityUtils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * The type Secure provider.
 */
public abstract class SecureProvider {

    /**
     * Append.
     *
     * @param strIn the str in
     * @throws CryptoException the crypto exception
     */
    public final void append(String strIn) throws CryptoException {
        this.append(strIn.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Append.
     *
     * @param dataBytes the data bytes
     * @throws CryptoException the crypto exception
     */
    public final void append(byte[] dataBytes) throws CryptoException {
        this.append(dataBytes, 0, dataBytes.length);
    }

    /**
     * Append.
     *
     * @param inBuffer the in buffer
     * @throws CryptoException the crypto exception
     */
    public final void append(ByteBuffer inBuffer) throws CryptoException {
        this.append(inBuffer.array());
    }

    /**
     * Finish byte [ ].
     *
     * @return the byte [ ]
     * @throws CryptoException the crypto exception
     */
    public final byte[] finish() throws CryptoException {
        return this.finish(new byte[0], 0, 0);
    }

    /**
     * Finish byte [ ].
     *
     * @param strIn the str in
     * @return the byte [ ]
     * @throws CryptoException the crypto exception
     */
    public final byte[] finish(String strIn) throws CryptoException {
        return this.finish(strIn.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Finish byte [ ].
     *
     * @param dataBytes the data bytes
     * @return the byte [ ]
     * @throws CryptoException the crypto exception
     */
    public final byte[] finish(byte[] dataBytes) throws CryptoException {
        return this.finish(dataBytes, 0, dataBytes.length);
    }

    /**
     * Finish byte [ ].
     *
     * @param inBuffer the in buffer
     * @return the byte [ ]
     * @throws CryptoException the crypto exception
     */
    public final byte[] finish(ByteBuffer inBuffer) throws CryptoException {
        return this.finish(inBuffer.array());
    }

    /**
     * Append.
     *
     * @param dataBytes the data bytes
     * @param position  the position
     * @param length    the length
     * @throws CryptoException the crypto exception
     */
    public abstract void append(byte[] dataBytes, int position, int length) throws CryptoException;

    /**
     * Finish byte [ ].
     *
     * @param dataBytes the data bytes
     * @param position  the position
     * @param length    the length
     * @return the byte [ ]
     * @throws CryptoException the crypto exception
     */
    public abstract byte[] finish(byte[] dataBytes, int position, int length) throws CryptoException;

    /**
     * Verify boolean.
     *
     * @param signature the signature
     * @return the boolean
     * @throws CryptoException the crypto exception
     */
    public abstract boolean verify(byte[] signature) throws CryptoException;

    /**
     * Reset.
     *
     * @throws CryptoException the crypto exception
     */
    public abstract void reset() throws CryptoException;

    /**
     * Convert crc result from byte array to string
     *
     * @param algorithm CRC algorithm
     * @param result    CRC result byte array
     * @return Converted result
     * @throws CryptoException CRC algorithm not found
     */
    public static String CRCResult(String algorithm, byte[] result) throws CryptoException {
        return SecurityUtils.crcConfig(algorithm)
                .map(crcConfig -> {
                    long crc = RawUtils.readLong(result, RawUtils.Endian.LITTLE);
                    StringBuilder stringBuilder = new StringBuilder(Long.toString(crc, 16));
                    while (stringBuilder.length() < crcConfig.getOutLength()) {
                        stringBuilder.insert(0, "0");
                    }
                    return "0x" + stringBuilder;
                })
                .orElseThrow(() -> new CryptoException("Unknown algorithm: " + algorithm));
    }
}
