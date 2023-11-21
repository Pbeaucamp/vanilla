package bpm.metadata.layer.logical.olap;

import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.physical.IColumn;
import bpm.metadata.layer.physical.ITable;
import bpm.metadata.layer.physical.olap.UnitedOlapMeasureColumn;
import bpm.metadata.layer.physical.olap.UnitedOlapTable;

public class UnitedOlapDataStream extends IDataStream {

	private boolean isMeasure = false;
	/**
	 * Forbidden, only for digester
	 */
	public UnitedOlapDataStream(){}
	
	public UnitedOlapDataStream(UnitedOlapTable table) {
		this.name = table.getName();
		this.origin = table;
		
		if(table.getColumns() != null && !table.getColumns().isEmpty() && table.getColumns().get(0) instanceof UnitedOlapMeasureColumn) {
			isMeasure = true;
		}
		
		for(IColumn col : table.getColumns()) {
			UnitedOlapDataStreamElement elem = new UnitedOlapDataStreamElement(col);
			this.addColumn(elem);
		}
	}
	
	@Override
	public ITable getOrigin() {
		
		return super.getOrigin();
	}
	
	@Override
	public boolean isMeasure() {
		return isMeasure;
	}

}
