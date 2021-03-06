package se.cenote.util.calendar;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public class CalendarUtil {
	
	private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";
	private static final DateTimeFormatter FMT = new DateTimeFormatterBuilder().appendPattern(PATTERN).toFormatter();
	
	
	public static String formatDuration(long seconds){
		return String.format("%02d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, (seconds % 60));
	}
	
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
