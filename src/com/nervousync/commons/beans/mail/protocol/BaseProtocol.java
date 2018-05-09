/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervousync Studio.
 */
package com.nervousync.commons.beans.mail.protocol;

import java.io.Serializable;
import java.security.Security;
import java.util.Properties;

import com.nervousync.commons.core.Globals;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Jul 31, 2012 7:07:08 PM $
 */
public class BaseProtocol implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6441571927997267674L;
	
	private static final String SSL_FACTORY_CLASS = "javax.net.ssl.SSLSocketFactory";
	
	protected String connectionTimeoutParam;
	protected String hostParam;
	protected String portParam;
	protected String timeoutParam;
	private ProtocolOption protocolOption;
	
	public BaseProtocol() {
		
	}
	
	protected BaseProtocol(ProtocolOption protocolOption) {
		this.protocolOption = protocolOption;
	}

	public Properties getConfigInfo(String host, int port) {
		return getConfigInfo(host, port, null, Globals.DEFAULT_TIME_OUT, Globals.DEFAULT_TIME_OUT, false, false);
	}

	public Properties getConfigInfo(String host, int port, boolean authLogin) {
		return getConfigInfo(host, port, null, Globals.DEFAULT_TIME_OUT, Globals.DEFAULT_TIME_OUT, false, authLogin);
	}

	public Properties getConfigInfo(String host, int port, boolean ssl, boolean authLogin) {
		return getConfigInfo(host, port, null, Globals.DEFAULT_TIME_OUT, Globals.DEFAULT_TIME_OUT, ssl, authLogin);
	}
	
	public Properties getConfigInfo(String host, int port, String sendAddress, boolean authLogin) {
		return getConfigInfo(host, port, sendAddress, Globals.DEFAULT_TIME_OUT, Globals.DEFAULT_TIME_OUT, false, authLogin);
	}
	
	public Properties getConfigInfo(String host, int port, String sendAddress, boolean ssl, boolean authLogin) {
		return getConfigInfo(host, port, sendAddress, Globals.DEFAULT_TIME_OUT, Globals.DEFAULT_TIME_OUT, ssl, authLogin);
	}
	
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
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
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
