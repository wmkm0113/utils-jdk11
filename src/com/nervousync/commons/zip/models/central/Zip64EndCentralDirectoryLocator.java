/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervous Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervous Studio.
 */
package com.nervousync.commons.zip.models.central;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Nov 28, 2017 4:32:41 PM $
 */
public final class Zip64EndCentralDirectoryLocator {

	private long signature;
	private int indexOfZip64EndOfCentralDirectoryRecord;
	private long offsetZip64EndOfCentralDirectoryRecord;
	private int totalNumberOfDiscs;
	
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
	 * @return the indexOfZip64EndOfCentralDirectoryRecord
	 */
	public int getIndexOfZip64EndOfCentralDirectoryRecord() {
		return indexOfZip64EndOfCentralDirectoryRecord;
	}

	/**
	 * @param indexOfZip64EndOfCentralDirectoryRecord the indexOfZip64EndOfCentralDirectoryRecord to set
	 */
	public void setIndexOfZip64EndOfCentralDirectoryRecord(int indexOfZip64EndOfCentralDirectoryRecord) {
		this.indexOfZip64EndOfCentralDirectoryRecord = indexOfZip64EndOfCentralDirectoryRecord;
	}

	/**
	 * @return the offsetZip64EndOfCentralDirectoryRecord
	 */
	public long getOffsetZip64EndOfCentralDirectoryRecord() {
		return offsetZip64EndOfCentralDirectoryRecord;
	}

	/**
	 * @param offsetZip64EndOfCentralDirectoryRecord the offsetZip64EndOfCentralDirectoryRecord to set
	 */
	public void setOffsetZip64EndOfCentralDirectoryRecord(long offsetZip64EndOfCentralDirectoryRecord) {
		this.offsetZip64EndOfCentralDirectoryRecord = offsetZip64EndOfCentralDirectoryRecord;
	}

	/**
	 * @return the totalNumberOfDiscs
	 */
	public int getTotalNumberOfDiscs() {
		return totalNumberOfDiscs;
	}

	/**
	 * @param totalNumberOfDiscs the totalNumberOfDiscs to set
	 */
	public void setTotalNumberOfDiscs(int totalNumberOfDiscs) {
		this.totalNumberOfDiscs = totalNumberOfDiscs;
	}
}
