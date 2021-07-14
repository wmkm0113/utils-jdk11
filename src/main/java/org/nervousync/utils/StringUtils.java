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
import java.lang.Character.UnicodeBlock;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import org.nervousync.commons.beans.core.BeanObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.nervousync.commons.core.Globals;
import org.nervousync.enumerations.xml.DataType;
import org.nervousync.huffman.HuffmanNode;
import org.nervousync.huffman.HuffmanObject;
import org.nervousync.huffman.HuffmanTree;

/**
 * The type String utils.
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: Jan 13, 2010 3:53:41 PM $
 */
public final class StringUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(StringUtils.class);

	private static final String TOP_PATH = "..";

	private static final String CURRENT_PATH = ".";

	/**
	 * The shift value required to create the upper nibble
	 * from the first of 2 byte values converted from ascii hex.
	 */
	private static final int UPPER_NIBBLE_SHIFT = Byte.SIZE / 2;

	private static final byte TRANSLATED_SPACE_CHARACTER = '_';

	private static final int INVALID_BYTE = -1;
	private static final int PAD_BYTE = -2;
	private static final int MASK_BYTE_UNSIGNED = 0xFF;

	private static final int INPUT_BYTES_PER_CHUNK = 4;
	private static final int PADDING = '=';

	private static final String BASE32 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
	private static final String BASE64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
	private static final String AUTHORIZATION_CODE_ITEMS = "23456789ABCEFGHJKLMNPQRSTUVWXYZ";

	private static final String CHN_ID_CARD_REGEX = "^[1-9]([0-9]{17}|([0-9]{16}X))$";
	private static final String CHN_ID_CARD_CODE = "0123456789X";
	private static final String CHN_SOCIAL_CREDIT_REGEX = "^[1-9|A|N|Y][0-9A-Z]{17}$";
	private static final String CHN_SOCIAL_CREDIT_CODE = "0123456789ABCDEFGHJKLMNPQRTUWXY";
	private static final String LUHN_CODE_REGEX = "^[0-9]{1,}";

	private StringUtils() {
	}

	/**
	 * Encode byte arrays using Base32 and not pending the padding character
	 * Note: Will return zero length string for given byte arrays is null or arrays length is 0.
	 * <pre>
	 * StringUtils.base32Encode(null) = ""
	 * StringUtils.base32Encode([]) = ""
	 * StringUtils.base32Encode([72, 101, 108, 108, 111, 32, 87, 111, 114, 108, 100]) = "JBSWY3DPEBLW64TMMQ"
	 * </pre>
	 *
	 * @param bytes byte arrays
	 * @return Encoded base32 string
	 */
	public static String base32Encode(final byte[] bytes) {
		return base32Encode(bytes, Globals.DEFAULT_VALUE_BOOLEAN);
	}

	/**
	 * Encode byte arrays using Base32
	 * Note: Will return zero length string for given byte arrays is null or arrays length is 0.
	 * <pre>
	 * StringUtils.base32Encode(null, true) = ""
	 * StringUtils.base32Encode([], true) = ""
	 * StringUtils.base32Encode([72, 101, 108, 108, 111, 32, 87, 111, 114, 108, 100], true) = "JBSWY3DPEBLW64TMMQ=="
	 * </pre>
	 *
	 * @param bytes   byte arrays
	 * @param padding append padding character if needed
	 * @return Encoded base32 string
	 */
	public static String base32Encode(final byte[] bytes, boolean padding) {
		if (bytes == null) {
			return Globals.DEFAULT_VALUE_STRING;
		}
		StringBuilder stringBuilder = new StringBuilder();

		int i = 0, index = 0;
		int currentByte, nextByte, digit;

		while (i < bytes.length) {
			currentByte = bytes[i] >= 0 ? bytes[i] : bytes[i] + 256;

			if (index > 3) {
				if ((i + 1) < bytes.length) {
					nextByte = bytes[i + 1] >= 0 ? bytes[i + 1] : bytes[i + 1] + 256;
				} else {
					nextByte = 0;
				}

				digit = currentByte & (MASK_BYTE_UNSIGNED >> index);
				index = (index + 5) % 8;
				digit = (digit << index) | nextByte >> (8 - index);
				i++;
			} else {
				digit = (currentByte >> (8 - (index + 5))) & 0x1F;
				index = (index + 5) % 8;
				if (index == 0) {
					i++;
				}
			}
			stringBuilder.append(BASE32.charAt(digit));
		}

		if (padding) {
			while (stringBuilder.length() % 5 > 0) {
				stringBuilder.append((char) PADDING);
			}
		}
		return stringBuilder.toString();
	}

	/**
	 * Convert given base32 string to byte array
	 * Note: Will return zero length array for given base64 string is null or string length is 0.
	 * <pre>
	 * StringUtils.base32Decode(null) = []
	 * StringUtils.base32Decode("") = []
	 * StringUtils.base32Decode("JBSWY3DPEBLW64TMMQ") = [72, 101, 108, 108, 111, 32, 87, 111, 114, 108, 100]
	 * </pre>
	 *
	 * @param string Encoded base32 string
	 * @return Decode byte arrays
	 */
	public static byte[] base32Decode(String string) {
		if (string == null || string.length() == 0) {
			return new byte[0];
		}

		while (string.charAt(string.length() - 1) == PADDING) {
			string = string.substring(0, string.length() - 1);
		}

		byte[] bytes = new byte[string.length() * 5 / 8];
		int index = 0;
		StringBuilder stringBuilder = new StringBuilder(8);
		StringBuilder temp;
		for (String c : string.split("")) {
			if (BASE32.contains(c)) {
				int current = BASE32.indexOf(c);
				temp = new StringBuilder(5);
				for (int i = 0; i < 5; i++) {
					temp.append(current & 1);
					current >>>= 1;
				}
				temp.reverse();
				if (stringBuilder.length() >= 3) {
					int currentLength = 8 - stringBuilder.length();
					stringBuilder.append(temp.substring(0, currentLength));
					bytes[index] = (byte) Integer.valueOf(stringBuilder.toString(), 2).intValue();
					index++;
					stringBuilder = new StringBuilder(8);
					stringBuilder.append(temp.substring(currentLength));
				} else {
					stringBuilder.append(temp);
				}
			}
		}
		return bytes;
	}

	/**
	 * Encode byte arrays using Base64
	 * Note: Will return zero length string for given byte arrays is null or arrays length is 0.
	 * <pre>
	 * StringUtils.base64Encode(null) = ""
	 * StringUtils.base64Encode([]) = ""
	 * StringUtils.base64Encode([72, 101, 108, 108, 111, 32, 87, 111, 114, 108, 100]) = "SGVsbG8gV29ybGQ="
	 * </pre>
	 *
	 * @param bytes byte arrays
	 * @return Encoded base64 string
	 */
	public static String base64Encode(final byte[] bytes) {
		if (bytes == null || bytes.length == 0) {
			return Globals.DEFAULT_VALUE_STRING;
		}
		int length = bytes.length;
		byte[] tempBytes;
		if (length % 3 == 0) {
			tempBytes = bytes;
		} else {
			while (length % 3 != 0) {
				length++;
			}
			tempBytes = new byte[length];
			System.arraycopy(bytes, 0, tempBytes, 0, bytes.length);
			for (int i = bytes.length; i < length; i++) {
				tempBytes[i] = 0;
			}
		}

		StringBuilder stringBuilder = new StringBuilder();
		int index = 0;
		while ((index * 3) < length) {
			stringBuilder.append(BASE64.charAt((tempBytes[index * 3] >> 2) & 0x3F));
			stringBuilder.append(BASE64.charAt(((tempBytes[index * 3] << 4)
					| ((tempBytes[index * 3 + 1] & MASK_BYTE_UNSIGNED) >> 4)) & 0x3F));
			if (index * 3 + 1 < bytes.length) {
				stringBuilder.append(BASE64.charAt(((tempBytes[index * 3 + 1] << 2)
						| ((tempBytes[index * 3 + 2] & MASK_BYTE_UNSIGNED) >> 6)) & 0x3F));
			}
			if (index * 3 + 2 < bytes.length) {
				stringBuilder.append(BASE64.charAt(tempBytes[index * 3 + 2] & 0x3F));
			}
			index++;
		}

		while (stringBuilder.length() % 3 > 0) {
			stringBuilder.append((char) PADDING);
		}
		return stringBuilder.toString();
	}

	/**
	 * Convert given base64 string to byte array
	 * Note: Will return zero length array for given base64 string is null or string length is 0.
	 * <pre>
	 * StringUtils.base64Decode(null) = []
	 * StringUtils.base64Decode("") = []
	 * StringUtils.base64Decode("SGVsbG8gV29ybGQ=") = [72, 101, 108, 108, 111, 32, 87, 111, 114, 108, 100]
	 * </pre>
	 *
	 * @param string Encoded base64 string
	 * @return Decode byte arrays
	 */
	public static byte[] base64Decode(String string) {
		if (string == null || string.length() == 0) {
			return new byte[0];
		}
		while (string.charAt(string.length() - 1) == PADDING) {
			string = string.substring(0, string.length() - 1);
		}

		byte[] bytes = new byte[string.length() * 3 / 4];

		int index = 0;
		for (int i = 0; i < string.length(); i += 4) {
			int index1 = BASE64.indexOf(string.charAt(i + 1));
			bytes[index * 3] = (byte) (((BASE64.indexOf(string.charAt(i)) << 2) | (index1 >> 4)) & MASK_BYTE_UNSIGNED);
			if (index * 3 + 1 >= bytes.length) {
				break;
			}

			int index2 = BASE64.indexOf(string.charAt(i + 2));
			bytes[index * 3 + 1] = (byte) (((index1 << 4) | (index2 >> 2)) & MASK_BYTE_UNSIGNED);
			if (index * 3 + 2 >= bytes.length) {
				break;
			}

			bytes[index * 3 + 2] = (byte) (((index2 << 6) | BASE64.indexOf(string.charAt(i + 3))) & MASK_BYTE_UNSIGNED);
			index++;
		}

		return bytes;
	}

	/**
	 * Convert the given string to HuffmanTree Object using given code mapping
	 *
	 * @param codeMapping Geven code mapping
	 * @param content     Given data string
	 * @return Converted HuffmanTree object
	 */
	public static String encodeWithHuffman(Hashtable<String, Object> codeMapping, String content) {
		return HuffmanTree.encodeString(codeMapping, content);
	}

	/**
	 * Convert the given string to HuffmanTree Object
	 *
	 * @param content Given data string
	 * @return Converted HuffmanTree object
	 */
	public static HuffmanObject encodeWithHuffman(String content) {
		HuffmanTree huffmanTree = new HuffmanTree();

		String temp = content;
		List<String> checkedStrings = new ArrayList<>();

		while (temp.length() > 0) {
			String keyword = temp.substring(0, 1);
			if (!checkedStrings.contains(keyword)) {
				huffmanTree.insertNode(new HuffmanNode(keyword,
						StringUtils.countOccurrencesOf(content, keyword)));
				checkedStrings.add(keyword);
			}
			temp = temp.substring(1);
		}

		huffmanTree.build();
		return huffmanTree.encodeString(content);
	}

	/**
	 * Check that the given CharSequence is <code>null</code> or length 0.
	 * Note: Will return <code>true</code> for a CharSequence that purely consists of whitespace.
	 * <pre>
	 * StringUtils.isEmpty(null) = true
	 * StringUtils.isEmpty(Globals.DEFAULT_VALUE_STRING) = true
	 * StringUtils.isEmpty(" ") = false
	 * StringUtils.isEmpty("Hello") = false
	 * </pre>
	 *
	 * @param str the CharSequence to check (may be <code>null</code>)
	 * @return <code>true</code> if the CharSequence is null or length 0.
	 */
	public static boolean isEmpty(CharSequence str) {
		return !StringUtils.hasLength(str);
	}

	/**
	 * Check that the given CharSequence is neither <code>null</code> nor of length 0.
	 * Note: Will return <code>true</code> for a CharSequence that purely consists of whitespace.
	 * <pre>
	 * StringUtils.notNull(null) = false
	 * StringUtils.notNull(Globals.DEFAULT_VALUE_STRING) = false
	 * StringUtils.notNull(" ") = true
	 * StringUtils.notNull("Hello") = true
	 * </pre>
	 *
	 * @param str the CharSequence to check (may be <code>null</code>)
	 * @return <code>true</code> if the CharSequence is not null and has length
	 */
	public static boolean notNull(CharSequence str) {
		return (str != null && str.length() > 0);
	}

	/**
	 * Check that the given CharSequence is neither <code>null</code> nor only blank character.
	 * Note: Will return <code>true</code> for a CharSequence that purely consists of whitespace.
	 * <pre>
	 * StringUtils.notBlank(null) = false
	 * StringUtils.notBlank(Globals.DEFAULT_VALUE_STRING) = false
	 * StringUtils.notBlank(" ") = false
	 * StringUtils.notBlank("Hello") = true
	 * </pre>
	 *
	 * @param str the CharSequence to check (may be <code>null</code>)
	 * @return <code>true</code> if the CharSequence is not null/blank character and has length
	 */
	public static boolean notBlank(String str) {
		return (str != null && str.trim().length() > 0);
	}

	/**
	 * Check that the given CharSequence is neither <code>null</code> nor of length 0.
	 * Note: Will return <code>true</code> for a CharSequence that purely consists of whitespace.
	 * <pre>
	 * StringUtils.hasLength(null) = false
	 * StringUtils.hasLength(Globals.DEFAULT_VALUE_STRING) = false
	 * StringUtils.hasLength(" ") = true
	 * StringUtils.hasLength("Hello") = true
	 * </pre>
	 *
	 * @param str the CharSequence to check (may be <code>null</code>)
	 * @return <code>true</code> if the CharSequence is not null and has length
	 */
	public static boolean hasLength(CharSequence str) {
		return (str != null && str.length() > 0);
	}

	/**
	 * Check whether the given CharSequence has actual text.
	 * More specifically, returns <code>true</code> if the string not <code>null</code>,
	 * its length is greater than 0, and it contains at least one non-whitespace character.
	 * <pre>
	 * StringUtils.hasText(null) = false
	 * StringUtils.hasText(Globals.DEFAULT_VALUE_STRING) = false
	 * StringUtils.hasText(" ") = false
	 * StringUtils.hasText("12345") = true
	 * StringUtils.hasText(" 12345 ") = true
	 * </pre>
	 *
	 * @param str the CharSequence to check (may be <code>null</code>)
	 * @return <code>true</code> if the CharSequence is not <code>null</code>, its length is greater than 0, and it does not contain whitespace only
	 * @see java.lang.Character#isWhitespace java.lang.Character#isWhitespacejava.lang.Character#isWhitespacejava.lang.Character#isWhitespace
	 */
	public static boolean hasText(CharSequence str) {
		if (hasLength(str)) {
			return false;
		}
		int strLen = str.length();
		for (int i = 0; i < strLen; i++) {
			if (!Character.isWhitespace(str.charAt(i))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check whether the given CharSequence contains any whitespace characters.
	 *
	 * @param str the CharSequence to check (may be <code>null</code>)
	 * @return <code>true</code> if the CharSequence is not empty and contains at least 1 whitespace character
	 * @see java.lang.Character#isWhitespace java.lang.Character#isWhitespacejava.lang.Character#isWhitespacejava.lang.Character#isWhitespace
	 */
	public static boolean containsWhitespace(CharSequence str) {
		if (hasLength(str)) {
			return false;
		}
		int strLen = str.length();
		for (int i = 0; i < strLen; i++) {
			if (Character.isWhitespace(str.charAt(i))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check whether the given String contains any whitespace characters.
	 *
	 * @param str the String to check (may be <code>null</code>)
	 * @return <code>true</code> if the String is not empty and contains at least 1 whitespace character
	 * @see #containsWhitespace(CharSequence) #containsWhitespace(CharSequence)#containsWhitespace(CharSequence)#containsWhitespace(CharSequence)
	 */
	public static boolean containsWhitespace(String str) {
		return containsWhitespace((CharSequence) str);
	}

	/**
	 * Trim leading and trailing whitespace from the given String.
	 *
	 * @param str the String to check
	 * @return the trimmed String
	 * @see java.lang.Character#isWhitespace java.lang.Character#isWhitespacejava.lang.Character#isWhitespacejava.lang.Character#isWhitespace
	 */
	public static String trimWhitespace(String str) {
		String string = StringUtils.trimLeadingWhitespace(str);
		string = StringUtils.trimTrailingWhitespace(string);
		return string;
	}

	/**
	 * Trim <i>all</i> whitespace from the given String:
	 * leading, trailing, and in between characters.
	 *
	 * @param str the String to check
	 * @return the trimmed String
	 * @see java.lang.Character#isWhitespace java.lang.Character#isWhitespacejava.lang.Character#isWhitespacejava.lang.Character#isWhitespace
	 */
	public static String trimAllWhitespace(String str) {
		if (hasLength(str)) {
			return str;
		}
		StringBuilder buf = new StringBuilder(str);
		int index = 0;
		while (buf.length() > index) {
			if (Character.isWhitespace(buf.charAt(index))) {
				buf.deleteCharAt(index);
			} else {
				index++;
			}
		}
		return buf.toString();
	}

	/**
	 * Trim leading whitespace from the given String.
	 *
	 * @param str the String to check
	 * @return the trimmed String
	 * @see java.lang.Character#isWhitespace java.lang.Character#isWhitespacejava.lang.Character#isWhitespacejava.lang.Character#isWhitespace
	 */
	public static String trimLeadingWhitespace(String str) {
		if (hasLength(str)) {
			return str;
		}
		StringBuilder buf = new StringBuilder(str);
		while (buf.length() > 0 && Character.isWhitespace(buf.charAt(0))) {
			buf.deleteCharAt(0);
		}
		return buf.toString();
	}

	/**
	 * Trim trailing whitespace from the given String.
	 *
	 * @param str the String to check
	 * @return the trimmed String
	 * @see java.lang.Character#isWhitespace java.lang.Character#isWhitespacejava.lang.Character#isWhitespacejava.lang.Character#isWhitespace
	 */
	public static String trimTrailingWhitespace(String str) {
		if (hasLength(str)) {
			return str;
		}
		StringBuilder buf = new StringBuilder(str);
		while (buf.length() > 0 && Character.isWhitespace(buf.charAt(buf.length() - 1))) {
			buf.deleteCharAt(buf.length() - 1);
		}
		return buf.toString();
	}

	/**
	 * Trim all occurrences of the supplied leading character from the given String.
	 *
	 * @param str              the String to check
	 * @param leadingCharacter the leading character to be trimmed
	 * @return the trimmed String
	 */
	public static String trimLeadingCharacter(String str, char leadingCharacter) {
		if (hasLength(str)) {
			return str;
		}
		StringBuilder buf = new StringBuilder(str);
		while (buf.length() > 0 && buf.charAt(0) == leadingCharacter) {
			buf.deleteCharAt(0);
		}
		return buf.toString();
	}

	/**
	 * Trim all occurrences of the supplied trailing character from the given String.
	 *
	 * @param str               the String to check
	 * @param trailingCharacter the trailing character to be trimmed
	 * @return the trimmed String
	 */
	public static String trimTrailingCharacter(String str, char trailingCharacter) {
		if (hasLength(str)) {
			return str;
		}
		StringBuilder buf = new StringBuilder(str);
		while (buf.length() > 0 && buf.charAt(buf.length() - 1) == trailingCharacter) {
			buf.deleteCharAt(buf.length() - 1);
		}
		return buf.toString();
	}


	/**
	 * Test if the given String starts with the specified prefix,
	 * ignoring upper/lower case.
	 *
	 * @param str    the String to check
	 * @param prefix the prefix to look for
	 * @return check result
	 * @see java.lang.String#startsWith java.lang.String#startsWithjava.lang.String#startsWithjava.lang.String#startsWith
	 */
	public static boolean startsWithIgnoreCase(String str, String prefix) {
		if (str == null || prefix == null) {
			return false;
		}
		if (str.startsWith(prefix)) {
			return true;
		}
		if (str.length() < prefix.length()) {
			return false;
		}
		String lcStr = str.substring(0, prefix.length()).toLowerCase();
		String lcPrefix = prefix.toLowerCase();
		return lcStr.equals(lcPrefix);
	}

	/**
	 * Test if the given String ends with the specified suffix,
	 * ignoring upper/lower case.
	 *
	 * @param str    the String to check
	 * @param suffix the suffix to look for
	 * @return check result
	 * @see java.lang.String#endsWith java.lang.String#endsWithjava.lang.String#endsWithjava.lang.String#endsWith
	 */
	public static boolean endsWithIgnoreCase(String str, String suffix) {
		if (str == null || suffix == null) {
			return false;
		}
		if (str.endsWith(suffix)) {
			return true;
		}
		if (str.length() < suffix.length()) {
			return false;
		}

		String lcStr = str.substring(str.length() - suffix.length()).toLowerCase();
		String lcSuffix = suffix.toLowerCase();
		return lcStr.equals(lcSuffix);
	}

	/**
	 * Check given string contains emoji info
	 *
	 * @param string Given string
	 * @return Check result
	 */
	public static boolean containsEmoji(String string) {
		if (string != null && string.length() > 0) {
			int length = string.length();
			for (int i = 0; i < length; i++) {
				char c = string.charAt(i);
				if (0xd800 <= c && c <= 0xdbff) {
					if (length > 1) {
						char next = string.charAt(i + 1);
						int result = ((c - 0xd800) * 0x400) + (next - 0xdc00) + 0x10000;
						if (0x1d000 <= result && result <= 0x1f77f) {
							return true;
						}
					}
				} else {
					if ((0x2100 <= c && c <= 0x27ff && c != 0x263b)
							|| (0x2805 <= c && c <= 0x2b07)
							|| (0x3297 <= c && c <= 0x3299)
							|| c == 0xa9 || c == 0xae || c == 0x303d
							|| c == 0x3030 || c == 0x2b55 || c == 0x2b1c
							|| c == 0x2b1b || c == 0x2b50) {
						return true;
					}

					if (length > 1 && i < (length - 1)) {
						char next = string.charAt(i + 1);
						if (next == 0x20e3) {
							return true;
						}
					}
				}
			}
		}
		return Globals.DEFAULT_VALUE_BOOLEAN;
	}

	/**
	 * Test whether the given string matches the given substring
	 * at the given index.
	 *
	 * @param str       the original string (or StringBuilder)
	 * @param index     the index in the original string to start matching against
	 * @param substring the substring to match at the given index
	 * @return check result
	 */
	public static boolean substringMatch(CharSequence str, int index, CharSequence substring) {
		for (int j = 0; j < substring.length(); j++) {
			int i = index + j;
			if (i >= str.length() || str.charAt(i) != substring.charAt(j)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Count the occurrences of the substring in string s.
	 *
	 * @param str string to search in. Return 0 if this is null.
	 * @param sub string to search for. Return 0 if this is null.
	 * @return count result
	 */
	public static int countOccurrencesOf(String str, String sub) {
		if (str == null || sub == null || str.length() == 0 || sub.length() == 0) {
			return 0;
		}
		int count = 0, pos = 0, idx;
		while ((idx = str.indexOf(sub, pos)) != -1) {
			++count;
			pos = idx + sub.length();
		}
		return count;
	}

	/**
	 * Replace all occurrences of a substring within a string with
	 * another string.
	 *
	 * @param inString   String to examine
	 * @param oldPattern String to replace
	 * @param newPattern String to insert
	 * @return a String with the replacements
	 */
	public static String replace(String inString, String oldPattern, String newPattern) {
		if (inString == null || oldPattern == null || newPattern == null) {
			return "";
		}

		StringBuilder stringBuilder = new StringBuilder();
		// output StringBuilder we'll build up
		int pos = 0; // our position in the old string
		int index = inString.indexOf(oldPattern);
		// the index of an occurrence we've found, or -1
		int patLen = oldPattern.length();
		while (index >= 0) {
			stringBuilder.append(inString, pos, index);
			stringBuilder.append(newPattern);
			pos = index + patLen;
			index = inString.indexOf(oldPattern, pos);
		}
		stringBuilder.append(inString.substring(pos));

		// remember to append any characters to the right of a match
		return stringBuilder.toString();
	}

	/**
	 * Delete all occurrences of the given substring.
	 *
	 * @param inString the original String
	 * @param pattern  the pattern to delete all occurrences of
	 * @return the resulting String
	 */
	public static String delete(String inString, String pattern) {
		return replace(inString, pattern, Globals.DEFAULT_VALUE_STRING);
	}

	/**
	 * Delete any character in a given String.
	 *
	 * @param inString      the original String
	 * @param charsToDelete a set of characters to delete. E.g. "az\n" will delete 'a's, 'z's and new lines.
	 * @return the resulting String
	 */
	public static String deleteAny(String inString, String charsToDelete) {
		if (hasLength(inString) || hasLength(charsToDelete)) {
			return inString;
		}
		StringBuilder out = new StringBuilder();
		for (int i = 0; i < inString.length(); i++) {
			char c = inString.charAt(i);
			if (charsToDelete.indexOf(c) == -1) {
				out.append(c);
			}
		}
		return out.toString();
	}

	/**
	 * Quote the given String with single quotes.
	 *
	 * @param str the input String (e.g. "myString")
	 * @return the quoted String (e.g. "'myString'"), or <code>null</code> if the input was <code>null</code>
	 */
	public static String quote(String str) {
		return (str != null ? "'" + str + "'" : null);
	}

	/**
	 * Turn the given Object into a String with single quotes
	 * if it is a String; keeping the Object as-is else.
	 *
	 * @param obj the input Object (e.g. "myString")
	 * @return the quoted String (e.g. "'myString'"), or the input object as-is if not a String
	 */
	public static Object quoteIfString(Object obj) {
		return (obj instanceof String ? quote((String) obj) : obj);
	}

	/**
	 * Unqualified a string qualified by a '.' dot character. For example,
	 * "this.name.is.qualified", returns "qualified".
	 *
	 * @param qualifiedName the qualified name
	 * @return qualified string
	 */
	public static String unqualified(String qualifiedName) {
		return unqualified(qualifiedName, '.');
	}

	/**
	 * Unqualified a string qualified by a separator character. For example,
	 * "this:name:is:qualified" returns "qualified" if using a ':' separator.
	 *
	 * @param qualifiedName the qualified name
	 * @param separator     the separator
	 * @return qualified string
	 */
	public static String unqualified(String qualifiedName, char separator) {
		return qualifiedName.substring(qualifiedName.lastIndexOf(separator) + 1);
	}

	/**
	 * Capitalize a <code>String</code>, changing the first letter to
	 * upper case as per {@link Character#toUpperCase(char)}.
	 * No other letters are changed.
	 *
	 * @param str the String to capitalize, may be <code>null</code>
	 * @return the capitalized String, <code>null</code> if null
	 */
	public static String capitalize(String str) {
		return changeFirstCharacterCase(str, true);
	}

	/**
	 * Uncapitalized a <code>String</code>, changing the first letter to
	 * lower case as per {@link Character#toLowerCase(char)}.
	 * No other letters are changed.
	 *
	 * @param str the String to uncapitalized, may be <code>null</code>
	 * @return the uncapitalized String, <code>null</code> if null
	 */
	public static String uncapitalized(String str) {
		return changeFirstCharacterCase(str, false);
	}

	/**
	 * Extract the filename from the given path,
	 * e.g. "mypath/myfile.txt" -&gt; "myfile.txt".
	 *
	 * @param path the file path (may be <code>null</code>)
	 * @return the extracted filename, or <code>null</code> if none
	 */
	public static String getFilename(String path) {
		if (path == null) {
			return null;
		}
		path = cleanPath(path);
		int separatorIndex = path.lastIndexOf(Globals.DEFAULT_PAGE_SEPARATOR);
		return (separatorIndex != -1 ? path.substring(separatorIndex + 1) : path);
	}

	/**
	 * Extract the filename extension from the given path,
	 * e.g. "mypath/myfile.txt" -&gt; "txt".
	 *
	 * @param path the file path (may be <code>null</code>)
	 * @return the extracted filename extension, or <code>null</code> if none
	 */
	public static String getFilenameExtension(String path) {
		if (path == null) {
			return "";
		}
		int sepIndex = path.lastIndexOf(Globals.EXTENSION_SEPARATOR);
		return (sepIndex != -1 ? path.substring(sepIndex + 1) : "");
	}

	/**
	 * Strip the filename extension from the given path,
	 * e.g. "mypath/myfile.txt" -&gt; "mypath/myfile".
	 *
	 * @param path the file path (may be <code>null</code>)
	 * @return the path with stripped filename extension, or <code>null</code> if none
	 */
	public static String stripFilenameExtension(String path) {
		if (path == null) {
			return null;
		}
		int sepIndex = path.lastIndexOf(Globals.EXTENSION_SEPARATOR);
		return (sepIndex != -1 ? path.substring(0, sepIndex) : path);
	}

	/**
	 * Apply the given relative path to the given path,
	 * assuming standard Java folder separation (i.e. "/" separators);
	 *
	 * @param path         the path to start from (usually a full file path)
	 * @param relativePath the relative path to apply (relative to the full file path above)
	 * @return the full file path that results from applying the relative path
	 */
	public static String applyRelativePath(String path, String relativePath) {
		int separatorIndex = path.lastIndexOf(Globals.DEFAULT_PAGE_SEPARATOR);
		if (separatorIndex != -1) {
			String newPath = path.substring(0, separatorIndex);
			if (!relativePath.startsWith(Globals.DEFAULT_PAGE_SEPARATOR)) {
				newPath += Globals.DEFAULT_PAGE_SEPARATOR;
			}
			return newPath + relativePath;
		} else {
			return relativePath;
		}
	}

	/**
	 * Normalize the path by suppressing sequences like "path/.." and
	 * inner simple dots.
	 * <p>The result is convenient for path comparison. For other uses,
	 * notice that Windows separators ("\") are replaced by simple slashes.
	 *
	 * @param path the original path
	 * @return the normalized path
	 */
	public static String cleanPath(String path) {
		String pathToUse = path;

		// Strip prefix from path to analyze, to not treat it as part of the
		// first path element. This is necessary to correctly parse paths like
		// "file:core/../core/io/Resource.class", where the ".." should just
		// strip the first "core" directory while keeping the "file:" prefix.
		int prefixIndex = pathToUse.indexOf(":");
		String prefix = Globals.DEFAULT_VALUE_STRING;
		if (prefixIndex != -1) {
			prefix = pathToUse.substring(0, prefixIndex + 1);
			pathToUse = pathToUse.substring(prefixIndex + 1);
		}

		String[] pathArray = delimitedListToStringArray(pathToUse, Globals.DEFAULT_PAGE_SEPARATOR);
		List<String> pathElements = new LinkedList<>();
		int tops = 0;

		for (int i = pathArray.length - 1; i >= 0; i--) {
			if (!CURRENT_PATH.equals(pathArray[i])) {
				if (TOP_PATH.equals(pathArray[i])) {
					// Registering top path found.
					tops++;
				} else {
					if (tops > 0) {
						// Merging path element with corresponding to top path.
						tops--;
					} else {
						// Normal path element found.
						pathElements.add(0, pathArray[i]);
					}
				}
			}
		}

		// Remaining top paths need to be retained.
		for (int i = 0; i < tops; i++) {
			pathElements.add(0, TOP_PATH);
		}

		return prefix + collectionToDelimitedString(pathElements, Globals.DEFAULT_PAGE_SEPARATOR);
	}

	/**
	 * Compare two paths after normalization of them.
	 *
	 * @param path1 first path for comparison
	 * @param path2 second path for comparison
	 * @return whether the two paths are equivalent after normalization
	 */
	public static boolean pathEquals(String path1, String path2) {
		return cleanPath(path1).equals(cleanPath(path2));
	}

	/**
	 * Parse the given <code>localeString</code> into a {@link Locale}.
	 * <p>This is the inverse operation of {@link Locale#toString Locale's toString}.
	 *
	 * @param localeString the locale string, following <code>Locale's</code> <code>toString()</code> format ("en", "en_UK", etc); also accepts spaces as separators, as an alternative to underscores
	 * @return a corresponding <code>Locale</code> instance
	 */
	public static Locale parseLocaleString(String localeString) {
		if (localeString == null) {
			return null;
		}
		String[] parts = tokenizeToStringArray(localeString, "_", false, false);

		if (parts == null) {
			return null;
		}

		String language = (parts.length > 0 ? parts[0] : Globals.DEFAULT_VALUE_STRING);
		String country = (parts.length > 1 ? parts[1] : Globals.DEFAULT_VALUE_STRING);
		String variant = Globals.DEFAULT_VALUE_STRING;
		if (parts.length >= 2) {
			// There is definitely a variant, and it is everything after the country
			// code sans the separator between the country code and the variant.
			int endIndexOfCountryCode = localeString.indexOf(country) + country.length();
			// Strip off any leading '_' and whitespace, what's left is the variant.
			variant = trimLeadingWhitespace(localeString.substring(endIndexOfCountryCode));
			if (variant.startsWith("_")) {
				variant = trimLeadingCharacter(variant, '_');
			}
		}
		return (language.length() > 0 ? new Locale(language, country, variant) : null);
	}

	//---------------------------------------------------------------------
	// Convenience methods for working with String arrays
	//---------------------------------------------------------------------

	/**
	 * Append the given String to the given String array, returning a new array
	 * consisting of the input array contents plus the given String.
	 *
	 * @param array the array to append to (can be <code>null</code>)
	 * @param str   the String to append
	 * @return the new array (never <code>null</code>)
	 */
	public static String[] addStringToArray(String[] array, String str) {
		if (ObjectUtils.isEmpty(array)) {
			return new String[]{str};
		}
		String[] newArr = new String[array.length + 1];
		System.arraycopy(array, 0, newArr, 0, array.length);
		newArr[array.length] = str;
		return newArr;
	}

	/**
	 * Concatenate the given String arrays into one,
	 * with overlapping array elements included twice.
	 * <p>The order of elements in the original arrays is preserved.
	 *
	 * @param array1 the first array (can be <code>null</code>)
	 * @param array2 the second array (can be <code>null</code>)
	 * @return the new array (<code>null</code> if both given arrays were <code>null</code>)
	 */
	public static String[] concatenateStringArrays(String[] array1, String[] array2) {
		if (ObjectUtils.isEmpty(array1)) {
			return array2;
		}
		if (ObjectUtils.isEmpty(array2)) {
			return array1;
		}
		String[] newArr = new String[array1.length + array2.length];
		System.arraycopy(array1, 0, newArr, 0, array1.length);
		System.arraycopy(array2, 0, newArr, array1.length, array2.length);
		return newArr;
	}

	/**
	 * Merge the given String arrays into one, with overlapping
	 * array elements only included once.
	 * <p>The order of elements in the original arrays is preserved
	 * (with the exception of overlapping elements, which are only
	 * included on their first occurrence).
	 *
	 * @param array1 the first array (can be <code>null</code>)
	 * @param array2 the second array (can be <code>null</code>)
	 * @return the new array (<code>null</code> if both given arrays were <code>null</code>)
	 */
	public static String[] mergeStringArrays(String[] array1, String[] array2) {
		if (ObjectUtils.isEmpty(array1)) {
			return array2;
		}
		if (ObjectUtils.isEmpty(array2)) {
			return array1;
		}
		List<String> result = new ArrayList<>(Arrays.asList(array1));
		for (String str : array2) {
			if (!result.contains(str)) {
				result.add(str);
			}
		}
		return toStringArray(result);
	}

	/**
	 * Turn given source String array into sorted array.
	 *
	 * @param array the source array
	 * @return the sorted array (never <code>null</code>)
	 */
	public static String[] sortStringArray(String[] array) {
		if (ObjectUtils.isEmpty(array)) {
			return new String[0];
		}
		Arrays.sort(array);
		return array;
	}

	/**
	 * Copy the given Collection into a String array.
	 * The Collection must contain String elements only.
	 *
	 * @param collection the Collection to copy
	 * @return the String array (<code>null</code> if the passed-in Collection was <code>null</code>)
	 */
	public static String[] toStringArray(Collection<String> collection) {
		if (collection == null) {
			return new String[0];
		}
		return collection.toArray(new String[0]);
	}

	/**
	 * Copy the given Enumeration into a String array.
	 * The Enumeration must contain String elements only.
	 *
	 * @param enumeration the Enumeration to copy
	 * @return the String array (<code>null</code> if the passed-in Enumeration was <code>null</code>)
	 */
	public static String[] toStringArray(Enumeration<String> enumeration) {
		if (enumeration == null) {
			return new String[0];
		}
		List<String> list = Collections.list(enumeration);
		return list.toArray(new String[0]);
	}

	/**
	 * Trim the elements of the given String array,
	 * calling <code>String.trim()</code> on each of them.
	 *
	 * @param array the original String array
	 * @return the resulting array (of the same size) with trimmed elements
	 */
	public static String[] trimArrayElements(String[] array) {
		if (ObjectUtils.isEmpty(array)) {
			return new String[0];
		}
		String[] result = new String[array.length];
		for (int i = 0; i < array.length; i++) {
			String element = array[i];
			result[i] = (element != null ? element.trim() : null);
		}
		return result;
	}

	/**
	 * Remove duplicate Strings from the given array.
	 * Also sorts the array, as it uses a TreeSet.
	 *
	 * @param array the String array
	 * @return an array without duplicates, in natural sort order
	 */
	public static String[] removeDuplicateStrings(String[] array) {
		if (ObjectUtils.isEmpty(array)) {
			return array;
		}
		Set<String> set = new TreeSet<>();
		Collections.addAll(set, array);
		return toStringArray(set);
	}

	/**
	 * Split a String at the first occurrence of the delimiter.
	 * Does not include the delimiter in the result.
	 *
	 * @param toSplit   the string to split
	 * @param delimiter to split the string up with
	 * @return a two element array with index 0 being before the delimiter, and index 1 being after the delimiter (neither element includes the delimiter); or <code>null</code> if the delimiter wasn't found in the given input String
	 */
	public static String[] split(String toSplit, String delimiter) {
		if (hasLength(toSplit) || hasLength(delimiter)) {
			return null;
		}
		int offset = toSplit.indexOf(delimiter);
		if (offset < 0) {
			return new String[]{toSplit};
		}
		String beforeDelimiter = toSplit.substring(0, offset);
		String afterDelimiter = toSplit.substring(offset + delimiter.length());
		return new String[]{beforeDelimiter, afterDelimiter};
	}

	/**
	 * Take an array Strings and split each element based on the given delimiter.
	 * A <code>Properties</code> instance is then generated, with the left of the
	 * delimiter providing the key, and the right of the delimiter providing the value.
	 * <p>Will trim both the key and value before adding them to the
	 * <code>Properties</code> instance.
	 *
	 * @param array     the array to process
	 * @param delimiter to split each element using (typically the equals symbol)
	 * @return a <code>Properties</code> instance representing the array contents, or <code>null</code> if the array to process was null or empty
	 */
	public static Properties splitArrayElementsIntoProperties(String[] array, String delimiter) {
		return splitArrayElementsIntoProperties(array, delimiter, null);
	}

	/**
	 * Take an array Strings and split each element based on the given delimiter.
	 * A <code>Properties</code> instance is then generated, with the left of the
	 * delimiter providing the key, and the right of the delimiter providing the value.
	 * <p>Will trim both the key and value before adding them to the
	 * <code>Properties</code> instance.
	 *
	 * @param array         the array to process
	 * @param delimiter     to split each element using (typically the equals symbol)
	 * @param charsToDelete one or more characters to remove from each element prior to attempting the split operation (typically the quotation mark symbol), or <code>null</code> if no removal should occur
	 * @return a <code>Properties</code> instance representing the array contents, or <code>null</code> if the array to process was <code>null</code> or empty
	 */
	public static Properties splitArrayElementsIntoProperties(
			String[] array, String delimiter, String charsToDelete) {

		if (ObjectUtils.isEmpty(array)) {
			return null;
		}
		Properties result = new Properties();
		for (String string : array) {
			String element = string;
			if (charsToDelete != null) {
				element = deleteAny(string, charsToDelete);
			}
			String[] splitterElement = split(element, delimiter);
			if (splitterElement == null) {
				continue;
			}
			result.setProperty(splitterElement[0].trim(), splitterElement[1].trim());
		}
		return result;
	}

	/**
	 * Tokenize the given String into a String array via a StringTokenizer.
	 * Trims tokens and omits empty tokens.
	 * <p>The given delimiters string is supposed to consist of any number of
	 * delimiter characters. Each of those characters can be used to separate
	 * tokens. A delimiter is always a single character; for multi-character
	 * delimiters, consider using <code>delimitedListToStringArray</code>
	 *
	 * @param str        the String to tokenize
	 * @param delimiters the delimiter characters, assembled as String (each of those characters is individually considered as delimiter).
	 * @return an array of the tokens
	 * @see java.util.StringTokenizer
	 * @see java.lang.String#trim() java.lang.String#trim()java.lang.String#trim()java.lang.String#trim()java.lang.String#trim()java.lang.String#trim()java.lang.String#trim()java.lang.String#trim()java.lang.String#trim()
	 * @see #delimitedListToStringArray #delimitedListToStringArray#delimitedListToStringArray#delimitedListToStringArray#delimitedListToStringArray#delimitedListToStringArray#delimitedListToStringArray#delimitedListToStringArray#delimitedListToStringArray
	 */
	public static String[] tokenizeToStringArray(String str, String delimiters) {
		return tokenizeToStringArray(str, delimiters, true, true);
	}

	/**
	 * Tokenize the given String into a String array via a StringTokenizer.
	 * <p>The given delimiters string is supposed to consist of any number of
	 * delimiter characters. Each of those characters can be used to separate
	 * tokens. A delimiter is always a single character; for multi-character
	 * delimiters, consider using <code>delimitedListToStringArray</code>
	 *
	 * @param str               the String to tokenize
	 * @param delimiters        the delimiter characters, assembled as String (each of those characters is individually considered as delimiter)
	 * @param trimTokens        trim the tokens via String's <code>trim</code>
	 * @param ignoreEmptyTokens omit empty tokens from the result array (only applies to tokens that are empty after trimming; StringTokenizer will not consider subsequent delimiters as token in the first place).
	 * @return an array of the tokens (<code>null</code> if the input String was <code>null</code>)
	 * @see java.util.StringTokenizer
	 * @see java.lang.String#trim() java.lang.String#trim()java.lang.String#trim()java.lang.String#trim()java.lang.String#trim()java.lang.String#trim()java.lang.String#trim()java.lang.String#trim()java.lang.String#trim()
	 * @see #delimitedListToStringArray #delimitedListToStringArray#delimitedListToStringArray#delimitedListToStringArray#delimitedListToStringArray#delimitedListToStringArray#delimitedListToStringArray#delimitedListToStringArray#delimitedListToStringArray
	 */
	public static String[] tokenizeToStringArray(
			String str, String delimiters, boolean trimTokens, boolean ignoreEmptyTokens) {

		if (str == null) {
			return null;
		}
		StringTokenizer st = new StringTokenizer(str, delimiters);
		List<String> tokens = new ArrayList<>();
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (trimTokens) {
				token = token.trim();
			}
			if (!ignoreEmptyTokens || token.length() > 0) {
				tokens.add(token);
			}
		}
		return toStringArray(tokens);
	}

	/**
	 * Take a String which is a delimited list and convert it to a String array.
	 * <p>A single delimiter can consists of more than one character: It will still
	 * be considered as single delimiter string, rather than as bunch of potential
	 * delimiter characters - in contrast to <code>tokenizeToStringArray</code>.
	 *
	 * @param str       the input String
	 * @param delimiter the delimiter between elements (this is a single delimiter, rather than a bunch individual delimiter characters)
	 * @return an array of the tokens in the list
	 * @see #tokenizeToStringArray #tokenizeToStringArray#tokenizeToStringArray#tokenizeToStringArray#tokenizeToStringArray#tokenizeToStringArray#tokenizeToStringArray#tokenizeToStringArray#tokenizeToStringArray
	 */
	public static String[] delimitedListToStringArray(String str, String delimiter) {
		return delimitedListToStringArray(str, delimiter, null);
	}

	/**
	 * Take a String which is a delimited list and convert it to a String array.
	 * <p>A single delimiter can consists of more than one character: It will still
	 * be considered as single delimiter string, rather than as bunch of potential
	 * delimiter characters - in contrast to <code>tokenizeToStringArray</code>.
	 *
	 * @param str           the input String
	 * @param delimiter     the delimiter between elements (this is a single delimiter, rather than a bunch individual delimiter characters)
	 * @param charsToDelete a set of characters to delete. Useful for deleting unwanted line breaks: e.g. "\r\n\f" will delete all new lines and line feeds in a String.
	 * @return an array of the tokens in the list
	 * @see #tokenizeToStringArray #tokenizeToStringArray#tokenizeToStringArray#tokenizeToStringArray#tokenizeToStringArray#tokenizeToStringArray#tokenizeToStringArray#tokenizeToStringArray#tokenizeToStringArray
	 */
	public static String[] delimitedListToStringArray(String str, String delimiter, String charsToDelete) {
		if (str == null) {
			return new String[0];
		}
		if (delimiter == null) {
			return new String[]{str};
		}
		List<String> result = new ArrayList<>();
		if (Globals.DEFAULT_VALUE_STRING.equals(delimiter)) {
			for (int i = 0; i < str.length(); i++) {
				result.add(deleteAny(str.substring(i, i + 1), charsToDelete));
			}
		} else {
			int pos = 0;
			int delPos;
			while ((delPos = str.indexOf(delimiter, pos)) != -1) {
				result.add(deleteAny(str.substring(pos, delPos), charsToDelete));
				pos = delPos + delimiter.length();
			}
			if (str.length() > 0 && pos <= str.length()) {
				// Add rest of String, but not in case of empty input.
				result.add(deleteAny(str.substring(pos), charsToDelete));
			}
		}
		return toStringArray(result);
	}

	/**
	 * Convert a CSV list into an array of Strings.
	 *
	 * @param str the input String
	 * @return an array of Strings, or the empty array in case of empty input
	 */
	public static String[] commaDelimitedListToStringArray(String str) {
		return delimitedListToStringArray(str, ",");
	}

	/**
	 * Convenience method to convert a CSV string list to a set.
	 * Note that this will suppress duplicates.
	 *
	 * @param str the input String
	 * @return a Set of String entries in the list
	 */
	public static Set<String> commaDelimitedListToSet(String str) {
		Set<String> set = new TreeSet<>();
		String[] tokens = commaDelimitedListToStringArray(str);
		Collections.addAll(set, tokens);
		return set;
	}

	/**
	 * Convenience method to return a Collection as a delimited (e.g. CSV)
	 * String. E.g. useful for <code>toString()</code> implementations.
	 *
	 * @param coll      the Collection to display
	 * @param delimiter the delimiter to use (probably a ",")
	 * @param prefix    the String to start each element with
	 * @param suffix    the String to end each element with
	 * @return the delimited String
	 */
	public static String collectionToDelimitedString(Collection<String> coll,
	                                                 String delimiter, String prefix, String suffix) {
		if (CollectionUtils.isEmpty(coll)) {
			return Globals.DEFAULT_VALUE_STRING;
		}
		StringBuilder sb = new StringBuilder();
		Iterator<String> it = coll.iterator();
		while (it.hasNext()) {
			sb.append(prefix).append(it.next()).append(suffix);
			if (it.hasNext()) {
				sb.append(delimiter);
			}
		}
		return sb.toString();
	}

	/**
	 * Convenience method to return a Collection as a delimited (e.g. CSV)
	 * String. E.g. useful for <code>toString()</code> implementations.
	 *
	 * @param coll      the Collection to display
	 * @param delimiter the delimiter to use (probably a ",")
	 * @return the delimited String
	 */
	public static String collectionToDelimitedString(Collection<String> coll, String delimiter) {
		return collectionToDelimitedString(coll, delimiter, Globals.DEFAULT_VALUE_STRING, Globals.DEFAULT_VALUE_STRING);
	}

	/**
	 * Convenience method to return a Collection as a CSV String.
	 * E.g. useful for <code>toString()</code> implementations.
	 *
	 * @param coll the Collection to display
	 * @return the delimited String
	 */
	public static String collectionToCommaDelimitedString(Collection<String> coll) {
		return collectionToDelimitedString(coll, ",");
	}

	/**
	 * Contains ignore case boolean.
	 *
	 * @param string the string
	 * @param search the search
	 * @return the boolean
	 */
	public static boolean containsIgnoreCase(String string, String search) {
		if (string == null || search == null) {
			return false;
		}

		int length = search.length();
		int maxLength = string.length() - length;

		for (int i = 0; i < maxLength; i++) {
			if (string.regionMatches(true, i, search, 0, length)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Convenience method to return a String array as a delimited (e.g. CSV)
	 * String. E.g. useful for <code>toString()</code> implementations.
	 *
	 * @param arr       the array to display
	 * @param delimiter the delimiter to use (probably a ",")
	 * @return the delimited String
	 */
	public static String arrayToDelimitedString(Object[] arr, String delimiter) {
		if (ObjectUtils.isEmpty(arr)) {
			return Globals.DEFAULT_VALUE_STRING;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < arr.length; i++) {
			if (i > 0) {
				sb.append(delimiter);
			}
			sb.append(arr[i]);
		}
		return sb.toString();
	}

	/**
	 * Convenience method to return a String array as a CSV String.
	 * E.g. useful for <code>toString()</code> implementations.
	 *
	 * @param arr the array to display
	 * @return the delimited String
	 */
	public static String arrayToCommaDelimitedString(Object[] arr) {
		return arrayToDelimitedString(arr, ",");
	}

	/**
	 * Convert BLOB to string and format for HTML
	 *
	 * @param content BLOB datas
	 * @return Convert string
	 */
	public static String convertContent(byte[] content) {
		if (content == null) {
			return null;
		}
		return textToHtml(ConvertUtils.convertToString(content));
	}

	/**
	 * Convert object to JSON string
	 *
	 * @param object       object
	 * @param stringType   the string type
	 * @param formatOutput the format output
	 * @return JSON string
	 */
	public static String objectToString(Object object, StringType stringType, boolean formatOutput) {
		if (object instanceof Map || object.getClass().isArray()
				|| Collection.class.isAssignableFrom(object.getClass())) {
			return StringUtils.writeToString(object, stringType, formatOutput);
		}

		TreeMap<String, Object> valueMap = new TreeMap<>();
		Arrays.stream(object.getClass().getDeclaredFields())
				.filter(ReflectionUtils::nonStaticMember)
				.forEach(field -> {
					Object fieldValue = ReflectionUtils.getFieldValue(field, object);
					Object mapValue;
					if (fieldValue instanceof byte[]) {
						mapValue = StringUtils.base64Encode((byte[]) fieldValue);
					} else {
						mapValue = fieldValue;
					}
					valueMap.put(field.getName(), mapValue);
				});

		return StringUtils.writeToString(valueMap, stringType, formatOutput);
	}

	/**
	 * The enum String type.
	 */
	public enum StringType {
		/**
		 * Json string type.
		 */
		JSON,
		/**
		 * Yaml string type.
		 */
		YAML,
		/**
		 * XML string type
		 */
		XML,
		/**
		 * Simple string type, include basic type wrapper class etc.
		 */
		SIMPLE
	}

	private static String writeToString(Object object, StringType stringType, boolean formatOutput) {
		ObjectMapper objectMapper;
		switch (stringType) {
			case JSON:
				objectMapper = new ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
				break;
			case YAML:
				objectMapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER))
						.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
				break;
			default:
				return Globals.DEFAULT_VALUE_STRING;
		}
		try {
			return formatOutput
					? objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object)
					: objectMapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			if (StringUtils.LOGGER.isDebugEnabled()) {
				StringUtils.LOGGER.debug("Convert object to string error! ", e);
			}
		}
		return Globals.DEFAULT_VALUE_STRING;
	}

	/**
	 * Convert JSON string to map
	 *
	 * @param data       the data
	 * @param stringType the string type
	 * @return Convert map
	 */
	public static Map<String, Object> dataToMap(String data, StringType stringType) {
		ObjectMapper objectMapper;
		switch (stringType) {
			case JSON:
				objectMapper = new ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
				break;
			case YAML:
				objectMapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER))
						.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
				break;
			default:
				return new HashMap<>();
		}
		try {
			return objectMapper.readValue(data, new TypeReference<>() {});
		} catch (Exception e) {
			if (StringUtils.LOGGER.isDebugEnabled()) {
				StringUtils.LOGGER.debug("Convert json string to object bean error! ", e);
			}
		}

		return new HashMap<>();
	}

	/**
	 * Check given string is ASCII
	 *
	 * @param s String
	 * @return check result code
	 * @throws UnsupportedEncodingException If the named charset is not supported
	 */
	public static int isASCII(String s) throws UnsupportedEncodingException {
		byte[] testString = s.getBytes(Globals.DEFAULT_ENCODING);

		if (testString.length > 3 || testString.length <= 0) {
			return 0;
		} else if (testString.length == 1) {
			return testString[0];
		} else if (testString.length == 3) {
			int heightByte = 256 + testString[0];
			int lowByte = 256 + testString[1];
			return (256 * heightByte + lowByte) - 256 * 256;
		}
		return 0;
	}

	/**
	 * Replace special XMl character with converted character in string
	 *
	 * @param sourceString input string
	 * @return replaced string
	 */
	public static String formatTextForXML(String sourceString) {
		if (sourceString == null) {
			return null;
		}
		int strLen;
		StringBuilder reString = new StringBuilder();
		String deString;
		strLen = sourceString.length();

		for (int i = 0; i < strLen; i++) {
			char ch = sourceString.charAt(i);
			switch (ch) {
				case '<':
					deString = "&lt;";
					break;
				case '>':
					deString = "&gt;";
					break;
				case '\"':
					deString = "&quot;";
					break;
				case '&':
					deString = "&amp;";
					break;
				case 13:
					deString = Globals.DEFAULT_VALUE_STRING;
					break;
				default:
					deString = Globals.DEFAULT_VALUE_STRING + ch;
			}
			reString.append(deString);
		}
		return reString.toString();
	}

	/**
	 * Replace converted character with special XMl character in string
	 *
	 * @param sourceString input string
	 * @return replaced string
	 */
	public static String formatForText(String sourceString) {

		if (sourceString == null) {
			return null;
		}

		sourceString = replace(sourceString, "&amp;", "&");
		sourceString = replace(sourceString, "&lt;", "<");
		sourceString = replace(sourceString, "&gt;", ">");
		sourceString = replace(sourceString, "&quot;", "\"");
		sourceString = replace(sourceString, "&#39;", "'");
		sourceString = replace(sourceString, "\\\\", "\\");
		sourceString = replace(sourceString, "\\n", "\n");
		sourceString = replace(sourceString, "\\r", "\r");
		sourceString = replace(sourceString, "<br/>", "\r");

		return sourceString;
	}

	/**
	 * Replace special HTML character with converted character in string
	 *
	 * @param sourceString input string
	 * @return replaced string
	 */
	public static String textToJSON(String sourceString) {
		int strLen;
		StringBuilder reString = new StringBuilder();
		strLen = sourceString.length();

		for (int i = 0; i < strLen; i++) {
			char ch = sourceString.charAt(i);
			switch (ch) {
				case '<':
					reString.append("&lt;");
					break;
				case '>':
					reString.append("&gt;");
					break;
				case '\"':
					reString.append("&quot;");
					break;
				case '&':
					reString.append("&amp;");
					break;
				case '\'':
					reString.append("&#39;");
					break;
				case '\\':
					reString.append("\\\\");
					break;
				case '\n':
					reString.append("\\n");
					break;
				case '\r':
					reString.append("\\r");
					break;
				default:
					reString.append(Globals.DEFAULT_VALUE_STRING).append(ch);
			}
		}
		return reString.toString();
	}

	/**
	 * Replace special HTML character with converted character in string
	 *
	 * @param sourceString input string
	 * @return replaced string
	 */
	public static String textToHtml(String sourceString) {
		int strLen;
		StringBuilder reString = new StringBuilder();
		strLen = sourceString.length();

		for (int i = 0; i < strLen; i++) {
			char ch = sourceString.charAt(i);
			switch (ch) {
				case '<':
					reString.append("&lt;");
					break;
				case '>':
					reString.append("&gt;");
					break;
				case '\"':
					reString.append("&quot;");
					break;
				case '&':
					reString.append("&amp;");
					break;
				case '\'':
					reString.append("&#39;");
					break;
				case '\\':
					reString.append("\\\\");
					break;
				case '\n':
					reString.append("\\n");
					break;
				case '\r':
					reString.append("<br/>");
					break;
				default:
					reString.append(Globals.DEFAULT_VALUE_STRING).append(ch);
			}
		}
		return reString.toString();
	}

	/**
	 * Get message key
	 *
	 * @param locale Locale instance
	 * @param key    message key
	 * @return Locale message key
	 */
	public static String messageKey(Locale locale, String key) {
		return messageKey(localeKey(locale), key);
	}

	/**
	 * Append localeKey and key
	 *
	 * @param strings the strings
	 * @return Locale message key
	 */
	public static String messageKey(String... strings) {
		StringJoiner stringJoiner = new StringJoiner(".");
		Arrays.asList(strings).forEach(stringJoiner::add);
		return stringJoiner.toString();
	}

	/**
	 * Convert <code>Locale</code> to <code>String</code>
	 * use toString() method. If locale is null, return Globals.DEFAULT_VALUE_STRING
	 *
	 * @param locale Locale instance
	 * @return Locale key
	 */
	public static String localeKey(Locale locale) {
		return locale == null ? Globals.DEFAULT_VALUE_STRING : locale.toString();
	}

	/**
	 * Matches with regex
	 *
	 * @param str   input string
	 * @param regex regex message
	 * @return match result
	 */
	public static boolean matches(String str, String regex) {
		if (str == null || regex == null) {
			return Globals.DEFAULT_VALUE_BOOLEAN;
		}
		return Pattern.compile(regex).matcher(str).find();
	}

	/**
	 * Replace template string with regex
	 *
	 * @param str         input string
	 * @param regex       regex message
	 * @param template    template string
	 * @param placeHolder the place holder
	 * @return replaced string. null for match failed
	 */
	public static String replaceWithRegex(String str, String regex, String template, String placeHolder) {
		if (!matches(str, regex)) {
			return null;
		}

		String matchesString = prepareRegexTemplate(template, placeHolder);
		Matcher matcher = Pattern.compile(regex).matcher(str);
		if (matcher.find()) {
			for (int i = 0; i < matcher.groupCount(); i++) {
				int index = i + 1;
				String matchValue = matcher.group(index);
				if (matchValue == null) {
					matchValue = Globals.DEFAULT_VALUE_STRING;
				}
				matchesString = replace(matchesString, "$" + index, matchValue);
			}

			return matchesString;
		}
		return str;
	}

	/**
	 * Random string
	 *
	 * @param length string length
	 * @return Random generate string
	 */
	public static String randomString(int length) {
		StringBuilder generateKey = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			generateKey.append(AUTHORIZATION_CODE_ITEMS.charAt(random.nextInt(AUTHORIZATION_CODE_ITEMS.length())));
		}
		return generateKey.toString();
	}

	/**
	 * Random number string
	 *
	 * @param length string length
	 * @return Random generate number string
	 */
	public static String randomNumber(int length) {
		StringBuilder generateKey = new StringBuilder();
		for (int i = 0; i < length; i++) {
			generateKey.append((char) (Math.random() * 10 + '0'));
		}
		return generateKey.toString();
	}

	/**
	 * Random index char.
	 *
	 * @param beginIndex the begin index
	 * @param endIndex   the end index
	 * @return the char
	 */
	public static char randomIndex(int beginIndex, int endIndex) {
		return (char) (Math.random() * (endIndex - beginIndex + 1) + beginIndex + '0');
	}

	/**
	 * Random char char.
	 *
	 * @param beginIndex the begin index
	 * @param endIndex   the end index
	 * @return the char
	 */
	public static char randomChar(int beginIndex, int endIndex) {
		return (char) (Math.random() * (endIndex - beginIndex + 1) + beginIndex + 'a');
	}

	/**
	 * Escape url address
	 *
	 * @param str original url address
	 * @return escape url address
	 */
	public static String escape(String str) {
		int length;
		char ch;
		StringBuilder StringBuilder = new StringBuilder();

		StringBuilder.ensureCapacity(str.length() * 6);

		for (length = 0; length < str.length(); length++) {
			ch = str.charAt(length);

			if (Character.isDigit(ch) || Character.isLowerCase(ch) || Character.isUpperCase(ch)) {
				StringBuilder.append(ch);
			} else if (length < 256) {
				StringBuilder.append("%");
				StringBuilder.append(Integer.toString(ch, 16));
			} else {
				StringBuilder.append("%u");
				StringBuilder.append(Integer.toString(ch, 16));
			}
		}

		return StringBuilder.toString();
	}

	/**
	 * Unescape url address
	 *
	 * @param str escaped url address string
	 * @return unescape url address
	 */
	public static String unescape(String str) {
		if (str == null) {
			str = Globals.DEFAULT_VALUE_STRING;
		}
		StringBuilder StringBuilder = new StringBuilder();
		StringBuilder.ensureCapacity(str.length());
		int lastIndex = 0;
		int index;
		char ch;
		while (lastIndex < str.length()) {
			index = str.indexOf("%", lastIndex);
			if (index == lastIndex) {
				if (str.charAt(index + 1) == 'u') {
					ch = (char) Integer.parseInt(str.substring(index + 2, index + 6), 16);
					StringBuilder.append(ch);
					lastIndex = index + 6;
				} else {
					ch = (char) Integer.parseInt(str.substring(index + 1, index + 3), 16);
					StringBuilder.append(ch);
					lastIndex = index + 3;
				}
			} else {
				if (index == -1) {
					StringBuilder.append(str.substring(lastIndex));
					lastIndex = str.length();
				} else {
					StringBuilder.append(str, lastIndex, index);
					lastIndex = index;
				}
			}
		}
		return StringBuilder.toString();
	}

	/**
	 * Check given character is space
	 *
	 * @param letter character
	 * @return check result
	 */
	public static boolean isSpace(char letter) {
		return (letter == 8 || letter == 9 || letter == 10 ||
				letter == 13 || letter == 32 || letter == 160);
	}

	/**
	 * Check given character is English character
	 *
	 * @param letter character
	 * @return check result
	 */
	public static boolean isEnglish(char letter) {
		return (letter > 'a' && letter < 'z') || (letter > 'A' && letter < 'Z');
	}

	/**
	 * Check given character is number
	 *
	 * @param letter character
	 * @return check result
	 */
	public static boolean isNumber(char letter) {
		return letter >= '0' && letter <= '9';
	}

	/**
	 * Check given character is Chinese/Japanese/Korean
	 *
	 * @param character character
	 * @return check result
	 */
	public static boolean isCJK(char character) {
		UnicodeBlock unicodeBlock = UnicodeBlock.of(character);

		return (unicodeBlock == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| unicodeBlock == UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| unicodeBlock == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| unicodeBlock == UnicodeBlock.GENERAL_PUNCTUATION
				|| unicodeBlock == UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				//全角数字字符和日韩字符
				|| unicodeBlock == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
				//韩文字符集
				|| unicodeBlock == Character.UnicodeBlock.HANGUL_SYLLABLES
				|| unicodeBlock == Character.UnicodeBlock.HANGUL_JAMO
				|| unicodeBlock == Character.UnicodeBlock.HANGUL_COMPATIBILITY_JAMO
				//日文字符集
				|| unicodeBlock == Character.UnicodeBlock.HIRAGANA //平假名
				|| unicodeBlock == Character.UnicodeBlock.KATAKANA //片假名
				|| unicodeBlock == Character.UnicodeBlock.KATAKANA_PHONETIC_EXTENSIONS);
	}

	/**
	 * Check type class is simple data class, e.g. Number(include int, Integer, long, Long...), String, boolean and Date
	 *
	 * @param typeClass type class
	 * @return check result
	 */
	public static boolean simpleDataType(Class<?> typeClass) {
		if (typeClass != null) {
			DataType dataType = ObjectUtils.retrieveSimpleDataType(typeClass);

			switch (dataType) {
				case NUMBER:
				case STRING:
				case BOOLEAN:
				case DATE:
					return true;
				default:
					break;
			}
		}
		return Globals.DEFAULT_VALUE_BOOLEAN;
	}

	/**
	 * Parse simple data to target class
	 *
	 * @param <T>       the type parameter
	 * @param dataValue value
	 * @param typeClass target define class
	 * @return target object
	 * @throws ParseException given string is null
	 */
	public static <T> T parseSimpleData(String dataValue, Class<T> typeClass) throws ParseException {
		Object paramObj = null;
		if (dataValue == null || typeClass == null || Globals.DEFAULT_VALUE_STRING.equals(dataValue)) {
			return null;
		}

		if (BeanObject.class.isAssignableFrom(typeClass)) {
			paramObj = BeanUtils.parseXml(dataValue, Globals.DEFAULT_ENCODING, typeClass);
		} else {
			DataType dataType = ObjectUtils.retrieveSimpleDataType(typeClass);

			switch (dataType) {
				case BOOLEAN:
					paramObj = Boolean.valueOf(dataValue);
					break;
				case DATE:
					paramObj = DateTimeUtils.parseSiteMapDate(dataValue);
					break;
				case ENUM:
					paramObj = ReflectionUtils.parseEnum(typeClass).get(dataValue);
					break;
				case NUMBER:
					if (typeClass.equals(Integer.class) || typeClass.equals(int.class)) {
						paramObj = Integer.valueOf(dataValue);
					} else if (typeClass.equals(Float.class) || typeClass.equals(float.class)) {
						paramObj = Float.valueOf(dataValue);
					} else if (typeClass.equals(Double.class) || typeClass.equals(double.class)) {
						paramObj = Double.valueOf(dataValue);
					} else if (typeClass.equals(Short.class) || typeClass.equals(short.class)) {
						paramObj = Short.valueOf(dataValue);
					} else if (typeClass.equals(Long.class) || typeClass.equals(long.class)) {
						paramObj = Long.valueOf(dataValue);
					} else if (typeClass.equals(BigInteger.class)) {
						paramObj = new BigInteger(dataValue);
					}
					break;
				case CDATA:
					paramObj = StringUtils.formatForText(dataValue).toCharArray();
					break;
				case BINARY:
					dataValue = StringUtils.replace(dataValue, " ", Globals.DEFAULT_VALUE_STRING);
					paramObj = StringUtils.base64Decode(dataValue);
					break;
				default:
					paramObj = StringUtils.formatForText(dataValue);
			}
		}

		return typeClass.cast(paramObj);
	}

	/**
	 * Validate given code, support China ID Code, China Social Credit Code, Luhn Algorithm
	 *
	 * @param code     Code
	 * @param codeType Code type
	 * @return Validate result
	 */
	public static boolean validateCode(String code, CodeType codeType) {
		switch (codeType) {
			case CHN_ID_Code:
				String cardCode = code.toUpperCase();
				if (StringUtils.matches(cardCode, CHN_ID_CARD_REGEX)) {
					int validateCode = CHN_ID_CARD_CODE.indexOf(cardCode.charAt(17));
					if (validateCode != -1) {
						int sigma = 0;
						for (int i = 0; i < 17; i++) {
							sigma += Character.digit(cardCode.charAt(i), 10) * (Math.pow(2, 17 - i) % 11);
						}
						return validateCode == ((12 - (sigma % 11)) % 11);
					}
				}
				break;
			case CHN_Social_Code:
				String creditCode = code.toUpperCase();
				if (StringUtils.matches(creditCode, CHN_SOCIAL_CREDIT_REGEX)) {
					int validateCode = CHN_SOCIAL_CREDIT_CODE.indexOf(creditCode.charAt(17));
					if (validateCode != -1) {
						int sigma = 0;
						for (int i = 0; i < 17; i++) {
							sigma += CHN_SOCIAL_CREDIT_CODE.indexOf(creditCode.charAt(i)) * (Math.pow(3, i) % 31);
						}

						int authCode = 31 - (sigma % 31);
						return (authCode == 31) ? (validateCode == 0) : (authCode == validateCode);
					}
				}
				break;
			case Luhn:
				if (StringUtils.matches(code, LUHN_CODE_REGEX)) {
					int result = 0;
					int index = code.length() - 1;
					while (index >= 0) {
						int currentCode = Character.getNumericValue(code.charAt(index));
						if (index % 2 == 0) {
							currentCode *= 2;
							if (currentCode > 9) {
								currentCode -= 9;
							}
						}
						result += currentCode;
						index--;
					}
					return result % 10 == 0;
				}
				break;
		}
		return Globals.DEFAULT_VALUE_BOOLEAN;
	}

	private static String prepareRegexTemplate(String template, String placeHolder) {
		if (StringUtils.isEmpty(template)) {
			return Globals.DEFAULT_VALUE_STRING;
		}
		if (StringUtils.isEmpty(placeHolder)) {
			return template;
		}
		String preparedTemplate = template;
		int index;
		int count = 1;
		while ((index = preparedTemplate.indexOf(placeHolder)) > 0) {
			preparedTemplate = preparedTemplate.substring(0, index)
					+ "$" + count
					+ preparedTemplate.substring(index + placeHolder.length());
			count++;
		}
		return preparedTemplate;
	}

	private static String changeFirstCharacterCase(String str, boolean capitalize) {
		if (str == null || str.length() == 0) {
			return str;
		}
		StringBuilder buf = new StringBuilder(str.length());
		if (capitalize) {
			buf.append(Character.toUpperCase(str.charAt(0)));
		} else {
			buf.append(Character.toLowerCase(str.charAt(0)));
		}
		buf.append(str.substring(1));
		return buf.toString();
	}

	/**
	 * The enum Code type.
	 */
	public enum CodeType {
		/**
		 * Chn social code code type.
		 */
		CHN_Social_Code,
		/**
		 * Chn id code code type.
		 */
		CHN_ID_Code,
		/**
		 * Luhn code type.
		 */
		Luhn
	}
}
