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
package com.nervousync.commons.core;

import java.io.File;
import java.util.Locale;

/**
 * Globals Constants Value
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Jul 2, 2018 $
 */
public final class Globals {

	public static final String READ_MODE = "r";
	public static final String WRITE_MODE = "rw";
	
	public static final int DEFAULT_BUFFER_SIZE = 8192;
	
	public static final int DEFAULT_TIME_OUT = 0;
	public static final int DEFAULT_DOWNLOAD_SINGLE_THREAD = 1;
	public static final int DEFAULT_DOWNLOAD_THREAD_COUNT = 5;
	public static final int DEFAULT_DOWNLOAD_BLOCK_SIZE = 512;
	
	public static final int DEFAULT_DOWNLOAD_STATUS_WAITING = 0;
	public static final int DEFAULT_DOWNLOAD_STATUS_PROCESSING = 1;
	public static final int DEFAULT_DOWNLOAD_STATUS_PAUSE = 2;
	public static final int DEFAULT_DOWNLOAD_STATUS_FAILED = 3;
	public static final int DEFAULT_DOWNLOAD_STATUS_FINISHED = 4;
	public static final int DEFAULT_DOWNLOAD_STATUS_DELETE = 5;
	public static final int DEFAULT_DOWNLOAD_STATUS_VALIDATE_FAILED = 6;
	public static final int DEFAULT_DOWNLOAD_STATUS_CANCEL = 7;
	public static final int DEFAULT_DOWNLOAD_STATUS_RETRY = 8;

	public static final int DEFAULT_VALUE_INT = -1;
	public static final long DEFAULT_VALUE_LONG = -1L;
	public static final short DEFAULT_VALUE_SHORT = -1;
	public static final double DEFAULT_VALUE_DOUBLE = -1;
	public static final float DEFAULT_VALUE_FLOAT = -1;
	public static final boolean DEFAULT_VALUE_BOOLEAN = false;
	public static final String DEFAULT_VALUE_STRING = "";
	
	public static final String DEFAULT_EMAIL_FOLDER_INBOX = "INBOX";
	public static final String DEFAULT_ENCODING = "UTF-8";
	public static final Locale DEFAULT_LOCALE = Locale.getDefault();
	public static final String DEFAULT_LANGUAGE = 
			System.getProperty("user.language")	+ "_" + System.getProperty("user.country");
	public static final String DEFAULT_SYSTEM_CHARSET = System.getProperty("file.encoding");
	public static final String DEFAULT_PAGE_SEPARATOR = File.separator;
	public static final String DEFAULT_URL_SEPARATOR = "/";
	public static final String DEFAULT_JAR_PAGE_SEPARATOR = "\\";
	public static final String DEFAULT_ZIP_PAGE_SEPARATOR = "/";

	public static final String DEFAULT_DOWNLOAD_CONFIGURE = "nervousync_download.conf";
	
	public static final String DEFAULT_EMAIL_CONTENT_TYPE_TEXT = "text/plain";
	public static final String DEFAULT_EMAIL_CONTENT_TYPE_HTML = "text/html";
	public static final String DEFAULT_EMAIL_CONTENT_TYPE_MULTIPART = "multipart/*";
	public static final String DEFAULT_EMAIL_CONTENT_TYPE_MESSAGE_RFC822 = "message/rfc822";

	public static final String DEFAULT_PROTOCOL_PREFIX_FTP = "http://";
	public static final String DEFAULT_PROTOCOL_PREFIX_HTTP = "http://";
	public static final String DEFAULT_PROTOCOL_PREFIX_HTTPS = "https://";
	
	public static final int NERVOUSYNC_STATUS_FALSE = 0;
	public static final int NERVOUSYNC_STATUS_TRUE = 1;
	
	public static final int INITIALIZE_INT_VALUE = 0;

	/* SNMP Version Code Define */
	public static final int SNMP_VERSION_1 = 0;
	public static final int SNMP_VERSION_2C = 1;
	public static final int SNMP_VERSION_3 = 2;
}
