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
package org.nervousync.beans.servlet.response;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HttpsURLConnection;

import org.nervousync.exceptions.xml.XmlException;
import org.nervousync.commons.http.header.SimpleHeader;
import org.nervousync.utils.FileUtils;
import org.nervousync.utils.IOUtils;
import org.nervousync.commons.core.Globals;
import org.nervousync.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Response content of request
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: Jun 11, 2015 12:25:33 PM $
 */
public final class ResponseInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1427305383899073910L;

	/**
	 * Logger
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * Response status code
	 */
	private int statusCode;
	/**
	 * Header Maps
	 */
	private final Map<String, String> headerMaps = new HashMap<>();
	/**
	 * Response content type
	 */
	private String contentType;
	/**
	 * Response charset encoding
	 */
	private String charset = null;
	/**
	 * Value of response header which header name is "identified"
	 */
	private String identifiedCode;
	/**
	 * Response content length
	 */
	private int contentLength;
	/**
	 * Response content data array
	 */
	private byte[] responseContent;

	/**
	 * Gets status code.
	 *
	 * @return the statusCode
	 */
	public int getStatusCode() {
		return statusCode;
	}

	/**
	 * Gets the value of headerMaps.
	 *
	 * @return the value of headerMaps
	 */
	public Map<String, String> getHeaderMaps() {
		return headerMaps;
	}

	/**
	 * Gets content type.
	 *
	 * @return the contentType
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * Gets charset.
	 *
	 * @return the charset
	 */
	public String getCharset() {
		return charset;
	}

	/**
	 * Gets identified code.
	 *
	 * @return the identifiedCode
	 */
	public String getIdentifiedCode() {
		return identifiedCode;
	}

	/**
	 * Gets content length.
	 *
	 * @return the contentLength
	 */
	public int getContentLength() {
		return contentLength;
	}

	/**
	 * Get response content byte [ ].
	 *
	 * @return the responseContent
	 */
	public byte[] getResponseContent() {
		return responseContent == null ? new byte[0] : responseContent.clone();
	}

	/**
	 * Instantiates a new Http response content.
	 *
	 * @param responseInfo the response info
	 * @param inputStream  the input stream
	 */
	public ResponseInfo(HttpResponse.ResponseInfo responseInfo, InputStream inputStream) {
		this.statusCode = responseInfo.statusCode();
		responseInfo.headers().map().forEach((key, values) -> {
			if (key != null && values != null && !values.isEmpty()) {
				StringBuilder stringBuilder = new StringBuilder();
				for (String headerValue : values) {
					stringBuilder.append(" ").append(headerValue);
				}
				this.headerMaps.put(key.toUpperCase(), stringBuilder.substring(1));
			}
		});
		this.contentType = this.headerMaps.get("CONTENT-TYPE");
		if (this.contentType != null
				&& this.contentType.contains("charset=")) {
			this.charset = this.contentType.substring(this.contentType.indexOf("charset="));
			if (this.contentType.contains("\"")) {
				this.charset = this.charset.substring(0, this.charset.indexOf("\""));
			}
			this.charset = this.charset.substring(this.charset.indexOf("=") + 1);
			if (this.charset.contains(";")) {
				this.charset = this.charset.substring(0, this.charset.indexOf(";"));
			}
		}
		this.identifiedCode = this.headerMaps.get("IDENTIFIED");
		this.responseContent = IOUtils.readBytes(inputStream);
		this.contentLength = this.responseContent.length;
	}

	/**
	 * Instantiates a new Http response content.
	 *
	 * @param urlConnection the url connection
	 */
	public ResponseInfo(HttpURLConnection urlConnection) {
		InputStream inputStream = null;
		ByteArrayOutputStream byteArrayOutputStream = null;
		
		try {
			this.statusCode = urlConnection.getResponseCode();
			this.contentLength = urlConnection.getContentLength();
			if (this.statusCode == HttpsURLConnection.HTTP_OK) {
				this.contentType = urlConnection.getContentType();
				if (this.contentType != null 
						&& this.contentType.contains("charset=")) {
					this.charset = this.contentType.substring(this.contentType.indexOf("charset="));
					if (this.contentType.contains("\"")) {
						this.charset = this.charset.substring(0, this.charset.indexOf("\""));
					}
					this.charset = this.charset.substring(this.charset.indexOf("=") + 1);
					if (this.charset.contains(";")) {
						this.charset = this.charset.substring(0, this.charset.indexOf(";"));
					}
				}
				
				if (this.isGZipResponse(urlConnection.getContentEncoding())) {
					inputStream = new GZIPInputStream(urlConnection.getInputStream());
				} else {
					inputStream = urlConnection.getInputStream();
				}
			} else {
				inputStream = urlConnection.getErrorStream();
			}
			
			Map<String, List<String>> headerFields = urlConnection.getHeaderFields();
			if (headerFields != null && !headerFields.isEmpty()) {
				for (Map.Entry<String, List<String>> entry : headerFields.entrySet()) {
					List<String> headerValues = entry.getValue();
					if (entry.getKey() != null && headerValues != null && !headerValues.isEmpty()) {
						StringBuilder stringBuilder = new StringBuilder();
						for (String headerValue : headerValues) {
							stringBuilder.append(" ").append(headerValue);
						}
						this.headerMaps.put(entry.getKey().toUpperCase(), stringBuilder.substring(1));
					}
				}
			}

			byteArrayOutputStream = new ByteArrayOutputStream(Globals.DEFAULT_BUFFER_SIZE);
			
			byte[] buffer = new byte[Globals.DEFAULT_BUFFER_SIZE];
			
			if (inputStream != null) {
				int readLength;
				while ((readLength = inputStream.read(buffer)) != -1) {
					byteArrayOutputStream.write(buffer, 0, readLength);
				}
			}

			this.responseContent = byteArrayOutputStream.toByteArray();
			
			if (this.charset == null) {
				String tempContent = new String(this.responseContent, Globals.DEFAULT_ENCODING);
				if (tempContent.contains("charset=")) {
					this.charset = tempContent.substring(tempContent.indexOf("charset="));
					this.charset = this.charset.substring(0, this.charset.indexOf("\""));
					this.charset = this.charset.substring(this.charset.indexOf("=") + 1);
					if (this.charset.contains(";")) {
						this.charset = this.charset.substring(0, this.charset.indexOf(";"));
					}
				} else {
					this.charset = Globals.DEFAULT_ENCODING;
				}
			}
			
			this.identifiedCode = urlConnection.getHeaderField("identified");
		} catch (IOException e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("Read response data error! ", e);
			}
		} finally {
			IOUtils.closeStream(inputStream);
			IOUtils.closeStream(byteArrayOutputStream);
		}
	}

	/**
	 * Parse xml t.
	 *
	 * @param <T>   the type parameter
	 * @param clazz the clazz
	 * @return the t
	 * @throws XmlException                 the xml exception
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 */
	public <T> T parseXml(Class<T> clazz) throws XmlException, UnsupportedEncodingException {
		return StringUtils.stringToObject(this.parseString(), this.charset, clazz);
	}

	/**
	 * Parse json t.
	 *
	 * @param <T>   the type parameter
	 * @param clazz the clazz
	 * @return the t
	 * @throws XmlException                 the xml exception
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 */
	public <T> T parseJson(Class<T> clazz) throws XmlException, UnsupportedEncodingException {
		return StringUtils.stringToObject(this.parseString(this.charset), this.charset, clazz);
	}

	/**
	 * Parse object object.
	 *
	 * @return the object
	 * @throws XmlException the xml exception
	 */
	public Object parseObject() throws XmlException {
		ByteArrayInputStream byteArrayInputStream = null;
		ObjectInputStream objectInputStream = null;
		
		try {
			byteArrayInputStream = new ByteArrayInputStream(this.responseContent);
			objectInputStream = new ObjectInputStream(byteArrayInputStream);
			return objectInputStream.readObject();
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("Convert to object error! ", e);
			}
			
			return null;
		} finally {
			IOUtils.closeStream(byteArrayInputStream);
			IOUtils.closeStream(objectInputStream);
		}
	}

	/**
	 * Parse string string.
	 *
	 * @return the string
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 */
	public String parseString() throws UnsupportedEncodingException {
		return this.parseString(this.charset);
	}

	/**
	 * Parse string string.
	 *
	 * @param charsetName the charset name
	 * @return the string
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 */
	public String parseString(String charsetName) throws UnsupportedEncodingException {
		if (StringUtils.isEmpty(charsetName)) {
			charsetName = Globals.DEFAULT_ENCODING;
		}
		String string = new String(this.getResponseContent(), charsetName);
		while (string.charAt(string.length() - 1) == '\n') {
			string = string.substring(0, string.length() - 1);
		}
		while (string.charAt(string.length() - 1) == '\r') {
			string = string.substring(0, string.length() - 1);
		}
		return string;
	}

	/**
	 * Parse file.
	 *
	 * @param savePath the save path
	 * @return the file
	 * @throws FileNotFoundException the io exception
	 */
	public File parseFile(String savePath) throws FileNotFoundException {
		return FileUtils.saveFile(this.getResponseContent(), savePath) ? FileUtils.getFile(savePath) : null;
	}

	/**
	 * Gets header.
	 *
	 * @param headerName the header name
	 * @return the header
	 */
	public String getHeader(String headerName) {
		return this.headerMaps.get(headerName.toUpperCase());
	}

	/**
	 * Header list.
	 *
	 * @return the list
	 */
	public List<SimpleHeader> headerList() {
		List<SimpleHeader> headerList = new ArrayList<>();
		
		for (Map.Entry<String, String> entry : this.headerMaps.entrySet()) {
			headerList.add(new SimpleHeader(entry.getKey(), entry.getValue()));
		}
		
		return headerList;
	}
	
	private boolean isGZipResponse(String contentEncoding) {
		return contentEncoding != null
				&& contentEncoding.contains("gzip");
	}
}
