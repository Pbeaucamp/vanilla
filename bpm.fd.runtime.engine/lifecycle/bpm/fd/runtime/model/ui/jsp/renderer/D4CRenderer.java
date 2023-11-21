package bpm.fd.runtime.model.ui.jsp.renderer;

import java.awt.Rectangle;

import org.eclipse.datatools.connectivity.oda.IResultSet;

import bpm.fd.api.core.model.components.definition.ComponentParameter;
import bpm.fd.api.core.model.components.definition.jsp.ComponentD4C;
import bpm.fd.api.core.model.components.definition.jsp.D4COptions;
import bpm.fd.runtime.model.Component;
import bpm.fd.runtime.model.DashState;

public class D4CRenderer extends AsbtractCssApplier implements IHTMLRenderer<ComponentD4C> {
	
	public String getHTML(Rectangle layout, ComponentD4C dashlet, DashState state, IResultSet datas, boolean refresh){
		D4COptions opts = (D4COptions)dashlet.getOptions(D4COptions.class);
		
		StringBuffer buf = new StringBuffer();
		buf.append(getComponentDefinitionDivStart(layout, dashlet));
		
		
		String src = new String(dashlet.getUrl());
		
		for(ComponentParameter p : dashlet.getParameters()){
			Component provider = (Component)state.getDashInstance().getDashBoard().getDesignParameterProvider(p);
			
			String pValue = state.getComponentValue(provider.getName());
			if (pValue == null){
				pValue = "";
			}
			src = src.replace("{$" + p.getName() + "}", pValue);
		}
		
		StringBuffer content = new StringBuffer();
		content.append("<iframe src='" + src + "' ");
		content.append(" width='" + (layout.width - 5) + "'");
		content.append(" height='" + (layout.height - 4) + "'");
		if (opts.getBorder_width() > 0){
			content.append(" frameborder='1'");
		}
		else{
			content.append(" frameborder='0'");
		}
		content.append(" >");
		content.append("iframe not supported");
		content.append("</iframe>\n");
		
		buf.append(content.toString());
		buf.append(getComponentDefinitionDivEnd());
		
		if (refresh){
			return content.toString();
		}
		buf.append("<script type=\"text/javascript\">\n");
		buf.append("    fdObjects[\"" + dashlet.getName() + "\"]= new FdFrame(\"" + dashlet.getName() + "\")" + ";\n");
		buf.append("</script>\n");
		return buf.toString();
	}

	public String getJavaScriptFdObjectVariable(ComponentD4C dashlet){
		StringBuffer buf = new StringBuffer();
		buf.append("<script type=\"text/javascript\">\n");
		buf.append("    fdObjects[\"" + dashlet.getName() + "\"]= new FdFrame(\"" + dashlet.getName() + "\")" + ";\n");
		buf.append("</script>\n");
		return buf.toString();
	}
}
