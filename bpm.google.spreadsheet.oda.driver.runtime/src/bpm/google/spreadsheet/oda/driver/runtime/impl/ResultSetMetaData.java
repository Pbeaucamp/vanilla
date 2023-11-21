/*
 *************************************************************************
 * Copyright (c) 2008 <<Your Company Name here>>
 *  
 *************************************************************************
 */

package bpm.google.spreadsheet.oda.driver.runtime.impl;

import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;

import bpm.google.spreadsheet.oda.driver.runtime.model.OdaGoogleSheet;

/**
 * Implementation class of IResultSetMetaData for an ODA runtime driver.
 * <br>
 * For demo purpose, the auto-generated method stubs have
 * hard-coded implementation that returns a pre-defined set
 * of meta-data and query results.
 * A custom ODA driver is expected to implement own data source specific
 * behavior in its place. 
 */
public class ResultSetMetaData implements IResultSetMetaData
{
	
	private OdaGoogleSheet metaDataSheet;
    
	public ResultSetMetaData(OdaGoogleSheet sheetSelected) {
		
		metaDataSheet = sheetSelected;		

	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSetMetaData#getColumnCount()
	 */
	public int getColumnCount() throws OdaException
	{

        return metaDataSheet.getSheetCountCol();
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSetMetaData#getColumnName(int)
	 */
	public String getColumnName( int index ) throws OdaException
	{
        return metaDataSheet.getSheetListCol().get(index-1).getColLabelName();
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSetMetaData#getColumnLabel(int)
	 */
	public String getColumnLabel( int index ) throws OdaException
	{
		return metaDataSheet.getSheetListCol().get(index-1).getColLabelName();
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSetMetaData#getColumnType(int)
	 */
	public int getColumnType( int index ) throws OdaException
	{

		Class typeCol = metaDataSheet.getSheetListCol().get(index-1).getColClass();
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

        return metaDataSheet.getSheetListCol().get(index-1).getColClass().getSimpleName();
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSetMetaData#getColumnDisplayLength(int)
	 */
	public int getColumnDisplayLength( int index ) throws OdaException
	{
        
		return metaDataSheet.getSheetCountRow();
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
