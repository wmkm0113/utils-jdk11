package org.nervousync.security.digest.impl;

import org.nervousync.security.SecureProvider;
import org.nervousync.security.config.CRCConfig;
import org.nervousync.exceptions.crypto.CryptoException;
import org.nervousync.utils.RawUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public final class CRCDigestProviderImpl extends SecureProvider {

    private transient static final Logger LOGGER = LoggerFactory.getLogger(CRCDigestProviderImpl.class);

    //  Current crc config
    private final CRCConfig crcConfig;
    //  Polynomial
    private final long polynomial;
    //  Initialize CRC Value
    private final long init;
    //  Check Value
    private final long check;
    //  Mask Value
    private final long mask;
    //  CRC Value
    private long crc;

    public CRCDigestProviderImpl(CRCConfig crcConfig) {
        this.crcConfig = crcConfig;
        if (this.crcConfig.isRefIn()) {
            this.polynomial = reverseBit(this.crcConfig.getPolynomial(), this.crcConfig.getBit());
            this.init = reverseBit(this.crcConfig.getInit(), this.crcConfig.getBit());
        } else {
            this.polynomial = (this.crcConfig.getBit() < 8)
                    ? (this.crcConfig.getPolynomial() << (8 - this.crcConfig.getBit()))
                    : this.crcConfig.getPolynomial();
            this.init = (this.crcConfig.getBit() < 8)
                    ? (this.crcConfig.getInit() << (8 - this.crcConfig.getBit()))
                    : this.crcConfig.getInit();
        }
        this.crc = this.init;
        if (this.crcConfig.isRefIn()) {
            this.check = 0x1L;
        } else {
            if (this.crcConfig.getBit() <= 8) {
                this.check = 0x80;
            } else {
                this.check = Double.valueOf(Math.pow(2, this.crcConfig.getBit() - 1)).longValue();
            }
        }
        if (this.crcConfig.getBit() <= 8) {
            this.mask = 0xFF;
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            while (stringBuilder.length() < this.crcConfig.getBit()) {
                stringBuilder.append("1");
            }
            this.mask = Long.valueOf(stringBuilder.toString(), 2);
        }
    }
    
    @Override
    public void append(byte[] dataBytes, int position, int length) throws CryptoException {
        if (dataBytes.length < (position + length)) {
            throw new CryptoException("Data bytes invalid!");
        }
        for (int i = position ; i < length ; i++) {
            long crc = (dataBytes[i] < 0) ? ((int)dataBytes[i]) + 256 : dataBytes[i];
            if (this.crcConfig.getBit() <= 8) {
                this.crc ^= crc;
            } else {
                this.crc ^= ((this.crcConfig.isRefIn() ? crc : (crc << (this.crcConfig.getBit() - 8))) & this.mask);
            }

            for (int j = 0; j < 8; j++) {
                if ((this.crc & this.check) > 0) {
                    this.crc = (this.crcConfig.isRefIn() ? (this.crc >>> 1) : (this.crc << 1)) ^ this.polynomial;
                } else {
                    this.crc = this.crcConfig.isRefIn() ? (this.crc >>> 1) : (this.crc << 1);
                }
            }
        }
        this.crc &= this.mask;
    }

    @Override
    public byte[] finish(byte[] dataBytes, int position, int length) throws CryptoException {
        this.append(dataBytes, position, length);
        if (this.crcConfig.getBit() < 8 && !this.crcConfig.isRefIn()) {
            this.crc >>>= (8 - this.crcConfig.getBit());
        }
        if (!Objects.equals(this.crcConfig.isRefIn(), this.crcConfig.isRefOut()) && this.crcConfig.isRefOut()) {
            //  Just using for CRC-12/UMTS
            this.crc &= this.mask;
            this.crc = (reverseBit(this.crc, Long.toString(this.crc, 2).length()) ^ this.crcConfig.getXorOut());
        } else {
            this.crc = (this.crc ^ this.crcConfig.getXorOut()) & this.mask;
        }

        byte[] result = new byte[8];
        RawUtils.writeLong(result, RawUtils.Endian.LITTLE, this.crc);
        this.reset();
        return result;
    }

    @Override
    public boolean verify(byte[] signature) throws CryptoException {
        if (signature == null || signature.length != 8) {
            return Boolean.FALSE;
        }
        return this.crc == RawUtils.readLong(signature, 0, RawUtils.Endian.LITTLE);
    }

    @Override
    public void reset() {
        this.crc = this.init;
    }

    /**
     * Reverse data
     * @param value     input data
     * @param bit       crc bit
     * @return          reversed data
     */
    private static long reverseBit(long value, int bit) {
        if (value < 0) {
            value += Math.pow(2, bit);
        }
        String reverseValue = new StringBuilder(Long.toString(value, 2)).reverse().toString();
        if (reverseValue.length() < bit) {
            StringBuilder stringBuilder = new StringBuilder(reverseValue);
            while (stringBuilder.length() < bit) {
                stringBuilder.append("0");
            }
            reverseValue = stringBuilder.toString();
        } else {
            reverseValue = reverseValue.substring(0, bit);
        }
        return Long.parseLong(reverseValue, 2);
    }
}