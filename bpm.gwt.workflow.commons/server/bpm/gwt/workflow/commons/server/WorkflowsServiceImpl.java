package bpm.gwt.workflow.commons.server;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.server.security.CommonSession;
import bpm.gwt.commons.server.security.CommonSessionHelper;
import bpm.gwt.workflow.commons.client.service.WorkflowsService;
import bpm.gwt.workflow.commons.server.utils.Utils;
import bpm.smart.core.model.RScriptModel;
import bpm.smart.core.xstream.ISmartManager;
import bpm.vanilla.platform.core.IResourceManager;
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
import bpm.workflow.commons.beans.IWorkflowManager;
import bpm.workflow.commons.beans.Log.Level;
import bpm.workflow.commons.beans.Workflow;
import bpm.workflow.commons.beans.WorkflowInstance;
import bpm.workflow.commons.utils.Constants;

public class WorkflowsServiceImpl extends RemoteServiceServlet implements WorkflowsService {

	private static final long serialVersionUID = 1L;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");

	private CommonSession getSession() throws ServiceException {
		return CommonSessionHelper.getCurrentSession(getThreadLocalRequest(), CommonSession.class);
	}
	
	private Locale getLocale() {
		return getThreadLocalRequest() != null ? getThreadLocalRequest().getLocale() : null;
	}

	private IWorkflowManager getWorkflowManager() throws ServiceException {
		IWorkflowManager manager;
		try {
			manager = getSession().getWorkflowManager(getLocale());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Workflows are not supported.");
		}
		if (manager == null) {
			throw new ServiceException("Method not supported !");
		}
		return manager;
	}

	private IResourceManager getResourceManager() throws ServiceException {
		IResourceManager manager = getSession().getResourceManager();
		if (manager == null) {
			throw new ServiceException("Method not supported !");
		}
		return manager;
	}

	private ISmartManager getSmartManager() throws ServiceException {
		return getSession().getSmartManager();
	}

	@Override
	public Workflow manageWorkflow(Workflow currentWorkflow, boolean modify) throws ServiceException {
		try {
			return getWorkflowManager().manageWorkflow(currentWorkflow, modify);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public List<Workflow> getWorkflows() throws ServiceException {
		try {
			return getWorkflowManager().getWorkflows();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public DataWithCount<Workflow> getWorkflows(String query, int start, int length, DataSort dataSort) throws ServiceException {
		try {
			return getWorkflowManager().getWorkflows(query, start, length, true, dataSort);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public void removeWorkflow(Workflow currentWorkflow) throws ServiceException {
		try {
			getWorkflowManager().removeWorkflow(currentWorkflow);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public List<Parameter> getWorkflowParameters(Workflow workflow) throws ServiceException {
		try {
			return getWorkflowManager().getWorkflowParameters(workflow);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public String initWorkflow(Workflow workflow) throws ServiceException {
		try {
			return getWorkflowManager().initWorkflow(workflow);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public WorkflowInstance runWorkflow(Workflow workflow, String uuid, User launcher, List<Parameter> parameters) throws ServiceException {
		try {
			return getWorkflowManager().runWorkflow(workflow, uuid, launcher, parameters);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public List<WorkflowInstance> getWorkflowRuns(Workflow workflow) throws ServiceException {
		try {
			return getWorkflowManager().getWorkflowRuns(workflow);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public List<ActivityLog> getWorkflowRun(Workflow workflow, WorkflowInstance instance) throws ServiceException {
		try {
			return getWorkflowManager().getWorkflowRun(instance);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public String downloadLogRun(Workflow workflow, WorkflowInstance instance, Level level) throws ServiceException {
		CommonSession session = getSession();

		List<ActivityLog> activityLogs = getWorkflowRun(workflow, instance);
		instance.setActivityLogs(activityLogs);

		String logFileName = Utils.clearName(workflow.getName()) + "_" + dateFormat.format(new Date());
		String logs = instance.getLogs(level);

		ByteArrayInputStream bais = new ByteArrayInputStream(logs.getBytes());
		session.addStream(logFileName, Constants.LOG_EXTENSION, bais);

		return logFileName;
	}

	@Override
	public WorkflowInstance getWorkflowProgress(Workflow workflow, String uuid) throws ServiceException {
		try {
			return getWorkflowManager().getWorkflowProgress(workflow, uuid);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public void stopWorkflow(int workflowId, String uuid) throws ServiceException {
		try {
			getWorkflowManager().stopWorkflow(workflowId, uuid);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public Resource manageResource(Resource resource, boolean edit) throws ServiceException {
		try {
			if (resource.getTypeResource() != TypeResource.USER) {
				return getResourceManager().manageResource(resource, edit);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}

		throw new ServiceException("This type of resource cannot be saved.");
	}

	@Override
	public void removeResource(Resource resource) throws ServiceException {
		try {
			getResourceManager().removeResource(resource);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public List<? extends Resource> getResources(TypeResource type) throws ServiceException {
		try {
			return getResourceManager().getResources(type);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public CheckResult validScript(Variable variable) throws ServiceException {
		try {
			return getResourceManager().validScript(variable);
		} catch (Exception e) {
			e.printStackTrace();
			return new CheckResult(e.getMessage(), true);
		}
	}

	@Override
	public String clearName(String value) throws ServiceException {
		return Utils.clearName(value);
	}

	@Override
	public WorkflowInstance runIncompleteWorkflow(Workflow workflow, List<Parameter> parameters, String stopActivityName) throws ServiceException {
		try {
			return getWorkflowManager().runIncompleteWorkflow(workflow, parameters, stopActivityName);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage(), e);
		}
	}

	@Override
	public List<String> getJdbcDrivers() throws ServiceException {
		try {
			return getResourceManager().getJdbcDrivers();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public String testConnection(DatabaseServer databaseServer) throws ServiceException {
		try {
			return getResourceManager().testConnection(databaseServer);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public RScriptModel executeScript(RScriptModel box) throws ServiceException {
		try {
			ISmartManager smartManager = getSmartManager();
			if (smartManager == null) {
				return null;
			}
	
			RScriptModel result = smartManager.executeScriptR(box, null);
			result.setOutputVars(null); // on n'envoie pas les variables cote client
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to execute the script.", e);
		}
	}

	@Override
	public Workflow duplicateWorkflow(int workflowId, String name) throws ServiceException {
		try {
			return getWorkflowManager().duplicateWorkflow(workflowId, name);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public Resource duplicateResource(int resourceId, String name) throws ServiceException {
		try {
			return getResourceManager().duplicateResource(resourceId, name);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}
}
