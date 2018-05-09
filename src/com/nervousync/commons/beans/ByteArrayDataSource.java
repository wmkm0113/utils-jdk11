/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervousync Studio.
 */
package com.nervousync.commons.beans;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

import com.nervousync.utils.ConvertUtils;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Jan 13, 2010 4:26:58 PM $
 */
public class ByteArrayDataSource implements DataSource {

	private byte[] data;
	
	private String type;
	
	public ByteArrayDataSource(InputStream inputStream, String type) {
		this.type = type;
		
		try {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			
			int length = 0;
			
			while ((length = inputStream.read()) != -1) {
				byteArrayOutputStream.write(length);
			}
			
			data = byteArrayOutputStream.toByteArray();
		} catch (IOException e) {
			
		}
	}
	
	public ByteArrayDataSource(byte[] data, String type) {
		this.data = data;
		this.type = type;
	}
	
	public ByteArrayDataSource(String data, String type) {
		this.data = ConvertUtils.convertToByteArray(data);
		this.type = type;
	}
	
	public String getContentType() {
		return this.type;
	}

	public InputStream getInputStream() throws IOException {
		if (this.data == null) {
			throw new IOException("No data");
		}
		
		return new ByteArrayInputStream(this.data);
	}

	public String getName() {
		return "dummy";
	}

	public OutputStream getOutputStream() throws IOException {
		throw new IOException("Can't do this");
	}

}
