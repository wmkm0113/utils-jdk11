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
package org.nervousync.commons.beans.files;

import java.io.UnsupportedEncodingException;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.nervousync.commons.beans.core.BeanObject;
import org.nervousync.commons.core.Globals;
import org.nervousync.utils.FileUtils;
import org.nervousync.utils.RawUtils;
import org.nervousync.utils.StringUtils;

/**
 * File identified information
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Aug 11, 2015 11:03:43 AM $
 */
@XmlRootElement(name = "FileExtensionInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public final class FileExtensionInfo extends BeanObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5402883763271419949L;

	/**
	 * File extension name
	 */
	private String extensionName = null;
	/**
	 * Header identified code, convert hex to String
	 */
	private String identifiedCode = null;
	/**
	 * Mime type define
	 */
	private String mimeType = null;
	/**
	 * File type code
	 */
	private int fileType;
	/**
	 * Is printable file
	 */
	private boolean printable = false;
	
	/**
	 * Default Constructor
	 */
	public FileExtensionInfo() {
		
	}
	
	/**
	 * Constructor
	 * @param extensionName			File extension name
	 * @param identifiedCode		File identified code
	 * @param mimeType				File mime type
	 * @param fileType				File type (define with FileUtils.FILE_TYPE_*)
	 * @param printable				File can printable
	 */
	public FileExtensionInfo(String extensionName, String identifiedCode, 
			String mimeType, int fileType, boolean printable) {
		this.extensionName = extensionName;
		this.identifiedCode = identifiedCode;
		this.mimeType = mimeType;
		this.fileType = fileType;
		this.printable = printable;
	}

	/**
	 * Constructor for parse data which read from .dat file
	 * @param fileType				File type (define with FileUtils.FILE_TYPE_*)
	 * @param printable				File can printable
	 * @param contentInfo			File identified information read from .dat file
	 */
	public FileExtensionInfo(int fileType, boolean printable, String contentInfo) {
		this.fileType = fileType;
		this.printable = printable;
		
		String[] splitItems = StringUtils.delimitedListToStringArray(contentInfo, "|");
		
		if (splitItems.length == 3) {
			this.extensionName = splitItems[0];
			this.identifiedCode = splitItems[1];
			this.mimeType = splitItems[2];
		}
	}
	
	/**
	 * @return the Serial Version UID
	 */
	public static long getSerialVersionUID() {
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
	 * @return the printable
	 */
	public boolean isPrintable() {
		return printable;
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
		byte[] dataArray = content.getBytes(Globals.DEFAULT_ENCODING);
		byte[] dataItem = new byte[6 + dataArray.length];
		
		dataItem[0] = (byte)this.fileType;
		dataItem[1] = this.printable ? (byte)Globals.NERVOUSYNC_STATUS_TRUE : (byte)Globals.NERVOUSYNC_STATUS_FALSE;
		RawUtils.writeIntFromLittleEndian(dataItem, 2, dataArray.length);
		RawUtils.writeStringFromLittleEndian(dataItem, 6, content);
		
		return dataItem;
	}
}
