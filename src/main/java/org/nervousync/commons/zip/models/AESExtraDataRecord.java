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
package org.nervousync.commons.zip.models;

import org.nervousync.commons.core.Globals;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Nov 28, 2017 4:50:18 PM $
 */
public final class AESExtraDataRecord {

	private long signature = Globals.DEFAULT_VALUE_LONG;
	private int dataSize = Globals.DEFAULT_VALUE_INT;
	private int versionNumber = Globals.DEFAULT_VALUE_INT;
	private String vendorID = null;
	private int aesStrength = Globals.DEFAULT_VALUE_INT;
	private int compressionMethod = Globals.DEFAULT_VALUE_INT;
	
	/**
	 * @return the signature
	 */
	public long getSignature() {
		return signature;
	}

	/**
	 * @param signature the signature to set
	 */
	public void setSignature(long signature) {
		this.signature = signature;
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
	 * @return the versionNumber
	 */
	public int getVersionNumber() {
		return versionNumber;
	}

	/**
	 * @param versionNumber the versionNumber to set
	 */
	public void setVersionNumber(int versionNumber) {
		this.versionNumber = versionNumber;
	}

	/**
	 * @return the vendorID
	 */
	public String getVendorID() {
		return vendorID;
	}

	/**
	 * @param vendorID the vendorID to set
	 */
	public void setVendorID(String vendorID) {
		this.vendorID = vendorID;
	}

	/**
	 * @return the aesStrength
	 */
	public int getAesStrength() {
		return aesStrength;
	}

	/**
	 * @param aesStrength the aesStrength to set
	 */
	public void setAesStrength(int aesStrength) {
		this.aesStrength = aesStrength;
	}

	/**
	 * @return the compressionMethod
	 */
	public int getCompressionMethod() {
		return compressionMethod;
	}

	/**
	 * @param compressionMethod the compressionMethod to set
	 */
	public void setCompressionMethod(int compressionMethod) {
		this.compressionMethod = compressionMethod;
	}
}
