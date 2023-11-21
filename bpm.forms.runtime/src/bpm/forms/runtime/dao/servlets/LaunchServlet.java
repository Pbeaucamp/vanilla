package bpm.forms.runtime.dao.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bpm.forms.core.communication.xml.XmlAction;
import bpm.forms.core.communication.xml.XmlArgumentsHolder;
import bpm.forms.core.design.IForm;
import bpm.forms.model.impl.Form;
import bpm.forms.model.impl.FormDefinition;
import bpm.forms.model.impl.FormDefinitionTableMapping;
import bpm.forms.model.impl.FormFieldMapping;
import bpm.forms.model.impl.FormInstance;
import bpm.forms.model.impl.FormInstanceFieldState;
import bpm.forms.model.impl.FormUIFd;
import bpm.forms.model.impl.FormUIProperty;
import bpm.forms.model.impl.InstanciationRule;
import bpm.forms.model.impl.TargetTable;
import bpm.forms.runtime.VanillaFormsRuntimeComponent;

import com.thoughtworks.xstream.XStream;

public class LaunchServlet extends HttpServlet{

	private XStream xstream;
	private VanillaFormsRuntimeComponent component;
	
	
	
	public LaunchServlet(VanillaFormsRuntimeComponent component){
		this.component = component;
	}
	@Override
	public void init() throws ServletException {
		xstream = new XStream();

	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)	throws ServletException, IOException {
		try{
			resp.setCharacterEncoding("UTF-8");
			XmlAction action = (XmlAction)xstream.fromXML(req.getInputStream());
			
			XmlArgumentsHolder args = action.getArguments();
			
			
			Object actionResult = null;
			
			switch(action.getActionType()){
			case LAU_INSTANCIATE:
				actionResult = launch(args);
				component.getLogger().info("Instanciated VanillaForm");
				break;
			
			default:
				throw new Exception("Unknown action " + action.getActionType().name());	
			}
			
			if (actionResult != null){
				xstream.toXML(actionResult, resp.getWriter());
			}
			
			
			resp.getWriter().close();
		}catch(Exception ex){
			ex.printStackTrace();
			
			resp.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, ex.getMessage());
			component.getLogger().error("An error occured\n" + ex.getMessage(), ex);

		}
	}
	
	private Object launch(XmlArgumentsHolder args) throws Exception{
		component.getLogger().debug("Instanciating VanillaForms ...");
		IForm form = null;
		Integer groupId = null;
		
		
		for(Object o : args.getArguments()){
			if (o instanceof Integer){
				groupId = (Integer)o;
			}
			if (o instanceof IForm){
				form = (IForm)o;
			}
		}
		
		if (form == null){
			throw new Exception("Missing form arguments");
		}
		
		if (groupId == null){
			throw new Exception("Missing groupId arguments");
		}
		
		
		return component.getFormInstanceLauncher().launchForm(form, groupId);
		
	}
}
