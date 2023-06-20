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
		return readShort(dataBytes, DEFAULT_INDEX);
	}

	/**
	 * Read short value from index 0 of data bytes, endian by given
	 *
	 * @param dataBytes Data bytes
	 * @param byteOrder ByteOrder
	 * @return Read short value
	 * @throws DataInvalidException If array index out of bounds or endian is invalid
	 */
	public static short readShort(byte[] dataBytes, ByteOrder byteOrder) throws DataInvalidException {
		return readShort(dataBytes, DEFAULT_INDEX, byteOrder);
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
		return readShort(dataBytes, position, ByteOrder.BIG_ENDIAN);
	}

	/**
	 * Read short value from position of data bytes, endian by given
	 *
	 * @param dataBytes Data bytes
	 * @param position  Position index
	 * @param byteOrder ByteOrder
	 * @return Read short value
	 * @throws DataInvalidException If array index out of bounds or endian is invalid
	 */
	public static short readShort(byte[] dataBytes, int position, ByteOrder byteOrder) throws DataInvalidException {
		return (short) readNumber(dataBytes, position, byteOrder, 2);
	}

	/**
	 * Write short value to index 0 of data bytes
	 *
	 * @param dataBytes Data bytes
	 * @param value     Write value
	 * @throws DataInvalidException If array index out of bounds
	 */
	public static void writeShort(byte[] dataBytes, short value) throws DataInvalidException {
		writeShort(dataBytes, DEFAULT_INDEX, value);
	}

	/**
	 * Write short value to index 0 of data bytes, endian by given
	 *
	 * @param dataBytes Data bytes
	 * @param byteOrder ByteOrder
	 * @param value     Write value
	 * @throws DataInvalidException If array index out of bounds or endian is invalid
	 */
	public static void writeShort(byte[] dataBytes, ByteOrder byteOrder, short value) throws DataInvalidException {
		writeShort(dataBytes, DEFAULT_INDEX, byteOrder, value);
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
		writeShort(dataBytes, position, ByteOrder.BIG_ENDIAN, value);
	}

	/**
	 * Write short value to position of data bytes, endian by given
	 *
	 * @param dataBytes Data bytes
	 * @param position  Position index
	 * @param byteOrder ByteOrder
	 * @param value     Write value
	 * @throws DataInvalidException If array index out of bounds or endian is invalid
	 */
	public static void writeShort(byte[] dataBytes, int position, ByteOrder byteOrder, short value)
			throws DataInvalidException {
		writeNumber(dataBytes, position, Short.SIZE, byteOrder, value);
	}

	/**
	 * Read int value from index 0 of data bytes
	 *
	 * @param dataBytes Data bytes
	 * @return Read int value
	 * @throws DataInvalidException If array index out of bounds
	 */
	public static int readInt(byte[] dataBytes) throws DataInvalidException {
		return readInt(dataBytes, DEFAULT_INDEX);
	}

	/**
	 * Read int value from index 0 of data bytes, endian by given
	 *
	 * @param dataBytes Data bytes
	 * @param byteOrder ByteOrder
	 * @return Read int value
	 * @throws DataInvalidException If array index out of bounds or endian is invalid
	 */
	public static int readInt(byte[] dataBytes, ByteOrder byteOrder) throws DataInvalidException {
		return readInt(dataBytes, DEFAULT_INDEX, byteOrder);
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
		return readInt(dataBytes, position, ByteOrder.BIG_ENDIAN);
	}

	/**
	 * Read int value from position of data bytes, endian by given
	 *
	 * @param dataBytes Data bytes
	 * @param position  Position index
	 * @param byteOrder ByteOrder
	 * @return Read int value
	 * @throws DataInvalidException If array index out of bounds or endian is invalid
	 */
	public static int readInt(byte[] dataBytes, int position, ByteOrder byteOrder) throws DataInvalidException {
		return (int) readNumber(dataBytes, position, byteOrder, 4);
	}

	/**
	 * Write int value to index 0 of data bytes
	 *
	 * @param dataBytes Data bytes
	 * @param value     Write value
	 * @throws DataInvalidException If array index out of bounds
	 */
	public static void writeInt(byte[] dataBytes, int value) throws DataInvalidException {
		writeInt(dataBytes, DEFAULT_INDEX, ByteOrder.BIG_ENDIAN, value);
	}

	/**
	 * Write int value to index 0 of data bytes, endian by given
	 *
	 * @param dataBytes Data bytes
	 * @param byteOrder ByteOrder
	 * @param value     Write value
	 * @throws DataInvalidException If array index out of bounds or endian is invalid
	 */
	public static void writeInt(byte[] dataBytes, ByteOrder byteOrder, int value) throws DataInvalidException {
		writeInt(dataBytes, DEFAULT_INDEX, byteOrder, value);
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
		writeInt(dataBytes, position, ByteOrder.BIG_ENDIAN, value);
	}

	/**
	 * Write int value to position of data bytes, endian by given
	 *
	 * @param dataBytes Data bytes
	 * @param position  Position index
	 * @param byteOrder ByteOrder
	 * @param value     Write value
	 * @throws DataInvalidException If array index out of bounds or endian is invalid
	 */
	public static void writeInt(byte[] dataBytes, int position, ByteOrder byteOrder, int value) throws DataInvalidException {
		writeNumber(dataBytes, position, Integer.SIZE, byteOrder, value);
	}

	/**
	 * Read long value from index 0 of data bytes
	 *
	 * @param dataBytes Data bytes
	 * @return Read long value
	 * @throws DataInvalidException If array index out of bounds
	 */
	public static long readLong(byte[] dataBytes) throws DataInvalidException {
		return readLong(dataBytes, DEFAULT_INDEX);
	}

	/**
	 * Read long value from index 0 of data bytes, endian by given
	 *
	 * @param dataBytes Data bytes
	 * @param byteOrder ByteOrder
	 * @return Read long value
	 * @throws DataInvalidException If array index out of bounds or endian is invalid
	 */
	public static long readLong(byte[] dataBytes, ByteOrder byteOrder) throws DataInvalidException {
		return readLong(dataBytes, DEFAULT_INDEX, byteOrder);
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
		return readLong(dataBytes, position, ByteOrder.BIG_ENDIAN);
	}

	/**
	 * Read long value from position of data bytes, endian by given
	 *
	 * @param dataBytes Data bytes
	 * @param position  Position index
	 * @param byteOrder ByteOrder
	 * @return Read long value
	 * @throws DataInvalidException If array index out of bounds or endian is invalid
	 */
	public static long readLong(byte[] dataBytes, int position, ByteOrder byteOrder) throws DataInvalidException {
		return (long) readNumber(dataBytes, position, byteOrder, 8);
	}

	/**
	 * Write long value to index 0 of data bytes
	 *
	 * @param dataBytes Data bytes
	 * @param value     Write value
	 * @throws DataInvalidException If array index out of bounds
	 */
	public static void writeLong(byte[] dataBytes, long value) throws DataInvalidException {
		writeLong(dataBytes, DEFAULT_INDEX, ByteOrder.BIG_ENDIAN, value);
	}

	/**
	 * Write long value to index 0 of data bytes, endian by given
	 *
	 * @param dataBytes Data bytes
	 * @param byteOrder ByteOrder
	 * @param value     Write value
	 * @throws DataInvalidException If array index out of bounds or endian is invalid
	 */
	public static void writeLong(byte[] dataBytes, ByteOrder byteOrder, long value) throws DataInvalidException {
		writeLong(dataBytes, DEFAULT_INDEX, byteOrder, value);
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
		writeLong(dataBytes, position, ByteOrder.BIG_ENDIAN, value);
	}

	/**
	 * Write long value to position of data bytes, endian by given
	 *
	 * @param dataBytes Data bytes
	 * @param position  Position index
	 * @param byteOrder ByteOrder
	 * @param value     Write value
	 * @throws DataInvalidException If array index out of bounds or endian is invalid
	 */
	public static void writeLong(byte[] dataBytes, int position, ByteOrder byteOrder, long value)
			throws DataInvalidException {
		writeNumber(dataBytes, position, Long.SIZE, byteOrder, value);
	}

	/**
	 * Read string from index 0 of data bytes
	 *
	 * @param dataBytes Data bytes
	 * @return Read string
	 * @throws DataInvalidException If array index out of bounds
	 */
	public static String readString(byte[] dataBytes) throws DataInvalidException {
		return readString(dataBytes, Globals.DEFAULT_VALUE_INT);
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
		return readString(dataBytes, DEFAULT_INDEX, length);
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
		return readString(dataBytes, position, length, ByteOrder.BIG_ENDIAN);
	}

	/**
	 * Read string from index 0 of data bytes, endian by given
	 *
	 * @param dataBytes Data bytes
	 * @param length    Data length
	 * @param byteOrder ByteOrder
	 * @return Read string
	 * @throws DataInvalidException If array index out of bounds, endian is invalid
	 */
	public static String readString(byte[] dataBytes, int length, ByteOrder byteOrder) throws DataInvalidException {
		return readString(dataBytes, length, Globals.DEFAULT_ENCODING, byteOrder);
	}

	/**
	 * Read string from position of data bytes, endian by given
	 *
	 * @param dataBytes Data bytes
	 * @param position  Position index
	 * @param length    Data length
	 * @param byteOrder ByteOrder
	 * @return Read string
	 * @throws DataInvalidException If array index out of bounds, endian is invalid
	 */
	public static String readString(byte[] dataBytes, int position, int length, ByteOrder byteOrder)
			throws DataInvalidException {
		return readString(dataBytes, position, length, Globals.DEFAULT_ENCODING, byteOrder);
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
		return readString(dataBytes, length, encoding, ByteOrder.BIG_ENDIAN);
	}

	/**
	 * Read string from index 0 of data bytes, encoding by given
	 *
	 * @param dataBytes Data bytes
	 * @param position  Position index
	 * @param length    Data length
	 * @param encoding  String encoding
	 * @return Read string
	 * @throws DataInvalidException If array index out of bounds or encoding didn't support
	 */
	public static String readString(byte[] dataBytes, int position, int length, String encoding) throws DataInvalidException {
		return readString(dataBytes, position, length, encoding, ByteOrder.BIG_ENDIAN);
	}

	/**
	 * Read string from index 0 of data bytes, endian and encoding by given
	 *
	 * @param dataBytes Data bytes
	 * @param length    Data length
	 * @param encoding  String encoding
	 * @param byteOrder ByteOrder
	 * @return Read string
	 * @throws DataInvalidException If array index out of bounds, endian is invalid or encoding not supported
	 */
	public static String readString(byte[] dataBytes, int length, String encoding, ByteOrder byteOrder)
			throws DataInvalidException {
		return readString(dataBytes, DEFAULT_INDEX, length, encoding, byteOrder);
	}

	/**
	 * Read string from position of data bytes, endian and encoding by given
	 *
	 * @param dataBytes Data bytes
	 * @param position  Position index
	 * @param length    Data length
	 * @param encoding  String encoding
	 * @param byteOrder ByteOrder
	 * @return Read string
	 * @throws DataInvalidException If array index out of bounds, endian is invalid or encoding not supported
	 */
	public static String readString(byte[] dataBytes, int position, int length, String encoding, ByteOrder byteOrder)
			throws DataInvalidException {
		if (position < 0 || dataBytes == null) {
			throw new DataInvalidException("Parameter invalid! ");
		}
		int readLength = (length == Globals.DEFAULT_VALUE_INT) ? dataBytes.length - position : length;
		if (dataBytes.length < (position + readLength)) {
			throw new DataInvalidException("No enough data to read! ");
		}
		try {
			byte[] readBytes = new byte[readLength];
			initBuffer(dataBytes, position, readLength, byteOrder).get(readBytes);
			return new String(readBytes, encoding);
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
		writeString(dataBytes, DEFAULT_INDEX, Globals.DEFAULT_ENCODING, ByteOrder.BIG_ENDIAN, value);
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
		writeString(dataBytes, position, Globals.DEFAULT_ENCODING, ByteOrder.BIG_ENDIAN, value);
	}

	/**
	 * Write string to index 0 of data bytes, endian by given
	 *
	 * @param dataBytes Data bytes
	 * @param byteOrder ByteOrder
	 * @param value     Write string
	 * @throws DataInvalidException If array index out of bounds, endian is invalid
	 */
	public static void writeString(byte[] dataBytes, ByteOrder byteOrder, String value) throws DataInvalidException {
		writeString(dataBytes, DEFAULT_INDEX, byteOrder, value);
	}

	/**
	 * Write string to target position of data bytes, endian by given
	 *
	 * @param dataBytes Data bytes
	 * @param position  Position index
	 * @param byteOrder ByteOrder
	 * @param value     Write string
	 * @throws DataInvalidException If array index out of bounds, endian is invalid
	 */
	public static void writeString(byte[] dataBytes, int position, ByteOrder byteOrder, String value) throws DataInvalidException {
		writeString(dataBytes, position, Globals.DEFAULT_ENCODING, byteOrder, value);
	}

	/**
	 * Write string to index 0 of data bytes
	 *
	 * @param dataBytes Data bytes
	 * @param value     Write string
	 * @throws DataInvalidException If array index out of bounds
	 */
	public static void writeString(byte[] dataBytes, String value, String encoding) throws DataInvalidException {
		writeString(dataBytes, ByteOrder.BIG_ENDIAN, value, encoding);
	}

	/**
	 * Write string to target position of data bytes
	 *
	 * @param dataBytes Data bytes
	 * @param position  Position index
	 * @param value     Write string
	 * @throws DataInvalidException If array index out of bounds
	 */
	public static void writeString(byte[] dataBytes, int position, String value, String encoding) throws DataInvalidException {
		writeString(dataBytes, position, encoding, ByteOrder.BIG_ENDIAN, value);
	}

	/**
	 * Write string to index 0 of data bytes, endian by given
	 *
	 * @param dataBytes Data bytes
	 * @param byteOrder ByteOrder
	 * @param value     Write string
	 * @throws DataInvalidException If array index out of bounds, endian is invalid
	 */
	public static void writeString(byte[] dataBytes, ByteOrder byteOrder, String value, String encoding) throws DataInvalidException {
		writeString(dataBytes, DEFAULT_INDEX, encoding, byteOrder, value);
	}

	/**
	 * Write string to target position of data bytes, encoding and endian by given
	 *
	 * @param dataBytes Data bytes
	 * @param position  Position index
	 * @param encoding  String encoding
	 * @param byteOrder ByteOrder
	 * @param value     Write string
	 * @throws DataInvalidException If array index out of bounds, endian is invalid or encoding not supported
	 */
	public static void writeString(byte[] dataBytes, int position, String encoding, ByteOrder byteOrder, String value)
			throws DataInvalidException {
		if (StringUtils.isEmpty(value)) {
			return;
		}

		try {
			byte[] valueBytes = initBuffer(value.getBytes(encoding), byteOrder).array();
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
	 * Initialize ByteBuffer by given data bytes and endian
	 *
	 * @param dataBytes		Data array
	 * @param byteOrder 	ByteOrder
	 * @return				Initialized ByteBuffer instance
	 * @throws DataInvalidException	If endian is invalid
	 */
	private static ByteBuffer initBuffer(byte[] dataBytes, ByteOrder byteOrder) throws DataInvalidException {
		return initBuffer(dataBytes, 0, dataBytes.length, byteOrder);
	}

	/**
	 * Initialize ByteBuffer by given data bytes and endian
	 *
	 * @param dataBytes		Data array
	 * @param position 		Position index
	 * @param length 		Data length
	 * @param byteOrder		ByteOrder
	 * @return				Initialized ByteBuffer instance
	 * @throws DataInvalidException If array index out of bounds, endian is invalid
	 */
	private static ByteBuffer initBuffer(byte[] dataBytes, int position, int length, ByteOrder byteOrder)
			throws DataInvalidException {
		if (dataBytes.length < (position + length)) {
			throw new DataInvalidException("Array index out of bounds! Array length: "
					+ dataBytes.length + " position: " + position + " length: " + length);
		}
		return ByteBuffer.wrap(dataBytes, position, length).order(byteOrder);
	}

	private static void writeNumber(final byte[] dataBytes, final int position, final int dataSize,
									final ByteOrder byteOrder, final Object value) throws DataInvalidException {
		if (dataSize % Byte.SIZE != 0) {
			throw new DataInvalidException("Data size invalid.");
		}
		int dataLength = dataSize / Byte.SIZE;
		if (dataBytes.length < (position + dataLength)) {
			throw new DataInvalidException("Array index out of bounds! Array length: "
					+ dataBytes.length + " position: " + position + " length: " + dataLength);
		}
		final ByteBuffer byteBuffer = initBuffer(dataBytes, position, dataLength, byteOrder);
		switch (dataSize) {
			case Short.SIZE:
				byteBuffer.putShort((short) value);
				break;
			case Integer.SIZE:
				byteBuffer.putInt((int) value);
				break;
			case Long.SIZE:
				byteBuffer.putLong((long) value);
				break;
			default:
				throw new DataInvalidException("Unknown data length");
		}
	}

	private static Object readNumber(final byte[] dataBytes, final int position, final ByteOrder byteOrder,
									 final int dataLength) throws DataInvalidException {
		if (dataBytes.length < (position + dataLength)) {
			throw new DataInvalidException("Array index out of bounds! Array length: "
					+ dataBytes.length + " position: " + position + " length: 8");
		}
		final ByteBuffer byteBuffer = initBuffer(dataBytes, position, dataLength, byteOrder);
		switch (dataLength) {
			case 2:
				return byteBuffer.getShort();
			case 4:
				return byteBuffer.getInt();
			case 8:
				return byteBuffer.getLong();
			default:
				throw new DataInvalidException("Unknown data length");
		}
	}
}
