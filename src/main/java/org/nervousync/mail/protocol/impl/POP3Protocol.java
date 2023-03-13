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
import org.eclipse.angus.mail.pop3.POP3Folder;
import org.nervousync.commons.core.Globals;
import org.nervousync.commons.proxy.ProxyConfig;
import org.nervousync.mail.operator.ReceiveOperator;
import org.nervousync.mail.protocol.BaseProtocol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Implement POP3 protocol
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: Jul 31, 2012 8:08:48 PM $
 */
public final class POP3Protocol extends BaseProtocol implements ReceiveOperator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8698112033277399242L;

	/**
	 * Instantiates a new Pop 3 protocol.
	 */
	public POP3Protocol(final String secureName, final ProxyConfig proxyConfig) {
		super(secureName, proxyConfig);
		this.hostParam = "mail.pop3.host";
		this.portParam = "mail.pop3.port";
		this.connectionTimeoutParam = "mail.pop3.connectiontimeout";
		this.timeoutParam = "mail.pop3.timeout";
	}

	@Override
	public String readUID(Folder folder, Message message) throws MessagingException {
		if (folder instanceof POP3Folder) {
			return ((POP3Folder) folder).getUID(message);
		}
		return Globals.DEFAULT_VALUE_STRING;
	}

	@Override
	public Message readMessage(Folder folder, String uid) throws MessagingException {
		if (folder instanceof POP3Folder) {
			for (Message msg : folder.getMessages()) {
				if (((POP3Folder) folder).getUID(msg).equals(uid)) {
					return msg;
				}
			}
		}
		return null;
	}

	@Override
	public List<Message> readMessages(Folder folder, String... uidArrays) throws MessagingException {
		List<Message> messageList = new ArrayList<>();
		if (folder instanceof POP3Folder) {
			List<String> uidList = Arrays.asList(uidArrays);
			for (Message message : folder.getMessages()) {
				if (uidList.contains(((POP3Folder)folder).getUID(message))) {
					messageList.add(message);
				}
			}
		}
		return messageList;
	}
}
