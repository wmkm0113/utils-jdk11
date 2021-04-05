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
package org.nervousync.zip.models.header;

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
		return internalFileAttr == null ? new byte[0] : internalFileAttr.clone();
	}

	/**
	 * @param internalFileAttr the internalFileAttr to set
	 */
	public void setInternalFileAttr(byte[] internalFileAttr) {
		this.internalFileAttr = internalFileAttr == null ? new byte[0] : internalFileAttr.clone();
	}

	/**
	 * @return the externalFileAttr
	 */
	public byte[] getExternalFileAttr() {
		return externalFileAttr == null ? new byte[0] : externalFileAttr.clone();
	}

	/**
	 * @param externalFileAttr the externalFileAttr to set
	 */
	public void setExternalFileAttr(byte[] externalFileAttr) {
		this.externalFileAttr = externalFileAttr == null ? new byte[0] : externalFileAttr.clone();
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
