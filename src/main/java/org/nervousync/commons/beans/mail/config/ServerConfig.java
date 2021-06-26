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

package org.nervousync.commons.beans.mail.config;

import jakarta.xml.bind.annotation.*;
import org.nervousync.commons.beans.core.BeanObject;

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

    /**
     * Instantiates a new Server config.
     */
    public ServerConfig() {
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
}
