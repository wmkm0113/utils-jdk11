/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervousync Studio.
 */
package com.nervousync.commons.beans.files;

import java.io.UnsupportedEncodingException;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.nervousync.commons.beans.xml.BaseElement;
import com.nervousync.commons.core.Globals;
import com.nervousync.commons.zip.operator.RawOperator;
import com.nervousync.utils.FileUtils;
import com.nervousync.utils.StringUtils;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
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
	
	public FileExtensionInfo() {
		
	}
	
	public FileExtensionInfo(String extensionName, String identifiedCode, 
			String mimeType, int fileType, boolean printing) {
		this.extensionName = extensionName;
		this.identifiedCode = identifiedCode;
		this.mimeType = mimeType;
		this.fileType = fileType;
		this.printing = printing;
	}

	public FileExtensionInfo(int fileType, boolean printing, String contentInfo) {
		this.fileType = fileType;
		this.printing = printing;
		
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
		return this.fileType == FileUtils.FILE_TYPE_AUDIO 
				|| this.fileType == FileUtils.FILE_TYPE_VIDEO;
	}

	/**
	 * @return the compressFile
	 */
	public boolean isCompressFile() {
		return this.fileType == FileUtils.FILE_TYPE_COMPRESS;
	}
	
	public boolean isPicture() {
		return this.fileType == FileUtils.FILE_TYPE_PIC;
	}
	
	public byte[] convertToByteArray() throws UnsupportedEncodingException {
		String content = this.extensionName + "|" + this.identifiedCode + "|" + this.mimeType;
		byte[] datas = content.getBytes(Globals.DEFAULT_ENCODING);
		byte[] dataItem = new byte[6 + datas.length];
		
		dataItem[0] = (byte)this.fileType;
		dataItem[1] = this.printing ? (byte)Globals.NERVOUSYNC_STATUS_TRUE : (byte)Globals.NERVOUSYNC_STATUS_FALSE;
		RawOperator.writeIntFromLittleEndian(dataItem, 2, datas.length);
		RawOperator.writeStringFromLittleEndian(dataItem, 6, content);
		
		return dataItem;
	}
}
