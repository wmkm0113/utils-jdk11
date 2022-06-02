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

import java.awt.Color;
import java.awt.Image;

import javax.swing.ImageIcon;

import org.nervousync.commons.core.Globals;

/**
 * Image options for mark operate
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: May 1, 2018 4:11:01 PM $
 */
public final class MarkOptions {
	
	/**
	 * Mark type
	 * @see MarkOptions.MarkType
	 */
	private final MarkType markType;
	/**
	 * Mark location define
	 * @see MarkOptions.MarkLocation
	 */
	private final MarkLocation markLocation;
	/**
	 * Mark image path if mark type is MarkType.ICON
	 */
	private final String markPath;
	/**
	 * Mark image transparent value, default is 1, valid value is between 0 and 1
	 */
	private final float transparency;
	/**
	 * Mark text value if mark type is MarkType.TEXT
	 */
	private final String markText;
	/**
	 * Mark text color setting
	 */
	private final Color color;
	/**
	 * Mark text font name
	 */
	private final String fontName;
	/**
	 * Mark text font size
	 */
	private final int fontSize;
	
	private MarkOptions(MarkType markType, MarkLocation markLocation, 
			String markPath, float transparency, String markText, 
			Color color, String fontName, int fontSize) {
		this.markType = markType;
		this.markLocation = markLocation;
		this.markPath = markPath;
		this.transparency = transparency;
		this.markText = markText;
		this.color = color;
		this.fontName = fontName;
		this.fontSize = fontSize;
	}

	/**
	 * Initialize ICON MarkOptions
	 *
	 * @param markLocation Mark location define
	 * @param markPath     Mark icon file path
	 * @param transparency Icon transparent setting
	 * @return MarkOption object
	 */
	public static MarkOptions markIcon(MarkLocation markLocation,
			String markPath, float transparency) {
		return new MarkOptions(MarkType.ICON, markLocation, markPath, transparency, null, null, 
				null, Globals.DEFAULT_VALUE_INT);
	}

	/**
	 * Initialize TEXT MarkOptions
	 *
	 * @param markLocation Mark location define
	 * @param markText     Mark text value
	 * @param color        Mark text color
	 * @param fontName     Mark text font name
	 * @param fontSize     Mark text font size
	 * @return MarkOption object
	 */
	public static MarkOptions markText(MarkLocation markLocation,
			String markText, Color color, String fontName, int fontSize) {
		return new MarkOptions(MarkType.TEXT, markLocation, null, Globals.DEFAULT_VALUE_FLOAT, 
				markText, color, fontName, fontSize);
	}

	/**
	 * Gets mark type.
	 *
	 * @return the markType
	 */
	public MarkType getMarkType() {
		return markType;
	}

	/**
	 * Gets mark location.
	 *
	 * @return the markLocation
	 */
	public MarkLocation getMarkLocation() {
		return markLocation;
	}

	/**
	 * Gets mark path.
	 *
	 * @return the markPath
	 */
	public String getMarkPath() {
		return markPath;
	}

	/**
	 * Gets transparency.
	 *
	 * @return the transparency
	 */
	public float getTransparency() {
		return transparency;
	}

	/**
	 * Gets mark text.
	 *
	 * @return the markText
	 */
	public String getMarkText() {
		return markText;
	}

	/**
	 * Gets color.
	 *
	 * @return the color
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Gets font name.
	 *
	 * @return the fontName
	 */
	public String getFontName() {
		return fontName;
	}

	/**
	 * Gets font size.
	 *
	 * @return the fontSize
	 */
	public int getFontSize() {
		return fontSize;
	}

	/**
	 * Retrieve position mark position.
	 *
	 * @param width  the width
	 * @param height the height
	 * @return the mark position
	 */
	public MarkPosition retrievePosition(int width, int height) {
		int positionX = Globals.DEFAULT_VALUE_INT;
		int positionY = Globals.DEFAULT_VALUE_INT;
		switch (this.markType) {
		case ICON:
			ImageIcon imageIcon = new ImageIcon(this.markPath);
			Image iconImg = imageIcon.getImage();

			if (iconImg != null && this.transparency >= 0 
					&& this.transparency <= 1) {
				
				switch (this.markLocation) {
				case LEFT_TOP:
					positionX = 0;
					positionY = 0;
					break;
				case TOP:
					positionX = (width - iconImg.getWidth(null)) / 2;
					positionY = 0;
					break;
				case RIGHT_TOP:
					positionX = width - iconImg.getWidth(null);
					positionY = 0;
					break;
				case LEFT:
					positionX = 0;
					positionY = (height - iconImg.getHeight(null)) / 2; 
					break;
				case CENTER:
					positionX = (width - iconImg.getWidth(null)) / 2;
					positionY = (height - iconImg.getHeight(null)) / 2; 
					break;
				case RIGHT:
					positionX = width - iconImg.getWidth(null);
					positionY = (height - iconImg.getHeight(null)) / 2; 
					break;
				case LEFT_BOTTOM:
					positionX = 0;
					positionY = height - iconImg.getHeight(null); 
					break;
				case BOTTOM:
					positionX = (width - iconImg.getWidth(null)) / 2;
					positionY = height - iconImg.getHeight(null); 
					break;
				case RIGHT_BOTTOM:
					positionX = width - iconImg.getWidth(null);
					positionY = height - iconImg.getHeight(null); 
					break;
				}
			}
			break;
		case TEXT:
			if (this.markText != null && this.fontName != null
					&& this.fontSize > 0) {
				int textWidth = this.markText.length() * this.fontSize;
				int textHeight = this.fontSize;
				
				switch (this.markLocation) {
				case LEFT_TOP:
					positionX = 0;
					positionY = textHeight;
					break;
				case TOP:
					positionX = (width - textWidth) / 2;
					positionY = textHeight;
					break;
				case RIGHT_TOP:
					positionX = width - textWidth;
					positionY = textHeight;
					break;
				case LEFT:
					positionX = 0;
					positionY = (height + textHeight) / 2;
					break;
				case CENTER:
					positionX = (width - textWidth) / 2;
					positionY = (height + textHeight) / 2;
					break;
				case RIGHT:
					positionX = width - textWidth;
					positionY = (height + textHeight) / 2;
					break;
				case LEFT_BOTTOM:
					positionX = 0;
					positionY = height;
					break;
				case BOTTOM:
					positionX = (width - textWidth) / 2;
					positionY = height;
					break;
				case RIGHT_BOTTOM:
					positionX = width - textWidth;
					positionY = height;
					break;
				}
			}
			break;
		}
		
		if (positionX != Globals.DEFAULT_VALUE_INT && positionY != Globals.DEFAULT_VALUE_INT) {
			return new MarkPosition(positionX, positionY);
		}
		return null;
	}

	/**
	 * The type Mark position.
	 */
	public static final class MarkPosition {
		
		private final int positionX;
		private final int positionY;

		/**
		 * Instantiates a new Mark position.
		 *
		 * @param positionX the position x
		 * @param positionY the position y
		 */
		public MarkPosition(int positionX, int positionY) {
			this.positionX = positionX;
			this.positionY = positionY;
		}

		/**
		 * Gets position x.
		 *
		 * @return the positionX
		 */
		public int getPositionX() {
			return positionX;
		}

		/**
		 * Gets position y.
		 *
		 * @return the positionY
		 */
		public int getPositionY() {
			return positionY;
		}
	}

	/**
	 * The enum Mark type.
	 */
	public enum MarkType {
		/**
		 * Icon mark type.
		 */
		ICON,
		/**
		 * Text mark type.
		 */
		TEXT
	}

	/**
	 * The enum Mark location.
	 */
	public enum MarkLocation {
		/**
		 * Left top mark location.
		 */
		LEFT_TOP,
		/**
		 * Top mark location.
		 */
		TOP,
		/**
		 * Right top mark location.
		 */
		RIGHT_TOP,
		/**
		 * Left mark location.
		 */
		LEFT,
		/**
		 * Center mark location.
		 */
		CENTER,
		/**
		 * Right mark location.
		 */
		RIGHT,
		/**
		 * Left bottom mark location.
		 */
		LEFT_BOTTOM,
		/**
		 * Bottom mark location.
		 */
		BOTTOM,
		/**
		 * Right bottom mark location.
		 */
		RIGHT_BOTTOM
	}
}
