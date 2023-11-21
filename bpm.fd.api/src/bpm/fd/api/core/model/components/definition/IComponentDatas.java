package bpm.fd.api.core.model.components.definition;

import org.dom4j.Element;

import bpm.fd.api.core.model.components.definition.chart.IChartData;
import bpm.fd.api.core.model.datas.DataSet;

public interface IComponentDatas {
	public DataSet getDataSet();
	public Element getElement();
	
	public IComponentDatas getAdapter(Object o);
	
	/**
	 * 
	 * @return true if the Data is rightly defined
	 */
	public boolean isFullyDefined();
	
	public IComponentDatas copy();
}
