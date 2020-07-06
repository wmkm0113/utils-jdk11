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
package org.nervousync.utils;

import java.awt.AlphaComposite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.nervousync.commons.beans.image.CutOptions;
import org.nervousync.commons.beans.image.MarkOptions;
import org.nervousync.commons.beans.image.MarkOptions.MarkPosition;
import org.nervousync.commons.core.Globals;

/**
 * Image utils
 * implements: 
 * Resize image by ratio
 * Resize image to target width/height
 * Retrieve image width value
 * Retrieve image height value
 * Cut image
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: May 1, 2018 1:49:46 PM $
 */
public final class ImageUtils {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ImageUtils.class);

	/**
	 * Read image width
	 * @param imagePath		image file path
	 * @return	image width value
	 */
	public static int imageWidth(String imagePath) {
		if (FileUtils.isExists(imagePath) && FileUtils.isPicture(imagePath)) {
			try {
				BufferedImage srcImage = ImageIO.read(FileUtils.getFile(imagePath));
				return srcImage.getWidth(null);
			} catch (Exception e) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Read picture error! ", e);
				}
			}
		}
		return Globals.DEFAULT_VALUE_INT;
	}

	/**
	 * Read image height
	 * @param imagePath		image file path
	 * @return	image height value
	 */
	public static int imageHeight(String imagePath) {
		if (FileUtils.isExists(imagePath) && FileUtils.isPicture(imagePath)) {
			try {
				BufferedImage srcImage = ImageIO.read(FileUtils.getFile(imagePath));
				return srcImage.getHeight(null);
			} catch (Exception e) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Read picture error! ", e);
				}
			}
		}
		return Globals.DEFAULT_VALUE_INT;
	}

	/**
	 * Read image ratio
	 * @param imagePath		image file path
	 * @return  image ratio
	 */
	public static double imageRatio(String imagePath) {
		double imageHeight = ImageUtils.imageHeight(imagePath);
		double imageWidth = ImageUtils.imageHeight(imagePath);

		if (imageHeight == Globals.DEFAULT_VALUE_DOUBLE || imageWidth == Globals.DEFAULT_VALUE_DOUBLE) {
			return Globals.DEFAULT_VALUE_DOUBLE;
		}

		return imageWidth / imageHeight;
	}
	
	/**
	 * Cut image 
	 * @param origPath			original image file path
	 * @param destPath			target output file path
	 * @param cutOptions		cut options
	 * @return		<code>true</code>success	<code>false</code>failed
	 */
	public static boolean cutImage(String origPath, String destPath, CutOptions cutOptions) {
		if (origPath != null && FileUtils.isExists(origPath) && cutOptions != null) {
			if (cutOptions.getPositionX() + cutOptions.getCutWidth() > ImageUtils.imageWidth(origPath)) {
				LOGGER.error("Width is out of original file");
				return Globals.DEFAULT_VALUE_BOOLEAN;
			}
			if (cutOptions.getPositionY() + cutOptions.getCutHeight() > ImageUtils.imageHeight(origPath)) {
				LOGGER.error("Height is out of original file");
				return Globals.DEFAULT_VALUE_BOOLEAN;
			}

			try {
				BufferedImage srcImage = ImageIO.read(FileUtils.getFile(origPath));
				BufferedImage bufferedImage = 
						new BufferedImage(cutOptions.getCutWidth(), cutOptions.getCutHeight(), 
								BufferedImage.TYPE_INT_RGB);
				
				for (int i = 0 ; i < cutOptions.getCutWidth() ; i++) {
					for (int j = 0 ; j < cutOptions.getCutHeight() ; j++) {
						bufferedImage.setRGB(i, j, 
								srcImage.getRGB(cutOptions.getPositionX() + i, cutOptions.getPositionY() + j));
					}
				}
				
				return ImageIO.write(bufferedImage, StringUtils.getFilenameExtension(destPath), FileUtils.getFile(destPath));
			} catch (Exception e) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Read picture error! ", e);
				}
			}
		}
		return Globals.DEFAULT_VALUE_BOOLEAN;
	}

	/**
	 * Resize picture by given ratio
	 * @param origPath		original picture file path
	 * @param destPath		target picture file path
	 * @param ratio			resize ratio
	 * @return		<code>true</code>success	<code>false</code>failed
	 */
	public static boolean resizeByRatio(String origPath, String destPath, double ratio) {
		return ImageUtils.resizeByRatio(origPath, destPath, ratio, null);
	}

	/**
	 * Resize picture by given ratio
	 * @param origPath		original picture file path
	 * @param destPath		target picture file path
	 * @param ratio			resize ratio
	 * @param markOptions	mark options
	 * @see MarkOptions
	 * @return		<code>true</code>success	<code>false</code>failed
	 */
	public static boolean resizeByRatio(String origPath, String destPath, double ratio, 
			MarkOptions markOptions) {
		if (FileUtils.isExists(origPath) && FileUtils.isPicture(origPath) && ratio > 0) {
			try {
				BufferedImage srcImage = ImageIO.read(FileUtils.getFile(origPath));
				
				int origWidth = srcImage.getWidth(null);
				int origHeight = srcImage.getHeight(null);
				
				int targetWidth = Double.valueOf(origWidth * ratio).intValue();
				int targetHeight = Double.valueOf(origHeight * ratio).intValue();

				return ImageIO.write(processImage(srcImage, targetWidth, targetHeight, markOptions),
						StringUtils.getFilenameExtension(destPath),
						FileUtils.getFile(destPath));
			} catch (Exception e) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Resize picture error! ", e);
				}
			}
		}
		return Globals.DEFAULT_VALUE_BOOLEAN;
	}
	
	/**
	 * Resize picture with given width and height
	 * @param origPath			original picture file path
	 * @param destPath			target picture file path
	 * @param targetWidth		target width	(if -1 width will auto set by height ratio)
	 * @param targetHeight		target height	(if -1 height will auto set by width ratio)
	 * @return		<code>true</code>success	<code>false</code>failed
	 */
	public static boolean resizeTo(String origPath, String destPath, 
			int targetWidth, int targetHeight) {
		return ImageUtils.resizeTo(origPath, destPath, targetWidth, targetHeight, null);
	}
	
	/**
	 * Resize picture with given width and height
	 * @param origPath			original picture file path
	 * @param destPath			target picture file path
	 * @param targetWidth		target width	(if -1 width will auto set by height ratio)
	 * @param targetHeight		target height	(if -1 height will auto set by width ratio)
	 * @param markOptions		mark options
	 * @see MarkOptions
	 * @return		<code>true</code>success	<code>false</code>failed
	 */
	public static boolean resizeTo(String origPath, String destPath, 
			int targetWidth, int targetHeight, MarkOptions markOptions) {
		if (FileUtils.isExists(origPath) && FileUtils.isPicture(origPath) 
				&& (targetWidth > 0 || targetHeight > 0)) {
			try {
				BufferedImage srcImage = ImageIO.read(FileUtils.getFile(origPath));

				int origWidth = srcImage.getWidth(null);
				int origHeight = srcImage.getHeight(null);

				if (targetWidth == Globals.DEFAULT_VALUE_INT) {
					double ratio = targetHeight * 1.0 / origHeight;
					targetWidth = Double.valueOf(ratio * origWidth).intValue();
				}

				if (targetHeight == Globals.DEFAULT_VALUE_INT) {
					double ratio = targetWidth * 1.0 / origWidth;
					targetHeight = Double.valueOf(ratio * origHeight).intValue();
				}

				return ImageIO.write(processImage(srcImage, targetWidth, targetHeight, markOptions),
						StringUtils.getFilenameExtension(destPath),
						FileUtils.getFile(destPath));
			} catch (Exception e) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Resize picture error! ", e);
				}
			}
		}
		return Globals.DEFAULT_VALUE_BOOLEAN;
	}

	/**
	 * Mark image
	 * @param filePath          original picture file path
	 * @param targetPath        target picture file path
	 * @param markOptions       mark options
	 * @return                  operate result
	 */
	public static boolean markImage(String filePath, String targetPath, MarkOptions markOptions) {
		int imageWidth = ImageUtils.imageWidth(filePath);
		int imageHeight = ImageUtils.imageHeight(filePath);
		try {
			BufferedImage srcImage = ImageIO.read(FileUtils.getFile(filePath));
			BufferedImage bufferedImage =
					new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
			return ImageIO.write(processImage(bufferedImage, imageWidth, imageHeight, markOptions),
					StringUtils.getFilenameExtension(targetPath),
					FileUtils.getFile(targetPath));
		} catch (Exception e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Mark picture error! ", e);
			}
		}
		return Globals.DEFAULT_VALUE_BOOLEAN;
	}

	/**
	 * Calculate dHash hamming between original image and target image
	 * @param origPath			original picture file path
	 * @param destPath			target picture file path
	 * @return                  Hamming result
	 */
	public static int dHashHamming(String origPath, String destPath) {
		String origHash = ImageUtils.dHash(origPath);
		String destHash = ImageUtils.dHash(destPath);
		int diff = 0;
		for (int j = 0 ; j < origHash.length() ; j++) {
			diff += (origHash.charAt(j) ^ destHash.charAt(j));
		}
		return diff;
	}

	/**
	 * Calculate pHash hamming between original image and target image
	 * @param origPath			original picture file path
	 * @param destPath			target picture file path
	 * @return                  Hamming result
	 */
	public static int pHashHamming(String origPath, String destPath) {
		String origHash = ImageUtils.pHash(origPath);
		String destHash = ImageUtils.pHash(destPath);
		int diff = 0;
		for (int j = 0 ; j < origHash.length() ; j++) {
			diff += (origHash.charAt(j) ^ destHash.charAt(j));
		}
		return diff;
	}

	/**
	 * dHash signature
	 * @param filePath  picture file path
	 * @return          signature value
	 */
	public static String dHash(String filePath) {
		try {
			return ImageUtils.dHash(FileUtils.getFile(filePath));
		} catch (FileNotFoundException e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Calculate picture pHash error! ", e);
			}
			return Globals.DEFAULT_VALUE_STRING;
		}
	}

	/**
	 * dHash signature
	 * @param file      picture file instance
	 * @return          signature value
	 */
	public static String dHash(File file) {
		try {
			return ImageUtils.dHash(ImageIO.read(file));
		} catch (IOException e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Calculate picture pHash error! ", e);
			}
			return Globals.DEFAULT_VALUE_STRING;
		}
	}

	/**
	 * dHash signature
	 * @param bufferedImage     picture file with bufferedImage instance
	 * @return                  signature value
	 */
	public static String dHash(BufferedImage bufferedImage) {
		int width = bufferedImage.getWidth();
		int height = bufferedImage.getHeight();
		BufferedImage prepareImage;
		if (bufferedImage.getWidth() != 9 || bufferedImage.getHeight() != 8) {
			prepareImage = ImageUtils.processImage(bufferedImage, 9, 8, null);
		} else {
			prepareImage = bufferedImage;
		}

		double[][] grayMatrix = ImageUtils.grayMatrix(prepareImage);
		StringBuilder pHash = new StringBuilder();
		for (int x = 0 ; x < 8 ; x++) {
			for (int y = 0 ; y < 8 ; y++) {
				pHash.append((grayMatrix[x][y] > grayMatrix[x][y + 1]) ? "1" : "0");
			}
		}
		return pHash.toString();
	}

	/**
	 * pHash signature
	 * @param filePath  picture file path
	 * @return          signature value
	 */
	public static String pHash(String filePath) {
		try {
			return ImageUtils.pHash(FileUtils.getFile(filePath));
		} catch (FileNotFoundException e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Calculate picture pHash error! ", e);
			}
			return Globals.DEFAULT_VALUE_STRING;
		}
	}

	/**
	 * pHash signature
	 * @param file      picture file instance
	 * @return          signature value
	 */
	public static String pHash(File file) {
		try {
			return ImageUtils.pHash(ImageIO.read(file));
		} catch (IOException e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Calculate picture pHash error! ", e);
			}
			return Globals.DEFAULT_VALUE_STRING;
		}
	}

	/**
	 * pHash signature
	 * @param bufferedImage     picture file with bufferedImage instance
	 * @return                  signature value
	 */
	public static String pHash(BufferedImage bufferedImage) {
		int width = bufferedImage.getWidth();
		int height = bufferedImage.getHeight();
		BufferedImage prepareImage;
		if (bufferedImage.getWidth() != 8 || bufferedImage.getHeight() != 8) {
			prepareImage = ImageUtils.processImage(bufferedImage, 8, 8, null);
		} else {
			prepareImage = bufferedImage;
		}

		double[][] DCT = ImageUtils.applyDCT(prepareImage);
		double total = 0.0;
		for (int x = 0 ; x < 8 ; x++) {
			for (int y = 0 ; y < 8 ; y++) {
				total += DCT[x][y];
			}
		}
		total -= DCT[0][0];
		double average = total / 63;
		StringBuilder pHash = new StringBuilder();
		for (int x = 0 ; x < 8 ; x++) {
			for (int y = 0 ; y < 8 ; y++) {
				pHash.append((DCT[x][y] > average) ? "1" : "0");
			}
		}
		return pHash.toString();
	}
	
	/**
	 * Process image mark
	 * @param graphics			target image graphics object
	 * @param width				image width
	 * @param height			image height
	 * @param markOptions		image mark options
	 */
	private static void markImage(Graphics2D graphics, int width, int height, MarkOptions markOptions) {
		MarkPosition markPosition = markOptions.retrievePosition(width, height);
		
		if (markPosition != null) {
			switch (markOptions.getMarkType()) {
			case ICON:
				try {
					BufferedImage iconImg = ImageIO.read(FileUtils.getFile(markOptions.getMarkPath()));
					if (iconImg != null && markOptions.getTransparency() >= 0 
							&& markOptions.getTransparency() <= 1) {
						graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 
								markOptions.getTransparency()));
						graphics.drawImage(iconImg, markPosition.getPositionX(), markPosition.getPositionY(), null);
						
						graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
					}
				} catch (Exception e) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Read icon file error! ", e);
					}
				}
				break;
			case TEXT:
				if (markOptions.getMarkText() != null && markOptions.getFontName() != null
						&& markOptions.getFontSize() > 0) {
					graphics.setColor(markOptions.getColor());
					graphics.setFont(new Font(markOptions.getFontName(), Font.PLAIN, markOptions.getFontSize()));
					
					graphics.drawString(markOptions.getMarkText(), markPosition.getPositionX(), markPosition.getPositionY());
				}
				break;
			}
		}
	}
	
	/**
	 * Process image operate
	 * @param srcImage				original image object
	 * @param targetWidth			target width
	 * @param targetHeight			target height
	 * @param markOptions			image mark options
	 * @return		<code>true</code>success	<code>false</code>failed
	 */
	private static BufferedImage processImage(BufferedImage srcImage,
			int targetWidth, int targetHeight, MarkOptions markOptions) {
		if (srcImage != null && targetWidth > 0 && targetHeight > 0) {
			try {
				BufferedImage bufferedImage = 
						new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
				Graphics2D graphics = bufferedImage.createGraphics();
				graphics.drawImage(srcImage, 0, 0, targetWidth, targetHeight, null);

				if (markOptions != null) {
					markImage(graphics, targetWidth, targetHeight, markOptions);
				}
				graphics.dispose();
				
				return bufferedImage;
			} catch (Exception e) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Resize picture error! ", e);
				}
			}
		}
		return srcImage;
	}

	/**
	 * Convert bufferedImage to grayMatrix
	 * @param bufferedImage     BufferedImage instance
	 * @return                  Gray matrix
	 */
	private static double[][] grayMatrix(BufferedImage bufferedImage) {
		if (bufferedImage == null) {
			return new double[0][0];
		}
		int width = bufferedImage.getWidth();
		int height = bufferedImage.getHeight();
		double[][] grayMatrix = new double[height][width];
		for (int y = 0 ; y < height ; y++) {
			for (int x = 0 ; x < width ; x++) {
				int pixel = bufferedImage.getRGB(x, y);
				grayMatrix[y][x] = ((pixel & 0xFF0000) >> 16) * 0.3
						+ ((pixel & 0xFF00) >> 8) * 0.59
						+ ((pixel & 0xFF) * 0.11);
			}
		}
		return grayMatrix;
	}

	/**
	 * Convert bufferedImage to DCT matrix
	 * @param bufferedImage     BufferedImage instance
	 * @return                  DCT matrix
	 */
	private static double[][] applyDCT(BufferedImage bufferedImage) {
		if (bufferedImage == null) {
			return new double[0][0];
		}
		int width = bufferedImage.getWidth();
		int height = bufferedImage.getHeight();
		int length = Math.max(width, height);
		double[] uv = new double[length];
		for (int i = 1 ; i < length ; i++) {
			uv[i] = 1;
		}
		uv[0] = 1 / Math.sqrt(2.0);
		double[][] DCT = new double[width][height];
		for (int x = 0 ; x < width ; x++) {
			for (int y = 0 ; y < height ; y++) {
				double sum = 0.0;
				int pixel = bufferedImage.getRGB(x, y);
				double gray = ((pixel & 0xFF0000) >> 16) * 0.3
						+ ((pixel & 0xFF00) >> 8) * 0.59
						+ ((pixel & 0xFF) * 0.11);
				for (int i = 0 ; i < width ; i++) {
					for (int j = 0 ; j < height ; j++) {
						sum += Math.cos(((2 * i + 1) / (width * height * 1.0)) * x * Math.PI)
								* Math.cos(((2 * j + 1) / (width * height * 1.0)) * y * Math.PI)
								* gray;
					}
				}
				DCT[x][y] = sum * ((uv[x] * uv[y]) / 4.0);
			}
		}
		return DCT;
	}
}
