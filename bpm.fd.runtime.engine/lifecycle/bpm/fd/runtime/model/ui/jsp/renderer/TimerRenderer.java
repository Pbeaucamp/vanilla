package bpm.fd.runtime.model.ui.jsp.renderer;

import java.awt.Rectangle;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.eclipse.datatools.connectivity.oda.IResultSet;

import bpm.fd.api.core.model.components.definition.timer.ComponentTimer;
import bpm.fd.api.core.model.components.definition.timer.TimerOptions;
import bpm.fd.runtime.model.DashState;

public class TimerRenderer extends AsbtractCssApplier implements IHTMLRenderer<ComponentTimer>{
	public String getHTML(Rectangle layout, ComponentTimer timer, DashState state, IResultSet datas, boolean refresh){
		TimerOptions opt = (TimerOptions)timer.getOptions(TimerOptions.class);
		
		StringBuffer buf = new StringBuffer();
		buf.append(getComponentDefinitionDivStart(layout, timer));
		buf.append(getComponentDefinitionDivEnd());
		
//		if (refresh){
//			return content.toString();
//		}
		buf.append("<script type=\"text/javascript\">\n");
		buf.append("    var " + timer.getId() + " = new Timer('" + timer.getId() + "', ");
		buf.append(opt.getDelay());
		if (opt.getLabel() != null){
//			try {
//				buf.append(", '" + URLEncoder.encode(opt.getLabel(), "UTF-8") + "', ");
//			} catch (UnsupportedEncodingException e) {
//				buf.append(", '" + opt.getLabel() + "', ");
//			}
			buf.append(", '" + opt.getLabel().replace("'", "’") + "', ");
			buf.append(opt.isStartOnLoad());
		}
		buf.append(");\n");
		buf.append("    " + timer.getId() + ".Draw();\n");
		
		buf.append("</script>\n");
		return buf.toString();
	}
	public String getJavaScriptFdObjectVariable(ComponentTimer filter){
		StringBuffer buf = new StringBuffer();
		buf.append("<script type=\"text/javascript\">\n");

		
		buf.append("</script>\n");
		return buf.toString();
	}
}
