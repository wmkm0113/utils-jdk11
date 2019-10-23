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
package com.nervousync.commons.zip.crypto.engine;

import com.nervousync.commons.core.Globals;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Nov 30, 2017 4:00:15 PM $
 */
public final class PBKDF2Options {

	private byte[] salt;
	private int iterationCount;
	private String hashAlgorithm;
	private String hashCharset;
	private byte[] derivedKey;
	
	public PBKDF2Options() {
        this.hashAlgorithm = null;
        this.hashCharset = Globals.DEFAULT_ENCODING;
        this.salt = null;
        this.iterationCount = 1000;
        this.derivedKey = null;
	}
	
	public PBKDF2Options(String hashAlgorithm, String hashCharset, 
			byte[] salt, int iterationCount) {
        this.hashAlgorithm = hashAlgorithm;
        this.hashCharset = hashCharset;
        this.salt = salt == null ? new byte[0] : salt.clone();
        this.iterationCount = iterationCount;
        this.derivedKey = null;
	}
	
	public PBKDF2Options(String hashAlgorithm, String hashCharset, 
			byte[] salt, int iterationCount, byte[] derivedKey) {
        this.hashAlgorithm = hashAlgorithm;
        this.hashCharset = hashCharset;
        this.salt = salt == null ? new byte[0] : salt.clone();
        this.iterationCount = iterationCount;
        this.derivedKey = derivedKey == null ? new byte[0] : derivedKey.clone();
	}

	/**
	 * @return the salt
	 */
	public byte[] getSalt() {
		return salt == null ? new byte[0] : salt.clone();
	}

	/**
	 * @param salt the salt to set
	 */
	public void setSalt(byte[] salt) {
		this.salt = salt == null ? new byte[0] : salt.clone();
	}

	/**
	 * @return the iterationCount
	 */
	public int getIterationCount() {
		return iterationCount;
	}

	/**
	 * @param iterationCount the iterationCount to set
	 */
	public void setIterationCount(int iterationCount) {
		this.iterationCount = iterationCount;
	}

	/**
	 * @return the hashAlgorithm
	 */
	public String getHashAlgorithm() {
		return hashAlgorithm;
	}

	/**
	 * @param hashAlgorithm the hashAlgorithm to set
	 */
	public void setHashAlgorithm(String hashAlgorithm) {
		this.hashAlgorithm = hashAlgorithm;
	}

	/**
	 * @return the hashCharset
	 */
	public String getHashCharset() {
		return hashCharset;
	}

	/**
	 * @param hashCharset the hashCharset to set
	 */
	public void setHashCharset(String hashCharset) {
		this.hashCharset = hashCharset;
	}

	/**
	 * @return the derivedKey
	 */
	public byte[] getDerivedKey() {
		return derivedKey == null ? new byte[0] : derivedKey.clone();
	}

	/**
	 * @param derivedKey the derivedKey to set
	 */
	public void setDerivedKey(byte[] derivedKey) {
		this.derivedKey = derivedKey == null ? new byte[0] : derivedKey.clone();
	}
}
