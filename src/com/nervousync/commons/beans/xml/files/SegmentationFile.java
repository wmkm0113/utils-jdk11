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

import javax.xml.bind.annotation.*;

import com.nervousync.commons.beans.xml.BaseElement;

import java.util.List;

/**
 * Segmentation item define
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Jun 9, 2015 9:43:23 AM $
 */
@XmlRootElement(name = "segment-file")
@XmlAccessorType(XmlAccessType.FIELD)
public class SegmentationFile extends BaseElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2885564700967742489L;
	
	/**
	 * File total size
	 */
	private long totalSize;
	/**
	 * File total size
	 */
	private int blockSize;
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
	@XmlElementWrapper(name = "segment-items")
	@XmlElement(name = "segment-item")
	private List<SegmentationItem> segmentationItemList;
	
	/**
	 * Default constructor
	 */
	public SegmentationFile() {
		
	}
	
	/**
	 * Constructor for define segmentation item
	 * @param totalSize					File total size
	 * @param blockSize					Item block size
	 * @param md5   				    File MD5
	 * @param sha   				    File SHA256
	 * @param segmentationItemList   	Block item list
	 */
	public SegmentationFile(long totalSize, int blockSize, String md5, String sha,
	                        List<SegmentationItem> segmentationItemList) {
		this.totalSize = totalSize;
		this.blockSize = blockSize;
		this.md5 = md5;
		this.sha = sha;
		this.segmentationItemList = segmentationItemList;
	}
	
	public long getTotalSize() {
		return totalSize;
	}
	
	public void setTotalSize(long totalSize) {
		this.totalSize = totalSize;
	}
	
	public int getBlockSize() {
		return blockSize;
	}
	
	public void setBlockSize(int blockSize) {
		this.blockSize = blockSize;
	}
	
	public String getMd5() {
		return md5;
	}
	
	public void setMd5(String md5) {
		this.md5 = md5;
	}
	
	public String getSha() {
		return sha;
	}
	
	public void setSha(String sha) {
		this.sha = sha;
	}
	
	public List<SegmentationItem> getSegmentationItemList() {
		return segmentationItemList;
	}
	
	public void setSegmentationItemList(List<SegmentationItem> segmentationItemList) {
		this.segmentationItemList = segmentationItemList;
	}
}
