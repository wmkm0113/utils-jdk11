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
package org.nervousync.commons.beans.mail.protocol.impl;

import com.sun.mail.imap.IMAPFolder;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import org.nervousync.commons.beans.mail.operator.MailReceiver;
import org.nervousync.commons.beans.mail.operator.MailSender;
import org.nervousync.commons.beans.mail.protocol.BaseProtocol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implement IMAP protocol
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Jul 31, 2012 7:58:05 PM $
 */
public final class IMAPProtocol extends BaseProtocol implements MailSender, MailReceiver {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8284024432628098776L;
	
	public IMAPProtocol() {
		this.hostParam = "mail.imap.host";
		this.portParam = "mail.imap.port";
		this.connectionTimeoutParam = "mail.imap.connectiontimeout";
		this.timeoutParam = "mail.imap.timeout";
	}

	@Override
	public String readUID(Folder folder, Message message) throws MessagingException {
		return Long.toString(((IMAPFolder) folder).getUID(message));
	}

	@Override
	public Message readMessage(Folder folder, String uid) throws MessagingException {
		return ((IMAPFolder) folder).getMessageByUID(Long.parseLong(uid));
	}

	@Override
	public List<Message> readMessages(Folder folder, String... uidArrays) throws MessagingException {
		List<Message> messageList = new ArrayList<>();
		long[] uidList = new long[uidArrays.length];

		for (int i = 0 ; i < uidArrays.length ; i++) {
			uidList[i] = Long.parseLong(uidArrays[i]);
		}
		Collections.addAll(messageList, ((IMAPFolder) folder).getMessagesByUID(uidList));
		return messageList;
	}
}
