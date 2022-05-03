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
package org.nervousync.zip.models.central;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Nov 28, 2017 4:32:41 PM $
 */
public final class Zip64EndCentralDirectoryLocator {

	private long signature;
	private int indexOfZip64EndOfCentralDirectoryRecord;
	private long offsetZip64EndOfCentralDirectoryRecord;
	private int totalNumberOfDiscs;

	public Zip64EndCentralDirectoryLocator() {
	}
	
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
	 * @return the indexOfZip64EndOfCentralDirectoryRecord
	 */
	public int getIndexOfZip64EndOfCentralDirectoryRecord() {
		return indexOfZip64EndOfCentralDirectoryRecord;
	}

	/**
	 * @param indexOfZip64EndOfCentralDirectoryRecord the indexOfZip64EndOfCentralDirectoryRecord to set
	 */
	public void setIndexOfZip64EndOfCentralDirectoryRecord(int indexOfZip64EndOfCentralDirectoryRecord) {
		this.indexOfZip64EndOfCentralDirectoryRecord = indexOfZip64EndOfCentralDirectoryRecord;
	}

	/**
	 * @return the offsetZip64EndOfCentralDirectoryRecord
	 */
	public long getOffsetZip64EndOfCentralDirectoryRecord() {
		return offsetZip64EndOfCentralDirectoryRecord;
	}

	/**
	 * @param offsetZip64EndOfCentralDirectoryRecord the offsetZip64EndOfCentralDirectoryRecord to set
	 */
	public void setOffsetZip64EndOfCentralDirectoryRecord(long offsetZip64EndOfCentralDirectoryRecord) {
		this.offsetZip64EndOfCentralDirectoryRecord = offsetZip64EndOfCentralDirectoryRecord;
	}

	/**
	 * @return the totalNumberOfDiscs
	 */
	public int getTotalNumberOfDiscs() {
		return totalNumberOfDiscs;
	}

	/**
	 * @param totalNumberOfDiscs the totalNumberOfDiscs to set
	 */
	public void setTotalNumberOfDiscs(int totalNumberOfDiscs) {
		this.totalNumberOfDiscs = totalNumberOfDiscs;
	}
}
