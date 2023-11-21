package bpm.fd.api.core.model.components.definition.chart;

import org.dom4j.Element;

import bpm.fd.api.core.model.components.definition.IComponentDatas;
import bpm.fd.api.core.model.datas.DataSet;

public class DimData implements IComponentDatas{
	
	private DataSet dimensionDataSet;
	
	public DimData(DataSet dataset){
		this.dimensionDataSet = dataset;
	}
	
	public IComponentDatas getAdapter(Object o) {
		return this;
	}
	public DataSet getDataSet() {
	
		return dimensionDataSet;
	}
	public Element getElement() {
		
		return null;
	}

	public boolean isFullyDefined() {
		return dimensionDataSet != null;
	}

	@Override
	public IComponentDatas copy() {
		DimData copy = new DimData(dimensionDataSet);
		return copy;
	}
}
