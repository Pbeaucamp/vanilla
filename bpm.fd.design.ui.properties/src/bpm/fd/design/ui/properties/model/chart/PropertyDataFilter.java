package bpm.fd.design.ui.properties.model.chart;

import org.eclipse.jface.viewers.CellEditor;

import bpm.fd.api.core.model.datas.filters.ValueFilter;
import bpm.fd.design.ui.properties.model.Property;

public class PropertyDataFilter extends Property{

	public PropertyDataFilter(ValueFilter filter, CellEditor fieldEditor){
		super(filter.getType().getType(), fieldEditor);
	}
}
