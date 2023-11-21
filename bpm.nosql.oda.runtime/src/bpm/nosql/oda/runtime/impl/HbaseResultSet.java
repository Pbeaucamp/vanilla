package bpm.nosql.oda.runtime.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.eclipse.datatools.connectivity.oda.IBlob;
import org.eclipse.datatools.connectivity.oda.IClob;
import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;

public class HbaseResultSet implements IResultSet{

	private HbaseResultSetMetaData resultSetMetaData;
	private int m_currentRowId;
	private HbaseResultSetMetaData rs;
	private ArrayList<String> columns = new ArrayList<String>();
	private int m_maxRows;
	private HbaseQuery query;
	private String selectedTable, selectedFamilies;
	private ResultScanner result;
	private Iterator<Result> it;
	private Result curRow;
	
	public HbaseResultSet(HbaseQuery query) throws Exception{
		this.query = query;
		this.selectedFamilies = this.query.getSelectedFamilie();
		this.selectedTable = this .query.getSelectedTable();
		this.resultSetMetaData = new HbaseResultSetMetaData(query);
		
        HTable table = this.query.getTable();
        Scan s = new Scan();
		s.addFamily(Bytes.toBytes(this.query.getSelectedFamilie()));
		try {
			result = table.getScanner(s);
			it = result.iterator();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		this.rs = new HbaseResultSetMetaData(query);
		this.columns = rs.getColumns();
	}
	
	@Override
	public void close() throws OdaException {
		
		
	}

	@Override
	public int findColumn(String columnName) throws OdaException {
	    int columnId = 0;
		String c = null;
		for(String col : columns){
			if(col.equalsIgnoreCase(columnName)){
				 c = col;
				break;
			}
			else {
				columnId++;
			}
		}
		if (c == null) {
    		throw new OdaException("No column with name " + columnName + " has been found");
    	}
//TODO
    return columnId;
	}

	@Override
	public BigDecimal getBigDecimal(int index) throws OdaException {
		throw new UnsupportedOperationException();
	}

	@Override
	public BigDecimal getBigDecimal(String columnName) throws OdaException {
		return getBigDecimal(columnName);
	}

	@Override
	public IBlob getBlob(int index) throws OdaException {
		throw new UnsupportedOperationException();
	}

	@Override
	public IBlob getBlob(String columnName) throws OdaException {
		return getBlob(columnName);
	}

	@Override
	public boolean getBoolean(int index) throws OdaException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean getBoolean(String columnName) throws OdaException {
		return getBoolean(columnName);
	}

	@Override
	public IClob getClob(int index) throws OdaException {
		throw new UnsupportedOperationException();
	}

	@Override
	public IClob getClob(String columnName) throws OdaException {
		return getClob(columnName);
	}

	@Override
	public Date getDate(int index) throws OdaException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Date getDate(String columnName) throws OdaException {
		return getDate(columnName);
	}

	@Override
	public double getDouble(int index) throws OdaException {
		 String colName = this.rs.getColumnName(index);
	       return getDouble(colName);
	}

	@Override
	public double getDouble(String columnName) throws OdaException {
		Double value = null;
		for (KeyValue kv : curRow.list()) {
			String column = Bytes.toString(kv.getQualifier());
			if(column.equals(columnName)){
				value = Double.parseDouble(Bytes.toString(kv.getValue()));
				break;
			}
		}
		return value;
	}

	@Override
	public int getInt(int index) throws OdaException {
		String colName = this.rs.getColumnName(index);
		return getInt(colName);
	}

	@Override
	public int getInt(String columnName) throws OdaException {
		Integer value = null;
		for (KeyValue kv : curRow.list()) {
			String column = Bytes.toString(kv.getQualifier());
			if(column.equals(columnName)){
				value = Integer.parseInt(Bytes.toString(kv.getValue()));
				break;
			}
		}
		return value;
	}

	@Override
	public IResultSetMetaData getMetaData() throws OdaException {
		return this.resultSetMetaData;
	}

	@Override
	public Object getObject(int index) throws OdaException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object getObject(String columnName) throws OdaException {
		return getObject(columnName);
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
		String value = null;
		for (KeyValue kv : curRow.list()) {
			String column = Bytes.toString(kv.getQualifier());
			if(column.equals(columnName)){
				value = Bytes.toString(kv.getValue());
				break;
			}
		}
		return value;
	}

	@Override
	public Time getTime(int index) throws OdaException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Time getTime(String columnName) throws OdaException {
		return getTime(columnName);
	}

	@Override
	public Timestamp getTimestamp(int index) throws OdaException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Timestamp getTimestamp(String columnName) throws OdaException {
		 return getTimestamp(columnName);
	}

	@Override
	public boolean next() throws OdaException {
		
		  int maxRows = getMaxRows();
	        if (maxRows > 0 && m_currentRowId > maxRows){
	            return false;
	        }
	        
	        while(it.hasNext()){
	        	curRow = it.next();
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
