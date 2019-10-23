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
package com.nervousync.commons.beans.servlet.response;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HttpsURLConnection;

import com.nervousync.commons.beans.xml.BaseElement;
import com.nervousync.commons.http.header.SimpleHeader;
import com.nervousync.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nervousync.commons.core.Globals;
import com.nervousync.exceptions.xml.XmlException;
import com.nervousync.utils.FileUtils;
import com.nervousync.utils.StringUtils;

/**
 * Response content of request
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Jun 11, 2015 12:25:33 PM $
 */
public final class HttpResponseContent implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1427305383899073910L;
	
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
	 * @return the contentType
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * @return the charset
	 */
	public String getCharset() {
		return charset;
	}

	/**
	 * @return the identifiedCode
	 */
	public String getIdentifiedCode() {
		return identifiedCode;
	}

	/**
	 * @return the contentLength
	 */
	public int getContentLength() {
		return contentLength;
	}
	
	/**
	 * @return the responseContent
	 */
	public byte[] getResponseContent() {
		return responseContent == null ? new byte[0] : responseContent.clone();
	}
	
	public HttpResponseContent(HttpURLConnection urlConnection) {
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

	public <T> T parseXml(Class<T> clazz) throws XmlException, UnsupportedEncodingException {
		return BaseElement.parseXml(this.parseString(), this.charset, clazz);
	}

	public <T> T parseJson(Class<T> clazz) throws XmlException, UnsupportedEncodingException {
		return StringUtils.convertJSONStringToObject(this.parseString(), clazz);
	}
	
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
	
	public String parseString() throws UnsupportedEncodingException {
		return new String(this.responseContent, this.charset);
	}
	
	public String parseString(String charsetName) throws UnsupportedEncodingException {
		return new String(this.responseContent, charsetName);
	}
	
	public File parseFile(String savePath) throws IOException {
		FileUtils.saveFile(this.responseContent, savePath);
		return FileUtils.getFile(savePath);
	}
	
	public String getHeader(String headerName) {
		return this.headerMaps.get(headerName.toUpperCase());
	}
	
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
