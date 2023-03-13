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
package org.nervousync.mail.config.builder;

import org.nervousync.commons.core.Globals;
import org.nervousync.commons.core.RegexGlobals;
import org.nervousync.commons.proxy.ProxyConfig;
import org.nervousync.exceptions.builder.BuilderException;
import org.nervousync.mail.config.MailConfig;
import org.nervousync.security.factory.SecureFactory;
import org.nervousync.utils.FileUtils;
import org.nervousync.utils.StringUtils;

import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

public final class MailConfigBuilder {

	private String secureName;
	private String userName;
	private String passWord;
	private ProxyConfig proxyConfig = ProxyConfig.redirect();
	private MailConfig.ServerConfig sendConfig;
	private MailConfig.ServerConfig receiveConfig;
	private String storagePath;
	private String certificate;
	private String privateKey;

	/**
	 * Instantiates a new Builder.
	 *
	 * @param mailConfig the mail config
	 */
	private MailConfigBuilder(MailConfig mailConfig) {
		this.secureName = Globals.DEFAULT_VALUE_STRING;
		if (mailConfig != null) {
			this.secureName = mailConfig.getSecureName();
			this.userName = mailConfig.getUserName();
			this.passWord = mailConfig.getPassWord();
			this.sendConfig = mailConfig.getSendConfig();
			this.receiveConfig = mailConfig.getReceiveConfig();
			this.storagePath = mailConfig.getStoragePath();
			this.certificate = mailConfig.getCertificate();
			this.privateKey = mailConfig.getPrivateKey();
		}
	}

	public static MailConfigBuilder newBuilder() {
		return newBuilder(null);
	}

	public static MailConfigBuilder newBuilder(final MailConfig mailConfig) {
		return new MailConfigBuilder(mailConfig);
	}

	public MailConfigBuilder secureName(final String secureName) {
		if (StringUtils.notBlank(secureName) && SecureFactory.getInstance().registeredConfig(secureName)) {
			SecureFactory secureFactory = SecureFactory.getInstance();
			if (StringUtils.notBlank(this.passWord)) {
				this.passWord = secureFactory.update(this.passWord, this.secureName, secureName);
			}
			if (StringUtils.notBlank(this.proxyConfig.getPassword())) {
				this.proxyConfig.setPassword(secureFactory.update(this.proxyConfig.getPassword(), this.secureName, secureName));
			}
			this.secureName = secureName;
		}
		return this;
	}

	/**
	 * Authentication builder.
	 *
	 * @param userName the username
	 * @param passWord the password
	 * @return the builder
	 * @throws BuilderException the builder exception
	 */
	public MailConfigBuilder authentication(final String userName, final String passWord) throws BuilderException {
		if (!StringUtils.matches(userName, RegexGlobals.EMAIL_ADDRESS)) {
			throw new BuilderException("Invalid username");
		}
		this.userName = userName;
		SecureFactory secureFactory = SecureFactory.getInstance();
		if (StringUtils.notBlank(passWord) && StringUtils.notBlank(this.secureName)
				&& secureFactory.registeredConfig(this.secureName)) {
			this.passWord = secureFactory.encrypt(this.secureName, passWord);
		} else {
			this.passWord = passWord;
		}
		return this;
	}

	public ProxyConfigBuilder proxyConfig() {
		return new ProxyConfigBuilder(this, this.secureName, this.proxyConfig);
	}

	public ServerConfigBuilder sendConfig() {
		return new ServerConfigBuilder(this, Boolean.TRUE, this.sendConfig);
	}

	public ServerConfigBuilder receiveConfig() {
		return new ServerConfigBuilder(this, Boolean.FALSE, this.sendConfig);
	}

	/**
	 * Storage path builder.
	 *
	 * @param storagePath the storage path
	 * @return the builder
	 * @throws BuilderException the builder exception
	 */
	public MailConfigBuilder storagePath(String storagePath) throws BuilderException {
		if (StringUtils.isEmpty(storagePath) || !FileUtils.isExists(storagePath)) {
			throw new BuilderException("Storage path not exists! ");
		}
		this.storagePath = storagePath;
		return this;
	}

	/**
	 * Signer builder.
	 *
	 * @param x509Certificate the x 509 certificate
	 * @param privateKey      the private key
	 * @return the builder
	 */
	public MailConfigBuilder signer(final X509Certificate x509Certificate, final PrivateKey privateKey) {
		if (x509Certificate != null && privateKey != null) {
			try {
				this.certificate = StringUtils.base64Encode(x509Certificate.getEncoded());
				this.privateKey = StringUtils.base64Encode(privateKey.getEncoded());
			} catch (CertificateEncodingException e) {
				this.certificate = Globals.DEFAULT_VALUE_STRING;
				this.privateKey = Globals.DEFAULT_VALUE_STRING;
			}
		}
		return this;
	}

	/**
	 * Build mail config.
	 *
	 * @return the mail config
	 * @throws BuilderException the builder exception
	 */
	public MailConfig build() throws BuilderException {
		if (this.sendConfig == null && this.receiveConfig == null) {
			throw new BuilderException("Unknown server config! ");
		}

		MailConfig mailConfig = new MailConfig();

		mailConfig.setSecureName(this.secureName);
		mailConfig.setUserName(this.userName);
		mailConfig.setPassWord(this.passWord);
		mailConfig.setProxyConfig(this.proxyConfig);
		mailConfig.setSendConfig(this.sendConfig);
		mailConfig.setReceiveConfig(this.receiveConfig);
		mailConfig.setStoragePath(this.storagePath);
		mailConfig.setCertificate(this.certificate);
		mailConfig.setPrivateKey(this.privateKey);

		return mailConfig;
	}

	void proxyConfig(final ProxyConfig proxyConfig) {
		this.proxyConfig = proxyConfig;
	}

	/**
	 * Send config builder.
	 *
	 * @param sendConfig send config
	 */
	void sendConfig(final MailConfig.ServerConfig sendConfig) {
		if (sendConfig != null) {
			this.sendConfig = sendConfig;
		}
	}

	/**
	 * Receive config builder.
	 *
	 * @param receiveConfig receive config
	 */
	void receiveConfig(final MailConfig.ServerConfig receiveConfig) {
		if (receiveConfig != null) {
			this.receiveConfig = receiveConfig;
		}
	}
}
