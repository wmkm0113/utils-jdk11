/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervous Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervous Studio.
 */
package com.nervousync.commons.beans.snmp;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Oct 27, 2017 2:41:56 PM $
 */
public class LocalHost extends TargetHost {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2104360473618474099L;

	public LocalHost() {
		super("127.0.0.1", "public");
	}
}
