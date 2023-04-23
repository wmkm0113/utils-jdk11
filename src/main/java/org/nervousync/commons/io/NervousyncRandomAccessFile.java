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
package org.nervousync.commons.io;

import java.io.Closeable;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.nervousync.commons.core.Globals;
import org.nervousync.utils.FileUtils;

import jcifs.smb.SmbRandomAccessFile;

/**
 * RandomAccessFile Supported local files and NAS files
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: Dec 22, 2017 11:49:46 AM $
 */
public class NervousyncRandomAccessFile implements DataInput, DataOutput, Closeable {
	
	/**
	 * Operate the file path
	 */
	private final String filePath;
	private final String domain;
	private final String userName;
	private final String passWord;
	/**
	 * The object of RandomAccessFile/SmbRandomAccessFile
	 */
	private Object originObject = null;

	/**
	 * Constructor
	 *
	 * @param filePath target file path
	 * @throws FileNotFoundException if target file was not found
	 */
	public NervousyncRandomAccessFile(final String filePath) throws FileNotFoundException {
		this(filePath, Globals.DEFAULT_VALUE_STRING, Globals.DEFAULT_VALUE_STRING, Globals.DEFAULT_VALUE_STRING);
	}

	/**
	 * Constructor
	 *
	 * @param filePath target file path
	 * @param writable the writable
	 * @throws FileNotFoundException if target file was not found
	 */
	public NervousyncRandomAccessFile(final String filePath, final boolean writable) throws FileNotFoundException {
		this(filePath, writable, Globals.DEFAULT_VALUE_STRING, Globals.DEFAULT_VALUE_STRING, Globals.DEFAULT_VALUE_STRING);
	}

	/**
	 * Constructor for open SMB file
	 *
	 * @param filePath SMB path
	 * @param domain   SMB Authentication Domain
	 * @param userName SMB Authentication Username
	 * @param passWord SMB Authentication Password
	 * @throws FileNotFoundException if connect to SMB file error
	 */
	public NervousyncRandomAccessFile(final String filePath, final String domain,
	                                  final String userName, final String passWord) throws FileNotFoundException {
		this(filePath, Boolean.FALSE, domain, userName, passWord);
	}

	/**
	 * Constructor for open SMB file
	 *
	 * @param filePath SMB path
	 * @param writable the writable
	 * @param domain   SMB Authentication Domain
	 * @param userName SMB Authentication Username
	 * @param passWord SMB Authentication Password
	 * @throws FileNotFoundException if connect to SMB file error
	 */
	public NervousyncRandomAccessFile(final String filePath, final boolean writable, final String domain,
	                                  final String userName, final String passWord) throws FileNotFoundException {
		this.filePath = filePath;
		if (this.filePath.startsWith(Globals.SAMBA_PROTOCOL)) {
			this.domain = domain;
			this.userName = userName;
			this.passWord = passWord;
		} else {
			this.domain = Globals.DEFAULT_VALUE_STRING;
			this.userName = Globals.DEFAULT_VALUE_STRING;
			this.passWord = Globals.DEFAULT_VALUE_STRING;
		}
		this.openFile(writable ? Globals.WRITE_MODE : Globals.READ_MODE);
	}

	/**
	 * Read file length
	 *
	 * @return file length
	 * @throws IOException If read file length failed
	 */
	public long length() throws IOException {
		return FileUtils.fileSize(this.filePath,
				FileUtils.generateContext(FileUtils.smbAuthenticator(this.domain, this.userName, this.passWord)));
	}

	/**
	 * Gets the file path.
	 *
	 * @return the file path
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * Return current file pointer position
	 *
	 * @return file pointer position
	 * @throws IOException Retrieve position failed
	 */
	public long getFilePointer() throws IOException {
		if (this.originObject instanceof SmbRandomAccessFile) {
			return ((SmbRandomAccessFile)this.originObject).getFilePointer();
		} else {
			return ((RandomAccessFile)this.originObject).getFilePointer();
		}
	}

	/**
	 * Set the file-pointer to position
	 *
	 * @param pos target position
	 * @throws IOException Seek position failed
	 */
	public void seek(long pos) throws IOException {
		if (this.originObject instanceof SmbRandomAccessFile) {
			((SmbRandomAccessFile)this.originObject).seek(pos);
		} else {
			((RandomAccessFile)this.originObject).seek(pos);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.Closeable#close()
	 */
	@Override
	public void close() throws IOException {
		if (this.originObject instanceof SmbRandomAccessFile) {
			((SmbRandomAccessFile)this.originObject).close();
		} else {
			((RandomAccessFile)this.originObject).close();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.DataOutput#write(int)
	 */
	@Override
	public void write(int b) throws IOException {
		if (this.originObject instanceof SmbRandomAccessFile) {
			((SmbRandomAccessFile)this.originObject).write(b);
		} else {
			((RandomAccessFile)this.originObject).write(b);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.DataOutput#write(byte[])
	 */
	@Override
	public void write(byte[] b) throws IOException {
		if (this.originObject instanceof SmbRandomAccessFile) {
			((SmbRandomAccessFile)this.originObject).write(b);
		} else {
			((RandomAccessFile)this.originObject).write(b);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.DataOutput#write(byte[], int, int)
	 */
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		if (this.originObject instanceof SmbRandomAccessFile) {
			((SmbRandomAccessFile)this.originObject).write(b, off, len);
		} else {
			((RandomAccessFile)this.originObject).write(b, off, len);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.DataOutput#writeBoolean(boolean)
	 */
	@Override
	public void writeBoolean(boolean v) throws IOException {
		if (this.originObject instanceof SmbRandomAccessFile) {
			((SmbRandomAccessFile)this.originObject).writeBoolean(v);
		} else {
			((RandomAccessFile)this.originObject).writeBoolean(v);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.DataOutput#writeByte(int)
	 */
	@Override
	public void writeByte(int v) throws IOException {
		if (this.originObject instanceof SmbRandomAccessFile) {
			((SmbRandomAccessFile)this.originObject).writeByte(v);
		} else {
			((RandomAccessFile)this.originObject).writeByte(v);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.DataOutput#writeShort(int)
	 */
	@Override
	public void writeShort(int v) throws IOException {
		if (this.originObject instanceof SmbRandomAccessFile) {
			((SmbRandomAccessFile)this.originObject).writeShort(v);
		} else {
			((RandomAccessFile)this.originObject).writeShort(v);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.DataOutput#writeChar(int)
	 */
	@Override
	public void writeChar(int v) throws IOException {
		if (this.originObject instanceof SmbRandomAccessFile) {
			((SmbRandomAccessFile)this.originObject).writeChar(v);
		} else {
			((RandomAccessFile)this.originObject).writeChar(v);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.DataOutput#writeInt(int)
	 */
	@Override
	public void writeInt(int v) throws IOException {
		if (this.originObject instanceof SmbRandomAccessFile) {
			((SmbRandomAccessFile)this.originObject).writeInt(v);
		} else {
			((RandomAccessFile)this.originObject).writeInt(v);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.DataOutput#writeLong(long)
	 */
	@Override
	public void writeLong(long v) throws IOException {
		if (this.originObject instanceof SmbRandomAccessFile) {
			((SmbRandomAccessFile)this.originObject).writeLong(v);
		} else {
			((RandomAccessFile)this.originObject).writeLong(v);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.DataOutput#writeFloat(float)
	 */
	@Override
	public void writeFloat(float v) throws IOException {
		if (this.originObject instanceof SmbRandomAccessFile) {
			((SmbRandomAccessFile)this.originObject).writeFloat(v);
		} else {
			((RandomAccessFile)this.originObject).writeFloat(v);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.DataOutput#writeDouble(double)
	 */
	@Override
	public void writeDouble(double v) throws IOException {
		if (this.originObject instanceof SmbRandomAccessFile) {
			((SmbRandomAccessFile)this.originObject).writeDouble(v);
		} else {
			((RandomAccessFile)this.originObject).writeDouble(v);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.DataOutput#writeBytes(java.lang.String)
	 */
	@Override
	public void writeBytes(String s) throws IOException {
		if (this.originObject instanceof SmbRandomAccessFile) {
			((SmbRandomAccessFile)this.originObject).writeBytes(s);
		} else {
			((RandomAccessFile)this.originObject).writeBytes(s);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.DataOutput#writeChars(java.lang.String)
	 */
	@Override
	public void writeChars(String s) throws IOException {
		if (this.originObject instanceof SmbRandomAccessFile) {
			((SmbRandomAccessFile)this.originObject).writeChars(s);
		} else {
			((RandomAccessFile)this.originObject).writeChars(s);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.DataOutput#writeUTF(java.lang.String)
	 */
	@Override
	public void writeUTF(String s) throws IOException {
		if (this.originObject instanceof SmbRandomAccessFile) {
			((SmbRandomAccessFile)this.originObject).writeUTF(s);
		} else {
			((RandomAccessFile)this.originObject).writeUTF(s);
		}
	}

	/**
	 * Read data
	 *
	 * @param b read buffer
	 * @return read length
	 * @throws IOException If the first byte cannot be read for any reason other than ends of current file, or if the random access file has been closed, or if some other I/O error occurs.
	 */
	public int read(byte[] b) throws IOException {
		if (this.originObject instanceof SmbRandomAccessFile) {
			return ((SmbRandomAccessFile)this.originObject).read(b, 0, b.length);
		} else {
			return ((RandomAccessFile)this.originObject).read(b, 0, b.length);
		}
	}

	/**
	 * Read data
	 *
	 * @param b   read buffer
	 * @param off buffer offset
	 * @param len read length
	 * @return read length
	 * @throws IOException If the first byte cannot be read for any reason other than ends of current file, or if the random access file has been closed, or if some other I/O error occurs.
	 */
	public int read(byte[] b, int off, int len) throws IOException {
		if (this.originObject instanceof SmbRandomAccessFile) {
			return ((SmbRandomAccessFile)this.originObject).read(b, off, len);
		} else {
			return ((RandomAccessFile)this.originObject).read(b, off, len);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.DataInput#readFully(byte[])
	 */
	@Override
	public void readFully(byte[] b) throws IOException {
		if (this.originObject instanceof SmbRandomAccessFile) {
			((SmbRandomAccessFile)this.originObject).readFully(b);
		} else {
			((RandomAccessFile)this.originObject).readFully(b);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.DataInput#readFully(byte[], int, int)
	 */
	@Override
	public void readFully(byte[] b, int off, int len) throws IOException {
		if (this.originObject instanceof SmbRandomAccessFile) {
			((SmbRandomAccessFile)this.originObject).readFully(b, off, len);
		} else {
			((RandomAccessFile)this.originObject).readFully(b, off, len);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.DataInput#skipBytes(int)
	 */
	@Override
	public int skipBytes(int n) throws IOException {
		if (this.originObject instanceof SmbRandomAccessFile) {
			return ((SmbRandomAccessFile)this.originObject).skipBytes(n);
		} else {
			return ((RandomAccessFile)this.originObject).skipBytes(n);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.DataInput#readBoolean()
	 */
	@Override
	public boolean readBoolean() throws IOException {
		if (this.originObject instanceof SmbRandomAccessFile) {
			return ((SmbRandomAccessFile)this.originObject).readBoolean();
		} else {
			return ((RandomAccessFile)this.originObject).readBoolean();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.DataInput#readByte()
	 */
	@Override
	public byte readByte() throws IOException {
		if (this.originObject instanceof SmbRandomAccessFile) {
			return ((SmbRandomAccessFile)this.originObject).readByte();
		} else {
			return ((RandomAccessFile)this.originObject).readByte();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.DataInput#readUnsignedByte()
	 */
	@Override
	public int readUnsignedByte() throws IOException {
		if (this.originObject instanceof SmbRandomAccessFile) {
			return ((SmbRandomAccessFile)this.originObject).readUnsignedByte();
		} else {
			return ((RandomAccessFile)this.originObject).readUnsignedByte();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.DataInput#readShort()
	 */
	@Override
	public short readShort() throws IOException {
		if (this.originObject instanceof SmbRandomAccessFile) {
			return ((SmbRandomAccessFile)this.originObject).readShort();
		} else {
			return ((RandomAccessFile)this.originObject).readShort();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.DataInput#readUnsignedShort()
	 */
	@Override
	public int readUnsignedShort() throws IOException {
		if (this.originObject instanceof SmbRandomAccessFile) {
			return ((SmbRandomAccessFile)this.originObject).readUnsignedShort();
		} else {
			return ((RandomAccessFile)this.originObject).readUnsignedShort();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.DataInput#readChar()
	 */
	@Override
	public char readChar() throws IOException {
		if (this.originObject instanceof SmbRandomAccessFile) {
			return ((SmbRandomAccessFile)this.originObject).readChar();
		} else {
			return ((RandomAccessFile)this.originObject).readChar();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.DataInput#readInt()
	 */
	@Override
	public int readInt() throws IOException {
		if (this.originObject instanceof SmbRandomAccessFile) {
			return ((SmbRandomAccessFile)this.originObject).readInt();
		} else {
			return ((RandomAccessFile)this.originObject).readInt();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.DataInput#readLong()
	 */
	@Override
	public long readLong() throws IOException {
		if (this.originObject instanceof SmbRandomAccessFile) {
			return ((SmbRandomAccessFile)this.originObject).readLong();
		} else {
			return ((RandomAccessFile)this.originObject).readLong();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.DataInput#readFloat()
	 */
	@Override
	public float readFloat() throws IOException {
		if (this.originObject instanceof SmbRandomAccessFile) {
			return ((SmbRandomAccessFile)this.originObject).readFloat();
		} else {
			return ((RandomAccessFile)this.originObject).readFloat();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.DataInput#readDouble()
	 */
	@Override
	public double readDouble() throws IOException {
		if (this.originObject instanceof SmbRandomAccessFile) {
			return ((SmbRandomAccessFile)this.originObject).readDouble();
		} else {
			return ((RandomAccessFile)this.originObject).readDouble();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.DataInput#readLine()
	 */
	@Override
	public String readLine() throws IOException {
		if (this.originObject instanceof SmbRandomAccessFile) {
			return ((SmbRandomAccessFile)this.originObject).readLine();
		} else {
			return ((RandomAccessFile)this.originObject).readLine();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.DataInput#readUTF()
	 */
	@Override
	public String readUTF() throws IOException {
		if (this.originObject instanceof SmbRandomAccessFile) {
			return ((SmbRandomAccessFile)this.originObject).readUTF();
		} else {
			return ((RandomAccessFile)this.originObject).readUTF();
		}
	}
	
	/**
	 * Open target file
	 * @param mode	Open type(Read-Only/Read-Write)
	 * @throws FileNotFoundException	if target file was not found
	 */
	private void openFile(String mode) throws FileNotFoundException {
		if (this.filePath.startsWith(Globals.SAMBA_PROTOCOL)) {
			try {
				this.originObject = FileUtils.getFile(this.filePath,
						FileUtils.smbAuthenticator(this.domain, this.userName, this.passWord));
			} catch (Exception e) {
				throw new FileNotFoundException("Open file error! File location: " + this.filePath);
			}
		} else {
			this.originObject = new RandomAccessFile(this.filePath, mode);
		}
	}
}
