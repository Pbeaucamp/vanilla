package bpm.fd.api.core.model.components.definition;

import java.util.Collection;
import java.util.HashMap;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class ComponentStyle {
	private HashMap<String, String> styles = new HashMap<String, String>();
	
	public String getStyleFor(String element){
		return styles.get(element);
	}
	
	public Collection<String> getObjects(){
		return styles.keySet();
	}
	
	public void setStyleFor(String element, String style){
		styles.put(element, style);
	}
	
	public Element getElement(){
		Element e = DocumentHelper.createElement("componentStyle");
		for(String s : getObjects()){
			if (getStyleFor(s) != null){
				e.addElement("style").addAttribute("objectName", s).setText(getStyleFor(s));
			}
		}
		return e;
	}
}
