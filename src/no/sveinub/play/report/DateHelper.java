package no.sveinub.play.report;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateHelper {
	private static DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMdd");

	public static DateTime toDate(String date) {
		return formatter.parseDateTime(date);
	}
	
	public static String toString(DateTime date) {
		return date.toString(formatter);
	}
}
