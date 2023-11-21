package bpm.fd.api.core.model.components.definition.chart;

import org.dom4j.Element;

import bpm.fd.api.core.model.components.definition.IComponentDatas;
import bpm.fd.api.core.model.components.definition.IDimensionableDatas;
import bpm.fd.api.core.model.datas.DataSet;

public class MultiSerieChartDataDimension extends MultiSerieChartData implements IDimensionableDatas{
	private DataSet dimensionDataSet;
	private Integer dimensionValueIndex;
	private Integer dimensionLabelIndex;
	
	
	
	public MultiSerieChartDataDimension(){
		
	}
	
	
	public IComponentDatas getDimensionDatas(){
		return new DimData(dimensionDataSet);
	}
	
	public MultiSerieChartDataDimension(MultiSerieChartDataDimension data){
		super(data);
		this.setDimensionDataSet(data.getDimensionDataSet());
		this.setDimensionLabelIndex(data.getDimensionLabelIndex());
		this.setDimensionValueIndex(data.getDimensionValueIndex());
	}
	
	public MultiSerieChartDataDimension(MultiSerieChartData data){
		super(data);

	}
	
	/**
	 * @return the dimensionDataSet
	 */
	public DataSet getDimensionDataSet() {
		return dimensionDataSet;
	}
	/**
	 * @param dimensionDataSet the dimensionDataSet to set
	 */
	public void setDimensionDataSet(DataSet dimensionDataSet) {
		this.dimensionDataSet = dimensionDataSet;
	}
	/**
	 * @return the dimensionValueIndex
	 */
	public Integer getDimensionValueIndex() {
		return dimensionValueIndex;
	}
	/**
	 * @param dimensionValueIndex the dimensionValueIndex to set
	 */
	public void setDimensionValueIndex(Integer dimensionValueIndex) {
		this.dimensionValueIndex = dimensionValueIndex;
	}
	/**
	 * @return the dimensionLabelIndex
	 */
	public Integer getDimensionLabelIndex() {
		return dimensionLabelIndex;
	}
	/**
	 * @param dimensionLabelIndex the dimensionLabelIndex to set
	 */
	public void setDimensionLabelIndex(Integer dimensionLabelIndex) {
		this.dimensionLabelIndex = dimensionLabelIndex;
	}
	
	
	

	/* (non-Javadoc)
	 * @see bpm.fd.api.core.model.components.definition.chart.ChartData#getElement()
	 */
	@Override
	public Element getElement() {
		Element e = super.getElement();
		e.setName("multiSeriesChartData");
		e.addElement("dimension-data-set-ref").setText(dimensionDataSet.getName());
		e.addElement("dimension-value-index").setText(dimensionValueIndex + "");
		if (getDimensionLabelIndex() != null){
			e.addElement("dimension-label-index").setText(dimensionLabelIndex + "");
		}
		
		return e; 
	}
	
}
