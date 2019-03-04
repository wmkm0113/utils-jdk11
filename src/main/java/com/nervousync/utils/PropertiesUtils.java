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
package com.nervousync.utils;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nervousync.commons.core.Globals;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Mar 5, 2010 11:03:51 AM $
 */
public final class PropertiesUtils {

	private transient static final Logger LOGGER = LoggerFactory.getLogger(PropertiesUtils.class);
	
	private PropertiesUtils() {
		
	}

	public static Hashtable<String, String> convertPropertiesToHashtable(String propertiesFilePath) {
		return convertPropertiesToHashtable(propertiesFilePath, null);
	}
	
	public static Hashtable<String, String> convertPropertiesToHashtable(String propertiesFilePath, 
			Hashtable<String, String> messageMap) {
		Properties properties = loadProperties(propertiesFilePath);
		
		return convertPropertiesToHashtable(properties, messageMap);
	}
	
	public static Hashtable<String, String> convertPropertiesToHashtable(URL url) {
		return convertPropertiesToHashtable(url, null);
	}
	
	public static Hashtable<String, String> convertPropertiesToHashtable(URL url, Hashtable<String, String> messageMap) {
		return convertPropertiesToHashtable(loadProperties(url), messageMap);
	}
	
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
	
	public static Properties convertStringToProperties(String propertiesContent) {
		Properties properties = new Properties();
		InputStream inputStream;
		if (propertiesContent != null) {
			inputStream = new ByteArrayInputStream(propertiesContent.getBytes());
			
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
	
	public static Properties loadProperties(String propertiesFilePath) {
		try {
			URL url = FileUtils.getURL(propertiesFilePath);
			return loadProperties(url);
		} catch (Exception e) {
			return new Properties();
		}
	}
	
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
	
	public static boolean modifyProperties(String propertiesFilePath, Map<String, String> modifyMap, String comment) {
		try {
			Properties modifyProperties = loadProperties(propertiesFilePath);

			for (String key : modifyMap.keySet()) {
				String value = modifyMap.get(key);
				if (value != null) {
					modifyProperties.setProperty(key, value);
				}
			}

			return storeProperties(modifyProperties, propertiesFilePath, comment);
		} catch (Exception e) {
			if (PropertiesUtils.LOGGER.isDebugEnabled()) {
				PropertiesUtils.LOGGER.debug("Modify properties error! ", e);
			}
			return false;
		}
	}

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

	private static boolean storeProperties(Properties properties, String propertiesFilePath, String comment) {
		FileOutputStream fileOutputStream = null;
		try {
			String filePath = propertiesFilePath.substring(0,
					propertiesFilePath.lastIndexOf(Globals.DEFAULT_PAGE_SEPARATOR));
			FileUtils.makeHome(filePath);
			String fileExtName = StringUtils.getFilenameExtension(propertiesFilePath);

			fileOutputStream = new FileOutputStream(propertiesFilePath, false);

			if (StringUtils.endsWithIgnoreCase(propertiesFilePath, "xml")) {
				properties.storeToXML(fileOutputStream, comment, "UTF-8");
			} else if (StringUtils.endsWithIgnoreCase(propertiesFilePath, "properties")) {
				properties.store(fileOutputStream, comment);
			} else {
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
	
	public static boolean saveProperties(Properties properties, String propertiesFilePath, String comment) {
		return storeProperties(properties, propertiesFilePath, comment);
	}
	
	public static String getPropertiesValue(String propertiesFilePath, String keyName) {
		if (keyName == null) {
			return null;
		}
		
		Properties properties = loadProperties(propertiesFilePath);
		
		if (properties == null) {
			return null;
		}
		
		return properties.getProperty(keyName);
	}
}
