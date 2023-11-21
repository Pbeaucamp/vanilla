package bpm.fd.api.core.model.datas;

import bpm.fd.api.internal.ILabelable;

public class ColumnDescriptor implements ILabelable{
	private int columnIndex = 0;
	private String columnLabel;
	private String columnName;
	private int columnType;
	private String columnTypeName;
	private DataSet dataSet; 
	
	
	
	
	protected ColumnDescriptor(DataSet dataSet, int columnIndex, String columnLabel, String columnName, int columnType, String columnTypeName) {
		super();
		this.dataSet = dataSet;
		this.columnIndex = columnIndex;
		this.columnLabel = columnLabel;
		this.columnName = columnName;
		this.columnType = columnType;
		this.columnTypeName = columnTypeName;
	}
	
	public String getLabel(){
		return getColumnLabel();
	}
	public DataSet getDataSet(){
		return dataSet;
	}
	
	/**
	 * @return the columnIndex
	 */
	public int getColumnIndex() {
		return columnIndex;
	}
	/**
	 * @return the columnLabel
	 */
	public String getColumnLabel() {
		return columnLabel;
	}
	/**
	 * @return the columnName
	 */
	public String getColumnName() {
		return columnName;
	}
	/**
	 * @return the columnType
	 */
	public int getColumnType() {
		return columnType;
	}
	/**
	 * @return the columnTypeName
	 */
	public String getColumnTypeName() {
		return columnTypeName;
	}

	public String getName() {
		return getColumnName();
	}
	
	
}
