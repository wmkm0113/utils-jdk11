/*
 * Copyright 2018 Nervousync Studio
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.nervousync.beans.xml.files;

import jakarta.xml.bind.annotation.*;
import org.nervousync.commons.adapter.xml.CDataAdapter;
import org.nervousync.commons.adapter.xml.DateTimeAdapter;
import org.nervousync.beans.core.BeanObject;
import org.nervousync.utils.ConvertUtils;
import org.nervousync.utils.SecurityUtils;
import org.nervousync.utils.StringUtils;

import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.util.Date;
import java.util.Objects;

/**
 * The type Segmentation item.
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $Date: 2018-10-15 12:41
 */
@XmlType(name = "segment_item", namespace = "https://nervousync.org/schemas/file/segment")
@XmlRootElement(name = "segment_item", namespace = "https://nervousync.org/schemas/file/segment")
@XmlAccessorType(XmlAccessType.NONE)
public class SegmentationItem extends BeanObject {
	
	private static final long serialVersionUID = 2993229461743423521L;
	
	/**
	 * Item position
	 */
	@XmlElement(name = "position")
	private long position;
	/**
	 * Block item size
	 */
	@XmlElement(name = "block_size")
	private long blockSize;
	/**
	 * Item identified value of MD5
	 */
	@XmlElement(name = "signature_md5")
	private String md5;
	/**
	 * Item identified value of SHA256
	 */
	@XmlElement(name = "signature_sha")
	private String sha;
	/**
	 * Item data info
	 */
	@XmlElement(name = "data_info")
	@XmlJavaTypeAdapter(CDataAdapter.class)
	private String dataInfo;
	/**
	 * Current time
	 */
	@XmlElement(name = "current_time")
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	private Date currentTime;

	/**
	 * Default constructor
	 */
	public SegmentationItem() {
	}

	/**
	 * Constructor for define segmentation item
	 *
	 * @param position    Item position
	 * @param dataContent Item data content
	 */
	public SegmentationItem(long position, byte[] dataContent) {
		this.md5 = ConvertUtils.byteToHex(SecurityUtils.MD5(dataContent));
		this.sha = ConvertUtils.byteToHex(SecurityUtils.SHA256(dataContent));
		this.position = position;
		this.blockSize = dataContent.length;
		this.dataInfo = StringUtils.base64Encode(dataContent);
		this.currentTime = new Date();
	}

	/**
	 * Gets position.
	 *
	 * @return the position
	 */
	public long getPosition() {
		return position;
	}

	/**
	 * Gets block size.
	 *
	 * @return the totalSize
	 */
	public long getBlockSize() {
		return blockSize;
	}

	/**
	 * Gets md 5.
	 *
	 * @return the md5
	 */
	public String getMd5() {
		return md5;
	}

	/**
	 * Gets sha.
	 *
	 * @return the sha
	 */
	public String getSha() {
		return sha;
	}

	/**
	 * Gets data info.
	 *
	 * @return the dataInfo
	 */
	public String getDataInfo() {
		return dataInfo;
	}

	/**
	 * Gets current time.
	 *
	 * @return the current time
	 */
	public Date getCurrentTime() {
		return currentTime == null ? null : (Date)currentTime.clone();
	}

	/**
	 * Validate this item data
	 *
	 * @return <code>true</code> for valid, <code>false</code> for invalid
	 */
	public boolean securityCheck() {
		byte[] dataContent = StringUtils.base64Decode(this.dataInfo);
		try {
			return dataContent.length == this.blockSize
					&& Objects.equals(ConvertUtils.byteToHex(SecurityUtils.MD5(dataContent)), this.md5)
					&& Objects.equals(ConvertUtils.byteToHex(SecurityUtils.SHA256(dataContent)), this.sha);
		} catch (Exception e) {
			return Boolean.FALSE;
		}
	}
}
