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
package org.nervousync.beans.image;

import org.nervousync.commons.core.Globals;

/**
 * Images options of cut operate
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: May 1, 2018 5:36:55 PM $
 */
public final class CutOptions {

	/**
	 * Begin position X, default value is 0
	 */
	private final int positionX;
	/**
	 * Begin position Y, default value is 0
	 */
	private final int positionY;
	/**
	 * Cut width
	 */
	private final int cutWidth;
	/**
	 * Cut height
	 */
	private final int cutHeight;

	/**
	 * Initialize cut options with given position X and Y
	 * @param positionX			Cut begin position X
	 * @param positionY			Cut begin position Y
	 * @param cutWidth			Cut width
	 * @param cutHeight			Cut height
	 */
	private CutOptions(final int positionX, final int positionY, final int cutWidth, final int cutHeight) {
		this.positionX = positionX < 0 ? Globals.INITIALIZE_INT_VALUE : positionX;
		this.positionY = positionY < 0 ? Globals.INITIALIZE_INT_VALUE : positionY;
		this.cutWidth = cutWidth;
		this.cutHeight = cutHeight;
	}

	public static CutOptions newInstance(final int cutWidth, final int cutHeight) {
		return newInstance(Globals.DEFAULT_VALUE_INT, Globals.DEFAULT_VALUE_INT, cutWidth, cutHeight);
	}

	public static CutOptions newInstance(final int positionX, final int positionY,
	                                     final int cutWidth, final int cutHeight) {
		return new CutOptions(positionX, positionY, cutWidth, cutHeight);
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
