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

import java.io.*;

import org.nervousync.commons.core.Globals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Jun 3, 2015 11:20:20 AM $
 */
public final class IOUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(IOUtils.class);
	
	private IOUtils() {
	}

	/**
	 * Read byte[] from current input stream use default charset UTF-8
	 * @param inputStream		Input stream
	 * @return	Data by byte arrays
	 */
	public static byte[] readBytes(InputStream inputStream) {
		ByteArrayOutputStream byteArrayOutputStream = null;
		
		byte[] content;
		try {
			byteArrayOutputStream = new ByteArrayOutputStream(Globals.DEFAULT_BUFFER_SIZE);
			
			byte[] buffer = new byte[Globals.DEFAULT_BUFFER_SIZE];
			int readLength;
			while ((readLength = inputStream.read(buffer)) != -1) {
				byteArrayOutputStream.write(buffer, 0, readLength);
			}
			
			content = byteArrayOutputStream.toByteArray();
		} catch (Exception e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Catch error! ", e);
			}
			content = new byte[0];
		} finally {
			closeStream(byteArrayOutputStream);
		}
		
		return content;
	}

	/**
	 * Read String content from the current input stream use default charset: UTF-8
	 * @param inputStream		Input stream
	 * @return File content as string
	 */
	public static String readContent(InputStream inputStream) {
		return IOUtils.readContent(inputStream, Globals.DEFAULT_ENCODING);
	}

	/**
	 * Read String content from current input stream use current encoding
	 * @param inputStream		Input stream
	 * @param encoding			Charset encoding
	 * @return File content as string
	 */
	public static String readContent(InputStream inputStream, String encoding) {
		char [] readBuffer = new char[Globals.DEFAULT_BUFFER_SIZE];
		int len;
		StringBuilder returnValue = new StringBuilder();
		
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;
		try {
			inputStreamReader = new InputStreamReader(inputStream, encoding);
			bufferedReader = new BufferedReader(inputStreamReader);
			
			while ((len = bufferedReader.read(readBuffer)) > -1) {
				returnValue.append(readBuffer, 0, len);
			}
		} catch (Exception e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Catch error! ", e);
			}
			return returnValue.toString();
		} finally {
			closeStream(inputStreamReader);
			closeStream(bufferedReader);
			closeStream(inputStream);
		}
		return returnValue.toString();
	}

	/**
	 * Copy data from input stream to output stream
	 * @param inputStream				Input stream
	 * @param outputStream				Output stream
	 * @param closeOutputAfterCopy		close output stream after copy
	 * @return	copy length
	 * @throws IOException	if an I/O error occurs
	 */
	public static long copyStream(InputStream inputStream, 
			OutputStream outputStream, boolean closeOutputAfterCopy) throws IOException {
		return copyStream(inputStream, outputStream, 
				closeOutputAfterCopy, new byte[Globals.DEFAULT_BUFFER_SIZE]);
	}
	
	/**
	 * Copy data from input stream to output stream using given buffer
	 * @param inputStream				Input stream
	 * @param outputStream				Output stream
	 * @param closeOutputAfterCopy		close output stream after copy
	 * @param buffer					Copy buffer
	 * @return	copy length
	 * @throws IOException	if an I/O error occurs
	 */
	public static long copyStream(InputStream inputStream, OutputStream outputStream,
			boolean closeOutputAfterCopy, byte[] buffer) throws IOException {
		if (inputStream == null) {
			return 0L;
		}
		try {
			long totalCount = 0L;
			int readCount = inputStream.read(buffer);
			while (readCount != Globals.DEFAULT_VALUE_INT) {
				totalCount += readCount;
				outputStream.write(buffer, 0, readCount);
				readCount = inputStream.read(buffer);
			}
			outputStream.flush();
			return totalCount;
		} finally {
			closeStream(inputStream);
            if (closeOutputAfterCopy) {
            	closeStream(outputStream);
            }
		}
	}

	/**
	 * Close current stream
	 * @param closeable     stream to close
	 */
	public static void closeStream(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
				LOGGER.error("Close stream error! ");
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Catch error! ", e);
				}
			}
		}
	}
}
