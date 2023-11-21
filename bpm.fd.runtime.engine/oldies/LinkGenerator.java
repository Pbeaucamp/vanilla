package bpm.fd.runtime.engine.components;

import java.util.HashMap;

import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.api.core.model.components.definition.link.ComponentLink;
import bpm.fd.api.core.model.components.definition.link.LinkOptions;
import bpm.fd.api.core.model.events.ElementsEventType;

public class LinkGenerator {

	
	
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
	
	public static String generateJspBlock(int offset, ComponentLink link, String outputParameterName)throws Exception{
		StringBuffer buf = new StringBuffer();
		
		
		for(int i = 0; i < offset * 4; i++){
			buf.append(" ");
		}
		
		LinkOptions opt = (LinkOptions)link.getOptions(LinkOptions.class);
		String event = generateEvents(link, null, true);
		buf.append("<a href=\"");
		buf.append("<%= i18nReader.getLabelValue(clientLocale, \"" + opt.getUrl()  + "\", _parameterMap) %>\"");
		
		if (link.getCssClass() != null && !link.getCssClass().equals("")){
			buf.append(" class=\"" + link.getCssClass() + "\"");
		}
		
		buf.append(" " + event + ">");
		buf.append("<%= i18nReader.getLabel(clientLocale, \"" + link.getName() + ".label\", _parameterMap) %>");
		buf.append("</a><br>\n");
		
		
		
		
		return buf.toString();
	}
	
	
	
}
