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
package com.nervousync.commons.zip.models.header.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.nervousync.commons.core.zip.ZipConstants;
import com.nervousync.commons.zip.models.header.LocalFileHeader;
import com.nervousync.exceptions.zip.ZipException;
import com.nervousync.utils.RawUtils;

/**
 * @author Steven Wee   <a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Dec 2, 2017 6:07:27 PM $
 */
public final class HeaderOperator {

	public static int writeExtendedLocalHeader(LocalFileHeader localFileHeader, OutputStream outputStream)
			throws ZipException, IOException {
		if (localFileHeader == null || outputStream == null) {
			throw new ZipException("input parameters is null, cannot write extended local header");
		}

		List<String> byteArrayList = new ArrayList<String>();
		byte[] intBuffer = new byte[4];

		// Extended local file header signature
		RawUtils.writeIntFromLittleEndian(intBuffer, 0, (int) ZipConstants.EXTSIG);
		copyByteArrayToArrayList(intBuffer, byteArrayList);

		// CRC
		RawUtils.writeIntFromLittleEndian(intBuffer, 0, (int) localFileHeader.getCrc32());
		copyByteArrayToArrayList(intBuffer, byteArrayList);

		// Compressed size
		long compressedSize = localFileHeader.getCompressedSize();
		if (compressedSize > Integer.MAX_VALUE) {
			compressedSize = Integer.MAX_VALUE;
		}
		RawUtils.writeIntFromLittleEndian(intBuffer, 0, (int) compressedSize);
		copyByteArrayToArrayList(intBuffer, byteArrayList);

		// Original size
		long originalSize = localFileHeader.getOriginalSize();
		if (originalSize > Integer.MAX_VALUE) {
			originalSize = Integer.MAX_VALUE;
		}
		RawUtils.writeIntFromLittleEndian(intBuffer, 0, (int) originalSize);
		copyByteArrayToArrayList(intBuffer, byteArrayList);

		byte[] extendLocationHdrBytes = convertByteArrayListToByteArray(byteArrayList);
		outputStream.write(extendLocationHdrBytes);
		return extendLocationHdrBytes.length;
	}

	public static byte[] convertByteArrayListToByteArray(List<String> arrayList) throws ZipException {
		if (arrayList == null) {
			throw new ZipException("input byte array list is null, cannot conver to byte array");
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

	public static void copyByteArrayToArrayList(byte[] byteArray, List<String> arrayList) throws ZipException {
		if (arrayList == null || byteArray == null) {
			throw new ZipException("one of the input parameters is null, cannot copy byte array to array list");
		}

		for (byte b : byteArray) {
			arrayList.add(Byte.toString(b));
		}
	}
}
