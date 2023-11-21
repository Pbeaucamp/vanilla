package bpm.fd.runtime.engine.components;

import java.util.HashMap;

import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.api.core.model.components.definition.image.ComponentPicture;
import bpm.fd.api.core.model.events.ElementsEventType;

public class PictureGenerator {
	public static String generateJspBlock(int offset, ComponentPicture picture, String outputParameterName)throws Exception{
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < offset * 4; i++){
			buf.append(" ");
		}
		String event = generateEvents(picture, null, true)	;
		buf.append("<img id='" + picture.getId() + "' src='" + picture.getPictureUrl() + "' " + event + "/>\n");
		
		return buf.toString();
	}
	
	private static String generateEvents(IBaseElement element, HashMap<ElementsEventType, String> defaultEvents, boolean pureHtml){
		StringBuffer buf = new StringBuffer();
		
		for(ElementsEventType type : element.getEventsType()){
			String sc = element.getJavaScript(type);
			if (sc != null && !"".equals(sc.trim())){
				buf.append(" " + type.name() + (pureHtml ? "=\"" : "=\\\"") + sc.replace("\r\n", "").replace("\n", ""));
				
				if (defaultEvents == null || defaultEvents.get(type) == null){
					buf.append((pureHtml ? "\"" : "\\\"") + " ");
					continue;
				}
				
			}
			if (defaultEvents != null && defaultEvents.get(type) != null){
				if (sc != null && !"".equals(sc.trim())){
					buf.append(";" );
				}
				else{
					buf.append(" " + type.name() + (pureHtml ? "=\"" : "=\\\""));
				}
				buf.append(defaultEvents.get(type));
				buf.append((pureHtml ? "\"" : "\\\"") + " ");
			}
			
		}
		
		return buf.toString();
	}
}
