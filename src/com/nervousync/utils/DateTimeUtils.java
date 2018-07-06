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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.nervousync.commons.core.Globals;

/**
 * Date time utils
 * 
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Jan 13, 2010 11:15:20 AM $
 */
public final class DateTimeUtils {

	public static final String DEFAULT_DATE_PATTERN = "yyyy/MM/dd";
	public static final String DEFAULT_DATETIME_PATTERN_ISO8601 = "yyyy-MM-dd'T'HH:mm:ss";
	public static final String COOKIE_DATETIME_PATTERN = "EEE, dd-MMM-yyyy HH:mm:ss 'GMT'";
	
	private DateTimeUtils() {
		
	}
	
	/**
	 * Formats given date according to string with ISO8601 format
	 * @param date		date instance
	 * @return			formated result by ISO8601
	 */
	public static String formatDateForSitemap(Date date) {
		if (date == null) {
			return null;
		}
		return formatDate(date, DEFAULT_DATETIME_PATTERN_ISO8601 + DateTimeUtils.getTimeZone());
	}
	
	/**
	 * Parses given string according to <code>java.util.Date</code> with ISO8601 format
	 * @param string			String value with ISO8601 format
	 * @return					Date object
	 * @throws ParseException	given string is null
	 */
	public static Date parseSitemapDate(String string) throws ParseException {
		return parseDate(string, DEFAULT_DATETIME_PATTERN_ISO8601 + DateTimeUtils.getTimeZone());
	}

	/**
	 * Formats given date according to string with vCard format
	 * @param date		date instance
	 * @return			formated result by ISO8601
	 */
	public static String formatDateForVCard(Date date) {
		if (date == null) {
			return null;
		}
		return formatDate(date, DEFAULT_DATETIME_PATTERN_ISO8601) + "Z";
	}
	
	/**
	 * Parses given cookie expire string according to java.util.Date
	 * @param string			string will be parsed
	 * @return					Date object
	 * @throws ParseException	given string is null
	 */
	public static Date parseGMTDate(String string) throws ParseException {
		return parseDate(string, COOKIE_DATETIME_PATTERN);
	}
	
	/**
	 * Parses given string according to format style
	 * @param string			string will be parsed
	 * @param format			Date format
	 * @return					Date object
	 * @throws ParseException	given string is null or format was not matched
	 */
	public static Date parseDate(String string, String format) throws ParseException {
		if (string == null || string.length() == 0) {
			throw new ParseException("Date string is null", 0);
		}
		
		if (format == null) {
			format = DEFAULT_DATE_PATTERN;
		}
		Date date = new SimpleDateFormat(format).parse(string);
		
		return date;
	}

	/**
	 * Formats given date according to system style
	 * @param date		date instance
	 * @return			formated result by given format
	 */
	public static String formatDate(Date date) {
		return formatDate(date, "");
	}
	
	/**
	 * Return current day value with format "yyyyMMdd"
	 * @return		current day
	 */
	public static int currentDay() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		return Integer.valueOf(simpleDateFormat.format(new Date())).intValue();
	}

	/**
	 * Return current GMT day value with format "yyyyMMdd"
	 * @return		current GMT day
	 */
	public static int currentGMTDay() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		return Integer.valueOf(simpleDateFormat.format(new Date())).intValue();
	}
	
	/**
	 * Return expire day value with format "yyyyMMdd"
	 * @param expireTime expire time
	 * @return		expire day
	 */
	public static int expireDay(long expireTime) {
		long expireDate = System.currentTimeMillis() + expireTime;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		return Integer.valueOf(simpleDateFormat.format(new Date(expireDate))).intValue();
	}

	/**
	 * Return expire day value with format "yyyyMMdd"
	 * @param expireTime expire time
	 * @return		expire GMT day
	 */
	public static int expireGMTDay(long expireTime) {
		long expireDate = DateTimeUtils.currentGMTTimeMillis() + expireTime;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		return Integer.valueOf(simpleDateFormat.format(new Date(expireDate))).intValue();
	}
	
	/**
	 * Converts input time from Java to DOS format
	 * @param time		time value
	 * @return time in DOS format 
	 */
	public static long toDosTime(long time) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time);
		
		int year = cal.get(Calendar.YEAR);
		if (year < 1980) {
		    return (1 << 21) | (1 << 16);
		}
		return (year - 1980) << 25 | (cal.get(Calendar.MONTH) + 1) << 21 |
	               cal.get(Calendar.DATE) << 16 | cal.get(Calendar.HOUR_OF_DAY) << 11 | cal.get(Calendar.MINUTE) << 5 |
	               cal.get(Calendar.SECOND) >> 1;
	}
	
	/**
	 * Converts time in dos format to Java format
	 * @param dosTime		dos time
	 * @return time in java format
	 */
	public static long dosToJavaTme(int dosTime) {
		int sec = 2 * (dosTime & 0x1f);
	    int min = (dosTime >> 5) & 0x3f;
	    int hrs = (dosTime >> 11) & 0x1f;
	    int day = (dosTime >> 16) & 0x1f;
	    int mon = ((dosTime >> 21) & 0xf) - 1;
	    int year = ((dosTime >> 25) & 0x7f) + 1980;
	    
	    Calendar cal = Calendar.getInstance();
		cal.set(year, mon, day, hrs, min, sec);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime().getTime();
	}
	
	/**
	 * Return current time value with format "yyyyMMddHHmm"
	 * @return		current time with format "yyyyMMddHHmm"
	 */
	public static long currentTime() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmm");
		return Long.valueOf(simpleDateFormat.format(new Date())).longValue();
	}

	/**
	 * Return current time in milliseconds.
	 * @return		current time in milliseconds.
	 */
	public static long currentTimeMillis() {
		return System.currentTimeMillis();
	}
	
	/**
	 * Return current GMT time value with format "yyyyMMddHHmm"
	 * @return		current GMT time value with format "yyyyMMddHHmm"
	 */
	public static long currentGMTTime() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmm");
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		return Long.valueOf(simpleDateFormat.format(new Date())).longValue();
	}
	
	/**
	 * Returns the GMT time in milliseconds.
	 * @return		current GMT time in milliseconds.
	 */
	public static long currentGMTTimeMillis() {
		long currentTime = System.currentTimeMillis();
		return currentTime - TimeZone.getDefault().getRawOffset();
	}

	/**
	 * Formats given date according to format style
	 * @param date			Date instance
	 * @param format		String format
	 * @return				Format date value as string
	 */
	public static String formatDate(Date date, String format) {
		return DateTimeUtils.formatDate(date, format, TimeZone.getDefault());
	}
	
	/**
	 * Formats given date according to format style
	 * @param date			Date instance
	 * @param format		String format
	 * @param timeZone		Time zone
	 * @return				Time value of String
	 */
	public static String formatDate(Date date, String format, TimeZone timeZone) {
		if (date == null) {
			date = new Date();
		}
		SimpleDateFormat simpleDateFormat = null;
		if (format == null || format.length() == 0) {
			simpleDateFormat = new SimpleDateFormat();
		} else {
			simpleDateFormat = new SimpleDateFormat(format);
		}
		
		if (timeZone != null) {
			simpleDateFormat.setTimeZone(timeZone);
		}
		
		return simpleDateFormat.format(date);
	}
	
	/**
	 * Formats given date according to specified locale and date style
	 *
	 * @param date	  Date to convert
	 * @param locale	Locale to use for formatting date
	 * @param dateStyle Date style
	 * @return String representation of date according to given locale and date style
	 * @see java.text.DateFormat
	 */
	public static String formatDate(Date date, Locale locale, int dateStyle) {
		DateFormat formatter = DateFormat.getDateInstance(dateStyle, locale);
		return formatter.format(date);
	}

	/**
	 * Formats given date according to specified locale and <code>DateFormat.MEDIUM</code> style
	 *
	 * @param date   Date to convert
	 * @param locale Locale to use for formatting date
	 * @return String representation of date according to given locale and <code>DateFormat.MEDIUM</code> style
	 * @see java.text.DateFormat
	 * @see java.text.DateFormat#MEDIUM
	 */
	public static String formatDate(Date date, Locale locale) {
		return formatDate(date, locale, DateFormat.MEDIUM);
	}

	/**
	 * Parses given string according to specified locale and date style
	 *
	 * @param source	Source string to parse date from
	 * @param locale	Locale to use for parsing date
	 * @param dateStyle Date style
	 * @return Date object corresponding to representation given in source string
	 * @throws ParseException if given string could not be properly parsed according to given locale and style
	 * @see java.text.DateFormat
	 */
	public static Date parseDate(String source, Locale locale, int dateStyle) throws ParseException {
		DateFormat formatter = DateFormat.getDateInstance(dateStyle, locale);
		return formatter.parse(source);
	}

	/**
	 * Parses given string according to specified locale and <code>DateFormat.MEDIUM</code> style
	 *
	 * @param source Source string to parse date from
	 * @param locale Locale to use for parsing date
	 * @return Date object corresponding to representation given in source string
	 * @throws ParseException if given string could not be properly parsed according to given locale and <code>DateFormat.MEDIUM</code> style
	 * @see java.text.DateFormat
	 * @see java.text.DateFormat#MEDIUM
	 */
	public static Date parseDate(String source, Locale locale) throws ParseException {
		return parseDate(source, locale, DateFormat.MEDIUM);
	}


	/**
	 * Formats given time according to specified locale and time style
	 *
	 * @param time	  Time to convert
	 * @param locale	Locale to use for formatting time
	 * @param timeStyle Time style
	 * @return String representation of time according to given locale and time style
	 * @see java.text.DateFormat
	 */
	public static String formatTime(Date time, Locale locale, int timeStyle) {
		DateFormat formatter = DateFormat.getTimeInstance(timeStyle, locale);
		return formatter.format(time);
	}

	/**
	 * Formats given time according to specified locale and <code>DateFormat.MEDIUM</code> style
	 *
	 * @param time   Time to convert
	 * @param locale Locale to use for formatting time
	 * @return String representation of time according to given locale and <code>DateFormat.MEDIUM</code> style
	 * @see java.text.DateFormat
	 * @see java.text.DateFormat#MEDIUM
	 */
	public static String formatTime(Date time, Locale locale) {
		return formatTime(time, locale, DateFormat.MEDIUM);
	}
	
	/**
	 * Parses given string according to specified locale and time style
	 *
	 * @param source	Source string to parse time from
	 * @param locale	Locale to use for parsing time
	 * @param timeStyle Time style
	 * @return Time object corresponding to representation given in source string
	 * @throws ParseException if given string could not be properly parsed according to given locale and style
	 * @see java.text.DateFormat
	 */
	public static Date parseTime(String source, Locale locale, int timeStyle) throws ParseException {
		DateFormat formatter = DateFormat.getTimeInstance(timeStyle, locale);
		return formatter.parse(source);
	}

	/**
	 * Parses given string according to specified locale and <code>DateFormat.MEDIUM</code> style
	 *
	 * @param source Source string to parse time from
	 * @param locale Locale to use for parsing time
	 * @return Time object corresponding to representation given in source string
	 * @throws ParseException if given string could not be properly parsed according to given locale and <code>DateFormat.MEDIUM</code> style
	 * @see java.text.DateFormat
	 * @see java.text.DateFormat#MEDIUM
	 */
	public static Date parseTime(String source, Locale locale) throws ParseException {
		return parseTime(source, locale, DateFormat.MEDIUM);
	}

	/**
	 * Formats given date and time according to specified locale and date style
	 *
	 * @param date	  Date object to convert
	 * @param locale	Locale to use for formatting date and time
	 * @param dateStyle Date style
	 * @param timeStyle Time style
	 * @return String representation of date and time according to given locale and date style
	 * @see java.text.DateFormat
	 */
	public static String formatDateTime(Date date, Locale locale, int dateStyle, int timeStyle) {
		DateFormat formatter = DateFormat.getDateTimeInstance(dateStyle, timeStyle, locale);
		return formatter.format(date);
	}

	/**
	 * Formats given date and time according to specified locale and <code>DateFormat.MEDIUM</code> style
	 *
	 * @param date   Date object to convert
	 * @param locale Locale to use for formatting date and time
	 * @return String representation of date and time according to given locale and <code>DateFormat.MEDIUM</code> style
	 * @see java.text.DateFormat
	 * @see java.text.DateFormat#MEDIUM
	 */
	public static String formatDateTime(Date date, Locale locale) {
		return formatDateTime(date, locale, DateFormat.MEDIUM, DateFormat.MEDIUM);
	}

	/**
	 * Formats given date and time according to specified locale and <code>DateFormat.MEDIUM</code> style
	 *
	 * @param date   Date object to convert
	 * @return String representation of date and time according to <code>Globals.DEFAULT_LOCALE</code> and <code>DateFormat.MEDIUM</code> style
	 * @see java.text.DateFormat
	 * @see java.text.DateFormat#MEDIUM
	 */
	public static String formatDateTime(Date date) {
		return formatDateTime(date, Globals.DEFAULT_LOCALE);
	}

	/**
	 * Parses given string according to specified locale and date and time styles
	 *
	 * @param source	Source string to parse date and time from
	 * @param locale	Locale to use for parsing date and time
	 * @param dateStyle Date style
	 * @param timeStyle Time style
	 * @return Date object corresponding to representation given in source string
	 * @throws ParseException if given string could not be properly parsed according to given locale and style
	 * @see java.text.DateFormat
	 */
	public static Date parseDateTime(String source, Locale locale, int dateStyle, int timeStyle) throws ParseException {
		DateFormat formatter = DateFormat.getDateTimeInstance(dateStyle, timeStyle, locale);
		return formatter.parse(source);
	}

	/**
	 * Parses given string according to specified locale and <code>DateFormat.MEDIUM</code> style
	 *
	 * @param source Source string to parse date and time from
	 * @param locale Locale to use for parsing date and time
	 * @return Date object corresponding to representation given in source string
	 * @throws ParseException if given string could not be properly parsed according to given locale and <code>DateFormat.MEDIUM</code> style
	 * @see java.text.DateFormat
	 * @see java.text.DateFormat#MEDIUM
	 */
	public static Date parseDateTime(String source, Locale locale) throws ParseException {
		return parseDateTime(source, locale, DateFormat.MEDIUM, DateFormat.MEDIUM);
	}

	/**
	 * Parses given string according to specified locale and <code>DateFormat.MEDIUM</code> style
	 *
	 * @param source Source string to parse date and time from
	 * @return Date object corresponding to <code>Globals.DEFAULT_LOCALE</code> in source string
	 * @throws ParseException if given string could not be properly parsed according to given locale and <code>DateFormat.MEDIUM</code> style
	 * @see java.text.DateFormat
	 * @see java.text.DateFormat#MEDIUM
	 */
	public static Date parseDateTime(String source) throws ParseException {
		return parseDateTime(source, Globals.DEFAULT_LOCALE);
	}

	/**
	 * Formats given Date object according to specified locale and a given
	 * pattern.
	 *
	 * @param date   Date object to convert
	 * @param locale Locale to use for formatting
	 * @param pattern Pattern to use
	 * @return String representation of date and time according to given locale and <code>DateFormat.MEDIUM</code> style
	 * @see java.text.SimpleDateFormat
	 */
	public static String format(Date date, Locale locale, String pattern) {
		SimpleDateFormat formatter = new SimpleDateFormat(pattern, locale);
		return formatter.format(date);
	}

	/**
	 * Parses given string according to specified locale and a given pattern.
	 *
	 * @param source Source string to parse date and time from
	 * @param locale Locale to use for parsing date and time
	 * @param pattern Pattern to use
	 * @return Date object corresponding to representation given in source
	 * string
	 * @throws ParseException if given string could not be properly parsed
	 * according to given locale and pattern
	 * @see java.text.SimpleDateFormat
	 */
	public static Date parse(String source, Locale locale, String pattern)
			throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat(pattern, locale);
		return formatter.parse(source);
	}
	
	/**
	 * Check current year is leap year
	 * @return		check result
	 */
	public static boolean isLeapYear() {
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		
		if ((currentYear % 4 == 0 && currentYear % 100 != 0) 
				|| currentYear % 400 == 0) {
			return true;
		}
		
		return Globals.DEFAULT_VALUE_BOOLEAN;
	}

	public static String getCurrentMonthFirstDay(String format) {
		int firstDay = Calendar.getInstance().getActualMinimum(Calendar.DAY_OF_MONTH);
		return getCurrentDateByDay(format, firstDay);
	}

	public static String getCurrentMonthLastDay(String format) {
		int lastDay = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);
		return getCurrentDateByDay(format, lastDay);
	}
	
	private static String getCurrentDateByDay(String format, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(Calendar.DAY_OF_MONTH, day);
		return new SimpleDateFormat(format).format(calendar.getTime());
	}

	private static String getTimeZone() {
		String timeZone = "";
		Integer zone = TimeZone.getDefault().getRawOffset() / (1000 * 60 * 60);
		if (zone > 0) {
			timeZone = "+";
		}
		timeZone += zone + ":00";
		return timeZone;
	}
}
