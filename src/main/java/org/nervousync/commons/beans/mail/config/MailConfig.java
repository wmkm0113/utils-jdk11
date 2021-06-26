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
 * The type Mail config.
 */
@XmlType(name = "mail-config")
@XmlRootElement(name = "mail-config")
@XmlAccessorType(XmlAccessType.NONE)
public final class MailConfig extends BeanObject {

    private static final long serialVersionUID = -506685998495058905L;

    @XmlElement(name = "send-username")
    private String sendUserName;
    @XmlElement(name = "send-password")
    private String sendPassWord;
    @XmlElement(name = "send-config")
    private ServerConfig sendConfig;
    @XmlElement(name = "recv-username")
    private String recvUserName;
    @XmlElement(name = "recv-password")
    private String recvPassWord;
    @XmlElement(name = "recv-config")
    private ServerConfig recvConfig;
    @XmlElement(name = "connection-timeout")
    private int connectionTimeout = 5;
    @XmlElement(name = "process-timeout")
    private int processTimeout = 5;
    @XmlElement(name = "storage-path")
    private String storagePath;

    /**
     * Instantiates a new Mail config.
     */
    public MailConfig() {
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
     * Gets send user name.
     *
     * @return the send user name
     */
    public String getSendUserName() {
        return sendUserName;
    }

    /**
     * Sets send user name.
     *
     * @param sendUserName the send user name
     */
    public void setSendUserName(String sendUserName) {
        this.sendUserName = sendUserName;
    }

    /**
     * Gets send pass word.
     *
     * @return the send pass word
     */
    public String getSendPassWord() {
        return sendPassWord;
    }

    /**
     * Sets send pass word.
     *
     * @param sendPassWord the send pass word
     */
    public void setSendPassWord(String sendPassWord) {
        this.sendPassWord = sendPassWord;
    }

    /**
     * Gets send config.
     *
     * @return the send config
     */
    public ServerConfig getSendConfig() {
        return sendConfig;
    }

    /**
     * Sets send config.
     *
     * @param sendConfig the send config
     */
    public void setSendConfig(ServerConfig sendConfig) {
        this.sendConfig = sendConfig;
    }

    /**
     * Gets recv user name.
     *
     * @return the recv user name
     */
    public String getRecvUserName() {
        return recvUserName;
    }

    /**
     * Sets recv user name.
     *
     * @param recvUserName the recv user name
     */
    public void setRecvUserName(String recvUserName) {
        this.recvUserName = recvUserName;
    }

    /**
     * Gets recv pass word.
     *
     * @return the recv pass word
     */
    public String getRecvPassWord() {
        return recvPassWord;
    }

    /**
     * Sets recv pass word.
     *
     * @param recvPassWord the recv pass word
     */
    public void setRecvPassWord(String recvPassWord) {
        this.recvPassWord = recvPassWord;
    }

    /**
     * Gets recv config.
     *
     * @return the recv config
     */
    public ServerConfig getRecvConfig() {
        return recvConfig;
    }

    /**
     * Sets recv config.
     *
     * @param recvConfig the recv config
     */
    public void setRecvConfig(ServerConfig recvConfig) {
        this.recvConfig = recvConfig;
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
}
