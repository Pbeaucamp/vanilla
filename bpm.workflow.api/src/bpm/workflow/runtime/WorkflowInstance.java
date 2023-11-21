package bpm.workflow.runtime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.ItemInstance;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityResult;
import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityState;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;
import bpm.vanilla.platform.core.components.IRuntimeConfig;
import bpm.vanilla.platform.core.components.workflow.WorkflowInstanceState;
import bpm.vanilla.platform.core.components.workflow.WorkflowInstanceState.StepInfos;
import bpm.vanilla.platform.core.components.workflow.WorkflowInstanceState.StepNature;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.workflow.runtime.model.IActivity;
import bpm.workflow.runtime.model.WorkflowModel;
import bpm.workflow.runtime.model.activities.StopActivity;
import bpm.workflow.runtime.model.activities.XorActivity;
import bpm.workflow.runtime.resources.variables.ListVariable;
import bpm.workflow.runtime.resources.variables.Variable;
import bpm.workflow.runtime.resources.variables.VariablesHelper;

/**
 * This is a workflow instance.
 * It handle all the running process.
 * 
 * You use the method execute to run it.
 * The method has a boolean which will run the workflow async or not.
 * You can call the method getState to know if the workflow is finished or not.
 * 
 * 
 * @author Marc Lanquetin
 *
 */
public class WorkflowInstance {

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
	private Logger logger = Logger.getLogger(this.getClass());

	private String uuid;
	private WorkflowModel model;
	private String itemName;
	
	private InstanceThread instanceThread;
	private IRuntimeConfig config;
	private WorkflowInstanceState state;
	private RemoteVanillaPlatform vanillaApi;
	private RemoteRepositoryApi repositoryApi;
	
	private HashMap<String, Variable> listVariable;
	
	private InstanceThread parentThread;
	
	public WorkflowInstance(WorkflowModel model, String itemName) {
		this.model = model;
		this.itemName = itemName;
		
		//try to reorder activities to simplified getting activity states.
		List<String> orders = new ArrayList<String>();
		findActivitiesOrder(orders);
		model.setOrders(orders);
		
		//init variables
		listVariable = new HashMap<String, Variable>();
		createEnvironmentVariables();
		for(Variable v : model.getVariables()) {
			listVariable.put(v.getName(), v);
		}
	}
	
	public WorkflowInstance(WorkflowModel model, String itemName, String uuid) {
		this(model, itemName);
		this.uuid = uuid;
	}
	
	private void createEnvironmentVariables() {
		for(String var : ListVariable.ENVIRONEMENT_VARIABLE) {
			Variable v = new Variable();
			v.setName(var);
			v.setId(var);
			v.setType(0);
			v.addValue(VariablesHelper.parseString(var));
			listVariable.put(var, v);
		}
	}

	private void findActivitiesOrder(List<String> orders) {
		if(orders.isEmpty()) {
			orders.add("Start");
			findActivitiesOrder(orders);
		}
		else {
			String lastAct = orders.get(orders.size() - 1);
			for(IActivity a : model.getActivities().get(lastAct).getTargets()) {
				if(!orders.contains(a.getId())) {
					orders.add(a.getId());
					if(a.getId().equals("End")) {
	//					return;
					}
					else {
						findActivitiesOrder(orders);
					}
				}
			}
			
		}
	}

	/**
	 * 
	 * @param config the config (for the parameters).
	 * @param async run in a different thread (mandatory for workflows containing manual tasks). 
	 */
	public String execute(IRuntimeConfig config, boolean async) throws Exception {
		Logger.getLogger(this.getClass()).info("Start running the workflow.");
		uuid = UUID.randomUUID().toString();

		this.config = config;
		state = new WorkflowInstanceState(uuid, getItemName());
		
//		if(async) {
			instanceThread = new InstanceThread(new WorkflowInstance(model, getItemName(), uuid));
			instanceThread.start(config);
//		}
//		else {
//			runWorkflow(config);
//		}
		
		Logger.getLogger(this.getClass()).info("Generated uuid for this workflow instance : " + uuid);
		return uuid;
	}
	
	private void runWorkflow(IRuntimeConfig config) throws Exception {
		try {

			logger.info("##############################################################");
			logger.info("Launch Workflow " + itemName);
			logger.info("##############################################################");
			
			//add the instance to the activities (to have context)
			String vanillaUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL);
			String user = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN);
			String pass = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD);
			
			IVanillaContext vanillaContext = new BaseVanillaContext(vanillaUrl, user, pass);
			vanillaApi = new RemoteVanillaPlatform(vanillaContext);
			
			Group g = vanillaApi.getVanillaSecurityManager().getGroupById(config.getVanillaGroupId());
			Repository r = vanillaApi.getVanillaRepositoryManager().getRepositoryById(config.getObjectIdentifier().getRepositoryId());
			
			IRepositoryContext repositoryContext = new BaseRepositoryContext(vanillaContext, g, r);
			
			repositoryApi = new RemoteRepositoryApi(repositoryContext);
			
			for(IActivity act : model.getActivities().values()) {
				act.setInstance(this);
			}
			
			List<VanillaGroupParameter> params = config.getParametersValues();
			if(params != null) {
				for(VanillaGroupParameter param : params) {
					for(VanillaParameter p : param.getParameters()) {
						createVariable("{$"+p.getName()+"}", p.getSelectedValues().get(0));
					}
				}
			}
			
			//launch the first activity (the activities will take care of the running after that)
			String first = model.getOrders().get(0);
			IActivity firstActivity = model.getActivities().get(first);
			firstActivity.execute();
			logger.info("##############################################################");
			logger.info("End Workflow " + itemName);
			logger.info("##############################################################");
			historizeTask();
		} catch(Exception e) {
			e.printStackTrace();
			
			state.setFailureCause(e.getMessage());
			state.setResult(ActivityResult.FAILED);
			logger.info("##############################################################");
			logger.info("End Workflow " + itemName + " with an error");
			logger.info("##############################################################");
			historizeTask();
		}
	}

	private void historizeTask() {
		WorkflowInstanceState m;
		try {
			m = getState();
			
			synchronized (m) {
				IRepositoryApi repositoryApi = getRootRepositoryApi(getConfig().getObjectIdentifier().getRepositoryId());

				logger.info("Task " + uuid + " save workflow resume in database.");
				
				ItemInstance instance = new ItemInstance();
				instance.setItemId(getConfig().getObjectIdentifier().getDirectoryItemId());
				instance.setGroupId(getConfig().getVanillaGroupId());
				instance.setItemType(IRepositoryApi.BIW_TYPE);
				instance.setResult(m.getResult());
				instance.setDuration(m.getDurationTime());
				instance.setRunDate(new Date());
				instance.setState(m);
				
				repositoryApi.getAdminService().addItemInstance(instance);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		Runtime.getRuntime().gc();
	}
	
	private IRepositoryApi getRootRepositoryApi(int repositoryId) {
		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		String login = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN);
		String password = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD);
		String url = config.getProperty(VanillaConfiguration.P_VANILLA_URL);
		
		Repository repository = new Repository();
		repository.setId(repositoryId);
		
		Group grp = new Group();
		grp.setId(-1);
		
		return new RemoteRepositoryApi(new BaseRepositoryContext(new BaseVanillaContext(url, login, password), grp, repository));
	}
	
	public WorkflowInstanceState getState() {
		if(instanceThread != null) {
			return instanceThread.getWorkflowState();
		}
		return getWorkflowState();
	}
	
	public IRuntimeConfig getConfig() {
		if(instanceThread != null) {
			return instanceThread.getConfig();
		}
		return config;
	}
	
	private synchronized WorkflowInstanceState getWorkflowState() {
		state.getStepInfos().clear();
		boolean isFinished = true;
		for(String order : model.getOrders()) {
			IActivity act = model.getActivities().get(order);			
			StepInfos info = new StepInfos(act.getStartDate(), act.getStopDate(), act.getName(), StepNature.Automatic, act.getState(), act.getFailureCause());
			state.addStepInfos(info);
			if(act instanceof StopActivity && act.getState() != ActivityState.ENDED && act.getState() != ActivityState.FAILED && state.getResult() != ActivityResult.FAILED) {
				isFinished = false;
			}
		}
		if(isFinished) {
			if(!state.isStopped()) {
				state.setState(ActivityState.ENDED);
				state.setStopDate(new Date());
				
//				if(state.getStepInfos() != null) {
//					StringBuffer buf = new StringBuffer();
//					for(StepInfos step : state.getStepInfos()) {
//						if(step.getFailureCause() != null && !step.getFailureCause().isEmpty()) {
//							
//						}
//					}
//				}
				if(state.getFailureCause() != null && !state.getFailureCause().isEmpty()) {
					state.setResult(ActivityResult.FAILED);
				}
				else {
					state.setResult(ActivityResult.SUCCEED);
				}
			}
		}
		else {
			state.setState(ActivityState.STARTED);
		}
		return state;
	}

	private boolean hasAConditionAsSource(IActivity act) {
		for(IActivity a : act.getSources()) {
			if(a instanceof XorActivity) {
				return true;
			}
		}
		return false;
	}

	public class InstanceThread extends Thread {
		
		private WorkflowInstance workflowInstance;
		private IRuntimeConfig config;
		
		public InstanceThread(WorkflowInstance workflowInstance) {
			this.workflowInstance = workflowInstance;
			this.workflowInstance.setParentThread(this);
		}

		public IRuntimeConfig getConfig() {
			return config;
		}

		@Override
		public void run() {
			try {
				workflowInstance.runWorkflow(config);
			} catch(Exception e) {
				e.printStackTrace();
				Logger.getLogger(InstanceThread.class).error("Error while executing the workflow " + workflowInstance.uuid, e);
			}
		}
		
		public void start(IRuntimeConfig config) {
			WorkflowInstanceState state = new WorkflowInstanceState(workflowInstance.getUuid(), workflowInstance.getItemName());
			workflowInstance.setInfos(state, config);
			this.config = config;
			super.start();
		}
		
		public WorkflowInstanceState getWorkflowState() {
			return workflowInstance.getWorkflowState();
		}
		
		public void stopWorkflow() {
			workflowInstance.getWorkflowState().setResult(ActivityResult.FAILED);
			workflowInstance.getWorkflowState().setStopDate(new Date());
			try {
				this.interrupt();
			} catch(Exception e) {}
			finally {
				this.interrupt();
			}
		}
	}
	
	public String getItemName() {
		return itemName;
	}
	
	private void setInfos(WorkflowInstanceState state, IRuntimeConfig config) {
		this.state = state;
		this.config = config;
	}

	public String getUuid() {
		return uuid;
	}

	public IVanillaAPI getVanillaApi() {
		return vanillaApi;
	}

	public IRepositoryApi getRepositoryApi() {
		return repositoryApi;
	}
	
	public WorkflowModel getModel() {
		return model;
	}

	public void setListVariable(HashMap<String, Variable> listVariable) {
		this.listVariable = listVariable;
	}

	public HashMap<String, Variable> getListVariable() {
		return listVariable;
	}
	
	/**
	 * 
	 * @param value
	 * @return the String with the variables replaced by the values
	 */
	public String parseString(String value) {
		for(Variable v : listVariable.values()) {
			try {
				value = value.replace(v.getName(), v.getLastValue());
				value = value.replace("{$" + v.getLastValue() + "}", v.getLastValue());
			} catch(Exception e) {
			}
		}
		
		return value;
	}
	
	/**
	 * 
	 * @param value
	 * @return the String with the variables replaced by the values
	 */
	public List<String> parseStringMultiple(String value) {
		for(Variable v : listVariable.values()) {
			try {
				if (value.equals(v.getName())) {
					return v.getValues();
				}
			} catch(Exception e) {
			}
		}
		
		return null;
	}
	
	
	public void createVariable(String name, String value) {
		Variable v = new Variable();
		v.setName(name);
		v.addValue(value);
		listVariable.put(v.getName(), v);
	}
	
	public Variable getOrCreateVariable(String name) {
		if(listVariable.get(name) == null) {
			Variable v = new Variable();
			v.setName(name);
			listVariable.put(v.getName(), v);
		}
		return listVariable.get(name);
	}

	public void stop() throws Exception {
		if(instanceThread != null) {
			instanceThread.stopWorkflow();
			return;
		}
		stopWorkflow();
	}

	private void stopWorkflow() throws Exception {
		throw new Exception("A synchronous workflow cannot be stopped");
	}

	public void setParentThread(InstanceThread parentThread) {
		this.parentThread = parentThread;
	}

	public InstanceThread getParentThread() {
		return parentThread;
	}

}
