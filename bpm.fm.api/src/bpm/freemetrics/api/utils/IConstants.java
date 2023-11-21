/**
 * 
 */
package bpm.freemetrics.api.utils;

/**
 * @author ansybelgarde
 *
 */
public abstract class IConstants {
	
	public final static String[] PERIODS = {"YEAR","BIANNUAL","QUARTER","MONTH","WEEK"
		,"DAY","HOUR","MINUTES","SECONDES","REALTIME"};
	
	public final static String[] SEMESTRES = {"S1","S2"};
	public final static int SEMESTRE1 = 0;
	public final static int SEMESTRE2 = 1;
	
	public final static String[] TRIMESTRES = {"T1","T2", "T3", "T4"};
	public final static int TRIMESTRE1 = 0;
	public final static int TRIMESTRE2 = 1;
	public final static int TRIMESTRE3 = 2;
	public final static int TRIMESTRE4 = 3;
	
	public final static String[] WEEKS = {"s1","s2","s3","s4","s5","s6","s7","s8","s9","s10","s11","s12","s13","s14","s15","s16","s17","s18","s19","s20","s21","s22","s23","s24","s25","s26","s27","s28","s29","s30","s31","s32","s33","s34","s35","s36","s37","s38","s39","s40","s41","s42","s43","s44","s45","s46","s47","s48","s49","s50","s51","s52","s53"};
	
	public final static int PERIOD_YEARLY = 0;
	public final static int PERIOD_BIANNUAL = 1;
	public final static int PERIOD_QUARTERLY = 2;
	public final static int PERIOD_MONTHLY = 3;
	public static final int PERIOD_WEEKLY = 4;
	public static final int PERIOD_DAYLY = 5;
	public static final int PERIOD_HOURLY = 6;
	public static final int PERIOD_MINUTLY = 7;
	public static final int PERIOD_SECONDLY = 8;
	public static final int PERIOD_REALTIME = 9;

	public final static String OBJECTIF_LOCAL = "LOCAL";
	public final static String OBJECTIF_NATIONAL = "NATIONAL";

	public static final String CALCULATION_TYPE_PROCESS = "Imported";
	public static final String CALCULATION_TYPE_BASIC = "Process";
	public static final String CALCULATION_TYPE_COMPUTED = "Computed";
	
	public static final String CALCULATION_TYPE_WAIT = "Wait";
	public static final String CALCULATION_TYPE_TRUE = "True";
	public static final String CALCULATION_TYPE_FALSE = "False";

	public final static String[] ACTIONTYPE_KEYS = {"eml","spl","sky","sms"};

	public static final int ACTIONTYPE_SEND_TO_MAIL = 0;
	public static final int ACTIONTYPE_SEND_TO_POPUP = 1;
	public static final int ACTIONTYPE_SEND_TO_SKYPE = 2;
	public static final int ACTIONTYPE_SEND_TO_SMS = 3;
	public static final int ACTIONTYPE_NO_TYPE =-1;

	public static final int ALERTE_TYPE_IS_PUBLIC = 0;
	public static final int ALERTE_TYPE_IS_PRIVATE = 1;
	
	public static final String VALUE_KEY_ACTUAL = "actual";
	public static final String VALUE_KEY_MAXIMUM = "maximum";
	public static final String VALUE_KEY_MINIMUM = "minimum";
	public static final String VALUE_KEY_TARGET = "target";


	public static final String TREND_ORDER_DOWN = "DOWN";
	public static final String TREND_ORDER_UP = "UP";
	public static final String TREND_ORDER_STABLE = "STABL";
	
	public static final String TREND_GOOD_DOWN = "G_DOWN";
	public static final String TREND_GOOD_UP = "G_UP";
	public static final String TREND_GOOD_STABLE = "G_STABL";
	
	public static final String TREND_BAD_DOWN = "B_DOWN";
	public static final String TREND_BAD_UP = "B_UP";
	public static final String TREND_BAD_STABLE = "B_STABL";
	
	public static final String TREND_WARN_DOWN = "W_DOWN";
	public static final String TREND_WARN_UP = "W_UP";
	public static final String TREND_WARN_STABLE = "W_STABL";

	public static final String STATUS_UNDER = "DOWN";
	public static final String STATUS_OVER = "UP";
	public static final String STATUS_ACHIEVED = "STABL";

	public static final int METRIC_FILTER_ANY = 0;
	public static final int METRIC_FILTER_COMPTEUR = 1;
	public static final int METRIC_FILTER_INDICATEUR = 2;

	public static final String DATASOURCES_TYPE_DATABASE = "DATABASE";
	public static final String DATASOURCES_TYPE_METADATA = "METADATA";

	public static final int OBJECTISNULL = - 1;

	
//	public static String driverFile = null;
//	public static String jdbcJarFolder = null;
//	
//	public static String driverPath = null;
//	public static String resourcePath = null;
	
	public static void main(String[] args) {
		String s = "";
		for (int i = 1; i < 54; i++) {
			s += "\"s" + i + "\",";
		}
		System.out.println(s);
	}
}


