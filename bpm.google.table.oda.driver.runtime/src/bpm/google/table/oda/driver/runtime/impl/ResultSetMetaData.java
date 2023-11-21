/*
 *************************************************************************
 * Copyright (c) 2008 <<Your Company Name here>>
 *  
 *************************************************************************
 */

package bpm.google.table.oda.driver.runtime.impl;

import java.util.ArrayList;

import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;

import bpm.google.table.oda.driver.runtime.model.OdaGoogleTableColumn;

public class ResultSetMetaData implements IResultSetMetaData
{
	
	private ArrayList<OdaGoogleTableColumn> listCols;
	private int countRow;
    
	public ResultSetMetaData(ArrayList<OdaGoogleTableColumn> listColumns, int pCount) {
		
		listCols = listColumns;
		countRow = pCount;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSetMetaData#getColumnCount()
	 */
	public int getColumnCount() throws OdaException
	{
        return listCols.size();
        
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSetMetaData#getColumnName(int)
	 */
	public String getColumnName( int index ) throws OdaException
	{
        return listCols.get(index-1).getColLabelName();
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSetMetaData#getColumnLabel(int)
	 */
	public String getColumnLabel( int index ) throws OdaException
	{
		  return listCols.get(index-1).getColLabelName();
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSetMetaData#getColumnType(int)
	 */
	public int getColumnType( int index ) throws OdaException
	{
		Class typeCol = listCols.get(index-1).getColClass();
		
		int odaScalarTypeReturned = 1;
		
		if(typeCol == Integer.class){
			odaScalarTypeReturned =  4;
		}
		
		else if(typeCol == Double.class){
			odaScalarTypeReturned =  8;
		}
		else{
			odaScalarTypeReturned =  1;
		}
		
		return odaScalarTypeReturned;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSetMetaData#getColumnTypeName(int)
	 */
	public String getColumnTypeName( int index ) throws OdaException
	{
        return listCols.get(index-1).getColClass().getSimpleName();
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSetMetaData#getColumnDisplayLength(int)
	 */
	public int getColumnDisplayLength( int index ) throws OdaException
	{
        
		return countRow;
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
