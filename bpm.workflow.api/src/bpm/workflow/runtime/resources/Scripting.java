package bpm.workflow.runtime.resources;

/**
 * Used for the calculation activities
 * @author CAMUS
 *
 */
public final class Scripting {

	public static final String[] CATEGORIES = new String[]{
		"Math", "Trigonometric", "Logical", "Operators", "String", "Date", "PrebuilFunctions"
	};
	
	public static final int CAT_MATH = 0;
	public static final int CAT_TRIGO = 1;
	public static final int CAT_LOGICAL = 2;
	public static final int CAT_OPERATORS = 3;
	public static final int CAT_STRINGS = 4;
	public static final int CAT_DATE = 5;
	public static final int CAT_PREBUILT = 6;
	
	public static final String[] MATH_FUNCTIONS = new String[]{
		"Math.abs(arg1, arg2)", "Math.exp(arg)", "Math.log(arg1)", "Math.max(arg1, arg2)", "Math.min(arg1, arg2)",
		"Math.pow(arg1, arg2)", "Math.sqrt(arg)"};
	
	public static final int MATH_ABS = 0;
	public static final int MATH_EXP = 1;
	public static final int MATH_LOG = 2;
	public static final int MATH_MAX = 3;
	public static final int MATH_MIN = 4;
	public static final int MATH_POW = 5;
	public static final int MATH_SQRT = 6;
	
	
	public static final String[] TRIGO_FUNCTIONS = new String[]{
		"Math.acos(arg)", "Math.asin(arg)", "Math.atan(arg)", "Math.cos(arg)", "Math.sin(arg)",
		"Math.tan(arg)", "Math.PI"};
	
	public static final int TRIGO_ACOS = 0;
	public static final int TRIGO_ASIN = 1;
	public static final int TRIGO_ATAN = 2;
	public static final int TRIGO_COS = 3;
	public static final int TRIGO_SIN = 4;
	public static final int TRIGO_TAN = 5;
	public static final int TRIGO_PI = 6;
	
	
	public static final String[] LOGICAL_FUNCTIONS = new String[]{
		"AND(arg1,arg2)","OR(arg1,arg2)", "XOR(arg1,arg2)", "NOT(arg)"
	};
	
	public static final int LOGICAL_AND = 0;
	public static final int LOGICAL_OR = 1;
	public static final int LOGICAL_XOR = 2;
	public static final int LOGICAL_NOT = 3;
	
	
	
	public static final String[] OPERATOR_FUNCTIONS = new String[]{
		"+", "*", "/", "-"};
	
	public static final int OPERATOR_ADD = 0;
	public static final int OPERATOR_MUL = 1;
	public static final int OPERATOR_DIV = 2;
	public static final int OPERATOR_SUB = 3;
	
	
	public static final String[] STRING_FUNCTIONS = new String[]{
		".substr(start,stop)", ".substr(start)",".charAt(ind)", ".concat(str)", 
		".indexOf(value)",".indexOf(value, fromindex)",
		".lastIndexOf(value)",".lastIndexOf(value, fromindex)",
		".toLowerCase()",".toUpperCase()"
	};
	
	
	public static final int STRING_SUB1 = 0;
	public static final int STRING_SUB2 = 1;
	public static final int STRING_CHAR_AT = 2;
	public static final int STRING_CONCAT = 3;
	public static final int STRING_INDEXOF = 4;
	public static final int STRING_INDEXOF2 = 5;
	public static final int STRING_LAST_INDEXOF = 6;
	public static final int STRING_LAST_INDEXOF2 = 7;
	public static final int STRING_TO_LOWER = 8;
	public static final int STRING_TO_UPPER = 9;
	
	
	public static final String[] DATE_FUNCTIONS = new String[]{
		".getUTCDate()",".getUTCDay()",".getUTCFullYear()",".getUTCHours()",
		".getUTCMilliseconds()", ".getUTCMinutes()", ".getUTCMonth()", 
		".getUTCSeconds()", ".getTime()", ".toUTCString()", "new Date()"
	};

	public static final int DATE_DATE = 0;
	public static final int DATE_DAY = 1;
	public static final int DATE_YEAR = 2;
	public static final int DATE_HOURS = 3;
	public static final int DATE_MILLISECONDS = 4;
	public static final int DATE_MINUTES = 5;
	public static final int DATE_MONTH = 6;
	public static final int DATE_SECONDS = 7;
	public static final int DATE_TIME = 8;
	public static final int DATE_TO_STRING = 9;
	public static final int DATE_NEW = 10;
	
	
	public static final String[] PREBUILT_FUNCTIONS = new String[]{
		"dateDifference(date1,date2)"
	};
	
	public static final String[] INTERVALS_TYPE = new String[]{
		"]a,b[","]a,b]","[a,b]","[a,b[", "other" 
	};
	
	
	private Scripting(){}
}
