package bpm.vanilla.platform.core.utils;

import java.util.Calendar;
import java.util.Date;

import bpm.vanilla.platform.core.beans.scheduler.JobDetail;
import bpm.vanilla.platform.core.beans.scheduler.JobDetail.Period;

public class SchedulerUtils {

	public static Date getNextExecution(Date currentDate, JobDetail schedule) {
		if (schedule == null) {
			return null;
		}

		Date beginDate = schedule.getBeginDate();
		Date endDate = schedule.getStopDate();

		Period period = schedule.getPeriod();
		int interval = schedule.getInterval();

		return getNextExecution(currentDate, beginDate, endDate, period, interval);
	}

	public static Date getPreviousExecution(Date currentDate, JobDetail schedule, int checkTime) {
		if (schedule == null) {
			return null;
		}

		Date beginDate = schedule.getBeginDate();

		Period period = schedule.getPeriod();
		int interval = schedule.getInterval();

		return getPreviousExecution(currentDate, beginDate, period, interval, checkTime);
	}

	private static Date getNextExecution(Date currentDate, Date beginDate, Date endDate, Period period, int interval) {
		Calendar c = Calendar.getInstance();
		c.setTime(beginDate);

		if (period == null || interval <= 0) {
			if (currentDate.before(c.getTime()) && endDate != null && c.getTime().before(endDate)) {
				return c.getTime();
			}
			else {
				return null;
			}
		}

		while (!valid(currentDate, c.getTime())) {
			addInterval(period, c, interval);
		}

		if (endDate == null || c.getTime().before(endDate)) {
			return c.getTime();
		}
		else {
			return null;
		}
	}

	private static Date getPreviousExecution(Date currentDate, Date beginDate, Period period, int interval, int checkTime) {
		Calendar c = Calendar.getInstance();
		c.setTime(beginDate);

		if (currentDate.before(c.getTime())) {
			return null;
		}

		Date previousExecution = c.getTime();
		if (period == null || interval <= 0) {
			return previousExecution;
		}

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

	private static void addInterval(Period period, Calendar c, int interval) {
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
