/*
 * Copyright © 2003 - 2012 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervousync Studio.
 */
package com.nervousync.commons.beans.mail.authenticator;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Jul 31, 2012 8:56:49 PM $
 */
public class DefaultAuthenticator extends Authenticator {

	private PasswordAuthentication passwordAuthentication;
	
	public DefaultAuthenticator(String username, String password){
		passwordAuthentication = new PasswordAuthentication(username, password);
	}
	
	protected PasswordAuthentication getPasswordAuthentication(){
		return passwordAuthentication;
	}
}
