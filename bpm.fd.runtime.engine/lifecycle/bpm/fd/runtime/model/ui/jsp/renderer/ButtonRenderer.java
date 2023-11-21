package bpm.fd.runtime.model.ui.jsp.renderer;

import java.awt.Rectangle;
import java.util.HashMap;

import org.eclipse.datatools.connectivity.oda.IResultSet;

import bpm.fd.api.core.model.components.definition.buttons.ButtonOptions;
import bpm.fd.api.core.model.components.definition.buttons.ComponentButtonDefinition;
import bpm.fd.api.core.model.events.ElementsEventType;
import bpm.fd.runtime.model.DashState;

public class ButtonRenderer extends AsbtractCssApplier implements IHTMLRenderer<ComponentButtonDefinition>{
	public String getHTML(Rectangle layout, ComponentButtonDefinition button, DashState state, IResultSet datas, boolean refresh){
		ButtonOptions opts = (ButtonOptions)button.getOptions(ButtonOptions.class);
		
		StringBuffer buf = new StringBuffer();
		buf.append(getComponentDefinitionDivStart(layout, button));
		
		
		StringBuffer content = new StringBuffer();
		HashMap<ElementsEventType, String> defaultEvents = new HashMap<ElementsEventType, String>();
		String event = generateEvents(button, defaultEvents, true);
		
		
		
		content.append("<input id='" + button.getId() + "' name='" + button.getName() + "' type='button' ");
		content.append(" " + event);
		content.append(" value='");
		

		
		content.append("" + opts.getLabel() + "'/>\n");
		buf.append(content.toString());
		buf.append(getComponentDefinitionDivEnd());
		
//		if (refresh){
//			return content.toString();
//		}
//		buf.append("<script type=\"text/javascript\">\n");
//		buf.append("    fdObjects[\"" + dashlet.getName() + "\"]= new FdFrame(\"" + dashlet.getName() + "\")" + ";\n");
//		buf.append("</script>\n");
		return buf.toString();
	}

	@Override
	public String getJavaScriptFdObjectVariable(
			ComponentButtonDefinition definition) {
		
		return "";
	}
}
