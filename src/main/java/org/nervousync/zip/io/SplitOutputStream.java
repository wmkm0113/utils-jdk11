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
package org.nervousync.zip.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteOrder;

import org.nervousync.commons.core.Globals;
import org.nervousync.exceptions.zip.ZipException;
import org.nervousync.commons.io.NervousyncRandomAccessFile;
import org.nervousync.utils.FileUtils;
import org.nervousync.utils.RawUtils;
import org.nervousync.utils.StringUtils;

/**
 * The type Split output stream.
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: Nov 29, 2017 2:57:01 PM $
 */
public class SplitOutputStream extends OutputStream {
	
	private static final long[] HEADER_SIGNATURES = new long[11];
	
	static {
		HEADER_SIGNATURES[0] = Globals.LOCSIG;
		HEADER_SIGNATURES[1] = Globals.EXTSIG;
		HEADER_SIGNATURES[2] = Globals.CENSIG;
		HEADER_SIGNATURES[3] = Globals.ENDSIG;
		HEADER_SIGNATURES[4] = Globals.DIGSIG;
		HEADER_SIGNATURES[5] = Globals.ARCEXTDATREC;
		HEADER_SIGNATURES[6] = Globals.SPLITSIG;
		HEADER_SIGNATURES[7] = Globals.ZIP64ENDCENDIRLOC;
		HEADER_SIGNATURES[8] = Globals.ZIP64ENDCENDIRREC;
		HEADER_SIGNATURES[9] = Globals.EXTRAFIELDZIP64LENGTH;
		HEADER_SIGNATURES[10] = Globals.AESSIG;
	}
	
	private NervousyncRandomAccessFile dataOutput;
	private final String fileName;
	private final String filePath;
	private final String currentFullPath;
	private final long splitLength;
	private int currentSplitFileIndex;
	private long bytesWrittenForThisPart;

	/**
	 * Instantiates a new Split output stream.
	 *
	 * @param filePath the file path
	 * @throws FileNotFoundException the file was not found exception
	 * @throws ZipException          the zip exception
	 */
	public SplitOutputStream(String filePath) throws FileNotFoundException, ZipException {
		this(filePath, Globals.DEFAULT_VALUE_LONG);
	}

	/**
	 * Instantiates a new Split output stream.
	 *
	 * @param savePath    the save path
	 * @param splitLength the split length
	 * @throws FileNotFoundException the file was not found exception
	 * @throws ZipException          the zip exception
	 */
	public SplitOutputStream(String savePath, long splitLength) throws FileNotFoundException, ZipException {
		if (splitLength >= 0 && splitLength < Globals.MIN_SPLIT_LENGTH) {
			throw new ZipException("split length less than minimum allowed split length of " + Globals.MIN_SPLIT_LENGTH +" Bytes");
		}

		int beginIndex;
		if (savePath.startsWith(Globals.SAMBA_PROTOCOL)) {
			beginIndex = savePath.lastIndexOf(Globals.DEFAULT_URL_SEPARATOR);
		} else {
			beginIndex = savePath.lastIndexOf(Globals.DEFAULT_PAGE_SEPARATOR);
		}
		this.filePath = savePath.substring(0, beginIndex);
		this.fileName = StringUtils.stripFilenameExtension(savePath.substring(beginIndex + 1));
		this.dataOutput = new NervousyncRandomAccessFile(savePath, Boolean.TRUE);
		this.currentFullPath = savePath;
		this.splitLength = splitLength;
		this.currentSplitFileIndex = 0;
		this.bytesWrittenForThisPart = 0L;
	}
	
	@Override
	public void write(int b) throws IOException {
		byte[] buffer = new byte[1];
		buffer[0] = (byte)b;
		this.write(buffer, 0, 1);
	}
	
	@Override
	public void write(byte[] b) throws IOException {
		this.write(b, 0, b.length);
	}

	@Override
	public void close() throws IOException {
		this.dataOutput.close();
		super.close();
	}
	
	public void write(byte[] b, int off, int len) throws IOException {
		if (len < 0) {
			return;
		}
		
		if (this.splitLength != Globals.DEFAULT_VALUE_LONG) {
			if (this.splitLength < Globals.MIN_SPLIT_LENGTH) {
				throw new ZipException("split length less than minimum allowed split length of " + Globals.MIN_SPLIT_LENGTH +" Bytes");
			}
			
			if (this.bytesWrittenForThisPart >= this.splitLength) {
				this.startNextSplitFile();
				this.dataOutput.write(b, off, len);
				this.bytesWrittenForThisPart = len;
			} else if (this.bytesWrittenForThisPart + len > this.splitLength) {
				if (this.isHeaderData(b)) {
					this.startNextSplitFile();
					this.dataOutput.write(b, off, len);
					this.bytesWrittenForThisPart = len;
				} else {
					this.dataOutput.write(b, off, (int)(this.splitLength - this.bytesWrittenForThisPart));
					this.startNextSplitFile();
					this.dataOutput.write(b, (int)(this.splitLength - this.bytesWrittenForThisPart), 
							(int)(len - (this.splitLength - this.bytesWrittenForThisPart)));
					this.bytesWrittenForThisPart = len - (this.splitLength - this.bytesWrittenForThisPart);
				}
			} else {
				this.dataOutput.write(b, off, len);
				this.bytesWrittenForThisPart += len;
			}
		} else {
			this.dataOutput.write(b, off, len);
			this.bytesWrittenForThisPart += len;
		}
	}

	/**
	 * Check buffer size and start next split file boolean.
	 *
	 * @param bufferSize the buffer size
	 * @return the boolean
	 * @throws ZipException the zip exception
	 */
	public boolean checkBufferSizeAndStartNextSplitFile(int bufferSize) throws ZipException {
		if (bufferSize < 0) {
			throw new ZipException("negative buffer size for checkBuffSizeAndStartNextSplitFile");
		}
		
		if (!this.isBufferSizeFitForCurrentSplitFile(bufferSize)) {
			try {
				this.startNextSplitFile();
				this.bytesWrittenForThisPart = 0;
				return true;
			} catch (IOException e) {
				throw new ZipException(e);
			}
		}
		return Boolean.FALSE;
	}
	
	private boolean isBufferSizeFitForCurrentSplitFile(int bufferSize) throws ZipException {
		if (bufferSize < 0) {
			throw new ZipException("negative buffer size for checkBuffSizeAndStartNextSplitFile");
		}
		
		if (this.splitLength >= Globals.MIN_SPLIT_LENGTH) {
			return (this.bytesWrittenForThisPart + bufferSize <= this.splitLength);
		} else {
			return true;
		}
	}

	/**
	 * Seek.
	 *
	 * @param pos the pos
	 * @throws IOException the io exception
	 */
	public void seek(long pos) throws IOException {
		this.dataOutput.seek(pos);
	}

	/**
	 * Gets the file pointer.
	 *
	 * @return the file pointer
	 * @throws IOException the io exception
	 */
	public long getFilePointer() throws IOException {
		return this.dataOutput.getFilePointer();
	}
	
	public void flush() {
		
	}

	/**
	 * Is split zip file boolean.
	 *
	 * @return the boolean
	 */
	public boolean isSplitZipFile() {
		return this.splitLength != Globals.DEFAULT_VALUE_LONG;
	}

	/**
	 * Gets split length.
	 *
	 * @return the splitLength
	 */
	public long getSplitLength() {
		return splitLength;
	}

	/**
	 * Gets current split file index.
	 *
	 * @return the currentSplitFileIndex
	 */
	public int getCurrentSplitFileIndex() {
		return currentSplitFileIndex;
	}
	
	private void startNextSplitFile() throws IOException {
		try {
			String folderPath;
			
			if (this.filePath.startsWith(Globals.SAMBA_PROTOCOL)) {
				folderPath = this.filePath + Globals.DEFAULT_URL_SEPARATOR;
			} else {
				folderPath = this.filePath + Globals.DEFAULT_PAGE_SEPARATOR;
			}

			String currentSplitFile;
			if (this.currentSplitFileIndex < 9) {
				currentSplitFile = folderPath + fileName + ".zip.0" + (this.currentSplitFileIndex + 1);
			} else {
				currentSplitFile = folderPath + fileName + ".zip." + (this.currentSplitFileIndex + 1);
			}

			this.dataOutput.close();
			
			if (FileUtils.isExists(currentSplitFile)) {
				throw new IOException("split file: " + currentSplitFile
						+ " already exists in the current directory, cannot rename this file");
			}
			
			if (!FileUtils.moveFile(this.currentFullPath, currentSplitFile)) {
				throw new IOException("Cannot create split file!");
			}

			this.dataOutput = new NervousyncRandomAccessFile(this.currentFullPath, Boolean.TRUE);
			this.currentSplitFileIndex++;
		} catch (ZipException e) {
			throw new IOException(e);
		}
	}
	
	private boolean isHeaderData(byte[] buffer) {
		if (buffer != null && buffer.length >= 4) {
			int signature = RawUtils.readInt(buffer, 0, ByteOrder.LITTLE_ENDIAN);
			
			for (long headerSignature : HEADER_SIGNATURES) {
				if (headerSignature != Globals.SPLITSIG && headerSignature == signature) {
					return Boolean.TRUE;
				}
			}
		}
		return Boolean.FALSE;
	}
}
