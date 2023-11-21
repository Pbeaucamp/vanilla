package bpm.smart.core.xstream;

import java.util.List;
import java.util.Locale;

import com.thoughtworks.xstream.XStream;

import bpm.smart.core.xstream.internal.HttpCommunicator;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.beans.DataSort;
import bpm.vanilla.platform.core.beans.DataWithCount;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.resources.AbstractD4CIntegrationInformations;
import bpm.vanilla.platform.core.beans.resources.CheckResult;
import bpm.vanilla.platform.core.beans.resources.CkanPackage;
import bpm.vanilla.platform.core.beans.resources.ClassRule;
import bpm.vanilla.platform.core.beans.resources.ContractIntegrationInformations;
import bpm.vanilla.platform.core.beans.resources.DatabaseServer;
import bpm.vanilla.platform.core.beans.resources.KPIGenerationInformations;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanilla.platform.core.beans.resources.Resource.TypeResource;
import bpm.vanilla.platform.core.beans.resources.ValidationDataResult;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.exceptions.VanillaException;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;
import bpm.workflow.commons.beans.ActivityLog;
import bpm.workflow.commons.beans.Workflow;
import bpm.workflow.commons.beans.WorkflowInstance;

public class RemoteWorkflowManager implements ISmartWorkflowManager {

	private HttpCommunicator httpCommunicator = new HttpCommunicator();
	private static XStream xstream;
	static {
		xstream = new XStream();
	}

	public RemoteWorkflowManager(String runtimeUrl, String sessionId, Locale locale) {
		httpCommunicator.init(runtimeUrl, sessionId, locale);
	}

	private Object handleError(String responseMessage) throws Exception {
		if (responseMessage.isEmpty()) {
			return null;
		}
		Object o = xstream.fromXML(responseMessage);
		if (o != null && o instanceof VanillaException) {
			throw (VanillaException) o;
		}
		return o;
	}

	private XmlArgumentsHolder createArguments(Object... arguments) {
		XmlArgumentsHolder args = new XmlArgumentsHolder();

		for (int i = 0; i < arguments.length; i++) {
			args.addArgument(arguments[i]);
		}
		return args;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Workflow> getWorkflows() throws Exception {
		XmlAction op = new XmlAction(createArguments(), ISmartWorkflowManager.ActionType.GET_WORKFLOWS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Workflow>) handleError(xml);
	}
	
	@Override
	public DataWithCount<Workflow> getWorkflows(String query, int firstResult, int length, boolean lightWeight, DataSort dataSort) throws Exception {
		throw new Exception("Not implemented");
	}
	
	@Override
	public Workflow getWorkflow(int workflowId, boolean lightWeight) throws Exception {
		XmlAction op = new XmlAction(createArguments(workflowId, lightWeight), ISmartWorkflowManager.ActionType.GET_WORKFLOW);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Workflow) handleError(xml);
	}

	@Override
	public Workflow manageWorkflow(Workflow workflow, boolean modify) throws Exception {
		XmlAction op = new XmlAction(createArguments(workflow, modify), ISmartWorkflowManager.ActionType.MANAGE_WORKFLOW);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Workflow) handleError(xml);
	}

	@Override
	public void removeWorkflow(Workflow workflow) throws Exception {
		XmlAction op = new XmlAction(createArguments(workflow), ISmartWorkflowManager.ActionType.DELETE_WORKFLOW);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		handleError(xml);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Parameter> getWorkflowParameters(Workflow workflow) throws Exception {
		XmlAction op = new XmlAction(createArguments(workflow), ISmartWorkflowManager.ActionType.GET_WORKLFOW_PARAMETERS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Parameter>) handleError(xml);
	}

//	@Override
//	public Workflow getWorkflowById(int workflowId, boolean lightWeight) throws Exception {
//		XmlAction op = new XmlAction(createArguments(workflowId, lightWeight), ISmartWorkflowManager.ActionType.GET_WORKFLOW_BY_ID);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (Workflow) handleError(xml);
//	}

	@Override
	@SuppressWarnings("unchecked")
	public List<WorkflowInstance> getWorkflowRunningInstances(int workflowId) throws Exception {
		XmlAction op = new XmlAction(createArguments(workflowId), ISmartWorkflowManager.ActionType.GET_WORKFLOW_RUNNING);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<WorkflowInstance>) handleError(xml);
	}

	@Override
	public Resource manageResource(Resource resource, boolean edit) throws Exception {
		XmlAction op = new XmlAction(createArguments(resource, edit), ISmartWorkflowManager.ActionType.MANAGE_RESOURCE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Resource) handleError(xml);
	}

	@Override
	public void removeResource(Resource resource) throws Exception {
		XmlAction op = new XmlAction(createArguments(resource), ISmartWorkflowManager.ActionType.REMOVE_RESOURCE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		handleError(xml);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<? extends Resource> getResources(TypeResource type) throws Exception {
		XmlAction op = new XmlAction(createArguments(type), ISmartWorkflowManager.ActionType.GET_RESOURCES);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<? extends Resource>) handleError(xml);
	}

	@Override
	public CheckResult validScript(Variable variable) throws Exception {
		XmlAction op = new XmlAction(createArguments(variable), ISmartWorkflowManager.ActionType.VALID_SCRIPT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (CheckResult) handleError(xml);
	}

	@Override
	public WorkflowInstance runIncompleteWorkflow(Workflow workflow, List<Parameter> parameters, String stopActivityName) throws Exception {
		XmlAction op = new XmlAction(createArguments(workflow, parameters, stopActivityName), ISmartWorkflowManager.ActionType.RUN_INCOMPLETE_WORKFLOW);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (WorkflowInstance) handleError(xml);
	}

	@Override
	public String initWorkflow(Workflow workflow) throws Exception {
		throw new Exception("Not implemented");
	}

	@Override
	public WorkflowInstance runWorkflow(Workflow workflow, String uuid, User launcher, List<Parameter> parameters) throws Exception {
		XmlAction op = new XmlAction(createArguments(workflow, uuid, launcher, parameters), ISmartWorkflowManager.ActionType.RUN_WORKFLOW);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (WorkflowInstance) handleError(xml);
	}

	@Override
	public void stopWorkflow(int workflowId, String uuid) throws Exception {
		throw new Exception("Not implemented");
	}

	@Override
	public WorkflowInstance getWorkflowProgress(Workflow workflow, String uuid) throws Exception {
		XmlAction op = new XmlAction(createArguments(workflow, uuid), ISmartWorkflowManager.ActionType.GET_WORKFLOW_PROGRESS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (WorkflowInstance) handleError(xml);
	}

	@Override
	public List<WorkflowInstance> getWorkflowRuns(Workflow workflow) throws Exception {
		XmlAction op = new XmlAction(createArguments(workflow), ISmartWorkflowManager.ActionType.GET_WORKFLOW_RUNS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<WorkflowInstance>) handleError(xml);
	}

	@Override
	public List<ActivityLog> getWorkflowRun(WorkflowInstance instance) throws Exception {
		throw new Exception("Not implemented");
	}

	@Override
	public List<String> getJdbcDrivers() throws Exception {
		throw new Exception("Not implemented");
	}

	@Override
	public String testConnection(DatabaseServer databaseServer) throws Exception {
		throw new Exception("Not implemented");
	}

	@Override
	public ClassRule addOrUpdateClassRule(ClassRule classRule) throws Exception {
		throw new Exception("Not implemented");
	}

	@Override
	public void removeClassRule(ClassRule classRule) throws Exception {
		throw new Exception("Not implemented");
	}

	@Override
	public List<ClassRule> getClassRules(String identifiant) throws Exception {
		throw new Exception("Not implemented");
	}

	@Override
	public List<CkanPackage> getCkanDatasets(String ckanUrl) throws Exception {
		throw new Exception("Not implemented");
	}
	
	@Override
	public Resource duplicateResource(int resourceId, String name) throws Exception {
		throw new Exception("Not implemented");
	}
	
	@Override
	public Workflow duplicateWorkflow(int workflowId, String name) throws Exception {
		throw new Exception("Not implemented");
	}

	@Override
	public ContractIntegrationInformations generateIntegration(IRepositoryContext ctx, AbstractD4CIntegrationInformations integrationInfos, boolean modifyMetadata, boolean modifyIntegration) throws Exception {
		throw new Exception("Not implemented");
	}

	@Override
	public ContractIntegrationInformations generateKPI(IRepositoryContext ctx, KPIGenerationInformations infos) throws Exception {
		throw new Exception("Not implemented");
	}
	
//	@Override
//	public ContractIntegrationInformations generateSimpleKPI(IRepositoryContext ctx, SimpleKPIGenerationInformations infos) throws Exception {
//		throw new Exception("Not implemented");
//	}
	
	@Override
	public List<String> getValidationSchemas() throws Exception {
		throw new Exception("Not implemented");
	}
	
	@Override
	public void deleteIntegration(IRepositoryContext ctx, ContractIntegrationInformations integrationInfos) throws Exception {
		throw new Exception("Not implemented");
	}

	@Override
	public ValidationDataResult validateData(String d4cUrl, String d4cObs, String datasetId, String resourceId, int contractId, List<String> schemas) throws Exception {
		throw new Exception("Not implemented");
	}
}
