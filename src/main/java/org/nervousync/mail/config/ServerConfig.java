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
import org.nervousync.exceptions.builder.BuilderException;
import org.nervousync.utils.StringUtils;

import java.util.Arrays;

/**
 * The type Server config.
 */
@XmlType(name = "server-config")
@XmlRootElement(name = "server-config")
@XmlAccessorType(XmlAccessType.NONE)
public final class ServerConfig extends BeanObject {

    private static final long serialVersionUID = -1768113760096890529L;

    @XmlElement(name = "host-name")
    private String hostName;
    @XmlElement(name = "host-port")
    private int hostPort;
    @XmlElement(name = "ssl")
    private boolean ssl;
    @XmlElement(name = "auth-login")
    private boolean authLogin;
    @XmlElement(name = "protocol")
    private String protocolOption;
    @XmlElement(name = "connection-timeout")
    private int connectionTimeout = 5;
    @XmlElement(name = "process-timeout")
    private int processTimeout = 5;

    /**
     * Instantiates a new Server config.
     */
    public ServerConfig() {
    }

    private ServerConfig(String hostName, int hostPort, boolean ssl, boolean authLogin,
                         String protocolOption, int connectionTimeOut, int processTimeOut) {
        this.hostName = hostName;
        this.hostPort = hostPort;
        this.ssl = ssl;
        this.authLogin = authLogin;
        this.protocolOption = protocolOption;
        this.connectionTimeout = connectionTimeOut;
        this.processTimeout = processTimeOut;
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
    public String getProtocolOption() {
        return protocolOption;
    }

    /**
     * Sets protocol option.
     *
     * @param protocolOption the protocol option
     */
    public void setProtocolOption(String protocolOption) {
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

    public static final class Builder {

        private static final String[] SUPPORTED_PROTOCOL = new String[]{"SMTP", "POP3", "IMAP"};

        private String hostName;
        private int hostPort = Globals.DEFAULT_VALUE_INT;
        private boolean ssl;
        private boolean authLogin;
        private final String protocolOption;
        private int connectionTimeout = 5;
        private int processTimeout = 5;

        public Builder(String protocolOption) throws BuilderException {
            if (Arrays.stream(SUPPORTED_PROTOCOL).noneMatch(protocol -> protocol.equalsIgnoreCase(protocolOption))) {
                throw new BuilderException("Unsupported protocol option");
            }
            this.protocolOption = protocolOption;
        }

        public Builder(ServerConfig serverConfig) throws BuilderException {
            if (serverConfig == null) {
                throw new BuilderException("Server config is null! ");
            }
            this.hostName = serverConfig.getHostName();
            this.hostPort = serverConfig.getHostPort();
            this.ssl = serverConfig.isSsl();
            this.authLogin = serverConfig.isAuthLogin();
            this.protocolOption = serverConfig.getProtocolOption();
            this.connectionTimeout = serverConfig.getConnectionTimeout();
            this.processTimeout = serverConfig.getProcessTimeout();
        }

        public Builder configHost(String hostAddress) {
            return this.configHost(hostAddress, Globals.DEFAULT_VALUE_INT);
        }

        public Builder configHost(String hostAddress, int hostPort) {
            this.hostName = hostAddress;
            if (hostPort > 0) {
                this.hostPort = hostPort;
            }
            return this;
        }

        public Builder useSSL(boolean useSSL) {
            this.ssl = useSSL;
            return this;
        }

        public Builder authLogin(boolean authLogin) {
            this.authLogin = authLogin;
            return this;
        }

        public Builder connectionTimeOut(int connectionTimeout) {
            if (connectionTimeout > 0) {
                this.connectionTimeout = connectionTimeout;
            }
            return this;
        }

        public Builder processTimeout(int processTimeout) {
            if (processTimeout > 0) {
                this.processTimeout = processTimeout;
            }
            return this;
        }

        public ServerConfig build() throws BuilderException {
            if (StringUtils.isEmpty(this.hostName)) {
                throw new BuilderException("Unknown server host address! ");
            }
            return new ServerConfig(this.hostName, this.hostPort, this.ssl, this.authLogin,
                    this.protocolOption, this.connectionTimeout, this.processTimeout);
        }
    }
}
