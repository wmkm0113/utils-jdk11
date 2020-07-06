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
package org.nervousync.commons.zip.engine;

import org.nervousync.commons.core.Globals;
import org.nervousync.commons.zip.crypto.engine.MacBasedPRF;
import org.nervousync.commons.zip.crypto.engine.PBKDF2Options;
import org.nervousync.utils.RawUtils;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Nov 30, 2017 4:05:36 PM $
 */
public final class PBKDF2Engine {

	private final PBKDF2Options options;
	private MacBasedPRF macBasedPRF;
	
	public PBKDF2Engine() {
		this.options = null;
		this.macBasedPRF = null;
	}
	
	public PBKDF2Engine(PBKDF2Options options) {
		this.options = options;
		this.macBasedPRF = new MacBasedPRF(this.options.getHashAlgorithm());
	}
	
	public byte[] deriveKey(char[] password) {
		return this.deriveKey(password, 0);
	}
	
	public byte[] deriveKey(char[] password, int dkLen) {
		if (password == null || password.length == 0) {
			throw new NullPointerException();
		}
		
		byte[] passwordBytes = RawUtils.convertCharArrayToByteArray(password);
		this.assertPRF(passwordBytes);
		
		if (dkLen == 0) {
			dkLen = this.macBasedPRF.getLength();
		}
		
		return this.PBKDF2(this.options.getSalt(), dkLen);
	}
	
	public boolean verifyKey(char[] password) {
		byte[] referenceKey = this.options.getDerivedKey();
		if (referenceKey == null || referenceKey.length == 0) {
			return Globals.DEFAULT_VALUE_BOOLEAN;
		}
		
		byte[] passwordBytes = this.deriveKey(password, referenceKey.length);
		
		if (passwordBytes.length != referenceKey.length) {
			return Globals.DEFAULT_VALUE_BOOLEAN;
		}
		
		for (int i = 0 ; i < passwordBytes.length ; i++) {
			if (passwordBytes[i] != referenceKey[i]) {
				return Globals.DEFAULT_VALUE_BOOLEAN;
			}
		}
		
		return true;
	}
	
	private void assertPRF(byte[] bytes) {
		if (this.macBasedPRF == null) {
			this.macBasedPRF = new MacBasedPRF(this.options.getHashAlgorithm());
		}
		this.macBasedPRF.init(bytes);
	}
	
	private byte[] PBKDF2(byte[] saltBytes, int dkLen) {
		if (saltBytes == null) {
			saltBytes = new byte[0];
		}
		
		int length = this.macBasedPRF.getLength();
		int l = this.ceil(dkLen, length);
		int r = dkLen - (l - 1) * length;
		byte[] tempBytes = new byte[l * length];
		int offset = 0;
		for (int i = 1 ; i <= l ; i++) {
			process(tempBytes, offset, saltBytes, this.options.getIterationCount(), i);
			offset += length;
		}
		
		if (r < length) {
			byte[] bytes = new byte[dkLen];
			System.arraycopy(tempBytes, 0, bytes, 0, dkLen);
			return bytes;
		}
		return tempBytes;
	}
	
	private int ceil(int a, int b) {
		int m = 0;
		if (a % b > 0) {
			m = 1;
		}
		return a / b + m;
	}
	
	private void process(byte[] dest, int offset, byte[] source, int count, int blockIndex) {
		int length = this.macBasedPRF.getLength();
		byte[] tempBytes = new byte[length];
		
		byte[] intTmpBytes = new byte[source.length + 4];
		System.arraycopy(source, 0, intTmpBytes, 0, source.length);
		INT(intTmpBytes, source.length, blockIndex);
		
		for (int i = 0 ; i < count ; i++) {
			intTmpBytes = this.macBasedPRF.doFinal(intTmpBytes);
			XOR(tempBytes, intTmpBytes);
		}
		System.arraycopy(tempBytes, 0, dest, offset, length);
	}
	
	private void INT(byte[] dest, int offset, int value) {
		dest[offset] = (byte)(value / (Math.pow(256, 3)));
		dest[offset + 1] = (byte)(value / (Math.pow(256, 2)));
		dest[offset + 2] = (byte)(value / (Math.pow(256, 1)));
		dest[offset + 3] = (byte)value;
	}
	
	private void XOR(byte[] dest, byte[] source) {
		for (int i = 0 ; i < dest.length ; i++) {
			dest[i] ^= source[i];
		}
	}
}
