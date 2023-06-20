package org.nervousync.test.mail;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.nervousync.commons.core.Globals;
import org.nervousync.enumerations.mail.MailProtocol;
import org.nervousync.exceptions.builder.BuilderException;
import org.nervousync.mail.MailObject;
import org.nervousync.mail.config.MailConfig;
import org.nervousync.mail.config.builder.MailConfigBuilder;
import org.nervousync.security.factory.SecureConfig;
import org.nervousync.security.factory.SecureFactory;
import org.nervousync.test.BaseTest;
import org.nervousync.utils.*;

import java.net.Proxy;
import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.util.*;

public final class MailTest extends BaseTest {

    private static final String MAIL_SECURE = "MailSecure";
    private static final String MAIL_SUBJECT = "Test mail";
    private static final String MAIL_CONTENT = "Test mail content";

    private static Properties PROPERTIES = null;
    private static MailConfig MAIL_CONFIG = null;

    private static boolean SKIP_TEST = Boolean.FALSE;

    @BeforeAll
    public static void initialize() {
        SKIP_TEST = !FileUtils.isExists("src/test/resources/mail.xml");
    }

    @Test
	@Order(0)
    public void generateConfig() throws BuilderException {
        if (SKIP_TEST) {
            return;
        }
        //  Configure security factory
        if (!SecureFactory.initialized()) {
            boolean initResult = SecureFactory.initConfig(SecureFactory.SecureAlgorithm.AES256)
                    .map(SecureFactory::initialize)
                    .orElse(Boolean.FALSE);
            this.logger.info("Initialize SecureFactory result: {}", initResult);
        }
        SecureConfig secureConfig = SecureFactory.initConfig(SecureFactory.SecureAlgorithm.AES192)
                .filter(config -> SecureFactory.getInstance().register(MAIL_SECURE, config))
                .orElse(null);
        long currentTime = DateTimeUtils.currentUTCTimeMillis();
        KeyPair keyPair = SecurityUtils.RSAKeyPair(1024);
        X509Certificate x509Certificate = CertificateUtils.x509(keyPair.getPublic(), IDUtils.snowflake(),
                new Date(currentTime), new Date(currentTime + 365 * 24 * 60 * 60 * 1000L), "TestCert", keyPair.getPrivate(), "SHA1withRSA");
        PROPERTIES = ConvertUtils.loadProperties("src/test/resources/mail.xml");
        MAIL_CONFIG = MailConfigBuilder.newBuilder()
                .secureName(MAIL_SECURE)
                .secureConfig(MAIL_SECURE, secureConfig)
                .sendConfig()
                .mailProtocol(MailProtocol.SMTP)
                .configHost(PROPERTIES.getProperty("config.send.address"),
                        Integer.parseInt(PROPERTIES.getProperty("config.send.port")))
                .authLogin(Boolean.parseBoolean(PROPERTIES.getProperty("config.send.auth")))
                .useSSL(Boolean.parseBoolean(PROPERTIES.getProperty("config.send.ssl")))
                .connectionTimeout(10)
                .processTimeout(10)
                .confirm()
                .proxyConfig()
                .proxyType(Proxy.Type.SOCKS)
                .serverConfig("127.0.0.1", 1080)
                .confirm()
                .receiveConfig()
                .mailProtocol(MailProtocol.valueOf(PROPERTIES.getProperty("config.receive.protocol")))
                .configHost(PROPERTIES.getProperty("config.receive.address"),
                        Integer.parseInt(PROPERTIES.getProperty("config.receive.port")))
                .authLogin(Boolean.parseBoolean(PROPERTIES.getProperty("config.receive.auth")))
                .useSSL(Boolean.parseBoolean(PROPERTIES.getProperty("config.receive.ssl")))
                .confirm()
                .authentication(PROPERTIES.getProperty("config.userName"), PROPERTIES.getProperty("config.passWord"))
                .storagePath(PROPERTIES.getProperty("config.storagePath"))
                .signer(x509Certificate, keyPair.getPrivate())
                .confirm();
        String xmlContent = MAIL_CONFIG.toXML();
        MailConfig parseConfig = StringUtils.stringToObject(xmlContent, MailConfig.class, "https://nervousync.org/schemas/mail", "https://nervousync.org/schemas/proxy");
        this.logger.info("Parse and verified config: {}", parseConfig.toFormattedJson());
    }

    @Test
	@Order(10)
    public void folderList() {
        if (SKIP_TEST) {
            return;
        }
        MailUtils.Agent mailAgent = MailUtils.mailAgent(MAIL_CONFIG);
        Assertions.assertNotNull(mailAgent);
        mailAgent.folderList().forEach(this.logger::info);
    }

    @Test
	@Order(20)
    public void sendMail() {
        if (SKIP_TEST) {
            return;
        }
        MailUtils.Agent mailAgent = MailUtils.mailAgent(MAIL_CONFIG);
        Assertions.assertNotNull(mailAgent);
        this.logger.info("Mail count: {}", mailAgent.mailCount());
        MailObject mailObject = new MailObject();
        Optional.ofNullable(PROPERTIES.getProperty("mail.sender")).ifPresent(mailObject::setSendAddress);
        Optional.ofNullable(PROPERTIES.getProperty("mail.receiver"))
                .flatMap(receiver -> Optional.of(StringUtils.tokenizeToStringArray(receiver, "|")))
                .ifPresent(receiveAddress -> mailObject.setReceiveAddress(Arrays.asList(receiveAddress)));
        Optional.ofNullable(PROPERTIES.getProperty("mail.cc"))
                .flatMap(receiver -> Optional.of(StringUtils.tokenizeToStringArray(receiver, "|")))
                .ifPresent(receiveAddress -> mailObject.setCcAddress(Arrays.asList(receiveAddress)));
        Optional.ofNullable(PROPERTIES.getProperty("mail.bcc"))
                .flatMap(receiver -> Optional.of(StringUtils.tokenizeToStringArray(receiver, "|")))
                .ifPresent(receiveAddress -> mailObject.setBccAddress(Arrays.asList(receiveAddress)));
        Optional.ofNullable(PROPERTIES.getProperty("mail.replies"))
                .flatMap(replies -> Optional.of(StringUtils.tokenizeToStringArray(replies, "|")))
                .ifPresent(replyAddress -> mailObject.setReplyAddress(Arrays.asList(replyAddress)));
        Optional.ofNullable(PROPERTIES.getProperty("mail.attaches"))
                .flatMap(attaches -> Optional.of(StringUtils.tokenizeToStringArray(attaches, "|")))
                .ifPresent(attacheFiles -> mailObject.setAttachFiles(Arrays.asList(attacheFiles)));
        mailObject.setSubject(MAIL_SUBJECT);
        mailObject.setContent(MAIL_CONTENT);
        this.logger.info("Send result: {}", mailAgent.sendMail(mailObject));
    }

    @Test
	@Order(30)
    public void receiveMail() {
        if (SKIP_TEST) {
            return;
        }
        MailUtils.Agent mailAgent = MailUtils.mailAgent(MAIL_CONFIG);
        Assertions.assertNotNull(mailAgent);
        mailAgent.mailList(Globals.DEFAULT_EMAIL_FOLDER_INBOX)
                .stream()
                .filter(uid ->
                        mailAgent.readMail(Globals.DEFAULT_EMAIL_FOLDER_INBOX, uid)
                                .map(receiveObject -> MAIL_SUBJECT.equalsIgnoreCase(receiveObject.getSubject()))
                                .orElse(Boolean.FALSE))
                .forEach(uid ->
                        mailAgent.readMail(Globals.DEFAULT_EMAIL_FOLDER_INBOX, uid, Boolean.TRUE)
                                .ifPresent(receiveObject -> {
                                    this.logger.info("UID: {}", receiveObject.getUid());
                                    this.logger.info("Title: {}", receiveObject.getSubject());
                                    this.logger.info("Content: {}", receiveObject.getContent());
                                    receiveObject.getAttachFiles().forEach(filePath -> {
                                        this.logger.info(filePath);
                                        FileUtils.removeFile(filePath);
                                    });
                                }));
    }

    @Test
	@Order(40)
    public void flagMail() {
        if (SKIP_TEST) {
            return;
        }
        MailUtils.Agent mailAgent = MailUtils.mailAgent(MAIL_CONFIG);
        Assertions.assertNotNull(mailAgent);
        mailAgent.mailList(Globals.DEFAULT_EMAIL_FOLDER_INBOX)
                .stream()
                .filter(uid ->
                        mailAgent.readMail(Globals.DEFAULT_EMAIL_FOLDER_INBOX, uid)
                                .map(receiveObject -> MAIL_SUBJECT.equalsIgnoreCase(receiveObject.getSubject()))
                                .orElse(Boolean.FALSE))
                .forEach(uid -> {
                    this.logger.info("Read mail result: {}",
                            mailAgent.readMails(Globals.DEFAULT_EMAIL_FOLDER_INBOX, uid));
                    this.logger.info("Unread mail result: {}",
                            mailAgent.unreadMails(Globals.DEFAULT_EMAIL_FOLDER_INBOX, uid));
                    this.logger.info("Answer mail result: {}",
                            mailAgent.answerMails(Globals.DEFAULT_EMAIL_FOLDER_INBOX, uid));
                    this.logger.info("Flag mail result: {}",
                            mailAgent.flagMails(Globals.DEFAULT_EMAIL_FOLDER_INBOX, uid));
                    this.logger.info("Unflag mail result: {}",
                            mailAgent.unflagMails(Globals.DEFAULT_EMAIL_FOLDER_INBOX, uid));
                    this.logger.info("Delete mail result: {}",
                            mailAgent.deleteMails(Globals.DEFAULT_EMAIL_FOLDER_INBOX, uid));
                });
    }

    @Test
	@Order(50)
    public void recoveryMail() {
        if (SKIP_TEST) {
            return;
        }
        MailUtils.Agent mailAgent = MailUtils.mailAgent(MAIL_CONFIG);
        Assertions.assertNotNull(mailAgent);
        mailAgent.mailList(Globals.DEFAULT_EMAIL_FOLDER_TRASH)
                .stream()
                .filter(uid ->
                        mailAgent.readMail(Globals.DEFAULT_EMAIL_FOLDER_TRASH, uid)
                                .map(receiveObject -> MAIL_SUBJECT.equalsIgnoreCase(receiveObject.getSubject()))
                                .orElse(Boolean.FALSE))
                .forEach(uid ->
                        this.logger.info("Recovery mail uid {} result: {}", uid,
                                mailAgent.recoverMails(Globals.DEFAULT_EMAIL_FOLDER_TRASH, uid)));
    }

    @Test
	@Order(60)
    public void dropMail() {
        if (SKIP_TEST) {
            return;
        }
        MailUtils.Agent mailAgent = MailUtils.mailAgent(MAIL_CONFIG);
        Assertions.assertNotNull(mailAgent);
        mailAgent.mailList(Globals.DEFAULT_EMAIL_FOLDER_INBOX)
                .stream()
                .filter(uid ->
                        mailAgent.readMail(Globals.DEFAULT_EMAIL_FOLDER_INBOX, uid)
                                .map(receiveObject -> MAIL_SUBJECT.equalsIgnoreCase(receiveObject.getSubject()))
                                .orElse(Boolean.FALSE))
                .forEach(uid ->
                        this.logger.info("Delete mail result: {}",
                                mailAgent.deleteMails(Globals.DEFAULT_EMAIL_FOLDER_INBOX, uid)));
        mailAgent.mailList(Globals.DEFAULT_EMAIL_FOLDER_TRASH).stream().filter(uid ->
                        mailAgent.readMail(Globals.DEFAULT_EMAIL_FOLDER_TRASH, uid)
                                .map(receiveObject -> MAIL_SUBJECT.equalsIgnoreCase(receiveObject.getSubject()))
                                .orElse(Boolean.FALSE))
                .forEach(uid ->
                        this.logger.info("Drop mail uid: {} result: {}", uid,
                                mailAgent.deleteMails(Globals.DEFAULT_EMAIL_FOLDER_TRASH, uid)));
    }

    @Test
	@Order(70)
    public void mailCount() {
        if (SKIP_TEST) {
            return;
        }
        MailUtils.Agent mailAgent = MailUtils.mailAgent(MAIL_CONFIG);
        Assertions.assertNotNull(mailAgent);
        this.logger.info("INBOX Count: {}", mailAgent.mailCount(Globals.DEFAULT_EMAIL_FOLDER_INBOX));
        this.logger.info("SPAM Count: {}", mailAgent.mailCount(Globals.DEFAULT_EMAIL_FOLDER_SPAM));
        this.logger.info("DRAFTS Count: {}", mailAgent.mailCount(Globals.DEFAULT_EMAIL_FOLDER_DRAFTS));
        this.logger.info("SENT Count: {}", mailAgent.mailCount(Globals.DEFAULT_EMAIL_FOLDER_SENT));
        this.logger.info("TRASH Count: {}", mailAgent.mailCount(Globals.DEFAULT_EMAIL_FOLDER_TRASH));
    }
}
