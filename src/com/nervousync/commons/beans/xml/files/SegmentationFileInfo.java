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
package com.nervousync.commons.beans.xml.files;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.nervousync.commons.beans.xml.BaseElement;
import com.nervousync.commons.core.Globals;
import com.nervousync.utils.SecurityUtils;
import com.nervousync.utils.StringUtils;

/**
 * Segmentation item define
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

	/**
	 * Item position
	 */
	@XmlElement
	private long position;
	/**
	 * File total size
	 */
	@XmlElement
	private long totalSize;
	/**
	 * Item identified value of MD5
	 */
	@XmlElement
	private String md5;
	/**
	 * Item identified value of SHA256
	 */
	@XmlElement
	private String sha;
	/**
	 * Item data info
	 */
	@XmlElement
	private String dataInfo;
	
	/**
	 * Default constructor
	 */
	public SegmentationFileInfo() {
		
	}
	
	/**
	 * Constructor for define segmentation item
	 * @param position					Item position
	 * @param totalSize					File total size
	 * @param dataContent				Item data content
	 */
	public SegmentationFileInfo(long position, long totalSize, byte[] dataContent) {
		this.md5 = SecurityUtils.MD5(dataContent);
		this.sha = SecurityUtils.SHA256(dataContent);
		this.position = position;
		this.totalSize = totalSize;
		this.dataInfo = StringUtils.base64Encode(dataContent);
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
	
	/**
	 * Read item datas
	 * @return
	 */
	public byte[] getDataContent() {
		if (this.securityCheck()) {
			return StringUtils.base64Decode(this.dataInfo);
		}
		return new byte[0];
	}
	
	/**
	 * Validate this item data
	 * @return	<code>true</code> for valid, <code>false</code> for invalid
	 */
	public boolean securityCheck() {
		byte[] dataContent = StringUtils.base64Decode(this.dataInfo);
		try {
			return SecurityUtils.MD5(dataContent).equals(this.md5) 
					&& SecurityUtils.SHA256(dataContent).equals(this.sha);
		} catch (Exception e) {
			return Globals.DEFAULT_VALUE_BOOLEAN;
		}
	}
}
