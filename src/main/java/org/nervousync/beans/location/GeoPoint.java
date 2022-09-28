/*
 * Copyright 2017 Nervousync Studio
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.nervousync.beans.location;

import java.io.Serializable;


/**
 * Location point define
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: Dec 19, 2017 1:03:40 PM $
 */
public final class GeoPoint implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3501428042311016856L;

	/**
	 * Define type of location
	 * @see GeoPoint.LocationType
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
	
	private GeoPoint(final LocationType locationType, final double longitude, final double latitude) {
		this.locationType = locationType;
		this.longitude = longitude;
		this.latitude = latitude;
	}

	/**
	 * Define a GPS location point
	 *
	 * @param longitude Location longitude value
	 * @param latitude  Location latitude value
	 * @return LocationPoint object
	 */
	public static GeoPoint gpsPoint(final double longitude, final double latitude) {
		return new GeoPoint(LocationType.GPS, longitude, latitude);
	}

	/**
	 * Define a GCJ02 location point
	 *
	 * @param longitude Location longitude value
	 * @param latitude  Location latitude value
	 * @return LocationPoint object
	 */
	public static GeoPoint gcj02Point(final double longitude, final double latitude) {
		return new GeoPoint(LocationType.GCJ_02, longitude, latitude);
	}

	/**
	 * Define a BD09 location point
	 *
	 * @param longitude Location longitude value
	 * @param latitude  Location latitude value
	 * @return LocationPoint object
	 */
	public static GeoPoint bd09Point(final double longitude, final double latitude) {
		return new GeoPoint(LocationType.BD_09, longitude, latitude);
	}

	/**
	 * Define a DELTA location point
	 *
	 * @param longitude Location longitude value
	 * @param latitude  Location latitude value
	 * @return LocationPoint object
	 */
	public static GeoPoint deltaPoint(final double longitude, final double latitude) {
		return new GeoPoint(LocationType.DELTA, longitude, latitude);
	}

	/**
	 * Gets location type.
	 *
	 * @return the locationType
	 */
	public LocationType getLocationType() {
		return locationType;
	}

	/**
	 * Gets longitude.
	 *
	 * @return the longitude
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * Gets latitude.
	 *
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude;
	}

	@Override
	public String toString() {
		return this.latitude + "," + this.longitude;
	}

	/**
	 * The enum Location type.
	 */
	public enum LocationType {
		/**
		 * Gps location type.
		 */
		GPS,
		/**
		 * Gcj 02 location type.
		 */
		GCJ_02,
		/**
		 * Bd 09 location type.
		 */
		BD_09,
		/**
		 * Delta location type.
		 */
		DELTA
	}
}
