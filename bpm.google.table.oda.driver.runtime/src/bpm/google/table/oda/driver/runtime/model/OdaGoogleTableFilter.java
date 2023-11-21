package bpm.google.table.oda.driver.runtime.model;

import java.util.ArrayList;

public class OdaGoogleTableFilter {

	private String nameColFilter, typeFilter, value1Filter, value2Filter, logicalSeparator;
	
	private static ArrayList<Integer> listIndexRowsHiddenFirst;
	private static ArrayList<Integer> listIndexRowsHiddenNext;
	private static ArrayList<Integer> listIndexRowsHiddenFinal;
	
	public OdaGoogleTableFilter(String nameColFilter, String typeFilter,
			String value1Filter, String value2Filter, String pLogical) {
		
		super();
		this.nameColFilter = nameColFilter;
		this.typeFilter = typeFilter;
		this.value1Filter = value1Filter;
		this.value2Filter = value2Filter;
		this.logicalSeparator = pLogical;
		
	}
	
	

	public OdaGoogleTableFilter() {
		
		this.nameColFilter = "";
		this.typeFilter = "";
		this.value1Filter = "";
		this.value2Filter = "";
		this.logicalSeparator = "";
	}



	public static void updateDataSetWithFilters( OdaGoogleTableModel tableSelected, ArrayList<OdaGoogleTableFilter> listDataSetFilters) {
		
		listIndexRowsHiddenFirst = new ArrayList<Integer>();
		listIndexRowsHiddenNext = new ArrayList<Integer>();
		listIndexRowsHiddenFinal = new ArrayList<Integer>();
		
		for(OdaGoogleTableFilter currentFilter : listDataSetFilters){
			
			int indexCurrentRow = 0; 
			
			
			listIndexRowsHiddenFirst.clear();
			listIndexRowsHiddenFirst.addAll(listIndexRowsHiddenFinal);
			listIndexRowsHiddenNext.clear();
			
			for(OdaGoogleTableRow currentRow : tableSelected.getListRows()){
				
				applyCurrentFilter(tableSelected, currentRow, currentFilter, indexCurrentRow);
				indexCurrentRow++;
			}
			
			applyLogicalFilter(currentFilter.getLogicalSeparator());
		}
		
		//Update final hidden row
		int subIndexRow = 0;
		for(OdaGoogleTableRow rowUpdate : tableSelected.getListRows()){
			
			if(listIndexRowsHiddenFinal.contains(subIndexRow)){
				rowUpdate.setFiltered(true);
			}
			else{
				rowUpdate.setFiltered(false);
			}
			
			subIndexRow++;
		}
	}
	

	private static void applyLogicalFilter(String logicalSeparator2) {
		
		listIndexRowsHiddenFinal.clear();

	//Logical AND or null >> Hide all row from both filters
		if(logicalSeparator2.equals(OdaGoogleTableParameter.QUERY_NO_LOGICAL) || logicalSeparator2.equals(OdaGoogleTableParameter.LOGICAL_OPERATORS_TAB[OdaGoogleTableParameter.LOGICAL_INDEX_AND])){
			
			listIndexRowsHiddenFinal.addAll(listIndexRowsHiddenFirst);
		
			for(Integer rowNext : listIndexRowsHiddenNext){
				if(listIndexRowsHiddenFinal.contains(rowNext) == false){
					
					listIndexRowsHiddenFinal.add(rowNext);
				}
			}
		}
		
	//Logical OR >> Hide just common row from both filters
		else{
			
			listIndexRowsHiddenFirst.retainAll(listIndexRowsHiddenNext);
			listIndexRowsHiddenNext.retainAll(listIndexRowsHiddenFirst);
			
			listIndexRowsHiddenFinal.addAll(listIndexRowsHiddenNext);
		}
	}

	private static void applyCurrentFilter(OdaGoogleTableModel tableSelect, OdaGoogleTableRow currentRow,
			OdaGoogleTableFilter currentFilter, int indexCurrentRow) {
		
		
		//Valide or not the cell with the filter in concerned column
		
			String indexOperatorFilter = currentFilter.getTypeFilter();
			String value1, value2, valueCell = null;
			Class classColConcerned = null;
			
			int indexColConcerned = 0;
			
			boolean valueOk = false;

		//Get concerned column
			for(OdaGoogleTableColumn col: tableSelect.getListColumns()){
				
				if(col.getColName().equals(currentFilter.getNameColFilter())){
					indexColConcerned = col.getColIndex();
					classColConcerned = col.getColClass();
				}
			}	
			
		//Test if value is filterable or not
			try {
				if(classColConcerned == Integer.class){
					valueCell = currentRow.getListCells().get(indexColConcerned);
					Integer.parseInt(valueCell);
					valueOk = true;
				}
				else if(classColConcerned == Double.class){
					valueCell = currentRow.getListCells().get(indexColConcerned);
					Double.parseDouble(valueCell);
					valueOk = true;
				}
				else{
					valueOk = true;
				}
				
				
				
			} catch (Exception e) {
				valueOk = false;
			}
			
			if(valueOk == false){
				currentRow.setFiltered(true);
			}
			else{
			
		
				// ***** 1 -  Operator : less
					if(indexOperatorFilter.equals(OdaGoogleTableParameter.OPERATORS_TAB[OdaGoogleTableParameter.OPERATORS_INDEX_LESS])){
						
						value1 = currentFilter.getValue1Filter();
						valueCell = currentRow.getListCells().get(indexColConcerned);
						
						if((valueCell == null) || valueCell.length() == 0){
							currentRow.setFiltered(true);
						}
						
						else {
							if(objCompareTo(classColConcerned,valueCell,value1) < 0){
								
								currentRow.setFiltered(false);	
							}
							else{
								currentRow.setFiltered(true);
							}
						}
					}
					
					
				// ***** 2 -  Operator : less equals
					if(indexOperatorFilter.equals(OdaGoogleTableParameter.OPERATORS_TAB[OdaGoogleTableParameter.OPERATORS_INDEX_LESS_EQUALS])){
						
						value1 = currentFilter.getValue1Filter();
						valueCell = currentRow.getListCells().get(indexColConcerned);
						
						if((valueCell == null) || valueCell.length() == 0){
							currentRow.setFiltered(true);
						}
						
						else {
							if(objCompareTo(classColConcerned,valueCell,value1) <= 0){	
								currentRow.setFiltered(false);	
							}
							else{
								currentRow.setFiltered(true);	
							}
						}
					}
					
				// ***** 3 -  Operator : Greather
					if(indexOperatorFilter.equals(OdaGoogleTableParameter.OPERATORS_TAB[OdaGoogleTableParameter.OPERATORS_INDEX_GREATHER])){
						
						value1 = currentFilter.getValue1Filter();
						valueCell = currentRow.getListCells().get(indexColConcerned);
						
						if((valueCell == null) || valueCell.length() == 0){
							currentRow.setFiltered(true);
						}
						
						else {
							if(objCompareTo(classColConcerned,valueCell,value1) > 0){	
								currentRow.setFiltered(false);	
							}
							else{
								currentRow.setFiltered(true);	
							}
						}
					}
					
				// ***** 4 - Operator : Greather equals
					if(indexOperatorFilter.equals(OdaGoogleTableParameter.OPERATORS_TAB[OdaGoogleTableParameter.OPERATORS_INDEX_GREATHER_EQUALS])){
						
						value1 = currentFilter.getValue1Filter();
						valueCell = currentRow.getListCells().get(indexColConcerned);
						
						if((valueCell == null) || valueCell.length() == 0){
							currentRow.setFiltered(true);
						}
						
						else {
							if(objCompareTo(classColConcerned,valueCell,value1) >= 0){	
								currentRow.setFiltered(false);	
							}
							else{
								currentRow.setFiltered(true);	
							}
						}
					}
					
					
					// ***** 5 - Operator : Equals
						if(indexOperatorFilter.equals(OdaGoogleTableParameter.OPERATORS_TAB[OdaGoogleTableParameter.OPERATORS_INDEX_EQUALS])){
							
							value1 = currentFilter.getValue1Filter();
							valueCell = currentRow.getListCells().get(indexColConcerned);
							
							if(valueCell == null){
								currentRow.setFiltered(true);
							}
							
							else {
								if(objCompareTo(classColConcerned,valueCell,value1) == 0){	
									currentRow.setFiltered(false);	
								}
								else{
									currentRow.setFiltered(true);	
								}
							}
						}
						
						
						
					// ***** 6 - Operator : NOT equals
							if(indexOperatorFilter.equals(OdaGoogleTableParameter.OPERATORS_TAB[OdaGoogleTableParameter.OPERATORS_INDEX_NOT_EQUALS])){
								
								value1 = currentFilter.getValue1Filter();
								valueCell = currentRow.getListCells().get(indexColConcerned);
								
								if((valueCell == null) || valueCell.length() == 0){
									currentRow.setFiltered(true);
								}
								
								else {
									if(objCompareTo(classColConcerned,valueCell,value1) != 0){	
										currentRow.setFiltered(false);	
									}
									else{
										currentRow.setFiltered(true);	
									}
								}
							}
							
						// ***** 7 - Operator : NULL
							if(indexOperatorFilter.equals(OdaGoogleTableParameter.OPERATORS_TAB[OdaGoogleTableParameter.OPERATORS_INDEX_NULL])){
								
								valueCell = currentRow.getListCells().get(indexColConcerned);
								
									if(valueCell == null){	
										currentRow.setFiltered(false);	
									}
									else if((valueCell.toString().equals("0")) || (valueCell.toString().equals("0.0")) ||  (valueCell.equals(""))){
										currentRow.setFiltered(false);	
									}
									else{
										currentRow.setFiltered(true);
									}
								
								}
							
						// ***** 8 - Operator : Not null
							if(indexOperatorFilter.equals(OdaGoogleTableParameter.OPERATORS_TAB[OdaGoogleTableParameter.OPERATORS_INDEX_NULL_NOT])){
								
								valueCell = currentRow.getListCells().get(indexColConcerned);
								
									if((valueCell != null) && (!valueCell.equals(""))){	
										currentRow.setFiltered(false);	
									}
									
									else{
										currentRow.setFiltered(true);	
									}
								
								}
							
						// ***** 9 - Operator : TRUE
							if(indexOperatorFilter.equals(OdaGoogleTableParameter.OPERATORS_TAB[OdaGoogleTableParameter.OPERATORS_INDEX_TRUE])){
								
								valueCell = currentRow.getListCells().get(indexColConcerned);
								
								if((valueCell == null) || valueCell.length() == 0){
									currentRow.setFiltered(true);
								}
								
								else {
									
									valueCell = valueCell.toLowerCase();
									
									if(valueCell.equals("true") ){	
										currentRow.setFiltered(false);	
									}
									else{
										currentRow.setFiltered(true);
									}
								}
								
							}
							
							
						// ***** 10 - Operator : false
							if(indexOperatorFilter.equals(OdaGoogleTableParameter.OPERATORS_TAB[OdaGoogleTableParameter.OPERATORS_INDEX_FALSE])){
								
								valueCell = currentRow.getListCells().get(indexColConcerned);
								
								if((valueCell == null) || valueCell.length() == 0){
									currentRow.setFiltered(true);
								}
								
								else {
									
									valueCell = valueCell.toLowerCase();
									
									if(valueCell.equals("false")){	
										currentRow.setFiltered(false);	
									}
									else{
										currentRow.setFiltered(true);	
									}
								}
							}
							
						//***** 11 - Operator : bewteen
							if(indexOperatorFilter.equals(OdaGoogleTableParameter.OPERATORS_TAB[OdaGoogleTableParameter.OPERATORS_INDEX_BETWEEN])){
								
								valueCell = currentRow.getListCells().get(indexColConcerned);
								value1 = currentFilter.getValue1Filter();
								value2 = currentFilter.getValue2Filter();
								
								if(betweenCompare(valueCell,value1,value2, classColConcerned)){
									
									currentRow.setFiltered(false);	
								}
								else{
									currentRow.setFiltered(true);
								}
		
							}
					
						//***** 12 - Operator : not bewteen
							if(indexOperatorFilter.equals(OdaGoogleTableParameter.OPERATORS_TAB[OdaGoogleTableParameter.OPERATORS_INDEX_BETWEEN_NOT])){
								
								valueCell = currentRow.getListCells().get(indexColConcerned);
								value1 = currentFilter.getValue1Filter();
								value2 = currentFilter.getValue2Filter();
								
								if(betweenCompare(valueCell,value1,value2, classColConcerned) == false){
									
									currentRow.setFiltered(false);	
								}
								else{
									currentRow.setFiltered(true);
								}
		
							}
					
				}			
							
		if(currentRow.isFiltered()){
			listIndexRowsHiddenNext.add(indexCurrentRow);
		}

			
		
	}

	private static int objCompareTo(Class classObj, String valueData, String valueFilter1){
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
	
	private static boolean betweenCompare(String valueData, String valueFilter1, String valueFilter2, Class classObj){
		
		int comp1 = 2, comp2 = 2;
		if(valueData == null){
			valueData = "0";
		}
		
		if(classObj == Integer.class){
			
			Integer intValueFilter1 = Integer.valueOf(valueFilter1);
			Integer intValueFilter2 = Integer.valueOf(valueFilter2);
			Integer intValueData = Integer.valueOf(valueData);
			
			comp1 = intValueFilter1.compareTo(intValueData);
			comp2 = intValueData.compareTo(intValueFilter2);
			
		}
		
		else if(classObj == Double.class){
			
			Double intValueFilter1 = Double.valueOf(valueFilter1);
			Double intValueFilter2 = Double.valueOf(valueFilter2);
			Double intValueData = Double.valueOf(valueData);
			
			comp1 = intValueFilter1.compareTo(intValueData);
			comp2 = intValueData.compareTo(intValueFilter2);
		}
		
		else{
			String intValueFilter1 = valueFilter1;
			String intValueFilter2 = valueFilter2;
			String intValueData = valueData;
			
			comp1 = intValueFilter1.compareTo(intValueData);
			comp2 = intValueData.compareTo(intValueFilter2);
		}
		
		
		if((comp1 <= 0) && (comp2 <= 0)){
			return true;
		}
		
		else{
			return  false;
		}
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
	
	// Operators

	


}
