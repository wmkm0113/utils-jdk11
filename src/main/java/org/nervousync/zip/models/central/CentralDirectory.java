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
package org.nervousync.zip.models.central;

import java.util.ArrayList;
import java.util.List;

import org.nervousync.commons.core.Globals;
import org.nervousync.zip.models.header.GeneralFileHeader;
import org.nervousync.utils.FileUtils;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Nov 28, 2017 4:52:40 PM $
 */
public final class CentralDirectory {

	private List<GeneralFileHeader> fileHeaders = null;
	private DigitalSignature digitalSignature = null;

	public CentralDirectory() {
	}
	
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
		List<String> headerList = new ArrayList<>();
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
