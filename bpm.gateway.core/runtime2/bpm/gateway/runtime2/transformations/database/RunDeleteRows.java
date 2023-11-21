package bpm.gateway.runtime2.transformations.database;

import java.awt.Point;
import java.util.HashMap;

import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.transformations.DeleteRows;
import bpm.gateway.core.transformations.outputs.DataBaseOutputStream;
import bpm.gateway.runtime2.transformations.outputs.RunDataBaseOutput;

public class RunDeleteRows extends RunDataBaseOutput{

	public RunDeleteRows(DeleteRows transformation, int bufferSize) {
		super(transformation, bufferSize);
		
	}

	@Override
	protected String prepareQuery(Transformation tr) throws Exception {
		StringBuffer insertionBaseSql = new StringBuffer();
		insertionBaseSql.append("delete from ");
		insertionBaseSql.append(((DataBaseOutputStream)getTransformation()).getTableName());
		insertionBaseSql.append(" where ");
		
		boolean isFirst = true;
		StreamDescriptor desc = getTransformation().getDescriptor(getTransformation());
		
		DeleteRows delete = (DeleteRows)getTransformation();
		
		HashMap<String, String> maps = delete.getMappingsFor(tr);
		for(String key : maps.keySet()){
			if (isFirst){
				isFirst = false;
			}
			else{
				insertionBaseSql.append(" AND ");
			}
			
			insertionBaseSql.append(maps.get(key) + " = ? ");
		}
		
		return insertionBaseSql.toString();
	}

	

}
