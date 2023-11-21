/*
 *************************************************************************
 * Copyright (c) 2008 <<Your Company Name here>>
 *  
 *************************************************************************
 */

package bpm.inlinedatas.oda.driver.runtime.impl;

import java.util.ArrayList;
import java.util.Date;

import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;

import bpm.inlinedatas.oda.driver.runtime.structureProperties.PropertieColumn;


public class ResultSetMetaData implements IResultSetMetaData
{
	private ArrayList<PropertieColumn> listResultSetMetaData;
    
	public ResultSetMetaData(ArrayList<PropertieColumn> listDataSet) {
		
		listResultSetMetaData = new ArrayList<PropertieColumn>();
		listResultSetMetaData.addAll(listDataSet);
		
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSetMetaData#getColumnCount()
	 */
	public int getColumnCount() throws OdaException
	{
        return listResultSetMetaData.size();
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSetMetaData#getColumnName(int)
	 */
	public String getColumnName( int index ) throws OdaException
	{
		return(listResultSetMetaData.get(index -1).getPropertieColName());
	
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSetMetaData#getColumnLabel(int)
	 */
	public String getColumnLabel( int index ) throws OdaException
	{
		return listResultSetMetaData.get(index-1).getPropertieColName();
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSetMetaData#getColumnType(int)
	 */
	public int getColumnType( int index ) throws OdaException
	{
		int intTypeReturned = 0;
		index--;
		
		if(listResultSetMetaData.get(index).getPropertieColType() == Integer.class){
			intTypeReturned = 4;	
		}
		
		if (listResultSetMetaData.get(index).getPropertieColType() == Long.class){
			intTypeReturned = 4;
		}

		if (listResultSetMetaData.get(index).getPropertieColType() == Double.class){
			intTypeReturned = 8;	 
		}
		
		if (listResultSetMetaData.get(index).getPropertieColType() == Float.class){
			intTypeReturned = 8;	
		}
		
		if (listResultSetMetaData.get(index).getPropertieColType() == String.class){
			intTypeReturned = 1;	 
		}
		
		if (listResultSetMetaData.get(index).getPropertieColType() == Boolean.class){
			intTypeReturned = 16;	
		}
		
		if (listResultSetMetaData.get(index).getPropertieColType() == Character.class){
			intTypeReturned = 1; 
		}
		
		if (listResultSetMetaData.get(index).getPropertieColType() == Date.class){
			intTypeReturned = 91; 
		}

		
		
		return intTypeReturned;
		
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSetMetaData#getColumnTypeName(int)
	 */
	public String getColumnTypeName( int index ) throws OdaException
	{
        return listResultSetMetaData.get(index-1).getPropertieColType().getSimpleName();
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSetMetaData#getColumnDisplayLength(int)
	 */
	public int getColumnDisplayLength( int index ) throws OdaException
	{
        return listResultSetMetaData.get(index-1).getListPropertieData().size();
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSetMetaData#getPrecision(int)
	 */
	public int getPrecision( int index ) throws OdaException
	{
        
		return -1;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSetMetaData#getScale(int)
	 */
	public int getScale( int index ) throws OdaException
	{
        
		return -1;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSetMetaData#isNullable(int)
	 */
	public int isNullable( int index ) throws OdaException
	{
        
		return IResultSetMetaData.columnNullableUnknown;
	}
    
}
