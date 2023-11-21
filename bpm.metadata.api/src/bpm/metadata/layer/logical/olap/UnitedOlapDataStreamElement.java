package bpm.metadata.layer.logical.olap;

import java.util.HashMap;
import java.util.List;

import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.layer.physical.IColumn;
import bpm.metadata.layer.physical.olap.UnitedOlapLevelColumn;
import bpm.metadata.layer.physical.olap.UnitedOlapMeasureColumn;

public class UnitedOlapDataStreamElement extends IDataStreamElement {

	public UnitedOlapDataStreamElement() {}
	
	public UnitedOlapDataStreamElement(IColumn col) {
		this.name = col.getShortName();
		this.origin = col;
		
		if(origin instanceof UnitedOlapLevelColumn) {
			this.type = IDataStreamElement.SubType.DIMENSION;
		}
		else if (origin instanceof UnitedOlapMeasureColumn) {
			this.type = IDataStreamElement.SubType.SUM;
		}
	}
	
	@Override
	public List<String> getDistinctValues() throws Exception {
		if(origin instanceof UnitedOlapMeasureColumn) {
			throw new Exception("Can not get distinct values for a measure.");
		}
		
		if(origin == null) {
			origin = table.getElementOrigin(originName);
		}
		
		return origin.getValues();
	}

	@Override
	public String getJavaClassName() throws Exception {
		return this.getClass().getName();
	}

	@Override
	public IColumn getOrigin() {
		if(origin == null) {
			origin = table.getElementOrigin(originName);
		}
		return origin;
	}

	@Override
	public boolean isCalculated() {
		return false;
	}

	@Override
	public List<String> getDistinctValues(HashMap<String, String> parentValues) throws Exception {
		return origin.getValues(parentValues);
	}

}
