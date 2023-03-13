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

import jakarta.xml.bind.annotation.*;
import org.nervousync.beans.core.BeanObject;
import org.nervousync.commons.core.Globals;
import org.nervousync.commons.proxy.ProxyConfig;
import org.nervousync.enumerations.mail.MailProtocol;

/**
 * The type Mail config.
 */
@XmlType(name = "mail_config", namespace = "https://nervousync.org/schemas/mail")
@XmlRootElement(name = "mail_config", namespace = "https://nervousync.org/schemas/mail")
@XmlAccessorType(XmlAccessType.NONE)
public final class MailConfig extends BeanObject {

    private static final long serialVersionUID = -506685998495058905L;

    /**
     * Secure name
     */
    @XmlElement(name = "secure_name")
    private String secureName;
    /**
     * Mail account username
     */
    @XmlElement(name = "username")
    private String userName;
    /**
     * Mail account password
     */
    @XmlElement(name = "password")
    private String passWord;
    @XmlElement(name = "proxy_config", namespace = "https://nervousync.org/schemas/proxy")
    private ProxyConfig proxyConfig = ProxyConfig.redirect();
    /**
     * Mail send server config
     */
    @XmlElement(name = "send_config")
    private ServerConfig sendConfig;
    /**
     * Mail receive server config
     */
    @XmlElement(name = "receive_config")
    private ServerConfig receiveConfig;
    /**
     * Attaches file storage path
     */
    @XmlElement(name = "storage_path")
    private String storagePath;
    @XmlElement
    private String certificate;
    @XmlElement(name = "private_key")
    private String privateKey;

    /**
     * Instantiates a new Mail config.
     */
    public MailConfig() {
        this.secureName = Globals.DEFAULT_VALUE_STRING;
        this.certificate = Globals.DEFAULT_VALUE_STRING;
        this.privateKey = Globals.DEFAULT_VALUE_STRING;
    }

    /**
     * Gets secure name.
     *
     * @return the secure name
     */
    public String getSecureName() {
        return secureName;
    }

    /**
     * Sets secure name.
     *
     * @param secureName the secure name
     */
    public void setSecureName(String secureName) {
        this.secureName = secureName;
    }

    /**
     * Gets username.
     *
     * @return the username
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets username.
     *
     * @param userName the username
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Gets pass word.
     *
     * @return the pass word
     */
    public String getPassWord() {
        return passWord;
    }

    /**
     * Sets pass word.
     *
     * @param passWord the pass word
     */
    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    /**
     * Gets proxy config.
     *
     * @return the proxy config
     */
    public ProxyConfig getProxyConfig() {
        return proxyConfig;
    }

    /**
     * Sets proxy config.
     *
     * @param proxyConfig the proxy config
     */
    public void setProxyConfig(ProxyConfig proxyConfig) {
        this.proxyConfig = proxyConfig;
    }

    /**
     * Gets send config.
     *
     * @return send config
     */
    public ServerConfig getSendConfig() {
        return sendConfig;
    }

    /**
     * Sets send config.
     *
     * @param sendConfig send config
     */
    public void setSendConfig(ServerConfig sendConfig) {
        this.sendConfig = sendConfig;
    }

    /**
     * Gets receive config.
     *
     * @return receive config
     */
    public ServerConfig getReceiveConfig() {
        return receiveConfig;
    }

    /**
     * Sets receive config.
     *
     * @param receiveConfig receive config
     */
    public void setReceiveConfig(ServerConfig receiveConfig) {
        this.receiveConfig = receiveConfig;
    }

    /**
     * Gets storage path.
     *
     * @return the storage path
     */
    public String getStoragePath() {
        return storagePath;
    }

    /**
     * Sets storage path.
     *
     * @param storagePath the storage path
     */
    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    /**
     * Gets certificate.
     *
     * @return the certificate
     */
    public String getCertificate() {
        return certificate;
    }

    /**
     * Sets certificate.
     *
     * @param certificate the certificate
     */
    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    /**
     * Gets private key.
     *
     * @return the private key
     */
    public String getPrivateKey() {
        return privateKey;
    }

    /**
     * Sets private key.
     *
     * @param privateKey the private key
     */
    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    /**
     * The type Server config.
     */
    @XmlType(name = "server_config", namespace = "https://nervousync.org/schemas/mail")
    @XmlRootElement(name = "server_config", namespace = "https://nervousync.org/schemas/mail")
    @XmlAccessorType(XmlAccessType.NONE)
    public static final class ServerConfig extends BeanObject {

        private static final long serialVersionUID = -1768113760096890529L;

        /**
         * Host name
         */
        @XmlElement(name = "host_name")
        private String hostName;
        /**
         * Host port
         */
        @XmlElement(name = "host_port")
        private int hostPort;
        /**
         * Using SSL
         */
        @XmlElement(name = "ssl")
        private boolean ssl;
        /**
         * Auth Login
         */
        @XmlElement(name = "auth_login")
        private boolean authLogin;
        /**
         * Protocol option
         */
        @XmlElement(name = "protocol")
        private MailProtocol protocolOption;
        /**
         * Connect timeout
         */
        @XmlElement(name = "connection_timeout")
        private int connectionTimeout = 5;
        /**
         * Process time out
         */
        @XmlElement(name = "process_timeout")
        private int processTimeout = 5;

        /**
         * Instantiates a new Server config.
         */
        public ServerConfig() {
            this.hostName = Globals.DEFAULT_VALUE_STRING;
        }

        /**
         * Gets serial version uid.
         *
         * @return the serial version uid
         */
        public static long getSerialVersionUID() {
            return serialVersionUID;
        }

        /**
         * Gets host name.
         *
         * @return the host name
         */
        public String getHostName() {
            return hostName;
        }

        /**
         * Sets host name.
         *
         * @param hostName the host name
         */
        public void setHostName(String hostName) {
            this.hostName = hostName;
        }

        /**
         * Gets host port.
         *
         * @return the host port
         */
        public int getHostPort() {
            return hostPort;
        }

        /**
         * Sets host port.
         *
         * @param hostPort the host port
         */
        public void setHostPort(int hostPort) {
            this.hostPort = hostPort;
        }

        /**
         * Is ssl boolean.
         *
         * @return the boolean
         */
        public boolean isSsl() {
            return ssl;
        }

        /**
         * Sets ssl.
         *
         * @param ssl the ssl
         */
        public void setSsl(boolean ssl) {
            this.ssl = ssl;
        }

        /**
         * Is auth login boolean.
         *
         * @return the boolean
         */
        public boolean isAuthLogin() {
            return authLogin;
        }

        /**
         * Sets auth login.
         *
         * @param authLogin the auth login
         */
        public void setAuthLogin(boolean authLogin) {
            this.authLogin = authLogin;
        }

        /**
         * Gets protocol option.
         *
         * @return the protocol option
         */
        public MailProtocol getProtocolOption() {
            return protocolOption;
        }

        /**
         * Sets protocol option.
         *
         * @param protocolOption the protocol option
         */
        public void setProtocolOption(MailProtocol protocolOption) {
            this.protocolOption = protocolOption;
        }

        /**
         * Gets connection timeout.
         *
         * @return the connection timeout
         */
        public int getConnectionTimeout() {
            return connectionTimeout;
        }

        /**
         * Sets connection timeout.
         *
         * @param connectionTimeout the connection timeout
         */
        public void setConnectionTimeout(int connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
        }

        /**
         * Gets process timeout.
         *
         * @return the process timeout
         */
        public int getProcessTimeout() {
            return processTimeout;
        }

        /**
         * Sets process timeout.
         *
         * @param processTimeout the process timeout
         */
        public void setProcessTimeout(int processTimeout) {
            this.processTimeout = processTimeout;
        }
    }
}
