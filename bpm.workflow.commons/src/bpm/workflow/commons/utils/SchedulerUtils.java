package bpm.workflow.commons.utils;

import java.util.Calendar;
import java.util.Date;

import bpm.workflow.commons.beans.Schedule;
import bpm.workflow.commons.beans.Schedule.Period;

public class SchedulerUtils {

	public static Date getNextExecution(Date currentDate, Schedule schedule) {
		if (schedule == null) {
			return null;
		}

		Date beginDate = schedule.getBeginDate();

		Period period = schedule.getPeriod();
		int interval = schedule.getInterval();

		if (period == null || interval <= 0) {
			return null;
		}

		return getNextExecution(currentDate, beginDate, period, interval);
	}
	
	public static Date getPreviousExecution(Date currentDate, Schedule schedule, int checkTime) {
		if (schedule == null) {
			return null;
		}

		Date beginDate = schedule.getBeginDate();

		Period period = schedule.getPeriod();
		int interval = schedule.getInterval();

		if (period == null || interval <= 0) {
			return null;
		}

		return getPreviousExecution(currentDate, beginDate, period, interval, checkTime);
	}

	private static Date getNextExecution(Date currentDate, Date beginDate, Period period, int interval) {
		Calendar c = Calendar.getInstance();
		c.setTime(beginDate);

		while (!valid(currentDate, c.getTime())) {
			addInterval(period, c, interval);
		}

		return c.getTime();
	}
	
	private static Date getPreviousExecution(Date currentDate, Date beginDate, Period period, int interval, int checkTime) {
		Calendar c = Calendar.getInstance();
		c.setTime(beginDate);

		if (currentDate.before(c.getTime())) {
			return null;
		}

		Date previousExecution = c.getTime();
		boolean after = false;
		while (!after) {
			addInterval(period, c, interval);
			if (currentDate.before(c.getTime())) {
				after = true;
			}
			else {
				previousExecution = c.getTime();
			}
		}

		return previousExecution;
	}

	private static boolean valid(Date currentDate, Date time) {
		return currentDate.before(time);
	}
	
	public static void addInterval(Period period, Calendar c, int interval) {
		switch (period) {
		case YEAR:
			addYearlyInterval(c, interval);
			break;
		case MONTH:
			addMonthlyInterval(c, interval);
			break;
		case WEEK:
			addWeeklyInterval(c, interval);
			break;
		case DAY:
			addDailyInterval(c, interval);
			break;
		case HOUR:
			addHourlyInterval(c, interval);
			break;
		case MINUTE:
			addMinuteInterval(c, interval);
			break;
		default:
			break;
		}
	}

	private static Date addYearlyInterval(Calendar c, int interval) {
		c.add(Calendar.YEAR, interval);
		return c.getTime();
	}

	private static Date addMonthlyInterval(Calendar c, int interval) {
		c.add(Calendar.MONTH, interval);
		return c.getTime();
	}

	private static Date addWeeklyInterval(Calendar c, int interval) {
		c.add(Calendar.DATE, interval * 7);
		return c.getTime();
	}

	private static Date addDailyInterval(Calendar c, int interval) {
		c.add(Calendar.DATE, interval);
		return c.getTime();
	}

	private static Date addHourlyInterval(Calendar c, int interval) {
		c.add(Calendar.HOUR, interval);
		return c.getTime();
	}

	private static Date addMinuteInterval(Calendar c, int interval) {
		c.add(Calendar.MINUTE, interval);
		return c.getTime();
	}
}
