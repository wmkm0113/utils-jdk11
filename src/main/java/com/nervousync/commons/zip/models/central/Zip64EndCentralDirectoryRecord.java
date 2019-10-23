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
package com.nervousync.commons.zip.models.central;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Nov 28, 2017 4:28:52 PM $
 */
public final class Zip64EndCentralDirectoryRecord {

	private long signature;
	private long recordSize;
	private int madeVersion;
	private int extractNeeded;
	private int index;
	private int startOfCentralDirectory;
	private long totalEntriesInCentralDirectoryOnThisDisk;
	private long totalEntriesInCentralDirectory;
	private long sizeOfCentralDirectory;
	private long offsetStartCenDirWRTStartDiskNo;
	private byte[] extensibleDataSector;
	
	/**
	 * @return the signature
	 */
	public long getSignature() {
		return signature;
	}

	/**
	 * @param signature the signature to set
	 */
	public void setSignature(long signature) {
		this.signature = signature;
	}

	/**
	 * @return the recordSize
	 */
	public long getRecordSize() {
		return recordSize;
	}

	/**
	 * @param recordSize the recordSize to set
	 */
	public void setRecordSize(long recordSize) {
		this.recordSize = recordSize;
	}

	/**
	 * @return the madeVersion
	 */
	public int getMadeVersion() {
		return madeVersion;
	}

	/**
	 * @param madeVersion the madeVersion to set
	 */
	public void setMadeVersion(int madeVersion) {
		this.madeVersion = madeVersion;
	}

	/**
	 * @return the extractNeeded
	 */
	public int getExtractNeeded() {
		return extractNeeded;
	}

	/**
	 * @param extractNeeded the extractNeeded to set
	 */
	public void setExtractNeeded(int extractNeeded) {
		this.extractNeeded = extractNeeded;
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @param index the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * @return the startOfCentralDirectory
	 */
	public int getStartOfCentralDirectory() {
		return startOfCentralDirectory;
	}

	/**
	 * @param startOfCentralDirectory the startOfCentralDirectory to set
	 */
	public void setStartOfCentralDirectory(int startOfCentralDirectory) {
		this.startOfCentralDirectory = startOfCentralDirectory;
	}

	/**
	 * @return the totalEntriesInCentralDirectoryOnThisDisk
	 */
	public long getTotalEntriesInCentralDirectoryOnThisDisk() {
		return totalEntriesInCentralDirectoryOnThisDisk;
	}

	/**
	 * @param totalEntriesInCentralDirectoryOnThisDisk the totalEntriesInCentralDirectoryOnThisDisk to set
	 */
	public void setTotalEntriesInCentralDirectoryOnThisDisk(long totalEntriesInCentralDirectoryOnThisDisk) {
		this.totalEntriesInCentralDirectoryOnThisDisk = totalEntriesInCentralDirectoryOnThisDisk;
	}

	/**
	 * @return the totalEntriesInCentralDirectory
	 */
	public long getTotalEntriesInCentralDirectory() {
		return totalEntriesInCentralDirectory;
	}

	/**
	 * @param totalEntriesInCentralDirectory the totalEntriesInCentralDirectory to set
	 */
	public void setTotalEntriesInCentralDirectory(long totalEntriesInCentralDirectory) {
		this.totalEntriesInCentralDirectory = totalEntriesInCentralDirectory;
	}

	/**
	 * @return the sizeOfCentralDirectory
	 */
	public long getSizeOfCentralDirectory() {
		return sizeOfCentralDirectory;
	}

	/**
	 * @param sizeOfCentralDirectory the sizeOfCentralDirectory to set
	 */
	public void setSizeOfCentralDirectory(long sizeOfCentralDirectory) {
		this.sizeOfCentralDirectory = sizeOfCentralDirectory;
	}

	/**
	 * @return the offsetStartCenDirWRTStartDiskNo
	 */
	public long getOffsetStartCenDirWRTStartDiskNo() {
		return offsetStartCenDirWRTStartDiskNo;
	}

	/**
	 * @param offsetStartCenDirWRTStartDiskNo the offsetStartCenDirWRTStartDiskNo to set
	 */
	public void setOffsetStartCenDirWRTStartDiskNo(long offsetStartCenDirWRTStartDiskNo) {
		this.offsetStartCenDirWRTStartDiskNo = offsetStartCenDirWRTStartDiskNo;
	}

	/**
	 * @return the extensibleDataSector
	 */
	public byte[] getExtensibleDataSector() {
		return extensibleDataSector == null ? new byte[0] : extensibleDataSector.clone();
	}

	/**
	 * @param extensibleDataSector the extensibleDataSector to set
	 */
	public void setExtensibleDataSector(byte[] extensibleDataSector) {
		this.extensibleDataSector = extensibleDataSector == null ? new byte[0] : extensibleDataSector.clone();
	}
}
