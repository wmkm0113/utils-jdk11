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
package org.nervousync.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.*;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import jakarta.mail.util.ByteArrayDataSource;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.smime.SMIMECapabilitiesAttribute;
import org.bouncycastle.asn1.smime.SMIMECapability;
import org.bouncycastle.asn1.smime.SMIMECapabilityVector;
import org.bouncycastle.asn1.smime.SMIMEEncryptionKeyPreferenceAttribute;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cms.SignerInfoGenerator;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationVerifier;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoGeneratorBuilder;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.mail.smime.SMIMEException;
import org.bouncycastle.mail.smime.SMIMESignedGenerator;
import org.bouncycastle.mail.smime.SMIMESignedParser;
import org.bouncycastle.mail.smime.SMIMEUtil;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.eclipse.angus.mail.imap.IMAPFolder;
import org.eclipse.angus.mail.pop3.POP3Folder;
import org.nervousync.enumerations.mail.MailProtocol;
import org.nervousync.mail.MailObject;
import org.nervousync.mail.authenticator.DefaultAuthenticator;
import org.nervousync.mail.config.MailConfig;
import org.nervousync.mail.operator.ReceiveOperator;
import org.nervousync.mail.operator.SendOperator;
import org.nervousync.mail.protocol.impl.IMAPProtocol;
import org.nervousync.mail.protocol.impl.POP3Protocol;
import org.nervousync.mail.protocol.impl.SMTPProtocol;
import org.nervousync.commons.core.Globals;
import org.nervousync.security.factory.SecureFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The type Mail utils.
 *
 * @author Steven Wee     <a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: Jul 31, 2012 8:54:04 PM $
 */
public final class MailUtils {

    private MailUtils() {
    }

    /**
     * Initialize a new mail agent by given mail config
     *
     * @param mailConfig Mail config
     * @return Optional instance of generated mail agent or empty Option instance if mail config is invalid
     */
    public static Agent mailAgent(final MailConfig mailConfig) {
        if (mailConfig == null || StringUtils.isEmpty(mailConfig.getUserName())
                || StringUtils.isEmpty(mailConfig.getPassword())) {
            return null;
        }
        return new Agent(mailConfig);
    }

    /**
     * The type Agent.
     */
    public static final class Agent {

        private final Logger logger = LoggerFactory.getLogger(this.getClass());

        private final String userName;
        private final String passWord;
        private final MailConfig.ServerConfig sendConfig;
        private final SendOperator sendOperator;
        private final MailConfig.ServerConfig receiveConfig;
        private final ReceiveOperator receiveOperator;
        private final String storagePath;
        private final X509Certificate x509Certificate;
        private final PrivateKey privateKey;

        private Agent(final MailConfig mailConfig) {
            this.userName = mailConfig.getUserName().toLowerCase();
            this.passWord = SecureFactory.getInstance().decrypt(mailConfig.getSecureName(), mailConfig.getPassword());
            if (mailConfig.getSendConfig() == null
                    || !MailProtocol.SMTP.equals(mailConfig.getSendConfig().getProtocolOption())) {
                this.sendConfig = null;
                this.sendOperator = null;
            } else {
                this.sendConfig = mailConfig.getSendConfig();
                this.sendOperator = new SMTPProtocol(mailConfig.getSecureName(), mailConfig.getProxyConfig());
            }
            if (mailConfig.getReceiveConfig() == null) {
                this.receiveConfig = null;
                this.receiveOperator = null;
            } else {
                this.receiveConfig = mailConfig.getReceiveConfig();
                switch (this.receiveConfig.getProtocolOption()) {
                    case IMAP:
                        this.receiveOperator = new IMAPProtocol(mailConfig.getSecureName(), mailConfig.getProxyConfig());
                        break;
                    case POP3:
                        this.receiveOperator = new POP3Protocol(mailConfig.getSecureName(), mailConfig.getProxyConfig());
                        break;
                    default:
                        this.receiveOperator = null;
                        break;
                }
            }
            this.storagePath = mailConfig.getStoragePath();
            this.x509Certificate = StringUtils.notBlank(mailConfig.getCertificate())
                    ? CertificateUtils.x509(StringUtils.base64Decode(mailConfig.getCertificate()))
                    : null;
            this.privateKey = StringUtils.notBlank(mailConfig.getPrivateKey())
                    ? CertificateUtils.privateKey("RSA", StringUtils.base64Decode(mailConfig.getPrivateKey()))
                    : null;
        }

        /**
         * Send mail boolean.
         *
         * @param mailObject the mail object
         * @return the boolean
         */
        public boolean sendMail(final MailObject mailObject) {
            if (this.sendOperator == null) {
                //	Not config send server
                return Boolean.FALSE;
            }
            try {
                Properties properties = this.sendOperator.readConfig(this.sendConfig);
                if (StringUtils.notBlank(this.userName)) {
                    properties.setProperty("mail.smtp.from", this.userName);
                }
                Session session =
                        Session.getDefaultInstance(properties, new DefaultAuthenticator(this.userName, this.passWord));
                session.setDebug(this.logger.isDebugEnabled());
                if (StringUtils.isEmpty(mailObject.getSendAddress())) {
                    mailObject.setSendAddress(this.userName);
                }
                Transport.send(convert(session, mailObject, this.x509Certificate, this.privateKey));
                return Boolean.TRUE;
            } catch (MessagingException e) {
                this.logger.error("Send mail failed!");
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Stack message: ", e);
                }
                return Boolean.FALSE;
            }
        }

        public List<String> folderList() {
            List<String> folderList = new ArrayList<>();
            try (Store store = connect()) {
                Optional.ofNullable(store.getDefaultFolder())
                        .ifPresent(defaultFolder -> {
                            try {
                                Optional.ofNullable(defaultFolder.list()).map(Arrays::asList)
                                        .ifPresent(folders ->
                                                folders.forEach(folder -> folderList.add(folder.getFullName())));
                            } catch (MessagingException e) {
                                if (this.logger.isDebugEnabled()) {
                                    this.logger.debug("Read folder list error! ", e);
                                }
                            }
                        });
            } catch (Exception e) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Read folder list error! ", e);
                }
            }
            return folderList;
        }

        /**
         * Mail count int.
         *
         * @return the int
         */
        public int mailCount() {
            return this.mailCount(Globals.DEFAULT_EMAIL_FOLDER_INBOX);
        }

        /**
         * Mail count int.
         *
         * @param folderName the folder name
         * @return the int
         */
        public int mailCount(final String folderName) {
            if (this.receiveOperator == null) {
                //	Not configs receive server
                return Globals.DEFAULT_VALUE_INT;
            }
            try (Store store = connect(); Folder folder = openReadOnlyFolder(store, folderName)) {
                if (folder.exists() && folder.isOpen()) {
                    return folder.getMessageCount();
                }
            } catch (Exception e) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Receive Message Error! ", e);
                }
            }
            return Globals.DEFAULT_VALUE_INT;
        }

        /**
         * Mail list.
         *
         * @return the list
         */
        public List<String> mailList() {
            return this.mailList(Globals.DEFAULT_EMAIL_FOLDER_INBOX);
        }

        /**
         * Mail list.
         *
         * @param folderName the folder name
         * @return the list
         */
        public List<String> mailList(final String folderName) {
            return mailList(folderName, Globals.DEFAULT_VALUE_INT, Globals.DEFAULT_VALUE_INT);
        }

        /**
         * Mail list.
         *
         * @param folderName the folder name
         * @param begin      the beginning index
         * @param end        the end index
         * @return the list
         */
        public List<String> mailList(final String folderName, final int begin, final int end) {
            if (this.receiveOperator == null || end < begin) {
                return Collections.emptyList();
            }

            try (Store store = connect(); Folder folder = openReadOnlyFolder(store, folderName)) {
                if (!folder.exists() || !folder.isOpen()) {
                    return Collections.emptyList();
                }

                int totalCount = folder.getMessageCount();
                List<String> mailList = new ArrayList<>();
                int start = Math.max(1, begin);
                int stop = (end < 0) ? totalCount : Math.min(totalCount, end);
                for (Message message : folder.getMessages(start, stop)) {
                    mailList.add(this.receiveOperator.readUID(folder, message));
                }
                return mailList;
            } catch (Exception e) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Receive Message Error! ", e);
                }
            }
            return Collections.emptyList();
        }

        /**
         * Read mail optional.
         *
         * @param folderName the folder name
         * @param uid        the uid
         * @return the optional
         */
        public Optional<MailObject> readMail(final String folderName, final String uid) {
            return this.readMail(folderName, uid, Boolean.FALSE);
        }

        /**
         * Read mail optional.
         *
         * @param folderName the folder name
         * @param uid        the uid
         * @param detail     read mail detail
         * @return the optional
         */
        public Optional<MailObject> readMail(final String folderName, final String uid, final boolean detail) {
            if (this.receiveOperator == null) {
                return Optional.empty();
            }
            try (Store store = connect(); Folder folder = openReadOnlyFolder(store, folderName)) {
                if (!folder.exists() || !folder.isOpen()) {
                    return Optional.empty();
                }

                Message message = this.receiveOperator.readMessage(folder, uid);
                if (message != null) {
                    return receiveMessage((MimeMessage) message, detail);
                }
            } catch (Exception e) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Receive Message Error! ", e);
                }
            }

            return Optional.empty();
        }

        /**
         * Read the mail list.
         *
         * @param folderName the folder name
         * @param uidArrays  the uid arrays
         * @return the list
         */
        public List<MailObject> readMailList(final String folderName, final String... uidArrays) {
            List<MailObject> mailList = new ArrayList<>();
            if (this.receiveOperator == null) {
                return mailList;
            }

            try (Store store = connect(); Folder folder = openReadOnlyFolder(store, folderName)) {
                if (!folder.exists() || !folder.isOpen()) {
                    return mailList;
                }
                this.receiveOperator.readMessages(folder, uidArrays)
                        .forEach(message ->
                                receiveMessage((MimeMessage) message, Boolean.FALSE)
                                        .ifPresent(mailList::add));
            } catch (Exception e) {
                this.logger.error("Receive Message Error! ");
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Stack message: ", e);
                }
            }

            return mailList;
        }

        /**
         * Set mails status as read by uid list
         *
         * @param folderName the folder name
         * @param uidArrays  the uid arrays
         * @return the boolean
         */
        public boolean readMails(final String folderName, final String... uidArrays) {
            return this.flagMailsStatus(Flags.Flag.SEEN, Boolean.TRUE, folderName, uidArrays);
        }

        /**
         * Set mails status as unread by uid list
         *
         * @param folderName the folder name
         * @param uidArrays  the uid arrays
         * @return the boolean
         */
        public boolean unreadMails(final String folderName, final String... uidArrays) {
            return this.flagMailsStatus(Flags.Flag.SEEN, Boolean.FALSE, folderName, uidArrays);
        }

        /**
         * Set mails status as answered by uid list
         *
         * @param folderName the folder name
         * @param uidArrays  the uid arrays
         * @return the boolean
         */
        public boolean answerMails(final String folderName, final String... uidArrays) {
            return this.flagMailsStatus(Flags.Flag.ANSWERED, Boolean.TRUE, folderName, uidArrays);
        }

        /**
         * Set mails status as deleted by uid list
         *
         * @param folderName the folder name
         * @param uidArrays  the uid arrays
         * @return the boolean
         */
        public boolean deleteMails(final String folderName, final String... uidArrays) {
            return this.flagMailsStatus(Flags.Flag.DELETED, Boolean.TRUE, folderName, uidArrays);
        }

        /**
         * Set mails status as not deleted by uid list
         *
         * @param folderName the folder name
         * @param uidArrays  the uid arrays
         * @return the boolean
         */
        public boolean recoverMails(final String folderName, final String... uidArrays) {
            if (this.receiveOperator == null) {
                return Boolean.FALSE;
            }
            try (Store store = connect(); Folder folder = openFolder(store, Boolean.FALSE, folderName);
                 Folder inbox = openFolder(store, Boolean.FALSE, Globals.DEFAULT_EMAIL_FOLDER_INBOX)) {
                if (!folder.exists() || !folder.isOpen()) {
                    return Boolean.FALSE;
                }

                List<Message> messageList = this.receiveOperator.readMessages(folder, uidArrays);

                folder.copyMessages(messageList.toArray(new Message[0]), inbox);
                return true;
            } catch (Exception e) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Set message status error! ", e);
                }
                return Boolean.FALSE;
            }
        }

        /**
         * Set mails status as flagged by uid list
         *
         * @param folderName the folder name
         * @param uidArrays  the uid arrays
         * @return the boolean
         */
        public boolean flagMails(final String folderName, final String... uidArrays) {
            return this.flagMailsStatus(Flags.Flag.FLAGGED, Boolean.TRUE, folderName, uidArrays);
        }

        /**
         * Set mails status as not flagged by uid list
         *
         * @param folderName the folder name
         * @param uidArrays  the uid arrays
         * @return the boolean
         */
        public boolean unflagMails(final String folderName, final String... uidArrays) {
            return this.flagMailsStatus(Flags.Flag.FLAGGED, Boolean.FALSE, folderName, uidArrays);
        }

        /**
         * Flag mails boolean.
         *
         * @param flag      the flag
         * @param status    the status
         * @param uidArrays the uid arrays
         * @return the boolean
         */
        private boolean flagMailsStatus(final Flags.Flag flag, final boolean status,
                                        final String folderName, final String... uidArrays) {
            if (this.receiveOperator == null) {
                return Boolean.FALSE;
            }
            try (Store store = connect(); Folder folder = openFolder(store, Boolean.FALSE, folderName)) {

                if (!folder.exists() || !folder.isOpen()) {
                    return Boolean.FALSE;
                }

                List<Message> messageList = this.receiveOperator.readMessages(folder, uidArrays);

                for (Message message : messageList) {
                    message.setFlag(flag, status);
                }
                return true;
            } catch (Exception e) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Set message status error! ", e);
                }
                return Boolean.FALSE;
            }
        }

        /**
         * Connect to mail server
         *
         * @return Store instance
         * @throws MessagingException connect failed
         */
        private Store connect() throws MessagingException {
            Properties properties = this.receiveOperator.readConfig(this.receiveConfig);
            Session session =
                    Session.getInstance(properties, new DefaultAuthenticator(this.userName, this.passWord));
            session.setDebug(this.logger.isDebugEnabled());

            Store store = session.getStore(properties.getProperty("mail.store.protocol"));

            store.connect(this.receiveConfig.getHostName(), this.receiveConfig.getHostPort(),
                    this.userName, this.passWord);
            return store;
        }

        @SuppressWarnings("unchecked")
        private boolean verifyMessage(final MimeMessage mimeMessage) {
            try {
                MimeMessage signedMessage = new MimeMessage(mimeMessage);
                SMIMESignedParser signedParser;
                if (signedMessage.isMimeType("multipart/signed")) {
                    signedParser = new SMIMESignedParser(new JcaDigestCalculatorProviderBuilder().build(),
                            (MimeMultipart) signedMessage.getContent());
                } else if (signedMessage.isMimeType("application/pkcs7-mime")) {
                    signedParser = new SMIMESignedParser(new JcaDigestCalculatorProviderBuilder().build(), signedMessage);
                } else {
                    return Boolean.TRUE;
                }

                org.bouncycastle.util.Store<?> certificates = signedParser.getCertificates();
                for (SignerInformation signerInformation : signedParser.getSignerInfos().getSigners()) {
                    Collection<?> certCollection = certificates.getMatches(signerInformation.getSID());
                    X509Certificate certificate =
                            new JcaX509CertificateConverter().setProvider("BC")
                                    .getCertificate((X509CertificateHolder) certCollection.iterator().next());

                    try {
                        SignerInformationVerifier signerInformationVerifier =
                                new JcaSimpleSignerInfoVerifierBuilder().setProvider("BC").build(certificate);
                        return signerInformation.verify(signerInformationVerifier);
                    } catch (Exception e) {
                        if (this.logger.isDebugEnabled()) {
                            this.logger.debug("Verify signature failed! ", e);
                        }
                    }
                }
            } catch (Exception e) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Verify signature failed! ", e);
                }
            }
            return Boolean.FALSE;
        }

        /**
         * Read mail info
         *
         * @param mimeMessage MIME message instance
         * @param detail      read detail
         * @return Mail object instance
         */
        private Optional<MailObject> receiveMessage(final MimeMessage mimeMessage, final boolean detail) {
            if (!verifyMessage(mimeMessage)) {
                return Optional.empty();
            }
            try {
                MailObject mailObject = new MailObject();
                List<String> receiveList = new ArrayList<>();
                Address[] allRecipients = mimeMessage.getAllRecipients();
                if (allRecipients == null) {
                    return Optional.empty();
                }
                Arrays.stream(allRecipients)
                        .filter(InternetAddress.class::isInstance)
                        .forEach(address -> receiveList.add(((InternetAddress) address).getAddress().toLowerCase()));
                if (!receiveList.contains(this.userName)) {
                    throw new MessagingException("Current account not in receive list! ");
                }

                mailObject.setReceiveAddress(receiveList);

                Folder folder = mimeMessage.getFolder();

                if (folder instanceof POP3Folder) {
                    mailObject.setUid(((POP3Folder) folder).getUID(mimeMessage));
                } else if (folder instanceof IMAPFolder) {
                    mailObject.setUid(Long.valueOf(((IMAPFolder) folder).getUID(mimeMessage)).toString());
                }
                String subject = mimeMessage.getSubject();

                if (StringUtils.notBlank(subject)) {
                    mailObject.setSubject(MimeUtility.decodeText(subject));
                } else {
                    mailObject.setSubject(Globals.DEFAULT_VALUE_STRING);
                }
                mailObject.setSendDate(mimeMessage.getSentDate());
                mailObject.setSendAddress(MimeUtility.decodeText(InternetAddress.toString(mimeMessage.getFrom())));

                if (detail) {
                    //	Read mail cc address
                    Optional.ofNullable((InternetAddress[]) mimeMessage.getRecipients(Message.RecipientType.CC))
                            .ifPresent(ccAddress -> {
                                List<String> ccList = new ArrayList<>();
                                Arrays.asList(ccAddress).forEach(address -> ccList.add(address.getAddress()));
                                mailObject.setCcAddress(ccList);
                            });

                    //	Read mail bcc address
                    Optional.ofNullable((InternetAddress[]) mimeMessage.getRecipients(Message.RecipientType.BCC))
                            .ifPresent(bccAddress -> {
                                List<String> bccList = new ArrayList<>();
                                Arrays.asList(bccAddress).forEach(address -> bccList.add(address.getAddress()));
                                mailObject.setBccAddress(bccList);
                            });

                    //	Read mail content message
                    StringBuilder contentBuffer = new StringBuilder();
                    getMailContent(mimeMessage, contentBuffer);
                    mailObject.setContent(contentBuffer.toString());
                    mailObject.setContentType(mimeMessage.getContentType());

                    mailObject.setAttachFiles(getMailAttachment(mimeMessage));
                }

                return Optional.of(mailObject);
            } catch (MessagingException | IOException e) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Receive message error! ", e);
                }
                return Optional.empty();
            }
        }

        private List<String> getMailAttachment(final Part part) throws MessagingException, IOException {
            List<String> saveFiles = new ArrayList<>();
            if (StringUtils.isEmpty(this.storagePath)) {
                throw new IOException("Save attach file path error! ");
            }
            if (part.isMimeType(Globals.DEFAULT_CONTENT_TYPE_MULTIPART)) {
                Multipart multipart = (Multipart) part.getContent();
                int count = multipart.getCount();
                for (int i = 0; i < count; i++) {
                    Optional.ofNullable(multipart.getBodyPart(i))
                            .ifPresent(bodyPart -> this.readBodyPart(bodyPart, saveFiles));
                }
            }
            return saveFiles;
        }

        private void readBodyPart(final Part bodyPart, final List<String> saveFiles) {
            try {
                if (bodyPart.getHeader("Content-ID") != null
                        && bodyPart.getHeader("Content-ID").length > 0) {
                    return;
                }
                if (StringUtils.notBlank(bodyPart.getFileName())) {
                    String disposition = bodyPart.getDisposition();
                    if (disposition != null
                            && (disposition.equals(Part.ATTACHMENT) || disposition.equals(Part.INLINE))) {
                        String savePath = this.storagePath + Globals.DEFAULT_PAGE_SEPARATOR
                                + MimeUtility.decodeText(bodyPart.getFileName());
                        if (!savePath.toLowerCase().endsWith("p7s")) {
                            boolean saveFile = FileUtils.saveFile(bodyPart.getInputStream(), savePath);
                            if (saveFile) {
                                saveFiles.add(savePath);
                            }
                        }
                    } else if (bodyPart.isMimeType(Globals.DEFAULT_CONTENT_TYPE_MULTIPART)) {
                        saveFiles.addAll(getMailAttachment(bodyPart));
                    }
                }
            } catch (MessagingException | IOException e) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Receive attaches file error! ", e);
                }
            }
        }
    }

    private static MimeMessage convert(final Session session, final MailObject mailObject,
                                       final X509Certificate x509Certificate, final PrivateKey privateKey)
            throws MessagingException {
        MimeMessage message = new MimeMessage(session);

        message.setSubject(mailObject.getSubject(), mailObject.getCharset());

        MimeMultipart mimeMultipart = new MimeMultipart();

        if (mailObject.getAttachFiles() != null) {
            for (String attachment : mailObject.getAttachFiles()) {
                MimeBodyPart mimeBodyPart = new MimeBodyPart();

                File file;

                try {
                    file = FileUtils.getFile(attachment);
                } catch (FileNotFoundException e) {
                    throw new MessagingException("Attachment file not found! ", e);
                }

                DataSource dataSource = new FileDataSource(file);

                mimeBodyPart.setFileName(StringUtils.getFilename(attachment));
                mimeBodyPart.setDataHandler(new DataHandler(dataSource));

                mimeMultipart.addBodyPart(mimeBodyPart, mimeMultipart.getCount());
            }
        }

        if (mailObject.getIncludeFiles() != null) {
            List<String> includeFiles = mailObject.getIncludeFiles();
            for (String filePath : includeFiles) {
                File file;
                MimeBodyPart mimeBodyPart;

                try {
                    file = FileUtils.getFile(filePath);
                    String fileName = StringUtils.getFilename(filePath);
                    mimeBodyPart = new MimeBodyPart();
                    DataHandler dataHandler =
                            new DataHandler(new ByteArrayDataSource(file.toURI().toURL().openStream(),
                                    "application/octet-stream"));
                    mimeBodyPart.setDataHandler(dataHandler);

                    mimeBodyPart.setFileName(fileName);
                    mimeBodyPart.setHeader("Content-ID", fileName);
                } catch (Exception e) {
                    throw new MessagingException("Process include file error! ", e);
                }

                mimeMultipart.addBodyPart(mimeBodyPart, mimeMultipart.getCount());
            }
        }

        if (StringUtils.notBlank(mailObject.getContent())) {
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(mailObject.getContent(),
                    mailObject.getContentType() + "; charset=" + mailObject.getCharset());
            mimeMultipart.addBodyPart(mimeBodyPart, mimeMultipart.getCount());
        }

        if (x509Certificate != null && privateKey != null) {
            message.setContent(mimeMultipart);
            try {
                //  Generate signature attribute
                ASN1EncodableVector signatureAttribute = new ASN1EncodableVector();
                SMIMECapabilityVector capabilityVector = new SMIMECapabilityVector();
                capabilityVector.addCapability(SMIMECapability.aES256_CBC);
                capabilityVector.addCapability(SMIMECapability.dES_CBC);
                capabilityVector.addCapability(SMIMECapability.rC2_CBC, 128);
                signatureAttribute.add(new SMIMECapabilitiesAttribute(capabilityVector));
                signatureAttribute.add(new SMIMEEncryptionKeyPreferenceAttribute(SMIMEUtil.createIssuerAndSerialNumberFor(x509Certificate)));

                List<X509Certificate> certificateList = Collections.singletonList(x509Certificate);
                JcaCertStore certStore = new JcaCertStore(certificateList);

                SignerInfoGenerator signerInfoGenerator = new JcaSimpleSignerInfoGeneratorBuilder()
                        .setProvider("BC")
                        .setSignedAttributeGenerator(new AttributeTable(signatureAttribute))
                        .build("SHA1withRSA", privateKey, x509Certificate);

                SMIMESignedGenerator generator = new SMIMESignedGenerator();
                generator.addSignerInfoGenerator(signerInfoGenerator);
                generator.addCertificates(certStore);

                MimeMultipart signedMimeMultipart = generator.generate(message);
                message.setContent(signedMimeMultipart, signedMimeMultipart.getContentType());
            } catch (CertificateEncodingException | CertificateParsingException | OperatorCreationException |
                     SMIMEException e) {
                throw new MessagingException("Signature mail error! ", e);
            }
        } else {
            message.setContent(mimeMultipart, mimeMultipart.getContentType());
        }

        message.setFrom(new InternetAddress(mailObject.getSendAddress()));

        if (mailObject.getReceiveAddress() == null || mailObject.getReceiveAddress().isEmpty()) {
            throw new MessagingException("Unknown receive address");
        }
        StringBuilder receiveAddress = new StringBuilder();
        mailObject.getReceiveAddress().forEach(address -> receiveAddress.append(",").append(address));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiveAddress.substring(1)));

        if (mailObject.getCcAddress() != null && !mailObject.getCcAddress().isEmpty()) {
            StringBuilder ccAddress = new StringBuilder();
            mailObject.getCcAddress().forEach(address -> ccAddress.append(",").append(address));
            message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(ccAddress.substring(1)));
        }

        if (mailObject.getBccAddress() != null && !mailObject.getBccAddress().isEmpty()) {
            StringBuilder bccAddress = new StringBuilder();
            mailObject.getBccAddress().forEach(address -> bccAddress.append(",").append(address));
            message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(bccAddress.substring(1)));
        }

        if (mailObject.getReplyAddress() != null && !mailObject.getReplyAddress().isEmpty()) {
            StringBuilder replyAddress = new StringBuilder();
            mailObject.getReplyAddress().forEach(address -> replyAddress.append(",").append(address));
            message.setReplyTo(InternetAddress.parse(replyAddress.substring(1)));
        } else {
            message.setReplyTo(InternetAddress.parse(mailObject.getSendAddress()));
        }
        message.setSentDate(mailObject.getSendDate() == null ? new Date() : mailObject.getSendDate());
        return message;
    }

    private static Folder openReadOnlyFolder(final Store store, final String folderName)
            throws MessagingException {
        return openFolder(store, Boolean.TRUE, folderName);
    }

    private static Folder openFolder(final Store store, final boolean readOnly, final String folderName)
            throws MessagingException {
        Folder folder = store.getFolder(folderName);
        folder.open(readOnly ? Folder.READ_ONLY : Folder.READ_WRITE);
        return folder;
    }

    private static void getMailContent(final Part part, final StringBuilder contentBuffer)
            throws MessagingException, IOException {
        String contentType = part.getContentType();
        int nameIndex = contentType.indexOf("name");

        if (contentBuffer == null) {
            throw new IOException();
        }

        if (part.isMimeType(Globals.DEFAULT_CONTENT_TYPE_TEXT) && (nameIndex == -1)) {
            contentBuffer.append(part.getContent().toString());
        } else if (part.isMimeType(Globals.DEFAULT_CONTENT_TYPE_HTML) && (nameIndex == -1)) {
            contentBuffer.append(part.getContent().toString());
        } else if (part.isMimeType(Globals.DEFAULT_CONTENT_TYPE_MULTIPART)) {
            Multipart multipart = (Multipart) part.getContent();
            int count = multipart.getCount();
            for (int i = 0; i < count; i++) {
                getMailContent(multipart.getBodyPart(i), contentBuffer);
            }
        } else if (part.isMimeType(Globals.DEFAULT_CONTENT_TYPE_MESSAGE_RFC822)) {
            getMailContent((Part) part.getContent(), contentBuffer);
        }
    }
}
