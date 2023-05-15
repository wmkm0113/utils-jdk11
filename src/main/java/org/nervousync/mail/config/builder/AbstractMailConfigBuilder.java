package org.nervousync.mail.config.builder;

import org.nervousync.builder.AbstractBuilder;
import org.nervousync.commons.core.Globals;
import org.nervousync.commons.core.RegexGlobals;
import org.nervousync.commons.proxy.AbstractProxyConfigBuilder;
import org.nervousync.commons.proxy.ProxyConfig;
import org.nervousync.enumerations.mail.MailProtocol;
import org.nervousync.exceptions.builder.BuilderException;
import org.nervousync.mail.config.MailConfig;
import org.nervousync.security.factory.SecureConfig;
import org.nervousync.security.factory.SecureFactory;
import org.nervousync.utils.FileUtils;
import org.nervousync.utils.StringUtils;

import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Optional;

public abstract class AbstractMailConfigBuilder<T> extends AbstractBuilder<T> {

    protected final MailConfig mailConfig;

    protected AbstractMailConfigBuilder(final T parentBuilder, final MailConfig mailConfig) {
        super(parentBuilder);
        this.mailConfig = (mailConfig == null) ? new MailConfig() : mailConfig;
    }

    public AbstractMailConfigBuilder<T> secureName(final String secureName) {
        if (StringUtils.notBlank(secureName) && SecureFactory.getInstance().registeredConfig(secureName)) {
            SecureFactory secureFactory = SecureFactory.getInstance();
            if (StringUtils.notBlank(this.mailConfig.getPassword())) {
                String newPassword =
                        secureFactory.update(this.mailConfig.getPassword(), this.mailConfig.getSecureName(), secureName);
                this.mailConfig.setPassword(newPassword);
            }
            Optional.ofNullable(this.mailConfig.getProxyConfig())
                    .filter(proxyConfig -> StringUtils.notBlank(proxyConfig.getPassword()))
                    .ifPresent(proxyConfig -> {
                        String newPassword = secureFactory.update(proxyConfig.getPassword(),
                                this.mailConfig.getSecureName(), secureName);
                        proxyConfig.setPassword(newPassword);
                        this.mailConfig.setProxyConfig(proxyConfig);
                    });
            this.mailConfig.setSecureName(secureName);
        }
        return this;
    }

    public AbstractMailConfigBuilder<T> secureConfig(final String secureName, final SecureConfig secureConfig) {
        if (StringUtils.notBlank(secureName) && secureConfig != null) {
            SecureFactory secureFactory = SecureFactory.getInstance();
            if (StringUtils.notBlank(this.mailConfig.getPassword())) {
                String newPassword;
                if (secureFactory.registeredConfig(secureName)) {
                    secureFactory.register(Globals.DEFAULT_TEMPLATE_SECURE_NAME, secureConfig);
                    newPassword = secureFactory.update(this.mailConfig.getPassword(), this.mailConfig.getSecureName(),
                            Globals.DEFAULT_TEMPLATE_SECURE_NAME);
                    secureFactory.deregister(Globals.DEFAULT_TEMPLATE_SECURE_NAME);
                } else {
                    newPassword =
                            secureFactory.encrypt(this.mailConfig.getPassword(), secureName);
                }
                this.mailConfig.setPassword(newPassword);
            }
            secureFactory.register(secureName, secureConfig);
            this.mailConfig.setSecureConfig(secureConfig);
        }
        return this;
    }

    /**
     * Authentication builder.
     *
     * @param userName the username
     * @param password the password
     * @return the builder
     * @throws BuilderException the builder exception
     */
    public AbstractMailConfigBuilder<T> authentication(final String userName, final String password) throws BuilderException {
        if (!StringUtils.matches(userName, RegexGlobals.EMAIL_ADDRESS)) {
            throw new BuilderException("Invalid username");
        }
        SecureFactory secureFactory = SecureFactory.getInstance();
        String encPassword;
        if (StringUtils.notBlank(password) && StringUtils.notBlank(this.mailConfig.getSecureName())
                && secureFactory.registeredConfig(this.mailConfig.getSecureName())) {
            encPassword = secureFactory.encrypt(this.mailConfig.getSecureName(), password);
        } else {
            encPassword = password;
        }
        this.mailConfig.setUserName(userName);
        this.mailConfig.setPassword(encPassword);
        return this;
    }

    public ProxyConfigBuilder<T> proxyConfig() {
        return new ProxyConfigBuilder<>(this, this.mailConfig.getSecureName(), this.mailConfig.getProxyConfig());
    }

    public ServerConfigBuilder<T> sendConfig() {
        return new ServerConfigBuilder<>(this, Boolean.TRUE, this.mailConfig.getSendConfig());
    }

    public ServerConfigBuilder<T> receiveConfig() {
        return new ServerConfigBuilder<>(this, Boolean.FALSE, this.mailConfig.getReceiveConfig());
    }

    /**
     * Storage path builder.
     *
     * @param storagePath the storage path
     * @return the builder
     * @throws BuilderException the builder exception
     */
    public AbstractMailConfigBuilder<T> storagePath(String storagePath) throws BuilderException {
        if (StringUtils.isEmpty(storagePath) || !FileUtils.isExists(storagePath)) {
            throw new BuilderException("Storage path not exists! ");
        }
        this.mailConfig.setStoragePath(storagePath);
        return this;
    }

    /**
     * Signer builder.
     *
     * @param x509Certificate the x 509 certificate
     * @param privateKey      the private key
     * @return the builder
     */
    public AbstractMailConfigBuilder<T> signer(final X509Certificate x509Certificate, final PrivateKey privateKey) {
        if (x509Certificate != null && privateKey != null) {
            try {
                this.mailConfig.setCertificate(StringUtils.base64Encode(x509Certificate.getEncoded()));
                this.mailConfig.setPrivateKey(StringUtils.base64Encode(privateKey.getEncoded()));
            } catch (CertificateEncodingException e) {
                this.mailConfig.setCertificate(Globals.DEFAULT_VALUE_STRING);
                this.mailConfig.setPrivateKey(Globals.DEFAULT_VALUE_STRING);
            }
        }
        return this;
    }

    private void proxyConfig(final ProxyConfig proxyConfig) {
        Optional.ofNullable(proxyConfig).ifPresent(this.mailConfig::setProxyConfig);
    }

    private void serverConfig(final boolean sendConfig, final MailConfig.ServerConfig serverConfig) {
        Optional.ofNullable(serverConfig).ifPresent(config -> {
            if (sendConfig) {
                this.mailConfig.setSendConfig(config);
            } else {
                this.mailConfig.setReceiveConfig(config);
            }
        });
    }

    public static final class ProxyConfigBuilder<T> extends AbstractProxyConfigBuilder<AbstractMailConfigBuilder<T>> {

        private ProxyConfigBuilder(final AbstractMailConfigBuilder<T> parentBuilder, final String secureName,
                                   final ProxyConfig proxyConfig) {
            super(parentBuilder, secureName, proxyConfig);
        }

        protected void build() {
            super.parentBuilder.proxyConfig(this.proxyConfig);
        }
    }

    public static final class ServerConfigBuilder<T> extends AbstractBuilder<AbstractMailConfigBuilder<T>> {

        private final boolean sendConfig;
        private String hostName;
        private int hostPort = Globals.DEFAULT_VALUE_INT;
        private boolean ssl;
        private boolean authLogin;
        private MailProtocol protocolOption = MailProtocol.UNKNOWN;
        private int connectionTimeout = 5;
        private int processTimeout = 5;

        private ServerConfigBuilder(final AbstractMailConfigBuilder<T> parentBuilder, final boolean sendConfig,
                                    final MailConfig.ServerConfig serverConfig) {
            super(parentBuilder);
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
        public ServerConfigBuilder<T> configHost(String hostAddress, int hostPort) {
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
        public ServerConfigBuilder<T> useSSL(boolean useSSL) {
            this.ssl = useSSL;
            return this;
        }

        /**
         * Auth login builder.
         *
         * @param authLogin the auth login
         * @return the builder
         */
        public ServerConfigBuilder<T> authLogin(boolean authLogin) {
            this.authLogin = authLogin;
            return this;
        }

        public ServerConfigBuilder<T> mailProtocol(final MailProtocol protocolOption) {
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
        public ServerConfigBuilder<T> connectionTimeout(int connectionTimeout) {
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
        public ServerConfigBuilder<T> processTimeout(int processTimeout) {
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

            super.parentBuilder.serverConfig(this.sendConfig, serverConfig);
        }
    }

}
