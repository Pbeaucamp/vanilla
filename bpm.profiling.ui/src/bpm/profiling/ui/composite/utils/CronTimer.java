package bpm.profiling.ui.composite.utils;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class CronTimer {

	// not case sensitive in cron expressions
	private static String[] dayOfWeek = { "SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT" };

	private int ss = 0; // seconds, not used
	private int mn = 0; // minutes < 59
	private int ho = 0; // hour < 23
	private int dm = 1; // day of month < 31
	private int mo = 1; // month < 12
	private int dw = 0; // day of week < 7

	private String cron;

	/*
	 * hourly : "0 0 * * * ?" Fire every hour every day on hh:00 "0 15 0/1 * * ?" Fire every hour every day on :15 daily : "0 15 10 * * ?" Fire at 10:15am every day weekly : "0 15 10 ? * MON-FRI" Fire at 10:15am every Monday, Tuesday, Wednesday, Thursday and Friday monthly : "0 15 10 15 * ?" Fire at 10:15am on the 15th day of every month "0 15 10 15 * JAN" Fire at 10:15am on the 15th day of January
	 */

	public CronTimer() {}

	public void setHour(int hour) throws Exception {
		if(hour > 23)
			throw new Exception("Invalid value for hours");
		if(hour < 0)
			throw new Exception("Invalid value for hours");
		ho = hour;
	}

	public void setHour(String hour) throws Exception {
		if(hour.length() == 0)
			return;
		setHour(Integer.parseInt(hour));
	}

	public void setMinute(int minute) throws Exception {
		if(minute > 59)
			throw new Exception("Invalid value for minutes");
		if(minute < 0)
			throw new Exception("Invalid Value for minutes");
		mn = minute;
	}

	public void setMinute(String minute) throws Exception {
		if(minute.length() == 0)
			return;
		setMinute(Integer.parseInt(minute));
	}

	public void setSecond(int second) throws Exception {
		if(second > 59)
			throw new Exception("Invalid value for seconds");
		if(second < 0)
			throw new Exception("Invalid value for seconds");
		ss = second;
	}

	public void setSecond(String second) throws Exception {
		if(second.length() == 0)
			return;
		setSecond(Integer.parseInt(second));
	}

	public void setMonth(int month) throws Exception {
		if(month > 12)
			throw new Exception("Invalid value for month");
		if(month < 0)
			throw new Exception("Invalid value for month");
		mo = month;
	}

	public void setMonth(String month) throws Exception {
		if(month.length() == 0)
			return;
		setMonth(Integer.parseInt(month));
	}

	public void setDayOfMonth(int dayOfMonth) throws Exception {
		if(dayOfMonth > 31)
			throw new Exception("Invalid value for day of month");
		if(dayOfMonth < 0)
			throw new Exception("Invalid value for day of month");
		dm = dayOfMonth;
	}

	public void setDayOfMonth(String dayOfMonth) throws Exception {
		if(dayOfMonth.length() == 0)
			return;
		setDayOfMonth(Integer.parseInt(dayOfMonth));
	}

	public void setDayOfWeek(int dayOfWeek) throws Exception {
		if(dayOfWeek > 7)
			throw new Exception("Invalid Value for day of week");
		if(dayOfWeek < 0)
			throw new Exception("Invalid Value for day of week");
		dw = dayOfWeek;
	}

	public void setDayOfWeek(String dayOfWeek) throws Exception {
		if(dayOfWeek.length() == 0)
			return;
		setDayOfWeek(Integer.parseInt(dayOfWeek));
	}

	public void setOnce(int month, int day, int hour, int minute, int second) throws Exception {
		setHour(hour);
		setMinute(minute);
		setSecond(second);

		StringBuffer buf = new StringBuffer();
		buf.append("" + second);
		buf.append(" " + minute);
		buf.append(" " + hour);
		buf.append(" " + day);
		buf.append(" " + month); // month
		buf.append(" ?"); // day of week
		cron = buf.toString();
	}

	public void setHourly(int hourInterval, int hour, int minute, int second) throws Exception {
		setHour(hour);
		setMinute(minute);
		setSecond(second);

		StringBuffer buf = new StringBuffer();
		buf.append("" + second);
		buf.append(" " + minute);
		buf.append(" 1/" + hourInterval);

		buf.append(" *"); // day of month
		buf.append(" *"); // month
		buf.append(" ?"); // day of week
		cron = buf.toString();
	}

	public void setDaily(int dayInterval, int day, int hour, int minute, int second) throws Exception {
		setHour(hour);
		setMinute(minute);
		setSecond(second);

		StringBuffer buf = new StringBuffer();
		buf.append("" + second);
		buf.append(" " + minute);
		buf.append(" " + hour);
		buf.append(" 1/" + dayInterval);
		buf.append(" *"); // month
		buf.append(" ?"); // day of week
		cron = buf.toString();
	}

	public void setWeekly(List<Integer> days, int hour, int minute, int second) throws Exception {
		if(days.isEmpty())
			throw new Exception("Must have at least one day");
		setHour(hour);
		setMinute(minute);
		setSecond(second);

		StringBuffer buf = new StringBuffer();
		buf.append("" + second);
		buf.append(" " + minute);
		buf.append(" " + hour);
		buf.append(" ?"); // day of month
		buf.append(" * "); // month
		for(Integer i : days) {
			buf.append(dayOfWeek[i] + ",");
		}

		cron = buf.toString();
		if(cron.endsWith(","))
			cron = cron.substring(0, cron.length() - 1);
	}

	public void setMonthlyByDay(int day, int hour, int minute, int second) throws Exception {
		setHour(hour);
		setMinute(minute);
		setSecond(second);

		StringBuffer buf = new StringBuffer();
		buf.append("" + second);
		buf.append(" " + minute);
		buf.append(" " + hour);
		buf.append(" " + day);
		buf.append(" *"); // month
		buf.append(" ?"); // day of week

		cron = buf.toString();
	}

	public void setMonthlyByLastDay(int day, int hour, int minute, int second) throws Exception {
		if(day < 0)
			throw new Exception("Must have at least one day");
		if(day > 7)
			throw new Exception("Day must be between 0 and 7");
		setHour(hour);
		setMinute(minute);
		setSecond(second);

		StringBuffer buf = new StringBuffer();
		buf.append("" + second);
		buf.append(" " + minute);
		buf.append(" " + hour);
		buf.append(" ?"); // day
		buf.append(" *"); // month
		buf.append(" " + day + "L"); // day of week

		cron = buf.toString();
	}

	public void setMonthlyByFirstDay(int day, int hour, int minute, int second) throws Exception {
		if(day < 0)
			throw new Exception("Must have at least one day");
		if(day > 7)
			throw new Exception("Day must be between 0 and 7");
		setHour(hour);
		setMinute(minute);
		setSecond(second);

		StringBuffer buf = new StringBuffer();
		buf.append("" + second);
		buf.append(" " + minute);
		buf.append(" " + hour);
		buf.append(" ?"); // day
		buf.append(" *"); // month
		buf.append(" " + day + "#1"); // day of week

		cron = buf.toString();
	}

	// Emannuel's. May be bugged
	public void setHourly(String mn, String ho, String interval) throws Exception {
		setMinute(mn);
		setHour(ho);

		StringBuffer buf = new StringBuffer();

		buf.append("0");
		buf.append(" " + mn);
		buf.append(" " + ho + "/" + interval);
		buf.append(" *");
		buf.append(" *");
		buf.append(" ?");

		cron = buf.toString();
	}

	// Emannuel's. May be bugged
	public void setDaily(String mn, String ho, String interval) throws Exception {
		setHour(ho);
		setMinute(mn);

		StringBuffer buf = new StringBuffer();

		buf.append("0");
		buf.append(" " + mn);
		buf.append(" " + ho);
		// we start on first day of week
		buf.append(" " + 0 + "/" + interval);
		buf.append(" *");
		buf.append(" ?");

		cron = buf.toString();
	}

	// Emannuel's. May be bugged
	public void setWeekly(String mn, String ho, ArrayList<String> days) throws Exception {
		setMinute(mn);
		setHour(ho);

		StringBuffer buf = new StringBuffer();

		buf.append("0");
		buf.append(" " + mn);
		buf.append(" " + ho);
		buf.append(" ?");
		buf.append(" *");

		buf.append(" ");

		for(int i = 0; i < days.size(); i++) {
			buf.append(days.get(i));
			if(i != days.size())
				buf.append(", ");
		}


		cron = buf.toString();
	}

	// Emannuel's. May be bugged
	public void setMonthlyByDay(String mn, String ho, String dm) throws Exception {
		setMinute(mn);
		setHour(ho);
		setDayOfMonth(dm);

		StringBuffer buf = new StringBuffer();

		buf.append("0");
		buf.append(" " + mn);
		buf.append(" " + ho);
		buf.append(" " + dm);
		buf.append(" *");
		buf.append(" ?");
		cron = buf.toString();
	}

	/**
	 * 
	 * @param mn
	 * @param ho
	 * @param dow
	 *            day of week, last friday : 6L or first friday 6#1
	 * @throws Exception
	 */
	// Emannuel's. May be bugged
	public void setMonthlyByDayOfWeek(String mn, String ho, String dow) throws Exception {
		setMinute(mn);
		setHour(ho);

		StringBuffer buf = new StringBuffer();

		buf.append("0");
		buf.append(" " + mn);
		buf.append(" " + ho);
		buf.append(" ?");
		buf.append(" *");
		buf.append(" " + dow); // last friday : 6L or first friday 6#1


		cron = buf.toString();
	}

	public String getCronString() {
		if(cron == null)
			return "";
		else
			return cron;
	}
}
