package bpm.gateway.runtime2.internal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormaterTool {
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	synchronized public static String format(Date date){
		return sdf.format(date);
	}
	
	synchronized public static Date parse(String string) throws ParseException{
		return sdf.parse(string);
	}
}
