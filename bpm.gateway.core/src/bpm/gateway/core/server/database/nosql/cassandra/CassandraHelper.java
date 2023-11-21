package bpm.gateway.core.server.database.nosql.cassandra;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import me.prettyprint.cassandra.model.AllOneConsistencyLevelPolicy;
import me.prettyprint.cassandra.model.CqlQuery;
import me.prettyprint.cassandra.model.CqlRows;
import me.prettyprint.cassandra.serializers.ByteBufferSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.FailoverPolicy;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.QueryResult;
import bpm.gateway.core.Activator;
import bpm.gateway.core.DataStream;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.server.userdefined.Variable.SQLTYPE;
import bpm.gateway.core.transformations.inputs.CassandraInputStream;

public class CassandraHelper {
	
	/**
	 * Helper method to create a StreamDescript from an CQL Query
	 * if the given server is null, the Descriptor will be empty
	 * if the Sql query is invalid an exception is thrown
	 * @param server
	 * @param query
	 * @return
	 */
	public static StreamDescriptor buildDescriptor(DataStream stream) throws Exception {
		/*
		 * we create the DataStreamDescriptor
		 */
		DefaultStreamDescriptor dsd = new DefaultStreamDescriptor();
		Exception thrown = null;
		try{
			
			if(stream.getDefinition() == null || stream.getDefinition().isEmpty()){
				return new DefaultStreamDescriptor();
			}
			
			/* we modify the query execute it faster as possible
			* because all we want is the MetaData Information
			*/
			String query;
			try {
				if(stream.getDocument() != null){
					query = new String(stream.getDocument().getStringParser().getValue(stream.getDocument(), stream.getDefinition()));
				}
				else {
					//The box is not ready, we return for now
					return new DefaultStreamDescriptor();
				}
			} catch (Exception e1) {
				throw new Exception("Unable to parse query:" + stream.getDefinition() + ", reason : " + e1.getMessage(), e1);
			}
			
			int fromIndex = query.toLowerCase().indexOf("from");
	        String tempS = query.toLowerCase();
	        int offset = fromIndex;
	        while (fromIndex > 0 && tempS.charAt(fromIndex - 1) != ' ' 
	        	&& (fromIndex + 4 < tempS.length()) && tempS.charAt(fromIndex + 4) != ' ') {
		          tempS = tempS.substring(fromIndex + 4, tempS.length());
		          fromIndex = tempS.indexOf("from");
		          offset += (4 + fromIndex);
	        }
	        
	        fromIndex = offset;
	        
	        if (fromIndex < 0) {
	        	return new DefaultStreamDescriptor(); // no from clause
	        }
			
			String colFamName = query.substring(fromIndex + 4, query.length()).trim();
	        if (colFamName.indexOf(' ') > 0) {
	        	colFamName = colFamName.substring(0, colFamName.indexOf(' '));
	        } 
	        else {
	        	colFamName = colFamName.replace(";", "");
	        }
	        
	        if (colFamName.length() == 0) {
	        	return new DefaultStreamDescriptor(); // no column family specified
	        }
			
			if (query.toLowerCase().contains(" where ")){
				query = query.substring(0, query.toLowerCase().indexOf(" where " ) + 7)+ " 1=0 AND "  + query.substring(query.toLowerCase().indexOf(" where " ) + 7 ) ;
			}
			
			if (query.toLowerCase().contains(" limit ")){
				query = query.substring(0, query.toLowerCase().indexOf(" limit " ) + 7) + " 1 ";
			}
			else {
				boolean containsGroupBy = query.toLowerCase().indexOf(" group by ") != -1 ;
				boolean orderGroupBy = query.toLowerCase().indexOf(" order by ") != -1 ;
				boolean containWhere =  query.toLowerCase().indexOf(" where ") != -1;
				
				if (containsGroupBy){
					query = query.substring(0, query.toLowerCase().indexOf(" group by " )) + " limit 1"  + query.substring(query.toLowerCase().indexOf(" group by " )) ;
				}
				else if (orderGroupBy){
					query = query.substring(0, query.toLowerCase().indexOf(" order by " )) + " limit 1 "  + query.substring(query.toLowerCase().indexOf(" order by " )) ;
				}
				else if (containWhere){
					query = query.substring(0, query.toLowerCase().indexOf(" where " )) + " limit 1 "  + query.substring(query.toLowerCase().indexOf(" where " )) ;
				}
				else{
					query += " limit 1";
				}
				
				
			}
			
			Activator.getLogger().info("Query = " + query);
			
			if(stream == null || stream.getServer() == null){
				return new DefaultStreamDescriptor();
			}
			
			CassandraConnection con = (CassandraConnection)stream.getServer().getCurrentConnection(null);
			
			Cluster myCluster = HFactory.getOrCreateCluster("TestConnection_" + new Object().hashCode(), con.getHost() + ":" + con.getPort());

			Keyspace ksp = null;
			if(con.getUsername() != null && !con.getUsername().isEmpty() && con.getPassword() != null && !con.getPassword().isEmpty()){
				HashMap<String, String> accessMap = new HashMap<String, String>();
				accessMap.put("username", con.getUsername());
				accessMap.put("password", con.getPassword());

				ksp = HFactory.createKeyspace(con.getKeyspace(), myCluster, new AllOneConsistencyLevelPolicy(),
						FailoverPolicy.ON_FAIL_TRY_ALL_AVAILABLE, accessMap);
			}
			else {
				ksp = HFactory.createKeyspace(con.getKeyspace(), myCluster);
			}
			
			CqlQuery<String, String, ByteBuffer> cqlQuery = new CqlQuery<String, String, ByteBuffer>(ksp, StringSerializer.get(), StringSerializer.get(), ByteBufferSerializer.get());
	    	cqlQuery.setQuery(query);

	    	QueryResult<CqlRows<String, String, ByteBuffer>> results = cqlQuery.execute();
			
			if(results.get() != null){
				for(Row<String, String, ByteBuffer> rs : results.get()){
					
					ColumnSlice<String, ByteBuffer> col = rs.getColumnSlice();
		    		for(HColumn<String, ByteBuffer> c : col.getColumns()){
						StreamElement e = new StreamElement();
						e.className = null;
						e.name = c.getName();
						e.tableName = colFamName;
						e.transfoName = stream.getName();
						e.originTransfo = stream.getName();
						e.typeName = null;

						dsd.addColumn(e);
		    		}
				}
			}

		}catch(SQLException e){
			thrown = e;
		}
				
		
		
		if (thrown != null){
			throw new ServerException("Error while getting DataStreamDescriptor for " + stream.getName(), thrown, null);
		}

		return dsd;
	}

	public static boolean testConnection(CassandraConnection con) throws Exception {
		Cluster myCluster = HFactory.getOrCreateCluster("TestConnection_" + new Object().hashCode(), con.getHost() + ":" + con.getPort());

		KeyspaceDefinition kspDef = myCluster.describeKeyspace(con.getKeyspace());
		
		if(kspDef != null){
			return kspDef.getCfDefs() != null && !kspDef.getCfDefs().isEmpty();
		}
	
		return false;
	}

	public static void truncate(CassandraConnection con, String columnFamily) {
		Cluster myCluster = HFactory.getOrCreateCluster("TestConnection_" + new Object().hashCode(), con.getHost() + ":" + con.getPort());
		myCluster.truncate(con.getKeyspace(), columnFamily);
	}

	public static List<List<Object>> getValues(CassandraInputStream model, int limit) throws Exception {
		Exception thrown = null;
		
		List<List<Object>> values = new ArrayList<List<Object>>();
		
		CassandraConnection c = (CassandraConnection)model.getServer().getCurrentConnection(null);
		
		Cluster myCluster = HFactory.getOrCreateCluster("Test_Cluster_" + new Object().hashCode(), 
				c.getHost() + ":" + c.getPort());
		
		Keyspace ksp = null;
		if(c.getUsername() != null && !c.getUsername().isEmpty() && c.getPassword() != null && !c.getPassword().isEmpty()){
			HashMap<String, String> accessMap = new HashMap<String, String>();
			accessMap.put("username", c.getUsername());
			accessMap.put("password", c.getPassword());

			ksp = HFactory.createKeyspace(c.getKeyspace(), myCluster, new AllOneConsistencyLevelPolicy(),
					FailoverPolicy.ON_FAIL_TRY_ALL_AVAILABLE, accessMap);
		}
		else {
			ksp = HFactory.createKeyspace(c.getKeyspace(), myCluster);
		}
		
		try {
			String query = model.getDocument().getStringParser().getValue(model.getDocument(), model.getCQLDefinition());
			
			int fromIndex = query.toLowerCase().indexOf("from");
	        String tempS = query.toLowerCase();
	        int offset = fromIndex;
	        while (fromIndex > 0 && tempS.charAt(fromIndex - 1) != ' ' 
	        	&& (fromIndex + 4 < tempS.length()) && tempS.charAt(fromIndex + 4) != ' ') {
		          tempS = tempS.substring(fromIndex + 4, tempS.length());
		          fromIndex = tempS.indexOf("from");
		          offset += (4 + fromIndex);
	        }
	        
	        fromIndex = offset;
	        
	        if (fromIndex < 0) {
	        	return null; // no from clause
	        }
			
			String colFamName = query.substring(fromIndex + 4, query.length()).trim();
	        if (colFamName.indexOf(' ') > 0) {
	        	colFamName = colFamName.substring(0, colFamName.indexOf(' '));
	        } 
	        else {
	        	colFamName = colFamName.replace(";", "");
	        }
	        
	        if (colFamName.length() == 0) {
	        	return null; // no column family specified
	        }
			
			if (query.toLowerCase().contains(" limit ")){
				query = query.substring(0, query.toLowerCase().indexOf(" limit " ) + 7) + limit;
			}
			else {
				boolean containsGroupBy = query.toLowerCase().indexOf(" group by ") != -1 ;
				boolean orderGroupBy = query.toLowerCase().indexOf(" order by ") != -1 ;
				boolean containWhere =  query.toLowerCase().indexOf(" where ") != -1;
				
				if (containsGroupBy){
					query = query.substring(0, query.toLowerCase().indexOf(" group by " )) + " limit " + limit + " "  + query.substring(query.toLowerCase().indexOf(" group by " )) ;
				}
				else if (orderGroupBy){
					query = query.substring(0, query.toLowerCase().indexOf(" order by " )) + " limit " + limit + " "  + query.substring(query.toLowerCase().indexOf(" order by " )) ;
				}
				else if (containWhere){
					query = query.substring(0, query.toLowerCase().indexOf(" where " )) + " limit " + limit + " "  + query.substring(query.toLowerCase().indexOf(" where " )) ;
				}
				else{
					query += " limit " + limit;
				}
			}
			
			HashMap<String, String> columnNames = new LinkedHashMap<String, String>();
			StreamDescriptor descriptor = model.getDescriptor(null);
			for(StreamElement el : descriptor.getStreamElements()){
				columnNames.put(el.name, el.typeName);
			}
				
			CqlQuery<String, String, ByteBuffer> cqlQuery = new CqlQuery<String, String, ByteBuffer>(
					ksp, new StringSerializer(), new StringSerializer(), new ByteBufferSerializer());
			cqlQuery.setQuery(query);
			
			QueryResult<CqlRows<String, String, ByteBuffer>> result = cqlQuery.execute();
			Iterator<me.prettyprint.hector.api.beans.Row<String, String, ByteBuffer>> resultSet = result.get().iterator();
			
			while(resultSet.hasNext()){
				me.prettyprint.hector.api.beans.Row<String, String, ByteBuffer> rowTmp = resultSet.next();
	    		ColumnSlice<String, ByteBuffer> slice = rowTmp.getColumnSlice();

    			List<Object> row = new ArrayList<Object>();
    			
	    		for(String columnName : columnNames.keySet()){
	    			HColumn<String, ByteBuffer> col = slice.getColumnByName(columnName);
	    			
	    			SQLTYPE type = null;
	    			try {
	    				type = SQLTYPE.getTypeFromValue(columnNames.get(columnName));
	    			} catch (Exception e) {
	    				throw new ServerException("", e, null);
					}
	    			if(col != null && col.getValue() != null){
	    				
	    				switch (type) {
						case INTEGER:
		    				row.add(col.getValue().getInt());
							break;
							
						case LONG:
		    				row.add(col.getValue().getLong());
							break;
							
//						case DATE:
//		    				row.set(i, col.getValue().get);
//							break;
							
						case DOUBLE:
		    				row.add(col.getValue().getDouble());
							break;
						
						case VARCHAR:
		    				row.add(Charset.defaultCharset().decode(col.getValue()).toString());
							break;
							
						default:
		    				row.add(Charset.defaultCharset().decode(col.getValue()).toString());
							break;
						}
	    			}
	    			else {
	    				row.add(null);
	    			}
	    		}
				values.add(row);
			}
		} catch (ServerException e1) {
			throw new ServerException("The column are not typed. Typed them first...", e1, null);
		} catch (Exception e) {
			thrown = e;
		}
		
		if (thrown != null){
			throw new ServerException("Error while getting data for " + model.getName(), thrown, null);
		}
		
		return values;
	}
}
