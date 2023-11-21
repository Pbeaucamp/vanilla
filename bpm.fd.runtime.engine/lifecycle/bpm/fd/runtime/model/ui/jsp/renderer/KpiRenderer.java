package bpm.fd.runtime.model.ui.jsp.renderer;

import java.awt.Rectangle;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.eclipse.datatools.connectivity.oda.IResultSet;

import bpm.fd.api.core.model.components.definition.ComponentParameter;
import bpm.fd.api.core.model.components.definition.report.ComponentKpi;
import bpm.fd.runtime.model.Component;
import bpm.fd.runtime.model.DashState;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.KpiTheme;
import bpm.vanilla.platform.core.repository.RepositoryItem;

import com.thoughtworks.xstream.XStream;

public class KpiRenderer extends AsbtractCssApplier implements IHTMLRenderer<ComponentKpi>{

	private SimpleDateFormat dfDecode = new SimpleDateFormat("MM/dd/yyyy");
	private SimpleDateFormat dfEncode = new SimpleDateFormat("yyyy-MM-dd hh:mm");
	
	public String getJavaScriptFdObjectVariable(ComponentKpi report){
		StringBuffer buf = new StringBuffer();
		buf.append("<script type=\"text/javascript\">\n");
		buf.append("    fdObjects[\"" + report.getName() + "\"]= new FdReport(\"" + report.getName() + "\")" + ";\n");
		buf.append("</script>\n");
		return buf.toString();
	}
	public String getHTML(Rectangle layout, ComponentKpi report, DashState state, IResultSet datas, boolean refresh){
		HashMap<String, String> map = new HashMap<String, String>();
		
		StringBuffer buf = new StringBuffer();
		buf.append(getComponentDefinitionDivStart(layout, report));
		String html = null;
		try{
			
			StringBuffer params = new StringBuffer();
			
			for (ComponentParameter p : report.getParameters()) {
				Component c = (Component) state.getDashInstance().getDashBoard().getDesignParameterProvider(p);
				
				if (p.getName().equalsIgnoreCase("date")) {
					String value = state.getComponentValue(c.getName());
					
					Date date = null;
					if (value != null && !value.isEmpty()) {
						try {
							date = dfDecode.parse(value);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					
					params.append("&");
					params.append(p.getName() + "=" + (date != null ? dfEncode.format(date) : value));
				}
				else {
					params.append("&");
					params.append(p.getName() + "=" + state.getComponentValue(c.getName()));
				}
			}
			
			
//			html = ReportHelper.generateHTML(
//					state.getDashInstance().getUser(),
//					state.getDashInstance().getGroup().getId(),
//					state.getDashInstance().getDashBoard().getMeta().getIdentifier().getRepositoryId(),
//					report.getDirectoryItemId(),
//					map
			
			String faBaseUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_WEBAPPS_FMUSERWEB);
			
//			RemoteVanillaPlatform api = new RemoteVanillaPlatform(ConfigurationManager.getInstance().getVanillaConfiguration().getVanillaServerUrl(), state.getDashInstance().getUser().getLogin(), state.getDashInstance().getUser().getPassword());
			RemoteVanillaPlatform api = new RemoteVanillaPlatform(state.getDashInstance().getDashBoard().getCtx().getVanillaContext());
			try {
				api.getVanillaSecurityManager().authentify("", state.getDashInstance().getUser().getLogin(), state.getDashInstance().getUser().getPassword(), false);
			} catch (Exception e) {
				e.printStackTrace();
			}
			String sessionId = api.getSessionId();
			
			html = "<iframe width=" + "\"" + (layout.width - 5) + "px\" height=\"" + (layout.height - 5) + "px\" style=\"overflow:auto;\"  src=\"" + faBaseUrl;
			if (faBaseUrl.contains("?"))
				html += "&";
			else
				html += "?";
			html += "bpm.vanilla.sessionId=" + sessionId;
			html += "&bpm.vanilla.groupId=" + state.getDashInstance().getGroup().getId();
			html += "&bpm.vanilla.repositoryId=" + state.getDashInstance().getDashBoard().getMeta().getIdentifier().getRepositoryId();
			
//			Repository repDef = api.getVanillaRepositoryManager().getRepositoryById(state.getDashInstance().getDashBoard().getMeta().getIdentifier().getRepositoryId());
//			IRepositoryContext ctx = new BaseRepositoryContext(api.getVanillaContext(), state.getDashInstance().getGroup(), repDef);
			
			IRepositoryApi sock = new RemoteRepositoryApi(state.getDashInstance().getDashBoard().getCtx());
			RepositoryItem i = null;
			try {
				i = sock.getRepositoryService().getDirectoryItem(report.getDirectoryItemId());
			} catch (Exception e) {
				e.printStackTrace();
			}

			String itemXml = null;
			try {
				itemXml = sock.getRepositoryService().loadModel(i);
			} catch (Exception e) {
				e.printStackTrace();
			}


			XStream xstream = new XStream();
			KpiTheme th = (KpiTheme) xstream.fromXML(itemXml);
			
			html += "&bpm.vanilla.theme.id=" + th.getThemeId();
			html += "&viewer=" + true;

			html += params;

			html += "\"></iframe>";
					
			
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
