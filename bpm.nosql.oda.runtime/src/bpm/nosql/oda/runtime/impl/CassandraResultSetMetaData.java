package bpm.nosql.oda.runtime.impl;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;

public class CassandraResultSetMetaData implements IResultSetMetaData{

	 private String[] columnTypeNames;
	  private String[] columnNames;
	  private Map<String, Integer> columnNameIndexMap = new HashMap<String, Integer>();

	  public CassandraResultSetMetaData(String[] queryColumnNames, String[] queryColumnTypes)
	  {
	    this.columnNames = queryColumnNames;
	    this.columnTypeNames = queryColumnTypes;
	    for (int i = 0; i < this.columnNames.length; i++)
	      this.columnNameIndexMap.put(this.columnNames[i].toUpperCase(), 
	        Integer.valueOf(i + 1));
	  }

	  public int getColumnCount() throws OdaException {
	    return this.columnNames.length;
	  }

	  public String getColumnName(int index) throws OdaException {
	    validateColumnIndex(index);
	    return this.columnNames[(index - 1)].trim();
	  }

	  private void validateColumnIndex(int index) throws OdaException {
	    if ((index > getColumnCount()) || (index < 1))
	      throw new OdaException("INVALID_COLUMN_INDEX" + index);
	  }

	  private Integer getDataType(String sampleValue) {
	    if (sampleValue.indexOf('.') > 0)
	      try {
	        new Double(sampleValue);
	        return Integer.valueOf(8);
	      } catch (NumberFormatException localNumberFormatException1) {
	      }
	    try {
	      new Integer(sampleValue);
	      return Integer.valueOf(4);
	    } catch (NumberFormatException localNumberFormatException2) {
	    }
	    return Integer.valueOf(1);
	  }

	  public String getColumnLabel(int index) throws OdaException {
	    return getColumnName(index);
	  }

	  public int getColumnType(int index) throws OdaException {
	    Integer dataType = getDataType(this.columnNames[(index - 1)].toString());

	    validateColumnIndex(index);

	    return dataType.intValue();
	  }

	  public String getColumnTypeName(int index) throws OdaException {
	    int nativeTypeCode = getColumnType(index);
	    return CassandraDriver.getNativeDataTypeName(nativeTypeCode);
	  }

	  public int getColumnDisplayLength(int index) throws OdaException {
	    return 8;
	  }

	  public int getPrecision(int index) throws OdaException {
	    return -1;
	  }

	  public int getScale(int index) throws OdaException {
	    return -1;
	  }

	  public int isNullable(int index) throws OdaException {
	    return 2;
	  }

	  public int findColumn(String columnName) throws OdaException {
	    String trimmedColumnName = columnName.trim();
	    Integer index = (Integer)this.columnNameIndexMap.get(trimmedColumnName
	      .toUpperCase());
	    if (index == null) {
	      throw new OdaException("resultSet_COLUMN_NOT_FOUND " + columnName);
	    }
	    return index.intValue();
	  }
}
