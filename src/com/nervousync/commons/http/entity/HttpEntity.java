/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervousync Studio.
 */
package com.nervousync.commons.http.entity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.nervousync.commons.core.Globals;
import com.nervousync.enumeration.web.HttpMethodOption;
import com.nervousync.utils.FileUtils;
import com.nervousync.utils.StringUtils;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Jan 2, 2018 2:05:04 PM $
 */
public final class HttpEntity {

	private static final char[] BOUNDARY_CHAR_ARRAY = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
	
	private static final String TEXT_CONTENT_TYPE = "text/plain";
	private static final String ENCODED_CONTENT_TYPE = "application/x-www-form-urlencoded";
	private static final String MULTIPART_CONTENT_TYPE = "multipart/form-data";
	private static final String MIXED_CONTENT_TYPE = "multipart/mixed";
	
	private static final String CRLF = "\r\n";
	private static final String CONTENT_DISPOSITION = "form-data";
	private static final String BINARY_CONTENT_TYPE = "application/octet-stream";
	
	private String boundary = null;
	private List<EntityInfo> entityList = null;
	private boolean multipart = Globals.DEFAULT_VALUE_BOOLEAN;
	private boolean mixed = Globals.DEFAULT_VALUE_BOOLEAN;
	
	private HttpEntity() {
		this.entityList = new ArrayList<EntityInfo>();
	}
	
	public static HttpEntity newInstance() {
		return new HttpEntity();
	}
	
	public void addTextEntity(String name, String value) {
		this.entityList.add(EntityInfo.generateTextEntity(name, value));
	}

	public void addBinaryEntity(String name, String value) throws FileNotFoundException {
		this.entityList.add(EntityInfo.generateBinaryEntity(name, value));
		if (this.boundary == null) {
			this.boundary = this.generateBoundary();
		}
	}
	
	public String generateContentType(String charset, HttpMethodOption methodOption) 
			throws UnsupportedEncodingException {
		if (charset == null || charset.trim().length() == 0) {
			charset = "ISO-8859-1";
		}
		String contentType = "";
		switch (methodOption) {
		case POST: case PUT:
			this.checkType();
			if (this.multipart) {
				if (this.mixed) {
					contentType = MIXED_CONTENT_TYPE + ";boundary=" + this.boundary;
				} else {
					contentType = MULTIPART_CONTENT_TYPE + ";boundary=" + this.boundary;
				}
			} else {
				contentType = ENCODED_CONTENT_TYPE + ";charset=" + charset;
			}
			break;
		case GET: case TRACE: case HEAD: case DELETE: case OPTIONS:
			contentType = TEXT_CONTENT_TYPE + ";charset=" + charset;
			break;
		default:
			throw new UnsupportedEncodingException("Unknown Request Method");
		}
		
		return contentType;
	}
	
	public void writeData(String charset, OutputStream outputStream) throws IOException {
		if (this.entityList.isEmpty()) {
			return;
		}
		
		if (charset == null || charset.trim().length() == 0) {
			charset = Globals.DEFAULT_ENCODING;
		}
		this.checkType();
		if (this.multipart) {
			StringBuilder stringBuilder = null;
			for (EntityInfo entityInfo : this.entityList) {
				stringBuilder = new StringBuilder();
				stringBuilder.append("--" + this.boundary + CRLF);
				stringBuilder.append("Content-Disposition:" + CONTENT_DISPOSITION + ";");
				stringBuilder.append("name=\"" + entityInfo.getEntityName() + "\"");
				String value = entityInfo.getEntityValue();
				if (entityInfo.isBinary()) {
					stringBuilder.append(";filename=\"" + StringUtils.getFilename(value) + "\"");
					stringBuilder.append(CRLF);
					stringBuilder.append("Content-Type:" + BINARY_CONTENT_TYPE);
				}
				stringBuilder.append(CRLF);
				stringBuilder.append(CRLF);
				
				outputStream.write(stringBuilder.toString().getBytes());
				if (entityInfo.isBinary()) {
					outputStream.write(FileUtils.readFileBytes(value));
				} else {
					outputStream.write(value.getBytes(charset));
				}
				outputStream.write(CRLF.getBytes(charset));
			}
			outputStream.write(("--" + this.boundary).getBytes(charset));
		} else {
			StringBuilder stringBuilder = new StringBuilder();
			for (EntityInfo entityInfo : this.entityList) {
				stringBuilder.append("&");
				stringBuilder.append(URLEncoder.encode(entityInfo.getEntityName(), charset));
				stringBuilder.append("=");
				stringBuilder.append(URLEncoder.encode(entityInfo.getEntityValue(), charset));
			}
			outputStream.write(stringBuilder.substring(1).getBytes(charset));
		}
	}
	
	private void checkType() {
		int formItemCount = 0;
		int fileItemCount = 0;
		for (EntityInfo entityInfo : this.entityList) {
			if (entityInfo.isBinary()) {
				fileItemCount++;
			} else {
				formItemCount++;
			}
		}
		
		if (fileItemCount > 0) {
			this.multipart = true;
			if (formItemCount > 0) {
				this.mixed = true;
			}
		}
	}
	
	private String generateBoundary() {
		StringBuilder stringBuilder = new StringBuilder();
		final Random random = new Random();
		for (int i = 0 ; i < 32 ; i++) {
			stringBuilder.append(BOUNDARY_CHAR_ARRAY[random.nextInt(BOUNDARY_CHAR_ARRAY.length)]);
		}
		return stringBuilder.toString();
	}
	
	private static final class EntityInfo {
		private boolean binary = Globals.DEFAULT_VALUE_BOOLEAN;
		private String entityName = null;
		private String entityValue = null;
		
		private EntityInfo(boolean binary, String name, String value) {
			this.binary = binary;
			this.entityName = name;
			this.entityValue = value;
		}
		
		public static EntityInfo generateTextEntity(String name, String value) {
			return new EntityInfo(Globals.DEFAULT_VALUE_BOOLEAN, name, value);
		}

		public static EntityInfo generateBinaryEntity(String name, String value) 
				throws FileNotFoundException {
			if (FileUtils.isExists(value)) {
				return new EntityInfo(true, name, value);
			}
			throw new FileNotFoundException("File not exists");
		}
		
		/**
		 * @return the binary
		 */
		public boolean isBinary() {
			return binary;
		}

		/**
		 * @return the entityName
		 */
		public String getEntityName() {
			return entityName;
		}

		/**
		 * @return the entityValue
		 */
		public String getEntityValue() {
			return entityValue;
		}
	}
}
