package bpm.gateway.ui.utils;

import java.util.ArrayList;
import java.util.List;

import bpm.gateway.ui.i18n.Messages;

@SuppressWarnings("unused")
public class CronTimer {

	// not case sensitive in cron expressions 
	private static String[] dayOfWeek = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$

	private int ss = 0; //seconds, not used
	private int mn = 0; // minutes < 59
	private int ho = 0; // hour < 23
	private int dm = 1; // day of month < 31
	private int mo = 1; // month < 12
	private int dw = 0; // day of week < 7
//	private int ye;     // year, not used, optionnal
	
	private String cron;
	
	/*
	 * hourly :
	 * "0 0 * * * ?"  Fire every hour every day on hh:00
	 * "0 15 0/1 * * ?"  Fire every hour every day on :15
	 * 
	 * daily :
	 * "0 15 10 * * ?" Fire at 10:15am every day
	 * 
	 * weekly :
	 * "0 15 10 ? * MON-FRI" Fire at 10:15am every Monday, Tuesday, Wednesday, Thursday and Friday
	 * 
	 * monthly :
	 * "0 15 10 15 * ?" Fire at 10:15am on the 15th day of every month
	 * "0 15 10 15 * JAN" Fire at 10:15am on the 15th day of January
	 * 
	 */
	
	public CronTimer() {}
	

	public void setHour(int hour) throws Exception {
		if (hour >23)
			throw new Exception(Messages.CronTimer_7);
		if (hour <0)
			throw new Exception(Messages.CronTimer_8);
		ho = hour;
	}
	public void setHour(String hour) throws Exception {
		if (hour.length()==0) return;
		setHour(Integer.parseInt(hour));
	}
	public void setMinute(int minute) throws Exception {
		if (minute >59)
			throw new Exception(Messages.CronTimer_9);
		if (minute <0)
			throw new Exception(Messages.CronTimer_10);
		mn = minute;
	}
	public void setMinute(String minute) throws Exception {
		if (minute.length()==0) return;
		setMinute(Integer.parseInt(minute));
	}
	public void setSecond(int second) throws Exception {
		if (second >59)
			throw new Exception(Messages.CronTimer_11);
		if (second <0)
			throw new Exception(Messages.CronTimer_12);
		ss = second;
	}
	public void setSecond(String second) throws Exception {
		if (second.length()==0) return;
		setSecond(Integer.parseInt(second));
	}
	public void setMonth(int month) throws Exception {
		if (month >12)
			throw new Exception(Messages.CronTimer_13);
		if (month <0)
			throw new Exception(Messages.CronTimer_14);
		mo = month;
	}
	public void setMonth(String month) throws Exception {
		if (month.length()==0) return;
		setMonth(Integer.parseInt(month));
	}
	public void setDayOfMonth(int dayOfMonth) throws Exception {
		if (dayOfMonth >31)
			throw new Exception(Messages.CronTimer_15);
		if (dayOfMonth <0)
			throw new Exception(Messages.CronTimer_16);
		dm = dayOfMonth;
	}
	public void setDayOfMonth(String dayOfMonth) throws Exception {
		if (dayOfMonth.length()==0) return;
		setDayOfMonth(Integer.parseInt(dayOfMonth));
	}
	public void setDayOfWeek(int dayOfWeek) throws Exception {
		if (dayOfWeek >7)
			throw new Exception(Messages.CronTimer_17);
		if (dayOfWeek <0)
			throw new Exception(Messages.CronTimer_18);
		dw = dayOfWeek;
	}
	public void setDayOfWeek(String dayOfWeek) throws Exception {
		if (dayOfWeek.length()==0) return;
		setDayOfWeek(Integer.parseInt(dayOfWeek));
	}
	

	public void setOnce(int month, int day, int hour, int minute, int second) throws Exception {
		setHour(hour);
		setMinute(minute);
		setSecond(second);

		StringBuffer buf = new StringBuffer();
		buf.append( ""+ second);  //$NON-NLS-1$
		buf.append(" "+ minute); //$NON-NLS-1$
		buf.append(" "+ hour);  //$NON-NLS-1$
		buf.append(" "+ day);  //$NON-NLS-1$
		buf.append(" "+ month); // month //$NON-NLS-1$
		buf.append(" ?"); // day of week //$NON-NLS-1$
		cron = buf.toString();
	}
	public void setHourly(int hourInterval, int hour, int minute, int second) throws Exception {
		setHour(hour);
		setMinute(minute);
		setSecond(second);

		StringBuffer buf = new StringBuffer();
		buf.append( ""+ second);  //$NON-NLS-1$
		//for debug purposes
		buf.append(" "+ minute);  //$NON-NLS-1$
		buf.append(" 1/"+ hourInterval); //$NON-NLS-1$
		
		
		buf.append(" *"); // day of month //$NON-NLS-1$
		buf.append(" *"); // month //$NON-NLS-1$
		buf.append(" ?"); // day of week //$NON-NLS-1$
		cron = buf.toString();
	}
	public void setDaily(int dayInterval, int day, int hour, int minute, int second) throws Exception {
		setHour(hour);
		setMinute(minute);
		setSecond(second);

		StringBuffer buf = new StringBuffer();
		buf.append( ""+ second);  //$NON-NLS-1$
		buf.append(" "+ minute);  //$NON-NLS-1$
		buf.append(" "+ hour);  //$NON-NLS-1$
		buf.append(" 1/"+ dayInterval); //$NON-NLS-1$
		buf.append(" *"); // month //$NON-NLS-1$
		buf.append(" ?"); // day of week //$NON-NLS-1$
		cron = buf.toString();
	}
	public void setWeekly(List<Integer> days, int hour, int minute, int second) throws Exception {
		if(days.isEmpty())
			throw new Exception(Messages.CronTimer_37);
		setHour(hour);
		setMinute(minute);
		setSecond(second);
		
		StringBuffer buf = new StringBuffer();
		buf.append(""  + second); //$NON-NLS-1$
		buf.append(" " + minute); //$NON-NLS-1$
		buf.append(" " + hour); //$NON-NLS-1$
		buf.append(" ?"); // day of month //$NON-NLS-1$
		buf.append(" * "); // month //$NON-NLS-1$
		for(Integer i : days) { 
			buf.append(dayOfWeek[i] + ","); //$NON-NLS-1$
		}
		
		cron = buf.toString();
		if(cron.endsWith(",")) //$NON-NLS-1$
			cron = cron.substring(0, cron.length()-1);
	}
	public void setMonthlyByDay(int day, int hour, int minute, int second) throws Exception {
		setHour(hour);
		setMinute(minute);
		setSecond(second);

		StringBuffer buf = new StringBuffer();
		buf.append(""  + second); //$NON-NLS-1$
		buf.append(" " + minute); //$NON-NLS-1$
		buf.append(" " + hour); //$NON-NLS-1$
		buf.append(" " + day); //$NON-NLS-1$
		buf.append(" *"); // month //$NON-NLS-1$
		buf.append(" ?"); // day of week //$NON-NLS-1$
	
		cron = buf.toString();
	}
	public void setMonthlyByLastDay(int day, int hour, int minute, int second) throws Exception {
		if(day<0)
			throw new Exception(Messages.CronTimer_51);
		if(day>7)
			throw new Exception(Messages.CronTimer_52);
		setHour(hour);
		setMinute(minute);
		setSecond(second);

		StringBuffer buf = new StringBuffer();
		buf.append(""  + second); //$NON-NLS-1$
		buf.append(" " + minute); //$NON-NLS-1$
		buf.append(" " + hour); //$NON-NLS-1$
		buf.append(" ?"); // day //$NON-NLS-1$
		buf.append(" *"); // month //$NON-NLS-1$
		buf.append(" "+ day +"L"); // day of week //$NON-NLS-1$ //$NON-NLS-2$
	
		cron = buf.toString();
	}
	public void setMonthlyByFirstDay(int day, int hour, int minute, int second) throws Exception {
		if(day<0)
			throw new Exception(Messages.CronTimer_60);
		if(day>7)
			throw new Exception(Messages.CronTimer_61);
		setHour(hour);
		setMinute(minute);
		setSecond(second);

		StringBuffer buf = new StringBuffer();
		buf.append(""  + second); //$NON-NLS-1$
		buf.append(" " + minute); //$NON-NLS-1$
		buf.append(" " + hour); //$NON-NLS-1$
		buf.append(" ?"); // day //$NON-NLS-1$
		buf.append(" *"); // month //$NON-NLS-1$
		buf.append(" "+ day +"#1"); // day of week //$NON-NLS-1$ //$NON-NLS-2$
	
		cron = buf.toString();
	}
	

	
	
	
	// Emannuel's. May be bugged
	public void setHourly(String mn, String ho, String interval) throws Exception {
		setMinute(mn);
		setHour(ho);
		
		StringBuffer buf = new StringBuffer();
		
		buf.append("0"); //$NON-NLS-1$
		buf.append(" " + mn); //$NON-NLS-1$
		buf.append(" " + ho + "/" + interval); //$NON-NLS-1$ //$NON-NLS-2$
		buf.append(" *"); //$NON-NLS-1$
		buf.append(" *"); //$NON-NLS-1$
		buf.append(" ?"); //$NON-NLS-1$
		
		cron = buf.toString();
	}
	// Emannuel's. May be bugged
	public void setDaily(String mn, String ho, String interval) throws Exception {
		setHour(ho);
		setMinute(mn);
		
		StringBuffer buf = new StringBuffer();
		
		buf.append("0"); //$NON-NLS-1$
		buf.append(" " + mn); //$NON-NLS-1$
		buf.append(" " + ho); //$NON-NLS-1$
		//we start on first day of week
		buf.append(" " + 0 + "/" + interval); //$NON-NLS-1$ //$NON-NLS-2$
		buf.append(" *"); //$NON-NLS-1$
		buf.append(" ?"); //$NON-NLS-1$
		
		cron = buf.toString();
	}
	// Emannuel's. May be bugged
	public void setWeekly(String mn, String ho, ArrayList<String> days) throws Exception {
		setMinute(mn);
		setHour(ho);
		
		StringBuffer buf = new StringBuffer();
		
		buf.append("0"); //$NON-NLS-1$
		buf.append(" " + mn); //$NON-NLS-1$
		buf.append(" " + ho); //$NON-NLS-1$
		buf.append(" ?"); //$NON-NLS-1$
		buf.append(" *"); //$NON-NLS-1$
		
		buf.append(" "); //$NON-NLS-1$
		
		for (int i=0; i < days.size(); i++) {
			buf.append(days.get(i));
			if (i != days.size())
				buf.append(", "); //$NON-NLS-1$
		}
		
		cron = buf.toString();
	}
	
	// Emannuel's. May be bugged
	public void setMonthlyByDay(String mn, String ho, String dm) throws Exception {
		setMinute(mn);
		setHour(ho);
		setDayOfMonth(dm);
		
		StringBuffer buf = new StringBuffer();
		
		buf.append("0"); //$NON-NLS-1$
		buf.append(" " + mn); //$NON-NLS-1$
		buf.append(" " + ho); //$NON-NLS-1$
		buf.append(" " + dm); //$NON-NLS-1$
		buf.append(" *"); //$NON-NLS-1$
		buf.append(" ?"); //$NON-NLS-1$
		
		cron = buf.toString();
	}
	
	/**
	 * 
	 * @param mn
	 * @param ho
	 * @param dow day of week, last friday : 6L or first friday 6#1
	 * @throws Exception
	 */
	// Emannuel's. May be bugged
	public void setMonthlyByDayOfWeek(String mn, String ho, String dow) throws Exception {
		setMinute(mn);
		setHour(ho);
		
		StringBuffer buf = new StringBuffer();
		
		buf.append("0"); //$NON-NLS-1$
		buf.append(" " + mn); //$NON-NLS-1$
		buf.append(" " + ho); //$NON-NLS-1$
		buf.append(" ?"); //$NON-NLS-1$
		buf.append(" *"); //$NON-NLS-1$
		buf.append(" " + dow ); //last friday : 6L or first friday 6#1  //$NON-NLS-1$
		
		cron = buf.toString();
	}
	
	public String getCronString() {
		if (cron == null)
			return ""; //$NON-NLS-1$
		else
			return cron;
	}
}
