package bpm.nosql.oda.runtime.impl;

import java.io.IOException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;

public class HbaseResultSetMetaData implements IResultSetMetaData{

	private HbaseQuery query;
	private ArrayList<String> columns = new ArrayList<String>();
	private ArrayList<String> selectedColumns = new ArrayList<String>();
	private List<String> colHeaders = new ArrayList<String>();
	private List<Integer> colDataType = new ArrayList<Integer>();
	private HbaseConnection connection ;
	private String selectedTable, selectedFamilie;
	private HTableDescriptor[] listTable;
	private HTable table;
	
	public HbaseResultSetMetaData(HbaseQuery hbaseQuery) throws OdaException {
		try {
			this.query = hbaseQuery;
			this.connection = this.query.getConnection();
			selectedTable = this.query.getSelectedTable();
			selectedFamilie = this.query.getSelectedFamilie();
			listTable = this.connection.getListTables();

				if(selectedTable == null){
					String tb = this.query.getEffectiveQueryText();
					if(tb != null){
						selectedTable = tb.substring(tb.indexOf(" from ")+6, tb.indexOf("."));
						selectedFamilie = tb.substring(tb.indexOf(".")+1);
						selectedColumns = new ArrayList<String>();
						selectedColumns.addAll(Arrays.asList(tb.substring(tb.indexOf("select ") + 7, tb.indexOf(" from ")).split(","))) ;
						this.query.setSelectColumns(selectedColumns);
					}

				}else if(selectedColumns.isEmpty()){
					String tb = this.query.getEffectiveQueryText();
					if(tb != null){
						selectedColumns = new ArrayList<String>();
						selectedColumns.addAll(Arrays.asList(tb.substring(tb.indexOf("select ") + 7, tb.indexOf(" from ")).split(","))) ;
						this.query.setSelectColumns(selectedColumns);
					}
				}
				
			for(HTableDescriptor tableDescr : listTable){
				if(tableDescr.getNameAsString().equals(selectedTable)){
					Collection<HColumnDescriptor> listFamilies = tableDescr.getFamilies();
					
					
					for(HColumnDescriptor families : listFamilies){
						if(families.getNameAsString().equals(selectedFamilie)){
							
							Scan s;
							ResultScanner result = null;
							try {
								table = new HTable(this.connection.getConfig(), selectedTable);
								s = new Scan();
								s.addFamily(Bytes.toBytes(selectedFamilie));
								
								result = table.getScanner(s);
							} catch (IOException e) {
								e.printStackTrace();
							}
							Result r = result.next();
							
							for (KeyValue kv : r.list()) {
								String column = Bytes.toString(kv.getQualifier());
								Integer dataType = getDataType(Bytes.toString(kv.getValue()));		
									if(this.query.getSelectColumns() != null && 
											this.query.getSelectColumns().contains(column) && 
											!this.query.getSelectColumns().isEmpty()){
										
										colHeaders.add(column);
										colDataType.add(dataType);
											if(!selectedColumns.contains(column)){
												selectedColumns.add(column);
											}
										
									}else{
										columns.add(column);
									}
							}
						}
					}
			
				}
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			throw new OdaException(e.getMessage());
		}
	}


	@Override
	public int getColumnCount() throws OdaException {
		return colHeaders.size();
	}

	@Override
	public int getColumnDisplayLength(int index) throws OdaException {
		return getColumnName(index).length();
	}

	@Override
	public String getColumnLabel(int index) throws OdaException {
	    return getColumnName(index);
	}

	@Override
	public String getColumnName(int index) throws OdaException {
	    return colHeaders.get(index-1);
	}

	@Override
	public int getColumnType(int index) throws OdaException {

	    return colDataType.get(index - 1);
	}

	@Override
	public String getColumnTypeName(int index) throws OdaException {
		int nativeTypeCode = getColumnType(index);
		return HbaseDriver.getNativeDataTypeName(nativeTypeCode);
	}

	@Override
	public int getPrecision(int index) throws OdaException {
		return -1;
	}

	@Override
	public int getScale(int index) throws OdaException {
		return -1;
	}

	@Override
	public int isNullable(int index) throws OdaException {
		return IResultSetMetaData.columnNullableUnknown;
	}

	private void validateColumnIndex(int index) throws OdaException {
		if ((index > getColumnCount()) || (index < 1))
		      throw new OdaException("INVALID_COLUMN_INDEX" + index);
	}	
	
	private Integer getDataType(String string) {
		  try  
		  {  
			double d = Double.parseDouble(string); 
			return Types.DOUBLE;

		  }  
		  catch(NumberFormatException nbl)  
		  {  
			  try  
			  {  
				  Float d = Float.parseFloat(string); 
				  return Types.FLOAT;
			  }  
			  catch(NumberFormatException nin)  
			  {  
				  try  
				  {  
					  Integer d = Integer.parseInt(string);  
					  return Types.INTEGER;
				  }  
				  catch(NumberFormatException nlo)  
				  {  
					  return Types.VARCHAR;
				  }  
			  } 
		  } 
		  
	  }
	
	public ArrayList<String> getColumns() throws Exception {
		if(this.query.getEffectiveQueryText() == null){
			return  columns;
		}else{
			
			
				for(String selection : selectedColumns){
					if(!colHeaders.contains(selection)){
						colHeaders.add(selection);
					}
					
				}
				
			return selectedColumns;
		}
	}
}
