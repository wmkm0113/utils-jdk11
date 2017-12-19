/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervous Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervous Studio.
 */
package com.nervousync.commons.zip.io.input;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;

import com.nervousync.commons.core.Globals;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Dec 2, 2017 10:29:09 AM $
 */
public class ZipInputStream extends InputStream {

	private CRC32 crc = null;
	private InputStream inputStream = null;
	
	public ZipInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
		this.crc = new CRC32();
	}
	
	@Override
	public int read() throws IOException {
		int readByte = this.inputStream.read();
		if (readByte != Globals.DEFAULT_VALUE_INT) {
			this.crc.update(readByte);
		}
		return readByte;
	}
	
	@Override
	public int read(byte[] b) throws IOException {
		return this.read(b, 0, b.length);
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int readLength = this.inputStream.read(b, off, len);
		if (readLength != Globals.DEFAULT_VALUE_INT) {
			this.crc.update(b, off, readLength);
		}
		return readLength;
	}
	
	@Override
	public void close() throws IOException {
		this.inputStream.close();
	}
	
	public long crcValue() {
		return this.crc.getValue();
	}
}
