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
package com.nervousync.commons.beans.mail.protocol;

import java.io.Serializable;
import java.security.Security;
import java.util.Properties;

import com.nervousync.commons.core.Globals;
import com.nervousync.enumerations.mail.ProtocolOption;

/**
 * JavaMail base protocol
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Jul 31, 2012 7:07:08 PM $
 */
public class BaseProtocol implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6441571927997267674L;
	
	private static final String SSL_FACTORY_CLASS = "javax.net.ssl.SSLSocketFactory";
	
	/**
	 * Connection timeout parameter name
	 */
	protected String connectionTimeoutParam;
	/**
	 * Host parameter name
	 */
	protected String hostParam;
	/**
	 * Port parameter name
	 */
	protected String portParam;
	/**
	 * Timeout parameter name
	 */
	protected String timeoutParam;
	/**
	 * Protocol type
	 * @see com.nervousync.enumerations.mail.ProtocolOption
	 */
	private final ProtocolOption protocolOption;

	/**
	 * Constructor for define protocol type
	 * @param protocolOption		Protocol type
	 * @see com.nervousync.enumerations.mail.ProtocolOption
	 */
	protected BaseProtocol(ProtocolOption protocolOption) {
		this.protocolOption = protocolOption;
	}

	/**
	 * Read configuration for JavaMail using
	 * @param host		Target server domain name or address
	 * @param port		Target server port
	 * @return			java.util.Properties for JavaMail using
	 */
	public Properties getConfigInfo(String host, int port) {
		return getConfigInfo(host, port, null, Globals.DEFAULT_TIME_OUT, Globals.DEFAULT_TIME_OUT, false, false);
	}

	/**
	 * Read configuration for JavaMail using
	 * @param host			Target server domain name or address
	 * @param port			Target server port
	 * @param authLogin		Server must authentication login
	 * @return				java.util.Properties for JavaMail using
	 */
	public Properties getConfigInfo(String host, int port, boolean authLogin) {
		return getConfigInfo(host, port, null, Globals.DEFAULT_TIME_OUT, Globals.DEFAULT_TIME_OUT, false, authLogin);
	}

	/**
	 * Read configuration for JavaMail using
	 * @param host			Target server domain name or address
	 * @param port			Target server port
	 * @param ssl			The connection must using SSL
	 * @param authLogin		Server must authentication login
	 * @return				java.util.Properties for JavaMail using
	 */
	public Properties getConfigInfo(String host, int port, boolean ssl, boolean authLogin) {
		return getConfigInfo(host, port, null, Globals.DEFAULT_TIME_OUT, Globals.DEFAULT_TIME_OUT, ssl, authLogin);
	}
	
	/**
	 * Read configuration for JavaMail using
	 * @param host			Target server domain name or address
	 * @param port			Target server port
	 * @param sendAddress	Sender e-mail address, using for SMTP
	 * @param authLogin		Server must authentication login
	 * @return				java.util.Properties for JavaMail using
	 */
	public Properties getConfigInfo(String host, int port, String sendAddress, boolean authLogin) {
		return getConfigInfo(host, port, sendAddress, Globals.DEFAULT_TIME_OUT, Globals.DEFAULT_TIME_OUT, false, authLogin);
	}
	
	/**
	 * Read configuration for JavaMail using
	 * @param host			Target server domain name or address
	 * @param port			Target server port
	 * @param sendAddress	Sender e-mail address, using for SMTP
	 * @param ssl			The connection must using SSL
	 * @param authLogin		Server must authentication login
	 * @return				java.util.Properties for JavaMail using
	 */
	public Properties getConfigInfo(String host, int port, String sendAddress, boolean ssl, boolean authLogin) {
		return getConfigInfo(host, port, sendAddress, Globals.DEFAULT_TIME_OUT, Globals.DEFAULT_TIME_OUT, ssl, authLogin);
	}
	
	/**
	 * Read configuration for JavaMail using
	 * @param host						Target server domain name or address
	 * @param port						Target server port
	 * @param sendAddress				Sender e-mail address, using for SMTP
	 * @param connectionTimeout			Connection timeout value
	 * @param timeout					Operate timeout value
	 * @param ssl						The connection must using SSL
	 * @param authLogin					Server must authentication login
	 * @return							java.util.Properties for JavaMail using
	 */
	public Properties getConfigInfo(String host, int port, String sendAddress, 
			int connectionTimeout, int timeout, boolean ssl, boolean authLogin) {
		Properties properties = new Properties();
		
		properties.setProperty(hostParam, host);
		if (port != Globals.DEFAULT_VALUE_INT) {
			properties.setProperty(portParam, Integer.valueOf(port).toString());
		}
		properties.setProperty(connectionTimeoutParam, Integer.valueOf(connectionTimeout).toString());
		properties.setProperty(timeoutParam, Integer.valueOf(timeout).toString());
		
		if (ssl) {
			Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
		}
		
		switch (protocolOption) {
		case IMAP:
			properties.setProperty("mail.store.protocol", "imap");

			if (authLogin) {
				properties.setProperty("mail.imap.auth.plain.disable", "true");
				properties.setProperty("mail.imap.auth.login.disable", "true");
			}
			
			if (ssl) {
				properties.setProperty("mail.store.protocol", "imaps");
				properties.setProperty("mail.imap.socketFactory.class", SSL_FACTORY_CLASS);
				if (port != 0) {
					properties.setProperty("mail.imap.socketFactory.port", Integer.valueOf(port).toString());
				}
			}
			break;
		case SMTP:
			properties.setProperty("mail.store.protocol", "smtp");

			if (authLogin) {
				properties.setProperty("mail.smtp.auth", "true");
				if (sendAddress != null) {
					properties.setProperty("mail.smtp.from", sendAddress);
				}
			}
			
			if (ssl) {
				properties.setProperty("mail.store.protocol", "smtps");
				properties.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY_CLASS);
				if (port != 0) {
					properties.setProperty("mail.smtp.socketFactory.port", Integer.valueOf(port).toString());
				}
			} else {
				properties.setProperty("mail.smtp.starttls.enable", "true");
			}
			break;
		case POP3:
			properties.setProperty("mail.store.protocol", "pop3");
			
			if (ssl) {
				properties.setProperty("mail.store.protocol", "pop3s");
				properties.setProperty("mail.pop3.socketFactory.class", SSL_FACTORY_CLASS);
				if (port != 0) {
					properties.setProperty("mail.pop3.socketFactory.port", Integer.valueOf(port).toString());
				}
				properties.setProperty("mail.pop3.disabletop", "true");
				properties.setProperty("mail.pop3.ssl.enable", "true");
			} else {
				properties.setProperty("mail.pop3.useStartTLS", "true");
			}
			break;
			default:
				break;
		}
		
		return properties;
	}

	/**
	 * @return the serialVersionUID
	 */
	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	/**
	 * @return the connectionTimeoutParam
	 */
	public String getConnectionTimeoutParam() {
		return connectionTimeoutParam;
	}

	/**
	 * @return the hostParam
	 */
	public String getHostParam() {
		return hostParam;
	}

	/**
	 * @return the portParam
	 */
	public String getPortParam() {
		return portParam;
	}

	/**
	 * @return the timeoutParam
	 */
	public String getTimeoutParam() {
		return timeoutParam;
	}

	/**
	 * @return the protocolOption
	 */
	public ProtocolOption getProtocolOption() {
		return protocolOption;
	}
}
