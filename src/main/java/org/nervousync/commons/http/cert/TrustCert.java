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

import org.nervousync.exceptions.http.CertInfoException;
import org.nervousync.utils.FileUtils;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.Objects;

/**
 * The type Cert info.
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $Date: 2018-10-30 15:38
 */
public final class TrustCert {

	/**
	 * Certificate full path
	 */
	private byte[] certContent;
	/**
	 * Certificate password
	 */
	private String certPassword;
	
	/**
	 * Default constructor
	 * @param certContent       certificate bytes
	 * @param certPassword      certificate password
	 */
	private TrustCert(byte[] certContent, String certPassword) {
		this.certContent = certContent;
		this.certPassword = certPassword;
	}

	/**
	 * New instance cert info.
	 *
	 * @param certPath     the cert path
	 * @param certPassword the cert password
	 * @return the cert info
	 */
	public static TrustCert newInstance(String certPath, String certPassword) {
		if (FileUtils.isExists(certPath)) {
			try {
				return new TrustCert(FileUtils.readFileBytes(certPath), certPassword);
			} catch (IOException ignored) {
			}
		}
		return null;
	}

	/**
	 * New instance cert info.
	 *
	 * @param certContent  the cert content
	 * @param certPassword the cert password
	 * @return the cert info
	 */
	public static TrustCert newInstance(byte[] certContent, String certPassword) {
		return new TrustCert(certContent, certPassword);
	}

	/**
	 * Generate key managers key manager [ ].
	 *
	 * @return the key manager [ ]
	 * @throws CertInfoException the certificate exception
	 */
	public KeyManager[] generateKeyManagers() throws CertInfoException {
		try {
			KeyStore clientStore = KeyStore.getInstance("JKS");
			clientStore.load(new ByteArrayInputStream(this.certContent), this.certPassword.toCharArray());
			KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			keyManagerFactory.init(clientStore, this.certPassword.toCharArray());
			return keyManagerFactory.getKeyManagers();
		} catch (Exception e) {
			throw new CertInfoException("Parse certificate error! ", e);
		}
	}

	/**
	 * Get cert content byte [ ].
	 *
	 * @return the byte [ ]
	 */
	public byte[] getCertContent() {
		return certContent;
	}

	/**
	 * Sets cert content.
	 *
	 * @param certContent the cert content
	 */
	public void setCertContent(byte[] certContent) {
		this.certContent = certContent;
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TrustCert trustCert = (TrustCert) o;
		return Arrays.equals(certContent, trustCert.certContent)
				&& Objects.equals(certPassword, trustCert.certPassword);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(certPassword);
		result = 31 * result + Arrays.hashCode(certContent);
		return result;
	}
}
