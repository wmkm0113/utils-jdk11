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
package org.nervousync.mail;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.nervousync.commons.core.Globals;

/**
 * Mail define
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: Jul 31, 2012 7:03:47 PM $
 */
public final class MailObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4277408041510934598L;
	
	/**
	 * Identified id
	 */
	private String uid;
	/**
	 * Mail subject
	 */
	private String subject;
	/**
	 * Mail content
	 */
	private String content;
	/**
	 * Charset encoding
	 */
	private String charset = Globals.DEFAULT_ENCODING;
	/**
	 * Mail content type
	 */
	private String contentType = Globals.DEFAULT_EMAIL_CONTENT_TYPE_TEXT;
	/**
	 * Sender e-mail address
	 */
	private String sendAddress;
	/**
	 * Reply e-mail address
	 */
	private List<String> replyAddress;
	/**
	 * Send datetime
	 */
	private Date sendDate;
	/**
	 * Receiver e-mail address list
	 */
	private List<String> receiveAddress;
	/**
	 * CC e-mail address list
	 */
	private List<String> ccAddress;
	/**
	 * BCC e-mail address list
	 */
	private List<String> bccAddress;
	/**
	 * Attaches file list
	 */
	private List<String> attachFiles;
	/**
	 * Mail content include file list
	 */
	private List<String> includeFiles;

	/**
	 * Gets uid.
	 *
	 * @return the uid
	 */
	public String getUid() {
		return uid;
	}

	/**
	 * Sets uid.
	 *
	 * @param uid the uid to set
	 */
	public void setUid(String uid) {
		this.uid = uid;
	}

	/**
	 * Gets subject.
	 *
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * Sets subject.
	 *
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * Gets content.
	 *
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Sets content.
	 *
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * Gets charset.
	 *
	 * @return the charset
	 */
	public String getCharset() {
		return charset;
	}

	/**
	 * Sets charset.
	 *
	 * @param charset the charset to set
	 */
	public void setCharset(String charset) {
		this.charset = charset;
	}

	/**
	 * Gets content type.
	 *
	 * @return the contentType
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * Sets content type.
	 *
	 * @param contentType the contentType to set
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/**
	 * Gets send address.
	 *
	 * @return the sendAddress
	 */
	public String getSendAddress() {
		return sendAddress;
	}

	/**
	 * Sets send address.
	 *
	 * @param sendAddress the sendAddress to set
	 */
	public void setSendAddress(String sendAddress) {
		this.sendAddress = sendAddress;
	}

	/**
	 * Gets reply address.
	 *
	 * @return the replyAddress
	 */
	public List<String> getReplyAddress() {
		return replyAddress;
	}

	/**
	 * Sets reply address.
	 *
	 * @param replyAddress the replyAddress to set
	 */
	public void setReplyAddress(List<String> replyAddress) {
		this.replyAddress = replyAddress;
	}

	/**
	 * Gets send date.
	 *
	 * @return the sendDate
	 */
	public Date getSendDate() {
		return sendDate == null ? null : (Date)sendDate.clone();
	}

	/**
	 * Sets send date.
	 *
	 * @param sendDate the sendDate to set
	 */
	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate == null ? null : (Date)sendDate.clone();
	}

	/**
	 * Gets receive address.
	 *
	 * @return the receiveAddress
	 */
	public List<String> getReceiveAddress() {
		return receiveAddress;
	}

	/**
	 * Sets receive address.
	 *
	 * @param receiveAddress the receiveAddress to set
	 */
	public void setReceiveAddress(List<String> receiveAddress) {
		this.receiveAddress = receiveAddress;
	}

	/**
	 * Gets cc address.
	 *
	 * @return the ccAddress
	 */
	public List<String> getCcAddress() {
		return ccAddress;
	}

	/**
	 * Sets cc address.
	 *
	 * @param ccAddress the ccAddress to set
	 */
	public void setCcAddress(List<String> ccAddress) {
		this.ccAddress = ccAddress;
	}

	/**
	 * Gets bcc address.
	 *
	 * @return the bccAddress
	 */
	public List<String> getBccAddress() {
		return bccAddress;
	}

	/**
	 * Sets bcc address.
	 *
	 * @param bccAddress the bccAddress to set
	 */
	public void setBccAddress(List<String> bccAddress) {
		this.bccAddress = bccAddress;
	}

	/**
	 * Gets attach files.
	 *
	 * @return the attachFiles
	 */
	public List<String> getAttachFiles() {
		return attachFiles;
	}

	/**
	 * Sets attach files.
	 *
	 * @param attachFiles the attachFiles to set
	 */
	public void setAttachFiles(List<String> attachFiles) {
		this.attachFiles = attachFiles;
	}

	/**
	 * Gets include files.
	 *
	 * @return the includeFiles
	 */
	public List<String> getIncludeFiles() {
		return includeFiles;
	}

	/**
	 * Sets include files.
	 *
	 * @param includeFiles the includeFiles to set
	 */
	public void setIncludeFiles(List<String> includeFiles) {
		this.includeFiles = includeFiles;
	}
}
