package bpm.fd.api.core.model.components.definition.chart;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.components.definition.IComponentDataFilter;
import bpm.fd.api.core.model.components.definition.IComponentDatas;
import bpm.fd.api.core.model.datas.DataSet;
import bpm.fd.api.core.model.datas.filters.ColumnFilter;
import bpm.fd.api.core.model.datas.filters.ValueFilter;
import bpm.fd.api.core.model.resources.Palette;

public class MultiSerieChartData implements IChartData{
	private DataSet dataSet;
	
	private Integer groupFieldLabelIndex;
	private Integer serieFieldIndex;
	private Integer categorieFieldIndex;
	private ChartOrderingType orderType = ChartOrderingType.CATEGORY_LABEL_ASC;
	private List<DataAggregation> aggregation = new ArrayList<DataAggregation>();
	
	private DatasLimit limit = new DatasLimit();
	private Palette colorPalette;
	
	private List<Integer> dimensionLevelIndex = new ArrayList<Integer>();
	
	public void setLevelIndex(int levelNumber, Integer dataSetLevelCategoryIndex){
		List<Integer> toAdd = new ArrayList<Integer>();
		while (dimensionLevelIndex.size() + toAdd.size() < levelNumber + 1){
			toAdd.add(null);
		}
		dimensionLevelIndex.addAll(toAdd);
		dimensionLevelIndex.set(levelNumber , dataSetLevelCategoryIndex);
	}
	public void removeLevel(int lvlNumber){
		try{
			dimensionLevelIndex.remove(lvlNumber);
		}catch(Exception ex){
			
		}
	}
	
	public List<Integer> getLevelCategoriesIndex(){
		return new ArrayList<Integer>(dimensionLevelIndex);
	}
	
	public MultiSerieChartData(){
		
	}
	
	public MultiSerieChartData(MultiSerieChartData data){
		setDataSet(data.getDataSet());
		setSerieFieldIndex(data.getSerieFieldIndex());
		setOrderType(data.getOrderType());
		setCategorieFieldIndex(data.getCategorieFieldIndex());
		this.setCategorieFieldLabelIndex(data.getCategorieFieldLabelIndex());
		for(DataAggregation agg : data.getAggregation()){
			addAggregation(agg);
		}
		
		limit.setSize(data.getLimit().getSize());
		limit.setType(data.getLimit().getType());
	}
	/**
	 * @return the colorPalette
	 */
	public Palette getColorPalette() {
		return colorPalette;
	}

	/**
	 * @param colorPalette the colorPalette to set
	 */
	public void setColorPalette(Palette colorPalette) {
		this.colorPalette = colorPalette;
	}
	
	public int getSplitedMesureNumber(){
		int i = 0;
		for(DataAggregation agg : getAggregation()){
			if (agg.isApplyOnDistinctSerie()){
				i++;
			}
		}
		return i;
	}
	
	public int getNonSplitedMesureNumber(){
		int i = 0;
		for(DataAggregation agg : getAggregation()){
			if (!agg.isApplyOnDistinctSerie()){
				i++;
			}
		}
		return i;
	}
	
	public DatasLimit getLimit(){
		return limit;
	}

	public Integer getCategorieFieldLabelIndex(){
		if (groupFieldLabelIndex == null){
			return getCategorieFieldIndex();
		}
		else{
			return groupFieldLabelIndex;
		}
		
	}
	
	public void setCategorieFieldLabelIndex(Integer categoryFieldLabelIndex){
		this.groupFieldLabelIndex = categoryFieldLabelIndex;
	}
	
	public IComponentDatas getAdapter(Object o) {
		if (o  == MultiSerieChartDataDimension.class){
			return new MultiSerieChartDataDimension(this);
		}
		else{
			return new MultiSerieChartData(this);
		}
		
	}

	public DataSet getDataSet() {
		return dataSet;
	}

	public Element getElement() {
		Element e = DocumentHelper.createElement("multiSeriesChartData");
				
		if (getColorPalette() != null){
			e.addAttribute("colorPalette", getColorPalette().getName());
		}
		if (getCategorieFieldIndex() != null){
			e.addAttribute("groupField", "" + getCategorieFieldIndex());
		}
		if (getCategorieFieldLabelIndex() != null){
			e.addAttribute("groupLabelField", "" + getCategorieFieldLabelIndex());
		}
		
		if (getOrderType() != null){
			e.addAttribute("orderType", "" + getOrderType().name());
		}
		
		if (getSerieFieldIndex() != null){
			e.addAttribute("serieField", "" + getSerieFieldIndex());
		}
		
		for(DataAggregation a : getAggregation()){
			Element aggreg = DocumentHelper.createElement("dataAggregation");
			aggreg.addAttribute("aggregator", "" + a.getAggregator());
			aggreg.addAttribute("valueField", "" + a.getValueFieldIndex());
			aggreg.addAttribute("applyOnDistinctSerie", "" + a.isApplyOnDistinctSerie());
			if (a.getMeasureName() != null){
				aggreg.addAttribute("measureName", "" + a.getMeasureName());
			}
			aggreg.addAttribute("primaryAxis", a.isPrimaryAxis() + "");
			aggreg.addAttribute("rendering", a.getRendering().name());
			
			if (getOrderType() != null){
				aggreg.addAttribute("orderType", "" + a.getOrderType().name());
			}
			
			Element palette = aggreg.addElement("paletteColors");
			for(String s : a.getColorsCode()){
				palette.addElement("color").setText(s);
			}
			
			for(IComponentDataFilter f : a.getFilter()){
				if(f instanceof ValueFilter) {
					aggreg.add(((ValueFilter)f).getElement());
				}
			}
			
			for(ColumnFilter f : a.getColumnFilters()){
				Element cf = aggreg.addElement("columnFilter");
				cf.addElement("value").setText(f.getValue());
				cf.addElement("columnIndex").setText(f.getColumnIndex() + "");
			}
			
			e.add(aggreg);
		}
		
		
		
		
		if (dataSet != null){
			e.addElement("dataSet-ref").setText(dataSet.getName());
		}
		
		e.add(getLimit().getElement());
		for(int i = 0; i < dimensionLevelIndex.size(); i++){
			e.addElement("level").addAttribute("index", i + "").addAttribute("categoryDataSetIndex", dimensionLevelIndex.get(i) + "");
		}
		return e;
	}

	/**
	 * @return the serieFieldIndex
	 */
	public Integer getSerieFieldIndex() {
		return serieFieldIndex;
	}

	/**
	 * @param serieFieldIndex the serieFieldIndex to set
	 */
	public void setSerieFieldIndex(Integer serieFieldIndex) {
		this.serieFieldIndex = serieFieldIndex;
	}

	/**
	 * @return the categorieFieldIndex
	 */
	public Integer getCategorieFieldIndex() {
		return categorieFieldIndex;
	}

	/**
	 * @param categorieFieldIndex the categorieFieldIndex to set
	 */
	public void setCategorieFieldIndex(Integer categorieFieldIndex) {
		this.categorieFieldIndex = categorieFieldIndex;
	}

	/**
	 * @return the OrderingType
	 */
	public ChartOrderingType getOrderType() {
		return orderType;
	}

	public void setOrderType(ChartOrderingType orderType) {
		this.orderType = orderType;
	}

	/**
	 * @return the aggregation
	 */
	public List<DataAggregation> getAggregation() {
		return new ArrayList<DataAggregation>(aggregation);
	}
	
	/**
	 * 
	 * @param aggregation
	 */
	public void addAggregation(DataAggregation aggregation){
		this.aggregation.add(aggregation);
	}
	
	public void removeAggregation(DataAggregation aggregation){
		this.aggregation.remove(aggregation);
	}
	
	public void setAggregations(List<DataAggregation> aggregations) {
		this.aggregation = aggregations;
	}

	/**
	 * @param dataSet the dataSet to set
	 */
	public void setDataSet(DataSet dataSet) {
		this.dataSet = dataSet;
	}

	public boolean isFullyDefined() {
		if (dataSet == null){
			return false;
		}
		
		if (dataSet.getDataSetDescriptor() == null){
			return false;
		}
		
		if (aggregation == null){
			return false;
		}
		
		for(DataAggregation agg : aggregation){
			if (agg.getValueFieldIndex() == null || agg.getValueFieldIndex() < 0 || agg.getValueFieldIndex() >=  dataSet.getDataSetDescriptor().getColumnsDescriptors().size()){
				return false;
			}
			if (agg.getAggregator() < 0 || agg.getAggregator() > 6){
				return false;
			}
		}
		
		
		
		if (getCategorieFieldIndex() == null || getCategorieFieldIndex() >=  dataSet.getDataSetDescriptor().getColumnsDescriptors().size()){
			return false;
		}
		

		if (getCategorieFieldLabelIndex() == null || getCategorieFieldLabelIndex() >=  dataSet.getDataSetDescriptor().getColumnsDescriptors().size()){
			return false;
		}
		
		return true;
	}


	@Override
	public void addLevel() {
		dimensionLevelIndex.add(null);
		
	}
	@Override
	public IComponentDatas copy() {
		MultiSerieChartData copy = new MultiSerieChartData(this);
		
		return copy;
	}
	
	
	
}
