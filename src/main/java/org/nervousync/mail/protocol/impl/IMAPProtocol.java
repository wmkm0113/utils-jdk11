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
package org.nervousync.mail.protocol.impl;

import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import org.eclipse.angus.mail.imap.IMAPFolder;
import org.nervousync.commons.core.Globals;
import org.nervousync.commons.proxy.ProxyConfig;
import org.nervousync.mail.operator.ReceiveOperator;
import org.nervousync.mail.operator.SendOperator;
import org.nervousync.mail.protocol.BaseProtocol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implement IMAP protocol
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: Jul 31, 2012 7:58:05 PM $
 */
public final class IMAPProtocol extends BaseProtocol implements SendOperator, ReceiveOperator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8284024432628098776L;

	/**
	 * Instantiates a new Imap protocol.
	 */
	public IMAPProtocol(final String secureName, final ProxyConfig proxyConfig) {
		super(secureName, proxyConfig);
		this.hostParam = "mail.imap.host";
		this.portParam = "mail.imap.port";
		this.connectionTimeoutParam = "mail.imap.connectiontimeout";
		this.timeoutParam = "mail.imap.timeout";
	}

	@Override
	public String readUID(Folder folder, Message message) throws MessagingException {
		if (folder instanceof IMAPFolder) {
			return Long.toString(((IMAPFolder) folder).getUID(message));
		}
		return Globals.DEFAULT_VALUE_STRING;
	}

	@Override
	public Message readMessage(Folder folder, String uid) throws MessagingException {
		if (folder instanceof IMAPFolder) {
			return ((IMAPFolder) folder).getMessageByUID(Long.parseLong(uid));
		}
		return null;
	}

	@Override
	public List<Message> readMessages(Folder folder, String... uidArrays) throws MessagingException {
		List<Message> messageList = new ArrayList<>();
		if (folder instanceof IMAPFolder) {
			long[] uidList = new long[uidArrays.length];
			for (int i = 0 ; i < uidArrays.length ; i++) {
				uidList[i] = Long.parseLong(uidArrays[i]);
			}
			Collections.addAll(messageList, ((IMAPFolder) folder).getMessagesByUID(uidList));
		}
		return messageList;
	}
}
