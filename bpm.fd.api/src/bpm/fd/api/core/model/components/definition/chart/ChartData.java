package bpm.fd.api.core.model.components.definition.chart;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.components.definition.IComponentDataFilter;
import bpm.fd.api.core.model.components.definition.IComponentDatas;
import bpm.fd.api.core.model.datas.DataSet;
import bpm.fd.api.core.model.datas.filters.ValueFilter;
import bpm.fd.api.core.model.resources.Palette;

public class ChartData implements IChartData{
		
	private DatasLimit limit = new DatasLimit();
	
	private DataSet dataSet;
	
	private Integer groupFieldLabelIndex;
	private Integer groupFieldIndex; 
	
	//Pour le composant RChartComponent pour afficher la fenetre des Chart R
	private Integer axeXField , axeYField ;
	private String axeXFieldName , axeYFieldName  , groupFieldName  ;
	private List<String> selectedColumns ;
	private List<String>  selectedColumName;
	
	//
	private DataAggregation aggregation = new DataAggregation();
	private Palette colorPalette;
	private ChartOrderingType orderType = ChartOrderingType.CATEGORY_LABEL_ASC;
	private boolean orderAsc = true;
	
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
	
	public ChartData(){
		
	}
	
	public ChartData(ChartData data){
		this.setAggregator(data.getAggregator());
		this.setDataSet(data.getDataSet());
		this.setGroupFieldIndex(data.getCategorieFieldIndex());
		this.setGroupFieldName(data.getAxeXFieldName());
		this.setOrderType(data.getOrderType());
		this.setValueFieldIndex(data.getValueFieldIndex());
		this.setCategorieFieldLabelIndex(data.getCategorieFieldLabelIndex());
		this.setAxeXField(data.getAxeXField());
		this.setAxeYField(data.getAxeYField());
		this.setAxeXFieldName(data.getAxeXFieldName());
		this.setAxeYFieldName(data.getAxeYFieldName());
		this.setSelectedColumns( data.getSelectedColumns() );
		this.setSelectedColumName(data.getSelectedColumName());
	
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
	

	public ChartOrderingType getOrderType() {
		return orderType;
	}


	public void setOrderType(ChartOrderingType orderType) {
		this.orderType = orderType;
	}

	/**
	 * @return the aggregator
	 */
	public int getAggregator() {
		return aggregation.getAggregator();
	}



	/**
	 * @param dataSet the dataSet to set
	 */
	public void setDataSet(DataSet dataSet) {
		this.dataSet = dataSet;
	}



	/**
	 * @param aggregator the aggregator to set
	 */
	public void setAggregator(int aggregator) {
		if(aggregator < 0) {
			aggregator = 0;
		}
		this.aggregation.setAggregator( aggregator);
	}

	public void setAggregator(String agg) {
		int i = 0;
		for(String a : DataAggregation.AGGREGATORS_NAME) {
			if(agg.equals(a)) {
				setAggregator(i);
				break;
			}
			i++;
		}
	}

	/**
	 * @return the groupFieldIndex
	 */
	public Integer getCategorieFieldIndex() {
		return groupFieldIndex;
	}



	/**
	 * @param groupFieldIndex the groupFieldIndex to set
	 */
	public void setGroupFieldIndex(Integer groupFieldIndex) {
		this.groupFieldIndex = groupFieldIndex;
	}



	/**
	 * @return the valueFieldIndex
	 */
	public Integer getValueFieldIndex() {
		return aggregation.getValueFieldIndex();
	}



	/**
	 * @param valueFieldIndex the valueFieldIndex to set
	 */
	public void setValueFieldIndex(Integer valueFieldIndex) {
		this.aggregation.setValueFieldIndex(valueFieldIndex);
	}



	public DataSet getDataSet() {
		return dataSet;
	}



	public Element getElement() {
		Element e = DocumentHelper.createElement("chartData");

		if (getColorPalette() != null){
			e.addAttribute("colorPalette", getColorPalette().getName());
		}
		
		if (getValueFieldIndex() != null){
			e.addAttribute("valueField", "" + getValueFieldIndex());
		}
		
		if (getCategorieFieldIndex() != null){
			e.addAttribute("groupField", "" + getCategorieFieldIndex());
		}
		if (getCategorieFieldLabelIndex() != null){
			e.addAttribute("groupLabelField", "" + getCategorieFieldLabelIndex());
		}
		
		if (getAxeXField() != null) {
			e.addAttribute("axeXField", "" + getAxeXField());
		}
		if (getAxeXFieldName() != null) {
			e.addAttribute("axeXFieldName", "" + getAxeXFieldName());
		}
		if (getAxeYField() != null) {
			e.addAttribute("axeYField", "" + getAxeYField());
		}
		if (getAxeYFieldName() != null) {
			e.addAttribute("axeYFieldName", "" + getAxeYFieldName());
		}
		
		if (getOrderType() != null){
			e.addAttribute("orderType", "" + getOrderType().name());
		}
		
		e.addAttribute("aggregator", "" + getAggregator());
		if (dataSet != null){
			e.addElement("dataSet-ref").setText(dataSet.getName());
		}
		
		Element aggreg = DocumentHelper.createElement("dataAggregation");
		aggreg.addAttribute("aggregator", "" + aggregation.getAggregator());
		aggreg.addAttribute("valueField", "" + aggregation.getValueFieldIndex());
		aggreg.addAttribute("applyOnDistinctSerie", "" + aggregation.isApplyOnDistinctSerie());
		if (aggregation.getMeasureName() != null){
			aggreg.addAttribute("measureName", "" + aggregation.getMeasureName());
		}
		aggreg.addAttribute("primaryAxis", aggregation.isPrimaryAxis() + "");
		aggreg.addAttribute("rendering", aggregation.getRendering().name());
		
		if (getOrderType() != null){
			aggreg.addAttribute("orderType", "" + aggregation.getOrderType().name());
		}
		
		Element palette = aggreg.addElement("paletteColors");
		for(String s : aggregation.getColorsCode()){
			palette.addElement("color").setText(s);
		}
		
		for(IComponentDataFilter f : aggregation.getFilter()){
			if(f instanceof ValueFilter) {
				aggreg.add(((ValueFilter)f).getElement());
			}
		}
		
		e.add(aggreg);
		e.add(getLimit().getElement());
		
		
		for(int i = 0; i < dimensionLevelIndex.size(); i++){
			e.addElement("level").addAttribute("index", i + "").addAttribute("categoryDataSetIndex", dimensionLevelIndex.get(i) + "");
		}
		
		return e;
	}



	public IComponentDatas getAdapter(Object o) {
		return this;
	}

	public List<DataAggregation> getAggregation() {
		List<DataAggregation> l = new ArrayList<DataAggregation>();
		l.add(aggregation);
		return l;
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
		
		if (aggregation.getValueFieldIndex() ==  null || aggregation.getValueFieldIndex() < 0 || aggregation.getValueFieldIndex() >= dataSet.getDataSetDescriptor().getColumnsDescriptors().size()){
			return false;
		}
		
		if (getCategorieFieldIndex() == null || getCategorieFieldIndex() >=  dataSet.getDataSetDescriptor().getColumnsDescriptors().size()){
			return false;
		}
		

		if (getCategorieFieldLabelIndex() == null || getCategorieFieldLabelIndex() >=  dataSet.getDataSetDescriptor().getColumnsDescriptors().size()){
			return false;
		}
		
		if (getValueFieldIndex() == null || getValueFieldIndex() >=  dataSet.getDataSetDescriptor().getColumnsDescriptors().size()){
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
		ChartData copy = new ChartData(this);
		
		return copy;
	}
	public Integer getAxeXField() {
		return axeXField;
	}
	public void setAxeXField(Integer axeXField) {
		this.axeXField = axeXField;
	}
	public Integer getAxeYField() {
		return axeYField;
	}
	public void setAxeYField(Integer axeYField) {
		this.axeYField = axeYField;
	}
	
	
	public String getAxeXFieldName() {
		return axeXFieldName;
	}
	public void setAxeXFieldName(String axeXFieldName) {
		this.axeXFieldName = axeXFieldName;
	}
	public String getAxeYFieldName() {
		return axeYFieldName;
	}
	public void setAxeYFieldName(String axeYFieldName) {
		this.axeYFieldName = axeYFieldName;
	}
	public String getGroupFieldName() {
		return groupFieldName;
	}
	public void setGroupFieldName(String groupFieldName) {
		this.groupFieldName = groupFieldName;
	}
	public List<String> getSelectedColumns() {
		return selectedColumns;
	}
	public void setSelectedColumns(List<String> selectedColumns) {
		this.selectedColumns = selectedColumns;
	}
	public List<String> getSelectedColumName() {
		return selectedColumName;
	}
	public void setSelectedColumName(List<String> selectedColumName) {
		this.selectedColumName = selectedColumName;
	}
	
}


