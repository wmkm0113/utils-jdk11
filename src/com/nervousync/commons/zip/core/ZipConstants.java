/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervousync Studio.
 */
package com.nervousync.commons.zip.core;

import com.nervousync.commons.core.Globals;

/**
 * @author Steven Wee
 *         <a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Nov 28, 2017 5:17:09 PM $
 */
public final class ZipConstants {

	/*
	 * Header signatures
	 */
	public static final long LOCSIG = 0x04034b50L; // "PK\003\004"
	public static final long EXTSIG = 0x08074b50L; // "PK\007\008"
	public static final long CENSIG = 0x02014b50L; // "PK\001\002"
	public static final long ENDSIG = 0x06054b50L; // "PK\005\006"
	public static final long DIGSIG = 0x05054b50L;
	public static final long ARCEXTDATREC = 0x08064b50L;
	public static final long SPLITSIG = 0x08074b50L;
	public static final long ZIP64ENDCENDIRLOC = 0x07064b50L;
	public static final long ZIP64ENDCENDIRREC = 0x06064b50;
	public static final int EXTRAFIELDZIP64LENGTH = 0x0001;
	public static final int AESSIG = 0x9901;

	public static final int MAX_ALLOWED_ZIP_COMMENT_LENGTH = 0xFFFF;

	// Compression Types
	public static final int COMP_STORE = 0;
	public static final int COMP_DEFLATE = 8;

	// AES Constants
	public static final int AES_AUTH_LENGTH = 10;
	public static final int AES_BLOCK_SIZE = 16;

	// AES Key Strength
	public static final int AES_STRENGTH_128 = 0x01;
	public static final int AES_STRENGTH_192 = 0x02;
	public static final int AES_STRENGTH_256 = 0x03;

	public static final int MIN_SPLIT_LENGTH = 65536;
	public static final long ZIP_64_LIMIT = 4294967295L;

	public static final int UFT8_NAMES_FLAG = 1 << 11;
	// Encryption types
	public static final int ENC_NO_ENCRYPTION = -1;
	public static final int ENC_METHOD_STANDARD = 0;
	public static final int ENC_METHOD_STRONG = 1;
	public static final int ENC_METHOD_AES = 99;

	// Compression level for deflate algorithm
	public static final int DEFLATE_LEVEL_FASTEST = 1;
	public static final int DEFLATE_LEVEL_FAST = 3;
	public static final int DEFLATE_LEVEL_NORMAL = 5;
	public static final int DEFLATE_LEVEL_MAXIMUM = 7;
	public static final int DEFLATE_LEVEL_ULTRA = 9;

	public static final int PASSWORD_VERIFIER_LENGTH = 2;
	public static final int STD_DEC_HDR_SIZE = 12;

	public static final int LOCHDR = 30; // LOC header size
	public static final int EXTHDR = 16; // EXT header size
	public static final int CENHDR = 46; // CEN header size
	public static final int ENDHDR = 22; // END header size

	public static final int BUFFER_SIZE = 1024 * 4;
	public static final int ZIP64_EXTRA_BUFFER_SIZE = 50;

	public static final int FILE_MODE_NONE = 0;
	public static final int FILE_MODE_READ_ONLY = 1;
	public static final int FOLDER_MODE_NONE = 16;

	public static final String CHARSET_CP850 = "Cp850";
	public static final String CHARSET_COMMENTS_DEFAULT = "windows-1254";

	public static final String ZIP_FILE_SEPARATOR = "/";
	public static final String ZIP_ENTRY_SEPARATOR = ":" + Globals.DEFAULT_PAGE_SEPARATOR;

}
