/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
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
