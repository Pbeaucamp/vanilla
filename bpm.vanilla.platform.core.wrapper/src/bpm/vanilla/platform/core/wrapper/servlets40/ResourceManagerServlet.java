package bpm.vanilla.platform.core.wrapper.servlets40;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.thoughtworks.xstream.XStream;

import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IResourceManager;
import bpm.vanilla.platform.core.beans.resources.AbstractD4CIntegrationInformations;
import bpm.vanilla.platform.core.beans.resources.ClassRule;
import bpm.vanilla.platform.core.beans.resources.ContractIntegrationInformations;
import bpm.vanilla.platform.core.beans.resources.DatabaseServer;
import bpm.vanilla.platform.core.beans.resources.KPIGenerationInformations;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanilla.platform.core.beans.resources.Resource.TypeResource;
import bpm.vanilla.platform.core.beans.resources.SimpleKPIGenerationInformations;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.components.impl.IVanillaComponentProvider;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

public class ResourceManagerServlet extends AbstractComponentServlet {

	private static final long serialVersionUID = 1L;
	
	private IResourceManager component;
	private XStream xstream = new XStream();
	
	public ResourceManagerServlet(IVanillaComponentProvider component) {
		this.component = component.getResourceManager();
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		try{
			resp.setCharacterEncoding("UTF-8"); 
			XmlAction action = (XmlAction)xstream.fromXML(req.getInputStream());
			
			XmlArgumentsHolder args = action.getArguments();
			Object actionResult = null;

			if (action.getActionType() == null) {
				throw new Exception("XmlAction has no actionType");
			}

			if (!(action.getActionType() instanceof IResourceManager.ActionType)) {
				throw new Exception("ActionType not a Archive manager action");
			}
			
			IResourceManager.ActionType type = (IResourceManager.ActionType) action.getActionType();
			
			switch(type){
			case GET_DRIVERS:
				actionResult = component.getJdbcDrivers();
				break;
			case GET_RESOURCES:
				actionResult = component.getResources((TypeResource) args.getArguments().get(0));
				break;
			case MANAGE_RESOURCE:
				actionResult = component.manageResource((Resource) args.getArguments().get(0), (Boolean) args.getArguments().get(1));
				break;
			case REMOVE_RESOURCE:
				component.removeResource((Resource) args.getArguments().get(0));
				break;
			case TEST_CONNECTION:
				actionResult = component.testConnection((DatabaseServer) args.getArguments().get(0));
				break;
			case VALID_SCRIPT:
				actionResult = component.validScript((Variable) args.getArguments().get(0));
				break;
			case MANAGE_CLASSRULE:
				actionResult = component.addOrUpdateClassRule((ClassRule) args.getArguments().get(0));
				break;
			case REMOVE_CLASSRULE:
				component.removeClassRule((ClassRule) args.getArguments().get(0));
				break;
			case GET_CLASSRULES:
				actionResult = component.getClassRules((String) args.getArguments().get(0));
				break;
			case GET_CKAN_DATASETS:
				actionResult = component.getCkanDatasets((String) args.getArguments().get(0));
				break;
			case DUPLICATE_RESOURCE:
				actionResult = component.duplicateResource((Integer) args.getArguments().get(0), (String) args.getArguments().get(1));
				break;
			case GENERATE_INTEGRATION:
				actionResult = component.generateIntegration((IRepositoryContext) args.getArguments().get(0), (AbstractD4CIntegrationInformations) args.getArguments().get(1), (Boolean) args.getArguments().get(2), (Boolean) args.getArguments().get(3));
				break;
			case GENERATE_KPI:
				actionResult = component.generateKPI((IRepositoryContext) args.getArguments().get(0), (KPIGenerationInformations) args.getArguments().get(1));
				break;
//			case GENERATE_SIMPLE_KPI:
//				actionResult = component.generateSimpleKPI((IRepositoryContext) args.getArguments().get(0), (SimpleKPIGenerationInformations) args.getArguments().get(1));
//				break;
			case GET_VALIDATION_SCHEMAS:
				actionResult = component.getValidationSchemas();
				break;
			case REMOVE_INTEGRATION:
				component.deleteIntegration((IRepositoryContext) args.getArguments().get(0), (ContractIntegrationInformations) args.getArguments().get(1));
				break;
			case VALIDATE_DATA:
				actionResult = component.validateData((String) args.getArguments().get(0), (String) args.getArguments().get(1), (String) args.getArguments().get(2), (String) args.getArguments().get(3), (Integer) args.getArguments().get(4), (List<String>) args.getArguments().get(5));
				break;
			default:
				throw new Exception("Unknown action " + type.name());
			}
				
			if (actionResult != null){
				xstream.toXML(actionResult, resp.getWriter());
			}
			
			resp.getWriter().close();
		}catch(Exception ex){
			ex.printStackTrace();
			resp.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, ex.getMessage());
		}
	}

}
