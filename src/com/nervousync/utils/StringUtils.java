/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervousync Studio.
 */
package com.nervousync.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.Character.UnicodeBlock;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nervousync.commons.beans.xml.BaseElement;
import com.nervousync.commons.beans.xml.DataType;
import com.nervousync.commons.core.Globals;
import com.nervousync.commons.zip.core.ZipConstants;
import com.nervousync.exceptions.zip.ZipException;
import com.nervousync.huffman.HuffmanNode;
import com.nervousync.huffman.HuffmanObject;
import com.nervousync.huffman.HuffmanTree;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Jan 13, 2010 3:53:41 PM $
 */
public final class StringUtils {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(StringUtils.class);

	private static final String FOLDER_SEPARATOR = "/";

	private static final String WINDOWS_FOLDER_SEPARATOR = "\\";

	private static final String TOP_PATH = "..";

	private static final String CURRENT_PATH = ".";

	private static final char EXTENSION_SEPARATOR = '.';
	
	private static final String BASE64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
	private static final String AUTHCODEITEMS = "23456789ABCEFGHJKLMNPQRSTUVWXYZ";
	
	private static final String CHN_IDEN_REGEX = "^[1-9]([0-9]{17}|([0-9]{16}X))$";
	private static final String CHN_IDEN_AUTHCODE = "10X98765432";
	private static final int[] CHN_IDEN_WEIGHT = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};

	private static final String CHN_ORG_CODE_REGEX = "^[1-9A-Z]([0-9A-Z]{7})-{0,1}([0-9]|X)$";
	private static final String CHN_ORG_CODE_MAP = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final int[] CHN_ORG_CODE_WEIGHT = {3, 7, 9, 10, 5, 8, 4, 2};
	
	private static final String CHN_SOCIAL_CREDIT_REGEX = "^[1|5|9|Y][0-9A-Z]{17}$";
	private static final String CHN_SOCIAL_CREDIT_AUTHCODE = "0123456789ABCDEFGHJKLMNPQRTUWXY";
	private static final int[] CHN_SOCIAL_CREDIT_WEIGHT = {1, 3, 9, 27, 19, 26, 16, 17, 20, 29, 25, 13, 8, 24, 10, 30, 28};
	
	private StringUtils() {
		
	}
	
	public static boolean supportedCharset(String charset) {
		try {
			new String("a".getBytes(), charset);
			return true;
		} catch (UnsupportedEncodingException e) {
			return Globals.DEFAULT_VALUE_BOOLEAN;
		}
	}

	public static byte[] convertCharset(String str) throws ZipException {
		try {
			byte[] converted = null;
			String charSet = detectCharSet(str);
			if (charSet.equals(ZipConstants.CHARSET_CP850)) {
				converted = str.getBytes(ZipConstants.CHARSET_CP850);
			} else if (charSet.equals(Globals.DEFAULT_ENCODING)) {
				converted = str.getBytes(Globals.DEFAULT_ENCODING);
			} else {
				converted = str.getBytes();
			}
			return converted;
		} catch (UnsupportedEncodingException err) {
			return str.getBytes();
		} catch (Exception e) {
			throw new ZipException(e);
		}
	}
	
	public static boolean isNotNullAndNotEmpty(String str) {
		if (str == null || str.trim().length() <= 0) {
			return false;
		}
		
		return true;
	}
	
	public static String base64Encode(byte[] bytes) {
		int length = bytes.length;
		byte[] tempBytes = null;
		if (length % 3 == 0) {
			tempBytes = bytes;
		} else {
			while (length % 3 != 0) {
				length++;
			}
			tempBytes = new byte[length];
			System.arraycopy(bytes, 0, tempBytes, 0, bytes.length);
			for (int i = bytes.length ; i < length ; i++) {
				tempBytes[i] = 0;
			}
		}
		char[] charArray = new char[((length + 2) / 3) * 4];
		
		int index = 0;
		while ((index * 3) < length) {
			charArray[index * 4] = BASE64.charAt((tempBytes[index * 3] >> 2) & 0x3F);
			charArray[index * 4 + 1] = BASE64.charAt(((tempBytes[index * 3] << 4) | ((tempBytes[index * 3 + 1] & 0xFF) >> 4)) & 0x3F);
			if (index * 3 + 1 >= bytes.length) {
				charArray[index * 4 + 2] = '=';
			} else {
				charArray[index * 4 + 2] = BASE64.charAt(((tempBytes[index * 3 + 1] << 2) | ((tempBytes[index * 3 + 2] & 0xFF) >> 6)) & 0x3F);
			}
			if (index * 3 + 2 >= bytes.length) {
				charArray[index * 4 + 3] = '=';
			} else {
				charArray[index * 4 + 3] = BASE64.charAt(tempBytes[index * 3 + 2] & 0x3F);
			}
			index++;
		}
		
		return new String(charArray);
	}
	
	public static byte[] base64Decode(String string) {
		while (string.endsWith("=")) {
			string = string.substring(0, string.length() - 1);
		}
		
		byte[] bytes = new byte[string.length() * 3 / 4];
		
		int index = 0;
		for (int i = 0 ; i < string.length() ; i += 4) {
			bytes[index * 3] = (byte)(((BASE64.indexOf(string.charAt(i)) << 2) | (BASE64.indexOf(string.charAt(i + 1)) >> 4)) & 0xFF);
			if (index * 3 + 1 >= bytes.length) {
				break;
			}
			
			bytes[index * 3 + 1] = (byte)(((BASE64.indexOf(string.charAt(i + 1)) << 4) | (BASE64.indexOf(string.charAt(i + 2)) >> 2)) & 0xFF);
			if (index * 3 + 2 >= bytes.length) {
				break;
			}

			bytes[index * 3 + 2] = (byte)(((BASE64.indexOf(string.charAt(i + 2)) << 6) | BASE64.indexOf(string.charAt(i + 3))) & 0xFF);
			index++;
		}
		
		return bytes;
	}
	
	/**
	 * returns the length of the string by wrapping it in a byte buffer with
	 * the appropriate charset of the input string and returns the limit of the 
	 * byte buffer
	 * @param str
	 * @return length of the string
	 * @throws ZipException
	 */
	public static int getEncodedStringLength(String str) throws ZipException {
		if (!isNotNullAndNotEmpty(str)) {
			throw new ZipException("input string is null, cannot calculate encoded String length");
		}
		
		String charset = detectCharSet(str);
		return getEncodedStringLength(str, charset);
	}
	
	/**
	 * returns the length of the string in the input encoding
	 * @param str
	 * @param charset
	 * @return int
	 * @throws ZipException
	 */
	public static int getEncodedStringLength(String str, String charset) throws ZipException {
		if (!isNotNullAndNotEmpty(str)) {
			throw new ZipException("input string is null, cannot calculate encoded String length");
		}
		
		if (!isNotNullAndNotEmpty(charset)) {
			throw new ZipException("encoding is not defined, cannot calculate string length");
		}
		
		ByteBuffer byteBuffer = null;
		
		try {
			if (charset.equals(ZipConstants.CHARSET_CP850)) {
				byteBuffer = ByteBuffer.wrap(str.getBytes(ZipConstants.CHARSET_CP850));
			} else if (charset.equals(Globals.DEFAULT_ENCODING)) {
				byteBuffer = ByteBuffer.wrap(str.getBytes(Globals.DEFAULT_ENCODING));
			} else {
				byteBuffer = ByteBuffer.wrap(str.getBytes(charset));
			}
		} catch (UnsupportedEncodingException e) {
			byteBuffer = ByteBuffer.wrap(str.getBytes());
		} catch (Exception e) {
			throw new ZipException(e);
		}
		
		return byteBuffer.limit();
	}

	/**
	 * Detects the encoding charset for the input string
	 * 
	 * @param str
	 * @return String - charset for the String
	 * @throws ZipException
	 *             - if input string is null. In case of any other exception
	 *             this method returns default System charset
	 */
	public static String detectCharSet(String str) throws ZipException {
		if (str == null) {
			throw new ZipException("input string is null, cannot detect charset");
		}

		try {
			byte[] byteString = str.getBytes(ZipConstants.CHARSET_CP850);
			String tempString = new String(byteString, ZipConstants.CHARSET_CP850);

			if (str.equals(tempString)) {
				return ZipConstants.CHARSET_CP850;
			}

			byteString = str.getBytes(Globals.DEFAULT_ENCODING);
			tempString = new String(byteString, Globals.DEFAULT_ENCODING);

			if (str.equals(tempString)) {
				return Globals.DEFAULT_ENCODING;
			}

			return Globals.DEFAULT_SYSTEM_CHARSET;
		} catch (UnsupportedEncodingException e) {
			return Globals.DEFAULT_SYSTEM_CHARSET;
		} catch (Exception e) {
			return Globals.DEFAULT_SYSTEM_CHARSET;
		}
	}

	public static String encodeWithHuffman(Hashtable<String, Object> codeMapping, String content) {
		return HuffmanTree.encodeString(codeMapping, content);
	}
	
	public static HuffmanObject encodeWithHuffman(String content) {
		HuffmanTree huffmanTree = new HuffmanTree();
		
		String temp = content;
		List<String> checkedStrings = new ArrayList<String>();
		
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
	
	//---------------------------------------------------------------------
	// General convenience methods for working with Strings
	//---------------------------------------------------------------------

	/**
	 * Check that the given CharSequence is neither <code>null</code> nor of length 0.
	 * Note: Will return <code>true</code> for a CharSequence that purely consists of whitespace.
	 * <p><pre>
	 * StringUtils.hasLength(null) = false
	 * StringUtils.hasLength("") = false
	 * StringUtils.hasLength(" ") = true
	 * StringUtils.hasLength("Hello") = true
	 * </pre>
	 * @param str the CharSequence to check (may be <code>null</code>)
	 * @return <code>true</code> if the CharSequence is not null and has length
	 * @see #hasText(String)
	 */
	public static boolean hasLength(CharSequence str) {
		return (str != null && str.length() > 0);
	}

	/**
	 * Check that the given String is neither <code>null</code> nor of length 0.
	 * Note: Will return <code>true</code> for a String that purely consists of whitespace.
	 * @param str the String to check (may be <code>null</code>)
	 * @return <code>true</code> if the String is not null and has length
	 * @see #hasLength(CharSequence)
	 */
	public static boolean hasLength(String str) {
		return hasLength((CharSequence) str);
	}

	/**
	 * Check whether the given CharSequence has actual text.
	 * More specifically, returns <code>true</code> if the string not <code>null</code>,
	 * its length is greater than 0, and it contains at least one non-whitespace character.
	 * <p><pre>
	 * StringUtils.hasText(null) = false
	 * StringUtils.hasText("") = false
	 * StringUtils.hasText(" ") = false
	 * StringUtils.hasText("12345") = true
	 * StringUtils.hasText(" 12345 ") = true
	 * </pre>
	 * @param str the CharSequence to check (may be <code>null</code>)
	 * @return <code>true</code> if the CharSequence is not <code>null</code>,
	 * its length is greater than 0, and it does not contain whitespace only
	 * @see java.lang.Character#isWhitespace
	 */
	public static boolean hasText(CharSequence str) {
		if (!hasLength(str)) {
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
	 * Check whether the given String has actual text.
	 * More specifically, returns <code>true</code> if the string not <code>null</code>,
	 * its length is greater than 0, and it contains at least one non-whitespace character.
	 * @param str the String to check (may be <code>null</code>)
	 * @return <code>true</code> if the String is not <code>null</code>, its length is
	 * greater than 0, and it does not contain whitespace only
	 * @see #hasText(CharSequence)
	 */
	public static boolean hasText(String str) {
		return hasText((CharSequence) str);
	}

	/**
	 * Check whether the given CharSequence contains any whitespace characters.
	 * @param str the CharSequence to check (may be <code>null</code>)
	 * @return <code>true</code> if the CharSequence is not empty and
	 * contains at least 1 whitespace character
	 * @see java.lang.Character#isWhitespace
	 */
	public static boolean containsWhitespace(CharSequence str) {
		if (!hasLength(str)) {
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
	 * @param str the String to check (may be <code>null</code>)
	 * @return <code>true</code> if the String is not empty and
	 * contains at least 1 whitespace character
	 * @see #containsWhitespace(CharSequence)
	 */
	public static boolean containsWhitespace(String str) {
		return containsWhitespace((CharSequence) str);
	}

	/**
	 * Trim leading and trailing whitespace from the given String.
	 * @param str the String to check
	 * @return the trimmed String
	 * @see java.lang.Character#isWhitespace
	 */
	public static String trimWhitespace(String str) {
		if (!hasLength(str)) {
			return str;
		}
		StringBuffer buf = new StringBuffer(str);
		while (buf.length() > 0 && Character.isWhitespace(buf.charAt(0))) {
			buf.deleteCharAt(0);
		}
		while (buf.length() > 0 && Character.isWhitespace(buf.charAt(buf.length() - 1))) {
			buf.deleteCharAt(buf.length() - 1);
		}
		return buf.toString();
	}

	/**
	 * Trim <i>all</i> whitespace from the given String:
	 * leading, trailing, and inbetween characters.
	 * @param str the String to check
	 * @return the trimmed String
	 * @see java.lang.Character#isWhitespace
	 */
	public static String trimAllWhitespace(String str) {
		if (!hasLength(str)) {
			return str;
		}
		StringBuffer buf = new StringBuffer(str);
		int index = 0;
		while (buf.length() > index) {
			if (Character.isWhitespace(buf.charAt(index))) {
				buf.deleteCharAt(index);
			}
			else {
				index++;
			}
		}
		return buf.toString();
	}

	/**
	 * Trim leading whitespace from the given String.
	 * @param str the String to check
	 * @return the trimmed String
	 * @see java.lang.Character#isWhitespace
	 */
	public static String trimLeadingWhitespace(String str) {
		if (!hasLength(str)) {
			return str;
		}
		StringBuffer buf = new StringBuffer(str);
		while (buf.length() > 0 && Character.isWhitespace(buf.charAt(0))) {
			buf.deleteCharAt(0);
		}
		return buf.toString();
	}

	/**
	 * Trim trailing whitespace from the given String.
	 * @param str the String to check
	 * @return the trimmed String
	 * @see java.lang.Character#isWhitespace
	 */
	public static String trimTrailingWhitespace(String str) {
		if (!hasLength(str)) {
			return str;
		}
		StringBuffer buf = new StringBuffer(str);
		while (buf.length() > 0 && Character.isWhitespace(buf.charAt(buf.length() - 1))) {
			buf.deleteCharAt(buf.length() - 1);
		}
		return buf.toString();
	}

	/**
	 * Trim all occurences of the supplied leading character from the given String.
	 * @param str the String to check
	 * @param leadingCharacter the leading character to be trimmed
	 * @return the trimmed String
	 */
	public static String trimLeadingCharacter(String str, char leadingCharacter) {
		if (!hasLength(str)) {
			return str;
		}
		StringBuffer buf = new StringBuffer(str);
		while (buf.length() > 0 && buf.charAt(0) == leadingCharacter) {
			buf.deleteCharAt(0);
		}
		return buf.toString();
	}

	/**
	 * Trim all occurences of the supplied trailing character from the given String.
	 * @param str the String to check
	 * @param trailingCharacter the trailing character to be trimmed
	 * @return the trimmed String
	 */
	public static String trimTrailingCharacter(String str, char trailingCharacter) {
		if (!hasLength(str)) {
			return str;
		}
		StringBuffer buf = new StringBuffer(str);
		while (buf.length() > 0 && buf.charAt(buf.length() - 1) == trailingCharacter) {
			buf.deleteCharAt(buf.length() - 1);
		}
		return buf.toString();
	}


	/**
	 * Test if the given String starts with the specified prefix,
	 * ignoring upper/lower case.
	 * @param str the String to check
	 * @param prefix the prefix to look for
	 * @see java.lang.String#startsWith
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
	 * @param str the String to check
	 * @param suffix the suffix to look for
	 * @see java.lang.String#endsWith
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
	 * Test whether the given string matches the given substring
	 * at the given index.
	 * @param str the original string (or StringBuffer)
	 * @param index the index in the original string to start matching against
	 * @param substring the substring to match at the given index
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
	 * @param str string to search in. Return 0 if this is null.
	 * @param sub string to search for. Return 0 if this is null.
	 */
	public static int countOccurrencesOf(String str, String sub) {
		if (str == null || sub == null || str.length() == 0 || sub.length() == 0) {
			return 0;
		}
		int count = 0, pos = 0, idx = 0;
		while ((idx = str.indexOf(sub, pos)) != -1) {
			++count;
			pos = idx + sub.length();
		}
		return count;
	}

	/**
	 * Replace all occurences of a substring within a string with
	 * another string.
	 * @param inString String to examine
	 * @param oldPattern String to replace
	 * @param newPattern String to insert
	 * @return a String with the replacements
	 */
	public static String replace(String inString, String oldPattern, String newPattern) {
		if (inString == null) {
			return null;
		}
		if (oldPattern == null || newPattern == null) {
			return inString;
		}

		StringBuffer sbuf = new StringBuffer();
		// output StringBuffer we'll build up
		int pos = 0; // our position in the old string
		int index = inString.indexOf(oldPattern);
		// the index of an occurrence we've found, or -1
		int patLen = oldPattern.length();
		while (index >= 0) {
			sbuf.append(inString.substring(pos, index));
			sbuf.append(newPattern);
			pos = index + patLen;
			index = inString.indexOf(oldPattern, pos);
		}
		sbuf.append(inString.substring(pos));

		// remember to append any characters to the right of a match
		return sbuf.toString();
	}

	/**
	 * Delete all occurrences of the given substring.
	 * @param inString the original String
	 * @param pattern the pattern to delete all occurrences of
	 * @return the resulting String
	 */
	public static String delete(String inString, String pattern) {
		return replace(inString, pattern, "");
	}

	/**
	 * Delete any character in a given String.
	 * @param inString the original String
	 * @param charsToDelete a set of characters to delete.
	 * E.g. "az\n" will delete 'a's, 'z's and new lines.
	 * @return the resulting String
	 */
	public static String deleteAny(String inString, String charsToDelete) {
		if (!hasLength(inString) || !hasLength(charsToDelete)) {
			return inString;
		}
		StringBuffer out = new StringBuffer();
		for (int i = 0; i < inString.length(); i++) {
			char c = inString.charAt(i);
			if (charsToDelete.indexOf(c) == -1) {
				out.append(c);
			}
		}
		return out.toString();
	}


	//---------------------------------------------------------------------
	// Convenience methods for working with formatted Strings
	//---------------------------------------------------------------------

	/**
	 * Quote the given String with single quotes.
	 * @param str the input String (e.g. "myString")
	 * @return the quoted String (e.g. "'myString'"),
	 * or <code>null<code> if the input was <code>null</code>
	 */
	public static String quote(String str) {
		return (str != null ? "'" + str + "'" : null);
	}

	/**
	 * Turn the given Object into a String with single quotes
	 * if it is a String; keeping the Object as-is else.
	 * @param obj the input Object (e.g. "myString")
	 * @return the quoted String (e.g. "'myString'"),
	 * or the input object as-is if not a String
	 */
	public static Object quoteIfString(Object obj) {
		return (obj instanceof String ? quote((String) obj) : obj);
	}

	/**
	 * Unqualify a string qualified by a '.' dot character. For example,
	 * "this.name.is.qualified", returns "qualified".
	 * @param qualifiedName the qualified name
	 */
	public static String unqualify(String qualifiedName) {
		return unqualify(qualifiedName, '.');
	}

	/**
	 * Unqualify a string qualified by a separator character. For example,
	 * "this:name:is:qualified" returns "qualified" if using a ':' separator.
	 * @param qualifiedName the qualified name
	 * @param separator the separator
	 */
	public static String unqualify(String qualifiedName, char separator) {
		return qualifiedName.substring(qualifiedName.lastIndexOf(separator) + 1);
	}

	/**
	 * Capitalize a <code>String</code>, changing the first letter to
	 * upper case as per {@link Character#toUpperCase(char)}.
	 * No other letters are changed.
	 * @param str the String to capitalize, may be <code>null</code>
	 * @return the capitalized String, <code>null</code> if null
	 */
	public static String capitalize(String str) {
		return changeFirstCharacterCase(str, true);
	}

	/**
	 * Uncapitalize a <code>String</code>, changing the first letter to
	 * lower case as per {@link Character#toLowerCase(char)}.
	 * No other letters are changed.
	 * @param str the String to uncapitalize, may be <code>null</code>
	 * @return the uncapitalized String, <code>null</code> if null
	 */
	public static String uncapitalize(String str) {
		return changeFirstCharacterCase(str, false);
	}

	/**
	 * Extract the filename from the given path,
	 * e.g. "mypath/myfile.txt" -> "myfile.txt".
	 * @param path the file path (may be <code>null</code>)
	 * @return the extracted filename, or <code>null</code> if none
	 */
	public static String getFilename(String path) {
		if (path == null) {
			return null;
		}
		path = cleanPath(path);
		int separatorIndex = path.lastIndexOf(FOLDER_SEPARATOR);
		return (separatorIndex != -1 ? path.substring(separatorIndex + 1) : path);
	}

	/**
	 * Extract the filename extension from the given path,
	 * e.g. "mypath/myfile.txt" -> "txt".
	 * @param path the file path (may be <code>null</code>)
	 * @return the extracted filename extension, or <code>null</code> if none
	 */
	public static String getFilenameExtension(String path) {
		if (path == null) {
			return null;
		}
		int sepIndex = path.lastIndexOf(EXTENSION_SEPARATOR);
		return (sepIndex != -1 ? path.substring(sepIndex + 1) : null);
	}

	/**
	 * Strip the filename extension from the given path,
	 * e.g. "mypath/myfile.txt" -> "mypath/myfile".
	 * @param path the file path (may be <code>null</code>)
	 * @return the path with stripped filename extension,
	 * or <code>null</code> if none
	 */
	public static String stripFilenameExtension(String path) {
		if (path == null) {
			return null;
		}
		int sepIndex = path.lastIndexOf(EXTENSION_SEPARATOR);
		return (sepIndex != -1 ? path.substring(0, sepIndex) : path);
	}

	/**
	 * Apply the given relative path to the given path,
	 * assuming standard Java folder separation (i.e. "/" separators);
	 * @param path the path to start from (usually a full file path)
	 * @param relativePath the relative path to apply
	 * (relative to the full file path above)
	 * @return the full file path that results from applying the relative path
	 */
	public static String applyRelativePath(String path, String relativePath) {
		int separatorIndex = path.lastIndexOf(FOLDER_SEPARATOR);
		if (separatorIndex != -1) {
			String newPath = path.substring(0, separatorIndex);
			if (!relativePath.startsWith(FOLDER_SEPARATOR)) {
				newPath += FOLDER_SEPARATOR;
			}
			return newPath + relativePath;
		}
		else {
			return relativePath;
		}
	}

	/**
	 * Normalize the path by suppressing sequences like "path/.." and
	 * inner simple dots.
	 * <p>The result is convenient for path comparison. For other uses,
	 * notice that Windows separators ("\") are replaced by simple slashes.
	 * @param path the original path
	 * @return the normalized path
	 */
	public static String cleanPath(String path) {
		String pathToUse = replace(path, WINDOWS_FOLDER_SEPARATOR, FOLDER_SEPARATOR);

		// Strip prefix from path to analyze, to not treat it as part of the
		// first path element. This is necessary to correctly parse paths like
		// "file:core/../core/io/Resource.class", where the ".." should just
		// strip the first "core" directory while keeping the "file:" prefix.
		int prefixIndex = pathToUse.indexOf(":");
		String prefix = "";
		if (prefixIndex != -1) {
			prefix = pathToUse.substring(0, prefixIndex + 1);
			pathToUse = pathToUse.substring(prefixIndex + 1);
		}

		String[] pathArray = delimitedListToStringArray(pathToUse, FOLDER_SEPARATOR);
		List<String> pathElements = new LinkedList<String>();
		int tops = 0;

		for (int i = pathArray.length - 1; i >= 0; i--) {
			if (CURRENT_PATH.equals(pathArray[i])) {
				// Points to current directory - drop it.
			}
			else if (TOP_PATH.equals(pathArray[i])) {
				// Registering top path found.
				tops++;
			}
			else {
				if (tops > 0) {
					// Merging path element with corresponding to top path.
					tops--;
				}
				else {
					// Normal path element found.
					pathElements.add(0, pathArray[i]);
				}
			}
		}

		// Remaining top paths need to be retained.
		for (int i = 0; i < tops; i++) {
			pathElements.add(0, TOP_PATH);
		}

		return prefix + collectionToDelimitedString(pathElements, FOLDER_SEPARATOR);
	}

	/**
	 * Compare two paths after normalization of them.
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
	 * @param localeString the locale string, following <code>Locale's</code>
	 * <code>toString()</code> format ("en", "en_UK", etc);
	 * also accepts spaces as separators, as an alternative to underscores
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
		
		String language = (parts.length > 0 ? parts[0] : "");
		String country = (parts.length > 1 ? parts[1] : "");
		String variant = "";
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
	 * @param array the array to append to (can be <code>null</code>)
	 * @param str the String to append
	 * @return the new array (never <code>null</code>)
	 */
	public static String[] addStringToArray(String[] array, String str) {
		if (ObjectUtils.isEmpty(array)) {
			return new String[] {str};
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
	 * included on their first occurence).
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
		List<String> result = new ArrayList<String>();
		result.addAll(Arrays.asList(array1));
		for (int i = 0; i < array2.length; i++) {
			String str = array2[i];
			if (!result.contains(str)) {
				result.add(str);
			}
		}
		return toStringArray(result);
	}

	/**
	 * Turn given source String array into sorted array.
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
	 * @param collection the Collection to copy
	 * @return the String array (<code>null</code> if the passed-in
	 * Collection was <code>null</code>)
	 */
	public static String[] toStringArray(Collection<String> collection) {
		if (collection == null) {
			return null;
		}
		return (String[]) collection.toArray(new String[collection.size()]);
	}

	/**
	 * Copy the given Enumeration into a String array.
	 * The Enumeration must contain String elements only.
	 * @param enumeration the Enumeration to copy
	 * @return the String array (<code>null</code> if the passed-in
	 * Enumeration was <code>null</code>)
	 */
	public static String[] toStringArray(Enumeration<String> enumeration) {
		if (enumeration == null) {
			return null;
		}
		List<String> list = Collections.list(enumeration);
		return (String[]) list.toArray(new String[list.size()]);
	}

	/**
	 * Trim the elements of the given String array,
	 * calling <code>String.trim()</code> on each of them.
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
	 * @param array the String array
	 * @return an array without duplicates, in natural sort order
	 */
	public static String[] removeDuplicateStrings(String[] array) {
		if (ObjectUtils.isEmpty(array)) {
			return array;
		}
		Set<String> set = new TreeSet<String>();
		for (int i = 0; i < array.length; i++) {
			set.add(array[i]);
		}
		return toStringArray(set);
	}

	/**
	 * Split a String at the first occurrence of the delimiter.
	 * Does not include the delimiter in the result.
	 * @param toSplit the string to split
	 * @param delimiter to split the string up with
	 * @return a two element array with index 0 being before the delimiter, and
	 * index 1 being after the delimiter (neither element includes the delimiter);
	 * or <code>null</code> if the delimiter wasn't found in the given input String
	 */
	public static String[] split(String toSplit, String delimiter) {
		if (!hasLength(toSplit) || !hasLength(delimiter)) {
			return null;
		}
		int offset = toSplit.indexOf(delimiter);
		if (offset < 0) {
			return new String[]{toSplit};
		}
		String beforeDelimiter = toSplit.substring(0, offset);
		String afterDelimiter = toSplit.substring(offset + delimiter.length());
		return new String[] {beforeDelimiter, afterDelimiter};
	}

	/**
	 * Take an array Strings and split each element based on the given delimiter.
	 * A <code>Properties</code> instance is then generated, with the left of the
	 * delimiter providing the key, and the right of the delimiter providing the value.
	 * <p>Will trim both the key and value before adding them to the
	 * <code>Properties</code> instance.
	 * @param array the array to process
	 * @param delimiter to split each element using (typically the equals symbol)
	 * @return a <code>Properties</code> instance representing the array contents,
	 * or <code>null</code> if the array to process was null or empty
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
	 * @param array the array to process
	 * @param delimiter to split each element using (typically the equals symbol)
	 * @param charsToDelete one or more characters to remove from each element
	 * prior to attempting the split operation (typically the quotation mark
	 * symbol), or <code>null</code> if no removal should occur
	 * @return a <code>Properties</code> instance representing the array contents,
	 * or <code>null</code> if the array to process was <code>null</code> or empty
	 */
	public static Properties splitArrayElementsIntoProperties(
			String[] array, String delimiter, String charsToDelete) {

		if (ObjectUtils.isEmpty(array)) {
			return null;
		}
		Properties result = new Properties();
		for (int i = 0; i < array.length; i++) {
			String element = array[i];
			if (charsToDelete != null) {
				element = deleteAny(array[i], charsToDelete);
			}
			String[] splittedElement = split(element, delimiter);
			if (splittedElement == null) {
				continue;
			}
			result.setProperty(splittedElement[0].trim(), splittedElement[1].trim());
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
	 * @param str the String to tokenize
	 * @param delimiters the delimiter characters, assembled as String
	 * (each of those characters is individually considered as delimiter).
	 * @return an array of the tokens
	 * @see java.util.StringTokenizer
	 * @see java.lang.String#trim()
	 * @see #delimitedListToStringArray
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
	 * @param str the String to tokenize
	 * @param delimiters the delimiter characters, assembled as String
	 * (each of those characters is individually considered as delimiter)
	 * @param trimTokens trim the tokens via String's <code>trim</code>
	 * @param ignoreEmptyTokens omit empty tokens from the result array
	 * (only applies to tokens that are empty after trimming; StringTokenizer
	 * will not consider subsequent delimiters as token in the first place).
	 * @return an array of the tokens (<code>null</code> if the input String
	 * was <code>null</code>)
	 * @see java.util.StringTokenizer
	 * @see java.lang.String#trim()
	 * @see #delimitedListToStringArray
	 */
	public static String[] tokenizeToStringArray(
			String str, String delimiters, boolean trimTokens, boolean ignoreEmptyTokens) {

		if (str == null) {
			return null;
		}
		StringTokenizer st = new StringTokenizer(str, delimiters);
		List<String> tokens = new ArrayList<String>();
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
	 * @param str the input String
	 * @param delimiter the delimiter between elements (this is a single delimiter,
	 * rather than a bunch individual delimiter characters)
	 * @return an array of the tokens in the list
	 * @see #tokenizeToStringArray
	 */
	public static String[] delimitedListToStringArray(String str, String delimiter) {
		return delimitedListToStringArray(str, delimiter, null);
	}

	/**
	 * Take a String which is a delimited list and convert it to a String array.
	 * <p>A single delimiter can consists of more than one character: It will still
	 * be considered as single delimiter string, rather than as bunch of potential
	 * delimiter characters - in contrast to <code>tokenizeToStringArray</code>.
	 * @param str the input String
	 * @param delimiter the delimiter between elements (this is a single delimiter,
	 * rather than a bunch individual delimiter characters)
	 * @param charsToDelete a set of characters to delete. Useful for deleting unwanted
	 * line breaks: e.g. "\r\n\f" will delete all new lines and line feeds in a String.
	 * @return an array of the tokens in the list
	 * @see #tokenizeToStringArray
	 */
	public static String[] delimitedListToStringArray(String str, String delimiter, String charsToDelete) {
		if (str == null) {
			return new String[0];
		}
		if (delimiter == null) {
			return new String[] {str};
		}
		List<String> result = new ArrayList<String>();
		if ("".equals(delimiter)) {
			for (int i = 0; i < str.length(); i++) {
				result.add(deleteAny(str.substring(i, i + 1), charsToDelete));
			}
		}
		else {
			int pos = 0;
			int delPos = 0;
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
	 * @param str the input String
	 * @return an array of Strings, or the empty array in case of empty input
	 */
	public static String[] commaDelimitedListToStringArray(String str) {
		return delimitedListToStringArray(str, ",");
	}

	/**
	 * Convenience method to convert a CSV string list to a set.
	 * Note that this will suppress duplicates.
	 * @param str the input String
	 * @return a Set of String entries in the list
	 */
	public static Set<String> commaDelimitedListToSet(String str) {
		Set<String> set = new TreeSet<String>();
		String[] tokens = commaDelimitedListToStringArray(str);
		for (int i = 0; i < tokens.length; i++) {
			set.add(tokens[i]);
		}
		return set;
	}

	/**
	 * Convenience method to return a Collection as a delimited (e.g. CSV)
	 * String. E.g. useful for <code>toString()</code> implementations.
	 * @param coll the Collection to display
	 * @param delim the delimiter to use (probably a ",")
	 * @param prefix the String to start each element with
	 * @param suffix the String to end each element with
	 * @return the delimited String
	 */
	public static String collectionToDelimitedString(Collection<String> coll, 
			String delim, String prefix, String suffix) {
		if (CollectionUtils.isEmpty(coll)) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		Iterator<String> it = coll.iterator();
		while (it.hasNext()) {
			sb.append(prefix).append(it.next()).append(suffix);
			if (it.hasNext()) {
				sb.append(delim);
			}
		}
		return sb.toString();
	}

	/**
	 * Convenience method to return a Collection as a delimited (e.g. CSV)
	 * String. E.g. useful for <code>toString()</code> implementations.
	 * @param coll the Collection to display
	 * @param delim the delimiter to use (probably a ",")
	 * @return the delimited String
	 */
	public static String collectionToDelimitedString(Collection<String> coll, String delim) {
		return collectionToDelimitedString(coll, delim, "", "");
	}

	/**
	 * Convenience method to return a Collection as a CSV String.
	 * E.g. useful for <code>toString()</code> implementations.
	 * @param coll the Collection to display
	 * @return the delimited String
	 */
	public static String collectionToCommaDelimitedString(Collection<String> coll) {
		return collectionToDelimitedString(coll, ",");
	}
	
	public static boolean containsIgnoreCase(String string, String search) {
		if (string == null || search == null) {
			return false;
		}
		
		int length = search.length();
		int maxLength = string.length() - length;
		
		for (int i = 0 ; i < maxLength ; i++) {
			if (string.regionMatches(true, i, search, 0, length)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Convenience method to return a String array as a delimited (e.g. CSV)
	 * String. E.g. useful for <code>toString()</code> implementations.
	 * @param arr the array to display
	 * @param delim the delimiter to use (probably a ",")
	 * @return the delimited String
	 */
	public static String arrayToDelimitedString(Object[] arr, String delim) {
		if (ObjectUtils.isEmpty(arr)) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < arr.length; i++) {
			if (i > 0) {
				sb.append(delim);
			}
			sb.append(arr[i]);
		}
		return sb.toString();
	}

	/**
	 * Convenience method to return a String array as a CSV String.
	 * E.g. useful for <code>toString()</code> implementations.
	 * @param arr the array to display
	 * @return the delimited String
	 */
	public static String arrayToCommaDelimitedString(Object[] arr) {
		return arrayToDelimitedString(arr, ",");
	}
	/**
	 * 转换数据库中BLOB类型的数据，并过滤HTML非法代码
	 * @param content		BLOB类型数据
	 * @return
	 * @throws Exception
	 */
	public static String convertContent(byte [] content) throws Exception {
		if (content == null) {
			return null;
		}
		return TextToHtml(ConvertUtils.convertToString(content));
	}
	
	public static String convertObjectToJSONString(Object object) {
		TreeMap<String, Object> valueMap = new TreeMap<String, Object>();
		if (object instanceof Map || object.getClass().isArray() 
				|| Collection.class.isAssignableFrom(object.getClass())) {
			ObjectMapper objectMapper = new ObjectMapper();
			try {
				return objectMapper.writeValueAsString(object);
			} catch (JsonProcessingException e) {
				if (StringUtils.LOGGER.isDebugEnabled()) {
					StringUtils.LOGGER.debug("Convert object to string error! ", e);
				}
			}
		} else {
			Field[] fields = object.getClass().getDeclaredFields();
			for (Field field : fields) {
				if (ReflectionUtils.isStatic(field)) {
					continue;
				}
				Object fieldValue = ReflectionUtils.getFieldValue(field, object);
				Object mapValue = null;
				if (fieldValue instanceof byte[]) {
					mapValue = new Base64().encodeAsString((byte[])fieldValue);
				} else {
					mapValue = fieldValue;
				}
				valueMap.put(field.getName(), mapValue);
			}
			
			ObjectMapper objectMapper = new ObjectMapper();
			try {
				return objectMapper.writeValueAsString(valueMap);
			} catch (JsonProcessingException e) {
				if (StringUtils.LOGGER.isDebugEnabled()) {
					StringUtils.LOGGER.debug("Convert object to string error! ", e);
				}
			}
		}
		
		return null;
	}
	
	public static <T> T convertJSONStringToObject(String jsonData, Class<T> clazz) {
		return ConvertUtils.convertMapToObject(StringUtils.convertJSONStringToMap(jsonData), clazz);
	}
	
	public static <T> List<T> convertJSONStringToList(String jsonData, Class<T> clazz) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			JavaType javaType = objectMapper.getTypeFactory().constructParametricType(ArrayList.class, String.class);
			List<String> dataList = objectMapper.readValue(jsonData, javaType);
			
			List<T> returnList = new ArrayList<T>();
			for (String itemData : dataList) {
				returnList.add(StringUtils.convertJSONStringToObject(itemData, clazz));
			}
			
			return returnList;
		} catch (Exception e) {
			if (StringUtils.LOGGER.isDebugEnabled()) {
				StringUtils.LOGGER.debug("Convert json string to object bean error! ", e);
			}
		}
		
		return null;
	}
	
	public static Map<String, Object> convertJSONStringToMap(String jsonData) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.readValue(jsonData, new TypeReference<Map<String, Object>>() {
			});
		} catch (Exception e) {
			if (StringUtils.LOGGER.isDebugEnabled()) {
				StringUtils.LOGGER.debug("Convert json string to object bean error! ", e);
			}
		}
		
		return new HashMap<String, Object>();
	}
	
	/**
	 * 	判断字符串是否为象形文字
	 */
	public static int isASCII(String s) throws Exception {
		byte[] testString = s.getBytes("UTF-8");
		
		if (testString == null || testString.length > 3 || testString.length <= 0) {
			return 0;
		} else if (testString.length == 1) {
			return testString[0];
		} else if (testString.length == 3) {
			int hightByte = 256 + testString[0];
			int lowByte = 256 + testString[1];
			int ascii = (256 * hightByte + lowByte) - 256 * 256;
			return ascii;
		}
		return 0;
	}

	/**
	 * 过滤字符串，防止XML文件中出现非法字符
	 * @param sourceString	要过滤的字符串
	 * @return	过滤后的字符串
	 */
	public static String formatTextForXML(String sourceString) {
		if (sourceString == null) {
			return null;
		}
		int strLen = 0;
		StringBuffer reString = new StringBuffer();
		String deString = "";
		strLen = sourceString.length();
		
		for (int i = 0 ; i < strLen ; i++) {
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
				deString = "";
				break;
				default:
					deString = "" + ch;
			}
			reString.append(deString);
		}
		return reString.toString();
	}
	
	/**
	 * 过滤字符串，将格式化后的字符串转换为初始状态
	 * @param sourceString
	 * @return
	 */
	public static String formatXMLForText(String sourceString) {
		
		if (sourceString == null) {
			return null;
		}

		sourceString = replace(sourceString, "&amp;", "&");
		sourceString = replace(sourceString, "&lt;", "<");
		sourceString = replace(sourceString, "&gt;", ">");
		sourceString = replace(sourceString, "&quot;", "\"");
		
		return sourceString;
	}
	
	/**
	 * 过滤字符串，防止Html中出现非法字符
	 * @param sourceString
	 * @return
	 */
	public static String TextToHtml (String sourceString) {
		int strLen = 0;
		StringBuffer reString = new StringBuffer();
		String deString = "";
		strLen = sourceString.length();
		
		for (int i = 0 ; i < strLen ; i++) {
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
				deString = "<br/>";
				break;
				default:
					deString = "" + ch;
			}
			reString.append(deString);
		}
		return reString.toString();
	}
	
	/**
	 * Get message key
	 * 
	 * @param locale
	 * @param key
	 * @return
	 */
	public static String messageKey(Locale locale, String key) {
		StringBuffer messageKey = new StringBuffer();
		String localeKey = localeKey(locale);
		if (localeKey.length() > 0) {
			messageKey.append(localeKey(locale) + ".");
		}
		messageKey.append(key);
		
		return messageKey.toString().toUpperCase();
	}
	
	/**
	 * Append localeKey and key
	 * 
	 * @param localeKey
	 * @param key
	 * @return
	 */
	public static String messageKey(String localeKey, String key) {
		return (localeKey + "." + key).toUpperCase();
	}
	
	/**
	 * Convert <code>Locale</code> to <code>String</code>
	 * 		use toString() method. If locale is null, return ""
	 * 
	 * @param locale
	 * @return
	 */
	public static String localeKey(Locale locale) {
		return locale == null ? "" : locale.toString();
	}
	
	/**
	 * Matches with regex
	 * @param str
	 * @param regex
	 * @return
	 */
	public static boolean matches(String str, String regex) {
		if (str == null || regex == null) {
			return Globals.DEFAULT_VALUE_BOOLEAN;
		}
		return Pattern.compile(regex).matcher(str).find();
	}
	
	/**
	 * Replace template string with regex
	 * @param str
	 * @param regex
	 * @param template
	 * @return replaced string. null for match failed
	 */
	public static String replaceWithRegex(String str, String regex, String template) {
		if (!matches(str, regex)) {
			return null;
		}
		
		String matchesString = template;
		Matcher matcher = Pattern.compile(regex).matcher(str);
		matcher.find();
		
		for (int i = 0 ; i < matcher.groupCount() ; i++) {
			int index = i + 1;
			matchesString = replace(matchesString, "$" + index, matcher.group(index));
		}
		
		return matchesString;
	}

	public static String randomString(int length) {
		StringBuffer generateKey = new StringBuffer();
		Random random = new Random();
		for (int i = 0 ; i < length ; i++) {
			generateKey.append(AUTHCODEITEMS.charAt(random.nextInt(AUTHCODEITEMS.length())));
		}
		return generateKey.toString();
	}

	public static String randomNumber(int length) {
		StringBuffer generateKey = new StringBuffer();
		for (int i = 0 ; i < length ; i++) {
			generateKey.append((char)(Math.random() * 10 + '0'));
		}
		return generateKey.toString();
	}

	public static char randomIndex(int beginIndex, int endIndex) {
		return (char)(Math.random() * (endIndex - beginIndex + 1) + beginIndex + '0');
	}
	
	public static char randomChar(int beginIndex, int endIndex) {
		return (char)(Math.random() * (endIndex - beginIndex + 1) + beginIndex + 'a');
	}
	
	/**
	 * Escape url address
	 * @param str
	 * @return
	 */
	public static String escape(String str) {
		int length;
		char ch;
		StringBuffer stringBuffer = new StringBuffer();
		
		stringBuffer.ensureCapacity(str.length() * 6);
		
		for (length = 0; length < str.length(); length++) {
			ch = str.charAt(length);

			if (Character.isDigit(ch) || Character.isLowerCase(ch) || Character.isUpperCase(ch)) {
				stringBuffer.append(ch);
			} else if (length < 256) {
				stringBuffer.append("%");
				stringBuffer.append(Integer.toString(ch, 16));
			} else {
				stringBuffer.append("%u");
				stringBuffer.append(Integer.toString(ch, 16));
			}
		}
		
		return stringBuffer.toString();
	}
	
	/**
	 * Unescape url address
	 * @param str
	 * @return
	 */
	public static String unescape(String str) {
		if (str == null) {
			str = "";
		}
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.ensureCapacity(str.length());
		int lastIndex = 0;
		int index = 0;
		char ch;
		while (lastIndex < str.length()) {
			index = str.indexOf("%", lastIndex);
			if (index == lastIndex)	{
				if (str.charAt(index + 1)=='u')	{
					ch = (char)Integer.parseInt(str.substring(index + 2, index + 6), 16);
					stringBuffer.append(ch);
					lastIndex = index + 6;
				} else {
					ch = (char)Integer.parseInt(str.substring(index + 1, index + 3), 16);
					stringBuffer.append(ch);
					lastIndex = index + 3;
				}
			} else {
				if (index == -1) {
					stringBuffer.append(str.substring(lastIndex));
					lastIndex = str.length();
				} else {
					stringBuffer.append(str.substring(lastIndex,index));
					lastIndex = index;
				}
			}
		}
		return stringBuffer.toString();
	}
	
	public static String getUUID() {
		return UUID.randomUUID().toString();
	}
	
	public static String converterToFirstSpell(String chines) {
		String pinyinName = "";
		char[] nameChar = chines.toCharArray();
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		for (int i = 0; i < nameChar.length; i++) {
			if (nameChar[i] > 128) {
				try {
					pinyinName += PinyinHelper.toHanyuPinyinStringArray(
							nameChar[i], defaultFormat)[0].charAt(0);
				} catch (BadHanyuPinyinOutputFormatCombination e) {
					e.printStackTrace();
				}
			} else {
				pinyinName += nameChar[i];
			}
		}
		return pinyinName;
	}

	/**
	 * 汉字转换位汉语拼音，英文字符不变
	 * 
	 * @param chinese	汉字
	 * @return 拼音
	 */
	public static String converterToSpell(String chinese) {
		String pinyinName = "";
		char[] nameChar = chinese.toCharArray();
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		for (int i = 0; i < nameChar.length; i++) {
			if (nameChar[i] > 128) {
				try {
					pinyinName += PinyinHelper.toHanyuPinyinStringArray(
							nameChar[i], defaultFormat)[0];
				} catch (BadHanyuPinyinOutputFormatCombination e) {
					e.printStackTrace();
				}
			} else {
				pinyinName += nameChar[i];
			}
		}
		return pinyinName.trim();
	}
	
	public static String compress(String content) {
		if (content == null || content.length() == 0) {
			return content;
		}
		
		byte[] zippedByteArray = null;
		
		try {
			zippedByteArray = ConvertUtils.zipByteArray(content.getBytes());
		} catch (IOException e) {
			return content;
		}
		
		if (zippedByteArray.length >= content.getBytes().length) {
			return content;
		} else {
			if (StringUtils.LOGGER.isDebugEnabled()) {
				StringUtils.LOGGER.debug("Compress size : " 
						+ (content.getBytes().length - zippedByteArray.length));
			}
		}

		int length = zippedByteArray.length;
		StringBuffer stringBuffer = new StringBuffer(length * 2);
		for(int i = 0 ; i < length ; i++) {
			int intTmp = zippedByteArray[i];
			
			while (intTmp < 0) {
				intTmp = intTmp + 256;
			}
			
			if (intTmp < 16) {
				stringBuffer.append("0");
			}
			
			stringBuffer.append(Integer.toString(intTmp, 16));
		}
		return stringBuffer.toString();
	}

	public static String deCompress(String strIn) {
		byte[] arrB = strIn.getBytes();
		int iLen = arrB.length;

		byte[] arrOut = new byte[iLen / 2];
		for (int i = 0; i < iLen; i = i + 2) {
			String strTmp = new String(arrB, i, 2);
			try {
				arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);
			} catch (NumberFormatException e) {
				return strIn;
			}
		}
		
		return new String(ConvertUtils.unzipByteArray(arrOut));
	}

	/**
	 * 判断字符是否为空白字符
	 * @param letter
	 * @return
	 */
	public static boolean isSpace(char letter) {
		if (letter == 8 || letter == 9 || letter == 10 || 
				letter == 13 || letter == 32 || letter == 160) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 判断字符是否为英文字母
	 * @param letter
	 * @return
	 */
	public static boolean isEnglish(char letter) {
		if ((letter > 'a' && letter < 'z') || (letter > 'A' && letter < 'Z')) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 判断字符是否为数字
	 * @param letter
	 * @return
	 */
	public static boolean isNumber(char letter) {
		if (letter >= '0' && letter <= '9') {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 判断字符是否为中日韩字符
	 * @param character
	 * @return
	 */
	public static boolean isCJK(char character) {
		UnicodeBlock unicodeBlock = UnicodeBlock.of(character);
		
		if (unicodeBlock == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
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
				|| unicodeBlock == Character.UnicodeBlock.KATAKANA_PHONETIC_EXTENSIONS) {
			return true;
		} else {
			return false;
		}
	}

	public static Object parseSimpleData(String dataValue, Class<?> typeClass) throws ParseException, IOException {
		Object paramObj = null;
		if (dataValue == null || typeClass == null || Globals.DEFAULT_VALUE_STRING.equals(dataValue)) {
			return paramObj;
		}
		
		if (BaseElement.class.isAssignableFrom(typeClass)) {
			paramObj = XmlUtils.convertToObject(dataValue, typeClass);
		} else {
			DataType dataType = ObjectUtils.retrieveSimpleDataType(typeClass);
			
			switch (dataType) {
			case STRING:
				paramObj = StringUtils.formatXMLForText(dataValue);
				break;
			case BOOLEAN:
				paramObj = new Boolean(dataValue);
				break;
			case DATE:
				paramObj = DateTimeUtils.parseSitemapDate(dataValue);
				break;
			case ENUM:
				paramObj = ReflectionUtils.parseEnum(typeClass).get(dataValue);
				break;
			case NUMBER:
				if (typeClass.equals(Integer.class)
						|| typeClass.equals(int.class)) {
					paramObj = Integer.valueOf(dataValue);
				} else if (typeClass.equals(Float.class)
						|| typeClass.equals(float.class)) {
					paramObj = Float.valueOf(dataValue);
				} else if (typeClass.equals(Double.class)
						|| typeClass.equals(double.class)) {
					paramObj = Double.valueOf(dataValue);
				} else if (typeClass.equals(Short.class)
						|| typeClass.equals(short.class)) {
					paramObj = Short.valueOf(dataValue);
				} else if (typeClass.equals(Long.class)
						|| typeClass.equals(long.class)) {
					paramObj = Long.valueOf(dataValue);
				} else if (typeClass.equals(BigInteger.class)) {
					paramObj = new BigInteger(dataValue);
				}
				break;
			case CDATA:
				paramObj = StringUtils.formatXMLForText(dataValue).toCharArray();
				break;
			case BINARY:
				dataValue = StringUtils.replace(dataValue, " ", "");
				paramObj = new Base64().decode(dataValue.getBytes());
				break;
				default:
					paramObj = StringUtils.formatXMLForText(dataValue);
			}
		}
		
		return paramObj;
	}

	/**
	 * 格式化字符
	 * @param character
	 * @return
	 */
	public static char format(char character) {
		if (character == 12288) {
			character = (char)32;
		} else if (character > 65280 && character < 65375) {
			character = (char)(character - 65248);
		} else if (character > 'A' && character < 'Z') {
			character += 32;
		}
		
		return character;
	}
	
	/**
	 * Authentication CHN ID
	 * 验证身份证号码
	 * @param idenCode
	 * @return
	 */
	public static boolean validateCHNIdenCode(String idenCode) {
		idenCode = idenCode.toUpperCase();
		if (StringUtils.matches(idenCode, CHN_IDEN_REGEX)) {
			int result = 0;
			for (int i = 0 ; i < 17 ; i++) {
				int code = Character.digit(idenCode.charAt(i), 10);
				if (code != 0) {
					result += code * CHN_IDEN_WEIGHT[i];
				}
			}
			
			return idenCode.endsWith(Character.toString(CHN_IDEN_AUTHCODE.charAt(result % 11)));
		}
		
		return Globals.DEFAULT_VALUE_BOOLEAN;
	}
	
	/**
	 * Authentication CHN organization code
	 * 验证组织机构代码
	 * @param orgCode
	 * @return
	 */
	public static boolean validateCHNOrgCode(String orgCode) {
		orgCode = orgCode.toUpperCase();
		if (StringUtils.matches(orgCode, CHN_ORG_CODE_REGEX)) {
			int result = 0;
			for (int i = 0 ; i < 8 ; i++) {
				char codeChar = orgCode.charAt(i);
				int code = CHN_ORG_CODE_MAP.indexOf(Character.toString(codeChar));
				if (code != 0) {
					result += code * CHN_ORG_CODE_WEIGHT[i];
				}
			}
			
			int authCode = 11 - (result % 11);
			if (authCode == 11) {
				authCode = 0;
			}
			
			if (authCode == 10) {
				return orgCode.endsWith("X");
			} else {
				return orgCode.endsWith(Integer.valueOf(authCode).toString());
			}
		}
		
		return Globals.DEFAULT_VALUE_BOOLEAN;
	}
	
	/**
	 * Authentication CHN social credit code
	 * 验证统一信用代码
	 * @param socialCreditCode
	 * @return
	 */
	public static boolean validateCHNSocialCreditCode(String socialCreditCode) {
		socialCreditCode = socialCreditCode.toUpperCase();
		if (StringUtils.matches(socialCreditCode, CHN_SOCIAL_CREDIT_REGEX)) {
			int result = 0;
			for (int i = 0 ; i < 17 ; i++) {
				char codeChar = socialCreditCode.charAt(i);
				int code = CHN_SOCIAL_CREDIT_AUTHCODE.indexOf(Character.toString(codeChar));
				if (code != 0) {
					result += code * CHN_SOCIAL_CREDIT_WEIGHT[i];
				}
			}
			
			int authIndex = 31 - (result % 31);
			if (authIndex == 31) {
				authIndex = 0;
			}
			
			return socialCreditCode.endsWith(Character.toString(CHN_SOCIAL_CREDIT_AUTHCODE.charAt(authIndex)));
		}
		
		return Globals.DEFAULT_VALUE_BOOLEAN;
	}
	
	private static String changeFirstCharacterCase(String str, boolean capitalize) {
		if (str == null || str.length() == 0) {
			return str;
		}
		StringBuffer buf = new StringBuffer(str.length());
		if (capitalize) {
			buf.append(Character.toUpperCase(str.charAt(0)));
		}
		else {
			buf.append(Character.toLowerCase(str.charAt(0)));
		}
		buf.append(str.substring(1));
		return buf.toString();
	}
}
