package bpm.fd.api.core.model.components.definition.slicer;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.components.definition.IComponentDatas;
import bpm.fd.api.core.model.datas.DataSet;

public class SlicerData implements IComponentDatas{

	public static class Level{
		private String label;
		private Integer fieldIndex;
		public Level(String label){
			this.label = label;
		}
		/**
		 * @return the label
		 */
		public String getLabel() {
			return label;
		}
		/**
		 * @param label the label to set
		 */
		public void setLabel(String label) {
			this.label = label;
		}
		/**
		 * @return the fieldIndex
		 */
		public Integer getFieldIndex() {
			return fieldIndex;
		}
		/**
		 * @param fieldIndex the fieldIndex to set
		 */
		public void setFieldIndex(Integer fieldIndex) {
			this.fieldIndex = fieldIndex;
		}
		
	}
	
	private DataSet dataSet;
	private List<Level> levelsDataSetColumnIndex = new ArrayList<Level>();
	
	public void addLevel() {
		levelsDataSetColumnIndex.add(new Level("Level " + (levelsDataSetColumnIndex.size() + 1)));
		
	}
	public void setLevelIndex(int levelNumber, Integer dataSetLevelCategoryIndex){
		List<Level> toAdd = new ArrayList<Level>();
		while (levelsDataSetColumnIndex.size() + toAdd.size() < levelNumber + 1){
			toAdd.add(new Level("Level " + (levelsDataSetColumnIndex.size() + 1)));
		}
		levelsDataSetColumnIndex.addAll(toAdd);
		levelsDataSetColumnIndex.get(levelNumber).setFieldIndex(dataSetLevelCategoryIndex);
	}
	public void removeLevel(int lvlNumber){
		try{
			levelsDataSetColumnIndex.remove(lvlNumber);
		}catch(Exception ex){
			
		}
	}
	
	public List<Level> getLevelCategoriesIndex(){
		return new ArrayList<Level>(levelsDataSetColumnIndex);
	}
	
	/**
	 * @param dataSet the dataSet to set
	 */
	public void setDataSet(DataSet dataSet) {
		this.dataSet = dataSet;
	}
	@Override
	public IComponentDatas getAdapter(Object o) {
		return this;
	}

	@Override
	public DataSet getDataSet() {
		return dataSet;
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("slicerData");
		

		if (dataSet != null){
			e.addElement("dataSet-ref").setText(dataSet.getName());
		}
		
		
		for(int i = 0; i < levelsDataSetColumnIndex.size(); i++){
			e.addElement("level").addAttribute("index", i + "").addAttribute("categoryDataSetIndex", levelsDataSetColumnIndex.get(i).getFieldIndex() + "").addAttribute("levelLabel", levelsDataSetColumnIndex.get(i).getLabel());
		}
		
		return e;
	}

	@Override
	public boolean isFullyDefined() {
		return dataSet != null;
	}
	@Override
	public IComponentDatas copy() {
		SlicerData copy = new SlicerData();
		
		copy.setDataSet(dataSet);
		int i = 0;
		for(Level lvl  : getLevelCategoriesIndex()) {
			copy.setLevelIndex(i, lvl.getFieldIndex());
			i++;
		}
		
		return copy;
	}

}
