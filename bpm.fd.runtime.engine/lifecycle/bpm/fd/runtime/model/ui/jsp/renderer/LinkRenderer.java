package bpm.fd.runtime.model.ui.jsp.renderer;

import java.awt.Rectangle;

import org.eclipse.datatools.connectivity.oda.IResultSet;

import bpm.fd.api.core.model.components.definition.ComponentParameter;
import bpm.fd.api.core.model.components.definition.link.ComponentLink;
import bpm.fd.api.core.model.components.definition.link.LinkOptions;
import bpm.fd.runtime.model.Component;
import bpm.fd.runtime.model.DashState;

public class LinkRenderer extends AsbtractCssApplier implements IHTMLRenderer<ComponentLink>{
	
	public String getJavaScriptFdObjectVariable(ComponentLink link){
		StringBuffer buf = new StringBuffer();
		buf.append("<script type=\"text/javascript\">\n");
		buf.append("    fdObjects[\"" + link.getName() + "\"]= new FdLink(\"" + link.getName() + "\")" + ";\n");
		buf.append("</script>\n");
		return buf.toString();
	}
	public String getHTML(Rectangle layout, ComponentLink link, DashState state, IResultSet datas, boolean refresh){
		
		LinkOptions opt = (LinkOptions)link.getOptions(LinkOptions.class);
		StringBuffer buf = new StringBuffer();
		
		String url = opt.getUrl();
		String label =  null;
		
		label = state.getDashInstance().getDashBoard().getI18Reader().getLabel(
				state, 
				link.getId() , opt.getInternationalizationKeys()[LinkOptions.KEY_LABEL]);
		
		if (label == null){
			label = opt.getLabel();
		}
		
		
		
		for(ComponentParameter p : link.getParameters()){
			Component provider = (Component)state.getDashInstance().getDashBoard().getDesignParameterProvider(p);
			
			String pValue = state.getComponentValue(provider.getName());
			if (pValue == null){
				pValue = "";
			}
			url = url.replace("{$" + p.getName() + "}", pValue);
			label = label.replace("{$" + p.getName() + "}", pValue);
		}
		
		buf.append(getComponentDefinitionDivStart(layout, link));
		
		StringBuffer content = new StringBuffer();
		
		content.append("<a href='" + url + "' target='_blank' >" + label + "</a>\n");
		buf.append(content.toString());
		buf.append(getComponentDefinitionDivEnd());
		buf.append("<script type=\"text/javascript\">\n");
		buf.append("    fdObjects[\"" + link.getName() + "\"]= new FdLink(\"" + link.getName() + "\")" + ";\n");
		buf.append("</script>\n");
		if (refresh){
			return content.toString();
		}
		return buf.toString();
	}
}
