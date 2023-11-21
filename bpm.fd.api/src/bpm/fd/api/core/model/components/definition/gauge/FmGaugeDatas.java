package bpm.fd.api.core.model.components.definition.gauge;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.components.definition.IComponentDatas;
import bpm.fd.api.core.model.datas.DataSet;

public abstract class FmGaugeDatas implements IComponentDatas{

	private DataSet dataset;
	private Integer indexMax;
	private Integer indexMin;
	private Integer indexMinSeuil;
	private Integer indexMaxSeuil;
	private Float targetValue;
	private Integer indexTolerance;
	private Integer indexValue;

	
	

	/**
	 * @param dataset the dataset to set
	 */
	public void setDataSet(DataSet dataset) {
		this.dataset = dataset;
	}

	public IComponentDatas getAdapter(Object o) {
		return this;
	}

	public DataSet getDataSet() {
		return dataset;
	}

	public Element getElement() {
		Element e = DocumentHelper.createElement("fmGaugeData");
		
		if (dataset != null){
			e.addElement("dataSet-ref").setText(dataset.getName());
		}
		
		if (indexMax != null){
			e.addElement("indexMax").setText(indexMax + "");
		}
		if (indexMin != null){
			e.addElement("indexMin").setText(indexMin + "");
		}
		if (indexMinSeuil != null){
			e.addElement("indexMinSeuil").setText(indexMinSeuil + "");
		}
		if (indexMaxSeuil != null){
			e.addElement("indexMaxSeuil").setText(indexMaxSeuil + "");
		}
		if (targetValue != null){
			e.addElement("targetValue").setText(targetValue + "");
		}
		if (indexTolerance != null){
			e.addElement("indexTolerance").setText(indexTolerance + "");
		}
		if (indexValue != null){
			e.addElement("indexValue").setText(indexValue + "");
		}
		
		
		return e;
	}

	public boolean isFullyDefined() {
		//TODO
		return true;
	}

	/**
	 * @return the indexMax
	 */
	public Integer getIndexMax() {
		return indexMax;
	}

	/**
	 * @param indexMax the indexMax to set
	 */
	public void setIndexMax(Integer indexMax) {
		this.indexMax = indexMax;
	}

	/**
	 * @return the indexMin
	 */
	public Integer getIndexMin() {
		return indexMin;
	}

	/**
	 * @param indexMin the indexMin to set
	 */
	public void setIndexMin(Integer indexMin) {
		this.indexMin = indexMin;
	}

	/**
	 * @return the indexMinSeuil
	 */
	public Integer getIndexMinSeuil() {
		return indexMinSeuil;
	}

	/**
	 * @param indexMinSeuil the indexMinSeuil to set
	 */
	public void setIndexMinSeuil(Integer indexMinSeuil) {
		this.indexMinSeuil = indexMinSeuil;
	}

	/**
	 * @return the indexMaxSeuil
	 */
	public Integer getIndexMaxSeuil() {
		return indexMaxSeuil;
	}

	/**
	 * @param indexMaxSeuil the indexMaxSeuil to set
	 */
	public void setIndexMaxSeuil(Integer indexMaxSeuil) {
		this.indexMaxSeuil = indexMaxSeuil;
	}

	/**
	 * @return the indexTarget
	 */
	public Float getTargetValue() {
		return targetValue;
	}

	/**
	 * @param indexTarget the indexTarget to set
	 */
	public void setTargetValue(Float targetValue) {
		this.targetValue = targetValue;
	}

	/**
	 * @return the indexTolerance
	 */
	public Integer getIndexTolerance() {
		return indexTolerance;
	}

	/**
	 * @param indexTolerance the indexTolerance to set
	 */
	public void setIndexTolerance(Integer indexTolerance) {
		this.indexTolerance = indexTolerance;
	}

	/**
	 * @return the indexValue
	 */
	public Integer getIndexValue() {
		return indexValue;
	}

	/**
	 * @param indexValue the indexValue to set
	 */
	public void setIndexValue(Integer indexValue) {
		this.indexValue = indexValue;
	}

}
