package bpm.dataprovider.odainput.consumer;

import java.math.BigInteger;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;

import bpm.vanilla.platform.core.beans.data.OdaInput;

public class OdaHelper {


	public static List<List<Object>> getValues(OdaInput input, int rowNumber) throws Exception {
		IQuery query = QueryHelper.buildquery(input);
		
		if (rowNumber > 0) {
			query.setMaxRows(rowNumber);
		}
		
		List<List<Object>> values = new ArrayList<List<Object>>();
		
		IResultSetMetaData rsmd = query.getMetaData();
			
		IResultSet rs = query.executeQuery();
	
		while(rs.next()){
			List<Object> row = new ArrayList<Object>();

			for(int i = 1; i <= rsmd.getColumnCount(); i++){
				row.add(getValue(i, rs));
			}
			values.add(row);
		}
		rs.close();
		query.close();
		
		return values;
	}
	
	public static List<List<Object>> getValues(OdaInput input, int rowNumber, String column) throws Exception {
		IQuery query = QueryHelper.buildquery(input);
		
		if (rowNumber > 0) {
			query.setMaxRows(rowNumber);
		}
		
		List<List<Object>> values = new ArrayList<List<Object>>();
		
		IResultSetMetaData rsmd = query.getMetaData();
		
		int colIndex = -1;
		for(int i = 1; i <= rsmd.getColumnCount(); i++){
			if (rsmd.getColumnName(i).endsWith(column)){
				colIndex = i;
				break;
			}
			
		}
		if (colIndex == -1){
			for(int i = 1; i <= rsmd.getColumnCount(); i++){
				if (rsmd.getColumnLabel(i).endsWith(column)){
					colIndex = i;
					break;
				}
				
			}
		}
		
		if (colIndex == -1){
			throw new Exception("Unable to find a Column named nor Labeled  "+ column + " within the query " + query.getEffectiveQueryText());
		}
		
		IResultSet rs = query.executeQuery();
		
		
		
		while(rs.next()){
			List<Object> row = new ArrayList<Object>();
			
			row.add(getValue(colIndex, rs));
			values.add(row);
		}
		rs.close();
		query.close();
		
		return values;
	}
	
	
	
	private static Object getValue(int i, IResultSet rs) throws OdaException {
		Object val = null;
		int columnType = rs.getMetaData().getColumnType(i);
		switch(columnType){
		
		
		case Types.BIGINT:
		case Types.SMALLINT:
		case Types.TINYINT:
		case Types.INTEGER:
			val = rs.getInt(i);
			break;
		case Types.DATE:
			val = rs.getDate(i);
			break;
		case Types.TIMESTAMP:
			val = rs.getTimestamp(i);
			break;
		case Types.TIME:
			val = rs.getTime(i);
			break;
			
		case Types.FLOAT:
		case Types.DOUBLE:
		case Types.REAL:
		case Types.NUMERIC:
			val = rs.getDouble(i);
			break;
			
		case Types.DECIMAL:
			val = rs.getBigDecimal(i);
			
			break;
		
		default:
			val = rs.getString(i);
		}
		
		return val;
	}
	
	



	public static List<String> createDescriptor(OdaInput input) throws Exception{
		IQuery query = QueryHelper.buildquery(input);
		
		try{
			query.setMaxRows(1);
			query.setProperty("rowFetchSize", 1 + "");
		}catch(Exception ex){
			
		}
		
		
		IResultSetMetaData meta =  query.getMetaData();;
		List<String> columns = new ArrayList<String>();
		
		for( int i = 1; i <= meta.getColumnCount(); i ++){
			columns.add(meta.getColumnName(i));
		}
		
		QueryHelper.removeQuery(query);
		return columns;
	
	}
	
	public static int getType(OdaInput input, String field) throws Exception {
		IQuery query = QueryHelper.buildquery(input);
		
		IResultSetMetaData meta =  query.getMetaData();
		
		for( int i = 1; i <= meta.getColumnCount(); i ++){
			if (meta.getColumnName(i).equals(field)) {
				return meta.getColumnType(i);
			}
		}
		
		throw new Exception("JavaClass not found");
	}
	
	public static String getJavaClassName(int typeCode){
		switch(typeCode){
		case Types.BIGINT:
			return BigInteger.class.getName();
		case Types.INTEGER:
		case Types.SMALLINT:
			return Integer.class.getName();
			
		case Types.BOOLEAN:
			return Boolean.class.getName();
			
		case Types.CHAR:
		case Types.LONGVARCHAR:
		case Types.VARCHAR:
		case Types.VARBINARY:
			return String.class.getName();
			
		case Types.FLOAT:
			return Float.class.getName();
		case Types.REAL:	
		case Types.DECIMAL:
		case Types.DOUBLE:
		case Types.NUMERIC:
			return Double.class.getName();
			
		
		case Types.DATE:
		case Types.TIME:
		case Types.TIMESTAMP:
			return Date.class.getName();
		}
		return Object.class.getName();
	}


}
