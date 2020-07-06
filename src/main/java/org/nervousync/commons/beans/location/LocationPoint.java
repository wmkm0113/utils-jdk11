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
package org.nervousync.commons.beans.location;

import java.io.Serializable;


/**
 * Location point define
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Dec 19, 2017 1:03:40 PM $
 */
public final class LocationPoint implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3501428042311016856L;

	/**
	 * Define type of location
	 * @see LocationPoint.LocationType
	 */
	private final LocationType locationType;
	/**
	 * Location longitude value
	 */
	private final double longitude;
	/**
	 * Location latitude value
	 */
	private final double latitude;
	
	private LocationPoint(LocationType locationType, 
			double longitude, double latitude) {
		this.locationType = locationType;
		this.longitude = longitude;
		this.latitude = latitude;
	}
	
	/**
	 * Define a GPS location point
	 * @param longitude		Location longitude value
	 * @param latitude		Location latitude value
	 * @return				LocationPoint object
	 */
	public static LocationPoint gpsPoint(double longitude, double latitude) {
		return new LocationPoint(LocationType.GPS, longitude, latitude);
	}
	
	/**
	 * Define a GCJ02 location point
	 * @param longitude		Location longitude value
	 * @param latitude		Location latitude value
	 * @return				LocationPoint object
	 */
	public static LocationPoint gcj02Point(double longitude, double latitude) {
		return new LocationPoint(LocationType.GCJ_02, longitude, latitude);
	}
	
	/**
	 * Define a BD09 location point
	 * @param longitude		Location longitude value
	 * @param latitude		Location latitude value
	 * @return				LocationPoint object
	 */
	public static LocationPoint bd09Point(double longitude, double latitude) {
		return new LocationPoint(LocationType.BD_09, longitude, latitude);
	}

	/**
	 * Define a DELTA location point
	 * @param longitude		Location longitude value
	 * @param latitude		Location latitude value
	 * @return				LocationPoint object
	 */
	public static LocationPoint deltaPoint(double longitude, double latitude) {
		return new LocationPoint(LocationType.DELTA, longitude, latitude);
	}
	
	/**
	 * @return the locationType
	 */
	public LocationType getLocationType() {
		return locationType;
	}

	/**
	 * @return the longitude
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude;
	}

	@Override
	public String toString() {
		return this.latitude + "," + this.longitude;
	}
	
	public enum LocationType {
		GPS, GCJ_02, BD_09, DELTA
	}
}
