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
import bpm.forms.core.design.ITargetTable;
import bpm.forms.model.impl.Form;
import bpm.forms.model.impl.FormDefinition;
import bpm.forms.model.impl.FormDefinitionTableMapping;
import bpm.forms.model.impl.FormFieldMapping;
import bpm.forms.model.impl.FormUIFd;
import bpm.forms.model.impl.FormUIProperty;
import bpm.forms.model.impl.InstanciationRule;
import bpm.forms.model.impl.TargetTable;
import bpm.forms.runtime.VanillaFormsRuntimeComponent;

import com.thoughtworks.xstream.XStream;

public class DefinitionServlet extends HttpServlet{

	private XStream xstream;
	private VanillaFormsRuntimeComponent component;
	
	
	
	public DefinitionServlet(VanillaFormsRuntimeComponent component){
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
			case DEF_ACTIVE_FORM:
				actionResult = getActiveForms(args);
				break;
			case DEF_COLUMNS_FOR_TT:
				actionResult = getTargetTableColumns(args);
				break;
			case DEF_DELETE:
				delete(args);
				break;
			case DEF_FORM_DEF:
				actionResult = getFormDefinition(args);
				break;
			case DEF_FORM_VERS:
				actionResult = getFormDefinitionVersion(args);
				break;
			case DEF_FORMS:
				actionResult = getForms(args);
				break;
			case DEF_SAVE:
				actionResult = save(args);
				break;
			case DEF_TARGET_TABLE:
				actionResult = getTargetTable(args);
				break;
			case DEF_UPDATE:
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
			
			
			resp.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, ex.getMessage());
			component.getLogger().error("An error occured", ex);

		}
	}
	
	private void update(XmlArgumentsHolder args) throws Exception{
		if (args.getArguments().size() == 0){
			throw new Exception("Cannot update nothing");
		}
		
		Object o = args.getArguments().get(0);
		if (o instanceof IForm){
			try{
				component.getFormServiceProvider().getDefinitionService().update((IForm)o);
			}catch(Exception ex){
				String s = "Cannot update the IForm " + ((IForm)o).getName() + ": " + ex.getMessage();
				component.getLogger().error(s, ex);
				throw new Exception(s);
			}
		}
		else if (o instanceof IFormDefinition){
			try{
				component.getFormServiceProvider().getDefinitionService().update((IFormDefinition)o);
			}catch(Exception ex){
				String s = "Cannot update the IFormDefinition " + ((IFormDefinition)o).getId() + ": " + ex.getMessage();
				component.getLogger().error(s, ex);
				throw new Exception(s);
			}
			
		}
		
	}

	private Object getTargetTable(XmlArgumentsHolder args) {
		
		if (args.getArguments().size() > 0){
			Long l = (Long)args.getArguments().get(0);
			return component.getFormServiceProvider().getDefinitionService().getTargetTable(l);
		}
		else{
			return component.getFormServiceProvider().getDefinitionService().getTargetTables();
		}
		
		
		
	}

	private Object save(XmlArgumentsHolder args) throws Exception{
		if (args.getArguments().size() == 0){
			throw new Exception("Cannot save nothing");
		}
		
		Object o = args.getArguments().get(0);
		if (o instanceof IForm){
			try{
				return component.getFormServiceProvider().getDefinitionService().saveForm((IForm)o);
			}catch(Exception ex){
				String s = "Cannot save the IForm " + ((IForm)o).getName() + ": " + ex.getMessage();
				component.getLogger().error(s, ex);
				throw new Exception(s);
			}
		}
		else if (o instanceof IFormDefinition){
			try{
				return component.getFormServiceProvider().getDefinitionService().saveFormDefinition((IFormDefinition)o);
			}catch(Exception ex){
				String s = "Cannot save the IFormDefinition " + ((IFormDefinition)o).getId() + ": " + ex.getMessage();
				component.getLogger().error(s, ex);
				throw new Exception(s);
			}
			
		}
		else if (o instanceof ITargetTable){
			try{
				return component.getFormServiceProvider().getDefinitionService().saveTargetTable((ITargetTable)o);
			}catch(Exception ex){
				String s = "Cannot save the ITargetTable " + ((ITargetTable)o).getName() + ": " + ex.getMessage();
				component.getLogger().error(s, ex);
				throw new Exception(s);
			}
			
		}
		else{
			throw new Exception("Cannot save " + o.getClass().getName() + " objects");
		}
	}

	private Object getFormDefinitionVersion(XmlArgumentsHolder args) throws Exception{
		
		if (args.getArguments().size() < 1){
			throw new Exception("Missing argument FormId");
		}
		
		Long formId= (Long)args.getArguments().get(0);
		return component.getFormServiceProvider().getDefinitionService().getFormDefinitionVersions(formId);
	}

	private Object getFormDefinition(XmlArgumentsHolder args) throws Exception{
		
		if (args.getArguments().size() < 1){
			throw new Exception("Missing argument FormDefinitionId");
		}
		Long l = (Long)args.getArguments().get(0);
		return component.getFormServiceProvider().getDefinitionService().getFormDefinition(l);
	}

	private void delete(XmlArgumentsHolder args) throws Exception{
		if (args.getArguments().size() == 0){
			throw new Exception("Cannot delete nothing");
		}
		
		Object o = args.getArguments().get(0);
		if (o instanceof IForm){
			try{
				component.getFormServiceProvider().getDefinitionService().delete((IForm)o);
			}catch(Exception ex){
				String s = "Cannot delete the IForm " + ((IForm)o).getName() + ": " + ex.getMessage();
				component.getLogger().error(s, ex);
				throw new Exception(s);
			}
		}
		else if (o instanceof IFormDefinition){
			try{
				component.getFormServiceProvider().getDefinitionService().delete((IFormDefinition)o);
			}catch(Exception ex){
				String s = "Cannot delete the IFormDefinition " + ((IFormDefinition)o).getId() + ": " + ex.getMessage();
				component.getLogger().error(s, ex);
				throw new Exception(s);
			}
			
		}
	}

	private Object getTargetTableColumns(XmlArgumentsHolder args) throws Exception{
		
		if (args.getArguments().size() == 0){
			throw new Exception("Missing Target Table Id");
		}
		
		Long l = (Long)args.getArguments().get(0);
		return component.getFormServiceProvider().getDefinitionService().getColumnsForTargetTable(l);
		
	}

	private Object getActiveForms(XmlArgumentsHolder args) throws Exception{
		
		if (args.getArguments().size() == 0){
			throw new Exception("Missing IForm argument");
		}
		IForm form = (IForm)args.getArguments().get(0);
		
		return component.getFormServiceProvider().getDefinitionService().getActiveFormDefinition(form);
	}

	private Object getForms(XmlArgumentsHolder args){
		if (args.getArguments().size() == 0){
			return component.getFormServiceProvider().getDefinitionService().getForms();
		}
		else {
			Long i = (Long)args.getArguments().get(0);
			return component.getFormServiceProvider().getDefinitionService().getForm(i);
			
		}
		
		
	}
}
