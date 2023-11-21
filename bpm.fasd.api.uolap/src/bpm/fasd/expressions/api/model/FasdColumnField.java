package bpm.fasd.expressions.api.model;



import org.fasd.datasource.DataObjectItem;
import org.fasd.olap.OLAPMeasure;

import bpm.studio.expressions.core.model.IField;

public class FasdColumnField implements IField{
	private Object columnItem;
	
	
	public FasdColumnField(DataObjectItem columnItem){
		this.columnItem = columnItem;
	}
	
	public FasdColumnField(OLAPMeasure measure){
		if (measure.getOrigin() != null){
			this.columnItem = measure.getOrigin();
		}
		else{
			this.columnItem = measure;
		}
		
	}

	@Override
	public String getName() {
		if (columnItem instanceof DataObjectItem){
			return ((DataObjectItem)columnItem).getName();
		}
		else if (columnItem instanceof OLAPMeasure){
			return ((OLAPMeasure)columnItem).getName();
		}
		
		return columnItem.toString();
	}

	@Override
	public Object getParent() {
		if (columnItem instanceof DataObjectItem){
			return ((DataObjectItem)columnItem).getParent();
		}
		return null;
	}
}
