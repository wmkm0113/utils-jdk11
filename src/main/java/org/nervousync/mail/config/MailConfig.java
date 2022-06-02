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
import org.nervousync.commons.core.RegexGlobals;
import org.nervousync.exceptions.builder.BuilderException;
import org.nervousync.utils.FileUtils;
import org.nervousync.utils.StringUtils;

import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

/**
 * The type Mail config.
 */
@XmlType(name = "mail-config")
@XmlRootElement(name = "mail-config")
@XmlAccessorType(XmlAccessType.NONE)
public final class MailConfig extends BeanObject {

    private static final long serialVersionUID = -506685998495058905L;

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
    /**
     * Mail send server config
     */
    @XmlElement(name = "send-config")
    private ServerConfig sendConfig;
    /**
     * Mail receive server config
     */
    @XmlElement(name = "receive-config")
    private ServerConfig receiveConfig;
    /**
     * Attaches file storage path
     */
    @XmlElement(name = "storage-path")
    private String storagePath;
    @XmlElement
    private String certificate;
    @XmlElement(name = "private-key")
    private String privateKey;

    /**
     * Instantiates a new Mail config.
     */
    public MailConfig() {
    }

    private MailConfig(final String userName, final String passWord,
                       final ServerConfig sendConfig, final ServerConfig receiveConfig,
                       final String storagePath, final String certificate, final String privateKey) {
        this.userName = userName;
        this.passWord = passWord;
        this.sendConfig = sendConfig;
        this.receiveConfig = receiveConfig;
        this.storagePath = storagePath;
        this.certificate = certificate;
        this.privateKey = privateKey;
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
     * The type Builder.
     */
    public static final class Builder {

        private String userName;
        private String passWord;
        private ServerConfig sendConfig;
        private ServerConfig receiveConfig;
        private String storagePath;
        private String certificate;
        private String privateKey;

        /**
         * Instantiates a new Builder.
         */
        public Builder() {
        }

        /**
         * Instantiates a new Builder.
         *
         * @param mailConfig the mail config
         */
        public Builder(MailConfig mailConfig) {
            if (mailConfig != null) {
                this.userName = mailConfig.getUserName();
                this.passWord = mailConfig.getPassWord();
                this.sendConfig = mailConfig.getSendConfig();
                this.receiveConfig = mailConfig.getReceiveConfig();
                this.storagePath = mailConfig.getStoragePath();
                this.certificate = mailConfig.getCertificate();
                this.privateKey = mailConfig.getPrivateKey();
            }
        }

        /**
         * Authentication builder.
         *
         * @param userName the username
         * @param passWord the password
         * @return the builder
         * @throws BuilderException the builder exception
         */
        public Builder authentication(String userName, String passWord) throws BuilderException {
            if (!StringUtils.matches(userName, RegexGlobals.EMAIL_ADDRESS)) {
                throw new BuilderException("Invalid username");
            }
            this.userName = userName;
            this.passWord = passWord;
            return this;
        }

        /**
         * Send config builder.
         *
         * @param sendConfig send config
         * @return the builder
         */
        public Builder sendConfig(ServerConfig sendConfig) {
            if (sendConfig != null) {
                this.sendConfig = sendConfig;
            }
            return this;
        }

        /**
         * Receive config builder.
         *
         * @param receiveConfig receive config
         * @return the builder
         */
        public Builder receiveConfig(ServerConfig receiveConfig) {
            if (receiveConfig != null) {
                this.receiveConfig = receiveConfig;
            }
            return this;
        }

        /**
         * Storage path builder.
         *
         * @param storagePath the storage path
         * @return the builder
         * @throws BuilderException the builder exception
         */
        public Builder storagePath(String storagePath) throws BuilderException {
            if (StringUtils.isEmpty(storagePath) || !FileUtils.isExists(storagePath)) {
                throw new BuilderException("Storage path not exists! ");
            }
            this.storagePath = storagePath;
            return this;
        }

        public Builder signer(final X509Certificate x509Certificate, final PrivateKey privateKey) {
            if (x509Certificate != null && privateKey != null) {
                try {
                    this.certificate = StringUtils.base64Encode(x509Certificate.getEncoded());
                    this.privateKey = StringUtils.base64Encode(privateKey.getEncoded());
                } catch (CertificateEncodingException e) {
                    this.certificate = Globals.DEFAULT_VALUE_STRING;
                    this.privateKey = Globals.DEFAULT_VALUE_STRING;
                }
            }
            return this;
        }

        /**
         * Build mail config.
         *
         * @return the mail config
         * @throws BuilderException the builder exception
         */
        public MailConfig build() throws BuilderException {
            if (this.sendConfig == null && this.receiveConfig == null) {
                throw new BuilderException("Unknown server config! ");
            }
            return new MailConfig(this.userName, this.passWord, this.sendConfig, this.receiveConfig,
                    this.storagePath, this.certificate, this.privateKey);
        }
    }
}
