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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.nervousync.commons.core.Globals;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Jan 12, 2010 3:12:05 PM $
 */
public final class ConvertUtils {

	private final static Logger LOGGER = LoggerFactory.getLogger(ConvertUtils.class);

	private ConvertUtils() {
	}
	
	/**
	 * Convert collection to List
	 * @param collection	collection
	 * @return				Convert list
	 */
	public static List<Object> convertCollectionToList(Object collection) {
		List<Object> list;
		
		if (collection instanceof Collection) {
			list = new ArrayList<>((Collection<?>)collection);
		} else if (collection instanceof Enumeration) {
			list = new ArrayList<>();
			Enumeration<?> enumeration = (Enumeration<?>)collection;
			while(enumeration.hasMoreElements()) {
				list.add(enumeration.nextElement());
			}
		} else if (collection instanceof Iterator) {
			list = new ArrayList<>();
			Iterator<?> iterator = (Iterator<?>)collection;
			while(iterator.hasNext()) {
				list.add(iterator.next());
			}
		} else if (collection instanceof Map) {
			list = new ArrayList<>(((Map<?, ?>) collection).entrySet());
		} else if (collection instanceof String) {
			list = Arrays.asList(convertPrimitivesToObjects(((String) collection).toCharArray()));
		} else if (collection instanceof Object[]) {
			list = Arrays.asList((Object[]) collection);
		} else if (collection.getClass().isArray()) {
			list = Arrays.asList(convertPrimitivesToObjects(collection));
		} else {
			// type is not supported
			throw new IllegalArgumentException("Class '" + collection.getClass().getName()
					+ "' is not convertible to java.util.List");
		}

		return list;
	}
	
	/**
	 * Convert primitives to object arrays
	 * @param primitiveArray		primitive arrays
	 * @return		Object arrays
	 */
	public static Object[] convertPrimitivesToObjects(Object primitiveArray) {
		if (!primitiveArray.getClass().isArray()) {
			throw new IllegalArgumentException("Specified object is not array");
		}

		if (primitiveArray instanceof Object[]) {
			throw new IllegalArgumentException("Specified object is not primitive array");
		}

		int length = Array.getLength(primitiveArray);
		Object[] result = new Object[length];
		for (int i = 0; i < length; i++) {
			result[i] = Array.get(primitiveArray, i);
		}

		return result;
	}

	/**
	 * Convert hex string to byte arrays
	 * @param strIn			Hex string
	 * @return				Convert byte arrays
	 */
	public static byte[] hexStrToByteArr(String strIn) {
		byte[] arrB = strIn.getBytes(Charset.defaultCharset());
		int iLen = arrB.length;

		byte[] arrOut = new byte[iLen / 2];
		for (int i = 0; i < iLen; i = i + 2) {
			String strTmp = new String(arrB, i, 2, Charset.defaultCharset());
			arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);
		}
		return arrOut;
	}
	
	/**
	 * Convert hex to String
	 * @param sourceBytes	hex byte arrays
	 * @return convert String
	 */
	public static String byteArrayToHexString(byte [] sourceBytes) {
		int length = sourceBytes.length;
		StringBuilder stringBuilder = new StringBuilder(length * 2);
		for (byte source : sourceBytes) {
			int intTmp = source;

			while (intTmp < 0) {
				intTmp = intTmp + 256;
			}

			if (intTmp < 16) {
				stringBuilder.append("0");
			}

			stringBuilder.append(Integer.toString(intTmp, 16));
		}
		return stringBuilder.toString();
	}
	
	/**
	 * Converts byte array to string using default encoding
	 *
	 * @param content Byte array to convert to string
	 * @return string resulted from converting byte array using default encoding
	 */
	public static String convertToString(byte[] content) {
		return convertToString(content, Globals.DEFAULT_ENCODING);
	}

	/**
	 * Converts byte array to string according to specified encoding
	 *
	 * @param content  Byte array to convert to string
	 * @param encoding Encoding string, if <code>null</code> default is used
	 * @return string resulted from converting byte array
	 */
	public static String convertToString(byte[] content, String encoding) {
		try {
			return new String(content, encoding);
		} catch (UnsupportedEncodingException ex) {
			return new String(content, Charset.defaultCharset());
		}
	}

	/**
	 * Converts string to byte array using default encoding
	 *
	 * @param content String to convert to array
	 * @return byte array resulted from converting string using default encoding
	 */
	public static byte[] convertToByteArray(String content) {
		return convertToByteArray(content, Globals.DEFAULT_ENCODING);
	}

	/**
	 * Converts string to byte array according to specified encoding
	 *
	 * @param content  String to convert to array
	 * @param encoding Encoding string, if <code>null</code> default is used
	 * @return byte array
	 */
	public static byte[] convertToByteArray(String content, String encoding) {
		try {
			return content.getBytes(encoding);
		} catch (UnsupportedEncodingException ex) {
			return content.getBytes(Charset.defaultCharset());
		}
	}
	
	/**
	 * Convert object to byte array
	 * @param object		if <code>null</code> convert error
	 * @return byte array
	 */
	public static byte[] convertToByteArray(Object object) {
		if (object instanceof String) {
			return convertToByteArray((String)object);
		}

		if (object instanceof byte[] || object instanceof Byte[]) {
			assert object instanceof byte[];
			return (byte[])object;
		}

		ByteArrayOutputStream outputStream = null;
		ObjectOutputStream objectOutputStream = null;
		try {
			outputStream = new ByteArrayOutputStream();
			objectOutputStream = new ObjectOutputStream(outputStream);
			objectOutputStream.writeObject(object);
			return outputStream.toByteArray();
		} catch (Exception e) {
			if (ConvertUtils.LOGGER.isDebugEnabled()) {
				ConvertUtils.LOGGER.debug("Convert object to byte[] error! ", e);
			}
		} finally {
			IOUtils.closeStream(objectOutputStream);
			IOUtils.closeStream(outputStream);
		}
		
		return new byte[0];
	}

	/**
	 * Convert byte array to Object
	 * @param content       byte array
	 * @return              Converted object or byte array when failed
	 */
	public static Object convertToObject(byte[] content) {
		if (content.length == 0) {
			return null;
		}
		
		ByteArrayInputStream byteInputStream = null;
		ObjectInputStream objectInputStream = null;
		try {
			byteInputStream = new ByteArrayInputStream(content);
			objectInputStream = new ObjectInputStream(byteInputStream);
			
			return objectInputStream.readObject();
		} catch (Exception e) {
			return content;
		} finally {
			IOUtils.closeStream(objectInputStream);
			IOUtils.closeStream(byteInputStream);
		}
	}

	/**
	 * Compress given data bytes using gzip
	 * @param dataBytes     Data bytes
	 * @return              Compressed byte array
	 */
	public static byte[] zipByteArray(byte[] dataBytes) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		GZIPOutputStream gzipOutputStream = null;

		try {
			gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
			gzipOutputStream.write(dataBytes);
			
			gzipOutputStream.close();
			byteArrayOutputStream.close();
			
			return byteArrayOutputStream.toByteArray();
		} catch (Exception ex) {
			return dataBytes;
		} finally {
			IOUtils.closeStream(gzipOutputStream);
			IOUtils.closeStream(byteArrayOutputStream);
		}
	}

	/**
	 * Decompress given data bytes which data compressed by gzip
	 * @param dataBytes     Compressed data bytes
	 * @return              Decompressed data bytes
	 */
	public static byte[] unzipByteArray(byte[] dataBytes) {
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(dataBytes);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		GZIPInputStream gzipInputStream = null;
		byte[] readBuffer = new byte[Globals.DEFAULT_BUFFER_SIZE];
		try {
			gzipInputStream = new GZIPInputStream(byteArrayInputStream);
			int readLength;
			while ((readLength = gzipInputStream.read(readBuffer)) != -1) {
				byteArrayOutputStream.write(readBuffer, 0, readLength);
			}
			gzipInputStream.close();
			byteArrayInputStream.close();
			byteArrayOutputStream.close();
			
			return byteArrayOutputStream.toByteArray();
		} catch (Exception ex) {
			return dataBytes;
		} finally {
			IOUtils.closeStream(gzipInputStream);
			IOUtils.closeStream(byteArrayInputStream);
			IOUtils.closeStream(byteArrayOutputStream);
		}
	}
}
