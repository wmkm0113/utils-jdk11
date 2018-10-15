/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Nervousync Studio.
 */
package com.nervousync.commons.beans.xml.files;

import com.nervousync.commons.adapter.xml.CDataAdapter;
import com.nervousync.commons.adapter.xml.DateTimeAdapter;
import com.nervousync.commons.beans.xml.BaseElement;
import com.nervousync.commons.core.Globals;
import com.nervousync.utils.SecurityUtils;
import com.nervousync.utils.StringUtils;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;

/**
 * The type Segmentation item.
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $Date: 2018-10-15 12:41
 */
@XmlRootElement(name = "segment-item")
@XmlAccessorType(XmlAccessType.FIELD)
public class SegmentationItem extends BaseElement {
	
	private static final long serialVersionUID = 2993229461743423521L;
	
	/**
	 * Item position
	 */
	private long position;
	/**
	 * Block item size
	 */
	private long blockSize;
	/**
	 * Item identified value of MD5
	 */
	private String md5;
	/**
	 * Item identified value of SHA256
	 */
	private String sha;
	/**
	 * Item data info
	 */
	@XmlJavaTypeAdapter(CDataAdapter.class)
	private String dataInfo;
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	private Date currentTime;
	
	/**
	 * Default constructor
	 */
	public SegmentationItem() {
	
	}
	
	/**
	 * Constructor for define segmentation item
	 * @param position					Item position
	 * @param dataContent				Item data content
	 */
	public SegmentationItem(long position, byte[] dataContent) {
		this.md5 = SecurityUtils.MD5(dataContent);
		this.sha = SecurityUtils.SHA256(dataContent);
		this.position = position;
		this.blockSize = dataContent.length;
		this.dataInfo = StringUtils.base64Encode(dataContent);
		this.currentTime = new Date();
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
	public long getBlockSize() {
		return blockSize;
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
	
	public Date getCurrentTime() {
		return currentTime;
	}
	
	/**
	 * Validate this item data
	 * @return	<code>true</code> for valid, <code>false</code> for invalid
	 */
	public boolean securityCheck() {
		byte[] dataContent = StringUtils.base64Decode(this.dataInfo);
		try {
			return dataContent.length == this.blockSize
					&& SecurityUtils.MD5(dataContent).equals(this.md5)
					&& SecurityUtils.SHA256(dataContent).equals(this.sha);
		} catch (Exception e) {
			return Globals.DEFAULT_VALUE_BOOLEAN;
		}
	}
}
