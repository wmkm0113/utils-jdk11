/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervousync Studio.
 */
package com.nervousync.commons.beans.location;

import java.io.Serializable;


/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Dec 19, 2017 1:03:40 PM $
 */
public final class LocationPoint implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3501428042311016856L;

	private LocationType locationType;
	private double longitude;
	private double latitude;
	
	private LocationPoint(LocationType locationType, 
			double longitude, double latitude) {
		this.locationType = locationType;
		this.longitude = longitude;
		this.latitude = latitude;
	}
	
	public static LocationPoint gpsPoint(double longitude, double latitude) {
		return new LocationPoint(LocationType.GPS, longitude, latitude);
	}
	
	public static LocationPoint gcj02Point(double longitude, double latitude) {
		return new LocationPoint(LocationType.GCJ_02, longitude, latitude);
	}
	
	public static LocationPoint bd09Point(double longitude, double latitude) {
		return new LocationPoint(LocationType.BD_09, longitude, latitude);
	}

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

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	@Override
	public String toString() {
		return this.latitude + "," + this.longitude;
	}
	
	public static enum LocationType {
		GPS, GCJ_02, BD_09, DELTA
	}
}
