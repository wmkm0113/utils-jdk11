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
package org.nervousync.commons.http.cert;

import org.nervousync.exceptions.http.CertificateException;
import org.nervousync.utils.FileUtils;

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
