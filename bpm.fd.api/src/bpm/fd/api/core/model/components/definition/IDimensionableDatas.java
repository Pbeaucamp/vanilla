package bpm.fd.api.core.model.components.definition;

import bpm.fd.api.core.model.datas.DataSet;

public interface IDimensionableDatas extends IComponentDatas{
	/**
	 * @return the dimensionDataSet
	 */
	public DataSet getDimensionDataSet();
	public Integer getDimensionValueIndex();
	public Integer getDimensionLabelIndex() ;
	public void setDimensionDataSet(DataSet dataSet);
	public void setDimensionValueIndex(Integer columnIndex);
	public void setDimensionLabelIndex(Integer columnIndex);
	public IComponentDatas getDimensionDatas();
}
