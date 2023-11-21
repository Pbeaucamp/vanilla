/*
 *************************************************************************
 * Copyright (c) 2008 <<Your Company Name here>>
 *  
 *************************************************************************
 */

package bpm.google.spreadsheet.oda.driver.runtime.impl;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.eclipse.datatools.connectivity.oda.IParameterMetaData;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.datatools.connectivity.oda.SortSpec;
import org.eclipse.datatools.connectivity.oda.spec.QuerySpecification;

import bpm.google.spreadsheet.oda.driver.runtime.model.OdaGoogleDataSetFilterQuery;
import bpm.google.spreadsheet.oda.driver.runtime.model.OdaGoogleFilterData;
import bpm.google.spreadsheet.oda.driver.runtime.model.OdaGoogleParameter;
import bpm.google.spreadsheet.oda.driver.runtime.model.OdaGoogleProperties;
import bpm.google.spreadsheet.oda.driver.runtime.model.OdaGoogleSheet;
import bpm.google.spreadsheet.oda.driver.runtime.model.OdaGoogleSheetColumn;
import bpm.google.spreadsheet.oda.driver.runtime.model.OdaGoogleSheetRow;


public class Query implements IQuery
{
	private int m_maxRows;
	
	private ArrayList<OdaGoogleSheet> listQuerySheets;
	private ArrayList<OdaGoogleFilterData> listDataSetFilters;
	private ArrayList<OdaGoogleParameter> listQueryParameters;
	
	private OdaGoogleSheet sheetSelected;
	
	private ResultSetMetaData resultSetMetaData;
	private ParameterMetaData parameterMetaData;
	
	public Query(ArrayList<OdaGoogleSheet> listSheets) {
		
			
		listQuerySheets = new ArrayList<OdaGoogleSheet>();
		listQuerySheets.addAll(listSheets);
		
		listDataSetFilters = new ArrayList<OdaGoogleFilterData>();
		listQueryParameters = new ArrayList<OdaGoogleParameter>();
		
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#prepare(java.lang.String)
	 */
	public void prepare( String queryText ) throws OdaException
	{
	
	//+++++++ Split the differents options in query text
		
		String[] tabElementsQuery = queryText.split(OdaGoogleProperties.QUERY_SEPARATOR_ELEMENT);

        
	//------- First element: Sheet selected : Get the selected Sheet by his name

		for(OdaGoogleSheet currentSheet : listQuerySheets){
			
			if(currentSheet.getSheetName().equals(tabElementsQuery[0])){
				
				sheetSelected = currentSheet;
			}
			
		}		
		
	//------- Second element: Skip the first row or not
		String rowSkipped = tabElementsQuery[1];
		
		
		if((rowSkipped.equals(OdaGoogleProperties.QUERY_SKIP_TRUE)) && (!sheetSelected.isFirstRowRemoved())){
			
			if(sheetSelected.getSheetListRow().get(0) != null){
				sheetSelected.getSheetListRow().remove(0);
				sheetSelected.setSheetCountRow(sheetSelected.getSheetCountRow() -1);
				sheetSelected.setFirstRowRemoved(true);
			}
			
		}
		
		
	//------- Third element: Get selected columns
		
		String queryColSelected = tabElementsQuery[2];
		
		String[] listColsSelected = queryColSelected.split(OdaGoogleProperties.QUERY_SEPARATOR_COL_SELECTED);
		
		//Remove columns no selected and concerned cells.
		sheetSelected.sortColumnsToRemove(listColsSelected);
			
		
		
		
	//------- Fourth element: Get selected columns label
		
		String queryColSelectedLabel = tabElementsQuery[3];
		
		String[] listColsSelectedLabels = queryColSelectedLabel.split(OdaGoogleProperties.QUERY_SEPARATOR_COL_SELECTED);
		
		//Insert Labels name
		int indexLabel = 0;
		for(OdaGoogleSheetColumn currentCol : sheetSelected.getSheetListCol()){
			
			currentCol.setColLabelName(listColsSelectedLabels[indexLabel]);
			indexLabel++;
		}
		
	//-------- 5° element : Get filers
		String strQueryFilter = tabElementsQuery[4];

		//++++++ Split Filters List, if there is greather than or equal 1 filter
		
		if(strQueryFilter.equals(OdaGoogleDataSetFilterQuery.QUERY_NO_FILTER)== false){

			String[] tabFilters = strQueryFilter.split(OdaGoogleDataSetFilterQuery.SEPARATOR_QUERY);
		
			for(int i = 0; i< tabFilters.length; i++){

				//Split the Filter to get the coolumns name, the operator and values
				String[] subTabFilter = tabFilters[i].split(OdaGoogleProperties.QUERY_SEPARATOR_COL_SELECTED);
				
					//Teste IF there is 0, 1 or 2 values
					if(subTabFilter.length == 3){
						listDataSetFilters.add(new OdaGoogleFilterData(subTabFilter[0],subTabFilter[1],OdaGoogleDataSetFilterQuery.QUERY_FILTER_NO_VALUES, OdaGoogleDataSetFilterQuery.QUERY_FILTER_NO_VALUES,subTabFilter[2]));
					}
					else if(subTabFilter.length == 4){
						listDataSetFilters.add(new OdaGoogleFilterData(subTabFilter[0],subTabFilter[1],subTabFilter[2], OdaGoogleDataSetFilterQuery.QUERY_FILTER_NO_VALUES,subTabFilter[3]));
					}
					else{
						listDataSetFilters.add(new OdaGoogleFilterData(subTabFilter[0],subTabFilter[1],subTabFilter[2], subTabFilter[3],subTabFilter[4]));
					}
				}

			
		}
		
		//Apply filter(s)
		OdaGoogleFilterData.updateDataSetWithFilters(sheetSelected, listDataSetFilters);
		
		ArrayList<OdaGoogleSheetRow> listRowToRemove = new ArrayList<OdaGoogleSheetRow>();
		
		for(OdaGoogleSheetRow currentRow : sheetSelected.getSheetListRow()){
			
			if(currentRow.isFiltered()){
				listRowToRemove.add(currentRow);
			
			}
		}
		
		sheetSelected.getSheetListRow().removeAll(listRowToRemove);
		sheetSelected.setSheetCountRow(sheetSelected.getSheetListRow().size());
		
		
	//---------- 6° element : Parameters
		String queryParameters = tabElementsQuery[5];
		
		if(queryParameters.equals(OdaGoogleDataSetFilterQuery.QUERY_NO_PARAMETER) == false	){
			String[] listParameters = queryParameters.split(OdaGoogleDataSetFilterQuery.SEPARATOR_QUERY);

			
			
			for(String currentDetailsParam : listParameters){
				
				String[] detailsParam = currentDetailsParam.split(OdaGoogleProperties.QUERY_SEPARATOR_COL_SELECTED);
				
				//Get Column
				String colParameter = detailsParam[0];
				
				//Get Operator
				String opeParameter = detailsParam[1];
				
				//Get Column class
				Class classParameter = null;
				for(OdaGoogleSheetColumn co : sheetSelected.getSheetListCol()){
					if(co.getColName().equals(colParameter)){
						classParameter = co.getColClass();
					}
				}
			
				
				/*In case the user can enter a logical operator, change only the last parameter 
				 * in  constructors. If the operator is "between" or "not between", just change 
				 * the operator of "TempParam". */ 
				
				OdaGoogleParameter tempParam = null;
				OdaGoogleParameter tempParam2 = null;
				
				String tempLogicalAnd = OdaGoogleFilterData.LOGICAL_OPERATORS_TAB[OdaGoogleFilterData.LOGICAL_INDEX_AND];
				String tempLogicalOr = OdaGoogleFilterData.LOGICAL_OPERATORS_TAB[OdaGoogleFilterData.LOGICAL_INDEX_OR];
				String tempLogicalNull = OdaGoogleDataSetFilterQuery.QUERY_NO_LOGICAL;
				
				//------- Build two parameters if Operator is "Between"
				if(opeParameter.equals(OdaGoogleFilterData.OPERATORS_TAB[OdaGoogleFilterData.OPERATORS_INDEX_BETWEEN])){
					tempParam =  new OdaGoogleParameter(colParameter, OdaGoogleFilterData.OPERATORS_TAB[OdaGoogleFilterData.OPERATORS_INDEX_GREATHER_EQUALS], classParameter, tempLogicalNull);
					tempParam2 =  new OdaGoogleParameter(colParameter, OdaGoogleFilterData.OPERATORS_TAB[OdaGoogleFilterData.OPERATORS_INDEX_LESS_EQUALS], classParameter, tempLogicalAnd);
					
				}
				
				//------- Build two parameters if Operator is "NOT Between"
				else if(opeParameter.equals(OdaGoogleFilterData.OPERATORS_TAB[OdaGoogleFilterData.OPERATORS_INDEX_BETWEEN_NOT])){
					tempParam =  new OdaGoogleParameter(colParameter, OdaGoogleFilterData.OPERATORS_TAB[OdaGoogleFilterData.OPERATORS_INDEX_LESS], classParameter, tempLogicalNull);
					tempParam2 =  new OdaGoogleParameter(colParameter, OdaGoogleFilterData.OPERATORS_TAB[OdaGoogleFilterData.OPERATORS_INDEX_GREATHER], classParameter, tempLogicalOr);
					
				}
						
				else{
					tempParam =  new OdaGoogleParameter(colParameter, opeParameter, classParameter, tempLogicalNull);
				}
				
				if(tempParam2 == null){
					tempParam.setParamName("Parameter " + (listQueryParameters.size()+1) + " on " + tempParam.getParamColumn());
					listQueryParameters.add(tempParam);
				}
				else{
					tempParam.setParamName("Parameter " + (listQueryParameters.size()+1) + ".1 on " + tempParam.getParamColumn());
					listQueryParameters.add(tempParam);
					
					tempParam2.setParamName("Parameter " + (listQueryParameters.size()) + ".2  on " + tempParam.getParamColumn());
					listQueryParameters.add(tempParam2);
				}
			}
		}
	
		parameterMetaData = new ParameterMetaData(listQueryParameters);
		resultSetMetaData = new ResultSetMetaData(sheetSelected);
	}
	
	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setAppContext(java.lang.Object)
	 */
	public void setAppContext( Object context ) throws OdaException
	{
	    // do nothing; assumes no support for pass-through context
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#close()
	 */
	public void close() throws OdaException
	{
        
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#getMetaData()
	 */
	public IResultSetMetaData getMetaData() throws OdaException
	{
		
		return resultSetMetaData;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#executeQuery()
	 */
	public IResultSet executeQuery() throws OdaException
	{
		// Apply parameters 
		
		OdaGoogleParameter.updateDataSetWithParameters(sheetSelected, listQueryParameters);
		
		ArrayList<OdaGoogleSheetRow> listRowToRemoveParam = new ArrayList<OdaGoogleSheetRow>();
		
		for(OdaGoogleSheetRow currentRow : sheetSelected.getSheetListRow()){
			
			if(currentRow.isFiltered()){
				listRowToRemoveParam.add(currentRow);
			
			}
		}
		//System.out.println("Rows removed : " + listRowToRemoveParam.size());
		
		sheetSelected.getSheetListRow().removeAll(listRowToRemoveParam);
		sheetSelected.setSheetCountRow(sheetSelected.getSheetListRow().size());
		
		
		
		IResultSet resultSet = new ResultSet(sheetSelected);
		
		resultSet.setMaxRows(getMaxRows());
		
		return resultSet;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setProperty(java.lang.String, java.lang.String)
	 */
	public void setProperty( String name, String value ) throws OdaException
	{
		// do nothing; assumes no data set query property
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setMaxRows(int)
	 */
	public void setMaxRows( int max ) throws OdaException
	{
	    m_maxRows = max;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#getMaxRows()
	 */
	public int getMaxRows() throws OdaException
	{
		return m_maxRows;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#clearInParameters()
	 */
	public void clearInParameters() throws OdaException
	{
        listQueryParameters.clear();
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setInt(java.lang.String, int)
	 */
	public void setInt( String parameterName, int value ) throws OdaException
	{
		
        for(OdaGoogleParameter currentPara : listQueryParameters){
        	
        	if(currentPara.getParamName().equals(parameterName)){
        		currentPara.setParamValue1(value);
        	}
        }
        
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setInt(int, int)
	 */
	public void setInt( int parameterId, int value ) throws OdaException
	{
		listQueryParameters.get(parameterId-1).setParamValue1(value);	
		
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setDouble(java.lang.String, double)
	 */
	public void setDouble( String parameterName, double value ) throws OdaException
	{
		 for(OdaGoogleParameter currentPara : listQueryParameters){
	        	
	        	if(currentPara.getParamName().equals(parameterName)){
	        		currentPara.setParamValue1(value);
	        	}
	        }
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setDouble(int, double)
	 */
	public void setDouble( int parameterId, double value ) throws OdaException
	{
		listQueryParameters.get(parameterId-1).setParamValue1(value);	
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setBigDecimal(java.lang.String, java.math.BigDecimal)
	 */
	public void setBigDecimal( String parameterName, BigDecimal value ) throws OdaException
	{
		 for(OdaGoogleParameter currentPara : listQueryParameters){
	        	
	        	if(currentPara.getParamName().equals(parameterName)){
	        		currentPara.setParamValue1(value);
	        	}
	        }
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setBigDecimal(int, java.math.BigDecimal)
	 */
	public void setBigDecimal( int parameterId, BigDecimal value ) throws OdaException
	{
		listQueryParameters.get(parameterId-1).setParamValue1(value);	
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setString(java.lang.String, java.lang.String)
	 */
	public void setString( String parameterName, String value ) throws OdaException
	{
		 for(OdaGoogleParameter currentPara : listQueryParameters){
	        	
	        	if(currentPara.getParamName().equals(parameterName)){
	        		currentPara.setParamValue1(value);
	        	}
	        }
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setString(int, java.lang.String)
	 */
	public void setString( int parameterId, String value ) throws OdaException
	{
		listQueryParameters.get(parameterId-1).setParamValue1(value);	
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setDate(java.lang.String, java.sql.Date)
	 */
	public void setDate( String parameterName, Date value ) throws OdaException
	{
		 for(OdaGoogleParameter currentPara : listQueryParameters){
	        	
	        	if(currentPara.getParamName().equals(parameterName)){
	        		currentPara.setParamValue1(value);
	        	}
	        }
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setDate(int, java.sql.Date)
	 */
	public void setDate( int parameterId, Date value ) throws OdaException
	{
		listQueryParameters.get(parameterId-1).setParamValue1(value);	
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setTime(java.lang.String, java.sql.Time)
	 */
	public void setTime( String parameterName, Time value ) throws OdaException
	{
         for(OdaGoogleParameter currentPara : listQueryParameters){
        	
        	if(currentPara.getParamName().equals(parameterName)){
        		currentPara.setParamValue1(value);
        	}
        }
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setTime(int, java.sql.Time)
	 */
	public void setTime( int parameterId, Time value ) throws OdaException
	{
		listQueryParameters.get(parameterId-1).setParamValue1(value);	
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setTimestamp(java.lang.String, java.sql.Timestamp)
	 */
	public void setTimestamp( String parameterName, Timestamp value ) throws OdaException
	{
		 for(OdaGoogleParameter currentPara : listQueryParameters){
	        	
	        	if(currentPara.getParamName().equals(parameterName)){
	        		currentPara.setParamValue1(value);
	        	}
	        }
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setTimestamp(int, java.sql.Timestamp)
	 */
	public void setTimestamp( int parameterId, Timestamp value ) throws OdaException
	{
		listQueryParameters.get(parameterId-1).setParamValue1(value);	
	}

    /* (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.oda.IQuery#setBoolean(java.lang.String, boolean)
     */
    public void setBoolean( String parameterName, boolean value )
            throws OdaException
    {
    	 for(OdaGoogleParameter currentPara : listQueryParameters){
         	
         	if(currentPara.getParamName().equals(parameterName)){
         		currentPara.setParamValue1(value);
         	}
         }
    }

    /* (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.oda.IQuery#setBoolean(int, boolean)
     */
    public void setBoolean( int parameterId, boolean value )
            throws OdaException
    {
    	listQueryParameters.get(parameterId-1).setParamValue1(value);	
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.oda.IQuery#setNull(java.lang.String)
     */
    public void setNull( String parameterName ) throws OdaException
    {
    	 for(OdaGoogleParameter currentPara : listQueryParameters){
         	
         	if(currentPara.getParamName().equals(parameterName)){
         		currentPara.setParamValue1(null);
         	}
         }
    }

    /* (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.oda.IQuery#setNull(int)
     */
    public void setNull( int parameterId ) throws OdaException
    {
    	listQueryParameters.get(parameterId-1).setParamValue1(null);	
    }

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#findInParameter(java.lang.String)
	 */
	public int findInParameter( String parameterName ) throws OdaException
	{
		int i = 0;
		int result = 0;
		
		for(OdaGoogleParameter currentPara : listQueryParameters){
         	
         	if(currentPara.getParamName().equals(parameterName)){
         		result = i;
         	}
         	i++;
         }
		
		return result;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#getParameterMetaData()
	 */
	public IParameterMetaData getParameterMetaData() throws OdaException
	{
		
		return new ParameterMetaData(listQueryParameters);
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setSortSpec(org.eclipse.datatools.connectivity.oda.SortSpec)
	 */
	public void setSortSpec( SortSpec sortBy ) throws OdaException
	{
        
		// only applies to sorting, assumes not supported
        throw new UnsupportedOperationException();
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#getSortSpec()
	 */
	public SortSpec getSortSpec() throws OdaException
	{
        
		// only applies to sorting
		return null;
	}

	public void setParameterMetaData(ParameterMetaData parameterMetaData) {
		
		this.parameterMetaData = parameterMetaData;
	}

	public void cancel() throws OdaException, UnsupportedOperationException {
		
		
	}

	public String getEffectiveQueryText() {
		
		return null;
	}

	public QuerySpecification getSpecification() {
		
		return null;
	}

	public void setObject(String parameterName, Object value)
			throws OdaException {
		
		
	}

	public void setObject(int parameterId, Object value) throws OdaException {
		
		
	}

	public void setSpecification(QuerySpecification querySpec)
			throws OdaException, UnsupportedOperationException {
		
		
	}
    
}
