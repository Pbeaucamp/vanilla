package bpm.fd.runtime.model.ui.jsp.renderer;

import java.awt.Rectangle;
import java.net.URLEncoder;

import org.eclipse.datatools.connectivity.oda.IResultSet;

import bpm.fd.api.core.model.components.definition.ComponentParameter;
import bpm.fd.api.core.model.components.definition.olap.ComponentFaView;
import bpm.fd.api.core.model.components.definition.olap.FaViewOption;
import bpm.fd.api.core.model.components.definition.report.ReportOptions;
import bpm.fd.runtime.engine.datas.helper.OlapViewHelper;
import bpm.fd.runtime.model.Component;
import bpm.fd.runtime.model.DashState;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.utils.MD5Helper;

public class FaViewRenderer extends AsbtractCssApplier implements IHTMLRenderer<ComponentFaView> {
	public String getJavaScriptFdObjectVariable(ComponentFaView olap) {
		StringBuffer buf = new StringBuffer();
		buf.append("<script type=\"text/javascript\">\n");
		buf.append("    fdObjects[\"" + olap.getName() + "\"]= new FdOlap(\"" + olap.getName() + "\")" + ";\n");
		buf.append("</script>\n");
		return buf.toString();
	}

	public String getHTML(Rectangle layout, ComponentFaView olap, DashState state, IResultSet datas, boolean refresh) {
		StringBuffer params = new StringBuffer();
		ReportOptions opts = (ReportOptions) olap.getOptions(ReportOptions.class);
		String s_Opts = "";

		RemoteVanillaPlatform api = new RemoteVanillaPlatform(ConfigurationManager.getInstance().getVanillaConfiguration().getVanillaServerUrl(), state.getDashInstance().getUser().getLogin(), state.getDashInstance().getUser().getPassword());
		try {
			api.getVanillaSecurityManager().authentify("", state.getDashInstance().getUser().getLogin(), state.getDashInstance().getUser().getPassword(), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String sessionId = api.getSessionId();

		String faBaseUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_WEBAPPS_FAWEB);

		if (opts != null) {
			s_Opts = "width=\\\"" + (opts.getWidth() - 5) + "px\\\" height=\\\"" + (opts.getHeight() - 5) + "px\\\" ";
		}

		StringBuffer buf = new StringBuffer();
		buf.append(getComponentDefinitionDivStart(layout, olap));
		String html = null;
		try {

			for (ComponentParameter p : olap.getParameters()) {
				Component c = (Component) state.getDashInstance().getDashBoard().getDesignParameterProvider(p);
				params.append("&");
				params.append(p.getName() + "=" + state.getComponentValue(c.getName()));
			}

			FaViewOption faOpts = (FaViewOption) olap.getOptions(FaViewOption.class);
			if (!faOpts.isInteractive()) {
				html = OlapViewHelper.generateHTML(state.getDashInstance().getUser().getLogin(), MD5Helper.encode(state.getDashInstance().getUser().getPassword()), state.getDashInstance().getGroup().getId(), state.getDashInstance().getDashBoard().getMeta().getIdentifier().getRepositoryId(), opts.getWidth(), olap.getName(), olap.getDirectoryItemId(), s_Opts, params.toString());
			}
			else {

				bpm.vanilla.platform.core.beans.Repository repDef = null;
				repDef = api.getVanillaRepositoryManager().getRepositoryById(state.getDashInstance().getDashBoard().getMeta().getIdentifier().getRepositoryId());

				IRepositoryContext ctx = new BaseRepositoryContext(api.getVanillaContext(), state.getDashInstance().getGroup(), repDef);

				IRepositoryApi sock = new RemoteRepositoryApi(ctx);

				RepositoryItem item = null;

				item = sock.getRepositoryService().getDirectoryItem(olap.getDirectoryItemId());
				String itemXML = null;
				itemXML = sock.getRepositoryService().loadModel(item);
				int start = itemXML.indexOf("<cubename>") + 10;
				int end = itemXML.indexOf("</cubename>");
				String cubeName = itemXML.substring(start, end);

				start = itemXML.indexOf("<fasdid>") + 8;
				end = itemXML.indexOf("</fasdid>");
				String fasdItemId = itemXML.substring(start, end);
				String viewName = item.getName();

				html = "<iframe width=" + "\"" + (layout.width - 5) + "px\" height=\"" + (layout.height - 5) + "px\" style=\"overflow:auto;\"  src=\"" + faBaseUrl;
				if (faBaseUrl.contains("?"))
					html += "&";
				else
					html += "?";
				html += "bpm.vanilla.sessionId=" + sessionId;
				html += "&bpm.vanilla.groupId=" + state.getDashInstance().getGroup().getId();
				html += "&bpm.vanilla.repositoryId=" + state.getDashInstance().getDashBoard().getMeta().getIdentifier().getRepositoryId();
				html += "&bpm.vanilla.fasd.id=" + fasdItemId;
				html += "&bpm.vanilla.view.id=" + olap.getDirectoryItemId();
				if (cubeName != null)
					html += "&bpm.vanilla.cubename=" + URLEncoder.encode(cubeName, "UTF-8");
				if (viewName != null)
					html += "&bpm.vanilla.viewname=" + URLEncoder.encode(viewName, "UTF-8");
				html += params.toString();
				html += "&dimensionpanel=" + String.valueOf(faOpts.isShowDimensions());
				if (faOpts.isShowDimensions())
					html += "&dimensionsliste=" + faOpts.getElementsUname();
				html += "&functionstools=" + String.valueOf(faOpts.isShowCubeFunctions());

				html += params;

				html += "\"></iframe>";

			}

		} catch (Exception ex) {
			ex.printStackTrace();
			html = "Unable to generate report : <br>" + ex.getMessage();
		}
		if (refresh) {
			return html;
		}
		else {
			buf.append(html);
		}

		buf.append(getComponentDefinitionDivEnd());
		buf.append("<script type=\"text/javascript\">\n");
		buf.append("    fdObjects[\"" + olap.getName() + "\"]= new FdOlap(\"" + olap.getName() + "\")" + ";\n");
		buf.append("</script>\n");
		return buf.toString();
	}
}
