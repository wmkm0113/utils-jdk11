/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervousync Studio.
 */
package com.nervousync.utils;

import com.nervousync.commons.beans.location.LocationPoint;
import com.nervousync.exceptions.location.LocationConvertException;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Dec 19, 2017 1:01:14 PM $
 */
public final class LocationUtils {
	
	private static final double X_PI = Math.PI * 3000.0 / 180.0;
	private static final double EARTH_R = 6378245.0;
	private static final double EARTH_EE = 0.00669342162296594323;
	
	/**
	 * Convert to GPS point
	 * @param currentPoint
	 * @return
	 * @throws LocationConvertException
	 */
	public static LocationPoint convertToGPS(LocationPoint currentPoint) throws LocationConvertException {
		if (currentPoint == null) {
			throw new LocationConvertException("Current point is null");
		}
		
		switch (currentPoint.getLocationType()) {
		case GPS:
			return currentPoint;
		case GCJ_02:
			return convertGCJ02ToGPS(currentPoint.getLongitude(), currentPoint.getLatitude());
		case BD_09:
			LocationPoint gcjPoint = convertBD09ToGCJ02(currentPoint.getLongitude(), currentPoint.getLatitude());
			return convertGCJ02ToGPS(gcjPoint.getLongitude(), gcjPoint.getLatitude());
			default:
				throw new LocationConvertException("Location type does not supported");
		}
	}

	/**
	 * Convert to GCJ-02 point
	 * @param currentPoint
	 * @return
	 * @throws LocationConvertException
	 */
	public static LocationPoint convertToGCJ02(LocationPoint currentPoint) throws LocationConvertException {
		if (currentPoint == null) {
			throw new LocationConvertException("Current point is null");
		}
		
		switch (currentPoint.getLocationType()) {
		case GPS:
			return convertGPSToGCJ02(currentPoint.getLongitude(), currentPoint.getLatitude());
		case GCJ_02:
			return currentPoint;
		case BD_09:
			return convertBD09ToGCJ02(currentPoint.getLongitude(), currentPoint.getLatitude());
			default:
				throw new LocationConvertException("Location type does not supported");
		}
	}

	/**
	 * Convert to BD-09 point
	 * @param currentPoint
	 * @return
	 * @throws LocationConvertException
	 */
	public static LocationPoint convertToBD09(LocationPoint currentPoint) throws LocationConvertException {
		if (currentPoint == null) {
			throw new LocationConvertException("Current point is null");
		}
		
		switch (currentPoint.getLocationType()) {
		case GPS:
			LocationPoint gcjPoint = convertGPSToGCJ02(currentPoint.getLongitude(), currentPoint.getLatitude());
			return convertGCJ02ToBD09(gcjPoint.getLongitude(), gcjPoint.getLatitude());
		case GCJ_02:
			return convertGCJ02ToBD09(currentPoint.getLongitude(), currentPoint.getLatitude());
		case BD_09:
			return currentPoint;
			default:
				throw new LocationConvertException("Location type does not supported");
		}
	}

	private static LocationPoint convertGCJ02ToBD09(double longitude, double latitude) {
		double fixValue = Math.sqrt(Math.pow(longitude, 2) + Math.pow(latitude, 2)) + 0.00002 * Math.sin(latitude * X_PI);
		double fixTemp = Math.atan2(latitude, longitude) + 0.000003 * Math.cos(longitude * X_PI);
		double bdLat = fixValue * Math.sin(fixTemp) + 0.006;
		double bdLon = fixValue * Math.cos(fixTemp) + 0.0065;
		return LocationPoint.bd09Point(bdLon, bdLat);
	}
	
	private static LocationPoint convertBD09ToGCJ02(double longitude, double latitude) {
		double fixValue = Math.sqrt(Math.pow(longitude - 0.0065, 2) + Math.pow(latitude - 0.006, 2)) 
				- 0.00002 * Math.sin((latitude - 0.006) * X_PI);
		double fixTemp = Math.atan2(latitude - 0.006, longitude - 0.0065) 
				- 0.000003 * Math.cos((longitude - 0.0065) * X_PI);
		double gcjLat = fixValue * Math.sin(fixTemp);
		double gcjLon = fixValue * Math.cos(fixTemp);
		return LocationPoint.gcj02Point(gcjLon, gcjLat);
	}
	
	private static LocationPoint convertGCJ02ToGPS(double longitude, double latitude) {
		double initDelta = 0.01;
		double threshold = 0.000000001;
		double mLat = latitude - initDelta;
		double mLon = longitude - initDelta;
		double pLat = latitude + initDelta;
		double pLon = longitude + initDelta;
		double gpsLat = 0.0;
		double gpsLon = 0.0;
		int index = 0;
		while (true) {
			gpsLat = (mLat + pLat) / 2;
			gpsLon = (mLon + pLon) / 2;
			
			LocationPoint fixedPoint = convertGPSToGCJ02(gpsLon, gpsLat);
			
			if ((Math.abs(fixedPoint.getLatitude() - latitude) < threshold) 
					&& (Math.abs(fixedPoint.getLongitude()) < threshold)) {
				break;
			}
			
			if (fixedPoint.getLatitude() - latitude > 0) {
				pLat = gpsLat;
			} else {
				mLat = gpsLat;
			}
			
			if (fixedPoint.getLongitude() - longitude > 0) {
				pLon = gpsLon;
			} else {
				mLon = gpsLon;
			}
			
			if (++index > 10000) {
				break;
			}
		}
		
		return LocationPoint.gpsPoint(gpsLon, gpsLat);
	}
	
	private static LocationPoint convertGPSToGCJ02(double longitude, double latitude) {
		if ((longitude < 72.004 || longitude > 137.8347) || (latitude < 0.8293 || latitude > 55.8271)) {
			return LocationPoint.gcj02Point(longitude, latitude);
		}
		double radiLati = latitude / 180.0 * Math.PI;
		double magic = 1 - EARTH_EE * Math.pow(Math.sin(radiLati), 2);
		double magicSqrt = Math.sqrt(magic);
		double fixedLatitude = ((transformLatidute(longitude - 105.0, latitude - 35.0) * 180.0) 
				/ ((EARTH_R * (1 - EARTH_EE)) / (magic * magicSqrt) * Math.PI));
		double fixedLongitude = ((transformLongidute(longitude - 105.0, latitude - 35.0) * 180.0) 
				/ (EARTH_R / magicSqrt * Math.cos(radiLati) * Math.PI));
		return LocationPoint.gcj02Point(longitude + fixedLongitude, latitude + fixedLatitude);
	}
	
	private static double transformLatidute(double longitude, double latitude) {
		double transformLatitude = -100.0 + 2.0 * longitude + 3.0 * latitude + 0.2 * Math.pow(latitude, 2);
		transformLatitude += 0.1 * longitude * latitude + 0.2 * Math.sqrt(Math.abs(longitude));
		transformLatitude += (20.0 * Math.sin(6.0 * longitude * Math.PI) + 20.0 * Math.sin(2.0 * longitude * Math.PI)) * 2.0 / 3.0;
		transformLatitude += (20.0 * Math.sin(latitude * Math.PI) + 40.0 * Math.sin(latitude / 3.0 * Math.PI)) * 2.0 / 3.0;
		transformLatitude += (160.0 * Math.sin(latitude / 12.0 * Math.PI) + 320.0 * Math.sin(latitude * Math.PI / 30.0)) * 2.0 / 3.0;
		return transformLatitude;
	}
	
	private static double transformLongidute(double longitude, double latitude) {
		double transformLongitude = 300.0 + longitude + 2.0 * latitude + 0.1 * Math.pow(longitude, 2);
		transformLongitude += 0.1 * longitude * latitude + 0.1 * Math.sqrt(Math.abs(longitude));
		transformLongitude += (20.0 * Math.sin(6.0 * longitude * Math.PI) + 20.0 * Math.sin(2.0 * longitude * Math.PI)) * 2.0 / 3.0;
		transformLongitude += (20.0 * Math.sin(longitude * Math.PI) + 40.0 * Math.sin(longitude / 3.0 * Math.PI)) * 2.0 / 3.0;
		transformLongitude += (150.0 * Math.sin(longitude / 12.0 * Math.PI) + 300.0 * Math.sin(longitude * Math.PI / 30.0)) * 2.0 / 3.0;
		return transformLongitude;
	}
}
