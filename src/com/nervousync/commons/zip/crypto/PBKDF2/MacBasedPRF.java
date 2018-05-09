/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervousync Studio.
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
	private String algorithm;
	
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
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (NoSuchProviderException e) {
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
