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
package org.nervousync.mail.authenticator;

import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import org.nervousync.commons.core.Globals;
import org.nervousync.utils.StringUtils;

/**
 * Username and password authenticator for SMTP/POP3/IMAP server
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Jul 31, 2012 8:56:49 PM $
 */
public final class DefaultAuthenticator extends Authenticator {

	private final PasswordAuthentication passwordAuthentication;
	
	/**
	 * Initialize authenticator
	 * @param username		Authenticate username
	 * @param password		Authenticate password
	 */
	public DefaultAuthenticator(String username, String password) {
		this.passwordAuthentication =
				new PasswordAuthentication(StringUtils.notBlank(username) ? username : Globals.DEFAULT_VALUE_STRING,
						StringUtils.notBlank(password) ? password : Globals.DEFAULT_VALUE_STRING);
	}
	
	@Override
	protected PasswordAuthentication getPasswordAuthentication(){
		return this.passwordAuthentication;
	}
}
