package bpm.metadata.layer.physical.olap;

import java.util.HashMap;
import java.util.List;

import bpm.fa.api.olap.Measure;
import bpm.metadata.layer.physical.IColumn;
import bpm.metadata.layer.physical.ITable;

public class UnitedOlapMeasureColumn implements IColumn {

	private Measure measure;
	private UnitedOlapTable table;
	
	public UnitedOlapMeasureColumn() {}
	
	public UnitedOlapMeasureColumn(UnitedOlapTable table, Measure measure) {
		this.measure = measure;
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
		return measure.getUniqueName();
	}

	@Override
	public String getShortName() {
		return measure.getName();
	}

	@Override
	public ITable getTable() {
		return table;
	}

	@Override
	public List<String> getValues() {
		//Not implemented
		return null;
	}

	@Override
	public List<String> getValues(HashMap<String, String> parentValues) {
		//Not implemented
		return null;
	}

}
