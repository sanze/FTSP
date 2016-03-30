/**
 * All rights Reserved, Copyright (C) JFTT 2011<BR>
 * 
 * FileName: TimeUtil.java <BR>
 * Version: $Id: TimeUtil.java, v 1.00 2011-03-18 $ <BR>
 * Modify record: <BR>
 * NO. |     Date         |    Name                 |      Content <BR>
 * 1   | 2011-03-18       | JFTT)Cheng YingQi           | original version <BR>
 */
package com.fujitsu.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public final class TimeUtil {
	// the default date format
	private static final String PATTERN = "yyyy-MM-dd";
	// one day millionseconds
	private static final Long MILLISECOND_DAY = 86400000L; // 1000L*60*60*24;

	//get the millionseconds
	public static Long getMillisecondDay() {
		return MILLISECOND_DAY;
	}

	// get the simledateformat date 
	private static SimpleDateFormat getSimpleDateFormatInstance(String pattern) {
		return new SimpleDateFormat(pattern == null ? PATTERN : pattern);
	}

	// date to string
	public static String parseDate2String(Date date, String pattern) {
		return getSimpleDateFormatInstance(pattern).format(date);
	}

	// date to string with default format
	public static String parseDate2String(Date date) {
		return parseDate2String(date, null);
	}

	// string to date with the pattern
	public static Date parseString2Date(String date, String pattern)
			throws ParseException {
		return getSimpleDateFormatInstance(pattern).parse(date);
	}

	// string to date with default format
	public static Date parseString2Date(String date) throws ParseException {
		return parseString2Date(date, null);
	}

	// calendar to string with the pattern
	public static String parseCalendar2String(Calendar calendar, String pattern) {
		return parseDate2String(calendar.getTime(), pattern);
	}

	// calendar to string with default format
	public static String parseCalendar2String(Calendar calendar) {
		return parseCalendar2String(calendar, null);
	}

	// String to Calendar with the pattern
	public static Calendar parseString2Calendar(String date, String pattern)
			throws ParseException {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(parseString2Date(date, pattern));
		return calendar;
	}

	// String to Calendar with default format
	public static Calendar parseString2Calendar(String date)
			throws ParseException {
		return parseString2Calendar(date, null);
	}

	// Date to Calendar
	public static Calendar parseDate2Calendar(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}

	//Calendar to Date
	public static Date parseCalendar2Date(Calendar calendar) {
		return calendar.getTime();
	}

	// days between two calendar
	public static int getDaysFrom2Dates(Calendar calendar1, Calendar calendar2) {
		return (int) (Math.abs(calendar1.getTimeInMillis()
				- calendar2.getTimeInMillis()) / MILLISECOND_DAY);
	}

	// days between two date
	public static int getDaysFrom2Dates(Date date1, Date date2) {
		return (int) (Math.abs(date1.getTime() - date2.getTime()) / MILLISECOND_DAY);
	}

	// days between two string date
	public static int getDaysFrom2Dates(String date1, String date2,
			String pattern) throws ParseException {
		return getDaysFrom2Dates(parseString2Date(date1, pattern),
				parseString2Date(date2, pattern));
	}

	// days between two string date
	public static int getDaysFrom2Dates(String date1, String date2)
			throws ParseException {
		return getDaysFrom2Dates(date1, date2, null);
	}

	// calculate the date from date with days
	public static String getOtherDay(String date, String pattern, Integer n)
			throws ParseException {
		Calendar calendar = getOtherDay(parseString2Calendar(date, pattern), n);
		return parseCalendar2String(calendar, pattern);
	}

	//get Other day 
	public static String getOtherDay(String date, Integer n)
			throws ParseException {
		return getOtherDay(date, null, n);
	}

	// get the other date from date ,between them is n
	public static Date getOtherDay(Date date, Integer n) {
		return getOtherDay(parseDate2Calendar(date), n).getTime();
	}

	// get the other Calendar from date ,between them is n
	public static Calendar getOtherDay(Calendar calendar, Integer n) {
		calendar.add(Calendar.DAY_OF_MONTH, n);// 此处参数用Calendar中的DAY_OF_MONTH或DAY_OF_YEAR或DAY_OF_WEEK效果等同
		return calendar;
	}

	//if it is SAT,SUN ,return true
	public static boolean isWeekend(Calendar calendar) {
		int i = calendar.get(Calendar.DAY_OF_WEEK);
		if (i == 1 || i == 7) {
			return true;
		}
		return false;
	}

	// get the days from year month
	public static int getMonthDays(int year, int month) {
		switch (month) {
		case 1:
			return 31;
		case 2:
			if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) {
				return 29;
			} else {
				return 28;
			}
		case 3:
			return 31;
		case 4:
			return 30;
		case 5:
			return 31;
		case 6:
			return 30;
		case 7:
			return 31;
		case 8:
			return 31;
		case 9:
			return 30;
		case 10:
			return 31;
		case 11:
			return 30;
		case 12:
			return 31;
		default:
			return 0;
		}
	}

	// get the date today
	public static String getWeekDay() {
		int i = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
		switch (i) {
		case 1:
			return "\u661f\u671f\u65e5"; // 星期日
		case 2:
			return "\u661f\u671f\u4e00"; // 星期一
		case 3:
			return "\u661f\u671f\u4e8c"; // 星期二
		case 4:
			return "\u661f\u671f\u4e09"; // 星期三
		case 5:
			return "\u661f\u671f\u56db"; // 星期四
		case 6:
			return "\u661f\u671f\u4e94"; // 星期五
		default:
			return "\u661f\u671f\u516d"; // 星期六
		}
	}

	//return the string "1988-03-16"
	public static String getCurrentTime() {
		return getCurrentTime(null);
	}

	// get the default time
	public static String getCurrentTime(String pattern) {
		return parseDate2String(new Date(), pattern);
	}

	// get the last month,"yyyymm"
	public static String getLastMonth() {
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		if (month == 0) {
			year--;
			month = 12;
		}
		return year + ((month + "").length() == 1 ? "0" + month : (month + ""));
	}

	public static String getWeekOfYear() {
		String num = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR) + "";
		return num.length() == 1 ? "0" + num : num;
	}

	public static String getLastWeekOfYear() {
		int num = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
		if (num > 1) {
			num--;
		}
		return (num + "").length() == 1 ? "0" + num : "" + num;
	}

	public static String getWeekName() {
		return getCurrentTime("yyyy") + getWeekOfYear();
	}

	public static String getStringDate() {
		// Date currentTime = new Date();
		// SimpleDateFormat formatter = new
		// SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// String dateString = formatter.format(currentTime);
		// return dateString;
		return getCurrentTime("yyyy-MM-dd HH:mm:ss");
	}
	
	public static Date getDate(){
		Date date = Calendar.getInstance().getTime();
		return date;
	}
}