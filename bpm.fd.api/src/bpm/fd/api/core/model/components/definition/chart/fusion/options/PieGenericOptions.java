package bpm.fd.api.core.model.components.definition.chart.fusion.options;

import org.dom4j.Element;

public class PieGenericOptions extends GenericOptions{
	private int slicingDistance = 10;
	private int pieSliceDepth = 20;
	private int pieRadius = 100;
	
	
	public PieGenericOptions(){}
	
	public PieGenericOptions(GenericOptions opt){
		this.setCaption(opt.getCaption());
		this.setShowLabel(opt.isShowLabel());
		this.setShowValues(opt.isShowValues());
		this.setSubCaption(opt.getSubCaption());
		this.setMultiLineLabels(opt.isMultiLineLabels());
		this.setBaseFontColor(opt.getBaseFontColor());
		this.setBaseFontSize(opt.getBaseFontSize());
		this.setLabelSize(opt.getLabelSize());
		this.setExportEnable(opt.isExportEnable());
		this.setDynamicLegend(opt.isDynamicLegend());
		
		if(opt instanceof PieGenericOptions) {
			PieGenericOptions oldOpt = (PieGenericOptions) opt;
			this.setSlicingDistance(oldOpt.getSlicingDistance());
			this.setPieSliceDepth(oldOpt.getPieSliceDepth());
			this.setPieRadius(oldOpt.getPieRadius());
		}
	}
	
	
	/**
	 * @return the pieRadius
	 */
	public int getPieRadius() {
		return pieRadius;
	}

	/**
	 * @param pieRadius the pieRadius to set
	 */
	public void setPieRadius(int pieRadius) {
		this.pieRadius = pieRadius;
	}

	/**
	 * @return the slicingDistance
	 */
	public int getSlicingDistance() {
		return slicingDistance;
	}
	/**
	 * @param slicingDistance the slicingDistance to set
	 */
	public void setSlicingDistance(int slicingDistance) {
		this.slicingDistance = slicingDistance;
	}
	/**
	 * @return the pieSliceDepth
	 */
	public int getPieSliceDepth() {
		return pieSliceDepth;
	}
	/**
	 * @param pieSliceDepth the pieSliceDepth to set
	 */
	public void setPieSliceDepth(int pieSliceDepth) {
		this.pieSliceDepth = pieSliceDepth;
	}
	/* (non-Javadoc)
	 * @see bpm.fd.api.core.model.components.definition.chart.fusion.options.GenericOptions#getElement()
	 */
	@Override
	public Element getElement() {
		Element e =  super.getElement();
		e.setName("pieGenericOptions");
		e.addAttribute("slicingDistance", getSlicingDistance() + "");
		e.addAttribute("pieSliceDepth", getPieSliceDepth() + "");
		e.addAttribute("pieRadius", getPieRadius() + "");
		
		return e;
	}
	
	public String[] getNonInternationalizationKeys() {
		String[] s = new String[super.getNonInternationalizationKeys().length + 3];
		for(int i = 0; i < super.getNonInternationalizationKeys().length; i++){
			s[i] = super.getNonInternationalizationKeys()[i];
		}
		s[s.length - 3] = "pieRadius";
		s[s.length - 2] = "slicingDistance";
		s[s.length - 1] = "pieSliceDepth";
		
		return s;
	}
	public String getValue(String key) {
		String s = super.getValue(key);
		
		if (s == null){
			if ("slicingDistance".equals(key)){
				return getSlicingDistance() + "";
			}
			else if ("pieSliceDepth".equals(key)){
				return getPieSliceDepth() + "";
			}
			else if ("pieRadius".equals(key)){
				return getPieRadius() + "";
			}
		}
		
		return s;
	}
}

