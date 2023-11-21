package bpm.fd.api.core.model.components.definition.olap;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentHelper;
//import org.dom4j.Element;
import bpm.fa.api.olap.Element;


import bpm.fd.api.core.model.components.definition.IComponentOptions;

public class FaViewOption implements IComponentOptions{

	public static final int KEY_INTERACTIVE= 0;
	public static final int KEY_DIMENSIONS = 1;
	public static final int KEY_CUBEFUNCTIONS = 2;
	public static final int KEY_LISTDIMENSIONS = 3;
	
	
	
	private static final String[] standardKeys = new String[] {"isInteractive", "showDimensions", "showCubeFunctions", "listDimensions"};
	private boolean isInteractive =false;
	private boolean showDimensions = false;
	private boolean showCubeFunctions = false;
	private List<Element> elements =null;
	


	@Override
	public org.dom4j.Element getElement() {
		org.dom4j.Element e = DocumentHelper.createElement("faViewOption");
		e.addAttribute("isInteractive", isInteractive() + "");
		e.addAttribute("showDimensions", isShowDimensions() + "");
		e.addAttribute("showCubeFunctions", isShowCubeFunctions() + "");
		e.addAttribute("listDimension",getStringElements());
		return e;
	}

	@Override
	public IComponentOptions getAdapter(Object type) {
		return this;
	}

	@Override
	public String[] getInternationalizationKeys() {
		return new String[]{};
	}

	public String[] getNonInternationalizationKeys() {
		return standardKeys;
	}

	@Override
	public String getValue(String key) {
		if (standardKeys[0].equals(key)){
			return isInteractive()? "true" : "false";
		}
		if (standardKeys[1].equals(key)){
			return isShowDimensions()? "true" : "false";
		}
		if (standardKeys[2].equals(key)){
			return isShowCubeFunctions()? "true" : "false";
		}
		if (standardKeys[3].equals(key)){
			//return getElements()!=null? getElements().toString() : null;
			return getStringElements();
		}
		
		return null;
	}
	
	
	@Override
	public String getDefaultLabelValue(String key) {
		return null;
	}

	@Override
	public IComponentOptions copy() {
		FaViewOption copy = new FaViewOption();
		
		copy.setInteraction(isInteractive);;
		copy.setShowDimensions(showDimensions);
		copy.setShowCubeFunctions(showCubeFunctions);
		copy.setElements(elements);
		
		return copy;
	}
	
	public boolean isInteractive(){
		return isInteractive;
	}
	
	public void setInteraction(boolean isInteractive){
		this.isInteractive= isInteractive;
	}
	
	public boolean isShowDimensions(){
		return showDimensions;
	}
	
	public List<Element> getElements(){
		return elements;
	}
	
	public String getStringElements(){
		if(elements!=null){
			String elementContent = "";
			for (bpm.fa.api.olap.Element element : elements){
				elementContent+=element.getCaption()+","+element.getName()+","+element.getUniqueName()+";";
			}
			return elementContent;
		} else return null;
		//return elements!=null? elements.toString() : null;
	}
	
	public String getElementsUname(){
		if(elements!=null){
			String elementContent = "";
			for (bpm.fa.api.olap.Element element : elements){
				elementContent+=element.getUniqueName()+";";
			}
			return elementContent;
		} else return null;	
	}
	
	
	public void setShowDimensions(boolean showDimensions){
		this.showDimensions = showDimensions;
	}
	
	public boolean isShowCubeFunctions(){
		return showCubeFunctions;
	}
	
	public void setShowCubeFunctions(boolean showCubeFunctions){
		this.showCubeFunctions= showCubeFunctions;
	}
	
	public void setElements(List<bpm.fa.api.olap.Element> elements) {
		this.elements = elements;
	}
	
	public void setElementsFromString(String stringElement) {
		if (stringElement!=null){
			String[] stringSplite= stringElement.split(";");
			List<bpm.fa.api.olap.Element> listElement = new ArrayList<bpm.fa.api.olap.Element>();
			for (String element : stringSplite){
				String[] attributes = element.split(",");
				Element olapElement = new Element( attributes[1], attributes[2], attributes[0])  {}; 
				listElement.add(olapElement);
			}
			if (listElement!=null && !listElement.isEmpty())
				this.elements=listElement;
		}
	}

}
