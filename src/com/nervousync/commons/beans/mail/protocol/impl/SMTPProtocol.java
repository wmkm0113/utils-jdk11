/*
 * Copyright © 2003 - 2012 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervousync Studio.
 */
package com.nervousync.commons.beans.mail.protocol.impl;

import com.nervousync.commons.beans.mail.protocol.BaseProtocol;
import com.nervousync.commons.beans.mail.protocol.ProtocolOption;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Jul 31, 2012 8:10:30 PM $
 */
public final class SMTPProtocol extends BaseProtocol {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5226459745420272131L;

	public SMTPProtocol() {
		super(ProtocolOption.SMTP);
		this.hostParam = "mail.smtp.host";
		this.portParam = "mail.smtp.port";
		this.connectionTimeoutParam = "mail.smtp.connectiontimeout";
		this.timeoutParam = "mail.smtp.timeout";
	}
}
