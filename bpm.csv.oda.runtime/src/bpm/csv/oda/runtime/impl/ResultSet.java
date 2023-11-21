/*
 *************************************************************************
 * Copyright (c) 2009 <<Your Company Name here>>
 *  
 *************************************************************************
 */

package bpm.csv.oda.runtime.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.eclipse.datatools.connectivity.oda.IBlob;
import org.eclipse.datatools.connectivity.oda.IClob;
import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;

import bpm.csv.oda.runtime.datas.CsvColumn;

/**
 * Implementation class of IResultSet for an ODA runtime driver.
 * <br>
 * For demo purpose, the auto-generated method stubs have
 * hard-coded implementation that returns a pre-defined set
 * of meta-data and query results.
 * A custom ODA driver is expected to implement own data source specific
 * behavior in its place. 
 */
public class ResultSet implements IResultSet {
	private int m_maxRows;
    private int m_currentRowId;
	private Query query;
	private BufferedReader reader;
	private FileInputStream is;
	private String[] currentLine;
	private String[] header;
	private List<CsvColumn> columns;
	private SimpleDateFormat sdf;
	
	public ResultSet(Query query) throws IOException, OdaException {
		this.query = query;
		columns = ((ResultSetMetaData) query.getMetaData()).getColumns();
		File f = new File(this.query.getFilePath());
		is = new FileInputStream(f);
		reader = new BufferedReader(new InputStreamReader(is, query.getEncoding()));
		
		// skip the header line
		header = reader.readLine().split("\\" + query.getSeparator(), -1);
		
		sdf= new SimpleDateFormat(query.getDateFormat());
	}
	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getMetaData()
	 */
	public IResultSetMetaData getMetaData() throws OdaException {
		return query.getMetaData();
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#setMaxRows(int)
	 */
	public void setMaxRows( int max ) throws OdaException {
		m_maxRows = max;
	}
	
	/**
	 * Returns the maximum number of rows that can be fetched from this result set.
	 * @return the maximum number of rows to fetch.
	 */
	protected int getMaxRows() {
		return m_maxRows;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#next()
	 */
	public boolean next() throws OdaException {
        int maxRows = getMaxRows();

        if( m_currentRowId < maxRows ) {
            m_currentRowId++;
            try {
				currentLine = reader.readLine().split("\\" + query.getSeparator(), -1);
	            return true;
			} catch (IOException e) {
				e.printStackTrace();
				throw new OdaException(e.getMessage());
			}

        }
        
        return false;        
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#close()
	 */
	public void close() throws OdaException {
        if (reader != null) {
        	try {
        		reader.close();
        	} catch (IOException e) {
        		e.printStackTrace();
        	}
        }
		if (is != null) {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		columns = null;
		sdf = null;
		currentLine = null;
		header = null;
        m_currentRowId = 0;     // reset row counter
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getRow()
	 */
	public int getRow() throws OdaException {
		return m_currentRowId;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getString(int)
	 */
	public String getString( int index ) throws OdaException {
		index = query.getColumnIds().get(index -1);
		checkIndex(index);
        return currentLine[index];
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getString(java.lang.String)
	 */
	public String getString( String columnName ) throws OdaException {
	    return getString( findColumn( columnName ) );
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getInt(int)
	 */
	public int getInt( int index ) throws OdaException {
		index = query.getColumnIds().get(index -1);
		checkIndex(index);
        String value = currentLine[index];
        if (value.equalsIgnoreCase("")) {
        	throw new OdaException("Error on line " + m_currentRowId + "\nInvalide value cannot convert empty string to int");
        }
        
        return new Integer(value);
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
	public double getDouble( int index ) throws OdaException {
		index = query.getColumnIds().get(index -1);
		checkIndex(index);
        String value = currentLine[index];
        if (value.equalsIgnoreCase("")) {
        	throw new OdaException("Error on line " + m_currentRowId + "\nInvalide value cannot convert empty string to double");
        }
        return new Double(value);
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
	public BigDecimal getBigDecimal( int index ) throws OdaException {
		index = query.getColumnIds().get(index -1);
		checkIndex(index);
        String value = currentLine[index];
        if (value.equalsIgnoreCase("")) {
        	return null;
        }
        return new BigDecimal(value);
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getBigDecimal(java.lang.String)
	 */
	public BigDecimal getBigDecimal( String columnName ) throws OdaException {
	    return getBigDecimal( findColumn( columnName ) );
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getDate(int)
	 */
	public Date getDate( int index ) throws OdaException {
		index = query.getColumnIds().get(index -1);
		checkIndex(index);
        String value = currentLine[index];
        java.util.Date d;
		try {
			d = sdf.parse(value);
			return new Date(d.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
			throw new OdaException(e.getMessage());
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
	public Time getTime( int index ) throws OdaException {
		index = query.getColumnIds().get(index -1);
		checkIndex(index);
        String value = currentLine[index];
        java.util.Date d;
		try {
			d = sdf.parse(value);
			return new Time(d.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
			throw new OdaException(e.getMessage());
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
	public Timestamp getTimestamp( int index ) throws OdaException {
		index = query.getColumnIds().get(index -1);
		checkIndex(index);
        String value = currentLine[index];
        java.util.Date d;
		try {
			d = sdf.parse(value);
			return new Timestamp(d.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
			throw new OdaException(e.getMessage());
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
    public boolean getBoolean( int index ) throws OdaException {
    	index = query.getColumnIds().get(index -1);
    	checkIndex(index);
    	String value = currentLine[index];
    	if (value.equalsIgnoreCase("")) {
        	throw new OdaException("Error on line " + m_currentRowId + "\nInvalide value cannot convert empty string to boolean");
        }
    	return new Boolean(value);
	
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
    public Object getObject( int index ) throws OdaException
    {
        
        throw new UnsupportedOperationException();
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
    public int findColumn( String columnName ) throws OdaException {
    	CsvColumn c = null;
    	for (CsvColumn col : columns) {
    		if (col.getName().equals(columnName)) {
    			c = col;
    			break;
    		}
    	}
    	
    	if (c == null) {
    		throw new OdaException("No column with name " + columnName + " has been found");
    	}
    	int index = 0;
        for (Integer i : query.getColumnIds()) {
        	index++;
        	if (i.intValue() == c.getPosition()) {
        		return index;
        	}
        }
        
        return index;
    }
    
    private void checkIndex(int index) throws OdaException {
    	try {
    		String s = currentLine[index];
    	}
    	catch (Exception e) {
			throw new OdaException(e.getMessage() + "\n Problem when trying to get value at position " + index + " in line " + m_currentRowId);
		}
		
	}
    
}
