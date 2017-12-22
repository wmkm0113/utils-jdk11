/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervousync Studio.
 */
package com.nervousync.commons.beans.files;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.nervousync.commons.beans.xml.BaseElement;
import com.nervousync.utils.StringUtils;

/**
 * @author wmkm0	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Aug 11, 2015 11:03:43 AM $
 */
@XmlType
@XmlRootElement
public final class FileExtensionInfo extends BaseElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5402883763271419949L;

	@XmlElement
	private String extensionName = null;
	@XmlElement
	private String identifiedCode = null;
	@XmlElement
	private String mimeType = null;
	@XmlElement
	private int fileType;
	@XmlElement
	private boolean printing = false;
	@XmlElement
	private boolean mediaFile = false;
	@XmlElement
	private boolean compressFile = false;
	
	public FileExtensionInfo() {
		
	}
	
	public FileExtensionInfo(String extensionName, String identifiedCode, String mimeType, 
			int fileType, boolean printing, boolean mediaFile, boolean compressFile) {
		this.extensionName = extensionName;
		this.identifiedCode = identifiedCode;
		this.mimeType = mimeType;
		this.fileType = fileType;
		this.printing = printing;
		this.mediaFile = mediaFile;
		this.compressFile = compressFile;
	}

	public FileExtensionInfo(int fileType, boolean printing, boolean mediaFile, 
			boolean compressFile, String contentInfo) {
		this.fileType = fileType;
		this.printing = printing;
		this.mediaFile = mediaFile;
		this.compressFile = compressFile;
		
		String[] splitItems = StringUtils.delimitedListToStringArray(contentInfo, "|");
		
		if (splitItems.length == 3) {
			this.extensionName = splitItems[0];
			this.identifiedCode = splitItems[1];
			this.mimeType = splitItems[2];
		}
	}
	
	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * @return the extName
	 */
	public String getExtensionName() {
		return extensionName;
	}

	/**
	 * @return the identifiedCode
	 */
	public String getIdentifiedCode() {
		return identifiedCode;
	}

	/**
	 * @return the mimeType
	 */
	public String getMimeType() {
		return mimeType;
	}

	/**
	 * @return the fileType
	 */
	public int getFileType() {
		return fileType;
	}

	/**
	 * @return the printing
	 */
	public boolean isPrinting() {
		return printing;
	}

	/**
	 * @return the mediaFile
	 */
	public boolean isMediaFile() {
		return mediaFile;
	}

	/**
	 * @return the compressFile
	 */
	public boolean isCompressFile() {
		return compressFile;
	}
}
