package bpm.gateway.core.transformations.cleansing;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

import bpm.gateway.core.transformations.cleansing.ValidationCleansing.ValidatorType;

public class ValidatorHelper {

	public static boolean isValid(String expression, String pattern, ValidatorType type){
		switch(type){
		case Date:
			SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			sdf.setLenient(false);
			try{
				sdf.parse(expression);
				return true;
			}catch(Exception ex){
				return false;
			}
//		case Decimal:Mes
//			DecimalFormat dcf = new DecimalFormat(pattern);
//			try{
//				dcf.parse(expression);
//				return true;
//			}catch (ParseException e) {
//				return false;
//			}
		case Regex:
			return Pattern.matches(pattern, expression);
		}
		
		return false;
		
	}
}
