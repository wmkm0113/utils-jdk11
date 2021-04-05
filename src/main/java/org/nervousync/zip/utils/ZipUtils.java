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

package org.nervousync.zip.utils;

import org.nervousync.commons.core.Globals;
import org.nervousync.commons.core.zip.ZipConstants;
import org.nervousync.exceptions.zip.ZipException;
import org.nervousync.utils.StringUtils;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: 2021/2/5 18:08 $
 */
public final class ZipUtils {

	/**
	 * Supported charset boolean.
	 *
	 * @param charset the charset
	 * @return the boolean
	 */
	public static boolean supportedCharset(String charset) {
		try {
			new String("a".getBytes(Charset.defaultCharset()), charset);
			return true;
		} catch (UnsupportedEncodingException e) {
			return Globals.DEFAULT_VALUE_BOOLEAN;
		}
	}

	/**
	 * returns the length of the string by wrapping it in a byte buffer with
	 * the appropriate charset of the input string and returns the limit of the
	 * byte buffer
	 *
	 * @param str string
	 * @return length of the string
	 * @throws ZipException if input string is null. In case of any other exception this method returns default System charset
	 */
	public static int getEncodedStringLength(String str) throws ZipException {
		if (StringUtils.isEmpty(str)) {
			throw new ZipException("input string is null, cannot calculate encoded String length");
		}

		String charset = detectCharSet(str);
		return getEncodedStringLength(str, charset);
	}

	/**
	 * returns the length of the string in the input encoding
	 *
	 * @param str     string
	 * @param charset charset encoding
	 * @return length of the string
	 * @throws ZipException if input string is null. In case of any other exception this method returns default System charset
	 */
	public static int getEncodedStringLength(String str, String charset) throws ZipException {
		if (StringUtils.isEmpty(str)) {
			throw new ZipException("input string is null, cannot calculate encoded String length");
		}

		if (StringUtils.isEmpty(charset)) {
			throw new ZipException("encoding is not defined, cannot calculate string length");
		}

		ByteBuffer byteBuffer;

		try {
			switch (charset) {
				case ZipConstants.CHARSET_CP850:
					byteBuffer = ByteBuffer.wrap(str.getBytes(ZipConstants.CHARSET_CP850));
					break;
				case Globals.DEFAULT_ENCODING:
					byteBuffer = ByteBuffer.wrap(str.getBytes(Globals.DEFAULT_ENCODING));
					break;
				default:
					byteBuffer = ByteBuffer.wrap(str.getBytes(charset));
					break;
			}
		} catch (UnsupportedEncodingException e) {
			byteBuffer = ByteBuffer.wrap(str.getBytes(Charset.defaultCharset()));
		} catch (Exception e) {
			throw new ZipException(e);
		}

		return byteBuffer.limit();
	}

	/**
	 * Detects the encoding charset for the input string
	 *
	 * @param str string
	 * @return String - charset for the String
	 * @throws ZipException if input string is null. In case of any other exception this method returns default System charset
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
		} catch (Exception e) {
			return Globals.DEFAULT_SYSTEM_CHARSET;
		}
	}
}
