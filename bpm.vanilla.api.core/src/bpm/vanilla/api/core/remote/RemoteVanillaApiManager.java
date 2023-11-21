package bpm.vanilla.api.core.remote;

import java.io.InputStream;
import java.util.List;

import com.thoughtworks.xstream.XStream;

import bpm.vanilla.api.core.IAPIManager;
import bpm.vanilla.api.core.IVanillaAPIManager;
import bpm.vanilla.api.core.model.ItemRunInformations;
import bpm.vanilla.api.core.remote.internal.HttpCommunicator;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.resources.AbstractD4CIntegrationInformations;
import bpm.vanilla.platform.core.beans.resources.ContractIntegrationInformations;
import bpm.vanilla.platform.core.beans.resources.ContractType;
import bpm.vanilla.platform.core.beans.resources.KPIGenerationInformations;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.ValidationDataResult;
import bpm.vanilla.platform.core.exceptions.VanillaException;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;
import bpm.workflow.commons.beans.Schedule;
import bpm.workflow.commons.beans.Workflow;
import bpm.workflow.commons.beans.WorkflowInstance;

public class RemoteVanillaApiManager implements IVanillaAPIManager {

	private HttpCommunicator httpCommunicator;

	private static XStream xstream;
	static {
		xstream = new XStream();
	}

	public RemoteVanillaApiManager(HttpCommunicator httpCommunicator) {
		this.httpCommunicator = httpCommunicator;
	}

	private XmlArgumentsHolder createArguments(Object... arguments) {
		XmlArgumentsHolder args = new XmlArgumentsHolder();

		for (int i = 0; i < arguments.length; i++) {
			args.addArgument(arguments[i]);
		}
		return args;
	}
	
	@Override
	public ContractIntegrationInformations generateIntegration(int repositoryId, int groupId, AbstractD4CIntegrationInformations integrationInfos, boolean modifyMetadata, boolean modifyIntegration) throws Exception {
		XmlAction op = new XmlAction(createArguments(repositoryId, groupId, integrationInfos, modifyMetadata, modifyIntegration), IAPIManager.ApiActionType.GENERATE_PROCESS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (ContractIntegrationInformations) handleError(xml);
	}
	
	@Override
	public ContractIntegrationInformations getIntegrationProcessByContract(int contractId) throws Exception {
		XmlAction op = new XmlAction(createArguments(contractId), IAPIManager.ApiActionType.GET_INTEGRATION_PROCESS_BY_CONTRACT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (ContractIntegrationInformations) handleError(xml);
	}
	
	@Override
	public ContractIntegrationInformations getIntegrationProcessByLimesurvey(String limesurveyId) throws Exception {
		XmlAction op = new XmlAction(createArguments(limesurveyId), IAPIManager.ApiActionType.GET_INTEGRATION_PROCESS_BY_LIMESURVEY);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (ContractIntegrationInformations) handleError(xml);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<Workflow> getVanillaHubs() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IAPIManager.ApiActionType.GET_VANILLA_HUBS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Workflow>) handleError(xml);
	}

	@Override
	public String runVanillaHub(int workflowId, List<Parameter> parameters) throws Exception {
		XmlAction op = new XmlAction(createArguments(workflowId, parameters), IAPIManager.ApiActionType.RUN_VANILLA_HUB);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (String) handleError(xml);
	}
	
	@Override
	public WorkflowInstance getVanillaHubProgress(int workflowId, String uuid) throws Exception {
		XmlAction op = new XmlAction(createArguments(workflowId, uuid), IAPIManager.ApiActionType.GET_VANILLA_HUB_PROGRESS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (WorkflowInstance) handleError(xml);
	}
	
	@Override
	public ItemRunInformations getItemInformations(User user, int repositoryId, int groupId, int itemId) throws Exception {
		XmlAction op = new XmlAction(createArguments(user, repositoryId, groupId, itemId), IAPIManager.ApiActionType.GET_ITEM_INFORMATIONS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (ItemRunInformations) handleError(xml);
	}
	
	@Override
	public String runETL(User user, int repositoryId, int groupId, int itemId, List<VanillaGroupParameter> parameters) throws Exception {
		XmlAction op = new XmlAction(createArguments(user, repositoryId, groupId, itemId, parameters), IAPIManager.ApiActionType.RUN_ETL);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (String) handleError(xml);
	}
	
	@Override
	public String runWorkflow(User user, int repositoryId, int groupId, int itemId, List<VanillaGroupParameter> parameters) throws Exception {
		XmlAction op = new XmlAction(createArguments(user, repositoryId, groupId, itemId, parameters), IAPIManager.ApiActionType.RUN_WORKFLOW);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (String) handleError(xml);
	}
	
	@Override
	public InputStream runReport(User user, int repositoryId, int groupId, int itemId, String outputName, String format, List<VanillaGroupParameter> parameters, List<String> mails) throws Exception {
		XmlAction op = new XmlAction(createArguments(user, repositoryId, groupId, itemId, outputName, format, parameters, mails), IAPIManager.ApiActionType.RUN_REPORT);
		return httpCommunicator.executeActionAsStream(xstream.toXML(op));
	}
	
	@Override
	public ContractIntegrationInformations generateKpi(int repositoryId, int groupId, KPIGenerationInformations infos) throws Exception {
		XmlAction op = new XmlAction(createArguments(repositoryId, groupId, infos), IAPIManager.ApiActionType.GENERATE_KPI);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (ContractIntegrationInformations) handleError(xml);
	}
	
//	@Override
//	public ContractIntegrationInformations generateSimpleKpi(int repositoryId, int groupId, SimpleKPIGenerationInformations infos) throws Exception {
//		XmlAction op = new XmlAction(createArguments(repositoryId, groupId, infos), IAPIManager.ApiActionType.GENERATE_SIMPLE_KPI);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (ContractIntegrationInformations) handleError(xml);
//	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<ContractIntegrationInformations> getIntegrationKPIByDatasetId(String datasetId) throws Exception {
		XmlAction op = new XmlAction(createArguments(datasetId), IAPIManager.ApiActionType.GET_INTEGRATION_KPI_BY_DATASET_ID);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<ContractIntegrationInformations>) handleError(xml);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<String> getValidationSchemas() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IAPIManager.ApiActionType.GET_VALIDATION_SCHEMAS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<String>) handleError(xml);
	}
	
	@Override
	public void deleteIntegration(int repositoryId, int groupId, ContractIntegrationInformations infos) throws Exception {
		XmlAction op = new XmlAction(createArguments(repositoryId, groupId, infos), IAPIManager.ApiActionType.REMOVE_INTEGRATION);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		handleError(xml);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<ContractIntegrationInformations> getIntegrationByOrganisation(String organisation, ContractType type) throws Exception {
		XmlAction op = new XmlAction(createArguments(organisation, type), IAPIManager.ApiActionType.GET_INTEGRATION_INFOS_BY_ORGANISATION);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<ContractIntegrationInformations>) handleError(xml);
	}
	
	@Override
	public void updateSchedule(Schedule schedule) throws Exception {
		XmlAction op = new XmlAction(createArguments(schedule), IAPIManager.ApiActionType.UPDATE_SCHEDULE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		handleError(xml);
	}
	
	@Override
	public ValidationDataResult validateData(String d4cUrl, String d4cObs, String datasetId, String resourceId, int contractId, List<String> schemas) throws Exception {
		XmlAction op = new XmlAction(createArguments(d4cUrl, d4cObs, datasetId, resourceId, contractId, schemas), IAPIManager.ApiActionType.VALIDATE_DATA);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (ValidationDataResult) handleError(xml);
	}

	private Object handleError(String responseMessage) throws Exception {
		if(responseMessage.isEmpty()) {
			return null;
		}
		Object o = xstream.fromXML(responseMessage);
		if(o != null && o instanceof VanillaException) {
			throw (VanillaException) o;
		}
		return o;
	}
}
