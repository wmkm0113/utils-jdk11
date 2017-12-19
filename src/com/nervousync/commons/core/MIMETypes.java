/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervousync Studio.
 */
package com.nervousync.commons.core;

import java.io.Serializable;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Jun 15, 2015 10:10:56 AM $
 */
public final class MIMETypes implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7681153227888421456L;
	
	/* General MIME Type List */
	
	/* Application MIME Type */
	public static final String MIME_TYPE_WORD_DOC = "application/msword";
	public static final String MIME_TYPE_WORD_DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
	public static final String MIME_TYPE_EXCEL_XLS = "application/vnd.ms-excel";
	public static final String MIME_TYPE_EXCEL_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
	public static final String MIME_TYPE_POWERPOINT_PPT = "application/vnd.ms-powerpoint";
	public static final String MIME_TYPE_POWERPOINT_PPTX = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
	public static final String MIME_TYPE_POSTSCRIPT = "application/postscript";
	public static final String MIME_TYPE_RTF = "application/rtf";
	public static final String MIME_TYPE_PDF = "application/pdf";
	public static final String MIME_TYPE_FLV = "flv-application/octet-stream";
	public static final String MIME_TYPE_7Z = "application/x-7z-compressed";
	public static final String MIME_TYPE_BZ2 = "application/x-bzip2";
	public static final String MIME_TYPE_GZIP = "application/x-gzip";
	public static final String MIME_TYPE_TAR = "application/x-tar";
	public static final String MIME_TYPE_ZIP = "application/zip";
	public static final String MIME_TYPE_RAR = "application/x-rar-compressed";
	public static final String MIME_TYPE_XZ = "application/x-xz";
	
	/* Media MIME Type */
	public static final String MIME_TYPE_MPEG_AUDIO = "audio/x-ms-wma";
	public static final String MIME_TYPE_MPEG_VIDEO = "video/mpeg";
	public static final String MIME_TYPE_QUICKTIME = "video/quicktime";
	public static final String MIME_TYPE_AVI = "video/x-msvideo";
	public static final String MIME_TYPE_ASF = "video/x-ms-asf";
	public static final String MIME_TYPE_MID = "audio/midi";
	public static final String MIME_TYPE_MIDI = "audio/x-midi";
	public static final String MIME_TYPE_REAL_AUDIO = "audio/x-pn-realaudio";
	public static final String MIME_TYPE_WAV = "audio/x-wav";

	private static final String [] MIME_TYPE_MEDIAS = {
		MIME_TYPE_MPEG_AUDIO, MIME_TYPE_MPEG_VIDEO, MIME_TYPE_QUICKTIME, 
		MIME_TYPE_AVI, MIME_TYPE_MID, MIME_TYPE_MIDI, MIME_TYPE_REAL_AUDIO, 
		MIME_TYPE_ASF, MIME_TYPE_WAV
	};

	/* Image MIME Type */
	public static final String MIME_TYPE_BMP = "image/bmp";
	public static final String MIME_TYPE_GIF = "image/gif";
	public static final String MIME_TYPE_JPEG = "image/jpeg";
	public static final String MIME_TYPE_TIFF = "image/tiff";
	public static final String MIME_TYPE_PNG = "image/png";
	public static final String MIME_TYPE_DWG = "image/vnd.dwg";
	public static final String MIME_TYPE_PSD = "image/psd";

	private static final String [] MIME_TYPE_IMAGES = {
		MIME_TYPE_PNG, MIME_TYPE_GIF, MIME_TYPE_JPEG, 
		MIME_TYPE_TIFF, MIME_TYPE_BMP, MIME_TYPE_DWG, MIME_TYPE_PSD
	};

	/* Text MIME Type */
	public static final String MIME_TYPE_CSS = "text/css";
	public static final String MIME_TYPE_TEXT = "text/plain";
	public static final String MIME_TYPE_TEXT_XML = "text/xml";
	public static final String MIME_TYPE_APPLICATION_XML = "application/xml";
	public static final String MIME_TYPE_XUL = "text/xul";
	public static final String MIME_TYPE_JSON = "application/json";
	public static final String MIME_TYPE_JAVA_SCRIPT = "application/x-javascript";
	public static final String MIME_TYPE_HTML = "text/html";
	public static final String MIME_TYPE_XHTML = "application/xhtml+xml";
	
	private static final String [] MIME_TYPE_TEXTS = {
		MIME_TYPE_CSS, MIME_TYPE_TEXT, MIME_TYPE_TEXT_XML, MIME_TYPE_APPLICATION_XML, MIME_TYPE_XUL, 
		MIME_TYPE_JSON, MIME_TYPE_JAVA_SCRIPT, MIME_TYPE_HTML, MIME_TYPE_XHTML
	};
	
	/* Other MIME Type */
	public static final String MIME_TYPE_VRML = "x-world/x-vrml";
	public static final String MIME_TYPE_BINARY = "application/octet-stream";
	
	/* Mobile MIME Type */
	public static final String MIME_TYPE_MRP = "application/octet-stream";
	public static final String MIME_TYPE_IPA = "application/iphone-package-archive";
	public static final String MIME_TYPE_DEB = "application/x-debian-package-archive";
	public static final String MIME_TYPE_APK = "application/vnd.android.package-archive";
	public static final String MIME_TYPE_CAB = "application/vnd.cab-com-archive";
	public static final String MIME_TYPE_XAP = "application/x-silverlight-app";
	public static final String MIME_TYPE_SIS = "application/vnd.symbian.install-archive";
	public static final String MIME_TYPE_SISX = "application/vnd.symbian.epoc/x-sisx-app";
	public static final String MIME_TYPE_JAR = "application/java-archive";
	public static final String MIME_TYPE_JAD = "text/vnd.sun.j2me.app-descriptor";
	
	public static final String MIME_TYPE_EMAIL = "message/rfc822";
	public static final String MIME_TYPE_OUTLOOK = "application/vnd.ms-outlook";
	public static final String MIME_TYPE_ACCESS = "application/x-msaccess";

	private MIMETypes() {
		//	Do nothing
	}
	
	public static boolean isImage(String mimeType) {
		for (String imageType : MIME_TYPE_IMAGES) {
			if (mimeType.indexOf(imageType) != Globals.DEFAULT_VALUE_INT) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isMedia(String mimeType) {
		for (String mediaType : MIME_TYPE_MEDIAS) {
			if (mimeType.indexOf(mediaType) != Globals.DEFAULT_VALUE_INT) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isXml(String mimeType) {
		if (mimeType.indexOf(MIME_TYPE_TEXT_XML) != Globals.DEFAULT_VALUE_INT
				|| mimeType.indexOf(MIME_TYPE_APPLICATION_XML) != Globals.DEFAULT_VALUE_INT) {
			return true;
		}
		return false;
	}
	
	public static boolean isJSON(String mimeType) {
		if (mimeType.indexOf(MIME_TYPE_JSON) != Globals.DEFAULT_VALUE_INT) {
			return true;
		}
		return false;
	}
	
	public static boolean isText(String mimeType) {
		for (String textType : MIME_TYPE_TEXTS) {
			if (mimeType.indexOf(textType) != Globals.DEFAULT_VALUE_INT) {
				return true;
			}
		}
		return false;
	}
}
