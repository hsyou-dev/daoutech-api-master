package com.daoutech.api.util;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateTimeUtil {

	private final static DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
	private final static DateTimeFormatter DATETIME_MILLISEC_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss SSS");
	
	public static String toDateTimeStringFrom(Date date) {
		return DATETIME_FORMATTER.format(date.toInstant().atZone(ZoneId.systemDefault()));
	}
	
	public static String toDateTimeMillsStringFrom(Date date) {
		return DATETIME_MILLISEC_FORMATTER.format(date.toInstant().atZone(ZoneId.systemDefault()));
	}
	
	public static String toStartDateStringFrom(String date) {
		return date + "00";
	}
	
	public static String toEndDateStringFrom(String date) {
		return date + "23";
	}
}
