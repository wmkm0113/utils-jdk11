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
package com.nervousync.commons.beans.mail.protocol.impl;

import com.nervousync.commons.beans.mail.protocol.BaseProtocol;
import com.nervousync.enumerations.mail.ProtocolOption;

/**
 * Implement POP3 protocol
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Jul 31, 2012 8:08:48 PM $
 */
public final class POP3Protocol extends BaseProtocol {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8698112033277399242L;

	public POP3Protocol() {
		super(ProtocolOption.POP3);
		this.hostParam = "mail.pop3.host";
		this.portParam = "mail.pop3.port";
		this.connectionTimeoutParam = "mail.pop3.connectiontimeout";
		this.timeoutParam = "mail.pop3.timeout";
	}
}
