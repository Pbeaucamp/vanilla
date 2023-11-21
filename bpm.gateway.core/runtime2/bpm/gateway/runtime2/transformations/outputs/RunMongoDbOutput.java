package bpm.gateway.runtime2.transformations.outputs;

import java.util.Date;
import java.util.HashMap;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;

import bpm.gateway.core.Transformation;
import bpm.gateway.core.Trashable;
import bpm.gateway.core.server.database.nosql.mongodb.MongoDbConnection;
import bpm.gateway.core.transformations.outputs.MongoDbOutputStream;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.vanilla.platform.core.IRepositoryContext;

public class RunMongoDbOutput extends RuntimeStep{

	public RunMongoDbOutput(IRepositoryContext repositoryCtx,
			Transformation transformation, int bufferSize) {
		super(repositoryCtx, transformation, bufferSize);
	}

	private DBCollection coll;
	private String tableName, columnFamily;
	private String Host,table;
	private int Port;
	
	protected boolean handleError = false;
	protected RuntimeStep errorHandler;


	@Override
	public void init(Object adapter) throws Exception {
		MongoDbOutputStream stream = (MongoDbOutputStream)getTransformation();
		
		MongoDbConnection c = (MongoDbConnection)stream.getServer().getCurrentConnection(null);
		createResources( stream.getTableName(), stream.getColumnFamily(),c);
		
		isInited = true;		
	}

	private void createResources( String tableName, String columnFamily, MongoDbConnection connection) throws Exception{
		this.tableName = tableName;
		this.columnFamily = columnFamily;
		Host = connection.getHost();
		Port = Integer.parseInt(connection.getPort());
				
		if (((MongoDbOutputStream)getTransformation()).isTruncate()){
			
				Mongo mongo = new Mongo(Host,Port);
			    DB table = mongo.getDB(tableName);
				DBCollection collection = table.getCollection(columnFamily);
				DBCursor cursor = collection.find();
				while (cursor.hasNext()) {
					collection.remove(cursor.next());
				}
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
		
			if(coll == null && table != tableName){
				table = tableName;
				coll = new Mongo(Host,Port).getDB(tableName).getCollection(columnFamily);
			}
		
		
		MongoDbOutputStream mapper = (MongoDbOutputStream)getTransformation();
		BasicDBObject doc = null;
		for(Transformation input : mapper.getInputs()){
			
			
			HashMap<String, String> maps = mapper.getMappingsFor(input);
			
			doc = new BasicDBObject();
			for(String columnValue : maps.keySet()){
				if(!maps.get(columnValue).equals(MongoDbOutputStream.KEY_DEFINITION)){
					int indexValue = mapper.getMappingsIndexFor(input, columnValue);
					String columnName = mapper.getMappingsFor(input).get(columnValue);
					

					Object value = row.get(indexValue);
					if(value instanceof String){
						doc.put(columnName, (String)value);
					}
					else if(value instanceof Long){
						doc.put(columnName, (Long)value);
					}
					else if(value instanceof Integer){
						doc.put(columnName, (Integer)value);
					}else if(value instanceof Double){
						doc.put(columnName, (Double)value);
					}
					else if(value instanceof Date){
						throw new Exception("Date is not supported for now. Don't use a column of type Date.");
					}
					
				}
				
			}
			
		}
		coll.save(doc);
		
		if (areInputStepAllProcessed()){
			if (inputEmpty()){
				setEnd();
			}
		}
	}

	@Override
	public void releaseResources() {
	}

	@Override
	protected void writeRow(Row row) throws InterruptedException {
		super.writeRow(row);
		for(RuntimeStep r : getOutputs()){
			if (r != errorHandler){
				r.insertRow(row, this);	
			}
		}	
		writedRows++;
	}
}
