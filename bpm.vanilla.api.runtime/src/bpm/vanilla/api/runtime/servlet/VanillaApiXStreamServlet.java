package bpm.vanilla.api.runtime.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.thoughtworks.xstream.XStream;

import bpm.vanilla.api.core.IAPIManager.ApiActionType;
import bpm.vanilla.api.runtime.ComponentAPI;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.resources.AbstractD4CIntegrationInformations;
import bpm.vanilla.platform.core.beans.resources.ContractIntegrationInformations;
import bpm.vanilla.platform.core.beans.resources.ContractType;
import bpm.vanilla.platform.core.beans.resources.KPIGenerationInformations;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.utils.IOWriter;
import bpm.vanilla.platform.core.xstream.IXmlActionType;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;
import bpm.workflow.commons.beans.Schedule;

//TODO: Replace APIServlet by this servlet to use xstream globally
public class VanillaApiXStreamServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private ComponentAPI component;
	protected XStream xstream;
	
	public VanillaApiXStreamServlet(ComponentAPI component) {
		this.component = component;
	}
	
	@Override
	public void init() throws ServletException {
		super.init();
		xstream = new XStream();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			resp.setCharacterEncoding("UTF-8");
			XmlAction action = (XmlAction) xstream.fromXML(req.getInputStream());
			
			XmlArgumentsHolder args = action.getArguments();
			
			if (action.getActionType() == null){
				throw new Exception("XmlAction has no actionType");
			}
			
			if (!(action.getActionType() instanceof IXmlActionType)){
				throw new Exception("ActionType not a IVanillaAPIManager");
			}
			
			ApiActionType type = (ApiActionType) action.getActionType();

			Object actionResult = null;
			try{
				switch (type) {
				case GENERATE_PROCESS:
					actionResult = component.getApiService().generateIntegration((Integer) args.getArguments().get(0), (Integer) args.getArguments().get(1), (AbstractD4CIntegrationInformations) args.getArguments().get(2), (Boolean) args.getArguments().get(3), (Boolean) args.getArguments().get(4));
					break;
				case GET_INTEGRATION_PROCESS_BY_CONTRACT:
					actionResult = component.getApiService().getIntegrationProcessByContract((Integer) args.getArguments().get(0));
					break;
				case GET_INTEGRATION_PROCESS_BY_LIMESURVEY:
					actionResult = component.getApiService().getIntegrationProcessByLimesurvey((String) args.getArguments().get(0));
					break;
				case GET_VANILLA_HUBS:
					actionResult = component.getApiService().getVanillaHubs();
					break;
				case RUN_VANILLA_HUB:
					actionResult = component.getApiService().runVanillaHub((Integer) args.getArguments().get(0), (List<Parameter>) args.getArguments().get(1));
					break;
				case GET_VANILLA_HUB_PROGRESS:
					actionResult = component.getApiService().getVanillaHubProgress((Integer) args.getArguments().get(0), (String) args.getArguments().get(1));
					break;
				case GENERATE_KPI:
					actionResult = component.getApiService().generateKpi((Integer) args.getArguments().get(0), (Integer) args.getArguments().get(1), (KPIGenerationInformations) args.getArguments().get(2));
					break;
//				case GENERATE_SIMPLE_KPI:
//					actionResult = component.getApiService().generateSimpleKpi((Integer) args.getArguments().get(0), (Integer) args.getArguments().get(1), (SimpleKPIGenerationInformations) args.getArguments().get(2));
//					break;
				case GET_INTEGRATION_KPI_BY_DATASET_ID:
					actionResult = component.getApiService().getIntegrationKPIByDatasetId((String) args.getArguments().get(0));
					break;
				case GET_VALIDATION_SCHEMAS:
					actionResult = component.getApiService().getValidationSchemas();
					break;
				case REMOVE_INTEGRATION:
					component.getApiService().deleteIntegration((Integer) args.getArguments().get(0), (Integer) args.getArguments().get(1), (ContractIntegrationInformations) args.getArguments().get(2));
					break;
				case GET_INTEGRATION_INFOS_BY_ORGANISATION:
					actionResult = component.getApiService().getIntegrationByOrganisation((String) args.getArguments().get(0), (ContractType) args.getArguments().get(1));
					break;
				case UPDATE_SCHEDULE:
					component.getApiService().updateSchedule((Schedule) args.getArguments().get(0));
					break;
				case VALIDATE_DATA:
					actionResult = component.getApiService().validateData((String) args.getArguments().get(0), (String) args.getArguments().get(1), (String) args.getArguments().get(2), (String) args.getArguments().get(3), (Integer) args.getArguments().get(4), (List<String>) args.getArguments().get(5));
					break;
				case GET_ITEM_INFORMATIONS:
					actionResult = component.getApiService().getItemInformations((User) args.getArguments().get(0), (Integer) args.getArguments().get(1), (Integer) args.getArguments().get(2), (Integer) args.getArguments().get(3));
					break;
				case RUN_ETL:
					actionResult = component.getApiService().runETL((User) args.getArguments().get(0), (Integer) args.getArguments().get(1), (Integer) args.getArguments().get(2), (Integer) args.getArguments().get(3), (List<VanillaGroupParameter>) args.getArguments().get(4));
					break;
				case RUN_WORKFLOW:
					actionResult = component.getApiService().runWorkflow((User) args.getArguments().get(0), (Integer) args.getArguments().get(1), (Integer) args.getArguments().get(2), (Integer) args.getArguments().get(3), (List<VanillaGroupParameter>) args.getArguments().get(4));
					break;
				case RUN_REPORT:
					serializeReport(component.getApiService().runReport((User) args.getArguments().get(0), (Integer) args.getArguments().get(1), (Integer) args.getArguments().get(2), (Integer) args.getArguments().get(3), (String) args.getArguments().get(4), (String) args.getArguments().get(5), (List<VanillaGroupParameter>) args.getArguments().get(6), (List<String>) args.getArguments().get(7)), resp.getOutputStream());
					return;
				}
			}catch(Exception ex){
				ex.printStackTrace();
				throw new Exception("Operation " + type.name() + " failed - " + ex.getMessage(), ex);
			}
			
			if (actionResult != null){
				xstream.toXML(actionResult, resp.getWriter());
			}
			resp.getWriter().close();
		} catch (Exception e) {
			e.printStackTrace();
			
			component.getLogger().error(e.getMessage(), e);
		
			resp.getWriter().write("<error>" + e.getMessage() + "</error>");
			resp.getWriter().close();
		}
	}

	private void serializeReport(InputStream is, OutputStream os) throws Exception{
		IOWriter.write(is, os, true, false);
	}
}
