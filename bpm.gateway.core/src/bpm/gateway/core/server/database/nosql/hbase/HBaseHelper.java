package bpm.gateway.core.server.database.nosql.hbase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

import bpm.gateway.core.DataStream;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.server.userdefined.Variable.SQLTYPE;
import bpm.gateway.core.transformations.inputs.HbaseInputStream;

public class HBaseHelper {

	/**
	 * Helper method to create a StreamDescript from an CQL Query
	 * if the given server is null, the Descriptor will be empty
	 * if the Sql query is invalid an exception is thrown
	 * @param server
	 * @param query
	 * @return
	 */
	public static StreamDescriptor buildDescriptor(String tableName, String columnFamily, DataStream stream) throws Exception {

		if(stream == null || stream.getServer() == null || stream.getServer().getCurrentConnection(null) == null){
			return new DefaultStreamDescriptor();
		}
		
		HBaseConnection con = (HBaseConnection)stream.getServer().getCurrentConnection(null);
		
        Configuration c = new Configuration();
        c.addResource(new Path(con.getConfigurationFileUrl()));
		
		HTable table = new HTable(c, tableName);
		
		Scan s = new Scan();
		s.addFamily(Bytes.toBytes(columnFamily));
		
		ResultScanner result = table.getScanner(s);
		
		/*
		 * we create the DataStreamDescriptor
		 */
		DefaultStreamDescriptor dsd = new DefaultStreamDescriptor();
		Exception thrown = null;
		try {

			Iterator<Result> it = result.iterator();
			boolean first = true;
			while(it.hasNext() && first){
	    		
				Result d =  it.next();
				
				Cell[] keyValues = d.rawCells();
				
				//We add a column which will be representing the KEY column
				StreamElement key = new StreamElement();
				key.className = null;
				key.name = HbaseInputStream.KEY_DEFINITION;
				key.tableName = tableName;
				key.transfoName = stream.getName();
				key.originTransfo = stream.getName();
				key.typeName = null;

				dsd.addColumn(key);
				
				for(Cell kv : keyValues){
					StreamElement e = new StreamElement();
					e.className = null;
					byte[] qualifier = Arrays.copyOfRange(kv.getQualifierArray(), kv.getQualifierOffset(), kv.getQualifierOffset() + kv.getQualifierLength());
					e.name = Bytes.toString(qualifier);
					e.tableName = tableName;
					e.transfoName = stream.getName();
					e.originTransfo = stream.getName();
					e.typeName = null;

					dsd.addColumn(e);
				}
				
				first = false;
			}

			if(first) {
				StreamElement key = new StreamElement();
				key.className = null;
				key.name = HbaseInputStream.KEY_DEFINITION;
				key.tableName = tableName;
				key.transfoName = stream.getName();
				key.typeName = null;

				dsd.addColumn(key);
			}
		}catch(Exception e){
			thrown = e;
		}
				
		
		
		if (thrown != null){
			throw new ServerException("Error while getting DataStreamDescriptor for " + stream.getName(), thrown, null);
		}

		return dsd;
	}

	public static boolean testConnection(HBaseConnection con) throws Exception {

        Configuration c = new Configuration();
        c.addResource(new Path(con.getConfigurationFileUrl()));
        
		HBaseAdmin admin = new HBaseAdmin(c);
		
		return admin.isMasterRunning();
	}

	public static void truncate(HBaseConnection con, String tableName, String columnFamily) throws Exception {
        Configuration c = new Configuration();
        c.addResource(new Path(con.getConfigurationFileUrl()));
        
		HBaseAdmin admin = new HBaseAdmin(c);
		
		HTableDescriptor descriptor = admin.getTableDescriptor(Bytes.toBytes(tableName));
		
		admin.disableTable(tableName);
		admin.deleteTable(tableName);
		admin.createTable(descriptor);
	}

	public static List<List<Object>> getValues(HbaseInputStream model, int limit) throws Exception {
		Exception thrown = null;
		
		List<List<Object>> values = new ArrayList<List<Object>>();
		
		try {
			
			HashMap<String, String> columnNames = new LinkedHashMap<String, String>();
			StreamDescriptor descriptor = model.getDescriptor(null);
			for(StreamElement el : descriptor.getStreamElements()){
				columnNames.put(el.name, el.typeName);
			}
			
			HBaseConnection con = (HBaseConnection)model.getServer().getCurrentConnection(null);

	        Configuration c = new Configuration();
	        c.addResource(new Path(con.getConfigurationFileUrl()));
			
			HTable table = new HTable(c, model.getTableName());
			Scan s = new Scan();
			s.addFamily(Bytes.toBytes(model.getColumnFamily()));

			ResultScanner result = table.getScanner(s);
			
			Iterator<Result> it = result.iterator();
			while(it.hasNext() && limit >= 0){
				Result d =  it.next();
				
				Cell[] cells = d.rawCells();
				List<Object> row = new ArrayList<Object>();

    			
	    		for(String colName : columnNames.keySet()){
	    			
					boolean found = false;
					
					for(Cell cell : cells){
						
						if(colName.equals(HbaseInputStream.KEY_DEFINITION)){
							byte[] value = Arrays.copyOfRange(cell.getRowArray(), cell.getRowOffset(), cell.getRowOffset() + cell.getRowLength());
							row.add(getValue(columnNames.get(colName), value));
							found = true;
							break;
						}
						else {
							byte[] qualifier = Arrays.copyOfRange(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierOffset() + cell.getQualifierLength());
							String colTmp = Bytes.toString(qualifier);
							if(colTmp.equals(colName)){
								byte[] value = Arrays.copyOfRange(cell.getValueArray(), cell.getValueOffset(), cell.getValueOffset() + cell.getValueLength());
								row.add(getValue(columnNames.get(colName), value));
								found = true;
								break;
							}
						}
					}
					
					if(!found){
						row.add(null);
					}
				}
				
				values.add(row);
				
				limit--;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			thrown = e;
		} catch (Exception e) {
			e.printStackTrace();
			thrown = e;
		}

		if (thrown != null){
			throw new ServerException("Error while getting data for " + model.getName(), thrown, null);
		}
		
		return values;
	}
	
	private static Object getValue(String columnType, byte[] value) throws ServerException{
		SQLTYPE type = null;
		try {
			type = SQLTYPE.getTypeFromValue(columnType);
		} catch (Exception e) {
			throw new ServerException("", e, null);
		}
		
		if(value != null){
			
			switch (type) {
			case INTEGER:
				return Bytes.toInt(value);
				
			case LONG:
				return Bytes.toLong(value);
				
//			case DATE:
//				row.set(i, col.getValue().get);
//				break;
				
			case DOUBLE:
				return Bytes.toDouble(value);
			
			case VARCHAR:
				return Bytes.toString(value);
				
			default:
				return Bytes.toString(value);
			}
		}
		else {
			return(null);
		}
	}
}
