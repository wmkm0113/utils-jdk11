/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervous Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervous Studio.
 */
package com.nervousync.commons.zip.io.input;

import java.io.DataInput;
import java.io.EOFException;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import com.nervousync.commons.core.Globals;
import com.nervousync.commons.zip.ZipFile;
import com.nervousync.commons.zip.crypto.Decryptor;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Dec 2, 2017 1:06:01 PM $
 */
public class InflaterInputStream extends PartInputStream {

	private Inflater inflater = null;
	private byte[] buffer = null;
	private byte[] oneByteBuffer = new byte[1];
	private long writeBytes = Globals.DEFAULT_VALUE_LONG;
	private long originalSize = Globals.DEFAULT_VALUE_LONG;
	
	public InflaterInputStream(ZipFile zipFile, DataInput input, 
			long length, long originalSize, Decryptor decryptor, boolean isAESEncryptedFile) {
		super(zipFile, input, length, decryptor, isAESEncryptedFile);
		this.inflater = new Inflater(true);
		this.buffer = new byte[Globals.DEFAULT_BUFFER_SIZE];
		this.writeBytes = 0L;
		this.originalSize = originalSize;
	}
	
	public int read() throws IOException {
		return this.read(this.oneByteBuffer, 0, 1) == Globals.DEFAULT_VALUE_INT ? 
				Globals.DEFAULT_VALUE_INT : this.oneByteBuffer[0] & 0xFF;
	}
	
	@Override
	public int read(byte[] b) throws IOException {
		if (b == null) {
			throw new NullPointerException("Input buffer is null");
		}
		return this.read(b, 0, b.length);
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		if (b == null) {
			throw new NullPointerException("Input buffer is null");
		} else if (off < 0 || len < 0 || off + len > b.length) {
			throw new IndexOutOfBoundsException();
		} else if (b.length == 0) {
			return 0;
		}
		
		try {
			if (this.writeBytes >= this.originalSize) {
				this.finishInflating();
				return Globals.DEFAULT_VALUE_INT;
			}
			
			int readLength = 0;
			while ((readLength = this.inflater.inflate(b, off, len)) == 0) {
				if (this.inflater.finished() || this.inflater.needsDictionary()) {
					this.finishInflating();
					return Globals.DEFAULT_VALUE_INT;
				}

				if (this.inflater.needsInput()) {
					this.fill();
				}
			}
			
			this.writeBytes += readLength;
			return readLength;
		} catch (DataFormatException e) {
			throw new IOException("Invalid data format", e);
		}
	}

	@Override
	public long skip(long length) throws IOException {
		if (length < 0L) {
			throw new IllegalArgumentException("Negative skip length");
		}
		
		int limit = (int)Math.min(length, Integer.MAX_VALUE);
		int total = 0;
		byte[] b = new byte[512];
		while (total < limit) {
			int len = limit - total;
			if (len > b.length) {
				len = b.length;
			}
			len = this.read(b, 0, len);
			if (len == Globals.DEFAULT_VALUE_INT) {
				break;
			}
			total += len;
		}
		return total;
	}

	@Override
	public int available() {
		return this.inflater.finished() ? 0 : 1;
	}
	
	@Override
	public void close() throws IOException {
		this.inflater.end();
		super.close();
	}
	
	private void finishInflating() throws IOException {
		super.seekToEnd();
		this.checkAndReadAESMacBytes();
	}
	
	private void fill() throws IOException {
		int length = super.read(this.buffer, 0, this.buffer.length);
		if (length == Globals.DEFAULT_VALUE_INT) {
			throw new EOFException("Unexpected end of input stream");
		}
		this.inflater.setInput(this.buffer, 0, length);
	}
}
