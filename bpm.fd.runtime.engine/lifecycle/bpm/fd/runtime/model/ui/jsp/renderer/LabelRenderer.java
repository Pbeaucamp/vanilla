package bpm.fd.runtime.model.ui.jsp.renderer;

import java.awt.Rectangle;
import java.util.HashMap;

import org.eclipse.datatools.connectivity.oda.IResultSet;

import bpm.fd.api.core.model.components.definition.ComponentParameter;
import bpm.fd.api.core.model.components.definition.text.LabelComponent;
import bpm.fd.api.core.model.components.definition.text.LabelOptions;
import bpm.fd.api.core.model.events.ElementsEventType;
import bpm.fd.runtime.model.Component;
import bpm.fd.runtime.model.DashState;

public class LabelRenderer extends AsbtractCssApplier implements IHTMLRenderer<LabelComponent>{
	
	public String getJavaScriptFdObjectVariable(LabelComponent label){
		StringBuffer buf = new StringBuffer();
		buf.append("<script type=\"text/javascript\">\n");
		buf.append("    fdObjects[\"" + label.getName() + "\"]= new FdLabel(\"" + label.getName() + "\")" + ";\n");
		buf.append("</script>\n");
		return buf.toString();
	}
	
	public String getHTML(Rectangle layout, LabelComponent label, DashState state, IResultSet datas, boolean refresh){
		String tag = ((bpm.fd.api.core.model.components.definition.text.LabelRenderer)label.getRenderer()).getOpenTag();
		String etag = ((bpm.fd.api.core.model.components.definition.text.LabelRenderer)label.getRenderer()).getCloseTag();
		LabelOptions opts = (LabelOptions)label.getOptions(LabelOptions.class); 
		
		
		
		StringBuffer buf = new StringBuffer();
		buf.append(getComponentDefinitionDivStart(layout, label));
		StringBuffer content = new StringBuffer();
		
		content.append(tag);
		
		HashMap<ElementsEventType, String> defaultEvents = new HashMap<ElementsEventType, String>();
		String event = generateEvents(label, defaultEvents, true);
		
		content.append(" " + event);
		
		//content.append(" id='" + label.getName() + "' ");
		content.append(">");
		
		String textLabel = null;
		
//		textLabel = state.getDashInstance().getDashBoard().getI18Reader().getLabel(
//				state, 
//				label.getId() , opts.getInternationalizationKeys()[LabelOptions.KEY_TEXT]);
		
		if (textLabel == null){
			textLabel = new String(opts.getText());
			
			for(ComponentParameter p : label.getParameters()){
				Component provider = (Component)state.getDashInstance().getDashBoard().getDesignParameterProvider(p);
				
				String pValue = state.getComponentValue(provider.getName());
				if (pValue == null){
					pValue = "Undefined";
				}
				textLabel = textLabel.replace("{$" + p.getName() + "}", pValue);
			}
		}
		
		
		content.append(textLabel);
				
		content.append(etag);
		content.append("\n");
		buf.append(content.toString());
		buf.append(getComponentDefinitionDivEnd());
		
		if (refresh){
			return content.toString();
		}
		buf.append("<script type=\"text/javascript\">\n");
		buf.append("    fdObjects[\"" + label.getName() + "\"]= new FdLabel(\"" + label.getName() + "\")" + ";\n");
		buf.append("</script>\n");
		return buf.toString();
	}
	

}
