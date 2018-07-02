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
package com.nervousync.commons.zip.crypto.impl;

import java.util.Random;

import com.nervousync.commons.core.zip.ZipConstants;
import com.nervousync.commons.zip.crypto.Encryptor;
import com.nervousync.commons.zip.engine.ZipCryptoEngine;
import com.nervousync.exceptions.zip.ZipException;

/**
 * Encryptor implement of standard
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Nov 29, 2017 5:08:31 PM $
 */
public final class StandardEncryptor implements Encryptor {

	private ZipCryptoEngine zipCryptoEngine;
	private byte[] headerBytes;
	
	public StandardEncryptor(char[] password, int crc) throws ZipException {
		if (password == null || password.length <= 0) {
			throw new ZipException("input password is null or empty in standard encrpyter constructor");
		}
		
		this.zipCryptoEngine = new ZipCryptoEngine();
		this.headerBytes = new byte[ZipConstants.STD_DEC_HDR_SIZE];
		this.init(password, crc);
	}
	
	@Override
	public int encryptData(byte[] buff) throws ZipException {
		if (buff == null) {
			throw new NullPointerException();
		}
		return encryptData(buff, 0, buff.length);
	}

	@Override
	public int encryptData(byte[] buff, int start, int len) throws ZipException {
		if (len < 0) {
			throw new ZipException("invalid length specified to decrpyt data");
		}
		
		try {
			for (int i = start; i <  start + len; i++) {
				buff[i] = encryptByte(buff[i]);
			}
			return len;
		} catch (Exception e) {
			throw new ZipException(e);
		}
	}

	/**
	 * @return the headerBytes
	 */
	public byte[] getHeaderBytes() {
		return headerBytes;
	}

	private void init(char[] password, int crc) throws ZipException {
		if (password == null || password.length <= 0) {
			throw new ZipException("input password is null or empty in standard encrpyter constructor");
		}
		
		this.zipCryptoEngine.initKeys(password);
		this.headerBytes = this.generateRandomBytes(ZipConstants.STD_DEC_HDR_SIZE);
		
		this.zipCryptoEngine.initKeys(password);
		
		this.headerBytes[ZipConstants.STD_DEC_HDR_SIZE - 1] = (byte)(crc >>> 24);
		this.headerBytes[ZipConstants.STD_DEC_HDR_SIZE - 2] = (byte)(crc >>> 16);
		
		if (this.headerBytes.length < ZipConstants.STD_DEC_HDR_SIZE) {
			throw new ZipException("invalid header bytes generated, cannot perform standard encryption");
		}
		
		this.encryptData(this.headerBytes);
	}
	
	private byte[] generateRandomBytes(int size) throws ZipException {
		if (size <= 0) {
			throw new ZipException("size is either 0 or less than 0, cannot generate header for standard encryptor");
		}
		
		byte[] buffer = new byte[size];
		
		Random rand = new Random();
		
		for (int i = 0 ; i < size ; i++) {
			buffer[i] = this.encryptByte((byte)rand.nextInt(256));
		}
		
		return buffer;
	}
	
	private byte encryptByte(byte b) {
		byte temp = (byte)(b ^ this.zipCryptoEngine.decryptByte() & 0xFF);
		this.zipCryptoEngine.updateKeys(b);
		return temp;
	}
}
