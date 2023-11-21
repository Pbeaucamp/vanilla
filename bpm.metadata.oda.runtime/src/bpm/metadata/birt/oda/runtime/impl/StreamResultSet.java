/*
 *************************************************************************
 * Copyright (c) 2007 <<Your Company Name here>>
 *  
 *************************************************************************
 */

package bpm.metadata.birt.oda.runtime.impl;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;

import org.apache.log4j.Logger;
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
public class StreamResultSet implements IResultSet
{
	private int m_maxRows;
    private int m_currentRowId = -1;
	
    private boolean isNull = false;
      
    private Object lastRead = null;
    private QuerySql fmdtQuery;
    private java.sql.ResultSet jdbcResultSet;
    
    public StreamResultSet(java.sql.ResultSet resultSet, QuerySql fmdtQuery){
    	this.jdbcResultSet = resultSet;
    	this.fmdtQuery = fmdtQuery;
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
		try{
			return new ResultSetMetaData(fmdtQuery, this.jdbcResultSet.getMetaData());
		}catch(Exception ex){
			Logger.getLogger(getClass()).warn("Could not get JDBC ResultSet MetaData - " + ex.getMessage());
			return new ResultSetMetaData(fmdtQuery, null);
		}
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
        
        if(maxRows == 0 || m_currentRowId < maxRows )
        {
           
            try {
				boolean b = jdbcResultSet.next();
				if (b){
					m_currentRowId++;
				}
				 
				 return b;
			} catch (SQLException e) {
				e.printStackTrace();
				throw new OdaException(e);
			}
           
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

		try{
			try {
				isNull = (jdbcResultSet.getObject(index) == null);
			} catch(Error e) {
			}
			return jdbcResultSet.getString(index);
		}catch(SQLException e){
			throw new OdaException(e);
		}
		
		
		
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getString(java.lang.String)
	 */
	public String getString( String columnName ) throws OdaException
	{

		try{
			try {
				isNull = (jdbcResultSet.getObject(columnName) == null);
			} catch(Error e) {
			}
			return jdbcResultSet.getString(columnName);
		}catch(SQLException e){
			throw new OdaException(e);
		}
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getInt(int)
	 */
	public int getInt( int index ) throws OdaException
	{
		
		try{
			isNull = (jdbcResultSet.getObject(index) == null);
			return jdbcResultSet.getInt(index);
		}catch(SQLException e){
			throw new OdaException(e);
		}
		
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getInt(java.lang.String)
	 */
	public int getInt( String columnName ) throws OdaException
	{
		try{
			isNull = (jdbcResultSet.getObject(columnName) == null);
			return jdbcResultSet.getInt(columnName);
		}catch(SQLException e){
			throw new OdaException(e);
		}
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getDouble(int)
	 */
	public double getDouble( int index ) throws OdaException
	{
		try{
			isNull = (jdbcResultSet.getObject(index) == null);
			return jdbcResultSet.getDouble(index);
		}catch(SQLException e){
			throw new OdaException(e);
		}
		
		
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getDouble(java.lang.String)
	 */
	public double getDouble( String columnName ) throws OdaException
	{
		try{
			isNull = (jdbcResultSet.getObject(columnName) == null);
			return jdbcResultSet.getDouble(columnName);
		}catch(SQLException e){
			throw new OdaException(e);
		}
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getBigDecimal(int)
	 */
	public BigDecimal getBigDecimal( int index ) throws OdaException
	{
		try{
			isNull = (jdbcResultSet.getObject(index) == null);
			return jdbcResultSet.getBigDecimal(index);
		}catch(SQLException e){
			throw new OdaException(e);
		}
       
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getBigDecimal(java.lang.String)
	 */
	public BigDecimal getBigDecimal( String columnName ) throws OdaException
	{
		try{
			isNull = (jdbcResultSet.getObject(columnName) == null);
			return jdbcResultSet.getBigDecimal(columnName);
		}catch(SQLException e){
			throw new OdaException(e);
		}
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getDate(int)
	 */
	public Date getDate( int index ) throws OdaException
	{
		try{
			isNull = (jdbcResultSet.getObject(index) == null);
			return jdbcResultSet.getDate(index);
		}catch(SQLException e){
			throw new OdaException(e);
		}
		
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getDate(java.lang.String)
	 */
	public Date getDate( String columnName ) throws OdaException
	{
		try{
			isNull = (jdbcResultSet.getObject(columnName) == null);
			return jdbcResultSet.getDate(columnName);
		}catch(SQLException e){
			throw new OdaException(e);
		}
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getTime(int)
	 */
	public Time getTime( int index ) throws OdaException
	{
		try{
			isNull = (jdbcResultSet.getObject(index) == null);
			return jdbcResultSet.getTime(index);
		}catch(SQLException e){
			throw new OdaException(e);
		}
		
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getTime(java.lang.String)
	 */
	public Time getTime( String columnName ) throws OdaException
	{
		try{
			isNull = (jdbcResultSet.getObject(columnName) == null);
			return jdbcResultSet.getTime(columnName);
		}catch(SQLException e){
			throw new OdaException(e);
		}
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getTimestamp(int)
	 */
	public Timestamp getTimestamp( int index ) throws OdaException
	{
		try{
			isNull = (jdbcResultSet.getObject(index) == null);
			return jdbcResultSet.getTimestamp(index);
		}catch(SQLException e){
			throw new OdaException(e);
		}
		
        
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getTimestamp(java.lang.String)
	 */
	public Timestamp getTimestamp( String columnName ) throws OdaException
	{
		try{
			isNull = (jdbcResultSet.getObject(columnName) == null);
			return jdbcResultSet.getTimestamp(columnName);
		}catch(SQLException e){
			throw new OdaException(e);
		}
	}

    /* 
     * @see org.eclipse.datatools.connectivity.oda.IResultSet#getBlob(int)
     */
    public IBlob getBlob( int index ) throws OdaException
    {
		throw new OdaException("Blob type are not supported for now");

    }

    /* 
     * @see org.eclipse.datatools.connectivity.oda.IResultSet#getBlob(java.lang.String)
     */
    public IBlob getBlob( String columnName ) throws OdaException
    {
    	throw new OdaException("Blob type are not supported for now");
    }

    /* 
     * @see org.eclipse.datatools.connectivity.oda.IResultSet#getClob(int)
     */
    public IClob getClob( int index ) throws OdaException
    {
    	throw new OdaException("CBlob type are not supported for now");
    }

    /* 
     * @see org.eclipse.datatools.connectivity.oda.IResultSet#getClob(java.lang.String)
     */
    public IClob getClob( String columnName ) throws OdaException
    {
    	throw new OdaException("CBlob type are not supported for now");
    }

    /* (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.oda.IResultSet#getBoolean(int)
     */
    public boolean getBoolean( int index ) throws OdaException
    {
    	try{
    		isNull = (jdbcResultSet.getObject(index) == null);
			return jdbcResultSet.getBoolean(index);
		}catch(SQLException e){
			throw new OdaException(e);
		}
    	
    }

    /* (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.oda.IResultSet#getBoolean(java.lang.String)
     */
    public boolean getBoolean( String columnName ) throws OdaException
    {
    	try{
    		isNull = (jdbcResultSet.getObject(columnName) == null);
			return jdbcResultSet.getBoolean(columnName);
		}catch(SQLException e){
			throw new OdaException(e);
		}
    }

    /*
     * @see org.eclipse.datatools.connectivity.oda.IResultSet#wasNull()
     */
    public boolean wasNull() throws OdaException
    {
        return isNull;
    	
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
