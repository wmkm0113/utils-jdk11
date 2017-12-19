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
 * @version $Revision: 1.0 $ $Date: Nov 28, 2017 4:34:55 PM $
 */
public final class EndCentralDirectoryRecord {

	private long signature;
	private int indexOfThisDisk;
	private int indexOfThisDiskStartOfCentralDirectory;
	private int totalOfEntriesInCentralDirectoryOnThisDisk;
	private int totalOfEntriesInCentralDirectory;
	private int sizeOfCentralDirectory;
	private long offsetOfStartOfCentralDirectory;
	private int commentLength;
	private byte[] commentBytes;

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
	 * @return the indexOfThisDisk
	 */
	public int getIndexOfThisDisk() {
		return indexOfThisDisk;
	}

	/**
	 * @param indexOfThisDisk the indexOfThisDisk to set
	 */
	public void setIndexOfThisDisk(int indexOfThisDisk) {
		this.indexOfThisDisk = indexOfThisDisk;
	}

	/**
	 * @return the indexOfThisDiskStartOfCentralDirectory
	 */
	public int getIndexOfThisDiskStartOfCentralDirectory() {
		return indexOfThisDiskStartOfCentralDirectory;
	}

	/**
	 * @param indexOfThisDiskStartOfCentralDirectory the indexOfThisDiskStartOfCentralDirectory to set
	 */
	public void setIndexOfThisDiskStartOfCentralDirectory(int indexOfThisDiskStartOfCentralDirectory) {
		this.indexOfThisDiskStartOfCentralDirectory = indexOfThisDiskStartOfCentralDirectory;
	}

	/**
	 * @return the totalOfEntriesInCentralDirectoryOnThisDisk
	 */
	public int getTotalOfEntriesInCentralDirectoryOnThisDisk() {
		return totalOfEntriesInCentralDirectoryOnThisDisk;
	}

	/**
	 * @param totalOfEntriesInCentralDirectoryOnThisDisk the totalOfEntriesInCentralDirectoryOnThisDisk to set
	 */
	public void setTotalOfEntriesInCentralDirectoryOnThisDisk(int totalOfEntriesInCentralDirectoryOnThisDisk) {
		this.totalOfEntriesInCentralDirectoryOnThisDisk = totalOfEntriesInCentralDirectoryOnThisDisk;
	}

	/**
	 * @return the totalOfEntriesInCentralDirectory
	 */
	public int getTotalOfEntriesInCentralDirectory() {
		return totalOfEntriesInCentralDirectory;
	}

	/**
	 * @param totalOfEntriesInCentralDirectory the totalOfEntriesInCentralDirectory to set
	 */
	public void setTotalOfEntriesInCentralDirectory(int totalOfEntriesInCentralDirectory) {
		this.totalOfEntriesInCentralDirectory = totalOfEntriesInCentralDirectory;
	}

	/**
	 * @return the sizeOfCentralDirectory
	 */
	public int getSizeOfCentralDirectory() {
		return sizeOfCentralDirectory;
	}

	/**
	 * @param sizeOfCentralDirectory the sizeOfCentralDirectory to set
	 */
	public void setSizeOfCentralDirectory(int sizeOfCentralDirectory) {
		this.sizeOfCentralDirectory = sizeOfCentralDirectory;
	}

	/**
	 * @return the offsetOfStartOfCentralDirectory
	 */
	public long getOffsetOfStartOfCentralDirectory() {
		return offsetOfStartOfCentralDirectory;
	}

	/**
	 * @param offsetOfStartOfCentralDirectory the offsetOfStartOfCentralDirectory to set
	 */
	public void setOffsetOfStartOfCentralDirectory(long offsetOfStartOfCentralDirectory) {
		this.offsetOfStartOfCentralDirectory = offsetOfStartOfCentralDirectory;
	}

	/**
	 * @return the commentLength
	 */
	public int getCommentLength() {
		return commentLength;
	}

	/**
	 * @param commentLength the commentLength to set
	 */
	public void setCommentLength(int commentLength) {
		this.commentLength = commentLength;
	}

	/**
	 * @return the commentBytes
	 */
	public byte[] getCommentBytes() {
		return commentBytes;
	}

	/**
	 * @param commentBytes the commentBytes to set
	 */
	public void setCommentBytes(byte[] commentBytes) {
		this.commentBytes = commentBytes;
	}
}
