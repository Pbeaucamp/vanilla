package bpm.forms.runtime.dao.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bpm.forms.core.communication.xml.XmlAction;
import bpm.forms.core.communication.xml.XmlArgumentsHolder;
import bpm.forms.core.design.IForm;
import bpm.forms.core.design.IFormDefinition;
import bpm.forms.core.runtime.IFormInstance;
import bpm.forms.core.runtime.IFormInstanceFieldState;
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

public class InstanceServlet extends HttpServlet{

	private XStream xstream;
	
	
	private VanillaFormsRuntimeComponent component;
	
	
	
	public InstanceServlet(VanillaFormsRuntimeComponent component){
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
			case INST_DELETE:
				delete(args);
				break;
			case INST_FIELD_STATE:
				actionResult = getFieldsSate(args);
				break;
			case INST_GETTOSUBMIT:
				actionResult = getSubmitRequested(args);
				break;
			case INST_GETTOVALIDATE:
				actionResult = getValidateRequested(args);
				break;
			case INST_RUNNING:
				actionResult = getRunning(args);
				break;
			case INST_SAVE:
				actionResult = save(args);
				break;
			case INST_UPDATE:
				update(args);
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
			component.getLogger().error("An error occured", ex);

		}
	}
	private void update(XmlArgumentsHolder args) throws Exception{
		
		if (args.getArguments().size() == 0){
			throw new Exception("Cannot update nothing");
		}
		
		Object o = args.getArguments().get(0);
		
		if (o instanceof IFormInstance){
			component.getFormServiceProvider().getInstanceService().update((IFormInstance)o);
		}
		else if (o instanceof IFormInstanceFieldState){
			component.getFormServiceProvider().getInstanceService().update((IFormInstanceFieldState)o);
		}
		else{
			throw new Exception("Cannot update " + o.getClass().getName() + " Objects");
		}
		
	}
	private Object save(XmlArgumentsHolder args) throws Exception{
		if (args.getArguments().size() == 0){
			throw new Exception("Cannot save nothing");
		}
		
		Object o = args.getArguments().get(0);
		
		if (o instanceof IFormInstance){
			return component.getFormServiceProvider().getInstanceService().save((IFormInstance)o);
		}
		else if (o instanceof IFormInstanceFieldState){
			return component.getFormServiceProvider().getInstanceService().save((IFormInstanceFieldState)o);
		}
		else{
			throw new Exception("Cannot save " + o.getClass().getName() + " Objects");
		}
	}
	private Object getRunning(XmlArgumentsHolder args) throws Exception{
		
		if (args.getArguments().size() == 0){
			throw new Exception("Missing arguments : instanceId or IForm or IFormDefinition");
		}
		
		
		Object o = args.getArguments().get(0);
		
		if (o instanceof Long){
			return component.getFormServiceProvider().getInstanceService().getRunningInstance((Long)o);
		}
		else if (o instanceof IForm){
			return component.getFormServiceProvider().getInstanceService().getRunningInstances((IForm)o);
		}
		else if (o instanceof IFormDefinition){
			return component.getFormServiceProvider().getInstanceService().getRunningInstances((IFormDefinition)o);
		}
		else{
			throw new Exception("Bad arguments : instanceId or IForm or IFormDefinition expected");
		}
		
	}
	
	private Object getValidateRequested(XmlArgumentsHolder args) throws Exception{
		if (args.getArguments().size() == 0){
			throw new Exception("Missing groupId argument");
		}
		
		Integer i = (Integer)args.getArguments().get(0);
		
		return component.getFormServiceProvider().getInstanceService().getFormsToValidate(i);
	}
	private Object getSubmitRequested(XmlArgumentsHolder args) throws Exception{
		if (args.getArguments().size() == 0){
			throw new Exception("Missing groupId argument");
		}
		
		Integer i = (Integer)args.getArguments().get(0);
		
		return component.getFormServiceProvider().getInstanceService().getFormsToSubmit(i);
	}
	private Object getFieldsSate(XmlArgumentsHolder args) throws Exception{
		if (args.getArguments().size() == 0){
			throw new Exception("Missing instanceId argument");
		}
		
		Long instanceId = (Long)args.getArguments().get(0);
		return component.getFormServiceProvider().getInstanceService().getFieldsState(instanceId);
	}
	private void delete(XmlArgumentsHolder args) throws Exception{
		if (args.getArguments().size() == 0){
			throw new Exception("Cannot delete nothing");
		}
		
		Object o = args.getArguments().get(0);
		
		if (o instanceof IFormInstance){
			component.getFormServiceProvider().getInstanceService().delete((IFormInstance)o);
		}
		else if (o instanceof IFormDefinition){
			component.getFormServiceProvider().getInstanceService().deleteFor((IFormDefinition)o);
		}
		else{
			throw new Exception("Bad arguments, expecting :  IFormInstance of IFormDefinition");
		}
	}
}
