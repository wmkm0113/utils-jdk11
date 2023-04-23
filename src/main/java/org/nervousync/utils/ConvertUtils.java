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
import java.lang.reflect.Array;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.nervousync.commons.core.Globals;

/**
 * The type Convert utils.
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: Jan 12, 2010 3:12:05 PM $
 */
public final class ConvertUtils {

	private final static Logger LOGGER = LoggerFactory.getLogger(ConvertUtils.class);

	private ConvertUtils() {
	}

	/**
	 * Convert the collection to List
	 *
	 * @param collection collection
	 * @return Convert list
	 */
	public static List<Object> collectionToList(final Object collection) {
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
	 *
	 * @param primitiveArray primitive arrays
	 * @return Object arrays
	 */
	public static Object[] convertPrimitivesToObjects(final Object primitiveArray) {
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
	 * Byte to hex string.
	 *
	 * @param dataBytes the data bytes
	 * @return the string
	 */
	public static String byteToHex(final byte[] dataBytes) {
		if (dataBytes == null) {
			return Globals.DEFAULT_VALUE_STRING;
		}

		StringBuilder stringBuilder = new StringBuilder();
		for (byte b : dataBytes) {
			String tmp = Integer.toHexString(b & 0xFF);
			if (tmp.length() == 1) {
				stringBuilder.append("0");
			}
			stringBuilder.append(tmp);
		}
		return stringBuilder.toString();
	}

	/**
	 * Converts byte arrays to string using default encoding
	 *
	 * @param content Byte array to convert to string
	 * @return string resulted from converting byte arrays using default encoding
	 */
	public static String convertToString(final byte[] content) {
		return convertToString(content, Globals.DEFAULT_ENCODING);
	}

	/**
	 * Converts byte arrays to string according to specified encoding
	 *
	 * @param content  Byte array to convert to string
	 * @param encoding Encoding string, if <code>null</code> default is used
	 * @return string resulted from converting byte arrays
	 */
	public static String convertToString(final byte[] content, final String encoding) {
		try {
			return new String(content, encoding);
		} catch (UnsupportedEncodingException ex) {
			return new String(content, Charset.defaultCharset());
		}
	}

	/**
	 * Converts string to byte arrays using default encoding
	 *
	 * @param content String to convert to array
	 * @return byte arrays resulted from converting string using default encoding
	 */
	public static byte[] objectToByteArray(final String content) {
		return objectToByteArray(content, Globals.DEFAULT_ENCODING);
	}

	/**
	 * Converts string to byte arrays according to specified encoding
	 *
	 * @param content  String to convert to array
	 * @param encoding Encoding string, if <code>null</code> default is used
	 * @return byte arrays
	 */
	public static byte[] objectToByteArray(final String content, final String encoding) {
		try {
			return content.getBytes(encoding);
		} catch (UnsupportedEncodingException ex) {
			return content.getBytes(Charset.defaultCharset());
		}
	}

	/**
	 * Convert the object to byte arrays
	 *
	 * @param object if <code>null</code> convert error
	 * @return byte arrays
	 */
	public static byte[] objectToByteArray(final Object object) {
		if (object instanceof String) {
			return objectToByteArray((String)object);
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
	 * Convert byte arrays to Object
	 *
	 * @param content byte arrays
	 * @return Converted object or byte arrays when failed
	 */
	public static Object byteArrayToObject(final byte[] content) {
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
	 * Read properties file and convert data to hash table
	 * @param propertiesFilePath    The properties file paths
	 * @return                      Data hash table
	 */
	public static Map<String, String> propertiesToMap(final String propertiesFilePath) {
		return propertiesToMap(propertiesFilePath, null);
	}

	/**
	 * Read properties file and write data to given hash table
	 * @param propertiesFilePath    Properties file paths
	 * @param messageMap            Exists hash table to write data
	 * @return                      Data hash table
	 */
	public static Map<String, String> propertiesToMap(final String propertiesFilePath,
	                                                         Map<String, String> messageMap) {
		return propertiesToMap(loadProperties(propertiesFilePath), messageMap);
	}

	/**
	 * Read properties file from URL and convert data to hash table
	 * @param url    Properties file url
	 * @return       Data hash table
	 */
	public static Map<String, String> propertiesToMap(final URL url) {
		return propertiesToMap(url, null);
	}

	/**
	 * Read properties file from URL and write data to given hash table
	 * @param url                   Properties file url
	 * @param messageMap            Exists hash table to write data
	 * @return                      Data hash table
	 */
	public static Map<String, String> propertiesToMap(final URL url, Map<String, String> messageMap) {
		return propertiesToMap(loadProperties(url), messageMap);
	}

	/**
	 * Read data from properties' object and write to given hash table
	 * @param properties        Properties object
	 * @param messageMap        Exists hash table to write data
	 * @return                  Data hash table
	 */
	public static Map<String, String> propertiesToMap(final Properties properties, Map<String, String> messageMap) {
		if (messageMap == null) {
			messageMap = new HashMap<>();
		}

		if (properties != null) {
			Enumeration<Object> enumeration = properties.keys();
			
			while (enumeration.hasMoreElements()) {
				String key = (String)enumeration.nextElement();
				String value = properties.getProperty(key);
				
				messageMap.put(key, value);
			}
			
		}
		
		return messageMap;
	}

	/**
	 * Read properties from string
	 * @param content     string data
	 * @return                      Properties object
	 */
	public static Properties readProperties(final String content) {
		Properties properties = new Properties();
		InputStream inputStream;
		if (StringUtils.notBlank(content)) {
			inputStream = new ByteArrayInputStream(content.getBytes(Charset.forName(Globals.DEFAULT_ENCODING)));
			
			try {
				if (content.startsWith("<")) {
					properties.loadFromXML(inputStream);
				} else {
					properties.load(inputStream);
				}
				
				inputStream.close();
				inputStream = null;
			} catch (IOException e) {
				properties = new Properties();
			} finally {
				IOUtils.closeStream(inputStream);
			}
		}
		return properties;
	}

	/**
	 * Read properties from string
	 * @param propertiesFilePath    Properties file paths
	 * @return                      Properties object
	 */
	public static Properties loadProperties(final String propertiesFilePath) {
		try {
			URL url = FileUtils.getURL(propertiesFilePath);
			return loadProperties(url);
		} catch (Exception e) {
			return new Properties();
		}
	}

	/**
	 * Read properties from URL
	 * @param url    Properties file URL
	 * @return       Properties object
	 */
	public static Properties loadProperties(final URL url) {
		InputStream inputStream = null;
		try {
			String fileName = url.getFile();
			String fileExtName = StringUtils.getFilenameExtension(fileName);
			inputStream = url.openStream();
			if (fileExtName.equalsIgnoreCase("xml")) {
				return loadProperties(inputStream, true);
			} else {
				return loadProperties(inputStream, false);
			}
		} catch (Exception e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Load properties error! ", e);
			}
			return new Properties();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Close input stream error! ", e);
					} else {
						LOGGER.warn("Close input stream error! ");
					}
				}
			}
		}
	}

	/**
	 * Read properties from InputStream
	 * @param inputStream   Properties input stream
	 * @param isXML         Data is xml
	 * @return              Properties object
	 */
	public static Properties loadProperties(final InputStream inputStream, final boolean isXML) {
		Properties properties = new Properties();
		try {
			if (isXML) {
				properties.loadFromXML(inputStream);
			} else {
				properties.load(inputStream);
			}
			
			return properties;
		} catch (Exception e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Load properties error! ", e);
			}
			return new Properties();
		}
	}

	/**
	 * Write given data and comment to target properties file
	 * @param propertiesFilePath    Properties file paths
	 * @param modifyMap             Data hash table
	 * @param comment               Comment string
	 * @return                      Operate result
	 */
	public static boolean modifyProperties(final String propertiesFilePath, final Map<String, String> modifyMap,
	                                       final String comment) {
		try {
			Properties modifyProperties = loadProperties(propertiesFilePath);

			modifyMap.forEach((key, value) -> {
				if (value != null) {
					modifyProperties.setProperty(key, value);
				}
			});

			return storeProperties(modifyProperties, propertiesFilePath, comment);
		} catch (Exception e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Modify properties error! ", e);
			}
			return false;
		}
	}

	/**
	 * Write given data to target properties object
	 * @param properties    Properties Object
	 * @param modifyMap     Data hash table
	 * @return              Operate result
	 */
	public static Properties modifyProperties(final Properties properties, final Map<String, String> modifyMap) {

		for (Object o : properties.keySet()) {
			String key = (String) o;
			String value = modifyMap.get(key);

			if (value != null) {
				properties.setProperty(key, value);
			}
		}
		
		return properties;
	}

	/**
	 * Write properties' object to the target path
	 * @param properties            Properties Object
	 * @param propertiesFilePath    Properties file paths
	 * @param comment               Comment string
	 * @return                      Operate result
	 */
	private static boolean storeProperties(final Properties properties, final String propertiesFilePath,
	                                       final String comment) {
		FileOutputStream fileOutputStream = null;
		try {
			String filePath = propertiesFilePath.substring(0,
					propertiesFilePath.lastIndexOf(Globals.DEFAULT_PAGE_SEPARATOR));
			FileUtils.makeDir(filePath);
			String fileExtName = StringUtils.getFilenameExtension(propertiesFilePath);

			fileOutputStream = new FileOutputStream(propertiesFilePath, false);

			switch (fileExtName.toLowerCase()) {
				case "xml":
					properties.storeToXML(fileOutputStream, comment, Globals.DEFAULT_ENCODING);
					break;
				case "properties":
					properties.store(fileOutputStream, comment);
					break;
				default:
					throw new Exception("Properties file error");
			}
			return true;
		} catch (Exception e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Save properties error! ", e);
			}
			return false;
		} finally {
			IOUtils.closeStream(fileOutputStream);
		}
	}
}
