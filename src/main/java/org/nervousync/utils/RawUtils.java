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

import org.nervousync.commons.core.Globals;
import org.nervousync.exceptions.zip.ZipException;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Nov 28, 2017 5:34:55 PM $
 */
public final class RawUtils {
	
	/**
	 * Read long value from little endian of byte arrays
	 * @param bytes			Byte arrays
	 * @param position		Begin position
	 * @return				Read long value
	 */
	public static long readLongFromLittleEndian(byte[] bytes, int position) {
		long readValue = 0L;
		int i = 8;
		while (true) {
			i--;
			readValue |= bytes[position + i] & 0xFF;
			if (i == 0) {
				break;
			} else {
				readValue <<= 8;
			}
		}
		return readValue;
	}

	/**
	 * Read long value from big endian of byte arrays
	 * @param bytes			Byte arrays
	 * @param position		Begin position
	 * @return				Read long value
	 */
	public static long readLongFromBigEndian(byte[] bytes, int position) {
		long readValue = 0L;
		int i = 0;
		while (true) {
			readValue |= bytes[position + i] & 0xFF;
			if (i == 7) {
				break;
			} else {
				readValue <<= 8;
				i++;
			}
		}
		return readValue;
	}
	
	/**
	 * Read short value from little endian of byte arrays
	 * @param bytes			Byte arrays
	 * @param position		Begin position
	 * @return				Read long value
	 */
	public static int readShortFromLittleEndian(byte[] bytes, int position) {
		return ((bytes[position] & 0xFF) | (bytes[position + 1] & 0xFF) << 8);
	}
	
	/**
	 * Read short value from big endian of byte arrays
	 * @param bytes			Byte arrays
	 * @param position		Begin position
	 * @return				Read long value
	 */
	public static int readShortFromBigEndian(byte[] bytes, int position) {
		return (bytes[position] & 0xFF) << 8 | (bytes[position + 1] & 0xFF);
	}

	/**
	 * Read int value from little endian of byte arrays
	 * @param bytes			Byte arrays
	 * @param position		Begin position
	 * @return				Read int value
	 */
	public static int readIntFromLittleEndian(byte[] bytes, int position) {
		return ((bytes[position] & 0xFF) | (bytes[position + 1] & 0xFF) << 8) 
				| ((bytes[position + 2] & 0xFF) | (bytes[position + 3] & 0xFF) << 8) << 16;
	}

	/**
	 * Read int value from big endian of byte arrays
	 * @param bytes			Byte arrays
	 * @param position		Begin position
	 * @return				Read int value
	 */
	public static int readIntFromBigEndian(byte[] bytes, int position) {
		return ((bytes[position + 3] & 0xFF) | (bytes[position + 2] & 0xFF) << 8) 
				| ((bytes[position + 1] & 0xFF) | (bytes[position] & 0xFF) << 8) << 16;
	}
	
	/**
	 * Read string value from little endian of byte arrays by default charset encoding
	 * @param bytes			Byte arrays
	 * @param position		Begin position
	 * @param length		Read length
	 * @return				Read String value
	 */
	public static String readStringFromLittleEndian(byte[] bytes, int position, int length) {
		return RawUtils.readStringFromLittleEndian(bytes, position, length, Globals.DEFAULT_ENCODING);
	}
	
	/**
	 * Read string value from little endian of byte arrays by given charset encoding
	 * @param bytes			Byte arrays
	 * @param position		Begin position
	 * @param length		Read length
	 * @param encoding		Charset encoding
	 * @return				Read String value or null if given length was out of array length or charset encoding does not supported
	 */
	public static String readStringFromLittleEndian(byte[] bytes, int position, int length, String encoding) {
		if (position < 0 || length < 0 || bytes == null 
				|| (position + length) > bytes.length) {
			return null;
		}
		byte[] readBuffer = new byte[length];
		System.arraycopy(bytes, position, readBuffer, 0, length);
		try {
			return new String(readBuffer, encoding);
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}
	
	/**
	 * Write string value from little endian of byte arrays by default charset encoding
	 * @param bytes			Byte arrays
	 * @param position		Begin position
	 * @param value			Write value
	 */
	public static void writeStringFromLittleEndian(byte[] bytes, int position, String value) {
		writeStringFromLittleEndian(bytes, position, value, Globals.DEFAULT_ENCODING);
	}
	
	/**
	 * Write string value from little endian of byte arrays by given charset encoding
	 * @param bytes			Byte arrays
	 * @param position		Begin position
	 * @param value			Write value
	 * @param encoding		Charset encoding
	 */
	public static void writeStringFromLittleEndian(byte[] bytes, int position, String value, String encoding) {
		if (value == null) {
			return;
		}
		
		byte[] valueBytes;
		try {
			valueBytes = value.getBytes(encoding);
		} catch (UnsupportedEncodingException e) {
			valueBytes = null;
		}
		if (valueBytes == null) {
			return;
		}
		if ((position + valueBytes.length) <= bytes.length) {
			System.arraycopy(valueBytes, 0, bytes, position, valueBytes.length);
		}
	}
	
	/**
	 * Write short value from little endian of byte arrays
	 * @param bytes			Byte arrays
	 * @param position		Begin position
	 * @param value			Short value 
	 */
	public static void writeShortFromLittleEndian(byte[] bytes, int position, short value) {
		bytes[position + 1] = (byte)(value >>> 8);
		bytes[position] = (byte)(value & 0xFF);
	}
	
	/**
	 * Write short value from big endian of byte arrays
	 * @param bytes			Byte arrays
	 * @param position		Begin position
	 * @param value			Short value 
	 */
	public static void writeShortFromBigEndian(byte[] bytes, int position, short value) {
		bytes[position] = (byte)(value >>> 8);
		bytes[position + 1] = (byte)(value & 0xFF);
	}
	
	/**
	 * Write int value from little endian of byte arrays
	 * @param bytes			Byte arrays
	 * @param position		Begin position
	 * @param value			int value 
	 */
	public static void writeIntFromLittleEndian(byte[] bytes, int position, int value) {
		int index = 0;
		while (index < 4) {
			if (index == 3) {
				bytes[position + (3 - index)] = (byte)(value & 0xFF);
			} else {
				bytes[position + (3 - index)] = (byte)(value >>> ((3 - index) * 8));
			}
			index++;
		}
	}
	
	/**
	 * Write int value from big endian of byte arrays
	 * @param bytes			Byte arrays
	 * @param position		Begin position
	 * @param value			int value 
	 */
	public static void writeIntFromBigEndian(byte[] bytes, int position, int value) {
		int index = 0;
		while (true) {
			if (index == 3) {
				bytes[position + index] = (byte)(value & 0xFF);
				break;
			} else {
				bytes[position + index] = (byte)(value >>> ((3 - index) * 8));
			}
			index++;
		}
	}
	
	/**
	 * Write long value from little endian of byte arrays
	 * @param bytes			Byte arrays
	 * @param position		Begin position
	 * @param value			long value 
	 */
	public static void writeLongFromLittleEndian(byte[] bytes, int position, long value) {
		int index = 0;
		while (index < 8) {
			if (index == 7) {
				bytes[position + (7 - index)] = (byte)(value & 0xFF);
			} else {
				bytes[position + (7 - index)] = (byte)(value >>> ((7 - index) * 8));
			}
			index++;
		}
	}
	
	/**
	 * Write long value from big endian of byte arrays
	 * @param bytes			Byte arrays
	 * @param position		Begin position
	 * @param value			long value 
	 */
	public static void writeLongFromBigEndian(byte[] bytes, int position, long value) {
		int index = 0;
		while (true) {
			if (index == 7) {
				bytes[position + index] = (byte)(value & 0xFF);
				break;
			} else {
				bytes[position + index] = (byte)(value >>> ((7 - index) * 8));
			}
			index++;
		}
	}
	
	/**
	 * Convert int to byte arrays
	 * @param value		int value
	 * @return			Convert byte arrays
	 */
	public static byte[] convertIntToByteArray(int value) {
		return RawUtils.convertIntToByteArray(value, 4, Globals.DEFAULT_VALUE_BOOLEAN);
	}
	
	/**
	 * Convert int to byte arrays by given array size
	 * @param value			int value
	 * @param arraySize		array size
	 * @return				Convert byte arrays
	 */
	public static byte[] convertIntToByteArray(int value, int arraySize) {
		return RawUtils.convertIntToByteArray(value, arraySize, Globals.DEFAULT_VALUE_BOOLEAN);
	}
	
	/**
	 * Prepare AES buffer
	 * @param nonce		nonce
	 * @return			Prepared buffer
	 */
	public static byte[] prepareAESBuffer(int nonce) {
		return RawUtils.convertIntToByteArray(nonce, 16, true);
	}
	
	/**
	 * Convert char arrays to byte arrays
	 * @param charArray		char arrays
	 * @return				Convert byte arrays
	 */
	public static byte[] convertCharArrayToByteArray(char[] charArray) {
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
	 * @param bitArray			bit arrays
	 * @return					Convert byte value
	 * @throws ZipException		bitArray is null or invalid
	 */
	public static byte convertBitArrayToByte(int[] bitArray) throws ZipException {
		if (bitArray == null) {
			throw new ZipException("Bit array is null!");
		}
		
		if (bitArray.length != 8) {
			throw new ZipException("Invalid bit array length!");
		}
		
		if (RawUtils.checkBitArray(bitArray)) {
			int calValue = 0;
			for (int i = 0 ; i < bitArray.length ; i++) {
				calValue += Math.pow(2, i) * bitArray[i];
			}
			
			return (byte)calValue;
		}
		
		throw new ZipException("Invalid bits provided!");
	}
	
	/**
	 * Check bit array is valid
	 * @param bitArray		bit array
	 * @return				Check result
	 */
	private static boolean checkBitArray(int[] bitArray) {
		for (int bit : bitArray) {
			if (bit != 0 && bit != 1) {
				return Globals.DEFAULT_VALUE_BOOLEAN;
			}
		}
		return true;
	}
	
	/**
	 * Convert int to byte arrays
	 * @param value				int value
	 * @param arraySize			array size
	 * @param appendZero		append zero if array is empty
	 * @return  converted byte array
	 */
	private static byte[] convertIntToByteArray(int value, int arraySize, boolean appendZero) {
		if (arraySize < 4) {
			throw new ZipException("Array size must lager than 4");
		}
		
		byte[] bitArray = new byte[arraySize];
		
		for (int i = 0 ; i < 4 ; i++) {
			if (i == 0) {
				bitArray[i] = (byte)(value);
			} else {
				bitArray[i] = (byte)(value >> (8 * i));
			}
		}
		
		if (appendZero) {
			for (int i = 4 ; i < arraySize ; i++) {
				bitArray[i] = 0;
			}
		}
		
		return bitArray;
	}
}
