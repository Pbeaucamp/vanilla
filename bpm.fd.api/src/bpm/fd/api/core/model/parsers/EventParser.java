package bpm.fd.api.core.model.parsers;

import java.util.List;

import org.dom4j.Element;

import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.api.core.model.events.ElementsEventType;

public class EventParser {

	public static void parse(IBaseElement element, Element root){
		
	
		Element events = root.element("events");
		
		if (events == null){
			return;
		}
		
		for(Element e : (List<Element>)events.elements("event")){
			String type = e.attributeValue("type");
			if (type == null){
				continue;
			}
			ElementsEventType eventType = ElementsEventType.valueOf(type);
			String script = e.getStringValue();
			
			element.setJavaScript(eventType, script);
		}
		
		
	}
	
}
