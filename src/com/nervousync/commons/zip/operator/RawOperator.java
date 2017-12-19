/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervous Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervous Studio.
 */
package com.nervousync.commons.zip.operator;

import com.nervousync.commons.core.Globals;
import com.nervousync.exceptions.zip.ZipException;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Nov 28, 2017 5:34:55 PM $
 */
public final class RawOperator {
	
	public static short readShortFromBigEndian(byte[] bytes, int position) {
		short readValue = 0;
		
		readValue |= bytes[position] & 0xFF;
		readValue <<= 8;
		readValue |= bytes[position + 1] & 0xFF;
		
		return readValue;
	}

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
	
	public static int readShortFromLittleEndian(byte[] bytes, int position) {
		return (bytes[position] & 0xFF) | (bytes[position + 1] & 0xFF) << 8;
	}
	
	public static int readIntFromLittleEndian(byte[] bytes, int position) {
		return ((bytes[position] & 0xFF) | (bytes[position + 1] & 0xFF) << 8) 
				| ((bytes[position + 2] & 0xFF) | (bytes[position + 3] & 0xFF) << 8) << 16;
	}
	
	public static void writeShortFromLittleEndian(byte[] bytes, int position, short value) {
		bytes[position + 1] = (byte)(value >>> 8);
		bytes[position] = (byte)(value & 0xFF);
	}
	
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
	
	public static byte[] convertIntToByteArray(int value) {
		return RawOperator.convertIntToByteArray(value, 4, Globals.DEFAULT_VALUE_BOOLEAN);
	}
	
	public static byte[] convertIntToByteArray(int value, int arraySize) {
		return RawOperator.convertIntToByteArray(value, arraySize, Globals.DEFAULT_VALUE_BOOLEAN);
	}
	
	public static byte[] prepareAESBuffer(int nonce) {
		return RawOperator.convertIntToByteArray(nonce, 16, true);
	}
	
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
	
	public static byte convertBitArrayToByte(int[] bitArray) throws ZipException {
		if (bitArray == null) {
			throw new ZipException("Bit array is null!");
		}
		
		if (bitArray.length != 8) {
			throw new ZipException("Invalid bit array length!");
		}
		
		if (!RawOperator.checkBits(bitArray)) {
			throw new ZipException("Invalid bits provided!");
		}
		
		int calValue = 0;
		for (int i = 0 ; i < bitArray.length ; i++) {
			calValue += Math.pow(2, i) * bitArray[i];
		}
		
		return (byte)calValue;
	}
	
	private static boolean checkBits(int[] bitArray) {
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
