package bpm.gwt.workflow.commons.client.service;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

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

public interface WorkflowsServiceAsync {

	public void manageWorkflow(Workflow currentWorkflow, boolean modify, AsyncCallback<Workflow> callback);

	public void getWorkflows(AsyncCallback<List<Workflow>> callback);
	
	public void getWorkflows(String query, int start, int length, DataSort dataSort, AsyncCallback<DataWithCount<Workflow>> callback);

	public void removeWorkflow(Workflow currentWorkflow, AsyncCallback<Void> callback);

	public void duplicateWorkflow(int workflowId, String name, AsyncCallback<Workflow> callback);

	public void getWorkflowParameters(Workflow workflow, AsyncCallback<List<Parameter>> callback);

	public void initWorkflow(Workflow workflow, AsyncCallback<String> callback);
	
	public void runWorkflow(Workflow workflow, String uuid, User launcher, List<Parameter> parameters, AsyncCallback<WorkflowInstance> callback);

	public void getWorkflowRuns(Workflow workflow, AsyncCallback<List<WorkflowInstance>> callback);
	
	public void getWorkflowRun(Workflow workflow, WorkflowInstance item, AsyncCallback<List<ActivityLog>> callback);
	
	public void downloadLogRun(Workflow workflow, WorkflowInstance instance, Level level, AsyncCallback<String> callback);

	public void getWorkflowProgress(Workflow workflow, String uuid, AsyncCallback<WorkflowInstance> callback);

	public void stopWorkflow(int workflowId, String uuid, AsyncCallback<Void> callback);

	
	public void manageResource(Resource resource, boolean edit, AsyncCallback<Resource> callback);

	public void removeResource(Resource resource, AsyncCallback<Void> callback);
	
	public void duplicateResource(int resourceId, String name, AsyncCallback<Resource> callback);
	
	public void getResources(TypeResource type, AsyncCallback<List<? extends Resource>> callback);

	public void validScript(Variable variable, AsyncCallback<CheckResult> callback);

	public void clearName(String value, AsyncCallback<String> callback);

	public void runIncompleteWorkflow(Workflow workflow, List<Parameter> parameters, String stopActivityName, AsyncCallback<WorkflowInstance> asyncCallback);
	
	public void getJdbcDrivers(AsyncCallback<List<String>> callback);

	public void testConnection(DatabaseServer databaseServer, AsyncCallback<String> asyncCallback);
	
	public void executeScript(RScriptModel box, AsyncCallback<RScriptModel> asyncCallback);
}
