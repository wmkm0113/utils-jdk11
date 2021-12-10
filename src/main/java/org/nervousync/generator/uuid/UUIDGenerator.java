package org.nervousync.generator.uuid;

import org.nervousync.generator.IGenerator;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.UUID;

public abstract class UUIDGenerator implements IGenerator {

    /**
     * Convert uuid to big integer.
     *
     * @param uuid the uuid
     * @return the big integer
     */
    public static BigInteger UUIDtoBigInteger(UUID uuid) {
        byte[] dataBytes = ByteBuffer.allocate(16)
                .putLong(uuid.getMostSignificantBits())
                .putLong(uuid.getLeastSignificantBits())
                .array();
        return new BigInteger(dataBytes);
    }

    protected final long highBits(byte[] randomBytes) {
        long highBits = 0L;
        for (int i = 0 ; i < 8 ; i++) {
            highBits = (highBits << 8) | (randomBytes[i] & 0xFF);
        }
        return highBits;
    }

    protected final long highBits(long currentTimeMillis) {
        return ((currentTimeMillis & 0xFFFFFFFFL) << 32)
                | ((currentTimeMillis & 0xFFFF00000000L) >> 16)
                | 0x1000L
                | ((currentTimeMillis & 0xFFF000000000000L) >> 48);
    }

    protected long lowBits(byte[] dataBytes) {
        long lowBits = 0L;
        for (int index = 8 ; index < 16 ; index++) {
            lowBits = (lowBits << 8) | (dataBytes[index] & 0xFF);
        }
        return lowBits;
    }
}
