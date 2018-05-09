/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervousync Studio.
 */
package com.nervousync.commons.beans.image;

import java.awt.Color;
import java.awt.Image;

import javax.swing.ImageIcon;

import com.nervousync.commons.core.Globals;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: May 1, 2018 4:11:01 PM $
 */
public final class MarkOptions {
	
	private MarkType markType;
	private MarkLocation markLocation;
	private String markPath = null;
	private float transparency = 1f;
	private String markText = null;
	private Color color = Color.BLACK;
	private String fontName = null;
	private int fontSize = 12;
	
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
	
	public static MarkOptions markIcon(MarkLocation markLocation, 
			String markPath, float transparency) {
		return new MarkOptions(MarkType.ICON, markLocation, markPath, transparency, null, null, 
				null, Globals.DEFAULT_VALUE_INT);
	}

	public static MarkOptions markText(MarkLocation markLocation, 
			String markText, Color color, String fontName, int fontSize) {
		return new MarkOptions(MarkType.TEXT, markLocation, null, Globals.DEFAULT_VALUE_FLOAT, 
				markText, color, fontName, fontSize);
	}

	/**
	 * @return the markType
	 */
	public MarkType getMarkType() {
		return markType;
	}

	/**
	 * @return the markLocation
	 */
	public MarkLocation getMarkLocation() {
		return markLocation;
	}

	/**
	 * @return the markPath
	 */
	public String getMarkPath() {
		return markPath;
	}

	/**
	 * @return the transparency
	 */
	public float getTransparency() {
		return transparency;
	}

	/**
	 * @return the markText
	 */
	public String getMarkText() {
		return markText;
	}

	/**
	 * @return the color
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * @return the fontName
	 */
	public String getFontName() {
		return fontName;
	}

	/**
	 * @return the fontSize
	 */
	public int getFontSize() {
		return fontSize;
	}
	
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
		
	public static final class MarkPosition {
		
		private int positionX = Globals.DEFAULT_VALUE_INT;
		private int positionY = Globals.DEFAULT_VALUE_INT;
		
		public MarkPosition(int positionX, int positionY) {
			this.positionX = positionX;
			this.positionY = positionY;
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
	}

	public enum MarkType {
		ICON, TEXT
	}
	
	public enum MarkLocation {
		LEFT_TOP, TOP, RIGHT_TOP, 
		LEFT, CENTER, RIGHT,
		LEFT_BOTTOM, BOTTOM, RIGHT_BOTTOM
	}
}
