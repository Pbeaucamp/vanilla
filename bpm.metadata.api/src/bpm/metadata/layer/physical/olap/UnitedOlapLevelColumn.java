package bpm.metadata.layer.physical.olap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.fa.api.olap.Level;
import bpm.fa.api.olap.unitedolap.UnitedOlapServiceProvider;
import bpm.metadata.layer.physical.IColumn;
import bpm.metadata.layer.physical.ITable;
import bpm.united.olap.api.runtime.IRuntimeContext;

public class UnitedOlapLevelColumn implements IColumn {

	private Level level;
	private UnitedOlapTable table;
	
	public UnitedOlapLevelColumn(){}
	
	public UnitedOlapLevelColumn(UnitedOlapTable table, Level level) {
		this.level = level;
		this.table = table;
	}
	
	@Override
	public String getClassName() {
		return this.getClass().getName();
	}

	@Override
	public Class<?> getJavaClass() throws Exception {
		return this.getClass();
	}

	@Override
	public String getName() {
		return level.getUniqueName();
	}

	@Override
	public String getShortName() {
		return level.getName();
	}

	@Override
	public ITable getTable() {
		return table;
	}

	@Override
	public List<String> getValues() {
		try {
			String schemaId = ((UnitedOlapConnection)table.getConnection()).getCube().getSchemaId();
			String cubeName = ((UnitedOlapConnection)table.getConnection()).getCubeName();
			IRuntimeContext runtimeContext = ((UnitedOlapConnection)table.getConnection()).getRuntimeContext();
			return UnitedOlapServiceProvider.getInstance().getModelService().getLevelValues(level.getUniqueName(), schemaId, cubeName, runtimeContext);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public int getLevelIndex() {
		return level.getLevelNumber();
	}

	@Override
	public List<String> getValues(HashMap<String, String> parentValues) {
		try {
			String schemaId = ((UnitedOlapConnection)table.getConnection()).getCube().getSchemaId();
			String cubeName = ((UnitedOlapConnection)table.getConnection()).getCubeName();
			IRuntimeContext runtimeContext = ((UnitedOlapConnection)table.getConnection()).getRuntimeContext();
			List<String> members = UnitedOlapServiceProvider.getInstance().getModelService().getLevelValues(level.getUniqueName(), schemaId, cubeName, runtimeContext);
	
			String value = parentValues.values().iterator().next();
	
			List<String> result = new ArrayList<String>();
			for(String member : members) {
				if(member.contains("[" + value + "]")) {
					String[] unameParts = member.replace("[","").replace("]","").split("\\.");
					result.add(unameParts[unameParts.length - 1]);
				}
			}
			return result;
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

}
