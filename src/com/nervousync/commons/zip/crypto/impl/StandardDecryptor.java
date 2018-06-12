/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervousync Studio.
 */
package com.nervousync.commons.zip.crypto.impl;

import com.nervousync.commons.core.zip.ZipConstants;
import com.nervousync.commons.zip.crypto.Decryptor;
import com.nervousync.commons.zip.engine.ZipCryptoEngine;
import com.nervousync.commons.zip.models.header.LocalFileHeader;
import com.nervousync.exceptions.zip.ZipException;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Dec 2, 2017 12:23:51 PM $
 */
public class StandardDecryptor implements Decryptor {

	private LocalFileHeader localFileHeader;
	private byte[] crc = new byte[4];
	private ZipCryptoEngine zipCryptoEngine;
	
	public StandardDecryptor(LocalFileHeader localFileHeader, 
			byte[] decryptorHeader) throws ZipException {
		if (localFileHeader == null) {
			throw new ZipException("General file header is null!");
		}
		
		this.localFileHeader = localFileHeader;
		this.zipCryptoEngine = new ZipCryptoEngine();
		
		byte[] crcBuffer = localFileHeader.getCrcBuffer();
		
		this.crc[3] = (byte)(crcBuffer[3] & 0xFF);
		this.crc[2] = (byte)((crcBuffer[3] >> 8) & 0xFF);
		this.crc[1] = (byte)((crcBuffer[3] >> 16) & 0xFF);
		this.crc[0] = (byte)((crcBuffer[3] >> 24) & 0xFF);
		
		if (this.crc[2] > 0 || this.crc[1] > 0 || this.crc[0] > 0) {
			throw new IllegalStateException("Invalid CRC in file header");
		}
		
		if (this.localFileHeader.getPassword() == null 
				|| this.localFileHeader.getPassword().length == 0) {
			throw new ZipException("Wrong password");
		}
		
		this.zipCryptoEngine.initKeys(this.localFileHeader.getPassword());
		
		try {
			int result = decryptorHeader[0];
			for (int i = 0 ; i < ZipConstants.STD_DEC_HDR_SIZE ; i++) {
				this.zipCryptoEngine.updateKeys((byte)(result ^ this.zipCryptoEngine.decryptByte()));
				if ((i + 1) != ZipConstants.STD_DEC_HDR_SIZE) {
					result = decryptorHeader[i + 1];
				}
			}
		} catch (Exception e) {
			if (e instanceof ZipException) {
				throw (ZipException)e;
			} else {
				throw new ZipException(e);
			}
		}
	}

	@Override
	public int decryptData(byte[] buff) throws ZipException {
		return this.decryptData(buff, 0, buff.length);
	}
	
	@Override
	public int decryptData(byte[] buff, int start, int len) throws ZipException {
		if (start < 0 || len < 0) {
			throw new ZipException("input argument error!");
		}
		
		try {
			for (int i = start ; i < start + len ; i++) {
				int value = buff[i] & 0xFF;
				value = (value ^ this.zipCryptoEngine.decryptByte()) & 0xFF;
				this.zipCryptoEngine.updateKeys((byte)value);
				buff[i] = (byte)value;
			}
			return len;
		} catch (Exception e) {
			if (e instanceof ZipException) {
				throw (ZipException)e;
			} else {
				throw new ZipException(e);
			}
		}
	}

}
