/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervousync Studio.
 */
package com.nervousync.commons.beans.mail;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.nervousync.commons.core.Globals;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Jul 31, 2012 7:03:47 PM $
 */
public class MailObject {

	private String uid;
	private String subject;
	private String content;
	private String charset = Globals.DEFAULT_ENCODING;
	private String contentType = Globals.DEFAULT_EMAIL_CONTENT_TYPE_TEXT;
	private String sendAddress;
	private List<String> replyAddress;
	private Date sendDate;
	private boolean junk = false;
	private List<String> recvAddress;
	private List<String> ccAddress;
	private List<String> bccAddress;
	private List<String> attachFiles;
	private List<String> includeFiles;
	private Map<String, String> contentMap;
	
	/**
	 * @return the uid
	 */
	public String getUid() {
		return uid;
	}

	/**
	 * @param uid the uid to set
	 */
	public void setUid(String uid) {
		this.uid = uid;
	}

	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}
	
	/**
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @return the charset
	 */
	public String getCharset() {
		return charset;
	}

	/**
	 * @param charset the charset to set
	 */
	public void setCharset(String charset) {
		this.charset = charset;
	}

	/**
	 * @return the contentType
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * @param contentType the contentType to set
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/**
	 * @return the sendAddress
	 */
	public String getSendAddress() {
		return sendAddress;
	}

	/**
	 * @param sendAddress the sendAddress to set
	 */
	public void setSendAddress(String sendAddress) {
		this.sendAddress = sendAddress;
	}

	/**
	 * @return the replyAddress
	 */
	public List<String> getReplyAddress() {
		return replyAddress;
	}

	/**
	 * @param replyAddress the replyAddress to set
	 */
	public void setReplyAddress(List<String> replyAddress) {
		this.replyAddress = replyAddress;
	}

	/**
	 * @return the sendDate
	 */
	public Date getSendDate() {
		return sendDate;
	}

	/**
	 * @param sendDate the sendDate to set
	 */
	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}

	/**
	 * @return the junk
	 */
	public boolean isJunk() {
		return junk;
	}

	/**
	 * @param junk the junk to set
	 */
	public void setJunk(boolean junk) {
		this.junk = junk;
	}

	/**
	 * @return the recvAddress
	 */
	public List<String> getRecvAddress() {
		return recvAddress;
	}

	/**
	 * @param recvAddress the recvAddress to set
	 */
	public void setRecvAddress(List<String> recvAddress) {
		this.recvAddress = recvAddress;
	}

	/**
	 * @return the ccAddress
	 */
	public List<String> getCcAddress() {
		return ccAddress;
	}

	/**
	 * @param ccAddress the ccAddress to set
	 */
	public void setCcAddress(List<String> ccAddress) {
		this.ccAddress = ccAddress;
	}

	/**
	 * @return the bccAddress
	 */
	public List<String> getBccAddress() {
		return bccAddress;
	}

	/**
	 * @param bccAddress the bccAddress to set
	 */
	public void setBccAddress(List<String> bccAddress) {
		this.bccAddress = bccAddress;
	}

	/**
	 * @return the attachFiles
	 */
	public List<String> getAttachFiles() {
		return attachFiles;
	}

	/**
	 * @param attachFiles the attachFiles to set
	 */
	public void setAttachFiles(List<String> attachFiles) {
		this.attachFiles = attachFiles;
	}

	/**
	 * @return the includeFiles
	 */
	public List<String> getIncludeFiles() {
		return includeFiles;
	}

	/**
	 * @param includeFiles the includeFiles to set
	 */
	public void setIncludeFiles(List<String> includeFiles) {
		this.includeFiles = includeFiles;
	}

	/**
	 * @return the contentMap
	 */
	public Map<String, String> getContentMap() {
		return contentMap;
	}

	/**
	 * @param contentMap the contentMap to set
	 */
	public void setContentMap(Map<String, String> contentMap) {
		this.contentMap = contentMap;
	}
}
