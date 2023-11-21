package bpm.fd.design.ui.properties.model.chart;

import bpm.fd.api.core.model.datas.filters.ValueFilter;
import bpm.fd.design.ui.properties.i18n.Messages;
import bpm.fd.design.ui.properties.model.Property;

public class FilterProperty extends Property{
	private ValueFilter filter;
	
	public FilterProperty(ValueFilter f){
		super(f.getType().getType(), null);
		this.filter = f;
	}
	
	public String getValue(){
		return filter.getValue() + ""; //$NON-NLS-1$
	}
}
