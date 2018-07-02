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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.CRC32;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbFileOutputStream;

import com.nervousync.commons.beans.files.FileExtensionInfo;
import com.nervousync.commons.beans.xml.files.SegmentationFileInfo;
import com.nervousync.commons.core.Globals;
import com.nervousync.commons.core.MIMETypes;
import com.nervousync.commons.zip.ZipFile;
import com.nervousync.commons.zip.operator.RawOperator;

/**
 * File operate utils
 * 	support zip/unzip Files Folders
 * 
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Jan 13, 2010 11:08:14 AM $
 */
public final class FileUtils {
	
	private transient static final Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);
	
	/** URL prefix for loading from the class path: "classpath:" */
	public static final String CLASSPATH_URL_PREFIX = "classpath:";

	/** URL prefix for loading from the file system: "file:" */
	public static final String FILE_URL_PREFIX = "file:";

	/** URL prefix for loading from the samba path: "smb:" */
	public static final String SAMBA_URL_PREFIX = "smb:";

	/** URL protocol for a file in the file system: "file" */
	public static final String URL_PROTOCOL_FILE = "file";

	/** URL protocol for an entry from a jar file: "jar" */
	public static final String URL_PROTOCOL_JAR = "jar";

	/** URL protocol for an entry from a zip file: "zip" */
	public static final String URL_PROTOCOL_ZIP = "zip";

	/** URL protocol for an entry from a WebSphere jar file: "wsjar" */
	public static final String URL_PROTOCOL_WSJAR = "wsjar";

	/** URL protocol for an entry from an OC4J jar file: "code-source" */
	public static final String URL_PROTOCOL_CODE_SOURCE = "code-source";

	/** Separator between JAR URL and file path within the JAR */
	public static final String JAR_URL_SEPARATOR = "!/";

	private static Hashtable<String, FileExtensionInfo> REGISTER_IDEN_MAP = new Hashtable<String, FileExtensionInfo>();
	
	/** Define file type code for picture */
	public static final int FILE_TYPE_PIC 				= 0;
	/** Define file type code for audio */
	public static final int FILE_TYPE_AUDIO 			= 1;
	/** Define file type code for video */
	public static final int FILE_TYPE_VIDEO 			= 2;
	/** Define file type code for jar */
	public static final int FILE_TYPE_JAVA_PACKAGE 		= 3;
	/** Define file type code for java */
	public static final int FILE_TYPE_JAVA_FILE 		= 4;
	/** Define file type code for java class */
	public static final int FILE_TYPE_JAVA_CLASS 		= 5;
	/** Define file type code for properties */
	public static final int FILE_TYPE_JAVA_RESOURCE 	= 6;
	/** Define file type code for compress */
	public static final int FILE_TYPE_COMPRESS 			= 7;
	/** Define file type code for document */
	public static final int FILE_TYPE_DOCUMENT 			= 8;
	/** Define file type code for xml */
	public static final int FILE_TYPE_XML 				= 9;
	/** Define file type code for xml */
	public static final int FILE_TYPE_JSON 				= 10;
	/** Define file type code for access database */
	public static final int FILE_TYPE_DATABASE_FILE 	= 11;
	/** Define file type code for sql */
	public static final int FILE_TYPE_DATABASE_SQL 		= 12;
	/** Define file type code for outlook email configure */
	public static final int FILE_TYPE_EMAIL_CONF 		= 13;
	/** Define file type code for eml */
	public static final int FILE_TYPE_EMAIL_DOCUMENT 	= 14;
	/** Define file type code for html */
	public static final int FILE_TYPE_WEB_HTML 			= 15;
	/** Define file type code for javascript */
	public static final int FILE_TYPE_WEB_JS 			= 16;
	/** Define file type code for css */
	public static final int FILE_TYPE_WEB_CSS 			= 17;
	/** Define file type code for unknown */
	public static final int FILE_TYPE_UNKNOWN 			= 18;
	
	private FileUtils() {
	}
	
	static {
		FileUtils.registerFileType();
	}
	
	/**
	 * Register user define file type
	 * @param extName 			file extension name
	 * @param identifiedCode	file identified code
	 * @param fileType			file type code
	 * @param printing			file is ready for printing
	 * @param mediaFile			is media file
	 * @param compressFile		is compress file
	 */
	public static void registerFileType(String extensionName, String identifiedCode, 
			int fileType, boolean printing) {
		if (FileUtils.REGISTER_IDEN_MAP.containsKey(extensionName)) {
			FileUtils.LOGGER.warn("Override file type define! Ext name: " + extensionName);
		}
		
		FileUtils.REGISTER_IDEN_MAP.put(extensionName, 
				new FileExtensionInfo(extensionName, identifiedCode, null, fileType, printing));
	}
	
	/**
	 * Register user define file type
	 * @param extName 			file extension name
	 * @param identifiedCode	file identified code
	 * @param mimeType			file mime type
	 * @param fileType			file type code
	 * @param printing			file is ready for printing
	 * @param mediaFile			is media file
	 * @param compressFile		is compress file
	 */
	public static void registerFileType(String extensionName, String identifiedCode, String mimeType, 
			int fileType, boolean printing) {
		if (FileUtils.REGISTER_IDEN_MAP.containsKey(extensionName)) {
			FileUtils.LOGGER.warn("Override file type define! Ext name: " + extensionName);
		}
		
		if (mimeType == null) {
			mimeType = MIMETypes.MIME_TYPE_BINARY;
		}
		
		FileUtils.REGISTER_IDEN_MAP.put(extensionName, 
				new FileExtensionInfo(extensionName, identifiedCode, mimeType, fileType, printing));
	}
	
	public static boolean matchFolder(String entryPath, String folderPath) {
		if (entryPath == null || folderPath == null) {
			return Globals.DEFAULT_VALUE_BOOLEAN;
		}
		
		String convertFolderPath = FileUtils.replacePageSeparator(folderPath) + "|";
		return FileUtils.replacePageSeparator(entryPath).startsWith(convertFolderPath);
	}
	
	public static boolean matchFilePath(String origPath, String destPath, boolean ignoreCase) {
		if (origPath == null || destPath == null) {
			return Globals.DEFAULT_VALUE_BOOLEAN;
		}
		
		String origConvert = FileUtils.replacePageSeparator(origPath);
		String destConvert = FileUtils.replacePageSeparator(destPath);
		
		if (ignoreCase) {
			return origConvert.equalsIgnoreCase(destConvert);
		} else {
			return origConvert.equals(destConvert);
		}
	}
	
	/**
	 * Check the resource location is compressed jar file
	 * @param resourceLocation
	 * @return check status
	 * @throws FileNotFoundException
	 */
	public static boolean isJarFile(String resourceLocation) throws FileNotFoundException {
		if (resourceLocation == null) {
			throw new FileNotFoundException("The resource location is null!");
		}
		
		if (FileUtils.retrieveFileType(resourceLocation) == FileUtils.FILE_TYPE_JAVA_PACKAGE) {
			return FileUtils.validateFileType(resourceLocation);
		}
		return Globals.DEFAULT_VALUE_BOOLEAN;
	}
	
	/**
	 * Retrieve exists file type code
	 * @param resourceLocation
	 * @return found file type code or #FILE_TYPE_UNKNOWN for not found or file identify code check error
	 */
	public static int retrieveFileType(String extensionName) {
		if (extensionName != null) {
			FileExtensionInfo fileExtensionInfo = FileUtils.REGISTER_IDEN_MAP.get(extensionName);
			if (fileExtensionInfo != null) {
				return fileExtensionInfo.getFileType();
			}
		}
		
		return FileUtils.FILE_TYPE_UNKNOWN;
	}
	
	public static String retrieveMimeType(String extensionName) {
		if (extensionName != null) {
			FileExtensionInfo fileExtensionInfo = FileUtils.REGISTER_IDEN_MAP.get(extensionName);
			if (fileExtensionInfo != null) {
				return fileExtensionInfo.getMimeType();
			}
		}
		
		return MIMETypes.MIME_TYPE_BINARY;
	}
	
	/**
	 * Validate resource file with identify code
	 * @param resourceLocation
	 * @return validate result
	 * @throws FileNotFoundException
	 */
	public static boolean validateFileType(String resourceLocation) {
		if (resourceLocation == null) {
			return Globals.DEFAULT_VALUE_BOOLEAN;
		}

		String extensionName = StringUtils.getFilenameExtension(resourceLocation);
		if (FileUtils.REGISTER_IDEN_MAP.containsKey(extensionName)) {
			FileExtensionInfo fileExtensionInfo = FileUtils.REGISTER_IDEN_MAP.get(extensionName);
			
			byte[] fileTypeByte = FileUtils.readFileBytes(resourceLocation, 0, 
					fileExtensionInfo.getIdentifiedCode().length() / 2);
			String fileType = ConvertUtils.byteArrayToHexString(fileTypeByte);
			return fileType.equalsIgnoreCase(fileExtensionInfo.getIdentifiedCode());
		}
		return Globals.DEFAULT_VALUE_BOOLEAN;
	}
	
	public static boolean validateFileType(String extensionName, byte[] fileContent) {
		if (FileUtils.REGISTER_IDEN_MAP.containsKey(extensionName)) {
			FileExtensionInfo fileExtensionInfo = FileUtils.REGISTER_IDEN_MAP.get(extensionName);
			
			int idenLength = fileExtensionInfo.getIdentifiedCode().length() / 2;
			if (fileContent.length < idenLength) {
				return Globals.DEFAULT_VALUE_BOOLEAN;
			}
			byte[] fileTypeByte = new byte[idenLength];
			
			for (int i = 0 ; i < idenLength ; i++) {
				fileTypeByte[i] = fileContent[i];
			}

			String fileType = ConvertUtils.byteArrayToHexString(fileTypeByte);
			return fileType.equalsIgnoreCase(fileExtensionInfo.getIdentifiedCode());
		}
		
		return Globals.DEFAULT_VALUE_BOOLEAN;
	}
	
	/**
	 * Retrieve file extension name with file identified code
	 * @param identifiedByteCode identified code
	 * @return matched file extension name
	 */
	public static String identifiedFileType(byte[] identifiedByteCode) {
		String identifiedCode = ConvertUtils.byteArrayToHexString(identifiedByteCode);
		for (FileExtensionInfo fileExtensionInfo : FileUtils.REGISTER_IDEN_MAP.values()) {
			if (identifiedCode.startsWith(fileExtensionInfo.getIdentifiedCode())) {
				return fileExtensionInfo.getExtensionName();
			}
		}
		return null;
	}
	
	/**
	 * Return whether the given resource location is a URL:
	 * either a special "classpath" pseudo URL or a standard URL.
	 * @param resourceLocation the location String to check
	 * @return true when location qualifies as a URL, Globals.DEFAULT_VALUE_BOOLEAN for others
	 * @see #CLASSPATH_URL_PREFIX
	 * @see java.net.URL
	 */
	public static boolean isUrl(String resourceLocation) {
		if (!FileUtils.isExists(resourceLocation)) {
			return Globals.DEFAULT_VALUE_BOOLEAN;
		}
		
		try {
			new URL(resourceLocation);
			return true;
		} catch (MalformedURLException ex) {
			return Globals.DEFAULT_VALUE_BOOLEAN;
		}
	}

	/**
	 * Resolve the given resource location to a <code>java.net.URL</code>.
	 * <p>Does not check whether the URL actually exists; simply returns
	 * the URL that the given location would correspond to.
	 * @param resourceLocation the resource location to resolve: either a
	 * "classpath:" pseudo URL, a "file:" URL, or a plain file path
	 * @return a corresponding URL object
	 * @throws FileNotFoundException if the resource cannot be resolved to a URL
	 */
	public static URL getURL(String resourceLocation) throws FileNotFoundException {
		if (resourceLocation == null) {
			throw new IllegalArgumentException("Resource location must not be null");
		}
		if (resourceLocation.startsWith(CLASSPATH_URL_PREFIX)) {
			String path = resourceLocation.substring(CLASSPATH_URL_PREFIX.length());
			URL url = ClassUtils.getDefaultClassLoader().getResource(path);
			if (url == null) {
				String description = "class path resource [" + path + "]";
				throw new FileNotFoundException(
						description + " cannot be resolved to URL because it does not exist");
			}
			return url;
		}
		try {
			// try URL
			return new URL(resourceLocation);
		}
		catch (MalformedURLException ex) {
			// no URL -> treat as file path
			try {
				return new File(resourceLocation).toURI().toURL();
			}
			catch (MalformedURLException ex2) {
				throw new FileNotFoundException("Resource location [" + resourceLocation +
						"] is neither a URL not a well-formed file path");
			}
		}
	}

	/**
	 * Read file last modified time
	 * @param resourceLocation
	 * @return last modified time with long type if file exists 
	 * @return <code>Globals.DEFAULT_VALUE_LONG</code> for others
	 */
	public static long lastModify(String resourceLocation) {
		if (resourceLocation == null || resourceLocation.trim().length() == 0) {
			return Globals.DEFAULT_VALUE_LONG;
		}
		try {
			if (resourceLocation.startsWith(FileUtils.SAMBA_URL_PREFIX)) {
				SmbFile smbFile = new SmbFile(resourceLocation);
				if (smbFile.exists()) {
					return smbFile.getLastModified();
				}
			} else {
				File file = FileUtils.getFile(resourceLocation);
				if (file != null && file.exists()) {
					return file.lastModified();
				}
			}
		} catch (Exception e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Read file last modify error! ", e);
			}
		}
		return Globals.DEFAULT_VALUE_LONG;
	}
	
	/**
	 * Read file last modified time
	 * @param resourceLocation
	 * @return last modified time with <code>java.util.Date</code> type if file exists 
	 * @return null for others
	 */
	public static Date getLastModify(String resourceLocation) {
		long lastModify = FileUtils.lastModify(resourceLocation);
		if (lastModify != Globals.DEFAULT_VALUE_LONG) {
			return new Date(lastModify);
		} else {
			return null;
		}
	}
	
	/**
	 * Open Samba file input stream
	 * @param resourceLocation
	 * @return
	 * @throws IOException
	 */
	public static InputStream loadSMBFile(String resourceLocation) throws IOException {
		if (resourceLocation == null || !resourceLocation.startsWith(SAMBA_URL_PREFIX)) {
			throw new IOException("SMB file path error! ");
		}
		return new SmbFileInputStream(resourceLocation);
	}
	
	/**
	 * Load resource and convert to java.io.InputStream used <code>Globals.DEFAULT_ENCODING</code>
	 * @param resourceLocation
	 * @return <code>java.io.InputStream</code>
	 * @throws Exception
	 */
	public static InputStream loadFile(String resourceLocation) 
			throws FileNotFoundException, IOException {
		return FileUtils.loadFile(resourceLocation, null);
	}
	
	/**
	 * Load resource and convert to java.io.InputStream used encode
	 * @param resourceLocation
	 * @param encode 
	 * @return <code>java.io.InputStream</code>
	 * @throws Exception
	 */
	public static InputStream loadFile(String resourceLocation, String encode) 
			throws FileNotFoundException, IOException {
		if (encode == null || encode.length() == 0) {
			encode = Globals.DEFAULT_ENCODING;
		}

		//	Convert resource location to input stream
		InputStream inputStream = FileUtils.class.getResourceAsStream(resourceLocation);
		
		if (inputStream == null) {
            try {
            	inputStream = new FileInputStream(resourceLocation);
            } catch (Exception e) {
            }
		}

		if (inputStream == null) {
			URL url = FileUtils.getURL(resourceLocation);
			if (url != null) {
				inputStream = url.openStream();
			}
		}
		
		if (inputStream == null) {
			throw new IOException("Load file error!");
		}
		
		return inputStream;
	}
	
	/**
	 * Resolve the given resource location to a <code>java.io.File</code>,
	 * i.e. to a file in the file system.
	 * <p>Does not check whether the fil actually exists; simply returns
	 * the File that the given location would correspond to.
	 * @param resourceLocation the resource location to resolve: either a
	 * "classpath:" pseudo URL, a "file:" URL, or a plain file path
	 * @return a corresponding File object
	 * @throws FileNotFoundException if the resource cannot be resolved to
	 * a file in the file system
	 */
	public static File getFile(String resourceLocation) throws FileNotFoundException {
		if (resourceLocation == null) {
			throw new IllegalArgumentException("Resource location must not be null");
		}
		if (resourceLocation.startsWith(CLASSPATH_URL_PREFIX)) {
			String path = resourceLocation.substring(CLASSPATH_URL_PREFIX.length());
			String description = "class path resource [" + path + "]";
			URL url = ClassUtils.getDefaultClassLoader().getResource(path);
			if (url != null) {
				return getFile(url, description);
			}
		}
		
		try {
			// try URL
			return getFile(new URL(resourceLocation));
		}
		catch (MalformedURLException ex) {
			// no URL -> treat as file path
			return new File(resourceLocation);
		}
	}

	/**
	 * Resolve the given resource URL to a <code>java.io.File</code>,
	 * i.e. to a file in the file system.
	 * @param resourceUrl the resource URL to resolve
	 * @return a corresponding File object
	 * @throws FileNotFoundException if the URL cannot be resolved to
	 * a file in the file system
	 */
	public static File getFile(URL resourceUrl) throws FileNotFoundException {
		return getFile(resourceUrl, "URL");
	}

	/**
	 * Resolve the given resource URL to a <code>java.io.File</code>,
	 * i.e. to a file in the file system.
	 * @param resourceUrl the resource URL to resolve
	 * @param description a description of the original resource that
	 * the URL was created for (for example, a class path location)
	 * @return a corresponding File object
	 * @throws FileNotFoundException if the URL cannot be resolved to
	 * a file in the file system
	 */
	public static File getFile(URL resourceUrl, String description) throws FileNotFoundException {
		if (resourceUrl == null) {
			throw new IllegalArgumentException("Resource URL must not be null");
		}
		if (!URL_PROTOCOL_FILE.equals(resourceUrl.getProtocol())) {
			throw new FileNotFoundException(
					description + " cannot be resolved to absolute file path " +
					"because it does not reside in the file system: " + resourceUrl);
		}
		
		try {
			return new File(toURI(resourceUrl).getSchemeSpecificPart());
		} catch (URISyntaxException ex) {
			// Fallback for URLs that are not valid URIs (should hardly ever happen).
			return new File(resourceUrl.getFile());
		}
	}

	/**
	 * Resolve the given resource URI to a <code>java.io.File</code>,
	 * i.e. to a file in the file system.
	 * @param resourceUri the resource URI to resolve
	 * @return a corresponding File object
	 * @throws FileNotFoundException if the URL cannot be resolved to
	 * a file in the file system
	 */
	public static File getFile(URI resourceUri) throws FileNotFoundException {
		return getFile(resourceUri, "URI");
	}

	/**
	 * Resolve the given resource URI to a <code>java.io.File</code>,
	 * i.e. to a file in the file system.
	 * @param resourceUri the resource URI to resolve
	 * @param description a description of the original resource that
	 * the URI was created for (for example, a class path location)
	 * @return a corresponding File object
	 * @throws FileNotFoundException if the URL cannot be resolved to
	 * a file in the file system
	 */
	public static File getFile(URI resourceUri, String description) throws FileNotFoundException {
		if (resourceUri == null) {
			throw new IllegalArgumentException("Resource URI must not be null");
		}
		if (!URL_PROTOCOL_FILE.equals(resourceUri.getScheme())) {
			throw new FileNotFoundException(
					description + " cannot be resolved to absolute file path " +
					"because it does not reside in the file system: " + resourceUri);
		}
		return new File(resourceUri.getSchemeSpecificPart());
	}
	
	public static List<String> listJarEntry(String filePath) throws IOException {

		List<String> entryList = new ArrayList<String>();
		JarFile jarFile = null;
		try {
			jarFile = new JarFile(getFile(filePath));
			
			Enumeration<JarEntry> enumeration = jarFile.entries();
			
			while (enumeration.hasMoreElements()) {
				JarEntry jarEntry = enumeration.nextElement();
				if (!jarEntry.isDirectory()) {
					entryList.add(jarEntry.getName());
				}
			}
		} catch (Exception e) {
			if (FileUtils.LOGGER.isDebugEnabled()) {
				FileUtils.LOGGER.debug("Load jar entry content error! ", e);
			}
		} finally {
			if (jarFile != null) {
				jarFile.close();
			}
		}
		
		return entryList;
	}
	
	public static String readJarEntryInfo(String filePath, String entryPath) throws IOException {
		String entryContent = null;
		InputStream inputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;
		
		try {
			JarFile jarFile = new JarFile(getFile(filePath));
			
			JarEntry packageEntry = jarFile.getJarEntry(entryPath);
			
			if(packageEntry != null){
				inputStream = jarFile.getInputStream(packageEntry);
				inputStreamReader = new InputStreamReader(inputStream, Globals.DEFAULT_ENCODING);

				char [] readBuffer = new char[Globals.DEFAULT_BUFFER_SIZE];
				int readLength = 0;
				StringBuffer returnValue = new StringBuffer();
				
				while (((readLength = inputStreamReader.read(readBuffer)) > -1)) {
					returnValue.append(readBuffer, 0, readLength);
				}
				
				entryContent = returnValue.toString();
			}
			
			jarFile.close();
		} catch (Exception e) {
			if (FileUtils.LOGGER.isDebugEnabled()) {
				FileUtils.LOGGER.debug("Load jar entry content error! ", e);
			}
		} finally {
			try {
				if (inputStreamReader != null) {
					inputStreamReader.close();
				}
				if (bufferedReader != null) {
					bufferedReader.close();
				}
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (Exception e) {
				if (FileUtils.LOGGER.isDebugEnabled()) {
					FileUtils.LOGGER.debug("Close stream error! ", e);
				}
			}
		}
		
		return entryContent;
	}
	
	/**
	 * Read entry content from jar file
	 * @param filePath			jar file location
	 * @param entryPath			read entry path
	 * @return					entry content or null if not exists
	 * @throws IOException
	 */
	public static byte[] readJarEntryBytes(String filePath, String entryPath) throws IOException {
		byte[] classContent = null;
		InputStream inputStream = null;
		ByteArrayOutputStream byteArrayOutputStream = null;
		
		try {
			if (!FileUtils.validateFileType(filePath)) {
				throw new Exception("Validate file type error!");
			}
			
			JarFile jarFile = new JarFile(getFile(filePath));
			
			String resourcePath = null;
			
			if (entryPath.endsWith(ClassUtils.CLASS_FILE_SUFFIX)) {
				resourcePath = ClassUtils.convertClassNameToResourcePath(entryPath.substring(0, entryPath.length() - 6));
			} else {
				resourcePath = ClassUtils.convertClassNameToResourcePath(entryPath);
			}
			
			JarEntry packageEntry = 
					jarFile.getJarEntry(resourcePath + ClassUtils.CLASS_FILE_SUFFIX);
			
			if(packageEntry != null){
				inputStream = jarFile.getInputStream(packageEntry);
				byteArrayOutputStream = new ByteArrayOutputStream();
				
				byte [] buffer = new byte[8192];
				int readLength = 8192;
				while (((readLength = inputStream.read(buffer)) != -1)) {
					byteArrayOutputStream.write(buffer, 0 , readLength);
				}
				
				classContent = byteArrayOutputStream.toByteArray();
				
				inputStream.close();
				inputStream = null;
			}
			
			jarFile.close();
		} catch (Exception e) {
			if (FileUtils.LOGGER.isDebugEnabled()) {
				FileUtils.LOGGER.debug("Load jar entry content error! ", e);
			}
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
				if (byteArrayOutputStream != null) {
					byteArrayOutputStream.close();
				}
			} catch (Exception e) {
				if (FileUtils.LOGGER.isDebugEnabled()) {
					FileUtils.LOGGER.debug("Close stream error! ", e);
				}
			}
		}
		
		return classContent;
	}

	/**
	 * Read file to byte[] from current input stream use default charset: UTF-8
	 * @param inputStream
	 * @return
	 */
	public static byte[] readFileBytes(InputStream inputStream) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = null;
		
		byte[] content = null;
		try {
			byteArrayOutputStream = new ByteArrayOutputStream(Globals.DEFAULT_BUFFER_SIZE);
			
			byte[] buffer = new byte[Globals.DEFAULT_BUFFER_SIZE];
			int readLength = 0;
			while ((readLength = inputStream.read(buffer)) != -1) {
				byteArrayOutputStream.write(buffer, 0, readLength);
			}
			
			content = byteArrayOutputStream.toByteArray();
		} catch (Exception e) {
			content = new byte[0];
		} finally {
			if (byteArrayOutputStream != null) {
				byteArrayOutputStream.close();
			}
			
		}
		
		return content;
	}
	
	/**
	 * Read resource content
	 * @param file object
	 * @return	
	 * @throws IOException
	 */
	public static byte[] readFileBytes(File file) throws IOException {
		if (file == null || !file.exists()) {
			throw new IOException("File not found");
		}
		
		FileInputStream fileInputStream = null;
		ByteArrayOutputStream byteArrayOutputStream = null;
		
		byte[] content = null;
		try {
			fileInputStream = new FileInputStream(file);
			byteArrayOutputStream = new ByteArrayOutputStream(Globals.DEFAULT_BUFFER_SIZE);
			
			byte[] buffer = new byte[Globals.DEFAULT_BUFFER_SIZE];
			int readLength = 0;
			while ((readLength = fileInputStream.read(buffer)) != -1) {
				byteArrayOutputStream.write(buffer, 0, readLength);
			}
			
			content = byteArrayOutputStream.toByteArray();
		} catch (Exception e) {
			content = new byte[0];
		} finally {
			if (fileInputStream != null) {
				fileInputStream.close();
			}
			
			if (byteArrayOutputStream != null) {
				byteArrayOutputStream.close();
			}
			
		}
		
		return content;
	}
	
	/**
	 * Read resource content
	 * @param resourceLocation
	 * @return	
	 * @throws IOException
	 */
	public static byte[] readFileBytes(String resourceLocation) throws IOException {
		return FileUtils.readFileBytes(FileUtils.getFile(resourceLocation));
	}
	
	/**
	 * Read resource content info in define length
	 * @param resourceLocation
	 * @param offset	start point
	 * @param length	read length
	 * @return
	 */
	public static byte[] readFileBytes(String resourceLocation, long position, int length) {
		RandomAccessFile randomAccessFile = null;
		byte[] readByte = new byte[length];
		
		try {
			randomAccessFile = new RandomAccessFile(resourceLocation, "r");
			randomAccessFile.seek(position);
			randomAccessFile.read(readByte);
		} catch (Exception e) {
			readByte = new byte[0];
		} finally {
			if (randomAccessFile != null) {
				try {
					randomAccessFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return readByte;
	}
	
	public static long getSMBFileSize(String resourceLocation) {
		try {
			SmbFile smbFile = new SmbFile(resourceLocation);
			return smbFile.length();
		} catch (Exception e) {
			return 0L;
		}
	}
	
	/**
	 * Retrieve resource location size
	 * @param resourceLocation
	 * @return
	 */
	public static long getFileSize(String resourceLocation) {
		try {
			return getFileSize(FileUtils.getFile(resourceLocation));
		} catch (FileNotFoundException e) {
			return 0L;
		}
	}

	/**
	 * Retrieve file size
	 * @param file
	 * @return
	 */
	public static long getFileSize(File file) {
		long fileSize = 0L;
		
		if (file != null) {
			if (file.exists()) {
				if (file.isDirectory()) {
					File[] childFiles = file.listFiles();
						if (childFiles != null) {
						for (File childFile : childFiles) {
							fileSize += getFileSize(childFile);
						}
					}
				} else if (file.isFile()) {
					fileSize += file.length();
				}
			}
		}
		
		return fileSize;
	}

	/**
	 * Determine whether the given URL points to a resource in a jar file,
	 * that is, has protocol "jar", "zip", "wsjar" or "code-source".
	 * <p>"zip" and "wsjar" are used by BEA WebLogic Server and IBM WebSphere, respectively,
	 * but can be treated like jar files. The same applies to "code-source" URLs on Oracle
	 * OC4J, provided that the path contains a jar separator.
	 * @param url the URL to check
	 * @return whether the URL has been identified as a JAR URL
	 */
	public static boolean isJarURL(URL url) {
		String protocol = url.getProtocol();
		return (URL_PROTOCOL_JAR.equals(protocol) ||
				URL_PROTOCOL_ZIP.equals(protocol) ||
				URL_PROTOCOL_WSJAR.equals(protocol) ||
				(URL_PROTOCOL_CODE_SOURCE.equals(protocol) && url.getPath().indexOf(JAR_URL_SEPARATOR) != -1));
	}

	/**
	 * Extract the URL for the actual jar file from the given URL
	 * (which may point to a resource in a jar file or to a jar file itself).
	 * @param jarUrl the original URL
	 * @return the URL for the actual jar file
	 * @throws MalformedURLException if no valid jar file URL could be extracted
	 */
	public static URL extractJarFileURL(URL jarUrl) throws MalformedURLException {
		String urlFile = jarUrl.getFile();
		int separatorIndex = urlFile.indexOf(JAR_URL_SEPARATOR);
		if (separatorIndex != -1) {
			String jarFile = urlFile.substring(0, separatorIndex);
			try {
				return new URL(jarFile);
			}
			catch (MalformedURLException ex) {
				// Probably no protocol in original jar URL, like "jar:C:/mypath/myjar.jar".
				// This usually indicates that the jar file resides in the file system.
				if (!jarFile.startsWith("/")) {
					jarFile = "/" + jarFile;
				}
				return new URL(FILE_URL_PREFIX + jarFile);
			}
		} else {
			return jarUrl;
		}
	}

	/**
	 * Create a URI instance for the given URL,
	 * replacing spaces with "%20" quotes first.
	 * <p>Furthermore, this method works on JDK 1.4 as well,
	 * in contrast to the <code>URL.toURI()</code> method.
	 * @param url the URL to convert into a URI instance
	 * @return the URI instance
	 * @throws URISyntaxException if the URL wasn't a valid URI
	 * @see java.net.URL#toURI()
	 */
	public static URI toURI(URL url) throws URISyntaxException {
		return FileUtils.toURI(url.toString());
	}

	/**
	 * Create a URI instance for the given location String,
	 * replacing spaces with "%20" quotes first.
	 * @param location the location String to convert into a URI instance
	 * @return the URI instance
	 * @throws URISyntaxException if the location wasn't a valid URI
	 */
	public static URI toURI(String location) throws URISyntaxException {
		return new URI(StringUtils.replace(location, " ", "%20"));
	}
	
	public static List<String> listJarEntry(URI uri) throws IOException {
		List<String> returnList = new ArrayList<String>();
		
		if (uri != null) {
			String fullPath = uri.getPath();
			String filePath = null;
			String entryPath = null;
			if (fullPath.indexOf(JAR_URL_SEPARATOR) > 0) {
				filePath = fullPath.substring(0, fullPath.indexOf(JAR_URL_SEPARATOR));
				entryPath = fullPath.substring(fullPath.indexOf(JAR_URL_SEPARATOR) + JAR_URL_SEPARATOR.length());
			} else {
				filePath = fullPath;
			}
			
			if (FileUtils.isExists(filePath)) {
				JarFile jarFile = null;
				try {
					File file = FileUtils.getFile(filePath);
					if (file.isDirectory()) {
						returnList = FileUtils.listFiles(file);
					} else if (file.isFile()) {
						jarFile = new JarFile(file);
						Enumeration<JarEntry> enumeration = jarFile.entries();
						
						while (enumeration.hasMoreElements()) {
							JarEntry jarEntry = enumeration.nextElement();
							if (jarEntry.isDirectory()) {
								continue;
							}
							String entryName = jarEntry.getName();
							if (entryPath == null || (entryPath != null && entryName.startsWith(entryPath))) {
								returnList.add(entryName);
							}
						}
					}
				} catch (Exception e) {
					returnList = new ArrayList<String>();
				} finally {
					if (jarFile != null) {
						jarFile.close();
						jarFile = null;
					}
				}
			}
		}
		return returnList;
	}

	/**
	 * List child files
	 * @param filePath		parent file path
	 * @return				list of child file path 
	 * @throws IOException
	 */
	public static List<String> listFiles(String filePath) throws IOException {
		return FileUtils.listFiles(FileUtils.getFile(filePath));
	}

	/**
	 * List child files
	 * @param filePath		parent file path
	 * @return				list of child file path 
	 * @throws IOException
	 */
	public static List<String> listFiles(String filePath, boolean readHiddenFiles) throws IOException {
		return FileUtils.listFiles(FileUtils.getFile(filePath), readHiddenFiles);
	}

	/**
	 * List child files
	 * @param filePath		parent file path
	 * @return				list of child file path 
	 * @throws IOException
	 */
	public static List<String> listFiles(String filePath, 
			boolean readHiddenFiles, boolean includeRootFolder) throws IOException {
		return FileUtils.listFiles(FileUtils.getFile(filePath), readHiddenFiles, includeRootFolder);
	}

	/**
	 * List child files
	 * @param file			parent file object
	 * @return				list of child file path 
	 * @throws IOException
	 */
	public static List<String> listFiles(File file) throws IOException {
		return FileUtils.listFiles(file, null);
	}

	/**
	 * List child files
	 * @param file			parent file object
	 * @return				list of child file path 
	 * @throws IOException
	 */
	public static List<String> listFiles(File file, boolean readHiddenFiles) throws IOException {
		return FileUtils.listFiles(file, null, readHiddenFiles);
	}

	/**
	 * List child files
	 * @param file			parent file object
	 * @return				list of child file path 
	 * @throws IOException
	 */
	public static List<String> listFiles(File file, boolean readHiddenFiles, boolean includeRootFolder) throws IOException {
		return FileUtils.listFiles(file, null, readHiddenFiles, includeRootFolder);
	}
	
	/**
	 * List child files by file name filter
	 * @param filePath		parent file path
	 * @param filter		file name filter
	 * @return				list of child file path 
	 * @throws IOException
	 */
	public static List<String> listFiles(String filePath, FilenameFilter filter) throws IOException {
		return FileUtils.listFiles(FileUtils.getFile(filePath), filter);
	}

	/**
	 * List child files by file name filter
	 * @param filePath		parent file path
	 * @param filter		file name filter
	 * @return				list of child file path 
	 * @throws IOException
	 */
	public static List<String> listFiles(String filePath, FilenameFilter filter, boolean readHiddenFiles) throws IOException {
		return FileUtils.listFiles(FileUtils.getFile(filePath), filter, readHiddenFiles);
	}

	/**
	 * List child files by file name filter
	 * @param file			parent file object
	 * @param filter		file name filter
	 * @return				list of child file path 
	 * @throws IOException
	 */
	public static List<String> listFiles(File file, FilenameFilter filter) throws IOException {
		List<String> returnList = new ArrayList<String>();
		FileUtils.listFiles(file, filter, returnList, true, Globals.DEFAULT_VALUE_BOOLEAN);
		return returnList;
	}

	/**
	 * List child files by file name filter
	 * @param file			parent file object
	 * @param filter		file name filter
	 * @return				list of child file path 
	 * @throws IOException
	 */
	public static List<String> listFiles(File file, FilenameFilter filter, boolean readHiddenFiles) throws IOException {
		List<String> returnList = new ArrayList<String>();
		FileUtils.listFiles(file, filter, returnList, readHiddenFiles, Globals.DEFAULT_VALUE_BOOLEAN);
		return returnList;
	}

	/**
	 * List child files by file name filter
	 * @param file			parent file object
	 * @param filter		file name filter
	 * @return				list of child file path 
	 * @throws IOException
	 */
	public static List<String> listFiles(File file, FilenameFilter filter, boolean readHiddenFiles, boolean includeRootFolder) throws IOException {
		List<String> returnList = new ArrayList<String>();
		FileUtils.listFiles(file, filter, returnList, readHiddenFiles, includeRootFolder);
		return returnList;
	}
	
	/**
	 * List child files and append file path to current list
	 * @param filePath		parent file path
	 * @param fileList		current child file list
	 * @throws IOException
	 */
	public static void listFiles(String filePath, List<String> fileList) throws IOException {
		FileUtils.listFiles(FileUtils.getFile(filePath), null, fileList, true, Globals.DEFAULT_VALUE_BOOLEAN);
	}

	/**
	 * List child files and append file path to current list
	 * @param filePath		parent file path
	 * @param fileList		current child file list
	 * @throws IOException
	 */
	public static void listFiles(String filePath, List<String> fileList, boolean readHiddenFiles) throws IOException {
		FileUtils.listFiles(FileUtils.getFile(filePath), null, fileList, readHiddenFiles, Globals.DEFAULT_VALUE_BOOLEAN);
	}

	/**
	 * List child files and append file path to current list
	 * @param filePath		parent file path
	 * @param fileList		current child file list
	 * @throws IOException
	 */
	public static void listFiles(String filePath, List<String> fileList, 
			boolean readHiddenFiles, boolean includeRootFolder) throws IOException {
		FileUtils.listFiles(FileUtils.getFile(filePath), null, fileList, readHiddenFiles, includeRootFolder);
	}
	
	/**
	 * List child files by file name filter and append file path to current list
	 * @param filePath		parent file path
	 * @param filter		file name filter
	 * @param fileList		current child file list
	 * @throws IOException
	 */
	public static void listFiles(String filePath, FilenameFilter filter, List<String> fileList) throws IOException {
		FileUtils.listFiles(FileUtils.getFile(filePath), filter, fileList, true, Globals.DEFAULT_VALUE_BOOLEAN);
	}

	/**
	 * List child files by file name filter and append file path to current list
	 * @param filePath				parent file path
	 * @param filter				file name filter
	 * @param fileList				current child file list
	 * @param includeRootFolder		include root folder
	 * @throws IOException
	 */
	public static void listFiles(String filePath, FilenameFilter filter, 
			List<String> fileList, boolean includeRootFolder) throws IOException {
		FileUtils.listFiles(FileUtils.getFile(filePath), filter, fileList, true, includeRootFolder);
	}

	/**
	 * List child files by file name filter and append file path to current list
	 * @param file					parent file object
	 * @param filter				file name filter
	 * @param fileList				current child file list
	 * @param readHiddenFiles		include hidden file
	 * @param includeRootFolder		include root folder
	 * @throws IOException
	 */
	public static void listFiles(File file, FilenameFilter filter, List<String> fileList, 
			boolean readHiddenFiles, boolean includeRootFolder) throws IOException {
		if (fileList == null) {
			fileList = new ArrayList<String>();
		}
		
		if (file.isDirectory()) {
			if (includeRootFolder) {
				fileList.add(file.getAbsolutePath());
			}
			File[] childFiles = file.listFiles();
			if (childFiles != null) {
				for (File childDir : childFiles) {
					FileUtils.listFiles(childDir, filter, fileList, readHiddenFiles, includeRootFolder);
				}
			}
		} else {
			if (!readHiddenFiles && file.isHidden()) {
				return;
			}
			
			boolean match = Globals.DEFAULT_VALUE_BOOLEAN;
			if (filter == null) {
				match = true;
			} else if (filter != null && filter.accept(file.getParentFile(), file.getName())) {
				match = true;
			}
			
			if (match) {
				String filePath = file.getAbsolutePath();
				if (!fileList.contains(filePath)) {
					fileList.add(filePath);
				}
			}
		}
	}
	
	/**
	 * List child directory
	 * @param filePath		parent path
	 * @return				list of child directory path
	 * @throws IOException
	 */
	public static List<String> listDirectory(String filePath) throws IOException {
		List<String> directoryList = new ArrayList<String>();
		FileUtils.listDirectory(FileUtils.getFile(filePath), directoryList);
		return directoryList;
	}

	/**
	 * List child directory
	 * @param directory		parent directory object
	 * @return				list of child directory path
	 * @throws IOException
	 */
	public static List<String> listDirectory(File directory) throws IOException {
		List<String> directoryList = new ArrayList<String>();
		FileUtils.listDirectory(directory, directoryList);
		return directoryList;
	}
	
	/**
	 * List child directory and append to current directory list
	 * @param file				parent directory object
	 * @param directoryList		current directory list
	 * @throws IOException
	 */
	public static void listDirectory(File file, List<String> directoryList) throws IOException {
		if (file == null || !file.isDirectory() || directoryList == null) {
			return;
		}
		
		FileFilter fileFilter = new DirectoryFileFilter();
		
		File[] directories = file.listFiles(fileFilter);
		
		if (directories != null) {
			for (File directory : directories) {
				directoryList.add(directory.getAbsolutePath());
				FileUtils.listDirectory(directory, directoryList);
			}
		}
	}

	/**
	 * List child files and filter by extension name
	 * @param filePath			parent file path
	 * @param fileExtName		extension name
	 * @return					list of file path
	 * @throws IOException
	 */
	public static List<String> listExtNameFiles(String filePath, String fileExtName) throws IOException {
		return FileUtils.listFiles(FileUtils.getFile(filePath), new FilenameExtensionFilter(fileExtName));
	}

	/**
	 * List child files by filter extension name is .class
	 * @param filePath			parent file path
	 * @return					list of file path
	 * @throws IOException
	 */
	public static List<String> listClassesFiles(String filePath) throws IOException {
		return FileUtils.listFiles(FileUtils.getFile(filePath), new FilenameExtensionFilter("class"));
	}

	/**
	 * List child files by filter extension name is .class
	 * @param file				parent file object
	 * @return					list of file path
	 * @throws IOException
	 */
	public static List<String> listClassesFiles(File file) throws IOException {
		return FileUtils.listFiles(file, new FilenameExtensionFilter("class"));
	}

	/**
	 * List child files by filter extension name is .jar
	 * @param filePath			parent file path
	 * @return					list of file path
	 * @throws IOException
	 */
	public static List<String> listJarFiles(String filePath) throws IOException {
		return FileUtils.listFiles(FileUtils.getFile(filePath), new FilenameExtensionFilter(URL_PROTOCOL_JAR));
	}

	/**
	 * List child files by filter extension name is .jar
	 * @param file				parent file object
	 * @return					list of file path
	 * @throws IOException
	 */
	public static List<String> listJarFiles(File file) throws IOException {
		return FileUtils.listFiles(file, new FilenameExtensionFilter(URL_PROTOCOL_JAR));
	}

	/**
	 * List child files by filter extension name is .zip
	 * @param filePath			parent file path
	 * @return					list of file path
	 * @throws IOException
	 */
	public static List<String> listZipFiles(String filePath) throws IOException {
		return FileUtils.listFiles(FileUtils.getFile(filePath), new FilenameExtensionFilter(URL_PROTOCOL_ZIP));
	}

	/**
	 * List child files by filter extension name is .zip
	 * @param file				parent file object
	 * @return					list of file path
	 * @throws IOException
	 */
	public static List<String> listZipFiles(File file) throws IOException {
		return FileUtils.listFiles(file, new FilenameExtensionFilter(URL_PROTOCOL_ZIP));
	}

	/**
	 * List child files by filter extension name is .wsjar
	 * @param filePath			parent file path
	 * @return					list of file path
	 * @throws IOException
	 */
	public static List<String> listWebSphereJarFiles(String filePath) throws IOException {
		return FileUtils.listFiles(FileUtils.getFile(filePath), new FilenameExtensionFilter(URL_PROTOCOL_WSJAR));
	}

	/**
	 * List child files by filter extension name is .wsjar
	 * @param file				parent file object
	 * @return					list of file path
	 * @throws IOException
	 */
	public static List<String> listWebSphereJarFiles(File file) throws IOException {
		return FileUtils.listFiles(file, new FilenameExtensionFilter(URL_PROTOCOL_WSJAR));
	}
	
	/**
	 * Write file content to local file path
	 * @param fileData			file content
	 * @param filePath			write path
	 * @return					true for success and Globals.DEFAULT_VALUE_BOOLEAN for error 
	 * @throws IOException		close output stream error
	 */
	public static boolean saveFile(byte[] fileData, String filePath) throws IOException {
		FileOutputStream fileOutputStream = null;
		
		try {
			File destFile = FileUtils.getFile(filePath);
			FileUtils.makeHome(destFile.getParent());
			
			fileOutputStream = new FileOutputStream(destFile);
			fileOutputStream.write(fileData);
			fileOutputStream.flush();
		} catch (FileNotFoundException e) {
			return Globals.DEFAULT_VALUE_BOOLEAN;
		} finally {
			if (fileOutputStream != null) {
				fileOutputStream.close();
			}
		}
		
		return true;
	}
	
	/**
	 * Write input stream content to file path
	 * @param inputStream		file content by input stream
	 * @param filePath			write to file path
	 * @return					true for success and Globals.DEFAULT_VALUE_BOOLEAN for error 
	 * @throws IOException		close stream error
	 */
	public static boolean saveFile(InputStream inputStream, String filePath) throws IOException {
		OutputStream outputStream = null;
		try {
			File file = FileUtils.getFile(filePath);
			FileUtils.makeHome(file.getParent());
			
			outputStream = new FileOutputStream(file);
		    int bytesRead = 0;
		    byte[] buffer = new byte[8192];
		    while ((bytesRead = inputStream.read(buffer, 0, 8192)) != -1) {
		    	outputStream.write(buffer, 0, bytesRead);
		    }
		    return true;
		} catch(Exception e) {
			return Globals.DEFAULT_VALUE_BOOLEAN;
		} finally {
			if (outputStream != null) {
				outputStream.flush();
				outputStream.close();
			}
			
			if (inputStream != null) {
				inputStream.close();
			}
		}
	}
	
	/**
	 * Save String to File use default charset: UTF-8
	 * @param fileName
	 * @param filePath
	 * @param content
	 * @return
	 */
	public static boolean saveFile(String fileName, String filePath, String content) {
		return FileUtils.saveFile(fileName, filePath, content, Globals.DEFAULT_ENCODING);
	}
	
	/**
	 * Save String to File
	 * @param fileName
	 * @param filePath
	 * @param content		File Content
	 * @param encoding
	 * @return
	 */
	public static boolean saveFile(String fileName, String filePath, String content,String encoding) {
		PrintWriter printWriter = null;
		OutputStreamWriter outputStreamWriter = null;
		try {
			FileUtils.makeHome(filePath);
			outputStreamWriter = new OutputStreamWriter(
					new FileOutputStream(filePath + Globals.DEFAULT_PAGE_SEPARATOR + fileName), encoding);
			printWriter = new PrintWriter(outputStreamWriter);
			
			printWriter.print(content);
			return true;
		} catch (Exception e) {
			return Globals.DEFAULT_VALUE_BOOLEAN;
		} finally {
			if (printWriter != null) {
				printWriter.close();
			}
			
			if (outputStreamWriter != null) {
				try {
					outputStreamWriter.flush();
					outputStreamWriter.close();
				} catch (IOException e) {
					
				}
			}
		}
	}
	
	/**
	 * Read File to String use default charset: UTF-8
	 * @param filePath
	 * @return
	 */
	public static String readFile(String filePath) {
		return FileUtils.readFile(filePath, Globals.DEFAULT_ENCODING);
	}

	/**
	 * Read File to String
	 * @param filePath
	 * @param encoding
	 * @return
	 */
	public static String readFile(String filePath, String encoding) {
		try {
			return FileUtils.readFile(getURL(filePath).openStream(), encoding);
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Read file to String from current input stream use default charset: UTF-8
	 * @param inputStream
	 * @return
	 */
	public static String readFile(InputStream inputStream) {
		return FileUtils.readFile(inputStream, null);
	}
	
	/**
	 * Read file to String from current input stream use current encoding
	 * @param inputStream
	 * @param encoding
	 * @return
	 */
	public static String readFile(InputStream inputStream, String encoding) {
		if (encoding == null) {
			encoding = Globals.DEFAULT_ENCODING;
		}
		
		char [] readBuffer = new char[Globals.DEFAULT_BUFFER_SIZE];
		int len = 0;
		StringBuffer returnValue = new StringBuffer();
		
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;
		try {
			inputStreamReader = new InputStreamReader(inputStream, encoding);
			bufferedReader = new BufferedReader(inputStreamReader);
			
			while ((len = bufferedReader.read(readBuffer)) > -1) {
				returnValue.append(readBuffer, 0, len);
			}
		} catch (Exception e) {
			return returnValue.toString();
		} finally {
			try {
				if (inputStreamReader != null) {
					inputStreamReader.close();
				}
				if (bufferedReader != null) {
					bufferedReader.close();
				}
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException ex) {
				
			}
		}
		return returnValue.toString();
	}
	
	/**
	 * Remove File by current file path
	 * @param filePath
	 * @return
	 * @throws FileNotFoundException 
	 */
	public static boolean removeFile(String filePath) {
		try {
			if (filePath.startsWith(FileUtils.SAMBA_URL_PREFIX)) {
				try {
					return FileUtils.removeSmbFile(new SmbFile(filePath));
				} catch (Exception e) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Remove samba file error");
					}
					return Globals.DEFAULT_VALUE_BOOLEAN;
				}
			} else {
				return FileUtils.removeFile(FileUtils.getFile(filePath));
			}
		} catch (FileNotFoundException e) {
			return true;
		}
	}
	
	/**
	 * Remove File by current file object
	 * @param file
	 * @return
	 */
	public static boolean removeFile(File file) {
		if (file == null) {
			return true;
		}
		
		if (file.exists()) {
			if (file.isDirectory()) {
				return FileUtils.removeDir(file);
			} else {
				return file.delete();
			}
		}
		return true;
	}

	/**
	 * Remove File by current file object
	 * @param file
	 * @return
	 */
	public static boolean removeSmbFile(SmbFile smbFile) {
		if (smbFile == null) {
			return true;
		}
		
		try {
			if (smbFile.exists()) {
				if (smbFile.isDirectory()) {
					FileUtils.removeSmbDir(smbFile);
				} else {
					smbFile.delete();
				}
			}
			return true;
		} catch (Exception e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Remove smb file error! ", e);
			}
			return Globals.DEFAULT_VALUE_BOOLEAN;
		}
	}
	
	public static boolean renameSmbFile(String origFile, String destFile) {
		if (origFile.startsWith(FileUtils.SAMBA_URL_PREFIX) 
				&& destFile.startsWith(FileUtils.SAMBA_URL_PREFIX)) {
			try {
				SmbFile origSmbFile = new SmbFile(origFile);
				SmbFile destSmbFile = new SmbFile(origFile);
				
				origSmbFile.renameTo(destSmbFile);
				return true;
			} catch (Exception e) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Rename smb file error! ", e);
				}
			}
		}
		return Globals.DEFAULT_VALUE_BOOLEAN;
	}
	
	/**
	 * Move file from basePath to moveToPath
	 * @param fileName
	 * @param basePath
	 * @param moveToPath
	 * @return
	 */
	public static boolean moveFile(String basePath, String moveToPath) {
		return FileUtils.moveFile(basePath, moveToPath, Globals.DEFAULT_VALUE_BOOLEAN);
	}
	
	/**
	 * Move file from basePath to moveToPath
	 * @param fileName
	 * @param basePath
	 * @param moveToPath
	 * @return
	 */
	public static boolean moveFile(String basePath, String moveToPath, boolean override) {
		if (FileUtils.isExists(basePath) && FileUtils.canRead(basePath)) {
			if (override || !FileUtils.isExists(moveToPath)) {
				try {
					File destFile = FileUtils.getFile(moveToPath);
					if (destFile != null && destFile.exists()) {
						if (override && !FileUtils.removeFile(destFile)) {
							return Globals.DEFAULT_VALUE_BOOLEAN;
						}
					}
					
					if (FileUtils.copyFile(basePath, moveToPath) && FileUtils.removeFile(basePath)) {
						return true;
					}
					
					return Globals.DEFAULT_VALUE_BOOLEAN;
				} catch (Exception e) {
				}
			}
		}
		return Globals.DEFAULT_VALUE_BOOLEAN;
	}
	
	/**
	 * Move directory from basePath to moveToPath and ignore exists file
	 * @param basePath
	 * @param moveToPath
	 * @return
	 */
	public static boolean moveDir(String basePath, String moveToPath) {
		return FileUtils.moveDir(basePath, moveToPath, Globals.DEFAULT_VALUE_BOOLEAN);
	}
	
	/**
	 * Move directory from basePath to moveToPath and override by user defined
	 * @param basePath
	 * @param moveToPath
	 * @param override
	 * @return
	 */
	public static boolean moveDir(String basePath, String moveToPath, boolean override) {
		try {
			return FileUtils.moveDir(FileUtils.getFile(basePath), moveToPath, override);
		} catch (FileNotFoundException e) {
			return Globals.DEFAULT_VALUE_BOOLEAN;
		}
	}
	
	/**
	 * Move directory from baseFile object to moveToPath and ignore exists file
	 * @param baseFile
	 * @param moveToPath
	 * @return
	 */
	public static boolean moveDir(File baseFile, String moveToPath) {
		return FileUtils.moveDir(baseFile, moveToPath, Globals.DEFAULT_VALUE_BOOLEAN);
	}
	
	/**
	 * Move dir from baseFile object to moveToPath and override by user defined
	 * @param basePath
	 * @param moveToPath
	 * @return
	 */
	public static boolean moveDir(File baseFile, String moveToPath, boolean override) {
		if (baseFile == null || !baseFile.exists()) {
			return Globals.DEFAULT_VALUE_BOOLEAN;
		}
		try {
			FileUtils.makeHome(moveToPath);
			
			boolean error = Globals.DEFAULT_VALUE_BOOLEAN;
			if (baseFile.isDirectory()) {
				File[] childFiles = baseFile.listFiles();
				if (childFiles != null) {
					for (File tempFile : childFiles) {
						String childPath = moveToPath + Globals.DEFAULT_PAGE_SEPARATOR + tempFile.getName();
						if (tempFile.isDirectory()) {
							error = FileUtils.moveDir(tempFile, childPath, override);
							removeFile(tempFile);
						} else if (tempFile.isFile()) {
							error = FileUtils.moveFile(tempFile.getAbsolutePath(), childPath, override);
						}
						
						if (!error) {
							return Globals.DEFAULT_VALUE_BOOLEAN;
						}
					}
				}
				return true;
			} else if (baseFile.isFile()) {
				return FileUtils.moveFile(baseFile.getAbsolutePath(), 
						moveToPath + Globals.DEFAULT_PAGE_SEPARATOR + baseFile.getName(), override);
			} else {
				return Globals.DEFAULT_VALUE_BOOLEAN;
			}
		} catch (Exception e) {
			return Globals.DEFAULT_VALUE_BOOLEAN;
		}
	}
	
	/**
	 * Copy file from basePath to copyToPath
	 * @param fileName
	 * @param basePath
	 * @param copyToPath
	 * @return
	 */
	public static boolean copyFile(String basePath, String copyToPath) {
		InputStream inputStream = null;
		OutputStream outputStream = null;
		try {
			String folderPath = null;
			if (copyToPath.startsWith(FileUtils.SAMBA_URL_PREFIX)) {
				folderPath = copyToPath.substring(0, copyToPath.lastIndexOf("/"));
			} else {
				folderPath = copyToPath.substring(0, copyToPath.lastIndexOf(Globals.DEFAULT_PAGE_SEPARATOR));
			}
			if (FileUtils.makeHome(folderPath)) {
				if (basePath.startsWith(FileUtils.SAMBA_URL_PREFIX)) {
					inputStream = new SmbFileInputStream(basePath);
				} else {
					inputStream = new FileInputStream(basePath);
				}
				
				if (copyToPath.startsWith(FileUtils.SAMBA_URL_PREFIX)) {
					outputStream = new SmbFileOutputStream(copyToPath);
				} else {
					outputStream = new FileOutputStream(copyToPath);
				}
				
				int len = 0;
				byte [] buffer = new byte[Globals.DEFAULT_BUFFER_SIZE];
				
				while ((len = inputStream.read(buffer)) > -1) {
					outputStream.write(buffer, 0, len);
				}
				return true;
			}
		} catch (Exception e) {
			if (FileUtils.LOGGER.isDebugEnabled()) {
				FileUtils.LOGGER.debug("Copy file error! ", e);
			}
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
	
				if (outputStream != null) {
					outputStream.flush();
					outputStream.close();
				}
			} catch (IOException e) {
				if (FileUtils.LOGGER.isDebugEnabled()) {
					FileUtils.LOGGER.debug("Close stream error! ", e);
				}
			}
		}
		return Globals.DEFAULT_VALUE_BOOLEAN;
	}
	
	public static boolean makeDir(String destPath) {
		if (FileUtils.isExists(destPath)) {
			return true;
		}
		
		if (FileUtils.makeHome(destPath)) {
			try {
				File destFile = FileUtils.getFile(destPath);
				return destFile.mkdirs();
			} catch (Exception e) {
			}
		}
		return Globals.DEFAULT_VALUE_BOOLEAN;
	}
	
	/**
	 * Create file directory
	 * @param homePath
	 * @return
	 */
	public static boolean makeHome(String homePath) {
		if (homePath.startsWith(FileUtils.SAMBA_URL_PREFIX)) {
			try {
				SmbFile smbFile = new SmbFile(homePath);
				smbFile.mkdirs();
				return true;
			} catch (Exception e) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Make smb file directories error! ", e);
				}
				return Globals.DEFAULT_VALUE_BOOLEAN;
			}
		} else {
			try {
				File homeDir = FileUtils.getFile(homePath);
				String parentPath = homeDir.getParent();
				if (parentPath != null 
						&& !FileUtils.isExists(parentPath)) {
					if (!FileUtils.makeHome(parentPath)) {
						return Globals.DEFAULT_VALUE_BOOLEAN;
					}
				}
				
				if (homeDir.exists()) {
					return true;
				} else {
					try {
						return homeDir.mkdirs();
					} catch (Exception e) {
						return Globals.DEFAULT_VALUE_BOOLEAN;
					}
				}
			} catch (FileNotFoundException e) {
				return Globals.DEFAULT_VALUE_BOOLEAN;
			}
		}
	}
	
	/**
	 * Check filePath is exists
	 * @param path
	 * @return
	 */
	public static boolean isDirectory(String resourceLocation) {
		if (resourceLocation == null) {
			return Globals.DEFAULT_VALUE_BOOLEAN;
		}

		try {
			File directory = FileUtils.getFile(resourceLocation);
			if (directory == null) {
				return Globals.DEFAULT_VALUE_BOOLEAN;
			}
			return (directory.exists() && directory.isDirectory());
		} catch (FileNotFoundException e) {
			return Globals.DEFAULT_VALUE_BOOLEAN;
		}
	}
	
	/**
	 * Copy directory from baseDir to destDir
	 * @param baseDir
	 * @param destDir
	 * @return
	 */
	public static boolean copyDir(String baseDir, String destDir) {
		if (!FileUtils.isDirectory(baseDir)) {
			return Globals.DEFAULT_VALUE_BOOLEAN;
		}
		
		File baseFiles = new File(baseDir);
		File destFiles = new File(destDir);
		
		String [] fileList = baseFiles.list();
		
		if (!FileUtils.makeHome(destDir)) {
			return Globals.DEFAULT_VALUE_BOOLEAN;
		}
		
		boolean copyStatus = true;
		
		for (int i = 0 ; i < fileList.length ; i++) {
			String baseFile = baseFiles + Globals.DEFAULT_PAGE_SEPARATOR + fileList[i];
			String destFile = destFiles + Globals.DEFAULT_PAGE_SEPARATOR + fileList[i];
			
			File tempFile = new File(baseFile);
			if (tempFile.isFile()) {
				copyStatus = FileUtils.copyFile(baseDir + Globals.DEFAULT_PAGE_SEPARATOR + tempFile.getName(), 
						destDir + Globals.DEFAULT_PAGE_SEPARATOR + tempFile.getName());
			} else if (tempFile.isDirectory()) {
				copyStatus = FileUtils.copyDir(baseFile, destFile);
			}
			if (!copyStatus) {
				return copyStatus;
			}
		}
		return true;
	}
	
	/**
	 * Delete directory
	 * @param directory
	 * @return
	 */
	public static boolean removeDir(File directory) {
		if (!directory.exists()) {
			return true;
		}
		String [] fileList = directory.list();
		
		if (fileList != null) {
			boolean operatStatus = true;
			for (int i = 0 ; i < fileList.length ; i++) {
				File tempFile = new File(directory.getAbsolutePath(), fileList[i]);
				if (tempFile.isDirectory()) {
					operatStatus = FileUtils.removeDir(tempFile);
				} else {
					operatStatus = tempFile.delete();
				}
				
				if (!operatStatus) {
					return operatStatus;
				}
			}
		}
		
		return directory.delete();
	}

	/**
	 * Delete directory
	 * @param directory
	 * @return
	 * @throws SmbException 
	 * @throws MalformedURLException 
	 */
	public static boolean removeSmbDir(SmbFile directory) {
		try {
			if (!directory.exists()) {
				return true;
			}
			String [] fileList = directory.list();
			
			if (fileList != null) {
				for (int i = 0 ; i < fileList.length ; i++) {
					SmbFile childFile = new SmbFile(fileList[i]);
					if (childFile.isDirectory()) {
						if (!FileUtils.removeSmbDir(childFile)) {
							return Globals.DEFAULT_VALUE_BOOLEAN;
						}
					} else {
						childFile.delete();
					}
				}
			}
			
			directory.delete();
			return true;
		} catch (Exception e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Remove smb folder error! ", e);
			}
			return Globals.DEFAULT_VALUE_BOOLEAN;
		}
	}

	public static long calcFileCRC(String filePath) {
		InputStream inputStream = null;
		try {
			inputStream = FileUtils.loadFile(filePath);
			if (inputStream != null) {
				byte[] readBuffer = new byte[Globals.DEFAULT_BUFFER_SIZE];
				int readLength = Globals.DEFAULT_VALUE_INT;
				CRC32 crc = new CRC32();
				
				while ((readLength = inputStream.read(readBuffer)) != Globals.DEFAULT_VALUE_INT) {
					crc.update(readBuffer, 0, readLength);
				}
				
				return crc.getValue();
			}
		} catch (Exception e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Calculate file crc error! ", e);
			}
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e2) {
				}
			}
		}
		
		return Globals.DEFAULT_VALUE_LONG;
	}
	
	/**
	 * Check current file type is compress file
	 * @param pathName
	 * @return boolean
	 */
	public static boolean isCompressFile(String resourceLocation) {
		if (!FileUtils.validateFileType(resourceLocation)) {
			return Globals.DEFAULT_VALUE_BOOLEAN;
		}
		String extensionName = StringUtils.getFilenameExtension(resourceLocation);
		
		if (extensionName != null) {
			FileExtensionInfo fileExtensionInfo = FileUtils.REGISTER_IDEN_MAP.get(extensionName);
			if (fileExtensionInfo != null) {
				return fileExtensionInfo.isCompressFile();
			}
		}
		
		return Globals.DEFAULT_VALUE_BOOLEAN;
	}
	
	/**
	 * Check current file type is ready for printing
	 * @param resourceLocation
	 * @return
	 */
	public static boolean isPrintable(String resourceLocation) {
		if (!FileUtils.validateFileType(resourceLocation)) {
			return Globals.DEFAULT_VALUE_BOOLEAN;
		}
		String extensionName = StringUtils.getFilenameExtension(resourceLocation);
		
		if (extensionName != null) {
			FileExtensionInfo fileExtensionInfo = FileUtils.REGISTER_IDEN_MAP.get(extensionName);
			if (fileExtensionInfo != null) {
				return fileExtensionInfo.isPrintable();
			}
		}

		return Globals.DEFAULT_VALUE_BOOLEAN;
	}
	
	public static boolean isPicture(String resourceLocation) {
		if (!FileUtils.validateFileType(resourceLocation)) {
			return Globals.DEFAULT_VALUE_BOOLEAN;
		}
		String extensionName = StringUtils.getFilenameExtension(resourceLocation);
		
		if (extensionName != null) {
			FileExtensionInfo fileExtensionInfo = FileUtils.REGISTER_IDEN_MAP.get(extensionName);
			if (fileExtensionInfo != null) {
				return fileExtensionInfo.isPicture();
			}
		}

		return Globals.DEFAULT_VALUE_BOOLEAN;
	}
	
	/**
	 * Check current file is exists
	 * @param filePath
	 * @return
	 */
	public static boolean isExists(String filePath) {
		if (filePath == null) {
			return Globals.DEFAULT_VALUE_BOOLEAN;
		}
		
		if (filePath.startsWith(SAMBA_URL_PREFIX)) {
			return FileUtils.isSMBFileExists(filePath);
		}
		
		try {
			File file = FileUtils.getFile(filePath);
			if (file == null) {
				return Globals.DEFAULT_VALUE_BOOLEAN;
			}
			return file.exists();
		} catch (FileNotFoundException e) {
			return Globals.DEFAULT_VALUE_BOOLEAN;
		}
	}
	
	public static boolean isEntryExists(String filePath, String entryPath) {
		if (filePath.endsWith(URL_PROTOCOL_JAR)) {
			JarFile jarFile = null;
			try {
				jarFile = new JarFile(getFile(filePath));
				
				JarEntry packageEntry = jarFile.getJarEntry(entryPath);
				
				if(packageEntry != null){
					return true;
				}
			} catch (Exception e) {
				if (FileUtils.LOGGER.isDebugEnabled()) {
					FileUtils.LOGGER.debug("Load jar entry content error! ", e);
				}
			} finally {
				if (jarFile != null) {
					try {
						jarFile.close();
					} catch (Exception e) {
						if (FileUtils.LOGGER.isDebugEnabled()) {
							FileUtils.LOGGER.debug("Close jar file error! ", e);
						}
					}
				}
			}
		} else if (filePath.endsWith(URL_PROTOCOL_ZIP)) {
			ZipFile zipFile = new ZipFile(filePath);
			return zipFile.isEntryExists(entryPath);
		}
		return Globals.DEFAULT_VALUE_BOOLEAN;
	}
	
	/**
	 * Check current file can read
	 * @param filePath
	 * @return
	 */
	public static boolean canRead(String filePath) {
		if (filePath == null) {
			return Globals.DEFAULT_VALUE_BOOLEAN;
		}
		
		if (filePath.startsWith(SAMBA_URL_PREFIX)) {
			return FileUtils.isSMBFileCanRead(filePath);
		}
		
		try {
			File file = FileUtils.getFile(filePath);
			if (file == null) {
				return Globals.DEFAULT_VALUE_BOOLEAN;
			}
			return file.canRead();
		} catch (FileNotFoundException e) {
			return Globals.DEFAULT_VALUE_BOOLEAN;
		}
	}
	
	/**
	 * Check current file can write
	 * @param filePath
	 * @return
	 */
	public static boolean canWrite(String filePath) {
		if (filePath == null) {
			return Globals.DEFAULT_VALUE_BOOLEAN;
		}
		
		if (filePath.startsWith(SAMBA_URL_PREFIX)) {
			return FileUtils.isSMBFileCanWrite(filePath);
		}
		
		try {
			File file = FileUtils.getFile(filePath);
			if (file == null) {
				return Globals.DEFAULT_VALUE_BOOLEAN;
			}
			return file.canWrite();
		} catch (FileNotFoundException e) {
			return Globals.DEFAULT_VALUE_BOOLEAN;
		}
	}
	
	/**
	 * Check current file can execute
	 * @param filePath
	 * @return
	 */
	public static boolean canExecute(String filePath) {
		try {
			File file = FileUtils.getFile(filePath);
			if (file == null) {
				return Globals.DEFAULT_VALUE_BOOLEAN;
			}
			return file.canExecute();
		} catch (FileNotFoundException e) {
			return Globals.DEFAULT_VALUE_BOOLEAN;
		}
	}
	
	/**
	 * Merge file to save path
	 * @param savePath
	 * @param segmentationFileInfos
	 * @return
	 */
	public static boolean mergeFileInfo(String savePath, List<SegmentationFileInfo> segmentationFileInfos) {
		RandomAccessFile randomAccessFile = null;
		
		try {
			long totalSize = 0;
			long defineSize = 0;
			randomAccessFile = new RandomAccessFile(savePath, "rw");
			
			for (SegmentationFileInfo segmentationFileInfo : segmentationFileInfos) {
				if (segmentationFileInfo == null) {
					return Globals.DEFAULT_VALUE_BOOLEAN;
				}

				if (defineSize == 0) {
					randomAccessFile.setLength(segmentationFileInfo.getTotalSize());
					defineSize = segmentationFileInfo.getTotalSize();
				}
				
				if (FileUtils.mergeFile(randomAccessFile, segmentationFileInfo)) {
					totalSize += segmentationFileInfo.getDataContent().length;
				}
			}

			randomAccessFile.close();
			randomAccessFile = null;
			
			if (FileUtils.LOGGER.isDebugEnabled()) {
				FileUtils.LOGGER.debug("Write file size: " + totalSize);
			}
			
			if (totalSize != defineSize) {
				FileUtils.removeFile(savePath);
				return Globals.DEFAULT_VALUE_BOOLEAN;
			}
			
			return true;
		} catch (Exception e) {
			return Globals.DEFAULT_VALUE_BOOLEAN;
		} finally {
			if (randomAccessFile != null) {
				try {
					randomAccessFile.close();
				} catch (IOException e) {
					if (FileUtils.LOGGER.isDebugEnabled()) {
						FileUtils.LOGGER.debug("Close random access file error! ", e);
					}
				}
			}
		}
	}
	
	/**
	 * Merge file to save path
	 * @param savePath
	 * @param segmentationFilePaths
	 * @return
	 */
	public static boolean mergeFile(String savePath, List<String> segmentationFilePaths) {
		RandomAccessFile randomAccessFile = null;
		
		try {
			long totalSize = 0;
			long defineSize = 0;
			randomAccessFile = new RandomAccessFile(savePath, "rw");
			
			for (String segmentationFilePath : segmentationFilePaths) {
				SegmentationFileInfo segmentationFileInfo = 
						XmlUtils.convertToObject(segmentationFilePath, SegmentationFileInfo.class);
				if (segmentationFileInfo == null) {
					return Globals.DEFAULT_VALUE_BOOLEAN;
				}

				if (defineSize == 0) {
					randomAccessFile.setLength(segmentationFileInfo.getTotalSize());
					defineSize = segmentationFileInfo.getTotalSize();
				}
				
				if (FileUtils.mergeFile(randomAccessFile, segmentationFileInfo)) {
					totalSize += segmentationFileInfo.getDataContent().length;
				}
			}

			randomAccessFile.close();
			randomAccessFile = null;
			
			if (FileUtils.LOGGER.isDebugEnabled()) {
				FileUtils.LOGGER.debug("Write file size: " + totalSize);
			}
			
			if (totalSize != defineSize) {
				FileUtils.removeFile(savePath);
				return Globals.DEFAULT_VALUE_BOOLEAN;
			}
			
			return true;
		} catch (Exception e) {
			return Globals.DEFAULT_VALUE_BOOLEAN;
		} finally {
			if (randomAccessFile != null) {
				try {
					randomAccessFile.close();
				} catch (IOException e) {
					if (FileUtils.LOGGER.isDebugEnabled()) {
						FileUtils.LOGGER.debug("Close random access file error! ", e);
					}
				}
			}
		}
	}
	
	/**
	 * Segment file by current block size
	 * @param filePath
	 * @param blockSize
	 * @return
	 */
	public static List<SegmentationFileInfo> segmentFile(String filePath, int blockSize) {
		if (!FileUtils.isExists(filePath)) {
			return new ArrayList<SegmentationFileInfo>();
		}
		
		List<SegmentationFileInfo> segmentationFileInfos = new ArrayList<SegmentationFileInfo>();
		InputStream fileInputStream = null;
		ByteArrayOutputStream byteArrayOutputStream = null;
		
		try {
			File file = getFile(filePath);
			long fileSize = getFileSize(file);
			
			fileInputStream = new FileInputStream(file);
			
			byte[] readBuffer = new byte[blockSize];
			int index = 0;
			int readLength = 0;
			while ((readLength = fileInputStream.read(readBuffer)) != -1) {
				if (FileUtils.LOGGER.isDebugEnabled()) {
					FileUtils.LOGGER.debug("Read index: " + index + ", read length: " + readLength);
				}
				byteArrayOutputStream = new ByteArrayOutputStream(blockSize);
				byteArrayOutputStream.write(readBuffer, 0, readLength);
				SegmentationFileInfo segmentationFileInfo = 
						new SegmentationFileInfo(index * blockSize, fileSize, byteArrayOutputStream.toByteArray());
				segmentationFileInfos.add(segmentationFileInfo);
				index++;
			}
		} catch (FileNotFoundException e) {
			return new ArrayList<SegmentationFileInfo>();
		} catch (IOException e) {
			if (FileUtils.LOGGER.isDebugEnabled()) {
				FileUtils.LOGGER.debug("Read file data error! ", e);
			}
			FileUtils.LOGGER.error("Read file data error! ");
			return new ArrayList<SegmentationFileInfo>();
		} finally {
			if (fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch (IOException e) {
					if (FileUtils.LOGGER.isDebugEnabled()) {
						FileUtils.LOGGER.debug("Close file input stream error! ", e);
					}
				}
			}
		}
		
		return segmentationFileInfos;
	}

	private static String replacePageSeparator(String path) {
		String replacePath = StringUtils.replace(path, Globals.DEFAULT_PAGE_SEPARATOR, "|");
		replacePath = StringUtils.replace(replacePath, "/", "|");
		replacePath = StringUtils.replace(replacePath, "\\", "|");
		replacePath = StringUtils.replace(replacePath, "\\\\", "|");
		if (replacePath.endsWith("|")) {
			replacePath = replacePath.substring(0, replacePath.length() - 1);
		}
		return replacePath;
	}
	
	private static boolean mergeFile(RandomAccessFile randomAccessFile, 
			SegmentationFileInfo segmentationFileInfo) throws IOException {
		if (segmentationFileInfo == null) {
			return Globals.DEFAULT_VALUE_BOOLEAN;
		}

		byte[] dataContent = segmentationFileInfo.getDataContent();
		
		if (dataContent.length > 0) {
			randomAccessFile.setLength(segmentationFileInfo.getTotalSize());
			
			randomAccessFile.seek(segmentationFileInfo.getPosition());
			randomAccessFile.write(dataContent);
			
			return true;
		} else {
			if (FileUtils.LOGGER.isDebugEnabled()) {
				FileUtils.LOGGER.debug("Segmentation file part is invalid");
			}
		}
		return Globals.DEFAULT_VALUE_BOOLEAN;
	}

	/**
	 * 
	 * @param resourceLocation
	 * @return
	 */
	private static boolean isSMBFileExists(String filePath) {
		if (filePath == null) {
			return Globals.DEFAULT_VALUE_BOOLEAN;
		}
		
		if (!filePath.startsWith(SAMBA_URL_PREFIX)) {
			return FileUtils.isExists(filePath);
		}
		
		try {
			SmbFile smbFile = new SmbFile(filePath);
			return smbFile.exists();
		} catch (Exception e) {
			return Globals.DEFAULT_VALUE_BOOLEAN;
		}
	}

	/**
	 * Check current file can read
	 * @param filePath
	 * @return
	 */
	private static boolean isSMBFileCanRead(String filePath) {
		if (filePath == null) {
			return Globals.DEFAULT_VALUE_BOOLEAN;
		}
		
		if (!filePath.startsWith(SAMBA_URL_PREFIX)) {
			return FileUtils.canRead(filePath);
		}
		
		try {
			SmbFile smbFile = new SmbFile(filePath);
			return smbFile.canRead();
		} catch (Exception e) {
			return Globals.DEFAULT_VALUE_BOOLEAN;
		}
	}

	/**
	 * Check current file can read
	 * @param filePath
	 * @return
	 */
	private static boolean isSMBFileCanWrite(String filePath) {
		if (filePath == null) {
			return Globals.DEFAULT_VALUE_BOOLEAN;
		}
		
		if (!filePath.startsWith(SAMBA_URL_PREFIX)) {
			return FileUtils.canWrite(filePath);
		}
		
		try {
			SmbFile smbFile = new SmbFile(filePath);
			return !smbFile.exists() || smbFile.canWrite();
		} catch (Exception e) {
			return Globals.DEFAULT_VALUE_BOOLEAN;
		}
	}
	
	private static void registerFileType() {
		byte[] bytes = null;
		InputStream inputStream = null;
		
		try {
			inputStream = FileUtils.class.getClassLoader().getResourceAsStream("com/nervousync/datas/File.dat");
			bytes = FileUtils.readFileBytes(inputStream);
		} catch (IOException e) {
			if (FileUtils.LOGGER.isDebugEnabled()) {
				FileUtils.LOGGER.debug("Register file extension info error! ");
			}
			return;
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
				}
			}
		}
		
		List<FileExtensionInfo> extensionInfoList = new ArrayList<FileExtensionInfo>();
		byte[] readBuffer = null;
		byte[] intBuffer = new byte[4];
		System.arraycopy(bytes, 0, intBuffer, 0, 4);
		int dataCount = RawOperator.readIntFromLittleEndian(intBuffer, 0);
		int srcPos = 4;
		for (int i = 0 ; i < dataCount ; i++) {
			byte[] indexData = new byte[6];
			System.arraycopy(bytes, srcPos, indexData, 0, indexData.length);
			srcPos += 6;
			
			int fileType = (int)indexData[0];
			boolean printing = ((int)indexData[1]) == 1;
			int dataLength = RawOperator.readIntFromLittleEndian(indexData, 2);
			
			readBuffer = new byte[dataLength];
			System.arraycopy(bytes, srcPos, readBuffer, 0, dataLength);
			srcPos += dataLength;
			try {
				String contentInfo = new String(readBuffer, Globals.DEFAULT_ENCODING);
				extensionInfoList.add(new FileExtensionInfo(fileType, printing, contentInfo));
			} catch (UnsupportedEncodingException e) {
			}
		}
		
		for (FileExtensionInfo fileExtensionInfo : extensionInfoList) {
			if (REGISTER_IDEN_MAP.containsKey(fileExtensionInfo.getExtensionName())) {
				FileUtils.LOGGER.warn("Override file extension info, extension name: " + fileExtensionInfo.getExtensionName());
			}
			REGISTER_IDEN_MAP.put(fileExtensionInfo.getExtensionName(), fileExtensionInfo);
		}
	}
	
	private static final class FilenameExtensionFilter implements FilenameFilter {
		private String fileExtName = null;
		
		public FilenameExtensionFilter(String fileExtName) {
			this.fileExtName = fileExtName;
		}
		
		public boolean accept(File dir, String name) {
			if (this.fileExtName != null && dir != null && dir.isDirectory() 
					&& dir.exists() && name != null) {
				String fileExtName = StringUtils.getFilenameExtension(name);
				return (fileExtName != null && fileExtName.equalsIgnoreCase(this.fileExtName));
			}
			return Globals.DEFAULT_VALUE_BOOLEAN;
		}
	}
	
	private static final class DirectoryFileFilter implements FileFilter {
		public boolean accept(File pathname) {
			return pathname.isDirectory();
		}
	}
}
