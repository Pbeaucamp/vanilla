package bpm.gateway.runtime2.transformations.inputs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.server.database.nosql.hbase.HBaseConnection;
import bpm.gateway.core.server.userdefined.Variable.SQLTYPE;
import bpm.gateway.core.transformations.inputs.HbaseInputStream;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.internal.RowFactory;

public class RunHBaseInput extends RuntimeStep {
	
	private Iterator<Result>  result;
	private HashMap<String, String> columnNames;

	public RunHBaseInput(Transformation transformation, int bufferSize) {
		super(null, transformation, bufferSize);
	}

	@Override
	public void init(Object adapter) throws Exception {
		HbaseInputStream model = (HbaseInputStream)getTransformation();
		HBaseConnection c = (HBaseConnection)model.getServer().getCurrentConnection(null);
		createResources(model.getTableName(), model.getColumnFamily(), c);
		
		isInited = true;
	}

	private void createResources(String tableName, String columnFamily, HBaseConnection con) throws Exception {
		//We init the list of columns we have to get
		columnNames = new LinkedHashMap<String, String>();
		StreamDescriptor descriptor = this.getTransformation().getDescriptor(getTransformation());
		for(StreamElement el : descriptor.getStreamElements()){
			columnNames.put(el.name, el.typeName);
		}
		
        Configuration c = new Configuration();
        c.addResource(new Path(con.getConfigurationFileUrl()));
		
		HTable table = new HTable(c, tableName);
		
		Scan s = new Scan();
		s.addFamily(Bytes.toBytes(columnFamily));
		
		ResultScanner res = table.getScanner(s);
		result = res.iterator();
	}

	@Override
	public void performRow() throws Exception {
		if (result == null){
			throw new Exception("No ResultSet defined");
		}
		
		
		if (result.hasNext()){
			Row row = RowFactory.createRow(this);
			
			Result d =  result.next();
			
			KeyValue[] kValues = d.raw();

    		int i = 0;
			for(String colName : columnNames.keySet()){
				
				boolean found = false;
				for(KeyValue kv : kValues){

					if(colName.equals(HbaseInputStream.KEY_DEFINITION)){
						insertData(colName, kv.getRow(), row, i);
						found = true;
						break;
					}
					else {
						String colTmp = Bytes.toString(kv.getQualifier());
						if(colTmp.equals(colName)){
							insertData(colName, kv.getValue(), row, i);
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
	
	private void insertData(String colName, byte[] value, Row row, int i) {
		switch (SQLTYPE.getTypeFromValue(columnNames.get(colName))) {
		case INTEGER:
			row.set(i, Bytes.toInt(value));
			break;
			
		case LONG:
			row.set(i, Bytes.toLong(value));
			break;
			
//		case DATE:
//			row.set(i, col.getValue().get);
//			break;
			
		case DOUBLE:
			row.set(i, Bytes.toDouble(value));
			break;
		
		case VARCHAR:
			row.set(i, Bytes.toString(value));
			break;
			
		default:
			row.set(i, Bytes.toString(value));
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
