package bpm.fmloader.client.tools;

import java.util.Date;

import bpm.fmloader.client.constante.Constantes;

public class DateToString {

	public static String newDate;
	
	public static String getDateString(Date date, String periodicity) {
		
		String year = date.getYear() + 1900 + "";
		
		String stringDate = "";
		if(periodicity.equalsIgnoreCase("YEAR")) {
			stringDate = year;
		}
		
		else if(periodicity.equalsIgnoreCase("BIANNUAL")) {
			if(date.getMonth() <= 5) {
				stringDate = Constantes.LBL.First() + " " + Constantes.LBL.Semester() + " " + year;
			}
			else {
				stringDate = Constantes.LBL.Second() + " " + Constantes.LBL.Semester() + " " + year;
			}
		}
		
		else if(periodicity.equalsIgnoreCase("QUARTER")) {
			if(date.getMonth() <= 2) {
				stringDate = Constantes.LBL.First() + " " + Constantes.LBL.Semester() + " " + year;
			}
			else if(date.getMonth() > 2 && date.getMonth() <= 5) {
				stringDate = Constantes.LBL.Second() + " " + Constantes.LBL.Semester() + " " +  year;
			}
			else if(date.getMonth() > 5 && date.getMonth() <= 8) {
				stringDate = Constantes.LBL.Third() + " " + Constantes.LBL.Semester() + " " + year;
			}
			else {
				stringDate = Constantes.LBL.Fourth() + " " + Constantes.LBL.Semester() + " " + year;
			}
		}
		
		else if(periodicity.equalsIgnoreCase("MONTH")) {
			stringDate = date.getMonth() + 1 + "/" + year;
		}
		
		else if(periodicity.equalsIgnoreCase("WEEK") || periodicity.equalsIgnoreCase("DAY")) {
			stringDate = date.getDate() + "/" + (date.getMonth() + 1) + "/" + year;
		}
		
		return stringDate;
	}
	
}
