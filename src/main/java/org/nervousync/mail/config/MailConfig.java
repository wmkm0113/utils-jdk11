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
import org.nervousync.commons.core.RegexGlobals;
import org.nervousync.exceptions.builder.BuilderException;
import org.nervousync.utils.FileUtils;
import org.nervousync.utils.StringUtils;

/**
 * The type Mail config.
 */
@XmlType(name = "mail-config")
@XmlRootElement(name = "mail-config")
@XmlAccessorType(XmlAccessType.NONE)
public final class MailConfig extends BeanObject {

    private static final long serialVersionUID = -506685998495058905L;

    @XmlElement(name = "username")
    private String userName;
    @XmlElement(name = "password")
    private String passWord;
    @XmlElement(name = "send-config")
    private ServerConfig sendConfig;
    @XmlElement(name = "receive-config")
    private ServerConfig receiveConfig;
    @XmlElement(name = "storage-path")
    private String storagePath;

    /**
     * Instantiates a new Mail config.
     */
    public MailConfig() {
    }

    private MailConfig(String userName, String passWord, ServerConfig sendConfig,
                       ServerConfig receiveConfig, String storagePath) {
        this.userName = userName;
        this.passWord = passWord;
        this.sendConfig = sendConfig;
        this.receiveConfig = receiveConfig;
        this.storagePath = storagePath;
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
     * Gets user name.
     *
     * @return the user name
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets user name.
     *
     * @param userName the user name
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
     * Gets receive config.
     *
     * @return the receive config
     */
    public ServerConfig getReceiveConfig() {
        return receiveConfig;
    }

    /**
     * Sets receive config.
     *
     * @param receiveConfig the receive config
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

    public static final class Builder {

        private String userName;
        private String passWord;
        private ServerConfig sendConfig;
        private ServerConfig receiveConfig;
        private String storagePath;

        public Builder() {
        }

        public Builder(MailConfig mailConfig) {
            if (mailConfig != null) {
                this.userName = mailConfig.getUserName();
                this.passWord = mailConfig.getPassWord();
                this.sendConfig = mailConfig.getSendConfig();
                this.receiveConfig = mailConfig.getReceiveConfig();
                this.storagePath = mailConfig.getStoragePath();
            }
        }

        public Builder authentication(String userName, String passWord) throws BuilderException {
            if (!StringUtils.matches(userName, RegexGlobals.EMAIL_ADDRESS)) {
                throw new BuilderException("Invalid username");
            }
            this.userName = userName;
            this.passWord = passWord;
            return this;
        }

        public Builder sendConfig(ServerConfig sendConfig) {
            if (sendConfig != null) {
                this.sendConfig = sendConfig;
            }
            return this;
        }

        public Builder receiveConfig(ServerConfig receiveConfig) {
            if (receiveConfig != null) {
                this.receiveConfig = receiveConfig;
            }
            return this;
        }

        public Builder storagePath(String storagePath) throws BuilderException {
            if (StringUtils.isEmpty(storagePath) || !FileUtils.isExists(storagePath)) {
                throw new BuilderException("Storage path not exists! ");
            }
            this.storagePath = storagePath;
            return this;
        }

        public MailConfig build() throws BuilderException {
            if (this.sendConfig == null && this.receiveConfig == null) {
                throw new BuilderException("Unknown server config! ");
            }
            return new MailConfig(this.userName, this.passWord, this.sendConfig, this.receiveConfig, this.storagePath);
        }
    }
}
