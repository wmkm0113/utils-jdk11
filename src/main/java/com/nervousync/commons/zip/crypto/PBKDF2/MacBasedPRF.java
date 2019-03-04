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
package com.nervousync.commons.zip.crypto.PBKDF2;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Nov 30, 2017 2:46:56 PM $
 */
public final class MacBasedPRF {

	private Mac mac;
	private int length;
	private final String algorithm;
	
	public MacBasedPRF(String algorithm) {
		this.algorithm = algorithm;
		try {
			this.mac = Mac.getInstance(this.algorithm);
			this.length = this.mac.getMacLength();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
	
	public MacBasedPRF(String algorithm, String provider) {
		this.algorithm = algorithm;
		try {
			this.mac = Mac.getInstance(this.algorithm, provider);
			this.length = this.mac.getMacLength();
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			throw new RuntimeException(e);
		}
	}
	
	public byte[] doFinal() {
		return this.mac.doFinal();
	}
	
	public byte[] doFinal(byte[] bytes) {
		return this.mac.doFinal(bytes);
	}
	
	public void init(byte[] bytes) {
		try {
			this.mac.init(new SecretKeySpec(bytes, this.algorithm));
		} catch (InvalidKeyException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void update(byte[] bytes) {
		try {
			this.mac.update(bytes);
		} catch (IllegalStateException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void update(byte[] bytes, int offset, int length) {
		try {
			this.mac.update(bytes, offset, length);
		} catch (IllegalStateException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @return the length
	 */
	public int getLength() {
		return length;
	}
}
