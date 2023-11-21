package bpm.nosql.oda.runtime.impl;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.datatools.connectivity.oda.IBlob;
import org.eclipse.datatools.connectivity.oda.IClob;
import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class ResultSet implements IResultSet {
	private Query query;
	private ArrayList<String> columns = new ArrayList<String>();
	private ResultSetMetaData rs;
	private DBObject curRow;
	private DBCursor cursor;
	private int m_currentRowId;
	private int m_maxRows;
	
	public ResultSet(Query query) throws Exception {
		this.query = query;
		this.cursor = this.query.getCursor();
		this.rs = new ResultSetMetaData(query);
		this.columns = rs.getColumns();
	}
	public ResultSet(DBCursor cursor2, DBObject metadataObject,
			List<String> selectColumns) {
	}
	@Override
	public void close() throws OdaException {
			query = null;
			columns = null;
		
	}

	@Override
	public int findColumn(String columnName) throws OdaException {
		int index = 0;
		String c = null;
		for(String col : columns){
			if(col.equalsIgnoreCase(columnName)){
				 c = col;
				break;
			}
			else {
				index++;
			}
		}
		if (c == null) {
    		throw new OdaException("No column with name " + columnName + " has been found");
    	}
		return index;
	}

	@Override
	public BigDecimal getBigDecimal(int index) throws OdaException {
		throw new UnsupportedOperationException();
	}

	@Override
	public BigDecimal getBigDecimal(String columnName) throws OdaException {
		throw new UnsupportedOperationException();
		}

	@Override
	public IBlob getBlob(int index) throws OdaException {
		throw new UnsupportedOperationException();
	}

	@Override
	public IBlob getBlob(String columnName) throws OdaException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean getBoolean(int index) throws OdaException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean getBoolean(String columnName) throws OdaException {
		throw new UnsupportedOperationException();
	}

	@Override
	public IClob getClob(int index) throws OdaException {
		throw new UnsupportedOperationException();
	}

	@Override
	public IClob getClob(String columnName) throws OdaException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Date getDate(int index) throws OdaException {
		String colName = this.rs.getColumnName(index);
        return getDate(colName);
        
	}

	@Override
	public Date getDate(String columnName) throws OdaException {
		   try {
               return new java.sql.Date(new SimpleDateFormat().parse(curRow.get(columnName).toString()).getTime());
       } catch (ParseException e) {
               throw new UnsupportedOperationException();
       }
	}

	@Override
	public double getDouble(int index) throws OdaException {
		 String colName = this.rs.getColumnName(index);
         return getDouble(colName);
	}

	@Override
	public double getDouble(String columnName) throws OdaException {
        Object value = curRow.get(columnName); 
        try
        {
                return (value==null)? 0.0 : new Double(value.toString());
        }
        catch(NumberFormatException ne)
        {
                        return 0.0;
        } 
	}

	@Override
	public int getInt(int index) throws OdaException {
		 String colName = this.rs.getColumnName(index);
         return getInt(colName);
	}

	@Override
	public int getInt(String columnName) throws OdaException {
       
		Object value = curRow.get(columnName); 
        try
        {
                return (value==null)? 0 : new Integer(value.toString());
        }
        catch(NumberFormatException ne)
        {
                return 0;
        }
	}

	@Override
	public IResultSetMetaData getMetaData() throws OdaException {
		return rs;
	}

	@Override
	public Object getObject(int index) throws OdaException {
		 throw new UnsupportedOperationException();
	}

	@Override
	public Object getObject(String columnName) throws OdaException {
		 throw new UnsupportedOperationException();
	}

	@Override
	public int getRow() throws OdaException {
		return m_currentRowId;
	}

	@Override
	public String getString(int index) throws OdaException {
		 String colName = this.rs.getColumnName(index);
	       return getString(colName);
	}

	@Override
	public String getString(String columnName) throws OdaException {
		Object value = curRow.get(columnName); 
        return (value==null)? "" : value.toString();
	}

	@Override
	public Time getTime(int index) throws OdaException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Time getTime(String columnName) throws OdaException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Timestamp getTimestamp(int index) throws OdaException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Timestamp getTimestamp(String columnName) throws OdaException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean next() throws OdaException {
		   int maxRows = getMaxRows();
	        if (maxRows > 0 && m_currentRowId > maxRows){
	            return false;
	        }
	        
	        while(cursor.hasNext()) {
	                curRow = cursor.next();
	                m_currentRowId++;
	                return true;
	        }      
	        
	        return false;    
	}

	@Override
	public void setMaxRows(int max) throws OdaException {
		m_maxRows = max;		
	}

	@Override
	public boolean wasNull() throws OdaException {
		return false;
	}

	 protected int getMaxRows()
     {
             return m_maxRows;
     }
}
