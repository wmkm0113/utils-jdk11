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
 * @version $Revision: 1.0 $ $Date: Nov 28, 2017 4:56:08 PM $
 */
public final class ArchiveExtraDataRecord {

	private int signature;
	private int extraFieldLength;
	private String extraFieldData;
	
	/**
	 * @return the signature
	 */
	public int getSignature() {
		return signature;
	}

	/**
	 * @param signature the signature to set
	 */
	public void setSignature(int signature) {
		this.signature = signature;
	}

	/**
	 * @return the extraFieldLength
	 */
	public int getExtraFieldLength() {
		return extraFieldLength;
	}

	/**
	 * @param extraFieldLength the extraFieldLength to set
	 */
	public void setExtraFieldLength(int extraFieldLength) {
		this.extraFieldLength = extraFieldLength;
	}

	/**
	 * @return the extraFieldData
	 */
	public String getExtraFieldData() {
		return extraFieldData;
	}

	/**
	 * @param extraFieldData the extraFieldData to set
	 */
	public void setExtraFieldData(String extraFieldData) {
		this.extraFieldData = extraFieldData;
	}
}
