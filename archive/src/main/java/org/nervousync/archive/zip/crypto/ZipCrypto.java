/*
 * Licensed to the Nervousync Studio (NSYC) under one or more
 * contributor license agreements. See the NOTICE file distributed with
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

package org.nervousync.archive.zip.crypto;

import org.nervousync.archive.exceptions.ArchiveException;
import org.nervousync.archive.zip.models.crypto.StrongRecipientTable;
import org.nervousync.archive.zip.models.header.StrongDecryptionHeader;
import org.nervousync.archive.zip.security.EncryptionProvider;
import org.nervousync.commons.Globals;
import org.nervousync.commons.zip.ZipGlobals;
import org.nervousync.exceptions.crypto.CryptoException;
import org.nervousync.exceptions.utils.DataInvalidException;
import org.nervousync.security.CryptoAdaptor;
import org.nervousync.utils.commons.RawUtils;
import org.nervousync.utils.security.DigestUtils;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

/**
 * <h2 class="en-US">Zip archive crypto</h2>
 * <h2 class="zh-CN">归档文件加密解密</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Oct 26, 2026 09:37:28 $
 */
public final class ZipCrypto {

	public enum PasswordEncoding {
		UTF8, LEGACY_LOW_BYTE
	}

	/**
	 * <h3 class="en-US">Convert the password from character array to bytes array</h3>
	 * <h3 class="zh-CN">转换密码字符数组为字节数组</h3>
	 *
	 * @param password <span class="en-US">Password</span>
	 *                 <span class="zh-CN">密码</span>
	 * @param encoding <span class="en-US">Password encoding</span>
	 *                 <span class="zh-CN">编码类型</span>
	 * @return <span class="en-US">Bytes array</span>
	 * <span class="zh-CN">字节数组</span>
	 * @throws ArchiveException <span class="en-US">If an error occurs when convert data</span>
	 *                          <span class="zh-CN">转换编码过程中出错</span>
	 */
	public static byte[] encodePassword(final char[] password, final PasswordEncoding encoding) throws ArchiveException {
		Objects.requireNonNull(password, "password cannot be null");
		if (password.length == 0) {
			throw new ArchiveException(0x0000001B0006L);
		}
		switch (encoding) {
			case UTF8:
				return encodeUTF8(password);
			case LEGACY_LOW_BYTE:
				return encodeLegacy(password);
			default:
				throw new ArchiveException(0x0000001A0027L);
		}
	}

	/**
	 * <h3 class="en-US">Convert the password from character array to bytes array</h3>
	 * <h3 class="zh-CN">转换密码字符数组为字节数组</h3>
	 *
	 * @param password <span class="en-US">Password</span>
	 *                 <span class="zh-CN">密码</span>
	 * @return <span class="en-US">Bytes array</span>
	 * <span class="zh-CN">字节数组</span>
	 * @throws ArchiveException <span class="en-US">If an error occurs when convert data</span>
	 *                          <span class="zh-CN">转换编码过程中出错</span>
	 */
	private static byte[] encodeUTF8(final char[] password) throws ArchiveException {
		ByteBuffer encoded = null;
		try {
			encoded = StandardCharsets.UTF_8.newEncoder()
					.onMalformedInput(CodingErrorAction.REPORT)
					.onUnmappableCharacter(CodingErrorAction.REPORT)
					.encode(CharBuffer.wrap(password));
			byte[] result = new byte[encoded.remaining()];
			encoded.get(result);
			return result;
		} catch (CharacterCodingException e) {
			throw new ArchiveException(0x0000001A0027L, e);
		} finally {
			if (encoded != null && encoded.hasArray()) {
				Arrays.fill(encoded.array(), (byte) 0);
			}
		}
	}

	/**
	 * <h3 class="en-US">Convert the password from character array to bytes array</h3>
	 * <h3 class="zh-CN">转换密码字符数组为字节数组</h3>
	 *
	 * @param password <span class="en-US">Password</span>
	 *                 <span class="zh-CN">密码</span>
	 * @return <span class="en-US">Bytes array</span>
	 * <span class="zh-CN">字节数组</span>
	 */
	private static byte[] encodeLegacy(final char[] password) {
		byte[] result = new byte[password.length];
		for (int i = 0; i < password.length; i++) {
			result[i] = (byte) password[i];
		}
		return result;
	}

	public static byte[] deriveKey(final byte[] saltBytes, final byte[] password, final int dkLen)
			throws CryptoException, DataInvalidException {
		//	PBKDF2
		if (password == null || password.length == 0) {
			throw new NullPointerException();
		}
		byte[] passwordBytes = null, tempBytes = null;
		try {
			passwordBytes = password.clone();
			CryptoAdaptor digestProvider = DigestUtils.HmacSHA1(passwordBytes);

			int dkLength = (dkLen == 0) ? digestProvider.macLength() : dkLen;
			byte[] salt = (saltBytes == null) ? new byte[0] : saltBytes;

			int length = digestProvider.macLength();
			int l = ceil(dkLength, length);
			int r = dkLength - (l - 1) * length;
			tempBytes = new byte[l * length];
			int offset = 0;
			for (int i = 1; i <= l; i++) {
				process(digestProvider, tempBytes, offset, salt, i);
				offset += length;
			}

			if (r < length) {
				byte[] bytes = new byte[dkLength];
				System.arraycopy(tempBytes, 0, bytes, 0, dkLength);
				return bytes;
			}
			return tempBytes.clone();
		} finally {
			RawUtils.clear(passwordBytes);
			RawUtils.clear(tempBytes);
		}
	}

	private static int ceil(final int a, final int b) {
		int m = 0;
		if (a % b > 0) {
			m = 1;
		}
		return a / b + m;
	}

	private static void process(final CryptoAdaptor digestProvider,
	                            final byte[] dest, final int offset, final byte[] source, final int blockIndex)
			throws CryptoException, DataInvalidException {
		int length = digestProvider.macLength();
		byte[] tempBytes = new byte[length];
		byte[] intTmpBytes = new byte[source.length + 4];
		try {
			System.arraycopy(source, Globals.INITIALIZE_INT_VALUE, intTmpBytes, Globals.INITIALIZE_INT_VALUE, source.length);
			RawUtils.writeInt(intTmpBytes, source.length, blockIndex);

			for (int i = 0; i < 1000; i++) {
				intTmpBytes = digestProvider.finish(intTmpBytes);
				XOR(tempBytes, intTmpBytes);
			}
			System.arraycopy(tempBytes, Globals.INITIALIZE_INT_VALUE, dest, offset, length);
		} finally {
			RawUtils.clear(tempBytes);
			RawUtils.clear(intTmpBytes);
		}
	}

	private static void XOR(final byte[] dest, final byte[] source) {
		for (int i = 0; i < dest.length; i++) {
			dest[i] ^= source[i];
		}
	}

	public static StrongDecryptionHeader readStrongDecryptionHeader(final DataInputStream inputStream,
	                                                                final long maxHeaderBytes) throws IOException {
		int ivLength = Short.toUnsignedInt(Short.reverseBytes(inputStream.readShort()));
		if (ivLength == 0) {
			throw new IOException("IVSize=0 requires entry CRC/size context; this overload does not reconstruct it");
		}
		if (maxHeaderBytes < 6L + ivLength) {
			throw new IOException("Strong header exceeds configured limit");
		}
		byte[] iv = new byte[ivLength];
		inputStream.readFully(iv);
		long size = Integer.toUnsignedLong(Integer.reverseBytes(inputStream.readInt()));
		long serializedSize = 6L + ivLength + size;
		if (size < 16 || size > Integer.MAX_VALUE || serializedSize > maxHeaderBytes) {
			throw new IOException("Invalid Strong header size");
		}
		byte[] bytes = new byte[(int) size];
		inputStream.readFully(bytes);
		ByteBuffer body = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
		if (body.getShort() != 3) {
			throw new IOException("Invalid Strong decryption format");
		}
		int algorithm = Short.toUnsignedInt(body.getShort());
		int bits = Short.toUnsignedInt(body.getShort());
		int flags = Short.toUnsignedInt(body.getShort());
		if (!EncryptionProvider.validateFlags(flags)) {
			throw new IOException("Invalid Strong flags");
		}
		int erdSize = Short.toUnsignedInt(body.getShort());
		if (erdSize > body.remaining() - 6) {
			throw new IOException("Invalid ErdSize");
		}
		byte[] erd = new byte[erdSize];
		body.get(erd);
		StrongRecipientTable recipients = StrongRecipientTable.read(body, ZipGlobals.MAX_RECIPIENTS);
		if (((flags & 2) == 0) == (recipients != null)) {
			throw new IOException("Strong recipient mode mismatch");
		}
		if (body.remaining() < 2) {
			throw new IOException("Missing validation length");
		}
		int validationSize = Short.toUnsignedInt(body.getShort());
		if (validationSize <= 4 || validationSize != body.remaining()) {
			throw new IOException("Invalid validation size");
		}
		byte[] validation = new byte[validationSize];
		body.get(validation);
		return new StrongDecryptionHeader(iv, algorithm, bits, flags, erd, validation, serializedSize, recipients);
	}
}
