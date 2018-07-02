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
package com.nervousync.commons.zip.operator;

import java.io.UnsupportedEncodingException;

import com.nervousync.commons.core.Globals;
import com.nervousync.exceptions.zip.ZipException;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Nov 28, 2017 5:34:55 PM $
 */
public final class RawOperator {
	
	/**
	 * 
	 * @param bytes
	 * @param position
	 * @return
	 */
	public static short readShortFromBigEndian(byte[] bytes, int position) {
		short readValue = 0;
		
		readValue |= bytes[position] & 0xFF;
		readValue <<= 8;
		readValue |= bytes[position + 1] & 0xFF;
		
		return readValue;
	}

	/**
	 * 
	 * @param bytes
	 * @param position
	 * @return
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
	 * 
	 * @param bytes
	 * @param position
	 * @return
	 */
	public static int readShortFromLittleEndian(byte[] bytes, int position) {
		return (bytes[position] & 0xFF) | (bytes[position + 1] & 0xFF) << 8;
	}
	
	/**
	 * 
	 * @param bytes
	 * @param position
	 * @return
	 */
	public static int readIntFromLittleEndian(byte[] bytes, int position) {
		return ((bytes[position] & 0xFF) | (bytes[position + 1] & 0xFF) << 8) 
				| ((bytes[position + 2] & 0xFF) | (bytes[position + 3] & 0xFF) << 8) << 16;
	}
	
	/**
	 * 
	 * @param bytes
	 * @param position
	 * @param length
	 * @return
	 */
	public static String readStringFromLittleEndian(byte[] bytes, int position, int length) {
		return RawOperator.readStringFromLittleEndian(bytes, position, length, Globals.DEFAULT_ENCODING);
	}
	
	/**
	 * 
	 * @param bytes
	 * @param position
	 * @param length
	 * @param encoding
	 * @return
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
	 * 
	 * @param bytes
	 * @param position
	 * @param value
	 */
	public static void writeStringFromLittleEndian(byte[] bytes, int position, String value) {
		writeStringFromLittleEndian(bytes, position, value, Globals.DEFAULT_ENCODING);
	}
	
	/**
	 * 
	 * @param bytes
	 * @param position
	 * @param value
	 * @param encoding
	 */
	public static void writeStringFromLittleEndian(byte[] bytes, int position, String value, String encoding) {
		if (value == null) {
			return;
		}
		
		byte[] valueBytes = null;
		try {
			valueBytes = value.getBytes(encoding);
		} catch (UnsupportedEncodingException e) {
			valueBytes = null;
		}
		if (valueBytes == null) {
			return;
		}
		if ((position + valueBytes.length) <= bytes.length) {
			for (int i = 0 ; i < valueBytes.length ; i++) {
				bytes[position + i] = valueBytes[i];
			}
		}
	}
	
	/**
	 * 
	 * @param bytes
	 * @param position
	 * @param value
	 */
	public static void writeShortFromLittleEndian(byte[] bytes, int position, short value) {
		bytes[position + 1] = (byte)(value >>> 8);
		bytes[position] = (byte)(value & 0xFF);
	}
	
	/**
	 * 
	 * @param bytes
	 * @param position
	 * @param value
	 */
	public static void writeIntFromLittleEndian(byte[] bytes, int position, int value) {
		int i = 4;
		while (i > 0) {
			i--;
			if (i == 0) {
				bytes[position + i] = (byte)(value & 0xFF);
			} else {
				bytes[position + i] = (byte)(value >>> (i * 8));
			}
		}
	}
	
	/**
	 * 
	 * @param bytes
	 * @param position
	 * @param value
	 */
	public static void writeLongFromLittleEndian(byte[] bytes, int position, long value) {
		int i = 8;
		while (i > 0) {
			i--;
			if (i == 0) {
				bytes[position + i] = (byte)(value & 0xFF);
			} else {
				bytes[position + i] = (byte)(value >>> (i * 8));
			}
		}
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	public static byte[] convertIntToByteArray(int value) {
		return RawOperator.convertIntToByteArray(value, 4, Globals.DEFAULT_VALUE_BOOLEAN);
	}
	
	/**
	 * 
	 * @param value
	 * @param arraySize
	 * @return
	 */
	public static byte[] convertIntToByteArray(int value, int arraySize) {
		return RawOperator.convertIntToByteArray(value, arraySize, Globals.DEFAULT_VALUE_BOOLEAN);
	}
	
	/**
	 * 
	 * @param nonce
	 * @return
	 */
	public static byte[] prepareAESBuffer(int nonce) {
		return RawOperator.convertIntToByteArray(nonce, 16, true);
	}
	
	/**
	 * 
	 * @param charArray
	 * @return
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
	 * 
	 * @param bitArray
	 * @return
	 * @throws ZipException
	 */
	public static byte convertBitArrayToByte(int[] bitArray) throws ZipException {
		if (bitArray == null) {
			throw new ZipException("Bit array is null!");
		}
		
		if (bitArray.length != 8) {
			throw new ZipException("Invalid bit array length!");
		}
		
		if (RawOperator.checkBitArray(bitArray)) {
			int calValue = 0;
			for (int i = 0 ; i < bitArray.length ; i++) {
				calValue += Math.pow(2, i) * bitArray[i];
			}
			
			return (byte)calValue;
		}
		
		throw new ZipException("Invalid bits provided!");
	}
	
	private static boolean checkBitArray(int[] bitArray) {
		for (int bit : bitArray) {
			if (bit != 0 && bit != 1) {
				return Globals.DEFAULT_VALUE_BOOLEAN;
			}
		}
		return true;
	}
	
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
