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
package com.nervousync.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.mail.util.ByteArrayDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPMessage;
import com.sun.mail.pop3.POP3Folder;
import com.nervousync.commons.beans.mail.MailObject;
import com.nervousync.commons.beans.mail.MailServerConfig;
import com.nervousync.commons.beans.mail.authenticator.DefaultAuthenticator;
import com.nervousync.commons.core.Globals;
import com.nervousync.enumerations.mail.ProtocolOption;

/**
 * @author Steven Wee <a
 *         href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Jul 31, 2012 8:54:04 PM $
 */
public final class MailUtils {

	private static transient final Logger LOGGER = LoggerFactory.getLogger(MailUtils.class);
	
	private MailUtils() {
		
	}
	
	public static boolean sendMessage(MailServerConfig mailServerConfig, MailObject mailObject, String userName, String passWord) 
					throws MessagingException, UnsupportedEncodingException, FileNotFoundException {
		MimeMessage message = 
				new MimeMessage(getSession(mailServerConfig.getSendConfigInfo(userName), 
						userName, passWord));
		
		message.setSubject(mailObject.getSubject(), mailObject.getCharset());
		
		MimeMultipart mimeMultipart = new MimeMultipart();
		
		if (mailObject.getAttachFiles() != null) {
			for (String attachment : mailObject.getAttachFiles()) {
				MimeBodyPart mimeBodyPart = new MimeBodyPart();
				
				File file = null;
				
				try {
					file = FileUtils.getFile(attachment);
				} catch (FileNotFoundException e) {
					continue;
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
				File file = null;
				MimeBodyPart mimeBodyPart = null;
				
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
					continue;
				}
				
				mimeMultipart.addBodyPart(mimeBodyPart, mimeMultipart.getCount());
			}
		}
		
		if (mailObject.getContent() != null) {
			String content = mailObject.getContent();
			
			if (mailObject.getContentMap() != null) {
				Map<String, String> argsMap = mailObject.getContentMap();
				Iterator<String> iterator = argsMap.keySet().iterator();
				
				while (iterator.hasNext()) {
					String key = iterator.next();
					content = StringUtils.replace(content, "###" + key + "###", argsMap.get(key));
				}
			}
			
			MimeBodyPart mimeBodyPart = new MimeBodyPart();
			mimeBodyPart.setContent(content, mailObject.getContentType() + "; charset=" + mailObject.getCharset());
			mimeMultipart.addBodyPart(mimeBodyPart, mimeMultipart.getCount());
		}
		
		message.setContent(mimeMultipart);
		if (mailObject.getSendAddress() != null) {
			message.setFrom(new InternetAddress(mailObject.getSendAddress()));
		} else {
			message.setFrom(new InternetAddress(userName));
		}
		
		StringBuffer recvAddress = new StringBuffer();
		
		for (String address : mailObject.getRecvAddress()) {
			recvAddress.append("," + address);
		}
		
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recvAddress.toString().substring(1)));

		if (mailObject.getCcAddress() != null) {
			StringBuffer ccAddress = new StringBuffer();
			
			for (String address : mailObject.getCcAddress()) {
				ccAddress.append("," + address);
			}
			
			message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(ccAddress.toString().substring(1)));
		}

		if (mailObject.getBccAddress() != null) {
			StringBuffer bccAddress = new StringBuffer();
			
			for (String address : mailObject.getBccAddress()) {
				bccAddress.append("," + address);
			}
			
			message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(bccAddress.toString().substring(1)));
		}

		if (mailObject.getReplyAddress() != null) {
			StringBuffer replyAddress = new StringBuffer();
			
			for (String address : mailObject.getReplyAddress()) {
				replyAddress.append("," + address);
			}
			
			message.setReplyTo(InternetAddress.parse(replyAddress.toString().substring(1)));
		} else {
			if (mailObject.getSendAddress() != null) {
				message.setReplyTo(InternetAddress.parse(mailObject.getSendAddress()));
			} else {
				message.setReplyTo(InternetAddress.parse(userName));
			}
		}
		
		message.setSentDate(mailObject.getSendDate());
		
		Transport.send(message);
		
		return true;
	}
	
	public static MailObject getMailInfo(MailServerConfig mailServerConfig, String userName, 
			String passWord, String uid, String saveAttchPath) throws MessagingException {
		Store store = null;
		Folder folder = null;

		try {
			Properties properties = mailServerConfig.getRecvConfigInfo(userName);
			Session session = getSession(properties, userName, passWord);
			
			store = session.getStore(properties.getProperty("mail.store.protocol"));
			
			if (mailServerConfig.getRecvServerConfig().getHostPort() == Globals.DEFAULT_VALUE_INT) {
				store.connect(mailServerConfig.getRecvServerConfig().getHostName(), userName, passWord);
			} else {
				store.connect(mailServerConfig.getRecvServerConfig().getHostName(), 
						mailServerConfig.getRecvServerConfig().getHostPort(), userName, passWord);
			}
			
			folder = openReadOnlyFolder(store);
			
			if (!folder.exists() || !folder.isOpen()) {
				return null;
			}
			
			Message message = null;
			if (mailServerConfig.getRecvServerConfig().getProtocolOption().equals(ProtocolOption.POP3)) {
				Message[] messages = folder.getMessages();
				
				for (Message msg : messages) {
					if (((POP3Folder)folder).getUID(msg).equals(uid)) {
						message = msg;
						break;
					}
				}
			} else if (mailServerConfig.getRecvServerConfig().getProtocolOption().equals(ProtocolOption.IMAP)) {
				message = ((IMAPFolder)folder).getMessageByUID(Long.valueOf(uid).longValue());
			}
			
			if (message != null) {
				return receiveMessage((MimeMessage)message, userName, true, saveAttchPath);
			}
		} catch (Exception e) {
			if (MailUtils.LOGGER.isDebugEnabled()) {
				MailUtils.LOGGER.debug("Receive Message Error! ", e);
			}
		} finally {
			if (folder != null) {
				folder.close(true);
			}
			
			if (store != null) {
				store.close();
			}
		}
		
		return null;
	}

	public static List<MailObject> getMailInfo(MailServerConfig mailServerConfig, String userName, 
			String passWord, List<String> uidList, String saveAttchPath) throws MessagingException {
		List<MailObject> mailList = new ArrayList<MailObject>();
		
		Store store = null;
		Folder folder = null;
		try {
			Properties properties = mailServerConfig.getRecvConfigInfo(userName);
			Session session = getSession(properties, userName, passWord);
			
			store = session.getStore(properties.getProperty("mail.store.protocol"));

			if (mailServerConfig.getRecvServerConfig().getHostPort() == 0) {
				store.connect(mailServerConfig.getRecvServerConfig().getHostName(), userName, passWord);
			} else {
				store.connect(mailServerConfig.getRecvServerConfig().getHostName(), 
						mailServerConfig.getRecvServerConfig().getHostPort(), userName, passWord);
			}
			
			folder = openReadOnlyFolder(store);
			
			if (!folder.exists() || !folder.isOpen()) {
				return mailList;
			}

			List<Message> messageList = new ArrayList<Message>();
			if (mailServerConfig.getRecvServerConfig().getProtocolOption().equals(ProtocolOption.POP3)) {
				Message[] messages = folder.getMessages();
				
				for (Message message : messages) {
					if (uidList.contains(((POP3Folder)folder).getUID(message))) {
						messageList.add(message);
					}
				}
			} else if (mailServerConfig.getRecvServerConfig().getProtocolOption().equals(ProtocolOption.IMAP)) {
				long[] uids = new long[uidList.size()];
				
				for (int i = 0 ; i < uidList.size() ; i++) {
					uids[i] = Long.valueOf(uidList.get(i)).longValue();
				}
				Message[] messages = ((IMAPFolder)folder).getMessagesByUID(uids);
				for (Message message : messages) {
					messageList.add(message);
				}
			}
			
			for (Message message : messageList) {
				MailObject mailObject = receiveMessage((MimeMessage)message, userName, true, saveAttchPath);
				if (mailObject != null) {
					mailList.add(mailObject);
				}
			}
		} catch (Exception e) {
			if (MailUtils.LOGGER.isDebugEnabled()) {
				MailUtils.LOGGER.debug("Receive Message Error! ", e);
			}
		} finally {
			if (folder != null) {
				folder.close(true);
			}
			
			if (store != null) {
				store.close();
			}
		}
		
		return mailList;
	}

	public static List<MailObject> getMailList(MailServerConfig mailServerConfig, String userName, 
			String passWord, String saveAttchPath) throws MessagingException {
		return getMailList(mailServerConfig, userName, passWord, null, saveAttchPath);
	}
	
	public static List<MailObject> getMailList(MailServerConfig mailServerConfig, String userName, 
			String passWord, Date date, String saveAttchPath) throws MessagingException {
		List<MailObject> mailList = new ArrayList<MailObject>();
		
		Store store = null;
		Folder folder = null;
		try {
			Properties properties = mailServerConfig.getRecvConfigInfo(userName);
			Session session = getSession(properties, userName, passWord);
			
			store = session.getStore(properties.getProperty("mail.store.protocol"));

			if (mailServerConfig.getRecvServerConfig().getHostPort() == 0) {
				store.connect(mailServerConfig.getRecvServerConfig().getHostName(), userName, passWord);
			} else {
				store.connect(mailServerConfig.getRecvServerConfig().getHostName(), 
						mailServerConfig.getRecvServerConfig().getHostPort(), userName, passWord);
			}
			
			folder = openReadOnlyFolder(store);
			
			if (!folder.exists() || !folder.isOpen()) {
				return mailList;
			}
			
			Message[] messages = folder.getMessages();
			
			for (Message message : messages) {
				if (date == null || message.getReceivedDate().after(date)) {
					MailObject mailObject = receiveMessage((MimeMessage)message, userName, false, saveAttchPath);
					if (mailObject != null) {
						mailList.add(mailObject);
					}
				}
			}
		} catch (Exception e) {
			if (MailUtils.LOGGER.isDebugEnabled()) {
				MailUtils.LOGGER.debug("Receive Message Error! ", e);
			}
		} finally {
			if (folder != null) {
				folder.close(true);
			}
			
			if (store != null) {
				store.close();
			}
		}
		
		return mailList;
	}

	public static void removeMail(MailServerConfig mailServerConfig, String userName, 
			String passWord, String uid) throws MessagingException {
		List<String> uidList = new ArrayList<String>();
		uidList.add(uid);
		setMessageStatus(mailServerConfig, userName, passWord, uidList, Flag.DELETED, true);
	}
	
	public static void removeMails(MailServerConfig mailServerConfig, String userName, 
			String passWord, List<String> uidList) throws MessagingException {
		setMessageStatus(mailServerConfig, userName, passWord, uidList, Flag.DELETED, true);
	}

	public static void recoverMail(MailServerConfig mailServerConfig, String userName, 
			String passWord, String uid) throws MessagingException {
		List<String> uidList = new ArrayList<String>();
		uidList.add(uid);
		setMessageStatus(mailServerConfig, userName, passWord, uidList, Flag.DELETED, false);
	}
	
	public static void recoverMails(MailServerConfig mailServerConfig, String userName, 
			String passWord, List<String> uidList) throws MessagingException {
		setMessageStatus(mailServerConfig, userName, passWord, uidList, Flag.DELETED, false);
	}

	public static void readMail(MailServerConfig mailServerConfig, String userName, 
			String passWord, String uid) throws MessagingException {
		List<String> uidList = new ArrayList<String>();
		uidList.add(uid);
		setMessageStatus(mailServerConfig, userName, passWord, uidList, Flag.SEEN, true);
	}

	public static void readMails(MailServerConfig mailServerConfig, String userName, 
			String passWord, List<String> uidList) throws MessagingException {
		setMessageStatus(mailServerConfig, userName, passWord, uidList, Flag.SEEN, true);
	}

	public static void unreadMail(MailServerConfig mailServerConfig, String userName, 
			String passWord, String uid) throws MessagingException {
		List<String> uidList = new ArrayList<String>();
		uidList.add(uid);
		setMessageStatus(mailServerConfig, userName, passWord, uidList, Flag.SEEN, false);
	}

	public static void unreadMails(MailServerConfig mailServerConfig, String userName, 
			String passWord, List<String> uidList) throws MessagingException {
		setMessageStatus(mailServerConfig, userName, passWord, uidList, Flag.SEEN, false);
	}

	public static void answerMail(MailServerConfig mailServerConfig, String userName, 
			String passWord, String uid) throws MessagingException {
		List<String> uidList = new ArrayList<String>();
		uidList.add(uid);
		setMessageStatus(mailServerConfig, userName, passWord, uidList, Flag.ANSWERED, true);
	}

	public static void answerMails(MailServerConfig mailServerConfig, String userName, 
			String passWord, List<String> uidList) throws MessagingException {
		setMessageStatus(mailServerConfig, userName, passWord, uidList, Flag.ANSWERED, true);
	}

	public static void flagMail(MailServerConfig mailServerConfig, String userName, 
			String passWord, String uid) throws MessagingException {
		List<String> uidList = new ArrayList<String>();
		uidList.add(uid);
		setMessageStatus(mailServerConfig, userName, passWord, uidList, Flag.FLAGGED, true);
	}

	public static void flagMails(MailServerConfig mailServerConfig, String userName, 
			String passWord, List<String> uidList) throws MessagingException {
		setMessageStatus(mailServerConfig, userName, passWord, uidList, Flag.FLAGGED, true);
	}

	public static void unflagMail(MailServerConfig mailServerConfig, String userName, 
			String passWord, String uid) throws MessagingException {
		List<String> uidList = new ArrayList<String>();
		uidList.add(uid);
		setMessageStatus(mailServerConfig, userName, passWord, uidList, Flag.FLAGGED, false);
	}
	
	public static void unflagMails(MailServerConfig mailServerConfig, String userName, 
			String passWord, List<String> uidList) throws MessagingException {
		setMessageStatus(mailServerConfig, userName, passWord, uidList, Flag.FLAGGED, false);
	}
	
	private static MailObject receiveMessage(MimeMessage mimeMessage, String recvAddress, 
			boolean detail, String saveAttchPath) throws MessagingException, IOException {
		MailObject mailObject = new MailObject();

		InternetAddress[] receiveAddress = null;
		
		if (mimeMessage instanceof IMAPMessage) {
			receiveAddress = (InternetAddress[]) ((IMAPMessage)mimeMessage).getRecipients(IMAPMessage.RecipientType.TO);
		} else {
			receiveAddress = (InternetAddress[]) mimeMessage.getRecipients(Message.RecipientType.TO);
		}

		List<String> receiveList = new ArrayList<String>();

		for (InternetAddress address : receiveAddress) {
			receiveList.add(address.getAddress());
		}
		
		if (!receiveList.contains(recvAddress)) {
			return null;
		}
		
		mailObject.setRecvAddress(receiveList);
		
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
				List<String> ccList = new ArrayList<String>();
		
				for (InternetAddress address : ccAddress) {
					ccList.add(address.getAddress());
				}
		
				mailObject.setCcAddress(ccList);
			}
	
			//	Read mail bcc address
			InternetAddress[] bccAddress = (InternetAddress[]) mimeMessage.getRecipients(Message.RecipientType.BCC);
	
			if (bccAddress != null) {
				List<String> bccList = new ArrayList<String>();
				
				for (InternetAddress address : bccAddress) {
					bccList.add(address.getAddress());
				}
		
				mailObject.setBccAddress(bccList);
			}

			//	Read mail content message
			StringBuffer contentBuffer = new StringBuffer();
			getMailContent(mimeMessage, contentBuffer);
			mailObject.setContent(contentBuffer.toString());
			
			List<String> attachFiles = new ArrayList<String>();
			getMailAttachment(mimeMessage, saveAttchPath, attachFiles);
			mailObject.setAttachFiles(attachFiles);
		}
		
		return mailObject;
	}
	
	private static void setMessageStatus(MailServerConfig mailServerConfig, String userName, 
			String passWord, List<String> uidList, Flag flag, boolean status) 
					throws MessagingException {
		Store store = null;
		Folder folder = null;
		try {
			Session session = getSession(mailServerConfig.getRecvConfigInfo(userName), userName, passWord);
			
			switch (mailServerConfig.getRecvServerConfig().getProtocolOption()) {
			case IMAP:
				store = session.getStore("imap");
				break;
			case POP3:
				store = session.getStore("pop3");
				break;
			default:
				throw new NoSuchProviderException("Protocol Error");
			}

			if (mailServerConfig.getRecvServerConfig().getHostPort() == 0) {
				store.connect(mailServerConfig.getRecvServerConfig().getHostName(), userName, passWord);
			} else {
				store.connect(mailServerConfig.getRecvServerConfig().getHostName(), 
						mailServerConfig.getRecvServerConfig().getHostPort(), userName, passWord);
			}
			
			folder = openFolder(store, false);
			
			if (!folder.exists() || !folder.isOpen()) {
				return;
			}
			
			List<Message> messageList = new ArrayList<Message>();
			if (mailServerConfig.getRecvServerConfig().getProtocolOption().equals(ProtocolOption.POP3)) {
				Message[] messages = folder.getMessages();
				
				for (Message message : messages) {
					if (uidList.contains(((POP3Folder)folder).getUID(message))) {
						messageList.add(message);
					}
				}
			} else if (mailServerConfig.getRecvServerConfig().getProtocolOption().equals(ProtocolOption.IMAP)) {
				long[] uids = new long[uidList.size()];
				
				for (int i = 0 ; i < uidList.size() ; i++) {
					uids[i] = Long.valueOf(uidList.get(i)).longValue();
				}
				Message[] messages = ((IMAPFolder)folder).getMessagesByUID(uids);
				for (Message message : messages) {
					messageList.add(message);
				}
			}
			
			for (Message message : messageList) {
				message.setFlag(flag, status);
			}
		} catch (Exception e) {
			
		} finally {
			if (folder != null) {
				folder.close(true);
			}
			
			if (store != null) {
				store.close();
			}
		}
	}
	
	private static void getMailContent(Part part, StringBuffer contentBuffer) throws MessagingException, IOException {
		String contentType = part.getContentType();
		int nameIndex = contentType.indexOf("name");

		boolean conname = false;
		if (nameIndex != -1) {
			conname = true;
		}
		
		if (contentBuffer == null) {
			throw new IOException();
		}

		if (part.isMimeType(Globals.DEFAULT_EMAIL_CONTENT_TYPE_TEXT) && !conname) {
			contentBuffer.append(part.getContent().toString());
		} else {
			if (part.isMimeType(Globals.DEFAULT_EMAIL_CONTENT_TYPE_HTML) && !conname) {
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
	
	private static void getMailAttachment(Part part, String saveAttchPath, List<String> saveFiles) throws MessagingException, IOException {
		if (saveFiles == null) {
			saveFiles = new ArrayList<String>();
		}
		if (part.isMimeType(Globals.DEFAULT_EMAIL_CONTENT_TYPE_MULTIPART)) {
			Multipart multipart = (Multipart) part.getContent();
			int count = multipart.getCount();
			for (int i = 0; i < count; i++) {
				Part bodyPart = multipart.getBodyPart(i);
				if (bodyPart.getFileName() != null) {
					String dispostion = bodyPart.getDisposition();
					if (dispostion != null && (dispostion.equals(Part.ATTACHMENT) || dispostion.equals(Part.INLINE))) {
						boolean saveFile = FileUtils.saveFile(bodyPart.getInputStream(), 
								saveAttchPath + Globals.DEFAULT_PAGE_SEPARATOR + MimeUtility.decodeText(bodyPart.getFileName()));
						if (saveFile) {
							saveFiles.add(saveAttchPath + Globals.DEFAULT_PAGE_SEPARATOR + MimeUtility.decodeText(bodyPart.getFileName()));
						}
					} else if (bodyPart.isMimeType(Globals.DEFAULT_EMAIL_CONTENT_TYPE_MULTIPART)) {
						getMailAttachment(bodyPart, saveAttchPath, saveFiles);
					}
				}
			}
		}
	}
	
	private static Session getSession(Properties properties, String userName, String passWord) {
		return Session.getDefaultInstance(properties, new DefaultAuthenticator(userName, passWord));
	}
	
	private static Folder openReadOnlyFolder(Store store) 
			throws NoSuchProviderException, MessagingException {
		return openFolder(store, true);
	}
	
	private static Folder openFolder(Store store, boolean readOnly) 
			throws NoSuchProviderException,
			MessagingException {
		Folder folder = store.getFolder(Globals.DEFAULT_EMAIL_FOLDER_INBOX);
		
		if (readOnly) {
			folder.open(Folder.READ_ONLY);
		} else {
			folder.open(Folder.READ_WRITE);
		}
		
		return folder;
	}
}
