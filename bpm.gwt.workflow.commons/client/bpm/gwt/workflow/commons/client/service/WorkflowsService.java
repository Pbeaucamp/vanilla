package bpm.gwt.workflow.commons.client.service;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.smart.core.model.RScriptModel;
import bpm.vanilla.platform.core.beans.DataSort;
import bpm.vanilla.platform.core.beans.DataWithCount;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.resources.CheckResult;
import bpm.vanilla.platform.core.beans.resources.DatabaseServer;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanilla.platform.core.beans.resources.Resource.TypeResource;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.workflow.commons.beans.ActivityLog;
import bpm.workflow.commons.beans.Log.Level;
import bpm.workflow.commons.beans.Workflow;
import bpm.workflow.commons.beans.WorkflowInstance;

@RemoteServiceRelativePath("workflowsService")
public interface WorkflowsService extends RemoteService {

	public static class Connect {
		private static WorkflowsServiceAsync instance;

		public static WorkflowsServiceAsync getInstance() {
			if (instance == null) {
				instance = (WorkflowsServiceAsync) GWT.create(WorkflowsService.class);
			}
			return instance;
		}
	}

	public Workflow manageWorkflow(Workflow currentWorkflow, boolean modify) throws ServiceException;

	public List<Workflow> getWorkflows() throws ServiceException;
	
	public DataWithCount<Workflow> getWorkflows(String query, int start, int length, DataSort dataSort) throws ServiceException;

	public void removeWorkflow(Workflow currentWorkflow) throws ServiceException;

	public Workflow duplicateWorkflow(int workflowId, String name) throws ServiceException;

	public List<Parameter> getWorkflowParameters(Workflow workflow) throws ServiceException;

	public String initWorkflow(Workflow workflow) throws ServiceException;
	
	public WorkflowInstance runWorkflow(Workflow workflow, String uuid, User launcher, List<Parameter> parameters) throws ServiceException;

	public List<WorkflowInstance> getWorkflowRuns(Workflow workflow) throws ServiceException;
	
	public List<ActivityLog> getWorkflowRun(Workflow workflow, WorkflowInstance item) throws ServiceException;
	
	public String downloadLogRun(Workflow workflow, WorkflowInstance instance, Level level) throws ServiceException;
	
	public WorkflowInstance getWorkflowProgress(Workflow workflow, String uuid) throws ServiceException;

	public void stopWorkflow(int workflowId, String uuid) throws ServiceException;

	
	public Resource manageResource(Resource resource, boolean edit) throws ServiceException;

	public void removeResource(Resource resource) throws ServiceException;
	
	public Resource duplicateResource(int resourceId, String name) throws ServiceException;
	
	public List<? extends Resource> getResources(TypeResource type) throws ServiceException;
	
	public CheckResult validScript(Variable variable) throws ServiceException;
	
	public String clearName(String value) throws ServiceException;
	
	public WorkflowInstance runIncompleteWorkflow(Workflow workflow, List<Parameter> parameters, String stopActivityName) throws ServiceException;
	
	public List<String> getJdbcDrivers() throws ServiceException;

	public String testConnection(DatabaseServer databaseServer) throws ServiceException;
	
	public RScriptModel executeScript(RScriptModel box) throws ServiceException;
}
