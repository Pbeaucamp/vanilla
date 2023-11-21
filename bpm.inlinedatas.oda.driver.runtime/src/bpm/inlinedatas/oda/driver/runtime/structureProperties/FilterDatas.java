package bpm.inlinedatas.oda.driver.runtime.structureProperties;

import java.util.ArrayList;

import bpm.inlinedatas.oda.driver.runtime.impl.Query;

public class FilterDatas {
	
	private String nameColFilter, typeFilter, value1Filter, value2Filter, logicalSeparator;
	
	public static ArrayList<Integer> listIndexLineToHide;
	public static ArrayList<Integer> listIndexLineToHideNext;
	public static ArrayList<Integer> listIndexLineToHideFinal;
	
	public FilterDatas(String nameColFilter, String typeFilter,
			String value1Filter, String value2Filter, String pLogical) {
		super();
		this.nameColFilter = nameColFilter;
		this.typeFilter = typeFilter;
		this.value1Filter = value1Filter;
		this.value2Filter = value2Filter;
		logicalSeparator = pLogical;
		
		listIndexLineToHideFinal = new ArrayList<Integer>();
	}
	
	
//Method to update the data set for each Filter
	public static void updateDataSetWithFilters( ArrayList<PropertieColumn> listDataSet, ArrayList<FilterDatas> listDataSetFilters) {
		
		listIndexLineToHide = new ArrayList<Integer>();
		listIndexLineToHideNext = new ArrayList<Integer>();

		
		//Update Dataset  with the propertie IsHiddeAfterFiler

		for(PropertieColumn currentCol: listDataSet){
			
			for (FilterDatas currentFilter: listDataSetFilters){
				
				//If this columns is concerned by this filter
				if(currentCol.getPropertieColName().equals(currentFilter.getNameColFilter())){
					
					//replace all lines in the first list by the second list, to compare logicaly
					listIndexLineToHide.clear();
					listIndexLineToHide.addAll(listIndexLineToHideFinal);
					
					//clear the second to add the new line
					listIndexLineToHideNext.clear();
					
					filterData(currentCol, currentFilter);
					
				}
				
				applyLogicalFilter(currentFilter.getLogicalSeparator());
				
			}
			
		}
		if(listIndexLineToHideFinal.size() > 0){
			
			updateLine(listDataSet);	
		}
	
		
	}
	

private static void applyLogicalFilter(String logicalSeparator2) {

	
	listIndexLineToHideFinal.clear();
	
//------ If logical operator = AND or Null :Hide all lines from list 1 and list2
	if(logicalSeparator2.equals(Query.NO_LOGICAL) || logicalSeparator2.equals(TAB_LOGICALS_OPERATOR[LOGICAL_INDEX_AND])){
		
		listIndexLineToHideFinal.addAll(listIndexLineToHideNext);
		
		for(Integer currentLine : listIndexLineToHide){
			
			if(listIndexLineToHideFinal.contains(currentLine) == false){
				listIndexLineToHideFinal.add(currentLine);
			}
		
		}
	
	}
	
//------ If logical operator = OR : pick up just the common hidden lines
	if(logicalSeparator2.equals(TAB_LOGICALS_OPERATOR[LOGICAL_INDEX_OR])){
		
		for(Integer intListeHide: listIndexLineToHide){
			
			for(Integer intListeHideNext: listIndexLineToHideNext){
				
				if(intListeHide.equals(intListeHideNext)){
					
					listIndexLineToHideFinal.add(intListeHide);
				}
				
			}
		}
		
	}
	
	
	
}


//**************** Method to apply a filter on one column
	
	private static void filterData(PropertieColumn currentCol, FilterDatas currentFilter) {
		
		int indexCurrentLine = 0;

		for(PropertieData currentData: currentCol.getListPropertieData()){
			
		//----------------  1 -  Operator EQUALS
			if(currentFilter.getTypeFilter().equals(TAB_OPERATOR[INDEX_EQUALS])){
				
				//If data is not equals to the filter's value, data is hidden.
				if(objComareTo(currentData.getPropertieDataValue(), currentFilter.getValue1Filter(), currentData.getPropertieDataType()) != 0){
					
					// Hide data and add all line's data which must be hidden
					currentData.setHidenAfterFilter(true);
					listIndexLineToHideNext.add(indexCurrentLine);
				}
			
			}
			
		//---------------- 2 -   Operator NOT EQUALS
			if(currentFilter.getTypeFilter().equals(TAB_OPERATOR[INDEX_NOT_EQUALS])){
				
				//If data is not equals to the filter's value, data is hidden.
				if(objComareTo(currentData.getPropertieDataValue(), currentFilter.getValue1Filter(), currentData.getPropertieDataType()) == 0){
					
					// Hide data and add all line's data which must be hidden
					currentData.setHidenAfterFilter(true);
					listIndexLineToHideNext.add(indexCurrentLine);
				}
			
			}	
			
		//----------------  3 - Operator LESS  THAN
			if(currentFilter.getTypeFilter().equals(TAB_OPERATOR[INDEX_LESS])){
				
				//If data is not equals to the filter's value, data is hidden.
				if(objComareTo(currentData.getPropertieDataValue(), currentFilter.getValue1Filter(), currentData.getPropertieDataType()) >= 0){
					
					// Hide data and add all line's data which must be hidden
					currentData.setHidenAfterFilter(true);
					listIndexLineToHideNext.add(indexCurrentLine);
				}
			
			}	
			
		//----------------  4 -  Operator LESS  THAN OR EQUALS
			if(currentFilter.getTypeFilter().equals(TAB_OPERATOR[INDEX_LESS_EQUALS])){
				
				//If data is not equals to the filter's value, data is hidden.
				if(objComareTo(currentData.getPropertieDataValue(), currentFilter.getValue1Filter(), currentData.getPropertieDataType()) > 0){
					
					// Hide data and add all line's data which must be hidden
					currentData.setHidenAfterFilter(true);
					listIndexLineToHideNext.add(indexCurrentLine);
				}
			
			}	
			
		//----------------  5 -  Operator GREATHER
			if(currentFilter.getTypeFilter().equals(TAB_OPERATOR[INDEX_GREATHER])){
				
				//If data is not equals to the filter's value, data is hidden.
				if(objComareTo(currentData.getPropertieDataValue(), currentFilter.getValue1Filter(), currentData.getPropertieDataType()) <= 0){
					
					// Hide data and add all line's data which must be hidden
					currentData.setHidenAfterFilter(true);
					listIndexLineToHideNext.add(indexCurrentLine);
				}
			
			}		

		//----------------  6 -  Operator GREATHER OR EQUALS
			if(currentFilter.getTypeFilter().equals(TAB_OPERATOR[INDEX_GREATHER_EQUALS])){
				
				//If data is not equals to the filter's value, data is hidden.
				if(objComareTo(currentData.getPropertieDataValue(), currentFilter.getValue1Filter(), currentData.getPropertieDataType()) < 0){
					
					// Hide data and add all line's data which must be hidden
					currentData.setHidenAfterFilter(true);
					listIndexLineToHideNext.add(indexCurrentLine);
				}
			
			}
			
		//----------------  7 - Operator NULL
			if(currentFilter.getTypeFilter().equals(TAB_OPERATOR[INDEX_NULL])){
				
				//If data is not equals to the filter's value, data is hidden.
				if(currentData.getPropertieDataValue().toString().equals("null") == false){
					
					// Hide data and add all line's data which must be hidden
					currentData.setHidenAfterFilter(true);
					listIndexLineToHideNext.add(indexCurrentLine);
				}
			
			}	
			
		//----------------  8 -  Operator NOT NULL
			if(currentFilter.getTypeFilter().equals(TAB_OPERATOR[INDEX_NULL_NOT])){
				
				//If data is not equals to the filter's value, data is hidden.
				if(currentData.getPropertieDataValue().toString().equals("null")){
					
					// Hide data and add all line's data which must be hidden
					currentData.setHidenAfterFilter(true);
					listIndexLineToHideNext.add(indexCurrentLine);
				}
			
			}	
			
		//----------------  9 -  Operator TRUE
			if(currentFilter.getTypeFilter().equals(TAB_OPERATOR[INDEX_TRUE])){
				
				//If data is not equals to the filter's value, data is hidden.
				if(currentData.getPropertieDataValue().equals(false)){
					
					// Hide data and add all line's data which must be hidden
					currentData.setHidenAfterFilter(true);
					listIndexLineToHideNext.add(indexCurrentLine);
				}
			
			}	
			
		//----------------  10 -  Operator TRUE
			if(currentFilter.getTypeFilter().equals(TAB_OPERATOR[INDEX_FALSE])){
				
				//If data is not equals to the filter's value, data is hidden.
				if(currentData.getPropertieDataValue().equals(true)){
					
					// Hide data and add all line's data which must be hidden
					currentData.setHidenAfterFilter(true);
					listIndexLineToHideNext.add(indexCurrentLine);
				}
			
			}	
			
		//----------------  11 -  Operator BETWEEN
			if(currentFilter.getTypeFilter().equals(TAB_OPERATOR[INDEX_BETWEEN])){
				
				//If data is not equals to the filter's value, data is hidden.
				if(betweenCompare(currentData.getPropertieDataValue(), currentFilter.getValue1Filter(), currentFilter.getValue2Filter(), currentData.getPropertieDataType()) == false){
		
					// Hide data and add all line's data which must be hidden
					currentData.setHidenAfterFilter(true);
					listIndexLineToHideNext.add(indexCurrentLine);
				}
			
			}
			
		//----------------  11 -  Operator NOT BETWEEN
			if(currentFilter.getTypeFilter().equals(TAB_OPERATOR[INDEX_BETWEEN_NOT])){
				
				//If data is not equals to the filter's value, data is hidden.
				if(betweenCompare(currentData.getPropertieDataValue(), currentFilter.getValue1Filter(), currentFilter.getValue2Filter(), currentData.getPropertieDataType())){
		
					// Hide data and add all line's data which must be hidden
					currentData.setHidenAfterFilter(true);
					listIndexLineToHideNext.add(indexCurrentLine);
				}
			
			}	
			
			
			
			
			indexCurrentLine++;
		}
	
	}

//----- Method to update all data in a same line
	private static void updateLine(ArrayList<PropertieColumn> listDataSet) {
		
		for(PropertieColumn col: listDataSet){
			
			for(int indexDat = 0; indexDat < col.getListPropertieData().size(); indexDat++){
				
				col.getListPropertieData().get(indexDat).setHidenAfterFilter(false);
				
				if(listIndexLineToHideFinal.contains(indexDat)){
					col.getListPropertieData().get(indexDat).setHidenAfterFilter(true);			
				}
			
				
			}
			
		}
		
	}
	
	
//----- Method to Compare the value with ONE filter's Value
	@SuppressWarnings("unchecked")
	private static int objComareTo(Object valueData, String valueFilter1, Class classObj){
		
		int resultCompare = 0;
		
		if(classObj == Integer.class){
			
			Integer intValueFilter1 = Integer.valueOf(valueFilter1);
			Integer intValueData = Integer.valueOf(valueData.toString());
			
			resultCompare = intValueData.compareTo(intValueFilter1);
		}
		
		else if(classObj == Double.class){
			
			Double doubleValueFilter1 = Double.valueOf(valueFilter1);
			Double doubleValueData = Double.valueOf(valueData.toString());

			resultCompare = doubleValueData.compareTo(doubleValueFilter1);
		}
		
		else{
			String strValueData= valueData.toString();
			
			resultCompare = strValueData.compareTo(valueFilter1);
		}
		
		return resultCompare;
	}
	
//----- Method to Compare the value with TWO filter's Value > Between
	@SuppressWarnings("unchecked")
	private static boolean betweenCompare(Object valueData, String valueFilter1, String valueFilter2, Class classObj){
		
		int comp1 = 2, comp2 = 2;
		
		if(classObj == Integer.class){
			
			Integer intValueFilter1 = Integer.valueOf(valueFilter1);
			Integer intValueFilter2 = Integer.valueOf(valueFilter2);
			Integer intValueData = Integer.valueOf(valueData.toString());
			
			comp1 = intValueFilter1.compareTo(intValueData);
			comp2 = intValueData.compareTo(intValueFilter2);
			
		}
		
		else if(classObj == Double.class){
			
			Double intValueFilter1 = Double.valueOf(valueFilter1);
			Double intValueFilter2 = Double.valueOf(valueFilter2);
			Double intValueData = Double.valueOf(valueData.toString());
			
			comp1 = intValueFilter1.compareTo(intValueData);
			comp2 = intValueData.compareTo(intValueFilter2);
		}
		
		else{
			String intValueFilter1 = String.valueOf(valueFilter1);
			String intValueFilter2 = String.valueOf(valueFilter2);
			String intValueData = String.valueOf(valueData.toString());
			
			comp1 = intValueFilter1.compareTo(intValueData);
			comp2 = intValueData.compareTo(intValueFilter2);
		}
		
		
		if((comp1 <= 0) && (comp2 <= 0)){
			return true;
		}
		
		else return  false;
	}
	
	
	
	
	

	public String getNameColFilter() {
		return nameColFilter;
	}

	public void setNameColFilter(String nameColFilter) {
		this.nameColFilter = nameColFilter;
	}

	public String getTypeFilter() {
		return typeFilter;
	}

	public void setTypeFilter(String typeFilter) {
		this.typeFilter = typeFilter;
	}

	public String getValue1Filter() {
		return value1Filter;
	}

	public void setValue1Filter(String value1Filter) {
		this.value1Filter = value1Filter;
	}

	public String getValue2Filter() {
		return value2Filter;
	}

	public void setValue2Filter(String value2Filter) {
		this.value2Filter = value2Filter;
	}


	public String getLogicalSeparator() {
		return logicalSeparator;
	}


	public void setLogicalSeparator(String logicalSeparator) {
		this.logicalSeparator = logicalSeparator;
	}

	public final static String[] TAB_OPERATOR = {"Equals to", "Not equals to", "Less than",
		"Less than or Equal", "Greather than", "Greather than or equal", 
		"Between", "Not between","Is null", "Is not null",
		"Is true", "Is false"};

	public final static int INDEX_EQUALS = 0;
	public final static int INDEX_NOT_EQUALS = 1;
	public final static int INDEX_LESS = 2;
	public final static int INDEX_LESS_EQUALS = 3;
	public final static int INDEX_GREATHER = 4;
	public final static int INDEX_GREATHER_EQUALS = 5;
	public final static int INDEX_BETWEEN = 6;
	public final static int INDEX_BETWEEN_NOT = 7;
	public final static int INDEX_NULL = 8;
	public final static int INDEX_NULL_NOT = 9;
	public final static int INDEX_TRUE = 10;
	public final static int INDEX_FALSE = 11;

	public final static String[] TAB_LOGICALS_OPERATOR = {"", "AND", "OR"};
	public final static int LOGICAL_INDEX_NONE = 0;
	public final static int LOGICAL_INDEX_AND = 1;
	public final static int LOGICAL_INDEX_OR = 2;

}
