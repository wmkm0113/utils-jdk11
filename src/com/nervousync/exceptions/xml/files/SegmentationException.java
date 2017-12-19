/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervousync Studio.
 */
package com.nervousync.exceptions.xml.files;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Jun 9, 2015 9:48:53 AM $
 */
public class SegmentationException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1716772082603124808L;

	/**
	 * Constructor for SegmentationException.
	 */
	public SegmentationException() {
		super();
	}

	/**
	 * Constructor for SegmentationException.
	 *
	 * @param message error message
	 */
	public SegmentationException(String message) {
		super(message);
	}

	/**
	 * Constructor for SegmentationException.
	 *
	 * @param message error message
	 * @param cause wrapped Throwable
	 */
	public SegmentationException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor for SegmentationException.
	 *
	 * @param cause wrapped Throwable
	 */
	public SegmentationException(Throwable cause) {
		super(cause);
	}
}
