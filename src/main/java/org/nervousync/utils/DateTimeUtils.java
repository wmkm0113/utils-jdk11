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

import java.text.DateFormat;
import java.text.ParseException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.nervousync.commons.core.Globals;

/**
 * Date time utils
 * 
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Jan 13, 2010 11:15:20 AM $
 */
public final class DateTimeUtils {

	/**
	 * Static value for date format yyyy/MM/dd
	 */
	public static final String DEFAULT_DATE_PATTERN = "yyyy/MM/dd";
	/**
	 * Static value for date format yyyy-MM-dd'T'HH:mm:ss
	 */
	public static final String DEFAULT_DATETIME_PATTERN_ISO8601 = "yyyy-MM-dd'T'HH:mm:ss";
	/**
	 * Static value for date format EEE, dd-MMM-yyyy HH:mm:ss 'GMT'
	 */
	public static final String COOKIE_DATETIME_PATTERN = "EEE, dd-MMM-yyyy HH:mm:ss 'GMT'";

	/**
	 * Static DateTimeFormatter instance for date format yyyy/MM/dd
	 */
	public static final DateTimeFormatter DEFAULT_ISO8601_PATTERN =
			DateTimeFormatter.ofPattern(DEFAULT_DATETIME_PATTERN_ISO8601);
	/**
	 * Static DateTimeFormatter instance for site map
	 */
	public static final DateTimeFormatter DEFAULT_SITE_MAP_PATTERN =
			DateTimeFormatter.ofPattern(DEFAULT_DATETIME_PATTERN_ISO8601 + DateTimeUtils.getTimeZone());
	/**
	 * Static DateTimeFormatter instance for date format yyyyMMdd
	 */
	public static final DateTimeFormatter DEFAULT_INT_PATTERN = DateTimeFormatter.ofPattern("yyyyMMdd");
	/**
	 * Static DateTimeFormatter instance for date format HHmmssSSS
	 */
	public static final DateTimeFormatter DEFAULT_TIME_PATTERN = DateTimeFormatter.ofPattern("HHmmssSSS");
	/**
	 * Static DateTimeFormatter instance for date format yyyyMMddHHmm
	 */
	public static final DateTimeFormatter DEFAULT_LONG_PATTERN = DateTimeFormatter.ofPattern("yyyyMMddHHmm");

	/**
	 * Current UTC Clock Instance
	 */
	private static final UTCClock UTC_CLOCK = new UTCClock();

	private DateTimeUtils() {
	}
	
	/**
	 * Formats given date according to string with ISO8601 format
	 * @param date		date instance
	 * @return			formatted result by ISO8601
	 */
	public static String formatDateForSiteMap(Date date) {
		if (date == null) {
			return Globals.DEFAULT_VALUE_STRING;
		}
		return formatDate(date, DEFAULT_SITE_MAP_PATTERN);
	}
	
	/**
	 * Parses given string according to <code>java.util.Date</code> with ISO8601 format
	 * @param string			String value with ISO8601 format
	 * @return					Date object
	 * @throws ParseException	given string is null
	 */
	public static Date parseSiteMapDate(String string) throws ParseException {
		return parseDate(string, DEFAULT_DATETIME_PATTERN_ISO8601 + DateTimeUtils.getTimeZone());
	}

	/**
	 * Formats given date according to string with vCard format
	 * @return			formatted result by ISO8601 and ending character is 'Z'
	 */
	public static String formatGMTDateForVCard() {
		return ZonedDateTime.ofInstant(Instant.now(), ZoneId.of("+00:00"))
				.format(DateTimeFormatter.ofPattern(DEFAULT_DATETIME_PATTERN_ISO8601 + "'Z'"));
	}

	/**
	 * Formats given date according to string with vCard format
	 * @param date		date instance
	 * @return			formatted result by ISO8601 and ending character is 'Z'
	 */
	public static String formatDateForVCard(Date date) {
		if (date == null) {
			return null;
		}
		return formatDate(date, DEFAULT_ISO8601_PATTERN) + "Z";
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

		return Date.from(LocalDate.parse(string, DateTimeFormatter.ofPattern(format))
				.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

	/**
	 * Formats given date according to system style
	 * @param date		date instance
	 * @return			formatted result by system default format
	 */
	public static String formatDate(Date date) {
		return formatDate(date, DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL));
	}
	
	/**
	 * Return current day value with format "yyyyMMdd"
	 * @return		current day
	 */
	public static int currentDay() {
		return Integer.parseInt(LocalDate.now().format(DEFAULT_INT_PATTERN));
	}

	/**
	 * Return current GMT day value with format "yyyyMMdd"
	 * @return		current GMT day
	 */
	public static int currentUTCDay() {
		return Integer.parseInt(DateTimeUtils.formatDate(new Date(currentUTCTimeMillis()), DEFAULT_INT_PATTERN));
	}
	
	/**
	 * Return expire day value with format "yyyyMMdd"
	 * @param expireTime expire time
	 * @return		expire day
	 */
	public static int expireDay(long expireTime) {
		return Integer.parseInt(DateTimeUtils.formatDate(new Date(currentTimeMillis()), DEFAULT_INT_PATTERN));
	}

	/**
	 * Return expire day value with format "yyyyMMdd"
	 * @param expireTime expire time
	 * @return		expire GMT day
	 */
	public static int expireUTCDay(long expireTime) {
		return Integer.parseInt(DateTimeUtils.formatDate(new Date(currentUTCTimeMillis() + expireTime),
				DEFAULT_INT_PATTERN));
	}

	/**
	 * Calc expire time millis by given month count
	 * @param monthCount        month count
	 * @return                  expire time millis
	 */
	public static long expireMonth(int monthCount) {
		return currentTimeMillis() + (expireDayCount(monthCount) * 24 * 60 * 60 * 1000L);
	}

	/**
	 * Calc expire UTC time millis by given month count
	 * @param monthCount        month count
	 * @return                  expire UTC time millis
	 */
	public static long expireUTCMonth(int monthCount) {
		return currentUTCTimeMillis() + (expireDayCount(monthCount) * 24 * 60 * 60 * 1000L);
	}

	/**
	 * Current Year
	 * @return  Current Year
	 */
	public static int currentYear() {
		return Calendar.getInstance().get(Calendar.YEAR);
	}

	/**
	 * Current Month
	 * @return  Current Month
	 */
	public static int currentMonth() {
		return Calendar.getInstance().get(Calendar.MONTH) + 1;
	}

	/**
	 * Days count of given year and month
	 * @param year      year
	 * @param month     month
	 * @return  Days count
	 */
	public static int getDaysOfMonth(int year, int month) {
		Calendar calendar = Calendar.getInstance();
		switch (month) {
			case 1:
				calendar.set(year, Calendar.JANUARY, 1);
				break;
			case 2:
				calendar.set(year, Calendar.FEBRUARY, 1);
				break;
			case 3:
				calendar.set(year, Calendar.MARCH, 1);
				break;
			case 4:
				calendar.set(year, Calendar.APRIL, 1);
				break;
			case 5:
				calendar.set(year, Calendar.MAY, 1);
				break;
			case 6:
				calendar.set(year, Calendar.JUNE, 1);
				break;
			case 7:
				calendar.set(year, Calendar.JULY, 1);
				break;
			case 8:
				calendar.set(year, Calendar.AUGUST, 1);
				break;
			case 9:
				calendar.set(year, Calendar.SEPTEMBER, 1);
				break;
			case 10:
				calendar.set(year, Calendar.OCTOBER, 1);
				break;
			case 11:
				calendar.set(year, Calendar.NOVEMBER, 1);
				break;
			case 12:
				calendar.set(year, Calendar.DECEMBER, 1);
				break;
			default:
				return Globals.DEFAULT_VALUE_INT;
		}
		return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
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
		return (year - 1980) << 25
				| (cal.get(Calendar.MONTH) + 1) << 21
				| cal.get(Calendar.DATE) << 16
				| cal.get(Calendar.HOUR_OF_DAY) << 11
				| cal.get(Calendar.MINUTE) << 5
				| cal.get(Calendar.SECOND) >> 1;
	}
	
	/**
	 * Converts time in dos format to Java format
	 * @param dosTime		dos time
	 * @return time in java format
	 */
	public static long dosToJavaTme(long dosTime) {
		int month;
		switch ((int)((dosTime >> 21) & 0x0F)) {
			case 1:
				month = Calendar.JANUARY;
				break;
			case 2:
				month = Calendar.FEBRUARY;
				break;
			case 3:
				month = Calendar.MARCH;
				break;
			case 4:
				month = Calendar.APRIL;
				break;
			case 5:
				month = Calendar.MAY;
				break;
			case 6:
				month = Calendar.JUNE;
				break;
			case 7:
				month = Calendar.JULY;
				break;
			case 8:
				month = Calendar.AUGUST;
				break;
			case 9:
				month = Calendar.SEPTEMBER;
				break;
			case 10:
				month = Calendar.OCTOBER;
				break;
			case 11:
				month = Calendar.NOVEMBER;
				break;
			case 12:
				month = Calendar.DECEMBER;
				break;
			default:
				return Globals.DEFAULT_VALUE_LONG;
		}

		Calendar calendar = Calendar.getInstance();
		calendar.set((int)(((dosTime >> 25) & 0x7F) + 1980), month,
				(int)((dosTime >> 16) & 0x1F),
				(int)((dosTime >> 11) & 0x1F),
				(int)((dosTime >> 5) & 0x3F),
				(int)((dosTime << 1) & 0x3E));
		calendar.clear(Calendar.MILLISECOND);
	    return calendar.getTimeInMillis();
	}

	/**
	 * Return current time in milliseconds.
	 * @return		current time in milliseconds.
	 */
	public static long currentTime() {
		return Long.parseLong(formatDate(new Date(currentTimeMillis()), DEFAULT_LONG_PATTERN));
	}

	/**
	 * Return current time in milliseconds.
	 * @return		current time in milliseconds.
	 */
	public static long currentTimeMillis() {
		return UTC_CLOCK.currentTimeMillis();
	}

	/**
	 * Returns the GMT time in milliseconds.
	 * @return		current GMT time in milliseconds.
	 */
	public static long currentUTCTime() {
		return Long.parseLong(formatDate(new Date(currentUTCTimeMillis()), DEFAULT_LONG_PATTERN));
	}

	/**
	 * Returns the GMT time in milliseconds.
	 * @return		current GMT time in milliseconds.
	 */
	public static long currentUTCTimeMillis() {
		return UTC_CLOCK.currentUTCTimeMillis();
	}

	/**
	 * Formats given date according to format style
	 * @param date			        Date instance
	 * @param dateTimeFormatter		Datetime formatter
	 * @return				        Format date value as string
	 */
	public static String formatDate(Date date, DateTimeFormatter dateTimeFormatter) {
		return DateTimeUtils.formatDate(date, dateTimeFormatter, TimeZone.getDefault());
	}
	
	/**
	 * Formats given date according to format style
	 * @param date			        Date instance
	 * @param dateTimeFormatter		Datetime formatter
	 * @param timeZone		        Time zone
	 * @return				        Time value of String
	 */
	public static String formatDate(Date date, DateTimeFormatter dateTimeFormatter, TimeZone timeZone) {
		if (date == null) {
			date = new Date();
		}

		return date.toInstant().atZone(timeZone.toZoneId()).toLocalDateTime()
				.format(dateTimeFormatter);
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
	 */
	public static String format(Date date, Locale locale, String pattern) {
		LocalDateTime localDateTime;
		if (date == null) {
			localDateTime = LocalDateTime.now();
		} else {
			localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		}
		return localDateTime.format(DateTimeFormatter.ofPattern(pattern, locale));
	}

	/**
	 * Parses given string according to specified locale and a given pattern.
	 *
	 * @param source Source string to parse date and time from
	 * @param locale Locale to use for parsing date and time
	 * @param pattern Pattern to use
	 * @return Date object corresponding to representation given in source
	 * string
	 */
	public static Date parse(String source, Locale locale, String pattern) {
		return Date.from(LocalDateTime.parse(source, DateTimeFormatter.ofPattern(pattern, locale))
				.atZone(ZoneId.systemDefault()).toInstant());
	}
	
	/**
	 * Check current year is leap year
	 * @return		check result
	 */
	public static boolean isLeapYear() {
		return LocalDate.now().isLeapYear();
	}

	/**
	 * Get current month first day
	 * @param format        Date format
	 * @return              Formatted date string
	 */
	public static String getCurrentMonthFirstDay(String format) {
		return LocalDateTime.now().with(TemporalAdjusters.firstDayOfMonth())
				.format(DateTimeFormatter.ofPattern(format));
	}

	/**
	 * Get current month last day
	 * @param format        Date format
	 * @return              Formatted date string
	 */
	public static String getCurrentMonthLastDay(String format) {
		return LocalDateTime.now().with(TemporalAdjusters.lastDayOfMonth())
				.format(DateTimeFormatter.ofPattern(format));
	}

	/**
	 * Calc expire days count
	 * @param monthCount    month count
	 * @return              days count
	 */
	private static int expireDayCount(int monthCount) {
		int dayCount = Globals.INITIALIZE_INT_VALUE;
		int currentYear = currentYear();
		int currentMonth = currentMonth();
		for (int i = 0 ; i < monthCount ; i++) {
			dayCount += getDaysOfMonth(currentYear, currentMonth);
			currentMonth++;
		}
		return dayCount;
	}

	/**
	 * Get default time zone string
	 * @return      time zone string
	 */
	private static String getTimeZone() {
		StringBuilder stringBuilder = new StringBuilder();
		int zoneCode = TimeZone.getDefault().getRawOffset() / (1000 * 60 * 60);
		stringBuilder.append(zoneCode >= 0 ? "+" : "-");
		if (zoneCode < 0) {
			zoneCode = Math.abs(zoneCode);
		}
		stringBuilder.append(zoneCode < 10 ? "0" : "").append(zoneCode).append(":00");
		return stringBuilder.toString();
	}

	/**
	 * UTC Clock
	 */
	private static final class UTCClock {

		private final AtomicLong currentLocalTime = new AtomicLong(System.currentTimeMillis());
		private final AtomicLong currentUTCTime =
				new AtomicLong(System.currentTimeMillis() - TimeZone.getDefault().getRawOffset());

		public UTCClock() {
			ScheduledThreadPoolExecutor threadPoolExecutor =
					new ScheduledThreadPoolExecutor(1, r -> {
						Thread thread = new Thread(r);
						thread.setDaemon(true);
						return thread;
					});
			threadPoolExecutor.scheduleAtFixedRate(this::readTime, 0L, 1L, TimeUnit.MILLISECONDS);
		}

		public long currentTimeMillis() {
			return this.currentLocalTime.get();
		}

		public long currentUTCTimeMillis() {
			return this.currentUTCTime.get();
		}

		private void readTime() {
			long currentTime = System.currentTimeMillis();
			this.currentLocalTime.set(currentTime);
			this.currentUTCTime.set(currentTime - TimeZone.getDefault().getRawOffset());
		}
	}
}
