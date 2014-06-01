package se.cenote.safestore.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public class CalendarUtil {
	
	private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";
	private static final DateTimeFormatter FMT = new DateTimeFormatterBuilder().appendPattern(PATTERN).toFormatter();
	
	
	public static String formatDateTime(LocalDateTime dateTime){
		String text = "";
		if(dateTime != null){
			return dateTime.format(FMT);
		}
		return text;
	}


	public static LocalDateTime parse(String text) {
		LocalDateTime dt = null;
		if(text != null && text.length() == PATTERN.length()){
			dt = LocalDateTime.parse(text, FMT);
		}
		return dt;
	}

}
