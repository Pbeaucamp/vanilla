package bpm.vanillahub.remote;

import java.util.List;
import java.util.Locale;

import com.thoughtworks.xstream.XStream;

import bpm.vanilla.platform.core.beans.DataSort;
import bpm.vanilla.platform.core.beans.DataWithCount;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;
import bpm.vanillahub.core.IHubWorkflowManager;
import bpm.vanillahub.core.exception.HubException;
import bpm.vanillahub.remote.internal.HttpCommunicator;
import bpm.workflow.commons.beans.ActivityLog;
import bpm.workflow.commons.beans.Workflow;
import bpm.workflow.commons.beans.WorkflowInstance;

public class RemoteWorkflowManager implements IHubWorkflowManager {

	private HttpCommunicator httpCommunicator = new HttpCommunicator();
	private static XStream xstream;
	static{
		xstream = new XStream();
	}
	
	public RemoteWorkflowManager(String runtimeUrl, String sessionId, Locale locale) {
		httpCommunicator.init(runtimeUrl, sessionId, locale);
	}
	
	private static XmlArgumentsHolder createArguments(Object... arguments) {
		XmlArgumentsHolder args = new XmlArgumentsHolder();

		for (int i = 0; i < arguments.length; i++) {
			args.addArgument(arguments[i]);
		}
		return args;
	}
	
	private Object handleError(String responseMessage) throws Exception {
		if (responseMessage.isEmpty()) {
			return null;
		}
		Object o = xstream.fromXML(responseMessage);
		if (o != null && o instanceof HubException) {
			throw (HubException) o;
		}
		return o;
	}

	@Override
	public Workflow manageWorkflow(Workflow currentWorkflow, boolean modify) throws Exception {
		XmlAction op = new XmlAction(createArguments(currentWorkflow, modify), IHubWorkflowManager.ActionType.MANAGE_WORKFLOW);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Workflow) handleError(xml);
	}

	@Override
	public void removeWorkflow(Workflow currentWorkflow) throws Exception {
		XmlAction op = new XmlAction(createArguments(currentWorkflow), IHubWorkflowManager.ActionType.REMOVE_WORKFLOW);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		handleError(xml);
	}
	
	@Override
	public Workflow duplicateWorkflow(int workflowId, String name) throws Exception {
		XmlAction op = new XmlAction(createArguments(workflowId, name), IHubWorkflowManager.ActionType.DUPLICATE_WORKFLOW);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Workflow) handleError(xml);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Workflow> getWorkflows() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IHubWorkflowManager.ActionType.GET_WORKFLOWS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Workflow>) handleError(xml);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public DataWithCount<Workflow> getWorkflows(String query, int firstResult, int length, boolean lightWeight, DataSort dataSort) throws Exception {
		XmlAction op = new XmlAction(createArguments(query, firstResult, length, lightWeight, dataSort), IHubWorkflowManager.ActionType.GET_WORKFLOWS_WITH_PAG);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (DataWithCount<Workflow>) handleError(xml);
	}
	
	@Override
	public Workflow getWorkflow(int workflowId, boolean lightWeight) throws Exception {
		XmlAction op = new XmlAction(createArguments(workflowId, lightWeight), IHubWorkflowManager.ActionType.GET_WORKFLOW);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Workflow) handleError(xml);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Parameter> getWorkflowParameters(Workflow workflow) throws Exception {
		XmlAction op = new XmlAction(createArguments(workflow), IHubWorkflowManager.ActionType.GET_PARAMETERS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Parameter>) handleError(xml);
	}

	@Override
	public String initWorkflow(Workflow workflow) throws Exception {
		XmlAction op = new XmlAction(createArguments(workflow), IHubWorkflowManager.ActionType.INIT_WORKFLOW);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (String) handleError(xml);
	}

	@Override
	public WorkflowInstance runWorkflow(Workflow workflow, String uuid, User launcher, List<Parameter> parameters) throws Exception {
		XmlAction op = new XmlAction(createArguments(workflow, uuid, launcher, parameters), IHubWorkflowManager.ActionType.RUN_WORKFLOW);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (WorkflowInstance) handleError(xml);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<WorkflowInstance> getWorkflowRuns(Workflow workflow) throws Exception {
		XmlAction op = new XmlAction(createArguments(workflow), IHubWorkflowManager.ActionType.GET_WORKFLOW_RUNS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<WorkflowInstance>) handleError(xml);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<ActivityLog> getWorkflowRun(WorkflowInstance instance) throws Exception {
		XmlAction op = new XmlAction(createArguments(instance), IHubWorkflowManager.ActionType.GET_WORKFLOW_RUN);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<ActivityLog>) handleError(xml);
	}

	@Override
	public WorkflowInstance getWorkflowProgress(Workflow workflow, String uuid) throws Exception {
		XmlAction op = new XmlAction(createArguments(workflow, uuid), IHubWorkflowManager.ActionType.GET_WORKFLOW_PROGRESS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (WorkflowInstance) handleError(xml);
	}

	@Override
	public void stopWorkflow(int workflowId, String uuid) throws Exception {
		XmlAction op = new XmlAction(createArguments(workflowId, uuid), IHubWorkflowManager.ActionType.STOP_WORKFLOW);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		handleError(xml);
	}

	@Override
	public WorkflowInstance runIncompleteWorkflow(Workflow workflow, List<Parameter> parameters, String stopActivityName) throws Exception {
		throw new Exception("Not implemented");
	}

	@Override
	public List<WorkflowInstance> getWorkflowRunningInstances(int workflowId) throws Exception {
		throw new Exception("Not implemented");
	}
}
