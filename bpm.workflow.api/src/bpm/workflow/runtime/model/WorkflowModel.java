package bpm.workflow.runtime.model;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.workflow.runtime.model.activities.DummyActivity;
import bpm.workflow.runtime.model.activities.ExcelAggregateActivity;
import bpm.workflow.runtime.model.activities.InterfaceActivity;
import bpm.workflow.runtime.model.activities.InterfaceGoogleActivity;
import bpm.workflow.runtime.model.activities.StartActivity;
import bpm.workflow.runtime.model.activities.StopActivity;
import bpm.workflow.runtime.resources.IResource;
import bpm.workflow.runtime.resources.servers.ListServer;
import bpm.workflow.runtime.resources.servers.Server;
import bpm.workflow.runtime.resources.variables.ListVariable;
import bpm.workflow.runtime.resources.variables.Variable;

/**
 * The model of the workflow
 * 
 * @author CHARBONNIER, MARTIN, MARC
 * 
 */
public class WorkflowModel {
	final static public String XPDL_FIELD_DIRECTORY_ITEM_ID = "bpm.bpm.workflow.runtime.model.xpdl.field.directoryItemId";
	final static public String XPDL_FIELD_REPOSITORY_ID = "bpm.bpm.workflow.runtime.model.xpdl.field.repositoryId";
	final static public String XPDL_FIELD_USER_LOGIN = "bpm.bpm.workflow.runtime.model.xpdl.field.userLogin";
	final static public String XPDL_FIELD_USER_PASSWORD = "bpm.bpm.workflow.runtime.model.xpdl.field.userPassword";
	final static public String XPDL_FIELD_GROUP_ID = "bpm.bpm.workflow.runtime.model.xpdl.field.groupId";
	/**
	 * fucking generic variable on process Instance to be able to retrieve this damned Bonita processInstance
	 */
	final static public String XPDL_FIELD_VANILLA_WORKFLOW_PROCESS_INSTANCE_UUID = "bpm.bpm.workflow.runtime.model.xpdl.field.vanillaProcessInstanceUUID";

	private DocumentProperties properties = new DocumentProperties();

	private HashMap<String, IResource> resources = new HashMap<String, IResource>();
	private HashMap<String, IActivity> activities = new HashMap<String, IActivity>();
	private PoolModel process = null;;

	private List<Transition> transitions = new ArrayList<Transition>();
	private List<PoolModel> pools = new ArrayList<PoolModel>();
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");

	private String name = "workflow model";
	private String description = "model description";
	private String id = "workflow_model";

	private List<WorkfowModelParameter> modelParameters = new ArrayList<WorkfowModelParameter>();
	private List<String> orders;

	/**
	 * do not use, only for XML parsing
	 */
	public WorkflowModel() {

	}

	public void addParameter(WorkfowModelParameter param) {
		modelParameters.add(param);
	}

	public void removeParameter(WorkfowModelParameter param) {
		modelParameters.remove(param);
	}

	public List<WorkfowModelParameter> getParameters() {
		return modelParameters;
	}

	/**
	 * Create a workflowmodel with the specified name
	 * 
	 * @param name
	 */
	public WorkflowModel(String name) {
		this.setName(name);
		try {
			IActivity start = new StartActivity();
			activities.put(start.getName(), start);

			IActivity stop = new StopActivity();
			activities.put(stop.getName(), stop);

			getPoolProcess().addChild(start);
			getPoolProcess().addChild(stop);

		} catch(Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @return the name of the workflowmodel
	 */
	public String getId() {
		return id;
	}

	/**
	 * do not use
	 * 
	 * @param id
	 */
	public void setId(String id) {
		this.id = id.replaceAll("[^a-zA-Z0-9]", "");;
	}

	/**
	 * Set the name of the workflowmodel
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
		setId(name.replace(" ", "_"));
		properties.setName(name);
	}

	/**
	 * 
	 * @return the name of the workflowmodel
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the description of the workflowmodel
	 * 
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * 
	 * @return the description of the workflowmodel
	 */
	public String getDescription() {
		return description;
	}

	public List<Variable> getVariables() {
		List<Variable> l = new ArrayList<Variable>();
		for(IResource r : resources.values()) {
			if(r instanceof Variable) {
				l.add((Variable) r);
			}
		}
		return l;
	}

	/**
	 * 
	 * @param resourceId
	 * @return the specified resources (thanks to the resource's name)
	 */
	public IResource getResource(String resourceId) {
		String key = null;
		for(String k : resources.keySet()) {
			if(k.equals(resourceId)) {
				key = k;
				break;
			}
		}

		return resources.get(key);

	}

	/**
	 * 
	 * @return all the resources names
	 */
	public Collection<String> getResourcesNames() {
		return resources.keySet();
	}

	/**
	 * Remove the resources with the specified name
	 * 
	 * @param resourceName
	 */
	public void removeResource(String resourceName) {
		String key = null;
		for(String k : resources.keySet()) {
			if(k.equals(resourceName)) {
				key = k;
				break;
			}
		}

		if(key != null) {
			resources.remove(key);
		}
	}

	/**
	 * Add the resource in the workflowmodel
	 * 
	 * @param resource
	 */
	public void addResource(IResource resource) {
		if(resource instanceof Variable) {
			try {
				ListVariable.getInstance().addVariable((Variable) resource);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		else {
			resources.put(resource.getId(), resource);

			if(resource instanceof Server) {
				ListServer.getInstance().addServer((Server) resource);
			}
		}
	}

	/**
	 * Add the activity in the workflowmodel
	 * 
	 * @param activity
	 * @throws WorkflowException
	 */
	public final void addActivity(IActivity activity) throws WorkflowException {

		for(String s : activities.keySet()) {
			if(s.equals(activity.getId())) {
				throw new WorkflowException("An activity with this name already exists " + s);
			}
		}

		if(!activities.containsKey(activity.getName())) {
			activities.put(activity.getId(), activity);
			if(!(activity instanceof IManual) && !getPoolProcess().contains(activity)) {
				getPoolProcess().addChild(activity);
			}
			else if(activity instanceof IManual) {
				IManual manual = (IManual) activity;
				for(PoolModel p : pools) {
					if(p.getName().equalsIgnoreCase(manual.getGroupForValidation())) {
						p.addChild(activity);
						break;
					}
				}
			}
		}

	}

	public final void addActivity(IActivity activity, boolean test) throws WorkflowException {
		if(!activities.containsKey(activity.getName())) {
			activities.put(activity.getId(), activity);
			if(!(activity instanceof IManual) && !getPoolProcess().contains(activity)) {
				getPoolProcess().addChild(activity);
			}
			else if(!getPoolProcess().contains(activity)) {
				IManual manual = (IManual) activity;
				for(PoolModel p : pools) {
					if(p.getName().equalsIgnoreCase(manual.getGroupForValidation())) {
						p.addChild(activity);
						break;
					}
				}
			}
		}

	}

	/**
	 * 
	 * @return the model of the pool
	 */
	public PoolModel getPoolProcess() {
		if(process == null) {
			boolean exist = false;
			for(PoolModel p : pools) {
				if(p.getName().equalsIgnoreCase(PoolModel.PROCESS)) {
					process = p;
					exist = true;
					break;
				}
			}
			if(!exist) {
				process = new PoolModel(PoolModel.PROCESS);
				pools.add(process);
			}
		}
		return process;
	}

	/**
	 * Delete the specified activity
	 * 
	 * @param activity
	 * @throws WorkflowException
	 */
	public void deleteActivity(IActivity activity) throws WorkflowException {
		if(activity instanceof StartActivity) {
			throw new WorkflowException("Cannot delete the Start Activity");
		}

		if(activity instanceof StopActivity) {
			throw new WorkflowException("Cannot delete the Stop Activity");
		}

		List<Transition> todel = new ArrayList<Transition>();
		for(Transition t : transitions) {
			if(t.getSource().equals(activity) || t.getTarget().equals(activity)) {
				todel.add(t);
			}
		}

		for(Transition t : todel) {
			removeTransition(t);
		}

		for(PoolModel p : pools) {
			if(p.contains(activity)) {
				p.removeChild(activity);
			}
		}

		activities.remove(activity.getId());
	}

	/**
	 * 
	 * @return all the names of the activities
	 */
	public Collection<String> getActivitiesNames() {
		return activities.keySet();
	}

	/**
	 * 
	 * @param activityId
	 * @return the specified activity thanks to its name
	 */
	public IActivity getActivity(String activityId) {
		String key = null;
		for(String k : activities.keySet()) {
			if(k.equals(activityId)) {
				key = k;
				break;
			}
		}

		return activities.get(key);
	}

	/**
	 * Remove the specified activity
	 * 
	 * @param activityId
	 * @throws WorkflowException
	 */
	public void removeActivity(String activityId) throws WorkflowException {
		String key = null;
		for(String k : activities.keySet()) {
			if(k.equals(activityId)) {
				key = k;
				break;
			}
		}

		if(key != null) {
			IActivity a = activities.get(key);
			if(a instanceof IManual) {
				for(PoolModel p : pools) {
					try {
						p.removeChild((IActivity) a);
					} catch(Exception e) {

					}
				}
			}
			deleteActivity(a);
		}
	}

	/**
	 * Add a pool in the workflowmodel
	 * 
	 * @param pool
	 * @throws WorkflowException
	 */
	public void addPool(PoolModel pool) throws WorkflowException {
		for(PoolModel p : pools) {
			if(p.getName().equalsIgnoreCase(pool.getName())) {
				throw new WorkflowException("This pool is already in the model");
			}
		}
		pools.add(pool);
	}

	/**
	 * Remove the specified pool
	 * 
	 * @param pool
	 * @throws Exception
	 */
	public void removePool(PoolModel pool) throws Exception {
		if(!pools.contains(pool)) {
			throw new Exception("This pool is not in the model");
		}
		pools.remove(pool);
	}

	/**
	 * 
	 * @return the pools contained in the model
	 */
	public List<PoolModel> getPools() {
		return pools;
	}

	/**
	 * 
	 * @param poolName
	 * @return true if a pool with the specified name is in the workflowmodel
	 */
	public boolean containsPoolModel(String poolName) {
		for(PoolModel p : pools) {
			if(p.getName().equalsIgnoreCase(poolName)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Add an activity into the pool specified with its name
	 * 
	 * @param activity
	 * @param poolName
	 * @throws WorkflowException
	 */
	public void addActivityToPool(IActivity activity, String poolName) throws WorkflowException {
		PoolModel pool = null;
		// here we add the activity to the poolmodel specified
		for(PoolModel p : pools) {
			if(p.getName().equalsIgnoreCase(poolName)) {
				try {
					p.addChild(activity);
					pool = p;
					break;
				} catch(WorkflowException ex) {

				}

			}
		}
		// we remove the activity from an old pool
		for(PoolModel p : pools) {

			if(p != pool) {
				try {
					p.removeChild(activity);

				} catch(WorkflowException ex) {

				}
			}
		}
	}

	/**
	 * 
	 * @return the transitions of the workflow model
	 */
	public List<Transition> getTransitions() {
		return transitions;
	}

	/**
	 * Remove the transition from the workflow model
	 * 
	 * @param transition
	 */
	public void removeTransition(Transition transition) {
		transitions.remove(transition);
		transition.disconnect();
	}

	/**
	 * Add a transition to the workflow model
	 * 
	 * @param transition
	 * @throws TransitionException
	 */
	public void addTransition(Transition transition) throws TransitionException {

		if((transition.getSource() != null && transition.getSource().getTargets().contains(transition.getTarget())) || (transition.getTarget() != null && transition.getTarget().getSources().contains(transition.getSource()))) {

			throw new TransitionException("A transition already exists between these two Activities in the same direction");
		}

		if(transition.getTarget() instanceof StartActivity) {
			throw new TransitionException("Cannot use Start Activity as a target");
		}

		if(transition.getSource() instanceof StopActivity) {
			throw new TransitionException("Cannot use Stop Activity as a source");
		}
//		if(transition.getSource() != null && transition.getTarget() != null) {
			transitions.add(transition);
	
			if(transition.getSource() != null) {
				transition.getSource().addTarget(transition.getTarget());
			}
	
			if(transition.getTarget() != null) {
				transition.getTarget().addSource(transition.getSource());
			}
//		}

	}

	/**
	 * 
	 * @param source
	 * @param target
	 * @return the transition which is between the source and the target
	 * @throws Exception
	 */
	public Transition getTransition(IActivity source, IActivity target) throws Exception {
		Transition look = null;
		for(Transition t : transitions) {
			if(t.getSource().equals(source) && t.getTarget().equals(target)) {
				look = t;
				break;
			}
		}
		if(look != null) {
			return look;
		}
		else {
			throw new Exception("this transition doesnt exist");
		}
	}

	public Document getXPDL(IRepositoryApi sock) {
		Document doc = DocumentHelper.createDocument();

		// root
		Element root = DocumentHelper.createElement("Package");
		doc.setRootElement(root);

		root.addAttribute("Id", id);
		root.addAttribute("Name", id);

		// packageHeader
		Element packageHeader = DocumentHelper.createElement("PackageHeader");

		packageHeader.addElement("XPDLVersion").setText("1.0");
		packageHeader.addElement("Vendor").setText("BPM-Conseil");

		packageHeader.addElement("Created").setText(sdf.format(Calendar.getInstance().getTime()));
		packageHeader.addElement("Description").setText(description == null ? "" : description);
		root.add(packageHeader);

		Element redef = DocumentHelper.createElement("RedefinableHeader");

		redef.addElement("Version").setText("1.0");

		root.add(redef);
		root.addElement("ConformanceClass").addAttribute("GraphConformance", "NON_BLOCKED");

		// processes
		Element processes = DocumentHelper.createElement("WorkflowProcesses");

		processes.remove(processes.getNamespace());

		root.add(processes);

		// process
		Element process = DocumentHelper.createElement("WorkflowProcess").addAttribute("AccessLevel", "PUBLIC");
		process.addAttribute("Id", id + "");
		process.addAttribute("Name", id);
		process.addElement("ProcessHeader");
		process.addElement("RedefinableHeader").addElement("Version").setText("1.0");

		// processParameters
		process.addElement("FormalParameters");

		processes.add(process);

		// datafields
		Element dataFields = process.addElement("DataFields");

		for(IResource r : resources.values()) {

			for(Element e : r.toXPDL()) {
				dataFields.add(e);
			}
		}

		for(WorkfowModelParameter p : getParameters()) {
			dataFields.add(p.toXPDL());
		}

		Element participants = null;
		{
			if(process.element("Participants") == null) {
				participants = process.addElement("Participants");
			}
			Element participant = participants.addElement("Participant");
			participant.addAttribute("Id", "VANILLA_SYSTEM").addAttribute("Name", "VANILLA_SYSTEM");
			participant.addElement("ParticipantType").addAttribute("Type", "HUMAN");

			Element extAts = participant.addElement("ExtendedAttributes");
			extAts.addElement("ExtendedAttribute").addAttribute("Name", "NewParticipant").addAttribute("Value", "true");
		}

		for(IActivity a : this.activities.values()) {
			if(a instanceof InterfaceActivity) {
				if(process.element("Participants") == null) {
					participants = process.addElement("Participants");
				}

				InterfaceActivity i = (InterfaceActivity) a;
				Element participant = participants.addElement("Participant");
				participant.addAttribute("Id", i.getGroupForValidation()).addAttribute("Name", i.getGroupForValidation());
				participant.addElement("ParticipantType").addAttribute("Type", "HUMAN");

				Element extAts = participant.addElement("ExtendedAttributes");
				extAts.addElement("ExtendedAttribute").addAttribute("Name", "NewParticipant").addAttribute("Value", "true");
				extAts.addElement("ExtendedAttribute").addAttribute("Name", "XOffset").addAttribute("Value", 0 + "");
				extAts.addElement("ExtendedAttribute").addAttribute("Name", "YOffset").addAttribute("Value", 5 + "");
			}
			if(a instanceof InterfaceGoogleActivity) {
				if(process.element("Participants") == null)
					participants = process.addElement("Participants");

				InterfaceGoogleActivity i = (InterfaceGoogleActivity) a;
				Element participant = participants.addElement("Participant");
				participant.addAttribute("Id", i.getGroupForValidation()).addAttribute("Name", i.getGroupForValidation());
				participant.addElement("ParticipantType").addAttribute("Type", "HUMAN");

				Element extAts = participant.addElement("ExtendedAttributes");
				extAts.addElement("ExtendedAttribute").addAttribute("Name", "NewParticipant").addAttribute("Value", "true");
				extAts.addElement("ExtendedAttribute").addAttribute("Name", "XOffset").addAttribute("Value", 0 + "");
				extAts.addElement("ExtendedAttribute").addAttribute("Name", "YOffset").addAttribute("Value", 5 + "");
			}
		}

		Element activities = process.addElement("Activities");

		/*
		 * add the VanillaStarterTask
		 */
		Element activity = DocumentHelper.createElement("Activity");
		activity.addAttribute("Id", "VanillaWorkflowRuntimeStarter").addAttribute("Name", "VanillaWorkflowRuntimeStarter");
		activity.addElement("Implementation").addElement("No");
		activity.addElement("Performer").setText("VANILLA_SYSTEM");
		activity.addElement("StartMode").addElement("Manual");

		Element tr = activity.addElement("TransitionRestrictions").addElement("TransitionRestriction");
		tr.addElement("Join").addAttribute("Type", "AND");

		Element extAts = activity.addElement("ExtendedAttributes");
		Element hook = extAts.addElement("ExtendedAttribute").addAttribute("Name", "hook").addAttribute("Value", "bpm.workflow.core.hooks.vanilla.WorkflowHook");
		hook.addElement("HookEventName").setText("task:onStart");
		hook.addElement("Rollback").setText("false");
		activities.add(activity);

		// transitions

		Element transitions = process.addElement("Transitions");
		for(Transition t : this.transitions) {
			if(t.getSource() instanceof StartActivity) {
				DummyActivity d = new DummyActivity();
				d.setName("VanillaWorkflowRuntimeStarter");
				Transition t1 = new Transition(t.getSource(), d);
				transitions.add(t1.getXPDL());
				t1 = new Transition(d, t.getTarget());
				transitions.add(t1.getXPDL());
			}
			else {
				transitions.add(t.getXPDL());
			}

		}

		return doc;
	}

	/**
	 * 
	 * @return the activities contained in the workflow model
	 */
	public HashMap<String, IActivity> getActivities() {
		return activities;
	}
	
	public void setActivityOrders(List<String> orders) {
		this.setOrders(orders);
	}

	public Element getXmlNode() {
		Element e = DocumentHelper.createElement("workflowModel");
		e.add(properties.getXmlNode());
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);
		e.addElement("id").setText(id);

		for(WorkfowModelParameter p : modelParameters) {
			e.add(p.getXmlNode());
		}

		for(IResource r : resources.values()) {
			e.add(r.getXmlNode());
		}
		
		for(Variable var : ListVariable.getInstance().getVariables()) {
			if(Arrays.asList(ListVariable.getInstance().getNoEnvironementVariable()).contains(var.getName())) {
				e.add(var.getXmlNode());
			}
		}

		for(PoolModel p : pools) {
			e.add(p.getXmlNode());
		}

		for(IActivity a : activities.values()) {
			if(a instanceof ExcelAggregateActivity) {

			}
			else {
				e.add(a.getXmlNode());
			}
		}
		for(IActivity a : activities.values()) {
			if(a instanceof ExcelAggregateActivity) {
				e.add(a.getXmlNode());
			}
		}

		for(Transition t : transitions) {
			e.add(t.getXmlNode());
		}

		return e;
	}

	/**
	 * save the currentModel Xml in the given output stream
	 * 
	 * @param stream
	 * @throws Exception
	 */
	public void saveToXml(OutputStream stream) throws Exception {
		XMLWriter writer;
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("UTF-8");
		format.setTrimText(false);
		writer = new XMLWriter(stream, format);

		writer.write(getXmlNode());

		writer.close();
	}

	/**
	 * save the currentModel Xml in the given output stream
	 * 
	 * @param stream
	 * @throws Exception
	 */
	public void saveToXPDL(OutputStream stream, IRepositoryApi sock) throws Exception {
		XMLWriter writer;
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setTrimText(false);
		format.setEncoding("UTF-8");
		writer = new XMLWriter(stream, format);

		writer.write(getXPDL(sock));

		writer.close();
	}

	/**
	 * 
	 * @return the servers contained into the workflow model
	 */
	public List<Server> getServers() {
		List<Server> l = new ArrayList<Server>();

		for(IResource r : resources.values()) {
			if(r instanceof Server) {
				l.add((Server) r);
			}
		}

		return l;
	}

	/**
	 * 
	 * @param serverName
	 * @return the specified server thanks to its name
	 */
	public Server getServer(String serverName) {
		for(Server s : getServers()) {
			if(s.getName().equals(serverName)) {
				return s;
			}
		}
		return null;
	}

	/**
	 * Set the properties of the workflow model
	 * 
	 * @param prop
	 */
	public void setProperties(DocumentProperties prop) {
		this.properties = prop;
	}

	/**
	 * 
	 * @return the properties of the workflow model
	 */
	public DocumentProperties getProperties() {
		return properties;
	}

	public void setOrders(List<String> orders) {
		this.orders = orders;
	}

	public List<String> getOrders() {
		return orders;
	}

}
