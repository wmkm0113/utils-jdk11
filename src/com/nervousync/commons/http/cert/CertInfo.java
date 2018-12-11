/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Nervousync Studio.
 */
package com.nervousync.commons.http.cert;

import com.nervousync.exceptions.http.CertificateException;
import com.nervousync.utils.FileUtils;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import java.security.KeyStore;

/**
 * The type Cert info.
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $Date: 2018-10-30 15:38
 */
public final class CertInfo {
	
	/**
	 * Certificate type
	 */
	private String certType;
	/**
	 * Certificate full path
	 */
	private String certPath;
	/**
	 * Certificate password
	 */
	private String certPassword;
	
	/**
	 * Default constructor
	 * @param certPath          certificate file path
	 * @param certPassword      certificate password
	 */
	private CertInfo(String certType, String certPath, String certPassword) {
		this.certType = certType;
		this.certPath = certPath;
		this.certPassword = certPassword;
	}
	
	public static CertInfo newInstance(String certType, String certPath, String certPassword) {
		if (certType != null && certPath != null && certPassword != null
				&& FileUtils.isExists(certPath)) {
			return new CertInfo(certType, certPath, certPassword);
		}
		return null;
	}
	
	public KeyManager[] generateKeyManagers() throws CertificateException {
		try {
			KeyStore clientStore = KeyStore.getInstance(this.certType);
			clientStore.load(FileUtils.loadFile(this.certPath), this.certPassword.toCharArray());
			KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			keyManagerFactory.init(clientStore, this.certPassword.toCharArray());
			return keyManagerFactory.getKeyManagers();
		} catch (Exception e) {
			throw new CertificateException("Parse certificate error! ", e);
		}
	}

	/**
	 * Gets the value of certType.
	 *
	 * @return the value of certType
	 */
	public String getCertType() {
		return certType;
	}

	/**
	 * Sets the certType.
	 *
	 * @param certType certType
	 */
	public void setCertType(String certType) {
		this.certType = certType;
	}

	/**
	 * Gets cert path.
	 *
	 * @return the cert path
	 */
	public String getCertPath() {
		return certPath;
	}
	
	/**
	 * Sets cert path.
	 *
	 * @param certPath the cert path
	 */
	public void setCertPath(String certPath) {
		this.certPath = certPath;
	}
	
	/**
	 * Gets cert password.
	 *
	 * @return the cert password
	 */
	public String getCertPassword() {
		return certPassword;
	}
	
	/**
	 * Sets cert password.
	 *
	 * @param certPassword the cert password
	 */
	public void setCertPassword(String certPassword) {
		this.certPassword = certPassword;
	}
}
