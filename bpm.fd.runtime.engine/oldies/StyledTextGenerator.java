package bpm.fd.runtime.engine.components;

import java.util.HashMap;

import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.api.core.model.components.definition.styledtext.ComponentStyledTextInput;
import bpm.fd.api.core.model.components.definition.styledtext.StyledTextOptions;
import bpm.fd.api.core.model.events.ElementsEventType;

public class StyledTextGenerator {
	
	private static final String TOOLBAR = "{\ntoolbar :[\n" + 
	"['Source','-','NewPage','Preview'],\n" + 
	"['Cut','Copy','Paste','PasteText','PasteFromWord','-','Print'],\n"+
	"['Undo','Redo','-','Find','Replace','-','SelectAll','RemoveFormat'],\n"+
	"'/'\n,"+
	"['NumberedList','BulletedList','-','Outdent','Indent','Blockquote'],\n"+
	"['JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock'],\n"+
	"['Link','Unlink','Anchor'],\n" + 
	"['Table','HorizontalRule','SpecialChar','PageBreak'],\n" + 
	"'/'\n,"+
	"['Bold','Italic','Underline','Strike','-','Subscript','Superscript']," +
	"['Font','FontSize'],\n" + 
	"['TextColor','BGColor'],\n" + 
	"['Maximize', 'ShowBlocks','-','About'\n]]," + 
	"fullPage : false}";
	
	public static String generateJspBlock(int offset, ComponentStyledTextInput text, String outputParameterName)throws Exception{
		StringBuffer buf = new StringBuffer();
		
		
		for(int i = 0; i < offset * 4; i++){
			buf.append(" ");
		}
		String event = generateEvents(text, null, false);
//		<textarea cols="80" id="editor1" name="editor1" rows="10"></textarea>
		StyledTextOptions opt = (StyledTextOptions)text.getOptions(StyledTextOptions.class);
		
		
		buf.append("<textarea id='" + text.getId() + "' name='" + text.getId() + "' cols='" + opt.getColumnsNumber());
		buf.append("' rows='" + opt.getRowsNumber() + "' ");
		if (text.getClass() != null && !"".equals(text.getCssClass())){
			buf.append(" class='" + text.getCssClass() + "' ");
		}
		buf.append(event);
		buf.append(">");
		
		buf.append(opt.getInitialValue() + "</textarea>\n");
		
		buf.append("<script type=\"text/javascript\">\n");
		buf.append("CKEDITOR.replace( '" + text.getId() + "'," + TOOLBAR + ");");
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
