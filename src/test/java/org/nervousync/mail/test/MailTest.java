package org.nervousync.mail.test;

import org.junit.Test;
import org.nervousync.commons.core.Globals;
import org.nervousync.exceptions.builder.BuilderException;
import org.nervousync.mail.MailObject;
import org.nervousync.mail.config.MailConfig;
import org.nervousync.mail.config.ServerConfig;
import org.nervousync.utils.MailUtils;
import org.nervousync.utils.PropertiesUtils;
import org.nervousync.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public final class MailTest {

    private transient final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String MAIL_SUBJECT = "Test mail";
    private static final String MAIL_CONTENT = "Test mail content";

    @Test
    public void mailConfig() throws BuilderException {
        Properties properties = PropertiesUtils.loadProperties("src/test/resources/mail.xml");
        ServerConfig smtpConfig =
                MailUtils.SMTPBuilder()
                        .configHost(properties.getProperty("config.sendAddress"),
                                Integer.parseInt(properties.getProperty("config.sendPort")))
                        .authLogin(true)
                        .useSSL(true).build();
        ServerConfig imapConfig =
                MailUtils.IMAPBuilder()
                        .configHost(properties.getProperty("config.receiveAddress"),
                                Integer.parseInt(properties.getProperty("config.receivePort")))
                        .authLogin(true)
                        .useSSL(true).build();
        MailConfig mailConfig =
                new MailConfig.Builder()
                        .sendConfig(smtpConfig)
                        .receiveConfig(imapConfig)
                        .authentication(properties.getProperty("config.userName"),
                                properties.getProperty("config.passWord"))
                        .storagePath(properties.getProperty("config.storagePath"))
                        .build();
        this.logger.info("Mail Config: {}", mailConfig.toXML());

        MailUtils.mailAgent(mailConfig).ifPresent(mailAgent -> {
            this.logger.info("Mail count: {}", mailAgent.mailCount());
            MailObject mailObject = new MailObject();
            Optional.ofNullable(properties.getProperty("mail.sender")).ifPresent(mailObject::setSendAddress);
            Optional.ofNullable(properties.getProperty("mail.receiver"))
                    .flatMap(receiver -> Optional.of(StringUtils.tokenizeToStringArray(receiver, "|")))
                    .ifPresent(receiveAddress -> mailObject.setReceiveAddress(Arrays.asList(receiveAddress)));
            Optional.ofNullable(properties.getProperty("mail.replies"))
                    .flatMap(replies -> Optional.of(StringUtils.tokenizeToStringArray(replies, "|")))
                    .ifPresent(replyAddress -> mailObject.setReplyAddress(Arrays.asList(replyAddress)));
            Optional.ofNullable(properties.getProperty("mail.attaches"))
                    .flatMap(attaches -> Optional.of(StringUtils.tokenizeToStringArray(attaches, "|")))
                    .ifPresent(attacheFiles -> mailObject.setAttachFiles(Arrays.asList(attacheFiles)));
            mailObject.setSubject(MAIL_SUBJECT);
            mailObject.setContent(MAIL_CONTENT);
            this.logger.info("Send result: {}", mailAgent.sendMail(mailObject));
            List<String> mailList = mailAgent.mailList(Globals.DEFAULT_EMAIL_FOLDER_INBOX);
            mailList.stream().filter(uid ->
                mailAgent.readMail(Globals.DEFAULT_EMAIL_FOLDER_INBOX, uid)
                        .map(receiveObject -> MAIL_SUBJECT.equalsIgnoreCase(receiveObject.getSubject()))
                        .orElse(Boolean.FALSE))
                    .forEach(uid -> {
                        mailAgent.readMail(Globals.DEFAULT_EMAIL_FOLDER_INBOX, uid, Boolean.TRUE)
                                .ifPresent(receiveObject -> {
                                    this.logger.info("UID: {}", receiveObject.getUid());
                                    this.logger.info("Title: {}", receiveObject.getSubject());
                                    this.logger.info("Content: {}", receiveObject.getContent());
                                    receiveObject.getAttachFiles().forEach(this.logger::info);
                                });
                        this.logger.info("Remove mail result: {}",
                                mailAgent.deleteMails(Globals.DEFAULT_EMAIL_FOLDER_INBOX, uid));
                    });
        });
    }
}
