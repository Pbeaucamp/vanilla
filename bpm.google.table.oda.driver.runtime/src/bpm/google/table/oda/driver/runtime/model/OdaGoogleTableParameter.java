package bpm.google.table.oda.driver.runtime.model;

import java.util.ArrayList;

public class OdaGoogleTableParameter {
	
	private String paramColumn;
	private String paramOperator;
	private String paramLogicalOperator;
	private String paramName;
	
	private Class paramClass;
	private Object paramValue1;
	
	private static ArrayList<Integer> listIndexRowsHiddenFirst;
	private static ArrayList<Integer> listIndexRowsHiddenNext;
	private static ArrayList<Integer> listIndexRowsHiddenFinal;
	
	public final static String QUERY_NO_FILTER = "++No filter++";
	public final static String QUERY_NO_LOGICAL = "++No Logical++";
	public final static String QUERY_NO_PARAMETER = "++No Parameter++";
	
	public final static String QUERY_FILTER_NO_VALUES = "++Any Value++";

	public final static String SEPARATOR_QUERY = ";";
	public final static String SEPARATOR_COLUMN_FILTER = ":";
	
	
	public OdaGoogleTableParameter(){
	}

	public OdaGoogleTableParameter(String colParameter, String opeParameter,
			Class classParameter, String opeLogical) {
		paramColumn = colParameter;
		paramOperator = opeParameter;
		paramLogicalOperator = opeLogical;

		paramName = "";
		
		paramClass = classParameter;
		paramValue1 = null;
		
	}

	public OdaGoogleTableParameter(String colP, String opP) {
		paramColumn = colP;
		paramOperator = opP;
	}
	

	public static void updateDataSetWithParameters(OdaGoogleTableModel tableSelected,
			ArrayList<OdaGoogleTableParameter> listQueryParameters) {
		
		listIndexRowsHiddenFirst = new ArrayList<Integer>();
		listIndexRowsHiddenNext = new ArrayList<Integer>();
		listIndexRowsHiddenFinal = new ArrayList<Integer>();
		

		for(OdaGoogleTableParameter currentParam : listQueryParameters){
			
			listIndexRowsHiddenFirst.clear();
			listIndexRowsHiddenFirst.addAll(listIndexRowsHiddenFinal);
			listIndexRowsHiddenNext.clear();
			
			int indexCurrentRow = 0; 

			for(OdaGoogleTableRow currentRow : tableSelected.getListRows()){
				
				applyCurrentParameter(currentParam, currentRow, tableSelected, indexCurrentRow);
				indexCurrentRow++;
			}
			
			applyLogicalFilter(currentParam.paramLogicalOperator);
			
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
	
	

	private static void applyLogicalFilter(String paramLogicalOperator2) {
		
		listIndexRowsHiddenFinal.clear();

	//Logical AND or null >> Hide all row from both filters
		if(paramLogicalOperator2.equals(QUERY_NO_LOGICAL) || paramLogicalOperator2.equals(LOGICAL_OPERATORS_TAB[LOGICAL_INDEX_AND])){
			
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

	private static void applyCurrentParameter(OdaGoogleTableParameter currentParam,
			OdaGoogleTableRow currentRow, OdaGoogleTableModel sheetSelected, int indexCurrentRow) {
		
		//Valide or not the cell with the filter in concerned column
		
		String indexOperatorFilter = currentParam.getParamOperator();
		String value1, valueCell = null;
		Class classColConcerned = null;
		
		int indexColConcerned = 0;
		
		boolean valueOk = false;

	//Get concerned column
		for(OdaGoogleTableColumn col: sheetSelected.getListColumns()){
			
			if(col.getColName().equals(currentParam.getParamColumn())){
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
		
		else if((currentParam.getParamValue1() == null) ){
			
		}
		
		else{
	
			// ***** 1 -  Operator : less
				if(indexOperatorFilter.equals(OPERATORS_TAB[OPERATORS_INDEX_LESS])){
					
					value1 = currentParam.getParamValue1().toString();
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
				if(indexOperatorFilter.equals(OPERATORS_TAB[OPERATORS_INDEX_LESS_EQUALS])){
					
					value1 = currentParam.getParamValue1().toString();
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
				if(indexOperatorFilter.equals(OPERATORS_TAB[OPERATORS_INDEX_GREATHER])){
					
					value1 = currentParam.getParamValue1().toString();
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
				if(indexOperatorFilter.equals(OPERATORS_TAB[OPERATORS_INDEX_GREATHER_EQUALS])){
					
					value1 = currentParam.getParamValue1().toString();
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
					if(indexOperatorFilter.equals(OPERATORS_TAB[OPERATORS_INDEX_EQUALS])){
						
						value1 = currentParam.getParamValue1().toString();
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
						if(indexOperatorFilter.equals(OPERATORS_TAB[OPERATORS_INDEX_NOT_EQUALS])){
							
							value1 = currentParam.getParamValue1().toString();
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
						if(indexOperatorFilter.equals(OPERATORS_TAB[OPERATORS_INDEX_NULL])){
							
							valueCell = currentRow.getListCells().get(indexColConcerned);
							
								if(valueCell == null){	
									currentRow.setFiltered(false);	
								}
								else if((valueCell.toString().equals("0")) || (valueCell.toString().equals("0.0"))){
									currentRow.setFiltered(false);	
								}
								else{
									currentRow.setFiltered(true);
								}
							
							}
						
					// ***** 8 - Operator : Not null
						if(indexOperatorFilter.equals(OPERATORS_TAB[OPERATORS_INDEX_NULL_NOT])){
							
							valueCell = currentRow.getListCells().get(indexColConcerned);
							
								if(valueCell != null){	
									currentRow.setFiltered(false);	
								}
								
								else{
									currentRow.setFiltered(true);	
								}
							
							}
						
					// ***** 9 - Operator : TRUE
						if(indexOperatorFilter.equals(OPERATORS_TAB[OPERATORS_INDEX_TRUE])){
							
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
						if(indexOperatorFilter.equals(OPERATORS_TAB[OPERATORS_INDEX_FALSE])){
							
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
	

	public String getParamColumn() {
		return paramColumn;
	}


	public void setParamColumn(String paramColumn) {
		this.paramColumn = paramColumn;
	}


	public String getParamOperator() {
		return paramOperator;
	}


	public void setParamOperator(String paramOperator) {
		this.paramOperator = paramOperator;
	}

	public Class getParamClass() {
		return paramClass;
	}

	public void setParamClass(Class paramClass) {
		this.paramClass = paramClass;
	}

	public Object getParamValue1() {
		return paramValue1;
	}

	public void setParamValue1(Object paramValue1) {
		this.paramValue1 = paramValue1;
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}
	
	public final static String[] OPERATORS_TAB = {"Equals to", "Not equals to", "Less than",
		"Less than or Equal", "Greather than", "Greather than or equal", 
		"Between", "Not between","Is null", "Is not null",
		"Is true", "Is false"};

	public final static int OPERATORS_INDEX_EQUALS = 0,
							OPERATORS_INDEX_NOT_EQUALS = 1,
							OPERATORS_INDEX_LESS = 2,
							OPERATORS_INDEX_LESS_EQUALS = 3,
							OPERATORS_INDEX_GREATHER = 4,
							OPERATORS_INDEX_GREATHER_EQUALS = 5,
							OPERATORS_INDEX_BETWEEN = 6,
							OPERATORS_INDEX_BETWEEN_NOT = 7,
							OPERATORS_INDEX_NULL = 8,
							OPERATORS_INDEX_NULL_NOT = 9,
							OPERATORS_INDEX_TRUE = 10,
							OPERATORS_INDEX_FALSE = 11;


	public final static String[] LOGICAL_OPERATORS_TAB = {"", "AND", "OR"};
	public final static int LOGICAL_INDEX_NONE = 0;
	public final static int LOGICAL_INDEX_AND = 1;
	public final static int LOGICAL_INDEX_OR = 2;

}

	
	