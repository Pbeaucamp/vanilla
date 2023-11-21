package bpm.nosql.oda.runtime.impl;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class ResultSetMetaData implements IResultSetMetaData{
	private int numberOfRow = 0;
	private Query query;
	
	private ArrayList<String> columns = new ArrayList<String>();
	private ArrayList<String> selectedColumns = new ArrayList<String>();
	private ArrayList<Integer> types = new ArrayList<Integer>();
	private Set<String> keyValues;
	 private List<String> colHeaders = new ArrayList<String>();
     private List<Integer> colDataType = new ArrayList<Integer>();
     
	public ArrayList<String> getColumns() throws Exception {
//		if(this.query.getEffectiveQueryText() == null){
//			return  columns;
//		}else{
//			colHeaders.clear();
//			
//				for(String selection : selectedColumns){
//					colHeaders.add(selection);
//				}
//				
//			return columns;
//		}
		return columns;
	}

	public void setColumns(ArrayList<String> columns) {
		this.colHeaders = columns;
	}

	public ResultSetMetaData(Query query) throws Exception {

		try {
				this.query = query;
				
				DBCollection collection = null;
				if(this.query.getCollection() != null) {
					collection = this.query.getCollection();
				}
				else {
					DB table = this.query.getDb();
					collection = table.getCollection(table.getName());
				}

				DBCursor cursor = collection.find();
				Iterator<DBObject> it = cursor.iterator();
				
				boolean end = false;

				
				while(it.hasNext() && !end){
					
					DBObject d = it.next();
					
					keyValues = d.keySet();
					
					for(String kv : keyValues){
						
						if(this.query.getSelectColumns() != null && 
								this.query.getSelectColumns().contains(kv) && 
								!this.query.getSelectColumns().isEmpty()){
							
							colHeaders.add(kv);
							Integer dataType = getDataType(d.get(kv)
	                                .toString());
							colDataType.add(dataType);
							selectedColumns.add(kv);
							
						}else {
							colHeaders.add(kv);				
							columns.add(kv);
							Integer dataType = getDataType(d.get(kv)
	                                .toString());
							colDataType.add(dataType);
							types.add(dataType);
							
						}
						
					}
					
					end= true;
					
				}
				cursor.close();
			

		}catch (Exception e) {
			e.printStackTrace();
			throw new OdaException(e.getMessage());
		}
	}

	public ResultSetMetaData(DBObject metadataObject, List<String> selectColumns) {
		
		boolean allColumns = false;
		
        if (selectColumns.size() == 0) {
                allColumns = true; 
        }
        
        for (String keyField : metadataObject.keySet()) {
        	
                if (allColumns || selectColumns.contains(keyField)) {
                	
                        colHeaders.add(keyField);
                        Integer dataType = getDataType(metadataObject.get(keyField)
                                        .toString());
                        colDataType.add(dataType);
                }
        }	
        
	}

	
    private Integer getDataType(String sampleValue) {


        if (sampleValue.indexOf('.') > 0) {
                try {
                        @SuppressWarnings("unused")
                        Double tDbl = new Double(sampleValue);
                        return Types.DOUBLE;
                } catch (NumberFormatException e) { 
                }
        }

        try {
                @SuppressWarnings("unused")
                Integer tInt = new Integer(sampleValue);
                return Types.INTEGER;
        } catch (NumberFormatException e) {
                return Types.CHAR;
        }
}
    
	@Override
	public int getColumnCount() throws OdaException {
		return colHeaders.size();
	}

	@Override
	public int getColumnDisplayLength(int index) throws OdaException {
		return 8;
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
         return Driver.getNativeDataTypeName(nativeTypeCode);
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
	
	public int getMaxRow() {
		return numberOfRow;
	}
}
