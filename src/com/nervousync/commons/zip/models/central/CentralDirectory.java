/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervous Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervous Studio.
 */
package com.nervousync.commons.zip.models.central;

import java.util.ArrayList;
import java.util.List;

import com.nervousync.commons.core.Globals;
import com.nervousync.commons.zip.models.header.GeneralFileHeader;
import com.nervousync.utils.FileUtils;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Nov 28, 2017 4:52:40 PM $
 */
public final class CentralDirectory {

	private List<GeneralFileHeader> fileHeaders = null;
	private DigitalSignature digitalSignature = null;
	
	/**
	 * @return the fileHeaders
	 */
	public List<GeneralFileHeader> getFileHeaders() {
		return fileHeaders;
	}

	/**
	 * @param fileHeaders the fileHeaders to set
	 */
	public void setFileHeaders(List<GeneralFileHeader> fileHeaders) {
		this.fileHeaders = fileHeaders;
	}

	/**
	 * @return the digitalSignature
	 */
	public DigitalSignature getDigitalSignature() {
		return digitalSignature;
	}

	/**
	 * @param digitalSignature the digitalSignature to set
	 */
	public void setDigitalSignature(DigitalSignature digitalSignature) {
		this.digitalSignature = digitalSignature;
	}
	
	public List<String> listFolderGeneralFileHeaders(String folderPath) {
		List<String> headerList = new ArrayList<String>();
		GeneralFileHeader folderFileHeader = this.retrieveGeneralFileHeader(folderPath);
		if (folderFileHeader != null && folderFileHeader.isDirectory()) {
			for (GeneralFileHeader generalFileHeader : this.fileHeaders) {
				if (FileUtils.matchFolder(generalFileHeader.getEntryPath(), folderPath)) {
					if (generalFileHeader.isDirectory()) {
						headerList.addAll(this.listFolderGeneralFileHeaders(generalFileHeader.getEntryPath()));
					} else {
						headerList.add(generalFileHeader.getEntryPath());
					}
				}
			}
			headerList.add(folderFileHeader.getEntryPath());
		}
		return headerList;
	}

	public GeneralFileHeader retrieveGeneralFileHeader(String entryPath) {
		if (this.fileHeaders != null && this.fileHeaders.size() > 0) {
			for (GeneralFileHeader generalFileHeader : this.fileHeaders) {
				if (FileUtils.matchFilePath(generalFileHeader.getEntryPath(), entryPath, true)) {
					return generalFileHeader;
				}
			}
		}
		return null;
	}
	
	public int retrieveIndexOfGeneralFileHeader(GeneralFileHeader generalFileHeader) {
		if (this.fileHeaders != null && this.fileHeaders.size() > 0) {
			for (int index = 0 ; index < this.fileHeaders.size() ; index++) {
				if (FileUtils.matchFilePath(generalFileHeader.getEntryPath(), 
						this.fileHeaders.get(index).getEntryPath(), true)) {
					return index;
				}
			}
		}
		return Globals.DEFAULT_VALUE_INT;
	}
}
