package bpm.geoloc.creator;

import java.util.Calendar;
import java.util.Date;

public class WaitHelper {
	
	public static final int TIMEOUT_DEFAULT = 3;
	private static final long ONE_MINUTE_IN_MILLIS=60000;//millisecs

	public static Date getTimoutDate(int minutes) {
		Calendar date = Calendar.getInstance();
		long t= date.getTimeInMillis();
		return new Date(t + (minutes * ONE_MINUTE_IN_MILLIS));
	}
}
