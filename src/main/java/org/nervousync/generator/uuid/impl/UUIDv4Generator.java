package org.nervousync.generator.uuid.impl;

import org.nervousync.annotations.generator.GeneratorProvider;
import org.nervousync.generator.uuid.UUIDGenerator;
import org.nervousync.utils.IDUtils;

import java.security.SecureRandom;
import java.util.UUID;

/**
 * The type Uui dv 4 generator.
 */
@GeneratorProvider(IDUtils.UUIDv4)
public final class UUIDv4Generator extends UUIDGenerator {

    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public Object random() {
        byte[] randomBytes = new byte[16];
        this.secureRandom.nextBytes(randomBytes);
        randomBytes[6] &= 0x0F;     /* clear version        */
        randomBytes[6] |= 0x40;     /* set to version 4     */
        randomBytes[8] &= 0x3F;     /* clear variant        */
        randomBytes[8] |= 0x80;     /* set to IETF variant  */
        return new UUID(super.highBits(randomBytes), super.lowBits(randomBytes)).toString();
    }

    @Override
    public Object random(byte[] dataBytes) {
        return this.random();
    }
}
