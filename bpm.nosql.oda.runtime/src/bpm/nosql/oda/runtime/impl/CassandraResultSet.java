package bpm.nosql.oda.runtime.impl;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.eclipse.datatools.connectivity.oda.IBlob;
import org.eclipse.datatools.connectivity.oda.IClob;
import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;

public class CassandraResultSet implements IResultSet{

	  private int m_maxRows;
	  private int m_currentRowId;
	  private String[][] sourceData;
	  private CassandraResultSetMetaData resultSetMetaData;
	  public static final int DEFAULT_MAX_ROWS = 1000;
	  private static final int CURSOR_INITIAL_VALUE = -1;
	  private int maxRows = 0;
	  private int cursor = -1;
	  private int fetchAccumulator = 0;
	  private boolean wasNull = false;

	  public CassandraResultSet(String[][] rowSet, CassandraResultSetMetaData resultSetMetaData) {
	    this.sourceData = rowSet;
	    this.resultSetMetaData = resultSetMetaData;
	  }

	  public IResultSetMetaData getMetaData() throws OdaException {
	    return this.resultSetMetaData;
	  }

	  public void setMaxRows(int max) throws OdaException {
	    this.m_maxRows = max;
	  }

	  protected int getMaxRows() {
	    return this.m_maxRows;
	  }

	  private void validateCursorState() throws OdaException {
	    if (this.cursor < 0)
	      throw new OdaException();
	  }

	  public boolean next() throws OdaException {
		  
	    if (((this.maxRows > 0) && (this.cursor >= this.maxRows - 1)) ||(this.cursor >= this.sourceData.length - 1)) {
	      this.cursor = -1;
	      return false;
	    }
	    
	    this.cursor += 1;
	    return true;
	  }

	  public void close() throws OdaException {
	    this.m_currentRowId = 0;
	  }

	  public int getRow() throws OdaException {
	    validateCursorState();
	    return this.fetchAccumulator;
	  }

	  public String getString(int index) throws OdaException {
	    validateCursorState();
	    String result = this.sourceData[this.cursor][(index - 1)];
	    
		    if (result.length() == 0){
		    	result = null;
		    }
	      
	    this.wasNull = (result == null);
	    return result;
	  }

	  public String getString(String columnName) throws OdaException {
	    return getString(findColumn(columnName));
	  }

	  public int getInt(int index) throws OdaException {
	    return getRow();
	  }

	  public int getInt(String columnName) throws OdaException {
	    return getInt(findColumn(columnName));
	  }

	  public double getDouble(int index) throws OdaException {
	    throw new UnsupportedOperationException();
	  }

	  public double getDouble(String columnName) throws OdaException {
	    return getDouble(findColumn(columnName));
	  }

	  public BigDecimal getBigDecimal(int index) throws OdaException {
	    throw new UnsupportedOperationException();
	  }

	  public BigDecimal getBigDecimal(String columnName) throws OdaException {
	    return getBigDecimal(findColumn(columnName));
	  }

	  public Date getDate(int index) throws OdaException {
	    throw new UnsupportedOperationException();
	  }

	  public Date getDate(String columnName) throws OdaException {
	    return getDate(findColumn(columnName));
	  }

	  public Time getTime(int index) throws OdaException {
	    throw new UnsupportedOperationException();
	  }

	  public Time getTime(String columnName) throws OdaException {
	    return getTime(findColumn(columnName));
	  }

	  public Timestamp getTimestamp(int index) throws OdaException {
	    throw new UnsupportedOperationException();
	  }

	  public Timestamp getTimestamp(String columnName) throws OdaException {
	    return getTimestamp(findColumn(columnName));
	  }

	  public IBlob getBlob(int index) throws OdaException {
	    throw new UnsupportedOperationException();
	  }

	  public IBlob getBlob(String columnName) throws OdaException {
	    return getBlob(findColumn(columnName));
	  }

	  public IClob getClob(int index) throws OdaException {
	    throw new UnsupportedOperationException();
	  }

	  public IClob getClob(String columnName) throws OdaException {
	    return getClob(findColumn(columnName));
	  }

	  public boolean getBoolean(int index) throws OdaException {
	    throw new UnsupportedOperationException();
	  }

	  public boolean getBoolean(String columnName) throws OdaException {
	    return getBoolean(findColumn(columnName));
	  }

	  public Object getObject(int index) throws OdaException {
	    throw new UnsupportedOperationException();
	  }

	  public Object getObject(String columnName) throws OdaException {
	    return getObject(findColumn(columnName));
	  }

	  public boolean wasNull() throws OdaException {
	    return false;
	  }

	  public int findColumn(String columnName) throws OdaException {
	    int columnId = 1;
		    if ((columnName == null) || (columnName.length() == 0))
		      {
		    	return columnId;
		      }
	    String lastChar = columnName.substring(columnName.length() - 1, 1);
	    
		    try {
		      columnId = Integer.parseInt(lastChar);
		    } catch (NumberFormatException localNumberFormatException) {
		    }
	    return columnId;
	  }
}
