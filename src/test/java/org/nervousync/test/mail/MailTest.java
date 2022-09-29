package org.nervousync.test.mail;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.nervousync.commons.core.Globals;
import org.nervousync.enumerations.mail.MailProtocol;
import org.nervousync.exceptions.builder.BuilderException;
import org.nervousync.mail.MailObject;
import org.nervousync.mail.config.MailConfig;
import org.nervousync.mail.config.MailConfigBuilder;
import org.nervousync.security.factory.SecureFactory;
import org.nervousync.test.BaseTest;
import org.nervousync.utils.*;

import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.util.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public final class MailTest extends BaseTest {

    private static final String MAIL_SUBJECT = "Test mail";
    private static final String MAIL_CONTENT = "Test mail content";
    
    private static Properties PROPERTIES = null;
    private static MailConfig MAIL_CONFIG = null;

    private static boolean SKIP_TEST = Boolean.FALSE;

    @BeforeClass
    public static void initialize() throws BuilderException {
        if (!FileUtils.isExists("src/test/resources/mail.xml")) {
            SKIP_TEST = Boolean.TRUE;
            return;
        }
        //  Configure security factory
        boolean initResult = SecureFactory.initConfig(SecureFactory.SecureAlgorithm.AES256)
                .map(SecureFactory::initialize)
                .orElse(Boolean.FALSE);
        System.out.println("Initialize SecureFactory result: " + initResult);
        SecureFactory.initConfig(SecureFactory.SecureAlgorithm.AES192)
                .ifPresent(secureConfig -> SecureFactory.getInstance().register("MailSecure", secureConfig));
        long currentTime = DateTimeUtils.currentUTCTimeMillis();
        KeyPair keyPair = SecurityUtils.RSAKeyPair(1024);
        X509Certificate x509Certificate = CertificateUtils.x509(keyPair.getPublic(), (long) IDUtils.random(IDUtils.SNOWFLAKE),
                new Date(currentTime), new Date(currentTime + 365 * 24 * 60 * 60 * 1000L), "TestCert", keyPair.getPrivate(), "SHA1withRSA");
        PROPERTIES = PropertiesUtils.loadProperties("src/test/resources/mail.xml");
        MAIL_CONFIG = MailConfigBuilder.newBuilder()
                .secureName(PROPERTIES.getProperty("config.secureName"))
                .sendConfig()
                .mailProtocol(MailProtocol.SMTP)
                .configHost(PROPERTIES.getProperty("config.sendAddress"),
                        Integer.parseInt(PROPERTIES.getProperty("config.sendPort")))
                .authLogin(Boolean.TRUE)
                .useSSL(Boolean.TRUE)
                .connectionTimeout(10)
                .processTimeout(10)
                .confirm()
                .receiveConfig()
                .mailProtocol(MailProtocol.valueOf(PROPERTIES.getProperty("config.receiveProtocol")))
                .configHost(PROPERTIES.getProperty("config.receiveAddress"),
                        Integer.parseInt(PROPERTIES.getProperty("config.receivePort")))
                .authLogin(Boolean.TRUE)
                .useSSL(Boolean.TRUE)
                .confirm()
                .authentication(PROPERTIES.getProperty("config.userName"), PROPERTIES.getProperty("config.passWord"))
                .storagePath(PROPERTIES.getProperty("config.storagePath"))
                .signer(x509Certificate, keyPair.getPrivate())
                .build();
        String xmlContent = MAIL_CONFIG.toXML();
        System.out.println("Mail Config: " + xmlContent);
        MailConfig parseConfig = StringUtils.stringToObject(xmlContent, Globals.DEFAULT_ENCODING, MailConfig.class,
                "src/main/resources/org/nervousync/resources/mail_config.xsd");
        Assert.assertNotNull(parseConfig);
    }

    @Test
    public void test000FolderList() {
        if (SKIP_TEST) {
            return;
        }
        MailUtils.Agent mailAgent = MailUtils.mailAgent(MAIL_CONFIG);
        Assert.assertNotNull(mailAgent);
        mailAgent.folderList().forEach(this.logger::info);
    }

    @Test
    public void test010SendMail() {
        if (SKIP_TEST) {
            return;
        }
        MailUtils.Agent mailAgent = MailUtils.mailAgent(MAIL_CONFIG);
        Assert.assertNotNull(mailAgent);
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
    public void test020ReceiveMail() {
        if (SKIP_TEST) {
            return;
        }
        MailUtils.Agent mailAgent = MailUtils.mailAgent(MAIL_CONFIG);
        Assert.assertNotNull(mailAgent);
        mailAgent.mailList(Globals.DEFAULT_EMAIL_FOLDER_INBOX).stream().filter(uid ->
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
    public void test030FlagMail() {
        if (SKIP_TEST) {
            return;
        }
        MailUtils.Agent mailAgent = MailUtils.mailAgent(MAIL_CONFIG);
        Assert.assertNotNull(mailAgent);
        mailAgent.mailList(Globals.DEFAULT_EMAIL_FOLDER_INBOX).stream().filter(uid ->
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
    public void test040RecoveryMail() {
        if (SKIP_TEST) {
            return;
        }
        MailUtils.Agent mailAgent = MailUtils.mailAgent(MAIL_CONFIG);
        Assert.assertNotNull(mailAgent);
        List<String> mailList = mailAgent.mailList("已删除邮件");
        mailList.stream().filter(uid ->
                        mailAgent.readMail("已删除邮件", uid)
                                .map(receiveObject -> MAIL_SUBJECT.equalsIgnoreCase(receiveObject.getSubject()))
                                .orElse(Boolean.FALSE))
                .forEach(uid ->
                        this.logger.info("Recovery mail uid {} result: {}", uid,
                                mailAgent.recoverMails("已删除邮件", uid)));
    }

    @Test
    public void test050DropMail() {
        if (SKIP_TEST) {
            return;
        }
        MailUtils.Agent mailAgent = MailUtils.mailAgent(MAIL_CONFIG);
        Assert.assertNotNull(mailAgent);
        mailAgent.mailList(Globals.DEFAULT_EMAIL_FOLDER_INBOX).stream().filter(uid ->
                        mailAgent.readMail(Globals.DEFAULT_EMAIL_FOLDER_INBOX, uid)
                                .map(receiveObject -> MAIL_SUBJECT.equalsIgnoreCase(receiveObject.getSubject()))
                                .orElse(Boolean.FALSE))
                .forEach(uid ->
                        this.logger.info("Delete mail result: {}",
                                mailAgent.deleteMails(Globals.DEFAULT_EMAIL_FOLDER_INBOX, uid)));
        mailAgent.mailList("已删除邮件").stream().filter(uid ->
                        mailAgent.readMail("已删除邮件", uid)
                                .map(receiveObject -> MAIL_SUBJECT.equalsIgnoreCase(receiveObject.getSubject()))
                                .orElse(Boolean.FALSE))
                .forEach(uid ->
                        this.logger.info("Drop mail uid: {} result: {}", uid,
                                mailAgent.deleteMails("已删除邮件", uid)));
    }

    @Test
    public void test060MailCount() {
        if (SKIP_TEST) {
            return;
        }
        MailUtils.Agent mailAgent = MailUtils.mailAgent(MAIL_CONFIG);
        Assert.assertNotNull(mailAgent);
        this.logger.info("INBOX Count: {}", mailAgent.mailCount(Globals.DEFAULT_EMAIL_FOLDER_INBOX));
        this.logger.info("SPAM Count: {}", mailAgent.mailCount(Globals.DEFAULT_EMAIL_FOLDER_SPAM));
        this.logger.info("DRAFTS Count: {}", mailAgent.mailCount(Globals.DEFAULT_EMAIL_FOLDER_DRAFTS));
        this.logger.info("SENT Count: {}", mailAgent.mailCount(Globals.DEFAULT_EMAIL_FOLDER_SENT));
        this.logger.info("TRASH Count: {}", mailAgent.mailCount(Globals.DEFAULT_EMAIL_FOLDER_TRASH));
    }
}
