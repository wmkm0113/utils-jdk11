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
import org.nervousync.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import java.security.cert.X509Certificate;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: 12/18/2020 8:50 PM $
 */
public class GeneHostnameVerifier implements HostnameVerifier {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public boolean verify(String s, SSLSession sslSession) {
		try {
			String hostName = sslSession.getPeerHost();
			for (X509Certificate certificate : (X509Certificate[]) sslSession.getPeerCertificates()) {
				String certDomainNames = certificate.getSubjectX500Principal().getName();
				for (String certDomain : StringUtils.delimitedListToStringArray(certDomainNames, ",")) {
					if (certDomain.startsWith("CN")) {
						if (certDomain.contains(s) && certDomain.contains(hostName)) {
							return true;
						}
					}
				}
			}
		} catch (SSLPeerUnverifiedException e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("Verify host name error! ", e);
			}
		}
		return Globals.DEFAULT_VALUE_BOOLEAN;
	}
}
