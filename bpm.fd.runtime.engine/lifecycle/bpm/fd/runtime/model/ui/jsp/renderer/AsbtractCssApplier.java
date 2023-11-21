package bpm.fd.runtime.model.ui.jsp.renderer;

import java.awt.Rectangle;
import java.util.HashMap;

import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.events.ElementsEventType;

public abstract class AsbtractCssApplier {

	public String getLayoutStyleCss(Rectangle layout, String additional){
		StringBuilder layoutS = new StringBuilder();
		if (layout != null){
			layoutS.append(" style='height:");
			layoutS.append(layout.height);
			layoutS.append("px;");
			layoutS.append("width:");
			layoutS.append(layout.width);
			layoutS.append("px;");
			layoutS.append("position:absolute;");
			layoutS.append("top:");
			layoutS.append(layout.y);
			layoutS.append("px;");
			layoutS.append("left:");
			layoutS.append(layout.x);
			layoutS.append("px;");
			if (additional != null){
				layoutS.append(additional);
			}
			layoutS.append("'");
			
		}
		return layoutS.toString();
	}
	public String getComponentDefinitionDivStart(Rectangle layout,IComponentDefinition def){
		StringBuffer buf = new StringBuffer();
		
		String css = " class='";
		if (def.getCssClass() != null){
			css +=def.getCssClass(); 
		}
		css += " cell' ";
		

		
		buf.append("<div id='" + def.getName() + "' name='" + def.getId() + "'" + css  + getLayoutStyleCss(layout, "") + ">\n");
		
//		buf.append("<script type=\"text/javascript\">\n");
//		buf.append("$(function() {$( \"#" + def.getName() + "\" ).draggable();});\n");
//		buf.append("</script>\n");
		
		
		
		return buf.toString();
	}
	
	public String getComponentDefinitionCanvas(Rectangle layout,IComponentDefinition def, String additional, boolean displayLegend){
		StringBuffer buf = new StringBuffer();
		
		String css = " class='";
		if (def.getCssClass() != null){
			css +=def.getCssClass(); 
		}
		css += " cell' ";
		
		int height = displayLegend ? layout.height - 50 : layout.height;
		
		String style = " style=\"width:" + layout.width + "px !important; height:" + height + "px !important;\"";
		buf.append("<canvas id='" + def.getName() + "_canvas' name='" + def.getId() + "'" + style+"></canvas>\n");
		
		return buf.toString();
	}
	
	public String getComponentDefinitionDivStart(Rectangle layout,IComponentDefinition def, String additional){
		StringBuffer buf = new StringBuffer();
		
		String css = " class='";
		if (def.getCssClass() != null){
			css +=def.getCssClass(); 
		}
		css += " cell' ";

		buf.append("<div id='" + def.getName() + "' name='" + def.getId() + "'" + css  + getLayoutStyleCss(layout, additional) + ">\n");
		
		return buf.toString();
	}
	
	public String getComponentDefinitionDivEnd(){
		return "</div>\n";
	}
	
	/**
	 * generate the STring for events
	 * 
	 * @param element
	 * @param defaultEvents : contains the default behavior for the given Type
	 * @return
	 */
	public static String generateEvents(IBaseElement element, HashMap<ElementsEventType, String> defaultEvents, boolean pureHtml){
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
					//buf.append(";" );
				}
				else{
					buf.append(" " + type.name() + (pureHtml ? "=\"" : "=\\\""));
				}
				//whats the fuck
				buf.append(defaultEvents.get(type));
				buf.append((pureHtml ? "\"" : "\\\"") + " ;");
				
			}
			
		}
		
		return buf.toString();
	}
}
