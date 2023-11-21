package bpm.faweb.client.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bpm.faweb.shared.infoscube.InfosReport;
import bpm.faweb.shared.infoscube.ItemCube;
import bpm.gwt.commons.client.I18N.LabelsConstants;

public class DateFunctionCalculator {
	
	public static int DIFFERENCE_BETWEEN_DATE = 1;
	public static int YEAR_TO_DATE = 2;
	public static int YEAR_TO_YEAR = 3;

	public static void differenceBetweenDate(ItemCube item, InfosReport infoReport, int itemI, int itemJ) {
		boolean onRow = false;
		if(itemI > infoReport.getIFirst()) {
			onRow = true;
		}
		
		int startI = itemI + 1;
		int startJ = itemJ + 1;
		
		if(onRow) {
			for(int i = startI ; i < infoReport.getGrid().getItems().size() - 1 ; i++) {
				
				for(int j = startJ ; j < infoReport.getGrid().getItems().get(i).size() ; j++) {
					if(j == startJ) {
						ItemCube it = infoReport.getGrid().getIJ(i, j);
						ItemCube next = infoReport.getGrid().getIJ(i + 1, j);
						it.setLabel(next.getLabel() + " - " + it.getLabel());
					}
					else {
						ItemCube it = infoReport.getGrid().getIJ(i, j);
						ItemCube next = infoReport.getGrid().getIJ(i + 1, j);
						it.setValue(next.getValue() - it.getValue());
						it.setLabel(it.getValue() + "");
					}
				}
			}
			
			infoReport.getGrid().getItems().remove(infoReport.getGrid().getItems().size() - 1);	
		}
		else {
			for(int j = startJ ; j < infoReport.getGrid().getItems().get(0).size() - 1 ; j++) {
				for(int i = startI ; i < infoReport.getGrid().getItems().size() ; i++) {
					if(i == startI) {
						ItemCube it = infoReport.getGrid().getIJ(i, j);
						ItemCube next = infoReport.getGrid().getIJ(i, j+1);
						it.setLabel(next.getLabel() + " - " + it.getLabel());
					}
					else {
						ItemCube it = infoReport.getGrid().getIJ(i, j);
						ItemCube next = infoReport.getGrid().getIJ(i, j+1);
						it.setValue(next.getValue() - it.getValue());
						it.setLabel(it.getValue() + "");
					}
				}
			}
			
			for(int i = 0 ; i < infoReport.getGrid().getItems().size() ; i++) {
				infoReport.getGrid().getItems().get(i).remove(infoReport.getGrid().getItems().get(i).size() - 1);
			}
		}
	}

	public static void yearToDate(ItemCube item, InfosReport infoReport, int month, int itemI, int itemJ) {
		boolean onRow = false;
		if(itemI >= infoReport.getIFirst()) {
			onRow = true;
		}
		
		List<Integer> toRemove = new ArrayList<Integer>();
		
		if(onRow) {
			int startI = itemI + 1;
			int startJ = 0 + 1;
			for(int i = startI + 1 ; i < infoReport.getGrid().getItems().size() ; i++) {
				
				if((i == infoReport.getGrid().getItems().size() - 1) || (infoReport.getGrid().getIJ(i, startJ).getType().equals("ItemElement")) && i != startI) {
					//change the year label
					infoReport.getGrid().getIJ(startI, startJ).setLabel("Jan to " + getMonthLabel(month) + " " + infoReport.getGrid().getIJ(startI, startJ).getLabel());
					
					if(i - startI >= month) {
						for(int j = startJ + 2 ; j < infoReport.getGrid().getItems().get(i).size() ; j++) {
							ItemCube it = infoReport.getGrid().getIJ(startI, j);
							it.setValue(0);
							for(int calc = startI + 1 ; calc < startI + month + 1 ; calc++) {
								ItemCube next = infoReport.getGrid().getIJ(calc, j);
								
								it.setValue(next.getValue() + it.getValue());
								it.setLabel(it.getValue() + "");
							}
						}
					}
					for(int rem = startI ; rem < i ;rem++) {
						if(i - startI >= month) {
							if(rem == startI) {
								continue;
							}
						}
						toRemove.add(rem);
					}
					
					startI = i;
				}
				
			}
			toRemove.add(infoReport.getGrid().getItems().size() - 1);
			for(int i = 0 ; i < infoReport.getGrid().getItems().size() ; i++) {
				infoReport.getGrid().getItems().get(i).remove(startJ + 1);
			}
			
			Collections.reverse(toRemove);
			
			for(Integer i : toRemove) {
				infoReport.getGrid().getItems().remove(i.intValue());
			}
			
		}
		else {
			int startI = 0 + 1;
			int startJ = itemJ + 1;
			for(int j = startJ + 1 ; j < infoReport.getGrid().getItems().get(0).size() ; j++) {
				
				if((j == infoReport.getGrid().getItems().get(0).size() - 1) || (infoReport.getGrid().getIJ(startI, j).getType().equals("ItemElement")) && j != startJ) {
					infoReport.getGrid().getIJ(startI, startJ).setLabel("Jan to " + getMonthLabel(month) + " " + infoReport.getGrid().getIJ(startI, startJ).getLabel());
					if(j - startJ >= month) {
						for(int i = startI + 2 ; i < infoReport.getGrid().getItems().size() ; i++) {
							ItemCube it = infoReport.getGrid().getIJ(i, startJ);
							it.setValue(0);
							for(int calc = startJ + 1 ; calc < startJ + month + 1 ; calc++) {
								ItemCube next = infoReport.getGrid().getIJ(i, calc);
								
								it.setValue(next.getValue() + it.getValue());
								it.setLabel(it.getValue() + "");
							}
						}
					}
					for(int rem = startJ ; rem < j ;rem++) {
						if(j - startJ >= month) {
							if(rem == startJ) {
								continue;
							}
						}
						toRemove.add(rem);
					}
					
					startJ = j;
				}
				
			}
			toRemove.add(infoReport.getGrid().getItems().get(0).size() - 1);
			infoReport.getGrid().getItems().remove(startI + 1);
			
			Collections.reverse(toRemove);
			
			for(int i = 0 ; i < infoReport.getGrid().getItems().size() ; i++) {
				for(Integer j : toRemove) {
					infoReport.getGrid().getItems().get(i).remove(j.intValue());
				}
			}
		}
	}
	
	public static void yearToYear(ItemCube item, InfosReport infoReport, int month, int itemI, int itemJ) {
		boolean onRow = false;
		if(itemI >= infoReport.getIFirst()) {
			onRow = true;
		}
		
		List<Integer> toRemove = new ArrayList<Integer>();
		
		if(onRow) {
			int startI = itemI + 1;
			int startJ = 0 + 1;
			
			List<Integer> yearIndexes = new ArrayList<Integer>();
			
			yearIndexes.add(startI);
			
			//find the year indexes
			for(int i = startI + 1 ; i < infoReport.getGrid().getItems().size() ; i++) {
				
				if((i == infoReport.getGrid().getItems().size() - 1) || (infoReport.getGrid().getIJ(i, startJ).getType().equals("ItemElement")) && i != startI) {
					if(!(i == infoReport.getGrid().getItems().size() - 1)) {
						yearIndexes.add(i);
					}
				}
				
			}
			//browse the grid per line to calculate
			for(int j = startJ + 2 ; j < infoReport.getGrid().getItems().get(0).size() ; j++) {
				for(int index = 0 ; index < yearIndexes.size() ; index++) {
					//look if there's enough month to calculate a value
					if(yearIndexes.size() - 1 == index || yearIndexes.get(index + 1) - yearIndexes.get(index) < month) {
						toRemove.add(yearIndexes.get(index));
						continue;
					}
					ItemCube it = infoReport.getGrid().getIJ(yearIndexes.get(index),j);
					it.setValue(0);
					for(int monthIndex = yearIndexes.get(index + 1) - 13 + month ; monthIndex <= yearIndexes.get(index + 1) + (month - 1) ; monthIndex++) {
						if(!(monthIndex == yearIndexes.get(index + 1))) {
							ItemCube monthVal = infoReport.getGrid().getIJ(monthIndex, j);
							it.setValue(it.getValue() + monthVal.getValue());
							it.setLabel(it.getValue() + "");
						}
						
					}
				}
			}
			for(int rem = startI ; rem < infoReport.getGrid().getItems().size() - 1 ;rem++) {
				if(yearIndexes.contains(rem)) {
					continue;
				}
				toRemove.add(rem);
			}
			toRemove.add(infoReport.getGrid().getItems().size() - 1);
			
			//change the labels
			for(int index = 0 ; index < yearIndexes.size() - 1 ; index++) {
				if(!toRemove.contains(yearIndexes.get(index))) {
					ItemCube yearToChange = infoReport.getGrid().getIJ(yearIndexes.get(index), startJ);
					ItemCube endItem = infoReport.getGrid().getIJ(yearIndexes.get(index + 1), startJ);
					
					yearToChange.setLabel(getMonthLabel(month) + " " + yearToChange.getLabel() + " - " + getMonthLabel(month > 0 ? (month - 1) : 11) + " " + endItem.getLabel());
				}
			}
			
			for(int i = 0 ; i < infoReport.getGrid().getItems().size() ; i++) {
				infoReport.getGrid().getItems().get(i).remove(startJ + 1);
			}
			
			Collections.sort(toRemove, new Comparator<Integer>() {
				@Override
				public int compare(Integer o1, Integer o2) {
					return o2.compareTo(o1);
				}
			});
			
			for(Integer i : toRemove) {
				infoReport.getGrid().getItems().remove(i.intValue());
			}
		}
		else {
			int startI = 0 + 1;
			int startJ = itemJ + 1;
			
			List<Integer> yearIndexes = new ArrayList<Integer>();
			
			yearIndexes.add(startJ);
			
			//find the year indexes
			for(int j = startJ + 1 ; j < infoReport.getGrid().getItems().get(0).size() ; j++) {
				
				if((j == infoReport.getGrid().getItems().get(0).size() - 1) || (infoReport.getGrid().getIJ(startI, j).getType().equals("ItemElement")) && j != startJ) {
					if(!(j == infoReport.getGrid().getItems().get(0).size() - 1)) {
						yearIndexes.add(j);
					}
				}
				
			}
			//browse the grid per line to calculate
			for(int i = startI + 2 ; i < infoReport.getGrid().getItems().size() ; i++) {
				for(int index = 0 ; index < yearIndexes.size() ; index++) {
					//look if there's enough month to calculate a value
					if(yearIndexes.size() - 1 == index || yearIndexes.get(index + 1) - yearIndexes.get(index) < month) {
						toRemove.add(yearIndexes.get(index));
						continue;
					}
					ItemCube it = infoReport.getGrid().getIJ(i, yearIndexes.get(index));
					it.setValue(0);
					for(int monthIndex = yearIndexes.get(index + 1) - 13 + month ; monthIndex <= yearIndexes.get(index + 1) + (month - 1) ; monthIndex++) {
						if(!(monthIndex == yearIndexes.get(index + 1))) {
							ItemCube monthVal = infoReport.getGrid().getIJ(i, monthIndex);
							it.setValue(it.getValue() + monthVal.getValue());
							it.setLabel(it.getValue() + "");
						}
						
					}
				}
			}
			for(int rem = startJ ; rem < infoReport.getGrid().getItems().get(0).size() - 1 ;rem++) {
				if(yearIndexes.contains(rem)) {
					continue;
				}
				toRemove.add(rem);
			}
			toRemove.add(infoReport.getGrid().getItems().get(0).size() - 1);
			
			//change the labels
			for(int index = 0 ; index < yearIndexes.size() - 1 ; index++) {
				if(!toRemove.contains(yearIndexes.get(index))) {
					ItemCube yearToChange = infoReport.getGrid().getIJ(startI, yearIndexes.get(index));
					ItemCube endItem = infoReport.getGrid().getIJ(startI, yearIndexes.get(index + 1));
					
					yearToChange.setLabel(getMonthLabel(month) + " " + yearToChange.getLabel() + " - " + getMonthLabel(month > 0 ? (month - 1) : 11) + " " + endItem.getLabel());
				}
			}
			
			
			infoReport.getGrid().getItems().remove(startI + 1);
			
			Collections.sort(toRemove, new Comparator<Integer>() {
				@Override
				public int compare(Integer o1, Integer o2) {
					return o2.compareTo(o1);
				}
			});
			
			for(int i = 0 ; i < infoReport.getGrid().getItems().size() ; i++) {
				for(Integer j : toRemove) {
					infoReport.getGrid().getItems().get(i).remove(j.intValue());
				}
			}
		}
		
		
	}
	
	private static String getMonthLabel(int month) {
		switch(month) {
			case 1:
				return LabelsConstants.lblCnst.January();
			case 2:
				return LabelsConstants.lblCnst.February();
			case 3:
				return LabelsConstants.lblCnst.March();
			case 4:
				return LabelsConstants.lblCnst.April();
			case 5:
				return LabelsConstants.lblCnst.May();
			case 6:
				return LabelsConstants.lblCnst.June();
			case 7:
				return LabelsConstants.lblCnst.July();
			case 8:
				return LabelsConstants.lblCnst.August();
			case 9:
				return LabelsConstants.lblCnst.September();
			case 10:
				return LabelsConstants.lblCnst.October();
			case 11:
				return LabelsConstants.lblCnst.November();
			case 12:
				return LabelsConstants.lblCnst.December();
		}
		return LabelsConstants.lblCnst.January();
	}

	public static boolean canCalculateDate(ItemCube dateItem, InfosReport infosReport, int dateItemI, int dateItemJ) {
		if(infosReport.getGrid().isDateFuntionCalculated()) {
			return false;
		}
		
		if(isDiffYearValid(dateItem, infosReport, dateItemI, dateItemJ)) {
			return true;
		}
		
		if(isYearToValid(dateItem, infosReport, dateItemI, dateItemJ)) {
			return true;
		}
		
		return false;
	}
	
	private static boolean isDiffYearValid(ItemCube item, InfosReport infosReport, int dateItemI, int dateItemJ) {
		if(dateItemI == 0 || dateItemJ == 0) {
			if(dateItemI == 0) {
				if(infosReport.getIFirst() == 2) {
					return true;
				}
			}
			else {
				if(infosReport.getJFirst() == 2) {
					return true;
				}
			}
		}
		return false;
	}

	private static boolean isYearToValid(ItemCube item, InfosReport infosReport, int dateItemI, int dateItemJ) {
		if(dateItemI == 0 || dateItemJ == 0) {
			if(dateItemI == 0) {
				if(infosReport.getIFirst() == 3) {
					return true;
				}
			}
			else {
				if(infosReport.getJFirst() == 3) {
					return true;
				}
			}
		}
		return false;
	}
}
