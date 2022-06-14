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
package org.nervousync.mail.config;

import org.nervousync.commons.core.Globals;
import org.nervousync.commons.core.RegexGlobals;
import org.nervousync.enumerations.mail.MailProtocol;
import org.nervousync.exceptions.builder.BuilderException;
import org.nervousync.security.factory.SecureFactory;
import org.nervousync.utils.ConvertUtils;
import org.nervousync.utils.FileUtils;
import org.nervousync.utils.StringUtils;

import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

public final class MailConfigBuilder {

	private String secureName;
	private String userName;
	private String passWord;
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
			this.passWord = SecureFactory.getInstance().update(this.passWord, this.secureName, secureName);
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
	public MailConfigBuilder authentication(String userName, String passWord) throws BuilderException {
		if (!StringUtils.matches(userName, RegexGlobals.EMAIL_ADDRESS)) {
			throw new BuilderException("Invalid username");
		}
		this.userName = userName;
		SecureFactory secureFactory = SecureFactory.getInstance();
		if (StringUtils.notBlank(passWord) && StringUtils.notBlank(this.secureName)
				&& secureFactory.registeredConfig(this.secureName)) {
			byte[] encBytes = secureFactory.encrypt(this.secureName, ConvertUtils.convertToByteArray(passWord));
			this.passWord = StringUtils.base64Encode(encBytes);
		} else {
			this.passWord = passWord;
		}
		return this;
	}

	public MailServerBuilder sendConfig() {
		return new MailServerBuilder(this, ServerType.SEND, this.sendConfig);
	}

	public MailServerBuilder receiveConfig() {
		return new MailServerBuilder(this, ServerType.RECEIVE, this.sendConfig);
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
		mailConfig.setSendConfig(this.sendConfig);
		mailConfig.setReceiveConfig(this.receiveConfig);
		mailConfig.setStoragePath(this.storagePath);
		mailConfig.setCertificate(this.certificate);
		mailConfig.setPrivateKey(this.privateKey);

		return mailConfig;
	}

	/**
	 * Send config builder.
	 *
	 * @param sendConfig send config
	 * @return the builder
	 */
	private MailConfigBuilder sendConfig(MailConfig.ServerConfig sendConfig) {
		if (sendConfig != null) {
			this.sendConfig = sendConfig;
		}
		return this;
	}

	/**
	 * Receive config builder.
	 *
	 * @param receiveConfig receive config
	 * @return the builder
	 */
	private MailConfigBuilder receiveConfig(MailConfig.ServerConfig receiveConfig) {
		if (receiveConfig != null) {
			this.receiveConfig = receiveConfig;
		}
		return this;
	}

	public static final class MailServerBuilder {

		private final MailConfigBuilder mailConfigBuilder;
		private final ServerType serverType;
		private String hostName;
		private int hostPort = Globals.DEFAULT_VALUE_INT;
		private boolean ssl;
		private boolean authLogin;
		private MailProtocol protocolOption = MailProtocol.UNKNOWN;
		private int connectionTimeout = 5;
		private int processTimeout = 5;

		/**
		 * Instantiates a new Builder.
		 *
		 * @param serverConfig the server config
		 */
		private MailServerBuilder(final MailConfigBuilder mailConfigBuilder, final ServerType serverType,
		                          final MailConfig.ServerConfig serverConfig) {
			this.mailConfigBuilder = mailConfigBuilder;
			this.serverType = serverType;
			if (serverConfig != null) {
				this.hostName = serverConfig.getHostName();
				this.hostPort = serverConfig.getHostPort();
				this.ssl = serverConfig.isSsl();
				this.authLogin = serverConfig.isAuthLogin();
				this.protocolOption = serverConfig.getProtocolOption();
				this.connectionTimeout = serverConfig.getConnectionTimeout();
				this.processTimeout = serverConfig.getProcessTimeout();
			}
		}

		/**
		 * Config host builder.
		 *
		 * @param hostAddress the host address
		 * @param hostPort    the host port
		 * @return the builder
		 */
		public MailServerBuilder configHost(String hostAddress, int hostPort) {
			this.hostName = hostAddress;
			if (hostPort > 0) {
				this.hostPort = hostPort;
			}
			return this;
		}

		/**
		 * Use ssl builder.
		 *
		 * @param useSSL the use ssl
		 * @return the builder
		 */
		public MailServerBuilder useSSL(boolean useSSL) {
			this.ssl = useSSL;
			return this;
		}

		/**
		 * Auth login builder.
		 *
		 * @param authLogin the auth login
		 * @return the builder
		 */
		public MailServerBuilder authLogin(boolean authLogin) {
			this.authLogin = authLogin;
			return this;
		}

		public MailServerBuilder mailProtocol(final MailProtocol protocolOption) {
			if (!MailProtocol.UNKNOWN.equals(protocolOption)) {
				this.protocolOption = protocolOption;
			}
			return this;
		}

		/**
		 * Connection time out builder.
		 *
		 * @param connectionTimeout the connection timeout
		 * @return the builder
		 */
		public MailServerBuilder connectionTimeout(int connectionTimeout) {
			if (connectionTimeout > 0) {
				this.connectionTimeout = connectionTimeout;
			}
			return this;
		}

		/**
		 * Process timeout builder.
		 *
		 * @param processTimeout the process timeout
		 * @return the builder
		 */
		public MailServerBuilder processTimeout(int processTimeout) {
			if (processTimeout > 0) {
				this.processTimeout = processTimeout;
			}
			return this;
		}

		/**
		 * Build server config.
		 *
		 * @return the server config
		 * @throws BuilderException the builder exception
		 */
		public MailConfigBuilder confirm() throws BuilderException {
			if (StringUtils.isEmpty(this.hostName)) {
				throw new BuilderException("Unknown server host address! ");
			}
			if (MailProtocol.UNKNOWN.equals(this.protocolOption)) {
				throw new BuilderException("Unknown mail protocol! ");
			}
			MailConfig.ServerConfig serverConfig = new MailConfig.ServerConfig();
			serverConfig.setHostName(this.hostName);
			serverConfig.setHostPort(this.hostPort);
			serverConfig.setSsl(this.ssl);
			serverConfig.setAuthLogin(this.authLogin);
			serverConfig.setProtocolOption(this.protocolOption);
			serverConfig.setConnectionTimeout(this.connectionTimeout);
			serverConfig.setProcessTimeout(this.processTimeout);

			switch (this.serverType) {
				case SEND:
					return this.mailConfigBuilder.sendConfig(serverConfig);
				case RECEIVE:
					return this.mailConfigBuilder.receiveConfig(serverConfig);
				default:
					return this.mailConfigBuilder;
			}
		}
	}
	
	private enum ServerType {
		SEND, RECEIVE
	}
}
