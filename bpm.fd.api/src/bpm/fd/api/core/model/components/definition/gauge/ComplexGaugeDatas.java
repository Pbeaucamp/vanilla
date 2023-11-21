package bpm.fd.api.core.model.components.definition.gauge;

import org.dom4j.Element;

import bpm.fd.api.core.model.components.definition.IComponentDatas;

public class ComplexGaugeDatas extends FmGaugeDatas{
	private Float minValue;
	private Float maxValue;
	
	private boolean manualValues = false;
	private boolean useTarget = true;
	private boolean useExpected = true;
	private boolean useFieldForTarget = false;
	private Integer targetIndex;
	
	public void setTargetIndex(Integer index){
		this.targetIndex = index;
	}
	
	public Integer getTargetIndex(){
		return targetIndex;
	}
	public void setUseFieldForTarget(boolean val){
		useFieldForTarget = val;
	}
	
	public boolean isUseFieldForTarget(){
		return useFieldForTarget;
	}
	
	/**
	 * @return the minValue
	 */
	public Float getMinValue() {
		return minValue;
	}
	/**
	 * @param minValue the minValue to set
	 */
	public void setMinValue(Float minValue) {
		this.minValue = minValue;
	}

	
	
	/**
	 * @return the maxValue
	 */
	public Float getMaxValue() {
		return maxValue;
	}
	/**
	 * @param maxValue the maxValue to set
	 */
	public void setMaxValue(Float maxValue) {
		this.maxValue = maxValue;
	}
	public Element getElement() {
		Element e = super.getElement();
		e.addAttribute("rangeManual", isRangeManual() + "")
		 .addAttribute("useTarget", isTargetNeeded()+ "")
		 .addAttribute("useExpectedFields", isExpectedFieldsUsed() + "")
		 .addAttribute("useFieldForTarget", isUseFieldForTarget() + "");
		
		if (getTargetIndex() != null){
			e.addElement("targetIndex").setText(getTargetIndex()+ "");
		}
		if (getMaxValue() != null){
			e.addElement("maxValue").setText(getMaxValue()+ "");
		}
		
		if (getMinValue() != null){
			e.addElement("minValue").setText(getMinValue()+ "");
		}
		return e;
		
	}
	
	public boolean isExpectedFieldsUsed(){

		return useExpected;
	}
	
	public boolean isRangeManual(){
		return manualValues;
	}
	
	public boolean isTargetNeeded(){
		return useTarget;
	}
	
	public void setUseTarget(boolean v){
		this.useTarget = v;
	}
	public void setManualRange(boolean v){
		this.manualValues = v;
	}
	public void setUseExpected(boolean v){
		this.useExpected= v;
	}

	@Override
	public IComponentDatas copy() {
		ComplexGaugeDatas copy = new ComplexGaugeDatas();
		
		copy.setDataSet(getDataSet());
		copy.setIndexMax(getIndexMax());
		copy.setIndexMaxSeuil(getIndexMaxSeuil());
		copy.setIndexMin(getIndexMin());
		copy.setIndexMinSeuil(getIndexMinSeuil());
		copy.setIndexTolerance(getIndexTolerance());
		copy.setIndexValue(getIndexValue());
		copy.setManualRange(manualValues);
		copy.setMaxValue(maxValue);
		copy.setMinValue(minValue);
		copy.setTargetIndex(getTargetIndex());
		copy.setTargetValue(getTargetValue());
		copy.setUseExpected(useExpected);
		copy.setUseFieldForTarget(useFieldForTarget);
		copy.setUseTarget(useTarget);
		
		return copy;
	}
	
	
}
