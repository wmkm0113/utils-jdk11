/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervous Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervous Studio.
 */
package com.nervousync.commons.zip.io.input;

import java.io.DataInput;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import com.nervousync.commons.core.Globals;
import com.nervousync.commons.zip.ZipFile;
import com.nervousync.commons.zip.core.ZipConstants;
import com.nervousync.commons.zip.crypto.Decryptor;
import com.nervousync.commons.zip.crypto.impl.AESDecryptor;
import com.nervousync.exceptions.zip.ZipException;

import jcifs.smb.SmbRandomAccessFile;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Dec 2, 2017 10:30:23 AM $
 */
public class PartInputStream extends InputStream {

	private ZipFile zipFile = null;
	private DataInput input = null;
	private long readBytes = Globals.DEFAULT_VALUE_LONG;
	private long length = Globals.DEFAULT_VALUE_LONG;
	private Decryptor decryptor = null;
	private byte[] oneByteBuffer = new byte[1];
	private byte[] aesBlockBuffer = new byte[ZipConstants.AES_BLOCK_SIZE];
	private int aesBytesReturned = 0;
	private boolean isAESEncryptedFile = Globals.DEFAULT_VALUE_BOOLEAN;
	private int count = Globals.DEFAULT_VALUE_INT;
	
	public PartInputStream(ZipFile zipFile, DataInput input, 
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
			if (this.input instanceof RandomAccessFile) {
				this.count = ((RandomAccessFile)this.input).read(b, off, len);
			} else if (this.input instanceof SmbRandomAccessFile) {
				this.count = ((SmbRandomAccessFile)this.input).read(b, off, len);
			} else {
				throw new IOException("Input stream type error");
			}
			if ((this.count < len) && this.zipFile.isSplitArchive()) {
				if (this.input instanceof RandomAccessFile) {
					((RandomAccessFile)this.input).close();
				}
				this.input = this.zipFile.startNextSplitFile();
				
				if (this.count < 0) {
					this.count = 0;
				}
				
				int readCount = 0;
				if (this.input instanceof RandomAccessFile) {
					readCount = ((RandomAccessFile)this.input).read(b, this.count, len - this.count);
				} else if (this.input instanceof SmbRandomAccessFile) {
					readCount = ((SmbRandomAccessFile)this.input).read(b, this.count, len - this.count);
				} else {
					throw new IOException("Input stream type error");
				}
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
		if (this.input instanceof RandomAccessFile) {
			((RandomAccessFile)input).seek(pos);
		} else if (this.input instanceof SmbRandomAccessFile) {
			((SmbRandomAccessFile)input).seek(pos);
		} else {
			throw new IOException("Parameter input type error!");
		}
	}
	
	public void close() throws IOException {
		if (this.input instanceof RandomAccessFile) {
			((RandomAccessFile)this.input).close();
		} else if (this.input instanceof SmbRandomAccessFile) {
			((SmbRandomAccessFile)input).close();
		} else {
			throw new IOException("Parameter input type error!");
		}
	}
	
	protected void seekToEnd() throws IOException {
		if (this.input instanceof RandomAccessFile) {
			((RandomAccessFile)this.input).seek(this.length);
		} else if (this.input instanceof SmbRandomAccessFile) {
			((SmbRandomAccessFile)this.input).seek(this.length);
		} else {
			throw new IOException("Input stream type error");
		}
	}
	
	protected void checkAndReadAESMacBytes() throws IOException {
		if (this.isAESEncryptedFile && this.decryptor != null 
				&& (this.decryptor instanceof AESDecryptor)) {
			if (((AESDecryptor)this.decryptor).getStoredMac() != null) {
				//	Store mac already set
				return;
			}
			
			byte[] storedMac = new byte[ZipConstants.AES_AUTH_LENGTH];
			int readLength = 0;
			if (this.input instanceof RandomAccessFile) {
				readLength = ((RandomAccessFile)this.input).read(storedMac);
			} else if (this.input instanceof SmbRandomAccessFile) {
				readLength = ((SmbRandomAccessFile)this.input).read(storedMac);
			} else {
				throw new IOException("Input stream type error");
			}
			
			if (readLength != ZipConstants.AES_AUTH_LENGTH) {
				if (this.zipFile.isSplitArchive()) {
					if (this.input instanceof RandomAccessFile) {
						((RandomAccessFile)this.input).close();
					} else if (this.input instanceof SmbRandomAccessFile) {
						((SmbRandomAccessFile)this.input).close();
					} else {
						throw new IOException("Input stream type error");
					}
					this.input = this.zipFile.startNextSplitFile();
					int newReadLength = 0;
					if (this.input instanceof RandomAccessFile) {
						newReadLength = ((RandomAccessFile)this.input).read(storedMac, 
								readLength, ZipConstants.AES_AUTH_LENGTH - readLength);
					} else if (this.input instanceof SmbRandomAccessFile) {
						newReadLength = ((SmbRandomAccessFile)this.input).read(storedMac, 
								readLength, ZipConstants.AES_AUTH_LENGTH - readLength);
					} else {
						throw new IOException("Input stream type error");
					}
					
					readLength += newReadLength;
				} else {
					throw new ZipException("Error occured while reading stored AES authentication bytes");
				}
			}
			
			((AESDecryptor)this.decryptor).setStoredMac(storedMac);
		}
	}
}
