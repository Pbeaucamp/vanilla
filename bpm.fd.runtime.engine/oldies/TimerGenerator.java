package bpm.fd.runtime.engine.components;

import java.util.HashMap;

import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.api.core.model.components.definition.timer.ComponentTimer;
import bpm.fd.api.core.model.components.definition.timer.TimerOptions;
import bpm.fd.api.core.model.events.ElementsEventType;

public class TimerGenerator {
	
	
	public static String generateJspBlock(int offset, ComponentTimer text, String outputParameterName)throws Exception{
		StringBuffer buf = new StringBuffer();
		
		
		for(int i = 0; i < offset * 4; i++){
			buf.append(" ");
		}
		String event = generateEvents(text, null, false);
//		<textarea cols="80" id="editor1" name="editor1" rows="10"></textarea>
		
		
//		<div id="timer">
//		<script type="text/javascript">
//			   var timer = new Timer('timer', 3000);
//			   timer.Draw();
//			   timer.startCountDown();
//		</script>  
//	   </div>
		buf.append("<div id='" + text.getId() + "' name='" + text.getId() );
		if (text.getClass() != null && !"".equals(text.getCssClass())){
			buf.append(" class='" + text.getCssClass() + "' ");
		}
		buf.append(event);
		buf.append(" />\n");
		
		TimerOptions opt = (TimerOptions)text.getOptions(TimerOptions.class);
		
		buf.append("<script type=\"text/javascript\">\n");
		buf.append("    var " + text.getId() + " = new Timer('" + text.getId() + "', ");
		buf.append(opt.getDelay());
		if (opt.getLabel() != null){
			buf.append(", '" + opt.getLabel() + "', ");
			buf.append(opt.isStartOnLoad());
		}
		buf.append(");\n");
		buf.append("    " + text.getId() + ".Draw();\n");
		
		buf.append("</script>\n");
		
		
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
