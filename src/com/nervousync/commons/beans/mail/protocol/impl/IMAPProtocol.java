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
 * @version $Revision: 1.0 $ $Date: Jul 31, 2012 7:58:05 PM $
 */
public final class IMAPProtocol extends BaseProtocol {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8284024432628098776L;
	
	public IMAPProtocol() {
		super(ProtocolOption.IMAP);
		this.hostParam = "mail.imap.host";
		this.portParam = "mail.imap.port";
		this.connectionTimeoutParam = "mail.imap.connectiontimeout";
		this.timeoutParam = "mail.imap.timeout";
	}
}
