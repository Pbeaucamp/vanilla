package bpm.fd.runtime.model.ui.jsp.renderer;

import java.awt.Rectangle;
import java.util.HashMap;

import org.eclipse.datatools.connectivity.oda.IResultSet;

import bpm.fd.api.core.model.components.definition.ComponentParameter;
import bpm.fd.api.core.model.components.definition.report.ComponentReport;
import bpm.fd.runtime.engine.datas.helper.ReportHelper;
import bpm.fd.runtime.model.Component;
import bpm.fd.runtime.model.DashState;

public class ReportRenderer extends AsbtractCssApplier implements IHTMLRenderer<ComponentReport>{
	public String getJavaScriptFdObjectVariable(ComponentReport report){
		StringBuffer buf = new StringBuffer();
		buf.append("<script type=\"text/javascript\">\n");
		buf.append("    fdObjects[\"" + report.getName() + "\"]= new FdReport(\"" + report.getName() + "\")" + ";\n");
		buf.append("</script>\n");
		return buf.toString();
	}
	public String getHTML(Rectangle layout, ComponentReport report, DashState state, IResultSet datas, boolean refresh){
		HashMap<String, String> map = new HashMap<String, String>();
		
		StringBuffer buf = new StringBuffer();
		buf.append(getComponentDefinitionDivStart(layout, report));
		String html = null;
		try{
			for(ComponentParameter p : report.getParameters()){
				Component c = (Component)state.getDashInstance().getDashBoard().getDesignParameterProvider(p);
				map.put(p.getName(), state.getComponentValue(c.getName()));
			}
			
			
			html = ReportHelper.generateHTML(
					state.getDashInstance().getUser(),
					state.getDashInstance().getGroup().getId(),
					state.getDashInstance().getDashBoard().getMeta().getIdentifier().getRepositoryId(),
					report.getDirectoryItemId(),
					map
					
			);
		}catch(Exception ex){
			ex.printStackTrace();
			html = "Unable to generate report : <br>" + ex.getMessage();
		}
		if (refresh){
			return html;
		}
		else{
			buf.append(html);
		}
		
		
		buf.append(getComponentDefinitionDivEnd());
		buf.append("<script type=\"text/javascript\">\n");
		buf.append("    fdObjects[\"" + report.getName() + "\"]= new FdReport(\"" + report.getName() + "\")" + ";\n");
		buf.append("</script>\n");
		return buf.toString();
	}
}
