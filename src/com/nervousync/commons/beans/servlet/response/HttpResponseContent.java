/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervousync Studio.
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
import java.nio.charset.Charset;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nervousync.commons.core.Globals;
import com.nervousync.commons.core.MIMETypes;
import com.nervousync.exceptions.xml.XmlException;
import com.nervousync.utils.ConvertUtils;
import com.nervousync.utils.FileUtils;
import com.nervousync.utils.StringUtils;
import com.nervousync.utils.XmlUtils;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Jun 11, 2015 12:25:33 PM $
 */
public final class HttpResponseContent implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1427305383899073910L;
	
	private transient final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private int statusCode;
	private String contentType;
	private String charset;
	private String identifiedCode;
	private long contentLength;
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
	public long getContentLength() {
		return contentLength;
	}

	/**
	 * @return the responseContent
	 */
	public byte[] getResponseContent() {
		return responseContent;
	}

	public HttpResponseContent(CloseableHttpResponse httpResponse) {
		this.statusCode = httpResponse.getStatusLine().getStatusCode();
		if (httpResponse.getLastHeader("identified") != null) {
			this.identifiedCode = httpResponse.getLastHeader("identified").getValue();
		}

		HttpEntity httpEntity = httpResponse.getEntity();
		
		this.contentLength = httpEntity.getContentLength();
		if (logger.isDebugEnabled()) {
			logger.debug("Entity length: " + this.contentLength);
		}
		
		this.contentType = httpEntity.getContentType().getValue();
		Charset responseCharset = ContentType.getOrDefault(httpEntity).getCharset();

		if (responseCharset != null) {
			this.charset = responseCharset.name();
		} else {
			this.charset = Globals.DEFAULT_ENCODING;
		}
		
		InputStream inputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;
		ByteArrayOutputStream byteArrayOutputStream = null;
		
		if (this.charset == null) {
			this.charset = Globals.DEFAULT_ENCODING;
		}
		
		try {
			if (this.isGZipResponse(httpEntity)) {
				GzipDecompressingEntity gzipEntity = new GzipDecompressingEntity(httpEntity);
				inputStream = gzipEntity.getContent();
			} else {
				inputStream = httpEntity.getContent();
			}
			
			if (this.isTextContent()) {
				inputStreamReader = new InputStreamReader(inputStream);
				bufferedReader = new BufferedReader(inputStreamReader);
				
				String responseContent = null;
				StringBuffer responseBuffer = new StringBuffer();
				
				while ((responseContent = bufferedReader.readLine()) != null) {
					responseBuffer.append(responseContent);
				}
				
				this.responseContent = ConvertUtils.convertToByteArray(responseBuffer.toString(), this.charset);
			} else {
				byteArrayOutputStream = new ByteArrayOutputStream(Globals.DEFAULT_BUFFER_SIZE);
				
				byte[] buffer = new byte[Globals.DEFAULT_BUFFER_SIZE];
				int readLength = 0;
				
				while ((readLength = inputStream.read(buffer)) != -1) {
					byteArrayOutputStream.write(buffer, 0, readLength);
				}
				
				this.responseContent = byteArrayOutputStream.toByteArray();
			}
		} catch (Exception e) {
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
		return new String(this.responseContent, Globals.DEFAULT_ENCODING);
	}
	
	public String parseString(String charsetName) throws UnsupportedEncodingException {
		return new String(this.responseContent, charsetName);
	}
	
	public File parseFile(String savePath) throws IOException {
		FileUtils.saveFile(this.responseContent, savePath);
		return FileUtils.getFile(savePath);
	}
	
	private boolean isGZipResponse(HttpEntity httpEntity) {
		Header encodingHeader = httpEntity.getContentEncoding();
		
		if (encodingHeader != null 
				&& encodingHeader.getValue().indexOf("gzip") != -1) {
			return true;
		}
		return false;
	}
	
	private boolean isTextContent() {
		return MIMETypes.isText(this.contentType);
	}
}
