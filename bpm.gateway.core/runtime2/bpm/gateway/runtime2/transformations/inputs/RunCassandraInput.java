package bpm.gateway.runtime2.transformations.inputs;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

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
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.QueryResult;
import bpm.gateway.core.DataStream;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.server.database.nosql.cassandra.CassandraConnection;
import bpm.gateway.core.server.userdefined.Variable.SQLTYPE;
import bpm.gateway.core.transformations.inputs.CassandraInputStream;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.internal.RowFactory;

public class RunCassandraInput extends RuntimeStep{

	private Iterator<me.prettyprint.hector.api.beans.Row<String, String, ByteBuffer>> resultSet;
//	private int bufferSize;
	private HashMap<String, String> columnNames;
	
	public RunCassandraInput(Transformation transformation, int bufferSize) {
		super(null, transformation, bufferSize);
//		this.bufferSize = bufferSize;
	}
	
	@Override
	public void init(Object adapter) throws Exception {
		
		CassandraConnection c = (CassandraConnection)((DataStream)getTransformation()).getServer().getCurrentConnection(null);
		createResources(c);
		
		isInited = true;
	}

	private void createResources(CassandraConnection c) throws Exception{
		
		//We init the list of columns we have to get
		columnNames = new LinkedHashMap<String, String>();
		StreamDescriptor descriptor = this.getTransformation().getDescriptor(getTransformation());
		for(StreamElement el : descriptor.getStreamElements()){
			columnNames.put(el.name, el.typeName);
		}
		
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
		
		String query = getTransformation().getDocument().getStringParser().getValue(getTransformation().getDocument(), 
				((CassandraInputStream)getTransformation()).getCQLDefinition());
		
		CqlQuery<String, String, ByteBuffer> cqlQuery = new CqlQuery<String, String, ByteBuffer>(
				ksp, new StringSerializer(), new StringSerializer(), new ByteBufferSerializer());
		cqlQuery.setQuery(query);
		
		try{
			QueryResult<CqlRows<String, String, ByteBuffer>> result = cqlQuery.execute();
			resultSet = result.get().iterator();
			info(" Run query : " + query);
		}catch(Exception ex){
			error( " Failed to run query : " + query, ex);
		}
	}
	
	@Override
	public void performRow() throws Exception {
		if (resultSet == null){
			throw new Exception("No ResultSet defined");
		}
		
		
		if (resultSet.hasNext()){
			Row row = RowFactory.createRow(this);
			
			me.prettyprint.hector.api.beans.Row<String, String, ByteBuffer> rowTmp = resultSet.next();
    		ColumnSlice<String, ByteBuffer> slice = rowTmp.getColumnSlice();
    		
    		int i = 0;
    		for(String columnName : columnNames.keySet()){
    			HColumn<String, ByteBuffer> col = slice.getColumnByName(columnName);
    			if(col != null && col.getValue() != null){
    				
    				switch (SQLTYPE.getTypeFromValue(columnNames.get(columnName))) {
					case INTEGER:
	    				row.set(i, col.getValue().getInt());
						break;
						
					case LONG:
	    				row.set(i, col.getValue().getLong());
						break;
						
//					case DATE:
//	    				row.set(i, col.getValue().get);
//						break;
						
					case DOUBLE:
	    				row.set(i, col.getValue().getDouble());
						break;
					
					case VARCHAR:
	    				row.set(i, Charset.defaultCharset().decode(col.getValue()).toString());
						break;
						
					default:
	    				row.set(i, Charset.defaultCharset().decode(col.getValue()).toString());
						break;
					}
    			}
    			else {
    				row.set(i, null);
    			}
    			i++;
    		}
    		
			writeRow(row);
		}
		else{
			if (!areInputsAlive()){
				if (areInputStepAllProcessed()){
					if (inputEmpty()){
						setEnd();
					}
				}
			}
		}
	}

	@Override
	public void releaseResources() {
		if (resultSet != null){
			resultSet = null;
			info(" Closed resultSet");
		}
		
		if (columnNames != null){
			columnNames = null;
			info(" Closed Connection");
		}
	}
}
