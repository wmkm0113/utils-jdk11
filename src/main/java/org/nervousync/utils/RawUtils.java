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

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import org.nervousync.commons.core.Globals;
import org.nervousync.exceptions.utils.DataInvalidException;
import org.nervousync.exceptions.zip.ZipException;

/**
 * RAW data process utils
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: Nov 28, 2017 5:34:55 PM $
 */
public final class RawUtils {

	private static final int DEFAULT_INDEX = 0;

	private RawUtils() {
	}

	/**
	 * Read boolean value from index 0 of data bytes
	 *
	 * @param dataBytes Data bytes
	 * @return <code>true</code> If value is 1 or <code>false</code> for otherwise
	 * @throws DataInvalidException If array index out of bounds
	 */
	public static boolean readBoolean(byte[] dataBytes) throws DataInvalidException {
		return readBoolean(dataBytes, DEFAULT_INDEX);
	}

	/**
	 * Read boolean value from the position of data bytes
	 *
	 * @param dataBytes Data bytes
	 * @param position  Position index
	 * @return <code>true</code> If value is 1 or <code>false</code> for otherwise
	 * @throws DataInvalidException If array index out of bounds
	 */
	public static boolean readBoolean(byte[] dataBytes, int position) throws DataInvalidException {
		if (dataBytes.length <= position) {
			throw new DataInvalidException(
					"Array index out of bounds! Array length: " + dataBytes.length + " position: " + position);
		}
		return dataBytes[position] == Globals.NERVOUSYNC_STATUS_TRUE;
	}

	/**
	 * Write boolean value to data bytes
	 *
	 * @param dataBytes Data bytes
	 * @param value     Boolean value
	 * @throws DataInvalidException If array index out of bounds
	 */
	public static void writeBoolean(byte[] dataBytes, boolean value) throws DataInvalidException {
		writeBoolean(dataBytes, DEFAULT_INDEX, value);
	}

	/**
	 * Write boolean value to target position of data bytes or do nothing if position is out of byte array length
	 *
	 * @param dataBytes Data bytes
	 * @param position  Position index
	 * @param value     Boolean value
	 * @throws DataInvalidException If array index out of bounds
	 */
	public static void writeBoolean(byte[] dataBytes, int position, boolean value) throws DataInvalidException {
		if (dataBytes.length <= position) {
			throw new DataInvalidException(
					"Array index out of bounds! Array length: " + dataBytes.length + " position: " + position);
		}
		dataBytes[position] = (byte) (value ? Globals.NERVOUSYNC_STATUS_TRUE : Globals.NERVOUSYNC_STATUS_FALSE);
	}

	/**
	 * Read short value from index 0 of data bytes
	 *
	 * @param dataBytes Data bytes
	 * @return Read short value
	 * @throws DataInvalidException If array index out of bounds
	 */
	public static short readShort(byte[] dataBytes) throws DataInvalidException {
		return readShort(dataBytes, DEFAULT_INDEX, Endian.BIG);
	}

	/**
	 * Read short value from index 0 of data bytes, endian by given
	 *
	 * @param dataBytes Data bytes
	 * @param endian    Endian
	 * @return Read short value
	 * @throws DataInvalidException If array index out of bounds or endian is invalid
	 */
	public static short readShort(byte[] dataBytes, Endian endian) throws DataInvalidException {
		return readShort(dataBytes, DEFAULT_INDEX, endian);
	}

	/**
	 * Read short value from position of data bytes, endian by given
	 *
	 * @param dataBytes Data bytes
	 * @param position  Position index
	 * @return Read short value
	 * @throws DataInvalidException If array index out of bounds
	 */
	public static short readShort(byte[] dataBytes, int position) throws DataInvalidException {
		return readShort(dataBytes, position, Endian.BIG);
	}

	/**
	 * Read short value from position of data bytes, endian by given
	 *
	 * @param dataBytes Data bytes
	 * @param position  Position index
	 * @param endian    Endian
	 * @return Read short value
	 * @throws DataInvalidException If array index out of bounds or endian is invalid
	 */
	public static short readShort(byte[] dataBytes, int position, Endian endian) throws DataInvalidException {
		if (dataBytes.length <= (position + 1)) {
			throw new DataInvalidException("Array index out of bounds! Array length: "
					+ dataBytes.length + " position: " + position + " length: 2");
		}
		switch (endian) {
			case BIG:
				return (short) ((dataBytes[position] & 0xFF) << 8 | (dataBytes[position + 1] & 0xFF));
			case LITTLE:
				return (short) ((dataBytes[position] & 0xFF) | (dataBytes[position + 1] & 0xFF) << 8);
			default:
				throw new DataInvalidException("Unknown endian type");
		}
	}

	/**
	 * Write short value to index 0 of data bytes
	 *
	 * @param dataBytes Data bytes
	 * @param value     Write value
	 * @throws DataInvalidException If array index out of bounds
	 */
	public static void writeShort(byte[] dataBytes, short value) throws DataInvalidException {
		writeShort(dataBytes, DEFAULT_INDEX, Endian.BIG, value);
	}

	/**
	 * Write short value to index 0 of data bytes, endian by given
	 *
	 * @param dataBytes Data bytes
	 * @param endian    Endian
	 * @param value     Write value
	 * @throws DataInvalidException If array index out of bounds or endian is invalid
	 */
	public static void writeShort(byte[] dataBytes, Endian endian, short value) throws DataInvalidException {
		writeShort(dataBytes, DEFAULT_INDEX, endian, value);
	}

	/**
	 * Write short value to position of data bytes
	 *
	 * @param dataBytes Data bytes
	 * @param position  Position index
	 * @param value     Write value
	 * @throws DataInvalidException If array index out of bounds
	 */
	public static void writeShort(byte[] dataBytes, int position, short value) throws DataInvalidException {
		writeShort(dataBytes, position, Endian.BIG, value);
	}

	/**
	 * Write short value to position of data bytes, endian by given
	 *
	 * @param dataBytes Data bytes
	 * @param position  Position index
	 * @param endian    Endian
	 * @param value     Write value
	 * @throws DataInvalidException If array index out of bounds or endian is invalid
	 */
	public static void writeShort(byte[] dataBytes, int position, Endian endian, short value)
			throws DataInvalidException {
		if (dataBytes.length <= (position + 1)) {
			throw new DataInvalidException("Array index out of bounds! Array length: "
					+ dataBytes.length + " position: " + position + " length: 2");
		}
		switch (endian) {
			case BIG:
				dataBytes[position] = (byte)(value >>> 8);
				dataBytes[position + 1] = (byte)(value & 0xFF);
				break;
			case LITTLE:
				dataBytes[position + 1] = (byte)(value >>> 8);
				dataBytes[position] = (byte)(value & 0xFF);
				break;
			default:
				throw new DataInvalidException("Unknown endian type");
		}
	}

	/**
	 * Read int value from index 0 of data bytes
	 *
	 * @param dataBytes Data bytes
	 * @return Read int value
	 * @throws DataInvalidException If array index out of bounds
	 */
	public static int readInt(byte[] dataBytes) throws DataInvalidException {
		return readInt(dataBytes, DEFAULT_INDEX, Endian.BIG);
	}

	/**
	 * Read int value from index 0 of data bytes, endian by given
	 *
	 * @param dataBytes Data bytes
	 * @param endian    Endian
	 * @return Read int value
	 * @throws DataInvalidException If array index out of bounds or endian is invalid
	 */
	public static int readInt(byte[] dataBytes, Endian endian) throws DataInvalidException {
		return readInt(dataBytes, DEFAULT_INDEX, endian);
	}

	/**
	 * Read int value from position of data bytes, endian by given
	 *
	 * @param dataBytes Data bytes
	 * @param position  Position index
	 * @return Read int value
	 * @throws DataInvalidException If array index out of bounds
	 */
	public static int readInt(byte[] dataBytes, int position) throws DataInvalidException {
		return readInt(dataBytes, position, Endian.BIG);
	}

	/**
	 * Read int value from position of data bytes, endian by given
	 *
	 * @param dataBytes Data bytes
	 * @param position  Position index
	 * @param endian    Endian
	 * @return Read int value
	 * @throws DataInvalidException If array index out of bounds or endian is invalid
	 */
	public static int readInt(byte[] dataBytes, int position, Endian endian) throws DataInvalidException {
		if (dataBytes.length <= (position + 3)) {
			throw new DataInvalidException("Array index out of bounds! Array length: "
					+ dataBytes.length + " position: " + position + " length: 4");
		}
		switch (endian) {
			case BIG:
				return ((dataBytes[position + 3] & 0xFF) | (dataBytes[position + 2] & 0xFF) << 8)
						| ((dataBytes[position + 1] & 0xFF) | (dataBytes[position] & 0xFF) << 8) << 16;
			case LITTLE:
				return ((dataBytes[position] & 0xFF) | (dataBytes[position + 1] & 0xFF) << 8)
						| ((dataBytes[position + 2] & 0xFF) | (dataBytes[position + 3] & 0xFF) << 8) << 16;
			default:
				throw new DataInvalidException("Unknown endian type");
		}
	}

	/**
	 * Write int value to index 0 of data bytes
	 *
	 * @param dataBytes Data bytes
	 * @param value     Write value
	 * @throws DataInvalidException If array index out of bounds
	 */
	public static void writeInt(byte[] dataBytes, int value) throws DataInvalidException {
		writeInt(dataBytes, DEFAULT_INDEX, Endian.BIG, value);
	}

	/**
	 * Write int value to index 0 of data bytes, endian by given
	 *
	 * @param dataBytes Data bytes
	 * @param endian    Endian
	 * @param value     Write value
	 * @throws DataInvalidException If array index out of bounds or endian is invalid
	 */
	public static void writeInt(byte[] dataBytes, Endian endian, int value) throws DataInvalidException {
		writeInt(dataBytes, DEFAULT_INDEX, endian, value);
	}

	/**
	 * Write int value to position of data bytes
	 *
	 * @param dataBytes Data bytes
	 * @param position  Position index
	 * @param value     Write value
	 * @throws DataInvalidException If array index out of bounds
	 */
	public static void writeInt(byte[] dataBytes, int position, int value) throws DataInvalidException {
		writeInt(dataBytes, position, Endian.BIG, value);
	}

	/**
	 * Write int value to position of data bytes, endian by given
	 *
	 * @param dataBytes Data bytes
	 * @param position  Position index
	 * @param endian    Endian
	 * @param value     Write value
	 * @throws DataInvalidException If array index out of bounds or endian is invalid
	 */
	public static void writeInt(byte[] dataBytes, int position, Endian endian, int value) throws DataInvalidException {
		if (dataBytes.length <= (position + 3)) {
			throw new DataInvalidException("Array index out of bounds! Array length: "
					+ dataBytes.length + " position: " + position + " length: 4");
		}
		int index;
		switch (endian) {
			case BIG:
				index = 0;
				while (true) {
					if (index == 3) {
						dataBytes[position + index] = (byte)(value & 0xFF);
						break;
					} else {
						dataBytes[position + index] = (byte)(value >>> ((3 - index) * 8));
					}
					index++;
				}
				break;
			case LITTLE:
				index = 0;
				while (index < 4) {
					if (index == 3) {
						dataBytes[position] = (byte)(value & 0xFF);
					} else {
						dataBytes[position + (3 - index)] = (byte)(value >>> ((3 - index) * 8));
					}
					index++;
				}
				break;
			default:
				throw new DataInvalidException("Unknown endian type");
		}
	}

	/**
	 * Read long value from index 0 of data bytes
	 *
	 * @param dataBytes Data bytes
	 * @return Read long value
	 * @throws DataInvalidException If array index out of bounds
	 */
	public static long readLong(byte[] dataBytes) throws DataInvalidException {
		return readLong(dataBytes, DEFAULT_INDEX, Endian.BIG);
	}

	/**
	 * Read long value from index 0 of data bytes, endian by given
	 *
	 * @param dataBytes Data bytes
	 * @param endian    Endian
	 * @return Read long value
	 * @throws DataInvalidException If array index out of bounds or endian is invalid
	 */
	public static long readLong(byte[] dataBytes, Endian endian) throws DataInvalidException {
		return readLong(dataBytes, DEFAULT_INDEX, endian);
	}

	/**
	 * Read long value from position of data bytes, endian by given
	 *
	 * @param dataBytes Data bytes
	 * @param position  Position index
	 * @return Read long value
	 * @throws DataInvalidException If array index out of bounds
	 */
	public static long readLong(byte[] dataBytes, int position) throws DataInvalidException {
		return readLong(dataBytes, position, Endian.BIG);
	}

	/**
	 * Read long value from position of data bytes, endian by given
	 *
	 * @param dataBytes Data bytes
	 * @param position  Position index
	 * @param endian    Endian
	 * @return Read long value
	 * @throws DataInvalidException If array index out of bounds or endian is invalid
	 */
	public static long readLong(byte[] dataBytes, int position, Endian endian) throws DataInvalidException {
		if (dataBytes.length <= (position + 7)) {
			throw new DataInvalidException("Array index out of bounds! Array length: "
					+ dataBytes.length + " position: " + position + " length: 8");
		}
		long readValue = 0L;
		int i;
		switch (endian) {
			case BIG:
				i = 0;
				do {
					readValue |= dataBytes[position + i] & 0xFF;
					readValue <<= 8;
					i++;
				} while (i != 7);
				break;
			case LITTLE:
				i = 8;
				do {
					i--;
					readValue |= dataBytes[i] & 0xFF;
					if (i != 0) {
						readValue <<= 8;
					}
				} while (i != 0);
				break;
			default:
				throw new DataInvalidException("Unknown endian type");
		}
		return readValue;
	}

	/**
	 * Write long value to index 0 of data bytes
	 *
	 * @param dataBytes Data bytes
	 * @param value     Write value
	 * @throws DataInvalidException If array index out of bounds
	 */
	public static void writeLong(byte[] dataBytes, long value) throws DataInvalidException {
		writeLong(dataBytes, DEFAULT_INDEX, Endian.BIG, value);
	}

	/**
	 * Write long value to index 0 of data bytes, endian by given
	 *
	 * @param dataBytes Data bytes
	 * @param endian    Endian
	 * @param value     Write value
	 * @throws DataInvalidException If array index out of bounds or endian is invalid
	 */
	public static void writeLong(byte[] dataBytes, Endian endian, long value) throws DataInvalidException {
		writeLong(dataBytes, DEFAULT_INDEX, endian, value);
	}

	/**
	 * Write long value to position of data bytes
	 *
	 * @param dataBytes Data bytes
	 * @param position  Position index
	 * @param value     Write value
	 * @throws DataInvalidException If array index out of bounds
	 */
	public static void writeLong(byte[] dataBytes, int position, long value) throws DataInvalidException {
		writeLong(dataBytes, position, Endian.BIG, value);
	}

	/**
	 * Write long value to position of data bytes, endian by given
	 *
	 * @param dataBytes Data bytes
	 * @param position  Position index
	 * @param endian    Endian
	 * @param value     Write value
	 * @throws DataInvalidException If array index out of bounds or endian is invalid
	 */
	public static void writeLong(byte[] dataBytes, int position, Endian endian, long value)
			throws DataInvalidException {
		if (dataBytes.length <= (position + 7)) {
			throw new DataInvalidException("Array index out of bounds! Array length: "
					+ dataBytes.length + " position: " + position + " length: 8");
		}
		int index;
		switch (endian) {
			case BIG:
				index = 0;
				while (true) {
					if (index == 7) {
						dataBytes[position + index] = (byte)(value & 0xFF);
						break;
					} else {
						dataBytes[position + index] = (byte)(value >>> ((7 - index) * 8));
					}
					index++;
				}
				break;
			case LITTLE:
				index = 0;
				while (index < 8) {
					if (index == 7) {
						dataBytes[position] = (byte)(value & 0xFF);
					} else {
						dataBytes[position + (7 - index)] = (byte)(value >>> ((7 - index) * 8));
					}
					index++;
				}
				break;
			default:
				throw new DataInvalidException("Unknown endian type");
		}
	}


	/**
	 * Read string from index 0 of data bytes
	 *
	 * @param dataBytes Data bytes
	 * @param length    Data length
	 * @return Read string
	 * @throws DataInvalidException If array index out of bounds
	 */
	public static String readString(byte[] dataBytes, int length) throws DataInvalidException {
		return readString(dataBytes, DEFAULT_INDEX, length, Globals.DEFAULT_ENCODING, Endian.BIG);
	}


	/**
	 * Read string from index 0 of data bytes, encoding by given
	 *
	 * @param dataBytes Data bytes
	 * @param length    Data length
	 * @param encoding  String encoding
	 * @return Read string
	 * @throws DataInvalidException If array index out of bounds or encoding didn't support
	 */
	public static String readString(byte[] dataBytes, int length, String encoding) throws DataInvalidException {
		return readString(dataBytes, DEFAULT_INDEX, length, encoding, Endian.BIG);
	}


	/**
	 * Read string from position of data bytes
	 *
	 * @param dataBytes Data bytes
	 * @param position  Position index
	 * @param length    Data length
	 * @return Read string
	 * @throws DataInvalidException If array index out of bounds
	 */
	public static String readString(byte[] dataBytes, int position, int length) throws DataInvalidException {
		return readString(dataBytes, position, length, Globals.DEFAULT_ENCODING, Endian.BIG);
	}


	/**
	 * Read string from index 0 of data bytes, endian by given
	 *
	 * @param dataBytes Data bytes
	 * @param length    Data length
	 * @param endian    Endian
	 * @return Read string
	 * @throws DataInvalidException If array index out of bounds, endian is invalid
	 */
	public static String readString(byte[] dataBytes, int length, Endian endian) throws DataInvalidException {
		return readString(dataBytes, DEFAULT_INDEX, length, Globals.DEFAULT_ENCODING, endian);
	}


	/**
	 * Read string from index 0 of data bytes, endian and encoding by given
	 *
	 * @param dataBytes Data bytes
	 * @param length    Data length
	 * @param encoding  String encoding
	 * @param endian    Endian
	 * @return Read string
	 * @throws DataInvalidException If array index out of bounds, endian is invalid or encoding not supported
	 */
	public static String readString(byte[] dataBytes, int length, String encoding, Endian endian)
			throws DataInvalidException {
		return readString(dataBytes, DEFAULT_INDEX, length, encoding, endian);
	}

	/**
	 * Read string from position of data bytes, endian by given
	 *
	 * @param dataBytes Data bytes
	 * @param position  Position index
	 * @param length    Data length
	 * @param endian    Endian
	 * @return Read string
	 * @throws DataInvalidException If array index out of bounds, endian is invalid
	 */
	public static String readString(byte[] dataBytes, int position, int length, Endian endian)
			throws DataInvalidException {
		return readString(dataBytes, position, length, Globals.DEFAULT_ENCODING, endian);
	}

	/**
	 * Read string from position of data bytes, endian and encoding by given
	 *
	 * @param dataBytes Data bytes
	 * @param position  Position index
	 * @param length    Data length
	 * @param encoding  String encoding
	 * @param endian    Endian
	 * @return Read string
	 * @throws DataInvalidException If array index out of bounds, endian is invalid or encoding not supported
	 */
	public static String readString(byte[] dataBytes, int position, int length, String encoding, Endian endian)
			throws DataInvalidException {
		if (position < 0 || length < 0 || dataBytes == null || dataBytes.length < (position + length)) {
			throw new DataInvalidException("Parameter invalid! ");
		}
		try {
			return new String(initBuffer(dataBytes, position, length, endian).array(), encoding);
		} catch (UnsupportedEncodingException e) {
			throw new DataInvalidException(e);
		}
	}

	/**
	 * Write string to index 0 of data bytes
	 *
	 * @param dataBytes Data bytes
	 * @param value     Write string
	 * @throws DataInvalidException If array index out of bounds
	 */
	public static void writeString(byte[] dataBytes, String value) throws DataInvalidException {
		writeString(dataBytes, DEFAULT_INDEX, Globals.DEFAULT_ENCODING, Endian.BIG, value);
	}

	/**
	 * Write string to target position of data bytes
	 *
	 * @param dataBytes Data bytes
	 * @param position  Position index
	 * @param value     Write string
	 * @throws DataInvalidException If array index out of bounds
	 */
	public static void writeString(byte[] dataBytes, int position, String value) throws DataInvalidException {
		writeString(dataBytes, position, Globals.DEFAULT_ENCODING, Endian.BIG, value);
	}

	/**
	 * Write string to index 0 of data bytes, endian by given
	 *
	 * @param dataBytes Data bytes
	 * @param endian    Endian
	 * @param value     Write string
	 * @throws DataInvalidException If array index out of bounds, endian is invalid
	 */
	public static void writeString(byte[] dataBytes, Endian endian, String value) throws DataInvalidException {
		writeString(dataBytes, DEFAULT_INDEX, Globals.DEFAULT_ENCODING, endian, value);
	}

	/**
	 * Write string to target position of data bytes, endian by given
	 *
	 * @param dataBytes Data bytes
	 * @param position  Position index
	 * @param endian    Endian
	 * @param value     Write string
	 * @throws DataInvalidException If array index out of bounds, endian is invalid
	 */
	public static void writeString(byte[] dataBytes, int position, Endian endian, String value) throws DataInvalidException {
		writeString(dataBytes, position, Globals.DEFAULT_ENCODING, endian, value);
	}

	/**
	 * Write string to target position of data bytes, encoding and endian by given
	 *
	 * @param dataBytes Data bytes
	 * @param position  Position index
	 * @param encoding  String encoding
	 * @param endian    Endian
	 * @param value     Write string
	 * @throws DataInvalidException If array index out of bounds, endian is invalid or encoding not supported
	 */
	public static void writeString(byte[] dataBytes, int position, String encoding, Endian endian, String value)
			throws DataInvalidException {
		if (StringUtils.isEmpty(value)) {
			return;
		}

		try {
			byte[] valueBytes = initBuffer(value.getBytes(encoding), endian).array();
			if ((position + valueBytes.length) <= dataBytes.length) {
				System.arraycopy(valueBytes, 0, dataBytes, position, valueBytes.length);
			} else {
				throw new DataInvalidException("Array index out of bounds! Array length: "
						+ dataBytes.length + " position: " + position + " length: " + valueBytes.length);
			}
		} catch (UnsupportedEncodingException e) {
			throw new DataInvalidException(e);
		}
	}

	/**
	 * Convert int to byte arrays
	 *
	 * @param value int value
	 * @return Convert byte arrays
	 */
	public static byte[] intToByteArray(int value) {
		return RawUtils.intToByteArray(value, 4);
	}

	/**
	 * Convert int value to byte arrays by given array size
	 *
	 * @param value  int value
	 * @param length array length
	 * @return Convert byte arrays
	 */
	public static byte[] intToByteArray(int value, int length) {
		if (length < 4) {
			throw new ZipException("Array size must lager than 4");
		}

		byte[] dataBytes = new byte[length];

		for (int i = 0 ; i < 4 ; i++) {
			if (i == 0) {
				dataBytes[i] = (byte)(value);
			} else {
				dataBytes[i] = (byte)(value >> (8 * i));
			}
		}

		return dataBytes;
	}

	/**
	 * Convert char arrays to byte arrays
	 *
	 * @param charArray char arrays
	 * @return Convert byte arrays
	 */
	public static byte[] charArrayToByteArray(char[] charArray) {
		if (charArray == null) {
			throw new NullPointerException();
		}
		
		byte[] bytes = new byte[charArray.length];
		
		for (int i = 0 ; i < charArray.length ; i++) {
			bytes[i] = (byte)charArray[i];
		}
		
		return bytes;
	}

	/**
	 * Convert bit arrays to byte
	 *
	 * @param bitArray bit arrays
	 * @return Convert byte value
	 * @throws ZipException bitArray is null or invalid
	 */
	public static byte bitArrayToByte(int[] bitArray) throws ZipException {
		if (bitArray == null) {
			throw new ZipException("Bit array is null!");
		}
		
		if (bitArray.length != 8) {
			throw new ZipException("Invalid bit array length!");
		}

		if (Arrays.stream(bitArray).anyMatch(bit -> (bit != 0 && bit != 1))) {
			throw new ZipException("Invalid bits provided!");
		}

		int calValue = 0;
		for (int i = 0 ; i < bitArray.length ; i++) {
			calValue += Math.pow(2, i) * bitArray[i];
		}
		return (byte)calValue;
	}

	/**
	 * The enum Endian.
	 */
	public enum Endian {
		/**
		 * Big endian.
		 */
		BIG,
		/**
		 * Little endian.
		 */
		LITTLE
	}

	/**
	 * Initialize ByteBuffer by given data bytes and endian
	 *
	 * @param dataBytes		Data array
	 * @param endian		Endian
	 * @return				Initialized ByteBuffer instance
	 * @throws DataInvalidException	If endian is invalid
	 */
	private static ByteBuffer initBuffer(byte[] dataBytes, Endian endian) throws DataInvalidException {
		return initBuffer(dataBytes, 0, dataBytes.length, endian);
	}

	/**
	 * Initialize ByteBuffer by given data bytes and endian
	 *
	 * @param dataBytes		Data array
	 * @param position 		Position index
	 * @param length 		Data length
	 * @param endian		Endian
	 * @return				Initialized ByteBuffer instance
	 * @throws DataInvalidException If array index out of bounds, endian is invalid
	 */
	private static ByteBuffer initBuffer(byte[] dataBytes, int position, int length, Endian endian)
			throws DataInvalidException {
		if (dataBytes.length <= (position + length)) {
			throw new DataInvalidException("Array index out of bounds! Array length: "
					+ dataBytes.length + " position: " + position + " length: " + length);
		}
		ByteBuffer byteBuffer = ByteBuffer.wrap(dataBytes, position, length);
		switch (endian) {
			case BIG:
				byteBuffer.order(ByteOrder.BIG_ENDIAN);
				break;
			case LITTLE:
				byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
				break;
			default:
				throw new DataInvalidException("Unknown endian type");
		}
		return byteBuffer;
	}
}
