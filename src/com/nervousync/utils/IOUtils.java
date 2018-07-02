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
package com.nervousync.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.nervousync.commons.core.Globals;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Jun 3, 2015 11:20:20 AM $
 */
public class IOUtils {
	
	private IOUtils() {
		
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
			try {
				if (inStream != null) {
					inStream.close();
				}
			} catch (IOException e) {
			}
            if (closeOutputAfterCopy) {
    			try {
    				if (outStream != null) {
    					outStream.close();
    				}
    			} catch (IOException e) {
    			}
            }
		}
	}
}
