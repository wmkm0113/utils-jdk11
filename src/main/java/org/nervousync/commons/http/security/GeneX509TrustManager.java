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
package org.nervousync.commons.http.security;

import org.nervousync.commons.core.Globals;
import org.nervousync.commons.http.cert.TrustCert;
import org.nervousync.exceptions.http.CertInfoException;
import org.nervousync.utils.FileUtils;
import org.nervousync.utils.StringUtils;
import org.nervousync.utils.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

/**
 * The type Gene x 509 trust manager.
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: 12/18/2020 8:51 PM $
 */
public class GeneX509TrustManager implements X509TrustManager {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private static final String DEFAULT_PASSPHRASE = "changeit";

	private final String passPhrase;
	private final List<TrustCert> trustCertList;
	private X509TrustManager trustManager = null;

	private GeneX509TrustManager(String passPhrase,
	                             List<TrustCert> trustCertList) throws CertInfoException {
		this.passPhrase = StringUtils.notBlank(passPhrase) ? passPhrase : DEFAULT_PASSPHRASE;
		this.trustCertList = trustCertList;
		this.initManager();
	}

	/**
	 * Init gene x 509 trust manager.
	 *
	 * @param passPhrase    the pass phrase
	 * @param trustCertList the trust cert list
	 * @return the gene x 509 trust manager
	 * @throws CertInfoException the cert info exception
	 */
	public static GeneX509TrustManager init(String passPhrase, List<TrustCert> trustCertList) throws CertInfoException {
		return new GeneX509TrustManager(passPhrase, trustCertList);
	}

	@Override
	public void checkClientTrusted(X509Certificate[] ax509certificate, String s) throws CertificateException {
		this.trustManager.checkClientTrusted(ax509certificate, s);
	}

	@Override
	public void checkServerTrusted(X509Certificate[] ax509certificate, String s) throws CertificateException {
		this.trustManager.checkServerTrusted(ax509certificate, s);
	}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		return this.trustManager.getAcceptedIssuers();
	}

	private int certIndex(TrustCert currentCert) {
		int i = 0;
		for (TrustCert trustCert : this.trustCertList) {
			if (currentCert.equals(trustCert)) {
				return i;
			}
			i++;
		}
		return Globals.DEFAULT_VALUE_INT;
	}

	private void initManager() throws CertInfoException {
		try {
			KeyStore keyStore = KeyStore.getInstance("JKS");
			if (!FileUtils.isExists(SystemUtils.systemCertPath())) {
				this.logger.warn("System cert file not found!");
			} else {
				keyStore.load(FileUtils.loadFile(SystemUtils.systemCertPath()), this.passPhrase.toCharArray());
			}

			for (TrustCert trustCert : this.trustCertList) {
				keyStore.load(new ByteArrayInputStream(trustCert.getCertContent()),
						trustCert.getCertPassword().toCharArray());
			}

			TrustManagerFactory trustManagerFactory =
					TrustManagerFactory.getInstance("SunX509", "SunJSSE");
			trustManagerFactory.init(keyStore);

			for (TrustManager trustManager : trustManagerFactory.getTrustManagers()) {
				if (trustManager instanceof X509TrustManager) {
					this.trustManager = (X509TrustManager) trustManager;
					return;
				}
			}
		} catch (Exception e) {
			throw new CertInfoException("Initialize trust manager error! ", e);
		}

		throw new CertInfoException("Can't found X509TrustManager");
	}
}
