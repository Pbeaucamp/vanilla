package bpm.gateway.runtime2.transformations.outputs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

import bpm.gateway.core.Transformation;
import bpm.gateway.core.Trashable;
import bpm.gateway.core.server.database.nosql.INoSQLStream;
import bpm.gateway.core.server.database.nosql.IOutputNoSQL;
import bpm.gateway.core.server.database.nosql.hbase.HBaseConnection;
import bpm.gateway.core.transformations.outputs.HBaseOutputStream;
import bpm.gateway.runtime2.RuntimeEngine;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;

public class RunHBaseOutput extends RuntimeStep{
	
	private Configuration c;
	
	private String tableName;
	private String columnFamily;
	
	protected boolean handleError = false;
	protected RuntimeStep errorHandler;
	
	public RunHBaseOutput(Transformation transformation, int bufferSize) {
		super(null, transformation, bufferSize);
	}
	
	@Override
	public void init(Object adapter) throws Exception {

		HBaseOutputStream stream = (HBaseOutputStream)getTransformation();
		
		HBaseConnection c = (HBaseConnection)stream.getServer().getCurrentConnection(null);
		createResources(c, stream.getTableName(), stream.getColumnFamily());
		
		isInited = true;
	}
	
	private void createResources(HBaseConnection con, String tableName, String columnFamily) throws Exception{
		this.tableName = tableName;
		this.columnFamily = columnFamily;
		
        c = new Configuration();
        c.addResource(new Path(con.getConfigurationFileUrl()));

		IOutputNoSQL mapper = (IOutputNoSQL)getTransformation();

		for(Transformation input : mapper.getInputs()){
			if(!mapper.isIndexMap(input)){
				throw new Exception("The column index 'KEY' is not mapped. Please, do it before running.");
			}
		}
		
		if (((INoSQLStream)getTransformation()).isTruncate()){
			info(" Need truncate");
			HBaseAdmin admin = new HBaseAdmin(c);
			
			HTableDescriptor descriptor = admin.getTableDescriptor(Bytes.toBytes(tableName));
			
			admin.disableTable(tableName);
			admin.deleteTable(tableName);
			admin.createTable(descriptor);
			info(" Table " + tableName + " truncated ");
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
		
		try {
			table = new HTable(c, tableName);
		} catch(Exception e) {
		}
	}
	private HTable table;
	private List<Put> puts = new ArrayList<Put>();
	
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
				if(!maps.get(columnValue).equals(HBaseOutputStream.KEY_DEFINITION)){
					int indexValue = mapper.getMappingsIndexFor(input, columnValue);
					String columnName = mapper.getMappingsFor(input).get(columnValue);
					
					Object indexObject = row.get(indexKey);
					Object value = row.get(indexValue);
					if(indexObject instanceof String){
						Put put = new Put(Bytes.toBytes((String)indexObject));
						puts.add(insertData(put, columnFamily, columnName, value));
					}
					else if(indexObject instanceof Long){
						Put put = new Put(Bytes.toBytes((Long)indexObject));
						puts.add(insertData(put, columnFamily, columnName, value));
					}
					else if(indexObject instanceof Integer){
						Put put = new Put(Bytes.toBytes((Integer)indexObject));
						puts.add(insertData(put, columnFamily, columnName, value));
					}
					else if(indexObject instanceof Date){
						throw new Exception("Date is not supported for now. Don't use a column of type Date for your key.");
					}
				}
			}
		}
		
		if(puts.size() >= RuntimeEngine.MAX_ROWS) {
			table.batch(puts);
			puts.clear();
		}
		
		writeRow(row);
		if (areInputStepAllProcessed()){
			if (inputEmpty()){
				setEnd();
			}
		}
	}
	
	@Override
	protected synchronized void setEnd() {
		try {
			table.batch(puts);
		} catch(Exception e) {
		}
		try {
			puts.clear();
		} catch(Exception e) {
		}
		super.setEnd();
	}

	private Put insertData(Put put, String columnFamily, String columnName, Object value) throws Exception {
		if(value instanceof String){
			put.add(Bytes.toBytes(columnFamily), Bytes.toBytes(columnName), Bytes.toBytes((String)value));
			return put;
		}
		else if(value instanceof Long){
			put.add(Bytes.toBytes(columnFamily), Bytes.toBytes(columnName), Bytes.toBytes((Long)value));
			return put;
		}
		else if(value instanceof Integer){
			put.add(Bytes.toBytes(columnFamily), Bytes.toBytes(columnName), Bytes.toBytes((Integer)value));
			return put;
		}
		else if(value instanceof Date){
			throw new Exception("Date is not supported for now. Don't use a column of type Date.");
		}
		else {
			throw new Exception("The type of the Column " + columnName + " is not supported for now.");
		}
	}

	@Override
	public void releaseResources() {
		tableName = null;
		columnFamily = null;
		try {
			table.close();
		} catch(Exception e) {
		}
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
