/*
 *************************************************************************
 * Copyright (c) 2007 <<Your Company Name here>>
 *  
 *************************************************************************
 */

package bpm.metadata.birt.oda.runtime.impl;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

import org.eclipse.datatools.connectivity.oda.IBlob;
import org.eclipse.datatools.connectivity.oda.IClob;
import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;

import bpm.metadata.query.QuerySql;

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
	private int m_maxRows;
    private int m_currentRowId = -1;
	
    private QuerySql fmdtQuery;
    private List<List<String>> values;
    
    private Object lastRead = null;
    
    
    public ResultSet(QuerySql fmdtQuery, List<List<String>> values){
    	this.fmdtQuery = fmdtQuery;
    	this.values = values;
    	
    	try {
			setMaxRows(values.size() - 1);
		} catch (OdaException e) {
			
			e.printStackTrace();
		}
    	
    }
    
    
	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getMetaData()
	 */
	public IResultSetMetaData getMetaData() throws OdaException
	{
        /* 
         * Replace with implementation to return an instance 
         * based on this result set.
         */
		return new ResultSetMetaData(fmdtQuery, null);
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
	public boolean next() throws OdaException
	{
		
        
        // simple implementation done below for demo purpose only
        int maxRows = getMaxRows();
//        if( maxRows <= 0 )  // no limit is specified
//            maxRows = 5;    // hard-coded for demo purpose
        
        if( m_currentRowId < maxRows )
        {
            m_currentRowId++;
            return true;
        }
        
        return false;        
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#close()
	 */
	public void close() throws OdaException
	{
       fmdtQuery = null;
        m_currentRowId = 0;     // reset row counter
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getRow()
	 */
	public int getRow() throws OdaException
	{
		return m_currentRowId;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getString(int)
	 */
	public String getString( int index ) throws OdaException
	{

		if (values.get(m_currentRowId).size() >= index-1){
			
			lastRead = values.get(m_currentRowId).get(index - 1);
			
			return values.get(m_currentRowId).get(index - 1);

		}
		throw new OdaException("No result in the query");
		
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
		Object o = values.get(m_currentRowId).get(index - 1);
		try{
			lastRead = Integer.parseInt(values.get(m_currentRowId).get(index - 1));
			return Integer.parseInt(values.get(m_currentRowId).get(index - 1));
		}catch(Exception e){
			e.printStackTrace();
			lastRead = null;
			return 0;
		}
		
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
		Object o = values.get(m_currentRowId).get(index - 1);
		try{
			lastRead = Double.parseDouble(values.get(m_currentRowId).get(index - 1));
			return Double.parseDouble(values.get(m_currentRowId).get(index - 1));
		}catch(Exception e){
			e.printStackTrace();
			lastRead = null;
			return 0.f;
		}
		
		
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
		try{
			
			BigDecimal v = new BigDecimal(values.get(m_currentRowId).get(index - 1).replace(",", "."));

			
			lastRead = v; 
		    return v;
		}catch(Exception e){
			lastRead = null;
			return null;
		}
       
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
		try{
			lastRead = Date.valueOf(values.get(m_currentRowId).get(index - 1));
			return Date.valueOf(values.get(m_currentRowId).get(index - 1));
		}catch(Exception e){
			lastRead = null;
			return null;
		}
		
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
		try{
			lastRead = Time.valueOf(values.get(m_currentRowId).get(index - 1));
			return Time.valueOf(values.get(m_currentRowId).get(index - 1));
		}catch(Exception e){
			lastRead = null;
			return null;
		}
		
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
		try{
			lastRead = Timestamp.valueOf(values.get(m_currentRowId).get(index - 1));
	        return Timestamp.valueOf(values.get(m_currentRowId).get(index - 1));
		}catch(Exception e){
			lastRead = null;
			return null;
		}
		
        
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
    	try{
    		lastRead = Boolean.valueOf(values.get(m_currentRowId).get(index - 1));
            return Boolean.valueOf(values.get(m_currentRowId).get(index - 1));
    	}catch(Exception e){
    		lastRead = null;
    		return false;
    	}
    	
    }

    /* (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.oda.IResultSet#getBoolean(java.lang.String)
     */
    public boolean getBoolean( String columnName ) throws OdaException
    {
        return getBoolean( findColumn( columnName ) );
    }

    /*
     * @see org.eclipse.datatools.connectivity.oda.IResultSet#wasNull()
     */
    public boolean wasNull() throws OdaException
    {
        
        return lastRead == null;
    }

    /*
     * @see org.eclipse.datatools.connectivity.oda.IResultSet#findColumn(java.lang.String)
     */
    public int findColumn( String columnName ) throws OdaException
    {
        
        
        // hard-coded for demo purpose
        int columnId = 1;   // dummy column id
        if( columnName == null || columnName.length() == 0 )
            return columnId;
        String lastChar = columnName.substring( columnName.length()-1, 1 );
        try
        {
            columnId = Integer.parseInt( lastChar );
        }
        catch( NumberFormatException e )
        {
            // ignore, use dummy column id
        }
        return columnId;
    }


	public Object getObject(int arg0) throws OdaException {
		
		return null;
	}


	public Object getObject(String arg0) throws OdaException {
		
		return null;
	}
    
}
