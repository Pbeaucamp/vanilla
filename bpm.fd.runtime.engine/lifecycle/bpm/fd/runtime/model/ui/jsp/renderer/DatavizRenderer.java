package bpm.fd.runtime.model.ui.jsp.renderer;

import java.awt.Rectangle;
import java.util.HashMap;

import org.eclipse.datatools.connectivity.oda.IResultSet;

import bpm.fd.api.core.model.components.definition.maps.ComponentDataViz;
import bpm.fd.api.core.model.components.definition.maps.DataVizOption;
import bpm.fd.api.core.model.events.ElementsEventType;
import bpm.fd.runtime.model.DashState;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;

public class DatavizRenderer extends AsbtractCssApplier implements IHTMLRenderer<ComponentDataViz>{

	@Override
	public String getHTML(Rectangle layout, ComponentDataViz definition, DashState state, IResultSet datas, boolean refresh) {
		StringBuffer buf = new StringBuffer();
		buf.append(getComponentDefinitionDivStart(layout, definition));
		
		
		StringBuffer content = new StringBuffer();
		HashMap<ElementsEventType, String> defaultEvents = new HashMap<ElementsEventType, String>();
		String event = generateEvents(definition, defaultEvents, true);
		
		
		String html = "<iframe width=" + "\"" + (layout.width - 5) + "px\" height=\"" + (layout.height - 5) + "px\" style=\"overflow:auto;\"  src=\"";
		String url = "";
		url += ConfigurationManager.getProperty(VanillaConfiguration.P_WEBAPPS_DATAPREPARATION);
		RemoteVanillaPlatform api = new RemoteVanillaPlatform(ConfigurationManager.getInstance().getVanillaConfiguration().getVanillaServerUrl(), state.getDashInstance().getUser().getLogin(), state.getDashInstance().getUser().getPassword());
		try {
			api.getVanillaSecurityManager().authentify("", state.getDashInstance().getUser().getLogin(), state.getDashInstance().getUser().getPassword(), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String sessionId = api.getSessionId();
		if (url.contains("?")) {
			url += "&bpm.vanilla.sessionId=" + sessionId;
		}
		else {
			url += "?bpm.vanilla.sessionId=" + sessionId;
		}
		
		url += "&bpm.vanilla.groupId=" + state.getDashInstance().getDashBoard().getCtx().getGroup().getId();
		url += "&bpm.vanilla.repositoryId=" + state.getDashInstance().getDashBoard().getCtx().getRepository().getId();
		url += "&bpm.vanilla.dataprep.id=" + ((DataVizOption)definition.getOptions(DataVizOption.class)).getDataprepId();

		html += url;
		
		html += "\"></iframe>";
		
		content.append(html);
		
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
	public String getJavaScriptFdObjectVariable(ComponentDataViz definition) {
		return "";
	}

}
