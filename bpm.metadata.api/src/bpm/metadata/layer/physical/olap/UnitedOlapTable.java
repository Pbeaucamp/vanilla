package bpm.metadata.layer.physical.olap;

import java.util.List;

import bpm.metadata.layer.physical.IColumn;
import bpm.metadata.layer.physical.IConnection;
import bpm.metadata.layer.physical.ITable;

public class UnitedOlapTable implements ITable {

	public static final String MEASURE_TABLE_NAME = "[Measures]";
	
	private String name;
	private List<IColumn> columns;
	private UnitedOlapConnection connection;
	
	public UnitedOlapTable() {}
	
	public UnitedOlapTable(String name, UnitedOlapConnection connection) {
		this.name = name;
		this.connection = connection;
	}
	
	@Override
	public List<IColumn> getColumns() {
		if(columns == null || columns.isEmpty()) {
			try {
				columns = connection.getColumns(this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return columns;
	}

	@Override
	public IConnection getConnection() {
		return connection;
	}

	@Override
	public IColumn getElementNamed(String originName) {
		
		for(IColumn col : getColumns()) {
			if(col.getName().equals(originName)) {
				return col;
			}
		}
		
		return null;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isQuery() {
		return false;
	}

	@Override
	public String getNameWithoutShema() {
		// TODO Auto-generated method stub
		return null;
	}

}
