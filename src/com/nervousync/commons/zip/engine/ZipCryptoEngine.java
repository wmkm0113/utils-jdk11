/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervous Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervous Studio.
 */
package com.nervousync.commons.zip.engine;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Nov 29, 2017 4:58:52 PM $
 */
public class ZipCryptoEngine {

	private final int keys[] = new int[3];
	private static final int[] CRC_TABLE = new int[256];
	
	static {
		for (int i = 0 ; i < 256 ; i++) {
			int r = i;
			for (int j = 0 ; j < 8 ; j++) {
				if ((r & 1) == 1) {
					r = (r >>> 1) ^ 0xedb88320;
				} else {
					r >>>= 1;
				}
			}
			CRC_TABLE[i] = r;
		}
	}
	
	public ZipCryptoEngine() {
		
	}
	
	public void initKeys(char[] password) {
		this.keys[0] = 305419896;
		this.keys[1] = 591751049;
		this.keys[2] = 878082192;
		
		for (char ch : password) {
			this.updateKeys((byte)(ch & 0xFF));
		}
	}
	
	public void updateKeys(byte b) {
		this.keys[0] = crc32(this.keys[0], b);
		this.keys[1] += this.keys[0] & 0xff;
		this.keys[1] = this.keys[1] * 134775813 + 1;
		this.keys[2] = crc32(this.keys[2], (byte)(this.keys[1] >> 24));
	}
	
	public byte decryptByte() {
		int temp = this.keys[2] | 2;
		return (byte)((temp * (temp ^ 1)) >>> 8);
	}
	
	private int crc32(int currentCrc, byte b) {
		return ((currentCrc >>> 8) ^ CRC_TABLE[(currentCrc ^ b) & 0xFF]);
	}
}
