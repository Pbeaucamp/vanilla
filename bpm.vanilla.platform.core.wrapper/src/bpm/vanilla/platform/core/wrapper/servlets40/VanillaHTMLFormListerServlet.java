package bpm.vanilla.platform.core.wrapper.servlets40;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import bpm.forms.core.design.IFormDefinition;
import bpm.forms.core.runtime.IFormInstance;
import bpm.forms.core.tools.InstanceAccessHelper;
import bpm.forms.remote.services.RemoteDefinitionService;
import bpm.forms.remote.services.RemoteInstanceService;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.components.HTMLFormComponent;
import bpm.vanilla.platform.core.components.IVanillaComponentIdentifier;
import bpm.vanilla.platform.core.components.VanillaComponentType;
import bpm.vanilla.platform.core.components.forms.Form;
import bpm.vanilla.platform.core.components.forms.IForm;
import bpm.vanilla.platform.core.components.forms.IForm.IFormType;
import bpm.vanilla.platform.core.components.impl.IVanillaComponentProvider;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.wrapper.servlets.tools.WorkflowSystemRemote;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

/**
 * 
 * @author ludo
 *
 */
public class VanillaHTMLFormListerServlet extends AbstractComponentServlet{
	
	public VanillaHTMLFormListerServlet(IVanillaComponentProvider componentProvider)throws Exception{
		this.component = componentProvider;
	}
	
	@Override
	public void init() throws ServletException {
		super.init();
	}

	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)	throws ServletException, IOException {
		try{
			resp.setCharacterEncoding("UTF-8"); 
			XmlAction action = (XmlAction)xstream.fromXML(req.getInputStream());
			
			XmlArgumentsHolder args = action.getArguments();
			Object actionResult = null;
			
			if (action.getActionType() == null){
				throw new Exception("XmlAction has no actionType");
			}
			
			if (!(action.getActionType() instanceof HTMLFormComponent.ActionType)){
				throw new Exception("ActionType not a HTMLFormComponent");
			}
			
			HTMLFormComponent.ActionType type = (HTMLFormComponent.ActionType)action.getActionType();
			
			log(type, "bpm.vanilla.platform.core.runtime.components.VanillaFormRuntime", req);
			
//			try{
				switch (type) {
				case LIST:
					actionResult = listForms(extractUser(req), (Integer)args.getArguments().get(0));
					break;
				default:
					throw new Exception("TypeAction " + type.toString() + " not supported");
				}
				if (actionResult != null){
					xstream.toXML(actionResult, resp.getWriter());
				}
				resp.getWriter().close();	
//			}catch (Exception e) {
//				Logger.getLogger(getClass()).error(message);
//			}
		}catch (Exception e) {
			Logger.getLogger(getClass()).error(e.getMessage(),e);
			resp.getWriter().write("<error>" + e.getMessage() + "</error>");
			resp.getWriter().close();
		}
	}
	
	private List<IForm> listForms(User user, Integer groupId) throws Exception{
		
		List<IForm> result = new ArrayList<IForm>();
		
		// list the manual tasks from the Workflow Process Instances
		for(IVanillaComponentIdentifier id : component.getVanillaListenerComponent().getRegisteredComponents(VanillaComponentType.COMPONENT_WORKFLOW, false)){
			
			WorkflowSystemRemote remote = new WorkflowSystemRemote(id.getComponentUrl(), user.getLogin(), user.getPassword());		
			
			try{
				result.addAll(remote.listHTMLForms(groupId));
			}catch(Exception ex){
				Logger.getLogger(getClass()).error("Failed to get Workflow Forms from " + id.getComponentUrl() + " - " + ex.getMessage(), ex);
			}
			
		}
		
		IVanillaContext vCtx = new BaseVanillaContext(ConfigurationManager.getInstance().getVanillaConfiguration().getVanillaServerUrl(),
				user.getLogin(), user.getPassword());
		
		
		
		
		
		// note:the repository is created because it is not requested, its just for compile issues
		IRepositoryContext repCtx = new BaseRepositoryContext(vCtx, component.getSecurityManager().getGroupById(groupId), new Repository());
		//TODO : get vanillaForms
		for(IVanillaComponentIdentifier id: component.getVanillaListenerComponent().getRegisteredComponents(VanillaComponentType.COMPONENT_VANILLA_FORMS, false)){
			RemoteInstanceService remote = new RemoteInstanceService();
			remote.configure(id.getComponentUrl());
			RemoteDefinitionService remoteDef = new RemoteDefinitionService();
			remoteDef.configure(id.getComponentUrl());
			
			try{
				for(IFormInstance instance : remote.getFormsToSubmit(groupId)){
					
					Form f = new Form();
					f.setCreatedDate(instance.getCreationDate());
					
					f.setType(IFormType.VanillaFormSubmission);
					
					
					IFormDefinition def = remoteDef.getFormDefinition(instance.getFormDefinitionId());
					bpm.forms.core.design.IForm vForm = remoteDef.getForm(def.getFormId());
					f.setOriginName(vForm.getName());
					
					try{
						f.setHtmlFormUrl(InstanceAccessHelper.getFormUrl(instance, vCtx));
						result.add(f);
					}catch(Exception ex){
						
					}
//					f.setHtmlFormUrl(instance.get);
				}
			}catch(Exception ex){
				Logger.getLogger(getClass()).warn("Failed to load VanillaForms to submit from " + id.getComponentUrl());
			}
			
			try{
				for(IFormInstance instance : remote.getFormsToValidate(groupId)){
					Form f = new Form();
					f.setCreatedDate(instance.getCreationDate());
					
					f.setType(IFormType.VanillaFormSubmission);
					
					
					IFormDefinition def = remoteDef.getFormDefinition(instance.getFormDefinitionId());
					bpm.forms.core.design.IForm vForm = remoteDef.getForm(def.getFormId());
					f.setOriginName(vForm.getName());
					
					try{
						f.setHtmlFormUrl(InstanceAccessHelper.getValidationFormUrl(instance, repCtx));
						result.add(f);
					}catch(Exception ex){
						
					}
				}
			}catch(Exception ex){
				Logger.getLogger(getClass()).warn("Failed to load VanillaForms to validate from " + id.getComponentUrl());
			}
		}
		
		return result;
		
	}
}
