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
package com.nervousync.commons.zip.models;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Nov 28, 2017 4:43:23 PM $
 */
public final class ExtraDataRecord {

	private long header;
	private int dataSize;
	private byte[] dataContent;
	
	/**
	 * @return the header
	 */
	public long getHeader() {
		return header;
	}

	/**
	 * @param header the header to set
	 */
	public void setHeader(long header) {
		this.header = header;
	}

	/**
	 * @return the dataSize
	 */
	public int getDataSize() {
		return dataSize;
	}

	/**
	 * @param dataSize the dataSize to set
	 */
	public void setDataSize(int dataSize) {
		this.dataSize = dataSize;
	}

	/**
	 * @return the dataContent
	 */
	public byte[] getDataContent() {
		return dataContent == null ? new byte[0] : dataContent.clone();
	}

	/**
	 * @param dataContent the dataContent to set
	 */
	public void setDataContent(byte[] dataContent) {
		this.dataContent = dataContent == null ? new byte[0] : dataContent.clone();
	}
}
