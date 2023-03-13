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
import java.net.*;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.CRC32;

import jcifs.CIFSContext;
import jcifs.CIFSException;
import jcifs.Config;
import jcifs.smb.NtlmPasswordAuthenticator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jcifs.config.PropertyConfiguration;
import jcifs.context.BaseContext;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbFileOutputStream;

import org.nervousync.beans.xml.files.SegmentationFile;
import org.nervousync.beans.xml.files.SegmentationItem;
import org.nervousync.commons.core.Globals;
import org.nervousync.zip.ZipFile;

/**
 * File operate utils
 * support zip/unzip Files Folders
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: Jan 13, 2010 11:08:14 AM $
 */
public final class FileUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);

	/**
	 * URL prefixes for loading from the class path: "classpath:"
	 */
	public static final String CLASSPATH_URL_PREFIX = "classpath:";

	/**
	 * URL prefixes for loading from the file system: "file:"
	 */
	public static final String FILE_URL_PREFIX = "file:";

	/**
	 * URL protocol for a file in the file system: "file"
	 */
	public static final String URL_PROTOCOL_FILE = "file";

	/**
	 * URL protocol for an entry from a jar file: "jar"
	 */
	public static final String URL_PROTOCOL_JAR = "jar";

	/**
	 * URL protocol for an entry from a zip file: "zip"
	 */
	public static final String URL_PROTOCOL_ZIP = "zip";

	/**
	 * URL protocol for an entry from a WebSphere jar file: "wsjar"
	 */
	public static final String URL_PROTOCOL_WSJAR = "wsjar";

	/**
	 * URL protocol for an entry from an OC4J jar file: "code-source"
	 */
	public static final String URL_PROTOCOL_CODE_SOURCE = "code-source";

	/**
	 * Separator between JAR URL and file path within the JAR
	 */
	public static final String JAR_URL_SEPARATOR = "!/";

	/**
	 * Carriage Return character
	 */
	public static final char CR = '\r';

	/**
	 * Line Feed character
	 */
	public static final char LF = '\n';

	/**
	 * Carriage Return Line Feed character
	 */
	public static final String CRLF = "\r\n";
	/**
	 * The constant MIME_TYPE_TEXT.
	 */
	public static final String MIME_TYPE_TEXT = "text/plain";
	/**
	 * The constant MIME_TYPE_TEXT_XML.
	 */
	public static final String MIME_TYPE_TEXT_XML = "text/xml";
	/**
	 * The constant MIME_TYPE_TEXT_YAML.
	 */
	public static final String MIME_TYPE_TEXT_YAML = "text/yaml";
	/**
	 * The constant MIME_TYPE_BINARY.
	 */
	public static final String MIME_TYPE_BINARY = "application/octet-stream";
	/**
	 * The constant MIME_TYPE_XML.
	 */
	public static final String MIME_TYPE_XML = "application/xml";
	/**
	 * The constant MIME_TYPE_JSON.
	 */
	public static final String MIME_TYPE_JSON = "application/json";

	/**
	 * The constant MIME_TYPE_YAML.
	 */
	public static final String MIME_TYPE_YAML = "application/x-yaml";

	private FileUtils() {
	}

	static {
		//  Register SMB protocol handler for using java.net.URL class with "smb://"
		Config.registerSmbURLHandler();
	}

	/**
	 * Match folder path in entry path
	 *
	 * @param entryPath  entry path
	 * @param folderPath folder path
	 * @return Match result
	 */
	public static boolean matchFolder(final String entryPath, final String folderPath) {
		if (StringUtils.isEmpty(entryPath) || StringUtils.isEmpty(folderPath)) {
			return Boolean.FALSE;
		}

		String convertFolderPath = FileUtils.replacePageSeparator(folderPath) + "|";
		return FileUtils.replacePageSeparator(entryPath).startsWith(convertFolderPath);
	}

	/**
	 * Match two path was same
	 *
	 * @param origPath   orig path
	 * @param destPath   dest path
	 * @param ignoreCase ignore character case
	 * @return Match result
	 */
	public static boolean matchFilePath(final String origPath, final String destPath, final boolean ignoreCase) {
		if (origPath == null || destPath == null) {
			return Boolean.FALSE;
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
	 * Retrieve MIMEType string
	 *
	 * @param extensionName extension name
	 * @return MIMEType string
	 */
	public static String retrieveMimeType(final String extensionName) {
		if (StringUtils.notBlank(extensionName)) {
			String extName = extensionName.startsWith(".") ? extensionName : "." + extensionName;
			return Optional.ofNullable(URLConnection.getFileNameMap().getContentTypeFor(extName))
					.orElse(MIME_TYPE_BINARY);
		}

		return MIME_TYPE_BINARY;
	}

	public static boolean imageFile(final String fileLocation) {
		return retrieveMimeType(StringUtils.getFilenameExtension(fileLocation)).contains("image");
	}

	/**
	 * Return whether the given resource location is a URL:
	 * either a special "classpath" pseudo URL or a standard URL.
	 *
	 * @param resourceLocation the location String to check
	 * @return <code>Boolean.TRUE</code> when location qualifies as a URL, <code>Boolean.FALSE</code> for others
	 * @see java.net.URL
	 */
	public static boolean isUrl(final String resourceLocation) {
		if (!FileUtils.isExists(resourceLocation)) {
			return Boolean.FALSE;
		}

		try {
			new URL(resourceLocation);
			return true;
		} catch (MalformedURLException ex) {
			return Boolean.FALSE;
		}
	}

	/**
	 * Resolve the given resource location to a <code>java.net.URL</code>.
	 * <p>Does not check whether the URL actually exists; simply returns
	 * the URL that the given location would correspond to.
	 *
	 * @param resourceLocation the resource location to resolve: either a "classpath:" pseudo URL, a "file:" URL, or a plain file path
	 * @return a corresponding URL object
	 * @throws FileNotFoundException if the resource cannot be resolved to a URL
	 */
	public static URL getURL(final String resourceLocation) throws FileNotFoundException {
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
		} catch (MalformedURLException ex) {
			// no URL -> treat as file path
			try {
				return new File(resourceLocation).toURI().toURL();
			} catch (MalformedURLException ex2) {
				throw new FileNotFoundException("Resource location [" + resourceLocation +
						"] is neither a URL not a well-formed file path");
			}
		}
	}

	/**
	 * Read file last modified time
	 *
	 * @param resourceLocation resource location
	 * @return last modified time with long type if file exists
	 */
	public static long lastModify(final String resourceLocation) {
		return FileUtils.lastModify(resourceLocation, new Properties());
	}

	/**
	 * Read file last modified time
	 *
	 * @param resourceLocation resource location
	 * @param properties       the properties
	 * @return last modified time with long type if file exists
	 */
	public static long lastModify(final String resourceLocation, final Properties properties) {
		if (resourceLocation == null || resourceLocation.trim().length() == 0) {
			return Globals.DEFAULT_VALUE_LONG;
		}
		if (resourceLocation.startsWith(Globals.SAMBA_PROTOCOL)) {
			try (SmbFile smbFile = new SmbFile(resourceLocation, new BaseContext(new PropertyConfiguration(properties)))) {
				if (smbFile.exists()) {
					return smbFile.getLastModified();
				}
			} catch (Exception e) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Read file last modify error! ", e);
				}
			}
		} else {
			try {
				File file = FileUtils.getFile(resourceLocation);
				if (file.exists()) {
					return file.lastModified();
				}
			} catch (Exception e) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Read file last modify error! ", e);
				}
			}
		}
		return Globals.DEFAULT_VALUE_LONG;
	}

	/**
	 * Read file last modified time
	 *
	 * @param resourceLocation resource location
	 * @param properties       the properties
	 * @return last modified time with <code>java.util.Date</code> type if file exists or null for others
	 */
	public static Date modifyDate(final String resourceLocation, final Properties properties) {
		long lastModify = FileUtils.lastModify(resourceLocation, properties);
		if (lastModify != Globals.DEFAULT_VALUE_LONG) {
			return new Date(lastModify);
		} else {
			return null;
		}
	}

	/**
	 * Load resource and convert to java.io.InputStream used <code>Globals.DEFAULT_ENCODING</code>
	 *
	 * @param resourceLocation resource location
	 * @return <code>java.io.InputStream</code>
	 * @throws IOException when opening input stream error
	 */
	public static InputStream loadFile(String resourceLocation) throws IOException {
		if (StringUtils.isEmpty(resourceLocation)) {
			throw new IOException("Resource location is null! ");
		}
		//	Convert resource location to input stream
		InputStream inputStream;

		if (resourceLocation.startsWith(Globals.SAMBA_PROTOCOL)) {
			inputStream = new SmbFileInputStream(resourceLocation,
					new BaseContext(new PropertyConfiguration(new Properties())));
		} else {
			inputStream = FileUtils.class.getResourceAsStream(resourceLocation);
			if (inputStream == null) {
				try {
					inputStream = FileUtils.getURL(resourceLocation).openStream();
				} catch (Exception e) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Open file input stream error! ", e);
					}
					throw new IOException(e);
				}
			}
		}
		return inputStream;
	}

	public static InputStream loadFile(final String smbLocation, final Properties properties) throws IOException {
		return loadFile(smbLocation, properties, null);
	}

	public static InputStream loadFile(final String smbLocation,
	                                   final NtlmPasswordAuthenticator ntlmPasswordAuthenticator) throws IOException {
		return loadFile(smbLocation, null, ntlmPasswordAuthenticator);
	}

	public static InputStream loadFile(final String smbLocation, final Properties properties,
	                                   final NtlmPasswordAuthenticator ntlmPasswordAuthenticator) throws IOException {
		if (StringUtils.isEmpty(smbLocation) || !smbLocation.startsWith(Globals.SAMBA_PROTOCOL)) {
			throw new IOException("Location is not a valid smb location! ");
		}
		return new SmbFileInputStream(smbLocation, generateContext(properties, ntlmPasswordAuthenticator));
	}

	/**
	 * Resolve the given resource location to a <code>java.io.File</code>,
	 * i.e. to a file in the file system.
	 * <p>Does not check whether the fil actually exists; simply returns
	 * the File that the given location would correspond to.
	 *
	 * @param resourceLocation the resource location to resolve: either a "classpath:" pseudo URL, a "file:" URL, or a plain file path
	 * @return a corresponding File object
	 * @throws FileNotFoundException if the resource cannot be resolved to a file in the file system
	 */
	public static File getFile(final String resourceLocation) throws FileNotFoundException {
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
		} catch (MalformedURLException ex) {
			// no URL -> treat as file path
			return new File(resourceLocation);
		}
	}

	/**
	 * Gets file.
	 *
	 * @param filePath   the file path
	 * @param properties the properties
	 * @return the file
	 */
	public static SmbFile getFile(final String filePath, final Properties properties) {
		if (StringUtils.isEmpty(filePath)) {
			return null;
		}
		try {
			return new SmbFile(filePath, generateContext(properties, null));
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Gets file.
	 *
	 * @param filePath                  the file path
	 * @param ntlmPasswordAuthenticator the ntlm password authenticator
	 * @return the file
	 */
	public static SmbFile getFile(final String filePath, final NtlmPasswordAuthenticator ntlmPasswordAuthenticator) {
		if (StringUtils.isEmpty(filePath)) {
			return null;
		}
		try {
			return new SmbFile(filePath, generateContext(null, ntlmPasswordAuthenticator));
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Gets file.
	 *
	 * @param filePath                  the file path
	 * @param properties                the properties
	 * @param ntlmPasswordAuthenticator the ntlm password authenticator
	 * @return the file
	 */
	public static SmbFile getFile(final String filePath, final Properties properties,
	                              final NtlmPasswordAuthenticator ntlmPasswordAuthenticator) {
		if (StringUtils.isEmpty(filePath)) {
			return null;
		}
		try {
			return new SmbFile(filePath, generateContext(properties, ntlmPasswordAuthenticator));
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Gets file.
	 *
	 * @param filePath    the file path
	 * @param cifsContext the cifs context
	 * @return the file
	 */
	private static SmbFile getFile(final String filePath, final CIFSContext cifsContext) {
		if (StringUtils.isEmpty(filePath)) {
			return null;
		}
		try {
			return new SmbFile(filePath, cifsContext);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Resolve the given resource URL to a <code>java.io.File</code>,
	 * i.e. to a file in the file system.
	 *
	 * @param resourceUrl the resource URL to resolve
	 * @return a corresponding File object
	 * @throws FileNotFoundException if the URL cannot be resolved to a file in the file system
	 */
	public static File getFile(URL resourceUrl) throws FileNotFoundException {
		return getFile(resourceUrl, "URL");
	}

	/**
	 * Resolve the given resource URL to a <code>java.io.File</code>,
	 * i.e. to a file in the file system.
	 *
	 * @param resourceUrl the resource URL to resolve
	 * @param description a description of the original resource that the URL was created for (for example, a class path location)
	 * @return a corresponding File object
	 * @throws FileNotFoundException if the URL cannot be resolved to a file in the file system
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
	 *
	 * @param resourceUri the resource URI to resolve
	 * @return a corresponding File object
	 * @throws FileNotFoundException if the URL cannot be resolved to a file in the file system
	 */
	public static File getFile(URI resourceUri) throws FileNotFoundException {
		return getFile(resourceUri, "URI");
	}

	/**
	 * Resolve the given resource URI to a <code>java.io.File</code>,
	 * i.e. to a file in the file system.
	 *
	 * @param resourceUri the resource URI to resolve
	 * @param description a description of the original resource that the URI was created for (for example, a class path location)
	 * @return a corresponding File object
	 * @throws FileNotFoundException if the URL cannot be resolved to a file in the file system
	 */
	public static File getFile(URI resourceUri, String description) throws FileNotFoundException {
		if (resourceUri == null) {
			throw new IllegalArgumentException("Resource URI must not be null");
		}
		if (!URL_PROTOCOL_FILE.equals(resourceUri.getScheme())) {
			throw new FileNotFoundException(description + " cannot be resolved to absolute file path " +
					"because it does not reside in the file system: " + resourceUri);
		}
		return new File(resourceUri.getSchemeSpecificPart());
	}

	/**
	 * List jar entry list.
	 *
	 * @param filePath the file path
	 * @return the list
	 */
	public static List<String> listJarEntry(String filePath) {

		List<String> entryList = new ArrayList<>();
		try (JarFile jarFile = new JarFile(getFile(filePath))) {
			Enumeration<JarEntry> enumeration = jarFile.entries();
			while (enumeration.hasMoreElements()) {
				JarEntry jarEntry = enumeration.nextElement();
				if (!jarEntry.isDirectory()) {
					entryList.add(jarEntry.getName());
				}
			}
		} catch (Exception e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Load jar entry content error! ", e);
			}
		}

		return entryList;
	}

	/**
	 * Read jar entry info string.
	 *
	 * @param filePath  the file path
	 * @param entryPath the entry path
	 * @return the string
	 */
	public static String readJarEntryInfo(String filePath, String entryPath) {
		String entryContent = null;
		InputStream inputStream = null;
		InputStreamReader inputStreamReader = null;

		try {
			JarFile jarFile = new JarFile(getFile(filePath));

			JarEntry packageEntry = jarFile.getJarEntry(entryPath);

			if(packageEntry != null){
				inputStream = jarFile.getInputStream(packageEntry);
				inputStreamReader = new InputStreamReader(inputStream, Globals.DEFAULT_ENCODING);

				char [] readBuffer = new char[Globals.DEFAULT_BUFFER_SIZE];
				int readLength;
				StringBuilder returnValue = new StringBuilder();

				while (((readLength = inputStreamReader.read(readBuffer)) > -1)) {
					returnValue.append(readBuffer, 0, readLength);
				}

				entryContent = returnValue.toString();
			}

			jarFile.close();
		} catch (Exception e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Load jar entry content error! ", e);
			}
		} finally {
			IOUtils.closeStream(inputStreamReader);
			IOUtils.closeStream(inputStream);
		}

		return entryContent;
	}

	/**
	 * Read entry content from jar file
	 *
	 * @param filePath  jar file location
	 * @param entryPath read entry path
	 * @return entry content or zero length array if not exists
	 */
	public static byte[] readJarEntryBytes(String filePath, String entryPath) {
		return FileUtils.readJarEntryBytes(filePath, entryPath, 0, Globals.DEFAULT_VALUE_INT);
	}

	/**
	 * Read entry content from jar file
	 *
	 * @param filePath  jar file location
	 * @param entryPath read entry path
	 * @param offset    read offset
	 * @param length    read length
	 * @return entry content or zero length array if not exists
	 */
	public static byte[] readJarEntryBytes(String filePath, String entryPath, int offset, int length) {
		JarFile jarFile = null;
		InputStream inputStream = null;
		ByteArrayOutputStream byteArrayOutputStream = null;

		try {
			jarFile = new JarFile(getFile(filePath));

			JarEntry packageEntry = jarFile.getJarEntry(entryPath);

			if (packageEntry != null) {
				inputStream = jarFile.getInputStream(packageEntry);
				byteArrayOutputStream = new ByteArrayOutputStream();

				byte [] buffer;
				int readLength = 0;
				int position = Math.max(offset, Globals.INITIALIZE_INT_VALUE);
				int limitLength = Math.min(length, inputStream.available());
				do {
					int itemLength = Math.min((limitLength - readLength), Globals.DEFAULT_BUFFER_SIZE);
					buffer = new byte[itemLength];
					int currentLength = inputStream.read(buffer, position + readLength, itemLength);
					if (currentLength == itemLength) {
						byteArrayOutputStream.write(buffer, 0, buffer.length);
					} else if (currentLength == Globals.DEFAULT_VALUE_INT) {
						break;
					}
					readLength += itemLength;
				} while (readLength != limitLength);

				return byteArrayOutputStream.toByteArray();
			}
		} catch (Exception e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Load jar entry content error! ", e);
			}
		} finally {
			IOUtils.closeStream(inputStream);
			IOUtils.closeStream(byteArrayOutputStream);
			IOUtils.closeStream(jarFile);
		}
		return new byte[0];
	}

	/**
	 * Read resource content
	 *
	 * @param file object
	 * @return File data by byte arrays
	 * @throws IOException if an I/O error occurs
	 */
	public static byte[] readFileBytes(File file) throws IOException {
		if (file == null || !file.exists()) {
			throw new IOException("File not found");
		}

		byte[] content;
		try {
			content = IOUtils.readBytes(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			content = new byte[0];
		}

		return content;
	}

	/**
	 * Read resource content
	 *
	 * @param resourceLocation Resource location
	 * @return File data by byte arrays
	 * @throws IOException if an I/O error occurs
	 */
	public static byte[] readFileBytes(String resourceLocation) throws IOException {
		return FileUtils.readFileBytes(FileUtils.getFile(resourceLocation));
	}

	/**
	 * Read resource content info in define length
	 *
	 * @param resourceLocation resource location
	 * @param position         start point
	 * @param length           read length
	 * @return File data by byte arrays
	 */
	public static byte[] readFileBytes(String resourceLocation, long position, int length) {
		byte[] readByte = new byte[length];

		try (RandomAccessFile randomAccessFile = new RandomAccessFile(resourceLocation, "r")) {
			randomAccessFile.seek(position);
			randomAccessFile.read(readByte);
		} catch (Exception e) {
			readByte = new byte[0];
		}

		return readByte;
	}

	/**
	 * Retrieve resource location size
	 *
	 * @param resourceLocation resource location
	 * @return File size
	 */
	public static long fileSize(String resourceLocation) {
		return FileUtils.fileSize(resourceLocation, null);
	}

	/**
	 * Retrieve resource location size
	 *
	 * @param resourceLocation resource location
	 * @param cifsContext      the cifs context
	 * @return File size
	 */
	public static long fileSize(String resourceLocation, final CIFSContext cifsContext) {
		if (resourceLocation == null) {
			return Globals.DEFAULT_VALUE_LONG;
		}

		if (resourceLocation.startsWith(Globals.SAMBA_PROTOCOL)) {
			return fileSize(FileUtils.getFile(resourceLocation, cifsContext));
		} else {
			try {
				return fileSize(FileUtils.getFile(resourceLocation));
			} catch (FileNotFoundException e) {
				return Globals.DEFAULT_VALUE_LONG;
			}
		}
	}

	/**
	 * File size long.
	 *
	 * @param fileObject the file object
	 * @return the long
	 */
	public static long fileSize(final Object fileObject) {
		if (fileObject == null) {
			return Globals.DEFAULT_VALUE_LONG;
		}

		long fileSize = 0L;
		if (fileObject instanceof SmbFile) {
			try {
				if (((SmbFile) fileObject).exists()) {
					if (((SmbFile) fileObject).isDirectory()) {
						SmbFile[] childFiles = ((SmbFile) fileObject).listFiles();
						if (childFiles != null) {
							for (SmbFile childFile : childFiles) {
								fileSize += fileSize(childFile);
							}
						}
					} else if (((SmbFile) fileObject).isFile()) {
						fileSize += ((SmbFile) fileObject).length();
					}
				}
			} catch (Exception e) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Read file size error! ", e);
				}
				return Globals.DEFAULT_VALUE_LONG;
			}
		} else {
			if (((File) fileObject).exists()) {
				if (((File) fileObject).isDirectory()) {
					File[] childFiles = ((File) fileObject).listFiles();
					if (childFiles != null) {
						for (File childFile : childFiles) {
							fileSize += fileSize(childFile);
						}
					}
				} else if (((File) fileObject).isFile()) {
					fileSize += ((File) fileObject).length();
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
	 *
	 * @param url the URL to check
	 * @return whether the URL has been identified as a JAR URL
	 */
	public static boolean isJarURL(URL url) {
		String protocol = url.getProtocol();
		return (URL_PROTOCOL_JAR.equals(protocol) ||
				URL_PROTOCOL_ZIP.equals(protocol) ||
				URL_PROTOCOL_WSJAR.equals(protocol) ||
				(URL_PROTOCOL_CODE_SOURCE.equals(protocol) && url.getPath().contains(JAR_URL_SEPARATOR)));
	}

	/**
	 * Extract the URL for the actual jar file from the given URL
	 * (which may point to a resource in a jar file or to a jar file itself).
	 *
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
			} catch (MalformedURLException ex) {
				// Probably no protocol in original jar URL, like "jar:C:/path/jarFile.jar".
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
	 *
	 * @param url the URL to convert into a URI instance
	 * @return the URI instance
	 * @throws URISyntaxException if the URL wasn't a valid URI
	 * @see java.net.URL#toURI() java.net.URL#toURI()java.net.URL#toURI()java.net.URL#toURI()java.net.URL#toURI()java.net.URL#toURI()java.net.URL#toURI()java.net.URL#toURI()java.net.URL#toURI()java.net.URL#toURI()java.net.URL#toURI()java.net.URL#toURI()java.net.URL#toURI()java.net.URL#toURI()java.net.URL#toURI()java.net.URL#toURI()java.net.URL#toURI()java.net.URL#toURI()java.net.URL#toURI()java.net.URL#toURI()java.net.URL#toURI()java.net.URL#toURI()java.net.URL#toURI()java.net.URL#toURI()java.net.URL#toURI()java.net.URL#toURI()
	 */
	public static URI toURI(URL url) throws URISyntaxException {
		return FileUtils.toURI(url.toString());
	}

	/**
	 * Create a URI instance for the given location String,
	 * replacing spaces with "%20" quotes first.
	 *
	 * @param location the location String to convert into a URI instance
	 * @return the URI instance
	 * @throws URISyntaxException if the location wasn't a valid URI
	 */
	public static URI toURI(String location) throws URISyntaxException {
		return new URI(StringUtils.replace(location, " ", "%20"));
	}

	/**
	 * List jar entry
	 *
	 * @param uri Jar file URI
	 * @return List of entry names
	 */
	public static List<String> listJarEntry(URI uri) {
		List<String> returnList = new ArrayList<>();

		if (uri != null) {
			String fullPath = uri.getPath();
			String filePath;
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
					BasicFileAttributes basicFileAttributes =
							Files.readAttributes(file.toPath(), BasicFileAttributes.class);
					if (basicFileAttributes.isDirectory()) {
						returnList = FileUtils.listFiles(file);
					} else if (basicFileAttributes.isRegularFile()) {
						jarFile = new JarFile(file);
						Enumeration<JarEntry> enumeration = jarFile.entries();

						while (enumeration.hasMoreElements()) {
							JarEntry jarEntry = enumeration.nextElement();
							if (jarEntry.isDirectory()) {
								continue;
							}
							String entryName = jarEntry.getName();
							if (entryPath == null || entryName.startsWith(entryPath)) {
								returnList.add(entryName);
							}
						}
					}
				} catch (Exception e) {
					returnList = new ArrayList<>();
				} finally {
					IOUtils.closeStream(jarFile);
				}
			}
		}
		return returnList;
	}

	/**
	 * List child files
	 *
	 * @param filePath parent file path
	 * @return list of child file path
	 * @throws FileNotFoundException if the resource cannot be resolved to a file in the file system
	 */
	public static List<String> listFiles(String filePath) throws FileNotFoundException {
		return FileUtils.listFiles(FileUtils.getFile(filePath));
	}

	/**
	 * List child files
	 *
	 * @param filePath        parent file path
	 * @param readHiddenFiles List include hidden files
	 * @return list of child file path
	 * @throws FileNotFoundException if the resource cannot be resolved to a file in the file system
	 */
	public static List<String> listFiles(String filePath, boolean readHiddenFiles) throws FileNotFoundException {
		return FileUtils.listFiles(FileUtils.getFile(filePath), readHiddenFiles);
	}

	/**
	 * List child files
	 *
	 * @param filePath           parent file path
	 * @param readHiddenFiles    List include hidden files
	 * @param iterateChildFolder to iterate child folder
	 * @return list of child file path
	 * @throws FileNotFoundException if the resource cannot be resolved to a file in the file system
	 */
	public static List<String> listFiles(String filePath, boolean readHiddenFiles,
	                                     boolean iterateChildFolder) throws FileNotFoundException {
		return FileUtils.listFiles(FileUtils.getFile(filePath), readHiddenFiles, iterateChildFolder);
	}

	/**
	 * List child files
	 *
	 * @param filePath           parent file path
	 * @param readHiddenFiles    List include hidden files
	 * @param includeRootFolder  List include directories
	 * @param iterateChildFolder to iterate child folder
	 * @return list of child file path
	 * @throws FileNotFoundException if the resource cannot be resolved to a file in the file system
	 */
	public static List<String> listFiles(String filePath, boolean readHiddenFiles, boolean includeRootFolder,
	                                     boolean iterateChildFolder) throws FileNotFoundException {
		return FileUtils.listFiles(FileUtils.getFile(filePath), readHiddenFiles, includeRootFolder, iterateChildFolder);
	}

	/**
	 * List child files
	 *
	 * @param file parent file object
	 * @return list of child file path
	 */
	public static List<String> listFiles(File file) {
		return FileUtils.listFiles(file, null);
	}

	/**
	 * List child files
	 *
	 * @param file            parent file object
	 * @param readHiddenFiles List include hidden files
	 * @return list of child file path
	 */
	public static List<String> listFiles(File file, boolean readHiddenFiles) {
		return FileUtils.listFiles(file, null, readHiddenFiles);
	}

	/**
	 * List child files
	 *
	 * @param file               parent file object
	 * @param readHiddenFiles    List include hidden files
	 * @param iterateChildFolder to iterate child folder
	 * @return list of child file path
	 */
	public static List<String> listFiles(File file, boolean readHiddenFiles, boolean iterateChildFolder) {
		return FileUtils.listFiles(file, null, readHiddenFiles, iterateChildFolder);
	}

	/**
	 * List child files
	 *
	 * @param file               parent file object
	 * @param readHiddenFiles    List include hidden files
	 * @param includeRootFolder  List include directories
	 * @param iterateChildFolder to iterate child folder
	 * @return list of child file path
	 */
	public static List<String> listFiles(File file, boolean readHiddenFiles,
	                                     boolean includeRootFolder, boolean iterateChildFolder) {
		return FileUtils.listFiles(file, null, readHiddenFiles, includeRootFolder, iterateChildFolder);
	}

	/**
	 * List child files by file name filter
	 *
	 * @param filePath parent file path
	 * @param filter   file name filter
	 * @return list of child file path
	 * @throws FileNotFoundException if the resource cannot be resolved to a file in the file system
	 */
	public static List<String> listFiles(String filePath, FilenameFilter filter)
			throws FileNotFoundException {
		return FileUtils.listFiles(FileUtils.getFile(filePath), filter);
	}

	/**
	 * List child files by file name filter
	 *
	 * @param filePath        parent file path
	 * @param filter          file name filter
	 * @param readHiddenFiles List include hidden files
	 * @return list of child file path
	 * @throws FileNotFoundException if the resource cannot be resolved to a file in the file system
	 */
	public static List<String> listFiles(String filePath, FilenameFilter filter,
	                                     boolean readHiddenFiles) throws FileNotFoundException {
		return FileUtils.listFiles(FileUtils.getFile(filePath), filter, readHiddenFiles);
	}

	/**
	 * List child files by file name filter
	 *
	 * @param filePath           parent file path
	 * @param filter             file name filter
	 * @param readHiddenFiles    List include hidden files
	 * @param iterateChildFolder to iterate child folder
	 * @return list of child file path
	 * @throws FileNotFoundException if the resource cannot be resolved to a file in the file system
	 */
	public static List<String> listFiles(String filePath, FilenameFilter filter, boolean readHiddenFiles,
	                                     boolean iterateChildFolder) throws FileNotFoundException {
		return FileUtils.listFiles(FileUtils.getFile(filePath), filter, readHiddenFiles, iterateChildFolder);
	}

	/**
	 * List child files by file name filter
	 *
	 * @param file   parent file object
	 * @param filter file name filter
	 * @return list of child file path
	 */
	public static List<String> listFiles(File file, FilenameFilter filter) {
		List<String> returnList = new ArrayList<>();
		FileUtils.listFiles(file, filter, returnList, true,
				Boolean.FALSE, true);
		return returnList;
	}

	/**
	 * List child files by file name filter
	 *
	 * @param file            parent file object
	 * @param filter          file name filter
	 * @param readHiddenFiles List include hidden files
	 * @return list of child file path
	 */
	public static List<String> listFiles(File file, FilenameFilter filter, boolean readHiddenFiles) {
		List<String> returnList = new ArrayList<>();
		FileUtils.listFiles(file, filter, returnList, readHiddenFiles,
				Boolean.FALSE, true);
		return returnList;
	}

	/**
	 * List child files by file name filter
	 *
	 * @param file              parent file object
	 * @param filter            file name filter
	 * @param readHiddenFiles   List include hidden files
	 * @param includeRootFolder List include directories
	 * @return list of child file path
	 */
	public static List<String> listFiles(File file, FilenameFilter filter,
	                                     boolean readHiddenFiles, boolean includeRootFolder) {
		List<String> returnList = new ArrayList<>();
		FileUtils.listFiles(file, filter, returnList, readHiddenFiles, includeRootFolder, true);
		return returnList;
	}

	/**
	 * List child files by file name filter
	 *
	 * @param file               parent file object
	 * @param filter             file name filter
	 * @param readHiddenFiles    List include hidden files
	 * @param includeRootFolder  List include directories
	 * @param iterateChildFolder to iterate child folder
	 * @return list of child file path
	 */
	public static List<String> listFiles(File file, FilenameFilter filter, boolean readHiddenFiles,
	                                     boolean includeRootFolder, boolean iterateChildFolder) {
		List<String> returnList = new ArrayList<>();
		FileUtils.listFiles(file, filter, returnList, readHiddenFiles, includeRootFolder, iterateChildFolder);
		return returnList;
	}

	/**
	 * List child files and append file path to current list
	 *
	 * @param filePath parent file path
	 * @param fileList current child file list
	 * @throws IOException the io exception
	 */
	public static void listFiles(String filePath, List<String> fileList) throws IOException {
		FileUtils.listFiles(FileUtils.getFile(filePath), null, fileList, true,
				Boolean.FALSE, true);
	}

	/**
	 * List child files and append file path to current list
	 *
	 * @param filePath        parent file path
	 * @param fileList        current child file list
	 * @param readHiddenFiles List include hidden files
	 * @throws IOException the io exception
	 */
	public static void listFiles(String filePath, List<String> fileList, boolean readHiddenFiles) throws IOException {
		FileUtils.listFiles(FileUtils.getFile(filePath), null, fileList, readHiddenFiles,
				Boolean.FALSE, true);
	}

	/**
	 * List child files and append file path to current list
	 *
	 * @param filePath           parent file path
	 * @param fileList           current child file list
	 * @param readHiddenFiles    List include hidden files
	 * @param iterateChildFolder to iterate child folder
	 * @throws IOException the io exception
	 */
	public static void listFiles(String filePath, List<String> fileList, boolean readHiddenFiles,
	                             boolean iterateChildFolder) throws IOException {
		FileUtils.listFiles(FileUtils.getFile(filePath), null, fileList, readHiddenFiles,
				Boolean.FALSE, iterateChildFolder);
	}

	/**
	 * List child files and append file path to current list
	 *
	 * @param filePath           parent file path
	 * @param fileList           current child file list
	 * @param readHiddenFiles    List include hidden files
	 * @param includeRootFolder  List include directories
	 * @param iterateChildFolder to iterate child folder
	 * @throws IOException the io exception
	 */
	public static void listFiles(String filePath, List<String> fileList, boolean readHiddenFiles,
	                             boolean includeRootFolder, boolean iterateChildFolder) throws IOException {
		FileUtils.listFiles(FileUtils.getFile(filePath), null, fileList, readHiddenFiles,
				includeRootFolder, iterateChildFolder);
	}

	/**
	 * List child files by file name filter and append file path to current list
	 *
	 * @param filePath parent file path
	 * @param filter   file name filter
	 * @param fileList current child file list
	 * @throws IOException the io exception
	 */
	public static void listFiles(String filePath, FilenameFilter filter, List<String> fileList) throws IOException {
		FileUtils.listFiles(FileUtils.getFile(filePath), filter, fileList, true,
				Boolean.FALSE, true);
	}

	/**
	 * List child files by file name filter and append file path to current list
	 *
	 * @param filePath          parent file path
	 * @param filter            file name filter
	 * @param fileList          current child file list
	 * @param includeRootFolder include root folder
	 * @throws FileNotFoundException if the resource cannot be resolved to a file in the file system
	 */
	public static void listFiles(String filePath, FilenameFilter filter,
	                             List<String> fileList, boolean includeRootFolder) throws FileNotFoundException {
		FileUtils.listFiles(FileUtils.getFile(filePath), filter, fileList, true,
				includeRootFolder, true);
	}

	/**
	 * List child files by file name filter and append file path to current list
	 *
	 * @param filePath           parent file path
	 * @param filter             file name filter
	 * @param fileList           current child file list
	 * @param includeRootFolder  include root folder
	 * @param iterateChildFolder to iterate child folder
	 * @throws FileNotFoundException if the resource cannot be resolved to a file in the file system
	 */
	public static void listFiles(String filePath, FilenameFilter filter, List<String> fileList,
	                             boolean includeRootFolder, boolean iterateChildFolder) throws FileNotFoundException {
		FileUtils.listFiles(FileUtils.getFile(filePath), filter, fileList, true,
				includeRootFolder, iterateChildFolder);
	}

	/**
	 * List child files by file name filter and append file path to current list
	 *
	 * @param file               parent file object
	 * @param filter             file name filter
	 * @param fileList           current child file list
	 * @param readHiddenFiles    include hidden file
	 * @param includeRootFolder  include root folder
	 * @param iterateChildFolder to iterate child folder
	 */
	public static void listFiles(File file, FilenameFilter filter, List<String> fileList,
	                             boolean readHiddenFiles, boolean includeRootFolder, boolean iterateChildFolder) {
		if (fileList == null) {
			fileList = new ArrayList<>();
		}

		if (file.isDirectory()) {
			if (includeRootFolder) {
				fileList.add(file.getAbsolutePath());
			}
			File[] childFiles = file.listFiles();
			if (childFiles != null) {
				for (File childFile : childFiles) {
					if (childFile.isDirectory()) {
						if (iterateChildFolder) {
							FileUtils.listFiles(childFile, filter, fileList, readHiddenFiles,
									includeRootFolder, Boolean.TRUE);
						}
					} else {
						if (!readHiddenFiles && file.isHidden()) {
							continue;
						}

						boolean match = Boolean.FALSE;
						if (filter == null) {
							match = true;
						} else if (filter.accept(childFile.getParentFile(), childFile.getName())) {
							match = true;
						}

						if (match) {
							String filePath = childFile.getAbsolutePath();
							if (!fileList.contains(filePath)) {
								fileList.add(filePath);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * List child directory
	 *
	 * @param filePath parent path
	 * @return list of child directory path
	 * @throws FileNotFoundException if the resource cannot be resolved to a file in the file system
	 */
	public static List<String> listDirectory(String filePath) throws FileNotFoundException {
		List<String> directoryList = new ArrayList<>();
		FileUtils.listDirectory(FileUtils.getFile(filePath), directoryList);
		return directoryList;
	}

	/**
	 * List child directory
	 *
	 * @param directory parent directory object
	 * @return list of child directory path
	 */
	public static List<String> listDirectory(File directory) {
		List<String> directoryList = new ArrayList<>();
		FileUtils.listDirectory(directory, directoryList);
		return directoryList;
	}

	/**
	 * List child directory and append to current directory list
	 *
	 * @param file          parent directory object
	 * @param directoryList current directory list
	 */
	public static void listDirectory(File file, List<String> directoryList) {
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
	 *
	 * @param filePath    parent file path
	 * @param fileExtName extension name
	 * @return list of file path
	 * @throws FileNotFoundException if the resource cannot be resolved to a file in the file system
	 */
	public static List<String> listExtNameFiles(String filePath, String fileExtName) throws FileNotFoundException {
		return FileUtils.listFiles(FileUtils.getFile(filePath), new FilenameExtensionFilter(fileExtName));
	}

	/**
	 * List child files and filter by extension name
	 *
	 * @param filePath           parent file path
	 * @param fileExtName        extension name
	 * @param iterateChildFolder to iterate child folder
	 * @return list of file path
	 * @throws FileNotFoundException if the resource cannot be resolved to a file in the file system
	 */
	public static List<String> listExtNameFiles(String filePath, String fileExtName,
	                                            boolean iterateChildFolder) throws FileNotFoundException {
		return FileUtils.listFiles(FileUtils.getFile(filePath), new FilenameExtensionFilter(fileExtName),
				Boolean.FALSE, Boolean.FALSE, iterateChildFolder);
	}

	/**
	 * List child files and filter by extension name
	 *
	 * @param filePath           parent file path
	 * @param fileExtName        extension name
	 * @param readHiddenFile     the read hidden file
	 * @param iterateChildFolder to iterate child folder
	 * @return list of file path
	 * @throws FileNotFoundException if the resource cannot be resolved to a file in the file system
	 */
	public static List<String> listExtNameFiles(String filePath, String fileExtName, boolean readHiddenFile,
	                                            boolean iterateChildFolder) throws FileNotFoundException {
		return FileUtils.listFiles(FileUtils.getFile(filePath), new FilenameExtensionFilter(fileExtName),
				readHiddenFile, Boolean.FALSE, iterateChildFolder);
	}

	/**
	 * List child files by filter extension name is .class
	 *
	 * @param filePath parent file path
	 * @return list of file path
	 * @throws FileNotFoundException if the resource cannot be resolved to a file in the file system
	 */
	public static List<String> listClassesFiles(String filePath) throws FileNotFoundException {
		return FileUtils.listFiles(FileUtils.getFile(filePath), new FilenameExtensionFilter("class"));
	}

	/**
	 * List child files by filter extension name is .class
	 *
	 * @param file parent file object
	 * @return list of file path
	 */
	public static List<String> listClassesFiles(File file) {
		return FileUtils.listFiles(file, new FilenameExtensionFilter("class"));
	}

	/**
	 * List child files by filter extension name is .jar
	 *
	 * @param filePath parent file path
	 * @return list of file path
	 * @throws FileNotFoundException if the resource cannot be resolved to a file in the file system
	 */
	public static List<String> listJarFiles(String filePath) throws FileNotFoundException {
		return FileUtils.listFiles(FileUtils.getFile(filePath), new FilenameExtensionFilter(URL_PROTOCOL_JAR));
	}

	/**
	 * List child files by filter extension name is .jar
	 *
	 * @param file parent file object
	 * @return list of file path
	 */
	public static List<String> listJarFiles(File file) {
		return FileUtils.listFiles(file, new FilenameExtensionFilter(URL_PROTOCOL_JAR));
	}

	/**
	 * List child files by filter extension name is .zip
	 *
	 * @param filePath parent file path
	 * @return list of file path
	 * @throws FileNotFoundException if the resource cannot be resolved to a file in the file system
	 */
	public static List<String> listZipFiles(String filePath) throws FileNotFoundException {
		return FileUtils.listFiles(FileUtils.getFile(filePath), new FilenameExtensionFilter(URL_PROTOCOL_ZIP));
	}

	/**
	 * List child files by filter extension name is .zip
	 *
	 * @param file parent file object
	 * @return list of file path
	 */
	public static List<String> listZipFiles(File file) {
		return FileUtils.listFiles(file, new FilenameExtensionFilter(URL_PROTOCOL_ZIP));
	}

	/**
	 * List child files by filter extension name is .wsjar
	 *
	 * @param filePath parent file path
	 * @return list of file path
	 * @throws FileNotFoundException if the resource cannot be resolved to a file in the file system
	 */
	public static List<String> listWebSphereJarFiles(String filePath) throws FileNotFoundException {
		return FileUtils.listFiles(FileUtils.getFile(filePath), new FilenameExtensionFilter(URL_PROTOCOL_WSJAR));
	}

	/**
	 * List child files by filter extension name is .wsjar
	 *
	 * @param file parent file object
	 * @return list of file path
	 */
	public static List<String> listWebSphereJarFiles(File file) {
		return FileUtils.listFiles(file, new FilenameExtensionFilter(URL_PROTOCOL_WSJAR));
	}

	/**
	 * List files, filter by file name regex string
	 *
	 * @param filePath      folder path
	 * @param fileNameRegex file name regex
	 * @return list of file path
	 * @throws FileNotFoundException if the resource cannot be resolved to a file in the file system
	 */
	public static List<String> listFilesByRegex(String filePath, String fileNameRegex)
			throws FileNotFoundException {
		return FileUtils.listFilesByRegex(filePath, fileNameRegex, true,
				Boolean.FALSE, true);
	}

	/**
	 * List files, filter by file name regex string
	 *
	 * @param filePath           folder path
	 * @param fileNameRegex      file name regex
	 * @param iterateChildFolder to iterate child folder
	 * @return list of file path
	 * @throws FileNotFoundException if the resource cannot be resolved to a file in the file system
	 */
	public static List<String> listFilesByRegex(String filePath, String fileNameRegex,
	                                            boolean iterateChildFolder) throws FileNotFoundException {
		return FileUtils.listFilesByRegex(filePath, fileNameRegex, true,
				Boolean.FALSE, iterateChildFolder);
	}

	/**
	 * List files, filter by file name regex string
	 *
	 * @param filePath           folder path
	 * @param fileNameRegex      file name regex
	 * @param readHiddenFiles    include hidden file
	 * @param includeRootFolder  include root folder
	 * @param iterateChildFolder to iterate child folder
	 * @return list of file path
	 * @throws FileNotFoundException if the resource cannot be resolved to a file in the file system
	 */
	public static List<String> listFilesByRegex(String filePath, String fileNameRegex,
	                                            boolean readHiddenFiles, boolean includeRootFolder,
	                                            boolean iterateChildFolder) throws FileNotFoundException {
		List<String> fileList = new ArrayList<>();
		FileUtils.listFiles(FileUtils.getFile(filePath), new FilenameRegexFilter(fileNameRegex),
				fileList, readHiddenFiles, includeRootFolder, iterateChildFolder);
		return fileList;
	}

	/**
	 * Write file content to local file path
	 *
	 * @param fileData file content
	 * @param filePath write path
	 * @return <code>Boolean.TRUE</code> for success and <code>Boolean.FALSE</code> for error
	 */
	public static boolean saveFile(final byte[] fileData, final String filePath) {
		return FileUtils.saveFile(fileData, filePath, new Properties());
	}

	/**
	 * Write file content to local file path
	 *
	 * @param fileData   file content
	 * @param filePath   write path
	 * @param properties the properties
	 * @return <code>Boolean.TRUE</code> for success and <code>Boolean.FALSE</code> for error
	 */
	public static boolean saveFile(final byte[] fileData, final String filePath, final Properties properties) {
		if (StringUtils.isEmpty(filePath)) {
			return Boolean.FALSE;
		}

		if (filePath.startsWith(Globals.SAMBA_PROTOCOL)) {
			try (SmbFile smbFile = new SmbFile(filePath, new BaseContext(new PropertyConfiguration(properties)));
			     OutputStream outputStream = new SmbFileOutputStream(smbFile)) {
				smbFile.mkdirs();
				outputStream.write(fileData);
				outputStream.flush();
				return Boolean.TRUE;
			} catch (Exception e) {
				return Boolean.FALSE;
			}
		} else {
			FileOutputStream fileOutputStream = null;
			try {
				File destFile = FileUtils.getFile(filePath);
				File folder = destFile.getParentFile();
				if (folder.exists() || folder.mkdirs()) {
					fileOutputStream = new FileOutputStream(destFile);
					fileOutputStream.write(fileData);
					fileOutputStream.flush();
					return Boolean.TRUE;
				}
			} catch (IOException e) {
				LOGGER.error("Save file to storage error! ");
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Stack trace message: ", e);
				}
			} finally {
				IOUtils.closeStream(fileOutputStream);
			}
			return Boolean.FALSE;
		}
	}

	/**
	 * Write input stream content to file path
	 *
	 * @param inputStream file content by input stream
	 * @param filePath    write to file path
	 * @return <code>Boolean.TRUE</code> for success and <code>Boolean.FALSE</code> for error
	 */
	public static boolean saveFile(InputStream inputStream, String filePath) {
		return FileUtils.saveFile(inputStream, filePath, new Properties());
	}

	/**
	 * Write input stream content to file path
	 *
	 * @param inputStream file content by input stream
	 * @param filePath    write to file path
	 * @param properties  the properties
	 * @return <code>Boolean.TRUE</code> for success and <code>Boolean.FALSE</code> for error
	 */
	public static boolean saveFile(InputStream inputStream, String filePath, final Properties properties) {
		if (StringUtils.isEmpty(filePath)) {
			return Boolean.FALSE;
		}

		if (filePath.startsWith(Globals.SAMBA_PROTOCOL)) {
			try (SmbFile smbFile = new SmbFile(filePath, new BaseContext(new PropertyConfiguration(properties)));
			     OutputStream outputStream = new SmbFileOutputStream(smbFile)) {
				smbFile.mkdirs();
				int readLength;
				byte[] readBuffer = new byte[Globals.DEFAULT_BUFFER_SIZE];
				while ((readLength = inputStream.read(readBuffer, Globals.INITIALIZE_INT_VALUE, Globals.DEFAULT_BUFFER_SIZE)) != Globals.DEFAULT_VALUE_INT) {
					outputStream.write(readBuffer, Globals.INITIALIZE_INT_VALUE, readLength);
				}
				outputStream.flush();
				return Boolean.TRUE;
			} catch (Exception e) {
				return Boolean.FALSE;
			}
		} else {
			FileOutputStream fileOutputStream = null;
			try {
				File destFile = FileUtils.getFile(filePath);
				File folder = destFile.getParentFile();
				if (folder.exists() || folder.mkdirs()) {
					fileOutputStream = new FileOutputStream(destFile);
					int readLength;
					byte[] readBuffer = new byte[Globals.DEFAULT_BUFFER_SIZE];
					while ((readLength = inputStream.read(readBuffer, Globals.INITIALIZE_INT_VALUE, Globals.DEFAULT_BUFFER_SIZE)) != Globals.DEFAULT_VALUE_INT) {
						fileOutputStream.write(readBuffer, 0, readLength);
					}
					fileOutputStream.flush();
					return Boolean.TRUE;
				}
			} catch (IOException e) {
				LOGGER.error("Save file to storage error! ");
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Stack trace message: ", e);
				}
			} finally {
				IOUtils.closeStream(fileOutputStream);
			}
			return Boolean.FALSE;
		}
	}

	/**
	 * Save String to File use default charset: UTF-8
	 *
	 * @param filePath write to file path
	 * @param content  File content
	 * @return Save result
	 */
	public static boolean saveFile(final String filePath, final String content) {
		return FileUtils.saveFile(filePath, new Properties(), content, Globals.DEFAULT_ENCODING);
	}

	/**
	 * Save String to File use default charset: UTF-8
	 *
	 * @param filePath   write to file path
	 * @param properties the properties
	 * @param content    File content
	 * @return Save result
	 */
	public static boolean saveFile(final String filePath, final Properties properties, final String content) {
		return FileUtils.saveFile(filePath, properties, content, Globals.DEFAULT_ENCODING);
	}

	/**
	 * Save String to File
	 *
	 * @param filePath   write to file path
	 * @param properties the properties
	 * @param content    File content
	 * @param encoding   Charset encoding
	 * @return Save result
	 */
	public static boolean saveFile(final String filePath, final Properties properties,
	                               final String content, final String encoding) {
		PrintWriter printWriter = null;
		OutputStream outputStream = null;
		OutputStreamWriter outputStreamWriter = null;
		SmbFile smbFile = null;
		try {
			if (filePath.startsWith(Globals.SAMBA_PROTOCOL)) {
				smbFile = new SmbFile(filePath, new BaseContext(new PropertyConfiguration(properties)));
				outputStream = new SmbFileOutputStream(smbFile);
			} else {
				outputStream = new FileOutputStream(filePath);
			}
			outputStreamWriter = new OutputStreamWriter(outputStream, encoding);
			printWriter = new PrintWriter(outputStreamWriter);

			printWriter.print(content);
			outputStreamWriter.flush();
			return Boolean.TRUE;
		} catch (Exception e) {
			return Boolean.FALSE;
		} finally {
			IOUtils.closeStream(printWriter);
			IOUtils.closeStream(outputStreamWriter);
			IOUtils.closeStream(outputStream);
			if (smbFile != null) {
				smbFile.close();
			}
		}
	}

	/**
	 * Read File to String use default charset: UTF-8
	 *
	 * @param filePath File path
	 * @return File content as string
	 */
	public static String readFile(String filePath) {
		return FileUtils.readFile(filePath, Globals.DEFAULT_ENCODING);
	}

	/**
	 * Read File to String
	 *
	 * @param filePath File path
	 * @param encoding Charset encoding
	 * @return File content as string
	 */
	public static String readFile(String filePath, String encoding) {
		try {
			return IOUtils.readContent(getURL(filePath).openStream(), encoding);
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Remove File by current file path
	 *
	 * @param filePath File path
	 * @return Remove result
	 */
	public static boolean removeFile(String filePath) {
		try {
			return FileUtils.removeFile(FileUtils.getFile(filePath));
		} catch (FileNotFoundException e) {
			return Boolean.TRUE;
		}
	}

	/**
	 * Remove file boolean.
	 *
	 * @param filePath the file path
	 * @param domain   the domain
	 * @param userName the username
	 * @param passWord the password
	 * @return the boolean
	 */
	public static boolean removeFile(final String filePath, final String domain,
	                                 final String userName, final String passWord) {
		return FileUtils.removeFile(FileUtils.getFile(filePath, smbAuthenticator(domain, userName, passWord)));
	}

	/**
	 * Remove File by current file object
	 *
	 * @param file File instance
	 * @return Remove result
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
	 *
	 * @param smbFile SMB file instance
	 * @return Remove result
	 */
	public static boolean removeFile(SmbFile smbFile) {
		if (smbFile == null) {
			return Boolean.TRUE;
		}

		try {
			if (smbFile.exists()) {
				if (smbFile.isDirectory()) {
					FileUtils.removeDir(smbFile);
				} else {
					smbFile.delete();
				}
			}
			return Boolean.TRUE;
		} catch (Exception e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Remove smb file error! ", e);
			}
			return Boolean.FALSE;
		}
	}

	/**
	 * Move file from basePath to moveToPath
	 *
	 * @param basePath   Original path
	 * @param moveToPath Target path
	 * @return Move result
	 */
	public static boolean moveFile(final String basePath, final String moveToPath) {
		return FileUtils.moveFile(basePath, moveToPath, Boolean.FALSE);
	}

	/**
	 * Move file from basePath to moveToPath
	 *
	 * @param basePath   Original path
	 * @param moveToPath Target path
	 * @param override   the override
	 * @return Move result
	 */
	public static boolean moveFile(final String basePath, final String moveToPath, final boolean override) {
		return FileUtils.moveFile(basePath, null, moveToPath, null, override);
	}

	/**
	 * Move file from basePath to moveToPath
	 *
	 * @param basePath        Original path
	 * @param originalContext the original context
	 * @param moveToPath      Target path
	 * @return Move result
	 */
	public static boolean moveFile(final String basePath, final CIFSContext originalContext, final String moveToPath) {
		return FileUtils.moveFile(basePath, originalContext, moveToPath, null, Boolean.FALSE);
	}

	/**
	 * Move file from basePath to moveToPath
	 *
	 * @param basePath        Original path
	 * @param originalContext the original context
	 * @param moveToPath      Target path
	 * @param override        the override
	 * @return Move result
	 */
	public static boolean moveFile(final String basePath, final CIFSContext originalContext,
	                               final String moveToPath, final boolean override) {
		return FileUtils.moveFile(basePath, originalContext, moveToPath, null, override);
	}

	/**
	 * Move file from basePath to moveToPath
	 *
	 * @param basePath      Original path
	 * @param moveToPath    Target path
	 * @param targetContext the target context
	 * @return Move result
	 */
	public static boolean moveFile(final String basePath, final String moveToPath, final CIFSContext targetContext) {
		return FileUtils.moveFile(basePath, null, moveToPath, targetContext, Boolean.FALSE);
	}

	/**
	 * Move file from basePath to moveToPath
	 *
	 * @param basePath      Original path
	 * @param moveToPath    Target path
	 * @param targetContext the target context
	 * @param override      the override
	 * @return Move result
	 */
	public static boolean moveFile(final String basePath, final String moveToPath,
	                               final CIFSContext targetContext, final boolean override) {
		return FileUtils.moveFile(basePath, null, moveToPath, targetContext, override);
	}

	/**
	 * Move file from basePath to moveToPath
	 *
	 * @param originalPath    the original path
	 * @param originalContext the original context
	 * @param targetPath      the target path
	 * @param targetContext   the target context
	 * @return Move result
	 */
	public static boolean moveFile(final String originalPath, final CIFSContext originalContext,
	                               final String targetPath, final CIFSContext targetContext) {
		return FileUtils.moveFile(originalPath, originalContext, targetPath, targetContext, Boolean.FALSE);
	}

	/**
	 * Move file from basePath to moveToPath
	 *
	 * @param originalPath    the original path
	 * @param originalContext the original context
	 * @param targetPath      the target path
	 * @param targetContext   the target context
	 * @param override        Override target file if exists
	 * @return Operate result
	 */
	public static boolean moveFile(final String originalPath, final CIFSContext originalContext,
	                               final String targetPath, final CIFSContext targetContext,
	                               boolean override) {
		if (FileUtils.isExists(originalPath) && FileUtils.canRead(originalPath)) {
			if (override || !FileUtils.isExists(targetPath)) {
				try {
					File destFile = FileUtils.getFile(targetPath);
					if (destFile.exists()) {
						if (override && !FileUtils.removeFile(destFile)) {
							return Boolean.FALSE;
						}
					}

					return FileUtils.copy(originalPath, originalContext, targetPath, targetContext, override)
							&& FileUtils.removeFile(originalPath);
				} catch (Exception e) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Move file error! ", e);
					}
				}
			}
		}
		return Boolean.FALSE;
	}

	private static boolean processFile(final Object originalFile, final Object targetFile, final boolean override) {
		if (originalFile == null || targetFile == null) {
			return Boolean.FALSE;
		}

		try {
			if (targetFile instanceof SmbFile) {
				if (!override && ((SmbFile) targetFile).exists()) {
					return Boolean.FALSE;
				}
				try (InputStream inputStream = (originalFile instanceof SmbFile)
						? new SmbFileInputStream((SmbFile) originalFile) : new FileInputStream((File) originalFile);
				     OutputStream outputStream = new SmbFileOutputStream((SmbFile) targetFile)) {
					int readLength;
					byte[] readBuffer = new byte[Globals.DEFAULT_BUFFER_SIZE];

					while ((readLength = inputStream.read(readBuffer)) != -1) {
						outputStream.write(readBuffer, Globals.INITIALIZE_INT_VALUE, readLength);
					}
					return Boolean.TRUE;
				} catch (Exception e) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Copy file error! ", e);
					}
					return Boolean.FALSE;
				}
			} else {
				if (!override && ((File) targetFile).exists()) {
					return Boolean.FALSE;
				}
				try (InputStream inputStream = (originalFile instanceof SmbFile)
						? new SmbFileInputStream((SmbFile) originalFile) : new FileInputStream((File) originalFile);
				     OutputStream outputStream = new FileOutputStream((File) targetFile)) {
					int readLength;
					byte[] readBuffer = new byte[Globals.DEFAULT_BUFFER_SIZE];

					while ((readLength = inputStream.read(readBuffer)) != -1) {
						outputStream.write(readBuffer, Globals.INITIALIZE_INT_VALUE, readLength);
					}
					return Boolean.TRUE;
				} catch (Exception e) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Copy file error! ", e);
					}
					return Boolean.FALSE;
				}
			}
		} catch (Exception e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Move file error! ", e);
			}
			return Boolean.FALSE;
		}
	}

	/**
	 * Move directory from basePath to moveToPath and ignore exists file
	 *
	 * @param originalPath    the original path
	 * @param originalContext the original context
	 * @param targetPath      the target path
	 * @return Move result
	 */
	public static boolean moveDir(final String originalPath, final CIFSContext originalContext,
	                              final String targetPath) {
		return FileUtils.moveDir(originalPath, originalContext, targetPath, null, Boolean.FALSE);
	}

	/**
	 * Move directory from basePath to moveToPath and ignore exists file
	 *
	 * @param originalPath    the original path
	 * @param originalContext the original context
	 * @param targetPath      the target path
	 * @param override        the override
	 * @return Move result
	 */
	public static boolean moveDir(final String originalPath, final CIFSContext originalContext,
	                              final String targetPath, final boolean override) {
		return FileUtils.moveDir(originalPath, originalContext, targetPath, null, override);
	}

	/**
	 * Move directory from basePath to moveToPath and ignore exists file
	 *
	 * @param originalPath  the original path
	 * @param targetPath    the target path
	 * @param targetContext the target context
	 * @return Move result
	 */
	public static boolean moveDir(final String originalPath, final String targetPath, final CIFSContext targetContext) {
		return FileUtils.moveDir(originalPath, null, targetPath, targetContext, Boolean.FALSE);
	}

	/**
	 * Move directory from basePath to moveToPath and ignore exists file
	 *
	 * @param originalPath  the original path
	 * @param targetPath    the target path
	 * @param targetContext the target context
	 * @param override      the override
	 * @return Move result
	 */
	public static boolean moveDir(final String originalPath, final String targetPath,
	                              final CIFSContext targetContext, final boolean override) {
		return FileUtils.moveDir(originalPath, null, targetPath, targetContext, override);
	}

	/**
	 * Move directory from basePath to moveToPath and ignore exists file
	 *
	 * @param originalPath    the original path
	 * @param originalContext the original context
	 * @param targetPath      the target path
	 * @param targetContext   the target context
	 * @return Move result
	 */
	public static boolean moveDir(final String originalPath, final CIFSContext originalContext,
	                              final String targetPath, final CIFSContext targetContext) {
		return FileUtils.moveDir(originalPath, originalContext, targetPath, targetContext, Boolean.FALSE);
	}

	/**
	 * Move directory from basePath to moveToPath and override by user defined
	 *
	 * @param originalPath    the original path
	 * @param originalContext the original context
	 * @param targetPath      the target path
	 * @param targetContext   the target context
	 * @param override        Override target file if it's exists
	 * @return Move result
	 */
	public static boolean moveDir(final String originalPath, final CIFSContext originalContext,
	                              final String targetPath, final CIFSContext targetContext,
	                              final boolean override) {
		if (StringUtils.isEmpty(originalPath) || !FileUtils.isDirectory(originalPath, originalContext)
				|| StringUtils.isEmpty(targetPath)) {
			return Boolean.FALSE;
		}
		if (FileUtils.copy(originalPath, originalContext, targetPath, targetContext, override)) {
			return FileUtils.removeDir(originalPath, originalContext);
		}
		return Boolean.FALSE;
	}

	/**
	 * Move directory from baseFile object to moveToPath and ignore exists file
	 *
	 * @param baseFile   Original file instance
	 * @param moveToPath Target directory
	 * @param properties the properties
	 * @return Move result
	 */
	public static boolean moveDir(final File baseFile, final String moveToPath, final Properties properties) {
		return FileUtils.moveDir(baseFile, moveToPath, properties, Boolean.FALSE);
	}

	private static boolean processDirectory(final Object originalDirectory, final Object targetDirectory,
	                                        final boolean override) {
		if (originalDirectory == null || targetDirectory == null) {
			return Boolean.FALSE;
		}

		try {
			String targetBasePath;
			CIFSContext cifsContext = null;
			if (targetDirectory instanceof SmbFile) {
				((SmbFile) targetDirectory).mkdirs();
				targetBasePath = ((SmbFile) targetDirectory).getPath();
				cifsContext = ((SmbFile) targetDirectory).getContext();
			} else {
				if (((File) targetDirectory).exists() || ((File) targetDirectory).mkdirs()) {
					targetBasePath = ((File) targetDirectory).getAbsolutePath();
				} else {
					return Boolean.FALSE;
				}
			}

			boolean processResult = Boolean.TRUE;
			if (originalDirectory instanceof SmbFile) {
				SmbFile[] childFiles = ((SmbFile) originalDirectory).listFiles();
				for (SmbFile tempFile : childFiles) {
					String childPath = targetBasePath + Globals.DEFAULT_PAGE_SEPARATOR + tempFile.getName();
					Object childFile;
					if (targetDirectory instanceof SmbFile) {
						childFile = new SmbFile(childPath, cifsContext);
					} else {
						childFile = FileUtils.getFile(childPath);
					}
					if (tempFile.isDirectory()) {
						processResult &= FileUtils.processDirectory(tempFile, childFile, override);
					} else if (tempFile.isFile()) {
						processResult &= FileUtils.processFile(tempFile, childFile, override);
					}
				}
			} else {
				File[] childFiles = ((File) originalDirectory).listFiles();
				if (childFiles != null) {
					for (File tempFile : childFiles) {
						BasicFileAttributes basicFileAttributes =
								Files.readAttributes(tempFile.toPath(), BasicFileAttributes.class);
						String childPath = targetBasePath + Globals.DEFAULT_PAGE_SEPARATOR + tempFile.getName();
						Object childFile;
						if (targetDirectory instanceof SmbFile) {
							childFile = new SmbFile(childPath, cifsContext);
						} else {
							childFile = FileUtils.getFile(childPath);
						}
						if (basicFileAttributes.isDirectory()) {
							processResult &= FileUtils.processDirectory(tempFile, childFile, override);
						} else if (basicFileAttributes.isRegularFile()) {
							processResult &= FileUtils.processFile(tempFile, childFile, override);
						}
					}
				}
			}
			return processResult;
		} catch (Exception e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Move directory error! ", e);
			}
			return Boolean.FALSE;
		}
	}

	/**
	 * Move dir from baseFile object to moveToPath and override by user defined
	 *
	 * @param baseFile   Original file path
	 * @param moveToPath Target path
	 * @param properties the properties
	 * @param override   Override target file
	 * @return Move result
	 */
	public static boolean moveDir(final File baseFile, final String moveToPath,
	                              final Properties properties, final boolean override) {
		if (baseFile == null || !baseFile.exists()) {
			return Boolean.FALSE;
		}
		try {
			FileUtils.makeDir(moveToPath, properties);

			boolean error = Boolean.FALSE;
			BasicFileAttributes basicFileAttributes =
					Files.readAttributes(baseFile.toPath(), BasicFileAttributes.class);
			if (basicFileAttributes.isDirectory()) {
				File[] childFiles = baseFile.listFiles();
				if (childFiles != null) {
					for (File tempFile : childFiles) {
						String childPath = moveToPath + Globals.DEFAULT_PAGE_SEPARATOR + tempFile.getName();
						BasicFileAttributes fileAttributes =
								Files.readAttributes(tempFile.toPath(), BasicFileAttributes.class);
						if (fileAttributes.isDirectory()) {
							error = FileUtils.moveDir(tempFile, childPath, properties, override);
							removeFile(tempFile);
						} else if (fileAttributes.isRegularFile()) {
							error = FileUtils.moveFile(tempFile.getAbsolutePath(), childPath, override);
						}

						if (!error) {
							return Boolean.FALSE;
						}
					}
				}
				return true;
			} else if (basicFileAttributes.isRegularFile()) {
				return FileUtils.moveFile(baseFile.getAbsolutePath(),
						moveToPath + Globals.DEFAULT_PAGE_SEPARATOR + baseFile.getName(), override);
			} else {
				return Boolean.FALSE;
			}
		} catch (Exception e) {
			return Boolean.FALSE;
		}
	}

	/**
	 * Make directory
	 *
	 * @param destPath Target directory path
	 * @return Operate result
	 */
	public static boolean makeDir(final String destPath) {
		return FileUtils.makeDir(destPath, new Properties());
	}

	/**
	 * Make directory
	 *
	 * @param destPath   Target directory path
	 * @param properties the properties
	 * @return Operate result
	 */
	public static boolean makeDir(final String destPath, final Properties properties) {
		if (FileUtils.isExists(destPath)) {
			return true;
		}

		if (destPath.startsWith(Globals.SAMBA_PROTOCOL)) {
			try (SmbFile smbFile = new SmbFile(destPath,
					new BaseContext(new PropertyConfiguration(properties == null ? new Properties() : properties)))) {
				smbFile.mkdirs();
				return Boolean.TRUE;
			} catch (Exception e) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Make smb file directories error! ", e);
				}
				return Boolean.FALSE;
			}
		} else {
			try {
				File destDir = FileUtils.getFile(destPath);
				return destDir.mkdirs();
			} catch (FileNotFoundException e) {
				return Boolean.FALSE;
			}
		}
	}

	/**
	 * Makes a directory, including any necessary but nonexistent parent
	 * directories. If a file already exists with specified name, but it is
	 * not a directory then an IOException is thrown.
	 * If the directory cannot be created (or the file already exists but is not a directory)
	 * then an IOException is thrown.
	 *
	 * @param directory directory to create, must not be {@code null}
	 * @throws IOException if the directory cannot be created or the file already exists but is not a directory
	 */
	public static void forceMakeDir(final File directory) throws IOException {
		if (directory == null) {
			return;
		}
		if (directory.exists()) {
			if (!directory.isDirectory()) {
				throw new IOException("File " + directory + " was exists and not a directory.");
			}
		} else {
			if (!directory.mkdirs() && !directory.isDirectory()) {
				throw new IOException("Unable to create directory" + directory);
			}
		}
	}

	/**
	 * Makes any necessary but nonexistent parent directories for a given File. If the parent directory cannot be
	 * created then an IOException is thrown.
	 *
	 * @param file file with parent to create
	 * @throws IOException if the parent directory cannot be created
	 */
	public static void forceMakeParent(final File file) throws IOException {
		if (file == null) {
			return;
		}
		FileUtils.forceMakeDir(file.getParentFile());
	}

	/**
	 * Check filePath is exists
	 *
	 * @param resourceLocation Resource location
	 * @return Check result
	 */
	public static boolean isDirectory(final String resourceLocation) {
		return FileUtils.isDirectory(resourceLocation, null);
	}

	/**
	 * Check filePath is exists
	 *
	 * @param resourceLocation Resource location
	 * @param cifsContext      the cifs context
	 * @return Check result
	 */
	public static boolean isDirectory(final String resourceLocation, final CIFSContext cifsContext) {
		if (StringUtils.isEmpty(resourceLocation)) {
			return Boolean.FALSE;
		}

		if (resourceLocation.startsWith(Globals.SAMBA_PROTOCOL)) {
			try (SmbFile smbFile = new SmbFile(resourceLocation, cifsContext)) {
				return smbFile.isDirectory();
			} catch (Exception e) {
				return Boolean.FALSE;
			}
		} else {
			try {
				File directory = FileUtils.getFile(resourceLocation);
				return (directory.exists() && directory.isDirectory());
			} catch (Exception e) {
				return Boolean.FALSE;
			}
		}
	}

	/**
	 * Copy directory from baseDir to destDir
	 *
	 * @param originalPath the original path
	 * @param targetPath   the target path
	 * @return Operate result
	 */
	public static boolean copy(final String originalPath, final String targetPath) {
		return FileUtils.copy(originalPath, null, targetPath, null, Boolean.FALSE);
	}

	/**
	 * Copy directory from baseDir to destDir
	 *
	 * @param originalPath the original path
	 * @param targetPath   the target path
	 * @param override     the override
	 * @return Operate result
	 */
	public static boolean copy(final String originalPath, final String targetPath, final boolean override) {
		return FileUtils.copy(originalPath, null, targetPath, null, override);
	}

	/**
	 * Copy directory from baseDir to destDir
	 *
	 * @param originalPath the original path
	 * @param targetPath   the target path
	 * @param cifsContext  the cifs context
	 * @return Operate result
	 */
	public static boolean copy(final String originalPath, final String targetPath, final CIFSContext cifsContext) {
		return FileUtils.copy(originalPath, null, targetPath, cifsContext, Boolean.FALSE);
	}

	/**
	 * Copy directory from baseDir to destDir
	 *
	 * @param originalPath  the original path
	 * @param targetPath    the target path
	 * @param targetContext the target context
	 * @param override      the override
	 * @return Operate result
	 */
	public static boolean copy(final String originalPath, final String targetPath,
	                           final CIFSContext targetContext, final boolean override) {
		return FileUtils.copy(originalPath, null, targetPath, targetContext, override);
	}

	/**
	 * Copy directory from baseDir to destDir
	 *
	 * @param originalPath    the original path
	 * @param originalContext the original context
	 * @param targetPath      the target path
	 * @return Operate result
	 */
	public static boolean copy(final String originalPath, final CIFSContext originalContext, final String targetPath) {
		return FileUtils.copy(originalPath, originalContext, targetPath, null, Boolean.FALSE);
	}

	/**
	 * Copy directory from baseDir to destDir
	 *
	 * @param originalPath    the original path
	 * @param originalContext the original context
	 * @param targetPath      the target path
	 * @param override        the override
	 * @return Operate result
	 */
	public static boolean copy(final String originalPath, final CIFSContext originalContext,
	                           final String targetPath, final boolean override) {
		return FileUtils.copy(originalPath, originalContext, targetPath, null, override);
	}

	/**
	 * Copy directory from baseDir to destDir
	 *
	 * @param originalPath    the original path
	 * @param originalContext the original context
	 * @param targetPath      the target path
	 * @param targetContext   the target context
	 * @return Operate result
	 */
	public static boolean copy(final String originalPath, final CIFSContext originalContext,
	                           final String targetPath, final CIFSContext targetContext) {
		return FileUtils.copy(originalPath, originalContext, targetPath, targetContext, Boolean.FALSE);
	}

	/**
	 * Copy directory from baseDir to destDir
	 *
	 * @param originalPath    the original path
	 * @param originalContext the original context
	 * @param targetPath      the target path
	 * @param targetContext   the target context
	 * @param override        the override
	 * @return Operate result
	 */
	public static boolean copy(final String originalPath, final CIFSContext originalContext,
	                           final String targetPath, final CIFSContext targetContext,
	                           final boolean override) {
		if (StringUtils.isEmpty(originalPath) || StringUtils.isEmpty(targetPath)) {
			return Boolean.FALSE;
		}

		Object original = null;
		Object target = null;

		try {
			boolean directory;
			if (originalPath.startsWith(Globals.SAMBA_PROTOCOL)) {
				original = FileUtils.getFile(originalPath, originalContext);
				if (original == null) {
					return Boolean.FALSE;
				}
				directory = ((SmbFile) original).isDirectory();
			} else {
				original = FileUtils.getFile(originalPath);
				directory = ((File) original).isDirectory();
			}
			if (targetPath.startsWith(Globals.SAMBA_PROTOCOL)) {
				target = FileUtils.getFile(targetPath, targetContext);
			} else {
				target = FileUtils.getFile(targetPath);
			}
			if (directory) {
				return FileUtils.processDirectory(original, target, override);
			} else {
				return FileUtils.processFile(original, target, override);
			}
		} catch (Exception e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Copy directory data error! ", e);
			}
			return Boolean.FALSE;
		} finally {
			if (original instanceof SmbFile) {
				((SmbFile) original).close();
			}
			if (target instanceof SmbFile) {
				((SmbFile) target).close();
			}
		}
	}

	/**
	 * Remove dir boolean.
	 *
	 * @param directoryPath the directory path
	 * @return the boolean
	 */
	public static boolean removeDir(final String directoryPath) {
		return FileUtils.removeDir(directoryPath, null);
	}

	/**
	 * Remove dir boolean.
	 *
	 * @param directoryPath the directory path
	 * @param cifsContext   the cifs context
	 * @return the boolean
	 */
	public static boolean removeDir(final String directoryPath, final CIFSContext cifsContext) {
		if (directoryPath.startsWith(Globals.SAMBA_PROTOCOL)) {
			return FileUtils.removeDir(FileUtils.getFile(directoryPath, cifsContext));
		} else {
			try {
				return FileUtils.removeDir(FileUtils.getFile(directoryPath));
			} catch (Exception e) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Remove original directory error! ", e);
				}
				return Boolean.FALSE;
			}
		}
	}

	/**
	 * Remove dir boolean.
	 *
	 * @param directory the directory
	 * @return the boolean
	 */
	private static boolean removeDir(final Object directory) {
		if (directory == null) {
			return Boolean.FALSE;
		}

		try {
			final boolean smbFile;
			final CIFSContext cifsContext;
			final String basePath;
			String[] fileList;
			if (directory instanceof SmbFile) {
				fileList = ((SmbFile) directory).list();
				smbFile = Boolean.TRUE;
				cifsContext = ((SmbFile) directory).getContext();
				basePath = ((SmbFile) directory).getPath();
			} else {
				fileList = ((File) directory).list();
				smbFile = Boolean.FALSE;
				cifsContext = null;
				basePath = ((File) directory).getAbsolutePath();
			}
			if (fileList != null) {
				for (String filePath : fileList) {
					Object childFile;
					boolean isDirectory;
					if (smbFile) {
						childFile = new SmbFile(basePath + "/" + filePath, cifsContext);
						isDirectory = ((SmbFile) childFile).isDirectory();
					} else {
						childFile = new File(basePath, filePath);
						isDirectory = ((File) childFile).isDirectory();
					}
					if (isDirectory) {
						if (!FileUtils.removeDir(childFile)) {
							return Boolean.FALSE;
						}
					} else {
						if (smbFile) {
							((SmbFile) childFile).delete();
						} else {
							if (!((File) childFile).delete()) {
								return Boolean.FALSE;
							}
						}
					}
				}
			}
			if (directory instanceof SmbFile) {
				((SmbFile) directory).delete();
				return Boolean.TRUE;
			} else {
				return ((File) directory).delete();
			}
		} catch (Exception e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Remove directory error! ", e);
			}
			return Boolean.FALSE;
		}
	}

	/**
	 * Calculate file CRC value
	 *
	 * @param filePath file path
	 * @return CRC value
	 */
	public static long calcFileCRC(String filePath) {
		InputStream inputStream = null;
		try {
			inputStream = FileUtils.loadFile(filePath);
			if (inputStream != null) {
				byte[] readBuffer = new byte[Globals.DEFAULT_BUFFER_SIZE];
				int readLength;
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
			IOUtils.closeStream(inputStream);
		}

		return Globals.DEFAULT_VALUE_LONG;
	}

	/**
	 * Check current file is exists
	 *
	 * @param filePath File path
	 * @return Check result
	 */
	public static boolean isExists(String filePath) {
		return isExists(filePath, null);
	}

	/**
	 * Check current file is exists
	 *
	 * @param filePath                  File path
	 * @param ntlmPasswordAuthenticator the ntlm password authenticator
	 * @return Check result
	 */
	public static boolean isExists(final String filePath, final NtlmPasswordAuthenticator ntlmPasswordAuthenticator) {
		return FileUtils.isExists(filePath, null, ntlmPasswordAuthenticator);
	}

	/**
	 * Check current file is exists
	 *
	 * @param filePath                  File path
	 * @param properties                the properties
	 * @param ntlmPasswordAuthenticator the ntlm password authenticator
	 * @return Check result
	 */
	public static boolean isExists(final String filePath, final Properties properties,
	                               final NtlmPasswordAuthenticator ntlmPasswordAuthenticator) {
		if (StringUtils.isEmpty(filePath)) {
			return Boolean.FALSE;
		}

		if (filePath.startsWith(Globals.SAMBA_PROTOCOL)) {
			try (SmbFile smbFile = new SmbFile(filePath, generateContext(properties, ntlmPasswordAuthenticator))) {
				return smbFile.exists();
			} catch (Exception e) {
				return Boolean.FALSE;
			}
		} else {
			try {
				File file = FileUtils.getFile(filePath);
				return file.exists();
			} catch (FileNotFoundException e) {
				return Boolean.FALSE;
			}
		}
	}

	/**
	 * Read entry length
	 *
	 * @param filePath  Zip/jar file path
	 * @param entryPath Check entry path
	 * @return Entry length
	 */
	public static int readEntryLength(String filePath, String entryPath) {
		InputStream inputStream = null;
		JarFile jarFile = null;
		try {
			if (filePath.endsWith(URL_PROTOCOL_JAR)) {
				jarFile = new JarFile(getFile(filePath));
				JarEntry packageEntry = jarFile.getJarEntry(entryPath);

				if(packageEntry != null){
					inputStream = jarFile.getInputStream(jarFile.getJarEntry(entryPath));
					return inputStream.available();
				}
			} else if (filePath.endsWith(URL_PROTOCOL_ZIP)) {
				ZipFile zipFile = ZipFile.openZipFile(filePath);
				return zipFile.readEntryLength(entryPath);
			}
		} catch (Exception e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Read entry length error! ", e);
			}
		} finally {
			IOUtils.closeStream(inputStream);
			IOUtils.closeStream(jarFile);
		}
		return Globals.DEFAULT_VALUE_INT;
	}

	/**
	 * Check given entry path is exists in zip/jar file
	 *
	 * @param filePath  Zip/jar file path
	 * @param entryPath Check entry path
	 * @return Check result
	 */
	public static boolean isEntryExists(String filePath, String entryPath) {
		if (StringUtils.isEmpty(filePath) || StringUtils.isEmpty(entryPath)) {
			return Boolean.FALSE;
		}

		if (filePath.toLowerCase().endsWith(URL_PROTOCOL_JAR)) {
			JarFile jarFile = null;
			try {
				jarFile = new JarFile(getFile(filePath));
				return jarFile.getJarEntry(entryPath) != null;
			} catch (Exception e) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Load jar entry content error! ", e);
				}
			} finally {
				if (jarFile != null) {
					try {
						jarFile.close();
					} catch (Exception e) {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Close jar file error! ", e);
						}
					}
				}
			}
		} else if (filePath.toLowerCase().endsWith(URL_PROTOCOL_ZIP)) {
			ZipFile zipFile = ZipFile.openZipFile(filePath);
			return zipFile.isEntryExists(entryPath);
		}
		return Boolean.FALSE;
	}

	/**
	 * Entry input stream.
	 *
	 * @param fileObject the file object
	 * @param entryPath  the entry path
	 * @return input stream
	 * @throws IOException the io exception
	 */
	public static InputStream openInputStream(Object fileObject, String entryPath) throws IOException {
		if (fileObject == null || StringUtils.isEmpty(entryPath)) {
			return null;
		}

		if (fileObject instanceof JarFile) {
			JarEntry jarEntry = ((JarFile) fileObject).getJarEntry(entryPath);
			if(jarEntry != null){
				return ((JarFile) fileObject).getInputStream(jarEntry);
			}
			return null;
		}

		if (fileObject instanceof ZipFile) {
			return ((ZipFile) fileObject).entryInputStream(entryPath);
		}

		return null;
	}

	/**
	 * Check current file can read
	 *
	 * @param filePath File path
	 * @return Check result
	 */
	public static boolean canRead(String filePath) {
		return canRead(filePath, null, null, null);
	}

	/**
	 * Check current file can read
	 *
	 * @param filePath File path
	 * @param domain   SMB domain
	 * @param userName SMB user name
	 * @param passWord SMB password
	 * @return Check result
	 */
	public static boolean canRead(String filePath, String domain, String userName, String passWord) {
		if (StringUtils.isEmpty(filePath)) {
			return Boolean.FALSE;
		}

		if (filePath.startsWith(Globals.SAMBA_PROTOCOL)) {
			try (SmbFile smbFile = getFile(filePath, smbAuthenticator(domain, userName, passWord))) {
				return smbFile != null && smbFile.canRead();
			} catch (Exception e) {
				return Boolean.FALSE;
			}
		} else {
			try {
				File file = FileUtils.getFile(filePath);
				return file.canRead();
			} catch (FileNotFoundException e) {
				return Boolean.FALSE;
			}
		}
	}

	/**
	 * Check current file can write
	 *
	 * @param path File path
	 * @return Check result
	 */
	public static boolean canWrite(String path) {
		return canWrite(path, null, null, null);
	}

	/**
	 * Check current file can write
	 *
	 * @param filePath the file path
	 * @param domain   SMB domain
	 * @param userName SMB user name
	 * @param passWord SMB password
	 * @return Check result
	 */
	public static boolean canWrite(String filePath, String domain, String userName, String passWord) {
		if (StringUtils.isEmpty(filePath)) {
			return Boolean.FALSE;
		}

		if (filePath.startsWith(Globals.SAMBA_PROTOCOL)) {
			try (SmbFile smbFile = getFile(filePath, smbAuthenticator(domain, userName, passWord))) {
				return smbFile == null || !smbFile.exists() || smbFile.canWrite();
			} catch (Exception e) {
				return Boolean.FALSE;
			}
		} else {
			try {
				File file = FileUtils.getFile(filePath);
				return file.canWrite();
			} catch (FileNotFoundException e) {
				return Boolean.FALSE;
			}
		}
	}

	/**
	 * Check current file can execute
	 *
	 * @param filePath File path
	 * @return Check result
	 */
	public static boolean canExecute(String filePath) {
		try {
			File file = FileUtils.getFile(filePath);
			return file.canExecute();
		} catch (FileNotFoundException e) {
			return Boolean.FALSE;
		}
	}

	/**
	 * Merge file to save path
	 *
	 * @param savePath         Target save path
	 * @param segmentationFile Segmentation file object
	 * @return Operate result
	 */
	public static boolean mergeFile(String savePath, SegmentationFile segmentationFile) {
		try (RandomAccessFile randomAccessFile = new RandomAccessFile(savePath, "rw")) {
			String extName = StringUtils.getFilenameExtension(savePath);
			if (extName.length() == 0) {
				extName = Globals.DEFAULT_VALUE_STRING;
			}
			if (!segmentationFile.getExtName().equalsIgnoreCase(extName)) {
				LOGGER.warn("File extension name not match");
			}

			long totalSize = 0;
			randomAccessFile.setLength(segmentationFile.getTotalSize());

			for (SegmentationItem segmentationItem : segmentationFile.getSegmentationItemList()) {
				if (segmentationItem == null) {
					return Boolean.FALSE;
				}

				if (FileUtils.mergeFile(randomAccessFile, segmentationItem)) {
					totalSize += segmentationItem.getBlockSize();
				}
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Write file size: " + totalSize);
			}

			if (totalSize != segmentationFile.getTotalSize()) {
				FileUtils.removeFile(savePath);
				return Boolean.FALSE;
			}
			return Boolean.TRUE;
		} catch (Exception e) {
			return Boolean.FALSE;
		}
	}

	/**
	 * Segment file by current block size
	 *
	 * @param filePath  Which file will be split
	 * @param blockSize Block size
	 * @return List of split file
	 */
	public static SegmentationFile segmentFile(String filePath, int blockSize) {
		return segmentFile(filePath, blockSize, null, null, null);
	}

	/**
	 * Segment file by current block size
	 *
	 * @param filePath  Which file will be split
	 * @param blockSize Block size
	 * @param domain    SMB domain
	 * @param userName  SMB user name
	 * @param passWord  SMB password
	 * @return List of split file
	 */
	public static SegmentationFile segmentFile(final String filePath, final int blockSize,
											   final String domain, final String userName, final String passWord) {
		if (!FileUtils.isExists(filePath, smbAuthenticator(domain, userName, passWord))) {
			return null;
		}

		List<SegmentationItem> segmentationItemList = new ArrayList<>();
		InputStream fileInputStream = null;
		ByteArrayOutputStream byteArrayOutputStream;

		try {
			String extName = StringUtils.getFilenameExtension(filePath);
			if (extName.length() == 0) {
				extName = Globals.DEFAULT_VALUE_STRING;
			} else {
				extName = extName.toLowerCase();
			}
			Object fileObject;
			if (filePath.startsWith(Globals.SAMBA_PROTOCOL)) {
				fileObject = new SmbFile(filePath, generateContext(smbAuthenticator(domain, userName, passWord)));
				fileInputStream = new SmbFileInputStream((SmbFile) fileObject);
			} else {
				fileObject = getFile(filePath);
				fileInputStream = new FileInputStream((File) fileObject);
			}
			long fileSize = fileSize(fileObject);

			byte[] readBuffer = new byte[blockSize];
			int index = 0;
			int readLength;
			while ((readLength = fileInputStream.read(readBuffer)) != -1) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Read index: " + index + ", read length: " + readLength);
				}
				byteArrayOutputStream = new ByteArrayOutputStream(blockSize);
				byteArrayOutputStream.write(readBuffer, 0, readLength);
				SegmentationItem segmentationItem =
						new SegmentationItem((long) index * blockSize, byteArrayOutputStream.toByteArray());
				segmentationItemList.add(segmentationItem);
				index++;
			}

			return new SegmentationFile(extName, fileSize, blockSize,
					ConvertUtils.byteToHex(SecurityUtils.MD5(fileObject)),
					ConvertUtils.byteToHex(SecurityUtils.SHA256(fileObject)),
					segmentationItemList);
		} catch (FileNotFoundException e) {
			LOGGER.error("Target file not exists! ");
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Stack message: ", e);
			}
		} catch (IOException e) {
			LOGGER.error("Read file data error! ");
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Stack message: ", e);
			}
		} finally {
			IOUtils.closeStream(fileInputStream);
		}

		return null;
	}

	/**
	 * Generate context cifs context.
	 *
	 * @param ntlmPasswordAuthenticator the ntlm password authenticator
	 * @return the cifs context
	 * @throws CIFSException the cifs exception
	 */
	public static CIFSContext generateContext(final NtlmPasswordAuthenticator ntlmPasswordAuthenticator)
			throws CIFSException {
		return FileUtils.generateContext(null, ntlmPasswordAuthenticator);
	}

	/**
	 * Generate context cifs context.
	 *
	 * @param properties                the properties
	 * @param ntlmPasswordAuthenticator the ntlm password authenticator
	 * @return the cifs context
	 * @throws CIFSException the cifs exception
	 */
	public static CIFSContext generateContext(final Properties properties,
	                                          final NtlmPasswordAuthenticator ntlmPasswordAuthenticator)
			throws CIFSException {
		CIFSContext cifsContext =
				new BaseContext(new PropertyConfiguration(properties == null ? new Properties() : properties));
		if (ntlmPasswordAuthenticator != null) {
			cifsContext = cifsContext.withCredentials(ntlmPasswordAuthenticator);
		}
		return cifsContext;
	}

	/**
	 * Smb properties properties.
	 *
	 * @param domain   the domain
	 * @param userName the username
	 * @param passWord the password
	 * @return the properties
	 */
	public static NtlmPasswordAuthenticator smbAuthenticator(final String domain, final String userName,
	                                                         final String passWord) {
		return new NtlmPasswordAuthenticator(domain, userName, passWord);
	}

	/**
	 * Replace page separator to "|"
	 * @param path      file path
	 * @return          replaced file path
	 */
	private static String replacePageSeparator(String path) {
		String replacePath = StringUtils.replace(path, Globals.DEFAULT_PAGE_SEPARATOR, "|");
		replacePath = StringUtils.replace(replacePath, Globals.DEFAULT_ZIP_PAGE_SEPARATOR, "|");
		replacePath = StringUtils.replace(replacePath, Globals.DEFAULT_JAR_PAGE_SEPARATOR, "|");
		if (replacePath.endsWith("|")) {
			replacePath = replacePath.substring(0, replacePath.length() - 1);
		}
		return replacePath;
	}

	private static boolean mergeFile(RandomAccessFile randomAccessFile,
	                                 SegmentationItem segmentationItem) throws IOException {
		if (segmentationItem == null) {
			return Boolean.FALSE;
		}

		if (segmentationItem.securityCheck()) {
			randomAccessFile.seek(segmentationItem.getPosition());
			randomAccessFile.write(StringUtils.base64Decode(segmentationItem.getDataInfo()));
			return true;
		} else {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Segmentation file part is invalid");
			}
		}
		return Boolean.FALSE;
	}

	/**
	 * File name filter by regex
	 */
	private static final class FilenameRegexFilter implements FilenameFilter {
		private final String fileNameRegex;

		/**
		 * Instantiates a new Filename regex filter.
		 *
		 * @param fileNameRegex the file name regex
		 */
		public FilenameRegexFilter(String fileNameRegex) {
			this.fileNameRegex = fileNameRegex;
		}

		public boolean accept(File dir, String name) {
			if (this.fileNameRegex != null && dir != null && dir.isDirectory()
					&& dir.exists() && name != null) {
				String fileName = StringUtils.getFilename(name);
				return StringUtils.matches(fileName, this.fileNameRegex);
			}
			return Boolean.FALSE;
		}
	}

	/**
	 * file extension name filter
	 */
	private static final class FilenameExtensionFilter implements FilenameFilter {
		private final String fileExtName;

		/**
		 * Instantiates a new Filename extension filter.
		 *
		 * @param fileExtName the file ext name
		 */
		public FilenameExtensionFilter(String fileExtName) {
			this.fileExtName = fileExtName;
		}

		public boolean accept(File dir, String name) {
			if (this.fileExtName != null && dir != null && dir.isDirectory()
					&& dir.exists() && name != null) {
				String fileExtName = StringUtils.getFilenameExtension(name);
				return fileExtName.equalsIgnoreCase(this.fileExtName);
			}
			return Boolean.FALSE;
		}
	}

	private static final class DirectoryFileFilter implements FileFilter {

		/**
		 * Instantiates a new Directory file filter.
		 */
		DirectoryFileFilter() {
		}
		public boolean accept(File pathname) {
			return pathname.isDirectory();
		}
	}
}
