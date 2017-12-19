/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervous Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervous Studio.
 */
package com.nervousync.commons.zip.models.header;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Nov 28, 2017 4:44:25 PM $
 */
public final class GeneralFileHeader extends FileHeader {

	private int madeVersion;
	private int fileCommentLength;
	private int diskNumberStart;
	private byte[] internalFileAttr;
	private byte[] externalFileAttr;
	private long offsetLocalHeader;
	private String fileComment;
	private boolean isDirectory;
	
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
	 * @return the fileCommentLength
	 */
	public int getFileCommentLength() {
		return fileCommentLength;
	}

	/**
	 * @param fileCommentLength the fileCommentLength to set
	 */
	public void setFileCommentLength(int fileCommentLength) {
		this.fileCommentLength = fileCommentLength;
	}

	/**
	 * @return the diskNumberStart
	 */
	public int getDiskNumberStart() {
		return diskNumberStart;
	}

	/**
	 * @param diskNumberStart the diskNumberStart to set
	 */
	public void setDiskNumberStart(int diskNumberStart) {
		this.diskNumberStart = diskNumberStart;
	}

	/**
	 * @return the internalFileAttr
	 */
	public byte[] getInternalFileAttr() {
		return internalFileAttr;
	}

	/**
	 * @param internalFileAttr the internalFileAttr to set
	 */
	public void setInternalFileAttr(byte[] internalFileAttr) {
		this.internalFileAttr = internalFileAttr;
	}

	/**
	 * @return the externalFileAttr
	 */
	public byte[] getExternalFileAttr() {
		return externalFileAttr;
	}

	/**
	 * @param externalFileAttr the externalFileAttr to set
	 */
	public void setExternalFileAttr(byte[] externalFileAttr) {
		this.externalFileAttr = externalFileAttr;
	}

	/**
	 * @return the offsetLocalHeader
	 */
	public long getOffsetLocalHeader() {
		return offsetLocalHeader;
	}

	/**
	 * @param offsetLocalHeader the offsetLocalHeader to set
	 */
	public void setOffsetLocalHeader(long offsetLocalHeader) {
		this.offsetLocalHeader = offsetLocalHeader;
	}

	/**
	 * @return the fileComment
	 */
	public String getFileComment() {
		return fileComment;
	}

	/**
	 * @param fileComment the fileComment to set
	 */
	public void setFileComment(String fileComment) {
		this.fileComment = fileComment;
	}

	/**
	 * @return the isDirectory
	 */
	public boolean isDirectory() {
		return isDirectory;
	}

	/**
	 * @param isDirectory the isDirectory to set
	 */
	public void setDirectory(boolean isDirectory) {
		this.isDirectory = isDirectory;
	}
}
