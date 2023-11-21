package bpm.fm.runtime.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bpm.fm.api.model.FactTable;

public class NonStandardSgbdHandler {
	
	protected static SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy-MM");
	protected static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	protected static SimpleDateFormat hourDateFormat = new SimpleDateFormat("yyyy-MM-dd HH");
	protected static SimpleDateFormat hourMinuteDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	private static String DATE_FORMAT_MYSQL = "%Y-%m-%d";
	private static String DATE_HOUR_FORMAT_MYSQL = "%Y-%m-%d %H";
	private static String DATE_HOUR_MINUTE_FORMAT_MYSQL = "%Y-%m-%d %H:%i";
	private static String DATE_FORMAT_PGSQL = "YYYY-MM-DD";
	private static String DATE_HOUR_FORMAT_PGSQL = "YYYY-MM-DD HH24";
	private static String DATE_HOUR_MINUTE_FORMAT_PGSQL = "YYYY-MM-DD HH24:MI";
	private static String DATE_FORMAT_ORACLE = "%Y-%m-%d";
	
	public static final String MYSQL = "com.mysql.jdbc.Driver";
	public static final String PGSQL = "org.postgresql.Driver";
	public static final String ORACLE = "oracle.jdbc.driver.OracleDriver";
	public static final String H2 = "org.h2.Driver";
	public static final String SQLSERVER = "net.sourceforge.jtds.jdbc.Driver";
	
	public static List<String> shitSgbds;
	static {
		shitSgbds = new ArrayList<>();
		shitSgbds.add(ORACLE);
		shitSgbds.add(SQLSERVER);
	}
	
	
	public static void generateSelectDate(String periodicity, String valueDateColumn, StringBuffer buf, String jdbcDriver, ValueCalculator valueCalculator) {
		if(jdbcDriver.equals(PGSQL) || jdbcDriver.equals(H2)) {
			switch (periodicity) {
				case FactTable.PERIODICITY_YEARLY:
					buf.append("	to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'YYYY') as " + ValueCalculator.ALIAS_YEAR + "\n");
					break;
				case FactTable.PERIODICITY_MONTHLY:
				case FactTable.PERIODICITY_BIANNUAL:
				case FactTable.PERIODICITY_QUARTERLY:
					buf.append("	to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'YYYY') as " + ValueCalculator.ALIAS_YEAR + ",\n");
					buf.append("	to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'MM') as " + ValueCalculator.ALIAS_MONTH + "\n");
					break;
				case FactTable.PERIODICITY_DAILY:
				case FactTable.PERIODICITY_WEEKLY:
					buf.append("	to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'YYYY') as " + ValueCalculator.ALIAS_YEAR + ",\n");
					buf.append("	to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'MM') as " + ValueCalculator.ALIAS_MONTH + ",\n");
					buf.append("	to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'DD') as " + ValueCalculator.ALIAS_DAY + "\n");
					break;
				case FactTable.PERIODICITY_HOURLY:
					buf.append("	to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'YYYY') as " + ValueCalculator.ALIAS_YEAR + ",\n");
					buf.append("	to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'MM') as " + ValueCalculator.ALIAS_MONTH + ",\n");
					buf.append("	to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'DD') as " + ValueCalculator.ALIAS_DAY + ",\n");
					buf.append("	to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'HH24') as " + ValueCalculator.ALIAS_HOUR + "\n");
					break;
				case FactTable.PERIODICITY_MINUTE:
					buf.append("	to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'YYYY') as " + ValueCalculator.ALIAS_YEAR + ",\n");
					buf.append("	to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'MM') as " + ValueCalculator.ALIAS_MONTH + ",\n");
					buf.append("	to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'DD') as " + ValueCalculator.ALIAS_DAY + ",\n");
					buf.append("	to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'HH24') as " + ValueCalculator.ALIAS_HOUR + ",\n");
					buf.append("	to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'MI') as " + ValueCalculator.ALIAS_MINUTE + "\n");
					break;
				default:
					break;
			}
		}
		else if(jdbcDriver.equals(ORACLE)) {
			switch (periodicity) {
				case FactTable.PERIODICITY_YEARLY:
					buf.append("	extract(year from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") as " + ValueCalculator.ALIAS_YEAR + "\n");
					break;
				case FactTable.PERIODICITY_MONTHLY:
				case FactTable.PERIODICITY_BIANNUAL:
				case FactTable.PERIODICITY_QUARTERLY:
					buf.append("	extract(year from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") as " + ValueCalculator.ALIAS_YEAR + ",\n");
					buf.append("	extract(month from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") as " + ValueCalculator.ALIAS_MONTH + "\n");
					break;
				case FactTable.PERIODICITY_DAILY:
				case FactTable.PERIODICITY_WEEKLY:
					buf.append("	extract(year from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") as " + ValueCalculator.ALIAS_YEAR + ",\n");
					buf.append("	extract(month from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") as " + ValueCalculator.ALIAS_MONTH + ",\n");
					buf.append("	extract(day from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") as " + ValueCalculator.ALIAS_DAY + "\n");
					break;
				case FactTable.PERIODICITY_HOURLY:
					buf.append("	extract(year from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") as " + ValueCalculator.ALIAS_YEAR + ",\n");
					buf.append("	extract(month from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") as " + ValueCalculator.ALIAS_MONTH + ",\n");
					buf.append("	extract(day from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") as " + ValueCalculator.ALIAS_DAY + ",\n");
					buf.append("	extract(hour from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") as " + ValueCalculator.ALIAS_HOUR + "\n");
					break;
				case FactTable.PERIODICITY_MINUTE:
					buf.append("	extract(year from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") as " + ValueCalculator.ALIAS_YEAR + ",\n");
					buf.append("	extract(month from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") as " + ValueCalculator.ALIAS_MONTH + ",\n");
					buf.append("	extract(day from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") as " + ValueCalculator.ALIAS_DAY + ",\n");
					buf.append("	extract(hour from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") as " + ValueCalculator.ALIAS_HOUR + ",\n");
					buf.append("	extract(minute from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") as " + ValueCalculator.ALIAS_MINUTE + "\n");
					break;
				default:
					break;
			}
		}
		else if(jdbcDriver.equals(SQLSERVER)) {
			switch (periodicity) {
				case FactTable.PERIODICITY_YEARLY:
					buf.append("	datepart(year, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") as " + ValueCalculator.ALIAS_YEAR + "\n");
					break;
				case FactTable.PERIODICITY_MONTHLY:
				case FactTable.PERIODICITY_BIANNUAL:
				case FactTable.PERIODICITY_QUARTERLY:
					buf.append("	datepart(year, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") as " + ValueCalculator.ALIAS_YEAR + ",\n");
					buf.append("	datepart(month, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") as " + ValueCalculator.ALIAS_MONTH + "\n");
					break;
				case FactTable.PERIODICITY_DAILY:
				case FactTable.PERIODICITY_WEEKLY:
					buf.append("	datepart(year, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") as " + ValueCalculator.ALIAS_YEAR + ",\n");
					buf.append("	datepart(month, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") as " + ValueCalculator.ALIAS_MONTH + ",\n");
					buf.append("	datepart(day, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") as " + ValueCalculator.ALIAS_DAY + "\n");
					break;
				case FactTable.PERIODICITY_HOURLY:
					buf.append("	datepart(year, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") as " + ValueCalculator.ALIAS_YEAR + ",\n");
					buf.append("	datepart(month, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") as " + ValueCalculator.ALIAS_MONTH + ",\n");
					buf.append("	datepart(day, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") as " + ValueCalculator.ALIAS_DAY + ",\n");
					buf.append("	datepart(hour, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") as " + ValueCalculator.ALIAS_HOUR + "\n");
					break;
				case FactTable.PERIODICITY_MINUTE:
					buf.append("	datepart(year, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") as " + ValueCalculator.ALIAS_YEAR + ",\n");
					buf.append("	datepart(month, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") as " + ValueCalculator.ALIAS_MONTH + ",\n");
					buf.append("	datepart(day, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") as " + ValueCalculator.ALIAS_DAY + ",\n");
					buf.append("	datepart(hour, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") as " + ValueCalculator.ALIAS_HOUR + ",\n");
					buf.append("	datepart(minute, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") as " + ValueCalculator.ALIAS_MINUTE + "\n");
					break;
				default:
					break;
			}
		}
		else {
			switch (periodicity) {
				case FactTable.PERIODICITY_YEARLY:
					buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%Y') as " + ValueCalculator.ALIAS_YEAR + "\n");
					break;
				case FactTable.PERIODICITY_MONTHLY:
				case FactTable.PERIODICITY_BIANNUAL:
				case FactTable.PERIODICITY_QUARTERLY:
					buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%Y') as " + ValueCalculator.ALIAS_YEAR + ",\n");
					buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%m') as " + ValueCalculator.ALIAS_MONTH + "\n");
					break;
				case FactTable.PERIODICITY_DAILY:
				case FactTable.PERIODICITY_WEEKLY:
					buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%Y') as " + ValueCalculator.ALIAS_YEAR + ",\n");
					buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%m') as " + ValueCalculator.ALIAS_MONTH + ",\n");
					buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%d') as " + ValueCalculator.ALIAS_DAY + "\n");
					break;
				case FactTable.PERIODICITY_HOURLY:
					buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%Y') as " + ValueCalculator.ALIAS_YEAR + ",\n");
					buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%m') as " + ValueCalculator.ALIAS_MONTH + ",\n");
					buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%d') as " + ValueCalculator.ALIAS_DAY + ",\n");
					buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%H') as " + ValueCalculator.ALIAS_HOUR + "\n");
					break;
				case FactTable.PERIODICITY_MINUTE:
					buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%Y') as " + ValueCalculator.ALIAS_YEAR + ",\n");
					buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%m') as " + ValueCalculator.ALIAS_MONTH + ",\n");
					buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%d') as " + ValueCalculator.ALIAS_DAY + ",\n");
					buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%H') as " + ValueCalculator.ALIAS_HOUR + ",\n");
					buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%i') as " + ValueCalculator.ALIAS_MINUTE + "\n");
					break;
				default:
					break;
			}
		}
	}
	
	public static List<String> generateDateFilter(String periodicity, Date startDate, Date endDate, String valueDateColumn, StringBuffer buf, boolean getPrevious, String jdbcDriver, ValueCalculator valueCalculator, boolean isLoader) {
		List<String> columns = new ArrayList<>();
		if(jdbcDriver.equals(PGSQL) || jdbcDriver.equals(H2)) {
			switch (periodicity) {
			case FactTable.PERIODICITY_YEARLY:
				if (getPrevious) {
					buf.append("	CAST(to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'YYYY') AS integer) <= " + (startDate.getYear() + 1900) + " and \n");
					buf.append("	CAST(to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'YYYY') AS integer) >= " + (startDate.getYear() + 1900 - 12) + "\n");
				}
				else {
					if(endDate != null) {		
						buf.append("	(CAST(to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'YYYY') AS integer) <= " + (endDate.getYear() + 1900) + "\n and ");
						buf.append("	CAST(to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'YYYY') AS integer) >= " + (startDate.getYear() + 1900) + ")\n");
					}
					else {
						if(isLoader) {
							buf.append("	CAST(to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'YYYY') AS integer) = " + (startDate.getYear() + 1900) + "\n");
						}
						else {
							buf.append("	(CAST(to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'YYYY') AS integer) = " + (startDate.getYear() + 1900) + "\n or ");
							buf.append("	CAST(to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'YYYY') AS integer) = " + (startDate.getYear() + 1900 - 1) + ")\n");
						}
					}
				}
				
				break;
			case FactTable.PERIODICITY_MONTHLY:
			case FactTable.PERIODICITY_BIANNUAL:
			case FactTable.PERIODICITY_QUARTERLY:
				if (getPrevious) {
					String stringDate = dateFormat.format(startDate);
					
					if(jdbcDriver.equals(PGSQL)) {
					
						buf.append("	" + valueCalculator.getValueTableName() + "." + valueDateColumn + " <= '" + stringDate + "' and \n");
						buf.append("	" + valueCalculator.getValueTableName() + "." + valueDateColumn + " >= date '" + stringDate + "' - INTERVAL '12 months'\n");
					}
					else {
						buf.append("	" + valueCalculator.getValueTableName() + "." + valueDateColumn + " <= '" + stringDate + "' and \n");
						buf.append("	" + valueCalculator.getValueTableName() + "." + valueDateColumn + " >= dateadd('MONTH', -12, date '" + stringDate + "')\n");
					}
					
				}
				else {
					if(endDate != null) {
						String start = dateFormat.format(startDate);
						String end = dateFormat.format(endDate);
		
						buf.append("	" + valueCalculator.getValueTableName() + "." + valueDateColumn + " <= '" + end + "' and \n");
						buf.append("	" + valueCalculator.getValueTableName() + "." + valueDateColumn + " >= '" + start + "'\n");
					}
					else {
						if(isLoader) {
							buf.append("	CAST(to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'YYYY') AS integer) = " + (startDate.getYear() + 1900) + " and \n");
							buf.append("	CAST(to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'MM') AS integer) = " + (startDate.getMonth() + 1) + "\n");
						}
						else {
							buf.append("	CAST(to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'YYYY') AS integer) = " + (startDate.getYear() + 1900) + " and \n");
							buf.append("	(CAST(to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'MM') AS integer) = " + (startDate.getMonth() + 1) + "\n or ");
							buf.append("	CAST(to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'MM') AS integer) = " + (startDate.getMonth() + 1 - 1) + ")\n");
						}
					}
				}

				break;
			case FactTable.PERIODICITY_DAILY:
			case FactTable.PERIODICITY_WEEKLY:
				if (getPrevious) {
					String stringDate = dateFormat.format(startDate);
					buf.append("	" + valueCalculator.getValueTableName() + "." + valueDateColumn + " <= '" + stringDate + "' and \n");
					buf.append("	" + valueCalculator.getValueTableName() + "." + valueDateColumn + " >= date '" + stringDate + "' - INTERVAL '12 days'\n");
				}
				else {
					if(endDate != null) {
						String start = dateFormat.format(startDate);
						String end = dateFormat.format(endDate);
								
						buf.append("	" + valueCalculator.getValueTableName() + "." + valueDateColumn + " <= '" + end + "' and \n");
						buf.append("	" + valueCalculator.getValueTableName() + "." + valueDateColumn + " >= '" + start + "'\n");
					}
					else {
						if(isLoader) {
							buf.append("	CAST(to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'YYYY') AS integer) = " + (startDate.getYear() + 1900) + " and \n");
							buf.append("	CAST(to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'MM') AS integer) = " + (startDate.getMonth() + 1) + " and \n");
							buf.append("	CAST(to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'DD') AS integer) = " + startDate.getDate() + "\n");
						}
						else {
							buf.append("	CAST(to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'YYYY') AS integer) = " + (startDate.getYear() + 1900) + " and \n");
							buf.append("	CAST(to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'MM') AS integer) = " + (startDate.getMonth() + 1) + " and \n");
							buf.append("	(CAST(to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'DD') AS integer) = " + startDate.getDate() + "\n or ");
							buf.append("	CAST(to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'DD') AS integer) = " + (startDate.getDate() - 1) + ")\n");
						}
					}
				}

				break;
			
			case FactTable.PERIODICITY_HOURLY:
				if (getPrevious) {
					String stringDate = hourDateFormat.format(startDate);
					buf.append("	" + valueCalculator.getValueTableName() + "." + valueDateColumn + " <= '" + stringDate + "' and \n");
					buf.append("	" + valueCalculator.getValueTableName() + "." + valueDateColumn + " >= date '" + stringDate + "' - INTERVAL '12 hours'\n");
				}
				else {
					if(endDate != null) {
						String start = hourDateFormat.format(startDate);
						String end = hourDateFormat.format(endDate);
		
						buf.append("	" + valueCalculator.getValueTableName() + "." + valueDateColumn + " <= '" + end + "' and \n");
						buf.append("	" + valueCalculator.getValueTableName() + "." + valueDateColumn + " >= '" + start + "'\n");
					}
					else {
						if(isLoader) {
							buf.append("	CAST(to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'YYYY') AS integer) = " + (startDate.getYear() + 1900) + " and \n");
							buf.append("	CAST(to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'MM') AS integer) = " + (startDate.getMonth() + 1) + " and \n");
							buf.append("	CAST(to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'DD') AS integer) = " + startDate.getDate() + " and \n");
							buf.append("	CAST(to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'HH24') AS integer) = " + (startDate.getHours()) + "\n");
						}
						else {
							buf.append("	CAST(to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'YYYY') AS integer) = " + (startDate.getYear() + 1900) + " and \n");
							buf.append("	CAST(to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'MM') AS integer) = " + (startDate.getMonth() + 1) + " and \n");
							buf.append("	CAST(to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'DD') AS integer) = " + startDate.getDate() + " and \n");
							buf.append("	(CAST(to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'HH24') AS integer) = " + (startDate.getHours()) + " or \n");
							buf.append("	CAST(to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'HH24') AS integer) = " + (startDate.getHours() - 1) + ") \n");
						}
					}
				}
				break;
				
			case FactTable.PERIODICITY_MINUTE:
				if (getPrevious) {
					String stringDate = hourMinuteDateFormat.format(startDate);
					buf.append("	" + valueCalculator.getValueTableName() + "." + valueDateColumn + " <= '" + stringDate + "' and \n");
					buf.append("	" + valueCalculator.getValueTableName() + "." + valueDateColumn + " >= date '" + stringDate + "' - INTERVAL '12 minutes'\n");
				}
				else {
					if(endDate != null) {
						String start = hourMinuteDateFormat.format(startDate);
						String end = hourMinuteDateFormat.format(endDate);
		
						buf.append("	" + valueCalculator.getValueTableName() + "." + valueDateColumn + " <= '" + end + "' and \n");
						buf.append("	" + valueCalculator.getValueTableName() + "." + valueDateColumn + " >= '" + start + "'\n");
					}
					else {
						if(isLoader) {
							buf.append("	CAST(to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'YYYY') AS integer) = " + (startDate.getYear() + 1900) + " and \n");
							buf.append("	CAST(to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'MM') AS integer) = " + (startDate.getMonth() + 1) + " and \n");
							buf.append("	CAST(to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'DD') AS integer) = " + startDate.getDate() + " and \n");
							buf.append("	CAST(to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'HH24') AS integer) = " + (startDate.getHours()) + " and \n");
							buf.append("	CAST(to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'MI') AS integer) = " + (startDate.getMinutes()) + "\n");
						}
						else {
							buf.append("	CAST(to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'YYYY') AS integer) = " + (startDate.getYear() + 1900) + " and \n");
							buf.append("	CAST(to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'MM') AS integer) = " + (startDate.getMonth() + 1) + " and \n");
							buf.append("	CAST(to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'DD') AS integer) = " + startDate.getDate() + " and \n");
							buf.append("	CAST(to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'HH24') AS integer) = " + (startDate.getHours()) + " and \n");
							buf.append("	(CAST(to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'MI') AS integer) = " + (startDate.getMinutes()) + " or \n");
							buf.append("	CAST(to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'MI') AS integer) = " + (startDate.getMinutes() - 1) + ") \n");
						}
					}
				}
				break;
				
			default:
				break;
			}
		}
		else if(jdbcDriver.equals(ORACLE)) {
			//XXX
			switch (periodicity) {
			case FactTable.PERIODICITY_YEARLY:
				if (getPrevious) {
					buf.append("	extract(year from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") <= " + (startDate.getYear() + 1900) + " and \n");
					buf.append("	extract(year from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") >= " + (startDate.getYear() + 1900 - 12) + "\n");
				}
				else {
					if(endDate != null) {
		
						buf.append("	extract(year from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") <= " + (endDate.getYear() + 1900) + " and \n");
						buf.append("	extract(year from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") >= " + (startDate.getYear() + 1900) + "\n");
					}
					else {
						if(isLoader) {
							buf.append("	extract(year from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + (startDate.getYear() + 1900) + "\n");
						}
						else {
							buf.append("	(extract(year from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + (startDate.getYear() + 1900) + "\n or ");
							buf.append("	extract(year from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + (startDate.getYear() + 1900 - 1) + ")\n");
						}
					}
				}
				columns.add("extract(year from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ")");
				break;
			case FactTable.PERIODICITY_MONTHLY:
			case FactTable.PERIODICITY_BIANNUAL:
			case FactTable.PERIODICITY_QUARTERLY:
				if (getPrevious) {
					String stringDate = dateFormat.format(startDate);
					buf.append("	concat(extract(year from " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), '-', extract(month, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ")) <= '" + stringDate + "' and \n");
					buf.append("	concat(extract(year from " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), '-', extract(month, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ")) >= DATEADD(month, -12, '" + stringDate + "')\n");
					
				}
				else {
					if(endDate != null) {
						String start = monthFormat.format(startDate);
						String end = monthFormat.format(endDate);
		
						buf.append("	concat(extract(year from " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), '-', extract(month, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ")) <= '" + end + "' and \n");
						buf.append("	concat(extract(year from " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), '-', extract(month, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ")) >= '" + start + "' \n");
					}
					else {
						if(isLoader) {
							buf.append("	extract(year from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + (startDate.getYear() + 1900) + " and \n");
							buf.append("	extract(month from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + (startDate.getMonth() + 1) + "\n");
						}
						else {
							buf.append("	extract(year from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + (startDate.getYear() + 1900) + " and \n");
							buf.append("	(extract(month from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + (startDate.getMonth() + 1) + "\n or ");
							buf.append("	extract(month from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + (startDate.getMonth() + 1 - 1) + ")\n");
						}
					}
				}
				columns.add("extract(year from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ")");
				columns.add("extract(month from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ")");
				break;
			case FactTable.PERIODICITY_DAILY:
			case FactTable.PERIODICITY_WEEKLY:
				if (getPrevious) {
					String stringDate = dateFormat.format(startDate);
					buf.append("	concat(extract(year from " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), '-', extract(month from " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), '-', extract(day, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ")) <= '" + stringDate + "' and \n");
					buf.append("	concat(extract(year from " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), '-', extract(month from " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), '-', extract(day, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ")) >= DATEADD(day, -12, '" + stringDate + "')\n");
				}
				else {
					if(endDate != null) {
						String start = dateFormat.format(startDate);
						String end = dateFormat.format(endDate);
						
						String sqlDateFormat = DATE_FORMAT_MYSQL;
						
						buf.append("	concat(extract(year from " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), '-', extract(month from " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), '-', extract(day, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ")) <= '" + end + "' and \n");		
						buf.append("	concat(extract(year from " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), '-', extract(month from " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), '-', extract(day, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ")) >= '" + start + "' \n");		
						
					}
					else {
						if(isLoader) {
							buf.append("	extract(year from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + (startDate.getYear() + 1900) + " and \n");
							buf.append("	extract(month from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + (startDate.getMonth() + 1) + " and \n");
							buf.append("	extract(day from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + startDate.getDate() + "\n");
						}
						else {
							buf.append("	extract(year from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + (startDate.getYear() + 1900) + " and \n");
							buf.append("	extract(month from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + (startDate.getMonth() + 1) + " and \n");
							buf.append("	(extract(day from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + startDate.getDate() + "\n or ");
							buf.append("	extract(day from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + (startDate.getDate() - 1) + ")\n");							
						}

					}
				}
				columns.add("extract(year from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ")");
				columns.add("extract(month from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ")");
				columns.add("extract(day from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ")");
				break;
			case FactTable.PERIODICITY_HOURLY:
				if (getPrevious) {
					String stringDate = hourDateFormat.format(startDate);
					buf.append("	concat(extract(year from " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), '-', extract(month from " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), '-', extract(day from " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), ' ', extract(hour, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ")) <= '" + stringDate + "' and \n");
					buf.append("	concat(extract(year from " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), '-', extract(month from " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), '-', extract(day from " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), ' ', extract(hour, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ")) >= DATEADD(hour, -12, '" + stringDate + "')\n");
				}
				else {
					if(endDate != null) {
						String start = hourDateFormat.format(startDate);
						String end = hourDateFormat.format(endDate);
						
						String sqlDateFormat = DATE_HOUR_FORMAT_MYSQL;
						
						buf.append("	concat(extract(year from " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), '-', extract(month from " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), '-', extract(day from " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), ' ', extract(hour, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ")) <= '" + end + "' and \n");		
						buf.append("	concat(extract(year from " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), '-', extract(month from " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), '-', extract(day from " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), ' ', extract(hour, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ")) >= '" + start + "' \n");		
						
					}
					else {
						if(isLoader) {
							
							buf.append("	extract(year from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + (startDate.getYear() + 1900) + " and \n");
							buf.append("	extract(month from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + (startDate.getMonth() + 1) + " and \n");
							buf.append("	extract(day from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + startDate.getDate() + " and \n");
							buf.append("	extract(hour from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + startDate.getHours() + "\n");
						}
						else {
							
							buf.append("	extract(year from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + (startDate.getYear() + 1900) + " and \n");
							buf.append("	extract(month from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + (startDate.getMonth() + 1) + " and \n");
							buf.append("	extract(day from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + startDate.getDate() + " and \n");
							buf.append("	(extract(hour from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + startDate.getHours() + " or \n");
							buf.append("	extract(hour from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + (startDate.getHours() - 1) + ")\n");
						}
					}
				}
				columns.add("extract(year from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ")");
				columns.add("extract(month from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ")");
				columns.add("extract(day from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ")");
				columns.add("extract(hour from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ")");
				break;
				
			case FactTable.PERIODICITY_MINUTE:
				if (getPrevious) {
					String stringDate = hourMinuteDateFormat.format(startDate);
					buf.append("	concat(extract(year from " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), '-', extract(month from " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), '-', extract(day from " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), ' ', extract(hour from " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), ':', extract(minute, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ")) <= '" + stringDate + "' and \n");
					buf.append("	concat(extract(year from " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), '-', extract(month from " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), '-', extract(day from " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), ' ', extract(hour from " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), ':', extract(minute, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ")) >= DATEADD(minute, -12, '" + stringDate + "')\n");
				}
				else {
					if(endDate != null) {
						String start = hourMinuteDateFormat.format(startDate);
						String end = hourMinuteDateFormat.format(endDate);
						
						String sqlDateFormat = DATE_HOUR_MINUTE_FORMAT_MYSQL;
						
						buf.append("	concat(extract(year from " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), '-', extract(month from " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), '-', extract(day from " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), ' ', extract(hour from " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), ':', extract(minute, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ")) <= '" + end + "' and \n");		
						buf.append("	concat(extract(year from " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), '-', extract(month from " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), '-', extract(day from " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), ' ', extract(hour from " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), ':', extract(minute, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ")) >= '" + start + "' \n");		
						
					}
					else {
						if(isLoader) {
							
							buf.append("	extract(year from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + (startDate.getYear() + 1900) + " and \n");
							buf.append("	extract(month from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + (startDate.getMonth() + 1) + " and \n");
							buf.append("	extract(day from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + startDate.getDate() + " and \n");
							buf.append("	extract(hour from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + startDate.getHours() + " and \n");
							buf.append("	extract(minute from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + startDate.getMinutes() + "\n");
						}
						else {
							
							buf.append("	extract(year from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + (startDate.getYear() + 1900) + " and \n");
							buf.append("	extract(month from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + (startDate.getMonth() + 1) + " and \n");
							buf.append("	extract(day from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + startDate.getDate() + " and \n");
							buf.append("	extract(hour from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + startDate.getHours() + " and \n");
							buf.append("	(extract(minute from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + startDate.getMinutes() + " or \n");
							buf.append("	extract(minute from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + (startDate.getMinutes() - 1) + ")\n");
						}
					}
				}
				columns.add("extract(year from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ")");
				columns.add("extract(month from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ")");
				columns.add("extract(day from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ")");
				columns.add("extract(hour from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ")");
				columns.add("extract(minute from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ")");
				break;

			default:
				break;
			}
			//XXX
		}
		else if(jdbcDriver.equals(SQLSERVER)) {
			//XXX
			switch (periodicity) {
			case FactTable.PERIODICITY_YEARLY:
				if (getPrevious) {
					buf.append("	datepart(year, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") <= " + (startDate.getYear() + 1900) + " and \n");
					buf.append("	datepart(year, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") >= " + (startDate.getYear() + 1900 - 12) + "\n");
				}
				else {
					if(endDate != null) {
		
						buf.append("	datepart(year, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") <= " + (endDate.getYear() + 1900) + " and \n");
						buf.append("	datepart(year, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") >= " + (startDate.getYear() + 1900) + "\n");
					}
					else {
						if(isLoader) {
							buf.append("	datepart(year, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + (startDate.getYear() + 1900) + "\n");
						}
						else {
							buf.append("	(datepart(year, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + (startDate.getYear() + 1900) + "\n or ");
							buf.append("	datepart(year, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + (startDate.getYear() + 1900 - 1) + ")\n");
						}
					}
				}
				columns.add("datepart(year, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ")");
				break;
			case FactTable.PERIODICITY_MONTHLY:
			case FactTable.PERIODICITY_BIANNUAL:
			case FactTable.PERIODICITY_QUARTERLY:
				if (getPrevious) {
					String stringDate = dateFormat.format(startDate);
					buf.append("	concat(datepart(year, " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), '-', datepart(month, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ")) <= '" + stringDate + "' and \n");
					buf.append("	concat(datepart(year, " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), '-', datepart(month, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ")) >= DATEADD(month, -12, '" + stringDate + "')\n");
					
				}
				else {
					if(endDate != null) {
						String start = monthFormat.format(startDate);
						String end = monthFormat.format(endDate);
		
						buf.append("	concat(datepart(year, " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), '-', datepart(month, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ")) <= '" + end + "' and \n");
						buf.append("	concat(datepart(year, " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), '-', datepart(month, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ")) >= '" + start + "' \n");
					}
					else {
						if(isLoader) {
							buf.append("	datepart(year, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + (startDate.getYear() + 1900) + " and \n");
							buf.append("	datepart(month, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + (startDate.getMonth() + 1) + "\n");
						}
						else {
							buf.append("	datepart(year, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + (startDate.getYear() + 1900) + " and \n");
							buf.append("	(datepart(month, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + (startDate.getMonth() + 1) + "\n or ");
							buf.append("	datepart(month, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + (startDate.getMonth() + 1 - 1) + ")\n");
						}
					}
				}
				columns.add("datepart(year, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ")");
				columns.add("datepart(month, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ")");
				break;
			case FactTable.PERIODICITY_DAILY:
			case FactTable.PERIODICITY_WEEKLY:
				if (getPrevious) {
					String stringDate = dateFormat.format(startDate);
					buf.append("	concat(datepart(year, " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), '-', datepart(month, " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), '-', datepart(day, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ")) <= '" + stringDate + "' and \n");
					buf.append("	concat(datepart(year, " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), '-', datepart(month, " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), '-', datepart(day, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ")) >= DATEADD(day, -12, '" + stringDate + "')\n");
				}
				else {
					if(endDate != null) {
						String start = dateFormat.format(startDate);
						String end = dateFormat.format(endDate);
						
						String sqlDateFormat = DATE_FORMAT_MYSQL;
						
						buf.append("	concat(datepart(year, " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), '-', datepart(month, " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), '-', datepart(day, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ")) <= '" + end + "' and \n");		
						buf.append("	concat(datepart(year, " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), '-', datepart(month, " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), '-', datepart(day, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ")) >= '" + start + "' \n");		
						
					}
					else {
						if(isLoader) {
							buf.append("	datepart(year, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + (startDate.getYear() + 1900) + " and \n");
							buf.append("	datepart(month, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + (startDate.getMonth() + 1) + " and \n");
							buf.append("	datepart(day, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + startDate.getDate() + "\n");
						}
						else {
							buf.append("	datepart(year, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + (startDate.getYear() + 1900) + " and \n");
							buf.append("	datepart(month, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + (startDate.getMonth() + 1) + " and \n");
							buf.append("	(datepart(day, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + startDate.getDate() + "\n or ");
							buf.append("	datepart(day, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + (startDate.getDate() - 1) + ")\n");							
						}

					}
				}
				columns.add("datepart(year, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ")");
				columns.add("datepart(month, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ")");
				columns.add("datepart(day, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ")");
				break;
			case FactTable.PERIODICITY_HOURLY:
				if (getPrevious) {
					String stringDate = hourDateFormat.format(startDate);
					buf.append("	concat(datepart(year, " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), '-', datepart(month, " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), '-', datepart(day, " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), ' ', datepart(hour, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ")) <= '" + stringDate + "' and \n");
					buf.append("	concat(datepart(year, " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), '-', datepart(month, " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), '-', datepart(day, " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), ' ', datepart(hour, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ")) >= DATEADD(hour, -12, '" + stringDate + "')\n");
				}
				else {
					if(endDate != null) {
						String start = hourDateFormat.format(startDate);
						String end = hourDateFormat.format(endDate);
						
						String sqlDateFormat = DATE_HOUR_FORMAT_MYSQL;
						
						buf.append("	concat(datepart(year, " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), '-', datepart(month, " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), '-', datepart(day, " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), ' ', datepart(hour, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ")) <= '" + end + "' and \n");		
						buf.append("	concat(datepart(year, " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), '-', datepart(month, " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), '-', datepart(day, " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), ' ', datepart(hour, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ")) >= '" + start + "' \n");		
						
					}
					else {
						if(isLoader) {
							
							buf.append("	datepart(year, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + (startDate.getYear() + 1900) + " and \n");
							buf.append("	datepart(month, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + (startDate.getMonth() + 1) + " and \n");
							buf.append("	datepart(day, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + startDate.getDate() + " and \n");
							buf.append("	datepart(hour, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + startDate.getHours() + "\n");
						}
						else {
							
							buf.append("	datepart(year, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + (startDate.getYear() + 1900) + " and \n");
							buf.append("	datepart(month, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + (startDate.getMonth() + 1) + " and \n");
							buf.append("	datepart(day, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + startDate.getDate() + " and \n");
							buf.append("	(datepart(hour, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + startDate.getHours() + " or \n");
							buf.append("	datepart(hour, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + (startDate.getHours() - 1) + ")\n");
						}
					}
				}
				columns.add("datepart(year, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ")");
				columns.add("datepart(month, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ")");
				columns.add("datepart(day, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ")");
				columns.add("datepart(hour, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ")");
				break;
				
			case FactTable.PERIODICITY_MINUTE:
				if (getPrevious) {
					String stringDate = hourMinuteDateFormat.format(startDate);
					buf.append("	concat(datepart(year, " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), '-', datepart(month, " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), '-', datepart(day, " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), ' ', datepart(hour, " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), ':', datepart(minute, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ")) <= '" + stringDate + "' and \n");
					buf.append("	concat(datepart(year, " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), '-', datepart(month, " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), '-', datepart(day, " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), ' ', datepart(hour, " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), ':', datepart(minute, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ")) >= DATEADD(minute, -12, '" + stringDate + "')\n");
				}
				else {
					if(endDate != null) {
						String start = hourMinuteDateFormat.format(startDate);
						String end = hourMinuteDateFormat.format(endDate);
						
						String sqlDateFormat = DATE_HOUR_MINUTE_FORMAT_MYSQL;
						
						buf.append("	concat(datepart(year, " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), '-', datepart(month, " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), '-', datepart(day, " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), ' ', datepart(hour, " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), ':', datepart(minute, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ")) <= '" + end + "' and \n");		
						buf.append("	concat(datepart(year, " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), '-', datepart(month, " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), '-', datepart(day, " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), ' ', datepart(hour, " + valueCalculator.getValueTableName() + "." + valueDateColumn + "), ':', datepart(minute, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ")) >= '" + start + "' \n");		
						
					}
					else {
						if(isLoader) {
							
							buf.append("	datepart(year, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + (startDate.getYear() + 1900) + " and \n");
							buf.append("	datepart(month, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + (startDate.getMonth() + 1) + " and \n");
							buf.append("	datepart(day, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + startDate.getDate() + " and \n");
							buf.append("	datepart(hour, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + startDate.getHours() + " and \n");
							buf.append("	datepart(minute, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + startDate.getMinutes() + "\n");
						}
						else {
							
							buf.append("	datepart(year, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + (startDate.getYear() + 1900) + " and \n");
							buf.append("	datepart(month, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + (startDate.getMonth() + 1) + " and \n");
							buf.append("	datepart(day, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + startDate.getDate() + " and \n");
							buf.append("	datepart(hour, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + startDate.getHours() + " and \n");
							buf.append("	(datepart(minute, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + startDate.getMinutes() + " or \n");
							buf.append("	datepart(minute, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = " + (startDate.getMinutes() - 1) + ")\n");
						}
					}
				}
				columns.add("datepart(year, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ")");
				columns.add("datepart(month, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ")");
				columns.add("datepart(day, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ")");
				columns.add("datepart(hour, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ")");
				columns.add("datepart(minute, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ")");
				break;

			default:
				break;
			}
			//XXX
		}
		else {
			switch (periodicity) {
			case FactTable.PERIODICITY_YEARLY:
				if (getPrevious) {
					buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%Y') <= " + (startDate.getYear() + 1900) + " and \n");
					buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%Y') >= " + (startDate.getYear() + 1900 - 12) + "\n");
				}
				else {
					if(endDate != null) {
		
						buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%Y') <= " + (endDate.getYear() + 1900) + " and \n");
						buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%Y') >= " + (startDate.getYear() + 1900) + "\n");
					}
					else {
						if(isLoader) {
							buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%Y') = " + (startDate.getYear() + 1900) + "\n");
						}
						else {
							buf.append("	(date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%Y') = " + (startDate.getYear() + 1900) + "\n or ");
							buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%Y') = " + (startDate.getYear() + 1900 - 1) + ")\n");
						}
					}
				}
				
				break;
			case FactTable.PERIODICITY_MONTHLY:
			case FactTable.PERIODICITY_BIANNUAL:
			case FactTable.PERIODICITY_QUARTERLY:
				if (getPrevious) {
					String stringDate = dateFormat.format(startDate);
					buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%Y-%m') <= '" + stringDate + "' and \n");
					buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%Y-%m') >= date_sub('" + stringDate + "', INTERVAL 12 MONTH)\n");
					
				}
				else {
					if(endDate != null) {
						String start = monthFormat.format(startDate);
						String end = monthFormat.format(endDate);
		
						buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%Y-%m') <= '" + end + "' and \n");
						buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%Y-%m') >= '" + start + "' \n");
					}
					else {
						if(isLoader) {
							buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%Y') = " + (startDate.getYear() + 1900) + " and \n");
							buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%m') = " + (startDate.getMonth() + 1) + "\n");
						}
						else {
							buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%Y') = " + (startDate.getYear() + 1900) + " and \n");
							buf.append("	(date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%m') = " + (startDate.getMonth() + 1) + "\n or ");
							buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%m') = " + (startDate.getMonth() + 1 - 1) + ")\n");
						}
					}
				}

				break;
			case FactTable.PERIODICITY_DAILY:
			case FactTable.PERIODICITY_WEEKLY:
				if (getPrevious) {
					String stringDate = dateFormat.format(startDate);
					buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'" + DATE_FORMAT_MYSQL + "') <= '" + stringDate + "' and \n");
					buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'" + DATE_FORMAT_MYSQL + "') >= date_sub('" + stringDate + "', INTERVAL 12 DAY)\n");
				}
				else {
					if(endDate != null) {
						String start = dateFormat.format(startDate);
						String end = dateFormat.format(endDate);
						
						String sqlDateFormat = DATE_FORMAT_MYSQL;
						
						buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'" + sqlDateFormat + "') <= '" + end + "' and \n");		
						buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'" + sqlDateFormat + "') >= '" + start + "' \n");		
						
					}
					else {
						if(isLoader) {
							buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%Y') = " + (startDate.getYear() + 1900) + " and \n");
							buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%m') = " + (startDate.getMonth() + 1) + " and \n");
							buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%d') = " + startDate.getDate() + "\n");
						}
						else {
							buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%Y') = " + (startDate.getYear() + 1900) + " and \n");
							buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%m') = " + (startDate.getMonth() + 1) + " and \n");
							buf.append("	(date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%d') = " + startDate.getDate() + "\n or ");
							buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%d') = " + (startDate.getDate() - 1) + ")\n");							
						}

					}
				}

				break;
			case FactTable.PERIODICITY_HOURLY:
				if (getPrevious) {
					String stringDate = hourDateFormat.format(startDate);
					buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'" + DATE_HOUR_FORMAT_MYSQL + "') <= '" + stringDate + "' and \n");
					buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'" + DATE_HOUR_FORMAT_MYSQL + "') >= date_sub('" + stringDate + "', INTERVAL 12 HOUR)\n");
				}
				else {
					if(endDate != null) {
						String start = hourDateFormat.format(startDate);
						String end = hourDateFormat.format(endDate);
						
						String sqlDateFormat = DATE_HOUR_FORMAT_MYSQL;
						
						buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'" + sqlDateFormat + "') <= '" + end + "' and \n");		
						buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'" + sqlDateFormat + "') >= '" + start + "' \n");		
						
					}
					else {
						if(isLoader) {
							
							buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%Y') = " + (startDate.getYear() + 1900) + " and \n");
							buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%m') = " + (startDate.getMonth() + 1) + " and \n");
							buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%d') = " + startDate.getDate() + " and \n");
							buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%H') = " + startDate.getHours() + "\n");
						}
						else {
							
							buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%Y') = " + (startDate.getYear() + 1900) + " and \n");
							buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%m') = " + (startDate.getMonth() + 1) + " and \n");
							buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%d') = " + startDate.getDate() + " and \n");
							buf.append("	(date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%H') = " + startDate.getHours() + " or \n");
							buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%H') = " + (startDate.getHours() - 1) + ")\n");
						}
					}
				}
				break;
				
			case FactTable.PERIODICITY_MINUTE:
				if (getPrevious) {
					String stringDate = hourMinuteDateFormat.format(startDate);
					buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'" + DATE_HOUR_MINUTE_FORMAT_MYSQL + "') <= '" + stringDate + "' and \n");
					buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'" + DATE_HOUR_MINUTE_FORMAT_MYSQL + "') >= date_sub('" + stringDate + "', INTERVAL 12 MINUTE)\n");
				}
				else {
					if(endDate != null) {
						String start = hourMinuteDateFormat.format(startDate);
						String end = hourMinuteDateFormat.format(endDate);
						
						String sqlDateFormat = DATE_HOUR_MINUTE_FORMAT_MYSQL;
						
						buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'" + sqlDateFormat + "') <= '" + end + "' and \n");		
						buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'" + sqlDateFormat + "') >= '" + start + "' \n");		
						
					}
					else {
						if(isLoader) {
							
							buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%Y') = " + (startDate.getYear() + 1900) + " and \n");
							buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%m') = " + (startDate.getMonth() + 1) + " and \n");
							buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%d') = " + startDate.getDate() + " and \n");
							buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%H') = " + startDate.getHours() + " and \n");
							buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%i') = " + startDate.getMinutes() + "\n");
						}
						else {
							
							buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%Y') = " + (startDate.getYear() + 1900) + " and \n");
							buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%m') = " + (startDate.getMonth() + 1) + " and \n");
							buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%d') = " + startDate.getDate() + " and \n");
							buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%H') = " + startDate.getHours() + " and \n");
							buf.append("	(date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%i') = " + startDate.getMinutes() + " or \n");
							buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%i') = " + (startDate.getMinutes() - 1) + ")\n");
						}
					}
				}
				break;

			default:
				break;
			}
		}
		return columns;
	}
	
	public static String generateDateRelation(String periodicity, String valueDateColumn, String objDateColumn, String driver, ValueCalculator valueCalculator) {
		StringBuilder buf = new StringBuilder();
		if(driver.equals(PGSQL) || driver.equals(H2)) {
			switch (periodicity) {
			case FactTable.PERIODICITY_YEARLY:
				buf.append("	to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'YYYY') = to_char(" + valueCalculator.getObjectiveTableName() + "." + objDateColumn + ",'YYYY') and \n");
				break;
			case FactTable.PERIODICITY_MONTHLY:
			case FactTable.PERIODICITY_BIANNUAL:
			case FactTable.PERIODICITY_QUARTERLY:
				buf.append("	to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'YYYY') = to_char(" + valueCalculator.getObjectiveTableName() + "." + objDateColumn + ",'YYYY') and \n");
				buf.append("	to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'MM') = to_char(" + valueCalculator.getObjectiveTableName() + "." + objDateColumn + ",'MM') and \n");
				break;
			case FactTable.PERIODICITY_DAILY:
			case FactTable.PERIODICITY_WEEKLY:
				buf.append("	to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'YYYY') = to_char(" + valueCalculator.getObjectiveTableName() + "." + objDateColumn + ",'YYYY') and \n");
				buf.append("	to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'MM') = to_char(" + valueCalculator.getObjectiveTableName() + "." + objDateColumn + ",'MM') and \n");
				buf.append("	to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'DD') = to_char(" + valueCalculator.getObjectiveTableName() + "." + objDateColumn + ",'DD') and \n");
				break;
			case FactTable.PERIODICITY_HOURLY:
				buf.append("	to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'YYYY') = to_char(" + valueCalculator.getObjectiveTableName() + "." + objDateColumn + ",'YYYY') and \n");
				buf.append("	to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'MM') = to_char(" + valueCalculator.getObjectiveTableName() + "." + objDateColumn + ",'MM') and \n");
				buf.append("	to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'DD') = to_char(" + valueCalculator.getObjectiveTableName() + "." + objDateColumn + ",'DD') and \n");
				buf.append("	to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'HH24') = to_char(" + valueCalculator.getObjectiveTableName() + "." + objDateColumn + ",'HH24') and \n");
				break;
			case FactTable.PERIODICITY_MINUTE:
				buf.append("	to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'YYYY') = to_char(" + valueCalculator.getObjectiveTableName() + "." + objDateColumn + ",'YYYY') and \n");
				buf.append("	to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'MM') = to_char(" + valueCalculator.getObjectiveTableName() + "." + objDateColumn + ",'MM') and \n");
				buf.append("	to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'DD') = to_char(" + valueCalculator.getObjectiveTableName() + "." + objDateColumn + ",'DD') and \n");
				buf.append("	to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'HH24') = to_char(" + valueCalculator.getObjectiveTableName() + "." + objDateColumn + ",'HH24') and \n");
				buf.append("	to_char(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'MI') = to_char(" + valueCalculator.getObjectiveTableName() + "." + objDateColumn + ",'MI') and \n");
				break;
			default:
				break;
			}
		}
		else if(driver.equals(ORACLE)) {
			switch (periodicity) {
				case FactTable.PERIODICITY_YEARLY:
					buf.append("	extract(year from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = extract(year from " + valueCalculator.getObjectiveTableName() + "." + objDateColumn + ") and \n");
					break;
				case FactTable.PERIODICITY_MONTHLY:
				case FactTable.PERIODICITY_BIANNUAL:
				case FactTable.PERIODICITY_QUARTERLY:
					buf.append("	extract(year from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = extract(year from " + valueCalculator.getObjectiveTableName() + "." + objDateColumn + ") and \n");
					buf.append("	extract(month from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = extract(month from " + valueCalculator.getObjectiveTableName() + "." + objDateColumn + ") and \n");
					break;
				case FactTable.PERIODICITY_DAILY:
				case FactTable.PERIODICITY_WEEKLY:
					buf.append("	extract(year from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = extract(year from " + valueCalculator.getObjectiveTableName() + "." + objDateColumn + ") and \n");
					buf.append("	extract(month from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = extract(month from " + valueCalculator.getObjectiveTableName() + "." + objDateColumn + ") and \n");
					buf.append("	extract(day from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = extract(day from " + valueCalculator.getObjectiveTableName() + "." + objDateColumn + ") and \n");
					break;
				case FactTable.PERIODICITY_HOURLY:
					buf.append("	extract(year from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = extract(year from " + valueCalculator.getObjectiveTableName() + "." + objDateColumn + ") and \n");
					buf.append("	extract(month from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = extract(month from " + valueCalculator.getObjectiveTableName() + "." + objDateColumn + ") and \n");
					buf.append("	extract(day from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = extract(day from " + valueCalculator.getObjectiveTableName() + "." + objDateColumn + ") and \n");
					buf.append("	extract(hour from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = extract(hour from " + valueCalculator.getObjectiveTableName() + "." + objDateColumn + ") and \n");
					break;
				case FactTable.PERIODICITY_MINUTE:
					buf.append("	extract(year from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = extract(year from " + valueCalculator.getObjectiveTableName() + "." + objDateColumn + ") and \n");
					buf.append("	extract(month from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = extract(month from " + valueCalculator.getObjectiveTableName() + "." + objDateColumn + ") and \n");
					buf.append("	extract(day from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = extract(day from " + valueCalculator.getObjectiveTableName() + "." + objDateColumn + ") and \n");
					buf.append("	extract(hour from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = extract(hour from " + valueCalculator.getObjectiveTableName() + "." + objDateColumn + ") and \n");
					buf.append("	extract(minute from " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = extract(minute from " + valueCalculator.getObjectiveTableName() + "." + objDateColumn + ") and \n");
					break;
				default:
					break;
			}
		}
		else if(driver.equals(SQLSERVER)) {
			switch (periodicity) {
				case FactTable.PERIODICITY_YEARLY:
					buf.append("	datepart(year, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = datepart(year, " + valueCalculator.getObjectiveTableName() + "." + objDateColumn + ") and \n");
					break;
				case FactTable.PERIODICITY_MONTHLY:
				case FactTable.PERIODICITY_BIANNUAL:
				case FactTable.PERIODICITY_QUARTERLY:
					buf.append("	datepart(year, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = datepart(year, " + valueCalculator.getObjectiveTableName() + "." + objDateColumn + ") and \n");
					buf.append("	datepart(month, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = datepart(month, " + valueCalculator.getObjectiveTableName() + "." + objDateColumn + ") and \n");
					break;
				case FactTable.PERIODICITY_DAILY:
				case FactTable.PERIODICITY_WEEKLY:
					buf.append("	datepart(year, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = datepart(year, " + valueCalculator.getObjectiveTableName() + "." + objDateColumn + ") and \n");
					buf.append("	datepart(month, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = datepart(month, " + valueCalculator.getObjectiveTableName() + "." + objDateColumn + ") and \n");
					buf.append("	datepart(day, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = datepart(day, " + valueCalculator.getObjectiveTableName() + "." + objDateColumn + ") and \n");
					break;
				case FactTable.PERIODICITY_HOURLY:
					buf.append("	datepart(year, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = datepart(year, " + valueCalculator.getObjectiveTableName() + "." + objDateColumn + ") and \n");
					buf.append("	datepart(month, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = datepart(month, " + valueCalculator.getObjectiveTableName() + "." + objDateColumn + ") and \n");
					buf.append("	datepart(day, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = datepart(day, " + valueCalculator.getObjectiveTableName() + "." + objDateColumn + ") and \n");
					buf.append("	datepart(hour, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = datepart(hour, " + valueCalculator.getObjectiveTableName() + "." + objDateColumn + ") and \n");
					break;
				case FactTable.PERIODICITY_MINUTE:
					buf.append("	datepart(year, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = datepart(year, " + valueCalculator.getObjectiveTableName() + "." + objDateColumn + ") and \n");
					buf.append("	datepart(month, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = datepart(month, " + valueCalculator.getObjectiveTableName() + "." + objDateColumn + ") and \n");
					buf.append("	datepart(day, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = datepart(day, " + valueCalculator.getObjectiveTableName() + "." + objDateColumn + ") and \n");
					buf.append("	datepart(hour, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = datepart(hour, " + valueCalculator.getObjectiveTableName() + "." + objDateColumn + ") and \n");
					buf.append("	datepart(minute, " + valueCalculator.getValueTableName() + "." + valueDateColumn + ") = datepart(minute, " + valueCalculator.getObjectiveTableName() + "." + objDateColumn + ") and \n");
					break;
				default:
					break;
			}
		}
		else {
			switch (periodicity) {
			case FactTable.PERIODICITY_YEARLY:
				buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%Y') = date_format(" + valueCalculator.getObjectiveTableName() + "." + objDateColumn + ",'%Y') and \n");
				break;
			case FactTable.PERIODICITY_MONTHLY:
			case FactTable.PERIODICITY_BIANNUAL:
			case FactTable.PERIODICITY_QUARTERLY:
				buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%Y') = date_format(" + valueCalculator.getObjectiveTableName() + "." + objDateColumn + ",'%Y') and \n");
				buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%m') = date_format(" + valueCalculator.getObjectiveTableName() + "." + objDateColumn + ",'%m') and \n");
				break;
			case FactTable.PERIODICITY_DAILY:
			case FactTable.PERIODICITY_WEEKLY:
				buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%Y') = date_format(" + valueCalculator.getObjectiveTableName() + "." + objDateColumn + ",'%Y') and \n");
				buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%m') = date_format(" + valueCalculator.getObjectiveTableName() + "." + objDateColumn + ",'%m') and \n");
				buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%d') = date_format(" + valueCalculator.getObjectiveTableName() + "." + objDateColumn + ",'%d') and \n");
				break;
			case FactTable.PERIODICITY_HOURLY:
				buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%Y') = date_format(" + valueCalculator.getObjectiveTableName() + "." + objDateColumn + ",'%Y') and \n");
				buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%m') = date_format(" + valueCalculator.getObjectiveTableName() + "." + objDateColumn + ",'%m') and \n");
				buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%d') = date_format(" + valueCalculator.getObjectiveTableName() + "." + objDateColumn + ",'%d') and \n");
				buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%H') = date_format(" + valueCalculator.getObjectiveTableName() + "." + objDateColumn + ",'%H') and \n");
				break;
			case FactTable.PERIODICITY_MINUTE:
				buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%Y') = date_format(" + valueCalculator.getObjectiveTableName() + "." + objDateColumn + ",'%Y') and \n");
				buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%m') = date_format(" + valueCalculator.getObjectiveTableName() + "." + objDateColumn + ",'%m') and \n");
				buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%d') = date_format(" + valueCalculator.getObjectiveTableName() + "." + objDateColumn + ",'%d') and \n");
				buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%H') = date_format(" + valueCalculator.getObjectiveTableName() + "." + objDateColumn + ",'%H') and \n");
				buf.append("	date_format(" + valueCalculator.getValueTableName() + "." + valueDateColumn + ",'%i') = date_format(" + valueCalculator.getObjectiveTableName() + "." + objDateColumn + ",'%i') and \n");
				break;
			default:
				break;
			}
		}
		return buf.toString();
	}
	
	public static String generateUpdateDeleteDateFilter(String column, String date, String driver) {
		if(driver.equals(PGSQL) || driver.equals(H2)) {
			if(date.length() <= 10) {
				return "Where to_char(" + column + ",'" + DATE_FORMAT_PGSQL + "') LIKE '" + date + "%' \n";
			}
			else if(date.length() <= 13) {
				return "Where to_char(" + column + ",'" + DATE_HOUR_FORMAT_PGSQL + "') LIKE '" + date + "%' \n";
			}
			else {
				return "Where to_char(" + column + ",'" + DATE_HOUR_MINUTE_FORMAT_PGSQL + "') LIKE '" + date + "%' \n";
			}
		}
		else if(driver.equals(ORACLE)) {
			return "";
		}
		else {
			return "Where " + column + " LIKE '" + date + "%' \n";
		}
	}
}
