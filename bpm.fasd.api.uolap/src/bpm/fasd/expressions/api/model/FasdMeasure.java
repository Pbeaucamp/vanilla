package bpm.fasd.expressions.api.model;

import org.fasd.olap.OLAPMeasure;

import bpm.studio.expressions.core.measures.impl.Measure;

public class FasdMeasure extends Measure{
	private OLAPMeasure olapMeasure; 
	
	public FasdMeasure(OLAPMeasure m){
		super(new FasdColumnField(m));
		this.olapMeasure = m;
	}
	
	public String getMdx(){
		StringBuffer buf = new StringBuffer();
		buf.append("[Measures].[");
		buf.append(olapMeasure.getName());
		buf.append("]");
		
		return buf.toString();
	}
}
