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
package org.nervousync.commons.core;

import java.io.File;
import java.util.Locale;

/**
 * Globals Constants Value
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: Jul 2, 2018 $
 */
public final class Globals {

	/**
	 * The constant INITIAL_HASH.
	 */
	public static final int INITIAL_HASH = 0;
	/**
	 * The constant MULTIPLIER.
	 */
	public static final int MULTIPLIER = 31;

	/**
	 * The constant DEFAULT_BUFFER_SIZE.
	 */
	public static final int DEFAULT_BUFFER_SIZE = 8192;
	/**
	 * The constant READ_FILE_BUFFER_SIZE.
	 */
//  32K
	public static final int READ_FILE_BUFFER_SIZE = 32768;

	/**
	 * The constant DEFAULT_TIME_OUT.
	 */
	public static final int DEFAULT_TIME_OUT = 0;

	/**
	 * The constant DEFAULT_VALUE_INT.
	 */
	public static final int DEFAULT_VALUE_INT = -1;
	/**
	 * The constant DEFAULT_VALUE_LONG.
	 */
	public static final long DEFAULT_VALUE_LONG = -1L;
	/**
	 * The constant DEFAULT_VALUE_SHORT.
	 */
	public static final short DEFAULT_VALUE_SHORT = -1;
	/**
	 * The constant DEFAULT_VALUE_DOUBLE.
	 */
	public static final double DEFAULT_VALUE_DOUBLE = -1;
	/**
	 * The constant DEFAULT_VALUE_FLOAT.
	 */
	public static final float DEFAULT_VALUE_FLOAT = -1;
	/**
	 * The constant DEFAULT_VALUE_STRING.
	 */
	public static final String DEFAULT_VALUE_STRING = "";

	/**
	 * The constant DEFAULT_REFERENCE_TIME.
	 */
	public static final long DEFAULT_REFERENCE_TIME = 1303315200000L;

	/**
	 * The constant READ_MODE.
	 */
	public static final String READ_MODE = "r";
	/**
	 * The constant WRITE_MODE.
	 */
	public static final String WRITE_MODE = "rw";

	/**
	 * The constant FTP_PROTOCOL.
	 */
	public static final String FTP_PROTOCOL = "ftp://";
	/**
	 * The constant SECURE_FTP_PROTOCOL.
	 */
	public static final String SECURE_FTP_PROTOCOL = "sftp://";
	/**
	 * The constant WEBDAV_PROTOCOL.
	 */
	public static final String WEBDAV_PROTOCOL = "webdav://";
	/**
	 * The constant FILE_PROTOCOL.
	 */
	public static final String FILE_PROTOCOL = "file:///";
	/**
	 * The constant HTTP_PROTOCOL.
	 */
	public static final String HTTP_PROTOCOL = "http://";
	/**
	 * The constant SECURE_HTTP_PROTOCOL.
	 */
	public static final String SECURE_HTTP_PROTOCOL = "https://";

	/**
	 * The constant EXTENSION_SEPARATOR.
	 */
	public static final char EXTENSION_SEPARATOR = '.';

	/**
	 * The constant DEFAULT_EMAIL_FOLDER_INBOX.
	 */
	public static final String DEFAULT_EMAIL_FOLDER_INBOX = "INBOX";
	/**
	 * The constant DEFAULT_EMAIL_FOLDER_INBOX.
	 */
	public static final String DEFAULT_EMAIL_FOLDER_SPAM = "Spam";
	/**
	 * The constant DEFAULT_EMAIL_FOLDER_INBOX.
	 */
	public static final String DEFAULT_EMAIL_FOLDER_DRAFTS = "Drafts";
	/**
	 * The constant DEFAULT_EMAIL_FOLDER_INBOX.
	 */
	public static final String DEFAULT_EMAIL_FOLDER_TRASH = "Trash";
	/**
	 * The constant DEFAULT_EMAIL_FOLDER_INBOX.
	 */
	public static final String DEFAULT_EMAIL_FOLDER_SENT = "Sent";
	/**
	 * The constant DEFAULT_ENCODING.
	 */
	public static final String DEFAULT_ENCODING = "UTF-8";
	/**
	 * The constant CHARSET_CP850.
	 */
	public static final String CHARSET_CP850 = "Cp850";
	/**
	 * The constant CHARSET_GBK.
	 */
	public static final String CHARSET_GBK = "GBK";
	/**
	 * The constant DEFAULT_LOCALE.
	 */
	public static final Locale DEFAULT_LOCALE = Locale.getDefault();
	/**
	 * The constant DEFAULT_SYSTEM_CHARSET.
	 */
	public static final String DEFAULT_SYSTEM_CHARSET = System.getProperty("file.encoding");
	/**
	 * The constant DEFAULT_SPLIT_SEPARATOR.
	 */
	public static final String DEFAULT_SPLIT_SEPARATOR = ",";
	/**
	 * The constant DEFAULT_PAGE_SEPARATOR.
	 */
	public static final String DEFAULT_PAGE_SEPARATOR = File.separator;
	/**
	 * The constant DEFAULT_URL_SEPARATOR.
	 */
	public static final String DEFAULT_URL_SEPARATOR = "/";
	/**
	 * The constant DEFAULT_JAR_PAGE_SEPARATOR.
	 */
	public static final String DEFAULT_JAR_PAGE_SEPARATOR = "\\";
	/**
	 * The constant DEFAULT_ZIP_PAGE_SEPARATOR.
	 */
	public static final String DEFAULT_ZIP_PAGE_SEPARATOR = "/";

	/**
	 * The constant WEB_INF_FOLDER.
	 */
	public static final String WEB_INF_FOLDER = Globals.DEFAULT_PAGE_SEPARATOR + "WEB-INF";

	/**
	 * The constant DEFAULT_EMAIL_CONTENT_TYPE_TEXT.
	 */
	public static final String DEFAULT_EMAIL_CONTENT_TYPE_TEXT = "text/plain";
	/**
	 * The constant DEFAULT_EMAIL_CONTENT_TYPE_HTML.
	 */
	public static final String DEFAULT_EMAIL_CONTENT_TYPE_HTML = "text/html";
	/**
	 * The constant DEFAULT_EMAIL_CONTENT_TYPE_MULTIPART.
	 */
	public static final String DEFAULT_EMAIL_CONTENT_TYPE_MULTIPART = "multipart/*";
	/**
	 * The constant DEFAULT_EMAIL_CONTENT_TYPE_MESSAGE_RFC822.
	 */
	public static final String DEFAULT_EMAIL_CONTENT_TYPE_MESSAGE_RFC822 = "message/rfc822";

	/**
	 * The constant NERVOUSYNC_STATUS_FALSE.
	 */
	public static final int NERVOUSYNC_STATUS_FALSE = 0;
	/**
	 * The constant NERVOUSYNC_STATUS_TRUE.
	 */
	public static final int NERVOUSYNC_STATUS_TRUE = 1;

	/**
	 * The constant INITIALIZE_INT_VALUE.
	 */
	public static final int INITIALIZE_INT_VALUE = 0;

	private Globals() {
	}
}
