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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HttpsURLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nervousync.commons.core.Globals;
import com.nervousync.exceptions.xml.XmlException;
import com.nervousync.utils.FileUtils;
import com.nervousync.utils.StringUtils;
import com.nervousync.utils.XmlUtils;

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
	
	private transient final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * Response status code
	 */
	private int statusCode;
	/**
	 * Response content type
	 */
	private String contentType;
	/**
	 * Response charset encoding
	 */
	private String charset = null;
	/**
	 * Value of response header whick header name is "identified"
	 */
	private String identifiedCode;
	/**
	 * Response content length
	 */
	private int contentLength;
	/**
	 * Response content datas
	 */
	private byte[] responseContent;
	
	/**
	 * @return the statusCode
	 */
	public int getStatusCode() {
		return statusCode;
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
		return responseContent;
	}
	
	public HttpResponseContent(HttpURLConnection urlConnection) {
		this.contentType = urlConnection.getContentType();
		if (this.contentType.indexOf("charset=") != Globals.DEFAULT_VALUE_INT) {
			this.charset = this.contentType.substring(this.contentType.indexOf("charset="));
			if (this.contentType.indexOf("\"") != Globals.DEFAULT_VALUE_INT) {
				this.charset = this.charset.substring(0, this.charset.indexOf("\""));
			}
			this.charset = this.charset.substring(this.charset.indexOf("=") + 1);
			if (this.charset.indexOf(";") != Globals.DEFAULT_VALUE_INT) {
				this.charset = this.charset.substring(0, this.charset.indexOf(";"));
			}
		}
		
		InputStream inputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;
		ByteArrayOutputStream byteArrayOutputStream = null;
		
		try {
			this.statusCode = urlConnection.getResponseCode();
			this.contentLength = urlConnection.getContentLength();
			if (this.statusCode == HttpsURLConnection.HTTP_OK) {
				if (this.isGZipResponse(urlConnection.getContentEncoding())) {
					inputStream = new GZIPInputStream(urlConnection.getInputStream());
				} else {
					inputStream = urlConnection.getInputStream();
				}
			} else {
				inputStream = urlConnection.getErrorStream();
			}
			
			byteArrayOutputStream = new ByteArrayOutputStream(Globals.DEFAULT_BUFFER_SIZE);
			
			byte[] buffer = new byte[Globals.DEFAULT_BUFFER_SIZE];
			int readLength = 0;
			
			while ((readLength = inputStream.read(buffer)) != -1) {
				byteArrayOutputStream.write(buffer, 0, readLength);
			}

			this.responseContent = byteArrayOutputStream.toByteArray();
			
			if (this.charset == null) {
				String tempContent = new String(this.responseContent, Globals.DEFAULT_ENCODING);
				if (tempContent.contains("charset=")) {
					this.charset = tempContent.substring(tempContent.indexOf("charset="));
					this.charset = this.charset.substring(0, this.charset.indexOf("\""));
					this.charset = this.charset.substring(this.charset.indexOf("=") + 1);
					if (this.charset.indexOf(";") != Globals.DEFAULT_VALUE_INT) {
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
			try {
				if (inputStream != null) {
					inputStream.close();
					inputStream = null;
				}
				
				if (inputStreamReader != null) {
					inputStreamReader.close();
					inputStreamReader = null;
				}
				
				if (bufferedReader != null) {
					bufferedReader.close();
					bufferedReader = null;
				}

				if (byteArrayOutputStream != null) {
					byteArrayOutputStream.close();
					byteArrayOutputStream = null;
				}
			} catch (IOException e) {
				if (logger.isDebugEnabled()) {
					logger.debug("Close input stream error! ", e);
				}
			}
		}
	}

	public <T> T parseXml(Class<T> clazz) throws XmlException, UnsupportedEncodingException {
		if (Globals.CONTENT_TYPE_APPLICATION_XML.equalsIgnoreCase(this.contentType)) {
			return XmlUtils.convertToObject(this.parseString(), clazz);
		}
		
		throw new XmlException("Data type error! ");
	}

	public <T> T parseJson(Class<T> clazz) throws XmlException, UnsupportedEncodingException {
		if (Globals.CONTENT_TYPE_APPLICATION_JSON.equalsIgnoreCase(this.contentType)) {
			return StringUtils.convertJSONStringToObject(this.parseString(), clazz);
		}
		
		throw new XmlException("Data type error! ");
	}
	
	public Object parseObject() throws XmlException, UnsupportedEncodingException {
		ByteArrayInputStream byteArrayInputStream = null;
		ObjectInputStream objectInputStream = null;
		
		try {
			byteArrayInputStream = new ByteArrayInputStream(this.responseContent);
			objectInputStream = new ObjectInputStream(byteArrayInputStream);
			Object object = objectInputStream.readObject();
			return object;
		} catch (Exception e) {
			if (logger.isDebugEnabled()) {
				logger.debug("Convert to object error! ", e);
			}
			
			return null;
		} finally {
			if (byteArrayInputStream != null) {
				try {
					byteArrayInputStream.close();
				} catch (IOException e) {
					if (logger.isDebugEnabled()) {
						logger.debug("Close byte array input stream error! ", e);
					}
				}
			}
			if (objectInputStream != null) {
				try {
					objectInputStream.close();
				} catch (IOException e) {
					if (logger.isDebugEnabled()) {
						logger.debug("Close object input stream error! ", e);
					}
				}
			}
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
	
	private boolean isGZipResponse(String contentEncoding) {
		if (contentEncoding != null 
				&& contentEncoding.indexOf("gzip") != -1) {
			return true;
		}
		return false;
	}
}
