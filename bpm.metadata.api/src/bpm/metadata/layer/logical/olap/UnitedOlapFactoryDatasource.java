package bpm.metadata.layer.logical.olap;

import bpm.metadata.layer.physical.ITable;
import bpm.metadata.layer.physical.olap.UnitedOlapConnection;

public class UnitedOlapFactoryDatasource {

	public static UnitedOlapDatasource createUnitedOlapDatasource(UnitedOlapConnection connection) throws Exception {
		
		UnitedOlapDatasource ds = new UnitedOlapDatasource(connection);
		for(ITable table : connection.getTables()) {
			ds.add(table);
		}
		
		
		
		return ds;
	}
	
}
