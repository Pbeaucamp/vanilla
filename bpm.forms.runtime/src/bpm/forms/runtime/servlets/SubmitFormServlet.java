package bpm.forms.runtime.servlets;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bpm.forms.core.design.IForm;
import bpm.forms.core.design.IFormDefinition;
import bpm.forms.core.design.internal.FormsUIInternalConstants;
import bpm.forms.core.runtime.IFormInstance;
import bpm.forms.runtime.VanillaFormsRuntimeComponent;
import bpm.forms.runtime.submission.Submiter;
import bpm.forms.runtime.submission.Validator;

public class SubmitFormServlet extends HttpServlet{

	private VanillaFormsRuntimeComponent component;
	
	
	public SubmitFormServlet(VanillaFormsRuntimeComponent component){
		this.component = component;
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		component.getLogger().error("SubmitFormServlet only support POST method");
		throw new ServletException("Only POST method is supported");
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)	throws ServletException, IOException {
		
		String _instanceId = req.getParameter(FormsUIInternalConstants.VANILLA_FORM_INSTANCE_ID);
		
		
		try{
			
			if (_instanceId == null){
				throw new Exception("Missing bpm.forms.runtime.instanceId parameter");
			}
			
			Integer instanceId = Integer.parseInt(_instanceId);
			
			IFormInstance formInstance = component.getFormServiceProvider().getInstanceService().getRunningInstance(instanceId);
			
			if (formInstance == null){
				throw new Exception("No FormInstance with id = " + instanceId);
			}
			
			//TODO : check User identity
			String ctxLogin = req.getParameter(FormsUIInternalConstants.VANILLA_CTX_LOGIN);
			String ctxPassword = req.getParameter(FormsUIInternalConstants.VANILLA_CTX_PASSWORD);
			
			
			//check that the group is the same as the one given to the formInstance
			String ctxGroupId = req.getParameter(FormsUIInternalConstants.VANILLA_CTX_GROUP_ID);
			if (ctxGroupId == null){
				throw new Exception("Missing Vanilla Context Group Id when calling Vanilla Form submission servlet");
			}
			if (formInstance.getGroupId() != Integer.parseInt(ctxGroupId)){
				throw new Exception("The Vanilla Context Group Id is not the same as the FormInstance's one.");
			}
			
			
			Properties prop = new Properties();
			
			for(Object key : req.getParameterMap().keySet()){
				if (key instanceof String){
					prop.setProperty((String)key,req.getParameter((String)key));
				}
			}
			
			
			
			try{
				
				component.getLogger().info("Submiting formInstance " + formInstance.getId() + "...");
				
				Submiter submiter = new Submiter(component, req.getRemoteAddr(), null);
				submiter.submit(formInstance, prop);
				component.getLogger().info("Submited formInstance " + formInstance.getId() );
				
				
			}catch(Exception ex){
				ex.printStackTrace();
				throw new Exception("Error while submiting VanillaFormInstance " + formInstance.getId() + " : \n" + ex.getMessage(), ex);
			}
			
			/*
			 * if no validation group attached to the form owning the Instance, we validate it
			 */
			
			IFormDefinition formDefinition= component.getFormServiceProvider().getDefinitionService().getFormDefinition(formInstance.getFormDefinitionId());
			IForm form = component.getFormServiceProvider().getDefinitionService().getForm(formDefinition.getFormId());
			
			
			if (form.getValidatorGroups().isEmpty()){
				component.getLogger().info("Submited formInstance " + formInstance.getId() + " has no validatorsGroup");
				component.getLogger().info("Validating formInstance " + formInstance.getId() + " ...");
				
				Validator validator = new Validator(component, req.getRemoteAddr(), null);
				
				try{
					//XXX : replace null by a VanillaContext
					validator.validate(formInstance, prop, null);
					component.getLogger().info("Validated formInstance " + formInstance.getId() );
				}catch(Exception ex){
					ex.printStackTrace();
					throw new Exception("Error while validating VanillaFormInstance " + formInstance.getId() + " : \n" + ex.getMessage(), ex);
				}
				
			}
			
			
			resp.getWriter().write("<html><header></header><body>");
			resp.getWriter().write("Submission succeeded!!!<br>");
			resp.getWriter().write("</body></html>");
			
			resp.getWriter().flush();
			
			resp.flushBuffer();
			
			
		}catch(Exception ex){
			ex.printStackTrace();
			component.getLogger().error(ex);
			resp.getWriter().write("<html><header></header><body>");
			resp.getWriter().write("Submission failed!!!<br><br>");
			ex.printStackTrace(resp.getWriter());
			
			resp.getWriter().write("</body></html>");
			
			resp.getWriter().flush();
			
			resp.flushBuffer();
			
		}
		
		
	}
	
	
}
