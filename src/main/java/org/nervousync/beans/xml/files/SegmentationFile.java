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
package org.nervousync.beans.xml.files;

import org.nervousync.beans.core.BeanObject;

import jakarta.xml.bind.annotation.*;

import java.util.List;

/**
 * Segmentation item define
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: Jun 9, 2015 9:43:23 AM $
 */
@XmlType(name = "segment_file", namespace = "https://nervousync.org/schemas/file/segment")
@XmlRootElement(name = "segment_file", namespace = "https://nervousync.org/schemas/file/segment")
@XmlAccessorType(XmlAccessType.NONE)
public final class SegmentationFile extends BeanObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2885564700967742489L;
	
	/**
	 * File total size
	 */
	@XmlElement(name = "total_size")
	private long totalSize;
	/**
	 * File total size
	 */
	@XmlElement(name = "block_size")
	private int blockSize;
	/**
	 * File extension name
	 */
	@XmlElement(name = "ext_name")
	private String extName;
	/**
	 * Item identified value of SHA256
	 */
	@XmlElement(name = "signature_sha")
	private String sha;
	/**
	 * Item data info
	 */
	@XmlElementWrapper(name = "segment_item_list")
	@XmlElement(name = "segment_item")
	private List<SegmentationItem> segmentationItemList;

	/**
	 * Default constructor
	 */
	public SegmentationFile() {
		
	}

	/**
	 * Constructor for defined segmentation item
	 *
	 * @param extName              File extension name
	 * @param totalSize            File total size
	 * @param blockSize            Item block size
	 * @param sha                  File SHA256
	 * @param segmentationItemList Block item list
	 */
	public SegmentationFile(String extName, long totalSize, int blockSize, String sha,
	                        List<SegmentationItem> segmentationItemList) {
		this.extName = extName;
		this.totalSize = totalSize;
		this.blockSize = blockSize;
		this.sha = sha;
		this.segmentationItemList = segmentationItemList;
	}

	/**
	 * Gets total size.
	 *
	 * @return the total size
	 */
	public long getTotalSize() {
		return totalSize;
	}

	/**
	 * Gets block size.
	 *
	 * @return the block size
	 */
	public int getBlockSize() {
		return blockSize;
	}

	/**
	 * Gets ext name.
	 *
	 * @return the ext name
	 */
	public String getExtName() {
		return extName;
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
	 * Gets segmentation item list.
	 *
	 * @return the segmentation item list
	 */
	public List<SegmentationItem> getSegmentationItemList() {
		return segmentationItemList;
	}
}
