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
package org.nervousync.zip.models.header.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.nervousync.commons.core.Globals;
import org.nervousync.commons.core.zip.ZipConstants;
import org.nervousync.utils.StringUtils;
import org.nervousync.zip.models.header.LocalFileHeader;
import org.nervousync.exceptions.zip.ZipException;
import org.nervousync.utils.RawUtils;

/**
 * @author Steven Wee   <a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Dec 2, 2017 6:07:27 PM $
 */
public final class HeaderOperator {

	public static int retrieveSaltLength(int aesStrength) throws ZipException {
		int saltLength;
		switch (aesStrength) {
			case ZipConstants.AES_STRENGTH_128:
				saltLength = 8;
				break;
			case ZipConstants.AES_STRENGTH_192:
				saltLength = 12;
				break;
			case ZipConstants.AES_STRENGTH_256:
				saltLength = 16;
				break;
			default:
				throw new ZipException("unable to determine salt length: invalid aes key strength");
		}
		return saltLength;
	}

	public static int writeExtendedLocalHeader(LocalFileHeader localFileHeader, OutputStream outputStream)
			throws ZipException, IOException {
		if (localFileHeader == null || outputStream == null) {
			throw new ZipException("input parameters is null, cannot write extended local header");
		}

		List<String> byteArrayList = new ArrayList<>();
		byte[] intBuffer = new byte[4];

		// Extended local file header signature
		RawUtils.writeInt(intBuffer, RawUtils.Endian.LITTLE, (int) ZipConstants.EXTSIG);
		copyByteArrayToList(intBuffer, byteArrayList);

		// CRC
		RawUtils.writeInt(intBuffer, RawUtils.Endian.LITTLE, (int) localFileHeader.getCrc32());
		copyByteArrayToList(intBuffer, byteArrayList);

		// Compressed size
		long compressedSize = localFileHeader.getCompressedSize();
		if (compressedSize > Integer.MAX_VALUE) {
			compressedSize = Integer.MAX_VALUE;
		}
		RawUtils.writeInt(intBuffer, RawUtils.Endian.LITTLE, (int) compressedSize);
		copyByteArrayToList(intBuffer, byteArrayList);

		// Original size
		long originalSize = localFileHeader.getOriginalSize();
		if (originalSize > Integer.MAX_VALUE) {
			originalSize = Integer.MAX_VALUE;
		}
		RawUtils.writeInt(intBuffer, RawUtils.Endian.LITTLE, (int) originalSize);
		copyByteArrayToList(intBuffer, byteArrayList);

		byte[] extendLocationHdrBytes = convertByteArrayListToByteArray(byteArrayList);
		outputStream.write(extendLocationHdrBytes);
		return extendLocationHdrBytes.length;
	}

	public static byte[] convertByteArrayListToByteArray(List<String> arrayList) throws ZipException {
		if (arrayList == null) {
			throw new ZipException("input byte array list is null, cannot convert to byte array");
		}

		if (arrayList.size() > 0) {
			byte[] returnBytes = new byte[arrayList.size()];

			for (int i = 0; i < arrayList.size(); i++) {
				returnBytes[i] = Byte.parseByte(arrayList.get(i));
			}

			return returnBytes;
		}

		return new byte[0];
	}

	public static void appendShortToArrayList(short value, List<String> arrayList) throws ZipException {
		byte[] shortBuffer = new byte[2];
		RawUtils.writeShort(shortBuffer, RawUtils.Endian.LITTLE, value);
		HeaderOperator.copyByteArrayToList(shortBuffer, arrayList);
	}

	public static void appendIntToArrayList(int value, List<String> arrayList) throws ZipException {
		byte[] intBuffer = new byte[4];
		RawUtils.writeInt(intBuffer, RawUtils.Endian.LITTLE, value);
		HeaderOperator.copyByteArrayToList(intBuffer, arrayList);
	}

	public static void appendLongToArrayList(long value, List<String> arrayList) throws ZipException {
		byte[] longBuffer = new byte[8];
		RawUtils.writeLong(longBuffer, RawUtils.Endian.LITTLE, value);
		HeaderOperator.copyByteArrayToList(longBuffer, arrayList);
	}

	public static void copyByteArrayToList(byte[] byteArray, List<String> arrayList) throws ZipException {
		if (arrayList == null || byteArray == null) {
			throw new ZipException("one of the input parameters is null, cannot copy byte array to array list");
		}

		for (byte b : byteArray) {
			arrayList.add(Byte.toString(b));
		}
	}

	public static void copyByteArrayToList(String entryPath, List<String> arrayList) throws ZipException {
		copyByteArrayToList(convertCharset(entryPath), arrayList);
	}

	/**
	 * Convert charset byte [ ].
	 *
	 * @param string the string
	 * @return the byte [ ]
	 * @throws ZipException the zip exception
	 */
	private static byte[] convertCharset(String string) throws ZipException {
		try {
			byte[] converted;
			String charSet = StringUtils.detectCharset(string);
			switch (charSet) {
				case ZipConstants.CHARSET_CP850:
					converted = string.getBytes(ZipConstants.CHARSET_CP850);
					break;
				case Globals.DEFAULT_ENCODING:
					converted = string.getBytes(Globals.DEFAULT_ENCODING);
					break;
				default:
					converted = string.getBytes(Charset.forName(Globals.DEFAULT_ENCODING));
					break;
			}
			return converted;
		} catch (UnsupportedEncodingException err) {
			return string.getBytes(Charset.defaultCharset());
		} catch (Exception e) {
			throw new ZipException(e);
		}
	}
}
