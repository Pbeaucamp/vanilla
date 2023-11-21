package bpm.nosql.oda.runtime.impl;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.cassandra.thrift.TimedOutException;
import org.apache.cassandra.thrift.UnavailableException;
import org.apache.thrift.TException;
import org.eclipse.datatools.connectivity.oda.IParameterMetaData;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.datatools.connectivity.oda.SortSpec;
import org.eclipse.datatools.connectivity.oda.spec.QuerySpecification;

import bpm.nosql.oda.runtime.cassandraUtility.CassandraInterface;
import bpm.nosql.oda.runtime.cassandraUtility.CassandraRow;
import bpm.nosql.oda.runtime.queryParser.StringParser;

public class CassandraQuery implements IQuery {

	private int m_maxRows;
	private String m_preparedText;
	private CassandraConnection connection;
	private Properties connProperties;
	private CassandraInterface casInterface;
	private String colFamily;
	private CassandraResultSetMetaData resultSetMetaData;
	private List<CassandraRow> result;
	private List<String> columnHeader;
	private static String key;
	private String[] queryColumnNames;
	private String[] queryColumnTypes;
	private List<String> toQueryCols;
	private List<String> finalQueryCols;

	public CassandraQuery(CassandraConnection cassandraConnection, Properties connProperties, CassandraInterface casInterface) {
		this.casInterface = casInterface;
		this.connection = cassandraConnection;
		this.connProperties = connProperties;
	}

	public void prepare(String queryText) throws OdaException {
		this.m_preparedText = queryText;
		this.colFamily = queryText;
		this.toQueryCols = new ArrayList<String>();
		StringParser sp = new StringParser();
		sp.parse(queryText);
		this.toQueryCols = sp.getColumns();
		this.colFamily = sp.getTableName();
		prepareMetaData();
	}

	private void prepareMetaData() throws OdaException {
		this.result = new ArrayList<CassandraRow>();
		this.columnHeader = new ArrayList<String>();
		this.finalQueryCols = new ArrayList<String>();
		int count = 0;

		try {
			this.result = this.casInterface.fetchRows(this.colFamily);
		} catch (InvalidRequestException e) {
			throw new OdaException(e);
		} catch (UnavailableException e) {
			throw new OdaException(e);
		} catch (TimedOutException e) {
			throw new OdaException(e);
		} catch (TException e) {
			throw new OdaException(e);
		}

		Iterator<?> it;

		for (Iterator<CassandraRow> localIterator1 = this.result.iterator(); localIterator1.hasNext();

		it.hasNext()) {
			CassandraRow cRow = (CassandraRow) localIterator1.next();
			Map<String, String> resultSet = new HashMap<String, String>();

			key = cRow.getKey();
			resultSet = cRow.getColumns();
			it = resultSet.entrySet().iterator();
			continue;
		}

		this.columnHeader = this.toQueryCols;

		for (String colString : this.toQueryCols) {
			if (!this.columnHeader.contains(colString)) {
				throw new OdaException("Column Value " + colString + " not Found. Error in Query");
			}
		}

		this.queryColumnNames = new String[this.columnHeader.size() + 1];
		this.queryColumnTypes = new String[this.columnHeader.size() + 1];

		this.queryColumnNames[count] = "Key";
		this.queryColumnTypes[count] = "String";

		count++;

		for (String headers : this.columnHeader) {
			this.queryColumnNames[count] = headers;
			this.queryColumnTypes[count] = "String";
			count++;
		}
		count = 0;
//______________________________________________________
//		
//    	
//    		Cluster myCluster = HFactory.getOrCreateCluster("testKeyspace", 
//    				"192.168.1.201" + ":" + 9610);int limit = 100;
//    		Keyspace ksp = HFactory.createKeyspace("testKeyspace", myCluster);
//    		String query = m_preparedText;
//    		int fromIndex = query.toLowerCase().indexOf("from");
//    		
//	        String tempS = query.toLowerCase();
//	        int offset = fromIndex;
//	        while (fromIndex > 0 && tempS.charAt(fromIndex - 1) != ' ' 
//	        	&& (fromIndex + 4 < tempS.length()) && tempS.charAt(fromIndex + 4) != ' ') {
//		          tempS = tempS.substring(fromIndex + 4, tempS.length());
//		          fromIndex = tempS.indexOf("from");
//		          offset += (4 + fromIndex);
//	        }
//	        
//	        fromIndex = offset;
//	        
//	  
//			
//			String colFamName = query.substring(fromIndex + 4, query.length()).trim();
//	        if (colFamName.indexOf(' ') > 0) {
//	        	colFamName = colFamName.substring(0, colFamName.indexOf(' '));
//	        } 
//	        else {
//	        	colFamName = colFamName.replace(";", "");
//	        }
//	        
//	    
//			
//			if (query.toLowerCase().contains(" limit ")){
//				query = query.substring(0, query.toLowerCase().indexOf(" limit " ) + 7) + limit;
//			}
//			else {
//				boolean containsGroupBy = query.toLowerCase().indexOf(" group by ") != -1 ;
//				boolean orderGroupBy = query.toLowerCase().indexOf(" order by ") != -1 ;
//				boolean containWhere =  query.toLowerCase().indexOf(" where ") != -1;
//				
//				if (containsGroupBy){
//					query = query.substring(0, query.toLowerCase().indexOf(" group by " )) + " limit " + limit + " "  + query.substring(query.toLowerCase().indexOf(" group by " )) ;
//				}
//				else if (orderGroupBy){
//					query = query.substring(0, query.toLowerCase().indexOf(" order by " )) + " limit " + limit + " "  + query.substring(query.toLowerCase().indexOf(" order by " )) ;
//				}
//				else if (containWhere){
//					query = query.substring(0, query.toLowerCase().indexOf(" where " )) + " limit " + limit + " "  + query.substring(query.toLowerCase().indexOf(" where " )) ;
//				}
//				else{
////					query += " limit " + limit;
//				}
//			}
//			
//			HashMap<String, String> columnNames = new LinkedHashMap<String, String>();
////			StreamDescriptor descriptor = model.getDescriptor();
//			for(String el : queryColumnNames){
//				columnNames.put(el, null);
//			}
//				
//			CqlQuery<String, String, ByteBuffer> cqlQuery = new CqlQuery<String, String, ByteBuffer>(
//					ksp, new StringSerializer(), new StringSerializer(), new ByteBufferSerializer());
//			cqlQuery.setQuery(query);
//				
//				
//			QueryResult<CqlRows<String, String, ByteBuffer>> result1 = cqlQuery.execute();
//			Iterator<me.prettyprint.hector.api.beans.Row<String, String, ByteBuffer>> resultSet = result1.get().iterator();
//			
//			while(resultSet.hasNext()){
//				me.prettyprint.hector.api.beans.Row<String, String, ByteBuffer> rowTmp = resultSet.next();
//	    		ColumnSlice<String, ByteBuffer> slice = rowTmp.getColumnSlice();
//
//    			List<Object> row = new ArrayList<Object>();
//    			
//	    		for(String columnName : columnNames.keySet()){
//	    			HColumn<String, ByteBuffer> col = slice.getColumnByName(columnName);
//	    			
//	    			SQLTYPE type = null;
//	    			try {
//	    				type = SQLTYPE.getTypeFromValue(columnNames.get(columnName));
//	    			} catch (Exception e) {
////	    				throw new ServerException("", e, null);
//					}
//	    			if(col != null && col.getValue() != null){
//	    				
//	    				switch (type) {
//						case INTEGER:
//		    				row.add(col.getValue().getInt());
//							break;
//							
//						case LONG:
//		    				row.add(col.getValue().getLong());
//							break;
//							
////						case DATE:
////		    				row.set(i, col.getValue().get);
////							break;
//							
//						case DOUBLE:
//		    				row.add(col.getValue().getDouble());
//							break;
//						
//						case VARCHAR:
//		    				row.add(Charset.defaultCharset().decode(col.getValue()).toString());
//							break;
//							
//						default:
//		    				row.add(Charset.defaultCharset().decode(col.getValue()).toString());
//							break;
//						}
//	    			}
//	    			else {
//	    				row.add(null);
//	    			}
//	    		}
//				System.out.println();//values.add(row);
//    	}
//_________________________________________________________
		this.resultSetMetaData = new CassandraResultSetMetaData(this.queryColumnNames, this.queryColumnTypes);
	}

	public void setAppContext(Object context) throws OdaException {}

	public void close() throws OdaException {
		this.m_preparedText = null;
	}

	public IResultSetMetaData getMetaData() throws OdaException {
		if (this.resultSetMetaData == null) {
			prepareMetaData();
		}
		return this.resultSetMetaData;
	}

	public IResultSet executeQuery() throws OdaException {
		String[][] rowSet = new String[this.result.size()][this.columnHeader.size() + 1];
		int i = 0;
		int j = 0;
		for (CassandraRow cRow : this.result) {
			Map<String, String> resultSet = new HashMap<String, String>();
			j = 0;
			key = cRow.getKey();
			resultSet = cRow.getColumns();
			rowSet[i][j] = key;
			j++;
			for (String headers : this.columnHeader) {

				if (resultSet.get(headers) == null) {
					rowSet[i][j] = " ";
				}

				else {
					rowSet[i][j] = ((String) resultSet.get(headers));
				}

				j++;
			}

			i++;
		}
		i = 0;

		return new CassandraResultSet(rowSet, this.resultSetMetaData);
	}

	public void setProperty(String name, String value) throws OdaException {}

	public void setMaxRows(int max) throws OdaException {
		this.m_maxRows = max;
	}

	public int getMaxRows() throws OdaException {
		return this.m_maxRows;
	}

	public void clearInParameters() throws OdaException {}

	public void setInt(String parameterName, int value) throws OdaException {}

	public void setInt(int parameterId, int value) throws OdaException {}

	public void setDouble(String parameterName, double value) throws OdaException {}

	public void setDouble(int parameterId, double value) throws OdaException {}

	public void setBigDecimal(String parameterName, BigDecimal value) throws OdaException {}

	public void setBigDecimal(int parameterId, BigDecimal value) throws OdaException {}

	public void setString(String parameterName, String value) throws OdaException {}

	public void setString(int parameterId, String value) throws OdaException {}

	public void setDate(String parameterName, Date value) throws OdaException {}

	public void setDate(int parameterId, Date value) throws OdaException {}

	public void setTime(String parameterName, Time value) throws OdaException {}

	public void setTime(int parameterId, Time value) throws OdaException {}

	public void setTimestamp(String parameterName, Timestamp value) throws OdaException {}

	public void setTimestamp(int parameterId, Timestamp value) throws OdaException {}

	public void setBoolean(String parameterName, boolean value) throws OdaException {}

	public void setBoolean(int parameterId, boolean value) throws OdaException {}

	public void setObject(String parameterName, Object value) throws OdaException {}

	public void setObject(int parameterId, Object value) throws OdaException {}

	public void setNull(String parameterName) throws OdaException {}

	public void setNull(int parameterId) throws OdaException {}

	public int findInParameter(String parameterName) throws OdaException {
		return 0;
	}

	public IParameterMetaData getParameterMetaData() throws OdaException {
		return new ParameterMetaData();
	}

	public void setSortSpec(SortSpec sortBy) throws OdaException {
		throw new UnsupportedOperationException();
	}

	public SortSpec getSortSpec() throws OdaException {
		return null;
	}

	public void setSpecification(QuerySpecification querySpec) throws OdaException, UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public QuerySpecification getSpecification() {
		return null;
	}

	public String getEffectiveQueryText() {
		return this.m_preparedText;
	}

	public void cancel() throws OdaException, UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}
}
