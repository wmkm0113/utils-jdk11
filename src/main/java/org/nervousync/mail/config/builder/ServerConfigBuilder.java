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

import org.nervousync.builder.AbstractBuilder;
import org.nervousync.commons.core.Globals;
import org.nervousync.enumerations.mail.MailProtocol;
import org.nervousync.exceptions.builder.BuilderException;
import org.nervousync.mail.config.MailConfig;
import org.nervousync.utils.StringUtils;

public final class ServerConfigBuilder extends AbstractBuilder<MailConfigBuilder> {

    private final boolean sendConfig;
    private String hostName;
    private int hostPort = Globals.DEFAULT_VALUE_INT;
    private boolean ssl;
    private boolean authLogin;
    private MailProtocol protocolOption = MailProtocol.UNKNOWN;
    private int connectionTimeout = 5;
    private int processTimeout = 5;

    ServerConfigBuilder(final MailConfigBuilder mailConfigBuilder, final boolean sendConfig,
                        final MailConfig.ServerConfig serverConfig) {
        super(mailConfigBuilder);
        this.sendConfig = sendConfig;
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
    public ServerConfigBuilder configHost(String hostAddress, int hostPort) {
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
    public ServerConfigBuilder useSSL(boolean useSSL) {
        this.ssl = useSSL;
        return this;
    }

    /**
     * Auth login builder.
     *
     * @param authLogin the auth login
     * @return the builder
     */
    public ServerConfigBuilder authLogin(boolean authLogin) {
        this.authLogin = authLogin;
        return this;
    }

    public ServerConfigBuilder mailProtocol(final MailProtocol protocolOption) {
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
    public ServerConfigBuilder connectionTimeout(int connectionTimeout) {
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
    public ServerConfigBuilder processTimeout(int processTimeout) {
        if (processTimeout > 0) {
            this.processTimeout = processTimeout;
        }
        return this;
    }

    /**
     * Build server config.
     *
     * @throws BuilderException the builder exception
     */
    protected void build() throws BuilderException {
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

        if (this.sendConfig) {
            this.parentBuilder.sendConfig(serverConfig);
        } else {
            this.parentBuilder.receiveConfig(serverConfig);
        }
    }
}
