package bpm.freemetrics.api.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import bpm.freemetrics.api.organisation.metrics.MetricValues;

public class Tools {

	public static final int OS_TYPE_LINUX = 2;
	public static final int OS_TYPE_MAC = 1;
	public static final int OS_TYPE_WINDOWS = 0;

	public static final int ROLE_USER = 0;
	public static final int ROLE_SUPER_ADMIN = 1;
	public static final int ROLE_ADMIN = 2;
	public static final int ROLE_DESIGNER = 3;
	public static final int ROLE_LOADER = 4;
	public static final int ROLE_ALERT = 5;



	/**
	 * Check if the specified String is a valid entry. 
	 * By "valid" we mean a String which is neither null, neither empty. 
	 *
	 * @param entry the string to test
	 * @return true if entry is not null and not an empty string
	 */
	public static boolean isValid(String entry) {
		boolean succes = false;
		if(entry!=null && !entry.trim().equalsIgnoreCase(""))
			succes =true;
		return succes;
	}

	/**The value's status according to the target, 
	 * May have 3 values : 
	 * <LI> IConstants.STATUS_OVER, means that value is over the target (With Tolerance range)</LI>
	 * <LI> IConstants.STATUS_UNDER, means that value is under the target (With Tolerance range)</LI>
	 * <LI> IConstants.STATUS_ACHIEVED, means that value is close to the target (With Tolerance range)</LI>
	 * 
	 * </BR></BR>
	 * @param value
	 * @param target
	 * @return
	 */
	public static String computeStatus(MetricValues value, MetricValues target) {

//		if(value != null && target != null && value.getMvTrendOrder()!= null){

//		float v = value.getMvValue() != null ? value.getMvValue() : 0;
//		float t = target.getMvGlObjectif() != null ? target.getMvGlObjectif() : 0 ;
//		float toler = target.getMvTolerance()  != null ? target.getMvTolerance() : 0;

//		float tolerance = (t * toler)/100;

//		if(value.getMvTrendOrder().equals(IConstants.TREND_ORDER_UP)){ 
//		if (Float.compare(t, v) == 0)
//		stateArrow = stable_green;
//		else if (Float.compare(v, t) >= 0 && Float.compare(v, (t+tolerance)) < 0)
//		stateArrow = up_green; 
//		else if ( (t - tolerance) <= v)
//		stateArrow = down_yellow;
//		else 
//		stateArrow = down_red;
//		} else 
//		if(value.getMvTrendOrder().equals(IConstants.TREND_ORDER_DOWN)){ 
//		if(v <= t)
//		stateArrow = down_green;
//		else if( v <= (t + tolerance) )
//		stateArrow = up_yellow;
//		else 
//		stateArrow = up_red;
//		}else if(value.getMvTrendOrder().equals(IConstants.TREND_ORDER_STABLE)){ 

//		if( v < (t-tolerance))
//		stateArrow = down_red;
//		else if( v > (t + tolerance) )
//		stateArrow = up_red;
//		else 
////		stateArrow = up_red;
////		if( (target+tolerance) < value  |  value < (target-tolerance) )
////		stateArrow = stable_red;
////		else 
//		stateArrow = stable_yellow;
//		}
//		}else{
//		return IConstants.STATUS_ACHIEVED;
//		}


		if(value != null && target != null){
			float v = value.getMvValue() != null ? value.getMvValue() : 0;
			float t = target.getMvGlObjectif() != null ? target.getMvGlObjectif() : 0 ;
			float toler = target.getMvTolerance()  != null ? target.getMvTolerance() : 0;
			float tc = (t * toler)/100;
			if(Float.compare(v, (t+tc)) > 0){
				return IConstants.STATUS_OVER;
			}else if(Float.compare(v, (t-tc)) < 0){
				return IConstants.STATUS_UNDER;
			}else{
				return IConstants.STATUS_ACHIEVED;
			}
		}else{
			return IConstants.STATUS_ACHIEVED;
		}
	}

	/**
	 * @param value
	 * @param previous
	 * @return
	 */
	public static String computeTrend(MetricValues value, MetricValues refValue,MetricValues target) {

		if(value != null && refValue != null && target != null){

			float v = value.getMvValue() != null ? value.getMvValue() : 0;
			float ref = refValue.getMvValue() != null ? refValue.getMvValue() : 0 ;

			float t = target.getMvGlObjectif() != null ? target.getMvGlObjectif() : 0 ;
			float toler = target.getMvTolerance()  != null ? target.getMvTolerance() : 0;
			float tc = (t * toler)/100;

			if(Float.compare(v, ref)> 0){ 
				if(Float.compare(v, (t+tc)) > 0){
					return IConstants.TREND_BAD_UP;
				}else if(Float.compare(v, (t-tc)) < 0){
					return IConstants.TREND_GOOD_UP;
				}else{
					return IConstants.TREND_WARN_UP;
				}
			}else if(Float.compare(v, ref) < 0 ){

				if(Float.compare(v, (t+tc)) > 0){
					return IConstants.TREND_GOOD_DOWN;
				}else if(Float.compare(v, (t-tc)) < 0){
					return IConstants.TREND_BAD_DOWN;
				}else{
					return IConstants.TREND_WARN_DOWN;
				}
			}else{

				if(Float.compare(v, (t+tc)) > 0){
					return IConstants.TREND_BAD_STABLE;
				}else if(Float.compare(v, (t-tc)) < 0){
					return IConstants.TREND_BAD_STABLE;
				}else{
					return IConstants.TREND_GOOD_STABLE;
				}
			}		

		}else{
			return IConstants.TREND_WARN_STABLE;
		}

	}

	/**The trend order according the target, 
	 * May have 3 values : 
	 * <LI> IConstants.TREND_ORDER_DOWN, means that value is over the target and must regressed</LI>
	 * <LI> IConstants.TREND_ORDER_UP, means that value is under the target and must progressed</LI>
	 * <LI> IConstants.TREND_ORDER_STABLE, means that value is close to the target</LI>
	 * 
	 * </BR></BR>
	 * @param value
	 * @param target
	 * @return String the trend order
	 */
	public static String computeTrendOrder(MetricValues value, MetricValues target) {

		if(value != null && target != null){

			float v = value.getMvValue() != null ? value.getMvValue() : 0;

			float t = target.getMvGlObjectif() != null ? target.getMvGlObjectif() : 0 ;
			float toler = target.getMvTolerance()  != null ? target.getMvTolerance() : 0;

			float tc = (t * toler)/100;

			if(Float.compare(v, (t+tc)) > 0){
				return IConstants.TREND_ORDER_DOWN;
			}else if(Float.compare(v, (t-tc)) < 0){
				return IConstants.TREND_ORDER_UP;
			}else{
				return IConstants.TREND_ORDER_STABLE;
			}
		}else{
			return IConstants.TREND_ORDER_STABLE;
		}
	}

	public static boolean isNumeric(String p_strTest) {
		boolean res = false;

		if(isValid(p_strTest)){
			try {							 
				Double.parseDouble(p_strTest);				 
				res = true;

			} catch (NumberFormatException nfe) {}
		}

		return res;
	}

	public static Date computePreviousDateForPeriodicity(Date period, String periodicity) {

		Calendar cal=Calendar.getInstance();
		cal.setTime(period);

		if(periodicity.trim().equals(IConstants.PERIODS[IConstants.PERIOD_YEARLY].trim())){
			int year = (cal.get(Calendar.YEAR)) -1 ;
			cal.set(year, cal.get(Calendar.MONTH), cal.get(Calendar.DATE), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));

		}else if(periodicity.trim().equals(IConstants.PERIODS[IConstants.PERIOD_BIANNUAL].trim())){

			int month =0;
			int year = (cal.get(Calendar.YEAR));

			if(cal.get(Calendar.MONTH)>= 6){
				month = cal.get(Calendar.MONTH) - 6;
				cal.set(year, month, cal.get(Calendar.DATE), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
			}else{
				month = 11 - cal.get(Calendar.MONTH);
				year = year - 1;
				cal.set(year, month, cal.get(Calendar.DATE), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
			}

		}else if(periodicity.trim().equals(IConstants.PERIODS[IConstants.PERIOD_QUARTERLY].trim())){

			int month =0;
			int year = (cal.get(Calendar.YEAR));

			if(cal.get(Calendar.MONTH)>= 3){
				month = cal.get(Calendar.MONTH) - 3;
				cal.set(year, month, cal.get(Calendar.DATE), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
			}else{
				month = 11 - cal.get(Calendar.MONTH);
				year = year - 1;
				cal.set(year, month, cal.get(Calendar.DATE), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
			}
		}else if(periodicity.trim().equals(IConstants.PERIODS[IConstants.PERIOD_MONTHLY].trim())){

			int month =0;
			int year = (cal.get(Calendar.YEAR));

			if(cal.get(Calendar.MONTH)>= 1){
				month = cal.get(Calendar.MONTH) - 1;
				cal.set(year, month, cal.get(Calendar.DATE), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
			}else{
				month = 11 - cal.get(Calendar.MONTH);
				year = year - 1;
				cal.set(year, month, cal.get(Calendar.DATE), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
			}

		}else if(periodicity.trim().equals(IConstants.PERIODS[IConstants.PERIOD_WEEKLY].trim())){

			int date = 1;
			int month = cal.get(Calendar.MONTH);
			int year = (cal.get(Calendar.YEAR));

			if(cal.get(Calendar.DATE)>= 7){
				date = cal.get(Calendar.DATE) - 7;
				cal.set(year, month, date, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
			}else{

				date = 31 - cal.get(Calendar.DATE);

				if(cal.get(Calendar.MONTH)>= 1){
					month = cal.get(Calendar.MONTH) - 1;
					cal.set(year, month, date, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
				}else{
					month = 11 - cal.get(Calendar.MONTH);
					year = year - 1;
					cal.set(year, month, date, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
				}
			}
		}else if(periodicity.trim().equals(IConstants.PERIODS[IConstants.PERIOD_DAYLY].trim())){

			int date = 1;
			int month = cal.get(Calendar.MONTH);
			int year = (cal.get(Calendar.YEAR));

			if(cal.get(Calendar.DATE)>= 1){
				date = cal.get(Calendar.DATE) - 1;
				cal.set(year, month, date, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
			}else{

				date = 31 - cal.get(Calendar.DATE);

				if(cal.get(Calendar.MONTH)>= 1){
					month = cal.get(Calendar.MONTH) - 1;
					cal.set(year, month, date, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
				}else{
					month = 11 - cal.get(Calendar.MONTH);
					year = year - 1;
					cal.set(year, month, date, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
				}
			}
		}else if(periodicity.trim().equals(IConstants.PERIODS[IConstants.PERIOD_HOURLY].trim())){

			int hour = 1;
			int date = cal.get(Calendar.DATE);
			int month = cal.get(Calendar.MONTH);
			int year = (cal.get(Calendar.YEAR));

			if(cal.get(Calendar.HOUR_OF_DAY)>= 1){
				hour = cal.get(Calendar.HOUR_OF_DAY) - 1;

				cal.set(year, month, date, hour, cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
			}else{

				hour = 24 - cal.get(Calendar.HOUR_OF_DAY);				

				if(cal.get(Calendar.DATE)>= 1){
					date = cal.get(Calendar.DATE) - 1;
					cal.set(year, month, date, hour, cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
				}else{

					date = 31 - cal.get(Calendar.DATE);

					if(cal.get(Calendar.MONTH)>= 1){
						month = cal.get(Calendar.MONTH) - 1;
						cal.set(year, month, date, hour, cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
					}else{
						month = 11 - cal.get(Calendar.MONTH);
						year = year - 1;
						cal.set(year, month, date, hour, cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
					}
				}
			}
		}else if(periodicity.trim().equals(IConstants.PERIODS[IConstants.PERIOD_MINUTLY].trim())){

			int minute = 1;
			int hour = cal.get(Calendar.HOUR_OF_DAY);
			int date = cal.get(Calendar.DATE);
			int month = cal.get(Calendar.MONTH);
			int year = (cal.get(Calendar.YEAR));

			if(cal.get(Calendar.MINUTE)>= 1){
				minute = cal.get(Calendar.MINUTE) - 1;
				cal.set(year, month, date, hour, minute, cal.get(Calendar.SECOND));
			}else{

				hour = 60 - cal.get(Calendar.MINUTE);				

				if(cal.get(Calendar.HOUR_OF_DAY)>= 1){
					hour = cal.get(Calendar.HOUR_OF_DAY) - 1;

					cal.set(year, month, date, hour, minute, cal.get(Calendar.SECOND));
				}else{

					hour = 24 - cal.get(Calendar.HOUR_OF_DAY);				

					if(cal.get(Calendar.DATE)>= 1){
						date = cal.get(Calendar.DATE) - 1;
						cal.set(year, month, date, hour, minute, cal.get(Calendar.SECOND));
					}else{

						date = 31 - cal.get(Calendar.DATE);

						if(cal.get(Calendar.MONTH)>= 1){
							month = cal.get(Calendar.MONTH) - 1;
							cal.set(year, month, date, hour, minute, cal.get(Calendar.SECOND));
						}else{
							month = 11 - cal.get(Calendar.MONTH);
							year = year - 1;
							cal.set(year, month, date, hour, minute, cal.get(Calendar.SECOND));
						}
					}
				}
			}
		}else if(periodicity.trim().equals(IConstants.PERIODS[IConstants.PERIOD_SECONDLY].trim())){

			int seconde = 1;
			int minute = cal.get(Calendar.MINUTE);
			int hour = cal.get(Calendar.HOUR_OF_DAY);
			int date = cal.get(Calendar.DATE);
			int month = cal.get(Calendar.MONTH);
			int year = (cal.get(Calendar.YEAR));

			if(cal.get(Calendar.SECOND)>= 1){
				seconde = cal.get(Calendar.SECOND) - 1;
				cal.set(year, month, date, hour, minute,seconde);
			}else{

				seconde = 60 - cal.get(Calendar.SECOND);				

				if(cal.get(Calendar.MINUTE)>= 1){
					minute = cal.get(Calendar.MINUTE) - 1;
					cal.set(year, month, date, hour, minute, seconde);
				}else{

					hour = 60 - cal.get(Calendar.MINUTE);				

					if(cal.get(Calendar.HOUR_OF_DAY)>= 1){
						hour = cal.get(Calendar.HOUR_OF_DAY) - 1;

						cal.set(year, month, date, hour, minute,seconde);
					}else{

						hour = 24 - cal.get(Calendar.HOUR_OF_DAY);				

						if(cal.get(Calendar.DATE)>= 1){
							date = cal.get(Calendar.DATE) - 1;
							cal.set(year, month, date, hour, minute,seconde);
						}else{

							date = 31 - cal.get(Calendar.DATE);

							if(cal.get(Calendar.MONTH)>= 1){
								month = cal.get(Calendar.MONTH) - 1;
								cal.set(year, month, date, hour, minute,seconde);
							}else{
								month = 11 - cal.get(Calendar.MONTH);
								year = year - 1;
								cal.set(year, month, date, hour, minute,seconde);
							}
						}
					}
				}
			}
		}

		return cal.getTime();
	}

	public static MetricValues getCorrespondingMetricValue(Date expectedPeriod, List<MetricValues> tmp,int excludeValId, String timeFrame) {

		Calendar cperiod = Calendar.getInstance();
		cperiod.setTime(expectedPeriod);

		for (MetricValues metricValues : tmp) {

			if(metricValues != null && metricValues.getId() != excludeValId
					&& metricValues.getMvPeriodDate() != null){

				if(metricValues != null && expectedPeriod != null && metricValues.getMvPeriodDate() != null ){

					Calendar datePer = Calendar.getInstance();
					datePer.setTime(metricValues.getMvPeriodDate());

					if(timeFrame.trim().equalsIgnoreCase(IConstants.PERIODS[IConstants.PERIOD_YEARLY].trim())){

						if( datePer.get(Calendar.YEAR) == cperiod.get(Calendar.YEAR)){
							return metricValues;
						}

					}else if(timeFrame.trim().equalsIgnoreCase(IConstants.PERIODS[IConstants.PERIOD_BIANNUAL].trim())
							|| timeFrame.trim().equalsIgnoreCase(IConstants.PERIODS[IConstants.PERIOD_QUARTERLY].trim())
							|| timeFrame.trim().equalsIgnoreCase(IConstants.PERIODS[IConstants.PERIOD_MONTHLY].trim())){

						if( datePer.get(Calendar.YEAR) == cperiod.get(Calendar.YEAR)
								&& datePer.get(Calendar.MONTH) == cperiod.get(Calendar.MONTH)
						){
							return metricValues;
						}

					}else if(timeFrame.trim().equalsIgnoreCase(IConstants.PERIODS[IConstants.PERIOD_WEEKLY].trim())
							|| timeFrame.trim().equalsIgnoreCase(IConstants.PERIODS[IConstants.PERIOD_DAYLY].trim())){

						if( datePer.get(Calendar.YEAR) == cperiod.get(Calendar.YEAR)
								&& datePer.get(Calendar.MONTH) == cperiod.get(Calendar.MONTH)
								&& datePer.get(Calendar.DATE) == cperiod.get(Calendar.DATE)
						){
							return metricValues;
						}

					}else if(timeFrame.trim().equalsIgnoreCase(IConstants.PERIODS[IConstants.PERIOD_HOURLY].trim())){

						if( datePer.get(Calendar.YEAR) == cperiod.get(Calendar.YEAR)
								&& datePer.get(Calendar.MONTH) == cperiod.get(Calendar.MONTH)
								&& datePer.get(Calendar.DATE) == cperiod.get(Calendar.DATE)
								&& datePer.get(Calendar.HOUR_OF_DAY) == cperiod.get(Calendar.HOUR_OF_DAY)
						){
							return metricValues;
						}

					}else if(timeFrame.trim().equalsIgnoreCase(IConstants.PERIODS[IConstants.PERIOD_MINUTLY].trim())){

						if( datePer.get(Calendar.YEAR) == cperiod.get(Calendar.YEAR)
								&& datePer.get(Calendar.MONTH) == cperiod.get(Calendar.MONTH)
								&& datePer.get(Calendar.DATE) == cperiod.get(Calendar.DATE)
								&& datePer.get(Calendar.HOUR_OF_DAY) == cperiod.get(Calendar.HOUR_OF_DAY)
								&& datePer.get(Calendar.MINUTE) == cperiod.get(Calendar.MINUTE)
						){
							return metricValues;
						}

					}else if(timeFrame.trim().equalsIgnoreCase(IConstants.PERIODS[IConstants.PERIOD_SECONDLY].trim())){

						if( datePer.get(Calendar.YEAR) == cperiod.get(Calendar.YEAR)
								&& datePer.get(Calendar.MONTH) == cperiod.get(Calendar.MONTH)
								&& datePer.get(Calendar.DATE) == cperiod.get(Calendar.DATE)
								&& datePer.get(Calendar.HOUR_OF_DAY) == cperiod.get(Calendar.HOUR_OF_DAY)
								&& datePer.get(Calendar.MINUTE) == cperiod.get(Calendar.MINUTE)
								&& datePer.get(Calendar.SECOND) == cperiod.get(Calendar.SECOND)
						){
							return metricValues;
						}

					} 
				}
			}
		}
		return null;
	}

	/**
	 * @param date represents a specific instant in time, with millisecond precision
	 * @return the formatted string representing time "date".
	 */
	public static String getSQLDate(Date date) {
		if(date != null){

			SimpleDateFormat sdf = new SimpleDateFormat();

			sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			return sdf.format(date).trim();
		}
		return "";
	}

	public static String getBetweenForDatePeriode(String periode, Date date) {
		String between = "";
		if(periode.equalsIgnoreCase("YEAR")) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy");
			String year = format.format(date);
			between = "between '" + year + "-01-01" + "' AND '" + year + "-12-31" + "'";
		}
		else if(periode.equalsIgnoreCase("BIANNUAL")) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy");
			String year = format.format(date);
//			Calendar.getInstance().setTime(date);
//			int month = Calendar.getInstance().get(Calendar.MONTH);
			GregorianCalendar cal = new GregorianCalendar();
			cal.setTime(date);
			int month = cal.get(cal.MONTH);
			if(month < 6) {
				between = "between '" + year + "-01-01" + "' AND '" + year + "-06-30" + "'";
			}
			else {
				between = "between '" + year + "-07-01" + "' AND '" + year + "-12-31" + "'";
			}
		}
		else if(periode.equalsIgnoreCase("QUARTER")) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy");
			String year = format.format(date);
//			Calendar.getInstance().setTime(date);
//			int month = Calendar.getInstance().get(Calendar.MONTH);
			GregorianCalendar cal = new GregorianCalendar();
			cal.setTime(date);
			int month = cal.get(cal.MONTH);
			if(month < 3) {
				between = "between '" + year + "-01-01" + "' AND '" + year + "-03-31" + "'";
			}
			else if(month <6) {
				between = "between '" + year + "-04-01" + "' AND '" + year + "-06-30" + "'";
			}
			else if(month <9) {
				between = "between '" + year + "-07-01" + "' AND '" + year + "-09-30" + "'";
			}
			else {
				between = "between '" + year + "-10-01" + "' AND '" + year + "-12-31" + "'";
			}
		}
		else if(periode.equalsIgnoreCase("MONTH")) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
			String month = format.format(date);
			between = "between '" + month + "-01" + "' AND '" + month + "-31" + "'";
		}
		else if(periode.equalsIgnoreCase("WEEK")) {
			String dateDebut = "";
			String dateFin = "";
			Date d = new Date();
			d.setTime(date.getTime());
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			GregorianCalendar cal = new GregorianCalendar();
			cal.setFirstDayOfWeek(GregorianCalendar.MONDAY);
			cal.setTime(d);
			int day = cal.get(GregorianCalendar.DAY_OF_WEEK);
			if(day == 2) {
				dateDebut = format.format(d);
			}
			else if (day == 1){
				d.setDate(d.getDate() - 6);
				dateDebut = format.format(d);
			}
			else {
				d.setDate(d.getDate() - (day - 2));
				dateDebut = format.format(d);
			}
			d.setDate(d.getDate() + 6);
			dateFin = format.format(d);
			between = "between '" + dateDebut + "' AND '" + dateFin + "'";
		}
		else if(periode.equalsIgnoreCase("DAY")) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			String day = format.format(date);
			between = "= '" + day + "'";
		}
		else if(periode.equalsIgnoreCase("HOUR")) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH");
			String hour = format.format(date);
			between = "between '" + hour + ":00:00'" + "' AND '" + hour + ":59:59" + "'";;
		}
		
		return between;
	}
	
	public static boolean isInSamePeriod(Date date11, Date date22, String period) {
		
		Calendar date1 = Calendar.getInstance();
		date1.setTime(date11);
		Calendar date2 = Calendar.getInstance();
		date2.setTime(date22);
		
		if(period.equalsIgnoreCase(IConstants.PERIODS[IConstants.PERIOD_YEARLY].trim())){

			if( date1.get(Calendar.YEAR) == date2.get(Calendar.YEAR)){
				return true;
			}

		}else if(period.equalsIgnoreCase(IConstants.PERIODS[IConstants.PERIOD_BIANNUAL].trim())
				|| period.equalsIgnoreCase(IConstants.PERIODS[IConstants.PERIOD_QUARTERLY].trim())
				|| period.equalsIgnoreCase(IConstants.PERIODS[IConstants.PERIOD_MONTHLY].trim())){

			if( date1.get(Calendar.YEAR) == date2.get(Calendar.YEAR)
					&& date1.get(Calendar.MONTH) == date2.get(Calendar.MONTH)
			){
				return true;
			}

		}else if(period.equalsIgnoreCase(IConstants.PERIODS[IConstants.PERIOD_WEEKLY].trim())
				|| period.equalsIgnoreCase(IConstants.PERIODS[IConstants.PERIOD_DAYLY].trim())){

			if( date1.get(Calendar.YEAR) == date2.get(Calendar.YEAR)
					&& date1.get(Calendar.MONTH) == date2.get(Calendar.MONTH)
					&& date1.get(Calendar.DATE) == date2.get(Calendar.DATE)
			){
				return true;
			}

		}else if(period.equalsIgnoreCase(IConstants.PERIODS[IConstants.PERIOD_HOURLY].trim())){

			if( date1.get(Calendar.YEAR) == date2.get(Calendar.YEAR)
					&& date1.get(Calendar.MONTH) == date2.get(Calendar.MONTH)
					&& date1.get(Calendar.DATE) == date2.get(Calendar.DATE)
					&& date1.get(Calendar.HOUR_OF_DAY) == date2.get(Calendar.HOUR_OF_DAY)
			){
				return true;
			}

		}else if(period.equalsIgnoreCase(IConstants.PERIODS[IConstants.PERIOD_MINUTLY].trim())){

			if( date1.get(Calendar.YEAR) == date2.get(Calendar.YEAR)
					&& date1.get(Calendar.MONTH) == date2.get(Calendar.MONTH)
					&& date1.get(Calendar.DATE) == date2.get(Calendar.DATE)
					&& date1.get(Calendar.HOUR_OF_DAY) == date2.get(Calendar.HOUR_OF_DAY)
					&& date1.get(Calendar.MINUTE) == date2.get(Calendar.MINUTE)
			){
				return true;
			}

		}else if(period.equalsIgnoreCase(IConstants.PERIODS[IConstants.PERIOD_SECONDLY].trim())){

			if( date1.get(Calendar.YEAR) == date2.get(Calendar.YEAR)
					&& date1.get(Calendar.MONTH) == date2.get(Calendar.MONTH)
					&& date1.get(Calendar.DATE) == date2.get(Calendar.DATE)
					&& date1.get(Calendar.HOUR_OF_DAY) == date2.get(Calendar.HOUR_OF_DAY)
					&& date1.get(Calendar.MINUTE) == date2.get(Calendar.MINUTE)
					&& date1.get(Calendar.SECOND) == date2.get(Calendar.SECOND)
			){
				return true;
			}

		} 
		return false;
	}
}
