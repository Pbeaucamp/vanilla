package bpm.fd.runtime.model.ui.jsp.renderer;

import java.awt.Rectangle;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.codec.binary.Base64;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.eclipse.datatools.connectivity.oda.IResultSet;

import bpm.fd.api.core.model.components.definition.ComponentParameter;
import bpm.fd.api.core.model.components.definition.markdown.ComponentMarkdown;
import bpm.fd.runtime.model.Component;
import bpm.fd.runtime.model.DashState;
import bpm.smart.core.model.AirProject;
import bpm.smart.core.model.RScript;
import bpm.smart.core.model.RScriptModel;
import bpm.smart.core.xstream.ISmartManager;
import bpm.smart.core.xstream.RemoteAdminManager;
import bpm.smart.core.xstream.RemoteSmartManager;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.data.Dataset;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;

public class MarkdownRenderer extends AsbtractCssApplier implements IHTMLRenderer<ComponentMarkdown>{
	public String getJavaScriptFdObjectVariable(ComponentMarkdown markdown){
		StringBuffer buf = new StringBuffer();
		buf.append("<script type=\"text/javascript\">\n");
		buf.append("    fdObjects[\"" + markdown.getName() + "\"]= new FdMarkdown(\"" + markdown.getName() + "\")" + ";\n");
		buf.append("</script>\n");
		return buf.toString();
	}
	public String getHTML(Rectangle layout, ComponentMarkdown markdown, DashState state, IResultSet datas, boolean refresh){
		
		StringBuffer buf = new StringBuffer();
		buf.append(getComponentDefinitionDivStart(layout, markdown, "overflow:auto;"));
		String html = null;
		try{
			IRepositoryContext ctx = state.getDashInstance().getDashBoard().getCtx();
			
			RemoteAdminManager manag =  new RemoteAdminManager(ctx.getVanillaContext().getVanillaUrl(), null, Locale.getDefault());
			
			RemoteVanillaPlatform platform = new RemoteVanillaPlatform(ctx.getVanillaContext());
			
			User user = platform.getVanillaSecurityManager().getUserByLogin(ctx.getVanillaContext().getLogin());
			String sessionId = manag.connect(user);
			ISmartManager manager = new RemoteSmartManager(ctx.getVanillaContext().getVanillaUrl(), sessionId, Locale.getDefault());
			/*
			//manager.addDatasettoR(dataset);
			
			RScriptModel model = new RScriptModel();
			model.setOutputs(new String[] {"manual_result"});
			manager.executeScriptR(model, new ArrayList<Parameter>());
			*/
			//List<Serializable> outputs = model.getOutputVars();
			
			RemoteRepositoryApi rep = new RemoteRepositoryApi(ctx);
			
			String xml = rep.getRepositoryService().loadModel(rep.getRepositoryService().getDirectoryItem(markdown.getDirectoryItemId()));
			Document doc = null;
			doc = DocumentHelper.parseText(xml);
			Element root = doc.getRootElement();
			String idScript = (String) root.element("idScript").getData();
			String idProject = (String) root.element("idProject").getData();
			
			AirProject project = manager.getAirProjectbyId(Integer.parseInt(idProject));
			
			RScriptModel model = manager.getScriptModelbyId(Integer.parseInt(idScript));
			
			RScript script = manager.getScriptbyId(model.getIdScript());
			
			if(project.getLinkedDatasets() != null && !project.getLinkedDatasets().isEmpty()){
				
				List<Dataset> dts = manager.getDatasetsbyProject(project);
				manager.addDatasetstoR(dts);
			}
			
			for(ComponentParameter p : markdown.getParameters()){  //modification des parametres
				Component c = (Component)state.getDashInstance().getDashBoard().getDesignParameterProvider(p);
				model.setScript(model.getScript().replace("{$P_"+ p.getName() +"}", state.getComponentValue(c.getName())));
			}
			List<String> outputs = new ArrayList<String>();
			outputs.add("html");
			
			model = manager.renderMarkdown(model.getScript(), script.getName(), outputs, null);

			model.setIdScript(script.getId());
			
			if (model.getOutputVarstoString().size() > 0) {
				html = new String(Base64.decodeBase64(model.getOutputVarstoString().get(0).getBytes()), "UTF-8");		
			}
		}catch(Exception ex){
			ex.printStackTrace();
			html = "Unable to generate markdown : <br>" + ex.getMessage();
		}
		if (refresh){
			return html;
		}
		else{
			buf.append(html);
		}
		
		
		buf.append(getComponentDefinitionDivEnd());
		buf.append("<script type=\"text/javascript\">\n");
		buf.append("    fdObjects[\"" + markdown.getName() + "\"]= new FdMarkdown(\"" + markdown.getName() + "\")" + ";\n");
		buf.append("</script>\n");
		return buf.toString();
	}
}
