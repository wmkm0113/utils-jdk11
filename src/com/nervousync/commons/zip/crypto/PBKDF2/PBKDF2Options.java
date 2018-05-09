/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervousync Studio.
 */
package com.nervousync.commons.zip.crypto.PBKDF2;

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
        this.salt = salt;
        this.iterationCount = iterationCount;
        this.derivedKey = null;
	}
	
	public PBKDF2Options(String hashAlgorithm, String hashCharset, 
			byte[] salt, int iterationCount, byte[] derivedKey) {
        this.hashAlgorithm = hashAlgorithm;
        this.hashCharset = hashCharset;
        this.salt = salt;
        this.iterationCount = iterationCount;
        this.derivedKey = derivedKey;
	}

	/**
	 * @return the salt
	 */
	public byte[] getSalt() {
		return salt;
	}

	/**
	 * @param salt the salt to set
	 */
	public void setSalt(byte[] salt) {
		this.salt = salt;
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
		return derivedKey;
	}

	/**
	 * @param derivedKey the derivedKey to set
	 */
	public void setDerivedKey(byte[] derivedKey) {
		this.derivedKey = derivedKey;
	}
}
