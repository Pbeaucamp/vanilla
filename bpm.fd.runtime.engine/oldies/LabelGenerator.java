package bpm.fd.runtime.engine.components;

import java.util.HashMap;

import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.api.core.model.components.definition.IComponentOptions;
import bpm.fd.api.core.model.components.definition.text.LabelComponent;
import bpm.fd.api.core.model.components.definition.text.LabelRenderer;
import bpm.fd.api.core.model.events.ElementsEventType;

public class LabelGenerator {
	public static String generateJspBlock(int offset, LabelComponent label, String outputParameterName)throws Exception{
		StringBuffer buf = new StringBuffer();
		
		
		for(int i = 0; i < offset * 4; i++){
			buf.append(" ");
		}
		String event = generateEvents(label, null, false);
		
		for(IComponentOptions opt : label.getOptions()){
			
			buf.append("<% out.write(\""+ ((LabelRenderer)label.getRenderer()).getOpenTag() + " id='" + label.getId() + "'");
			for(String key : opt.getInternationalizationKeys()){
				if (label.getCssClass() != null || ! "".equals(label.getCssClass())){
					buf.append(" class='" + label.getCssClass() + "' ");
				}
				buf.append(" " + event + ">\" + i18nReader.getLabel(clientLocale, \"" + label.getName() + "." + key + "\", _parameterMap) + \""+ ((LabelRenderer)label.getRenderer()).getCloseTag() + "\"); %>\n");
			}
		}
		
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
