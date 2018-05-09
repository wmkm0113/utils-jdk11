/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervousync Studio.
 */
package com.nervousync.commons.beans.mail;

import java.io.Serializable;
import java.util.Properties;

import com.nervousync.commons.beans.mail.protocol.BaseProtocol;
import com.nervousync.commons.beans.mail.protocol.ProtocolOption;
import com.nervousync.commons.beans.mail.protocol.impl.IMAPProtocol;
import com.nervousync.commons.beans.mail.protocol.impl.POP3Protocol;
import com.nervousync.commons.beans.mail.protocol.impl.SMTPProtocol;
import com.nervousync.commons.core.Globals;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Sep 18, 2012 9:54:37 PM $
 */
public class MailServerConfig implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2026891055081503726L;
	
	private ServerConfig sendServerConfig;
	private ServerConfig recvServerConfig;
	
	public MailServerConfig(String recvProtocolName, String recvHostName, int recvHostPort, boolean recvSsl, boolean recvAuthLogin, 
			String sendProtocolName, String sendHostName, int sendHostPort, boolean sendSsl, boolean sendAuthLogin) throws Exception {
		if (recvProtocolName == null || recvHostName == null || sendProtocolName == null || sendHostName == null) {
			throw new Exception("Mail Server Info Error");
		}
		
		BaseProtocol recvProtocol;
		if (recvProtocolName.equalsIgnoreCase("IMAP")) {
			recvProtocol = new IMAPProtocol();
		} else if (recvProtocolName.equalsIgnoreCase("POP3")) {
			recvProtocol = new POP3Protocol();
		} else {
			throw new Exception("Receive Mail Protocol Unknown");
		}
		
		this.recvServerConfig = new ServerConfig(recvProtocol, recvHostName, recvHostPort, recvSsl, recvAuthLogin);
		this.sendServerConfig = new ServerConfig(new SMTPProtocol(), sendHostName, sendHostPort, sendSsl, sendAuthLogin);
	}
	
	public Properties getSendConfigInfo() {
		return this.sendServerConfig.getConfigInfo();
	}
	
	public Properties getSendConfigInfo(String userName) {
		return this.sendServerConfig.getConfigInfo(userName);
	}
	
	public Properties getSendConfigInfo(String userName, int connectionTimeout, int timeout) {
		return this.sendServerConfig.getConfigInfo(userName, connectionTimeout, timeout);
	}

	public Properties getRecvConfigInfo() {
		return this.recvServerConfig.getConfigInfo();
	}
	
	public Properties getRecvConfigInfo(String userName) {
		return this.recvServerConfig.getConfigInfo(userName);
	}
	
	public Properties getRecvConfigInfo(String userName, int connectionTimeout, int timeout) {
		return this.recvServerConfig.getConfigInfo(userName, connectionTimeout, timeout);
	}
	
	/**
	 * @return the sendServerConfig
	 */
	public ServerConfig getSendServerConfig() {
		return sendServerConfig;
	}

	/**
	 * @param sendServerConfig the sendServerConfig to set
	 */
	public void setSendServerConfig(ServerConfig sendServerConfig) {
		this.sendServerConfig = sendServerConfig;
	}

	/**
	 * @return the recvServerConfig
	 */
	public ServerConfig getRecvServerConfig() {
		return recvServerConfig;
	}

	/**
	 * @param recvServerConfig the recvServerConfig to set
	 */
	public void setRecvServerConfig(ServerConfig recvServerConfig) {
		this.recvServerConfig = recvServerConfig;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public final static class ServerConfig implements Serializable {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 4918987878564423832L;
		
		private BaseProtocol protocol;
		private String hostName;
		private int hostPort = Globals.DEFAULT_VALUE_INT;
		private boolean ssl = false;
		private boolean authLogin = false;
		private ProtocolOption protocolOption;
		
		public ServerConfig(BaseProtocol protocol, String hostName, int hostPort, boolean ssl, boolean authLogin) {
			this.protocol = protocol;
			this.hostName = hostName;
			this.hostPort = hostPort;
			this.ssl = ssl;
			this.authLogin = authLogin;
			
			if (protocol instanceof SMTPProtocol) {
				protocolOption = ProtocolOption.SMTP;
			} else if (protocol instanceof POP3Protocol) {
				protocolOption = ProtocolOption.POP3;
			} else if (protocol instanceof IMAPProtocol) {
				protocolOption = ProtocolOption.IMAP;
			} else {
				protocolOption = ProtocolOption.UNKNOWN;
			}
		}
		
		public Properties getConfigInfo() {
			return this.protocol.getConfigInfo(this.hostName, this.hostPort, this.ssl, this.authLogin);
		}
		
		public Properties getConfigInfo(String userName) {
			return this.protocol.getConfigInfo(this.hostName, this.hostPort, userName, this.ssl, this.authLogin);
		}
		
		public Properties getConfigInfo(String userName, int connectionTimeout, int timeout) {
			return this.protocol.getConfigInfo(this.hostName, this.hostPort, userName, 
					connectionTimeout, timeout, this.ssl, this.authLogin);
		}
		
		/**
		 * @return the protocol
		 */
		public BaseProtocol getProtocol() {
			return protocol;
		}

		/**
		 * @param protocol the protocol to set
		 */
		public void setProtocol(BaseProtocol protocol) {
			this.protocol = protocol;
		}

		/**
		 * @return the hostName
		 */
		public String getHostName() {
			return hostName;
		}

		/**
		 * @param hostName the hostName to set
		 */
		public void setHostName(String hostName) {
			this.hostName = hostName;
		}

		/**
		 * @return the hostPort
		 */
		public int getHostPort() {
			return hostPort;
		}

		/**
		 * @param hostPort the hostPort to set
		 */
		public void setHostPort(int hostPort) {
			this.hostPort = hostPort;
		}

		/**
		 * @return the ssl
		 */
		public boolean isSsl() {
			return ssl;
		}

		/**
		 * @param ssl the ssl to set
		 */
		public void setSsl(boolean ssl) {
			this.ssl = ssl;
		}

		/**
		 * @return the authLogin
		 */
		public boolean isAuthLogin() {
			return authLogin;
		}

		/**
		 * @param authLogin the authLogin to set
		 */
		public void setAuthLogin(boolean authLogin) {
			this.authLogin = authLogin;
		}

		/**
		 * @return the protocolOption
		 */
		public ProtocolOption getProtocolOption() {
			return protocolOption;
		}

		/**
		 * @param protocolOption the protocolOption to set
		 */
		public void setProtocolOption(ProtocolOption protocolOption) {
			this.protocolOption = protocolOption;
		}

		/**
		 * @return the serialversionuid
		 */
		public static long getSerialversionuid() {
			return serialVersionUID;
		}
	}
}
