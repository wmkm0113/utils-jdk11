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
package org.nervousync.commons.proxy;

import java.net.Proxy.Type;

import jakarta.xml.bind.annotation.*;
import org.nervousync.beans.core.BeanObject;
import org.nervousync.commons.core.Globals;

/**
 * Proxy server setting
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: Jan 4, 2018 4:05:54 PM $
 */
@XmlType(name = "proxy_config", namespace = "https://nervousync.org/schemas/proxy")
@XmlRootElement(name = "proxy_config", namespace = "https://nervousync.org/schemas/proxy")
@XmlAccessorType(XmlAccessType.NONE)
public final class ProxyConfig extends BeanObject {

    /**
     * Proxy type
     *
     * @see java.net.Proxy.Type
     */
    @XmlElement(name = "type")
    private Type proxyType = Type.DIRECT;
    /**
     * Proxy server address
     */
    @XmlElement(name = "address")
    private String proxyAddress = Globals.DEFAULT_VALUE_STRING;
    /**
     * Proxy server port
     */
    @XmlElement(name = "port")
    private int proxyPort = Globals.DEFAULT_VALUE_INT;
    /**
     * Proxy server username
     */
    @XmlElement(name = "username")
    private String userName = Globals.DEFAULT_VALUE_STRING;
    /**
     * Proxy server password
     */
    @XmlElement(name = "password")
    private String password = Globals.DEFAULT_VALUE_STRING;

    /**
     * Instantiates a new Proxy config.
     */
    public ProxyConfig() {
    }

    /**
     * Redirect proxy config.
     *
     * @return the proxy config
     */
    public static ProxyConfig redirect() {
        return new ProxyConfig();
    }

    /**
     * Gets the proxy type.
     *
     * @return the proxy type
     */
    public Type getProxyType() {
        return proxyType;
    }

    /**
     * Sets the proxy type.
     *
     * @param proxyType the proxy type
     */
    public void setProxyType(Type proxyType) {
        this.proxyType = proxyType;
    }

    /**
     * Gets proxy address.
     *
     * @return the proxy address
     */
    public String getProxyAddress() {
        return proxyAddress;
    }

    /**
     * Sets proxy address.
     *
     * @param proxyAddress the proxy address
     */
    public void setProxyAddress(String proxyAddress) {
        this.proxyAddress = proxyAddress;
    }

    /**
     * Gets proxy port.
     *
     * @return the proxy port
     */
    public int getProxyPort() {
        return proxyPort;
    }

    /**
     * Sets proxy port.
     *
     * @param proxyPort the proxy port
     */
    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
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
     * Gets password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets password.
     *
     * @param password the password
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
