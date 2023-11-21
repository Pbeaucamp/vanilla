package bpm.gateway.runtime2.transformations.outputs;

import java.util.Date;
import java.util.HashMap;

import me.prettyprint.cassandra.model.AllOneConsistencyLevelPolicy;
import me.prettyprint.cassandra.serializers.DateSerializer;
import me.prettyprint.cassandra.serializers.DoubleSerializer;
import me.prettyprint.cassandra.serializers.IntegerSerializer;
import me.prettyprint.cassandra.serializers.LongSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.FailoverPolicy;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import bpm.gateway.core.DataStream;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.Trashable;
import bpm.gateway.core.server.database.nosql.INoSQLStream;
import bpm.gateway.core.server.database.nosql.IOutputNoSQL;
import bpm.gateway.core.server.database.nosql.cassandra.CassandraConnection;
import bpm.gateway.core.transformations.outputs.CassandraOutputStream;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;

public class RunCassandraOutput extends RuntimeStep{
	
	private Keyspace ksp;
	private String columnFamily;
	
	protected boolean handleError = false;
	protected RuntimeStep errorHandler;
	
	public RunCassandraOutput(Transformation transformation, int bufferSize) {
		super(null, transformation, bufferSize);
	}
	
	@Override
	public void init(Object adapter) throws Exception {

		CassandraConnection c = (CassandraConnection)((DataStream)getTransformation()).getServer().getCurrentConnection(null);
		createResources(c);
		
		isInited = true;
	}
	
	private void createResources(CassandraConnection c) throws Exception{
		Cluster myCluster = HFactory.getOrCreateCluster("Test_Cluster_" + new Object().hashCode(), 
				c.getHost() + ":" + c.getPort());
		
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
		columnFamily = ((INoSQLStream)getTransformation()).getTableName();

		IOutputNoSQL mapper = (IOutputNoSQL)getTransformation();

		for(Transformation input : mapper.getInputs()){
			if(!mapper.isIndexMap(input)){
				throw new Exception("The column index 'KEY' is not mapped. Please, do it before running.");
			}
		}
		
		if (((INoSQLStream)getTransformation()).isTruncate()){
			info(" Need truncate");
			myCluster.truncate(c.getKeyspace(), columnFamily);
			info(" Column Family "  + columnFamily + " from keyspace " + c.getKeyspace() + " truncated ");
		}
		
		if (((Trashable)getTransformation()).getTrashTransformation() != null){
			handleError = true;
			
			for(RuntimeStep rs : getOutputs()){
				if (rs.getTransformation() == ((Trashable)getTransformation()).getTrashTransformation()){
					errorHandler = rs;
					break;
				}
			}
		}
		
		info(" created prepared Statement for commit");
		info(" inited");
	}
	
	@Override
	public void performRow() throws Exception {
		
		if (areInputStepAllProcessed()){
			if (inputEmpty()){
				setEnd();
			}
		}
		
		if (isEnd() && inputEmpty()){
			return;
		}
		
		if (!isEnd() && inputEmpty()){
			try {
				Thread.sleep(10);
			}
			catch (InterruptedException e) {
				Thread.currentThread().interrupt(); // restore interrupted status
			}
			return;
		}
		
		
		Row row = readRow();
		
		if (row == null){
			return;
		}
		
		IOutputNoSQL mapper = (IOutputNoSQL)getTransformation();

		for(Transformation input : mapper.getInputs()){
			int indexKey = mapper.getIndexKey(input);
			
			HashMap<String, String> maps = mapper.getMappingsFor(input);
			
			for(String columnValue : maps.keySet()){
				if(!maps.get(columnValue).equals(CassandraOutputStream.KEY_DEFINITION)){
					int indexValue = mapper.getMappingsIndexFor(input, columnValue);
					String columnName = mapper.getMappingsFor(input).get(columnValue);
					
					Object indexObject = row.get(indexKey);
					Object value = row.get(indexValue);
					if(indexObject instanceof String){
						Mutator<String> mutator = HFactory.createMutator(ksp, StringSerializer.get());
						String index = (String)indexObject;
						insertData(mutator, index, columnName, value);
					}
					else if(indexObject instanceof Long){
						Mutator<Long> mutator = HFactory.createMutator(ksp, LongSerializer.get());
						Long index = (Long)indexObject;
						insertData(mutator, index, columnName, value);
					}
					else if(indexObject instanceof Integer){
						Mutator<Integer> mutator = HFactory.createMutator(ksp, IntegerSerializer.get());
						Integer index = (Integer)indexObject;
						insertData(mutator, index, columnName, value);
					}
					else if(indexObject instanceof Date){
						Mutator<String> mutator = HFactory.createMutator(ksp, StringSerializer.get());
						String index = (String)indexObject;
						insertData(mutator, index, columnName, value);
					}
					else if(indexObject instanceof Double){
						Mutator<Double> mutator = HFactory.createMutator(ksp, DoubleSerializer.get());
						Double index = (Double)indexObject;
						insertData(mutator, index, columnName, value);
					}
				}
			}
		}
		
		writeRow(row);
		if (areInputStepAllProcessed()){
			if (inputEmpty()){
				setEnd();
			}
		}
	}

	private <T> void insertData(Mutator<T> mutator, T index, String columnName, Object columnValue) {
		if(columnValue instanceof String){
			mutator.insert(index, columnFamily, HFactory.createColumn(columnName, (String)columnValue, StringSerializer.get(), StringSerializer.get()));
		}
		else if(columnValue instanceof Long){
			mutator.insert(index, columnFamily, HFactory.createColumn(columnName, (Long)columnValue, StringSerializer.get(), LongSerializer.get()));
		}
		else if(columnValue instanceof Integer){
			mutator.insert(index, columnFamily, HFactory.createColumn(columnName, (Integer)columnValue, StringSerializer.get(), IntegerSerializer.get()));
		}
		else if(columnValue instanceof Date){
			mutator.insert(index, columnFamily, HFactory.createColumn(columnName, (Date)columnValue, StringSerializer.get(), DateSerializer.get()));
		}
		else if(columnValue instanceof Double){
			mutator.insert(index, columnFamily, HFactory.createColumn(columnName, (Double)columnValue, StringSerializer.get(), DoubleSerializer.get()));

		}
	}

	@Override
	public void releaseResources() {
		ksp = null;
		columnFamily = null;
	}
	
	@Override
	protected void writeRow(Row row) throws InterruptedException{
		for(RuntimeStep r : getOutputs()){
			if (r != errorHandler){
				r.insertRow(row, this);	
			}
		}
		
		writedRows++;
	}
	
	protected void writeErrorRow(Row row) throws InterruptedException{
		if (errorHandler != null){
			errorHandler.insertRow(row, this);
			writedRows++;
		}
	}

}
