/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervousync Studio.
 */
package com.nervousync.commons.zip.io.input;

import java.io.IOException;
import java.io.InputStream;

import com.nervousync.commons.core.Globals;
import com.nervousync.commons.io.NervousyncRandomAccessFile;
import com.nervousync.commons.zip.ZipFile;
import com.nervousync.commons.zip.core.ZipConstants;
import com.nervousync.commons.zip.crypto.Decryptor;
import com.nervousync.commons.zip.crypto.impl.AESDecryptor;
import com.nervousync.exceptions.zip.ZipException;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Dec 2, 2017 10:30:23 AM $
 */
public class PartInputStream extends InputStream {

	private ZipFile zipFile = null;
	private NervousyncRandomAccessFile input = null;
	private long readBytes = Globals.DEFAULT_VALUE_LONG;
	private long length = Globals.DEFAULT_VALUE_LONG;
	private Decryptor decryptor = null;
	private byte[] oneByteBuffer = new byte[1];
	private byte[] aesBlockBuffer = new byte[ZipConstants.AES_BLOCK_SIZE];
	private int aesBytesReturned = 0;
	private boolean isAESEncryptedFile = Globals.DEFAULT_VALUE_BOOLEAN;
	private int count = Globals.DEFAULT_VALUE_INT;
	
	public PartInputStream(ZipFile zipFile, NervousyncRandomAccessFile input, 
			long length, Decryptor decryptor, boolean isAESEncryptedFile) {
		this.zipFile = zipFile;
		this.input = input;
		this.readBytes = 0L;
		this.length = length;
		this.decryptor = decryptor;
		this.isAESEncryptedFile = isAESEncryptedFile;
	}
	
	@Override
	public int read() throws IOException {
		if (this.readBytes >= this.length) {
			return Globals.DEFAULT_VALUE_INT;
		}
		
		if (this.isAESEncryptedFile) {
			if (this.aesBytesReturned == 0 || this.aesBytesReturned == 16) {
				if (this.read(this.aesBlockBuffer) == Globals.DEFAULT_VALUE_INT) {
					return Globals.DEFAULT_VALUE_INT;
				}
				this.aesBytesReturned = 0;
			}
			return (this.aesBlockBuffer[this.aesBytesReturned++] & 0xFF);
		} else {
			return this.read(this.oneByteBuffer, 0, 1) == Globals.DEFAULT_VALUE_INT ? 
					Globals.DEFAULT_VALUE_INT : (this.oneByteBuffer[0] & 0xFF);
		}
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		if (len > (this.length - this.readBytes)) {
			len = (int)(this.length - this.readBytes);
			
			if (len == 0) {
				this.checkAndReadAESMacBytes();
				return Globals.DEFAULT_VALUE_INT;
			}
		}
		
		if (this.decryptor instanceof AESDecryptor) {
			if ((this.readBytes + len) < this.length
					&& (len % 16 != 0)) {
				len -= (len % 16);
			}
		}
		
		synchronized (this.input) {
			this.count = this.input.read(b, off, len);
			if ((this.count < len) && this.zipFile.isSplitArchive()) {
				this.input.close();
				this.input = this.zipFile.startNextSplitFile();
				
				if (this.count < 0) {
					this.count = 0;
				}
				
				int readCount = this.input.read(b, this.count, len - this.count);
				if (readCount > 0) {
					this.count += readCount;
				}
			}
		}
		
		if (this.count > 0) {
			if (this.decryptor != null) {
				try {
					this.decryptor.decryptData(b, off, this.count);
				} catch (ZipException e) {
					throw new IOException(e);
				}
			}
			
			this.readBytes += this.count;
		}
		
		if (this.readBytes >= this.length) {
			this.checkAndReadAESMacBytes();
		}
		
		return this.count;
	}
	
	public int available() {
		long amount = this.length - this.readBytes;
		if (amount > Integer.MAX_VALUE) {
			return Integer.MAX_VALUE;
		}
		return (int)amount;
	}
	
	public long skip(long length) throws IOException {
		if (length < 0L) {
			throw new IllegalArgumentException();
		}
		
		if (length > (this.length - this.readBytes)) {
			length = this.length - this.readBytes;
		}
		
		this.readBytes += length;
		return length;
	}
	
	public void seek(long pos) throws IOException {
		this.input.seek(pos);
	}
	
	public void close() throws IOException {
		this.input.close();
	}
	
	protected void seekToEnd() throws IOException {
		this.seek(this.length);
	}
	
	protected void checkAndReadAESMacBytes() throws IOException {
		if (this.isAESEncryptedFile && this.decryptor != null 
				&& (this.decryptor instanceof AESDecryptor)) {
			if (((AESDecryptor)this.decryptor).getStoredMac() != null) {
				//	Store mac already set
				return;
			}
			
			byte[] storedMac = new byte[ZipConstants.AES_AUTH_LENGTH];
			int readLength = this.input.read(storedMac);
			
			if (readLength != ZipConstants.AES_AUTH_LENGTH) {
				if (this.zipFile.isSplitArchive()) {
					this.input.close();
					this.input = this.zipFile.startNextSplitFile();
					int newReadLength = this.input.read(storedMac, 
							readLength, ZipConstants.AES_AUTH_LENGTH - readLength);
					
					readLength += newReadLength;
				} else {
					throw new ZipException("Error occured while reading stored AES authentication bytes");
				}
			}
			
			((AESDecryptor)this.decryptor).setStoredMac(storedMac);
		}
	}
}
