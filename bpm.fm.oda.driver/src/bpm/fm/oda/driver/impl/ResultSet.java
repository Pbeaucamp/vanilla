/*
 *************************************************************************
 * Copyright (c) 2009 <<Your Company Name here>>
 *  
 *************************************************************************
 */

package bpm.fm.oda.driver.impl;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.datatools.connectivity.oda.IBlob;
import org.eclipse.datatools.connectivity.oda.IClob;
import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;

import bpm.fm.api.model.Axis;
import bpm.fm.api.model.Level;
import bpm.fm.api.model.Metric;
import bpm.fm.api.model.utils.LevelMember;
import bpm.fm.api.model.utils.LoaderDataContainer;
import bpm.fm.api.model.utils.LoaderMetricValue;

import com.ibm.icu.text.SimpleDateFormat;

/**
 * Implementation class of IResultSet for an ODA runtime driver.
 * <br>
 * For demo purpose, the auto-generated method stubs have
 * hard-coded implementation that returns a pre-defined set
 * of meta-data and query results.
 * A custom ODA driver is expected to implement own data source specific
 * behavior in its place. 
 */
public class ResultSet implements IResultSet
{
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private int m_maxRows;
    private int m_currentRowId = -1;
	private List<Metric> metrics;
	private List<Axis> axes;
	private java.util.Date startDate;
	private java.util.Date endDate;
	private List<LoaderDataContainer> data;
	
	private List<LoaderMetricValue> values;
	
//    private List<MetricValue> metricValues;
//    private Iterator<MetricValue> iterator;
//    private MetricValue current;
    
       
//    public ResultSet(List<MetricValue> metricValues, String datePattern){
//    	this.metricValues = metricValues;
//    	iterator = this.metricValues.iterator();
//    	sdf.applyPattern(datePattern);
//    }
    
	public ResultSet(List<LoaderDataContainer> data, List<Metric> metrics, List<Axis> axes, java.util.Date startDate, java.util.Date endDate) {
		this.metrics = metrics;
		this.axes = axes;
		this.startDate = startDate;
		this.endDate = endDate;
		this.data = data;
		
		values = new ArrayList<LoaderMetricValue>();
		for(LoaderDataContainer c : data) {
			values.addAll(c.getValues());
		}
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getMetaData()
	 */
	public IResultSetMetaData getMetaData() throws OdaException
	{
		return new ResultSetMetaData(metrics, axes, startDate, endDate, startDate==null);
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#setMaxRows(int)
	 */
	public void setMaxRows( int max ) throws OdaException
	{
		m_maxRows = max;
	}
	
	/**
	 * Returns the maximum number of rows that can be fetched from this result set.
	 * @return the maximum number of rows to fetch.
	 */
	protected int getMaxRows()
	{
		return m_maxRows;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#next()
	 */
	public boolean next() throws OdaException{
        int maxRows = getMaxRows();
        if( maxRows <= 0 || (maxRows > 0 && m_currentRowId < maxRows))  {
        	m_currentRowId++;
        	if( m_currentRowId < values.size()) {
        		
        		return true;
        	}
        	else {
        		return false;
        	}
        	
//        	if (iterator.hasNext()){
//        		current = iterator.next();
//        		 m_currentRowId++;
//                 return true;
//        	}
//        	else{
//        		return false;
//        	}
        	
        }
        return false;        
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#close()
	 */
	public void close() throws OdaException{
        m_currentRowId = 0;
        data = null;
        values = null;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getRow()
	 */
	public int getRow() throws OdaException{
		return m_currentRowId;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getString(int)
	 */
	public String getString( int index ) throws OdaException{
		Object o = getObject(index);
		if (o == null){
			return null;
		}
		if (o instanceof Date){
			return sdf.format((Date)o);
			
		}
		else if(o instanceof Timestamp) {
			return sdf.format(new Date(((Timestamp)o).getTime()));
		}
		return o.toString();
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getString(java.lang.String)
	 */
	public String getString( String columnName ) throws OdaException
	{
	    return getString( findColumn( columnName ) );
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getInt(int)
	 */
	public int getInt( int index ) throws OdaException
	{
		Object o = getObject(index);
		if (o == null){
			throw new OdaException("Data is null cannot be wrapped into a primitive type");
			
		}
				
		return Integer.valueOf(o.toString());
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getInt(java.lang.String)
	 */
	public int getInt( String columnName ) throws OdaException
	{
	    return getInt( findColumn( columnName ) );
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getDouble(int)
	 */
	public double getDouble( int index ) throws OdaException
	{
		Object o = getObject(index);
		if (o == null){
			throw new OdaException("Data is null cannot be wrapped into a primitive type");
		}
		if (o instanceof Date){
			return ((Date)o).getTime();
		}
		
		return Double.valueOf(o.toString());
		
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getDouble(java.lang.String)
	 */
	public double getDouble( String columnName ) throws OdaException
	{
	    return getDouble( findColumn( columnName ) );
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getBigDecimal(int)
	 */
	public BigDecimal getBigDecimal( int index ) throws OdaException
	{
        throw new UnsupportedOperationException();
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getBigDecimal(java.lang.String)
	 */
	public BigDecimal getBigDecimal( String columnName ) throws OdaException
	{
	    return getBigDecimal( findColumn( columnName ) );
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getDate(int)
	 */
	public Date getDate( int index ) throws OdaException
	{
		return (Date) getObject(index);
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getDate(java.lang.String)
	 */
	public Date getDate( String columnName ) throws OdaException
	{
	    return getDate( findColumn( columnName ) );
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getTime(int)
	 */
	public Time getTime( int index ) throws OdaException
	{
        throw new UnsupportedOperationException();
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getTime(java.lang.String)
	 */
	public Time getTime( String columnName ) throws OdaException
	{
	    return getTime( findColumn( columnName ) );
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getTimestamp(int)
	 */
	public Timestamp getTimestamp( int index ) throws OdaException
	{
        throw new UnsupportedOperationException();
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getTimestamp(java.lang.String)
	 */
	public Timestamp getTimestamp( String columnName ) throws OdaException
	{
	    return getTimestamp( findColumn( columnName ) );
	}

    /* 
     * @see org.eclipse.datatools.connectivity.oda.IResultSet#getBlob(int)
     */
    public IBlob getBlob( int index ) throws OdaException
    {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see org.eclipse.datatools.connectivity.oda.IResultSet#getBlob(java.lang.String)
     */
    public IBlob getBlob( String columnName ) throws OdaException
    {
        return getBlob( findColumn( columnName ) );
    }

    /* 
     * @see org.eclipse.datatools.connectivity.oda.IResultSet#getClob(int)
     */
    public IClob getClob( int index ) throws OdaException
    {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see org.eclipse.datatools.connectivity.oda.IResultSet#getClob(java.lang.String)
     */
    public IClob getClob( String columnName ) throws OdaException
    {
        return getClob( findColumn( columnName ) );
    }

    /* (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.oda.IResultSet#getBoolean(int)
     */
    public boolean getBoolean( int index ) throws OdaException
    {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.oda.IResultSet#getBoolean(java.lang.String)
     */
    public boolean getBoolean( String columnName ) throws OdaException
    {
        return getBoolean( findColumn( columnName ) );
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.oda.IResultSet#getObject(int)
     */
    public Object getObject( int index ) throws OdaException{
    	switch(index){
		case ResultSetMetaData.METRIC_ID:
			return values.get(m_currentRowId).getMetric().getId();
		case ResultSetMetaData.METRIC_NAME:
			return values.get(m_currentRowId).getMetric().getName();
		case ResultSetMetaData.VALUE:
			return values.get(m_currentRowId).getValue();
		case ResultSetMetaData.VALUE_MAX:
			return values.get(m_currentRowId).getMaximum();
		case ResultSetMetaData.VALUE_MIN:
			return values.get(m_currentRowId).getMinimum();
		case ResultSetMetaData.VALUE_OBJECTIVE:
			return values.get(m_currentRowId).getObjective();
		default:
			int i = 7;
			for(LevelMember member : values.get(m_currentRowId).getMembers()) {
				if(i == index) {
					return member.getLabel();
				}
				i++;
			}
			
			return values.get(m_currentRowId).getDate();
			
		}

//       throw new OdaException("Index out of bounds, there is only 8 columns in this ResultSet");

    }

    /* (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.oda.IResultSet#getObject(java.lang.String)
     */
    public Object getObject( String columnName ) throws OdaException
    {
        return getObject( findColumn( columnName ) );
    }

    /*
     * @see org.eclipse.datatools.connectivity.oda.IResultSet#wasNull()
     */
    public boolean wasNull() throws OdaException
    {
        
        // hard-coded for demo purpose
        return false;
    }

    /*
     * @see org.eclipse.datatools.connectivity.oda.IResultSet#findColumn(java.lang.String)
     */
    public int findColumn( String columnName ) throws OdaException
    {
    	for(int i = 0 ; i < ResultSetMetaData.columnNames.length ; i++) {
    		if(columnName.equals(ResultSetMetaData.columnNames[i])) {
    			return i+1;
    		}
    	}
    	
    	int index = 7;
		for(Axis axis : axes) {
			for(Level level : axis.getChildren()) {
				if(level.getName().equals(columnName)) {
					return index;
				}
				index++;
			}
		}
    	
		if(columnName.equals("Start Date")) {
			return index + 1;
		}
		else {
			return index + 2;
		}
    	
//        // hard-coded for demo purpose
//        int columnId = 1;   // dummy column id
//        if( columnName == null || columnName.length() == 0 )
//            return columnId;
//        String lastChar = columnName.substring( columnName.length()-1, 1 );
//        try
//        {
//            columnId = Integer.parseInt( lastChar );
//        }
//        catch( NumberFormatException e )
//        {
//            // ignore, use dummy column id
//        }
//        return columnId;
    }
    
}
