/*
 *************************************************************************
 * Copyright (c) 2009 <<Your Company Name here>>
 *  
 *************************************************************************
 */

package bpm.excel.oda.runtime.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;

import jxl.Cell;
import jxl.NumberCell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;
import jxl.DateCell;

import org.eclipse.datatools.connectivity.oda.IBlob;
import org.eclipse.datatools.connectivity.oda.IClob;
import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;

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
	private int m_maxRows = 0;
    private int m_currentRowId;
    private Query query;
    private Workbook workbookin;
    private String error;
    
    public ResultSet(Query query) {
    	this.query = query;
    	InputStream is;
		try {
			is = new FileInputStream(query.getFilePath());
			WorkbookSettings set = new WorkbookSettings();
			set.setInitialFileSize(is.available());
			workbookin = Workbook.getWorkbook(is, set);
			is.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			error = e.getMessage();
		} catch (IOException e) {
			e.printStackTrace();
			error = e.getMessage();
		} catch (BiffException e) {
			e.printStackTrace();
			error = e.getMessage();
		}
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
        
		Sheet sheet = workbookin.getSheet(query.getSheetName());
		if(m_currentRowId >= sheet.getRows() - 1) {
			return false;
		}
		
        // simple implementation done below for demo purpose only
        int maxRows = getMaxRows();
        
        if( m_currentRowId < maxRows ) {
            m_currentRowId++;
            return true;
        }
        
        return false;        
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#close()
	 */
	public void close() throws OdaException {
		try {
			workbookin.close();
//			((ResultSetMetaData)query.getMetaData()).close();
			query.close();
		} catch (Exception e) {
		}
		workbookin = null;
		query = null;
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
	public String getString( int index ) throws OdaException {
		if (workbookin == null) {
			throw new OdaException(error);
		}
		index = query.getColumnIds().get(index -1);
		Sheet sheet = workbookin.getSheet(query.getSheetName());
		Cell cell = sheet.getCell(index, getRow());
		
        return cell.getContents();
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
		if (workbookin == null) {
			throw new OdaException(error);
		}
		index = query.getColumnIds().get(index -1);
		Sheet sheet = workbookin.getSheet(query.getSheetName());
		Cell cell = sheet.getCell(index, getRow());
		try {
			int res  = new Integer(cell.getContents());
	        return res;
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new OdaException(e.getMessage());
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
	public double getDouble( int index ) throws OdaException {
		if (workbookin == null) {
			throw new OdaException(error);
		}
		index = query.getColumnIds().get(index -1);
		Sheet sheet = workbookin.getSheet(query.getSheetName());
		Cell cell = sheet.getCell(index, getRow());
		try {
			Double res  = new Double(cell.getContents());
	        return res;
		}
		catch (Exception e) {
			try {
				return new Double(((NumberCell) cell).getValue());
			}
			catch (Exception _e) {
				_e.printStackTrace();
				throw new OdaException(e.getMessage() + " for cellcontents" + cell.getContents());
			}
			
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
	public BigDecimal getBigDecimal( int index ) throws OdaException {

		if (workbookin == null) {
			throw new OdaException(error);
		}
		index = query.getColumnIds().get(index -1);
		Sheet sheet = workbookin.getSheet(query.getSheetName());
		Cell cell = sheet.getCell(index, getRow());
		try {
			BigDecimal res  = new BigDecimal(cell.getContents());
	        return res;
		}
		catch (Exception e) {
			try {
				return new BigDecimal(((NumberCell) cell).getValue());
			}
			catch (Exception _e) {
				_e.printStackTrace();
				return null;
			}
			
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
	public Date getDate( int index ) throws OdaException {
		if (workbookin == null) {
			throw new OdaException(error);
		}
		index = query.getColumnIds().get(index -1);
		Sheet sheet = workbookin.getSheet(query.getSheetName());
		Cell cell = sheet.getCell(index, getRow());
		try {
			DateFormat df = ((DateCell) cell).getDateFormat();
			java.util.Date date = df.parse(cell.getContents());
	        return new Date(date.getTime());
		}
		catch (Exception e) {
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
		if (workbookin == null) {
			throw new OdaException(error);
		}
		index = query.getColumnIds().get(index -1);
		Sheet sheet = workbookin.getSheet(query.getSheetName());
		Cell cell = sheet.getCell(index, getRow());
		try {
			Time res  = Time.valueOf(cell.getContents());
	        return res;
		}
		catch (Exception e) {
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

		if (workbookin == null) {
			throw new OdaException(error);
		}
		index = query.getColumnIds().get(index -1);
		Sheet sheet = workbookin.getSheet(query.getSheetName());
		Cell cell = sheet.getCell(index, getRow());
		try {
			Timestamp res  = Timestamp.valueOf(cell.getContents());
	        return res;
		}
		catch (Exception e) {
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
		if (workbookin == null) {
			throw new OdaException(error);
		}
		Sheet sheet = workbookin.getSheet(query.getSheetName());
		Cell cell = sheet.getCell(index, getRow());
		try {
			Boolean res  = new Boolean(cell.getContents());
	        return res;
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new OdaException(e.getMessage());
		}
	
	
	
	
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
        int columnId = 1;   // dummy column id
        if( columnName == null || columnName.length() == 0 )
            return columnId;
        
        
        Sheet sheet = workbookin.getSheet(query.getSheetName());
        
        Cell c = sheet.findCell(columnName);
        
        if (c == null) {
        	throw new OdaException("No column with name " + columnName + " has beeen found");
        }
        int index = 0;
        for (Integer i : query.getColumnIds()) {
        	index++;
        	if (i.intValue() == c.getColumn()) {
        		return index;
        	}
        }
        
        return index;
    }
    
}
