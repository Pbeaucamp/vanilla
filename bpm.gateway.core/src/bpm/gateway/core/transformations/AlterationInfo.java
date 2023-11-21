package bpm.gateway.core.transformations;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.StreamElement;

public class AlterationInfo {
	private String className = "java.lang.Object";
	private String format = "";
	private StreamElement element;
	
	public Element getElement(){
		Element e = DocumentHelper.createElement("alteration");
		e.addElement("className").setText(className);
		if (format != null){
			e.addElement("format").setText(format);
		}
		
		return e;
	}
	
	public final StreamElement getStreamElement() {
		return element;
	}
	public final void setStreamElement(StreamElement element) {
		this.element = element;
	}
	public final String getClassName() {
		return className;
	}
	public final void setClassName(String className) {
		
		if (!(className.equals("java.lang.String") || className.equals("java.util.Date"))){
			this.format = "";
		}
		
		this.className = className;
		
	}
	public final String getFormat() {
		return format;
	}
	public final void setFormat(String format) {
		if (className.equals("java.lang.String")){
			this.format = format;
		}
		
	}
	
	
	
}
