package bpm.fd.runtime.engine.components;

import java.util.HashMap;

import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.api.core.model.components.definition.buttons.ButtonOptions;
import bpm.fd.api.core.model.components.definition.buttons.ComponentButtonDefinition;
import bpm.fd.api.core.model.events.ElementsEventType;

public class ButtonGenerator {
	public static String generateJspBlock(int offset, ComponentButtonDefinition button, String outputParameterName)throws Exception{
		StringBuffer buf = new StringBuffer();
		
		
		for(int i = 0; i < offset * 4; i++){
			buf.append(" ");
		}
		
		String event = generateEvents(button, new HashMap<ElementsEventType, String>(){{put(ElementsEventType.onClick, "setLocation();");}}, false);
		
		ButtonOptions opt = (ButtonOptions)button.getOptions(ButtonOptions.class);
		
		buf.append("<% out.write(\"<input id='" + button.getId() + "' name='" + button.getName() + "' type='submit' ");
		buf.append(" " + event);
		buf.append(" value='");
		buf.append("" + opt.getLabel() + "'/>\"); %>\n");

		return buf.toString();
	}
	
	/**
	 * generate the STring for events
	 * 
	 * @param element
	 * @param defaultEvents : contains the default behavior for the given Type
	 * @return
	 */
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
