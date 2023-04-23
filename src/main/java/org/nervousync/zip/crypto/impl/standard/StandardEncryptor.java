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
package org.nervousync.zip.crypto.impl.standard;

import java.util.Random;

import org.nervousync.commons.core.Globals;
import org.nervousync.zip.engine.ZipCryptoEngine;
import org.nervousync.exceptions.zip.ZipException;
import org.nervousync.zip.crypto.Encryptor;

/**
 * Encryptor implement of the standard
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: Nov 29, 2017 5:08:31 PM $
 */
public final class StandardEncryptor implements Encryptor {

	private final ZipCryptoEngine zipCryptoEngine;
	private byte[] headerBytes;

	/**
	 * Instantiates a new Standard encryptor.
	 *
	 * @param password the password
	 * @param crc      the crc
	 * @throws ZipException the zip exception
	 */
	public StandardEncryptor(char[] password, int crc) throws ZipException {
		if (password == null || password.length == 0) {
			throw new ZipException("input password is null or empty in standard encipher constructor");
		}
		
		this.zipCryptoEngine = new ZipCryptoEngine();
		this.headerBytes = new byte[Globals.STD_DEC_HDR_SIZE];
		this.init(password, crc);
	}
	
	@Override
	public void encryptData(byte[] buff) throws ZipException {
		if (buff == null) {
			throw new NullPointerException();
		}
		encryptData(buff, 0, buff.length);
	}

	@Override
	public void encryptData(byte[] buff, int start, int len) throws ZipException {
		if (len < 0) {
			throw new ZipException("invalid length specified to decrypt data");
		}
		
		try {
			for (int i = start; i <  start + len; i++) {
				buff[i] = encryptByte(buff[i]);
			}
		} catch (Exception e) {
			throw new ZipException(e);
		}
	}

	/**
	 * Get header bytes byte [ ].
	 *
	 * @return the headerBytes
	 */
	public byte[] getHeaderBytes() {
		return headerBytes == null ? new byte[0] : headerBytes.clone();
	}

	private void init(char[] password, int crc) throws ZipException {
		if (password == null || password.length == 0) {
			throw new ZipException("input password is null or empty in standard encipher constructor");
		}
		
		this.zipCryptoEngine.initKeys(password);
		this.headerBytes = this.generateRandomBytes();
		
		this.zipCryptoEngine.initKeys(password);
		
		this.headerBytes[Globals.STD_DEC_HDR_SIZE - 1] = (byte)(crc >>> 24);
		this.headerBytes[Globals.STD_DEC_HDR_SIZE - 2] = (byte)(crc >>> 16);

		this.encryptData(this.headerBytes);
	}
	
	private byte[] generateRandomBytes() throws ZipException {
		byte[] buffer = new byte[Globals.STD_DEC_HDR_SIZE];
		
		Random rand = new Random();
		
		for (int i = 0 ; i < Globals.STD_DEC_HDR_SIZE ; i++) {
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
