

package bpm.inlinedatas.oda.driver.runtime.impl;

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
//import org.eclipse.datatools.connectivity.oda.spec.QuerySpecification;

import bpm.inlinedatas.oda.driver.runtime.structureProperties.FilterDatas;
import bpm.inlinedatas.oda.driver.runtime.structureProperties.PropertieColumn;
import bpm.inlinedatas.oda.driver.runtime.structureProperties.PropertieData;


public class Query implements IQuery
{
	private int m_maxRows;
	
	private ResultSetMetaData resultSetMetaData;
	
	private ArrayList<PropertieColumn> listDataSource, listDataSet;
	private ArrayList<FilterDatas> listDataSetFilters;
	
	public final static String QUERY_SEPARATOR_ELEMENT = "}";
	public final static String QUERY_SEPARATOR_COL_SELECTED = ";";
	public final static String QUERY_SEPARATOR_SUB = "!";
	
	public final static String NO_FILTER = "++No filter++";
	public final static String FILTER_NO_VALUES = "++Any Value++";
	public final static String NO_LOGICAL = "++No Logical++";
	
	
	
	public Query(ArrayList<PropertieColumn> pListDS){
		
		listDataSource = new ArrayList<PropertieColumn>();
		listDataSource.addAll(pListDS);
		
		listDataSet = new ArrayList<PropertieColumn>();
		
		listDataSetFilters = new ArrayList<FilterDatas>();
		
	}
	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#prepare(java.lang.String)
	 */
	public void prepare( String queryText ) throws OdaException {
	
		//System.out.println(queryText);
		
		
	//++++++ Split  columns selected and filters for two new string : Columns and filters
		String[] tabQueryColAndFilter = queryText.split(QUERY_SEPARATOR_ELEMENT);
		
		String strQueryCol = tabQueryColAndFilter[0];
		String strQueryFilter = tabQueryColAndFilter[1];
		
		
	//++++++ Split columns list	
		String[] tabCols = strQueryCol.split(QUERY_SEPARATOR_COL_SELECTED);
		
		//Build datas list to build the final dataset
		for(int i = 0; i < tabCols.length; i++){
			
			for(PropertieColumn currentCol: listDataSource){
			
				if(tabCols[i].equals(currentCol.getPropertieColName())){
					listDataSet.add(currentCol);
				}
			
			}
			
		}

	//++++++ Split Filters List, if there is greather than or equal 1 filter
		
		if(strQueryFilter.equals(NO_FILTER)== false){

			String[] tabFilters = strQueryFilter.split(QUERY_SEPARATOR_COL_SELECTED);
		
			for(int i = 0; i< tabFilters.length; i++){

				//Split the Filter to get the coolumns name, the operator and values
				String[] subTabFilter = tabFilters[i].split(QUERY_SEPARATOR_SUB);
				
					//Testes IF there is 0, 1 or 2 values
					if(subTabFilter.length == 3){
						listDataSetFilters.add(new FilterDatas(subTabFilter[0],subTabFilter[1],FILTER_NO_VALUES, FILTER_NO_VALUES,subTabFilter[2]));
					}
					else if(subTabFilter.length == 4){
						listDataSetFilters.add(new FilterDatas(subTabFilter[0],subTabFilter[1],subTabFilter[2], FILTER_NO_VALUES,subTabFilter[3]));
					}
					else{
						listDataSetFilters.add(new FilterDatas(subTabFilter[0],subTabFilter[1],subTabFilter[2], subTabFilter[3],subTabFilter[4]));
					}
				}

			
			
			
			
		//Apply filter(s)
			FilterDatas.updateDataSetWithFilters(listDataSet, listDataSetFilters);
						
			
			
		//Remove hidden datas 
			
			ArrayList<PropertieData> listDataToRemove = new ArrayList<PropertieData>();
			
			//For each columns
			for(PropertieColumn currentCol : listDataSet){
				
				listDataToRemove.clear() ;
				
				//Look for hidden Datas and add it in a list
				for(PropertieData currentData: currentCol.getListPropertieData()){
					
					if(currentData.isHidenAfterFilter()){
						listDataToRemove.add(currentData);

					}
				}
				
				//Remove all hidden data in the current column
				currentCol.getListPropertieData().removeAll(listDataToRemove);
			}
			
		
			 
		}
		
	/*	
	//TEST
		for(PropertieColumn col: listDataSet){
			System.out.println(">Col: " + col.getPropertieColName());
			for(PropertieData dat: col.getListPropertieData()){
				System.out.println("      Data: " + dat.getPropertieDataValue().toString() + " / Hidden : " + dat.isHidenAfterFilter());
			}
		}
		
		*/
				
		resultSetMetaData = new ResultSetMetaData(listDataSet);
		
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
        /* 
         * Replace with implementation to return an instance 
         * based on this prepared query.
         */
		return resultSetMetaData;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#executeQuery()
	 */
	public IResultSet executeQuery() throws OdaException
	{
        /* 
         * Replace with implementation to return an instance 
         * based on this prepared query.
         */
		IResultSet resultSet = new ResultSet(listDataSet);
		resultSet.setMaxRows( getMaxRows() );
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
        
		// only applies to input parameter
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setInt(java.lang.String, int)
	 */
	public void setInt( String parameterName, int value ) throws OdaException
	{
        
		// only applies to named input parameter
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setInt(int, int)
	 */
	public void setInt( int parameterId, int value ) throws OdaException
	{
        
		// only applies to input parameter
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setDouble(java.lang.String, double)
	 */
	public void setDouble( String parameterName, double value ) throws OdaException
	{
        
		// only applies to named input parameter
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setDouble(int, double)
	 */
	public void setDouble( int parameterId, double value ) throws OdaException
	{
        
		// only applies to input parameter
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setBigDecimal(java.lang.String, java.math.BigDecimal)
	 */
	public void setBigDecimal( String parameterName, BigDecimal value ) throws OdaException
	{
        
		// only applies to named input parameter
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setBigDecimal(int, java.math.BigDecimal)
	 */
	public void setBigDecimal( int parameterId, BigDecimal value ) throws OdaException
	{
        
		// only applies to input parameter
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setString(java.lang.String, java.lang.String)
	 */
	public void setString( String parameterName, String value ) throws OdaException
	{
        
		// only applies to named input parameter
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setString(int, java.lang.String)
	 */
	public void setString( int parameterId, String value ) throws OdaException
	{
        
		// only applies to input parameter
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setDate(java.lang.String, java.sql.Date)
	 */
	public void setDate( String parameterName, Date value ) throws OdaException
	{
        
		// only applies to named input parameter
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setDate(int, java.sql.Date)
	 */
	public void setDate( int parameterId, Date value ) throws OdaException
	{
        
		// only applies to input parameter
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setTime(java.lang.String, java.sql.Time)
	 */
	public void setTime( String parameterName, Time value ) throws OdaException
	{
        
		// only applies to named input parameter
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setTime(int, java.sql.Time)
	 */
	public void setTime( int parameterId, Time value ) throws OdaException
	{
        
		// only applies to input parameter
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setTimestamp(java.lang.String, java.sql.Timestamp)
	 */
	public void setTimestamp( String parameterName, Timestamp value ) throws OdaException
	{
        
		// only applies to named input parameter
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setTimestamp(int, java.sql.Timestamp)
	 */
	public void setTimestamp( int parameterId, Timestamp value ) throws OdaException
	{
        
		// only applies to input parameter
	}

    /* (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.oda.IQuery#setBoolean(java.lang.String, boolean)
     */
    public void setBoolean( String parameterName, boolean value )
            throws OdaException
    {
        
        // only applies to named input parameter
    }

    /* (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.oda.IQuery#setBoolean(int, boolean)
     */
    public void setBoolean( int parameterId, boolean value )
            throws OdaException
    {
               
        // only applies to input parameter
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.oda.IQuery#setNull(java.lang.String)
     */
    public void setNull( String parameterName ) throws OdaException
    {
        
        // only applies to named input parameter
    }

    /* (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.oda.IQuery#setNull(int)
     */
    public void setNull( int parameterId ) throws OdaException
    {
        
        // only applies to input parameter
    }

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#findInParameter(java.lang.String)
	 */
	public int findInParameter( String parameterName ) throws OdaException
	{
        
		// only applies to named input parameter
		return 0;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#getParameterMetaData()
	 */
	public IParameterMetaData getParameterMetaData() throws OdaException
	{
        /* 
         * Replace with implementation to return an instance 
         * based on this prepared query.
         */
		return new ParameterMetaData();
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
