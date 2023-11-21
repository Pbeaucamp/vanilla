package bpm.architect.web.server;

import java.util.List;

import bpm.architect.web.server.security.ArchitectSession;
import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.server.security.CommonSessionHelper;
import bpm.gwt.workflow.commons.client.service.WorkflowsService;
import bpm.gwt.workflow.commons.server.utils.Utils;
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

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class WorkflowsServiceImpl extends RemoteServiceServlet implements WorkflowsService {

	private static final long serialVersionUID = 1L;

	private ArchitectSession getSession() throws ServiceException {
		return CommonSessionHelper.getCurrentSession(getThreadLocalRequest(), ArchitectSession.class);
	}

	@Override
	public Workflow manageWorkflow(Workflow currentWorkflow, boolean modify) throws ServiceException {
		throw new ServiceException("Method not supported !");
	}

	@Override
	public List<Workflow> getWorkflows() throws ServiceException {
		throw new ServiceException("Method not supported !");
	}
	
	@Override
	public DataWithCount<Workflow> getWorkflows(String query, int start, int length, DataSort dataSort) throws ServiceException {
		throw new ServiceException("Method not supported !");
	}

	@Override
	public void removeWorkflow(Workflow currentWorkflow) throws ServiceException {
		throw new ServiceException("Method not supported !");
	}

	@Override
	public List<Parameter> getWorkflowParameters(Workflow workflow) throws ServiceException {
		throw new ServiceException("Method not supported !");
	}

	@Override
	public String initWorkflow(Workflow workflow) throws ServiceException {
		throw new ServiceException("Method not supported !");
	}

	@Override
	public WorkflowInstance runWorkflow(Workflow workflow, String uuid, User launcher, List<Parameter> parameters) throws ServiceException {
		throw new ServiceException("Method not supported !");
	}

	@Override
	public List<WorkflowInstance> getWorkflowRuns(Workflow workflow) throws ServiceException {
		throw new ServiceException("Method not supported !");
	}

	@Override
	public List<ActivityLog> getWorkflowRun(Workflow workflow, WorkflowInstance instance) throws ServiceException {
		throw new ServiceException("Method not supported !");
	}

	@Override
	public String downloadLogRun(Workflow workflow, WorkflowInstance instance, Level level) throws ServiceException {
		throw new ServiceException("Method not supported !");
	}

	@Override
	public WorkflowInstance getWorkflowProgress(Workflow workflow, String uuid) throws ServiceException {
		throw new ServiceException("Method not supported !");
	}

	@Override
	public void stopWorkflow(int workflowId, String uuid) throws ServiceException {
		throw new ServiceException("Method not supported !");
	}

	@Override
	public Resource manageResource(Resource resource, boolean edit) throws ServiceException {
		ArchitectSession session = getSession();
		try {
			if (resource.getTypeResource() != TypeResource.USER) {
				return session.getVanillaApi().getResourceManager().manageResource(resource, edit);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}

		throw new ServiceException("This type of resource cannot be saved.");
	}

	@Override
	public void removeResource(Resource resource) throws ServiceException {
		ArchitectSession session = getSession();
		try {
			session.getVanillaApi().getResourceManager().removeResource(resource);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public List<? extends Resource> getResources(TypeResource type) throws ServiceException {
		ArchitectSession session = getSession();
		try {
			return session.getVanillaApi().getResourceManager().getResources(type);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public CheckResult validScript(Variable variable) throws ServiceException {
		try {
			ArchitectSession session = getSession();
			return session.getVanillaApi().getResourceManager().validScript(variable);
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
		throw new ServiceException("Method not supported !");
	}

	@Override
	public List<String> getJdbcDrivers() throws ServiceException {
		ArchitectSession session = getSession();
		try {
			return session.getVanillaApi().getResourceManager().getJdbcDrivers();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public String testConnection(DatabaseServer databaseServer) throws ServiceException {
		ArchitectSession session = getSession();
		try {
			return session.getVanillaApi().getResourceManager().testConnection(databaseServer);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public RScriptModel executeScript(RScriptModel box) throws ServiceException {
		throw new ServiceException("Method not supported !");
	}
	
	@Override
	public Resource duplicateResource(int resourceId, String name) throws ServiceException {
		throw new ServiceException("Method not supported !");
	}
	
	@Override
	public Workflow duplicateWorkflow(int workflowId, String name) throws ServiceException {
		throw new ServiceException("Method not supported !");
	}
}
