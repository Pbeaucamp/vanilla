package bpm.gateway.runtime2.transformations.inputs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import com.mongodb.Bytes;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.server.database.nosql.mongodb.MongoDbConnection;
import bpm.gateway.core.server.userdefined.Variable.SQLTYPE;
import bpm.gateway.core.transformations.inputs.MongoDbInputStream;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.internal.RowFactory;

public class RunMongoDbInput extends RuntimeStep{

	
	private HashMap<String, String> columnNames;
	private Iterator<DBObject> result;
	private int j = 0;
	private String Host, Name;
	private int Port;
	
	public RunMongoDbInput(Transformation transformation, int bufferSize){
		super(null, transformation, bufferSize);
	}
	
	private void createResources(String tableName, String columnFamily, MongoDbConnection connection) throws Exception {
		//We init the list of columns we have to get
		columnNames = new LinkedHashMap<String, String>();
		StreamDescriptor descriptor = this.getTransformation().getDescriptor(getTransformation());
		for(StreamElement el : descriptor.getStreamElements()){
			columnNames.put(el.name, el.typeName);
		}
		
		Host = connection.getHost();
		Port = Integer.parseInt(connection.getPort());
		Name = connection.getName();

		DBCollection coll = new Mongo(Host,Port).getDB(tableName).getCollection(columnFamily);
			
		DBCursor cursor = coll.find();
		result = cursor.iterator();
		

	}

	@Override
	public void init(Object adapter) throws Exception {
		MongoDbInputStream model = (MongoDbInputStream)getTransformation();
		MongoDbConnection c = (MongoDbConnection)model.getServer().getCurrentConnection(null);
		createResources(model.getTableName(), model.getDocumentFamily(),c);
		
		isInited = true;
	}

	@Override
	public void performRow() throws Exception {
		if (result == null){
			throw new Exception("No ResultSet defined");
		}
		
		
		if (result.hasNext()){
			Row row = RowFactory.createRow(this);
			
			 DBObject d = result.next();
			
			 int i = 0;
			 for(String colName : columnNames.keySet()){
				
				 boolean found = false;
				 
					for(String kv : d.keySet()){

						if(colName.equals(MongoDbInputStream.KEY_DEFINITION)){
							if(!d.containsField(colName)){
								inserDataE(colName,row,j,d);
								j++;
								found = true;
							}
							
							break;
						}
						else {
							
							if(kv.equals(colName)){
								insertData(colName, d.get(colName), row, i);
								found = true; 
								break;
							}
						}
					}
					
					if(!found){
						row.set(i, null);
					}
					
					i++;
			 }
			 writeRow(row);
		}else{
			if (!areInputsAlive()){
				if (areInputStepAllProcessed()){
					if (inputEmpty()){
						setEnd();
					}
				}
			}
		}
		}

	private void inserDataE(String colName, Row row, int i, DBObject d) {
		row.set(0, i);
	}

	private void insertData(String colName, Object object, Row row, int i) {
		switch (SQLTYPE.getTypeFromValue(columnNames.get(colName))) {
		case INTEGER:
			row.set(i, Bytes.toInt(object));
			break;
		case VARCHAR:
			row.set(i, object);		
			break;
		default:
			row.set(i, object);		
			break;

		}
	}
	
	@Override
	public void releaseResources() {
		if (result != null){
			result = null;
			info(" Closed resultSet");
		}
		
		if (columnNames != null){
			columnNames = null;
			info(" Closed Connection");
		}
		
	}
}
