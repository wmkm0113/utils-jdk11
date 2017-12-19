/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervous Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervous Studio.
 */
package com.nervousync.commons.zip.io;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.Deflater;

import com.nervousync.commons.core.Globals;
import com.nervousync.commons.zip.ZipFile;
import com.nervousync.commons.zip.core.ZipConstants;
import com.nervousync.commons.zip.core.ZipOptions;
import com.nervousync.exceptions.zip.ZipException;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Dec 1, 2017 12:19:07 PM $
 */
public class DeflaterOutputStream extends CipherOutputStream {

	private Deflater deflater = null;
	private byte[] buffer = new byte[ZipConstants.BUFFER_SIZE];
	private boolean firstBytesRead = Globals.DEFAULT_VALUE_BOOLEAN;
	
	public DeflaterOutputStream(OutputStream outputStream, ZipFile zipFile) {
		super(outputStream, zipFile);
		this.deflater = new Deflater();
	}

	@Override
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
		if (this.zipOptions.getCompressionMethod() != ZipConstants.COMP_DEFLATE) {
			super.write(b, off, len);
		} else {
			this.deflater.setInput(b, off, len);
			while (!this.deflater.needsInput()) {
				this.deflate();
			}
		}
	}
	
	public void putNextEntry(File file, ZipOptions zipOptions) throws ZipException {
		super.putNextEntry(file, zipOptions);
		
		if (zipOptions.getCompressionMethod() == ZipConstants.COMP_DEFLATE) {
			this.deflater.reset();
			if ((zipOptions.getCompressionLevel() < 0 || zipOptions.getCompressionLevel() > 9) 
					&& zipOptions.getCompressionLevel() != Globals.DEFAULT_VALUE_INT) {
				throw new ZipException("invalid compression level for deflater. compression level should be in the range of 0-9");
			}
			this.deflater.setLevel(zipOptions.getCompressionLevel());
		}
	}
	
	public void closeEntry() throws IOException, ZipException {
		if (this.zipOptions.getCompressionMethod() == ZipConstants.COMP_DEFLATE) {
			if (!this.deflater.finished()) {
				this.deflater.finish();
				while (!this.deflater.finished()) {
					this.deflate();
				}
			}
			this.firstBytesRead = Globals.DEFAULT_VALUE_BOOLEAN;
		}
		super.closeEntry();
	}
	
	private void deflate() throws IOException {
		int length = this.deflater.deflate(this.buffer, 0, this.buffer.length);
		if (length > 0) {
			if (this.deflater.finished()) {
				if (length == 4) {
					return;
				}
				
				if (length < 4) {
					this.decrementCompressedFileSize(4 - length);
					return;
				}
				length -= 4;
			}
			
			if (!this.firstBytesRead) {
				super.write(this.buffer, 2, length - 2);
				this.firstBytesRead = true;
			} else {
				super.write(this.buffer, 0, length);
			}
		}
	}
	
	private void decrementCompressedFileSize(int value) {
		if (value > 0 && value <= this.bytesWrittenForThisFile) {
			this.bytesWrittenForThisFile -= value;
		}
	}
}
