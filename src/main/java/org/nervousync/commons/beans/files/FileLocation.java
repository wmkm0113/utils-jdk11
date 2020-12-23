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
package org.nervousync.commons.beans.files;

import org.nervousync.exceptions.file.FileProtocolNotSupportException;
import org.nervousync.commons.core.RegexGlobals;
import org.nervousync.utils.FileUtils;
import org.nervousync.utils.ObjectUtils;
import org.nervousync.utils.StringUtils;

import java.io.Serializable;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: 2018-12-22 12:48 $
 */
public final class FileLocation implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -4777811442178508666L;

	private final String locationPath;
	private final LocationType locationType;
	private final String domainName;
	private final String userName;
	private final String passWord;

	public FileLocation(String locationPath) throws FileProtocolNotSupportException {
		this(locationPath, null, null, null);
	}

	public FileLocation(String locationPath, String domainName, String userName, String passWord)
			throws FileProtocolNotSupportException {
		if (locationPath == null) {
			throw new FileProtocolNotSupportException("Location path is null");
		}

		if (locationPath.startsWith(FileUtils.SAMBA_URL_PREFIX)) {
			this.locationType = LocationType.SMB;
		} else if (StringUtils.matches(locationPath, RegexGlobals.LOCAL_FILE_PATH_REGEX)) {
			this.locationType = LocationType.LOCAL;
		} else {
			throw new FileProtocolNotSupportException("Unsupported file protocol! ");
		}
		this.locationPath = locationPath;
		this.domainName = domainName;
		this.userName = userName;
		this.passWord = passWord;
	}

	/**
	 * Gets the value of locationPath.
	 *
	 * @return the value of locationPath
	 */
	public String getLocationPath() {
		return locationPath;
	}

	/**
	 * Gets the value of locationType.
	 *
	 * @return the value of locationType
	 */
	public LocationType getLocationType() {
		return locationType;
	}

	/**
	 * Gets the value of domainName.
	 *
	 * @return the value of domainName
	 */
	public String getDomainName() {
		return domainName;
	}

	/**
	 * Gets the value of userName.
	 *
	 * @return the value of userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * Gets the value of passWord.
	 *
	 * @return the value of passWord
	 */
	public String getPassWord() {
		return passWord;
	}

	/**
	 * Gets the value of serialVersionUID.
	 *
	 * @return the value of serialVersionUID
	 */
	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FileLocation)) {
			return false;
		}
		FileLocation fileLocation = (FileLocation) other;
		return (ObjectUtils.nullSafeEquals(this.locationPath, fileLocation.getLocationPath())
				&& ObjectUtils.nullSafeEquals(this.locationType, fileLocation.getLocationType())
				&& ObjectUtils.nullSafeEquals(this.domainName, fileLocation.getDomainName())
				&& ObjectUtils.nullSafeEquals(this.userName, fileLocation.getUserName())
				&& ObjectUtils.nullSafeEquals(this.passWord, fileLocation.getPassWord()));
	}

	public int hashCode() {
		int result = 0;
		result = 31 * result + (this.locationPath != null ? this.locationPath.hashCode() : 0);
		result = 31 * result + this.locationType.hashCode();
		result = 31 * result + (this.domainName != null ? this.domainName.hashCode() : 0);
		result = 31 * result + (this.userName != null ? this.userName.hashCode() : 0);
		result = 31 * result + (this.passWord != null ? this.passWord.hashCode() : 0);
		return result;
	}

	public enum LocationType {
		LOCAL, SMB
	}
}
