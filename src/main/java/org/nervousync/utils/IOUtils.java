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

import org.nervousync.commons.Globals;

/**
 * <h2 class="en">Input/Output Utilities</h2>
 * <h2 class="zh-CN">输入/输出工具集</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Jun 3, 2015 11:20:20 $
 */
public final class IOUtils {
    /**
     * <span class="en">Logger instance</span>
     * <span class="zh-CN">日志实例</span>
     */
	private static final LoggerUtils.Logger LOGGER = LoggerUtils.getLogger(IOUtils.class);
	/**
	 * <h3 class="en">Private constructor for IOUtils</h3>
	 * <h3 class="zh-CN">输入/输出工具集的私有构造方法</h3>
	 */
	private IOUtils() {
	}
	/**
	 * <h3 class="en">Read data bytes from given input stream instance</h3>
	 * <h3 class="en">从给定的输入流实例对象中读取字节数组</h3>
	 *
	 * @param inputStream 	<span class="en">input stream instance</span>
	 *                      <span class="zh-CN">输入流实例对象</span>
	 *
	 * @return 	<span class="en">Read data bytes, or zero length byte array if an error occurs</span>
	 * 			<span class="zh-CN">读取的字节数组，如果读取过程中出现异常则返回长度为0的字节数组</span>
	 */
	public static byte[] readBytes(final InputStream inputStream) {
		return readBytes(inputStream, Globals.DEFAULT_VALUE_INT, Globals.DEFAULT_VALUE_INT);
	}
	/**
	 * <h3 class="en">Read data bytes from given input stream instance</h3>
	 * <h3 class="en">从给定的输入流实例对象中读取字节数组</h3>
	 *
	 * @param inputStream 	<span class="en">input stream instance</span>
	 *                      <span class="zh-CN">输入流实例对象</span>
     * @param offset    <span class="en">read offset</span>
     *                  <span class="zh-CN">读取起始偏移量</span>
     * @param length    <span class="en">read length</span>
     *                  <span class="zh-CN">读取数据长度</span>
	 *
	 * @return 	<span class="en">Read data bytes, or zero length byte array if an error occurs</span>
	 * 			<span class="zh-CN">读取的字节数组，如果读取过程中出现异常则返回长度为0的字节数组</span>
	 */
	public static byte[] readBytes(final InputStream inputStream, final int offset, final int length) {
		ByteArrayOutputStream byteArrayOutputStream = null;
		byte[] content;
		try {
			byteArrayOutputStream = new ByteArrayOutputStream(Globals.DEFAULT_BUFFER_SIZE);
			byte[] readBuffer = new byte[Globals.DEFAULT_BUFFER_SIZE];
			int position = Globals.INITIALIZE_INT_VALUE, readLength = Globals.INITIALIZE_INT_VALUE, currentLength;
			while ((currentLength = inputStream.read(readBuffer)) != Globals.DEFAULT_VALUE_INT) {
				if (offset != Globals.DEFAULT_VALUE_INT && (position + currentLength) < offset) {
					position += currentLength;
					continue;
				}
				int off, len;
				if (offset == Globals.DEFAULT_VALUE_INT || offset < position) {
					off = Globals.INITIALIZE_INT_VALUE;
				} else {
					off = offset - position;
				}
				if (length == Globals.DEFAULT_VALUE_INT || (readLength + currentLength) < length) {
					len = currentLength;
				} else {
					len = length - readLength;
					if (currentLength < len) {
						len = currentLength;
					}
				}
				byteArrayOutputStream.write(readBuffer, off, len);
				position += currentLength;
				readLength += len;
			}
			content = byteArrayOutputStream.toByteArray();
		} catch (Exception e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Utils", "Stack_Message_Error", e);
			}
			content = new byte[0];
		} finally {
			closeStream(byteArrayOutputStream);
		}

		return content;
	}
	/**
	 * <h3 class="en">Read data bytes from given input stream instance use default charset: UTF-8</h3>
	 * <h3 class="en">使用UTF-8编码从给定的输入流实例对象中读取字节数组</h3>
	 *
	 * @param inputStream 	<span class="en">input stream instance</span>
	 *                      <span class="zh-CN">输入流实例对象</span>
	 *
	 * @return 	<span class="en">Read string, or zero length string if an error occurs</span>
	 * 			<span class="zh-CN">读取的字符串，如果读取过程中出现异常则返回空字符串</span>
	 */
	public static String readContent(final InputStream inputStream) {
		return IOUtils.readContent(inputStream, Globals.DEFAULT_ENCODING);
	}
	/**
	 * <h3 class="en">Read string from given input stream instance</h3>
	 * <h3 class="en">从给定的输入流实例对象中读取字符串</h3>
	 *
	 * @param inputStream 	<span class="en">input stream instance</span>
	 *                      <span class="zh-CN">输入流实例对象</span>
	 * @param encoding		<span class="en">Charset encoding</span>
	 *                      <span class="zh-CN">字符集编码</span>
	 *
	 * @return 	<span class="en">Read string, or zero length string if an error occurs</span>
	 * 			<span class="zh-CN">读取的字符串，如果读取过程中出现异常则返回空字符串</span>
	 */
	public static String readContent(final InputStream inputStream, final String encoding) {
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
				LOGGER.debug("Utils", "Stack_Message_Error", e);
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
	 * <h3 class="en">Copy data bytes from given input stream instance to output stream instance</h3>
	 * <h3 class="en">从给定的输入流实例对象中复制数据到给定的输出流实例对象中</h3>
	 *
	 * @param inputStream 			<span class="en">input stream instance</span>
	 *                      		<span class="zh-CN">输入流实例对象</span>
	 * @param outputStream			<span class="en">output stream instance</span>
	 *                      		<span class="zh-CN">输出流实例对象</span>
	 * @param closeOutputAfterCopy	<span class="en">close output stream after copy</span>
	 *                              <span class="zh-CN">是否在完成复制后关闭输出流实例对象</span>
	 *
	 * @return 	<span class="en">Copy length of data bytes</span>
	 * 			<span class="zh-CN">复制的数据长度</span>
	 *
	 * @throws IOException
	 * <span class="en">if an I/O error occurs</span>
	 * <span class="zh-CN">当复制过程中出现异常</span>
	 */
	public static long copyStream(final InputStream inputStream, final OutputStream outputStream,
								  final boolean closeOutputAfterCopy) throws IOException {
		return copyStream(inputStream, outputStream, closeOutputAfterCopy, new byte[Globals.DEFAULT_BUFFER_SIZE]);
	}
	/**
	 * <h3 class="en">Copy data bytes from given input stream instance to output stream instance using given buffer</h3>
	 * <h3 class="en">使用给定的缓冲区从给定的输入流实例对象中复制数据到给定的输出流实例对象中</h3>
	 *
	 * @param inputStream 			<span class="en">input stream instance</span>
	 *                      		<span class="zh-CN">输入流实例对象</span>
	 * @param outputStream			<span class="en">output stream instance</span>
	 *                      		<span class="zh-CN">输出流实例对象</span>
	 * @param closeOutputAfterCopy	<span class="en">close output stream after copy</span>
	 *                              <span class="zh-CN">是否在完成复制后关闭输出流实例对象</span>
	 * @param buffer				<span class="en">copy buffer</span>
	 *                              <span class="zh-CN">复制缓冲区</span>
	 *
	 * @return 	<span class="en">Copy length of data bytes</span>
	 * 			<span class="zh-CN">复制的数据长度</span>
	 *
	 * @throws IOException
	 * <span class="en">if an I/O error occurs</span>
	 * <span class="zh-CN">当复制过程中出现异常</span>
	 */
	public static long copyStream(final InputStream inputStream, final OutputStream outputStream,
								  final boolean closeOutputAfterCopy, final byte[] buffer) throws IOException {
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
	 * <h3 class="en">Close current stream instance</h3>
	 * <h3 class="en">关闭给定的流实例对象</h3>
	 *
	 * @param closeable 	<span class="en">Stream instance will close</span>
	 *                      <span class="zh-CN">即将关闭的流实例对象</span>
	 */
	public static void closeStream(final Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
				LOGGER.error("Utils", "Close_Stream_IO_Error");
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Utils", "Stack_Message_Error", e);
				}
			}
		}
	}
}
