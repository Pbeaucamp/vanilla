package bpm.google.spreadsheet.oda.driver.runtime.model;

import java.util.ArrayList;

public class OdaGoogleAllValuesSheet {

	private ArrayList<String> listValues;
	private ArrayList<String> listSortedCol;
	private ArrayList<Integer> listIndexColSorted;
	private ArrayList<String> listSortedValues;
	private int countSortedCol;


	public OdaGoogleAllValuesSheet(){
		listSortedCol = new ArrayList<String>();
		listIndexColSorted = new ArrayList<Integer>();
		listSortedValues = new ArrayList<String>();
		countSortedCol= 0;

	}


	public void extractColumns(int countCol, int rowCount){

		//Extract columns in the sheet > FIRT ROW in the sheet.
		listSortedCol.clear();
		listIndexColSorted.clear();
		listSortedValues.clear();

		for(int indexCol = 0; indexCol < countCol; indexCol++){

			//Select non empty columns and save index of empty col
			if(listValues.get(indexCol) != null){
				listSortedCol.add(listValues.get(indexCol));
				listIndexColSorted.add(indexCol);
			}	
			
		}
		
		//Keep values from selected columns
		for(int indexRow = 0; indexRow < rowCount; indexRow++){
			
			for(Integer currentIndexCol: listIndexColSorted){		
				listSortedValues.add(listValues.get((indexRow * countCol)+ currentIndexCol));
			}
		
		}
		
		//Add all selected values in values List.
		listValues.clear();
		listValues.addAll(listSortedValues);
		
		countSortedCol = listIndexColSorted.size();
	}





	public ArrayList<String> getListValues() {
		return listValues;
	}

	public void setListValues(ArrayList<String> listValues) {
		this.listValues = listValues;
	}


	public ArrayList<String> getListSortedCol() {
		return listSortedCol;
	}


	public ArrayList<Integer> getListIndexColSorted() {
		return listIndexColSorted;
	}


	public ArrayList<String> getListSortedValues() {
		return listSortedValues;
	}


	public int getCountSortedCol() {
		return countSortedCol;
	}



}
