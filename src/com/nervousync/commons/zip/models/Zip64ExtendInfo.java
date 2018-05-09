/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervousync Studio.
 */
package com.nervousync.commons.zip.models;

import com.nervousync.commons.core.Globals;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Nov 28, 2017 4:48:01 PM $
 */
public final class Zip64ExtendInfo {

	private int header = Globals.DEFAULT_VALUE_INT;
	private int size = Globals.DEFAULT_VALUE_INT;
	private long compressedSize = Globals.DEFAULT_VALUE_LONG;
	private long originalSize = Globals.DEFAULT_VALUE_LONG;
	private long offsetLocalHeader = Globals.DEFAULT_VALUE_LONG;
	private int diskNumberStart = Globals.DEFAULT_VALUE_INT;
	
	/**
	 * @return the header
	 */
	public int getHeader() {
		return header;
	}

	/**
	 * @param header the header to set
	 */
	public void setHeader(int header) {
		this.header = header;
	}

	/**
	 * @return the size
	 */
	public int getSize() {
		return size;
	}

	/**
	 * @param size the size to set
	 */
	public void setSize(int size) {
		this.size = size;
	}

	/**
	 * @return the compressedSize
	 */
	public long getCompressedSize() {
		return compressedSize;
	}

	/**
	 * @param compressedSize the compressedSize to set
	 */
	public void setCompressedSize(long compressedSize) {
		this.compressedSize = compressedSize;
	}

	/**
	 * @return the originalSize
	 */
	public long getOriginalSize() {
		return originalSize;
	}

	/**
	 * @param originalSize the originalSize to set
	 */
	public void setOriginalSize(long originalSize) {
		this.originalSize = originalSize;
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
}
