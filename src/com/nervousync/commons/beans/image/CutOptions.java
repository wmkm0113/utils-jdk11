/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervousync Studio.
 */
package com.nervousync.commons.beans.image;

import com.nervousync.commons.core.Globals;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: May 1, 2018 5:36:55 PM $
 */
public final class CutOptions {

	private int positionX = Globals.DEFAULT_VALUE_INT;
	private int positionY = Globals.DEFAULT_VALUE_INT;
	private int cutWidth = Globals.DEFAULT_VALUE_INT;
	private int cutHeight = Globals.DEFAULT_VALUE_INT;
	
	public CutOptions(int cutWidth, int cutHeight) {
		this.positionX = 0;
		this.positionY = 0;
		this.cutWidth = cutWidth;
		this.cutHeight = cutHeight;
	}
	
	public CutOptions(int positionX, int positionY, int cutWidth, int cutHeight) {
		this.positionX = positionX;
		this.positionY = positionY;
		this.cutWidth = cutWidth;
		this.cutHeight = cutHeight;
	}

	/**
	 * @return the positionX
	 */
	public int getPositionX() {
		return positionX;
	}

	/**
	 * @return the positionY
	 */
	public int getPositionY() {
		return positionY;
	}

	/**
	 * @return the cutWidth
	 */
	public int getCutWidth() {
		return cutWidth;
	}

	/**
	 * @return the cutHeight
	 */
	public int getCutHeight() {
		return cutHeight;
	}
}
