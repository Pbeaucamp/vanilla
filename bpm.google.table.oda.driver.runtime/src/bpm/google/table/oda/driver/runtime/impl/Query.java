/*
 *************************************************************************
 * Copyright (c) 2008 <<Your Company Name here>>
 *  
 *************************************************************************
 */

package bpm.google.table.oda.driver.runtime.impl;

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

import bpm.google.table.oda.driver.runtime.model.OdaGoogleTableColumn;
import bpm.google.table.oda.driver.runtime.model.OdaGoogleTableFilter;
import bpm.google.table.oda.driver.runtime.model.OdaGoogleTableModel;
import bpm.google.table.oda.driver.runtime.model.OdaGoogleTableParameter;
import bpm.google.table.oda.driver.runtime.model.OdaGoogleTableRow;


public class Query implements IQuery
{
	private int m_maxRows;
	
	private OdaGoogleTableModel tableSelected;
	
	private ArrayList<OdaGoogleTableParameter> listParameter;
	private ParameterMetaData paramMetaData;
	
	private ArrayList<OdaGoogleTableFilter> listFilters;
	
	private ResultSetMetaData resultSetMetaData;
	
	
	public final static String QUERY_SEPARATOR_ELEMENT = "ø";
	public final static String QUERY_SEPARATOR_COL_SELECTED = ";";
	public final static String QUERY_SEPARATOR_SUB = "!";
	
	public Query(OdaGoogleTableModel pTableSelected) {
		
		tableSelected = pTableSelected;
	}



	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#prepare(java.lang.String)
	 */
	public void prepare( String queryText ) throws OdaException
	{
		listParameter = new ArrayList<OdaGoogleTableParameter>();
		listFilters = new ArrayList<OdaGoogleTableFilter>();
		
		
		String[] tabElementsQuery = queryText.split(QUERY_SEPARATOR_ELEMENT);

        
	// First element: Columns selected 

		String strColSelect = tabElementsQuery[0];
		
		String[] listColsSelected = strColSelect.split(QUERY_SEPARATOR_COL_SELECTED);
		
		tableSelected.sortList(listColsSelected);
		
		
		
	// Second element: Columns Labels 
		
		String queryColSelectedLabel = tabElementsQuery[1];
		
		String[] listColsSelectedLabels = queryColSelectedLabel.split(QUERY_SEPARATOR_COL_SELECTED);
		
		int indexLabel = 0;
		for(OdaGoogleTableColumn currentCol : tableSelected.getListColumns()){
			
			currentCol.setColLabelName(listColsSelectedLabels[indexLabel]);
			indexLabel++;
		}
		
		
		
	// Third element: Parameters 
		String queryParameters = tabElementsQuery[2];
		
		if(!queryParameters.equals(OdaGoogleTableParameter.QUERY_NO_PARAMETER)){
			
			String[] listParameters = queryParameters.split(QUERY_SEPARATOR_COL_SELECTED);

			
			for(String currentDetailsParam : listParameters){
				
				String[] detailsParam = currentDetailsParam.split(QUERY_SEPARATOR_SUB);
				
				//Get Column
				String colParameter = detailsParam[0];
				
				//Get Operator
				String opeParameter = detailsParam[1];
				
				//Get Column class
				Class classParameter = null;
				for(OdaGoogleTableColumn co : tableSelected.getListColumns()){
					if(co.getColName().equals(colParameter)){
						classParameter = co.getColClass();
					}
				}
			
				
				/*In case the user can enter a logical operator, change only the last parameter 
				 * in  constructors. If the operator is "between" or "not between", just change 
				 * the operator of "TempParam". */ 
				
		
				OdaGoogleTableParameter tempParam = null;
				OdaGoogleTableParameter tempParam2 = null;
				
				String tempLogicalAnd = OdaGoogleTableParameter.LOGICAL_OPERATORS_TAB[OdaGoogleTableParameter.LOGICAL_INDEX_AND];
				String tempLogicalOr = OdaGoogleTableParameter.LOGICAL_OPERATORS_TAB[OdaGoogleTableParameter.LOGICAL_INDEX_OR];
				String tempLogicalNull = OdaGoogleTableParameter.QUERY_NO_LOGICAL;
				
				//------- Build two parameters if Operator is "Between"
				if(opeParameter.equals(OdaGoogleTableParameter.OPERATORS_TAB[OdaGoogleTableParameter.OPERATORS_INDEX_BETWEEN])){
					tempParam =  new OdaGoogleTableParameter(colParameter, OdaGoogleTableParameter.OPERATORS_TAB[OdaGoogleTableParameter.OPERATORS_INDEX_GREATHER_EQUALS], classParameter, tempLogicalNull);
					tempParam2 =  new OdaGoogleTableParameter(colParameter, OdaGoogleTableParameter.OPERATORS_TAB[OdaGoogleTableParameter.OPERATORS_INDEX_LESS_EQUALS], classParameter, tempLogicalAnd);
					
				}
				
				//------- Build two parameters if Operator is "NOT Between"
				else if(opeParameter.equals(OdaGoogleTableParameter.OPERATORS_TAB[OdaGoogleTableParameter.OPERATORS_INDEX_BETWEEN_NOT])){
					tempParam =  new OdaGoogleTableParameter(colParameter, OdaGoogleTableParameter.OPERATORS_TAB[OdaGoogleTableParameter.OPERATORS_INDEX_LESS], classParameter, tempLogicalNull);
					tempParam2 =  new OdaGoogleTableParameter(colParameter, OdaGoogleTableParameter.OPERATORS_TAB[OdaGoogleTableParameter.OPERATORS_INDEX_GREATHER], classParameter, tempLogicalOr);
					
				}
						
				else{
					tempParam =  new OdaGoogleTableParameter(colParameter, opeParameter, classParameter, tempLogicalNull);
				}
				
				if(tempParam2 == null){
					tempParam.setParamName("Parameter " + (listParameter.size()+1) + " on " + tempParam.getParamColumn());
					listParameter.add(tempParam);
				}
				else{
					tempParam.setParamName("Parameter " + (listParameter.size()+1) + ".1 on " + tempParam.getParamColumn());
					listParameter.add(tempParam);
					
					tempParam2.setParamName("Parameter " + (listParameter.size()) + ".2  on " + tempParam.getParamColumn());
					listParameter.add(tempParam2);
				}
			}
		}
		
	// Fourth element: Filters 
		if(!tabElementsQuery[3].equals(OdaGoogleTableParameter.QUERY_NO_FILTER)){
			
			listFilters.clear();
			
			//Split the query bu filters
			String[] filters = tabElementsQuery[3].split(Query.QUERY_SEPARATOR_COL_SELECTED);
			
			//Get each filters
			for(String filter : filters){
				
				//Split to get details
				String[] filterDetail = filter.split(Query.QUERY_SEPARATOR_SUB);
				
				//Init differents filters by their size : 0 / 1 or 2 values
				if(filterDetail.length == 3){
					listFilters.add(new OdaGoogleTableFilter(filterDetail[0], filterDetail[1],"","",filterDetail[2]));
				}
				
				else if(filterDetail.length == 4){
					listFilters.add(new OdaGoogleTableFilter(filterDetail[0], filterDetail[1],filterDetail[2],"",filterDetail[3]));
				}
				else{
					listFilters.add(new OdaGoogleTableFilter(filterDetail[0], filterDetail[1],filterDetail[2],filterDetail[3],filterDetail[4]));
				}
			}
			
			//Apply filter(s)
			OdaGoogleTableFilter.updateDataSetWithFilters(tableSelected, listFilters);
			
			ArrayList<OdaGoogleTableRow> listRowToRemove = new ArrayList<OdaGoogleTableRow>();
			
			for(OdaGoogleTableRow currentRow : tableSelected.getListRows()){
				
				if(currentRow.isFiltered()){
					listRowToRemove.add(currentRow);
				
				}
			}
			
			tableSelected.getListRows().removeAll(listRowToRemove);
			tableSelected.setTableCountRow(tableSelected.getListRows().size());
			
		}
				
		
		paramMetaData = new ParameterMetaData(listParameter);
		resultSetMetaData  = new ResultSetMetaData(tableSelected.getListColumns(), tableSelected.getTableCountRow());
		
		
       
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
		
		//-- Apply parameters
		
		OdaGoogleTableParameter.updateDataSetWithParameters(tableSelected, listParameter);
		
		ArrayList<OdaGoogleTableRow> listRowToRemoveParam = new ArrayList<OdaGoogleTableRow>();
		
		for(OdaGoogleTableRow currentRow : tableSelected.getListRows()){
			
			if(currentRow.isFiltered()){
				listRowToRemoveParam.add(currentRow);
			
			}
		}
		
		tableSelected.getListRows().removeAll(listRowToRemoveParam);
		tableSelected.setTableCountRow(tableSelected.getListRows().size());
		
		IResultSet resultSet = new ResultSet(tableSelected, resultSetMetaData);
		
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
		listParameter.clear();
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setInt(java.lang.String, int)
	 */
	public void setInt( String parameterName, int value ) throws OdaException
	{
		for(OdaGoogleTableParameter currentPara : listParameter){
        	
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
		listParameter.get(parameterId-1).setParamValue1(value);	
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setDouble(java.lang.String, double)
	 */
	public void setDouble( String parameterName, double value ) throws OdaException
	{
		for(OdaGoogleTableParameter currentPara : listParameter){
        	
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
		listParameter.get(parameterId-1).setParamValue1(value);	
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setBigDecimal(java.lang.String, java.math.BigDecimal)
	 */
	public void setBigDecimal( String parameterName, BigDecimal value ) throws OdaException
	{
		for(OdaGoogleTableParameter currentPara : listParameter){
        	
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
		listParameter.get(parameterId-1).setParamValue1(value);	
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setString(java.lang.String, java.lang.String)
	 */
	public void setString( String parameterName, String value ) throws OdaException
	{
		for(OdaGoogleTableParameter currentPara : listParameter){
        	
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
		listParameter.get(parameterId-1).setParamValue1(value);	
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setDate(java.lang.String, java.sql.Date)
	 */
	public void setDate( String parameterName, Date value ) throws OdaException
	{
		for(OdaGoogleTableParameter currentPara : listParameter){
        	
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
		listParameter.get(parameterId-1).setParamValue1(value);	
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setTime(java.lang.String, java.sql.Time)
	 */
	public void setTime( String parameterName, Time value ) throws OdaException
	{
		for(OdaGoogleTableParameter currentPara : listParameter){
        	
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
		listParameter.get(parameterId-1).setParamValue1(value);	
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setTimestamp(java.lang.String, java.sql.Timestamp)
	 */
	public void setTimestamp( String parameterName, Timestamp value ) throws OdaException
	{
		for(OdaGoogleTableParameter currentPara : listParameter){
        	
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
		listParameter.get(parameterId-1).setParamValue1(value);	
	}

    /* (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.oda.IQuery#setBoolean(java.lang.String, boolean)
     */
    public void setBoolean( String parameterName, boolean value )
            throws OdaException
    {
		for(OdaGoogleTableParameter currentPara : listParameter){
        	
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
		listParameter.get(parameterId-1).setParamValue1(value);	
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.oda.IQuery#setNull(java.lang.String)
     */
    public void setNull( String parameterName ) throws OdaException
    {
		for(OdaGoogleTableParameter currentPara : listParameter){
        	
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
		listParameter.get(parameterId-1).setParamValue1(null);	
    }

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#findInParameter(java.lang.String)
	 */
	public int findInParameter( String parameterName ) throws OdaException
	{
		int i = 0;
		int result = 0;
		
		for(OdaGoogleTableParameter currentPara : listParameter){
         	
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
		
		return new ParameterMetaData(listParameter);
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



	public void cancel() throws OdaException, UnsupportedOperationException {
		
		
	}



	public String getEffectiveQueryText() {
		
		return null;
	}



//	public QuerySpecification getSpecification() {
//		
//		return null;
//	}



	public void setObject(String parameterName, Object value)
			throws OdaException {
		
		
	}



	public void setObject(int parameterId, Object value) throws OdaException {
		
		
	}



	public QuerySpecification getSpecification() {
		
		return null;
	}



	public void setSpecification(QuerySpecification querySpec)
			throws OdaException, UnsupportedOperationException {
		
		
	}



//	public void setSpecification(QuerySpecification querySpec)
//			throws OdaException, UnsupportedOperationException {
//		
//		
//	}
	

    
}
