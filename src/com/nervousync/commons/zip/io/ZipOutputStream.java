/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervousync Studio.
 */
package com.nervousync.commons.zip.io;

import java.io.IOException;
import java.io.OutputStream;

import com.nervousync.commons.zip.ZipFile;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Dec 1, 2017 12:37:14 PM $
 */
public class ZipOutputStream extends DeflaterOutputStream {

	public ZipOutputStream(OutputStream outputStream) {
		this(outputStream, null);
	}
	
	public ZipOutputStream(OutputStream outputStream, ZipFile zipFile) {
		super(outputStream, zipFile);
	}
	
	public void write(int value) throws IOException {
		byte[] b = new byte[1];
		b[0] = (byte)value;
		this.write(b, 0, 1);
	}

	@Override
	public void write(byte[] b) throws IOException {
		this.write(b, 0, b.length);
	}
	
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		this.crc.update(b, off, len);
		this.updateTotalBytesRead(len);
		super.write(b, off, len);
	}
}
