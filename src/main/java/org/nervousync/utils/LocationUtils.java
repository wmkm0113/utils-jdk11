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
package org.nervousync.utils;

import org.nervousync.beans.location.GeoPoint;
import org.nervousync.exceptions.location.LocationConvertException;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Dec 19, 2017 1:01:14 PM $
 */
public final class LocationUtils {

	private static final double X_PI = Math.PI * 3000.0 / 180.0;
	private static final double EARTH_R = 6378137.0;
	private static final double EARTH_EE = 0.00669342162296594323;

	/**
	 * Calc distance of begin point and end point(Unit: Kilometers)
	 * @param beginPoint    Begin point
	 * @param endPoint      End point
	 * @return              distance
	 * @throws LocationConvertException     convert point to GPS failed
	 */
	public static double calcDistance(GeoPoint beginPoint, GeoPoint endPoint)
			throws LocationConvertException {
		GeoPoint beginGPSPoint = convertToGPS(beginPoint);
		GeoPoint endGPSPoint = convertToGPS(endPoint);

		double tmpX = (endGPSPoint.getLongitude() - beginGPSPoint.getLongitude()) * Math.PI * EARTH_R
				* Math.cos(((beginGPSPoint.getLatitude() + endGPSPoint.getLatitude()) / 2) * Math.PI / 180) / 180;
		double tmpY = (endGPSPoint.getLatitude() - beginGPSPoint.getLatitude()) * Math.PI * EARTH_R / 180;
		return Math.hypot(tmpX, tmpY);

	}
	
	/**
	 * Convert to GPS point
	 * @param currentPoint		current location point object
	 * @return					convert location point object
	 * @throws LocationConvertException  location type is invalid
	 */
	public static GeoPoint convertToGPS(GeoPoint currentPoint) throws LocationConvertException {
		if (currentPoint == null) {
			throw new LocationConvertException("Current point is null");
		}
		
		switch (currentPoint.getLocationType()) {
		case GPS:
			return currentPoint;
		case GCJ_02:
			return convertGCJ02ToGPS(currentPoint.getLongitude(), currentPoint.getLatitude());
		case BD_09:
			GeoPoint gcjPoint = convertBD09ToGCJ02(currentPoint.getLongitude(), currentPoint.getLatitude());
			return convertGCJ02ToGPS(gcjPoint.getLongitude(), gcjPoint.getLatitude());
			default:
				throw new LocationConvertException("Location type does not supported");
		}
	}

	/**
	 * Convert to GCJ-02 point
	 * @param currentPoint		current location point object
	 * @return					convert location point object
	 * @throws LocationConvertException  location type is invalid
	 */
	public static GeoPoint convertToGCJ02(GeoPoint currentPoint) throws LocationConvertException {
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
	 * @param currentPoint		current location point object
	 * @return					convert location point object
	 * @throws LocationConvertException  location type is invalid
	 */
	public static GeoPoint convertToBD09(GeoPoint currentPoint) throws LocationConvertException {
		if (currentPoint == null) {
			throw new LocationConvertException("Current point is null");
		}
		
		switch (currentPoint.getLocationType()) {
		case GPS:
			GeoPoint gcjPoint = convertGPSToGCJ02(currentPoint.getLongitude(), currentPoint.getLatitude());
			return convertGCJ02ToBD09(gcjPoint.getLongitude(), gcjPoint.getLatitude());
		case GCJ_02:
			return convertGCJ02ToBD09(currentPoint.getLongitude(), currentPoint.getLatitude());
		case BD_09:
			return currentPoint;
			default:
				throw new LocationConvertException("Location type does not supported");
		}
	}

	/**
	 * Convert GCJ02 to BD09
	 * @param longitude     longitude value of GCJ02
	 * @param latitude      latitude value of GCJ02
	 * @return              Location point
	 */
	private static GeoPoint convertGCJ02ToBD09(double longitude, double latitude) {
		double fixValue = Math.sqrt(Math.pow(longitude, 2) + Math.pow(latitude, 2)) + 0.00002 * Math.sin(latitude * X_PI);
		double fixTemp = Math.atan2(latitude, longitude) + 0.000003 * Math.cos(longitude * X_PI);
		double bdLat = fixValue * Math.sin(fixTemp) + 0.006;
		double bdLon = fixValue * Math.cos(fixTemp) + 0.0065;
		return GeoPoint.bd09Point(bdLon, bdLat);
	}

	/**
	 * Convert BD09 to GCJ02
	 * @param longitude     longitude value of BD09
	 * @param latitude      latitude value of BD09
	 * @return              Location point
	 */
	private static GeoPoint convertBD09ToGCJ02(double longitude, double latitude) {
		double fixValue = Math.sqrt(Math.pow(longitude - 0.0065, 2) + Math.pow(latitude - 0.006, 2)) 
				- 0.00002 * Math.sin((latitude - 0.006) * X_PI);
		double fixTemp = Math.atan2(latitude - 0.006, longitude - 0.0065) 
				- 0.000003 * Math.cos((longitude - 0.0065) * X_PI);
		double gcjLat = fixValue * Math.sin(fixTemp);
		double gcjLon = fixValue * Math.cos(fixTemp);
		return GeoPoint.gcj02Point(gcjLon, gcjLat);
	}

	/**
	 * Convert GCJ02 to GPS
	 * @param longitude     longitude value of GCJ02
	 * @param latitude      latitude value of GCJ02
	 * @return              Location point
	 */
	private static GeoPoint convertGCJ02ToGPS(double longitude, double latitude) {
		if ((longitude < 72.004 || longitude > 137.8347) || (latitude < 0.8293 || latitude > 55.8271)) {
			return GeoPoint.gpsPoint(longitude, latitude);
		} else {
			GeoPoint deltaPoint = deltaPoint(longitude, latitude);
			return GeoPoint.gpsPoint(longitude - deltaPoint.getLongitude(),
					latitude - deltaPoint.getLatitude());
		}
	}

	/**
	 * Convert GPS to GCJ02
	 * @param longitude     longitude value of GPS
	 * @param latitude      latitude value of GPS
	 * @return              Location point
	 */
	private static GeoPoint convertGPSToGCJ02(double longitude, double latitude) {
		if ((longitude < 72.004 || longitude > 137.8347) || (latitude < 0.8293 || latitude > 55.8271)) {
			return GeoPoint.gcj02Point(longitude, latitude);
		}
		GeoPoint deltaPoint = deltaPoint(longitude, latitude);
		return GeoPoint.gcj02Point(longitude + deltaPoint.getLongitude(),
				latitude + deltaPoint.getLatitude());
	}
	
	private static GeoPoint deltaPoint(double longitude, double latitude) {
		double transformLatitude = latitude / 180.0 * Math.PI;
		double magic = 1 - EARTH_EE * Math.pow(Math.sin(transformLatitude), 2);
		double magicSqrt = Math.sqrt(magic);
		double fixedLatitude = ((transformLatitude(longitude - 105.0, latitude - 35.0) * 180.0)
				/ ((EARTH_R * (1 - EARTH_EE)) / (magic * magicSqrt) * Math.PI));
		double fixedLongitude = ((transformLongitude(longitude - 105.0, latitude - 35.0) * 180.0)
				/ (EARTH_R / magicSqrt * Math.cos(transformLatitude) * Math.PI));
		return GeoPoint.deltaPoint(fixedLongitude, fixedLatitude);
	}
	
	private static double transformLatitude(double longitude, double latitude) {
		double transformLatitude = -100.0 + 2.0 * longitude + 3.0 * latitude + 0.2 * Math.pow(latitude, 2);
		transformLatitude += 0.1 * longitude * latitude + 0.2 * Math.sqrt(Math.abs(longitude));
		transformLatitude += calcFirst(longitude);
		transformLatitude += calcLast(latitude);
		transformLatitude += (160.0 * Math.sin(latitude / 12.0 * Math.PI) + 320.0 * Math.sin(latitude * Math.PI / 30.0)) * 2.0 / 3.0;
		return transformLatitude;
	}
	
	private static double transformLongitude(double longitude, double latitude) {
		double transformLongitude = 300.0 + longitude + 2.0 * latitude + 0.1 * Math.pow(longitude, 2);
		transformLongitude += 0.1 * longitude * latitude + 0.1 * Math.sqrt(Math.abs(longitude));
		transformLongitude += calcFirst(longitude);
		transformLongitude += calcLast(longitude);
		transformLongitude += (150.0 * Math.sin(longitude / 12.0 * Math.PI) + 300.0 * Math.sin(longitude * Math.PI / 30.0)) * 2.0 / 3.0;
		return transformLongitude;
	}

	private static double calcFirst(double value) {
		return (20.0 * Math.sin(6.0 * value * Math.PI) + 20.0 * Math.sin(2.0 * value * Math.PI)) * 2.0 / 3.0;
	}

	private static double calcLast(double value) {
		return (20.0 * Math.sin(value * Math.PI) + 40.0 * Math.sin(value / 3.0 * Math.PI)) * 2.0 / 3.0;
	}
}
