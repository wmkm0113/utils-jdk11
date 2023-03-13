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
package org.nervousync.mail.protocol;

import java.io.Serializable;
import java.security.Security;
import java.util.*;

import org.nervousync.commons.core.Globals;
import org.nervousync.commons.proxy.ProxyConfig;
import org.nervousync.enumerations.mail.MailProtocol;
import org.nervousync.mail.config.MailConfig;
import org.nervousync.security.factory.SecureFactory;
import org.nervousync.utils.StringUtils;

/**
 * JavaMail base protocol
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Jul 31, 2012 7:07:08 PM $
 */
public abstract class BaseProtocol implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 6441571927997267674L;

    private static final String SSL_FACTORY_CLASS = "javax.net.ssl.SSLSocketFactory";
    private static final String MAIL_STORE_PROTOCOL = "mail.store.protocol";
    private static final String MAIL_TRANSPORT_PROTOCOL = "mail.transport.protocol";

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
    private final String secureName;
    private final ProxyConfig proxyConfig;

    /**
     * Constructor for define protocol type
     */
    protected BaseProtocol(final String secureName, final ProxyConfig proxyConfig) {
        this.secureName = secureName;
        this.proxyConfig = proxyConfig;
    }

    /**
     * Read configuration for JavaMail using
     *
     * @param serverConfig the server config
     * @return java.util.Properties for JavaMail using
     */
    public final Properties readConfig(final MailConfig.ServerConfig serverConfig) {
        Properties properties = new Properties();

        properties.setProperty(this.hostParam, serverConfig.getHostName());
        int port = serverConfig.getHostPort();
        if (port != Globals.DEFAULT_VALUE_INT) {
            properties.setProperty(portParam, Integer.toString(port));
        }

        if (serverConfig.getConnectionTimeout() > 0) {
            properties.setProperty(connectionTimeoutParam,
                    Integer.toString(serverConfig.getConnectionTimeout() * 1000));
        }
        if (serverConfig.getProcessTimeout() > 0) {
            properties.setProperty(timeoutParam, Integer.toString(serverConfig.getConnectionTimeout() * 1000));
        }

        if (serverConfig.isSsl()) {
            Security.addProvider(Security.getProvider("SunJSSE"));
        }

        switch (serverConfig.getProtocolOption()) {
            case IMAP:
                properties.setProperty(MAIL_STORE_PROTOCOL, "imap");

                if (serverConfig.isAuthLogin()) {
                    properties.setProperty("mail.imap.auth.plain.disable", "true");
                    properties.setProperty("mail.imap.auth.login.disable", "true");
                }

                if (serverConfig.isSsl()) {
                    properties.setProperty(MAIL_STORE_PROTOCOL, "imaps");
                    properties.setProperty("mail.imap.socketFactory.class", SSL_FACTORY_CLASS);
                    if (port != Globals.DEFAULT_VALUE_INT) {
                        properties.setProperty("mail.imap.socketFactory.port", Integer.toString(port));
                    }
                    properties.setProperty("mail.imap.starttls.enable", "true");
                }
                break;
            case SMTP:
                properties.setProperty(MAIL_STORE_PROTOCOL, "smtp");
                properties.setProperty(MAIL_TRANSPORT_PROTOCOL, "smtp");

                if (serverConfig.isAuthLogin()) {
                    properties.setProperty("mail.smtp.auth", "true");
                }

                if (serverConfig.isSsl()) {
                    properties.setProperty(MAIL_STORE_PROTOCOL, "smtps");
                    properties.setProperty("mail.smtp.ssl.enable", "true");
                    properties.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY_CLASS);
                    properties.setProperty("mail.smtp.socketFactory.fallback", "false");
                    if (port != Globals.DEFAULT_VALUE_INT) {
                        properties.setProperty("mail.smtp.socketFactory.port", Integer.toString(port));
                    }
                    properties.setProperty("mail.smtp.starttls.enable", "true");
                }
                break;
            case POP3:
                properties.setProperty(MAIL_STORE_PROTOCOL, "pop3");
                properties.setProperty(MAIL_TRANSPORT_PROTOCOL, "pop3");

                if (serverConfig.isSsl()) {
                    properties.setProperty(MAIL_STORE_PROTOCOL, "pop3s");
                    properties.setProperty(MAIL_TRANSPORT_PROTOCOL, "pop3s");
                    properties.setProperty("mail.pop3.socketFactory.class", SSL_FACTORY_CLASS);
                    if (port != 0) {
                        properties.setProperty("mail.pop3.socketFactory.port", Integer.toString(port));
                    }
                    properties.setProperty("mail.pop3.disabletop", "true");
                    properties.setProperty("mail.pop3.ssl.enable", "true");
                    properties.setProperty("mail.pop3.useStartTLS", "true");
                }
                break;
            default:
                return new Properties();
        }
        this.configProxy(serverConfig.getProtocolOption(), properties);
        return properties;
    }

    private void configProxy(final MailProtocol mailProtocol, final Properties properties) {
        final String configPrefix;
        switch (mailProtocol) {
            case SMTP:
                configPrefix = "mail.smtp";
                break;
            case IMAP:
                configPrefix = "mail.imap";
                break;
            case POP3:
                configPrefix = "mail.pop3";
                break;
            default:
                return;
        }

        switch (this.proxyConfig.getProxyType()) {
            case HTTP:
                properties.setProperty(configPrefix + ".proxy.host", this.proxyConfig.getProxyAddress());
                if (this.proxyConfig.getProxyPort() != Globals.DEFAULT_VALUE_INT) {
                    properties.setProperty(configPrefix + ".proxy.port",
                            Integer.toString(this.proxyConfig.getProxyPort()));
                }
                if (StringUtils.notBlank(this.proxyConfig.getUserName())
                        && StringUtils.notBlank(this.proxyConfig.getPassword())) {
                    properties.setProperty(configPrefix + ".proxy.user", this.proxyConfig.getUserName());
                    properties.setProperty(configPrefix + ".proxy.password",
                            SecureFactory.getInstance().decrypt(this.secureName, this.proxyConfig.getPassword()));
                }
                break;
            case SOCKS:
                properties.setProperty(configPrefix + ".socks.host", this.proxyConfig.getProxyAddress());
                properties.setProperty(configPrefix + ".socks.port", Integer.toString(this.proxyConfig.getProxyPort()));
                break;
        }
    }
}
