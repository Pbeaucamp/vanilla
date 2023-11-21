package bpm.fd.api.core.model.components.definition.gauge;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.components.definition.IComponentDatas;
import bpm.fd.api.core.model.datas.DataSet;

public class StaticGaugeDatas implements IComponentDatas{

//	private DataSet dataSet;
	private Float target;
	private Float tolerancePerc = 10.0f;
	private Float value;
	private Float min = 0.0f;
	private Float max =  100.0f;
	private Float maxSeuil =  75.0f;
	private Float minSeuil =  25.0f;
	
	public IComponentDatas getAdapter(Object o) {
		return this;
	}

	public DataSet getDataSet() {
		return null;
	}

	public Element getElement() {
		Element e = DocumentHelper.createElement("staticGaugeData");
		
//		if (dataSet != null){
//			e.addElement("dataSet-ref").setText(dataSet.getName());
//		}
		
		if (max != null){
			e.addElement("maximum").setText(max + "");
		}
		if (min != null){
			e.addElement("minimum").setText(min + "");
		}
		if (minSeuil != null){
			e.addElement("minimumExpected").setText(minSeuil + "");
		}
		if (maxSeuil != null){
			e.addElement("maximumExpected").setText(maxSeuil + "");
		}
		if (target != null){
			e.addElement("target").setText(target + "");
		}
		if (tolerancePerc != null){
			e.addElement("tolerancePerc").setText(tolerancePerc + "");
		}
		if (value != null){
			e.addElement("value").setText(value + "");
		}
		
		return e;
	}

	public boolean isFullyDefined() {
		return true;
	}
	
	

	/**
	 * @return the targetMin
	 */
	public Float getTarget() {
		return target;
	}

	/**
	 * @param targetMin the targetMin to set
	 */
	public void setTarget(Float target) {
		this.target = target;
	}

	

	/**
	 * @return the tolerancePerc
	 */
	public Float getTolerancePerc() {
		return tolerancePerc;
	}

	/**
	 * @param tolerancePerc the tolerancePerc to set
	 */
	public void setTolerancePerc(Float tolerancePerc) {
		this.tolerancePerc = tolerancePerc;
	}

	/**
	 * @return the value
	 */
	public Float getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(Float value) {
		this.value = value;
	}

	/**
	 * @return the min
	 */
	public Float getMin() {
		return min;
	}

	/**
	 * @param min the min to set
	 */
	public void setMin(Float min) {
		this.min = min;
	}

	/**
	 * @return the max
	 */
	public Float getMax() {
		return max;
	}

	/**
	 * @param max the max to set
	 */
	public void setMax(Float max) {
		this.max = max;
	}

	public void setMaxSeuil(float parseFloat) {
		this.maxSeuil = parseFloat;
		
	}

	public void setMinSeuil(float parseFloat) {
		this.minSeuil = parseFloat;
		
	}

	/**
	 * @return the maxSeuil
	 */
	public Float getMaxSeuil() {
		return maxSeuil;
	}

	/**
	 * @return the minSeuil
	 */
	public Float getMinSeuil() {
		return minSeuil;
	}

	@Override
	public IComponentDatas copy() {
		StaticGaugeDatas copy = new StaticGaugeDatas();
		
		copy.setMax(max);
		copy.setMaxSeuil(maxSeuil);
		copy.setMin(min);
		copy.setMinSeuil(minSeuil);
		copy.setTarget(target);
		copy.setTolerancePerc(tolerancePerc);
		copy.setValue(value);
		
		return copy;
	}
	
	
	

}
