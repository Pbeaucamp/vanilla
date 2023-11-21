package bpm.fd.api.core.model.components.definition.chart.fusion.options;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.components.definition.IComponentOptions;
import bpm.fd.api.core.model.components.definition.chart.ChartNature;

public class LineCombinationOption implements IComponentOptions{
	public static final int KEY_LINE_SERIE_NAME = 0;

	
	private static final String[] i18nKeys = new String[]{};
	private static final String[] standardKeys = new String[] {
		"lineSerieName"};
	
	private String lineSerieName = "";
	

	
	public IComponentOptions getAdapter(Object type) {
		if (type instanceof ChartNature){
			ChartNature nature = (ChartNature)type;
			try{
				if (nature.isLineCombnation()){
					return this;
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			
			
			
		}
		return null;
	}

	
	public String getDefaultLabelValue(String key) {
		
		return null;
	}

	public Element getElement() {
		Element e = DocumentHelper.createElement("lineCombinationOptions");
		
		for(String s : standardKeys){
			e.addAttribute(s, getValue(s));
		}
		return e;
	}

	public String[] getInternationalizationKeys() {
		return i18nKeys;
	}

	public String[] getNonInternationalizationKeys() {
		return standardKeys;
	}

	public String getValue(String key) {
		if (standardKeys[KEY_LINE_SERIE_NAME].equals(key)){
			return getLineSerieName();
		}

		
		return null;
	}
	
	public void setLineSerieName(String serieName){
		this.lineSerieName = serieName;
	}
	
	public String getLineSerieName(){
		return this.lineSerieName ;
	}




	@Override
	public IComponentOptions copy() {
		LineCombinationOption copy = new LineCombinationOption();
		
		copy.setLineSerieName(lineSerieName);

		
		return copy;
	}

}
