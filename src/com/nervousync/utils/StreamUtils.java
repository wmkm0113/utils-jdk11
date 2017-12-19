/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervousync Studio.
 */
package com.nervousync.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

import com.nervousync.commons.core.Globals;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Jun 3, 2015 11:20:20 AM $
 */
public class StreamUtils {
	
	private StreamUtils() {
		
	}

	public static long copyStream(InputStream inputStream, 
			OutputStream outputStream, boolean closeOutputAfterCopy) throws IOException {
		return copyStream(inputStream, outputStream, 
				closeOutputAfterCopy, new byte[Globals.DEFAULT_BUFFER_SIZE]);
	}
	
	public static long copyStream(InputStream inputStream, OutputStream outputStream, 
			boolean closeOutputAfterCopy, byte[] buffer) throws IOException {
		if (inputStream == null) {
			return 0L;
		}
		
		InputStream inStream = inputStream;
		OutputStream outStream = outputStream;
		
		try {
			long totalCount = 0L;
			
			while (true) {
				int readCount = inStream.read(buffer);
				
				if (readCount == -1) {
					break;
				}
				
				if (readCount > 0) {
					totalCount += readCount;
					if (outStream != null) {
						outStream.write(buffer, 0, readCount);
					}
				}
			}
			
			if (outStream != null) {
				if (closeOutputAfterCopy) {
					outStream.close();
				} else {
					outStream.flush();
				}
				
				outStream = null;
			}
			inStream.close();
			inStream = null;
			
			return totalCount;
		} finally {
            IOUtils.closeQuietly(inStream);
            if (closeOutputAfterCopy) {
                IOUtils.closeQuietly(outStream);
            }
		}
	}
}
