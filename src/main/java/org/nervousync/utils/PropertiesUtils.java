/*
 * Copyright 2017 Nervousync Studio
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.nervousync.commons.core.Globals;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Mar 5, 2010 11:03:51 AM $
 */
public final class PropertiesUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesUtils.class);
	
	private PropertiesUtils() {
	}

	/**
	 * Read properties file and convert data to hash table
	 * @param propertiesFilePath    Properties file path
	 * @return                      Data hash table
	 */
	public static Hashtable<String, String> convertPropertiesToHashtable(String propertiesFilePath) {
		return convertPropertiesToHashtable(propertiesFilePath, null);
	}

	/**
	 * Read properties file and write data to given hash table
	 * @param propertiesFilePath    Properties file path
	 * @param messageMap            Exists hash table to write data
	 * @return                      Data hash table
	 */
	public static Hashtable<String, String> convertPropertiesToHashtable(String propertiesFilePath, 
			Hashtable<String, String> messageMap) {
		return convertPropertiesToHashtable(loadProperties(propertiesFilePath), messageMap);
	}

	/**
	 * Read properties file from URL and convert data to hash table
	 * @param url    Properties file url
	 * @return       Data hash table
	 */
	public static Hashtable<String, String> convertPropertiesToHashtable(URL url) {
		return convertPropertiesToHashtable(url, null);
	}

	/**
	 * Read properties file from URL and write data to given hash table
	 * @param url                   Properties file url
	 * @param messageMap            Exists hash table to write data
	 * @return                      Data hash table
	 */
	public static Hashtable<String, String> convertPropertiesToHashtable(URL url, Hashtable<String, String> messageMap) {
		return convertPropertiesToHashtable(loadProperties(url), messageMap);
	}

	/**
	 * Read data from properties object and write to given hash table
	 * @param properties        Properties object
	 * @param messageMap        Exists hash table to write data
	 * @return                  Data hash table
	 */
	public static Hashtable<String, String> convertPropertiesToHashtable(Properties properties, 
			Hashtable<String, String> messageMap) {
		if (messageMap == null) {
			messageMap = new Hashtable<>();
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
	 * @param propertiesContent     string data
	 * @return                      Properties object
	 */
	public static Properties convertStringToProperties(String propertiesContent) {
		Properties properties = new Properties();
		InputStream inputStream;
		if (propertiesContent != null) {
			inputStream = new ByteArrayInputStream(propertiesContent.getBytes(Charset.forName(Globals.DEFAULT_ENCODING)));
			
			try {
				if (propertiesContent.startsWith("<")) {
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
	 * @param propertiesFilePath    Properties file path
	 * @return                      Properties object
	 */
	public static Properties loadProperties(String propertiesFilePath) {
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
	public static Properties loadProperties(URL url) {
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
			if (PropertiesUtils.LOGGER.isDebugEnabled()) {
				PropertiesUtils.LOGGER.debug("Load properties error! ", e);
			}
			return new Properties();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					if (PropertiesUtils.LOGGER.isDebugEnabled()) {
						PropertiesUtils.LOGGER.debug("Close input stream error! ", e);
					} else {
						PropertiesUtils.LOGGER.warn("Close input stream error! ");
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
	public static Properties loadProperties(InputStream inputStream, boolean isXML) {
		Properties properties = new Properties();
		try {
			if (isXML) {
				properties.loadFromXML(inputStream);
			} else {
				properties.load(inputStream);
			}
			
			return properties;
		} catch (Exception e) {
			if (PropertiesUtils.LOGGER.isDebugEnabled()) {
				PropertiesUtils.LOGGER.debug("Load properties error! ", e);
			}
			return new Properties();
		}
	}

	/**
	 * Write given data and comment to target properties file
	 * @param propertiesFilePath    Properties file path
	 * @param modifyMap             Data hash table
	 * @param comment               Comment string
	 * @return                      Operate result
	 */
	public static boolean modifyProperties(String propertiesFilePath, Map<String, String> modifyMap, String comment) {
		try {
			Properties modifyProperties = loadProperties(propertiesFilePath);

			modifyMap.forEach((key, value) -> {
				if (value != null) {
					modifyProperties.setProperty(key, value);
				}
			});

			return storeProperties(modifyProperties, propertiesFilePath, comment);
		} catch (Exception e) {
			if (PropertiesUtils.LOGGER.isDebugEnabled()) {
				PropertiesUtils.LOGGER.debug("Modify properties error! ", e);
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
	public static Properties modifyProperties(Properties properties, Map<String, String> modifyMap) {

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
	 * Write properties object to target path
	 * @param properties            Properties Object
	 * @param propertiesFilePath    Properties file path
	 * @param comment               Comment string
	 * @return                      Operate result
	 */
	private static boolean storeProperties(Properties properties, String propertiesFilePath, String comment) {
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
			if (PropertiesUtils.LOGGER.isDebugEnabled()) {
				PropertiesUtils.LOGGER.debug("Save properties error! ", e);
			}
			return false;
		} finally {
			IOUtils.closeStream(fileOutputStream);
		}
	}

	/**
	 * Read properties file from given path and retrieve value by given key name
	 * @param propertiesFilePath    Properties file path
	 * @param keyName               Key name
	 * @return                      Retrieve value or null if not found
	 */
	public static String getPropertiesValue(String propertiesFilePath, String keyName) {
		if (keyName == null) {
			return null;
		}
		return loadProperties(propertiesFilePath).getProperty(keyName);
	}
}
