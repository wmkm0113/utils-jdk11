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
package org.nervousync.utils;

import org.nervousync.commons.core.Globals;
import org.nervousync.uuid.UUIDTimer;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The type Uuid utils.
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: 12/21/2020 2:33 PM $
 */
public final class UUIDUtils {

	private static final SecureRandom SECURE_RANDOM = new SecureRandom();

	private static final AtomicInteger VERSION_1_COUNT = new AtomicInteger(0);

	/**
	 * The constant UUID_SEQUENCE.
	 */
	public static final String UUID_SEQUENCE = "org.nervousync.uuid.UUIDSequence";
	private static final String ASSIGNED_SEQUENCES = "org.nervousync.uuid.AssignedSequences";

	/**
	 * Convert uuid to big integer.
	 *
	 * @param uuid the uuid
	 * @return the big integer
	 */
	public static BigInteger convertUUIDtoBigInteger(UUID uuid) {
		byte[] dataBytes = ByteBuffer.allocate(16)
				.putLong(uuid.getMostSignificantBits())
				.putLong(uuid.getLeastSignificantBits())
				.array();
		return new BigInteger(dataBytes);
	}

	/**
	 * Version 1 uuid.
	 *
	 * @return the uuid
	 */
	public static UUID Version1() {
		return UUIDUtils.randomUUID(Version.Ver_1, new byte[0], null);
	}

	/**
	 * Version 2 uuid.
	 *
	 * @return the uuid
	 */
	public static UUID Version2() {
		return UUIDUtils.randomUUID(Version.Ver_2, new byte[0], new UUIDTimer(null));
	}

	/**
	 * Version 2 uuid.
	 *
	 * @param uuidTimer the uuid timer
	 * @return the uuid
	 */
	public static UUID Version2(UUIDTimer uuidTimer) {
		return UUIDUtils.randomUUID(Version.Ver_2, new byte[0], uuidTimer);
	}

	/**
	 * Version 3 uuid.
	 *
	 * @param dataBytes the data bytes
	 * @return the uuid
	 */
	public static UUID Version3(byte[] dataBytes) {
		return UUIDUtils.randomUUID(Version.Ver_3, dataBytes, null);
	}

	/**
	 * Version 4 uuid.
	 *
	 * @return the uuid
	 */
	public static UUID Version4() {
		return UUIDUtils.randomUUID(Version.Ver_4, new byte[0], null);
	}

	/**
	 * Version 5 uuid.
	 *
	 * @param dataBytes the data bytes
	 * @return the uuid
	 */
	public static UUID Version5(byte[] dataBytes) {
		return UUIDUtils.randomUUID(Version.Ver_5, dataBytes, null);
	}

	private static long convertToLong(byte[] buffer, int offset) {
		return buffer[offset] << 24 | (buffer[offset + 1] & 255) << 16
				| (buffer[offset + 2] & 255) << 8 | buffer[offset + 3] & 255;
	}

	private static UUID randomUUID(Version version, byte[] dataBytes, UUIDTimer uuidTimer) {
		long highBits = 0L;
		long lowBits = 0L;
		byte[] randomBytes;
		switch (version) {
			case Ver_1:
				highBits = highBits(((System.currentTimeMillis() * 10000) + 0x01b21dd213814000L)
						+ (VERSION_1_COUNT.incrementAndGet() % 10000));
				lowBits = lowBits(SystemUtils.localMac(), version);
				break;
			case Ver_2:
				highBits = highBits(uuidTimer.getTimestamp());
				lowBits = lowBits(SystemUtils.localMac(), uuidTimer);
				break;
			case Ver_3:
				try {
					randomBytes = MessageDigest.getInstance("MD5").digest(dataBytes);
				} catch (NoSuchAlgorithmException e) {
					randomBytes = new byte[0];
				}
				randomBytes[6] &= 0x0F;     /* clear version        */
				randomBytes[6] |= 0x30;     /* set to version 3     */
				randomBytes[8] &= 0x3F;     /* clear variant        */
				randomBytes[8] |= 0x80;     /* set to IETF variant  */
				highBits = highBits(randomBytes);
				lowBits = lowBits(randomBytes, version);
				break;
			case Ver_4:
				randomBytes = new byte[16];
				SECURE_RANDOM.nextBytes(randomBytes);
				randomBytes[6] &= 0x0F;     /* clear version        */
				randomBytes[6] |= 0x40;     /* set to version 4     */
				randomBytes[8] &= 0x3F;     /* clear variant        */
				randomBytes[8] |= 0x80;     /* set to IETF variant  */
				highBits = highBits(randomBytes);
				lowBits = lowBits(randomBytes, version);
				break;
			case Ver_5:
				try {
					randomBytes = MessageDigest.getInstance("SHA1").digest(dataBytes);
				} catch (NoSuchAlgorithmException e) {
					randomBytes = new byte[0];
				}
				randomBytes[6] &= 0x0F;     /* clear version        */
				randomBytes[6] |= 0x50;     /* set to version 5     */
				randomBytes[8] &= 0x3F;     /* clear variant        */
				randomBytes[8] |= 0x80;     /* set to IETF variant  */
				highBits = highBits(randomBytes);
				lowBits = lowBits(randomBytes, version);
				break;
		}

		return new UUID(highBits, lowBits);
	}

	private static long lowBits(byte[] dataBytes, UUIDTimer uuidTimer) {
		if (dataBytes.length != 6) {
			throw new IllegalArgumentException("Illegal offset, need room for 6 bytes");
		}
		long address = dataBytes[0] & 255;

		for(int i = 1; i < 6; ++i) {
			address = address << 8 | (long)(dataBytes[i] & 255);
		}

		int i = (int)(address >> 32);
		byte[] uuidBytes = new byte[16];
		int pos = 10;
		uuidBytes[pos++] = (byte)(i >> 8);
		uuidBytes[pos++] = (byte)i;
		i = (int)address;
		uuidBytes[pos++] = (byte)(i >> 24);
		uuidBytes[pos++] = (byte)(i >> 16);
		uuidBytes[pos++] = (byte)(i >> 8);
		uuidBytes[pos] = (byte)i;

		int sequence = uuidTimer.clockSequence();
		uuidBytes[8] = (byte)(sequence >> 8);
		uuidBytes[9] = (byte)sequence;

		long lowBits = (convertToLong(uuidBytes, 8) << 32) | (convertToLong(uuidBytes, 12) << 32 >>> 32);
		lowBits = lowBits << 2 >>> 2;
		lowBits |= -9223372036854775808L;
		return lowBits;
	}

	private static long lowBits(byte[] dataBytes, Version version) {
		long lowBits = 0L;
		if (Version.Ver_1.equals(version)) {
			if (dataBytes == null || dataBytes.length == 0) {
				dataBytes = new byte[6];
				SECURE_RANDOM.nextBytes(dataBytes);
			}
			final int length = Math.min(dataBytes.length, 6);
			final int srcPos = dataBytes.length >= 6 ? dataBytes.length - 6 : 0;
			final byte[] node = new byte[]{(byte) 0x80, 0, 0, 0, 0, 0, 0, 0};
			System.arraycopy(dataBytes, srcPos, node, 2, length);
			final ByteBuffer byteBuffer = ByteBuffer.wrap(node);
			String assigned = System.getProperty(ASSIGNED_SEQUENCES, Globals.DEFAULT_VALUE_STRING);
			long[] sequences;
			if (StringUtils.isEmpty(assigned)) {
				sequences = new long[0];
			} else {
				final String[] array =
						StringUtils.tokenizeToStringArray(assigned, Globals.DEFAULT_SPLIT_SEPARATOR);
				sequences = new long[array.length];
				final AtomicInteger index = new AtomicInteger(0);
				Arrays.stream(array).forEach(splitItem ->
						sequences[index.getAndIncrement()] = Long.parseLong(splitItem));
			}

			long rand = Long.parseLong(System.getProperty(UUID_SEQUENCE, "0"));
			if (rand == 0L) {
				rand = SECURE_RANDOM.nextLong();
			}
			rand &= 0x3FFF;
			boolean duplicate;
			do {
				duplicate = false;
				for (final long sequence : sequences) {
					if (sequence == rand) {
						duplicate = true;
						break;
					}
				}
				if (duplicate) {
					rand = (rand + 1) & 0x3FFF;
				}
			} while (duplicate);
			assigned = StringUtils.isEmpty(assigned) ? Long.toString(rand) : assigned + ',' + rand;
			System.setProperty(ASSIGNED_SEQUENCES, assigned);

			lowBits = byteBuffer.getLong() | rand << 48;
		} else {
			for (int index = 8 ; index < 16 ; index++) {
				lowBits = (lowBits << 8) | (dataBytes[index] & 0xFF);
			}
		}
		return lowBits;
	}

	private static long highBits(byte[] randomBytes) {
		long highBits = 0L;
		for (int i = 0 ; i < 8 ; i++) {
			highBits = (highBits << 8) | (randomBytes[i] & 0xFF);
		}
		return highBits;
	}

	private static long highBits(long currentTimeMillis) {
		return ((currentTimeMillis & 0xFFFFFFFFL) << 32)
				| ((currentTimeMillis & 0xFFFF00000000L) >> 16)
				| 0x1000L
				| ((currentTimeMillis & 0xFFF000000000000L) >> 48);
	}

	private enum Version {
		/**
		 * Ver 1 version.
		 */
		Ver_1,
		/**
		 * Ver 2 version.
		 */
		Ver_2,
		/**
		 * Ver 3 version.
		 */
		Ver_3,
		/**
		 * Ver 4 version.
		 */
		Ver_4,
		/**
		 * Ver 5 version.
		 */
		Ver_5
	}
}
