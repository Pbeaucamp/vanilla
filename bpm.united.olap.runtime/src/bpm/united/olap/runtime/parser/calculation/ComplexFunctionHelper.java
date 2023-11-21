package bpm.united.olap.runtime.parser.calculation;

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;

public class ComplexFunctionHelper {

	public static final String[] FUNCTIONS = {
		"ddb(",
		"ipmt(",
		"nper(",
		"percent(",
		"pmt(",
		"ppmt(",
		"pv(",
		"sln(", 
		"syd("};
	public static final int DDB = 0;
	public static final int IPMT = 1;
	public static final int NPER = 2;
	public static final int PERCENT = 3;
	public static final int PMT = 4;
	public static final int PPMT = 5;
	public static final int PV = 6;
	public static final int SLN = 7;
	public static final int SYD = 8;
	
	/**
	 * Replace the complex functions in the formula by the javascript equivalent
	 * @param formula
	 * @return
	 */
	public static String replaceComplexFunctions(String formula) {
		String result = formula;
		
		for(String func : FUNCTIONS) {
			if(result.contains(func)) {
				
				switch (ArrayUtils.indexOf(FUNCTIONS, func)) {
				case SLN:
					result = addSLN(result);
					break;
				case SYD:
					result = addSYD(result);
					break;
				case DDB:
					result = addDDB(result);
					break;
				case IPMT:
					result = addIPMT(result);
					break;
				case NPER:
					result = addNPER(result);
					break;
				case PERCENT:
					result = addPERCENT(result);
					break;
				case PMT:
					result = addPMT(result);
					break;
				case PV:
					result = addPV(result);
					break;
				case PPMT:
					result = addPPMT(result);
					break;
				default:
					break;
				}
				
				
			}
		}
		
		return result;
	}

	private static String addPERCENT(String result) {
		StringBuilder buf = new StringBuilder();
		
		buf.append("function percent( denom, num, valueIfZero){\n");
		
		buf.append("if(num == 0)\n");
		buf.append("{\n");
		buf.append("return valueIfZero;\n");
		buf.append("}\n");
	    buf.append("return ( num / denom ) * 100;\n");
		
		
		buf.append("}\n");
		
		return buf.toString() + "\r\n" + result;
	}

	private static String addPV(String result) {

		StringBuilder buf = new StringBuilder();
		
		buf.append("function pv( rate, nper, pmt, fv, due){\n");
		
		buf.append("var pv = 0;\n");
		buf.append("var denom;\n");
		buf.append("var num;\n");
		buf.append("if ( rate == 0 )\n");
		buf.append("{\n");
		buf.append("pv = -fv - ( pmt * nper );\n");
		buf.append("}\n");
		buf.append("else\n");
		buf.append("{\n");
		buf.append("denom = Math.pow( ( 1 + rate ), nper );\n");
		buf.append("num = -fv - pmt * ( 1 + ( rate * due ) ) * ( denom - 1 ) / rate;\n");
		buf.append("pv = num / denom;\n");
		buf.append("}\n");
		buf.append("return pv;\n");
		
		buf.append("}\n");
		
		return buf.toString() + "\r\n" + result;
	}

	private static String addNPER(String result) {
		StringBuilder buf = new StringBuilder();
		
		buf.append("function nper( rate, pmt, pv, fv, due){\n");
		
		buf.append("var retval = 0;\n");
		buf.append("if (rate == 0) {\n");
		buf.append("retval = -1 * (fv + pv) / pmt;\n");
		buf.append("} else {\n");
		buf.append("var r1 = rate + 1;\n");
		buf.append("var ryr = (due == 1 ? r1 : 1) * pmt / rate;\n");
		buf.append("var a1 = ((ryr - fv) < 0)\n");
		buf.append("? Math.log(fv - ryr)\n");
		buf.append(": Math.log(ryr - fv);\n");
		buf.append("var a2 = ((ryr - fv) < 0)\n");
		buf.append("? Math.log(-pv - ryr)\n");
		buf.append(": Math.log(pv + ryr);\n");
		buf.append("var a3 = Math.log(r1);\n");
		buf.append("retval = (a1 - a2) / a3;\n");
		buf.append("}\n");
		buf.append("return retval;\n");
		
		
		buf.append("}\n");
		
		return buf.toString() + "\r\n" + result;
	}

	private static String addPPMT(String result) {

//		result = addPMT(result);
		
		StringBuilder buf = new StringBuilder();
		
		buf.append("function ppmt( rate, per, nper, pv, fv, due){\n");
		buf.append("var ppmt2 = 0;\n");
		buf.append("var pmt2;\n");
		buf.append("var principal;\n");
		buf.append("var ipmt = 0;\n");
		buf.append("pmt2 = pmt( rate, nper, pv, fv, due );\n");
		buf.append("principal = Math.abs( pv );\n");
		buf.append("for ( curper = 1; curper <= per; curper++ )\n");
		buf.append("{\n");
		buf.append("ppmt2 = pmt2 - ipmt;\n");
		buf.append("principal = principal - ppmt2;\n");
		buf.append("ipmt = rate * principal;\n");
		buf.append("}\n");
		buf.append("return ppmt2;\n");
		
		buf.append("}\n");
		
		return buf.toString() + "\r\n" + result;
	}

	private static String addPMT(String result) {
		StringBuilder buf = new StringBuilder();
		
		buf.append("function pmt( rate, nper, pv, fv, due){\n");
		buf.append("var pmt2 = 0;\n");
		buf.append("if ( rate == 0 )\n");
		buf.append("{\n");
		buf.append("pmt2 = -( fv + pv ) / nper;\n");
		buf.append("}\n");
		buf.append("else\n");
		buf.append("{\n");
		buf.append("denom = Math.pow( ( 1 + rate ), nper );\n");
		buf.append("pmt2 = ( -fv - pv * denom ) * ( rate / ( ( 1 + rate * due ) * ( denom - 1 ) ) );\n");
		buf.append("}\n");
		buf.append("return pmt2;\n");
		
		buf.append("}\n");
		
		return buf.toString() + "\r\n" + result;
	}

	private static String addIPMT(String result) {

//		result = addPMT(result);
		
		StringBuilder buf = new StringBuilder();
		
		buf.append("function ipmt( rate, per, nper, pv, fv, due){\n");
		buf.append("var ipmt2 = 0;\n");
		buf.append("var ppmt = 0;\n");
		buf.append("var pmt2;\n");
		buf.append("var principal;\n");
		buf.append("pmt2 = pmt(rate,nper,pv,fv,due);\n");
		buf.append("principal = Math.abs( pv );\n");
		buf.append("for ( curper = 1; curper <= per; curper++ )\n");
		buf.append("{\n");
		buf.append("if ( curper != 1 || due == 0 )\n");
		buf.append("ipmt2 = rate * principal;\n");
		buf.append("ppmt = pmt2 - ipmt2;\n");
		buf.append("principal = principal - ppmt;\n");
		buf.append("}\n");
		buf.append("return ipmt2;\n");
		
		buf.append("}\n");
		
		return buf.toString() + "\r\n" + result;
	}

	private static String addDDB(String result) {
		StringBuilder buf = new StringBuilder();
		
		buf.append("function ddb( cost, salvage, life, period ){\n");
		buf.append("var depr = 0;\n");
		buf.append("var prior = 0;\n");
		buf.append("var rate;\n");
		buf.append("var basis;\n");
		buf.append("for ( x = 0; x < period; x++ )\n");
		buf.append("{\n");
		buf.append("basis = cost - prior;\n");
		buf.append("depr = Math.min( basis - salvage, basis * ( 1 / life ) * 2 );\n");
		buf.append("prior += depr;\n");
		buf.append("}\n");
		buf.append("return depr;\n");
		buf.append("}\n");
		
		return buf.toString() + "\r\n" + result;
	}

	private static String addSYD(String result) {
		StringBuilder buf = new StringBuilder();
		
		buf.append("function syd(cost,salvage,life,period){return (cost - salvage)* (life -period + 1) * 2 / (life * (life + 1));\n");
		
		buf.append("}\n");
		
		return buf.toString() + "\r\n" + result;
	}

	private static String addSLN(String result) {
		StringBuilder buf = new StringBuilder();
		
		buf.append("function sln(cost, salvage, life){return ( ( cost - salvage ) / life );\n");
		
		buf.append("}\n");
		
		return buf.toString() + "\r\n" + result;
	}
	
	/**
	 * 
	 * @param result
	 * @param func
	 * @return an array containing the original function and the arguments
	 */
	private static String[] extractParameters(String result, String func) {
		int index = result.indexOf(func);
		
		int endIndex = result.indexOf(")", index);
		
		String sln = result.substring(index, endIndex);
		
		String trimSln = sln.replace(" ", "");
		
		String[] args = trimSln.split(",");
		
		String[] values = new String[1+args.length];
		
		values[0] = sln;
		for( int i = 0 ; i < args.length ; i++) {
			values[i+1] = args[i]; 
		}
		
		return values;
	}
}
