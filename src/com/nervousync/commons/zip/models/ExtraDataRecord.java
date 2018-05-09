/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervousync Studio.
 */
package com.nervousync.commons.zip.models;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Nov 28, 2017 4:43:23 PM $
 */
public final class ExtraDataRecord {

	private long header;
	private int dataSize;
	private byte[] dataContent;
	
	/**
	 * @return the header
	 */
	public long getHeader() {
		return header;
	}

	/**
	 * @param header the header to set
	 */
	public void setHeader(long header) {
		this.header = header;
	}

	/**
	 * @return the dataSize
	 */
	public int getDataSize() {
		return dataSize;
	}

	/**
	 * @param dataSize the dataSize to set
	 */
	public void setDataSize(int dataSize) {
		this.dataSize = dataSize;
	}

	/**
	 * @return the dataContent
	 */
	public byte[] getDataContent() {
		return dataContent;
	}

	/**
	 * @param dataContent the dataContent to set
	 */
	public void setDataContent(byte[] dataContent) {
		this.dataContent = dataContent;
	}
}
