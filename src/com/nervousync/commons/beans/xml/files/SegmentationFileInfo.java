/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervousync Studio.
 */
package com.nervousync.commons.beans.xml.files;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.codec.binary.Base64;

import com.nervousync.commons.beans.xml.BaseElement;
import com.nervousync.commons.core.Globals;
import com.nervousync.exceptions.xml.files.SegmentationException;
import com.nervousync.utils.SecurityUtils;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Jun 9, 2015 9:43:23 AM $
 */
@XmlRootElement(name = "segment-file")
@XmlType
public class SegmentationFileInfo extends BaseElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2885564700967742489L;

	@XmlElement
	private long position;
	@XmlElement
	private long totalSize;
	@XmlElement
	private String md5;
	@XmlElement
	private String sha;
	@XmlElement
	private String dataInfo;
	
	public SegmentationFileInfo() {
		
	}
	
	public SegmentationFileInfo(long position, long totalSize, byte[] dataContent) throws SegmentationException {
		this.md5 = SecurityUtils.MD5Encode(dataContent);
		this.sha = SecurityUtils.SHAEncode(dataContent);
		this.position = position;
		this.totalSize = totalSize;
		this.dataInfo = new String(new Base64().encode(dataContent));
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * @return the position
	 */
	public long getPosition() {
		return position;
	}

	/**
	 * @return the totalSize
	 */
	public long getTotalSize() {
		return totalSize;
	}

	/**
	 * @return the md5
	 */
	public String getMd5() {
		return md5;
	}

	/**
	 * @return the sha
	 */
	public String getSha() {
		return sha;
	}

	/**
	 * @return the dataInfo
	 */
	public String getDataInfo() {
		return dataInfo;
	}
	
	public byte[] getDataContent() {
		if (this.securityCheck()) {
			return new Base64().decode(this.dataInfo.getBytes());
		}
		return new byte[0];
	}
	
	public boolean securityCheck() {
		byte[] dataContent = new Base64().decode(this.dataInfo.getBytes());
		try {
			return SecurityUtils.MD5Encode(dataContent).equals(this.md5) 
					&& SecurityUtils.SHAEncode(dataContent).equals(this.sha);
		} catch (Exception e) {
			return Globals.DEFAULT_VALUE_BOOLEAN;
		}
	}
}
