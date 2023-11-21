package bpm.fd.api.core.model.components.definition.chart;

import java.util.List;

import bpm.fd.api.core.model.components.definition.IComponentDatas;
import bpm.fd.api.core.model.datas.DataSet;
import bpm.fd.api.core.model.resources.Palette;

public interface IChartData extends IComponentDatas{
	
	public void setLevelIndex(int levelNumber, Integer dataSetLevelCategoryIndex);
	public List<Integer> getLevelCategoriesIndex();
	
	
	/**
	 * @return the OrderingType
	 */
	public ChartOrderingType getOrderType();
	
	/**
	 * @return the groupFieldIndex
	 */
	public Integer getCategorieFieldIndex() ;
	
	/**
	 * @return the groupFieldLabelIndex
	 */
	public Integer getCategorieFieldLabelIndex();
	
	public List<DataAggregation> getAggregation();
	
	public DatasLimit getLimit();
	
	public void setDataSet(DataSet dataSet);
	
	public Palette getColorPalette();
	
	public void setColorPalette(Palette palette);
	public void addLevel();
	public void removeLevel(int index);
}
