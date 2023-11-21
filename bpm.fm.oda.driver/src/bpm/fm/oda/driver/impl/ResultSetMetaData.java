/*
 *************************************************************************
 * Copyright (c) 2009 <<Your Company Name here>>
 *  
 *************************************************************************
 */

package bpm.fm.oda.driver.impl;

import java.util.HashMap;
import java.util.List;

import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;

import bpm.fm.api.model.Axis;
import bpm.fm.api.model.Level;
import bpm.fm.api.model.Metric;

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
//	public static final int MIN_VALUE = 1;
//	public static final int MIN_SEUIL = 2;
//	public static final int TARGET = 3;
//	public static final int MAX_SEUIL = 4;
//	public static final int MAX = 5;
//	public static final int TOLERANCE = 6;
//	public static final int VALUE = 7;
//	public static final int DATE = 8;
//	public static final int AXE = 9;
//	
	public static String[] columnNames = new String[]{
		"Metric id" , "Metric name", "Value", "Objective", "Minimum", "Maximum"};//, "Value", "Date", "Axe"
	//};
//	private static String[] columnLabels = new String[]{
//		"MinValue" , "MinSeuil", "TargetValue", "MaxSeuil", "MaxValue", "Delta", "Value", "Date", "Axe"
//	};
	
	public static final int METRIC_ID = 1;
	public static final int METRIC_NAME = 2;
	public static final int VALUE = 3;
	public static final int VALUE_OBJECTIVE = 4;
	public static final int VALUE_MIN = 5;
	public static final int VALUE_MAX = 6;
	
	public HashMap<Integer, Level> levelIndexes = new HashMap<Integer, Level>();
	
	private int stardDateIndex;
	private int endDateIndex;
	private boolean dateAsParameter;
	
	public ResultSetMetaData(List<Metric> metrics, List<Axis> axes, java.util.Date startDate, java.util.Date endDate, boolean dateAsParameter) {
		
		int index = 7;
		for(Axis axis : axes) {
			for(Level level : axis.getChildren()) {
				levelIndexes.put(index, level);
				index++;
			}
		}
		
		this.dateAsParameter = dateAsParameter;
		
//		if(!dateAsParameter) {
			stardDateIndex = index + 1;
//			if(endDate != null) {
//				endDateIndex = index + 2;
//			}
//		}
		
		
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSetMetaData#getColumnCount()
	 */
	public int getColumnCount() throws OdaException
	{
		int columnNb = 6;
		columnNb += levelIndexes.size();
//		if(!dateAsParameter) {
			columnNb+=1;
//			if(endDateIndex > 0) {
//				columnNb+=1;
//			}
//		}
		
        return columnNb;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSetMetaData#getColumnName(int)
	 */
	public String getColumnName( int index ) throws OdaException
	{
		if(index <= 6) {
			return columnNames[index - 1];
		}
		else if(index > 6 && index <= 6 + levelIndexes.size()) {
			return levelIndexes.get(index).getName();
		}
		else {
			if(index == 6 + levelIndexes.size() + 1) {
				return "Date";
			}
		}
		throw new OdaException("Out of bounds");
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSetMetaData#getColumnLabel(int)
	 */
	public String getColumnLabel( int index ) throws OdaException
	{
		  return getColumnName(index);
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSetMetaData#getColumnType(int)
	 */
	public int getColumnType( int index ) throws OdaException
	{
		switch(index){
		case METRIC_ID:
			return java.sql.Types.INTEGER;
		case METRIC_NAME:
			return java.sql.Types.VARCHAR;
		case VALUE:
			return java.sql.Types.DOUBLE;
		case VALUE_OBJECTIVE:
			return java.sql.Types.DOUBLE;
		case VALUE_MIN:
			return java.sql.Types.DOUBLE;
		case VALUE_MAX:
			return java.sql.Types.DOUBLE;
		default :
			if(index > 6 && index <= 6 + levelIndexes.size()) {
				return java.sql.Types.VARCHAR;
			}
			else {
				return java.sql.Types.DATE;
			}
		}
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSetMetaData#getColumnTypeName(int)
	 */
	public String getColumnTypeName( int index ) throws OdaException
	{
        int nativeTypeCode = getColumnType( index );
        return Driver.getNativeDataTypeName( nativeTypeCode );
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSetMetaData#getColumnDisplayLength(int)
	 */
	public int getColumnDisplayLength( int index ) throws OdaException
	{
		return -1;
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
