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
import java.util.*;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPMessage;
import com.sun.mail.pop3.POP3Folder;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import jakarta.mail.util.ByteArrayDataSource;
import org.nervousync.commons.beans.mail.MailObject;
import org.nervousync.commons.beans.mail.authenticator.DefaultAuthenticator;
import org.nervousync.commons.beans.mail.config.MailConfig;
import org.nervousync.commons.beans.mail.operator.MailReceiver;
import org.nervousync.commons.beans.mail.operator.MailSender;
import org.nervousync.commons.beans.mail.config.ServerConfig;
import org.nervousync.commons.beans.mail.protocol.impl.IMAPProtocol;
import org.nervousync.commons.beans.mail.protocol.impl.POP3Protocol;
import org.nervousync.commons.beans.mail.protocol.impl.SMTPProtocol;
import org.nervousync.commons.core.Globals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The type Mail utils.
 *
 * @author Steven Wee     <a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Jul 31, 2012 8:54:04 PM $
 */
public final class MailUtils {

	private MailUtils() {
	}

	/**
	 * Operator optional.
	 *
	 * @param mailConfig the mail config
	 * @return the optional
	 */
	public static Optional<MailOperator> operator(MailConfig mailConfig) {
		return mailConfig == null ? Optional.empty() : Optional.of(new MailOperator(mailConfig));
	}

	/**
	 * The type Mail operator.
	 */
	public static final class MailOperator {

		private final Logger logger = LoggerFactory.getLogger(this.getClass());

		private final MailConfig mailConfig;
		private final MailSender mailSender = new SMTPProtocol();
		private final MailReceiver mailReceiver;

		private MailOperator(MailConfig mailConfig) {
			this.mailConfig = mailConfig;
			ServerConfig receiveConfig = this.mailConfig.getRecvConfig();
			if (receiveConfig != null && StringUtils.notBlank(receiveConfig.getProtocolOption())) {
				switch (receiveConfig.getProtocolOption().toUpperCase()) {
					case "IMAP":
						this.mailReceiver = new IMAPProtocol();
						break;
					case "POP3":
						this.mailReceiver = new POP3Protocol();
						break;
					default:
						this.mailReceiver = null;
						break;
				}
			} else {
				this.mailReceiver = null;
			}
		}

		/**
		 * Send mail boolean.
		 *
		 * @param mailObject the mail object
		 * @return the boolean
		 */
		public final boolean sendMail(MailObject mailObject) {
			try {
				Properties properties = this.mailSender.readConfig(this.mailConfig.getSendConfig(),
						this.mailConfig.getConnectionTimeout(), this.mailConfig.getProcessTimeout(),
						this.mailConfig.getSendUserName());
				Session session = Session.getInstance(properties,
						new DefaultAuthenticator(this.mailConfig.getSendUserName(), this.mailConfig.getSendPassWord()));
				session.setDebug(true);
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
							return Globals.DEFAULT_VALUE_BOOLEAN;
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
							return Globals.DEFAULT_VALUE_BOOLEAN;
						}

						mimeMultipart.addBodyPart(mimeBodyPart, mimeMultipart.getCount());
					}
				}

				if (mailObject.getContent() != null) {
					String content = mailObject.getContent();

					if (mailObject.getContentMap() != null) {
						Map<String, String> argsMap = mailObject.getContentMap();

						for (Map.Entry<String, String> entry : argsMap.entrySet()) {
							content = StringUtils.replace(content, "###" + entry.getKey() + "###", entry.getValue());
						}
					}

					MimeBodyPart mimeBodyPart = new MimeBodyPart();
					mimeBodyPart.setContent(content, mailObject.getContentType() + "; charset=" + mailObject.getCharset());
					mimeMultipart.addBodyPart(mimeBodyPart, mimeMultipart.getCount());
				}

				message.setContent(mimeMultipart);
				if (StringUtils.notBlank(mailObject.getSendAddress())) {
					message.setFrom(new InternetAddress(mailObject.getSendAddress()));
				} else {
					message.setFrom(new InternetAddress(this.mailConfig.getSendUserName()));
				}

				StringBuilder receiveAddress = new StringBuilder();

				for (String address : mailObject.getReceiveAddress()) {
					receiveAddress.append(",").append(address);
				}

				message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiveAddress.substring(1)));

				if (mailObject.getCcAddress() != null) {
					StringBuilder ccAddress = new StringBuilder();

					for (String address : mailObject.getCcAddress()) {
						ccAddress.append(",").append(address);
					}

					message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(ccAddress.substring(1)));
				}

				if (mailObject.getBccAddress() != null) {
					StringBuilder bccAddress = new StringBuilder();

					for (String address : mailObject.getBccAddress()) {
						bccAddress.append(",").append(address);
					}

					message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(bccAddress.substring(1)));
				}

				if (mailObject.getReplyAddress() != null) {
					StringBuilder replyAddress = new StringBuilder();

					for (String address : mailObject.getReplyAddress()) {
						replyAddress.append(",").append(address);
					}

					message.setReplyTo(InternetAddress.parse(replyAddress.substring(1)));
				} else {
					if (mailObject.getSendAddress() != null) {
						message.setReplyTo(InternetAddress.parse(mailObject.getSendAddress()));
					} else {
						message.setReplyTo(InternetAddress.parse(this.mailConfig.getSendUserName()));
					}
				}

				message.setSentDate(mailObject.getSendDate());

				Transport.send(message);

				return Boolean.TRUE;
			} catch (MessagingException e) {
				this.logger.error("Send mail failed!");
				e.printStackTrace();
				if (this.logger.isDebugEnabled()) {
					this.logger.debug("Stack message: ", e);
				}
				return Boolean.FALSE;
			}
		}

		/**
		 * Mail count int.
		 *
		 * @return the int
		 */
		public final int mailCount() {
			return this.mailCount(Globals.DEFAULT_EMAIL_FOLDER_INBOX);
		}

		/**
		 * Mail count int.
		 *
		 * @param folderName the folder name
		 * @return the int
		 */
		public final int mailCount(String folderName) {
			if (this.mailReceiver == null) {
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
		 * Mail list list.
		 *
		 * @return the list
		 */
		public final List<String> mailList() {
			return this.mailList(Globals.DEFAULT_EMAIL_FOLDER_INBOX);
		}

		/**
		 * Mail list list.
		 *
		 * @param folderName the folder name
		 * @return the list
		 */
		public final List<String> mailList(String folderName) {
			return mailList(folderName, Globals.DEFAULT_VALUE_INT, Globals.DEFAULT_VALUE_INT);
		}

		/**
		 * Mail list list.
		 *
		 * @param folderName the folder name
		 * @param begin      the begin
		 * @param end        the end
		 * @return the list
		 */
		public final List<String> mailList(String folderName, int begin, int end) {
			if (this.mailReceiver == null || end < begin) {
				return Collections.emptyList();
			}

			try (Store store = connect(); Folder folder = openReadOnlyFolder(store, folderName)) {
				if (!folder.exists() || !folder.isOpen()) {
					return Collections.emptyList();
				}

				if (begin < 0) {
					begin = 0;
				}
				if (end < 0) {
					end = folder.getMessageCount();
				}

				List<String> mailList = new ArrayList<>();
				for (Message message : folder.getMessages(begin, end)) {
					mailList.add(this.mailReceiver.readUID(folder, message));
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
		public final Optional<MailObject> readMail(String folderName, String uid) {
			if (this.mailReceiver == null) {
				return Optional.empty();
			}
			try (Store store = connect(); Folder folder = openReadOnlyFolder(store, folderName)) {
				if (!folder.exists() || !folder.isOpen()) {
					return Optional.empty();
				}

				Message message = this.mailReceiver.readMessage(folder, uid);
				if (message != null) {
					return receiveMessage((MimeMessage) message, Boolean.TRUE);
				}
			} catch (Exception e) {
				if (this.logger.isDebugEnabled()) {
					this.logger.debug("Receive Message Error! ", e);
				}
			}

			return Optional.empty();
		}

		/**
		 * Read mail list list.
		 *
		 * @param folderName the folder name
		 * @param uidArrays  the uid arrays
		 * @return the list
		 */
		public final List<MailObject> readMailList(String folderName, String... uidArrays) {
			List<MailObject> mailList = new ArrayList<>();
			if (this.mailReceiver == null) {
				return mailList;
			}

			try (Store store = connect(); Folder folder = openReadOnlyFolder(store, folderName)) {
				if (!folder.exists() || !folder.isOpen()) {
					return mailList;
				}
				this.mailReceiver.readMessages(folder, uidArrays)
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
		public final boolean readMails(String folderName, String... uidArrays) {
			return this.flagMailsStatus(Flags.Flag.SEEN, Boolean.TRUE, folderName, uidArrays);
		}

		/**
		 * Set mails status as unread by uid list
		 *
		 * @param folderName the folder name
		 * @param uidArrays  the uid arrays
		 * @return the boolean
		 */
		public final boolean unreadMails(String folderName, String... uidArrays) {
			return this.flagMailsStatus(Flags.Flag.SEEN, Boolean.FALSE, folderName, uidArrays);
		}

		/**
		 * Set mails status as answered by uid list
		 *
		 * @param folderName the folder name
		 * @param uidArrays  the uid arrays
		 * @return the boolean
		 */
		public final boolean answerMails(String folderName, String... uidArrays) {
			return this.flagMailsStatus(Flags.Flag.ANSWERED, Boolean.TRUE, folderName, uidArrays);
		}

		/**
		 * Set mails status as flagged by uid list
		 *
		 * @param folderName the folder name
		 * @param uidArrays  the uid arrays
		 * @return the boolean
		 */
		public final boolean flagMails(String folderName, String... uidArrays) {
			return this.flagMailsStatus(Flags.Flag.FLAGGED, Boolean.TRUE, folderName, uidArrays);
		}

		/**
		 * Set mails status as not flagged by uid list
		 *
		 * @param folderName the folder name
		 * @param uidArrays  the uid arrays
		 * @return the boolean
		 */
		public final boolean unflagMails(String folderName, String... uidArrays) {
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
		private boolean flagMailsStatus(Flags.Flag flag, boolean status, String folderName, String... uidArrays) {
			if (this.mailReceiver == null) {
				return Globals.DEFAULT_VALUE_BOOLEAN;
			}
			try (Store store = connect(); Folder folder = openFolder(store, Boolean.FALSE, folderName)) {

				if (!folder.exists() || !folder.isOpen()) {
					return Boolean.FALSE;
				}

				List<Message> messageList = this.mailReceiver.readMessages(folder, uidArrays);

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
		 * @return                      Store instance
		 * @throws MessagingException   connect failed
		 */
		private Store connect() throws MessagingException {
			Properties properties = this.mailReceiver.readConfig(this.mailConfig.getRecvConfig(),
					this.mailConfig.getConnectionTimeout(), this.mailConfig.getProcessTimeout(),
					this.mailConfig.getRecvUserName());
			Session session = Session.getDefaultInstance(properties,
					new DefaultAuthenticator(this.mailConfig.getRecvUserName(), this.mailConfig.getRecvPassWord()));

			Store store = session.getStore(properties.getProperty("mail.store.protocol"));

			if (this.mailConfig.getRecvConfig().getHostPort() == 0) {
				store.connect(this.mailConfig.getRecvConfig().getHostName(),
						this.mailConfig.getRecvUserName(), this.mailConfig.getRecvPassWord());
			} else {
				store.connect(this.mailConfig.getRecvConfig().getHostName(),
						this.mailConfig.getRecvConfig().getHostPort(),
						this.mailConfig.getRecvUserName(), this.mailConfig.getRecvPassWord());
			}
			return store;
		}

		private static Folder openReadOnlyFolder(Store store, String folderName)
				throws MessagingException {
			return openFolder(store, true, folderName);
		}

		private static Folder openFolder(Store store, boolean readOnly, String folderName)
				throws MessagingException {
			Folder folder = store.getFolder(folderName);

			if (readOnly) {
				folder.open(Folder.READ_ONLY);
			} else {
				folder.open(Folder.READ_WRITE);
			}

			return folder;
		}

		/**
		 * Read mail info
		 * @param mimeMessage           MIME message instance
		 * @param detail                read detail
		 * @return                      Mail object instance
		 */
		private Optional<MailObject> receiveMessage(MimeMessage mimeMessage, boolean detail) {
			try {
				MailObject mailObject = new MailObject();
				InternetAddress[] internetAddresses =
						(InternetAddress[]) mimeMessage.getRecipients(IMAPMessage.RecipientType.TO);
				List<String> receiveList = new ArrayList<>();
				Arrays.asList(mimeMessage.getRecipients(IMAPMessage.RecipientType.TO)).forEach(address -> {
					if (address instanceof InternetAddress) {
						receiveList.add(((InternetAddress)address).getAddress());
					}
				});

				if (!receiveList.contains(this.mailConfig.getRecvUserName())) {
					return Optional.empty();
				}

				mailObject.setReceiveAddress(receiveList);

				Folder folder = mimeMessage.getFolder();

				if (folder instanceof POP3Folder) {
					mailObject.setUid(((POP3Folder)folder).getUID(mimeMessage));
				} else if (folder instanceof IMAPFolder) {
					mailObject.setUid(Long.valueOf(((IMAPFolder)folder).getUID(mimeMessage)).toString());
				}
				String subject = mimeMessage.getSubject();

				if (subject != null) {
					mailObject.setSubject(MimeUtility.decodeText(mimeMessage.getSubject()));
				} else {
					mailObject.setSubject("");
				}
				mailObject.setSendDate(mimeMessage.getSentDate());
				mailObject.setSendAddress(MimeUtility.decodeText(InternetAddress.toString(mimeMessage.getFrom())));

				if (detail) {
					//	Read mail cc address
					InternetAddress[] ccAddress = (InternetAddress[]) mimeMessage.getRecipients(Message.RecipientType.CC);

					if (ccAddress != null) {
						List<String> ccList = new ArrayList<>();

						for (InternetAddress address : ccAddress) {
							ccList.add(address.getAddress());
						}

						mailObject.setCcAddress(ccList);
					}

					//	Read mail bcc address
					InternetAddress[] bccAddress = (InternetAddress[]) mimeMessage.getRecipients(Message.RecipientType.BCC);

					if (bccAddress != null) {
						List<String> bccList = new ArrayList<>();

						for (InternetAddress address : bccAddress) {
							bccList.add(address.getAddress());
						}

						mailObject.setBccAddress(bccList);
					}

					//	Read mail content message
					StringBuilder contentBuffer = new StringBuilder();
					getMailContent(mimeMessage, contentBuffer);
					mailObject.setContent(contentBuffer.toString());

					List<String> attachFiles = new ArrayList<>();
					getMailAttachment(mimeMessage, attachFiles);
					mailObject.setAttachFiles(attachFiles);
				}

				return Optional.of(mailObject);
			} catch (MessagingException | IOException e) {
				if (this.logger.isDebugEnabled()) {
					this.logger.debug("Receive message error! ", e);
				}
				return Optional.empty();
			}
		}

		private static void getMailContent(Part part, StringBuilder contentBuffer) throws MessagingException, IOException {
			String contentType = part.getContentType();
			int nameIndex = contentType.indexOf("name");

			if (contentBuffer == null) {
				throw new IOException();
			}

			if (part.isMimeType(Globals.DEFAULT_EMAIL_CONTENT_TYPE_TEXT) && (nameIndex == -1)) {
				contentBuffer.append(part.getContent().toString());
			} else {
				if (part.isMimeType(Globals.DEFAULT_EMAIL_CONTENT_TYPE_HTML) && (nameIndex == -1)) {
					contentBuffer.append(part.getContent().toString());
				} else {
					if (part.isMimeType(Globals.DEFAULT_EMAIL_CONTENT_TYPE_MULTIPART)) {
						Multipart multipart = (Multipart) part.getContent();
						int count = multipart.getCount();
						for (int i = 0; i < count; i++) {
							getMailContent(multipart.getBodyPart(i), contentBuffer);
						}
					} else {
						if (part.isMimeType(Globals.DEFAULT_EMAIL_CONTENT_TYPE_MESSAGE_RFC822)) {
							getMailContent((Part) part.getContent(), contentBuffer);
						}
					}
				}
			}
		}

		private void getMailAttachment(Part part, List<String> saveFiles) throws MessagingException, IOException {
			if (saveFiles == null) {
				saveFiles = new ArrayList<>();
			}
			String saveAttachPath = this.mailConfig.getStoragePath();
			if (StringUtils.isEmpty(saveAttachPath)) {
				throw new IOException("Save attach file path error! ");
			}
			if (part.isMimeType(Globals.DEFAULT_EMAIL_CONTENT_TYPE_MULTIPART)) {
				Multipart multipart = (Multipart) part.getContent();
				int count = multipart.getCount();
				for (int i = 0; i < count; i++) {
					Part bodyPart = multipart.getBodyPart(i);
					if (bodyPart.getFileName() != null) {
						String disposition = bodyPart.getDisposition();
						if (disposition != null && (disposition.equals(Part.ATTACHMENT) || disposition.equals(Part.INLINE))) {
							boolean saveFile = FileUtils.saveFile(bodyPart.getInputStream(),
									saveAttachPath + Globals.DEFAULT_PAGE_SEPARATOR + MimeUtility.decodeText(bodyPart.getFileName()));
							if (saveFile) {
								saveFiles.add(saveAttachPath + Globals.DEFAULT_PAGE_SEPARATOR + MimeUtility.decodeText(bodyPart.getFileName()));
							}
						} else if (bodyPart.isMimeType(Globals.DEFAULT_EMAIL_CONTENT_TYPE_MULTIPART)) {
							getMailAttachment(bodyPart, saveFiles);
						}
					}
				}
			}
		}
	}
}
